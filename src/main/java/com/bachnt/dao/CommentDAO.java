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
        comment.setParentCommentId(rs.getObject("parent_comment_id", Integer.class));
        comment.setAuthorName(rs.getString("author_name"));
        comment.setAuthorEmail(rs.getString("author_email"));
        comment.setContent(rs.getString("content"));
        comment.setCreatedDate(rs.getTimestamp("created_date"));
        comment.setStatus(rs.getString("status"));
        return comment;
    }

    public List<Comment> getApprovedCommentsByPostId(int blogPostId) {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT c.*, bp.title as blog_post_title FROM comments c JOIN blog_posts bp ON c.blog_post_id = bp.id WHERE c.blog_post_id = ? AND c.status = 'approved' ORDER BY c.created_date ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, blogPostId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Không cần lấy blog_post_title vào model Comment ở đây, nhưng query có thể hữu ích nếu muốn join
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
            stmt.setString(4, comment.getAuthorEmail());
            stmt.setString(5, comment.getContent());
            stmt.setTimestamp(6, new Timestamp(comment.getCreatedDate().getTime()));
            stmt.setString(7, comment.getStatus() != null ? comment.getStatus() : "pending");
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

    // HÀM MỚI CHO ADMIN
    public List<Comment> getAllCommentsAdmin() { // Có thể thêm filter/phân trang sau
        List<Comment> comments = new ArrayList<>();
        // JOIN với blog_posts để lấy tiêu đề bài viết cho dễ quản lý
        String sql = "SELECT c.*, bp.title as blog_post_title FROM comments c LEFT JOIN blog_posts bp ON c.blog_post_id = bp.id ORDER BY c.created_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Comment comment = extractCommentFromResultSet(rs);
                // Bạn có thể tạo một DTO hoặc thêm trường tạm thời vào Comment model để chứa blog_post_title
                // Hoặc xử lý ở Servlet nếu cần hiển thị tên bài viết
                // Ví dụ: comment.setBlogPostTitle(rs.getString("blog_post_title"));
                comments.add(comment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comments;
    }

    // HÀM MỚI CHO ADMIN
    public Comment getCommentById(int commentId) {
        Comment comment = null;
        String sql = "SELECT * FROM comments WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, commentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    comment = extractCommentFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comment;
    }

    // HÀM MỚI CHO ADMIN
    public boolean updateCommentStatus(int commentId, String newStatus) {
        String sql = "UPDATE comments SET status = ? WHERE id = ?";
        boolean rowUpdated = false;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, commentId);
            rowUpdated = stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowUpdated;
    }

    // HÀM MỚI CHO ADMIN (Nếu cho phép sửa nội dung)
    public boolean updateCommentContent(int commentId, String newContent) {
        String sql = "UPDATE comments SET content = ? WHERE id = ?";
        boolean rowUpdated = false;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newContent);
            stmt.setInt(2, commentId);
            rowUpdated = stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowUpdated;
    }

    // HÀM MỚI CHO ADMIN
    public boolean deleteComment(int commentId) {
        String sql = "DELETE FROM comments WHERE id = ?";
        boolean rowDeleted = false;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, commentId);
            // Cũng nên xóa các comment con nếu có, hoặc CSDL tự xử lý qua FOREIGN KEY ON DELETE CASCADE
            rowDeleted = stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowDeleted;
    }
}