package com.example.myapplication;

import java.util.Map;

public class ToUploadVar {
    private Map<String, Object> firebasedat;
    private  String datapath;
    private String mode;

    public ToUploadVar(Map<String, Object> firebasedat, String datapath, String mode) {
        this.firebasedat = firebasedat;
        this.datapath = datapath;
        this.mode = mode;
    }

    public Map<String, Object> getFirebasedat() {
        return firebasedat;
    }

    public String getDatapath() {
        return datapath;
    }

    public String getMode() {
        return mode;
    }
}
