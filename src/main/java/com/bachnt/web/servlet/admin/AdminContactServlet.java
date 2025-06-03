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

@WebServlet("/admin/messages")
public class AdminContactServlet extends HttpServlet {
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
        // Auth filter đã xử lý việc đăng nhập

        String action = request.getParameter("action");
        // Mặc định là list, không cần gán lại nếu null vì switch sẽ vào default

        try {
            // Xử lý các thông báo từ session (sau redirect từ POST)
            HttpSession session = request.getSession();
            if (session.getAttribute("messageOperationStatus") != null) {
                request.setAttribute("operationStatus", session.getAttribute("messageOperationStatus"));
                session.removeAttribute("messageOperationStatus");
            }
            if (session.getAttribute("messageOperationError") != null) {
                request.setAttribute("operationError", session.getAttribute("messageOperationError"));
                session.removeAttribute("messageOperationError");
            }

            // Chỉ có action delete là có thể xử lý qua GET (mặc dù POST được khuyến khích hơn)
            // Các action khác như view chi tiết thường không thay đổi trạng thái DB qua GET
            if ("delete".equals(action)) {
                handleDeleteMessage(request, response, true); // true để biết là redirect
            } else {
                // Tất cả các trường hợp khác (bao gồm action=null hoặc action=list) sẽ hiển thị danh sách
                listMessages(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("operationError", "Lỗi không xác định: " + e.getMessage());
            listMessages(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        // Auth filter đã xử lý

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
            e.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("messageOperationError", "Lỗi hệ thống khi xử lý yêu cầu: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/messages");
        }
    }

    private void listMessages(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<ContactMessage> messages = contactMessageDAO.getAllContactMessages();
        Profile profile = profileDAO.getDefaultProfile(); // Lấy profile cho tên website

        request.setAttribute("messages", messages);
        request.setAttribute("profileAdmin", profile); // Đặt tên khác để tránh trùng với profile của trang public nếu có
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
            e.printStackTrace();
            session.setAttribute("messageOperationError", "Lỗi máy chủ khi cập nhật trạng thái.");
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
            e.printStackTrace();
            session.setAttribute("messageOperationError", "Lỗi máy chủ khi xóa tin nhắn.");
        }

        if (redirect) {
            response.sendRedirect(request.getContextPath() + "/admin/messages");
        } else { // Nếu không redirect (ví dụ xử lý AJAX), ta có thể không làm gì hoặc forward lại
            listMessages(request, response);
        }
    }
}