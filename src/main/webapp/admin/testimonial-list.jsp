<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Đánh Giá</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
    <style>
        .admin-header { padding: 20px 0; background-color: #343a40; color: #fff; margin-bottom: 30px; }
        .table th, .table td { vertical-align: middle; font-size: 0.9rem;}
        .action-col { width: 120px; text-align: center;}
        .quote-preview { max-width: 300px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
        .client-img-thumb { max-width: 50px; max-height: 50px; border-radius: 3px; object-fit: cover;} /* Thêm object-fit */
    </style>
</head>
<body>
<jsp:include page="/admin/includes/admin-header.jsp" />

<div class="container-fluid px-3">
    <c:if test="${not empty messageSuccess}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <c:out value="${messageSuccess}"/><button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">×</span></button>
        </div>
    </c:if>
    <c:if test="${not empty messageError}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <c:out value="${messageError}"/><button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">×</span></button>
        </div>
    </c:if>

    <div class="mb-3">
        <a href="${pageContext.request.contextPath}/admin/testimonials?action=add" class="btn btn-primary"><i class="fas fa-plus"></i> Thêm Đánh Giá Mới</a>
    </div>

    <div class="card">
        <div class="card-header"><h4>Danh sách Đánh Giá</h4></div>
        <div class="card-body">
            <c:choose>
                <c:when test="${not empty listTestimonials}">
                    <div class="table-responsive">
                        <table class="table table-hover table-bordered table-sm">
                            <thead class="thead-dark">
                            <tr>
                                <th>ID</th>
                                <th>Ảnh</th>
                                <th>Tên Khách Hàng</th>
                                <th>Chức Vụ/Công Ty</th>
                                <th class="quote-preview">Trích Dẫn (ngắn)</th>
                                <th>Thứ tự</th>
                                <th class="action-col">Thao tác</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${listTestimonials}" var="item">
                                <tr>
                                    <td>${item.id}</td>
                                    <td>
                                        <c:set var="testimonialListImageSource">
                                            <c:choose>
                                                <c:when test="${not empty item.clientImageUrl}">
                                                    <c:choose>
                                                        <c:when test="${fn:startsWith(item.clientImageUrl, 'http') || fn:startsWith(item.clientImageUrl, 'https://ui-avatars.com')}">
                                                            ${item.clientImageUrl}
                                                        </c:when>
                                                        <c:otherwise>
                                                            ${pageContext.request.contextPath}/uploads/${item.clientImageUrl}
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:when>
                                                <c:otherwise>
                                                    https://ui-avatars.com/api/?name=${fn:escapeXml(fn:replace(item.clientName, ' ', '+'))}&size=50&background=random
                                                </c:otherwise>
                                            </c:choose>
                                        </c:set>
                                        <img src="${testimonialListImageSource}"
                                             alt="<c:out value='${item.clientName}'/>"
                                             class="client-img-thumb"
                                             onerror="this.src='https://ui-avatars.com/api/?name=${fn:escapeXml(fn:replace(item.clientName, ' ', '+'))}&size=50&background=random&color=fff'; this.onerror=null;"/>
                                    </td>
                                    <td><c:out value="${item.clientName}"/></td>
                                    <td><c:out value="${item.clientPositionCompany}"/></td>
                                    <td class="quote-preview" title="${fn:escapeXml(item.quoteText)}"><c:out value="${item.quoteText}"/></td>
                                    <td><c:out value="${item.displayOrder}"/></td>
                                    <td class="action-col">
                                        <a href="${pageContext.request.contextPath}/admin/testimonials?action=edit&id=${item.id}" class="btn btn-sm btn-warning mr-1" title="Sửa"><i class="fas fa-edit"></i></a>
                                        <form action="${pageContext.request.contextPath}/admin/testimonials" method="post" style="display:inline;">
                                            <input type="hidden" name="org.apache.catalina.filters.CSRF_NONCE" value="${sessionScope['org.apache.catalina.filters.CSRF_NONCE']}">
                                            <input type="hidden" name="action" value="delete"><input type="hidden" name="id" value="${item.id}">
                                            <button type="submit" class="btn btn-sm btn-danger" title="Xóa" onclick="return confirm('Bạn có chắc muốn xóa đánh giá này?');"><i class="fas fa-trash"></i></button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:when>
                <c:otherwise><p class="text-center text-muted">Chưa có đánh giá nào.</p></c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
<script>
    window.setTimeout(function() {
        $(".alert-success, .alert-danger").fadeTo(500, 0).slideUp(500, function(){ $(this).remove(); });
    }, 7000);
</script>
</body>
</html>