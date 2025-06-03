package com.bachnt.web.servlet.admin;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; // Required for session management (authentication)

import com.bachnt.dao.ProfileDAO;
// import com.bachnt.dao.BlogPostDAO; // Example: if you want to show blog post count
// import com.bachnt.dao.ProjectDAO; // Example: if you want to show project count
// import com.bachnt.dao.ContactMessageDAO; // Example: if you want to show new message count
import com.bachnt.model.Profile;

@WebServlet("/admin") // Default admin page
public class AdminDashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ProfileDAO profileDAO;
    // private BlogPostDAO blogPostDAO;
    // private ProjectDAO projectDAO;
    // private ContactMessageDAO contactMessageDAO;

    @Override
    public void init() throws ServletException {
        profileDAO = new ProfileDAO();
        // blogPostDAO = new BlogPostDAO();
        // projectDAO = new ProjectDAO();
        // contactMessageDAO = new ContactMessageDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Basic Authentication Check (Placeholder - implement proper filter later)
        // HttpSession session = request.getSession(false);
        // if (session == null || session.getAttribute("adminUser") == null) {
        //     response.sendRedirect(request.getContextPath() + "/admin/login"); // Redirect to login page
        //     return;
        // }

        Profile profile = profileDAO.getDefaultProfile();
        request.setAttribute("profile", profile);

        // Example: Fetch dashboard statistics (uncomment and implement if needed)
        // int totalBlogPosts = blogPostDAO.getAllBlogPostsForAdmin().size();
        // int totalProjects = projectDAO.getAllProjects().size();
        // int newMessagesCount = contactMessageDAO.getAllContactMessages().stream()
        //                              .filter(m -> "new".equalsIgnoreCase(m.getStatus()))
        //                              .collect(Collectors.toList()).size();
        //
        // request.setAttribute("totalBlogPosts", totalBlogPosts);
        // request.setAttribute("totalProjects", totalProjects);
        // request.setAttribute("newMessagesCount", newMessagesCount);

        request.getRequestDispatcher("/admin/index.jsp").forward(request, response);
    }
}