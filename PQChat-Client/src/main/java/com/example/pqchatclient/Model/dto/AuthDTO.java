package com.example.pqchatclient.Model.dto;

import java.io.Serializable;

public class AuthDTO implements Serializable {
    private String prefix;
    private String username;
    private String password;
    private String flag;


    public AuthDTO(String prefix, String username, String password, String flag) {
        this.prefix = prefix;
        this.username = username;
        this.password = password;
        this.flag = flag;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
