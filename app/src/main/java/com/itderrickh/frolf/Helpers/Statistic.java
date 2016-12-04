package com.itderrickh.frolf.Helpers;

public class Statistic {
    private String description;
    private double stat;

    public Statistic(String description, double stat) {
        this.description = description;
        this.stat = stat;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getStat() {
        return stat;
    }

    public void setStat(double stat) {
        this.stat = stat;
    }
}
