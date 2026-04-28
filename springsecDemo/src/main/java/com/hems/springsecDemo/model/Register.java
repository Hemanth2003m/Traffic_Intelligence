package com.hems.springsecDemo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "register")
public class Register {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long registerId;

    private String username;
    private String email;
    private String password;

    public long getRegisterId() {
        return registerId;
    }

    public void setRegisterId(long registerId) {
        this.registerId = registerId;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}