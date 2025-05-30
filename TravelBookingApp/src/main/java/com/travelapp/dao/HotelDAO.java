package com.travelapp.dao;

import com.travelapp.model.Hotel;
import com.travelapp.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HotelDAO {

    public void addHotel(Hotel hotel) {
        String sql = "INSERT INTO Hotel (name, city, price_per_night) VALUES (?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, hotel.getName());
            pstmt.setString(2, hotel.getCity());
            pstmt.setDouble(3, hotel.getPricePerNight());
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        hotel.setHotelId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding hotel: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* ignore */ }
        }
    }

    public List<Hotel> searchHotels(String city) {
        List<Hotel> hotels = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT hotel_id, name, city, price_per_night FROM Hotel WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (city != null && !city.isEmpty()) {
            sql.append(" AND city LIKE ?");
            params.add("%" + city + "%");
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql.toString());

            for (int i = 0; i < params.size(); i++) {
                pstmt.setString(i + 1, (String) params.get(i));
            }

            rs = pstmt.executeQuery();

            while (rs.next()) {
                hotels.add(new Hotel(
                        rs.getInt("hotel_id"),
                        rs.getString("name"),
                        rs.getString("city"),
                        rs.getDouble("price_per_night")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error searching hotels: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignore */ }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* ignore */ }
            DBConnection.closeConnection(conn);
        }
        return hotels;
    }

    public Hotel getHotelById(int hotelId) {
        String sql = "SELECT hotel_id, name, city, price_per_night FROM Hotel WHERE hotel_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Hotel hotel = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, hotelId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                hotel = new Hotel(
                        rs.getInt("hotel_id"),
                        rs.getString("name"),
                        rs.getString("city"),
                        rs.getDouble("price_per_night")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching hotel by ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignore */ }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* ignore */ }
            DBConnection.closeConnection(conn);
        }
        return hotel;
    }

    // Update Hotel
    public boolean updateHotel(Hotel hotel) {
        String sql = "UPDATE Hotel SET name = ?, city = ?, price_per_night = ? WHERE hotel_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, hotel.getName());
            pstmt.setString(2, hotel.getCity());
            pstmt.setDouble(3, hotel.getPricePerNight());
            pstmt.setInt(4, hotel.getHotelId());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating hotel: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeConnection(conn);
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* ignore */ }
        }
    }

    // Delete Hotel
    public boolean deleteHotel(int hotelId) {
        String sql = "DELETE FROM Hotel WHERE hotel_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, hotelId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting hotel: " + e.getMessage());
            System.err.println("Note: Cannot delete hotel if there are associated bookings (due to foreign key constraints).");
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeConnection(conn);
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* ignore */ }
        }
    }

    //  Get all hotels 
    public List<Hotel> getAllHotels() {
        return searchHotels(null); // Calling search with null to get all
    }
}