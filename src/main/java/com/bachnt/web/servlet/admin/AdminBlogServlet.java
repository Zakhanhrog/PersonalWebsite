package com.bachnt.web.servlet.admin;

import com.bachnt.dao.BlogPostDAO;
import com.bachnt.model.BlogPost;
import com.bachnt.model.Profile; // Để lấy tên tác giả mặc định nếu cần
import com.bachnt.dao.ProfileDAO;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@WebServlet("/admin/blog")
public class AdminBlogServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private BlogPostDAO blogPostDAO;
    private ProfileDAO profileDAO; // Để lấy tên tác giả mặc định

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
            action = "list"; // Default action
        }

        try {
            switch (action) {
                case "add":
                    showNewForm(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "delete": // Xử lý GET cho delete (có thể không an toàn bằng POST)
                    deletePost(request, response);
                    break;
                case "list":
                default:
                    listPosts(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi xử lý yêu cầu: " + e.getMessage());
            listPosts(request, response); // Hiển thị lại danh sách với lỗi
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

        try {
            switch (action) {
                case "save":
                    savePost(request, response);
                    break;
                case "delete": // Xử lý POST cho delete an toàn hơn
                    deletePost(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/admin/blog?error=InvalidAction");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("blogMessageError", "Lỗi hệ thống: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/blog");
        }
    }

    private void listPosts(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<BlogPost> listBlogPosts = blogPostDAO.getAllBlogPostsForAdmin(); // Lấy tất cả bài, kể cả draft
        request.setAttribute("listBlogPosts", listBlogPosts);

        HttpSession session = request.getSession();
        if (session.getAttribute("blogMessageSuccess") != null) {
            request.setAttribute("messageSuccess", session.getAttribute("blogMessageSuccess"));
            session.removeAttribute("blogMessageSuccess");
        }
        if (session.getAttribute("blogMessageError") != null) {
            request.setAttribute("messageError", session.getAttribute("blogMessageError"));
            session.removeAttribute("blogMessageError");
        }
        request.getRequestDispatcher("/admin/blog-list.jsp").forward(request, response);
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Profile defaultProfile = profileDAO.getDefaultProfile();
        String defaultAuthor = (defaultProfile != null) ? defaultProfile.getName() : "Admin";
        request.setAttribute("defaultAuthor", defaultAuthor);
        request.setAttribute("blogPost", new BlogPost()); // Gửi một đối tượng BlogPost rỗng
        request.setAttribute("formAction", "add");
        request.getRequestDispatcher("/admin/blog-form.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            BlogPost existingPost = blogPostDAO.getBlogPostById(id);
            if (existingPost != null) {
                request.setAttribute("blogPost", existingPost);
                request.setAttribute("formAction", "edit");
                request.getRequestDispatcher("/admin/blog-form.jsp").forward(request, response);
            } else {
                HttpSession session = request.getSession();
                session.setAttribute("blogMessageError", "Không tìm thấy bài viết để sửa.");
                response.sendRedirect(request.getContextPath() + "/admin/blog");
            }
        } catch (NumberFormatException e) {
            HttpSession session = request.getSession();
            session.setAttribute("blogMessageError", "ID bài viết không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/admin/blog");
        }
    }

    private void savePost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String idParam = request.getParameter("id");

        BlogPost blogPost = new BlogPost();
        boolean isNew = (idParam == null || idParam.isEmpty());

        if (!isNew) {
            blogPost.setId(Integer.parseInt(idParam));
        }

        blogPost.setTitle(request.getParameter("title"));
        blogPost.setContent(request.getParameter("content"));
        blogPost.setSummary(request.getParameter("summary"));
        blogPost.setAuthor(request.getParameter("author"));
        blogPost.setImageUrl(request.getParameter("imageUrl"));
        blogPost.setCategory(request.getParameter("category"));
        blogPost.setTags(request.getParameter("tags"));
        blogPost.setStatus(request.getParameter("status"));

        boolean success;
        if (isNew) {
            blogPost.setCreatedDate(new Date());
            blogPost.setModifiedDate(new Date());
            success = blogPostDAO.addBlogPost(blogPost);
            if (success) session.setAttribute("blogMessageSuccess", "Bài viết đã được thêm thành công!");
        } else {
            // Lấy createdDate cũ nếu không muốn nó bị ghi đè bởi new Date() khi update
            BlogPost existingPost = blogPostDAO.getBlogPostById(blogPost.getId());
            if (existingPost != null) {
                blogPost.setCreatedDate(existingPost.getCreatedDate());
            } else { // Không nên xảy ra nếu ID đúng
                blogPost.setCreatedDate(new Date());
            }
            blogPost.setModifiedDate(new Date()); // Luôn cập nhật modifiedDate
            success = blogPostDAO.updateBlogPost(blogPost);
            if (success) session.setAttribute("blogMessageSuccess", "Bài viết đã được cập nhật thành công!");
        }

        if (!success) {
            session.setAttribute("blogMessageError", "Lỗi: Không thể lưu bài viết.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/blog");
    }

    private void deletePost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            boolean success = blogPostDAO.deleteBlogPost(id);
            if (success) {
                session.setAttribute("blogMessageSuccess", "Bài viết đã được xóa thành công!");
            } else {
                session.setAttribute("blogMessageError", "Lỗi: Không thể xóa bài viết.");
            }
        } catch (NumberFormatException e) {
            session.setAttribute("blogMessageError", "ID bài viết không hợp lệ để xóa.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/blog");
    }
}