package com.bachnt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.bachnt.model.Profile;
import com.bachnt.model.Skill;

public class ProfileDAO {

    public Profile getProfileById(int id) {
        Profile profile = null;
        String sql = "SELECT * FROM profile WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    profile = extractProfileFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return profile;
    }

    public Profile getDefaultProfile() {
        return getProfileById(1);
    }

    public boolean updateProfile(Profile profile) {
        String sql = "UPDATE profile SET name = ?, position = ?, company_name = ?, company_tax_id = ?, company_address = ?, phone_number = ?, email = ?, bio = ?, photo_url = ? WHERE id = ?";
        boolean rowUpdated = false;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, profile.getName());
            stmt.setString(2, profile.getPosition());
            stmt.setString(3, profile.getCompanyName());
            stmt.setString(4, profile.getCompanyTaxId());
            stmt.setString(5, profile.getCompanyAddress());
            stmt.setString(6, profile.getPhoneNumber());
            stmt.setString(7, profile.getEmail());
            stmt.setString(8, profile.getBio());
            stmt.setString(9, profile.getPhotoUrl());
            stmt.setInt(10, profile.getId()); // Assuming Profile model has an ID field for the WHERE clause

            rowUpdated = stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowUpdated;
    }

    public List<Skill> getSkillsByProfileId(int profileId) {
        List<Skill> skills = new ArrayList<>();
        String sql = "SELECT * FROM skills WHERE profile_id = ? ORDER BY category, level DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, profileId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Skill skill = extractSkillFromResultSet(rs);
                    skills.add(skill);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return skills;
    }

    public boolean addSkill(Skill skill) {
        String sql = "INSERT INTO skills (profile_id, name, level, category) VALUES (?, ?, ?, ?)";
        boolean rowInserted = false;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, skill.getProfileId());
            stmt.setString(2, skill.getName());
            stmt.setInt(3, skill.getLevel());
            stmt.setString(4, skill.getCategory());

            rowInserted = stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowInserted;
    }

    public boolean updateSkill(Skill skill) {
        String sql = "UPDATE skills SET profile_id = ?, name = ?, level = ?, category = ? WHERE id = ?";
        boolean rowUpdated = false;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, skill.getProfileId());
            stmt.setString(2, skill.getName());
            stmt.setInt(3, skill.getLevel());
            stmt.setString(4, skill.getCategory());
            stmt.setInt(5, skill.getId());

            rowUpdated = stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowUpdated;
    }

    public boolean deleteSkill(int skillId) {
        String sql = "DELETE FROM skills WHERE id = ?";
        boolean rowDeleted = false;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, skillId);
            rowDeleted = stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowDeleted;
    }

    private Profile extractProfileFromResultSet(ResultSet rs) throws SQLException {
        Profile profile = new Profile();
        profile.setId(rs.getInt("id"));
        profile.setName(rs.getString("name"));
        profile.setPosition(rs.getString("position"));
        profile.setCompanyName(rs.getString("company_name"));
        profile.setCompanyTaxId(rs.getString("company_tax_id"));
        profile.setCompanyAddress(rs.getString("company_address"));
        profile.setPhoneNumber(rs.getString("phone_number"));
        profile.setEmail(rs.getString("email"));
        profile.setBio(rs.getString("bio"));
        profile.setPhotoUrl(rs.getString("photo_url"));
        return profile;
    }

    private Skill extractSkillFromResultSet(ResultSet rs) throws SQLException {
        Skill skill = new Skill();
        skill.setId(rs.getInt("id"));
        skill.setProfileId(rs.getInt("profile_id"));
        skill.setName(rs.getString("name"));
        skill.setLevel(rs.getInt("level"));
        skill.setCategory(rs.getString("category"));
        return skill;
    }
}