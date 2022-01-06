package com.example.excelimportalasproject.data;

public class Places {

    private int id;
    private String city;
    private String address;
    private String size;
    private String campaign;
    private int billboard_code;
    private int status;
    private int circle_id;
    private String done_date;

    public Places (int id, String city, String address, String size, String campaign, int billboard_code, int status, int circle_id, String done_date){
        this.id = id;
        this.city = city;
        this.address = address;
        this.size = size;
        this.campaign = campaign;
        this.billboard_code = billboard_code;
        this.status = status;
        this.circle_id = circle_id;
        this.done_date = done_date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getCampaign() {
        return campaign;
    }

    public void setCampaign(String campaign) {
        this.campaign = campaign;
    }

    public int getBillboard_code() {
        return billboard_code;
    }

    public void setBillboard_code(int billboard_code) {
        this.billboard_code = billboard_code;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCircle_id() {
        return circle_id;
    }

    public void setCircle_id(int circle_id) {
        this.circle_id = circle_id;
    }

    public String getDone_date() {
        return done_date;
    }

    public void setDone_date(String done_date) {
        this.done_date = done_date;
    }
}
