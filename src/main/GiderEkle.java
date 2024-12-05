package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;


import java.time.YearMonth;

public class GiderEkle extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField elektrikField;
    private JTextField suField;
    private JTextField isinmafield;
    private JTextField intfield;
    private JTextField yemekfield;
    private JTextField personelfield;
    private JTextField digerfield;
    private JComboBox<String> comboBoxAy;
    private JComboBox<Integer> comboBoxYil;
    private static final String url = "jdbc:postgresql://localhost:5432/yurt";
    private static final String username = "postgres";
    private static final String password = "123";
    private JTable table;
    private DefaultTableModel model;


    public GiderEkle() {
        setTitle("Gider Ekle");
        setResizable(false);
        setIconImage(Toolkit.getDefaultToolkit().getImage(LoginWindow.class.getResource("/main/logg.png")));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 870, 465);
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

        JLabel lblNewLabel = new JLabel("Elektrik:");
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
        lblNewLabel.setBounds(8, 24, 57, 13);
        contentPane.add(lblNewLabel);

        elektrikField = new JTextField();
        elektrikField.setBounds(76, 22, 85, 19);
        contentPane.add(elektrikField);
        elektrikField.setColumns(10);

        JLabel lblSu = new JLabel("Su:");
        lblSu.setFont(new Font("Tahoma", Font.BOLD, 13));
        lblSu.setBounds(8, 62, 57, 13);
        contentPane.add(lblSu);

        suField = new JTextField();
        suField.setBounds(76, 60, 85, 19);
        contentPane.add(suField);
        suField.setColumns(10);

        JLabel lblDoalGaz = new JLabel("Isınma:");
        lblDoalGaz.setFont(new Font("Tahoma", Font.BOLD, 13));
        lblDoalGaz.setBounds(8, 104, 65, 13);
        contentPane.add(lblDoalGaz);

        isinmafield = new JTextField();
        isinmafield.setBounds(76, 102, 85, 19);
        contentPane.add(isinmafield);
        isinmafield.setColumns(10);

        JLabel intlabel = new JLabel("İnternet:");
        intlabel.setFont(new Font("Tahoma", Font.BOLD, 13));
        intlabel.setBounds(8, 147, 65, 13);
        contentPane.add(intlabel);

        intfield = new JTextField();
        intfield.setBounds(76, 145, 85, 19);
        contentPane.add(intfield);
        intfield.setColumns(10);

        JLabel yemeklabel = new JLabel("Yemek:");
        yemeklabel.setFont(new Font("Tahoma", Font.BOLD, 13));
        yemeklabel.setBounds(8, 192, 65, 13);
        contentPane.add(yemeklabel);

        yemekfield = new JTextField();
        yemekfield.setBounds(76, 190, 85, 19);
        contentPane.add(yemekfield);
        yemekfield.setColumns(10);

        JLabel lblPersonel = new JLabel("Personel:");
        lblPersonel.setFont(new Font("Tahoma", Font.BOLD, 13));
        lblPersonel.setBounds(8, 236, 65, 13);
        contentPane.add(lblPersonel);

        personelfield = new JTextField();
        personelfield.setBounds(76, 234, 85, 19);
        contentPane.add(personelfield);
        personelfield.setColumns(10);

        JLabel lblDierGiderler = new JLabel("Diğer :");
        lblDierGiderler.setFont(new Font("Tahoma", Font.BOLD, 13));
        lblDierGiderler.setBounds(8, 296, 65, 13);
        contentPane.add(lblDierGiderler);

        digerfield = new JTextField();
        digerfield.setBounds(76, 294, 85, 19);
        contentPane.add(digerfield);
        digerfield.setColumns(10);

        comboBoxAy = new JComboBox<>();
        comboBoxAy.setBounds(76, 340, 85, 21);
        contentPane.add(comboBoxAy);
        String[] aylar = {"Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran", "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"};
        for (String ay : aylar) {
            comboBoxAy.addItem(ay);
        }

        JLabel aylbl = new JLabel("Aylar:");
        aylbl.setFont(new Font("Tahoma", Font.BOLD, 12));
        aylbl.setBounds(8, 344, 40, 13);
        contentPane.add(aylbl);

        comboBoxYil = new JComboBox<>();
        comboBoxYil.setBounds(76, 370, 85, 21);
        contentPane.add(comboBoxYil);

        int currentYear = YearMonth.now().getYear();
        for (int year = currentYear; year <= currentYear + 10; year++) {
            comboBoxYil.addItem(year);
        }
        comboBoxYil.setSelectedItem(currentYear);
        comboBoxAy.setSelectedItem(aylar[YearMonth.now().getMonthValue() - 1]);

        JButton eklebutton = new JButton("Ekle");
        eklebutton.setBackground(Color.GREEN);
        eklebutton.setBounds(185, 189, 96, 21);
        contentPane.add(eklebutton);

        JButton guncellebutton = new JButton("Güncelle");
        guncellebutton.setBackground(Color.CYAN);
        guncellebutton.setBounds(185, 233, 96, 21);
        contentPane.add(guncellebutton);

        personelMaasHesapla();

        model = new DefaultTableModel();
        table = new JTable(model);
        model.addColumn("Ay");
        model.addColumn("Yıl");
        model.addColumn("Elektrik");
        model.addColumn("Su");
        model.addColumn("Isınma");
        model.addColumn("İnternet");
        model.addColumn("Yemek");
        model.addColumn("Personel");
        model.addColumn("Diğer");
        model.addColumn("Ekleyen Kullanıcı");
        model.addColumn("Güncelleyen Kullanıcı");

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(289, 22, 565, 369);
        contentPane.add(scrollPane);
        
        JLabel lblNewLabel_1 = new JLabel("Yıllar:");
        lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblNewLabel_1.setBounds(8, 378, 40, 13);
        contentPane.add(lblNewLabel_1);

        loadDataFromDatabase();

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    comboBoxAy.setSelectedItem(model.getValueAt(selectedRow, 0).toString());
                    comboBoxYil.setSelectedItem(model.getValueAt(selectedRow, 1).toString());
                    elektrikField.setText(model.getValueAt(selectedRow, 2).toString());
                    suField.setText(model.getValueAt(selectedRow, 3).toString());
                    isinmafield.setText(model.getValueAt(selectedRow, 4).toString());
                    intfield.setText(model.getValueAt(selectedRow, 5).toString());
                    yemekfield.setText(model.getValueAt(selectedRow, 6).toString());
                    personelfield.setText(model.getValueAt(selectedRow, 7).toString());
                    digerfield.setText(model.getValueAt(selectedRow, 8).toString());
                }
            }
        });

        eklebutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ekleGider();
            }
        });

        guncellebutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                guncelleGider();
            }
        });
    }

    private void ekleGider() {
        String ay = comboBoxAy.getSelectedItem().toString();
        int yil = (int) comboBoxYil.getSelectedItem();
        double elektrik = Double.parseDouble(elektrikField.getText());
        double su = Double.parseDouble(suField.getText());
        double isinma = Double.parseDouble(isinmafield.getText());
        double internet = Double.parseDouble(intfield.getText());
        double yemek = Double.parseDouble(yemekfield.getText());
        double personel = Double.parseDouble(personelfield.getText());
        double diger = Double.parseDouble(digerfield.getText());

        String ekleyenKisi = login.getAd() + " " + login.getAd();

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String yilSql = "SELECT yil_id FROM yillar WHERE yil = ?";
            int yilId = 0;
            try (PreparedStatement yilStmt = conn.prepareStatement(yilSql)) {
                yilStmt.setInt(1, yil);
                try (ResultSet rs = yilStmt.executeQuery()) {
                    if (rs.next()) {
                        yilId = rs.getInt("yil_id");
                    } else {
                        String insertYilSql = "INSERT INTO yillar (yil) VALUES (?) RETURNING yil_id";
                        try (PreparedStatement insertYilStmt = conn.prepareStatement(insertYilSql)) {
                            insertYilStmt.setInt(1, yil);
                            try (ResultSet insertRs = insertYilStmt.executeQuery()) {
                                if (insertRs.next()) {
                                    yilId = insertRs.getInt("yil_id");
                                }
                            }
                        }
                    }
                }
            }

            String sql = "INSERT INTO gider (ay, yil_id, elektrik, su, isinma, internet, yemek, personel, diger, ekleyen_kullanici) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, ay);
                pst.setInt(2, yilId);
                pst.setDouble(3, elektrik);
                pst.setDouble(4, su);
                pst.setDouble(5, isinma);
                pst.setDouble(6, internet);
                pst.setDouble(7, yemek);
                pst.setDouble(8, personel);
                pst.setDouble(9, diger);
                pst.setString(10, ekleyenKisi);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Gider başarıyla eklendi.");
                loadDataFromDatabase();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void guncelleGider() {
        String ay = comboBoxAy.getSelectedItem().toString();
        int yil = (int) comboBoxYil.getSelectedItem();
        double elektrik = Double.parseDouble(elektrikField.getText());
        double su = Double.parseDouble(suField.getText());
        double isinma = Double.parseDouble(isinmafield.getText());
        double internet = Double.parseDouble(intfield.getText());
        double yemek = Double.parseDouble(yemekfield.getText());
        double personel = Double.parseDouble(personelfield.getText());
        double diger = Double.parseDouble(digerfield.getText());

        String guncelleyenKisi = login.getAd() + " " + login.getAd();

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String yilSql = "SELECT yil_id FROM yillar WHERE yil = ?";
            int yilId = 0;
            try (PreparedStatement yilStmt = conn.prepareStatement(yilSql)) {
                yilStmt.setInt(1, yil);
                try (ResultSet rs = yilStmt.executeQuery()) {
                    if (rs.next()) {
                        yilId = rs.getInt("yil_id");
                    }
                }
            }

            String sql = "UPDATE gider SET elektrik = ?, su = ?, isinma = ?, internet = ?, yemek = ?, personel = ?, diger = ?, guncelleyen_kullanici = ? WHERE ay = ? AND yil_id = ?";
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setDouble(1, elektrik);
                pst.setDouble(2, su);
                pst.setDouble(3, isinma);
                pst.setDouble(4, internet);
                pst.setDouble(5, yemek);
                pst.setDouble(6, personel);
                pst.setDouble(7, diger);
                pst.setString(8, guncelleyenKisi);
                pst.setString(9, ay);
                pst.setInt(10, yilId);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Gider başarıyla güncellendi.");
                loadDataFromDatabase();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadDataFromDatabase() {
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String sql = "SELECT g.ay, y.yil, g.elektrik, g.su, g.isinma, g.internet, g.yemek, g.personel, g.diger, g.ekleyen_kullanici, g.guncelleyen_kullanici " +
                         "FROM gider g JOIN yillar y ON g.yil_id = y.yil_id";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                model.setRowCount(0);
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("ay"),
                        rs.getInt("yil"),
                        rs.getDouble("elektrik"),
                        rs.getDouble("su"),
                        rs.getDouble("isinma"),
                        rs.getDouble("internet"),
                        rs.getDouble("yemek"),
                        rs.getDouble("personel"),
                        rs.getDouble("diger"),
                        rs.getString("ekleyen_kullanici"),
                        rs.getString("guncelleyen_kullanici")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void personelMaasHesapla() {
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String sql = "SELECT SUM(maas) AS toplam_maas FROM personel";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    double toplamMaas = rs.getDouble("toplam_maas");
                    personelfield.setText(String.valueOf(toplamMaas));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
