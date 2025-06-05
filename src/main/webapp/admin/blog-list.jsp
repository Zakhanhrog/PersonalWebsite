<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Quản lý Bài Viết Blog</title>
  <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
  <style>
    .admin-header { padding: 20px 0; background-color: #343a40; color: #fff; margin-bottom: 30px; }
    .table th, .table td { vertical-align: middle; }
    .action-col { width: 150px; text-align: center;}
    .status-published { color: green; font-weight: bold; }
    .status-draft { color: orange; font-weight: bold; }
    .status-archived { color: grey; }
  </style>
</head>
<body>
<header class="admin-header">
  <div class="container d-flex justify-content-between align-items-center">
    <h1>Quản lý Bài Viết Blog</h1>
    <a href="${pageContext.request.contextPath}/admin" class="btn btn-light btn-sm"><i class="fas fa-tachometer-alt"></i> Dashboard</a>
  </div>
</header>

<div class="container">
  <c:if test="${not empty messageSuccess}">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
      <c:out value="${messageSuccess}"/>
      <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">×</span></button>
    </div>
  </c:if>
  <c:if test="${not empty messageError}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
      <c:out value="${messageError}"/>
      <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">×</span></button>
    </div>
  </c:if>

  <div class="mb-3">
    <a href="${pageContext.request.contextPath}/admin/blog?action=add" class="btn btn-primary"><i class="fas fa-plus"></i> Thêm Bài Viết Mới</a>
  </div>

  <div class="card">
    <div class="card-body">
      <c:choose>
        <c:when test="${not empty listBlogPosts}">
          <table class="table table-hover table-bordered">
            <thead class="thead-dark">
            <tr>
              <th>ID</th>
              <th>Tiêu đề</th>
              <th>Danh mục</th>
              <th>Tác giả</th>
              <th>Ngày tạo</th>
              <th>Trạng thái</th>
              <th class="action-col">Thao tác</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${listBlogPosts}" var="post">
              <tr>
                <td>${post.id}</td>
                <td>
                  <a href="${pageContext.request.contextPath}/blog/post?id=${post.id}" target="_blank" title="Xem bài viết">
                    <c:out value="${post.title}"/>
                  </a>
                </td>
                <td><c:out value="${post.category}"/></td>
                <td><c:out value="${post.author}"/></td>
                <td><fmt:formatDate value="${post.createdDate}" pattern="dd/MM/yyyy HH:mm"/></td>
                <td>
                                            <span class="status-${fn:toLowerCase(post.status)}">
                                                <c:choose>
                                                  <c:when test="${post.status == 'published'}">Đã xuất bản</c:when>
                                                  <c:when test="${post.status == 'draft'}">Bản nháp</c:when>
                                                  <c:when test="${post.status == 'archived'}">Lưu trữ</c:when>
                                                  <c:otherwise><c:out value="${post.status}"/></c:otherwise>
                                                </c:choose>
                                            </span>
                </td>
                <td class="action-col">
                  <a href="${pageContext.request.contextPath}/admin/blog?action=edit&id=${post.id}" class="btn btn-sm btn-warning mr-1" title="Sửa"><i class="fas fa-edit"></i></a>
                  <form action="${pageContext.request.contextPath}/admin/blog" method="post" style="  display:inline;">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="org.apache.catalina.filters.CSRF_NONCE" value="${sessionScope['org.apache.catalina.filters.CSRF_NONCE']}">
                    <input type="hidden" name="id" value="${post.id}">
                    <button type="submit" class="btn btn-sm btn-danger" title="Xóa" onclick="return confirm('Bạn có chắc chắn muốn xóa bài viết này?');">
                      <i class="fas fa-trash"></i>
                    </button>
                  </form>
                </td>
              </tr>
            </c:forEach>
            </tbody>
          </table>
        </c:when>
        <c:otherwise>
          <p class="text-center">Chưa có bài viết nào.</p>
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