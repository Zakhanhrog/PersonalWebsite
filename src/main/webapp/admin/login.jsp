<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Admin Login</title>
  <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
  <style>
    body { display: flex; align-items: center; justify-content: center; min-height: 100vh; background-color: #f8f9fa; }
    .login-card { max-width: 400px; width: 100%; padding: 25px; box-shadow: 0 0.5rem 1rem rgba(0,0,0,.15); border-radius: 0.5rem; background-color: #fff; }
    .login-card h2 { text-align: center; margin-bottom: 20px; }
  </style>
</head>
<body>
<div class="login-card">
  <h2>Admin Login</h2>
  <c:if test="${not empty loginError}">
    <div class="alert alert-danger" role="alert">
      <c:out value="${loginError}"/>
    </div>
  </c:if>
  <form action="${pageContext.request.contextPath}/admin/login" method="post">
    <div class="form-group mb-3">
      <label for="username">Tên đăng nhập:</label>
      <input type="text" class="form-control" id="username" name="username" required autofocus>
    </div>
    <div class="form-group mb-3">
      <label for="password">Mật khẩu:</label>
      <input type="password" class="form-control" id="password" name="password" required>
    </div>
    <button type="submit" class="btn btn-primary btn-block">Đăng nhập</button>
  </form>
</div>
<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
</body>
</html>