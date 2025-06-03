package com.bachnt.model;

import java.util.Date;

public class Project {
    private int id;
    private String title;
    private String description;
    private String client;
    private String location;
    private Date startDate;
    private Date endDate; // Can be null if project is ongoing
    private String imageUrl;
    private String category;
    private String status; // e.g., "Đang triển khai", "Hoàn thành", "Tạm dừng"
    private String link;   // Link to project details or external site

    public Project() {
    }

    public Project(int id, String title, String description, String client, String location,
                   Date startDate, Date endDate, String imageUrl, String category, String status, String link) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.client = client;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.imageUrl = imageUrl;
        this.category = category;
        this.status = status;
        this.link = link;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}