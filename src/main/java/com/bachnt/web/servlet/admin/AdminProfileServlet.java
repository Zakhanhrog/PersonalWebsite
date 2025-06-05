package com.bachnt.web.servlet.admin;

import com.bachnt.dao.ProfileDAO;
import com.bachnt.model.Profile;
import com.bachnt.model.Skill;
import com.bachnt.dao.EducationDAO;
import com.bachnt.dao.ExperienceDAO;
import com.bachnt.model.Education;
import com.bachnt.model.Experience;

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
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/admin/profile")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 1,  // 1 MB
        maxFileSize = 1024 * 1024 * 10, // 10 MB
        maxRequestSize = 1024 * 1024 * 15 // 15 MB
)
public class AdminProfileServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(AdminProfileServlet.class);
    private static final long serialVersionUID = 1L;
    private ProfileDAO profileDAO;
    private EducationDAO educationDAO;
    private ExperienceDAO experienceDAO;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final String UPLOAD_DIR_PROFILE_ABSOLUTE = "/Users/ngogiakhanh/Documents/PersonalWebsite/PersonalWebsiteUploads/profile";
    private static final String URL_PATH_FOR_DB_AND_WEB = "/my-uploaded-images/profile";

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
                String currentPhotoUrl = profile.getPhotoUrl();
                String newPhotoUrl = null;
                boolean photoUpdatedOrDeleted = false;

                if (filePart != null && filePart.getSize() > 0) {
                    String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                    if (fileName != null && !fileName.isEmpty()) {
                        String fileExtension = "";
                        int i = fileName.lastIndexOf('.');
                        if (i > 0) fileExtension = fileName.substring(i);
                        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

                        String applicationPath = getServletContext().getRealPath("");
                        String uploadFilePathAbsolute = UPLOAD_DIR_PROFILE_ABSOLUTE;

                        File uploadDir = new File(uploadFilePathAbsolute);
                        if (!uploadDir.exists()) uploadDir.mkdirs();

                        if (currentPhotoUrl != null && !currentPhotoUrl.isEmpty() && !currentPhotoUrl.contains("default")) {
                            File oldFile = new File(applicationPath + File.separator + currentPhotoUrl.replaceFirst("^/", "").replace("/", File.separator));
                            if (oldFile.exists()) oldFile.delete();
                        }

                        filePart.write(uploadFilePathAbsolute + File.separator + uniqueFileName);
                        newPhotoUrl = URL_PATH_FOR_DB_AND_WEB + "/" + uniqueFileName;
                        profile.setPhotoUrl(newPhotoUrl);
                        photoUpdatedOrDeleted = true;
                        overallMessage += "Ảnh đại diện đã được cập nhật. ";
                        logger.debug("Processing profile photo upload: {}", uniqueFileName);
                        logger.debug("Đường dẫn vật lý của file: {}", uploadFilePathAbsolute + File.separator + uniqueFileName);
                        logger.debug("Final photoUrl to be saved to DB: {}", newPhotoUrl);
                    }
                }

                String deletePhotoFlag = request.getParameter("deletePhoto");
                if ("true".equals(deletePhotoFlag) && newPhotoUrl == null) { // Chỉ xóa nếu không có ảnh mới được upload
                    if (currentPhotoUrl != null && !currentPhotoUrl.isEmpty() && !currentPhotoUrl.contains("default")) {
                        File oldFile = new File(getServletContext().getRealPath("") + File.separator + currentPhotoUrl.replaceFirst("^/", "").replace("/", File.separator));
                        if (oldFile.exists()) oldFile.delete();
                    }
                    profile.setPhotoUrl(null);
                    photoUpdatedOrDeleted = true;
                    overallMessage += "Ảnh đại diện đã được xóa. ";
                }

                if (!photoUpdatedOrDeleted && newPhotoUrl == null) {
                    profile.setPhotoUrl(currentPhotoUrl);
                }


                boolean success = profileDAO.updateProfile(profile);
                if (success) {
                    logger.info("Profile updated successfully for profile ID: 1");
                    overallMessage += "Thông tin hồ sơ đã được cập nhật thành công!";
                    session.setAttribute("profileUpdateMessage", overallMessage.trim());
                } else {
                    logger.warn("Failed to update profile for ID: 1");
                    session.setAttribute("profileUpdateError", "Lỗi: Không thể cập nhật hồ sơ." + (photoUpdatedOrDeleted ? " (Lỗi cập nhật thông tin text)" : ""));
                }
            } catch (Exception e) {
                logger.error("Error updating profile: {}", e.getMessage(), e);
                session.setAttribute("profileUpdateError", "Lỗi hệ thống khi cập nhật hồ sơ: " + e.getMessage());
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
                    session.setAttribute("profileUpdateMessage", "Kỹ năng đã được thêm thành công!");
                } else {
                    logger.warn("Failed to add skill: {}", skill.getName());
                    session.setAttribute("profileUpdateError", "Lỗi: Không thể thêm kỹ năng.");
                }
            } catch (NumberFormatException e){
                logger.warn("Invalid skill level format");
                session.setAttribute("profileUpdateError", "Lỗi: Level kỹ năng phải là số.");
            } catch (Exception e) {
                logger.error("Error adding skill: {}", e.getMessage(), e);
                session.setAttribute("profileUpdateError", "Lỗi hệ thống khi thêm kỹ năng: " + e.getMessage());
            }
        } else if ("deleteSkill".equals(action)) {
            try {
                int skillId = Integer.parseInt(request.getParameter("skillId"));
                boolean success = profileDAO.deleteSkill(skillId);
                if (success) session.setAttribute("profileUpdateMessage", "Kỹ năng đã được xóa thành công!");
                else session.setAttribute("profileUpdateError", "Lỗi: Không thể xóa kỹ năng.");
            } catch (NumberFormatException e){
                session.setAttribute("profileUpdateError", "Lỗi: ID kỹ năng không hợp lệ.");
            } catch (Exception e) {
                logger.error("Lỗi khi xóa kỹ năng: {}", e.getMessage(), e);
                session.setAttribute("profileUpdateError", "Lỗi hệ thống nghiêm trọng, vui lòng thử lại sau.");
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
                if (success) session.setAttribute("profileUpdateMessage", "Học vấn đã được thêm!");
                else session.setAttribute("profileUpdateError", "Lỗi: Không thể thêm học vấn.");
            } catch (Exception e) {
                logger.error("Lỗi khi thêm học vấn: {}", e.getMessage(), e);
                session.setAttribute("profileUpdateError", "Lỗi hệ thống nghiêm trọng, vui lòng thử lại sau.");
            }
        } else if ("deleteEducation".equals(action)) {
            try {
                int eduId = Integer.parseInt(request.getParameter("eduId"));
                boolean success = educationDAO.deleteEducation(eduId);
                if (success) session.setAttribute("profileUpdateMessage", "Học vấn đã được xóa!");
                else session.setAttribute("profileUpdateError", "Lỗi: Không thể xóa học vấn.");
            } catch (NumberFormatException e) {
                session.setAttribute("profileUpdateError", "Lỗi: ID học vấn không hợp lệ.");
            } catch (Exception e) {
                logger.error("Lỗi khi xóa học vấn: {}", e.getMessage(), e);
                session.setAttribute("profileUpdateError", "Lỗi hệ thống nghiêm trọng, vui lòng thử lại sau.");
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
                if (success) session.setAttribute("profileUpdateMessage", "Kinh nghiệm đã được thêm!");
                else session.setAttribute("profileUpdateError", "Lỗi: Không thể thêm kinh nghiệm.");
            } catch (ParseException pe) {
                session.setAttribute("profileUpdateError", "Lỗi định dạng ngày tháng (yyyy-MM-dd) cho kinh nghiệm.");
            } catch (Exception e) {
                logger.error("Lỗi khi thêm kinh nghiệm: {}", e.getMessage(), e);
                session.setAttribute("profileUpdateError", "Lỗi hệ thống nghiêm trọng, vui lòng thử lại sau.");
            }
        } else if ("deleteExperience".equals(action)) {
            try {
                int expId = Integer.parseInt(request.getParameter("expId"));
                boolean success = experienceDAO.deleteExperience(expId);
                if (success) session.setAttribute("profileUpdateMessage", "Kinh nghiệm đã được xóa!");
                else session.setAttribute("profileUpdateError", "Lỗi: Không thể xóa kinh nghiệm.");
            } catch (NumberFormatException e) {
                session.setAttribute("profileUpdateError", "Lỗi: ID kinh nghiệm không hợp lệ.");
            } catch (Exception e) {
                logger.error("Lỗi khi xóa kinh nghiệm: {}", e.getMessage(), e);
                session.setAttribute("profileUpdateError", "Lỗi hệ thống nghiêm trọng, vui lòng thử lại sau.");
            }
        }
        
        logger.debug("Redirecting to profile management page");
        response.sendRedirect(request.getContextPath() + "/admin/profile");
    }
}