package db;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class ToplayiciFirmaGirisEkrani extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    private JPanel randevuPanel;
    private JTable depoTable;

    private JComboBox<AtikItem> cmbAtik;
    private long girisYapanTelefon;

    public ToplayiciFirmaGirisEkrani() {
        setTitle("Toplayıcı Firma");
        setSize(500, 380);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(girisPaneli(), "GIRIS");
        mainPanel.add(anaPaneli(), "ANA");

        randevuPanel = randevuPaneli();
        mainPanel.add(randevuPanel, "RANDEVU");

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
                         conn.prepareCall("SELECT toplayici_firma_giris(?, ?)")) {

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
                new ToplayiciFirmaKayitEkrani().setVisible(true));

        return panel;
    }

    // ---------------- ANA PANEL ----------------
    private JPanel anaPaneli() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(60, 60, 60, 60));

        JButton btnRandevu = new JButton("Randevu Oluştur");
        btnRandevu.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRandevu.setMaximumSize(new Dimension(220, 40));

        panel.add(btnRandevu);

        btnRandevu.addActionListener(e -> {
            depoTablosunuDoldur();
            comboBoxDoldur();
            cardLayout.show(mainPanel, "RANDEVU");
        });

        return panel;
    }

    // ---------------- RANDEVU PANELİ ----------------
    private JPanel randevuPaneli() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        depoTable = new JTable(new DefaultTableModel(
                new Object[]{"Atık ID", "Atık Türü", "Miktar(kg)"}, 0));

        JScrollPane scrollPane = new JScrollPane(depoTable);

        JPanel bottom = new JPanel(new GridLayout(2, 4, 10, 10));

        cmbAtik = new JComboBox<>();
        JTextField txtMiktar = new JTextField();
        JTextField txtTarih = new JTextField("2026-01-01");

        JButton btnOlustur = new JButton("Oluştur");
        JButton btnGeri = new JButton("Geri");

        bottom.add(new JLabel("Atık Türü:"));
        bottom.add(cmbAtik);
        bottom.add(new JLabel("Miktar:"));
        bottom.add(txtMiktar);
        bottom.add(new JLabel("Tarih (YYYY-MM-DD):"));
        bottom.add(txtTarih);
        bottom.add(btnOlustur);
        bottom.add(btnGeri);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        btnOlustur.addActionListener(e -> {
            AtikItem secilen = (AtikItem) cmbAtik.getSelectedItem();
            randevuOlustur(
                    secilen.id,
                    txtMiktar.getText(),
                    txtTarih.getText()
            );
        });

        btnGeri.addActionListener(e ->
                cardLayout.show(mainPanel, "ANA"));

        return panel;
    }

    // ---------------- DEPO TABLOSU ----------------
    private void depoTablosunuDoldur() {
        DefaultTableModel model = (DefaultTableModel) depoTable.getModel();
        model.setRowCount(0);

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs =
                     conn.prepareCall("SELECT * FROM depo_kayitlarini_getir()");
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("atik_id"),
                        rs.getString("atik_ismi"),
                        rs.getInt("miktar(kg)")
                });
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // ---------------- COMBOBOX DOLDUR ----------------
    private void comboBoxDoldur() {
        cmbAtik.removeAllItems();

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs =
                     conn.prepareCall("SELECT * FROM depo_kayitlarini_getir()");
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                cmbAtik.addItem(
                        new AtikItem(
                                rs.getInt("atik_id"),
                                rs.getString("atik_ismi")
                        )
                );
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // ---------------- RANDEVU OLUŞTUR ----------------
    private void randevuOlustur(int atikId, String miktar, String tarih) {
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs =
                     conn.prepareCall("SELECT randevu_olustur(?, ?, ?, ?)")) {

            String duzeltilmisTarih = tarih.trim() + " 00:00:00";

            cs.setLong(1, girisYapanTelefon);
            cs.setInt(2, atikId);
            cs.setInt(3, Integer.parseInt(miktar));
            cs.setTimestamp(4, Timestamp.valueOf(duzeltilmisTarih));

            cs.execute();
            JOptionPane.showMessageDialog(this, "Randevu oluşturuldu.");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ((org.postgresql.util.PSQLException) ex)
                            .getServerErrorMessage()
                            .getMessage()
            );
        }
    }

    // ---------------- YARDIMCI SINIF ----------------
    private static class AtikItem {
        int id;
        String isim;

        AtikItem(int id, String isim) {
            this.id = id;
            this.isim = isim;
        }

        @Override
        public String toString() {
            return isim;
        }
    }
}
