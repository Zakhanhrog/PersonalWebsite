package com.bachnt.web.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bachnt.dao.ProfileDAO;
import com.bachnt.dao.ProjectDAO;
import com.bachnt.dao.BlogPostDAO;
import com.bachnt.model.Profile;
import com.bachnt.model.Project;
import com.bachnt.model.BlogPost;
import java.util.List;

@WebServlet("")
public class HomeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ProfileDAO profileDAO;
    private ProjectDAO projectDAO;
    private BlogPostDAO blogPostDAO;

    @Override
    public void init() throws ServletException {
        profileDAO = new ProfileDAO();
        projectDAO = new ProjectDAO();
        blogPostDAO = new BlogPostDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Profile profile = profileDAO.getDefaultProfile();
        request.setAttribute("profile", profile);
        request.setAttribute("pageTitle", profile != null ? profile.getName() + " - Trang Chủ" : "Trang Chủ");
        request.setAttribute("activePage", "home");
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}