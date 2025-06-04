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
    private BlogPostDAO blogPostDAO;     // KHAI BÁO
    private ProjectDAO projectDAO;       // KHAI BÁO
    private ContactMessageDAO contactMessageDAO; // KHAI BÁO (đã có từ trước cho notif)


    @Override
    public void init() throws ServletException {
        profileDAO = new ProfileDAO();
        blogPostDAO = new BlogPostDAO();     // KHỞI TẠO
        projectDAO = new ProjectDAO();       // KHỞI TẠO
        contactMessageDAO = new ContactMessageDAO(); // KHỞI TẠO (đã có từ trước cho notif)
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Profile profile = profileDAO.getDefaultProfile();
        request.setAttribute("profileAdmin", profile);

        // Lấy các số liệu thống kê
        int totalBlogPosts = blogPostDAO.getTotalBlogPostCountAdmin();
        int totalProjects = projectDAO.getTotalProjectCount();
        int newMessagesCount = contactMessageDAO.getUnreadMessageCount();

        request.setAttribute("totalBlogPosts", totalBlogPosts);
        request.setAttribute("totalProjects", totalProjects);
        request.setAttribute("newMessagesCount", newMessagesCount); // Truyền số tin nhắn mới

        // Lấy số liệu cho header (nếu AdminAuthFilter chưa làm hoặc bạn muốn làm ở đây)
        // int unreadMessagesForHeader = contactMessageDAO.getUnreadMessageCount();
        // request.setAttribute("unreadAdminMessageCount", unreadMessagesForHeader);

        request.getRequestDispatcher("/admin/index.jsp").forward(request, response);
    }
}