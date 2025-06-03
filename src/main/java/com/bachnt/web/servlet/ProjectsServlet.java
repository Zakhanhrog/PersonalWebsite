package com.bachnt.web.servlet;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bachnt.dao.ProfileDAO;
import com.bachnt.dao.ProjectDAO;
import com.bachnt.model.Profile;
import com.bachnt.model.Project;

@WebServlet("/projects")
public class ProjectsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ProfileDAO profileDAO;
    private ProjectDAO projectDAO;

    @Override
    public void init() throws ServletException {
        profileDAO = new ProfileDAO();
        projectDAO = new ProjectDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Profile profile = profileDAO.getDefaultProfile();
        request.setAttribute("profile", profile);

        String categoryFilter = request.getParameter("category");
        List<Project> projects;

        if (categoryFilter != null && !categoryFilter.trim().isEmpty() && !categoryFilter.equals("*")) {
            projects = projectDAO.getAllProjects().stream()
                    .filter(p -> categoryFilter.equalsIgnoreCase(p.getCategory()))
                    .collect(Collectors.toList());
            request.setAttribute("currentCategoryFilter", categoryFilter);
        } else {
            projects = projectDAO.getAllProjects();
            request.setAttribute("currentCategoryFilter", "*");
        }

        request.setAttribute("projects", projects);

        // Get distinct categories for filter options
        List<String> projectCategories = projectDAO.getAllProjects().stream()
                .map(Project::getCategory)
                .filter(c -> c != null && !c.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        request.setAttribute("projectCategories", projectCategories);

        request.setAttribute("pageTitle", "Dự Án");
        if (profile != null) {
            request.setAttribute("pageTitle", profile.getName() + " - Dự Án");
        }
        request.setAttribute("activePage", "projects");
        request.getRequestDispatcher("/projects.jsp").forward(request, response);
    }
}