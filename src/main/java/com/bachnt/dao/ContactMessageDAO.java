package com.bachnt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.bachnt.model.ContactMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContactMessageDAO {
    private static final Logger logger = LoggerFactory.getLogger(ContactMessageDAO.class);

    public boolean saveContactMessage(ContactMessage message) {
        String sql = "INSERT INTO contact_messages (name, email, subject, message, created_date, status) VALUES (?, ?, ?, ?, ?, ?)";
        boolean success = false;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, message.getName());
            stmt.setString(2, message.getEmail());
            stmt.setString(3, message.getSubject());
            stmt.setString(4, message.getMessage());
            stmt.setTimestamp(5, new Timestamp(new Date().getTime())); // Set current time for created_date
            stmt.setString(6, message.getStatus() != null ? message.getStatus() : "new"); // Default status

            int rowsAffected = stmt.executeUpdate();
            success = (rowsAffected > 0);
        } catch (SQLException e) {
                logger.error("Lỗi SQL khi thực hiện [tên_hàm/mô_tả_ngắn_gọn]: {}", e.getMessage(), e);
        }
        return success;
    }

    public List<ContactMessage> getAllContactMessages() {
        List<ContactMessage> messages = new ArrayList<>();
        String sql = "SELECT * FROM contact_messages ORDER BY created_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ContactMessage message = extractContactMessageFromResultSet(rs);
                messages.add(message);
            }
        } catch (SQLException e) {
                logger.error("Lỗi SQL khi thực hiện [tên_hàm/mô_tả_ngắn_gọn]: {}", e.getMessage(), e);
        }
        return messages;
    }

    public ContactMessage getContactMessageById(int id) {
        ContactMessage message = null;
        String sql = "SELECT * FROM contact_messages WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    message = extractContactMessageFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
                logger.error("Lỗi SQL khi thực hiện [tên_hàm/mô_tả_ngắn_gọn]: {}", e.getMessage(), e);
        }
        return message;
    }

    public boolean updateMessageStatus(int id, String status) {
        String sql = "UPDATE contact_messages SET status = ? WHERE id = ?";
        boolean success = false;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, id);

            int rowsAffected = stmt.executeUpdate();
            success = (rowsAffected > 0);
        } catch (SQLException e) {
                logger.error("Lỗi SQL khi thực hiện [tên_hàm/mô_tả_ngắn_gọn]: {}", e.getMessage(), e);
        }
        return success;
    }

    public boolean deleteContactMessage(int id) {
        String sql = "DELETE FROM contact_messages WHERE id = ?";
        boolean rowDeleted = false;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            rowDeleted = stmt.executeUpdate() > 0;
        } catch (SQLException e) {
                logger.error("Lỗi SQL khi thực hiện [tên_hàm/mô_tả_ngắn_gọn]: {}", e.getMessage(), e);
        }
        return rowDeleted;
    }

    private ContactMessage extractContactMessageFromResultSet(ResultSet rs) throws SQLException {
        ContactMessage message = new ContactMessage();
        message.setId(rs.getInt("id"));
        message.setName(rs.getString("name"));
        message.setEmail(rs.getString("email"));
        message.setSubject(rs.getString("subject"));
        message.setMessage(rs.getString("message"));
        message.setCreatedDate(rs.getTimestamp("created_date"));
        message.setStatus(rs.getString("status"));
        return message;
    }

    public int getUnreadMessageCount() {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM contact_messages WHERE status = 'new'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
                logger.error("Lỗi SQL khi thực hiện [tên_hàm/mô_tả_ngắn_gọn]: {}", e.getMessage(), e);
        }
        return count;
    }
}