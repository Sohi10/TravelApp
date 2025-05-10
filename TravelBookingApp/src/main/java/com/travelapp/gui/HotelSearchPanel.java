package com.travelapp.gui;

import com.travelapp.dao.HotelDAO;
import com.travelapp.model.Hotel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HotelSearchPanel extends JPanel {
    private JTextField cityField;
    private JButton searchButton;
    private JTable hotelTable;
    private DefaultTableModel tableModel;
    private HotelDAO hotelDAO;

    public HotelSearchPanel() {
        hotelDAO = new HotelDAO();
        setLayout(new BorderLayout());

        // Search Panel
        JPanel searchInputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Search Hotels", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        searchInputPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        searchInputPanel.add(new JLabel("City:"), gbc);
        gbc.gridx = 1;
        cityField = new JTextField(15);
        searchInputPanel.add(cityField, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        searchButton = new JButton("Search Hotels");
        searchInputPanel.add(searchButton, gbc);

        add(searchInputPanel, BorderLayout.NORTH);

        // Results Table
        String[] columnNames = {"Hotel ID", "Name", "City", "Price Per Night"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        hotelTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(hotelTable);
        add(scrollPane, BorderLayout.CENTER);

        searchButton.addActionListener(e -> searchHotels());
    }

    private void searchHotels() {
        String city = cityField.getText().trim();

        List<Hotel> hotels = hotelDAO.searchHotels(city.isEmpty() ? null : city);

        tableModel.setRowCount(0); // Clear existing rows
        if (hotels.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hotels found matching your criteria.", "Search Results", JOptionPane.INFORMATION_MESSAGE);
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
    }
}