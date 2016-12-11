package com.itderrickh.frolf.Helpers;

public class Group {
    private int id;
    private String name;
    private double longitude;
    private double latitude;
    private String email;

    private boolean isCurrentLocationSet = false;
    private double curLat;
    private double curLong;

    public Group(int id, String name, double longitude, double latitude, String email) {
        this.id = id;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.email = email;
    }

    public void setCurrentLocation(double cLatitude, double cLongitude) {
        curLat = cLatitude;
        curLong = cLongitude;

        isCurrentLocationSet = true;
    }

    public boolean isCurrentLocationSet() {
        return isCurrentLocationSet;
    }
    public boolean isLocationSet() { return (latitude != 0 && longitude != 0); }

    public double getDistance() {
        if(curLong == 0 || curLat == 0) {
            return 0;
        } else {
            return distance();
        }
    }

    private double distance() {
        double theta = longitude - curLong;
        double dist = Math.sin(deg2rad(latitude)) * Math.sin(deg2rad(curLat))
                + Math.cos(deg2rad(latitude)) * Math.cos(deg2rad(curLat))
                * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        return dist;
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
