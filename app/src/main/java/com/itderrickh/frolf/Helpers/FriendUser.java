package com.itderrickh.frolf.Helpers;

import java.util.Date;

public class FriendUser {
    private int id;
    private Date dateAdded;
    private String email;

    public FriendUser(int id, Date dateAdded, String email) {
        this.id = id;
        this.dateAdded = dateAdded;
        this.email = email;
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
}
