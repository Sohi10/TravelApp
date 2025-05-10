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
    private JButton editButton;
    private JButton deleteButton;
    private JTextField editNameField;
    private JTextField editEmailField;
    private JTextField editPhoneField;
    private JButton updateButton; // Button to commit changes after editing

    private CustomerDAO customerDAO;
    private int selectedCustomerId = -1; // To store the ID of the customer being edited

    public CustomerManagementPanel() {
        customerDAO = new CustomerDAO();
        setLayout(new BorderLayout(10, 10)); // Add some gaps

        // --- Top Panel: Title and Controls ---
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Manage Customers", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        topPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        refreshButton = new JButton("Refresh Customers");
        editButton = new JButton("Edit Selected");
        deleteButton = new JButton("Delete Selected");
        controlPanel.add(refreshButton);
        controlPanel.add(editButton);
        controlPanel.add(deleteButton);
        topPanel.add(controlPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // --- Center Panel: Customer Table ---
        String[] columnNames = {"Customer ID", "Name", "Email", "Phone"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        customerTable = new JTable(tableModel);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(customerTable);
        add(scrollPane, BorderLayout.CENTER);

        // --- Bottom Panel: Edit Form ---
        JPanel editFormPanel = new JPanel(new GridBagLayout());
        editFormPanel.setBorder(BorderFactory.createTitledBorder("Edit Customer Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        editFormPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        editNameField = new JTextField(20);
        editFormPanel.add(editNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        editFormPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        editEmailField = new JTextField(20);
        editFormPanel.add(editEmailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        editFormPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        editPhoneField = new JTextField(20);
        editFormPanel.add(editPhoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        updateButton = new JButton("Update Customer");
        updateButton.setEnabled(false); // Disable until a customer is selected for edit
        editFormPanel.add(updateButton, gbc);

        add(editFormPanel, BorderLayout.SOUTH);

        // --- Action Listeners ---
        refreshButton.addActionListener(e -> populateCustomerTable());
        editButton.addActionListener(e -> loadSelectedCustomerForEdit());
        deleteButton.addActionListener(e -> deleteSelectedCustomer());
        updateButton.addActionListener(e -> updateCustomer());

        // Populate table initially
        populateCustomerTable();
    }

    public void populateCustomerTable() {
        tableModel.setRowCount(0); // Clear existing rows
        List<Customer> customers = customerDAO.getAllCustomers();
        for (Customer customer : customers) {
            tableModel.addRow(new Object[]{
                    customer.getCustomerId(),
                    customer.getName(),
                    customer.getEmail(),
                    customer.getPhone()
            });
        }
        // Disable edit/delete/update buttons if no rows or no selection
        boolean hasRows = tableModel.getRowCount() > 0;
        editButton.setEnabled(hasRows);
        deleteButton.setEnabled(hasRows);
        updateButton.setEnabled(false); // Always disable update until selected for edit
        clearEditFields();
    }

    private void loadSelectedCustomerForEdit() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow >= 0) {
            selectedCustomerId = (int) tableModel.getValueAt(selectedRow, 0);
            String name = (String) tableModel.getValueAt(selectedRow, 1);
            String email = (String) tableModel.getValueAt(selectedRow, 2);
            String phone = (String) tableModel.getValueAt(selectedRow, 3);

            editNameField.setText(name);
            editEmailField.setText(email);
            editPhoneField.setText(phone);
            updateButton.setEnabled(true); // Enable update button
        } else {
            JOptionPane.showMessageDialog(this, "Please select a customer from the table to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            clearEditFields();
        }
    }

    private void updateCustomer() {
        if (selectedCustomerId == -1) {
            JOptionPane.showMessageDialog(this, "No customer selected for update.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String name = editNameField.getText().trim();
        String email = editEmailField.getText().trim();
        String phone = editPhoneField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields for update.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Customer customer = new Customer(selectedCustomerId, name, email, phone);
        boolean success = customerDAO.updateCustomer(customer);

        if (success) {
            JOptionPane.showMessageDialog(this, "Customer updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            populateCustomerTable(); // Refresh table
            clearEditFields();
            selectedCustomerId = -1; // Reset selected ID
            updateButton.setEnabled(false); // Disable update button
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update customer. Check console for details.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow >= 0) {
            int customerId = (int) tableModel.getValueAt(selectedRow, 0);
            String customerName = (String) tableModel.getValueAt(selectedRow, 1);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete customer: " + customerName + " (ID: " + customerId + ")?\n" +
                    "Note: This will fail if there are associated bookings.",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = customerDAO.deleteCustomer(customerId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Customer deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    populateCustomerTable(); // Refresh table
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete customer. It might have existing bookings.\n" +
                                                        "Please delete associated bookings first.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a customer from the table to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void clearEditFields() {
        editNameField.setText("");
        editEmailField.setText("");
        editPhoneField.setText("");
        selectedCustomerId = -1;
    }
}