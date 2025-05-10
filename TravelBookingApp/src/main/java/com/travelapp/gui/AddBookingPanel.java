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
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class AddBookingPanel extends JPanel {
    private JComboBox<Customer> customerComboBox;
    private JTextField flightIdField;
    private JTextField hotelIdField;
    private JTextField bookingDateField; // Format: YYYY-MM-DD
    private JButton createBookingButton;

    private CustomerDAO customerDAO;
    private FlightDAO flightDAO;
    private HotelDAO hotelDAO;
    private BookingDAO bookingDAO;

    public AddBookingPanel() {
        customerDAO = new CustomerDAO();
        flightDAO = new FlightDAO();
        hotelDAO = new HotelDAO();
        bookingDAO = new BookingDAO();

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Create New Booking", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        add(new JLabel("Customer:"), gbc);
        gbc.gridx = 1;
        customerComboBox = new JComboBox<>();
        populateCustomers();
        add(customerComboBox, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        add(new JLabel("Flight ID (Optional):"), gbc);
        gbc.gridx = 1;
        flightIdField = new JTextField(15);
        add(flightIdField, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        add(new JLabel("Hotel ID (Optional):"), gbc);
        gbc.gridx = 1;
        hotelIdField = new JTextField(15);
        add(hotelIdField, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        add(new JLabel("Booking Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        bookingDateField = new JTextField(15);
        bookingDateField.setText(LocalDate.now().toString()); // Default to today
        add(bookingDateField, gbc);

        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        createBookingButton = new JButton("Create Booking");
        add(createBookingButton, gbc);

        createBookingButton.addActionListener(e -> createBooking());
    }

    private void populateCustomers() {
        List<Customer> customers = customerDAO.getAllCustomers();
        DefaultComboBoxModel<Customer> model = new DefaultComboBoxModel<>();
        for (Customer customer : customers) {
            model.addElement(customer);
        }
        customerComboBox.setModel(model);
    }

    private void createBooking() {
        Customer selectedCustomer = (Customer) customerComboBox.getSelectedItem();
        if (selectedCustomer == null) {
            JOptionPane.showMessageDialog(this, "Please select a customer.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int customerId = selectedCustomer.getCustomerId();

        Integer flightId = null;
        String flightIdText = flightIdField.getText().trim();
        if (!flightIdText.isEmpty()) {
            try {
                flightId = Integer.parseInt(flightIdText);
                Flight flight = flightDAO.getFlightById(flightId);
                if (flight == null) {
                    JOptionPane.showMessageDialog(this, "Flight with ID " + flightId + " not found.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid Flight ID. Must be a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        Integer hotelId = null;
        String hotelIdText = hotelIdField.getText().trim();
        if (!hotelIdText.isEmpty()) {
            try {
                hotelId = Integer.parseInt(hotelIdText);
                final int finalHotelId = hotelId;
                Hotel hotel = hotelDAO.searchHotels(null).stream().filter(h -> h.getHotelId() == finalHotelId).findFirst().orElse(null);
                if (hotel == null) {
                    JOptionPane.showMessageDialog(this, "Hotel with ID " + hotelId + " not found.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid Hotel ID. Must be a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (flightId == null && hotelId == null) {
            JOptionPane.showMessageDialog(this, "Please provide either a Flight ID or a Hotel ID (or both).", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate bookingDate;
        String dateString = bookingDateField.getText().trim();
        try {
            bookingDate = LocalDate.parse(dateString);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid Booking Date format. Please use YYYY-MM-DD.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Booking booking = new Booking(customerId, flightId, hotelId, bookingDate);
        bookingDAO.addBooking(booking);

        if (booking.getBookingId() > 0) {
            JOptionPane.showMessageDialog(this, "Booking created successfully! ID: " + booking.getBookingId(), "Success", JOptionPane.INFORMATION_MESSAGE);
            flightIdField.setText("");
            hotelIdField.setText("");
            bookingDateField.setText(LocalDate.now().toString());
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create booking. Check console for details.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}