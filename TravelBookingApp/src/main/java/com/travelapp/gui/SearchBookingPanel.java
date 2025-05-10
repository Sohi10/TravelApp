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
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class SearchBookingPanel extends JPanel {
    private JTextField searchCustomerIdField;
    private JTextField searchFlightIdField;
    private JTextField searchHotelIdField;
    private JButton searchButton;
    private JTable bookingTable;
    private DefaultTableModel tableModel;

    private BookingDAO bookingDAO;
    private CustomerDAO customerDAO;
    private FlightDAO flightDAO;
    private HotelDAO hotelDAO;

    // For management section
    private JButton refreshButton;
    private JButton editButton;
    private JButton deleteButton;
    private JComboBox<Customer> editCustomerComboBox;
    private JTextField editFlightIdField;
    private JTextField editHotelIdField;
    private JTextField editBookingDateField; // YYYY-MM-DD
    private JButton updateButton;
    private int selectedBookingId = -1; // ID of booking being edited

    public SearchBookingPanel() {
        bookingDAO = new BookingDAO();
        customerDAO = new CustomerDAO();
        flightDAO = new FlightDAO();
        hotelDAO = new HotelDAO();

        setLayout(new BorderLayout(10, 10));

        // --- Top Panel: Title and Search Form ---
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Booking Search & Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        topPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel searchInputPanel = new JPanel(new GridBagLayout());
        searchInputPanel.setBorder(BorderFactory.createTitledBorder("Search Bookings"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; searchInputPanel.add(new JLabel("Customer ID:"), gbc);
        gbc.gridx = 1; searchCustomerIdField = new JTextField(15); searchInputPanel.add(searchCustomerIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; searchInputPanel.add(new JLabel("Flight ID:"), gbc);
        gbc.gridx = 1; searchFlightIdField = new JTextField(15); searchInputPanel.add(searchFlightIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; searchInputPanel.add(new JLabel("Hotel ID:"), gbc);
        gbc.gridx = 1; searchHotelIdField = new JTextField(15); searchInputPanel.add(searchHotelIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        searchButton = new JButton("Search Bookings");
        searchInputPanel.add(searchButton, gbc);
        topPanel.add(searchInputPanel, BorderLayout.CENTER);

        JPanel managementControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        refreshButton = new JButton("Refresh All Bookings");
        editButton = new JButton("Edit Selected");
        deleteButton = new JButton("Delete Selected");
        managementControlPanel.add(refreshButton);
        managementControlPanel.add(editButton);
        managementControlPanel.add(deleteButton);
        topPanel.add(managementControlPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // --- Center Panel: Results Table ---
        String[] columnNames = {"Booking ID", "Customer Name", "Flight Details", "Hotel Details", "Booking Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookingTable = new JTable(tableModel);
        bookingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(bookingTable);
        add(scrollPane, BorderLayout.CENTER);

        // --- Bottom Panel: Edit Form ---
        JPanel editFormPanel = new JPanel(new GridBagLayout());
        editFormPanel.setBorder(BorderFactory.createTitledBorder("Edit Booking Details"));
        gbc = new GridBagConstraints(); // Reset GBC
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; editFormPanel.add(new JLabel("Customer:"), gbc);
        gbc.gridx = 1; editCustomerComboBox = new JComboBox<>(); populateCustomersComboBox(editCustomerComboBox); editFormPanel.add(editCustomerComboBox, gbc);

        gbc.gridx = 2; gbc.gridy = 0; editFormPanel.add(new JLabel("Flight ID (Optional):"), gbc);
        gbc.gridx = 3; editFlightIdField = new JTextField(10); editFormPanel.add(editFlightIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; editFormPanel.add(new JLabel("Hotel ID (Optional):"), gbc);
        gbc.gridx = 1; editHotelIdField = new JTextField(10); editFormPanel.add(editHotelIdField, gbc);

        gbc.gridx = 2; gbc.gridy = 1; editFormPanel.add(new JLabel("Booking Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 3; editBookingDateField = new JTextField(15); editFormPanel.add(editBookingDateField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        updateButton = new JButton("Update Booking");
        updateButton.setEnabled(false);
        editFormPanel.add(updateButton, gbc);

        add(editFormPanel, BorderLayout.SOUTH);

        // --- Action Listeners ---
        searchButton.addActionListener(e -> searchBookings());
        refreshButton.addActionListener(e -> populateBookingTable(null, null, null)); // Refresh all
        editButton.addActionListener(e -> loadSelectedBookingForEdit());
        deleteButton.addActionListener(e -> deleteSelectedBooking());
        updateButton.addActionListener(e -> updateBooking());

        // Initial population of table with all bookings
        populateBookingTable(null, null, null);
    }

    private void populateCustomersComboBox(JComboBox<Customer> comboBox) {
        List<Customer> customers = customerDAO.getAllCustomers();
        DefaultComboBoxModel<Customer> model = new DefaultComboBoxModel<>();
        for (Customer customer : customers) {
            model.addElement(customer);
        }
        comboBox.setModel(model);
    }

    private void searchBookings() {
        Integer customerId = null;
        if (!searchCustomerIdField.getText().trim().isEmpty()) {
            try {
                customerId = Integer.parseInt(searchCustomerIdField.getText().trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid Customer ID for search. Must be a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        Integer flightId = null;
        if (!searchFlightIdField.getText().trim().isEmpty()) {
            try {
                flightId = Integer.parseInt(searchFlightIdField.getText().trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid Flight ID for search. Must be a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        Integer hotelId = null;
        if (!searchHotelIdField.getText().trim().isEmpty()) {
            try {
                hotelId = Integer.parseInt(searchHotelIdField.getText().trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid Hotel ID for search. Must be a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        populateBookingTable(customerId, flightId, hotelId);
    }

    public void populateBookingTable(Integer customerId, Integer flightId, Integer hotelId) {
        tableModel.setRowCount(0); // Clear existing rows
        List<Booking> bookings = bookingDAO.searchBookings(customerId, flightId, hotelId);

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
                    Hotel hotel = hotelDAO.getHotelById(booking.getHotelId()); // Using getHotelById
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
        // Disable buttons if no rows or no selection
        boolean hasRows = tableModel.getRowCount() > 0;
        editButton.setEnabled(hasRows);
        deleteButton.setEnabled(hasRows);
        updateButton.setEnabled(false);
        clearEditFields();
    }

    private void loadSelectedBookingForEdit() {
        int selectedRow = bookingTable.getSelectedRow();
        if (selectedRow >= 0) {
            selectedBookingId = (int) tableModel.getValueAt(selectedRow, 0);
            Booking booking = bookingDAO.getBookingById(selectedBookingId); // Fetch full booking object

            if (booking != null) {
                // Set customer combobox
                List<Customer> customers = customerDAO.getAllCustomers();
                for (int i = 0; i < customers.size(); i++) {
                    if (customers.get(i).getCustomerId() == booking.getCustomerId()) {
                        editCustomerComboBox.setSelectedIndex(i);
                        break;
                    }
                }
                editFlightIdField.setText(booking.getFlightId() != null ? String.valueOf(booking.getFlightId()) : "");
                editHotelIdField.setText(booking.getHotelId() != null ? String.valueOf(booking.getHotelId()) : "");
                editBookingDateField.setText(booking.getBookingDate().toString());
                updateButton.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to retrieve booking details for editing.", "Error", JOptionPane.ERROR_MESSAGE);
                clearEditFields();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a booking from the table to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            clearEditFields();
        }
    }

    private void updateBooking() {
        if (selectedBookingId == -1) {
            JOptionPane.showMessageDialog(this, "No booking selected for update.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Customer selectedCustomer = (Customer) editCustomerComboBox.getSelectedItem();
        if (selectedCustomer == null) {
            JOptionPane.showMessageDialog(this, "Please select a customer for the booking.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int customerId = selectedCustomer.getCustomerId();

        Integer flightId = null;
        String flightIdText = editFlightIdField.getText().trim();
        if (!flightIdText.isEmpty()) {
            try {
                flightId = Integer.parseInt(flightIdText);
                // Optional: Verify flight exists
                if (flightDAO.getFlightById(flightId) == null) {
                     JOptionPane.showMessageDialog(this, "Flight ID " + flightId + " does not exist.", "Input Error", JOptionPane.ERROR_MESSAGE);
                     return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid Flight ID. Must be a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        Integer hotelId = null;
        String hotelIdText = editHotelIdField.getText().trim();
        if (!hotelIdText.isEmpty()) {
            try {
                hotelId = Integer.parseInt(hotelIdText);
                // Optional: Verify hotel exists
                if (hotelDAO.getHotelById(hotelId) == null) {
                    JOptionPane.showMessageDialog(this, "Hotel ID " + hotelId + " does not exist.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid Hotel ID. Must be a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (flightId == null && hotelId == null) {
            JOptionPane.showMessageDialog(this, "A booking must have at least a Flight ID or a Hotel ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate bookingDate;
        String dateString = editBookingDateField.getText().trim();
        try {
            bookingDate = LocalDate.parse(dateString);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid Booking Date format. Please use YYYY-MM-DD.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Booking booking = new Booking(selectedBookingId, customerId, flightId, hotelId, bookingDate);
        boolean success = bookingDAO.updateBooking(booking);

        if (success) {
            JOptionPane.showMessageDialog(this, "Booking updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            populateBookingTable(null, null, null); // Refresh table
            clearEditFields();
            selectedBookingId = -1;
            updateButton.setEnabled(false);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update booking. Check console for details.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedBooking() {
        int selectedRow = bookingTable.getSelectedRow();
        if (selectedRow >= 0) {
            int bookingId = (int) tableModel.getValueAt(selectedRow, 0);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete booking (ID: " + bookingId + ")?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = bookingDAO.deleteBooking(bookingId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Booking deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    populateBookingTable(null, null, null); // Refresh table
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete booking. Check console for details.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a booking from the table to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void clearEditFields() {
        //editCustomerComboBox.setSelectedIndex(-1); // Or set to null/first element
        editFlightIdField.setText("");
        editHotelIdField.setText("");
        editBookingDateField.setText("");
        selectedBookingId = -1;
    }
}