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

@WebServlet("/blog")
public class BlogServlet extends HttpServlet {
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
        // You can add pagination parameters here, e.g., int page = request.getParameter("page") != null ? Integer.parseInt(request.getParameter("page")) : 1;

        List<BlogPost> blogPosts;
        if (categoryParam != null && !categoryParam.isEmpty()) {
            // Assuming you might add a method like getBlogPostsByCategory
            // blogPosts = blogPostDAO.getPublishedBlogPostsByCategory(categoryParam);
            // For now, filter from all published posts
            blogPosts = blogPostDAO.getAllPublishedBlogPosts().stream()
                    .filter(p -> categoryParam.equalsIgnoreCase(p.getCategory()))
                    .collect(Collectors.toList());
            request.setAttribute("currentCategory", categoryParam);
        } else if (tagParam != null && !tagParam.isEmpty()) {
            // Assuming you might add a method like getBlogPostsByTag
            // blogPosts = blogPostDAO.getPublishedBlogPostsByTag(tagParam);
            blogPosts = blogPostDAO.getAllPublishedBlogPosts().stream()
                    .filter(p -> p.getTags() != null && p.getTags().toLowerCase().contains(tagParam.toLowerCase()))
                    .collect(Collectors.toList());
            request.setAttribute("currentTag", tagParam);
        } else {
            blogPosts = blogPostDAO.getAllPublishedBlogPosts();
        }

        request.setAttribute("blogPosts", blogPosts);

        // For sidebar: Categories and Tags
        // This is a simplified way; ideally, you'd have dedicated tables or more efficient queries
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
                .limit(5) // Show recent 5 posts
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