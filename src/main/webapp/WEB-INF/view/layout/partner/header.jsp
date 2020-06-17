﻿<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
   String cp = request.getContextPath();
%>
      <header id="header">
        <div class="headerInner">
          <h1 class="logo"><a href="#">eydiz</a></h1>
          <ul class="gnbExtended">
            <li> 
              <a href="#" class="menu on"><span>펀딩하기</span></a>
            </li>
            <li>
              <a href="#" class="menu"><span> 기부하기</span></a>
            </li>
          </ul>
          <div class="headerInnerRight">
            <ul class="gnbAuth">
              <li><a href="#" class="btnSubmit bordering btnMakeProject">프로젝트 만들기</a></li>
          	<c:if test="${empty sessionScope.member}">
              <li><a href="<%=cp%>/member/join">회원가입</a></li>
              <li><a href="<%=cp%>/member/login">로그인</a></li>
          	</c:if>
          	<c:if test="${not empty sessionScope.member}">
				<li><a href="<%=cp%>/member/logout">로그아웃</a><li>
				<li><a href="#" class="profilePic">${sessionScope.member.memberNickname}님</a></li>
          	</c:if>
          	</ul>
          </div>
        </div>
        <div class="headerInnerMobile">
          <div class="mobileNav">
            <h2 class="title">Eydiz Partner</h2>
            <button type="button" class="btnBack btnFlat btnMobileNav">
              <span class="hidden">이전</span>
            </button>
            <button type="button" class="btnHome btnFlat btnMobileNav">
              <span class="hidden">홈</span>
            </button>
          </div>
        </div>
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
      </header>