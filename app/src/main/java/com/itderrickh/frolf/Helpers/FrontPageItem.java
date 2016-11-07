package com.itderrickh.frolf.Helpers;

import java.util.Date;

public class FrontPageItem {
    private int id;
    private Date datescored;
    private String groupName;
    private int score;
    private int par;
    private int holes;
    private String email;

    public FrontPageItem(int id, Date datescored, String groupName, int score, int par, int holes, String email) {
        this.id = id;
        this.datescored = datescored;
        this.groupName = groupName;
        this.score = score;
        this.par = par;
        this.holes = holes;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDatescored() {
        return datescored;
    }

    public void setDatescored(Date datescored) {
        this.datescored = datescored;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getPar() {
        return par;
    }

    public void setPar(int par) {
        this.par = par;
    }

    public int getHoles() {
        return holes;
    }

    public void setHoles(int holes) {
        this.holes = holes;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
