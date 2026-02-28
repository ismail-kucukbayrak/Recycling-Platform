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

public class YetkiliGirisEkrani extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    private JTable table;

    public YetkiliGirisEkrani() {
        setTitle("Yetkili Paneli");
        setSize(750, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(girisPaneli(), "GIRIS");
        mainPanel.add(anaPaneli(), "ANA");
        mainPanel.add(listePaneli(), "LISTE");

        add(mainPanel);
        cardLayout.show(mainPanel, "GIRIS");
    }

    // ---------------- GİRİŞ PANELİ ----------------
    private JPanel girisPaneli() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        Dimension fieldSize = new Dimension(250, 30);

        JLabel lblTelefon = new JLabel("Telefon:");
        JLabel lblSifre = new JLabel("Şifre:");

        JTextField txtTelefon = new JTextField();
        JPasswordField txtSifre = new JPasswordField();

        txtTelefon.setMaximumSize(fieldSize);
        txtSifre.setMaximumSize(fieldSize);

        JButton btnGiris = new JButton("GİRİŞ YAP");
        btnGiris.setMaximumSize(new Dimension(150, 35));

        lblTelefon.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSifre.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtTelefon.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtSifre.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnGiris.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(lblTelefon);
        panel.add(txtTelefon);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(lblSifre);
        panel.add(txtSifre);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));
        panel.add(btnGiris);

        btnGiris.addActionListener(e -> {
            try (Connection conn = DBConnection.getConnection();
                 CallableStatement cs =
                         conn.prepareCall("SELECT yetkili_giris(?, ?)")) {

                cs.setLong(1, Long.parseLong(txtTelefon.getText()));
                cs.setString(2, new String(txtSifre.getPassword()));

                ResultSet rs = cs.executeQuery();
                rs.next();

                if (rs.getBoolean(1)) {
                    cardLayout.show(mainPanel, "ANA");
                } else {
                    JOptionPane.showMessageDialog(this, "Hatalı giriş");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        return panel;
    }

    // ---------------- ANA PANEL ----------------
    private JPanel anaPaneli() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JButton btnDepo = new JButton("Depo");
        JButton btnRandevular = new JButton("Randevular");
        JButton btnToplam = new JButton("Bu Ay Eklenen Toplam Atık");
        JButton btnEkleyenler = new JButton("Bu Ay Atık Ekleyenler");
        JButton btnMahalle = new JButton("Mahalle Sakini Görüntüle");
        JButton btnSifirla = new JButton("Aylık Atık Kaydı Sıfırla");

        panel.add(btnDepo);
        panel.add(btnRandevular);
        panel.add(btnToplam);
        panel.add(btnEkleyenler);
        panel.add(btnMahalle);
        panel.add(btnSifirla);

        btnDepo.addActionListener(e ->
                tabloyuDoldur(
                        "SELECT * FROM depo_kayitlarini_getir()",
                        new String[]{"Atık ID", "Atık Türü", "Miktar"}
                ));

        btnRandevular.addActionListener(e ->
                tabloyuDoldur(
                        "SELECT * FROM bugunun_randevulari",
                        new String[]{"ID", "Telefon", "Firma", "Atık ID", "Atık", "Miktar", "Zaman"}
                ));

        btnToplam.addActionListener(e ->
                tabloyuDoldur(
                        "SELECT * FROM aylik_toplam_atik_raporu()",
                        new String[]{"Toplam Karton", "Toplam Cam", "Toplam Elektronik"}
                ));

        btnEkleyenler.addActionListener(e ->
                tabloyuDoldur(
                        "SELECT * FROM bu_ay_atik_ekleyen_mahalle_sakinleri()",
                        new String[]{"Telefon", "İsim", "Soyisim"}
                ));

        btnMahalle.addActionListener(e -> {
            String isim = JOptionPane.showInputDialog(this, "Mahalle sakini ismi:");
            if (isim == null || isim.trim().isEmpty()) return;
            mahalleSakiniIsmeGoreGetir(isim.trim());
        });

        btnSifirla.addActionListener(e -> {
            try (Connection conn = DBConnection.getConnection();
                 CallableStatement cs =
                         conn.prepareCall("SELECT aylik_atiklari_sifirla()")) {

                cs.execute();
                JOptionPane.showMessageDialog(this, "Aylık atık kayıtları sıfırlandı.");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        return panel;
    }

    // ---------------- LİSTE PANELİ ----------------
    private JPanel listePaneli() {
        JPanel panel = new JPanel(new BorderLayout());

        table = new JTable();
        table.setRowHeight(28);
        table.setDefaultEditor(Object.class, null);

        JScrollPane scrollPane = new JScrollPane(table);

        JButton btnGeri = new JButton("Geri");
        btnGeri.setPreferredSize(new Dimension(120, 35));
        btnGeri.addActionListener(e ->
                cardLayout.show(mainPanel, "ANA"));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.add(btnGeri);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                if (row < 0 || col < 0) return;

                if (table.getColumnCount() == 4 && col == 3) {
                    long telefon = ((Number) table.getValueAt(row, 0)).longValue();

                    int onay = JOptionPane.showConfirmDialog(
                            YetkiliGirisEkrani.this,
                            "Bu mahalle sakinini silmek istiyor musunuz?",
                            "Onay",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (onay == JOptionPane.YES_OPTION) {
                        mahalleSakiniSil(telefon, row);
                    }
                }
            }
        });

        return panel;
    }

    // ---------------- TABLO DOLDUR ----------------
    private void tabloyuDoldur(String sql, String[] kolonlar) {

        DefaultTableModel model = new DefaultTableModel(kolonlar, 0) {
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

                if (kolonlar.length == 3 && kolonlar[0].equals("Atık ID")) {
                    model.addRow(new Object[]{
                            rs.getInt("atik_id"),
                            rs.getString("atik_ismi"),
                            rs.getInt("miktar(kg)")
                    });
                } else if (kolonlar.length == 7) {
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
                    Object[] row = new Object[kolonlar.length];
                    for (int i = 0; i < kolonlar.length; i++) {
                        row[i] = rs.getObject(i + 1);
                    }
                    model.addRow(row);
                }
            }

            cardLayout.show(mainPanel, "LISTE");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // ---------------- DELETE ----------------
    private void mahalleSakiniSil(long telefon, int rowIndex) {
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs =
                     conn.prepareCall("SELECT mahalle_sakini_sil(?)")) {

            cs.setLong(1, telefon);
            cs.execute();

            ((DefaultTableModel) table.getModel()).removeRow(rowIndex);
            JOptionPane.showMessageDialog(this, "Kayıt silindi.");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // ------------ MAHALLE SAKİNİ GÖRÜNTÜLE (BUTONLU) ------------
    private void mahalleSakiniIsmeGoreGetir(String isim) {

        String[] kolonlar = {"Telefon", "İsim", "Soyisim", "Sil"};
        DefaultTableModel model = new DefaultTableModel(kolonlar, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table.setModel(model);
        table.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs =
                     conn.prepareCall("SELECT * FROM mahalle_sakini_isme_gore_getir(?)")) {

            cs.setString(1, isim);
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getLong("telefon"),
                        rs.getString("isim"),
                        rs.getString("soyisim"),
                        "Sil"
                });
            }

            cardLayout.show(mainPanel, "LISTE");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // ------------ BUTTON RENDERER ------------
    static class ButtonRenderer extends JButton implements TableCellRenderer {

        public ButtonRenderer() {
            setText("Sil");
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
