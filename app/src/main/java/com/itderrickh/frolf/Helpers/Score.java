package com.itderrickh.frolf.Helpers;

public class Score {
    private int value;
    private int id;
    private int userId;
    private int holeNumber;
    private int groupId;

    public Score(int id, int value, int userId, int holeNumber, int groupId) {
        this.value = value;
        this.id = id;
        this.userId = userId;
        this.holeNumber = holeNumber;
        this.groupId = groupId;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getHoleNumber() {
        return holeNumber;
    }

    public void setHoleNumber(int holeNumber) {
        this.holeNumber = holeNumber;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
}
