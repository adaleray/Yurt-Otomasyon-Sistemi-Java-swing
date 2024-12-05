package main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;


public class GirisKontrol extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField kodField;
    private JLabel resultLabel;
    private JTable table;
    private DefaultTableModel tableModel;

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/yurt";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "123";

   

    public GirisKontrol() {
        setResizable(false);
        setIconImage(Toolkit.getDefaultToolkit().getImage(GirisKontrol.class.getResource("/main/logg.png")));
        setTitle("Giriş Çıkış Kontrol");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 804, 424);
        setLocationRelativeTo(null);

        contentPane = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon imageIcon = new ImageIcon(getClass().getResource("indir.jpg"));
                Image image = imageIcon.getImage();
                g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(400, 400);
            }
        };
        contentPane.setForeground(SystemColor.inactiveCaptionText);
        contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel kodLabel = new JLabel("Giriş Kodu:");
        kodLabel.setFont(new Font("Arial", Font.BOLD, 14));
        kodLabel.setBounds(347, 0, 100, 25);
        contentPane.add(kodLabel);

        kodField = new JTextField();
        kodField.setFont(new Font("Arial", Font.PLAIN, 14));
        kodField.setBounds(278, 31, 200, 25);
        contentPane.add(kodField);
        kodField.setColumns(10);

        JButton girisButton = new JButton("Yurt Giriş");
        girisButton.setBackground(Color.GREEN);
        girisButton.setFont(new Font("Arial", Font.BOLD, 14));
        girisButton.setBounds(103, 55, 117, 104);
        contentPane.add(girisButton);

        JButton cikisButton = new JButton("Yurt Çıkış");
        cikisButton.setBackground(Color.RED);
        cikisButton.setFont(new Font("Arial", Font.BOLD, 14));
        cikisButton.setBounds(567, 55, 117, 104);
        contentPane.add(cikisButton);
        JButton bilgiButton = new JButton("?");
        bilgiButton.setToolTipText("Nasıl Giriş Yapılır?");
        bilgiButton.setFont(new Font("Arial", Font.PLAIN, 12));
        bilgiButton.setBounds(488, 31, 47, 25);
        contentPane.add(bilgiButton);

        bilgiButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(contentPane,
                        "Giriş yapmak için öğrenciye ait 6 haneli giriş kodunu girin ve 'Yurt Giriş' butonuna tıklayın.",
                        "Nasıl Giriş Yapılır?",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        resultLabel = new JLabel("");
        resultLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        resultLabel.setBounds(50, 150, 500, 25);
        contentPane.add(resultLabel);

        tableModel = new DefaultTableModel(new Object[]{"İsim", "Soyisim", "Giriş Tarihi", "Çıkış Tarihi", "Durum"}, 0);
        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(20);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(50, 188, 700, 197);
        contentPane.add(scrollPane);

        JButton geributton = new JButton("Geri");
        geributton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                İlkMenu ilkMenu = new İlkMenu();
                ilkMenu.setVisible(true);
                dispose();
            }
        });
        geributton.setBackground(Color.ORANGE);
        geributton.setBounds(347, 134, 81, 21);
        contentPane.add(geributton);

        girisButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                girisYap();
                tabloGuncelle();
            }
        });

        cikisButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cikisYap();
                tabloGuncelle();
            }
        });

        tabloGuncelle();
    }

    private void girisYap() {
        String kod = kodField.getText().trim();

        if (kod.isEmpty() || kod.length() != 6) {
            JOptionPane.showMessageDialog(this, "Lütfen 6 haneli geçerli bir giriş kodu girin.", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String checkSql = "SELECT * FROM yurtgircik WHERE ogr_tc = (SELECT ogr_tc FROM t_ogrenci WHERE substr(ogr_tc, 1, 3) || substr(ogr_tc, 9, 3) = ?) AND cikis_tarih IS NULL";
            try (PreparedStatement checkPstmt = conn.prepareStatement(checkSql)) {
                checkPstmt.setString(1, kod);
                try (ResultSet checkRs = checkPstmt.executeQuery()) {
                    if (checkRs.next()) {
                        JOptionPane.showMessageDialog(this, "Bu kullanıcı zaten giriş yapmış ve çıkış yapmamış.", "Hata", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }

            String sql = "SELECT ogr_tc, ogr_isim, ogr_soyad FROM t_ogrenci WHERE substr(ogr_tc, 1, 3) || substr(ogr_tc, 9, 3) = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, kod);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        String tc = rs.getString("ogr_tc");
                        String isim = rs.getString("ogr_isim");
                        String soyad = rs.getString("ogr_soyad");

                        sql = "INSERT INTO yurtgircik (ogr_tc, giris_tarih, durum) VALUES (?, ?, ?)";
                        try (PreparedStatement pstmtInsert = conn.prepareStatement(sql)) {
                            pstmtInsert.setString(1, tc);
                            pstmtInsert.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                            pstmtInsert.setBoolean(3, true);
                            pstmtInsert.executeUpdate();
                            showTimedMessageDialog(this, isim + " " + soyad + " başarıyla giriş yaptı.");
                            kodField.setText("");  
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Giriş kodu geçerli bir öğrenciye ait değil.", "Hata", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Veritabanı hatası: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cikisYap() {
        String kod = kodField.getText().trim();

        if (kod.isEmpty() || kod.length() != 6) {
            JOptionPane.showMessageDialog(this, "Lütfen 6 haneli geçerli bir çıkış kodu girin.", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT ogr_tc, ogr_isim, ogr_soyad FROM t_ogrenci WHERE substr(ogr_tc, 1, 3) || substr(ogr_tc, 9, 3) = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, kod);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        String tc = rs.getString("ogr_tc");
                        String isim = rs.getString("ogr_isim");
                        String soyad = rs.getString("ogr_soyad");

                        sql = "UPDATE yurtgircik SET cikis_tarih = ?, durum = ? WHERE ogr_tc = ? AND cikis_tarih IS NULL";
                        try (PreparedStatement pstmtUpdate = conn.prepareStatement(sql)) {
                            pstmtUpdate.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                            pstmtUpdate.setBoolean(2, false);
                            pstmtUpdate.setString(3, tc);
                            int updated = pstmtUpdate.executeUpdate();
                            if (updated > 0) {
                                showTimedMessageDialog(this, isim + " " + soyad + " başarıyla çıkış yaptı.");
                                kodField.setText("");  
                            } else {
                                JOptionPane.showMessageDialog(this, "Çıkış yapılacak giriş kaydı bulunamadı.", "Hata", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Çıkış kodu geçerli bir öğrenciye ait değil.", "Hata", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Veritabanı hatası" + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void tabloGuncelle() {
        tableModel.setRowCount(0);

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT t_ogrenci.ogr_isim, t_ogrenci.ogr_soyad, yurtgircik.giris_tarih, yurtgircik.cikis_tarih, yurtgircik.durum " +
                    "FROM yurtgircik JOIN t_ogrenci ON yurtgircik.ogr_tc = t_ogrenci.ogr_tc";
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String isim = rs.getString("ogr_isim").toUpperCase();
                    String soyad = rs.getString("ogr_soyad") != null ? rs.getString("ogr_soyad").charAt(0) + "." : "Bilinmiyor";
                    Timestamp girisTarih = rs.getTimestamp("giris_tarih");
                    Timestamp cikisTarih = rs.getTimestamp("cikis_tarih");
                    boolean durum = rs.getBoolean("durum");
                    String durumText = durum ? "Yurtta" : "Dışarıda";
                    tableModel.addRow(new Object[]{isim, soyad, girisTarih, cikisTarih != null ? cikisTarih : "Bilinmiyor", durumText});
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Veritabanı hatası: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showTimedMessageDialog(JFrame frame, String message) {
        JOptionPane pane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = pane.createDialog("Bilgi");

        Timer timer = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
                dialog.dispose();
            }
        });
        timer.setRepeats(false);
        timer.start();

        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setModal(true);
        dialog.setVisible(true);
    }
}
