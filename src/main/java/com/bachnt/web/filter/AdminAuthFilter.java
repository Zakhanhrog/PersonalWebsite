package com.bachnt.web.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter; // Nếu dùng annotation thay vì web.xml
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

// Bỏ @WebFilter nếu khai báo trong web.xml
// @WebFilter(urlPatterns = "/admin/*", dispatcherTypes = {DispatcherType.REQUEST, DispatcherType.FORWARD})
public class AdminAuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Khởi tạo filter (nếu cần)
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        // Cho phép truy cập trang login và các tài nguyên tĩnh của trang admin mà không cần đăng nhập
        if (path.equals("/admin/login") ||
                path.equals("/admin/login.jsp") || // Quan trọng: cho phép forward đến trang jsp
                path.startsWith("/admin/css/") ||   // Ví dụ nếu bạn có CSS cho admin
                path.startsWith("/admin/js/") ||    // Ví dụ nếu bạn có JS cho admin
                path.startsWith("/resources/")      // Cho phép các tài nguyên chung nếu trang admin dùng
        ) {
            chain.doFilter(request, response); // Cho qua
            return;
        }

        // Kiểm tra xem người dùng đã đăng nhập chưa
        boolean loggedIn = (session != null && session.getAttribute("adminUser") != null);

        if (loggedIn) {
            // Nếu đã đăng nhập, cho phép truy cập
            chain.doFilter(request, response);
        } else {
            // Nếu chưa đăng nhập, redirect về trang login
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/admin/login");
        }
    }

    @Override
    public void destroy() {
        // Dọn dẹp tài nguyên (nếu cần)
    }
}