<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:include page="/WEB-INF/includes/header.jsp" />
<c:if test="${not empty blogPost}">
    <!-- Page Banner -->
    <section class="page-banner">
        <div class="container">
            <div class="page-banner-content text-center">
                <h1 data-aos="fade-up">
                    <c:out value="${blogPost.title}"/>
                </h1>
            </div>
        </div>
    </section>
    <!-- Blog Detail Section -->
    <section class="blog-detail-area section-padding">
        <div class="container">
            <div class="row">
                <div class="col-lg-8">
                    <article class="blog-post-full" data-aos="fade-up">
                        <div class="blog-post-full-img">
                            <img src="${not empty blogPost.imageUrl ? (blogPost.imageUrl.startsWith('http') ? blogPost.imageUrl : pageContext.request.contextPath.concat(blogPost.imageUrl)) : pageContext.request.contextPath.concat('/resources/images/default-blog-large.jpg')}"
                                 alt="<c:out value='${blogPost.title}'/>"
                                 onerror="this.src='${pageContext.request.contextPath}/resources/images/default-blog-large-placeholder.jpg'; this.onerror=null;">
                        </div>
                        <div class="blog-post-full-content">
                            <div class="blog-post-meta">
                        <span>
                           <i class="fas fa-user"></i>
                           <c:out value="${blogPost.author}"/>
                        </span>
                                <span>
                           <i class="fas fa-calendar-alt"></i>
                           <fmt:formatDate value="${blogPost.createdDate}" pattern="dd/MM/yyyy HH:mm" />
                        </span>
                                <c:if test="${not empty blogPost.category}">
                           <span>
                              <i class="fas fa-folder"></i>
                              <a href="${pageContext.request.contextPath}/blog?category=${fn:escapeXml(blogPost.category)}">
                                 <c:out value="${blogPost.category}"/>
                              </a>
                           </span>
                                </c:if>
                            </div>
                            <h2 class="blog-post-full-title">
                                <c:out value="${blogPost.title}"/>
                            </h2>
                            <div class="blog-post-full-text" style="white-space: pre-line;">
                                <c:out value="${blogPost.content}" escapeXml="false"/>
                            </div>
                            <c:if test="${not empty blogPost.tags}">
                                <div class="blog-post-tags">
                                    <h5 class="tags-title">Tags:</h5>
                                    <div class="tagcloud">
                                        <c:forTokens items="${blogPost.tags}" delims="," var="tag">
                                            <a href="${pageContext.request.contextPath}/blog?tag=${fn:escapeXml(fn:trim(tag))}">
                                                <c:out value="${fn:trim(tag)}"/>
                                            </a>
                                        </c:forTokens>
                                    </div>
                                </div>
                            </c:if>
                            <c:if test="${not empty profile}">
                                <div class="blog-post-author-box">
                                    <div class="author-img">
                                            <%-- SỬA Ở ĐÂY --%>
                                        <img src="${not empty profile.photoUrl ? (profile.photoUrl.startsWith('http') ? profile.photoUrl : pageContext.request.contextPath.concat(profile.photoUrl)) : pageContext.request.contextPath.concat('/resources/images/default-profile-full.jpg')}"
                                             alt="Ảnh của ${profile.name}"
                                             onerror="this.src='${pageContext.request.contextPath}/resources/images/default-profile-author-placeholder.jpg'; this.onerror=null;">
                                    </div>
                                    <div class="author-content">
                                        <h4>
                                            <c:out value="${profile.name}"/>
                                        </h4>
                                        <p>
                                            <c:out value="${profile.position}"/>
                                        </p>
                                        <div class="social-icons">
                                            <a href="#" aria-label="Facebook"><i class="fab fa-facebook-f"></i></a>
                                            <a href="#" aria-label="Twitter"><i class="fab fa-twitter"></i></a>
                                            <a href="#" aria-label="LinkedIn"><i class="fab fa-linkedin-in"></i></a>
                                        </div>
                                    </div>
                                </div>
                            </c:if>
                            <div class="comments-area" id="comments">
                                <h3 class="comments-title">
                                    Bình Luận (
                                    <c:out value="${fn:length(commentsList) ne null ? fn:length(commentsList) : 0}"/>
                                    )
                                </h3>
                                <c:choose>
                                    <c:when test="${not empty commentsList}">
                                        <ul class="comment-list">
                                            <c:forEach items="${commentsList}" var="comment">
                                                <c:if test="${comment.parentCommentId == null}">
                                                    <li class="comment-item" id="comment-${comment.id}">
                                                        <div class="comment-author-img"><img src="https://ui-avatars.com/api/?name=${fn:replace(comment.authorName, ' ', '+')}&size=60&background=random" alt="<c:out value='${comment.authorName}'/>"></div>
                                                        <div class="comment-content">
                                                            <div class="comment-meta">
                                                                <strong>
                                                                    <c:out value="${comment.authorName}"/>
                                                                </strong>
                                                                <span>
                                                   <fmt:formatDate value="${comment.createdDate}" pattern="dd/MM/yyyy 'lúc' HH:mm"/>
                                                </span>
                                                            </div>
                                                            <p>
                                                                <c:out value="${comment.content}"/>
                                                            </p>
                                                        </div>
                                                    </li>
                                                    <c:forEach items="${commentsList}" var="reply">
                                                        <c:if test="${reply.parentCommentId == comment.id}">
                                                            <li class="comment-item comment-reply" id="comment-${reply.id}">
                                                                <div class="comment-author-img"><img src="https://ui-avatars.com/api/?name=${fn:replace(reply.authorName, ' ', '+')}&size=50&background=random" alt="<c:out value='${reply.authorName}'/>"></div>
                                                                <div class="comment-content">
                                                                    <div class="comment-meta">
                                                                        <strong>
                                                                            <c:out value="${reply.authorName}"/>
                                                                        </strong>
                                                                        <span>
                                                         <fmt:formatDate value="${reply.createdDate}" pattern="dd/MM/yyyy 'lúc' HH:mm"/>
                                                      </span>
                                                                        <em>
                                                                            trả lời
                                                                            <c:out value="${comment.authorName}"/>
                                                                        </em>
                                                                    </div>
                                                                    <p>
                                                                        <c:out value="${reply.content}"/>
                                                                    </p>
                                                                </div>
                                                            </li>
                                                        </c:if>
                                                    </c:forEach>
                                                </c:if>
                                            </c:forEach>
                                        </ul>
                                    </c:when>
                                    <c:otherwise>
                                        <p>Chưa có bình luận nào. Hãy là người đầu tiên bình luận!</p>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="comment-form-area" id="comment-form-area">
                                <h3 class="comment-form-title">Để lại bình luận <span id="replyingToInfo" style="font-size: 0.8em; color: #777;"></span></h3>
                                <c:if test="${param.commentError == 'validation'}">
                                    <div class="alert alert-danger alert-dismissible fade show" role="alert">Vui lòng điền đầy đủ thông tin bắt buộc (Tên, Nội dung).<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button></div>
                                </c:if>
                                <c:if test="${param.commentError == 'invalidPostId'}">
                                    <div class="alert alert-danger alert-dismissible fade show" role="alert">Lỗi: ID bài viết không hợp lệ.<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button></div>
                                </c:if>
                                <c:if test="${param.commentError == 'serverError'}">
                                    <div class="alert alert-danger alert-dismissible fade show" role="alert">Lỗi máy chủ khi gửi bình luận. Vui lòng thử lại.<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button></div>
                                </c:if>
                                <c:if test="${param.commentAdded == 'true'}">
                                    <div class="alert alert-success alert-dismissible fade show" role="alert">Bình luận của bạn đã được gửi thành công (có thể chờ duyệt).<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button></div>
                                </c:if>
                                <c:if test="${param.commentAdded == 'false' && empty param.commentError}">
                                    <div class="alert alert-danger alert-dismissible fade show" role="alert">Không thể gửi bình luận. Vui lòng thử lại.<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button></div>
                                </c:if>
                                <form action="${pageContext.request.contextPath}/blog/post" method="post" class="comment-form">
                                    <input type="hidden" name="action" value="addComment"><input type="hidden" name="postId" value="${blogPost.id}"><input type="hidden" name="parentCommentId" id="parentCommentIdField" value="">
                                    <div class="row">
                                        <div class="col-md-6 form-group mb-3"><label for="authorName" class="form-label">Họ tên *</label><input type="text" id="authorName" name="authorName" class="form-control" placeholder="Tên của bạn" required></div>
                                        <div class="col-md-6 form-group mb-3"><label for="authorEmail" class="form-label">Email (tùy chọn)</label><input type="email" id="authorEmail" name="authorEmail" class="form-control" placeholder="Email của bạn"></div>
                                        <div class="col-12 form-group mb-3"><label for="commentContent" class="form-label">Nội dung bình luận *</label><textarea id="commentContent" name="commentContent" class="form-control" rows="5" placeholder="Viết bình luận của bạn ở đây..." required></textarea></div>
                                        <div class="col-12"><button type="submit" class="btn btn-primary">Gửi Bình Luận</button><button type="button" id="cancelReplyButton" class="btn btn-secondary" style="display:none;" onclick="cancelReply()">Hủy Trả Lời</button></div>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </article>
                </div>
                <div class="col-lg-4">
                    <aside class="blog-sidebar">
                        <c:if test="${not empty profile}">
                            <div class="sidebar-widget author-widget" data-aos="fade-left" data-aos-delay="100">
                                <div class="author-widget-img">
                                    <img src="${not empty profile.photoUrl ? (profile.photoUrl.startsWith('http') ? profile.photoUrl : pageContext.request.contextPath.concat(profile.photoUrl)) : pageContext.request.contextPath.concat('/resources/images/default-profile-full.jpg')}"
                                <h4>
                                    <c:out value="${profile.name}"/>
                                </h4>
                                <p>
                                    <c:out value="${profile.position}"/>
                                </p>
                                <div class="social-icons"><a href="#" aria-label="Facebook"><i class="fab fa-facebook-f"></i></a><a href="#" aria-label="Twitter"><i class="fab fa-twitter"></i></a><a href="#" aria-label="LinkedIn"><i class="fab fa-linkedin-in"></i></a></div>
                            </div>
                        </c:if>
                        <c:if test="${not empty categoriesCount}">
                            <div class="sidebar-widget category-widget" data-aos="fade-left" data-aos-delay="200">
                                <h4 class="widget-title">Danh Mục</h4>
                                <ul>
                                    <c:forEach items="${categoriesCount}" var="catEntry">
                                        <li>
                                            <a href="${pageContext.request.contextPath}/blog?category=${fn:escapeXml(catEntry.key)}">
                                                <i class="fas fa-angle-right"></i>
                                                <c:out value="${catEntry.key}"/>
                                                <span>
                                       (
                                       <c:out value="${catEntry.value}"/>
                                       )
                                    </span>
                                            </a>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </div>
                        </c:if>
                        <c:if test="${not empty recentPosts}">
                            <div class="sidebar-widget recent-posts-widget" data-aos="fade-left" data-aos-delay="300">
                                <h4 class="widget-title">Bài Viết Gần Đây</h4>
                                <ul>
                                    <c:forEach items="${recentPosts}" var="recentPost">
                                        <li>
                                            <div class="recent-post-item">
                                                <div class="recent-post-img"><a href="${pageContext.request.contextPath}/blog/post?id=${recentPost.id}"> <%-- SỬA Ở ĐÂY --%><img src="${not empty recentPost.imageUrl ? recentPost.imageUrl : pageContext.request.contextPath += '/resources/images/default-blog-thumb.jpg'}" alt="<c:out value='${recentPost.title}'/>" onerror="this.src='${pageContext.request.contextPath}/resources/images/default-blog-thumb-placeholder.jpg'; this.onerror=null;"></a></div>
                                                <div class="recent-post-content">
                                                    <h5>
                                                        <a href="${pageContext.request.contextPath}/blog/post?id=${recentPost.id}">
                                                            <c:out value="${recentPost.title}"/>
                                                        </a>
                                                    </h5>
                                                    <span>
                                          <fmt:formatDate value="${recentPost.createdDate}" pattern="dd MMM, yyyy"/>
                                       </span>
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
                                        <a href="${pageContext.request.contextPath}/blog?tag=${fn:escapeXml(tag)}">
                                            <c:out value="${tag}"/>
                                        </a>
                                    </c:forEach>
                                </div>
                            </div>
                        </c:if>
                    </aside>
                </div>
            </div>
        </div>
    </section>
</c:if>
<c:if test="${empty blogPost}">
    <section class="section-padding">
        <div class="container">
            <div class="alert alert-warning text-center" role="alert">
                Bài viết bạn tìm kiếm không tồn tại hoặc đã bị xóa.
                <a href="${pageContext.request.contextPath}/blog" class="alert-link">Quay lại trang Blog</a>.
            </div>
        </div>
    </section>
</c:if>
<script>
    function setReplyTo(commentId, authorName) {
        document.getElementById('parentCommentIdField').value = commentId;
        document.getElementById('replyingToInfo').innerText = 'Trả lời bình luận của ' + authorName;
        document.getElementById('cancelReplyButton').style.display = 'inline-block';
        document.getElementById('commentContent').focus();
    }
    function cancelReply() {
        document.getElementById('parentCommentIdField').value = '';
        document.getElementById('replyingToInfo').innerText = '';
        document.getElementById('cancelReplyButton').style.display = 'none';
    }
</script>
<jsp:include page="/WEB-INF/includes/footer.jsp" />