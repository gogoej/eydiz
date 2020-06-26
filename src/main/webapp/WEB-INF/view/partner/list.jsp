<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
   String cp = request.getContextPath();
%>

<script type="text/javascript">
function searchList() {
	var f = document.partnerSerchForm;
	f.submit();
}




</script>
		<div class = "partnerBannerImage" >
        	<div class="awardsTitle">
        		<div class = "patnerBannerTitle">이디즈 파트너</div>
        		<div class = "TitleBannerSubText">
        		   <p>이디즈에서는 누구나 투자받고 투자하면서 함께 성장합니다.</p><br>
				   <p>1%의 특별한 소수가 아닌 99%의 다양한 사람들이 더불어 성장할 수 있는 곳,</p><br>
				   <p class="patnerBannerPoint">이디즈에서 다양한 분야에서 도전하는 메이커를 찾아보세요</p><br><br>
				</div>
        	</div>
        </div>
		<div class="rowFull">
			<article class="row">
		        <div class="partnerResult">
		        	<div class="pResultWrap">
		        		<p class = "partnerContentText prTitle">이디즈 파트너가 함께 만들어가는 성과</p>
		        		<p class="partnerContentText prContent">이디즈의 파트너는 펀딩형 크라우드 펀딩에 도전하고 있으며, 괄목할 만한 성과를 만들어가고 있습니다.</p>
		        	</div>
		        </div>
          	</article>
          </div>
          <article class="row">
            <div class="rowInner awardContentBody">
              <ul>
               <li class="item partnerResultCell">
                  <div class="itemInner">
   					  <div class="partnerResultIcon" style="background-image: url('../resource/images/partner/iconmonstr-handshake-7-240.png');"></div>
                      <div class="subject partnerResultText">
                        <p>이디즈와 함께한 파트너</p>
                        <span class="partnerResultTextPoint">${totalBrandCount}</span><span> 브랜드</span>
                      </div>
                  </div>
                </li>
               <li class="item partnerResultCell">
                  <div class="itemInner">
                    <div class="partnerResultIcon" style="background-image: url('../resource/images/partner/iconmonstr-script-5-240.png');"></div>
                      <div class="subject partnerResultText">
                        <p>파트너가 진행한 프로젝트</p>
                        <span class="partnerResultTextPoint">${totalProjectCount}</span><span> 건</span>
                      </div>
                  </div>
                </li>
               <li class="item partnerResultCell">
                  <div class="itemInner">
                    <div class="partnerResultIcon" style="background-image: url('../resource/images/partner/iconmonstr-party-21-120.png');"></div>
                      <div class="subject partnerResultText">
                        <p>프로젝트에 참여한 투자자</p>
                        <span class="partnerResultTextPoint">${totalBuyMemberCount}</span><span> 명</span>
                      </div>
                  </div>
                </li>
               <li class="item partnerResultCell partnerStandard">
                  <p> * 이디즈 파트너의 총 합산 기록입니다. (${today})</p>
                </li>
              </ul>
            </div>
          </article>
          <article class="row partnerListContentBody">
	        <div class="rowInner">
	          <div class = "partnerListTitle">
	          	<p>이디즈와 함께하는 사람들</p>
	          </div>
	          <form name="partnerSerchForm" method="post">
		          <div class = "partnerSearchBox">
		          	<input type="text" name="keyword" id = "partnerSerchKeyword" class= "partnerSearch" value="${keyword}" placeholder=" 브랜드이름을 입력하세요">
					<button type="button" class = "partnerSearchBtn" onclick="searchList();"></button>
					<button type="button" class = "partnerReloadBtn" onclick="location.href='<%=cp%>/partner/list';"></button>
		          </div>
	          </form>
              <ul class="gridContent partnerListContent">
                <c:forEach var="dto" items="${list}">
	                <li class="item partnerBox" onclick="location.href='${articleUrl}&brandNo=${dto.brandNo}';">
	                  <div class="itemInner partnerBBoard">
	                    <div class="thumbnail"style="background-image: url('dto.memberImageUrl');">
	                      <span class="hidden">드럼</span>
	                    </div>
	                    <div class="textWrap ">
	                      <div class="subject awardsTabBName">
	                        <span>${dto.brandName}</span>
	                      </div>
	                      <div class = "partnerBDetail">
	                      	<span>총 펀딩금액 </span><span class="partnerBData"><fmt:formatNumber value="${dto.brandTotalAmount}"/></span><span>원</span><br>
	                      	<span>프로젝트 </span><span class="partnerBData"> ${dto.brandTotalProject}</span><span>건</span><br>
	                      	<span>투자자 </span><span class="partnerBData"> <fmt:formatNumber value="${dto.brandTotalBuyMember}"/></span><span>명</span><br>
	                      </div>
	                    </div>
	                  </div>
	                </li>
                </c:forEach>
              </ul>
              <div class = "partnerPaging">
              	${dataCount==0?"등록된 브랜드가 존재하지 않습니다.":paging}
              </div>
            </div>
            </article>
            <div class = "partnerMakePJ">
          	<p>이디즈에서는 누구나</p>
          	<p>성공의 주인공이 될 수 있습니다.</p>
          	<p>지금 바로 펀딩에 도전하세요!</p>
   			</div>
