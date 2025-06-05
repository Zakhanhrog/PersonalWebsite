package com.bachnt.web.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.bachnt.dao.BlogPostDAO;
import com.bachnt.dao.ProfileDAO;
import com.bachnt.model.BlogPost;
import com.bachnt.model.Profile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/blog")
public class BlogServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(BlogServlet.class);
    private static final long serialVersionUID = 1L;
    private BlogPostDAO blogPostDAO;
    private ProfileDAO profileDAO;

    @Override
    public void init() throws ServletException {
        blogPostDAO = new BlogPostDAO();
        profileDAO = new ProfileDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Profile profile = profileDAO.getDefaultProfile();
        request.setAttribute("profile", profile);

        String categoryParam = request.getParameter("category");
        String tagParam = request.getParameter("tag");
        List<BlogPost> blogPosts;
        if (categoryParam != null && !categoryParam.isEmpty()) {
            blogPosts = blogPostDAO.getAllPublishedBlogPosts().stream()
                    .filter(p -> categoryParam.equalsIgnoreCase(p.getCategory()))
                    .collect(Collectors.toList());
            request.setAttribute("currentCategory", categoryParam);
        } else if (tagParam != null && !tagParam.isEmpty()) {
            blogPosts = blogPostDAO.getAllPublishedBlogPosts().stream()
                    .filter(p -> p.getTags() != null && p.getTags().toLowerCase().contains(tagParam.toLowerCase()))
                    .collect(Collectors.toList());
            request.setAttribute("currentTag", tagParam);
        } else {
            blogPosts = blogPostDAO.getAllPublishedBlogPosts();
        }

        request.setAttribute("blogPosts", blogPosts);
        List<BlogPost> allPublishedPostsForSidebar = blogPostDAO.getAllPublishedBlogPosts();

        Map<String, Long> categoriesCount = allPublishedPostsForSidebar.stream()
                .filter(p -> p.getCategory() != null && !p.getCategory().trim().isEmpty())
                .collect(Collectors.groupingBy(BlogPost::getCategory, Collectors.counting()));
        request.setAttribute("categoriesCount", categoriesCount);

        List<String> allTags = allPublishedPostsForSidebar.stream()
                .filter(p -> p.getTags() != null && !p.getTags().trim().isEmpty())
                .flatMap(p -> java.util.Arrays.stream(p.getTags().split(",")))
                .map(String::trim)
                .distinct()
                .collect(Collectors.toList());
        request.setAttribute("allTags", allTags);

        List<BlogPost> recentPosts = allPublishedPostsForSidebar.stream()
                .limit(5)
                .collect(Collectors.toList());
        request.setAttribute("recentPosts", recentPosts);

        request.setAttribute("pageTitle", "Blog");
        if (profile != null) {
            request.setAttribute("pageTitle", profile.getName() + " - Blog");
        }
        request.setAttribute("activePage", "blog");
        request.getRequestDispatcher("/blog.jsp").forward(request, response);
    }
}