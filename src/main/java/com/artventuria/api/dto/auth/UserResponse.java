package com.artventuria.api.dto.auth;

public class UserResponse {
    private Integer id;
    private String username;
    private String email;
    private Integer points;

    public UserResponse() {}

    public UserResponse(Integer id, String username, String email, Integer points) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.points = points;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }
}