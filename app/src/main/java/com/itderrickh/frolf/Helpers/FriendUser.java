package com.itderrickh.frolf.Helpers;

import java.util.Date;

public class FriendUser {
    private int id;
    private Date dateAdded;
    private String email;
    private boolean isplaying;

    public FriendUser(int id, Date dateAdded, String email, boolean isplaying) {
        this.id = id;
        this.dateAdded = dateAdded;
        this.email = email;
        this.isplaying = isplaying;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isplaying() {
        return isplaying;
    }

    public void setIsplaying(boolean isplaying) {
        this.isplaying = isplaying;
    }
}
