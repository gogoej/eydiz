function ajaxJSON(url, method, data) {
  return new Promise(function (resolve, reject) {
    try {
      $.ajax({
        url: url,
        type: method,
        data: data,
        success: function (data) {
          if (data.result == "ok") {
            resolve(data);
          } else {
            reject("유효하지 않은 요청... 파라미터를 확인하세요");
          }
        },
        error: function (e) {
          reject(e.responseText);
        },
      });
    } catch (error) {
      reject(error);
    }
  });
}

function ajaxJSONFile(url, method, data) {
  return new Promise(function (resolve, reject) {
    try {
      $.ajax({
        url: url,
        type: method,
        enctype: "multipart/form-data",
        processData: false,
        contentType: false,
        data: data,
        dataType: "json",
        success: function (data) {
          if (data.result == "ok") {
            resolve(data);
          } else {
            reject("유효하지 않은 요청... 파라미터를 확인하세요");
          }
        },
        beforeSend: function (jqXHR) {
          jqXHR.setRequestHeader("AJAX", "true");
        },
        error: function (e) {
          reject(e.responseText);
        },
      });
    } catch (error) {
      reject(error);
    }
  });
}

//datepicker 사용
$(function () {
  $("#projectStartDate").datepicker({ dateFormat: "yymmdd" });
  $("#projectEndDate").datepicker({ dateFormat: "yymmdd" });
});

$(function () {
  $("#uploadImage").change(function () {
    const url = cp + "/studio/project/register/upload/image";
    const f = document.projectForm;
    const data = new FormData(f);
    ajaxJSONFile(url, "post", data)
      .then(function (data) {
        console.log(data);
      })
      .catch(function (e) {
        console.log(e);
      });
  });
});

///////////////////////////////////

//모바일 버튼 클릭
$(function () {
  $("#btnGnbMenu").click(function () {
    $(".columnSide").addClass("show");
    $(".overlay").addClass("show");
  });

  $(".overlay").click(function () {
    $(".columnSide").removeClass("show");
    $(".overlay").removeClass("show");
  });
});

//이미지 첨부 버튼 클릭
$(function () {
  $("#btnProjectImage").click(function () {
    $("#uploadImage").click();
  });
});

//키워드 불러오기
$(function () {
  const projectNo = $("input[name=projectNo").val();
  const url = cp + "/studio/project/" + projectNo + "/hashtag/view";
  console.log("ajax 호출", url);
  ajaxJSON(url, "get", "")
    .then(function (data) {
      const tags = data.hashtag;
      $.each(tags, function (idx, item) {
        addItem(item.keyword);
      });
    })
    .catch(function (e) {});
});

//키워드 입력
$(function () {
  //엔터키 누르면 해시태그 등록하기
  $("#keyword").keypress(function (e) {
    const code = e.keyCode;
    if (code == 13) {
      //엔터 입력되면
      const projectNo = $("input[name=projectNo]").val();
      const keyword = $(this).val();
      addItem(keyword);
      //서버 요청
      const url =
        cp + "/studio/project/" + projectNo + "/hashtag/insert/" + encodeURIComponent(keyword);
      ajaxJSON(url, "post", { projectNo: projectNo, keyword: keyword })
        .then(function (data) {
          console.log(data);
        })
        .catch(function (e) {
          console.log(e);
        });
      $(this).val("");
      $(this).focus();
    }
  });

  //지우기 누르면 해시태그 삭제하기
  $("body").on("click", ".btnHashDelete", function () {
	 const $this = $(this);
    const length = getCountHashItem();
    const keyword = $(this).closest("li").find("span.text").text().trim();
    const projectNo = $("input[name=projectNo]").val();
    const url = cp + "/studio/project/" + projectNo + "/hashtag/delete/" + encodeURI(keyword);
    ajaxJSON(url, "post", {})
      .then(function (data) {
        if (data.result == "ok") {
          $this.closest("li").remove();
          refreshHashTitle(length - 1 - 1); //template 제외
        }
      })
      .catch(function (e) {
        console.log(e);
      });
  });
});

function addItem(keyword) {
  if (keyword.trim() == "") {
    return;
  }
  const length = getCountHashItem();
  if (length - 1 == 10) {
    //template 제외
    alert("10개 이상 추가할 수 없습니다.");
    return;
  }
  refreshHashTitle(length);
  const $template = $("ul.hashItems").eq(0).find(".template").eq(0).clone(1);
  $template.removeClass("template").find(".text").eq(0).text(keyword);
  $template.appendTo(".hashItems");
}

function getCountHashItem() {
  return $("ul.hashItems li").length;
}

function refreshHashTitle(length) {
  const text = length + "/10개의 해시 태그";
  $("#hashTitle").text(text);
}