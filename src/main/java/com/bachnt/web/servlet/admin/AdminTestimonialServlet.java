package com.bachnt.web.servlet.admin;

import com.bachnt.dao.TestimonialDAO;
import com.bachnt.model.Testimonial;
import com.bachnt.dao.ProfileDAO; // For admin header consistency
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
import java.util.List;
import java.util.UUID;

@WebServlet("/admin/testimonials")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 1,  // 1 MB
        maxFileSize = 1024 * 1024 * 5,  // 5 MB
        maxRequestSize = 1024 * 1024 * 10 // 10 MB
)
public class AdminTestimonialServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TestimonialDAO testimonialDAO;
    private ProfileDAO profileDAO;

    private static final String UPLOAD_DIR_TESTIMONIAL_PHYSICAL = "/Users/ngogiakhanh/Documents/PersonalWebsite/PersonalWebsiteUploads/testimonials";
    private static final String URL_PATH_TESTIMONIAL_FOR_DB = "/my-uploaded-images/testimonials";

    @Override
    public void init() throws ServletException {
        testimonialDAO = new TestimonialDAO();
        profileDAO = new ProfileDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if (action == null) action = "list";

        HttpSession session = request.getSession();
        if (session.getAttribute("testimonialMessageSuccess") != null) {
            request.setAttribute("messageSuccess", session.getAttribute("testimonialMessageSuccess"));
            session.removeAttribute("testimonialMessageSuccess");
        }
        if (session.getAttribute("testimonialMessageError") != null) {
            request.setAttribute("messageError", session.getAttribute("testimonialMessageError"));
            session.removeAttribute("testimonialMessageError");
        }

        Profile profile = profileDAO.getDefaultProfile(); // For admin header
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
                    deleteTestimonialAction(request, response, true);
                    break;
                case "list":
                default:
                    listTestimonials(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("messageError", "Lỗi xử lý yêu cầu: " + e.getMessage());
            listTestimonials(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/admin/testimonials?error=NoActionSpecified");
            return;
        }
        HttpSession session = request.getSession();

        try {
            switch (action) {
                case "save":
                    saveTestimonial(request, response);
                    break;
                case "delete":
                    deleteTestimonialAction(request, response, true);
                    break;
                default:
                    session.setAttribute("testimonialMessageError", "Hành động không hợp lệ.");
                    response.sendRedirect(request.getContextPath() + "/admin/testimonials");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("testimonialMessageError", "Lỗi hệ thống: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/testimonials");
        }
    }

    private void listTestimonials(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Testimonial> listTestimonials = testimonialDAO.getAllTestimonialsForAdmin();
        request.setAttribute("listTestimonials", listTestimonials);
        request.getRequestDispatcher("/admin/testimonial-list.jsp").forward(request, response);
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("testimonial", new Testimonial());
        request.setAttribute("formAction", "add");
        request.getRequestDispatcher("/admin/testimonial-form.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Testimonial existingTestimonial = testimonialDAO.getTestimonialById(id);
            if (existingTestimonial != null) {
                request.setAttribute("testimonial", existingTestimonial);
                request.setAttribute("formAction", "edit");
                request.getRequestDispatcher("/admin/testimonial-form.jsp").forward(request, response);
            } else {
                session.setAttribute("testimonialMessageError", "Không tìm thấy đánh giá để sửa.");
                response.sendRedirect(request.getContextPath() + "/admin/testimonials");
            }
        } catch (NumberFormatException e) {
            session.setAttribute("testimonialMessageError", "ID đánh giá không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/admin/testimonials");
        }
    }

    private void saveTestimonial(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();
        String idParam = request.getParameter("id");
        Testimonial testimonial = new Testimonial();
        boolean isNew = (idParam == null || idParam.isEmpty());
        String overallMessage = "";

        if (!isNew) {
            testimonial.setId(Integer.parseInt(idParam));
            Testimonial existing = testimonialDAO.getTestimonialById(testimonial.getId());
            if(existing != null) testimonial.setClientImageUrl(existing.getClientImageUrl()); // Giữ ảnh cũ
        }

        testimonial.setClientName(request.getParameter("clientName"));
        testimonial.setClientPositionCompany(request.getParameter("clientPositionCompany"));
        testimonial.setQuoteText(request.getParameter("quoteText"));
        try {
            testimonial.setDisplayOrder(Integer.parseInt(request.getParameter("displayOrder")));
        } catch (NumberFormatException e) {
            testimonial.setDisplayOrder(0); // Mặc định
        }


        Part filePart = request.getPart("clientImageFile");
        String currentImageUrl = testimonial.getClientImageUrl();
        String newImageUrlFromUpload = null;
        boolean imageActionTaken = false;

        if (filePart != null && filePart.getSize() > 0) {
            String originalFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            if (originalFileName != null && !originalFileName.isEmpty()) {
                String fileExtension = "";
                int i = originalFileName.lastIndexOf('.');
                if (i > 0) fileExtension = originalFileName.substring(i);
                String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

                String physicalUploadPath = UPLOAD_DIR_TESTIMONIAL_PHYSICAL;
                File uploadDir = new File(physicalUploadPath);
                if (!uploadDir.exists()) uploadDir.mkdirs();

                String physicalFilePath = physicalUploadPath + File.separator + uniqueFileName;
                try {
                    filePart.write(physicalFilePath);
                    newImageUrlFromUpload = URL_PATH_TESTIMONIAL_FOR_DB + "/" + uniqueFileName;
                    imageActionTaken = true;
                    overallMessage += "Ảnh client đã được tải lên. ";

                    if (currentImageUrl != null && !currentImageUrl.isEmpty() && !currentImageUrl.contains("default") && !currentImageUrl.startsWith("https://ui-avatars.com")) {
                        String oldFileNameOnly = currentImageUrl.substring(currentImageUrl.lastIndexOf('/') + 1);
                        File oldFilePhysical = new File(UPLOAD_DIR_TESTIMONIAL_PHYSICAL + File.separator + oldFileNameOnly);
                        if (oldFilePhysical.exists()) oldFilePhysical.delete();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    session.setAttribute("testimonialMessageError", "Lỗi khi ghi file ảnh client: " + e.getMessage());
                }
            }
        }

        String deleteImageFlag = request.getParameter("deleteImage");
        if ("true".equals(deleteImageFlag) && newImageUrlFromUpload == null) {
            if (currentImageUrl != null && !currentImageUrl.isEmpty() && !currentImageUrl.contains("default") && !currentImageUrl.startsWith("https://ui-avatars.com")) {
                String oldFileNameOnly = currentImageUrl.substring(currentImageUrl.lastIndexOf('/') + 1);
                File oldFilePhysical = new File(UPLOAD_DIR_TESTIMONIAL_PHYSICAL + File.separator + oldFileNameOnly);
                if (oldFilePhysical.exists()) oldFilePhysical.delete();
            }
            testimonial.setClientImageUrl(null); // Xóa đường dẫn trong DB
            imageActionTaken = true;
            overallMessage += "Ảnh client đã được xóa. ";
        } else if (newImageUrlFromUpload != null) {
            testimonial.setClientImageUrl(newImageUrlFromUpload);
        }
        // Nếu không có hành động gì với ảnh, clientImageUrl đã được set từ existing hoặc là null (cho mới)

        boolean success;
        if (isNew) {
            success = testimonialDAO.addTestimonial(testimonial);
            if (success) session.setAttribute("testimonialMessageSuccess", (overallMessage.isEmpty()?"":overallMessage) + "Đánh giá đã được thêm thành công!");
        } else {
            success = testimonialDAO.updateTestimonial(testimonial);
            if (success) session.setAttribute("testimonialMessageSuccess", (overallMessage.isEmpty()?"":overallMessage) + "Đánh giá đã được cập nhật thành công!");
        }

        if (!success && !imageActionTaken && overallMessage.isEmpty()) {
            session.setAttribute("testimonialMessageError", "Lỗi: Không thể lưu đánh giá.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/testimonials");
    }

    private void deleteTestimonialAction(HttpServletRequest request, HttpServletResponse response, boolean redirectToList) throws IOException, ServletException {
        HttpSession session = request.getSession();
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Testimonial testimonialToDelete = testimonialDAO.getTestimonialById(id);
            boolean success = testimonialDAO.deleteTestimonial(id);
            if (success) {
                session.setAttribute("testimonialMessageSuccess", "Đánh giá ID " + id + " đã được xóa thành công!");
                if (testimonialToDelete != null && testimonialToDelete.getClientImageUrl() != null &&
                        !testimonialToDelete.getClientImageUrl().isEmpty() &&
                        !testimonialToDelete.getClientImageUrl().contains("default") &&
                        !testimonialToDelete.getClientImageUrl().startsWith("https://ui-avatars.com")) {

                    String imageFileName = testimonialToDelete.getClientImageUrl().substring(testimonialToDelete.getClientImageUrl().lastIndexOf('/') + 1);
                    File imageFile = new File(UPLOAD_DIR_TESTIMONIAL_PHYSICAL + File.separator + imageFileName);
                    if (imageFile.exists()) {
                        imageFile.delete();
                    }
                }
            } else {
                session.setAttribute("testimonialMessageError", "Lỗi: Không thể xóa đánh giá ID " + id + ".");
            }
        } catch (NumberFormatException e) {
            session.setAttribute("testimonialMessageError", "ID đánh giá không hợp lệ để xóa.");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("testimonialMessageError", "Lỗi hệ thống khi xóa đánh giá.");
        }
        if(redirectToList){
            response.sendRedirect(request.getContextPath() + "/admin/testimonials");
        } else {
            listTestimonials(request, response);
        }
    }
}