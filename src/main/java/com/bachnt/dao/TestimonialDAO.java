package com.bachnt.dao;

import com.bachnt.model.Testimonial;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TestimonialDAO {
    private Testimonial extractTestimonialFromResultSet(ResultSet rs) throws SQLException {
        Testimonial testimonial = new Testimonial();
        testimonial.setId(rs.getInt("id"));
        testimonial.setClientName(rs.getString("client_name"));
        testimonial.setClientPositionCompany(rs.getString("client_position_company"));
        testimonial.setQuoteText(rs.getString("quote_text"));
        testimonial.setClientImageUrl(rs.getString("client_image_url"));
        testimonial.setDisplayOrder(rs.getInt("display_order"));
        return testimonial;
    }

    // Hàm này dùng cho trang public about.jsp
    public List<Testimonial> getAllDisplayableTestimonials() {
        List<Testimonial> testimonials = new ArrayList<>();
        String sql = "SELECT * FROM testimonials ORDER BY display_order ASC, id ASC"; // Có thể thêm LIMIT nếu muốn
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                testimonials.add(extractTestimonialFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return testimonials;
    }

    // HÀM MỚI CHO ADMIN - Lấy tất cả để quản lý
    public List<Testimonial> getAllTestimonialsForAdmin() {
        List<Testimonial> testimonials = new ArrayList<>();
        String sql = "SELECT * FROM testimonials ORDER BY display_order ASC, id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                testimonials.add(extractTestimonialFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return testimonials;
    }

    // HÀM MỚI CHO ADMIN
    public Testimonial getTestimonialById(int testimonialId) {
        Testimonial testimonial = null;
        String sql = "SELECT * FROM testimonials WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, testimonialId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    testimonial = extractTestimonialFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return testimonial;
    }

    // HÀM MỚI CHO ADMIN
    public boolean addTestimonial(Testimonial testimonial) {
        String sql = "INSERT INTO testimonials (client_name, client_position_company, quote_text, client_image_url, display_order) VALUES (?, ?, ?, ?, ?)";
        boolean rowInserted = false;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, testimonial.getClientName());
            stmt.setString(2, testimonial.getClientPositionCompany());
            stmt.setString(3, testimonial.getQuoteText());
            stmt.setString(4, testimonial.getClientImageUrl()); // Sẽ được cập nhật từ logic upload file
            stmt.setInt(5, testimonial.getDisplayOrder());
            rowInserted = stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowInserted;
    }

    // HÀM MỚI CHO ADMIN
    public boolean updateTestimonial(Testimonial testimonial) {
        String sql = "UPDATE testimonials SET client_name = ?, client_position_company = ?, quote_text = ?, client_image_url = ?, display_order = ? WHERE id = ?";
        boolean rowUpdated = false;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, testimonial.getClientName());
            stmt.setString(2, testimonial.getClientPositionCompany());
            stmt.setString(3, testimonial.getQuoteText());
            stmt.setString(4, testimonial.getClientImageUrl()); // Sẽ được cập nhật từ logic upload file
            stmt.setInt(5, testimonial.getDisplayOrder());
            stmt.setInt(6, testimonial.getId());
            rowUpdated = stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowUpdated;
    }

    // HÀM MỚI CHO ADMIN
    public boolean deleteTestimonial(int testimonialId) {
        String sql = "DELETE FROM testimonials WHERE id = ?";
        boolean rowDeleted = false;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, testimonialId);
            rowDeleted = stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowDeleted;
    }
}