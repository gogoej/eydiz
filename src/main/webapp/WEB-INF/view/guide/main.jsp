<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
   String cp = request.getContextPath();
%>
<link href="https://use.fontawesome.com/releases/v5.0.6/css/all.css" rel="stylesheet">
<script type="text/javascript">
function updateGuide(faqNo) {
	var q = "faqNo="+faqNo;
	
	var url = "<%=cp%>/guide/update?"+q;
	
	location.href=url;
}

function deleteGuide(faqNo) {
	var q = "faqNo="+faqNo;
	var url = "<%=cp%>/guide/delete?"+q;
	
	if(confirm("게시글을 삭제 하시겠습니까 ??")) {
		location.href=url;
	}
}

$(function() {
	$(".guideinfo").hide();
	$(".guidebox").each(function(index) {
		$(this).click(function() {
			var isHidden = $(".guideinfo").eq(index).is(':hidden');
			$(".guideinfo").hide();
		
			if(isHidden) {
				$(".guideinfo").eq(index).show();
			} else {
				$(".guideinfo").eq(index).hide();
			}
		});
	});
});
</script>

<article class="row">
  <div class="contentWrapper">
        	<div class="guide_form">
	        	<br><br><br><br>
	        	<c:forEach var="dto" items="${list}">
					<div class="guidebox">
			        	<a href="#" class="boxcontent">
			        		<br><br>
			        		<span class="guideicon">${dto.faqIcon}</span>

			        		<span>${dto.faqSubject}</span><br><br><br><br><br>
			        		<span>${dto.memberNickname}</span>&nbsp;&nbsp;&nbsp;<span style="float: right;">${dto.faqCreated}</span>
			        	</a>
			       	</div>
			       	<div class="guideinfo">
						<c:if test="${sessionScope.member.memberNo==1}">
							<div align="right">
					        	<button type="button" onclick="updateGuide('${dto.faqNo}');" class="bbuuttoon">수정</button>
					        	<button type="button" onclick="deleteGuide('${dto.faqNo}');" class="bbuuttoon">삭제</button>
				        	</div>
			        	</c:if>
			       		<span class="faqwpahr"><i class="far fa-lightbulb"></i> &nbsp; ${dto.faqSubject}</span><br>
			        	<span class="faqekqrmf">${dto.faqContent}</span><br>
			        	<div>
							<img alt="" src="<%=cp%>/uploads/guide/${dto.faqFilename}" width="180" height="90">
						</div>
			       	</div>
		       	</c:forEach>
		       	<c:if test="${sessionScope.member.memberNo==1}">
			       	<div align="right">
			       		<button type="button" onclick="javascript:location.href='<%=cp%>/guide/createguide'" class="bbuuttoon" style="margin-right: 88px; margin-top: 20px;">등록하기</button>
			       	</div>
		       	</c:if>
	       	</div>
        </div>
</article>