<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="/WEB-INF/includes/header.jsp" />

<!-- Page Banner -->
<section class="page-banner">
    <div class="container">
        <div class="page-banner-content text-center">
            <h1 data-aos="fade-up">Dự Án</h1>
            <div class="breadcrumbs" data-aos="fade-up" data-aos-delay="100">
                <ul>
                    <li><a href="${pageContext.request.contextPath}/">Trang Chủ</a></li>
                    <li><span>Dự Án</span></li>
                </ul>
            </div>
        </div>
    </div>
</section>

<!-- Projects Section -->
<section class="projects-area section-padding">
    <div class="container">
        <!-- Project Filter -->
        <c:if test="${not empty projectCategories && fn:length(projectCategories) > 1}">
            <div class="project-filter-bar text-center" data-aos="fade-up">
                <ul class="project-filter-list">
                    <li class="${currentCategoryFilter == '*' || empty currentCategoryFilter ? 'active' : ''}">
                        <a href="${pageContext.request.contextPath}/projects?category=*">Tất cả</a>
                    </li>
                    <c:forEach items="${projectCategories}" var="category">
                        <li class="${currentCategoryFilter == category ? 'active' : ''}">
                            <a href="${pageContext.request.contextPath}/projects?category=${fn:escapeXml(category)}"><c:out value="${category}"/></a>
                        </li>
                    </c:forEach>
                </ul>
            </div>
        </c:if>

        <!-- Projects Grid -->
        <div class="row">
            <c:choose>
                <c:when test="${not empty projects}">
                    <c:forEach items="${projects}" var="project" varStatus="loop">
                        <div class="col-lg-4 col-md-6 mb-4 project-card-wrapper" data-aos="fade-up" data-aos-delay="${100 * (loop.index % 3)}">
                            <div class="project-item">
                                <div class="project-img">
                                    <img src="${pageContext.request.contextPath}${not empty project.imageUrl ? project.imageUrl : '/resources/images/default-project.jpg'}"
                                         alt="<c:out value='${project.title}'/>"
                                         onerror="this.src='${pageContext.request.contextPath}/resources/images/default-project-placeholder.jpg'; this.onerror=null;">
                                    <div class="project-overlay">
                                        <a href="${pageContext.request.contextPath}/projects/detail?id=${project.id}" class="project-overlay-icon" aria-label="Xem chi tiết dự án ${project.title}">
                                            <i class="fas fa-plus"></i>
                                        </a>
                                    </div>
                                </div>
                                <div class="project-content">
                                    <span class="project-category"><c:out value="${project.category}"/></span>
                                    <h4 class="project-title">
                                        <a href="${pageContext.request.contextPath}/projects/detail?id=${project.id}"><c:out value="${project.title}"/></a>
                                    </h4>
                                    <p class="project-description">
                                        <c:choose>
                                            <c:when test="${fn:length(project.description) > 120}">
                                                <c:out value="${fn:substring(project.description, 0, 120)}"/>...
                                            </c:when>
                                            <c:otherwise>
                                                <c:out value="${project.description}"/>
                                            </c:otherwise>
                                        </c:choose>
                                    </p>
                                    <div class="project-meta">
                                        <span><i class="fas fa-map-marker-alt"></i> <c:out value="${project.location}"/></span>
                                        <span class="project-status status-${fn:toLowerCase(fn:replace(project.status, ' ', '-'))}"><c:out value="${project.status}"/></span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <div class="col-12">
                        <div class="alert alert-info text-center" role="alert" data-aos="fade-up">
                            Hiện tại chưa có dự án nào phù hợp với tiêu chí của bạn.
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <%-- Pagination Placeholder --%>
        <%--
        <c:if test="${totalPages > 1}">
            <nav aria-label="Page navigation" class="mt-5" data-aos="fade-up">
                <ul class="pagination justify-content-center">
                    <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                        <a class="page-link" href="${pageContext.request.contextPath}/projects?page=${currentPage - 1}<c:if test="${not empty currentCategoryFilter}">&category=${currentCategoryFilter}</c:if>">Trước</a>
                    </li>
                    <c:forEach begin="1" end="${totalPages}" var="i">
                        <li class="page-item ${currentPage == i ? 'active' : ''}">
                            <a class="page-link" href="${pageContext.request.contextPath}/projects?page=${i}<c:if test="${not empty currentCategoryFilter}">&category=${currentCategoryFilter}</c:if>">${i}</a>
                        </li>
                    </c:forEach>
                    <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                        <a class="page-link" href="${pageContext.request.contextPath}/projects?page=${currentPage + 1}<c:if test="${not empty currentCategoryFilter}">&category=${currentCategoryFilter}</c:if>">Sau</a>
                    </li>
                </ul>
            </nav>
        </c:if>
        --%>
    </div>
</section>

<!-- Call to Action Section -->
<section class="contact-cta section-padding-small">
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-lg-8 text-center">
                <h2 data-aos="fade-up">Quan Tâm Đến Một Dự Án?</h2>
                <p data-aos="fade-up" data-aos-delay="100" class="lead">
                    Liên hệ với chúng tôi để được tư vấn chi tiết và tìm hiểu các cơ hội đầu tư hấp dẫn.
                </p>
                <a href="${pageContext.request.contextPath}/contact" class="btn btn-primary btn-lg mt-3" data-aos="fade-up" data-aos-delay="200">Liên Hệ Tư Vấn</a>
            </div>
        </div>
    </div>
</section>

<jsp:include page="/WEB-INF/includes/footer.jsp" />