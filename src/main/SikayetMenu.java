package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

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
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class SikayetMenu extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField textField;
    private JTextField textField_1;
    private JTextField textField_2;
    private JScrollPane scrollPane;
    private JTable table;
    private JComboBox<Integer> odacombo;

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/yurt";
    private static final String DB_USER = "postgres";
    private static final String DB_PASS = "123";

    public SikayetMenu() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(SikayetMenu.class.getResource("/main/logg.png")));
        setTitle("Yurt Otomasyonu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 804, 424);
        setLocationRelativeTo(null);
        contentPane = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                URL url = getClass().getResource("indir.jpg");
                super.paintComponent(g);
                ImageIcon imageIcon = new ImageIcon(url.getPath());
                Image image = imageIcon.getImage();
                g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(400, 400);
            }
        };
        contentPane.setForeground(SystemColor.inactiveCaptionText);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        contentPane.setLayout(null);
        odacombo = new JComboBox<Integer>(); 
        odacombo.setBounds(8, 107, 96, 21);
        contentPane.add(odacombo);
        JLabel si_isim = new JLabel("İsim");
        si_isim.setForeground(new Color(255, 255, 255));
        si_isim.setFont(new Font("Tahoma", Font.BOLD, 15));
        si_isim.setBounds(158, 50, 73, 32);
        contentPane.add(si_isim);

        JLabel lblNewLabel_1 = new JLabel("ŞİKAYET/SORUN");
        lblNewLabel_1.setForeground(Color.WHITE);
        lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 15));
        lblNewLabel_1.setBounds(457, 38, 230, 56);
        contentPane.add(lblNewLabel_1);

        textField = new JTextField();
        textField.setBounds(136, 105, 95, 25);
        contentPane.add(textField);
        textField.setColumns(10);

        textField_1 = new JTextField();
        textField_1.setBounds(457, 112, 215, 104);
        contentPane.add(textField_1);
        textField_1.setColumns(10);
        loadRooms();
        JButton s_olustur = new JButton("OLUŞTUR");
        s_olustur.setBackground(Color.GREEN);
        s_olustur.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sikayetekle();
            }

        });

        s_olustur.setBounds(308, 240, 109, 21);
        contentPane.add(s_olustur);

        JLabel lblNewLabel = new JLabel("Soyad");
        lblNewLabel.setForeground(Color.WHITE);
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblNewLabel.setBackground(Color.WHITE);
        lblNewLabel.setBounds(271, 50, 76, 25);
        contentPane.add(lblNewLabel);

        textField_2 = new JTextField();
        textField_2.setBounds(271, 105, 95, 25);
        contentPane.add(textField_2);
        textField_2.setColumns(10);

        JLabel si_odanum = new JLabel("Oda Numarası");
        si_odanum.setForeground(Color.WHITE);
        si_odanum.setFont(new Font("Tahoma", Font.BOLD, 14));
        si_odanum.setBounds(8, 50, 106, 32);
        contentPane.add(si_odanum);

        scrollPane = new JScrollPane();
        scrollPane.setBounds(8, 271, 748, 114);
        contentPane.add(scrollPane);

        table = new JTable() {

            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        scrollPane.setViewportView(table);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {
                        showSelectedRowData(selectedRow);
                    }
                }
            }
        });
        loadTableData();
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton anamen = new JButton("Geri");
        anamen.setBackground(new Color(204, 0, 51));
        anamen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                İlkMenu ilkMenu = new İlkMenu();
                ilkMenu.setVisible(true);
                dispose();
            }
        });
        anamen.setBounds(680, 33, 76, 21);
        contentPane.add(anamen);

    }

    private void loadRooms() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            String query = "SELECT oda_numarasi FROM oda_kapasiteleri";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                odacombo.addItem(resultSet.getInt("oda_numarasi"));
            }

            resultSet.close();
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showSelectedRowData(int selectedRow) {
        Object id = table.getValueAt(selectedRow, 0);
        Object isim = table.getValueAt(selectedRow, 1);
        Object soyad = table.getValueAt(selectedRow, 2);
        Object odaNumarasi = table.getValueAt(selectedRow, 3);
        Object sikayet = table.getValueAt(selectedRow, 4);
        Object tarih = table.getValueAt(selectedRow, 5);

        String message = "ID: " + id + "\n" + "İsim: " + isim + "\n" + "Soyad: " + soyad + "\n" + "Oda Numarası: "
                + odaNumarasi + "\n" + "Şikayet/Sorun: " + sikayet + "\n" + "Tarih: " + tarih;

        JOptionPane.showMessageDialog(SikayetMenu.this, message, "Seçilen Satırın Bilgileri",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void sikayetekle() {

        String isim = textField.getText().trim();
        String soyad = textField_2.getText().trim();
        Integer odaNumarasi = (Integer) odacombo.getSelectedItem();

        String sikayet = textField_1.getText().trim();
        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(now);

        // Eksik alanları kontrol et
        if (isim.isEmpty() || soyad.isEmpty() || sikayet.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder("Lütfen aşağıdaki alanları doldurun:\n");
            if (isim.isEmpty())
                errorMessage.append("- İsim\n");
            if (soyad.isEmpty())
                errorMessage.append("- Soyad\n");
            if (sikayet.isEmpty())
                errorMessage.append("- Şikayet/Sorun\n");
            JOptionPane.showMessageDialog(null, errorMessage.toString(), "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Harf kontrolü
        if (!isStringOnlyAlphabet(isim)) {
            JOptionPane.showMessageDialog(SikayetMenu.this, "Lütfen sadece harf kullanarak ad giriniz!", "Hata",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!isStringOnlyAlphabet(soyad)) {
            JOptionPane.showMessageDialog(SikayetMenu.this, "Lütfen sadece harf kullanarak soyad giriniz!", "Hata",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String message = "İsim: " + isim + "\n" + "Soyad: " + soyad + "\n" + "Oda Numarası: " + odaNumarasi + "\n"
                + "Şikayet/Sorun: " + sikayet + "\n\n" + "Yukarıdaki bilgileri onaylıyor musunuz?";
        int cevap = JOptionPane.showConfirmDialog(SikayetMenu.this, message, "Bilgi Onayı", JOptionPane.YES_NO_OPTION);

        if (cevap == JOptionPane.YES_OPTION) {
            try {
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                String query = "INSERT INTO t_sikayet (ogr_isim, ogr_soyad, ogr_odanumber, sorun, tarih) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement statement = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
                statement.setString(1, isim);
                statement.setString(2, soyad);
                statement.setInt(3, odaNumarasi);
                statement.setString(4, sikayet);
                statement.setTimestamp(5, timestamp);

                statement.executeUpdate();

                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    System.out.println("Eklenen Şikayet ID: " + id);
                }

                generatedKeys.close();
                statement.close();
                conn.close();

                JOptionPane.showMessageDialog(SikayetMenu.this, "Şikayet başarıyla kaydedildi.");

                textField.setText("");
                textField_1.setText("");
                textField_2.setText("");

                loadTableData();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private boolean isStringOnlyAlphabet(String str) {
        return str != null && str.matches("^[a-zA-ZüöçğıİĞÜÇÖşŞ]+$");
    }

    private void loadTableData() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            String query = "SELECT id, ogr_isim, ogr_soyad, ogr_odanumber, sorun, tarih::date FROM t_sikayet ORDER BY tarih DESC";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("ID");
            model.addColumn("İsim");
            model.addColumn("Soyad");
            model.addColumn("Oda Numarası");
            model.addColumn("Şikayet/Sorun");
            model.addColumn("Tarih");

            while (resultSet.next()) {
                Object[] row = new Object[6];
                row[0] = resultSet.getInt("id");
                row[1] = resultSet.getString("ogr_isim");
                row[2] = resultSet.getString("ogr_soyad");
                row[3] = resultSet.getInt("ogr_odanumber");
                row[4] = resultSet.getString("sorun");
                row[5] = resultSet.getDate("tarih");
                model.addRow(row);
            }

            table.setModel(model);

            resultSet.close();
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
