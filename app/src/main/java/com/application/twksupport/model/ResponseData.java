package com.application.twksupport.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ResponseData {
    private int bug_page_total;
    private int fitur_page_total;
    private int done_page_total;
    private int staff_done_page_total;
    private int staff_todo_page_total;
    private List<BugsData> dataBug;
    private List<FeatureData> featureData;
    private List<DoneData> doneData;
    private List<DoneData> hasDone;
    private List<TodoData> todoData;
    private List<AppsUserData> userApp;

    public List<DoneData> getHasDone() {
        return hasDone;
    }

    public int getDone_page_total() {
        return done_page_total;
    }

    public void setDone_page_total(int done_page_total) {
        this.done_page_total = done_page_total;
    }

    public int getStaff_done_page_total() {
        return staff_done_page_total;
    }

    public int getStaff_todo_page_total() {
        return staff_todo_page_total;
    }

    public void setStaff_todo_page_total(int staff_todo_page_total) {
        this.staff_todo_page_total = staff_todo_page_total;
    }

    public void setStaff_done_page_total(int staff_done_page_total) {
        this.staff_done_page_total = staff_done_page_total;
    }

    public void setHasDone(List<DoneData> hasDone) {
        this.hasDone = hasDone;
    }

    public List<TodoData> getTodoData() {
        return todoData;
    }

    public void setTodoData(List<TodoData> todoData) {
        this.todoData = todoData;
    }

    public int getBug_page_total() {
        return bug_page_total;
    }

    public void setBug_page_total(int bug_page_total) {
        this.bug_page_total = bug_page_total;
    }

    public int getFitur_page_total() {
        return fitur_page_total;
    }

    public void setFitur_page_total(int fitur_page_total) {
        this.fitur_page_total = fitur_page_total;
    }

    public List<BugsData> getBugData() {
        return dataBug;
    }

    public void setBugData(List<BugsData> bugData) {
        this.dataBug = bugData;
    }

    public List<FeatureData> getFeatureData() {
        return featureData;
    }

    public void setFeatureData(List<FeatureData> featureData) {
        this.featureData = featureData;
    }

    public List<DoneData> getDoneData() {
        return doneData;
    }

    public void setDoneData(List<DoneData> doneData) {
        this.doneData = doneData;
    }

    public List<AppsUserData> getUserApp() {
        return userApp;
    }

    public void setUserApp(List<AppsUserData> userApp) {
        this.userApp = userApp;
    }
}
