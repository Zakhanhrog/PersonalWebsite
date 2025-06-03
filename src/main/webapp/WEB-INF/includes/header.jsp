<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${pageTitle ne null ? pageTitle : 'Website Cá Nhân'}"/></title>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" integrity="sha512-9usAa10IRO0HhonpyAIVpjrylPvoDwiPUiKdWk5t3PyolY1cOd4DSE0Ga+ri4AuTroPR5aQvXU9xC6qOPnzFeg==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-9ndCyUaIbzAi2FUVXJi0CjmCapSmO7SnpJef0486qhLnuZ2cdeRhO02iuK6FUUVM" crossorigin="anonymous">
    <link rel="stylesheet" href="https://unpkg.com/aos@next/dist/aos.css" />

    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">

</head>
<body>
<header class="header-area">
    <div class="container">
        <nav class="navbar navbar-expand-lg navbar-light">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/">
                    <span class="logo-text">
                        <c:set var="profileName" value="${profile.name}"/>
                        <c:if test="${not empty profileName}">
                            <c:set var="nameParts" value="${fn:split(profileName, ' ')}" />
                            <c:set var="firstNameInitial" value="${fn:substring(nameParts[0], 0, 1)}" />
                            <c:set var="lastNameInitial" value="" />
                            <c:if test="${fn:length(nameParts) > 1}">
                                <c:set var="lastNameInitial" value="${fn:substring(nameParts[fn:length(nameParts)-1], 0, 1)}" />
                            </c:if>
                            <c:out value="${firstNameInitial}${lastNameInitial}"/>
                        </c:if>
                        <c:if test="${empty profileName}">NTB</c:if>
                    </span>
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ms-auto">
                    <li class="nav-item">
                        <a class="nav-link ${activePage == 'home' ? 'active' : ''}" href="${pageContext.request.contextPath}/">
                            <i class="fas fa-home"></i> Trang Chủ
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link ${activePage == 'about' ? 'active' : ''}" href="${pageContext.request.contextPath}/about">
                            <i class="fas fa-user"></i> Giới Thiệu
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link ${activePage == 'projects' ? 'active' : ''}" href="${pageContext.request.contextPath}/projects">
                            <i class="fas fa-briefcase"></i> Dự Án
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link ${activePage == 'blog' ? 'active' : ''}" href="${pageContext.request.contextPath}/blog">
                            <i class="fas fa-newspaper"></i> Blog
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link ${activePage == 'contact' ? 'active' : ''}" href="${pageContext.request.contextPath}/contact">
                            <i class="fas fa-envelope"></i> Liên Hệ
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link ${activePage == 'admin' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin">
                            <i class="fas fa-user-shield"></i> Admin
                        </a>
                    </li>
                </ul>
            </div>
        </nav>
    </div>
</header>

<div id="preloader">
    <div class="loader">
        <div class="spinner">
            <div class="double-bounce1"></div>
            <div class="double-bounce2"></div>
        </div>
        <h3 class="loading-text">Đang tải...</h3>
    </div>
</div>