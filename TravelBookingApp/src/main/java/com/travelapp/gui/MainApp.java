package com.travelapp.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainApp extends JFrame {

    private CustomerManagementPanel customerManagementPanel;
    private FlightSearchPanel flightSearchPanel; // Refers to the updated FlightSearchPanel
    private HotelSearchPanel hotelSearchPanel;   // Refers to the updated HotelSearchPanel
    private SearchBookingPanel searchBookingPanel; // Refers to the updated SearchBookingPanel

    public MainApp() {
        setTitle("Travel Booking Application");
        setSize(1000, 700); // Increased size to accommodate more content
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        customerManagementPanel = new CustomerManagementPanel();
        flightSearchPanel = new FlightSearchPanel();
        hotelSearchPanel = new HotelSearchPanel();
        searchBookingPanel = new SearchBookingPanel();


        tabbedPane.addTab("Add Customer", new AddCustomerPanel());
        tabbedPane.addTab("Manage Customers", customerManagementPanel);
        tabbedPane.addTab("Flights", flightSearchPanel); // Renamed tab for clarity
        tabbedPane.addTab("Hotels", hotelSearchPanel);   // Renamed tab for clarity
        tabbedPane.addTab("Create Booking", new AddBookingPanel());
        tabbedPane.addTab("Bookings", searchBookingPanel); // Renamed tab for clarity

        add(tabbedPane, BorderLayout.CENTER);

        // Add a listener to the tabbed pane to refresh data when a management tab is selected
        tabbedPane.addChangeListener(e -> {
            Component selectedComponent = tabbedPane.getSelectedComponent();
            if (selectedComponent == customerManagementPanel) {
                customerManagementPanel.populateCustomerTable();
            } else if (selectedComponent == flightSearchPanel) {
                flightSearchPanel.populateFlightTable(null, null, null); // Refresh all flights
            } else if (selectedComponent == hotelSearchPanel) {
                hotelSearchPanel.populateHotelTable(null); // Refresh all hotels
            } else if (selectedComponent == searchBookingPanel) {
                searchBookingPanel.populateBookingTable(null, null, null); // Refresh all bookings
            }
        });

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