package com.travelapp.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Flight {
    private int flightId;
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private double price;

    public Flight(int flightId, String origin, String destination, LocalDateTime departureTime, LocalDateTime arrivalTime, double price) {
        this.flightId = flightId;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
    }

    public Flight(String origin, String destination, LocalDateTime departureTime, LocalDateTime arrivalTime, double price) {
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
    }

    // Getters
    public int getFlightId() { return flightId; }
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public LocalDateTime getDepartureTime() { return departureTime; }
    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public double getPrice() { return price; }

    public void setFlightId(int flightId) { this.flightId = flightId; }
    public void setOrigin(String origin) { this.origin = origin; }
    public void setDestination(String destination) { this.destination = destination; }
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }
    public void setArrivalTime(LocalDateTime arrivalTime) { this.arrivalTime = arrivalTime; }
    public void setPrice(double price) { this.price = price; }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return "ID: " + flightId + ", " + origin + " to " + destination +
               " (Dep: " + departureTime.format(formatter) + ", Arr: " + arrivalTime.format(formatter) +
               "), Price: $" + String.format("%.2f", price);
    }
}