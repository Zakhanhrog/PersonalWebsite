<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${formAction == 'add' ? 'Thêm Đánh Giá Mới' : 'Chỉnh Sửa Đánh Giá'}</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
    <style>
        .current-image-preview { max-width: 100px; max-height: 100px; margin-bottom: 10px; border:1px solid #ddd; padding: 2px; background-color: #fff; border-radius:50%;}
    </style>
</head>
<body>
<jsp:include page="/admin/includes/admin-header.jsp" />

<div class="container mt-4">
    <h3>${formAction == 'add' ? 'Thêm Đánh Giá Mới' : 'Chỉnh Sửa Đánh Giá'} ID: ${testimonial.id}</h3>
    <hr/>
    <c:if test="${not empty sessionScope.testimonialMessageError}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <c:out value="${sessionScope.testimonialMessageError}"/>
            <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">×</span></button>
            <c:remove var="testimonialMessageError" scope="session"/>
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/admin/testimonials" method="post" enctype="multipart/form-data">
        <input type="hidden" name="action" value="save">
        <c:if test="${formAction == 'edit'}">
            <input type="hidden" name="id" value="${testimonial.id}">
        </c:if>

        <div class="form-group">
            <label for="clientName">Tên Khách Hàng/Đối Tác *</label>
            <input type="text" class="form-control" id="clientName" name="clientName" value="<c:out value='${testimonial.clientName}'/>" required>
        </div>
        <div class="form-group">
            <label for="clientPositionCompany">Chức Vụ, Công Ty</label>
            <input type="text" class="form-control" id="clientPositionCompany" name="clientPositionCompany" value="<c:out value='${testimonial.clientPositionCompany}'/>">
        </div>
        <div class="form-group">
            <label for="quoteText">Nội dung Đánh Giá *</label>
            <textarea class="form-control" id="quoteText" name="quoteText" rows="5" required><c:out value='${testimonial.quoteText}'/></textarea>
        </div>
        <div class="form-group">
            <label for="displayOrder">Thứ tự hiển thị (số nhỏ hơn hiện trước)</label>
            <input type="number" class="form-control" id="displayOrder" name="displayOrder" value="${testimonial.displayOrder ne 0 ? testimonial.displayOrder : 100}" min="0">
        </div>

        <div class="form-group">
            <label>Ảnh Khách Hàng/Logo hiện tại:</label><br>
            <c:if test="${not empty testimonial.clientImageUrl}">
                <img src="${not empty testimonial.clientImageUrl ? (testimonial.clientImageUrl.startsWith('http') ? testimonial.clientImageUrl : pageContext.request.contextPath.concat(testimonial.clientImageUrl)) : pageContext.request.contextPath.concat('/resources/images/default-avatar.jpg')}"
                     alt="Ảnh Client" class="current-image-preview">
                <div class="form-check mb-2">
                    <input class="form-check-input" type="checkbox" name="deleteImage" value="true" id="deleteTestimonialImageCheck">
                    <label class="form-check-label" for="deleteTestimonialImageCheck">Xóa ảnh hiện tại</label>
                </div>
            </c:if>
            <c:if test="${empty testimonial.clientImageUrl}"><p class="text-muted small">Chưa có ảnh.</p></c:if>
        </div>
        <div class="form-group">
            <label for="clientImageFile">Chọn ảnh mới (nếu muốn thay đổi/thêm):</label>
            <input type="file" class="form-control-file" id="clientImageFile" name="clientImageFile" accept="image/png, image/jpeg, image/gif">
            <small class="form-text text-muted">Để trống nếu không muốn thay đổi. Tối đa 5MB.</small>
        </div>

        <button type="submit" class="btn btn-success"><i class="fas fa-save"></i> Lưu Đánh Giá</button>
        <a href="${pageContext.request.contextPath}/admin/testimonials" class="btn btn-secondary">Hủy</a>
    </form>
</div>

<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
<script>
    window.setTimeout(function() {
        $(".alert-danger, .alert-success").fadeTo(500, 0).slideUp(500, function(){ $(this).remove(); });
    }, 7000);
</script>
</body>
</html>