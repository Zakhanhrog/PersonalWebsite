package com.bachnt.web.servlet.admin;

import com.bachnt.dao.UserDAO;
import com.bachnt.model.User;
import org.mindrot.jbcrypt.BCrypt; // Thêm import cho BCrypt

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/admin/login")
public class LoginServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(LoginServlet.class);
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Nếu đã đăng nhập, redirect về dashboard admin
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("adminUser") != null) {
            response.sendRedirect(request.getContextPath() + "/admin");
            return;
        }
        request.getRequestDispatcher("/admin/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            request.setAttribute("loginError", "Tên đăng nhập và mật khẩu không được để trống.");
            request.getRequestDispatcher("/admin/login.jsp").forward(request, response);
            return;
        }

        User user = userDAO.getUserByUsername(username.trim());

        if (user != null && BCrypt.checkpw(password, user.getPasswordHash())) {
            if ("admin".equalsIgnoreCase(user.getRole())) {
                HttpSession oldSession = request.getSession(false);
                if (oldSession != null) {
                    oldSession.invalidate();
                }
                HttpSession newSession = request.getSession(true);
                newSession.setAttribute("adminUser", user);
                newSession.setMaxInactiveInterval(30 * 60);
                response.sendRedirect(request.getContextPath() + "/admin");
            } else {
                request.setAttribute("loginError", "Tài khoản không có quyền truy cập admin.");
                request.getRequestDispatcher("/admin/login.jsp").forward(request, response);
            }
        } else {
            request.setAttribute("loginError", "Tên đăng nhập hoặc mật khẩu không đúng.");
            request.getRequestDispatcher("/admin/login.jsp").forward(request, response);
        }
    }
}