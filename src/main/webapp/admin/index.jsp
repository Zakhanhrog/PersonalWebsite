<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản trị - <c:out value="${profile.name ne null ? profile.name : 'Website'}"/></title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
    <%-- Link to your admin-specific CSS file --%>
    <%-- <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/admin-style.css"> --%>
    <style>
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f8f9fa; color: #343a40; }
        .admin-header { padding: 30px 0; background-color: #2c3e50; color: #ffffff; margin-bottom: 40px; }
        .admin-header h1 { font-size: 2.2rem; }
        .card { transition: transform 0.3s, box-shadow 0.3s; margin-bottom: 30px; border: none; box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1); }
        .card:hover { transform: translateY(-5px); box-shadow: 0 15px 30px rgba(0, 0, 0, 0.2); }
        .card-body { padding: 25px; }
        .card-icon { font-size: 3rem; margin-bottom: 15px; color: #3498db; }
        .card-title { font-weight: 600; font-size: 1.4rem; margin-bottom: 15px; }
        .footer { padding: 20px 0; margin-top: 40px; background-color: #f1f1f1; }
        .stat-value { font-size: 2rem; font-weight: bold; color: #3498db; }
    </style>
</head>
<body>
<header class="admin-header text-center">
    <div class="container">
        <h1 class="mb-3"><i class="fas fa-tachometer-alt mr-2"></i> Bảng Điều Khiển Quản Trị</h1>
        <p class="lead">Chào mừng đến với khu vực quản trị website của <c:out value="${profile.name ne null ? profile.name : 'bạn'}"/></p>
        <a href="${pageContext.request.contextPath}/" class="btn btn-outline-light mt-2">
            <i class="fas fa-home mr-1"></i> Về Trang Chủ
        </a>
        <%-- Add logout button if admin is logged in --%>
        <%-- <c:if test="${not empty adminUser}">
            <a href="${pageContext.request.contextPath}/admin/logout" class="btn btn-outline-danger mt-2 ml-2">
                <i class="fas fa-sign-out-alt mr-1"></i> Đăng Xuất
            </a>
        </c:if> --%>
    </div>
</header>

<div class="container">
    <%-- Placeholder for summary statistics --%>
    <%-- <div class="row mb-4">
        <div class="col-md-4">
            <div class="card text-center">
                <div class="card-body">
                    <div class="stat-value">${totalBlogPosts ne null ? totalBlogPosts : 0}</div>
                    <p class="card-text text-muted">Tổng số bài viết</p>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card text-center">
                <div class="card-body">
                    <div class="stat-value">${totalProjects ne null ? totalProjects : 0}</div>
                    <p class="card-text text-muted">Tổng số dự án</p>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card text-center">
                <div class="card-body">
                    <div class="stat-value">${newMessagesCount ne null ? newMessagesCount : 0}</div>
                    <p class="card-text text-muted">Tin nhắn mới</p>
                </div>
            </div>
        </div>
    </div> --%>

    <div class="row">
        <div class="col-md-4">
            <div class="card text-center h-100">
                <div class="card-body">
                    <div class="card-icon"><i class="fas fa-user-edit"></i></div>
                    <h5 class="card-title">Quản lý Hồ Sơ</h5>
                    <p class="card-text">Cập nhật thông tin cá nhân, kỹ năng.</p>
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
                    <p class="card-text">Cập nhật thông tin các dự án.</p>
                    <a href="${pageContext.request.contextPath}/admin/projects" class="btn btn-primary">Truy cập</a>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card text-center h-100">
                <div class="card-body">
                    <div class="card-icon"><i class="fas fa-envelope"></i></div>
                    <h5 class="card-title">Tin Nhắn Liên Hệ</h5>
                    <p class="card-text">Xem và quản lý tin nhắn từ khách truy cập.</p>
                    <a href="${pageContext.request.contextPath}/admin/messages" class="btn btn-primary">Truy cập</a>
                </div>
            </div>
        </div>
        <%-- Additional admin sections can be added here --%>
        <%-- <div class="col-md-4">
            <div class="card text-center h-100">
                <div class="card-body">
                    <div class="card-icon"><i class="fas fa-users-cog"></i></div>
                    <h5 class="card-title">Quản lý Người dùng</h5>
                    <p class="card-text">Quản lý tài khoản quản trị viên.</p>
                    <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-primary">Truy cập</a>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card text-center h-100">
                <div class="card-body">
                    <div class="card-icon"><i class="fas fa-cog"></i></div>
                    <h5 class="card-title">Cài Đặt Website</h5>
                    <p class="card-text">Cấu hình chung cho website.</p>
                    <a href="#" class="btn btn-secondary disabled">Truy cập (Chưa có)</a>
                </div>
            </div>
        </div> --%>
    </div>
</div>

<footer class="footer">
    <div class="container">
        <div class="row">
            <div class="col-md-6">
                <p>© ${java.time.Year.now().getValue()} <c:out value="${profile.name ne null ? profile.name : 'Admin Panel'}" />. Đã đăng ký bản quyền.</p>
            </div>
            <div class="col-md-6 text-md-right">
                <p>Phiên bản Quản trị 1.0.0</p>
            </div>
        </div>
    </div>
</footer>

<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
</body>
</html>