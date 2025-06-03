package com.bachnt.web.servlet.admin;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
// import javax.servlet.http.HttpSession; // For authentication

import com.bachnt.dao.ContactMessageDAO;
import com.bachnt.dao.ProfileDAO;
import com.bachnt.model.ContactMessage;
import com.bachnt.model.Profile;

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
        // Basic Authentication Check (Placeholder - implement proper filter later)
        // HttpSession session = request.getSession(false);
        // if (session == null || session.getAttribute("adminUser") == null) {
        //     response.sendRedirect(request.getContextPath() + "/admin/login");
        //     return;
        // }

        String action = request.getParameter("action");
        if (action == null) {
            action = "list"; // Default action
        }

        try {
            switch (action) {
                case "view":
                    viewMessage(request, response);
                    break;
                case "delete":
                    deleteMessage(request, response);
                    break;
                case "list":
                default:
                    listMessages(request, response);
                    break;
            }
        } catch (ServletException | IOException e) {
            e.printStackTrace(); // Log error
            request.setAttribute("errorMessage", "Error processing request: " + e.getMessage());
            listMessages(request, response); // Show list with error
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Basic Authentication Check (Placeholder - implement proper filter later)
        // HttpSession session = request.getSession(false);
        // if (session == null || session.getAttribute("adminUser") == null) {
        //     response.sendRedirect(request.getContextPath() + "/admin/login");
        //     return;
        // }

        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/admin/messages");
            return;
        }

        try {
            if ("updateStatus".equals(action)) {
                updateStatus(request, response);
            } else if ("delete".equals(action)) { // Handle delete via POST for robustness
                deleteMessage(request, response);
            }
            else {
                response.sendRedirect(request.getContextPath() + "/admin/messages");
            }
        } catch (ServletException | IOException e) {
            e.printStackTrace(); // Log error
            request.setAttribute("errorMessage", "Error processing request: " + e.getMessage());
            listMessages(request, response);
        }
    }

    private void listMessages(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<ContactMessage> messages = contactMessageDAO.getAllContactMessages();
        Profile profile = profileDAO.getDefaultProfile();

        request.setAttribute("messages", messages);
        request.setAttribute("profile", profile);
        request.getRequestDispatcher("/admin/messages.jsp").forward(request, response);
    }

    private void viewMessage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            ContactMessage message = contactMessageDAO.getContactMessageById(id);
            Profile profile = profileDAO.getDefaultProfile();

            if (message != null) {
                if ("new".equalsIgnoreCase(message.getStatus())) {
                    contactMessageDAO.updateMessageStatus(id, "read");
                    message.setStatus("read");
                }
                request.setAttribute("message", message);
                request.setAttribute("profile", profile);
                listMessages(request,response);
            } else {
                request.setAttribute("errorMessage", "Message not found.");
                listMessages(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid message ID.");
            listMessages(request, response);
        }
    }

    private void updateStatus(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String status = request.getParameter("status");

            if (status == null || (!status.equals("new") && !status.equals("read") && !status.equals("replied") && !status.equals("archived"))) {
                request.setAttribute("errorMessage", "Invalid status value.");
                response.sendRedirect(request.getContextPath() + "/admin/messages?updated=false&error=invalid_status");
                return;
            }

            boolean success = contactMessageDAO.updateMessageStatus(id, status);
            response.sendRedirect(request.getContextPath() + "/admin/messages?updated=" + success);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/messages?updated=false&error=invalid_id");
        }
    }

    private void deleteMessage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            boolean success = contactMessageDAO.deleteContactMessage(id);
            response.sendRedirect(request.getContextPath() + "/admin/messages?deleted=" + success);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/messages?deleted=false&error=invalid_id");
        }
    }
}