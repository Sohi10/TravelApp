package com.travelapp.gui;

import com.travelapp.dao.CustomerDAO;
import com.travelapp.model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerManagementPanel extends JPanel {
    private JTable customerTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;
    private CustomerDAO customerDAO;

    public CustomerManagementPanel() {
        customerDAO = new CustomerDAO();
        setLayout(new BorderLayout());

        
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel("Existing Customers", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.NORTH); // Title at the top of the topPanel

        refreshButton = new JButton("Refresh Customers");
        controlPanel.add(refreshButton);
        topPanel.add(controlPanel, BorderLayout.SOUTH); // Control panel below the title

        add(topPanel, BorderLayout.NORTH);


        
        String[] columnNames = {"Customer ID", "Name", "Email", "Phone"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        customerTable = new JTable(tableModel);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow single row selection

        JScrollPane scrollPane = new JScrollPane(customerTable);
        add(scrollPane, BorderLayout.CENTER);

        // Add action listener for the refresh button
        refreshButton.addActionListener(e -> populateCustomerTable());

        // Populate table initially when the panel is loaded
        populateCustomerTable();
    }

    public void populateCustomerTable() {
        tableModel.setRowCount(0); // Clear existing rows
        List<Customer> customers = customerDAO.getAllCustomers();
        if (customers.isEmpty()) {
            
            System.out.println("No customers found in the database.");
        } else {
            for (Customer customer : customers) {
                tableModel.addRow(new Object[]{
                        customer.getCustomerId(),
                        customer.getName(),
                        customer.getEmail(),
                        customer.getPhone()
                });
            }
        }
    }

  
    public Customer getSelectedCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow >= 0) {
            int customerId = (int) tableModel.getValueAt(selectedRow, 0);
            String name = (String) tableModel.getValueAt(selectedRow, 1);
            String email = (String) tableModel.getValueAt(selectedRow, 2);
            String phone = (String) tableModel.getValueAt(selectedRow, 3);
            return new Customer(customerId, name, email, phone);
        }
        return null;
    }
}