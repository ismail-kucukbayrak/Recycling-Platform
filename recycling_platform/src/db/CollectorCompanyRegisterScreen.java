package db;

import javax.swing.*;
import java.awt.*;
import java.sql.CallableStatement;
import java.sql.Connection;

public class CollectorCompanyRegisterScreen extends JFrame {

    public CollectorCompanyRegisterScreen() {
        setTitle("Collector Company Registration");
        setSize(350, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        Dimension fieldSize = new Dimension(250, 30);

        JLabel lblPhone = new JLabel("Phone:");
        JTextField txtPhone = new JTextField();
        txtPhone.setMaximumSize(fieldSize);

        JLabel lblName = new JLabel("Company Name:");
        JTextField txtName = new JTextField();
        txtName.setMaximumSize(fieldSize);

        JLabel lblPassword = new JLabel("Password:");
        JPasswordField txtPassword = new JPasswordField();
        txtPassword.setMaximumSize(fieldSize);

        // 🔹 CENTER ALIGNMENT
        lblPhone.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblPassword.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtPhone.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtName.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtPassword.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnRegister = new JButton("REGISTER");
        btnRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegister.setMaximumSize(new Dimension(150, 35));

        panel.add(lblPhone);
        panel.add(txtPhone);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(lblName);
        panel.add(txtName);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(lblPassword);
        panel.add(txtPassword);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        panel.add(btnRegister);

        add(panel);

        btnRegister.addActionListener(e -> {
            try (Connection conn = DBConnection.getConnection();
                 CallableStatement cs =
                         conn.prepareCall("SELECT register_collector_company(?, ?, ?)")) {

                cs.setLong(1, Long.parseLong(txtPhone.getText()));
                cs.setString(2, new String(txtPassword.getPassword()));
                cs.setString(3, txtName.getText());

                cs.execute();
                JOptionPane.showMessageDialog(this, "Registration successful.");
                dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });
    }
}
