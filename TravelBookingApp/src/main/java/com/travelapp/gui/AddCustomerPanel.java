package com.travelapp.gui;

import com.travelapp.dao.CustomerDAO;
import com.travelapp.model.Customer;

import javax.swing.*;
import java.awt.*;

public class AddCustomerPanel extends JPanel {
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JButton addButton;
    private CustomerDAO customerDAO;

    public AddCustomerPanel() {
        customerDAO = new CustomerDAO();
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Add New Customer", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        add(nameField, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        add(emailField, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField(20);
        add(phoneField, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        addButton = new JButton("Add Customer");
        add(addButton, gbc);

        addButton.addActionListener(e -> addCustomer());
    }

    private void addCustomer() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Customer customer = new Customer(name, email, phone);
        customerDAO.addCustomer(customer);

        if (customer.getCustomerId() > 0) {
            JOptionPane.showMessageDialog(this, "Customer added successfully! ID: " + customer.getCustomerId(), "Success", JOptionPane.INFORMATION_MESSAGE);
            nameField.setText("");
            emailField.setText("");
            phoneField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add customer. Check console for details.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}