<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>${formAction == 'add' ? 'Thêm Dự Án Mới' : 'Chỉnh Sửa Dự Án'}</title>
  <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
  <style>
    .current-image-preview { max-width: 200px; max-height: 150px; margin-bottom: 10px; border:1px solid #ddd; padding: 2px; background-color: #fff;}
  </style>
</head>
<body>
<jsp:include page="/admin/includes/admin-header.jsp" />

<div class="container mt-4">
  <h3>${formAction == 'add' ? 'Thêm Dự Án Mới' : 'Chỉnh Sửa Dự Án'}</h3>
  <hr/>
  <c:if test="${not empty sessionScope.projectMessageError}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
      <c:out value="${sessionScope.projectMessageError}"/>
      <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">×</span></button>
      <c:remove var="projectMessageError" scope="session"/>
    </div>
  </c:if>
  <c:if test="${not empty requestScope.messageError}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
      <c:out value="${requestScope.messageError}"/>
      <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">×</span></button>
    </div>
  </c:if>

  <form action="${pageContext.request.contextPath}/admin/projects" method="post" enctype="multipart/form-data">
    <input type="hidden" name="action" value="save">
    <c:if test="${formAction == 'edit'}">
      <input type="hidden" name="id" value="${project.id}">
    </c:if>

    <div class="form-group">
      <label for="title">Tên dự án *</label>
      <input type="text" class="form-control" id="title" name="title" value="<c:out value='${project.title}'/>" required>
    </div>

    <div class="form-group">
      <label for="description">Mô tả</label>
      <textarea class="form-control" id="description" name="description" rows="5"><c:out value='${project.description}'/></textarea>
    </div>

    <div class="form-row">
      <div class="form-group col-md-6">
        <label for="client">Khách hàng / Chủ đầu tư</label>
        <input type="text" class="form-control" id="client" name="client" value="<c:out value='${project.client}'/>">
      </div>
      <div class="form-group col-md-6">
        <label for="location">Địa điểm</label>
        <input type="text" class="form-control" id="location" name="location" value="<c:out value='${project.location}'/>">
      </div>
    </div>

    <div class="form-row">
      <div class="form-group col-md-6">
        <label for="startDate">Ngày bắt đầu (YYYY-MM-DD)</label>
        <input type="date" class="form-control" id="startDate" name="startDate"
               value="<fmt:formatDate value='${project.startDate}' pattern='yyyy-MM-dd'/>">
      </div>
      <div class="form-group col-md-6">
        <label for="endDate">Ngày kết thúc (YYYY-MM-DD)</label>
        <input type="date" class="form-control" id="endDate" name="endDate"
               value="<fmt:formatDate value='${project.endDate}' pattern='yyyy-MM-dd'/>">
        <small class="form-text text-muted">Để trống nếu dự án chưa kết thúc.</small>
      </div>
    </div>

    <div class="form-row">
      <div class="form-group col-md-4">
        <label for="category">Danh mục</label>
        <input type="text" class="form-control" id="category" name="category" value="<c:out value='${project.category}'/>" placeholder="Bất động sản, Công nghiệp,...">
      </div>
      <div class="form-group col-md-4">
        <label for="status">Trạng thái</label>
        <select class="form-control" id="status" name="status">
          <option value="Kế hoạch" ${project.status == 'Kế hoạch' || empty project.status ? 'selected' : ''}>Kế hoạch</option>
          <option value="Đang triển khai" ${project.status == 'Đang triển khai' ? 'selected' : ''}>Đang triển khai</option>
          <option value="Hoàn thành" ${project.status == 'Hoàn thành' ? 'selected' : ''}>Hoàn thành</option>
          <option value="Tạm dừng" ${project.status == 'Tạm dừng' ? 'selected' : ''}>Tạm dừng</option>
        </select>
      </div>
      <div class="form-group col-md-4">
        <label for="link">Link dự án (Website)</label>
        <input type="url" class="form-control" id="link" name="link" value="<c:out value='${project.link}'/>" placeholder="http://...">
      </div>
    </div>

    <div class="form-group">
      <label>Ảnh đại diện hiện tại:</label><br>
      <c:if test="${not empty project.imageUrl}">
        <img src="${project.imageUrl}" alt="Ảnh dự án" class="current-image-preview">
        <div class="form-check mb-2">
          <input class="form-check-input" type="checkbox" name="deleteImage" value="true" id="deleteProjectImageCheck">
          <label class="form-check-label" for="deleteProjectImageCheck">
            Xóa ảnh hiện tại
          </label>
        </div>
      </c:if>
      <c:if test="${empty project.imageUrl}">
        <p class="text-muted small">Chưa có ảnh đại diện.</p>
      </c:if>
    </div>
    <div class="form-group">
      <label for="imageFile">Chọn ảnh mới (nếu muốn thay đổi/thêm):</label>
      <input type="file" class="form-control-file" id="imageFile" name="imageFile" accept="image/png, image/jpeg, image/gif">
      <small class="form-text text-muted">Để trống nếu không muốn thay đổi. Tối đa 10MB.</small>
    </div>

    <button type="submit" class="btn btn-success"><i class="fas fa-save"></i> Lưu Dự Án</button>
    <a href="${pageContext.request.contextPath}/admin/projects" class="btn btn-secondary">Hủy</a>
  </form>
</div>

<footer class="text-center py-4 mt-5 bg-light">
  <p class="mb-0">© ${java.time.Year.now().getValue()} Admin Panel</p>
</footer>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
<script>
  $(document).ready(function() {
    window.setTimeout(function() {
      $(".alert-success, .alert-danger").fadeTo(500, 0).slideUp(500, function(){
        $(this).remove();
      });
    }, 7000);
  });
</script>
</body>
</html>