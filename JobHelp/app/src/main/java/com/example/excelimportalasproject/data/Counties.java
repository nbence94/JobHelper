package com.example.excelimportalasproject.data;

public class Counties {

    private int id;
    private String name;

    public Counties(int id, String county_name) {
        this.id = id;
        this.name = county_name;
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
}
