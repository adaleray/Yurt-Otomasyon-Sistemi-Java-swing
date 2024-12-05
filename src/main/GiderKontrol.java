package main;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.YearMonth;
import javax.swing.table.DefaultTableModel;


public class GiderKontrol extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> comboBoxAy;
    private JComboBox<String> comboBoxYil;
    private JLabel lblToplamGider;
    private JLabel lblEkleyenKullanici;
    private JLabel lblGuncelleyenKullanici;
    private static final String url = "jdbc:postgresql://localhost:5432/yurt";
    private static final String username = "postgres";
    private static final String password = "123";


    public GiderKontrol() {
        setTitle("Gider Kontrol");
        setResizable(false);
        setIconImage(Toolkit.getDefaultToolkit().getImage(LoginWindow.class.getResource("/main/logg.png")));
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

        JLabel lblAy = new JLabel("Ay:");
        lblAy.setFont(new Font("Tahoma", Font.BOLD, 13));
        lblAy.setBounds(10, 20, 40, 20);
        contentPane.add(lblAy);

        comboBoxAy = new JComboBox<>();
        comboBoxAy.setBounds(50, 20, 100, 20);
        contentPane.add(comboBoxAy);
        String[] aylar = {"Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran", "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"};
        for (String ay : aylar) {
            comboBoxAy.addItem(ay);
        }

        JLabel lblYil = new JLabel("Yıl:");
        lblYil.setFont(new Font("Tahoma", Font.BOLD, 13));
        lblYil.setBounds(10, 60, 40, 20);
        contentPane.add(lblYil);

        comboBoxYil = new JComboBox<>();
        comboBoxYil.setBounds(50, 60, 100, 20);
        contentPane.add(comboBoxYil);

        int currentYear = YearMonth.now().getYear();
        for (int year = currentYear; year >= currentYear - 10; year--) {
            comboBoxYil.addItem(String.valueOf(year));
        }
        comboBoxYil.setSelectedItem(String.valueOf(currentYear));
        comboBoxAy.setSelectedItem(aylar[YearMonth.now().getMonthValue() - 1]);

        JButton btnAyGetir = new JButton("Ay Getir");
        btnAyGetir.setBackground(Color.ORANGE);
        btnAyGetir.setBounds(170, 40, 100, 25);
        contentPane.add(btnAyGetir);

        JButton btnYilGetir = new JButton("Yıl Getir");
        btnYilGetir.setBackground(Color.GREEN);
        btnYilGetir.setBounds(280, 40, 100, 25);
        contentPane.add(btnYilGetir);

        model = new DefaultTableModel();
        table = new JTable(model);
        model.addColumn("Kategori");
        model.addColumn("Tutar");

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(10, 100, 780, 225);
        contentPane.add(scrollPane);

        lblToplamGider = new JLabel("Toplam Gider(tl): 0.0");
        lblToplamGider.setForeground(Color.WHITE);
        lblToplamGider.setFont(new Font("Tahoma", Font.BOLD, 13));
        lblToplamGider.setBounds(10, 335, 200, 20);
        contentPane.add(lblToplamGider);

        lblEkleyenKullanici = new JLabel("Ekleyen Kullanıcı: Bilinmiyor");
        lblEkleyenKullanici.setForeground(Color.WHITE);
        lblEkleyenKullanici.setFont(new Font("Tahoma", Font.BOLD, 13));
        lblEkleyenKullanici.setBounds(283, 335, 300, 20);
        contentPane.add(lblEkleyenKullanici);

        lblGuncelleyenKullanici = new JLabel("Güncelleyen Kullanıcı: Bilinmiyor");
        lblGuncelleyenKullanici.setForeground(Color.WHITE);
        lblGuncelleyenKullanici.setFont(new Font("Tahoma", Font.BOLD, 13));
        lblGuncelleyenKullanici.setBounds(572, 335, 300, 20);
        contentPane.add(lblGuncelleyenKullanici);

        btnAyGetir.addActionListener(e -> {
            String selectedAy = (String) comboBoxAy.getSelectedItem();
            String selectedYil = (String) comboBoxYil.getSelectedItem();
            loadData(selectedAy, selectedYil);
        });

        btnYilGetir.addActionListener(e -> {
            String selectedYil = (String) comboBoxYil.getSelectedItem();
            loadYearData(selectedYil);
        });

        loadData((String) comboBoxAy.getSelectedItem(), (String) comboBoxYil.getSelectedItem());
    }

    private void loadData(String ay, String yil) {
        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            String query = "SELECT g.*, y.yil FROM gider g JOIN yillar y ON g.yil_id = y.yil_id WHERE g.ay = ? AND y.yil = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, ay);
            statement.setInt(2, Integer.parseInt(yil));
            ResultSet resultSet = statement.executeQuery();

            model.setRowCount(0);
            double toplamGider = 0;
            String ekleyenKullanici = "Bilinmiyor";
            String guncelleyenKullanici = "Bilinmiyor";

            while (resultSet.next()) {
                double elektrik = resultSet.getDouble("elektrik");
                double su = resultSet.getDouble("su");
                double isinma = resultSet.getDouble("isinma");
                double internet = resultSet.getDouble("internet");
                double yemek = resultSet.getDouble("yemek");
                double personel = resultSet.getDouble("personel");
                double diger = resultSet.getDouble("diger");

                ekleyenKullanici = resultSet.getString("ekleyen_kullanici");
                guncelleyenKullanici = resultSet.getString("guncelleyen_kullanici");

                model.addRow(new Object[]{"Elektrik", elektrik});
                model.addRow(new Object[]{"Su", su});
                model.addRow(new Object[]{"Isınma", isinma});
                model.addRow(new Object[]{"İnternet", internet});
                model.addRow(new Object[]{"Yemek", yemek});
                model.addRow(new Object[]{"Personel", personel});
                model.addRow(new Object[]{"Diğer", diger});

                toplamGider += elektrik + su + isinma + internet + yemek + personel + diger;
            }

            lblToplamGider.setText("Toplam Gider(tl): " + toplamGider);
            lblEkleyenKullanici.setText("Ekleyen Kullanıcı: " + (ekleyenKullanici != null ? ekleyenKullanici : "Bilinmiyor"));
            lblGuncelleyenKullanici.setText("Güncelleyen Kullanıcı: " + (guncelleyenKullanici != null ? guncelleyenKullanici : "Bilinmiyor"));
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Veriler yüklenirken bir hata oluştu: " + e.getMessage());
        }
    }

    private void loadYearData(String yil) {
        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            String query = "SELECT g.ay, SUM(g.elektrik + g.su + g.isinma + g.internet + g.yemek + g.personel + g.diger) AS toplam " +
                           "FROM gider g JOIN yillar y ON g.yil_id = y.yil_id WHERE y.yil = ? GROUP BY g.ay";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, Integer.parseInt(yil));
            ResultSet resultSet = statement.executeQuery();

            model.setRowCount(0);
            double toplamYilGider = 0;

            while (resultSet.next()) {
                String ay = resultSet.getString("ay");
                double toplam = resultSet.getDouble("toplam");
                model.addRow(new Object[]{ay, toplam});
                toplamYilGider += toplam;
            }

            lblToplamGider.setText("Toplam Yıl Gideri(tl): " + toplamYilGider);
            lblEkleyenKullanici.setText("Ekleyen Kullanıcı: Bilinmiyor");
            lblGuncelleyenKullanici.setText("Güncelleyen Kullanıcı: Bilinmiyor");
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Veriler yüklenirken bir hata oluştu: " + e.getMessage());
        }
    }
}
