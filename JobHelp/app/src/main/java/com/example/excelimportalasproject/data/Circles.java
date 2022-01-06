package com.example.excelimportalasproject.data;

public class Circles {

    private int id;
    private String file_name;
    private String import_date;
    private String done_date;
    private int status;
    private int user_id;

    public Circles(int id, String filename, String import_date, String done_date, int status, int user_id) {
        this.id = id;
        this.file_name = filename;
        this.import_date = import_date;
        this.done_date = done_date;
        this.status = status;
        this.user_id = user_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getImport_date() {
        return import_date;
    }

    public void setImport_date(String import_date) {
        this.import_date = import_date;
    }

    public String getDone_date() {
        return done_date;
    }

    public void setDone_date(String done_date) {
        this.done_date = done_date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
