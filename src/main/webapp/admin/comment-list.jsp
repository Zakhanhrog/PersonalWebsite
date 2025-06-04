<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Quản lý Bình Luận</title>
  <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
  <style>
    .admin-header { padding: 20px 0; background-color: #343a40; color: #fff; margin-bottom: 30px; }
    .table th, .table td { vertical-align: middle; font-size: 0.9rem;}
    .action-col { width: 200px; text-align: center;}
    .comment-content-preview { max-width: 300px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
    .status-approved { color: green; font-weight: bold; }
    .status-pending { color: orange; font-weight: bold; }
    .status-spam { color: red; }
  </style>
</head>
<body>
<jsp:include page="/admin/includes/admin-header.jsp" />

<div class="container-fluid px-3">
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

  <div class="card">
    <div class="card-header">
      <h4>Danh sách Bình Luận</h4>
      <%-- TODO: Thêm filter nếu cần --%>
    </div>
    <div class="card-body">
      <c:choose>
        <c:when test="${not empty listComments}">
          <div class="table-responsive">
            <table class="table table-hover table-bordered table-sm">
              <thead class="thead-dark">
              <tr>
                <th>ID</th>
                <th>Tác giả</th>
                <th>Email</th>
                <th>Nội dung (ngắn)</th>
                <th>Bài viết</th>
                <th>Ngày gửi</th>
                <th>Trạng thái</th>
                <th class="action-col">Thao tác</th>
              </tr>
              </thead>
              <tbody>
              <c:forEach items="${listComments}" var="comment">
                <tr>
                  <td>${comment.id}</td>
                  <td><c:out value="${comment.authorName}"/></td>
                  <td><c:out value="${comment.authorEmail}"/></td>
                  <td class="comment-content-preview" title="${fn:escapeXml(comment.content)}"><c:out value="${comment.content}"/></td>
                  <td>
                    <a href="${pageContext.request.contextPath}/blog/post?id=${comment.blogPostId}" target="_blank">
                      ID: ${comment.blogPostId}
                        <%-- Nếu muốn hiển thị tên bài viết, cần join trong DAO và truyền qua --%>
                        <%-- <br><small><c:out value="${comment.blogPostTitle}"/></small> --%>
                    </a>
                  </td>
                  <td><fmt:formatDate value="${comment.createdDate}" pattern="dd/MM/yyyy HH:mm"/></td>
                  <td>
                                            <span class="status-${fn:toLowerCase(comment.status)}">
                                                 <c:out value="${comment.status}"/>
                                            </span>
                  </td>
                  <td class="action-col">
                    <div class="btn-group">
                      <button type="button" class="btn btn-sm btn-outline-secondary dropdown-toggle" data-toggle="dropdown">Duyệt</button>
                      <div class="dropdown-menu">
                        <form action="${pageContext.request.contextPath}/admin/comments" method="post" style="display: block;">
                          <input type="hidden" name="action" value="updateStatus"><input type="hidden" name="commentId" value="${comment.id}"><input type="hidden" name="newStatus" value="approved">
                          <button type="submit" class="dropdown-item ${comment.status == 'approved' ? 'active' : ''}"><i class="fas fa-check"></i> Approved</button>
                        </form>
                        <form action="${pageContext.request.contextPath}/admin/comments" method="post" style="display: block;">
                          <input type="hidden" name="action" value="updateStatus"><input type="hidden" name="commentId" value="${comment.id}"><input type="hidden" name="newStatus" value="pending">
                          <button type="submit" class="dropdown-item ${comment.status == 'pending' ? 'active' : ''}"><i class="fas fa-clock"></i> Pending</button>
                        </form>
                        <form action="${pageContext.request.contextPath}/admin/comments" method="post" style="display: block;">
                          <input type="hidden" name="action" value="updateStatus"><input type="hidden" name="commentId" value="${comment.id}"><input type="hidden" name="newStatus" value="spam">
                          <button type="submit" class="dropdown-item ${comment.status == 'spam' ? 'active' : ''}"><i class="fas fa-ban"></i> Spam</button>
                        </form>
                      </div>
                    </div>
                    <a href="${pageContext.request.contextPath}/admin/comments?action=editForm&commentId=${comment.id}" class="btn btn-sm btn-warning mr-1" title="Sửa nội dung"><i class="fas fa-edit"></i></a>
                    <form action="${pageContext.request.contextPath}/admin/comments" method="post" style="display:inline;">
                      <input type="hidden" name="action" value="delete">
                      <input type="hidden" name="commentId" value="${comment.id}">
                      <button type="submit" class="btn btn-sm btn-danger" title="Xóa" onclick="return confirm('Bạn có chắc muốn xóa bình luận này?');">
                        <i class="fas fa-trash"></i>
                      </button>
                    </form>
                  </td>
                </tr>
              </c:forEach>
              </tbody>
            </table>
          </div>
        </c:when>
        <c:otherwise>
          <p class="text-center text-muted">Chưa có bình luận nào.</p>
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
<script>
  window.setTimeout(function() {
    $(".alert-success, .alert-danger").fadeTo(500, 0).slideUp(500, function(){
      $(this).remove();
    });
  }, 7000);
</script>
</body>
</html>