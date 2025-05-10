package com.travelapp.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.SQLException;

import com.travelapp.util.DBConnection;

public class MainApp extends JFrame {

    public MainApp() {
        setTitle("Travel Booking Application");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Handle close manually
        setLocationRelativeTo(null); // Center the window

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Add Customer", new AddCustomerPanel());
        tabbedPane.addTab("Manage Customers", new CustomerManagementPanel());
        tabbedPane.addTab("Search Flights", new FlightSearchPanel());
        tabbedPane.addTab("Search Hotels", new HotelSearchPanel());
        tabbedPane.addTab("Create Booking", new AddBookingPanel());
        tabbedPane.addTab("Search Bookings", new SearchBookingPanel());

        add(tabbedPane, BorderLayout.CENTER);

        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showOptionDialog(
                        null, "Are you sure you want to exit?", "Exit Application",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                        null, null, null);
                if (confirm == 0) {
                    
                    dispose();
                    System.exit(0);
                }
            }
        });
    }
}