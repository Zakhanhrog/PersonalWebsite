package com.bachnt.web.servlet;

import java.io.IOException;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bachnt.dao.ContactMessageDAO;
import com.bachnt.dao.ProfileDAO;
import com.bachnt.model.ContactMessage;
import com.bachnt.model.Profile;

@WebServlet("/contact")
public class ContactServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ProfileDAO profileDAO;
    private ContactMessageDAO contactMessageDAO;

    @Override
    public void init() throws ServletException {
        profileDAO = new ProfileDAO();
        contactMessageDAO = new ContactMessageDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Profile profile = profileDAO.getDefaultProfile();
        request.setAttribute("profile", profile);

        HttpSession session = request.getSession();
        if (session.getAttribute("contactFormSuccessMessage") != null) {
            request.setAttribute("successMessage", session.getAttribute("contactFormSuccessMessage"));
            session.removeAttribute("contactFormSuccessMessage");
        }
        if (session.getAttribute("contactFormErrorMessage") != null) {
            request.setAttribute("errorMessage", session.getAttribute("contactFormErrorMessage"));
            session.removeAttribute("contactFormErrorMessage");
        }
        if (session.getAttribute("formName") != null) {
            request.setAttribute("formName", session.getAttribute("formName"));
            session.removeAttribute("formName");
        }
        if (session.getAttribute("formEmail") != null) {
            request.setAttribute("formEmail", session.getAttribute("formEmail"));
            session.removeAttribute("formEmail");
        }
        if (session.getAttribute("formPhone") != null) { // Added for phone
            request.setAttribute("formPhone", session.getAttribute("formPhone"));
            session.removeAttribute("formPhone");
        }
        if (session.getAttribute("formSubject") != null) {
            request.setAttribute("formSubject", session.getAttribute("formSubject"));
            session.removeAttribute("formSubject");
        }
        if (session.getAttribute("formMessage") != null) {
            request.setAttribute("formMessage", session.getAttribute("formMessage"));
            session.removeAttribute("formMessage");
        }

        request.setAttribute("pageTitle", "Liên Hệ");
        if (profile != null) {
            request.setAttribute("pageTitle", profile.getName() + " - Liên Hệ");
        }
        request.setAttribute("activePage", "contact");
        request.getRequestDispatcher("/contact.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone"); // Added phone
        String subject = request.getParameter("subject");
        String messageText = request.getParameter("message");

        HttpSession session = request.getSession();

        if (name == null || name.trim().isEmpty() ||
                email == null || email.trim().isEmpty() || !isValidEmail(email.trim()) ||
                subject == null || subject.trim().isEmpty() ||
                messageText == null || messageText.trim().isEmpty()) {

            session.setAttribute("contactFormErrorMessage", "Vui lòng điền đầy đủ thông tin bắt buộc (Tên, Email hợp lệ, Chủ đề, Tin nhắn).");
            session.setAttribute("formName", name);
            session.setAttribute("formEmail", email);
            session.setAttribute("formPhone", phone);
            session.setAttribute("formSubject", subject);
            session.setAttribute("formMessage", messageText);
            response.sendRedirect(request.getContextPath() + "/contact");
            return;
        }

        ContactMessage contactMessage = new ContactMessage();
        contactMessage.setName(name.trim());
        contactMessage.setEmail(email.trim());
        // contactMessage.setPhone(phone != null ? phone.trim() : ""); // Add phone to ContactMessage model if needed
        contactMessage.setSubject(subject.trim());
        contactMessage.setMessage(messageText.trim());
        contactMessage.setCreatedDate(new Date());
        contactMessage.setStatus("new");

        boolean success = contactMessageDAO.saveContactMessage(contactMessage);

        if (success) {
            session.setAttribute("contactFormSuccessMessage", "Cảm ơn bạn đã liên hệ! Chúng tôi sẽ phản hồi sớm nhất có thể.");
        } else {
            session.setAttribute("contactFormErrorMessage", "Có lỗi xảy ra khi gửi tin nhắn. Vui lòng thử lại sau.");
            session.setAttribute("formName", name);
            session.setAttribute("formEmail", email);
            session.setAttribute("formPhone", phone);
            session.setAttribute("formSubject", subject);
            session.setAttribute("formMessage", messageText);
        }
        response.sendRedirect(request.getContextPath() + "/contact");
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email != null && email.matches(emailRegex);
    }
}