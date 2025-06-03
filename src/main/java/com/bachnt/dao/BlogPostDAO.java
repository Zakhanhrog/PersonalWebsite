package com.bachnt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.bachnt.model.BlogPost;

public class BlogPostDAO {

    public BlogPost getBlogPostById(int id) {
        BlogPost blogPost = null;
        String sql = "SELECT * FROM blog_posts WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    blogPost = extractBlogPostFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return blogPost;
    }

    public List<BlogPost> getAllPublishedBlogPosts() {
        List<BlogPost> blogPosts = new ArrayList<>();
        String sql = "SELECT * FROM blog_posts WHERE status = 'published' ORDER BY created_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                BlogPost blogPost = extractBlogPostFromResultSet(rs);
                blogPosts.add(blogPost);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return blogPosts;
    }

    public List<BlogPost> getAllBlogPostsForAdmin() {
        List<BlogPost> blogPosts = new ArrayList<>();
        String sql = "SELECT * FROM blog_posts ORDER BY created_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                BlogPost blogPost = extractBlogPostFromResultSet(rs);
                blogPosts.add(blogPost);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return blogPosts;
    }

    public boolean addBlogPost(BlogPost blogPost) {
        String sql = "INSERT INTO blog_posts (title, content, summary, author, created_date, modified_date, image_url, category, tags, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        boolean rowInserted = false;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, blogPost.getTitle());
            stmt.setString(2, blogPost.getContent());
            stmt.setString(3, blogPost.getSummary());
            stmt.setString(4, blogPost.getAuthor());
            stmt.setTimestamp(5, new java.sql.Timestamp(blogPost.getCreatedDate().getTime()));
            stmt.setTimestamp(6, new java.sql.Timestamp(blogPost.getModifiedDate().getTime()));
            stmt.setString(7, blogPost.getImageUrl());
            stmt.setString(8, blogPost.getCategory());
            stmt.setString(9, blogPost.getTags());
            stmt.setString(10, blogPost.getStatus() != null ? blogPost.getStatus() : "draft"); // Default status

            rowInserted = stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowInserted;
    }

    public boolean updateBlogPost(BlogPost blogPost) {
        String sql = "UPDATE blog_posts SET title = ?, content = ?, summary = ?, author = ?, modified_date = ?, image_url = ?, category = ?, tags = ?, status = ? WHERE id = ?";
        boolean rowUpdated = false;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, blogPost.getTitle());
            stmt.setString(2, blogPost.getContent());
            stmt.setString(3, blogPost.getSummary());
            stmt.setString(4, blogPost.getAuthor());
            stmt.setTimestamp(5, new java.sql.Timestamp(new java.util.Date().getTime())); // Always update modified_date to current time
            stmt.setString(6, blogPost.getImageUrl());
            stmt.setString(7, blogPost.getCategory());
            stmt.setString(8, blogPost.getTags());
            stmt.setString(9, blogPost.getStatus());
            stmt.setInt(10, blogPost.getId());

            rowUpdated = stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowUpdated;
    }

    public boolean deleteBlogPost(int id) {
        String sql = "DELETE FROM blog_posts WHERE id = ?";
        boolean rowDeleted = false;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            rowDeleted = stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowDeleted;
    }

    private BlogPost extractBlogPostFromResultSet(ResultSet rs) throws SQLException {
        BlogPost blogPost = new BlogPost();
        blogPost.setId(rs.getInt("id"));
        blogPost.setTitle(rs.getString("title"));
        blogPost.setContent(rs.getString("content"));
        blogPost.setSummary(rs.getString("summary"));
        blogPost.setAuthor(rs.getString("author"));
        blogPost.setCreatedDate(rs.getTimestamp("created_date"));
        blogPost.setModifiedDate(rs.getTimestamp("modified_date"));
        blogPost.setImageUrl(rs.getString("image_url"));
        blogPost.setCategory(rs.getString("category"));
        blogPost.setTags(rs.getString("tags"));
        blogPost.setStatus(rs.getString("status"));
        return blogPost;
    }
}