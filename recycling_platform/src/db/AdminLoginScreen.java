package db;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;

public class AdminLoginScreen extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    private JTable table;

    public AdminLoginScreen() {
        setTitle("Admin Panel");
        setSize(750, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(loginPanel(), "LOGIN");
        mainPanel.add(mainPanelContent(), "MAIN");
        mainPanel.add(listPanel(), "LIST");

        add(mainPanel);
        cardLayout.show(mainPanel, "LOGIN");
    }

    // ---------------- LOGIN PANEL ----------------
    private JPanel loginPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        Dimension fieldSize = new Dimension(250, 30);

        JLabel lblPhone = new JLabel("Phone:");
        JLabel lblPassword = new JLabel("Password:");

        JTextField txtPhone = new JTextField();
        JPasswordField txtPassword = new JPasswordField();

        txtPhone.setMaximumSize(fieldSize);
        txtPassword.setMaximumSize(fieldSize);

        JButton btnLogin = new JButton("LOG IN");
        btnLogin.setMaximumSize(new Dimension(150, 35));

        lblPhone.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblPassword.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtPhone.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtPassword.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(lblPhone);
        panel.add(txtPhone);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(lblPassword);
        panel.add(txtPassword);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));
        panel.add(btnLogin);

        btnLogin.addActionListener(e -> {
            try (Connection conn = DBConnection.getConnection();
                 CallableStatement cs =
                         conn.prepareCall("SELECT admin_login(?, ?)")) {

                cs.setLong(1, Long.parseLong(txtPhone.getText()));
                cs.setString(2, new String(txtPassword.getPassword()));

                ResultSet rs = cs.executeQuery();
                rs.next();

                if (rs.getBoolean(1)) {
                    cardLayout.show(mainPanel, "MAIN");
                } else {
                    JOptionPane.showMessageDialog(this, "Login failed");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        return panel;
    }

    // ---------------- MAIN PANEL ----------------
    private JPanel mainPanelContent() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JButton btnWarehouse = new JButton("Warehouse");
        JButton btnAppointments = new JButton("Appointments");
        JButton btnTotal = new JButton("Total Waste Added This Month");
        JButton btnContributors = new JButton("Residents Who Added Waste This Month");
        JButton btnResidents = new JButton("View Neighborhood Resident");
        JButton btnReset = new JButton("Reset Monthly Waste Records");

        panel.add(btnWarehouse);
        panel.add(btnAppointments);
        panel.add(btnTotal);
        panel.add(btnContributors);
        panel.add(btnResidents);
        panel.add(btnReset);

        btnWarehouse.addActionListener(e ->
                fillTable(
                        "SELECT * FROM get_warehouse_records()",
                        new String[]{"Waste ID", "Waste Type", "Amount"}
                ));

        btnAppointments.addActionListener(e ->
                fillTable(
                        "SELECT * FROM todays_appointments",
                        new String[]{"ID", "Phone", "Company", "Waste ID", "Waste", "Amount", "Time"}
                ));

        btnTotal.addActionListener(e ->
                fillTable(
                        "SELECT * FROM monthly_total_waste_report()",
                        new String[]{"Total Plastic", "Total Glass", "Total Electronic"}
                ));

        btnContributors.addActionListener(e ->
                fillTable(
                        "SELECT * FROM neighborhood_residents_who_added_waste_this_month()",
                        new String[]{"Phone", "Name", "Surname"}
                ));

        btnResidents.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Neighborhood resident name:");
            if (name == null || name.trim().isEmpty()) return;
            getNeighborhoodResidentByName(name.trim());
        });

        btnReset.addActionListener(e -> {
            try (Connection conn = DBConnection.getConnection();
                 CallableStatement cs =
                         conn.prepareCall("SELECT reset_monthly_waste()")) {

                cs.execute();
                JOptionPane.showMessageDialog(this, "Monthly waste records have been reset.");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        return panel;
    }

    // ---------------- LIST PANEL ----------------
    private JPanel listPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        table = new JTable();
        table.setRowHeight(28);
        table.setDefaultEditor(Object.class, null);

        JScrollPane scrollPane = new JScrollPane(table);

        JButton btnBack = new JButton("Back");
        btnBack.setPreferredSize(new Dimension(120, 35));
        btnBack.addActionListener(e ->
                cardLayout.show(mainPanel, "MAIN"));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.add(btnBack);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                if (row < 0 || col < 0) return;

                if (table.getColumnCount() == 4 && col == 3) {
                    long phone = ((Number) table.getValueAt(row, 0)).longValue();

                    int confirm = JOptionPane.showConfirmDialog(
                            AdminLoginScreen.this,
                            "Do you want to delete this neighborhood resident?",
                            "Confirm",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (confirm == JOptionPane.YES_OPTION) {
                        deleteNeighborhoodResident(phone, row);
                    }
                }
            }
        });

        return panel;
    }

    // ---------------- FILL TABLE ----------------
    private void fillTable(String sql, String[] columns) {

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table.setModel(model);

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {

                if (columns.length == 3 && columns[0].equals("Waste ID")) {
                    model.addRow(new Object[]{
                            rs.getInt("waste_id"),
                            rs.getString("waste_name"),
                            rs.getInt("amount(kg)")
                    });
                } else if (columns.length == 7) {
                    model.addRow(new Object[]{
                            rs.getInt(1),
                            rs.getObject(2),
                            rs.getObject(3),
                            rs.getObject(4),
                            rs.getObject(5),
                            rs.getObject(6),
                            rs.getObject(7)
                    });
                } else {
                    Object[] row = new Object[columns.length];
                    for (int i = 0; i < columns.length; i++) {
                        row[i] = rs.getObject(i + 1);
                    }
                    model.addRow(row);
                }
            }

            cardLayout.show(mainPanel, "LIST");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // ---------------- DELETE ----------------
    private void deleteNeighborhoodResident(long phone, int rowIndex) {
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs =
                     conn.prepareCall("SELECT delete_neighborhood_resident(?)")) {

            cs.setLong(1, phone);
            cs.execute();

            ((DefaultTableModel) table.getModel()).removeRow(rowIndex);
            JOptionPane.showMessageDialog(this, "Record deleted.");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // ------------ VIEW NEIGHBORHOOD RESIDENT (WITH BUTTON) ------------
    private void getNeighborhoodResidentByName(String name) {

        String[] columns = {"Phone", "Name", "Surname", "Delete"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table.setModel(model);
        table.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs =
                     conn.prepareCall("SELECT * FROM get_neighborhood_resident_by_name(?)")) {

            cs.setString(1, name);
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getLong("phone"),
                        rs.getString("name"),
                        rs.getString("surname"),
                        "Delete"
                });
            }

            cardLayout.show(mainPanel, "LIST");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // ------------ BUTTON RENDERER ------------
    static class ButtonRenderer extends JButton implements TableCellRenderer {

        public ButtonRenderer() {
            setText("Delete");
            setFocusPainted(false);
            setMargin(new Insets(2, 6, 2, 6));
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            return this;
        }
    }
}
