package db;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;

public class NeighborhoodResidentLoginScreen extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    private JPanel addWastePanel;
    private JPanel reportPanel;

    private JTable reportTable;

    private long loggedInPhone;

    public NeighborhoodResidentLoginScreen() {
        setTitle("Neighborhood Resident");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(loginPanel(), "LOGIN");
        mainPanel.add(mainPanelContent(), "MAIN");

        addWastePanel = addWastePanel();
        reportPanel = reportPanel();

        mainPanel.add(addWastePanel, "ADD_WASTE");
        mainPanel.add(reportPanel, "REPORT");

        add(mainPanel);
        cardLayout.show(mainPanel, "LOGIN");
    }

    // ---------------- LOGIN PANEL ----------------
    private JPanel loginPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel lblPhone = new JLabel("Phone:");
        JLabel lblPassword = new JLabel("Password:");

        JTextField txtPhone = new JTextField();
        JPasswordField txtPassword = new JPasswordField();

        Dimension fieldSize = new Dimension(250, 30);
        txtPhone.setMaximumSize(fieldSize);
        txtPassword.setMaximumSize(fieldSize);

        lblPhone.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblPassword.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtPhone.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtPassword.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnLogin = new JButton("LOG IN");
        JButton btnRegister = new JButton("Register");
        btnRegister.setFont(new Font("Arial", Font.PLAIN, 11));

        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegister.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(lblPhone);
        panel.add(txtPhone);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(lblPassword);
        panel.add(txtPassword);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(btnLogin);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(btnRegister);

        btnLogin.addActionListener(e -> {
            try (Connection conn = DBConnection.getConnection();
                 CallableStatement cs =
                         conn.prepareCall("SELECT neighborhood_resident_login(?, ?)")) {

                long phone = Long.parseLong(txtPhone.getText());
                cs.setLong(1, phone);
                cs.setString(2, new String(txtPassword.getPassword()));

                ResultSet rs = cs.executeQuery();
                rs.next();

                if (rs.getBoolean(1)) {
                    loggedInPhone = phone;
                    cardLayout.show(mainPanel, "MAIN");
                } else {
                    JOptionPane.showMessageDialog(this, "Incorrect phone or password.");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        btnRegister.addActionListener(e ->
                new NeighborhoodResidentRegisterScreen().setVisible(true));

        return panel;
    }

    // ---------------- MAIN PANEL ----------------
    private JPanel mainPanelContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        JButton btnAddWaste = new JButton("Add Waste");
        JButton btnReport = new JButton("Report");

        Dimension size = new Dimension(200, 35);
        btnAddWaste.setMaximumSize(size);
        btnReport.setMaximumSize(size);

        btnAddWaste.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnReport.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(btnAddWaste);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(btnReport);

        btnAddWaste.addActionListener(e ->
                cardLayout.show(mainPanel, "ADD_WASTE"));

        btnReport.addActionListener(e ->
                fillReportTable());

        return panel;
    }

    // ---------------- ADD WASTE ----------------
    private JPanel addWastePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel lblWasteType = new JLabel("Waste Type:");
        JLabel lblAmount = new JLabel("Amount:");

        JComboBox<String> cmbType =
                new JComboBox<>(new String[]{"plastic", "glass", "electronic"});
        JTextField txtAmount = new JTextField();

        Dimension fieldSize = new Dimension(200, 30);
        cmbType.setMaximumSize(fieldSize);
        txtAmount.setMaximumSize(fieldSize);

        lblWasteType.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblAmount.setAlignmentX(Component.CENTER_ALIGNMENT);
        cmbType.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtAmount.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnAdd = new JButton("Add");
        JButton btnBack = new JButton("Back");

        btnAdd.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBack.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(lblWasteType);
        panel.add(cmbType);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(lblAmount);
        panel.add(txtAmount);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(btnAdd);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(btnBack);

        btnAdd.addActionListener(e -> {
            try (Connection conn = DBConnection.getConnection();
                 CallableStatement cs =
                         conn.prepareCall("SELECT add_waste_for_neighborhood_resident(?, ?, ?)")) {

                cs.setLong(1, loggedInPhone);
                cs.setString(2, cmbType.getSelectedItem().toString());
                cs.setInt(3, Integer.parseInt(txtAmount.getText()));
                cs.execute();

                JOptionPane.showMessageDialog(this, "Waste added.");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        btnBack.addActionListener(e ->
                cardLayout.show(mainPanel, "MAIN"));

        return panel;
    }

    // ---------------- REPORT PANEL ----------------
    private JPanel reportPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        reportTable = new JTable(new DefaultTableModel(
                new Object[]{"Product", "Amount"}, 0));

        panel.add(new JScrollPane(reportTable), BorderLayout.CENTER);

        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(e ->
                cardLayout.show(mainPanel, "MAIN"));

        panel.add(btnBack, BorderLayout.SOUTH);
        return panel;
    }

    // ---------------- FILL REPORT ----------------
    private void fillReportTable() {
        DefaultTableModel model = (DefaultTableModel) reportTable.getModel();
        model.setRowCount(0);

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs =
                     conn.prepareCall("SELECT * FROM neighborhood_resident_report(?)")) {

            cs.setLong(1, loggedInPhone);
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("product"),
                        rs.getInt("amount")
                });
            }

            cardLayout.show(mainPanel, "REPORT");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}
