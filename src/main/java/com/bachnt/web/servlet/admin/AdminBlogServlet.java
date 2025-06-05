package com.bachnt.web.servlet.admin;

import com.bachnt.dao.BlogPostDAO;
import com.bachnt.model.BlogPost;
import com.bachnt.model.Profile;
import com.bachnt.dao.ProfileDAO;

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
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/admin/blog")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 1,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 15
)
public class AdminBlogServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(BlogPostDAO.class);
    private static final long serialVersionUID = 1L;
    private BlogPostDAO blogPostDAO;
    private ProfileDAO profileDAO;
    private static final String UPLOAD_DIR_BLOG_PHYSICAL = "/Users/ngogiakhanh/Documents/PersonalWebsite/PersonalWebsiteUploads/blog_posts";
    private static final String URL_PATH_BLOG_FOR_DB = "/my-uploaded-images/blog_posts";

    @Override
    public void init() throws ServletException {
        blogPostDAO = new BlogPostDAO();
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
        if (session.getAttribute("blogMessageSuccess") != null) {
            request.setAttribute("messageSuccess", session.getAttribute("blogMessageSuccess"));
            session.removeAttribute("blogMessageSuccess");
        }
        if (session.getAttribute("blogMessageError") != null) {
            request.setAttribute("messageError", session.getAttribute("blogMessageError"));
            session.removeAttribute("blogMessageError");
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
                    deletePost(request, response, true);
                    break;
                case "list":
                default:
                    listPosts(request, response);
                    break;
            }
        } catch (Exception e) {
            logger.error("Lỗi không mong muốn trong AdminBlogServlet action {}: {}", action, e.getMessage(), e);
            session.setAttribute("blogUpdateError", "Lỗi hệ thống nghiêm trọng, vui lòng thử lại sau.");
            listPosts(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/admin/blog?error=NoActionSpecified");
            return;
        }
        HttpSession session = request.getSession();

        try {
            switch (action) {
                case "save":
                    savePost(request, response);
                    break;
                case "delete":
                    deletePost(request, response, true);
                    break;
                default:
                    session.setAttribute("blogMessageError", "Hành động POST không hợp lệ.");
                    response.sendRedirect(request.getContextPath() + "/admin/blog");
                    break;
            }
        } catch (Exception e) {
            logger.error("Lỗi không mong muốn trong AdminBlogServlet action {}: {}", action, e.getMessage(), e);
            session.setAttribute("blogUpdateError", "Lỗi hệ thống nghiêm trọng, vui lòng thử lại sau.");
            response.sendRedirect(request.getContextPath() + "/admin/blog");
        }
    }

    private void listPosts(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<BlogPost> listBlogPosts = blogPostDAO.getAllBlogPostsForAdmin();
        request.setAttribute("listBlogPosts", listBlogPosts);
        request.getRequestDispatcher("/admin/blog-list.jsp").forward(request, response);
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Profile defaultProfile = profileDAO.getDefaultProfile();
        String defaultAuthor = (defaultProfile != null) ? defaultProfile.getName() : "Admin";
        request.setAttribute("defaultAuthor", defaultAuthor);
        request.setAttribute("blogPost", new BlogPost());
        request.setAttribute("formAction", "add");
        request.getRequestDispatcher("/admin/blog-form.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            BlogPost existingPost = blogPostDAO.getBlogPostById(id);
            if (existingPost != null) {
                request.setAttribute("blogPost", existingPost);
                request.setAttribute("formAction", "edit");
                request.getRequestDispatcher("/admin/blog-form.jsp").forward(request, response);
            } else {
                session.setAttribute("blogMessageError", "Không tìm thấy bài viết để sửa (ID: " + id + ").");
                response.sendRedirect(request.getContextPath() + "/admin/blog");
            }
        } catch (NumberFormatException e) {
            session.setAttribute("blogUpdateError", "Lỗi hệ thống nghiêm trọng, vui lòng thử lại sau.");
            response.sendRedirect(request.getContextPath() + "/admin/blog");
        }
    }

    private void savePost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();
        String idParam = request.getParameter("id");

        BlogPost blogPost = new BlogPost();
        boolean isNew = (idParam == null || idParam.isEmpty());
        String overallMessage = "";

        if (!isNew) {
            blogPost.setId(Integer.parseInt(idParam));
            BlogPost existingPost = blogPostDAO.getBlogPostById(blogPost.getId());
            if (existingPost != null) {
                blogPost.setCreatedDate(existingPost.getCreatedDate());
                blogPost.setImageUrl(existingPost.getImageUrl());
            } else {
                isNew = true;
                blogPost.setCreatedDate(new Date());
            }
        } else {
            blogPost.setCreatedDate(new Date());
        }
        blogPost.setModifiedDate(new Date());

        blogPost.setTitle(request.getParameter("title"));
        blogPost.setContent(request.getParameter("content"));
        blogPost.setSummary(request.getParameter("summary"));
        blogPost.setAuthor(request.getParameter("author"));
        blogPost.setCategory(request.getParameter("category"));
        blogPost.setTags(request.getParameter("tags"));
        blogPost.setStatus(request.getParameter("status"));

        Part filePart = request.getPart("imageFile");
        String currentImageUrl = blogPost.getImageUrl();
        String newImageUrlFromUpload = null;
        boolean imageActionTaken = false;

        if (filePart != null && filePart.getSize() > 0) {
            String originalFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            if (originalFileName != null && !originalFileName.isEmpty()) {
                String fileExtension = "";
                int i = originalFileName.lastIndexOf('.');
                if (i > 0) fileExtension = originalFileName.substring(i);
                String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

                String physicalUploadPath = UPLOAD_DIR_BLOG_PHYSICAL;
                File uploadDir = new File(physicalUploadPath);
                if (!uploadDir.exists()) {
                    boolean dirCreated = uploadDir.mkdirs();
                    if(!dirCreated){
                        session.setAttribute("blogMessageError", "Lỗi nghiêm trọng: Không thể tạo thư mục upload cho blog.");
                        response.sendRedirect(request.getContextPath() + "/admin/blog");
                        return;
                    }
                }

                String physicalFilePath = physicalUploadPath + File.separator + uniqueFileName;
                try {
                    filePart.write(physicalFilePath);
                    newImageUrlFromUpload = URL_PATH_BLOG_FOR_DB + "/" + uniqueFileName;
                    imageActionTaken = true;
                    overallMessage += "Ảnh bài viết đã được tải lên. ";

                    if (currentImageUrl != null && !currentImageUrl.isEmpty() && !currentImageUrl.contains("default")) {
                        String oldFileNameOnly = currentImageUrl.substring(currentImageUrl.lastIndexOf('/') + 1);
                        File oldFilePhysical = new File(UPLOAD_DIR_BLOG_PHYSICAL + File.separator + oldFileNameOnly);
                        if (oldFilePhysical.exists()) oldFilePhysical.delete();
                    }
                } catch (IOException e) {
                    logger.error("Lỗi không mong muốn trong AdminBlogServlet action {}: {}", e.getMessage(), e);
                    session.setAttribute("blogUpdateError", "Lỗi hệ thống nghiêm trọng, vui lòng thử lại sau.");
                }
            }
        }

        String deleteImageFlag = request.getParameter("deleteImage");
        if ("true".equals(deleteImageFlag) && newImageUrlFromUpload == null) {
            if (currentImageUrl != null && !currentImageUrl.isEmpty() && !currentImageUrl.contains("default")) {
                String oldFileNameOnly = currentImageUrl.substring(currentImageUrl.lastIndexOf('/') + 1);
                File oldFilePhysical = new File(UPLOAD_DIR_BLOG_PHYSICAL + File.separator + oldFileNameOnly);
                if (oldFilePhysical.exists()) oldFilePhysical.delete();
            }
            blogPost.setImageUrl(null);
            imageActionTaken = true;
            overallMessage += "Ảnh bài viết đã được xóa. ";
        } else if (newImageUrlFromUpload != null) {
            blogPost.setImageUrl(newImageUrlFromUpload);
        }

        boolean success;
        if (isNew) {
            success = blogPostDAO.addBlogPost(blogPost);
            if (success) session.setAttribute("blogMessageSuccess", (overallMessage.isEmpty() ? "" : overallMessage) + "Bài viết đã được thêm thành công!");
            else  session.setAttribute("blogMessageError", "Lỗi: Không thể thêm bài viết.");
        } else {
            success = blogPostDAO.updateBlogPost(blogPost);
            if (success) session.setAttribute("blogMessageSuccess", (overallMessage.isEmpty() ? "" : overallMessage) + "Bài viết đã được cập nhật thành công!");
            else session.setAttribute("blogMessageError", "Lỗi: Không thể cập nhật bài viết.");
        }

        if (!success && !imageActionTaken && overallMessage.isEmpty()) { // Trường hợp chỉ lỗi lưu text và không có action ảnh
            session.setAttribute("blogMessageError", "Lỗi: Không thể lưu thông tin bài viết.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/blog");
    }

    private void deletePost(HttpServletRequest request, HttpServletResponse response, boolean redirectToList) throws IOException, ServletException {
        HttpSession session = request.getSession();
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            BlogPost postToDelete = blogPostDAO.getBlogPostById(id);
            boolean success = blogPostDAO.deleteBlogPost(id);
            if (success) {
                session.setAttribute("blogMessageSuccess", "Bài viết ID " + id + " đã được xóa thành công!");
                if (postToDelete != null && postToDelete.getImageUrl() != null && !postToDelete.getImageUrl().isEmpty() && !postToDelete.getImageUrl().contains("default")) {
                    String imageFileName = postToDelete.getImageUrl().substring(postToDelete.getImageUrl().lastIndexOf('/') + 1);
                    File imageFile = new File(UPLOAD_DIR_BLOG_PHYSICAL + File.separator + imageFileName);
                    if (imageFile.exists()) {
                        imageFile.delete();
                    }
                }
            } else {
                session.setAttribute("blogMessageError", "Lỗi: Không thể xóa bài viết ID " + id + ".");
            }
        } catch (NumberFormatException e) {
            session.setAttribute("blogMessageError", "ID bài viết không hợp lệ để xóa.");
        } catch (Exception e){
            logger.error("Lỗi không mong muốn trong AdminBlogServlet action {}: {}", e.getMessage(), e);
            session.setAttribute("blogUpdateError", "Lỗi hệ thống nghiêm trọng, vui lòng thử lại sau.");
        }
        if(redirectToList){
            response.sendRedirect(request.getContextPath() + "/admin/blog");
        } else {
            listPosts(request, response);
        }
    }
}