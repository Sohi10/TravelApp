package com.travelapp.gui;

import com.travelapp.dao.HotelDAO;
import com.travelapp.model.Hotel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HotelSearchPanel extends JPanel {
    private JTextField searchCityField;
    private JButton searchButton;
    private JTable hotelTable;
    private DefaultTableModel tableModel;
    private HotelDAO hotelDAO;

    // For management section
    private JButton refreshButton;
    private JButton editButton;
    private JButton deleteButton;
    private JTextField editNameField;
    private JTextField editCityField;
    private JTextField editPricePerNightField;
    private JButton updateButton;
    private int selectedHotelId = -1; // ID of hotel being edited

    public HotelSearchPanel() {
        hotelDAO = new HotelDAO();
        setLayout(new BorderLayout(10, 10));

        // --- Top Panel: Title and Search Form ---
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Hotel Search & Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        topPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel searchInputPanel = new JPanel(new GridBagLayout());
        searchInputPanel.setBorder(BorderFactory.createTitledBorder("Search Hotels"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; searchInputPanel.add(new JLabel("City:"), gbc);
        gbc.gridx = 1; searchCityField = new JTextField(15); searchInputPanel.add(searchCityField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        searchButton = new JButton("Search Hotels");
        searchInputPanel.add(searchButton, gbc);
        topPanel.add(searchInputPanel, BorderLayout.CENTER);

        JPanel managementControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        refreshButton = new JButton("Refresh All Hotels");
        editButton = new JButton("Edit Selected");
        deleteButton = new JButton("Delete Selected");
        managementControlPanel.add(refreshButton);
        managementControlPanel.add(editButton);
        managementControlPanel.add(deleteButton);
        topPanel.add(managementControlPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // --- Center Panel: Results Table ---
        String[] columnNames = {"Hotel ID", "Name", "City", "Price Per Night"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        hotelTable = new JTable(tableModel);
        hotelTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(hotelTable);
        add(scrollPane, BorderLayout.CENTER);

        // --- Bottom Panel: Edit Form ---
        JPanel editFormPanel = new JPanel(new GridBagLayout());
        editFormPanel.setBorder(BorderFactory.createTitledBorder("Edit Hotel Details"));
        gbc = new GridBagConstraints(); // Reset GBC
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; editFormPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; editNameField = new JTextField(15); editFormPanel.add(editNameField, gbc);

        gbc.gridx = 2; gbc.gridy = 0; editFormPanel.add(new JLabel("City:"), gbc);
        gbc.gridx = 3; editCityField = new JTextField(15); editFormPanel.add(editCityField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; editFormPanel.add(new JLabel("Price/Night:"), gbc);
        gbc.gridx = 1; editPricePerNightField = new JTextField(10); editFormPanel.add(editPricePerNightField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        updateButton = new JButton("Update Hotel");
        updateButton.setEnabled(false);
        editFormPanel.add(updateButton, gbc);

        add(editFormPanel, BorderLayout.SOUTH);

        // --- Action Listeners ---
        searchButton.addActionListener(e -> searchHotels());
        refreshButton.addActionListener(e -> populateHotelTable(null)); // Refresh all
        editButton.addActionListener(e -> loadSelectedHotelForEdit());
        deleteButton.addActionListener(e -> deleteSelectedHotel());
        updateButton.addActionListener(e -> updateHotel());

        // Initial population of table with all hotels
        populateHotelTable(null);
    }

    private void searchHotels() {
        String city = searchCityField.getText().trim();
        populateHotelTable(city.isEmpty() ? null : city);
    }

    public void populateHotelTable(String city) {
        tableModel.setRowCount(0); // Clear existing rows
        List<Hotel> hotels = hotelDAO.searchHotels(city);

        if (hotels.isEmpty()) {
            // JOptionPane.showMessageDialog(this, "No hotels found matching your criteria.", "Search Results", JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (Hotel hotel : hotels) {
                tableModel.addRow(new Object[]{
                        hotel.getHotelId(),
                        hotel.getName(),
                        hotel.getCity(),
                        String.format("%.2f", hotel.getPricePerNight())
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

    private void loadSelectedHotelForEdit() {
        int selectedRow = hotelTable.getSelectedRow();
        if (selectedRow >= 0) {
            selectedHotelId = (int) tableModel.getValueAt(selectedRow, 0);
            editNameField.setText((String) tableModel.getValueAt(selectedRow, 1));
            editCityField.setText((String) tableModel.getValueAt(selectedRow, 2));
            editPricePerNightField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 3)));
            updateButton.setEnabled(true);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a hotel from the table to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            clearEditFields();
        }
    }

    private void updateHotel() {
        if (selectedHotelId == -1) {
            JOptionPane.showMessageDialog(this, "No hotel selected for update.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String name = editNameField.getText().trim();
        String city = editCityField.getText().trim();
        String priceStr = editPricePerNightField.getText().trim();

        if (name.isEmpty() || city.isEmpty() || priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields for update.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double pricePerNight;
        try {
            pricePerNight = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid price format. Must be a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Hotel hotel = new Hotel(selectedHotelId, name, city, pricePerNight);
        boolean success = hotelDAO.updateHotel(hotel);

        if (success) {
            JOptionPane.showMessageDialog(this, "Hotel updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            populateHotelTable(null); // Refresh table with all hotels
            clearEditFields();
            selectedHotelId = -1;
            updateButton.setEnabled(false);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update hotel. Check console for details.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedHotel() {
        int selectedRow = hotelTable.getSelectedRow();
        if (selectedRow >= 0) {
            int hotelId = (int) tableModel.getValueAt(selectedRow, 0);
            String hotelName = (String) tableModel.getValueAt(selectedRow, 1);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete hotel: " + hotelName + " (ID: " + hotelId + ")?\n" +
                    "Note: This will fail if there are associated bookings.",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = hotelDAO.deleteHotel(hotelId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Hotel deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    populateHotelTable(null); // Refresh table
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete hotel. It might have existing bookings.\n" +
                                                        "Please delete associated bookings first.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a hotel from the table to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void clearEditFields() {
        editNameField.setText("");
        editCityField.setText("");
        editPricePerNightField.setText("");
        selectedHotelId = -1;
    }
}