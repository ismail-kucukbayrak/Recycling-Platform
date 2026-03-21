package db;

import javax.swing.*;
import java.awt.*;

public class AnaEkran extends JFrame {

    public AnaEkran() {
        setTitle("Geri Dönüşüm Platformu");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JButton btnMahalleSakini = new JButton("Mahalle Sakini");
        JButton btnToplayiciFirma = new JButton("Toplayıcı Firma");
        JButton btnYetkili = new JButton("Yetkili");

        Dimension buttonSize = new Dimension(180, 35);
        btnMahalleSakini.setMaximumSize(buttonSize);
        btnToplayiciFirma.setMaximumSize(buttonSize);
        btnYetkili.setMaximumSize(buttonSize);

        btnMahalleSakini.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnToplayiciFirma.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnYetkili.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(btnMahalleSakini);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(btnToplayiciFirma);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(btnYetkili);

        add(panel);

        // 🔹 YÖNLENDİRMELER
        btnMahalleSakini.addActionListener(e ->
                new MahalleSakiniGirisEkrani().setVisible(true));

        btnToplayiciFirma.addActionListener(e ->
                new ToplayiciFirmaGirisEkrani().setVisible(true));

        btnYetkili.addActionListener(e ->
                new YetkiliGirisEkrani().setVisible(true));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AnaEkran().setVisible(true));
    }
}
