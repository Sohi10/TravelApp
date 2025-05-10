package com.travelapp.model;

public class Hotel {
    private int hotelId;
    private String name;
    private String city;
    private double pricePerNight;

    public Hotel(int hotelId, String name, String city, double pricePerNight) {
        this.hotelId = hotelId;
        this.name = name;
        this.city = city;
        this.pricePerNight = pricePerNight;
    }

    public Hotel(String name, String city, double pricePerNight) {
        this.name = name;
        this.city = city;
        this.pricePerNight = pricePerNight;
    }

    // Getters
    public int getHotelId() { return hotelId; }
    public String getName() { return name; }
    public String getCity() { return city; }
    public double getPricePerNight() { return pricePerNight; }

    // Setters 
    public void setHotelId(int hotelId) { this.hotelId = hotelId; }
    public void setName(String name) { this.name = name; }
    public void setCity(String city) { this.city = city; }
    public void setPricePerNight(double pricePerNight) { this.pricePerNight = pricePerNight; }

    @Override
    public String toString() {
        return "ID: " + hotelId + ", Name: " + name + ", City: " + city + ", Price/Night: $" + String.format("%.2f", pricePerNight);
    }
}