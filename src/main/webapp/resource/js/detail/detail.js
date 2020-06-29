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
            reject(data);
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
            reject(data);
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

function toggleLike() {
  const url = cp + "/detail/" + projectNo + "/like";
  const count = parseInt($("#btnLike span.count").text());
  ajaxJSON(url, "post", {})
    .then(function (data) {
      if (data.result == "ok") {
        if (data.like_result == true) {
          $("#btnLike span.icon").addClass("on");
          $("#btnLike span.count").text(count + 1);
        } else {
          $("#btnLike span.icon").removeClass("on");
          $("#btnLike span.count").text(count - 1);
        }
      }
    })
    .catch(function (e) {
      console.log(e.responseText);
    });
}

$(function () {
  $("#btnLike").click(function () {
    toggleLike();
  });
});
