package com.bachnt.dao;

import com.bachnt.model.Education;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EducationDAO {
    private Education extractEducationFromResultSet(ResultSet rs) throws SQLException {
        Education edu = new Education();
        edu.setId(rs.getInt("id"));
        edu.setProfileId(rs.getInt("profile_id"));
        edu.setSchoolName(rs.getString("school_name"));
        edu.setDegree(rs.getString("degree"));
        edu.setFieldOfStudy(rs.getString("field_of_study"));
        edu.setStartYear(rs.getString("start_year"));
        edu.setEndYear(rs.getString("end_year"));
        edu.setDescription(rs.getString("description"));
        return edu;
    }

    public List<Education> getEducationsByProfileId(int profileId) {
        List<Education> educations = new ArrayList<>();
        String sql = "SELECT * FROM educations WHERE profile_id = ? ORDER BY end_year DESC, start_year DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, profileId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    educations.add(extractEducationFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return educations;
    }
    // Thêm các hàm add, update, delete Education nếu cần cho admin sau
}