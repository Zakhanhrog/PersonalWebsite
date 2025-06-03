package com.bachnt.model;

public class Testimonial {
    private int id;
    private String clientName;
    private String clientPositionCompany; // Ví dụ: "Giám đốc, Công ty ABC"
    private String quoteText;
    private String clientImageUrl;
    private int displayOrder; // Để sắp xếp

    public Testimonial() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public String getClientPositionCompany() { return clientPositionCompany; }
    public void setClientPositionCompany(String clientPositionCompany) { this.clientPositionCompany = clientPositionCompany; }
    public String getQuoteText() { return quoteText; }
    public void setQuoteText(String quoteText) { this.quoteText = quoteText; }
    public String getClientImageUrl() { return clientImageUrl; }
    public void setClientImageUrl(String clientImageUrl) { this.clientImageUrl = clientImageUrl; }
    public int getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; }
}