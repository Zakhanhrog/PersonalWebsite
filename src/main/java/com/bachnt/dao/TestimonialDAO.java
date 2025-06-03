package com.bachnt.dao;

import com.bachnt.model.Testimonial;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    public List<Testimonial> getAllDisplayableTestimonials() {
        List<Testimonial> testimonials = new ArrayList<>();
        // Giả sử có một cột 'is_active' hoặc tương tự, hoặc lấy hết rồi giới hạn số lượng
        String sql = "SELECT * FROM testimonials ORDER BY display_order ASC, id ASC LIMIT 3"; // Lấy 3 cái để hiển thị
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
    // Thêm các hàm add, update, delete Testimonial nếu cần cho admin sau
}