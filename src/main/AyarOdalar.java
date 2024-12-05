package main;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AyarOdalar extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private Connection con = null;
    private JTable table;

    private static final String url = "jdbc:postgresql://localhost:5432/yurt";
    private static final String username = "postgres";
    private static final String password = "123";



    public AyarOdalar() {
        setTitle("Oda Ayar Sistem");
        setResizable(false);
        setIconImage(Toolkit.getDefaultToolkit().getImage(LoginWindow.class.getResource("/main/logg.png")));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 774, 390);
        contentPane = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon imageIcon = new ImageIcon(getClass().getResource("erayy.jpg"));
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

        JLabel lblOdaNumarasi = new JLabel("Oda Numarası:");
        lblOdaNumarasi.setBounds(30, 50, 100, 20);
        contentPane.add(lblOdaNumarasi);

        JTextField txtOdaNumarasi = new JTextField();
        txtOdaNumarasi.setBounds(140, 50, 100, 20);
        contentPane.add(txtOdaNumarasi);
        txtOdaNumarasi.setColumns(10);

        JLabel lblKapasite = new JLabel("Kapasite:");
        lblKapasite.setBounds(30, 100, 100, 20);
        contentPane.add(lblKapasite);

        JTextField txtKapasite = new JTextField();
        txtKapasite.setBounds(140, 100, 100, 20);
        contentPane.add(txtKapasite);
        txtKapasite.setColumns(10);

        JButton btnEkle = new JButton("Ekle");
        btnEkle.setBackground(Color.GREEN);
        btnEkle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int odaNumarasi = Integer.parseInt(txtOdaNumarasi.getText());
                    int kapasite = Integer.parseInt(txtKapasite.getText());
                    // Onay için diyalog kutusu
                    int option = JOptionPane.showConfirmDialog(null, "Ekleme işlemi için onaylıyor musunuz?\nOda Numarası: " + odaNumarasi + "\nKapasite: " + kapasite, "Onay", JOptionPane.YES_NO_OPTION);
                    if (option == JOptionPane.YES_OPTION) {
                        odaEkle(odaNumarasi, kapasite);
                        tabloyuGuncelle(); // Tabloyu güncelle
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Lütfen Oda numarası ve kapasite kısımlarını doldurunuz", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        btnEkle.setBounds(11, 150, 100, 20);
        contentPane.add(btnEkle);

        JButton btnDuzenle = new JButton("Kapasite Düzenle");
        btnDuzenle.setBackground(Color.YELLOW);
        btnDuzenle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    int odaNumarasi = (int) table.getValueAt(selectedRow, 0);
                    String yeniKapasiteString = JOptionPane.showInputDialog(null, "Yeni Kapasite:");
                    if (yeniKapasiteString != null && !yeniKapasiteString.isEmpty()) {
                        int yeniKapasite = Integer.parseInt(yeniKapasiteString);
                        int option = JOptionPane.showConfirmDialog(null, "Yeni kapasite " + yeniKapasite + " olarak ayarlanacak. Devam etmek istiyor musunuz?", "Kapasite Değişikliği", JOptionPane.YES_NO_OPTION);
                        if (option == JOptionPane.YES_OPTION) {
                            odaDuzenle(odaNumarasi, yeniKapasite);
                            tabloyuGuncelle(); // Tabloyu güncelle
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Lütfen bir oda seçin.");
                }
            }
        });
        btnDuzenle.setBounds(119, 150, 149, 20);
        contentPane.add(btnDuzenle);

        JButton btnSil = new JButton("Oda Sil");
        btnSil.setBackground(Color.RED);
        btnSil.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    int odaNumarasi = (int) table.getValueAt(selectedRow, 0);
                    int option = JOptionPane.showConfirmDialog(null, "Seçilen odanın silinmesini onaylıyor musunuz?", "Oda Sil", JOptionPane.YES_NO_OPTION);
                    if (option == JOptionPane.YES_OPTION) {
                        odaSil(odaNumarasi);
                        tabloyuGuncelle(); // Tabloyu güncelle
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Lütfen bir oda seçin.");
                }
            }
        });
        btnSil.setBounds(82, 192, 100, 20);
        contentPane.add(btnSil);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(300, 50, 450, 300);
        contentPane.add(scrollPane);

        table = new JTable();
        scrollPane.setViewportView(table);
        tabloyuGuncelle(); // Tabloyu başlat
    }

    private void tabloyuGuncelle() {
        try {
            if (con == null) {
                con = DriverManager.getConnection(url, username, password);
            }
            String query = "SELECT oda_numarasi, kapasite, bosyer FROM oda_kapasiteleri ORDER BY oda_numarasi ASC";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Oda Numarası");
            model.addColumn("Oda Kapasite");
            model.addColumn("Odadaki Boş Yer");

            while (rs.next()) {
                int odaNumarasi = rs.getInt("oda_numarasi");
                int kapasite = rs.getInt("kapasite");
                int doluluk = rs.getInt("bosyer"); // Doluluk bilgisini "bosyer" sütunundan alıyoruz
                int bosyer = kapasite - doluluk; // Boş yer sayısını hesaplıyoruz
                model.addRow(new Object[]{odaNumarasi, kapasite, bosyer});
            }

            table.setModel(model);

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void odaEkle(int odaNumarasi, int kapasite) {
        try {
            if (con == null) {
                con = DriverManager.getConnection(url, username, password);
            }
            String query = "INSERT INTO oda_kapasiteleri (oda_numarasi, kapasite) VALUES (?, ?)";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, odaNumarasi);
            pst.setInt(2, kapasite);
            int affectedRows = pst.executeUpdate();
            pst.close();
            
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(null, "Oda " + odaNumarasi + " numaralı oda ile " + kapasite + " kapasiteyle başarıyla eklendi.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void odaSil(int odaNumarasi) {
        try {
            if (con == null) {
                con = DriverManager.getConnection(url, username, password);
            }
            String query = "DELETE FROM oda_kapasiteleri WHERE oda_numarasi = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, odaNumarasi);
            int affectedRows = pst.executeUpdate();
            pst.close();

            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(null, "Oda " + odaNumarasi + " numaralı oda başarıyla silindi.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void odaDuzenle(int odaNumarasi, int yeniKapasite) {
        try {
            if (con == null) {
                con = DriverManager.getConnection(url, username, password);
            }
            String query = "UPDATE oda_kapasiteleri SET kapasite = ? WHERE oda_numarasi = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, yeniKapasite);
            pst.setInt(2, odaNumarasi);
            int affectedRows = pst.executeUpdate();
            pst.close();
            
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(null, "Oda " + odaNumarasi + " numaralı oda kapasitesi " + yeniKapasite + " olarak güncellendi.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
