package com.example.myapplication;

public class qual {
    private static int index;
    private String path;
    private String qualsName;
    private String teamNumber;

    public qual(String path) {
        this.path = path;
        String quals = "Quals";
        path = path.substring(path.indexOf(quals)+quals.length()+1);
        qualsName = path.substring(0,path.indexOf("/"));
        teamNumber = path.substring(path.indexOf(qualsName)+qualsName.length()+1,path.indexOf(qualsName)+qualsName.length()+5);
        index++;
    }

    public int getIndex() {
        return index;
    }

    public String getPath() {
        return path;
    }

    public String getQualsName() {
        return qualsName;
    }

    public String getTeamNumber() {
        return teamNumber;
    }
}
