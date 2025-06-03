package com.bachnt.dao;

import com.bachnt.model.Comment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO {

    private Comment extractCommentFromResultSet(ResultSet rs) throws SQLException {
        Comment comment = new Comment();
        comment.setId(rs.getInt("id"));
        comment.setBlogPostId(rs.getInt("blog_post_id"));
        comment.setParentCommentId(rs.getObject("parent_comment_id", Integer.class)); // Cho phép null
        comment.setAuthorName(rs.getString("author_name"));
        comment.setAuthorEmail(rs.getString("author_email"));
        comment.setContent(rs.getString("content"));
        comment.setCreatedDate(rs.getTimestamp("created_date"));
        comment.setStatus(rs.getString("status"));
        return comment;
    }

    public List<Comment> getApprovedCommentsByPostId(int blogPostId) {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT * FROM comments WHERE blog_post_id = ? AND status = 'approved' ORDER BY created_date ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, blogPostId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    comments.add(extractCommentFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comments;
    }

    public boolean saveComment(Comment comment) {
        String sql = "INSERT INTO comments (blog_post_id, parent_comment_id, author_name, author_email, content, created_date, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        boolean rowInserted = false;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, comment.getBlogPostId());
            if (comment.getParentCommentId() != null) {
                stmt.setInt(2, comment.getParentCommentId());
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
            }
            stmt.setString(3, comment.getAuthorName());
            stmt.setString(4, comment.getAuthorEmail()); // Can be null
            stmt.setString(5, comment.getContent());
            stmt.setTimestamp(6, new Timestamp(comment.getCreatedDate().getTime()));
            stmt.setString(7, comment.getStatus() != null ? comment.getStatus() : "pending"); // Default to pending if not set

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                rowInserted = true;
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        comment.setId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowInserted;
    }

}