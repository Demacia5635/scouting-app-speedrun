package com.example.myapplication;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class qual {
    private String path;
    private String mode;
    private String qualsName;
    private String teamNumber;

    public qual(String path , String mode ) {
        this.path = path;
        this.mode = mode;
        String quals = this.mode;
        path = path.substring(path.indexOf(quals)+quals.length()+1);
        qualsName = path.substring(0,path.indexOf("/"));
        String  subteamNumber = path.substring(path.indexOf(qualsName)+qualsName.length()+1);
        String disCode="ARPKY";
        Pattern pattern = Pattern.compile("^[0-9]+[A-Z]", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(subteamNumber);
        boolean matchFound = matcher.find();
        if(matchFound){
        teamNumber = matcher.group().substring(0,matcher.group().length()-1);}
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

    public String getMode() {
        return this.mode;
    }
}
