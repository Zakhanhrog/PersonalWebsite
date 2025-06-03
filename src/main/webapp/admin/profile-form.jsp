<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Quản lý Hồ Sơ Admin</title>
  <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
  <%-- Thêm link CSS admin của bạn nếu có --%>
  <style>
    .admin-header { padding: 20px 0; background-color: #343a40; color: #fff; margin-bottom: 30px; }
    .card-title-custom { font-size: 1.25rem; margin-bottom: 1.5rem; }
    .form-section { margin-bottom: 2rem; }
  </style>
</head>
<body>
<header class="admin-header">
  <div class="container d-flex justify-content-between align-items-center">
    <h1>Quản lý Hồ Sơ</h1>
    <div>
      <a href="${pageContext.request.contextPath}/admin" class="btn btn-light btn-sm"><i class="fas fa-tachometer-alt"></i> Dashboard</a>
      <a href="${pageContext.request.contextPath}/" class="btn btn-info btn-sm" target="_blank"><i class="fas fa-eye"></i> Xem Website</a>
    </div>
  </div>
</header>

<div class="container">
  <c:if test="${not empty message}">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
      <c:out value="${message}"/>
      <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">×</span></button>
    </div>
  </c:if>
  <c:if test="${not empty error}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
      <c:out value="${error}"/>
      <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">×</span></button>
    </div>
  </c:if>

  <!-- Form cập nhật thông tin Profile -->
  <div class="card form-section">
    <div class="card-body">
      <h4 class="card-title-custom">Thông Tin Cá Nhân & Công Ty</h4>
      <form action="${pageContext.request.contextPath}/admin/profile" method="post">
        <input type="hidden" name="action" value="updateProfile">
        <div class="form-row">
          <div class="form-group col-md-6">
            <label for="name">Họ tên</label>
            <input type="text" class="form-control" id="name" name="name" value="<c:out value='${profile.name}'/>" required>
          </div>
          <div class="form-group col-md-6">
            <label for="position">Chức vụ</label>
            <input type="text" class="form-control" id="position" name="position" value="<c:out value='${profile.position}'/>">
          </div>
        </div>
        <div class="form-row">
          <div class="form-group col-md-6">
            <label for="companyName">Tên công ty</label>
            <input type="text" class="form-control" id="companyName" name="companyName" value="<c:out value='${profile.companyName}'/>">
          </div>
          <div class="form-group col-md-6">
            <label for="companyTaxId">Mã số thuế</label>
            <input type="text" class="form-control" id="companyTaxId" name="companyTaxId" value="<c:out value='${profile.companyTaxId}'/>">
          </div>
        </div>
        <div class="form-group">
          <label for="companyAddress">Địa chỉ công ty</label>
          <input type="text" class="form-control" id="companyAddress" name="companyAddress" value="<c:out value='${profile.companyAddress}'/>">
        </div>
        <div class="form-row">
          <div class="form-group col-md-6">
            <label for="phoneNumber">Số điện thoại</label>
            <input type="text" class="form-control" id="phoneNumber" name="phoneNumber" value="<c:out value='${profile.phoneNumber}'/>">
          </div>
          <div class="form-group col-md-6">
            <label for="email">Email</label>
            <input type="email" class="form-control" id="email" name="email" value="<c:out value='${profile.email}'/>" required>
          </div>
        </div>
        <div class="form-group">
          <label for="bio">Giới thiệu bản thân (Bio)</label>
          <textarea class="form-control" id="bio" name="bio" rows="5"><c:out value='${profile.bio}'/></textarea>
        </div>
        <div class="form-group">
          <label for="photoUrl">URL Ảnh đại diện</label>
          <input type="text" class="form-control" id="photoUrl" name="photoUrl" value="<c:out value='${profile.photoUrl}'/>" placeholder="/resources/images/ten_anh.jpg">
          <small class="form-text text-muted">Đường dẫn tương đối từ thư mục gốc webapp, ví dụ: /resources/images/profile.jpg</small>
        </div>
        <button type="submit" class="btn btn-primary">Cập nhật Hồ Sơ</button>
      </form>
    </div>
  </div>

  <!-- Phần quản lý Skills -->
  <div class="card form-section">
    <div class="card-body">
      <h4 class="card-title-custom">Quản lý Kỹ Năng</h4>

      <h5>Thêm Kỹ Năng Mới</h5>
      <form action="${pageContext.request.contextPath}/admin/profile" method="post" class="mb-4">
        <input type="hidden" name="action" value="addSkill">
        <div class="form-row align-items-end">
          <div class="form-group col-md-4">
            <label for="skillName">Tên kỹ năng</label>
            <input type="text" class="form-control" id="skillName" name="skillName" required>
          </div>
          <div class="form-group col-md-3">
            <label for="skillLevel">Mức độ (%)</label>
            <input type="number" class="form-control" id="skillLevel" name="skillLevel" min="0" max="100" required>
          </div>
          <div class="form-group col-md-3">
            <label for="skillCategory">Phân loại</label>
            <input type="text" class="form-control" id="skillCategory" name="skillCategory" placeholder="Chuyên môn, Kỹ năng mềm,...">
          </div>
          <div class="form-group col-md-2">
            <button type="submit" class="btn btn-success btn-block">Thêm Skill</button>
          </div>
        </div>
      </form>

      <h5>Danh Sách Kỹ Năng Hiện Tại</h5>
      <c:choose>
        <c:when test="${not empty skillsList}">
          <table class="table table-striped table-sm">
            <thead>
            <tr>
              <th>Tên Kỹ Năng</th>
              <th>Mức Độ</th>
              <th>Phân Loại</th>
              <th>Thao Tác</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${skillsList}" var="skill">
              <tr>
                <td><c:out value="${skill.name}"/></td>
                <td>${skill.level}%</td>
                <td><c:out value="${skill.category}"/></td>
                <td>
                    <%-- Nút Sửa (TODO: Cần logic và form/modal để sửa) --%>
                    <%-- <a href="#" class="btn btn-sm btn-warning mr-1"><i class="fas fa-edit"></i> Sửa</a> --%>
                  <form action="${pageContext.request.contextPath}/admin/profile" method="post" style="display: inline;">
                    <input type="hidden" name="action" value="deleteSkill">
                    <input type="hidden" name="skillId" value="${skill.id}">
                    <button type="submit" class="btn btn-sm btn-danger" onclick="return confirm('Bạn có chắc muốn xóa kỹ năng này?');">
                      <i class="fas fa-trash"></i> Xóa
                    </button>
                  </form>
                </td>
              </tr>
            </c:forEach>
            </tbody>
          </table>
        </c:when>
        <c:otherwise>
          <p>Chưa có kỹ năng nào được thêm.</p>
        </c:otherwise>
      </c:choose>
    </div>
  </div>
</div>

<footer class="text-center py-4 mt-5 bg-light">
  <p class="mb-0">© ${java.time.Year.now().getValue()} Admin Panel</p>
</footer>

<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
</body>
</html>