package com.bachnt.web.servlet.admin;

import com.bachnt.dao.CommentDAO;
import com.bachnt.model.Comment;
import com.bachnt.dao.ProfileDAO;
import com.bachnt.model.Profile;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/comments")
public class AdminCommentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private CommentDAO commentDAO;
    private ProfileDAO profileDAO;

    @Override
    public void init() throws ServletException {
        commentDAO = new CommentDAO();
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
        if (session.getAttribute("commentMessageSuccess") != null) {
            request.setAttribute("messageSuccess", session.getAttribute("commentMessageSuccess"));
            session.removeAttribute("commentMessageSuccess");
        }
        if (session.getAttribute("commentMessageError") != null) {
            request.setAttribute("messageError", session.getAttribute("commentMessageError"));
            session.removeAttribute("commentMessageError");
        }

        Profile profile = profileDAO.getDefaultProfile();
        request.setAttribute("profileAdmin", profile);

        try {
            switch (action) {
                case "editForm":
                    showEditCommentForm(request, response);
                    break;
                case "delete":
                    deleteCommentAction(request, response, true);
                    break;
                case "list":
                default:
                    listComments(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("messageError", "Lỗi xử lý yêu cầu GET: " + e.getMessage());
            listComments(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/admin/comments?error=NoActionSpecified");
            return;
        }
        HttpSession session = request.getSession();

        try {
            if ("updateStatus".equals(action)) {
                updateStatusAction(request, response);
            } else if ("saveEdit".equals(action)) {
                saveEditedCommentAction(request, response);
            } else if ("delete".equals(action)) {
                deleteCommentAction(request, response, true);
            } else {
                session.setAttribute("commentMessageError", "Hành động POST không hợp lệ.");
                response.sendRedirect(request.getContextPath() + "/admin/comments");
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("commentMessageError", "Lỗi hệ thống khi xử lý POST: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/comments");
        }
    }

    private void listComments(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Comment> listComments = commentDAO.getAllCommentsAdmin();
        request.setAttribute("listComments", listComments);
        request.getRequestDispatcher("/admin/comment-list.jsp").forward(request, response);
    }

    private void showEditCommentForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        try {
            int commentId = Integer.parseInt(request.getParameter("commentId"));
            Comment commentToEdit = commentDAO.getCommentById(commentId);
            if (commentToEdit != null) {
                request.setAttribute("commentToEdit", commentToEdit);
                request.getRequestDispatcher("/admin/comment-edit-form.jsp").forward(request, response);
            } else {
                session.setAttribute("commentMessageError", "Không tìm thấy bình luận để sửa (ID: " + commentId + ").");
                response.sendRedirect(request.getContextPath() + "/admin/comments");
            }
        } catch (NumberFormatException e) {
            session.setAttribute("commentMessageError", "ID bình luận không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/admin/comments");
        }
    }

    private void saveEditedCommentAction(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        try {
            int commentId = Integer.parseInt(request.getParameter("commentId"));
            String newContent = request.getParameter("content");

            if (newContent == null || newContent.trim().isEmpty()) {
                session.setAttribute("commentMessageError", "Nội dung bình luận không được để trống.");
                response.sendRedirect(request.getContextPath() + "/admin/comments?action=editForm&commentId=" + commentId);
                return;
            }

            boolean success = commentDAO.updateCommentContent(commentId, newContent.trim());
            if (success) {
                session.setAttribute("commentMessageSuccess", "Đã cập nhật nội dung bình luận ID " + commentId + ".");
            } else {
                session.setAttribute("commentMessageError", "Không thể cập nhật nội dung bình luận ID " + commentId + ".");
            }
        } catch (NumberFormatException e) {
            session.setAttribute("commentMessageError", "ID bình luận không hợp lệ khi lưu.");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("commentMessageError", "Lỗi máy chủ khi cập nhật nội dung bình luận.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/comments");
    }

    private void updateStatusAction(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        try {
            int commentId = Integer.parseInt(request.getParameter("commentId"));
            String newStatus = request.getParameter("newStatus");

            if (newStatus == null || (!newStatus.equals("approved") && !newStatus.equals("pending") && !newStatus.equals("spam"))) {
                session.setAttribute("commentMessageError", "Trạng thái bình luận không hợp lệ: " + newStatus);
            } else {
                boolean success = commentDAO.updateCommentStatus(commentId, newStatus);
                if (success) {
                    session.setAttribute("commentMessageSuccess", "Đã cập nhật trạng thái bình luận ID " + commentId + " thành '" + newStatus + "'.");
                } else {
                    session.setAttribute("commentMessageError", "Không thể cập nhật trạng thái bình luận ID " + commentId + ".");
                }
            }
        } catch (NumberFormatException e) {
            session.setAttribute("commentMessageError", "ID bình luận không hợp lệ khi cập nhật trạng thái.");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("commentMessageError", "Lỗi máy chủ khi cập nhật trạng thái.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/comments");
    }

    private void deleteCommentAction(HttpServletRequest request, HttpServletResponse response, boolean redirectToList) throws IOException, ServletException {
        HttpSession session = request.getSession();
        try {
            int commentId = Integer.parseInt(request.getParameter("commentId"));
            boolean success = commentDAO.deleteComment(commentId);
            if (success) {
                session.setAttribute("commentMessageSuccess", "Đã xóa bình luận ID " + commentId + ".");
            } else {
                session.setAttribute("commentMessageError", "Không thể xóa bình luận ID " + commentId + ".");
            }
        } catch (NumberFormatException e) {
            session.setAttribute("commentMessageError", "ID bình luận không hợp lệ để xóa.");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("commentMessageError", "Lỗi máy chủ khi xóa bình luận.");
        }
        if(redirectToList){
            response.sendRedirect(request.getContextPath() + "/admin/comments");
        } else {
            listComments(request, response);
        }
    }
}