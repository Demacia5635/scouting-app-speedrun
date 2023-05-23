package com.example.myapplication;

public class qual {
    private static int index;
    private String path;
    private String mode;
    private String qualsName;
    private String teamNumber;

    public qual(String path , String mode) {
        this.path = path;
        this.mode = mode;
        String quals = this.mode;
        path = path.substring(path.indexOf(quals)+quals.length()+1);
        qualsName = path.substring(0,path.indexOf("/"));
        String  subteamNumber = path.substring(path.indexOf(qualsName)+qualsName.length()+1,path.indexOf(qualsName)+qualsName.length());
        String disCode="ARPKY";
        teamNumber = subteamNumber.substring(0,subteamNumber.indexOf(disCode));
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
