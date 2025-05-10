package com.travelapp.gui;

import com.travelapp.dao.FlightDAO;
import com.travelapp.model.Flight;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class FlightSearchPanel extends JPanel {
    private JTextField searchOriginField;
    private JTextField searchDestinationField;
    private JTextField searchDepartureDateField; // Format: YYYY-MM-DD
    private JButton searchButton;
    private JTable flightTable;
    private DefaultTableModel tableModel;
    private FlightDAO flightDAO;

    // For management section
    private JButton refreshButton;
    private JButton editButton;
    private JButton deleteButton;
    private JTextField editOriginField;
    private JTextField editDestinationField;
    private JTextField editDepartureTimeField; // YYYY-MM-DD HH:MM:SS
    private JTextField editArrivalTimeField;   // YYYY-MM-DD HH:MM:SS
    private JTextField editPriceField;
    private JButton updateButton;
    private int selectedFlightId = -1; // ID of flight being edited

    public FlightSearchPanel() {
        flightDAO = new FlightDAO();
        setLayout(new BorderLayout(10, 10));

        // --- Top Panel: Title and Search Form ---
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Flight Search & Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        topPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel searchInputPanel = new JPanel(new GridBagLayout());
        searchInputPanel.setBorder(BorderFactory.createTitledBorder("Search Flights"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; searchInputPanel.add(new JLabel("Origin:"), gbc);
        gbc.gridx = 1; searchOriginField = new JTextField(15); searchInputPanel.add(searchOriginField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; searchInputPanel.add(new JLabel("Destination:"), gbc);
        gbc.gridx = 1; searchDestinationField = new JTextField(15); searchInputPanel.add(searchDestinationField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; searchInputPanel.add(new JLabel("Departure Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; searchDepartureDateField = new JTextField(15); searchInputPanel.add(searchDepartureDateField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        searchButton = new JButton("Search Flights");
        searchInputPanel.add(searchButton, gbc);
        topPanel.add(searchInputPanel, BorderLayout.CENTER);

        JPanel managementControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        refreshButton = new JButton("Refresh All Flights");
        editButton = new JButton("Edit Selected");
        deleteButton = new JButton("Delete Selected");
        managementControlPanel.add(refreshButton);
        managementControlPanel.add(editButton);
        managementControlPanel.add(deleteButton);
        topPanel.add(managementControlPanel, BorderLayout.SOUTH);


        add(topPanel, BorderLayout.NORTH);

        // --- Center Panel: Results Table ---
        String[] columnNames = {"Flight ID", "Origin", "Destination", "Departure Time", "Arrival Time", "Price"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        flightTable = new JTable(tableModel);
        flightTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(flightTable);
        add(scrollPane, BorderLayout.CENTER);

        // --- Bottom Panel: Edit Form ---
        JPanel editFormPanel = new JPanel(new GridBagLayout());
        editFormPanel.setBorder(BorderFactory.createTitledBorder("Edit Flight Details"));
        gbc = new GridBagConstraints(); // Reset GBC
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; editFormPanel.add(new JLabel("Origin:"), gbc);
        gbc.gridx = 1; editOriginField = new JTextField(15); editFormPanel.add(editOriginField, gbc);

        gbc.gridx = 2; gbc.gridy = 0; editFormPanel.add(new JLabel("Destination:"), gbc);
        gbc.gridx = 3; editDestinationField = new JTextField(15); editFormPanel.add(editDestinationField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; editFormPanel.add(new JLabel("Departure (YYYY-MM-DD HH:MM:SS):"), gbc);
        gbc.gridx = 1; editDepartureTimeField = new JTextField(20); editFormPanel.add(editDepartureTimeField, gbc);

        gbc.gridx = 2; gbc.gridy = 1; editFormPanel.add(new JLabel("Arrival (YYYY-MM-DD HH:MM:SS):"), gbc);
        gbc.gridx = 3; editArrivalTimeField = new JTextField(20); editFormPanel.add(editArrivalTimeField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; editFormPanel.add(new JLabel("Price:"), gbc);
        gbc.gridx = 1; editPriceField = new JTextField(10); editFormPanel.add(editPriceField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        updateButton = new JButton("Update Flight");
        updateButton.setEnabled(false);
        editFormPanel.add(updateButton, gbc);

        add(editFormPanel, BorderLayout.SOUTH);

        // --- Action Listeners ---
        searchButton.addActionListener(e -> searchFlights());
        refreshButton.addActionListener(e -> populateFlightTable(null, null, null)); // Refresh all
        editButton.addActionListener(e -> loadSelectedFlightForEdit());
        deleteButton.addActionListener(e -> deleteSelectedFlight());
        updateButton.addActionListener(e -> updateFlight());

        // Initial population of table with all flights
        populateFlightTable(null, null, null);
    }

    private void searchFlights() {
        String origin = searchOriginField.getText().trim();
        String destination = searchDestinationField.getText().trim();
        String dateString = searchDepartureDateField.getText().trim();

        LocalDateTime departureDate = null;
        if (!dateString.isEmpty()) {
            try {
                departureDate = LocalDateTime.parse(dateString + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid departure date format. Please use YYYY-MM-DD.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        populateFlightTable(origin.isEmpty() ? null : origin, destination.isEmpty() ? null : destination, departureDate);
    }

    public void populateFlightTable(String origin, String destination, LocalDateTime departureDate) {
        tableModel.setRowCount(0); // Clear existing rows
        List<Flight> flights;
        if (origin == null && destination == null && departureDate == null) {
            flights = flightDAO.getAllFlights(); // Get all if no search criteria
        } else {
            flights = flightDAO.searchFlights(origin, destination, departureDate);
        }

        if (flights.isEmpty()) {
            // JOptionPane.showMessageDialog(this, "No flights found matching your criteria.", "Search Results", JOptionPane.INFORMATION_MESSAGE);
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (Flight flight : flights) {
                tableModel.addRow(new Object[]{
                        flight.getFlightId(),
                        flight.getOrigin(),
                        flight.getDestination(),
                        flight.getDepartureTime().format(formatter),
                        flight.getArrivalTime().format(formatter),
                        String.format("%.2f", flight.getPrice())
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

    private void loadSelectedFlightForEdit() {
        int selectedRow = flightTable.getSelectedRow();
        if (selectedRow >= 0) {
            selectedFlightId = (int) tableModel.getValueAt(selectedRow, 0);
            editOriginField.setText((String) tableModel.getValueAt(selectedRow, 1));
            editDestinationField.setText((String) tableModel.getValueAt(selectedRow, 2));
            editDepartureTimeField.setText((String) tableModel.getValueAt(selectedRow, 3));
            editArrivalTimeField.setText((String) tableModel.getValueAt(selectedRow, 4));
            editPriceField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 5)));
            updateButton.setEnabled(true);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a flight from the table to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            clearEditFields();
        }
    }

    private void updateFlight() {
        if (selectedFlightId == -1) {
            JOptionPane.showMessageDialog(this, "No flight selected for update.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String origin = editOriginField.getText().trim();
        String destination = editDestinationField.getText().trim();
        String departureTimeStr = editDepartureTimeField.getText().trim();
        String arrivalTimeStr = editArrivalTimeField.getText().trim();
        String priceStr = editPriceField.getText().trim();

        if (origin.isEmpty() || destination.isEmpty() || departureTimeStr.isEmpty() || arrivalTimeStr.isEmpty() || priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields for update.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDateTime departureTime;
        LocalDateTime arrivalTime;
        double price;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try {
            departureTime = LocalDateTime.parse(departureTimeStr, formatter);
            arrivalTime = LocalDateTime.parse(arrivalTimeStr, formatter);
            price = Double.parseDouble(priceStr);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date/time format. Use YYYY-MM-DD HH:MM:SS.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid price format. Must be a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Flight flight = new Flight(selectedFlightId, origin, destination, departureTime, arrivalTime, price);
        boolean success = flightDAO.updateFlight(flight);

        if (success) {
            JOptionPane.showMessageDialog(this, "Flight updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            populateFlightTable(null, null, null); // Refresh table with all flights
            clearEditFields();
            selectedFlightId = -1;
            updateButton.setEnabled(false);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update flight. Check console for details.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedFlight() {
        int selectedRow = flightTable.getSelectedRow();
        if (selectedRow >= 0) {
            int flightId = (int) tableModel.getValueAt(selectedRow, 0);
            String flightDetails = (String) tableModel.getValueAt(selectedRow, 1) + " to " + tableModel.getValueAt(selectedRow, 2);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete flight: " + flightDetails + " (ID: " + flightId + ")?\n" +
                    "Note: This will fail if there are associated bookings.",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = flightDAO.deleteFlight(flightId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Flight deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    populateFlightTable(null, null, null); // Refresh table
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete flight. It might have existing bookings.\n" +
                                                        "Please delete associated bookings first.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a flight from the table to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void clearEditFields() {
        editOriginField.setText("");
        editDestinationField.setText("");
        editDepartureTimeField.setText("");
        editArrivalTimeField.setText("");
        editPriceField.setText("");
        selectedFlightId = -1;
    }
}