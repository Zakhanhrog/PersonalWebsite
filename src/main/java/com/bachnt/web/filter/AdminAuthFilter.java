package com.bachnt.web.filter;

import com.bachnt.dao.ContactMessageDAO; // THÊM IMPORT

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AdminAuthFilter implements Filter {
    private ContactMessageDAO contactMessageDAO; // THÊM BIẾN

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        contactMessageDAO = new ContactMessageDAO(); // KHỞI TẠO
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        boolean isStaticResource = path.startsWith("/admin/css/") || path.startsWith("/admin/js/") ||
                path.startsWith("/resources/") || path.endsWith(".css") ||
                path.endsWith(".js") || path.endsWith(".png") ||
                path.endsWith(".jpg") || path.endsWith(".gif");

        if (path.equals("/admin/login") || path.equals("/admin/login.jsp") || isStaticResource) {
            chain.doFilter(request, response);
            return;
        }

        boolean loggedIn = (session != null && session.getAttribute("adminUser") != null);

        if (loggedIn) {
            int unreadMessages = contactMessageDAO.getUnreadMessageCount();
            httpRequest.setAttribute("unreadAdminMessageCount", unreadMessages);

            chain.doFilter(request, response);
        } else {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/admin/login");
        }
    }

    @Override
    public void destroy() {
        // Dọn dẹp tài nguyên (nếu cần)
    }
}