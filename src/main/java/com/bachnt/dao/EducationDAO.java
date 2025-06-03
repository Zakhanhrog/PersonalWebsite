package com.bachnt.dao;

import com.bachnt.model.Education;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

    public boolean addEducation(Education edu) {
        String sql = "INSERT INTO educations (profile_id, school_name, degree, field_of_study, start_year, end_year, description) VALUES (?, ?, ?, ?, ?, ?, ?)";
        boolean rowInserted = false;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, edu.getProfileId());
            stmt.setString(2, edu.getSchoolName());
            stmt.setString(3, edu.getDegree());
            stmt.setString(4, edu.getFieldOfStudy());
            stmt.setString(5, edu.getStartYear());
            stmt.setString(6, edu.getEndYear());
            stmt.setString(7, edu.getDescription());
            rowInserted = stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowInserted;
    }

    public Education getEducationById(int educationId) {
        Education edu = null;
        String sql = "SELECT * FROM educations WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, educationId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    edu = extractEducationFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return edu;
    }

    public boolean updateEducation(Education edu) {
        String sql = "UPDATE educations SET school_name = ?, degree = ?, field_of_study = ?, start_year = ?, end_year = ?, description = ? WHERE id = ? AND profile_id = ?";
        boolean rowUpdated = false;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, edu.getSchoolName());
            stmt.setString(2, edu.getDegree());
            stmt.setString(3, edu.getFieldOfStudy());
            stmt.setString(4, edu.getStartYear());
            stmt.setString(5, edu.getEndYear());
            stmt.setString(6, edu.getDescription());
            stmt.setInt(7, edu.getId());
            stmt.setInt(8, edu.getProfileId()); // Đảm bảo cập nhật đúng của profile
            rowUpdated = stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowUpdated;
    }

    public boolean deleteEducation(int educationId) {
        String sql = "DELETE FROM educations WHERE id = ?";
        boolean rowDeleted = false;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, educationId);
            rowDeleted = stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowDeleted;
    }
}