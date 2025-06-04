package com.bachnt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import com.bachnt.model.Project;

public class ProjectDAO {

    public Project getProjectById(int id) {
        Project project = null;
        String sql = "SELECT * FROM projects WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    project = extractProjectFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return project;
    }

    public List<Project> getAllProjects() {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM projects ORDER BY start_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Project project = extractProjectFromResultSet(rs);
                projects.add(project);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projects;
    }

    public boolean addProject(Project project) {
        String sql = "INSERT INTO projects (title, description, client, location, start_date, end_date, image_url, category, status, link) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        boolean rowInserted = false;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, project.getTitle());
            stmt.setString(2, project.getDescription());
            stmt.setString(3, project.getClient());
            stmt.setString(4, project.getLocation());
            stmt.setTimestamp(5, new Timestamp(project.getStartDate().getTime()));
            stmt.setTimestamp(6, project.getEndDate() != null ? new Timestamp(project.getEndDate().getTime()) : null);
            stmt.setString(7, project.getImageUrl());
            stmt.setString(8, project.getCategory());
            stmt.setString(9, project.getStatus());
            stmt.setString(10, project.getLink());

            rowInserted = stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowInserted;
    }

    public boolean updateProject(Project project) {
        String sql = "UPDATE projects SET title = ?, description = ?, client = ?, location = ?, start_date = ?, end_date = ?, image_url = ?, category = ?, status = ?, link = ? WHERE id = ?";
        boolean rowUpdated = false;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, project.getTitle());
            stmt.setString(2, project.getDescription());
            stmt.setString(3, project.getClient());
            stmt.setString(4, project.getLocation());
            stmt.setTimestamp(5, new Timestamp(project.getStartDate().getTime()));
            stmt.setTimestamp(6, project.getEndDate() != null ? new Timestamp(project.getEndDate().getTime()) : null);
            stmt.setString(7, project.getImageUrl());
            stmt.setString(8, project.getCategory());
            stmt.setString(9, project.getStatus());
            stmt.setString(10, project.getLink());
            stmt.setInt(11, project.getId());

            rowUpdated = stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowUpdated;
    }

    public boolean deleteProject(int id) {
        String sql = "DELETE FROM projects WHERE id = ?";
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

    private Project extractProjectFromResultSet(ResultSet rs) throws SQLException {
        Project project = new Project();
        project.setId(rs.getInt("id"));
        project.setTitle(rs.getString("title"));
        project.setDescription(rs.getString("description"));
        project.setClient(rs.getString("client"));
        project.setLocation(rs.getString("location"));
        project.setStartDate(rs.getTimestamp("start_date"));
        project.setEndDate(rs.getTimestamp("end_date"));
        project.setImageUrl(rs.getString("image_url"));
        project.setCategory(rs.getString("category"));
        project.setStatus(rs.getString("status"));
        project.setLink(rs.getString("link"));
        return project;
    }
    public int getTotalProjectCount() {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM projects";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Nên dùng logging
        }
        return count;
    }
}