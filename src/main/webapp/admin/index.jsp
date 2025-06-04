<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản trị - <c:out value="${profileAdmin.name ne null ? profileAdmin.name : 'Website'}"/></title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
    <style>
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f8f9fa; color: #343a40; }
        .card { transition: transform 0.3s, box-shadow 0.3s; margin-bottom: 30px; border: none; box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1); }
        .card:hover { transform: translateY(-5px); box-shadow: 0 15px 30px rgba(0, 0, 0, 0.2); }
        .card-body { padding: 25px; }
        .card-icon { font-size: 3rem; margin-bottom: 15px; color: #3498db; }
        .card-title { font-weight: 600; font-size: 1.4rem; margin-bottom: 15px; }
        .page-footer { padding: 20px 0; margin-top: 40px; background-color: #e9ecef; border-top: 1px solid #dee2e6;}
        .stat-value { font-size: 2rem; font-weight: bold; color: #007bff; }
        .stat-box { background-color: #fff; padding: 20px; border-radius: 0.375rem; box-shadow: 0 0.125rem 0.25rem rgba(0,0,0,.075); margin-bottom:20px; }
        .btn .badge {
            position: absolute;
            top: -7px;
            right: -7px;
            padding: 0.35em 0.6em;
            font-size: 0.75em;
        }
    </style>
</head>
<body>

<jsp:include page="/admin/includes/admin-header.jsp" />

<div class="container mt-4">
    <c:if test="${not empty requestScope.adminDashboardMessage}">
        <div class="alert alert-info alert-dismissible fade show" role="alert">
            <c:out value="${requestScope.adminDashboardMessage}"/>
            <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">×</span></button>
        </div>
    </c:if>

    <div class="row mb-4">
        <div class="col-md-4">
            <div class="stat-box text-center">
                <div class="stat-value"><c:out value="${totalBlogPosts ne null ? totalBlogPosts : 0}"/></div>
                <p class="text-muted mb-0">Tổng số bài viết</p>
            </div>
        </div>
        <div class="col-md-4">
            <div class="stat-box text-center">
                <div class="stat-value"><c:out value="${totalProjects ne null ? totalProjects : 0}"/></div>
                <p class="text-muted mb-0">Tổng số dự án</p>
            </div>
        </div>
        <div class="col-md-4">
            <div class="stat-box text-center">
                <div class="stat-value"><c:out value="${newMessagesCount ne null ? newMessagesCount : 0}"/></div>
                <p class="text-muted mb-0">Tin nhắn mới</p>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-md-4">
            <div class="card text-center h-100">
                <div class="card-body">
                    <div class="card-icon"><i class="fas fa-user-edit"></i></div>
                    <h5 class="card-title">Quản lý Hồ Sơ</h5>
                    <p class="card-text">Cập nhật thông tin cá nhân, kỹ năng, học vấn và kinh nghiệm.</p>
                    <a href="${pageContext.request.contextPath}/admin/profile" class="btn btn-primary">Truy cập</a>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card text-center h-100">
                <div class="card-body">
                    <div class="card-icon"><i class="fas fa-newspaper"></i></div>
                    <h5 class="card-title">Quản lý Bài Viết</h5>
                    <p class="card-text">Thêm, sửa, xóa các bài viết blog.</p>
                    <a href="${pageContext.request.contextPath}/admin/blog" class="btn btn-primary">Truy cập</a>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card text-center h-100">
                <div class="card-body">
                    <div class="card-icon"><i class="fas fa-project-diagram"></i></div>
                    <h5 class="card-title">Quản lý Dự Án</h5>
                    <p class="card-text">Cập nhật thông tin các dự án đã thực hiện.</p>
                    <a href="${pageContext.request.contextPath}/admin/projects" class="btn btn-primary">Truy cập</a>
                </div>
            </div>
        </div>
        <div class="col-md-4 mt-md-4">
            <div class="card text-center h-100">
                <div class="card-body">
                    <div class="card-icon"><i class="fas fa-envelope-open-text"></i></div>
                    <h5 class="card-title">Tin Nhắn Liên Hệ</h5>
                    <p class="card-text">Xem và quản lý tin nhắn từ khách truy cập.</p>
                    <a href="${pageContext.request.contextPath}/admin/messages" class="btn btn-primary">Truy cập</a>
                </div>
            </div>
        </div>
        <div class="col-md-4 mt-md-4">
            <div class="card text-center h-100">
                <div class="card-body">
                    <div class="card-icon"><i class="fas fa-comments"></i></div>
                    <h5 class="card-title">Quản lý Bình Luận</h5>
                    <p class="card-text">Duyệt, sửa, xóa các bình luận trên blog.</p>
                    <a href="${pageContext.request.contextPath}/admin/comments" class="btn btn-primary">Truy cập</a>
                </div>
            </div>
        </div>
        <div class="col-md-4 mt-md-4">
            <div class="card text-center h-100">
                <div class="card-body">
                    <div class="card-icon"><i class="fas fa-star-half-alt"></i></div>
                    <h5 class="card-title">Quản lý Đánh Giá</h5>
                    <p class="card-text">Thêm, sửa, xóa các đánh giá từ đối tác/khách hàng.</p>
                    <a href="${pageContext.request.contextPath}/admin/testimonials" class="btn btn-primary">Truy cập</a>
                </div>
            </div>
        </div>
    </div>
</div>

<footer class="page-footer">
    <div class="container">
        <div class="row">
            <div class="col-md-6">
                <p class="mb-0">© ${java.time.Year.now().getValue()} <c:out value="${profileAdmin.name ne null ? profileAdmin.name : 'Admin Panel'}" />. Đã đăng ký bản quyền.</p>
            </div>
            <div class="col-md-6 text-md-right">
                <p class="mb-0">Phiên bản Quản trị 1.0.1</p>
            </div>
        </div>
    </div>
</footer>

<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
<script>
    window.setTimeout(function() {
        $(".alert-info, .alert-success, .alert-danger").fadeTo(500, 0).slideUp(500, function(){
            $(this).remove();
        });
    }, 7000);
</script>
</body>
</html>