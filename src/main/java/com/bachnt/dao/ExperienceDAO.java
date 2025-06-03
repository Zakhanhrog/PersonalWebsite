package com.bachnt.dao;

import com.bachnt.model.Experience;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExperienceDAO {
    private Experience extractExperienceFromResultSet(ResultSet rs) throws SQLException {
        Experience exp = new Experience();
        exp.setId(rs.getInt("id"));
        exp.setProfileId(rs.getInt("profile_id"));
        exp.setCompanyName(rs.getString("company_name"));
        exp.setPosition(rs.getString("position"));
        exp.setStartDate(rs.getDate("start_date"));
        exp.setEndDate(rs.getDate("end_date")); // Có thể null
        exp.setDescriptionResponsibilities(rs.getString("description_responsibilities"));
        return exp;
    }

    public List<Experience> getExperiencesByProfileId(int profileId) {
        List<Experience> experiences = new ArrayList<>();
        String sql = "SELECT * FROM experiences WHERE profile_id = ? ORDER BY end_date DESC NULLS FIRST, start_date DESC"; // NULLS FIRST cho công việc hiện tại
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, profileId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    experiences.add(extractExperienceFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return experiences;
    }
    // Thêm các hàm add, update, delete Experience nếu cần cho admin sau
}