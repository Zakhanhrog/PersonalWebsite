<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>${formAction == 'add' ? 'Thêm Bài Viết Mới' : 'Chỉnh Sửa Bài Viết'}</title>
  <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
  <style>
    .current-image-preview { max-width: 200px; max-height: 150px; margin-bottom: 10px; border:1px solid #ddd; padding: 2px; background-color: #fff;}
  </style>
</head>
<body>
<jsp:include page="/admin/includes/admin-header.jsp" />

<div class="container mt-4">
  <h3>${formAction == 'add' ? 'Thêm Bài Viết Mới' : 'Chỉnh Sửa Bài Viết'}</h3>
  <hr/>
  <c:if test="${not empty sessionScope.blogMessageError}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
      <c:out value="${sessionScope.blogMessageError}"/>
      <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">×</span></button>
      <c:remove var="blogMessageError" scope="session"/>
    </div>
  </c:if>
  <c:if test="${not empty requestScope.messageError}"> <%-- For errors not from session redirect --%>
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
      <c:out value="${requestScope.messageError}"/>
      <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">×</span></button>
    </div>
  </c:if>


  <form action="${pageContext.request.contextPath}/admin/blog" method="post" enctype="multipart/form-data">
    <input type="hidden" name="action" value="save">
    <c:if test="${formAction == 'edit'}">
      <input type="hidden" name="id" value="${blogPost.id}">
    </c:if>

    <div class="form-group">
      <label for="title">Tiêu đề *</label>
      <input type="text" class="form-control" id="title" name="title" value="<c:out value='${blogPost.title}'/>" required>
    </div>

    <div class="form-group">
      <label for="summary">Tóm tắt</label>
      <textarea class="form-control" id="summary" name="summary" rows="3"><c:out value='${blogPost.summary}'/></textarea>
    </div>

    <div class="form-group">
      <label for="content">Nội dung *</label>
      <textarea class="form-control" id="content" name="content" rows="10" required><c:out value='${blogPost.content}'/></textarea>
    </div>

    <div class="form-row">
      <div class="form-group col-md-6">
        <label for="author">Tác giả</label>
        <input type="text" class="form-control" id="author" name="author" value="<c:out value='${not empty blogPost.author ? blogPost.author : defaultAuthor}'/>">
      </div>
      <div class="form-group col-md-6">
        <label for="category">Danh mục</label>
        <input type="text" class="form-control" id="category" name="category" value="<c:out value='${blogPost.category}'/>" placeholder="Ví dụ: Bất động sản, Đầu tư">
      </div>
    </div>

    <div class="form-group">
      <label for="tags">Tags (cách nhau bởi dấu phẩy)</label>
      <input type="text" class="form-control" id="tags" name="tags" value="<c:out value='${blogPost.tags}'/>" placeholder="Ví dụ: tag1, tag2, tag mới">
    </div>

    <div class="form-group">
      <label>Ảnh đại diện hiện tại:</label><br>
      <c:if test="${not empty blogPost.imageUrl}">
        <img src="${not empty blogPost.imageUrl ? (blogPost.imageUrl.startsWith('http') ? blogPost.imageUrl : pageContext.request.contextPath.concat(blogPost.imageUrl)) : pageContext.request.contextPath.concat('/resources/images/default-blog-thumb.jpg')}"
             alt="<c:out value='${blogPost.title}'/>" class="current-image-preview">
        <div class="form-check mb-2">
          <input class="form-check-input" type="checkbox" name="deleteImage" value="true" id="deleteBlogImageCheck">
          <label class="form-check-label" for="deleteBlogImageCheck">
            Xóa ảnh hiện tại
          </label>
        </div>
      </c:if>
      <c:if test="${empty blogPost.imageUrl}">
        <p class="text-muted small">Chưa có ảnh đại diện.</p>
      </c:if>
    </div>
    <div class="form-group">
      <label for="imageFile">Chọn ảnh mới (nếu muốn thay đổi/thêm):</label>
      <input type="file" class="form-control-file" id="imageFile" name="imageFile" accept="image/png, image/jpeg, image/gif">
      <small class="form-text text-muted">Để trống nếu không muốn thay đổi. Tối đa 10MB.</small>
    </div>

    <div class="form-group">
      <label for="status">Trạng thái</label>
      <select class="form-control" id="status" name="status">
        <option value="draft" ${blogPost.status == 'draft' ? 'selected' : ''}>Bản nháp</option>
        <option value="published" ${blogPost.status == 'published' || (empty blogPost.status && formAction == 'add') ? 'selected' : ''}>Đã xuất bản</option>
        <option value="archived" ${blogPost.status == 'archived' ? 'selected' : ''}>Lưu trữ</option>
      </select>
    </div>

    <button type="submit" class="btn btn-success"><i class="fas fa-save"></i> Lưu Bài Viết</button>
    <a href="${pageContext.request.contextPath}/admin/blog" class="btn btn-secondary">Hủy</a>
  </form>
</div>

<footer class="text-center py-4 mt-5 bg-light">
  <p class="mb-0">© ${java.time.Year.now().getValue()} Admin Panel</p>
</footer>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script> <%-- THAY ĐỔI Ở ĐÂY --%>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
<script>
  $(document).ready(function() { // Đảm bảo DOM sẵn sàng
    window.setTimeout(function() {
      $(".alert-success, .alert-danger").fadeTo(500, 0).slideUp(500, function(){
        $(this).remove();
      });
    }, 7000);
  });
</script>
</body>
</html>