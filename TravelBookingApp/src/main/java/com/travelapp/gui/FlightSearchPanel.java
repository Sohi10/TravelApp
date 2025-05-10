package com.travelapp.gui;

import com.travelapp.dao.FlightDAO;
import com.travelapp.model.Flight;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class FlightSearchPanel extends JPanel {
    private JTextField originField;
    private JTextField destinationField;
    private JTextField departureDateField; // Format: YYYY-MM-DD
    private JButton searchButton;
    private JTable flightTable;
    private DefaultTableModel tableModel;
    private FlightDAO flightDAO;

    public FlightSearchPanel() {
        flightDAO = new FlightDAO();
        setLayout(new BorderLayout());

        // Search Panel
        JPanel searchInputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Search Flights", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        searchInputPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        searchInputPanel.add(new JLabel("Origin:"), gbc);
        gbc.gridx = 1;
        originField = new JTextField(15);
        searchInputPanel.add(originField, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        searchInputPanel.add(new JLabel("Destination:"), gbc);
        gbc.gridx = 1;
        destinationField = new JTextField(15);
        searchInputPanel.add(destinationField, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        searchInputPanel.add(new JLabel("Departure Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        departureDateField = new JTextField(15);
        searchInputPanel.add(departureDateField, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        searchButton = new JButton("Search Flights");
        searchInputPanel.add(searchButton, gbc);

        add(searchInputPanel, BorderLayout.NORTH);

        // Results Table
        String[] columnNames = {"Flight ID", "Origin", "Destination", "Departure Time", "Arrival Time", "Price"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        flightTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(flightTable);
        add(scrollPane, BorderLayout.CENTER);

        searchButton.addActionListener(e -> searchFlights());
    }

    private void searchFlights() {
        String origin = originField.getText().trim();
        String destination = destinationField.getText().trim();
        String dateString = departureDateField.getText().trim();

        LocalDateTime departureDate = null;
        if (!dateString.isEmpty()) {
            try {
                departureDate = LocalDateTime.parse(dateString + " 00:00:00", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        List<Flight> flights = flightDAO.searchFlights(
                origin.isEmpty() ? null : origin,
                destination.isEmpty() ? null : destination,
                departureDate
        );

        tableModel.setRowCount(0); // Clear existing rows
        if (flights.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No flights found matching your criteria.", "Search Results", JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (Flight flight : flights) {
                tableModel.addRow(new Object[]{
                        flight.getFlightId(),
                        flight.getOrigin(),
                        flight.getDestination(),
                        flight.getDepartureTime(),
                        flight.getArrivalTime(),
                        String.format("%.2f", flight.getPrice())
                });
            }
        }
    }
}