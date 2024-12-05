package main;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class Yemenuadmin extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable yemektablo;
    private Connection conn;
    private JComboBox<String> gunComboBox;

 

    public Yemenuadmin() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(Yemenuadmin.class.getResource("/main/logg.png")));
        setTitle("Yurt Otomasyonu");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 804, 424);
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

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(10, 120, 760, 190);
        contentPane.add(scrollPane);

        yemektablo = new JTable();
        scrollPane.setViewportView(yemektablo);

        gunComboBox = new JComboBox<>();
        gunComboBox.setBackground(Color.CYAN);
        gunComboBox.setModel(new DefaultComboBoxModel<>(new String[] {"Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma", "Cumartesi", "Pazar"}));
        gunComboBox.setBounds(10, 320, 150, 30);
        contentPane.add(gunComboBox);

        JButton btnEkle = new JButton("Ekle");
        btnEkle.setForeground(Color.GREEN);
        btnEkle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String gun = (String) gunComboBox.getSelectedItem();
                    boolean gunVarMi = checkIfGunExists(gun);
                    if (gunVarMi) {
                        JOptionPane.showMessageDialog(null, gun + " günü zaten mevcut.");
                        return;
                    }

                    String corba = JOptionPane.showInputDialog("Çorba:");
                    if (corba == null) {
                        return;
                    }
                    String anaYemek = JOptionPane.showInputDialog("Ana Yemek:");
                    if (anaYemek == null) {
                        return;
                    }
                    String suluYemek = JOptionPane.showInputDialog("Sulu Yemek:");
                    if (suluYemek == null) {
                        return;
                    }
                    String ekstra = JOptionPane.showInputDialog("Ekstra:");
                    if (ekstra == null) {
                        return;
                    }
                    String kahvalti = JOptionPane.showInputDialog("Kahvaltı:");
                    if (kahvalti == null) {
                        return;
                    }

                    if (gun.isEmpty() || corba.isEmpty() || anaYemek.isEmpty() || suluYemek.isEmpty() || ekstra.isEmpty() || kahvalti.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Lütfen boş alan bırakmayın.");
                        return;
                    }

                    String yemekListesi = corba + " | " + anaYemek + " | " + suluYemek + " | " + ekstra;
                    String kahvaltiListesi = kahvalti;

                    String sql = "INSERT INTO yemekmenu (gun, yemekListesi, kahvaltiListesi) VALUES (?, ?, ?)";
                    PreparedStatement statement = conn.prepareStatement(sql);
                    statement.setString(1,gun);
                    statement.setString(2, yemekListesi);
                    statement.setString(3, kahvaltiListesi);
                    statement.executeUpdate();

                    // Tabloyu yeniden yükle
                    loadDataFromDatabase();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        btnEkle.setBounds(200, 320, 100, 30);
        contentPane.add(btnEkle);

        JButton btnSil = new JButton("Sil");
        btnSil.setForeground(Color.RED);
        btnSil.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Silme işlemleri burada yapılacak
                try {
                    int selectedRow = yemektablo.getSelectedRow();
                    if (selectedRow == -1) {
                        JOptionPane.showMessageDialog(null, "Lütfen bir satır seçin.");
                        return;
                    }
                    String gun = (String) yemektablo.getValueAt(selectedRow, 0);

                    String sql = "DELETE FROM yemekmenu WHERE gun = ?";
                    PreparedStatement statement = conn.prepareStatement(sql);
                    statement.setString(1, gun);
                    statement.executeUpdate();

                    // Tabloyu yeniden yükle
                    loadDataFromDatabase();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        btnSil.setBounds(310, 320, 100, 30);
        contentPane.add(btnSil);

        JButton btnGuncelleYemek = new JButton("Yemek Güncelle");
        btnGuncelleYemek.setForeground(Color.PINK);
        btnGuncelleYemek.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	
            	yemekguncelle();
            }
        });
        btnGuncelleYemek.setBounds(420, 320, 150, 30);
        contentPane.add(btnGuncelleYemek);

        JButton btnGuncelleKahvalti = new JButton("Kahvaltı Güncelle");
        btnGuncelleKahvalti.setForeground(Color.PINK);
        btnGuncelleKahvalti.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Kahvaltı güncelleme işlemleri burada yapılacak
                try {
                    int selectedRow = yemektablo.getSelectedRow();
                    if (selectedRow == -1) {
                        JOptionPane.showMessageDialog(null, "Lütfen bir satır seçin.");
                        return;
                    }
                    String gun = (String) yemektablo.getValueAt(selectedRow, 0);
                    String yeniKahvaltiListesi = JOptionPane.showInputDialog("Yeni Kahvaltı Listesi:");

                    if (yeniKahvaltiListesi == null || yeniKahvaltiListesi.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Yeni kahvaltı listesi boş olamaz.");
                        return;
                    }

                    String sql = "UPDATE yemekmenu SET kahvaltiListesi = ? WHERE gun = ?";
                    PreparedStatement statement = conn.prepareStatement(sql);
                    statement.setString(1, yeniKahvaltiListesi);
                    statement.setString(2, gun);
                    statement.executeUpdate();

                    // Tabloyu yeniden yükle
                    loadDataFromDatabase();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        btnGuncelleKahvalti.setBounds(580, 320, 150, 30);
        contentPane.add(btnGuncelleKahvalti);
        
        JLabel lblNewLabel = new JLabel("HAFTALIK KAHVALTI - YEMEK LİSTESİ SİSTEMİ");
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
        lblNewLabel.setBounds(184, 41, 367, 13);
        contentPane.add(lblNewLabel);
        
     

        conn = DatabaseConnection.getConnection();

        loadDataFromDatabase();
    }

    private boolean checkIfGunExists(String gun) {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) AS count FROM yemekmenu WHERE gun = ?")) {
            stmt.setString(1, gun);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("count");
                return count > 0; 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Veritabanı hatası veya başka bir sorun olursa false döndür
    }

    private void loadDataFromDatabase() {
        try (Statement stmt = conn.createStatement()) {
            String sql = "SELECT * FROM yemekmenu ORDER BY CASE WHEN gun='Pazartesi' THEN 1 WHEN gun='Salı' THEN 2 WHEN gun='Çarşamba' THEN 3 WHEN gun='Perşembe' THEN 4 WHEN gun='Cuma' THEN 5 WHEN gun='Cumartesi' THEN 6 WHEN gun='Pazar' THEN 7 END";
            ResultSet rs = stmt.executeQuery(sql);

            DefaultTableModel model = new DefaultTableModel();
            yemektablo.setModel(model);

            model.addColumn("GÜN"); 
            model.addColumn("YEMEK LİSTESİ"); 
            model.addColumn("KAHVALTI LİSTESİ"); 

            while (rs.next()) {
                Object[] row = new Object[3];
                row[0] = rs.getObject("gun"); 
                row[1] = rs.getObject("yemekListesi"); 
                row[2] = rs.getObject("kahvaltiListesi"); 
                model.addRow(row);
            }

            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void yemekguncelle() {
        try {
            int selectedRow = yemektablo.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Lütfen bir satır seçin.");
                return;
            }
            String gun = (String) yemektablo.getValueAt(selectedRow, 0);
            String yeniCorba = JOptionPane.showInputDialog("Yeni Çorba:");
            if (yeniCorba == null) {
                return;
            }
            String yeniAnaYemek = JOptionPane.showInputDialog("Yeni Ana Yemek:");
            if (yeniAnaYemek == null) {
                return;
            }
            String yeniSuluYemek = JOptionPane.showInputDialog("Yeni Sulu Yemek:");
            if (yeniSuluYemek == null) {
                return;
            }
            String yeniEkstra = JOptionPane.showInputDialog("Yeni Ekstra:");
            if (yeniEkstra == null) {
                return;
            }

            if (yeniCorba.isEmpty() || yeniAnaYemek.isEmpty() || yeniSuluYemek.isEmpty() || yeniEkstra.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Lütfen boş alan bırakmayın.");
                return;
            }

            String yeniYemekListesi = yeniCorba + " | " + yeniAnaYemek + " | " + yeniSuluYemek + " | " + yeniEkstra;

            String sql = "UPDATE yemekmenu SET yemekListesi = ? WHERE gun = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, yeniYemekListesi);
            statement.setString(2, gun);
            statement.executeUpdate();

            loadDataFromDatabase();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    
		
	}
}

class DatabaseConnection {
    private static final String url = "jdbc:postgresql://localhost:5432/yurt";
    private static final String username = "postgres";
    private static final String password = "123";

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return conn;
    }
}
