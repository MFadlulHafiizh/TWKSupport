package com.application.twksupport.model;

import java.util.List;

public class ResponseData {
    private String message;
    private List<BugsData> bugData;
    private List<FeatureData> featureData;
    private List<DoneData> doneData;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<BugsData> getBugData() {
        return bugData;
    }

    public void setBugData(List<BugsData> bugData) {
        this.bugData = bugData;
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
}
