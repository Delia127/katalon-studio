package com.kms.katalon.integration.kobiton.entity;

import java.util.Date;

public class KobitonUser {
    private String id;

    private String name;

    private String role;

    private String email;

    private String username;

    private Date createdAt;

    private Date updatedAt;

    private Date deletedAt;

    private KobitonOrganization organization;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public KobitonOrganization getOrganization() {
        return organization;
    }

    public void setOrganization(KobitonOrganization organization) {
        this.organization = organization;
    }

    @Override
    public String toString() {
        return "KobitonUser [id=" + id + ", name=" + name + ", role=" + role + ", email=" + email + ", username="
                + username + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", deletedAt=" + deletedAt
                + ", organization=" + organization + "]";
    }
}
