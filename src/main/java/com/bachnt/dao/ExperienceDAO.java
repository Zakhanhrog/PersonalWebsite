package com.bachnt.dao;

import com.bachnt.model.Experience;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
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
        exp.setEndDate(rs.getDate("end_date"));
        exp.setDescriptionResponsibilities(rs.getString("description_responsibilities"));
        return exp;
    }

    public List<Experience> getExperiencesByProfileId(int profileId) {
        List<Experience> experiences = new ArrayList<>();
        String sql = "SELECT * FROM experiences WHERE profile_id = ? ORDER BY (end_date IS NULL) DESC, end_date DESC, start_date DESC";
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

    public Experience getExperienceById(int experienceId) {
        Experience exp = null;
        String sql = "SELECT * FROM experiences WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, experienceId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    exp = extractExperienceFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exp;
    }

    public boolean addExperience(Experience exp) {
        String sql = "INSERT INTO experiences (profile_id, company_name, position, start_date, end_date, description_responsibilities) VALUES (?, ?, ?, ?, ?, ?)";
        boolean rowInserted = false;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, exp.getProfileId());
            stmt.setString(2, exp.getCompanyName());
            stmt.setString(3, exp.getPosition());
            stmt.setDate(4, new java.sql.Date(exp.getStartDate().getTime()));
            if (exp.getEndDate() != null) {
                stmt.setDate(5, new java.sql.Date(exp.getEndDate().getTime()));
            } else {
                stmt.setNull(5, Types.DATE);
            }
            stmt.setString(6, exp.getDescriptionResponsibilities());
            rowInserted = stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowInserted;
    }

    public boolean updateExperience(Experience exp) {
        String sql = "UPDATE experiences SET company_name = ?, position = ?, start_date = ?, end_date = ?, description_responsibilities = ? WHERE id = ? AND profile_id = ?";
        boolean rowUpdated = false;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, exp.getCompanyName());
            stmt.setString(2, exp.getPosition());
            stmt.setDate(3, new java.sql.Date(exp.getStartDate().getTime()));
            if (exp.getEndDate() != null) {
                stmt.setDate(4, new java.sql.Date(exp.getEndDate().getTime()));
            } else {
                stmt.setNull(4, Types.DATE);
            }
            stmt.setString(5, exp.getDescriptionResponsibilities());
            stmt.setInt(6, exp.getId());
            stmt.setInt(7, exp.getProfileId());
            rowUpdated = stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowUpdated;
    }

    public boolean deleteExperience(int experienceId) {
        String sql = "DELETE FROM experiences WHERE id = ?";
        boolean rowDeleted = false;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, experienceId);
            rowDeleted = stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowDeleted;
    }
}