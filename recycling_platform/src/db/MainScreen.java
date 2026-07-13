package db;

import javax.swing.*;
import java.awt.*;

public class MainScreen extends JFrame {

    public MainScreen() {
        setTitle("Recycling Platform");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JButton btnResident = new JButton("Neighborhood Resident");
        JButton btnCollectorCompany = new JButton("Collector Company");
        JButton btnAdmin = new JButton("Admin");

        Dimension buttonSize = new Dimension(180, 35);
        btnResident.setMaximumSize(buttonSize);
        btnCollectorCompany.setMaximumSize(buttonSize);
        btnAdmin.setMaximumSize(buttonSize);

        btnResident.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCollectorCompany.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAdmin.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(btnResident);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(btnCollectorCompany);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(btnAdmin);

        add(panel);

        // 🔹 NAVIGATION
        btnResident.addActionListener(e ->
                new NeighborhoodResidentLoginScreen().setVisible(true));

        btnCollectorCompany.addActionListener(e ->
                new CollectorCompanyLoginScreen().setVisible(true));

        btnAdmin.addActionListener(e ->
                new AdminLoginScreen().setVisible(true));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainScreen().setVisible(true));
    }
}
