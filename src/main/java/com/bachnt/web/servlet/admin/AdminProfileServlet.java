package com.bachnt.web.servlet.admin;

import com.bachnt.dao.ProfileDAO;
import com.bachnt.model.Profile;
import com.bachnt.model.Skill;
import com.bachnt.dao.EducationDAO;
import com.bachnt.dao.ExperienceDAO;
import com.bachnt.model.Education;
import com.bachnt.model.Experience;
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
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/admin/profile")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 1,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 15
)
public class AdminProfileServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(AdminProfileServlet.class);
    private static final long serialVersionUID = 1L;
    private ProfileDAO profileDAO;
    private EducationDAO educationDAO;
    private ExperienceDAO experienceDAO;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final String PROFILE_SUBFOLDER = "profile";

    @Override
    public void init() throws ServletException {
        logger.info("Initializing AdminProfileServlet");
        profileDAO = new ProfileDAO();
        educationDAO = new EducationDAO();
        experienceDAO = new ExperienceDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        logger.debug("Processing GET request for profile management");

        Profile profile = profileDAO.getProfileById(1);
        if (profile == null) {
            profile = new Profile();
            profile.setId(1);
            logger.warn("No profile found for ID 1, creating a new empty profile object for the form.");
        }
        List<Skill> skills = profileDAO.getSkillsByProfileId(1);
        List<Education> educations = educationDAO.getEducationsByProfileId(1);
        List<Experience> experiences = experienceDAO.getExperiencesByProfileId(1);

        request.setAttribute("profile", profile);
        request.setAttribute("skillsList", skills);
        request.setAttribute("educationsList", educations);
        request.setAttribute("experiencesList", experiences);

        HttpSession session = request.getSession();
        if (session.getAttribute("profileUpdateMessage") != null) {
            request.setAttribute("message", session.getAttribute("profileUpdateMessage"));
            session.removeAttribute("profileUpdateMessage");
        }
        if (session.getAttribute("profileUpdateError") != null) {
            request.setAttribute("error", session.getAttribute("profileUpdateError"));
            session.removeAttribute("profileUpdateError");
        }

        request.getRequestDispatcher("/admin/profile-form.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        String action = request.getParameter("action");

        logger.debug("Processing POST request for profile management, action: {}", action);
        String overallMessage = "";

        if ("updateProfile".equals(action)) {
            try {
                Profile profile = profileDAO.getProfileById(1);
                if (profile == null) {
                    profile = new Profile();
                    profile.setId(1);
                    logger.info("No existing profile with ID 1 found. A new profile will be created/updated.");
                }
                profile.setName(request.getParameter("name"));
                profile.setPosition(request.getParameter("position"));
                profile.setCompanyName(request.getParameter("companyName"));
                profile.setCompanyTaxId(request.getParameter("companyTaxId"));
                profile.setCompanyAddress(request.getParameter("companyAddress"));
                profile.setPhoneNumber(request.getParameter("phoneNumber"));
                profile.setEmail(request.getParameter("email"));
                profile.setBio(request.getParameter("bio"));

                Part filePart = request.getPart("photoFile");
                String currentRelativePhotoUrl = profile.getPhotoUrl();
                String newRelativePhotoUrlFromUpload = null;
                boolean photoActionTaken = false;

                if (filePart != null && filePart.getSize() > 0 && filePart.getSubmittedFileName() != null && !filePart.getSubmittedFileName().isEmpty()) {
                    try {
                        newRelativePhotoUrlFromUpload = FileUploadUtils.saveUploadedFile(filePart, PROFILE_SUBFOLDER);
                        if (newRelativePhotoUrlFromUpload != null) {
                            overallMessage += "Profile photo uploaded. ";
                            if (currentRelativePhotoUrl != null && !currentRelativePhotoUrl.isEmpty() && !currentRelativePhotoUrl.contains("default")) {
                                FileUploadUtils.deleteUploadedFile(currentRelativePhotoUrl);
                            }
                            profile.setPhotoUrl(newRelativePhotoUrlFromUpload);
                            photoActionTaken = true;
                        } else {
                            logger.warn("FileUploadUtils.saveUploadedFile returned null for profile photo.");
                            session.setAttribute("profileUpdateError", (session.getAttribute("profileUpdateError") != null ? session.getAttribute("profileUpdateError") + " " : "") + "Failed to save uploaded photo.");
                        }
                    } catch (IOException e) {
                        logger.error("Error saving uploaded profile photo: {}", e.getMessage(), e);
                        session.setAttribute("profileUpdateError", (session.getAttribute("profileUpdateError") != null ? session.getAttribute("profileUpdateError") + " " : "") + "Error uploading photo: " + e.getMessage());
                    }
                }

                String deletePhotoFlag = request.getParameter("deletePhoto");
                if ("true".equals(deletePhotoFlag) && newRelativePhotoUrlFromUpload == null) {
                    if (currentRelativePhotoUrl != null && !currentRelativePhotoUrl.isEmpty() && !currentRelativePhotoUrl.contains("default")) {
                        FileUploadUtils.deleteUploadedFile(currentRelativePhotoUrl);
                    }
                    profile.setPhotoUrl(null);
                    overallMessage += "Profile photo removed. ";
                    photoActionTaken = true;
                }

                if (!photoActionTaken && newRelativePhotoUrlFromUpload == null) {
                    profile.setPhotoUrl(currentRelativePhotoUrl);
                }


                boolean success = profileDAO.updateProfile(profile);
                if (success) {
                    logger.info("Profile updated successfully for profile ID: 1. Photo URL: {}", profile.getPhotoUrl());
                    session.setAttribute("profileUpdateMessage", overallMessage.trim() + (overallMessage.isEmpty() && !photoActionTaken ? "" : " ") + "Profile information updated successfully!");
                } else {
                    logger.warn("Failed to update profile for ID: 1");
                    session.setAttribute("profileUpdateError", (session.getAttribute("profileUpdateError") != null ? session.getAttribute("profileUpdateError") + " " : "") + "Error: Could not update profile information.");
                }
            } catch (Exception e) {
                logger.error("Error updating profile: {}", e.getMessage(), e);
                session.setAttribute("profileUpdateError", "System error while updating profile: " + e.getMessage());
            }
        } else if ("addSkill".equals(action)) {
            try {
                Skill skill = new Skill();
                skill.setProfileId(1);
                skill.setName(request.getParameter("skillName"));
                skill.setLevel(Integer.parseInt(request.getParameter("skillLevel")));
                skill.setCategory(request.getParameter("skillCategory"));
                boolean success = profileDAO.addSkill(skill);
                if (success) {
                    logger.info("Skill added successfully: {}", skill.getName());
                    session.setAttribute("profileUpdateMessage", "Skill added successfully!");
                } else {
                    logger.warn("Failed to add skill: {}", skill.getName());
                    session.setAttribute("profileUpdateError", "Error: Could not add skill.");
                }
            } catch (NumberFormatException e){
                logger.warn("Invalid skill level format");
                session.setAttribute("profileUpdateError", "Error: Skill level must be a number.");
            } catch (Exception e) {
                logger.error("Error adding skill: {}", e.getMessage(), e);
                session.setAttribute("profileUpdateError", "System error while adding skill: " + e.getMessage());
            }
        } else if ("deleteSkill".equals(action)) {
            try {
                int skillId = Integer.parseInt(request.getParameter("skillId"));
                boolean success = profileDAO.deleteSkill(skillId);
                if (success) session.setAttribute("profileUpdateMessage", "Skill deleted successfully!");
                else session.setAttribute("profileUpdateError", "Error: Could not delete skill.");
            } catch (NumberFormatException e){
                session.setAttribute("profileUpdateError", "Error: Invalid skill ID.");
            } catch (Exception e) {
                logger.error("Error deleting skill: {}", e.getMessage(), e);
                session.setAttribute("profileUpdateError", "System error while deleting skill.");
            }
        } else if ("addEducation".equals(action)) {
            try {
                Education edu = new Education();
                edu.setProfileId(1);
                edu.setSchoolName(request.getParameter("eduSchoolName"));
                edu.setDegree(request.getParameter("eduDegree"));
                edu.setFieldOfStudy(request.getParameter("eduFieldOfStudy"));
                edu.setStartYear(request.getParameter("eduStartYear"));
                edu.setEndYear(request.getParameter("eduEndYear"));
                edu.setDescription(request.getParameter("eduDescription"));
                boolean success = educationDAO.addEducation(edu);
                if (success) session.setAttribute("profileUpdateMessage", "Education added successfully!");
                else session.setAttribute("profileUpdateError", "Error: Could not add education.");
            } catch (Exception e) {
                logger.error("Error adding education: {}", e.getMessage(), e);
                session.setAttribute("profileUpdateError", "System error while adding education.");
            }
        } else if ("deleteEducation".equals(action)) {
            try {
                int eduId = Integer.parseInt(request.getParameter("eduId"));
                boolean success = educationDAO.deleteEducation(eduId);
                if (success) session.setAttribute("profileUpdateMessage", "Education deleted successfully!");
                else session.setAttribute("profileUpdateError", "Error: Could not delete education.");
            } catch (NumberFormatException e) {
                session.setAttribute("profileUpdateError", "Error: Invalid education ID.");
            } catch (Exception e) {
                logger.error("Error deleting education: {}", e.getMessage(), e);
                session.setAttribute("profileUpdateError", "System error while deleting education.");
            }
        } else if ("addExperience".equals(action)) {
            try {
                Experience exp = new Experience();
                exp.setProfileId(1);
                exp.setCompanyName(request.getParameter("expCompanyName"));
                exp.setPosition(request.getParameter("expPosition"));
                exp.setDescriptionResponsibilities(request.getParameter("expDescription"));
                String startDateStr = request.getParameter("expStartDate");
                if (startDateStr != null && !startDateStr.isEmpty()) exp.setStartDate(dateFormat.parse(startDateStr));
                String endDateStr = request.getParameter("expEndDate");
                if (endDateStr != null && !endDateStr.isEmpty()) exp.setEndDate(dateFormat.parse(endDateStr));
                else exp.setEndDate(null);
                boolean success = experienceDAO.addExperience(exp);
                if (success) session.setAttribute("profileUpdateMessage", "Experience added successfully!");
                else session.setAttribute("profileUpdateError", "Error: Could not add experience.");
            } catch (ParseException pe) {
                session.setAttribute("profileUpdateError", "Date format error (yyyy-MM-dd) for experience.");
            } catch (Exception e) {
                logger.error("Error adding experience: {}", e.getMessage(), e);
                session.setAttribute("profileUpdateError", "System error while adding experience.");
            }
        } else if ("deleteExperience".equals(action)) {
            try {
                int expId = Integer.parseInt(request.getParameter("expId"));
                boolean success = experienceDAO.deleteExperience(expId);
                if (success) session.setAttribute("profileUpdateMessage", "Experience deleted successfully!");
                else session.setAttribute("profileUpdateError", "Error: Could not delete experience.");
            } catch (NumberFormatException e) {
                session.setAttribute("profileUpdateError", "Error: Invalid experience ID.");
            } catch (Exception e) {
                logger.error("Error deleting experience: {}", e.getMessage(), e);
                session.setAttribute("profileUpdateError", "System error while deleting experience.");
            }
        } else {
            logger.warn("Unknown or unhandled action in doPost: {}", action);
            session.setAttribute("profileUpdateError", "Unknown action performed.");
        }

        logger.debug("Redirecting to profile management page after POST action: {}", action);
        response.sendRedirect(request.getContextPath() + "/admin/profile");
    }
}