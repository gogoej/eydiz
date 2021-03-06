<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
	String cp=request.getContextPath();
%>
<!DOCTYPE html>
<html lang="ko">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>크라우드 펀딩</title>
    <link rel="stylesheet" href="<%=cp%>/resource/css/studio/reset.css" />
    <link rel="stylesheet" href="<%=cp%>/resource/css/studio/layout.css" />

    <!--[if lt IE 9]>
      <script src="resource/js//html5shiv.min.js"></script>
    <![endif]-->
    <script src="resource/js/jquery-3.5.1.min.js"></script>
  </head>
  <body>
    <div id="wrap">
      <div class="overlay"></div>
      <header>
        <h1 class="logoCenter"><a href="<%=cp %>/main">eydiz</a></h1>
      </header>
      <main id="content">
        <div class="contentWrapper">
          <div class="centerInner">
            <article class="row">
              <form name="<%=cp %>/studio/brand/info" method="post">
                <h2>브랜드 만들기</h2>
                <p class="description">
                  안녕하세요. 본격적으로 프로젝트 작성을 시작하기 전에 간단한 정보를 입력하세요
                </p>

                <div class="infoItemWrap">
                  <div class="infoItem required">
                    <div class="infoTitle"><span>브랜드 이름</span></div>
                    <p class="desc">펀딩이나 후원을 하며 알릴 브랜드의 이름을 입력하세요</p>
                    <div class="inputWrap">
                      <input type="text" id="brandName" name="brandName" />
                    </div>
                  </div>

                  <div class="infoItem">
                    <div class="infoTitle"><span>관리자 이름</span></div>
                    <p class="desc">프로젝트를 담당할 관리자의 이름을 입력하세요.</p>
                    <div class="inputWrap">
                      <input type="text" id="managerName" name="managerName" />
                    </div>
                  </div>
                  <div class="infoItem">
                    <div class="infoTitle"><span>관리자 이메일</span></div>
                    <p class="desc">프로젝트를 담당할 관리자의 이메일을 입력하세요.</p>
                    <div class="inputWrap">
                      <input type="text" id="managerEmail" name="managerEmail" />
                    </div>
                  </div>
                  <div class="infoItem">
                    <div class="infoTitle"><span>관리자 연락처</span></div>
                    <p class="desc">프로젝트를 담당할 관리자의 연락처를 입력하세요.</p>
                    <div class="inputWrap">
                      <input type="text" id="managerPhone" name="managerPhone" />
                    </div>
                  </div>
                </div>
                <div class="submitContainer">
                  <button type="submit" class="btnSubmit">시작하기</button>
                </div>
              </form>
            </article>
          </div>
        </div>
      </main>
    </div>
  </body>
</html>
