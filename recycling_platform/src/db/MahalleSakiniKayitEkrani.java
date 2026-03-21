package db;

import javax.swing.*;
import java.awt.*;
import java.sql.CallableStatement;
import java.sql.Connection;

public class MahalleSakiniKayitEkrani extends JFrame {

    public MahalleSakiniKayitEkrani() {
        setTitle("Mahalle Sakini Kayıt");
        setSize(350, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Ortak boyut
        Dimension fieldSize = new Dimension(250, 30);

        JLabel lblTelefon = new JLabel("Telefon:");
        JTextField txtTelefon = new JTextField();
        txtTelefon.setMaximumSize(fieldSize);

        JLabel lblIsim = new JLabel("İsim:");
        JTextField txtIsim = new JTextField();
        txtIsim.setMaximumSize(fieldSize);

        JLabel lblSoyisim = new JLabel("Soyisim:");
        JTextField txtSoyisim = new JTextField();
        txtSoyisim.setMaximumSize(fieldSize);

        JLabel lblSifre = new JLabel("Şifre:");
        JPasswordField txtSifre = new JPasswordField();
        txtSifre.setMaximumSize(fieldSize);

        // Ortala
        lblTelefon.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblIsim.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSoyisim.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSifre.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtTelefon.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtIsim.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtSoyisim.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtSifre.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnKayitOl = new JButton("KAYIT OL");
        btnKayitOl.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnKayitOl.setMaximumSize(new Dimension(150, 35));

        panel.add(lblTelefon);
        panel.add(txtTelefon);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(lblIsim);
        panel.add(txtIsim);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(lblSoyisim);
        panel.add(txtSoyisim);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(lblSifre);
        panel.add(txtSifre);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(btnKayitOl);

        add(panel);

        // JDBC: SADECE SQL FONKSİYONU ÇAĞRISI
        btnKayitOl.addActionListener(e -> {
            try (Connection conn = DBConnection.getConnection();
                 CallableStatement cs =
                         conn.prepareCall("SELECT mahalle_sakini_kayit_ol(?, ?, ?, ?)")) {

                cs.setLong(1, Long.parseLong(txtTelefon.getText()));
                cs.setString(2, new String(txtSifre.getPassword()));
                cs.setString(3, txtIsim.getText());
                cs.setString(4, txtSoyisim.getText());

                cs.execute();

                JOptionPane.showMessageDialog(this, "Kayıt başarılı.");
                dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });
    }
}
