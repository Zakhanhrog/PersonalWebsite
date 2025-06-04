<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="/WEB-INF/includes/header.jsp" />

<!-- Page Banner -->
<section class="page-banner">
    <div class="container">
        <div class="page-banner-content text-center">
            <h1 data-aos="fade-up">Blog</h1>
        </div>
    </div>
</section>

<!-- Blog Section -->
<section class="blog-area section-padding">
    <div class="container">
        <div class="row">
            <!-- Blog Posts -->
            <div class="col-lg-8">
                <c:choose>
                    <c:when test="${not empty blogPosts}">
                        <c:forEach items="${blogPosts}" var="post" varStatus="loop">
                            <div class="blog-post-item" data-aos="fade-up" data-aos-delay="${100 * (loop.index % 3)}">
                                <div class="blog-post-img">
                                    <a href="${pageContext.request.contextPath}/blog/post?id=${post.id}">
                                            <%-- SỬA Ở ĐÂY: Bỏ pageContext.request.contextPath nếu imageUrl là đường dẫn ảo đầy đủ --%>
                                        <img src="${not empty post.imageUrl ? post.imageUrl : pageContext.request.contextPath += '/resources/images/default-blog.jpg'}"
                                             alt="<c:out value='${post.title}'/>"
                                             onerror="this.src='${pageContext.request.contextPath}/resources/images/default-blog-placeholder.jpg'; this.onerror=null;">
                                    </a>
                                </div>
                                <div class="blog-post-content">
                                    <div class="blog-post-meta">
                                        <span><i class="fas fa-user"></i> <c:out value="${post.author}"/></span>
                                        <span><i class="fas fa-calendar-alt"></i> <fmt:formatDate value="${post.createdDate}" pattern="dd/MM/yyyy" /></span>
                                        <c:if test="${not empty post.category}">
                                            <span><i class="fas fa-folder"></i> <a href="${pageContext.request.contextPath}/blog?category=${fn:escapeXml(post.category)}"><c:out value="${post.category}"/></a></span>
                                        </c:if>
                                    </div>
                                    <h3 class="blog-post-title">
                                        <a href="${pageContext.request.contextPath}/blog/post?id=${post.id}"><c:out value="${post.title}"/></a>
                                    </h3>
                                    <p class="blog-post-summary">
                                        <c:out value="${post.summary}"/>
                                    </p>
                                    <a href="${pageContext.request.contextPath}/blog/post?id=${post.id}" class="btn btn-primary btn-sm read-more-btn">Đọc Thêm <i class="fas fa-arrow-right ms-1"></i></a>
                                </div>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div class="alert alert-info text-center" role="alert" data-aos="fade-up">
                            Chưa có bài viết nào trong mục này.
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Sidebar -->
            <div class="col-lg-4">
                <aside class="blog-sidebar">
                    <c:if test="${not empty profile}">
                        <div class="sidebar-widget author-widget" data-aos="fade-left" data-aos-delay="100">
                            <div class="author-widget-img">
                                <img src="${not empty profile.photoUrl ? profile.photoUrl : pageContext.request.contextPath += '/resources/images/default-profile-sidebar.jpg'}"
                                     alt="Ảnh của ${profile.name}"
                                     onerror="this.src='${pageContext.request.contextPath}/resources/images/default-profile-sidebar-placeholder.jpg'; this.onerror=null;">
                            </div>
                            <h4><c:out value="${profile.name}"/></h4>
                            <p><c:out value="${profile.position}"/></p>
                            <div class="social-icons">
                                <a href="#" aria-label="Facebook"><i class="fab fa-facebook-f"></i></a>
                                <a href="#" aria-label="Twitter"><i class="fab fa-twitter"></i></a>
                                <a href="#" aria-label="LinkedIn"><i class="fab fa-linkedin-in"></i></a>
                            </div>
                        </div>
                    </c:if>

                    <c:if test="${not empty categoriesCount}">
                        <div class="sidebar-widget category-widget" data-aos="fade-left" data-aos-delay="200">
                            <h4 class="widget-title">Danh Mục</h4>
                            <ul>
                                <c:forEach items="${categoriesCount}" var="catEntry">
                                    <li>
                                        <a href="${pageContext.request.contextPath}/blog?category=${fn:escapeXml(catEntry.key)}">
                                            <i class="fas fa-angle-right"></i> <c:out value="${catEntry.key}"/> <span>(<c:out value="${catEntry.value}"/>)</span>
                                        </a>
                                    </li>
                                </c:forEach>
                            </ul>
                        </div>
                    </c:if>

                    <c:if test="${not empty recentPosts}">
                        <div class="sidebar-widget recent-posts-widget" data-aos="fade-left" data-aos-delay="300">
                            <h4 class="widget-title">Bài Viết Mới</h4>
                            <ul>
                                <c:forEach items="${recentPosts}" var="recentPost">
                                    <li>
                                        <div class="recent-post-item">
                                            <div class="recent-post-img">
                                                <a href="${pageContext.request.contextPath}/blog/post?id=${recentPost.id}">
                                                        <%-- SỬA Ở ĐÂY: Bỏ pageContext.request.contextPath nếu imageUrl là đường dẫn ảo đầy đủ --%>
                                                    <img src="${not empty recentPost.imageUrl ? recentPost.imageUrl : pageContext.request.contextPath += '/resources/images/default-blog-thumb.jpg'}"
                                                         alt="<c:out value='${recentPost.title}'/>"
                                                         onerror="this.src='${pageContext.request.contextPath}/resources/images/default-blog-thumb-placeholder.jpg'; this.onerror=null;">
                                                </a>
                                            </div>
                                            <div class="recent-post-content">
                                                <h5><a href="${pageContext.request.contextPath}/blog/post?id=${recentPost.id}"><c:out value="${recentPost.title}"/></a></h5>
                                                <span><fmt:formatDate value="${recentPost.createdDate}" pattern="dd MMM, yyyy"/></span>
                                            </div>
                                        </div>
                                    </li>
                                </c:forEach>
                            </ul>
                        </div>
                    </c:if>

                    <c:if test="${not empty allTags}">
                        <div class="sidebar-widget tags-widget" data-aos="fade-left" data-aos-delay="400">
                            <h4 class="widget-title">Tags</h4>
                            <div class="tagcloud">
                                <c:forEach items="${allTags}" var="tag">
                                    <a href="${pageContext.request.contextPath}/blog?tag=${fn:escapeXml(tag)}"><c:out value="${tag}"/></a>
                                </c:forEach>
                            </div>
                        </div>
                    </c:if>
                </aside>
            </div>
        </div>
    </div>
</section>

<jsp:include page="/WEB-INF/includes/footer.jsp" />