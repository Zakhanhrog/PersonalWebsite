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

@WebServlet("/projects/detail")
public class ProjectDetailServlet extends HttpServlet {
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
        String projectIdParam = request.getParameter("id");
        Project project = null;
        Profile profile = profileDAO.getDefaultProfile();
        request.setAttribute("profile", profile);

        if (projectIdParam != null && !projectIdParam.isEmpty()) {
            try {
                int projectId = Integer.parseInt(projectIdParam);
                project = projectDAO.getProjectById(projectId);

                if (project == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Dự án không tồn tại.");
                    return;
                }
                request.setAttribute("project", project);
                final String currentProjectCategory = project.getCategory();

                List<Project> relatedProjects = projectDAO.getAllProjects().stream()
                        .filter(p -> p.getId() != projectId &&
                                p.getCategory() != null &&
                                currentProjectCategory != null &&
                                currentProjectCategory.equalsIgnoreCase(p.getCategory()))
                        .limit(3)
                        .collect(Collectors.toList());
                request.setAttribute("relatedProjects", relatedProjects);

                request.setAttribute("pageTitle", project.getTitle());
                if (profile != null) {
                    request.setAttribute("pageTitle", profile.getName() + " - " + project.getTitle());
                }

            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID dự án không hợp lệ.");
                return;
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Không tìm thấy ID dự án.");
            return;
        }

        request.setAttribute("activePage", "projects");
        request.getRequestDispatcher("/project-detail.jsp").forward(request, response);
    }
}