package com.travelapp.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Booking {
    private int bookingId;
    private int customerId;
    private Integer flightId; 
    private Integer hotelId;  
    private LocalDate bookingDate;

    // For retrieving from DB
    public Booking(int bookingId, int customerId, Integer flightId, Integer hotelId, LocalDate bookingDate) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.flightId = flightId;
        this.hotelId = hotelId;
        this.bookingDate = bookingDate;
    }

    // For creating new booking
    public Booking(int customerId, Integer flightId, Integer hotelId, LocalDate bookingDate) {
        this.customerId = customerId;
        this.flightId = flightId;
        this.hotelId = hotelId;
        this.bookingDate = bookingDate;
    }

    // Getters
    public int getBookingId() { return bookingId; }
    public int getCustomerId() { return customerId; }
    public Integer getFlightId() { return flightId; }
    public Integer getHotelId() { return hotelId; }
    public LocalDate getBookingDate() { return bookingDate; }

    // Setters 
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public void setFlightId(Integer flightId) { this.flightId = flightId; }
    public void setHotelId(Integer hotelId) { this.hotelId = hotelId; }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return "Booking ID: " + bookingId + ", Customer ID: " + customerId +
               ", Flight ID: " + (flightId != null ? flightId : "N/A") +
               ", Hotel ID: " + (hotelId != null ? hotelId : "N/A") +
               ", Date: " + bookingDate.format(formatter);
    }
}