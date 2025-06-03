package com.bachnt.model;

public class Skill {
    private int id;
    private int profileId; // Foreign key to Profile table
    private String name;
    private int level; // e.g., percentage 0-100, or a scale 1-5
    private String category; // e.g., "Technical", "Soft Skill", "Language"

    public Skill() {
    }

    public Skill(int id, int profileId, String name, int level, String category) {
        this.id = id;
        this.profileId = profileId;
        this.name = name;
        this.level = level;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        if (level < 0) {
            this.level = 0;
        } else if (level > 100) { // Assuming level is a percentage
            this.level = 100;
        } else {
            this.level = level;
        }
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}