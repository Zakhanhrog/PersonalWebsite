package com.bachnt.model;

import java.util.Date; // Hoặc String nếu bạn lưu ngày tháng dạng text

public class Experience {
    private int id;
    private int profileId;
    private String companyName;
    private String position;
    private Date startDate; // Hoặc String
    private Date endDate;   // Hoặc String, hoặc null nếu là công việc hiện tại
    private String descriptionResponsibilities;

    public Experience() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getProfileId() { return profileId; }
    public void setProfileId(int profileId) { this.profileId = profileId; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    public String getDescriptionResponsibilities() { return descriptionResponsibilities; }
    public void setDescriptionResponsibilities(String descriptionResponsibilities) { this.descriptionResponsibilities = descriptionResponsibilities; }
}