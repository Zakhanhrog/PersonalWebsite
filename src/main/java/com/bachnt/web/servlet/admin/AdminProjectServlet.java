package com.bachnt.web.servlet.admin;

import com.bachnt.dao.ProjectDAO;
import com.bachnt.model.Project;
import com.bachnt.dao.ProfileDAO;
import com.bachnt.model.Profile;
import com.bachnt.utils.FileUploadUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.Part;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/admin/projects")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 1,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 15
)
public class AdminProjectServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(AdminProjectServlet.class);
    private static final long serialVersionUID = 1L;
    private ProjectDAO projectDAO;
    private ProfileDAO profileDAO;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final String PROJECTS_SUBFOLDER = "projects";

    @Override
    public void init() throws ServletException {
        projectDAO = new ProjectDAO();
        profileDAO = new ProfileDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        HttpSession session = request.getSession();
        if (session.getAttribute("projectMessageSuccess") != null) {
            request.setAttribute("messageSuccess", session.getAttribute("projectMessageSuccess"));
            session.removeAttribute("projectMessageSuccess");
        }
        if (session.getAttribute("projectMessageError") != null) {
            request.setAttribute("messageError", session.getAttribute("projectMessageError"));
            session.removeAttribute("projectMessageError");
        }

        Profile profile = profileDAO.getDefaultProfile();
        request.setAttribute("profileAdmin", profile);

        try {
            switch (action) {
                case "add":
                    showNewForm(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "delete":
                    deleteProjectAction(request, response, true);
                    break;
                case "list":
                default:
                    listProjects(request, response);
                    break;
            }
        } catch (Exception e) {
            logger.error("Error processing GET request for action: {}", action, e);
            session.setAttribute("projectMessageError", "A system error occurred. Please try again later.");
            listProjects(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/admin/projects?error=NoActionSpecified");
            return;
        }
        HttpSession session = request.getSession();

        try {
            switch (action) {
                case "save":
                    saveProject(request, response);
                    break;
                case "delete":
                    deleteProjectAction(request, response, true);
                    break;
                default:
                    session.setAttribute("projectMessageError", "Invalid POST action.");
                    response.sendRedirect(request.getContextPath() + "/admin/projects");
                    break;
            }
        } catch (Exception e) {
            logger.error("Error processing POST request for action: {}", action, e.getMessage(), e);
            session.setAttribute("projectMessageError", "A critical system error occurred. Please try again later.");
            response.sendRedirect(request.getContextPath() + "/admin/projects");
        }
    }

    private void listProjects(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Project> listProjects = projectDAO.getAllProjects();
        request.setAttribute("listProjects", listProjects);
        request.getRequestDispatcher("/admin/project-list.jsp").forward(request, response);
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("project", new Project());
        request.setAttribute("formAction", "add");
        request.getRequestDispatcher("/admin/project-form.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Project existingProject = projectDAO.getProjectById(id);
            if (existingProject != null) {
                request.setAttribute("project", existingProject);
                request.setAttribute("formAction", "edit");
                request.getRequestDispatcher("/admin/project-form.jsp").forward(request, response);
            } else {
                session.setAttribute("projectMessageError", "Project not found for editing (ID: " + id + ").");
                response.sendRedirect(request.getContextPath() + "/admin/projects");
            }
        } catch (NumberFormatException e) {
            logger.warn("Invalid project ID format for editing: {}", request.getParameter("id"));
            session.setAttribute("projectMessageError", "Invalid project ID.");
            response.sendRedirect(request.getContextPath() + "/admin/projects");
        }
    }

    private void saveProject(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();
        String idParam = request.getParameter("id");

        Project project = new Project();
        boolean isNew = (idParam == null || idParam.isEmpty());
        String overallMessage = "";

        if (!isNew) {
            try {
                project.setId(Integer.parseInt(idParam));
                Project existingProject = projectDAO.getProjectById(project.getId());
                if (existingProject != null) {
                    project.setImageUrl(existingProject.getImageUrl());
                    project.setStartDate(existingProject.getStartDate());
                } else {
                    isNew = true;
                }
            } catch (NumberFormatException e) {
                logger.warn("Invalid project ID format for saving existing project: {}", idParam);
                isNew = true;
            }
        }

        project.setTitle(request.getParameter("title"));
        project.setDescription(request.getParameter("description"));
        project.setClient(request.getParameter("client"));
        project.setLocation(request.getParameter("location"));
        project.setCategory(request.getParameter("category"));
        project.setStatus(request.getParameter("status"));
        project.setLink(request.getParameter("link"));

        try {
            String startDateStr = request.getParameter("startDate");
            if (startDateStr != null && !startDateStr.isEmpty()) {
                project.setStartDate(dateFormat.parse(startDateStr));
            } else if (isNew) {
                project.setStartDate(new Date());
            }

            String endDateStr = request.getParameter("endDate");
            if (endDateStr != null && !endDateStr.isEmpty()) {
                project.setEndDate(dateFormat.parse(endDateStr));
            } else {
                project.setEndDate(null);
            }
        } catch (ParseException e) {
            logger.error("Error parsing date: {}", e.getMessage(), e);
            session.setAttribute("projectMessageError", "Date format error (yyyy-MM-dd).");
            response.sendRedirect(request.getContextPath() + (isNew ? "/admin/projects?action=add" : "/admin/projects?action=edit&id="+idParam) );
            return;
        }

        Part filePart = request.getPart("imageFile");
        String currentRelativeImageUrl = project.getImageUrl();
        String newRelativeImageUrlFromUpload = null;
        boolean imageActionTaken = false;

        if (filePart != null && filePart.getSize() > 0 && filePart.getSubmittedFileName() != null && !filePart.getSubmittedFileName().isEmpty()) {
            try {
                newRelativeImageUrlFromUpload = FileUploadUtils.saveUploadedFile(filePart, PROJECTS_SUBFOLDER);
                if (newRelativeImageUrlFromUpload != null) {
                    overallMessage += "Project image uploaded. ";
                    if (currentRelativeImageUrl != null && !currentRelativeImageUrl.isEmpty() && !currentRelativeImageUrl.contains("default")) {
                        FileUploadUtils.deleteUploadedFile(currentRelativeImageUrl);
                    }
                    project.setImageUrl(newRelativeImageUrlFromUpload);
                    imageActionTaken = true;
                } else {
                    session.setAttribute("projectMessageError", (session.getAttribute("projectMessageError") != null ? session.getAttribute("projectMessageError") + " " : "") + "Failed to save uploaded project image.");
                }
            } catch (IOException e) {
                logger.error("Error saving uploaded project image: {}", e.getMessage(), e);
                session.setAttribute("projectMessageError", (session.getAttribute("projectMessageError") != null ? session.getAttribute("projectMessageError") + " " : "") + "Error uploading project image: " + e.getMessage());
            }
        }

        String deleteImageFlag = request.getParameter("deleteImage");
        if ("true".equals(deleteImageFlag) && newRelativeImageUrlFromUpload == null) {
            if (currentRelativeImageUrl != null && !currentRelativeImageUrl.isEmpty() && !currentRelativeImageUrl.contains("default")) {
                FileUploadUtils.deleteUploadedFile(currentRelativeImageUrl);
            }
            project.setImageUrl(null);
            imageActionTaken = true;
            overallMessage += "Project image removed. ";
        }

        if (!imageActionTaken && newRelativeImageUrlFromUpload == null) {
            project.setImageUrl(currentRelativeImageUrl);
        }


        boolean success;
        if (isNew) {
            success = projectDAO.addProject(project);
            if (success) session.setAttribute("projectMessageSuccess", overallMessage.trim() + (overallMessage.isEmpty() && !imageActionTaken ? "" : " ") + "Project added successfully!");
            else session.setAttribute("projectMessageError", (session.getAttribute("projectMessageError") != null ? session.getAttribute("projectMessageError") + " " : "") + "Error: Could not add project.");
        } else {
            success = projectDAO.updateProject(project);
            if (success) session.setAttribute("projectMessageSuccess", overallMessage.trim() + (overallMessage.isEmpty() && !imageActionTaken ? "" : " ") + "Project updated successfully!");
            else session.setAttribute("projectMessageError", (session.getAttribute("projectMessageError") != null ? session.getAttribute("projectMessageError") + " " : "") + "Error: Could not update project.");
        }

        if (!success && overallMessage.isEmpty() && !imageActionTaken) {
            session.setAttribute("projectMessageError", (session.getAttribute("projectMessageError") != null ? session.getAttribute("projectMessageError") + " " : "") + "Error: Could not save project information.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/projects");
    }

    private void deleteProjectAction(HttpServletRequest request, HttpServletResponse response, boolean redirectToList) throws IOException, ServletException {
        HttpSession session = request.getSession();
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Project projectToDelete = projectDAO.getProjectById(id);
            boolean success = projectDAO.deleteProject(id);

            if (success) {
                session.setAttribute("projectMessageSuccess", "Project ID " + id + " deleted successfully!");
                if (projectToDelete != null && projectToDelete.getImageUrl() != null && !projectToDelete.getImageUrl().isEmpty() && !projectToDelete.getImageUrl().contains("default")) {
                    FileUploadUtils.deleteUploadedFile(projectToDelete.getImageUrl());
                }
            } else {
                session.setAttribute("projectMessageError", "Error: Could not delete project ID " + id + ".");
            }
        } catch (NumberFormatException e) {
            logger.warn("Invalid project ID format for deletion: {}", request.getParameter("id"));
            session.setAttribute("projectMessageError", "Invalid project ID for deletion.");
        } catch (Exception e){
            logger.error("Error deleting project: {}", e.getMessage(), e);
            session.setAttribute("projectMessageError", "A system error occurred while deleting the project.");
        }

        if(redirectToList){
            response.sendRedirect(request.getContextPath() + "/admin/projects");
        } else {
            listProjects(request, response);
        }
    }
}