package main;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.Color;

public class GelirEkle extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField textFieldOgrenciGeliri;
    private JTextField textFieldDevletDestegi;
    private JTextField textFieldCamasirYikama;
    private JTable table;
    private JComboBox<String> comboBoxYil;
    private JComboBox<String> comboBoxAy;
    private JComboBox<String> comboBoxYuzde;

    private static final String url = "jdbc:postgresql://localhost:5432/yurt";
    private static final String username = "postgres";
    private static final String password = "123";



    public GelirEkle() {
        setTitle("Gelir Kontrol");
        setResizable(false);
        setIconImage(Toolkit.getDefaultToolkit().getImage(GelirEkle.class.getResource("/main/logg.png")));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 813, 416);
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

        contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblOgrenciGeliri = new JLabel("Öğrenci Geliri:");
        lblOgrenciGeliri.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblOgrenciGeliri.setBounds(20, 24, 107, 19);
        contentPane.add(lblOgrenciGeliri);

        textFieldOgrenciGeliri = new JTextField();
        textFieldOgrenciGeliri.setBounds(150, 25, 76, 19);
        contentPane.add(textFieldOgrenciGeliri);
        textFieldOgrenciGeliri.setColumns(10);

        JLabel lblDevletDestegi = new JLabel("Devlet Desteği:");
        lblDevletDestegi.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblDevletDestegi.setBounds(20, 66, 107, 24);
        contentPane.add(lblDevletDestegi);

        textFieldDevletDestegi = new JTextField();
        textFieldDevletDestegi.setBounds(150, 70, 76, 19);
        contentPane.add(textFieldDevletDestegi);
        textFieldDevletDestegi.setColumns(10);

        JLabel lblCamasirYikama = new JLabel("Çamaşır Yıkama:");
        lblCamasirYikama.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblCamasirYikama.setBounds(20, 117, 119, 13);
        contentPane.add(lblCamasirYikama);

        textFieldCamasirYikama = new JTextField();
        textFieldCamasirYikama.setBounds(150, 115, 76, 19);
        contentPane.add(textFieldCamasirYikama);
        textFieldCamasirYikama.setColumns(10);

        JButton btnEkle = new JButton("Ekle");
        btnEkle.setBackground(Color.GREEN);
        btnEkle.setBounds(8, 264, 100, 21);
        contentPane.add(btnEkle);

        comboBoxYil = new JComboBox<>();
        comboBoxYil.setBounds(20, 163, 69, 21);
        contentPane.add(comboBoxYil);

        comboBoxAy = new JComboBox<>();
        comboBoxAy.setBounds(122, 163, 76, 21);
        contentPane.add(comboBoxAy);

        JButton btnGuncelle = new JButton("Güncelle");
        btnGuncelle.setBackground(Color.ORANGE);
        btnGuncelle.setBounds(8, 307, 100, 21);
        contentPane.add(btnGuncelle);

        comboBoxYuzde = new JComboBox<>();
        comboBoxYuzde.setBounds(236, 70, 69, 21);
        comboBoxYuzde.addItem("10%");
        comboBoxYuzde.addItem("15%");
        comboBoxYuzde.addItem("20%");
        comboBoxYuzde.addItem("25%");
        comboBoxYuzde.addItem("30%");
        comboBoxYuzde.addItem("35%");
        comboBoxYuzde.addItem("40%");
        contentPane.add(comboBoxYuzde);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(308, 24, 457, 332);
        contentPane.add(scrollPane);

        table = new JTable();
        scrollPane.setViewportView(table);

        String[] aylar = {"Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran", "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"};
        Map<Integer, String> ayMap = new HashMap<>();
        for (int i = 0; i < aylar.length; i++) {
            comboBoxAy.addItem(aylar[i]);
            ayMap.put(i + 1, aylar[i]);
        }

        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();

        loadYillar(currentYear);
        comboBoxAy.setSelectedItem(ayMap.get(currentMonth));
        loadOgrenciGeliri();
        loadTableData();

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int selectedRow = table.getSelectedRow();
                comboBoxYil.setSelectedItem(table.getValueAt(selectedRow, 0).toString());
                comboBoxAy.setSelectedItem(table.getValueAt(selectedRow, 1).toString());
                textFieldOgrenciGeliri.setText(table.getValueAt(selectedRow, 2).toString());
                textFieldDevletDestegi.setText(table.getValueAt(selectedRow, 3).toString());
                textFieldCamasirYikama.setText(table.getValueAt(selectedRow, 4).toString());
            }
        });

        comboBoxYil.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadOgrenciGeliri();
            }
        });

        comboBoxAy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadOgrenciGeliri();
            }
        });

        btnEkle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addGelir();
                loadTableData();
            }
        });

        btnGuncelle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(GelirEkle.this, "Lütfen tablodan güncellenecek veriyi seçin.",
                            "Veri Seçilmedi", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int response = JOptionPane.showConfirmDialog(GelirEkle.this,
                        "Bu veriyi güncellemek istediğinize emin misiniz?", "Güncelleme Onayı",
                        JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    String yil = (String) comboBoxYil.getSelectedItem();
                    String ay = (String) comboBoxAy.getSelectedItem();
                    String ogrenciGeliri = textFieldOgrenciGeliri.getText();
                    String devletDestegi = textFieldDevletDestegi.getText();
                    String camasirYikama = textFieldCamasirYikama.getText();
                    updateGelir(yil, ay, ogrenciGeliri, devletDestegi, camasirYikama);
                    loadTableData();
                }
            }
        });

        comboBoxYuzde.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateDevletDestegi();
            }
        });
    }

    private void loadYillar(int currentYear) {
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement("SELECT yil FROM yillar")) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                comboBoxYil.addItem(String.valueOf(rs.getInt("yil")));
            }
            comboBoxYil.setSelectedItem(String.valueOf(currentYear));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addGelir() {
        String ogrenciGeliri = textFieldOgrenciGeliri.getText();
        String devletDestegi = textFieldDevletDestegi.getText();
        String camasirYikama = textFieldCamasirYikama.getText();
        String yil = (String) comboBoxYil.getSelectedItem();
        String ay = (String) comboBoxAy.getSelectedItem();
        String ekleyenKullanici = login.getAd() + " " + login.getSoyad();

        String query = "INSERT INTO gelir (ogrenci_geliri, devlet_destegi, camasir_yikama, yil_id, ay, ekleyen_kullanici) VALUES (?, ?, ?, (SELECT yil_id FROM yillar WHERE yil = ?), ?, ?)";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setDouble(1, Double.parseDouble(ogrenciGeliri));
            stmt.setDouble(2, Double.parseDouble(devletDestegi));
            stmt.setDouble(3, Double.parseDouble(camasirYikama));
            stmt.setInt(4, Integer.parseInt(yil));
            stmt.setString(5, ay);
            stmt.setString(6, ekleyenKullanici);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Gelir başarıyla eklendi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
            loadTableData();
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        textFieldOgrenciGeliri.setText("");
        textFieldDevletDestegi.setText("");
        textFieldCamasirYikama.setText("");
    }

    private void updateGelir(String yil, String ay, String ogrenciGeliri, String devletDestegi, String camasirYikama) {
        String guncelleyenKullanici =   login.getAd() + " " + login.getSoyad();

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE gelir SET yil_id = (SELECT yil_id FROM yillar WHERE yil = ?), ay = ?, ogrenci_geliri = ?, devlet_destegi = ?, camasir_yikama = ?, güncelleyen_kullanici = ? WHERE yil_id = (SELECT yil_id FROM yillar WHERE yil = ?) AND ay = ?")) {

            stmt.setInt(1, Integer.parseInt(yil));
            stmt.setString(2, ay);
            stmt.setDouble(3, Double.parseDouble(ogrenciGeliri));
            stmt.setDouble(4, Double.parseDouble(devletDestegi));
            stmt.setDouble(5, Double.parseDouble(camasirYikama));
            stmt.setString(6, guncelleyenKullanici);
            stmt.setInt(7, Integer.parseInt(yil));
            stmt.setString(8, ay);

            stmt.executeUpdate();
            clearFields();
            JOptionPane.showMessageDialog(this, "Veri başarıyla güncellendi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Veri güncelleme başarısız.", "Başarısız", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void loadTableData() {
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT y.yil, g.ay, g.ogrenci_geliri, g.devlet_destegi, g.camasir_yikama, g.ekleyen_kullanici, g.güncelleyen_kullanici FROM gelir g JOIN yillar y ON g.yil_id = y.yil_id")) {

            ResultSet rs = stmt.executeQuery();
            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"Yıl", "Ay", "Öğrenci Geliri", "Devlet Desteği", "Çamaşır Yıkama", "Ekleyen Kullanıcı", "Güncelleyen Kullanıcı"}, 0);

            while (rs.next()) {
                int yil = rs.getInt("yil");
                String ay = rs.getString("ay");
                double ogrenciGeliri = rs.getDouble("ogrenci_geliri");
                double devletDestegi = rs.getDouble("devlet_destegi");
                double camasirYikama = rs.getDouble("camasir_yikama");
                String ekleyenKullanici = rs.getString("ekleyen_kullanici");
                String guncelleyenKullanici = rs.getString("güncelleyen_kullanici");

                model.addRow(new Object[]{yil, ay, ogrenciGeliri, devletDestegi, camasirYikama, ekleyenKullanici, guncelleyenKullanici});
            }
            table.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadOgrenciGeliri() {
        String yilStr = (String) comboBoxYil.getSelectedItem();
        String ayStr = (String) comboBoxAy.getSelectedItem();

        if (yilStr == null || ayStr == null) {
            textFieldOgrenciGeliri.setText("");
            textFieldDevletDestegi.setText("");
            return;
        }

        int yil;
        int ay;
        try {
            yil = Integer.parseInt(yilStr);
            ay = comboBoxAy.getSelectedIndex() + 1; // Ay indeksi 0'dan başlıyor, bu yüzden 1 ekliyoruz.
        } catch (NumberFormatException e) {
            textFieldOgrenciGeliri.setText("");
            textFieldDevletDestegi.setText("");
            return;
        }

        String query = "SELECT SUM(odeme_miktari) as toplam_gelir FROM t_odeme WHERE EXTRACT(YEAR FROM odeme_tarihi) = ? AND EXTRACT(MONTH FROM odeme_tarihi) = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, yil);
            stmt.setInt(2, ay);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                double toplamGelir = rs.getDouble("toplam_gelir");
                textFieldOgrenciGeliri.setText(String.valueOf(toplamGelir));
                updateDevletDestegi(toplamGelir);
            } else {
                textFieldOgrenciGeliri.setText("0.0");
                textFieldDevletDestegi.setText("0.0");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private void updateDevletDestegi() {
        String ogrenciGeliriStr = textFieldOgrenciGeliri.getText();
        if (!ogrenciGeliriStr.isEmpty()) {
            double ogrenciGeliri = Double.parseDouble(ogrenciGeliriStr);
            updateDevletDestegi(ogrenciGeliri);
        }
    }

    private void updateDevletDestegi(double ogrenciGeliri) {
        String yuzdeStr = (String) comboBoxYuzde.getSelectedItem();
        int yuzde = Integer.parseInt(yuzdeStr.replace("%", ""));
        double devletDestegi = ogrenciGeliri * yuzde / 100;
        textFieldDevletDestegi.setText(String.valueOf(devletDestegi));
    }
}
