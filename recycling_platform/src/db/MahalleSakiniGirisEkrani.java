package db;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;

public class MahalleSakiniGirisEkrani extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    private JPanel urunEklePanel;
    private JPanel raporPanel;

    private JTable raporTable;

    private long girisYapanTelefon;

    public MahalleSakiniGirisEkrani() {
        setTitle("Mahalle Sakini");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(girisPaneli(), "GIRIS");
        mainPanel.add(anaPaneli(), "ANA");

        urunEklePanel = urunEklePaneli();
        raporPanel = raporPaneli();

        mainPanel.add(urunEklePanel, "URUN_EKLE");
        mainPanel.add(raporPanel, "RAPOR");

        add(mainPanel);
        cardLayout.show(mainPanel, "GIRIS");
    }

    // ---------------- GİRİŞ PANELİ ----------------
    private JPanel girisPaneli() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel lblTelefon = new JLabel("Telefon:");
        JLabel lblSifre = new JLabel("Şifre:");

        JTextField txtTelefon = new JTextField();
        JPasswordField txtSifre = new JPasswordField();

        Dimension fieldSize = new Dimension(250, 30);
        txtTelefon.setMaximumSize(fieldSize);
        txtSifre.setMaximumSize(fieldSize);

        lblTelefon.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSifre.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtTelefon.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtSifre.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnGiris = new JButton("GİRİŞ YAP");
        JButton btnKayitOl = new JButton("Kayıt Ol");
        btnKayitOl.setFont(new Font("Arial", Font.PLAIN, 11));

        btnGiris.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnKayitOl.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(lblTelefon);
        panel.add(txtTelefon);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(lblSifre);
        panel.add(txtSifre);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(btnGiris);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(btnKayitOl);

        btnGiris.addActionListener(e -> {
            try (Connection conn = DBConnection.getConnection();
                 CallableStatement cs =
                         conn.prepareCall("SELECT mahalle_sakini_giris(?, ?)")) {

                long telefon = Long.parseLong(txtTelefon.getText());
                cs.setLong(1, telefon);
                cs.setString(2, new String(txtSifre.getPassword()));

                ResultSet rs = cs.executeQuery();
                rs.next();

                if (rs.getBoolean(1)) {
                    girisYapanTelefon = telefon;
                    cardLayout.show(mainPanel, "ANA");
                } else {
                    JOptionPane.showMessageDialog(this, "Telefon veya şifre hatalı.");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        btnKayitOl.addActionListener(e ->
                new MahalleSakiniKayitEkrani().setVisible(true));

        return panel;
    }

    // ---------------- ANA PANEL ----------------
    private JPanel anaPaneli() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        JButton btnUrunEkle = new JButton("Ürün Ekle");
        JButton btnRapor = new JButton("Rapor");

        Dimension size = new Dimension(200, 35);
        btnUrunEkle.setMaximumSize(size);
        btnRapor.setMaximumSize(size);

        btnUrunEkle.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRapor.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(btnUrunEkle);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(btnRapor);

        btnUrunEkle.addActionListener(e ->
                cardLayout.show(mainPanel, "URUN_EKLE"));

        btnRapor.addActionListener(e ->
                raporTablosunuDoldur());

        return panel;
    }

    // ---------------- ÜRÜN EKLE ----------------
    private JPanel urunEklePaneli() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel lblAtikTuru = new JLabel("Atık Türü:");
        JLabel lblMiktar = new JLabel("Miktar:");

        JComboBox<String> cmbTur =
                new JComboBox<>(new String[]{"karton", "cam", "elektronik"});
        JTextField txtMiktar = new JTextField();

        Dimension fieldSize = new Dimension(200, 30);
        cmbTur.setMaximumSize(fieldSize);
        txtMiktar.setMaximumSize(fieldSize);

        lblAtikTuru.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblMiktar.setAlignmentX(Component.CENTER_ALIGNMENT);
        cmbTur.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtMiktar.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnEkle = new JButton("Ekle");
        JButton btnGeri = new JButton("Geri");

        btnEkle.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnGeri.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(lblAtikTuru);
        panel.add(cmbTur);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(lblMiktar);
        panel.add(txtMiktar);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(btnEkle);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(btnGeri);

        btnEkle.addActionListener(e -> {
            try (Connection conn = DBConnection.getConnection();
                 CallableStatement cs =
                         conn.prepareCall("SELECT mahalle_sakini_atik_ekle(?, ?, ?)")) {

                cs.setLong(1, girisYapanTelefon);
                cs.setString(2, cmbTur.getSelectedItem().toString());
                cs.setInt(3, Integer.parseInt(txtMiktar.getText()));
                cs.execute();

                JOptionPane.showMessageDialog(this, "Atık eklendi.");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        btnGeri.addActionListener(e ->
                cardLayout.show(mainPanel, "ANA"));

        return panel;
    }

    // ---------------- RAPOR PANELİ ----------------
    private JPanel raporPaneli() {
        JPanel panel = new JPanel(new BorderLayout());

        raporTable = new JTable(new DefaultTableModel(
                new Object[]{"Ürün", "Miktar"}, 0));

        panel.add(new JScrollPane(raporTable), BorderLayout.CENTER);

        JButton btnGeri = new JButton("Geri");
        btnGeri.addActionListener(e ->
                cardLayout.show(mainPanel, "ANA"));

        panel.add(btnGeri, BorderLayout.SOUTH);
        return panel;
    }

    // ---------------- RAPOR DOLDUR ----------------
    private void raporTablosunuDoldur() {
        DefaultTableModel model = (DefaultTableModel) raporTable.getModel();
        model.setRowCount(0);

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs =
                     conn.prepareCall("SELECT * FROM mahalle_sakini_raporu(?)")) {

            cs.setLong(1, girisYapanTelefon);
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("urun"),
                        rs.getInt("miktar")
                });
            }

            cardLayout.show(mainPanel, "RAPOR");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}
