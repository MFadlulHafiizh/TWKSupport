package com.application.twksupport.model;

public class BugsData {
    private String priority;
    private String Subject;
    private String status;

    public BugsData() {
    }

    public BugsData(String priority, String subject, String status) {
        this.priority = priority;
        Subject = subject;
        this.status = status;
    }


    //setter
    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    //gettter
    public String getPriority() {
        return priority;
    }

    public String getSubject() {
        return Subject;
    }

    public String getStatus() {
        return status;
    }
}
