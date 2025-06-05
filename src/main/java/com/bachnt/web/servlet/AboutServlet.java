package com.bachnt.web.servlet;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bachnt.dao.ProfileDAO;
import com.bachnt.dao.EducationDAO;  
import com.bachnt.dao.ExperienceDAO; 
import com.bachnt.dao.TestimonialDAO;
import com.bachnt.model.Profile;
import com.bachnt.model.Skill;
import com.bachnt.model.Education;   
import com.bachnt.model.Experience;  
import com.bachnt.model.Testimonial; 


@WebServlet("/about")
public class AboutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ProfileDAO profileDAO;
    private EducationDAO educationDAO;   
    private ExperienceDAO experienceDAO; 
    private TestimonialDAO testimonialDAO; 

    @Override
    public void init() throws ServletException {
        profileDAO = new ProfileDAO();
        educationDAO = new EducationDAO();   
        experienceDAO = new ExperienceDAO(); 
        testimonialDAO = new TestimonialDAO(); 
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Profile profile = profileDAO.getDefaultProfile();

        if (profile != null) {
            List<Skill> skills = profileDAO.getSkillsByProfileId(profile.getId());
            request.setAttribute("skillsList", skills);

            List<Education> educations = educationDAO.getEducationsByProfileId(profile.getId());
            request.setAttribute("educationsList", educations);

            List<Experience> experiences = experienceDAO.getExperiencesByProfileId(profile.getId());
            request.setAttribute("experiencesList", experiences);

            request.setAttribute("pageTitle", profile.getName() + " - Giới Thiệu");
        } else {
            request.setAttribute("pageTitle", "Giới Thiệu");
        }

        List<Testimonial> testimonials = testimonialDAO.getAllDisplayableTestimonials();
        request.setAttribute("testimonialsList", testimonials);

        request.setAttribute("profile", profile);
        request.setAttribute("activePage", "about");
        request.getRequestDispatcher("/about.jsp").forward(request, response);
    }
}