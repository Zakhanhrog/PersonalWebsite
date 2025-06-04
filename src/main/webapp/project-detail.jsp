<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="/WEB-INF/includes/header.jsp" />

<c:if test="${not empty project}">
    <!-- Page Banner -->
    <section class="page-banner">
        <div class="container">
            <div class="page-banner-content text-center">
                <h1 data-aos="fade-up"><c:out value="${project.title}"/></h1>
            </div>
        </div>
    </section>

    <!-- Project Detail Section -->
    <section class="project-detail-area section-padding">
        <div class="container">
            <div class="row">
                <div class="col-lg-8">
                    <div class="project-main-image" data-aos="fade-up">
                        <img src="${not empty project.imageUrl ? (project.imageUrl.startsWith('http') ? project.imageUrl : pageContext.request.contextPath.concat(project.imageUrl)) : pageContext.request.contextPath.concat('/resources/images/default-project-large.jpg')}"
                             alt="<c:out value='${project.title}'/>"
                             onerror="this.src='${pageContext.request.contextPath}/resources/images/default-project-large-placeholder.jpg'; this.onerror=null;">
                    </div>

                    <div class="project-main-content mt-5" data-aos="fade-up" data-aos-delay="200">
                        <h2 class="project-content-title"><c:out value="${project.title}"/></h2>
                        <div class="project-meta-info">
                            <span><i class="fas fa-user"></i> <strong>Khách hàng:</strong> <c:out value="${project.client}"/></span>
                            <span><i class="fas fa-map-marker-alt"></i> <strong>Vị trí:</strong> <c:out value="${project.location}"/></span>
                            <span><i class="fas fa-folder"></i> <strong>Loại:</strong> <c:out value="${project.category}"/></span>
                            <span class="project-status status-${fn:toLowerCase(fn:replace(project.status, ' ', '-'))}"><i class="fas fa-tasks"></i> <strong>Trạng thái:</strong> <c:out value="${project.status}"/></span>
                        </div>
                        <div class="project-description-full mt-4" style="white-space: pre-line;">
                            <c:out value="${project.description}" escapeXml="false"/>
                        </div>
                        <div class="project-share mt-5">
                            <h5 class="share-title">Chia sẻ dự án này:</h5>
                            <div class="social-share-icons">
                                <a href="https://www.facebook.com/sharer/sharer.php?u=${pageContext.request.requestURL}?id=${project.id}" target="_blank" class="social-icon facebook" aria-label="Share on Facebook"><i class="fab fa-facebook-f"></i></a>
                                <a href="https://twitter.com/intent/tweet?url=${pageContext.request.requestURL}?id=${project.id}&text=${fn:escapeXml(project.title)}" target="_blank" class="social-icon twitter" aria-label="Share on Twitter"><i class="fab fa-twitter"></i></a>
                                <a href="https://www.linkedin.com/shareArticle?mini=true&url=${pageContext.request.requestURL}?id=${project.id}&title=${fn:escapeXml(project.title)}&summary=${fn:escapeXml(fn:substring(project.description, 0, 100))}" target="_blank" class="social-icon linkedin" aria-label="Share on LinkedIn"><i class="fab fa-linkedin-in"></i></a>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="col-lg-4">
                    <aside class="project-sidebar">
                        <div class="sidebar-widget project-info-widget" data-aos="fade-left" data-aos-delay="100">
                            <h3 class="widget-title">Thông Tin Dự Án</h3>
                            <ul>
                                <li><i class="fas fa-calendar"></i><div><strong>Bắt đầu:</strong> <fmt:formatDate value="${project.startDate}" pattern="dd/MM/yyyy" /></div></li>
                                <c:if test="${not empty project.endDate}"><li><i class="fas fa-calendar-check"></i><div><strong>Hoàn thành:</strong> <fmt:formatDate value="${project.endDate}" pattern="dd/MM/yyyy" /></div></li></c:if>
                                <li><i class="fas fa-map-marker-alt"></i><div><strong>Địa điểm:</strong> <c:out value="${project.location}"/></div></li>
                                <li><i class="fas fa-user"></i><div><strong>Chủ đầu tư/Khách hàng:</strong> <c:out value="${project.client}"/></div></li>
                                <li><i class="fas fa-folder-open"></i><div><strong>Hạng mục:</strong> <c:out value="${project.category}"/></div></li>
                                <li class="project-status-sidebar status-${fn:toLowerCase(fn:replace(project.status, ' ', '-'))}"><i class="fas fa-info-circle"></i><div><strong>Trạng thái:</strong> <c:out value="${project.status}"/></div></li>
                                <c:if test="${not empty project.link && project.link ne '#'}"><li><i class="fas fa-link"></i><div><strong>Website dự án:</strong> <a href="${fn:escapeXml(project.link)}" target="_blank" rel="noopener noreferrer">Truy cập</a></div></li></c:if>
                            </ul>
                        </div>
                        <div class="sidebar-widget contact-form-widget" data-aos="fade-left" data-aos-delay="200">
                            <h3 class="widget-title">Yêu Cầu Tư Vấn</h3>
                            <form action="${pageContext.request.contextPath}/contact" method="post"><input type="hidden" name="subject" value="Yêu cầu tư vấn dự án: ${fn:escapeXml(project.title)}"><div class="form-group"><input type="text" name="name" class="form-control" placeholder="Họ tên của bạn *" required></div><div class="form-group"><input type="email" name="email" class="form-control" placeholder="Email của bạn *" required></div><div class="form-group"><input type="tel" name="phone" class="form-control" placeholder="Số điện thoại"></div><div class="form-group"><textarea name="message" class="form-control" rows="3" placeholder="Nội dung yêu cầu..."></textarea></div><button type="submit" class="btn btn-primary w-100">Gửi Yêu Cầu</button></form>
                        </div>
                        <c:if test="${not empty relatedProjects}">
                            <div class="sidebar-widget related-projects-widget" data-aos="fade-left" data-aos-delay="300">
                                <h3 class="widget-title">Dự Án Liên Quan</h3>
                                <ul>
                                    <c:forEach items="${relatedProjects}" var="related">
                                        <li>
                                            <div class="related-project-item">
                                                <div class="related-project-img">
                                                    <a href="${pageContext.request.contextPath}/projects/detail?id=${related.id}">
                                                            <%-- SỬA Ở ĐÂY --%>
                                                        <img src="${not empty related.imageUrl ? (related.imageUrl.startsWith('http') ? related.imageUrl : pageContext.request.contextPath.concat(related.imageUrl)) : pageContext.request.contextPath.concat('/resources/images/default-project-thumb.jpg')}"
                                                             alt="<c:out value='${related.title}'/>"
                                                             onerror="this.src='${pageContext.request.contextPath}/resources/images/default-project-thumb-placeholder.jpg'; this.onerror=null;">
                                                    </a>
                                                </div>
                                                <div class="related-project-content">
                                                    <h5><a href="${pageContext.request.contextPath}/projects/detail?id=${related.id}"><c:out value="${related.title}"/></a></h5>
                                                    <span><c:out value="${related.category}"/></span>
                                                </div>
                                            </div>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </div>
                        </c:if>
                    </aside>
                </div>
            </div>
        </div>
    </section>
</c:if>
<c:if test="${empty project}">
    <section class="section-padding">
        <div class="container">
            <div class="alert alert-warning text-center" role="alert">
                Dự án bạn tìm kiếm không tồn tại hoặc đã bị xóa.
                <a href="${pageContext.request.contextPath}/projects" class="alert-link">Quay lại trang Dự án</a>.
            </div>
        </div>
    </section>
</c:if>

<jsp:include page="/WEB-INF/includes/footer.jsp" />