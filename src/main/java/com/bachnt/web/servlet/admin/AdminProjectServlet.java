package com.bachnt.web.servlet.admin;

import com.bachnt.dao.ProjectDAO;
import com.bachnt.model.Project;
import com.bachnt.dao.ProfileDAO;
import com.bachnt.model.Profile;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/admin/projects")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 1,  // 1 MB
        maxFileSize = 1024 * 1024 * 10, // 10 MB
        maxRequestSize = 1024 * 1024 * 15 // 15 MB
)
public class AdminProjectServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(AdminProjectServlet.class);
    private static final long serialVersionUID = 1L;
    private ProjectDAO projectDAO;
    private ProfileDAO profileDAO;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private static final String UPLOAD_DIR_PROJECT_PHYSICAL = "/Users/ngogiakhanh/Documents/PersonalWebsite/PersonalWebsiteUploads/projects";
    private static final String URL_PATH_PROJECT_FOR_DB = "/my-uploaded-images/projects";

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
            logger.error("Lỗi xử lý yêu cầu GET cho action: {}", action, e);
            session.setAttribute("projectUpdateMessageError", "Lỗi hệ thống nghiêm trọng, vui lòng thử lại sau.");
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
                    session.setAttribute("projectMessageError", "Hành động POST không hợp lệ.");
                    response.sendRedirect(request.getContextPath() + "/admin/projects");
                    break;
            }
        } catch (Exception e) {
            logger.error("Lỗi khi xử lý yêu cầu POST cho action: {}", action, e.getMessage(), e);
            session.setAttribute("projectUpdateMessageError", "Lỗi hệ thống nghiêm trọng, vui lòng thử lại sau.");
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
                session.setAttribute("projectMessageError", "Không tìm thấy dự án để sửa (ID: " + id + ").");
                response.sendRedirect(request.getContextPath() + "/admin/projects");
            }
        } catch (NumberFormatException e) {
            session.setAttribute("projectUpdateMessageError", "Lỗi hệ thống nghiêm trọng, vui lòng thử lại sau.");
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
            project.setId(Integer.parseInt(idParam));
            Project existingProject = projectDAO.getProjectById(project.getId());
            if (existingProject != null) {
                project.setImageUrl(existingProject.getImageUrl()); // Mặc định giữ ảnh cũ
                project.setStartDate(existingProject.getStartDate()); // Giữ ngày tạo/bắt đầu cũ nếu không được cung cấp mới
            } else {
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
            } else if (isNew) { // Nếu là mới và không có start date, có thể đặt ngày hiện tại hoặc báo lỗi
                project.setStartDate(new Date()); // Hoặc xử lý khác
            }

            String endDateStr = request.getParameter("endDate");
            if (endDateStr != null && !endDateStr.isEmpty()) {
                project.setEndDate(dateFormat.parse(endDateStr));
            } else {
                project.setEndDate(null);
            }
        } catch (ParseException e) {
            logger.error("Lỗi khi phân tích ngày tháng: {}", e.getMessage(), e);
            session.setAttribute("projectUpdateMessageError", "Lỗi hệ thống nghiêm trọng, vui lòng thử lại sau.");
            response.sendRedirect(request.getContextPath() + (isNew ? "/admin/projects?action=add" : "/admin/projects?action=edit&id="+idParam) );
            return;
        }

        Part filePart = request.getPart("imageFile"); 
        String currentImageUrl = project.getImageUrl();
        String newImageUrlFromUpload = null;
        boolean imageActionTaken = false;

        if (filePart != null && filePart.getSize() > 0) {
            String originalFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            if (originalFileName != null && !originalFileName.isEmpty()) {
                String fileExtension = "";
                int i = originalFileName.lastIndexOf('.');
                if (i > 0) fileExtension = originalFileName.substring(i);
                String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

                String physicalUploadPath = UPLOAD_DIR_PROJECT_PHYSICAL;
                File uploadDir = new File(physicalUploadPath);
                if (!uploadDir.exists()) {
                    boolean dirCreated = uploadDir.mkdirs();
                    if(!dirCreated){
                        session.setAttribute("projectMessageError", "Lỗi nghiêm trọng: Không thể tạo thư mục upload cho projects.");
                        response.sendRedirect(request.getContextPath() + "/admin/projects");
                        return;
                    }
                }

                String physicalFilePath = physicalUploadPath + File.separator + uniqueFileName;
                try {
                    filePart.write(physicalFilePath);
                    newImageUrlFromUpload = URL_PATH_PROJECT_FOR_DB + "/" + uniqueFileName;
                    imageActionTaken = true;
                    overallMessage += "Ảnh dự án đã được tải lên. ";

                    if (currentImageUrl != null && !currentImageUrl.isEmpty() && !currentImageUrl.contains("default")) {
                        String oldFileNameOnly = currentImageUrl.substring(currentImageUrl.lastIndexOf('/') + 1);
                        File oldFilePhysical = new File(UPLOAD_DIR_PROJECT_PHYSICAL + File.separator + oldFileNameOnly);
                        if (oldFilePhysical.exists()) oldFilePhysical.delete();
                    }
                } catch (IOException e) {
                    logger.error("Lỗi khi ghi file ảnh dự án: {}", e.getMessage(), e);
                    session.setAttribute("projectUpdateMessageError", "Lỗi hệ thống nghiêm trọng, vui lòng thử lại sau.");
                }
            }
        }

        String deleteImageFlag = request.getParameter("deleteImage");
        if ("true".equals(deleteImageFlag) && newImageUrlFromUpload == null) {
            if (currentImageUrl != null && !currentImageUrl.isEmpty() && !currentImageUrl.contains("default")) {
                String oldFileNameOnly = currentImageUrl.substring(currentImageUrl.lastIndexOf('/') + 1);
                File oldFilePhysical = new File(UPLOAD_DIR_PROJECT_PHYSICAL + File.separator + oldFileNameOnly);
                if (oldFilePhysical.exists()) oldFilePhysical.delete();
            }
            project.setImageUrl(null);
            imageActionTaken = true;
            overallMessage += "Ảnh dự án đã được xóa. ";
        } else if (newImageUrlFromUpload != null) {
            project.setImageUrl(newImageUrlFromUpload);
        }

        boolean success;
        if (isNew) {
            success = projectDAO.addProject(project);
            if (success) session.setAttribute("projectMessageSuccess", (overallMessage.isEmpty() ? "" : overallMessage) + "Dự án đã được thêm thành công!");
            else session.setAttribute("projectMessageError", "Lỗi: Không thể thêm dự án.");
        } else {
            success = projectDAO.updateProject(project);
            if (success) session.setAttribute("projectMessageSuccess", (overallMessage.isEmpty() ? "" : overallMessage) + "Dự án đã được cập nhật thành công!");
            else session.setAttribute("projectMessageError", "Lỗi: Không thể cập nhật dự án.");
        }

        if (!success && !imageActionTaken && overallMessage.isEmpty()) {
            session.setAttribute("projectUpdateMessageError", "Lỗi hệ thống nghiêm trọng, vui lòng thử lại sau.");
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
                session.setAttribute("projectMessageSuccess", "Dự án ID " + id + " đã được xóa thành công!");
                if (projectToDelete != null && projectToDelete.getImageUrl() != null && !projectToDelete.getImageUrl().isEmpty() && !projectToDelete.getImageUrl().contains("default")) {
                    String imageFileName = projectToDelete.getImageUrl().substring(projectToDelete.getImageUrl().lastIndexOf('/') + 1);
                    File imageFile = new File(UPLOAD_DIR_PROJECT_PHYSICAL + File.separator + imageFileName);
                    if (imageFile.exists()) {
                        imageFile.delete();
                    }
                }
            } else {
                session.setAttribute("projectMessageError", "Lỗi: Không thể xóa dự án ID " + id + ".");
            }
        } catch (NumberFormatException e) {
            session.setAttribute("projectMessageError", "ID dự án không hợp lệ để xóa.");
        } catch (Exception e){
            logger.error("Lỗi khi xóa dự án: {}", e.getMessage(), e);
            session.setAttribute("projectUpdateMessageError", "Lỗi hệ thống nghiêm trọng, vui lòng thử lại sau.");
        }
        if(redirectToList){
            response.sendRedirect(request.getContextPath() + "/admin/projects");
        } else {
            listProjects(request, response);
        }
    }
}