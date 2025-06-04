<%@ page language="java" contentType="text/html; charset=UTF-8" isErrorPage="true" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>404 - Không Tìm Thấy Trang</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
    <style>
        body { display: flex; flex-direction: column; align-items: center; justify-content: center; min-height: 100vh; background-color: #f8f9fa; text-align: center; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
        .error-code { font-size: 120px; font-weight: bold; color: #dc3545; }
        .error-message { font-size: 24px; margin-bottom: 20px; }
        .error-description { font-size: 16px; color: #6c757d; margin-bottom: 30px; }
    </style>
</head>
<body>
<div class="error-code">404</div>
<div class="error-message">Rất tiếc, trang bạn tìm kiếm không tồn tại!</div>
<div class="error-description">
    Có vẻ như bạn đã truy cập một đường dẫn không đúng hoặc trang đã bị xóa.<br>
    Vui lòng kiểm tra lại URL hoặc quay lại trang chủ.
</div>
<a href="${pageContext.request.contextPath}/" class="btn btn-primary">
    <i class="fas fa-home mr-2"></i>Về Trang Chủ
</a>
<!--
    Thông tin debug (chỉ hiển thị khi phát triển, xóa hoặc ẩn khi deploy production):
    <hr style="width:50%; margin-top:30px;">
    <p><small>Request URI: ${pageContext.errorData.requestURI}</small></p>
    <p><small>Servlet Name: ${pageContext.errorData.servletName}</small></p>
    -->
</body>
</html>