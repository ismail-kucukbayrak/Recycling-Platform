package db;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class CollectorCompanyLoginScreen extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    private JPanel appointmentPanel;
    private JTable warehouseTable;

    private JComboBox<WasteItem> cmbWaste;
    private long loggedInPhone;

    public CollectorCompanyLoginScreen() {
        setTitle("Collector Company");
        setSize(500, 380);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(loginPanel(), "LOGIN");
        mainPanel.add(mainPanelContent(), "MAIN");

        appointmentPanel = appointmentPanel();
        mainPanel.add(appointmentPanel, "APPOINTMENT");

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
                         conn.prepareCall("SELECT collector_company_login(?, ?)")) {

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
                new CollectorCompanyRegisterScreen().setVisible(true));

        return panel;
    }

    // ---------------- MAIN PANEL ----------------
    private JPanel mainPanelContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(60, 60, 60, 60));

        JButton btnAppointment = new JButton("Create Appointment");
        btnAppointment.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAppointment.setMaximumSize(new Dimension(220, 40));

        panel.add(btnAppointment);

        btnAppointment.addActionListener(e -> {
            fillWarehouseTable();
            fillComboBox();
            cardLayout.show(mainPanel, "APPOINTMENT");
        });

        return panel;
    }

    // ---------------- APPOINTMENT PANEL ----------------
    private JPanel appointmentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        warehouseTable = new JTable(new DefaultTableModel(
                new Object[]{"Waste ID", "Waste Type", "Amount(kg)"}, 0));

        JScrollPane scrollPane = new JScrollPane(warehouseTable);

        JPanel bottom = new JPanel(new GridLayout(2, 4, 10, 10));

        cmbWaste = new JComboBox<>();
        JTextField txtAmount = new JTextField();
        JTextField txtDate = new JTextField("2026-01-01");

        JButton btnCreate = new JButton("Create");
        JButton btnBack = new JButton("Back");

        bottom.add(new JLabel("Waste Type:"));
        bottom.add(cmbWaste);
        bottom.add(new JLabel("Amount:"));
        bottom.add(txtAmount);
        bottom.add(new JLabel("Date (YYYY-MM-DD):"));
        bottom.add(txtDate);
        bottom.add(btnCreate);
        bottom.add(btnBack);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        btnCreate.addActionListener(e -> {
            WasteItem selected = (WasteItem) cmbWaste.getSelectedItem();
            createAppointment(
                    selected.id,
                    txtAmount.getText(),
                    txtDate.getText()
            );
        });

        btnBack.addActionListener(e ->
                cardLayout.show(mainPanel, "MAIN"));

        return panel;
    }

    // ---------------- FILL WAREHOUSE TABLE ----------------
    private void fillWarehouseTable() {
        DefaultTableModel model = (DefaultTableModel) warehouseTable.getModel();
        model.setRowCount(0);

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs =
                     conn.prepareCall("SELECT * FROM get_warehouse_records()");
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("waste_id"),
                        rs.getString("waste_name"),
                        rs.getInt("amount(kg)")
                });
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // ---------------- FILL COMBOBOX ----------------
    private void fillComboBox() {
        cmbWaste.removeAllItems();

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs =
                     conn.prepareCall("SELECT * FROM get_warehouse_records()");
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                cmbWaste.addItem(
                        new WasteItem(
                                rs.getInt("waste_id"),
                                rs.getString("waste_name")
                        )
                );
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // ---------------- CREATE APPOINTMENT ----------------
    private void createAppointment(int wasteId, String amount, String date) {
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs =
                     conn.prepareCall("SELECT create_appointment(?, ?, ?, ?)")) {

            String normalizedDate = date.trim() + " 00:00:00";

            cs.setLong(1, loggedInPhone);
            cs.setInt(2, wasteId);
            cs.setInt(3, Integer.parseInt(amount));
            cs.setTimestamp(4, Timestamp.valueOf(normalizedDate));

            cs.execute();
            JOptionPane.showMessageDialog(this, "Appointment created.");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ((org.postgresql.util.PSQLException) ex)
                            .getServerErrorMessage()
                            .getMessage()
            );
        }
    }

    // ---------------- HELPER CLASS ----------------
    private static class WasteItem {
        int id;
        String name;

        WasteItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
