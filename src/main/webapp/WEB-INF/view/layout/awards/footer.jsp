<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
   String cp = request.getContextPath();
%>
<footer id="footer">
  <div class="footerFull">
  	<div class = "awardMakePJ">
	      <p>세상을 만들어가는 당신,</p>
	      <p>지금, 펀딩프로젝트를 신청하세요.</p>
	      <p>당신의 아이디어를 세상에 소개하고, 자금을 모집하도록 도와드립니다.</p>
	      <button class = "pjBtn" type="button" onclick="">프로젝트 오픈신청</button>
  </div>
    <ul class="fnb">
      <li>
        <a href="<%=cp %>/news/main"><span>공지사항</span></a>
      </li>
      <li>
        <a href="<%=cp%>/mate/main"><span>펀딩메이트</span></a>
      </li>
      <li>
        <a href="<%=cp%>/school/main"><span>이디즈 스쿨</span></a>
      </li>
      <li>
        <a href="<%=cp%>/guide/main"><span>이용 가이드</span></a>
      </li>
      <li>
        <a href="<%=cp%>/story/story"><span>스토리</span></a>
      </li>
      <li>
        <a href="<%=cp%>/cast/news"><span>캐스트</span></a>
      </li>
      <li>
        <a href="<%=cp%>/admin/"><span>관리자의 방</span></a>
      </li>
    </ul>
  </div>
  <div class="footerInner">
    <%--div class="footerInfo">
      <div class="info1">
        <p class="title">대표 고객센터</p>
        <p class="tel">1577-0000</p>
      </div>
    </div --%>
  </div>
  <div class="footerWarning">
    <div class="footerWarningInner">
      <p class="title">투자 위험고지</p>
      <p class="desc">
        비상장기업 투자는 원금 손실의 가능성이 크니 투자 위험 안내를 꼭 확인하세요.
        OOO플랫폼(주)는 크라우드 펀딩 플랫폼을 제공하는 중개자로 자금을 모집하는 당사자가
        아니며, 투자손실의 위험을 보전하거나 보상품 제공을 보장해 드리지 못합니다.
      </p>
    </div>
  </div>
</footer>