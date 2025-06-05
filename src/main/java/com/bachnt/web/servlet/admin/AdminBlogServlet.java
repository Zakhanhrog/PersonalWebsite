package com.bachnt.web.servlet.admin;

import com.bachnt.dao.BlogPostDAO;
import com.bachnt.model.BlogPost;
import com.bachnt.model.Profile;
import com.bachnt.dao.ProfileDAO;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/admin/blog")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 1,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 15
)
public class AdminBlogServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(AdminBlogServlet.class);
    private static final long serialVersionUID = 1L;
    private BlogPostDAO blogPostDAO;
    private ProfileDAO profileDAO;
    private static final String BLOG_SUBFOLDER = "blog_posts";

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
            logger.error("Error in AdminBlogServlet doGet action {}: {}", action, e.getMessage(), e);
            session.setAttribute("blogMessageError", "A system error occurred. Please try again later.");
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
                    session.setAttribute("blogMessageError", "Invalid POST action.");
                    response.sendRedirect(request.getContextPath() + "/admin/blog");
                    break;
            }
        } catch (Exception e) {
            logger.error("Error in AdminBlogServlet doPost action {}: {}", action, e.getMessage(), e);
            session.setAttribute("blogMessageError", "A critical system error occurred. Please try again later.");
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
                session.setAttribute("blogMessageError", "Blog post not found for editing (ID: " + id + ").");
                response.sendRedirect(request.getContextPath() + "/admin/blog");
            }
        } catch (NumberFormatException e) {
            logger.warn("Invalid blog post ID format for editing: {}", request.getParameter("id"));
            session.setAttribute("blogMessageError", "Invalid blog post ID.");
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
            try {
                blogPost.setId(Integer.parseInt(idParam));
                BlogPost existingPost = blogPostDAO.getBlogPostById(blogPost.getId());
                if (existingPost != null) {
                    blogPost.setCreatedDate(existingPost.getCreatedDate());
                    blogPost.setImageUrl(existingPost.getImageUrl());
                } else {
                    isNew = true;
                    blogPost.setCreatedDate(new Date());
                }
            } catch (NumberFormatException e) {
                logger.warn("Invalid blog post ID format for saving existing post: {}", idParam);
                isNew = true; // Treat as new if ID is invalid
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
        String currentRelativeImageUrl = blogPost.getImageUrl();
        String newRelativeImageUrlFromUpload = null;

        if (filePart != null && filePart.getSize() > 0 && filePart.getSubmittedFileName() != null && !filePart.getSubmittedFileName().isEmpty()) {
            try {
                newRelativeImageUrlFromUpload = FileUploadUtils.saveUploadedFile(filePart, BLOG_SUBFOLDER);
                if (newRelativeImageUrlFromUpload != null) {
                    overallMessage += "Blog post image uploaded. ";
                    if (currentRelativeImageUrl != null && !currentRelativeImageUrl.isEmpty() && !currentRelativeImageUrl.contains("default")) {
                        deletePhysicalFile(currentRelativeImageUrl);
                    }
                } else {
                    session.setAttribute("blogMessageError", "Failed to save uploaded file.");
                }
            } catch (IOException e) {
                logger.error("Error saving uploaded file for blog post: {}", e.getMessage(), e);
                session.setAttribute("blogMessageError", "Error uploading file: " + e.getMessage());
            }
        }

        String deleteImageFlag = request.getParameter("deleteImage");
        if ("true".equals(deleteImageFlag) && newRelativeImageUrlFromUpload == null) {
            if (currentRelativeImageUrl != null && !currentRelativeImageUrl.isEmpty() && !currentRelativeImageUrl.contains("default")) {
                deletePhysicalFile(currentRelativeImageUrl);
            }
            blogPost.setImageUrl(null);
            overallMessage += "Blog post image removed. ";
        } else if (newRelativeImageUrlFromUpload != null) {
            blogPost.setImageUrl(newRelativeImageUrlFromUpload);
        }


        boolean success;
        if (isNew) {
            success = blogPostDAO.addBlogPost(blogPost);
            if (success) session.setAttribute("blogMessageSuccess", overallMessage + "Blog post added successfully!");
            else  session.setAttribute("blogMessageError", "Error: Could not add blog post.");
        } else {
            success = blogPostDAO.updateBlogPost(blogPost);
            if (success) session.setAttribute("blogMessageSuccess", overallMessage + "Blog post updated successfully!");
            else session.setAttribute("blogMessageError", "Error: Could not update blog post.");
        }

        if (!success && overallMessage.isEmpty()) {
            session.setAttribute("blogMessageError", "Error: Could not save blog post information.");
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
                session.setAttribute("blogMessageSuccess", "Blog post ID " + id + " deleted successfully!");
                if (postToDelete != null && postToDelete.getImageUrl() != null && !postToDelete.getImageUrl().isEmpty() && !postToDelete.getImageUrl().contains("default")) {
                    deletePhysicalFile(postToDelete.getImageUrl());
                }
            } else {
                session.setAttribute("blogMessageError", "Error: Could not delete blog post ID " + id + ".");
            }
        } catch (NumberFormatException e) {
            logger.warn("Invalid blog post ID format for deletion: {}", request.getParameter("id"));
            session.setAttribute("blogMessageError", "Invalid blog post ID for deletion.");
        } catch (Exception e){
            logger.error("Error deleting blog post: {}", e.getMessage(), e);
            session.setAttribute("blogMessageError", "A system error occurred while deleting the post.");
        }

        if(redirectToList){
            response.sendRedirect(request.getContextPath() + "/admin/blog");
        } else {
            listPosts(request, response);
        }
    }

    private void deletePhysicalFile(String relativeImagePath) {
        if (relativeImagePath == null || relativeImagePath.isEmpty()) {
            return;
        }
        try {
            Path baseDir = Paths.get(FileUploadUtils.getBaseUploadDirectory());
            Path filePath = baseDir.resolve(relativeImagePath);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            logger.error("Error deleting physical file {}: {}", relativeImagePath, e.getMessage(), e);
        }
    }
}