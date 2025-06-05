package com.bachnt.web.servlet.admin;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
// Bỏ import HttpSession nếu không dùng trực tiếp ở đây, vì AdminAuthFilter đã xử lý

import com.bachnt.dao.ProfileDAO;
import com.bachnt.dao.BlogPostDAO;     // THÊM IMPORT
import com.bachnt.dao.ProjectDAO;      // THÊM IMPORT
import com.bachnt.dao.ContactMessageDAO;// THÊM IMPORT (đã có từ trước cho notif)
import com.bachnt.model.Profile;

@WebServlet("/admin")
public class AdminDashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ProfileDAO profileDAO;
    private BlogPostDAO blogPostDAO;
    private ProjectDAO projectDAO;
    private ContactMessageDAO contactMessageDAO;


    @Override
    public void init() throws ServletException {
        profileDAO = new ProfileDAO();
        blogPostDAO = new BlogPostDAO();
        projectDAO = new ProjectDAO();
        contactMessageDAO = new ContactMessageDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Profile profile = profileDAO.getDefaultProfile();
        request.setAttribute("profileAdmin", profile);

        int totalBlogPosts = blogPostDAO.getTotalBlogPostCountAdmin();
        int totalProjects = projectDAO.getTotalProjectCount();
        int newMessagesCount = contactMessageDAO.getUnreadMessageCount();

        request.setAttribute("totalBlogPosts", totalBlogPosts);
        request.setAttribute("totalProjects", totalProjects);
        request.setAttribute("newMessagesCount", newMessagesCount);
        request.getRequestDispatcher("/admin/index.jsp").forward(request, response);
    }
}