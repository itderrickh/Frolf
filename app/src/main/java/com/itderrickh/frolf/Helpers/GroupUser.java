package com.itderrickh.frolf.Helpers;

public class GroupUser {
    private int id;
    private String email;
    private Integer friendid;

    public GroupUser(int id, String email, Integer friendid) {
        this.id = id;
        this.email = email;
        this.friendid = friendid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getFriendid() {
        return friendid;
    }

    public void setFriendid(Integer friendid) {
        this.friendid = friendid;
    }
}
