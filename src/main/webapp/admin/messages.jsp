<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý tin nhắn - <c:out value="${profileAdmin.name ne null ? profileAdmin.name : 'Admin Panel'}"/></title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
    <style>
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f8f9fa; color: #343a40; }
        .admin-header { padding: 20px 0; background-color: #2c3e50; color: #ffffff; margin-bottom: 30px; }
        .table thead th { background-color: #3498db; color: #ffffff; vertical-align: middle;}
        .badge-status-new { background-color: #e74c3c; color: white; }
        .badge-status-read { background-color: #f39c12; color: white; }
        .badge-status-replied { background-color: #2ecc71; color: white; }
        .badge-status-archived { background-color: #95a5a6; color: white; }
        .message-content-preview { max-width: 250px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
        .action-button { margin: 2px; }
        .modal-body p strong { min-width: 80px; display: inline-block; }
    </style>
</head>
<body>
<header class="admin-header">
    <div class="container">
        <div class="row align-items-center">
            <div class="col-md-6">
                <h1><i class="fas fa-envelope-open-text mr-2"></i> Quản Lý Tin Nhắn</h1>
            </div>
            <div class="col-md-6 text-md-right">
                <a href="${pageContext.request.contextPath}/admin" class="btn btn-light mb-2 mb-md-0 btn-sm">
                    <i class="fas fa-tachometer-alt mr-1"></i> Dashboard
                </a>
                <a href="${pageContext.request.contextPath}/" class="btn btn-info btn-sm" target="_blank">
                    <i class="fas fa-eye mr-1"></i> Xem Website
                </a>
            </div>
        </div>
    </div>
</header>

<div class="container">
    <c:if test="${not empty operationStatus}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <c:out value="${operationStatus}"/>
            <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">×</span></button>
        </div>
    </c:if>
    <c:if test="${not empty operationError}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <c:out value="${operationError}"/>
            <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">×</span></button>
        </div>
    </c:if>

    <div class="card shadow mb-4">
        <div class="card-header py-3 d-flex flex-wrap justify-content-between align-items-center">
            <h5 class="m-0 font-weight-bold text-primary">Danh sách tin nhắn</h5>
            <div>
                <span class="badge badge-status-new mr-1">Mới</span>
                <span class="badge badge-status-read mr-1">Đã đọc</span>
                <span class="badge badge-status-replied mr-1">Đã phản hồi</span>
                <span class="badge badge-status-archived">Lưu trữ</span>
            </div>
        </div>
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-bordered table-hover" id="messagesTable">
                    <thead>
                    <tr>
                        <th scope="col" style="width: 5%;">ID</th>
                        <th scope="col" style="width: 15%;">Người gửi</th>
                        <th scope="col" style="width: 15%;">Email</th>
                        <th scope="col" style="width: 15%;">Chủ đề</th>
                        <th scope="col" style="width: 20%;">Nội dung (xem trước)</th>
                        <th scope="col" style="width: 12%;">Thời gian</th>
                        <th scope="col" style="width: 10%;">Trạng thái</th>
                        <th scope="col" style="width: 18%;">Thao tác</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${not empty messages}">
                            <c:forEach var="msg" items="${messages}">
                                <tr>
                                    <td>${msg.id}</td>
                                    <td><c:out value="${msg.name}"/></td>
                                    <td><a href="mailto:${msg.email}"><c:out value="${msg.email}"/></a></td>
                                    <td><c:out value="${msg.subject}"/></td>
                                    <td class="message-content-preview"><c:out value="${msg.message}"/></td>
                                    <td><fmt:formatDate value="${msg.createdDate}" pattern="dd/MM/yyyy HH:mm" /></td>
                                    <td>
                                                <span class="badge badge-status-${fn:toLowerCase(msg.status)}">
                                                    <c:out value="${msg.status}"/>
                                                </span>
                                    </td>
                                    <td>
                                        <button type="button" class="btn btn-sm btn-info action-button view-message-btn"
                                                data-toggle="modal" data-target="#messageModal"
                                                data-id="${msg.id}" data-name="${fn:escapeXml(msg.name)}" data-email="${fn:escapeXml(msg.email)}"
                                                data-subject="${fn:escapeXml(msg.subject)}" data-message="${fn:escapeXml(msg.message)}"
                                                data-date="<fmt:formatDate value='${msg.createdDate}' pattern='dd/MM/yyyy HH:mm' />"
                                                data-status="${msg.status}" title="Xem chi tiết">
                                            <i class="fas fa-eye"></i>
                                        </button>
                                        <div class="btn-group d-inline-block">
                                            <button type="button" class="btn btn-sm btn-secondary dropdown-toggle action-button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" title="Đổi trạng thái">
                                                <i class="fas fa-exchange-alt"></i>
                                            </button>
                                            <div class="dropdown-menu">
                                                <form action="${pageContext.request.contextPath}/admin/messages" method="post" class="status-form">
                                                    <input type="hidden" name="org.apache.catalina.filters.CSRF_NONCE" value="${sessionScope['org.apache.catalina.filters.CSRF_NONCE']}">
                                                    <input type="hidden" name="action" value="updateStatus"><input type="hidden" name="id" value="${msg.id}"><input type="hidden" name="status" value="new">
                                                    <button type="submit" class="dropdown-item ${msg.status == 'new' ? 'active' : ''}">Đánh dấu Mới</button>
                                                </form>
                                                <form action="${pageContext.request.contextPath}/admin/messages" method="post" class="status-form">
                                                    <input type="hidden" name="org.apache.catalina.filters.CSRF_NONCE" value="${sessionScope['org.apache.catalina.filters.CSRF_NONCE']}">
                                                    <input type="hidden" name="action" value="updateStatus"><input type="hidden" name="id" value="${msg.id}"><input type="hidden" name="status" value="read">
                                                    <button type="submit" class="dropdown-item ${msg.status == 'read' ? 'active' : ''}">Đánh dấu Đã đọc</button>
                                                </form>
                                                <form action="${pageContext.request.contextPath}/admin/messages" method="post" class="status-form">
                                                    <input type="hidden" name="org.apache.catalina.filters.CSRF_NONCE" value="${sessionScope['org.apache.catalina.filters.CSRF_NONCE']}">
                                                    <input type="hidden" name="action" value="updateStatus"><input type="hidden" name="id" value="${msg.id}"><input type="hidden" name="status" value="replied">
                                                    <button type="submit" class="dropdown-item ${msg.status == 'replied' ? 'active' : ''}">Đánh dấu Đã phản hồi</button>
                                                </form>
                                                <form action="${pageContext.request.contextPath}/admin/messages" method="post" class="status-form">
                                                    <input type="hidden" name="org.apache.catalina.filters.CSRF_NONCE" value="${sessionScope['org.apache.catalina.filters.CSRF_NONCE']}">
                                                    <input type="hidden" name="action" value="updateStatus"><input type="hidden" name="id" value="${msg.id}"><input type="hidden" name="status" value="archived">
                                                    <button type="submit" class="dropdown-item ${msg.status == 'archived' ? 'active' : ''}">Đánh dấu Lưu trữ</button>
                                                </form>
                                            </div>
                                        </div>
                                        <form action="${pageContext.request.contextPath}/admin/messages" method="post" class="d-inline delete-form">
                                            <input type="hidden" name="org.apache.catalina.filters.CSRF_NONCE" value="${sessionScope['org.apache.catalina.filters.CSRF_NONCE']}">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="id" value="${msg.id}">
                                            <button type="submit" class="btn btn-sm btn-danger action-button" title="Xóa" onclick="return confirm('Bạn có chắc chắn muốn xóa tin nhắn ID ${msg.id} này?');">
                                                <i class="fas fa-trash"></i>
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="8" class="text-center">Không có tin nhắn nào.</td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="messageModal" tabindex="-1" role="dialog" aria-labelledby="messageModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="messageModalLabel">Chi tiết tin nhắn</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">×</span>
                </button>
            </div>
            <div class="modal-body">
                <p><strong>ID:</strong> <span id="modalId"></span></p>
                <p><strong>Người gửi:</strong> <span id="modalName"></span></p>
                <p><strong>Email:</strong> <a id="modalEmailLink" href="#"><span id="modalEmail"></span></a></p>
                <p><strong>Ngày gửi:</strong> <span id="modalDate"></span></p>
                <p><strong>Trạng thái:</strong> <span id="modalStatus" class="badge"></span></p>
                <hr>
                <h5><strong>Chủ đề:</strong> <span id="modalSubject" style="font-weight:normal;"></span></h5>
                <div class="card p-3 bg-light mt-2">
                    <p id="modalMessage" style="white-space: pre-wrap; word-break: break-word;"></p>
                </div>
            </div>
            <div class="modal-footer">
                <a id="replyEmailButton" href="#" class="btn btn-primary"><i class="fas fa-reply mr-1"></i> Trả lời qua Email</a>
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Đóng</button>
            </div>
        </div>
    </div>
</div>

<footer class="text-center py-4 mt-5 bg-light">
    <p class="mb-0">© ${java.time.Year.now().getValue()} Admin Panel</p>
</footer>

<script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
<script>
    $(document).ready(function() {
        $('.view-message-btn').on('click', function() {
            var id = $(this).data('id');
            var name = $(this).data('name');
            var email = $(this).data('email');
            var subject = $(this).data('subject');
            var message = $(this).data('message');
            var date = $(this).data('date');
            var status = $(this).data('status');

            $('#modalId').text(id);
            $('#modalName').text(name);
            $('#modalEmail').text(email);
            $('#modalEmailLink').attr('href', 'mailto:' + email);
            $('#modalSubject').text(subject);
            $('#modalMessage').text(message);
            $('#modalDate').text(date);

            var statusBadge = $('#modalStatus');
            statusBadge.text(status.charAt(0).toUpperCase() + status.slice(1).toLowerCase());
            statusBadge.removeClass().addClass('badge badge-status-' + status.toLowerCase());

            $('#replyEmailButton').attr('href', 'mailto:' + email + '?subject=Re: ' + encodeURIComponent(subject));

            var currentRow = $(this).closest('tr');
            var currentStatusBadgeInTable = currentRow.find('.badge-status-' + status.toLowerCase());

            // Nếu trạng thái là "new", gửi AJAX để cập nhật thành "read"
            if (status.toLowerCase() === 'new') {
                $.ajax({
                    type: "POST",
                    url: "${pageContext.request.contextPath}/admin/messages",
                    data: {
                        action: "updateStatus",
                        id: id,
                        status: "read"
                    },
                    success: function(response) {
                        // Cập nhật UI trên bảng
                        if(currentStatusBadgeInTable.length > 0){
                            currentStatusBadgeInTable.removeClass('badge-status-new').addClass('badge-status-read').text('Read');
                        }
                        // Cập nhật trạng thái trong modal (nếu cần, nhưng thường không vì đã POST rồi)
                        // statusBadge.text('Read').removeClass('badge-status-new').addClass('badge-status-read');
                        // Đánh dấu lại dropdown item
                        currentRow.find('.status-form button.dropdown-item').removeClass('active');
                        currentRow.find('.status-form input[name="status"][value="read"]').closest('form').find('button.dropdown-item').addClass('active');

                        // Cập nhật data-status của nút view để lần sau mở lại modal thì status là 'read'
                        $('.view-message-btn[data-id="'+id+'"]').data('status', 'read');
                    },
                    error: function() {
                        console.error('Lỗi khi tự động cập nhật trạng thái tin nhắn ID ' + id + ' sang "read".');
                    }
                });
            }
        });

        window.setTimeout(function() {
            $(".alert-success, .alert-danger").fadeTo(500, 0).slideUp(500, function(){
                $(this).remove();
            });
        }, 5000);
    });
</script>
</body>
</html>