package com.movies.domain;

/**
 * Class for location.
 *
 * Participating in serialization/deserialization with
 * gson and objectify (pay attention during refactoring).
 */
public class Location {

    /**
     * location name from movies database
     * */
    private String name;
    /**
     * structured address provided by google geocoder
     * */
    private String address;
    /**
     * Latitude
     */
    private double lat;
    /**
     * Longitude
     */
    private double lng;

    // gson deserialization needs default constructor
    public Location() {
    }

    public Location(String name) {
        this.name = name;
    }

    public Location(String name, String address, double lat, double lng) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
