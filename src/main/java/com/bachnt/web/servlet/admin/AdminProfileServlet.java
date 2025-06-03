package com.bachnt.web.servlet.admin;

import com.bachnt.dao.ProfileDAO;
import com.bachnt.model.Profile;
import com.bachnt.model.Skill;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/profile")
public class AdminProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ProfileDAO profileDAO;

    @Override
    public void init() throws ServletException {
        profileDAO = new ProfileDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Auth filter đã xử lý việc đăng nhập
        // String action = request.getParameter("action"); // Có thể dùng action cho skill sau này

        Profile profile = profileDAO.getProfileById(1); // Luôn lấy profile ID 1
        List<Skill> skills = profileDAO.getSkillsByProfileId(1);

        request.setAttribute("profile", profile);
        request.setAttribute("skillsList", skills);

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

        if ("updateProfile".equals(action)) {
            try {
                Profile profile = profileDAO.getProfileById(1); // Lấy profile hiện tại để cập nhật
                if (profile == null) { // Trường hợp hi hữu profile chưa có trong DB
                    profile = new Profile();
                    profile.setId(1); // Mặc định ID là 1
                    // Có thể cần logic để INSERT nếu chưa có, nhưng thường profile sẽ được tạo sẵn
                }

                profile.setName(request.getParameter("name"));
                profile.setPosition(request.getParameter("position"));
                profile.setCompanyName(request.getParameter("companyName"));
                profile.setCompanyTaxId(request.getParameter("companyTaxId"));
                profile.setCompanyAddress(request.getParameter("companyAddress"));
                profile.setPhoneNumber(request.getParameter("phoneNumber"));
                profile.setEmail(request.getParameter("email"));
                profile.setBio(request.getParameter("bio"));
                profile.setPhotoUrl(request.getParameter("photoUrl")); // Cân nhắc việc upload file ảnh sau

                boolean success = profileDAO.updateProfile(profile);
                if (success) {
                    session.setAttribute("profileUpdateMessage", "Thông tin hồ sơ đã được cập nhật thành công!");
                } else {
                    session.setAttribute("profileUpdateError", "Lỗi: Không thể cập nhật hồ sơ.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                session.setAttribute("profileUpdateError", "Lỗi hệ thống: " + e.getMessage());
            }
        } else if ("addSkill".equals(action)) {
            try {
                Skill skill = new Skill();
                skill.setProfileId(1); // Luôn gắn với profile ID 1
                skill.setName(request.getParameter("skillName"));
                skill.setLevel(Integer.parseInt(request.getParameter("skillLevel")));
                skill.setCategory(request.getParameter("skillCategory"));

                boolean success = profileDAO.addSkill(skill);
                if (success) {
                    session.setAttribute("profileUpdateMessage", "Kỹ năng đã được thêm thành công!");
                } else {
                    session.setAttribute("profileUpdateError", "Lỗi: Không thể thêm kỹ năng.");
                }
            } catch (NumberFormatException e){
                session.setAttribute("profileUpdateError", "Lỗi: Level kỹ năng phải là số.");
            }
            catch (Exception e) {
                e.printStackTrace();
                session.setAttribute("profileUpdateError", "Lỗi hệ thống khi thêm kỹ năng: " + e.getMessage());
            }
        } else if ("deleteSkill".equals(action)) {
            try {
                int skillId = Integer.parseInt(request.getParameter("skillId"));
                boolean success = profileDAO.deleteSkill(skillId);
                if (success) {
                    session.setAttribute("profileUpdateMessage", "Kỹ năng đã được xóa thành công!");
                } else {
                    session.setAttribute("profileUpdateError", "Lỗi: Không thể xóa kỹ năng.");
                }
            } catch (NumberFormatException e){
                session.setAttribute("profileUpdateError", "Lỗi: ID kỹ năng không hợp lệ.");
            }
            catch (Exception e) {
                e.printStackTrace();
                session.setAttribute("profileUpdateError", "Lỗi hệ thống khi xóa kỹ năng: " + e.getMessage());
            }
        }
        response.sendRedirect(request.getContextPath() + "/admin/profile");
    }
}