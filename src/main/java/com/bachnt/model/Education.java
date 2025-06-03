package com.bachnt.model;

public class Education {
    private int id;
    private int profileId;
    private String schoolName;
    private String degree;
    private String fieldOfStudy;
    private String startYear; // Hoặc int
    private String endYear;
    private String description;

    public Education() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getProfileId() { return profileId; }
    public void setProfileId(int profileId) { this.profileId = profileId; }
    public String getSchoolName() { return schoolName; }
    public void setSchoolName(String schoolName) { this.schoolName = schoolName; }
    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }
    public String getFieldOfStudy() { return fieldOfStudy; }
    public void setFieldOfStudy(String fieldOfStudy) { this.fieldOfStudy = fieldOfStudy; }
    public String getStartYear() { return startYear; }
    public void setStartYear(String startYear) { this.startYear = startYear; }
    public String getEndYear() { return endYear; }
    public void setEndYear(String endYear) { this.endYear = endYear; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}