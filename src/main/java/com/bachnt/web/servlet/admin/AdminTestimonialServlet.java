package com.bachnt.web.servlet.admin;

import com.bachnt.dao.TestimonialDAO;
import com.bachnt.model.Testimonial;
import com.bachnt.dao.ProfileDAO;
import com.bachnt.model.Profile;
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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/admin/testimonials")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 1,
        maxFileSize = 1024 * 1024 * 5,
        maxRequestSize = 1024 * 1024 * 10
)
public class AdminTestimonialServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(AdminTestimonialServlet.class);
    private static final long serialVersionUID = 1L;
    private TestimonialDAO testimonialDAO;
    private ProfileDAO profileDAO;
    private static final String TESTIMONIALS_SUBFOLDER = "testimonials";

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
                    deleteTestimonialAction(request, response, true);
                    break;
                case "list":
                default:
                    listTestimonials(request, response);
                    break;
            }
        } catch (Exception e) {
            logger.error("Error processing GET request for action {}: {}", action, e.getMessage(), e);
            session.setAttribute("testimonialMessageError", "A system error occurred. Please try again later.");
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
                    session.setAttribute("testimonialMessageError", "Invalid POST action.");
                    response.sendRedirect(request.getContextPath() + "/admin/testimonials");
                    break;
            }
        } catch (Exception e) {
            logger.error("Error processing POST request for action {}: {}", action, e.getMessage(), e);
            session.setAttribute("testimonialMessageError", "A critical system error occurred. Please try again later.");
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
                session.setAttribute("testimonialMessageError", "Testimonial not found for editing (ID: " + id + ").");
                response.sendRedirect(request.getContextPath() + "/admin/testimonials");
            }
        } catch (NumberFormatException e) {
            logger.warn("Invalid testimonial ID format for editing: {}", request.getParameter("id"));
            session.setAttribute("testimonialMessageError", "Invalid testimonial ID.");
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
            try {
                testimonial.setId(Integer.parseInt(idParam));
                Testimonial existing = testimonialDAO.getTestimonialById(testimonial.getId());
                if(existing != null) {
                    testimonial.setClientImageUrl(existing.getClientImageUrl());
                } else {
                    isNew = true; // Not found, treat as new
                }
            } catch (NumberFormatException e) {
                logger.warn("Invalid testimonial ID format for saving existing: {}", idParam);
                isNew = true;
            }
        }

        testimonial.setClientName(request.getParameter("clientName"));
        testimonial.setClientPositionCompany(request.getParameter("clientPositionCompany"));
        testimonial.setQuoteText(request.getParameter("quoteText"));
        try {
            testimonial.setDisplayOrder(Integer.parseInt(request.getParameter("displayOrder")));
        } catch (NumberFormatException e) {
            testimonial.setDisplayOrder(0);
            logger.warn("Invalid display order format, defaulting to 0 for testimonial: {}", testimonial.getClientName());
        }


        Part filePart = request.getPart("clientImageFile");
        String currentRelativeImageUrl = testimonial.getClientImageUrl();
        String newRelativeImageUrlFromUpload = null;
        boolean imageActionTaken = false;

        if (filePart != null && filePart.getSize() > 0 && filePart.getSubmittedFileName() != null && !filePart.getSubmittedFileName().isEmpty()) {
            try {
                newRelativeImageUrlFromUpload = FileUploadUtils.saveUploadedFile(filePart, TESTIMONIALS_SUBFOLDER);
                if (newRelativeImageUrlFromUpload != null) {
                    overallMessage += "Client image uploaded. ";
                    if (currentRelativeImageUrl != null && !currentRelativeImageUrl.isEmpty() &&
                            !currentRelativeImageUrl.contains("default") && !currentRelativeImageUrl.startsWith("https://ui-avatars.com")) {
                        FileUploadUtils.deleteUploadedFile(currentRelativeImageUrl);
                    }
                    testimonial.setClientImageUrl(newRelativeImageUrlFromUpload);
                    imageActionTaken = true;
                } else {
                    session.setAttribute("testimonialMessageError", (session.getAttribute("testimonialMessageError") != null ? session.getAttribute("testimonialMessageError") + " " : "") + "Failed to save uploaded client image.");
                }
            } catch (IOException e) {
                logger.error("Error saving uploaded client image: {}", e.getMessage(), e);
                session.setAttribute("testimonialMessageError", (session.getAttribute("testimonialMessageError") != null ? session.getAttribute("testimonialMessageError") + " " : "") + "Error uploading client image: " + e.getMessage());
            }
        }

        String deleteImageFlag = request.getParameter("deleteImage");
        if ("true".equals(deleteImageFlag) && newRelativeImageUrlFromUpload == null) {
            if (currentRelativeImageUrl != null && !currentRelativeImageUrl.isEmpty() &&
                    !currentRelativeImageUrl.contains("default") && !currentRelativeImageUrl.startsWith("https://ui-avatars.com")) {
                FileUploadUtils.deleteUploadedFile(currentRelativeImageUrl);
            }
            testimonial.setClientImageUrl(null);
            imageActionTaken = true;
            overallMessage += "Client image removed. ";
        }

        if (!imageActionTaken && newRelativeImageUrlFromUpload == null) {
            testimonial.setClientImageUrl(currentRelativeImageUrl);
        }

        boolean success;
        if (isNew) {
            success = testimonialDAO.addTestimonial(testimonial);
            if (success) session.setAttribute("testimonialMessageSuccess", overallMessage.trim() + (overallMessage.isEmpty() && !imageActionTaken ? "" : " ") + "Testimonial added successfully!");
            else session.setAttribute("testimonialMessageError", (session.getAttribute("testimonialMessageError") != null ? session.getAttribute("testimonialMessageError") + " " : "") + "Error: Could not add testimonial.");
        } else {
            success = testimonialDAO.updateTestimonial(testimonial);
            if (success) session.setAttribute("testimonialMessageSuccess", overallMessage.trim() + (overallMessage.isEmpty() && !imageActionTaken ? "" : " ") + "Testimonial updated successfully!");
            else session.setAttribute("testimonialMessageError", (session.getAttribute("testimonialMessageError") != null ? session.getAttribute("testimonialMessageError") + " " : "") + "Error: Could not update testimonial.");
        }

        if (!success && overallMessage.isEmpty() && !imageActionTaken) {
            session.setAttribute("testimonialMessageError", (session.getAttribute("testimonialMessageError") != null ? session.getAttribute("testimonialMessageError") + " " : "") + "Error: Could not save testimonial information.");
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
                session.setAttribute("testimonialMessageSuccess", "Testimonial ID " + id + " deleted successfully!");
                if (testimonialToDelete != null && testimonialToDelete.getClientImageUrl() != null &&
                        !testimonialToDelete.getClientImageUrl().isEmpty() &&
                        !testimonialToDelete.getClientImageUrl().contains("default") &&
                        !testimonialToDelete.getClientImageUrl().startsWith("https://ui-avatars.com")) {
                    FileUploadUtils.deleteUploadedFile(testimonialToDelete.getClientImageUrl());
                }
            } else {
                session.setAttribute("testimonialMessageError", "Error: Could not delete testimonial ID " + id + ".");
            }
        } catch (NumberFormatException e) {
            logger.warn("Invalid testimonial ID format for deletion: {}", request.getParameter("id"));
            session.setAttribute("testimonialMessageError", "Invalid testimonial ID for deletion.");
        } catch (Exception e) {
            logger.error("Error deleting testimonial: {}", e.getMessage(), e);
            session.setAttribute("testimonialMessageError", "A system error occurred while deleting the testimonial.");
        }

        if(redirectToList){
            response.sendRedirect(request.getContextPath() + "/admin/testimonials");
        } else {
            listTestimonials(request, response);
        }
    }
}