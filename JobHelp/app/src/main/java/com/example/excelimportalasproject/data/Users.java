package com.example.excelimportalasproject.data;

public class Users {

    private int id;
    private String name;
    private String email;
    private String password;
    private String county;
    private int role;
    private int status;

    public Users(int user_id, String user_name, String user_email, String user_password, String user_county, int role_id, int status) {
        this.id = user_id;
        this.name = user_name;
        this.email = user_email;
        this.password = user_password;
        this.county = user_county;
        this.role = role_id;
        this.status = status;
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

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
