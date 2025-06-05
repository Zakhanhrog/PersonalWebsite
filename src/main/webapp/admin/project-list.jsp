<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Dự Án</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
    <style>
        .admin-header { padding: 20px 0; background-color: #343a40; color: #fff; margin-bottom: 30px; }
        .table th, .table td { vertical-align: middle; }
        .action-col { width: 150px; text-align: center;}
    </style>
</head>
<body>
<header class="admin-header">
    <div class="container d-flex justify-content-between align-items-center">
        <h1>Quản lý Dự Án</h1>
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
        <a href="${pageContext.request.contextPath}/admin/projects?action=add" class="btn btn-primary"><i class="fas fa-plus"></i> Thêm Dự Án Mới</a>
    </div>

    <div class="card">
        <div class="card-body">
            <c:choose>
                <c:when test="${not empty listProjects}">
                    <table class="table table-hover table-bordered">
                        <thead class="thead-dark">
                        <tr>
                            <th>ID</th>
                            <th>Tên Dự Án</th>
                            <th>Danh mục</th>
                            <th>Khách hàng</th>
                            <th>Trạng thái</th>
                            <th class="action-col">Thao tác</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${listProjects}" var="project">
                            <tr>
                                <td>${project.id}</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/projects/detail?id=${project.id}" target="_blank" title="Xem dự án">
                                        <c:out value="${project.title}"/>
                                    </a>
                                </td>
                                <td><c:out value="${project.category}"/></td>
                                <td><c:out value="${project.client}"/></td>
                                <td><c:out value="${project.status}"/></td>
                                <td class="action-col">
                                    <a href="${pageContext.request.contextPath}/admin/projects?action=edit&id=${project.id}" class="btn btn-sm btn-warning mr-1" title="Sửa"><i class="fas fa-edit"></i></a>
                                    <form action="${pageContext.request.contextPath}/admin/projects" method="post" style="display:inline;">
                                        <input type="hidden" name="org.apache.catalina.filters.CSRF_NONCE" value="${sessionScope['org.apache.catalina.filters.CSRF_NONCE']}">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="id" value="${project.id}">
                                        <button type="submit" class="btn btn-sm btn-danger" title="Xóa" onclick="return confirm('Bạn có chắc chắn muốn xóa dự án này?');">
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
                    <p class="text-center">Chưa có dự án nào.</p>
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