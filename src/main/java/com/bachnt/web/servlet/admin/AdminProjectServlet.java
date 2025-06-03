package com.bachnt.web.servlet.admin;

import com.bachnt.dao.ProjectDAO;
import com.bachnt.model.Project;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@WebServlet("/admin/projects")
public class AdminProjectServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ProjectDAO projectDAO;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void init() throws ServletException {
        projectDAO = new ProjectDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        try {
            switch (action) {
                case "add":
                    showNewForm(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "delete":
                    deleteProject(request, response);
                    break;
                case "list":
                default:
                    listProjects(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi xử lý yêu cầu: " + e.getMessage());
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

        try {
            switch (action) {
                case "save":
                    saveProject(request, response);
                    break;
                case "delete":
                    deleteProject(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/admin/projects?error=InvalidAction");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("projectMessageError", "Lỗi hệ thống: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/projects");
        }
    }

    private void listProjects(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Project> listProjects = projectDAO.getAllProjects();
        request.setAttribute("listProjects", listProjects);

        HttpSession session = request.getSession();
        if (session.getAttribute("projectMessageSuccess") != null) {
            request.setAttribute("messageSuccess", session.getAttribute("projectMessageSuccess"));
            session.removeAttribute("projectMessageSuccess");
        }
        if (session.getAttribute("projectMessageError") != null) {
            request.setAttribute("messageError", session.getAttribute("projectMessageError"));
            session.removeAttribute("projectMessageError");
        }
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
                session.setAttribute("projectMessageError", "Không tìm thấy dự án để sửa.");
                response.sendRedirect(request.getContextPath() + "/admin/projects");
            }
        } catch (NumberFormatException e) {
            session.setAttribute("projectMessageError", "ID dự án không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/admin/projects");
        }
    }

    private void saveProject(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String idParam = request.getParameter("id");

        Project project = new Project();
        boolean isNew = (idParam == null || idParam.isEmpty());

        if (!isNew) {
            project.setId(Integer.parseInt(idParam));
        }

        project.setTitle(request.getParameter("title"));
        project.setDescription(request.getParameter("description"));
        project.setClient(request.getParameter("client"));
        project.setLocation(request.getParameter("location"));
        project.setImageUrl(request.getParameter("imageUrl"));
        project.setCategory(request.getParameter("category"));
        project.setStatus(request.getParameter("status"));
        project.setLink(request.getParameter("link"));

        try {
            String startDateStr = request.getParameter("startDate");
            if (startDateStr != null && !startDateStr.isEmpty()) {
                project.setStartDate(dateFormat.parse(startDateStr));
            }
            String endDateStr = request.getParameter("endDate");
            if (endDateStr != null && !endDateStr.isEmpty()) {
                project.setEndDate(dateFormat.parse(endDateStr));
            } else {
                project.setEndDate(null); // Cho phép end_date là null
            }
        } catch (ParseException e) {
            e.printStackTrace();
            session.setAttribute("projectMessageError", "Lỗi định dạng ngày tháng (yyyy-MM-dd).");
            response.sendRedirect(request.getContextPath() + (isNew ? "/admin/projects?action=add" : "/admin/projects?action=edit&id="+idParam) );
            return;
        }

        boolean success;
        if (isNew) {
            success = projectDAO.addProject(project);
            if (success) session.setAttribute("projectMessageSuccess", "Dự án đã được thêm thành công!");
        } else {
            success = projectDAO.updateProject(project);
            if (success) session.setAttribute("projectMessageSuccess", "Dự án đã được cập nhật thành công!");
        }

        if (!success) {
            session.setAttribute("projectMessageError", "Lỗi: Không thể lưu dự án.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/projects");
    }

    private void deleteProject(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            boolean success = projectDAO.deleteProject(id);
            if (success) {
                session.setAttribute("projectMessageSuccess", "Dự án đã được xóa thành công!");
            } else {
                session.setAttribute("projectMessageError", "Lỗi: Không thể xóa dự án.");
            }
        } catch (NumberFormatException e) {
            session.setAttribute("projectMessageError", "ID dự án không hợp lệ để xóa.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/projects");
    }
}