package com.bachnt.web.servlet.admin;

import com.bachnt.dao.ContactMessageDAO;
import com.bachnt.dao.ProfileDAO; // Cần cho tên website trên trang admin
import com.bachnt.model.ContactMessage;
import com.bachnt.model.Profile;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/admin/messages")
public class AdminContactServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(AdminContactServlet.class);
    private static final long serialVersionUID = 1L;
    private ContactMessageDAO contactMessageDAO;
    private ProfileDAO profileDAO;

    @Override
    public void init() throws ServletException {
        contactMessageDAO = new ContactMessageDAO();
        profileDAO = new ProfileDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        try {
            HttpSession session = request.getSession();
            if (session.getAttribute("messageOperationStatus") != null) {
                request.setAttribute("operationStatus", session.getAttribute("messageOperationStatus"));
                session.removeAttribute("messageOperationStatus");
            }
            if (session.getAttribute("messageOperationError") != null) {
                request.setAttribute("operationError", session.getAttribute("messageOperationError"));
                session.removeAttribute("messageOperationError");
            }

            if ("delete".equals(action)) {
                handleDeleteMessage(request, response, true);
            } else {
                listMessages(request, response);
            }
        } catch (Exception e) {
            logger.error("Yêu cầu thông báo quản trị viên xử lý lỗi", action, e.getMessage(), e);
            request.setAttribute("operationError", "Lỗi không xác định: " + e.getMessage());
            listMessages(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/admin/messages?error=NoActionSpecified");
            return;
        }

        try {
            if ("updateStatus".equals(action)) {
                handleUpdateStatus(request, response);
            } else if ("delete".equals(action)) {
                handleDeleteMessage(request, response, true); // true để biết là redirect
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/messages?error=InvalidPostAction");
            }
        } catch (Exception e) {
            logger.error("Lỗi khi xử lý yêu cầu POST: {}", e.getMessage(), e);
            HttpSession session = request.getSession();
            session.setAttribute("contactUpdateError", "Lỗi hệ thống nghiêm trọng, vui lòng thử lại sau.");
            response.sendRedirect(request.getContextPath() + "/admin/messages");
        }
    }

    private void listMessages(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<ContactMessage> messages = contactMessageDAO.getAllContactMessages();
        Profile profile = profileDAO.getDefaultProfile();
        request.setAttribute("messages", messages);
        request.setAttribute("profileAdmin", profile);
        request.getRequestDispatcher("/admin/messages.jsp").forward(request, response);
    }

    private void handleUpdateStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String status = request.getParameter("status");

            if (status == null || (!status.equals("new") && !status.equals("read") && !status.equals("replied") && !status.equals("archived"))) {
                session.setAttribute("messageOperationError", "Giá trị trạng thái không hợp lệ.");
                response.sendRedirect(request.getContextPath() + "/admin/messages");
                return;
            }

            boolean success = contactMessageDAO.updateMessageStatus(id, status);
            if (success) {
                session.setAttribute("messageOperationStatus", "Cập nhật trạng thái tin nhắn ID " + id + " thành '" + status + "' thành công!");
            } else {
                session.setAttribute("messageOperationError", "Không thể cập nhật trạng thái cho tin nhắn ID " + id + ".");
            }
        } catch (NumberFormatException e) {
            session.setAttribute("messageOperationError", "ID tin nhắn không hợp lệ.");
        } catch (Exception e) {
            logger.error("Lỗi khi cập nhật trạng thái tin nhắn", e);
            session.setAttribute("contactUpdateError", "Lỗi hệ thống nghiêm trọng, vui lòng thử lại sau.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/messages");
    }

    private void handleDeleteMessage(HttpServletRequest request, HttpServletResponse response, boolean redirect) throws IOException, ServletException {
        HttpSession session = request.getSession();
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            boolean success = contactMessageDAO.deleteContactMessage(id);
            if (success) {
                session.setAttribute("messageOperationStatus", "Xóa tin nhắn ID " + id + " thành công!");
            } else {
                session.setAttribute("messageOperationError", "Không thể xóa tin nhắn ID " + id + ".");
            }
        } catch (NumberFormatException e) {
            session.setAttribute("messageOperationError", "ID tin nhắn không hợp lệ để xóa.");
        } catch (Exception e) {
            logger.error("Lỗi khi xóa tin nhắn", e);
            session.setAttribute("contactUpdateError", "Lỗi hệ thống nghiêm trọng, vui lòng thử lại sau.");
        }

        if (redirect) {
            response.sendRedirect(request.getContextPath() + "/admin/messages");
        } else {
            listMessages(request, response);
        }
    }
}