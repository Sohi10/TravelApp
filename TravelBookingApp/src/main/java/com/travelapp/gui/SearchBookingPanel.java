package com.travelapp.gui;

import com.travelapp.dao.BookingDAO;
import com.travelapp.dao.CustomerDAO;
import com.travelapp.dao.FlightDAO;
import com.travelapp.dao.HotelDAO;
import com.travelapp.model.Booking;
import com.travelapp.model.Customer;
import com.travelapp.model.Flight;
import com.travelapp.model.Hotel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SearchBookingPanel extends JPanel {
    private JTextField customerIdField;
    private JTextField flightIdField;
    private JTextField hotelIdField;
    private JButton searchButton;
    private JTable bookingTable;
    private DefaultTableModel tableModel;

    private BookingDAO bookingDAO;
    private CustomerDAO customerDAO;
    private FlightDAO flightDAO;
    private HotelDAO hotelDAO;

    public SearchBookingPanel() {
        bookingDAO = new BookingDAO();
        customerDAO = new CustomerDAO();
        flightDAO = new FlightDAO();
        hotelDAO = new HotelDAO();

        setLayout(new BorderLayout());

        // Search Panel
        JPanel searchInputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Search Bookings", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        searchInputPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        searchInputPanel.add(new JLabel("Customer ID:"), gbc);
        gbc.gridx = 1;
        customerIdField = new JTextField(15);
        searchInputPanel.add(customerIdField, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        searchInputPanel.add(new JLabel("Flight ID:"), gbc);
        gbc.gridx = 1;
        flightIdField = new JTextField(15);
        searchInputPanel.add(flightIdField, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        searchInputPanel.add(new JLabel("Hotel ID:"), gbc);
        gbc.gridx = 1;
        hotelIdField = new JTextField(15);
        searchInputPanel.add(hotelIdField, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        searchButton = new JButton("Search Bookings");
        searchInputPanel.add(searchButton, gbc);

        add(searchInputPanel, BorderLayout.NORTH);

        // Results Table
        String[] columnNames = {"Booking ID", "Customer Name", "Flight Details", "Hotel Details", "Booking Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        bookingTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookingTable);
        add(scrollPane, BorderLayout.CENTER);

        searchButton.addActionListener(e -> searchBookings());
    }

    private void searchBookings() {
        Integer customerId = null;
        if (!customerIdField.getText().trim().isEmpty()) {
            try {
                customerId = Integer.parseInt(customerIdField.getText().trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid Customer ID. Must be a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        Integer flightId = null;
        if (!flightIdField.getText().trim().isEmpty()) {
            try {
                flightId = Integer.parseInt(flightIdField.getText().trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid Flight ID. Must be a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        Integer hotelId = null;
        if (!hotelIdField.getText().trim().isEmpty()) {
            try {
                hotelId = Integer.parseInt(hotelIdField.getText().trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid Hotel ID. Must be a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        List<Booking> bookings = bookingDAO.searchBookings(customerId, flightId, hotelId);

        tableModel.setRowCount(0); // Clear existing rows
        if (bookings.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No bookings found matching your criteria.", "Search Results", JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (Booking booking : bookings) {
                String customerName = "N/A";
                Customer customer = customerDAO.getCustomerById(booking.getCustomerId());
                if (customer != null) {
                    customerName = customer.getName();
                }

                String flightDetails = "N/A";
                if (booking.getFlightId() != null) {
                    Flight flight = flightDAO.getFlightById(booking.getFlightId());
                    if (flight != null) {
                        flightDetails = flight.getOrigin() + " to " + flight.getDestination() + " (ID: " + flight.getFlightId() + ")";
                    }
                }

                String hotelDetails = "N/A";
                if (booking.getHotelId() != null) {
                    // Need to search for hotels by ID or get all and filter
                    // For simplicity, let's just get all and filter
                    Hotel hotel = hotelDAO.searchHotels(null).stream()
                                    .filter(h -> h.getHotelId() == booking.getHotelId())
                                    .findFirst().orElse(null);
                    if (hotel != null) {
                        hotelDetails = hotel.getName() + " (" + hotel.getCity() + ") (ID: " + hotel.getHotelId() + ")";
                    }
                }

                tableModel.addRow(new Object[]{
                        booking.getBookingId(),
                        customerName,
                        flightDetails,
                        hotelDetails,
                        booking.getBookingDate()
                });
            }
        }
    }
}