package com.bachnt.model;

import java.util.List;

public class Profile {
    private int id; // Added ID for database primary key
    private String name;
    private String position;
    private String companyName;
    private String companyTaxId;
    private String companyAddress;
    private String phoneNumber;
    private String email;
    private String bio;
    private String photoUrl;
    private List<Skill> skills; // To hold associated skills

    public Profile() {
    }

    public Profile(int id, String name, String position, String companyName, String companyTaxId,
                   String companyAddress, String phoneNumber, String email, String bio, String photoUrl) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.companyName = companyName;
        this.companyTaxId = companyTaxId;
        this.companyAddress = companyAddress;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.bio = bio;
        this.photoUrl = photoUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyTaxId() {
        return companyTaxId;
    }

    public void setCompanyTaxId(String companyTaxId) {
        this.companyTaxId = companyTaxId;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email.trim(); // Trim email
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }
}