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
  <%-- CSS cho editor nếu có, ví dụ CKEditor --%>
</head>
<body>
<header class="bg-dark text-white p-3 mb-4">
  <div class="container d-flex justify-content-between align-items-center">
    <h3>${formAction == 'add' ? 'Thêm Bài Viết Mới' : 'Chỉnh Sửa Bài Viết'}</h3>
    <a href="${pageContext.request.contextPath}/admin/blog" class="btn btn-light btn-sm"><i class="fas fa-list"></i> Danh sách bài viết</a>
  </div>
</header>

<div class="container">
  <form action="${pageContext.request.contextPath}/admin/blog" method="post">
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
      <%-- Có thể thay bằng CKEditor ở đây --%>
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

    <div class="form-row">
      <div class="form-group col-md-6">
        <label for="tags">Tags (cách nhau bởi dấu phẩy)</label>
        <input type="text" class="form-control" id="tags" name="tags" value="<c:out value='${blogPost.tags}'/>" placeholder="Ví dụ: tag1, tag2, tag mới">
      </div>
      <div class="form-group col-md-6">
        <label for="imageUrl">URL Ảnh đại diện</label>
        <input type="text" class="form-control" id="imageUrl" name="imageUrl" value="<c:out value='${blogPost.imageUrl}'/>" placeholder="/resources/images/blog/anh.jpg">
        <small class="form-text text-muted">Đường dẫn tương đối. Ví dụ: /resources/images/blog/my-post.jpg</small>
      </div>
    </div>

    <div class="form-group">
      <label for="status">Trạng thái</label>
      <select class="form-control" id="status" name="status">
        <option value="draft" ${blogPost.status == 'draft' ? 'selected' : ''}>Bản nháp</option>
        <option value="published" ${blogPost.status == 'published' || empty blogPost.status ? 'selected' : ''}>Đã xuất bản</option>
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

<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>

</body>
</html>