package com.travelapp;

import com.travelapp.gui.MainApp;
import com.travelapp.util.DatabaseInitializer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JOptionPane;

public class Main {
    public static void main(String[] args) {

        
    	  // ---Initialize the database ---
        try {
            DatabaseInitializer.initializeDatabase();
            System.out.println("Database initialization successful. Proceeding with application launch.");
        } catch (RuntimeException e) {
            System.err.println("Application startup aborted due to database initialization failure.");
            JOptionPane.showMessageDialog(null,
                                          "Failed to initialize database. Please check your MySQL server and credentials.\nError: " + e.getMessage(),
                                          "Database Error",
                                          JOptionPane.ERROR_MESSAGE);
            System.exit(1); 
        }
    	
    	try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

       
        SwingUtilities.invokeLater(() -> {
            MainApp app = new MainApp();
            app.setVisible(true);
        });
    }
}