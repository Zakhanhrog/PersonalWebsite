<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sửa Bình Luận</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
    <style>
        .admin-header { padding: 20px 0; background-color: #343a40; color: #fff; margin-bottom: 30px; }
    </style>
</head>
<body>
<jsp:include page="/admin/includes/admin-header.jsp" />

<div class="container">
    <c:if test="${not empty commentToEdit}">
        <h3>Sửa Bình Luận ID: ${commentToEdit.id}</h3>
        <hr/>
        <c:if test="${not empty sessionScope.commentMessageError}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <c:out value="${sessionScope.commentMessageError}"/>
                <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">×</span></button>
                <c:remove var="commentMessageError" scope="session"/>
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/admin/comments" method="post">
            <input type="hidden" name="action" value="saveEdit">
            <input type="hidden" name="commentId" value="${commentToEdit.id}">

            <div class="form-group">
                <label>Bài viết ID:</label>
                <p><a href="${pageContext.request.contextPath}/blog/post?id=${commentToEdit.blogPostId}" target="_blank">${commentToEdit.blogPostId}</a></p>
            </div>
            <div class="form-group">
                <label>Tác giả:</label>
                <p><c:out value="${commentToEdit.authorName}"/></p>
            </div>
            <div class="form-group">
                <label>Email:</label>
                <p><c:out value="${commentToEdit.authorEmail}"/></p>
            </div>
            <div class="form-group">
                <label>Ngày gửi:</label>
                <p><fmt:formatDate value="${commentToEdit.createdDate}" pattern="dd/MM/yyyy HH:mm:ss"/></p>
            </div>
            <div class="form-group">
                <label>Trạng thái hiện tại:</label>
                <p><span class="badge badge-info"><c:out value="${commentToEdit.status}"/></span></p>
            </div>

            <div class="form-group">
                <label for="content">Nội dung bình luận *</label>
                <textarea class="form-control" id="content" name="content" rows="5" required><c:out value="${commentToEdit.content}"/></textarea>
            </div>

            <button type="submit" class="btn btn-primary"><i class="fas fa-save"></i> Lưu thay đổi</button>
            <a href="${pageContext.request.contextPath}/admin/comments" class="btn btn-secondary">Hủy</a>
        </form>
    </c:if>
    <c:if test="${empty commentToEdit && empty sessionScope.commentMessageError}">
        <div class="alert alert-warning">Không tìm thấy bình luận để sửa.</div>
        <a href="${pageContext.request.contextPath}/admin/comments" class="btn btn-secondary">Quay lại danh sách</a>
    </c:if>
</div>

<footer class="text-center py-4 mt-5 bg-light">
    <p class="mb-0">© ${java.time.Year.now().getValue()} Admin Panel</p>
</footer>

<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
<script>
    window.setTimeout(function() {
        $(".alert-danger").fadeTo(500, 0).slideUp(500, function(){
            $(this).remove();
        });
    }, 7000);
</script>
</body>
</html>