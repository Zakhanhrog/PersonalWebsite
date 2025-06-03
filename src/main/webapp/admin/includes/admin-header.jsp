<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<header class="admin-header bg-dark text-white p-3 mb-4">
  <div class="container-fluid d-flex justify-content-between align-items-center">
    <h1 style="font-size: 1.75rem; margin-bottom: 0;">
      <a href="${pageContext.request.contextPath}/admin" style="color: #fff; text-decoration: none;">
        Admin Panel - <c:out value="${profileAdmin.name ne null ? profileAdmin.name : 'Website'}"/>
      </a>
    </h1>
    <div>
      <a href="${pageContext.request.contextPath}/admin/messages" class="btn btn-outline-light btn-sm position-relative mr-2" title="Tin nhắn">
        <i class="fas fa-envelope"></i>
        <c:if test="${unreadAdminMessageCount > 0}">
                    <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
                        ${unreadAdminMessageCount}
                        <span class="visually-hidden">unread messages</span>
                    </span>
        </c:if>
      </a>

      <%-- Nút thông báo chung (nếu bạn muốn mở rộng) --%>
      <%--
            <div class="dropdown d-inline-block mr-2">
                <button class="btn btn-outline-light btn-sm dropdown-toggle position-relative" type="button" id="adminNotificationDropdown" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    <i class="fas fa-bell"></i>
                    <c:set var="totalNotifications" value="${unreadAdminMessageCount}"/> --%><!-- Thêm các count khác nếu có --> <%--
                    <c:if test="${totalNotifications > 0}">
                         <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
                            ${totalNotifications}
                        </span>
                    </c:if>
                </button>
                <div class="dropdown-menu dropdown-menu-right" aria-labelledby="adminNotificationDropdown">
                    <h6 class="dropdown-header">Thông báo</h6>
                    <c:if test="${unreadAdminMessageCount > 0}">
                         <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/messages">
                            <i class="fas fa-envelope text-primary mr-2"></i> ${unreadAdminMessageCount} tin nhắn mới
                        </a>
                    </c:if>
                    --%><!-- Thêm các item thông báo khác --> <%--
                    <c:if test="${totalNotifications == 0}">
                        <span class="dropdown-item text-muted">Không có thông báo mới.</span>
                    </c:if>
                    <div class="dropdown-divider"></div>
                    <a class="dropdown-item text-center" href="#">Xem tất cả</a>
                </div>
            </div>
            --%>

      <a href="${pageContext.request.contextPath}/" class="btn btn-info btn-sm mr-2" target="_blank"><i class="fas fa-eye"></i> Xem Website</a>
      <a href="${pageContext.request.contextPath}/admin/logout" class="btn btn-danger btn-sm"><i class="fas fa-sign-out-alt"></i> Đăng xuất</a>
    </div>
  </div>
</header>