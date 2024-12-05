package main;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;


import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.awt.Color;
import java.awt.Font;

public class AyarlarDepartman extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable table;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private JTextField adTextField;
    private JTextField kontenjanTextField;
    private JButton ekleButton;
    private JButton duzenleButton;
    private JButton silButton;
    private DatabaseManager databaseManager;

 

    public AyarlarDepartman() {
        databaseManager = new DatabaseManager();
        setTitle("Departman Bilgileri");
        setIconImage(Toolkit.getDefaultToolkit().getImage(LoginWindow.class.getResource("/main/logg.png")));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 800, 500);
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

        // Tablo
        String[] columnNames = {	"Departman İd","Departman Adı", "Kontenjan"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        refreshTable();
        scrollPane = new JScrollPane(table);
        scrollPane.setBounds(10, 10, 400, 400);
        contentPane.add(scrollPane);

        JLabel lblNewLabel = new JLabel("Departman Adı:");
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
        lblNewLabel.setBounds(450, 50, 100, 20);
        contentPane.add(lblNewLabel);

        adTextField = new JTextField();
        adTextField.setBounds(560, 50, 200, 20);
        contentPane.add(adTextField);
        adTextField.setColumns(10);

        JLabel lblKontenjan = new JLabel("Kontenjan:");
        lblKontenjan.setFont(new Font("Tahoma", Font.BOLD, 13));
        lblKontenjan.setBounds(450, 100, 100, 20);
        contentPane.add(lblKontenjan);

        kontenjanTextField = new JTextField();
        kontenjanTextField.setBounds(560, 100, 200, 20);
        contentPane.add(kontenjanTextField);
        kontenjanTextField.setColumns(10);

        ekleButton = new JButton("Ekle");
        ekleButton.setBackground(Color.GREEN);
        ekleButton.setBounds(450, 150, 100, 30);
        contentPane.add(ekleButton);

        duzenleButton = new JButton("Düzenle");
        duzenleButton.setBackground(Color.CYAN);
        duzenleButton.setBounds(560, 150, 100, 30);
        contentPane.add(duzenleButton);

        silButton = new JButton("Sil");
        silButton.setBackground(Color.RED);
        silButton.setBounds(670, 150, 100, 30);
        contentPane.add(silButton);

        ekleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String ad = adTextField.getText();
                int kontenjan = Integer.parseInt(kontenjanTextField.getText());
                
                // Onay mesajı göster
                int choice = JOptionPane.showConfirmDialog(null, "Departman eklemek istiyor musunuz?\nAd: " + ad + "\nKontenjan: " + kontenjan, "Departman Ekle", JOptionPane.YES_NO_OPTION);
                
                if (choice == JOptionPane.YES_OPTION) {
                    databaseManager.addDepartment(ad, kontenjan);
                    refreshTable();
                    // Text alanlarını temizle
                    adTextField.setText("");
                    kontenjanTextField.setText("");
                }
            }
        });



     // Düzenleme işlemi
        duzenleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    int departmanId = (int) table.getValueAt(selectedRow, 0);
                    String eskiAd = (String) table.getValueAt(selectedRow, 1);
                    int eskiKontenjan = (int) table.getValueAt(selectedRow, 2);
                    
                    JTextField adField = new JTextField(eskiAd);
                    JTextField kontenjanField = new JTextField(Integer.toString(eskiKontenjan));
                    
                    Object[] message = {
                        "Departman Adı:", adField,
                        "Kontenjan:", kontenjanField
                    };
                    
                    int option = JOptionPane.showConfirmDialog(null, message, "Departman Düzenle", JOptionPane.OK_CANCEL_OPTION);
                    
                    if (option == JOptionPane.OK_OPTION) {
                        String yeniAd = adField.getText();
                        int yeniKontenjan = Integer.parseInt(kontenjanField.getText());
                        
                        int choice = JOptionPane.showConfirmDialog(null, "Seçili departmanı düzenlemek istiyor musunuz?\nEski Ad: " + eskiAd + "\nYeni Ad: " + yeniAd + "\nEski Kontenjan: " + eskiKontenjan + "\nYeni Kontenjan: " + yeniKontenjan, "Departman Düzenle", JOptionPane.YES_NO_OPTION);
                        
                        if (choice == JOptionPane.YES_OPTION) {
                            databaseManager.editDepartment(departmanId, yeniAd, yeniKontenjan);
                            refreshTable();
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Lütfen bir departman seçin.");
                }
            }
        });


        // Silme işlemi
        silButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    int departmanId = (int) table.getValueAt(selectedRow, 0);
                    databaseManager.deleteDepartment(departmanId);
                    refreshTable();
                } else {
                    JOptionPane.showMessageDialog(null, "Lütfen bir departman seçin.");
                }
            }
        });
    }


    private void refreshTable() {
        tableModel.setRowCount(0);

        ResultSet rs = databaseManager.getDepartments();
        if (rs != null) {
            try {
                while (rs.next()) {
                    // Sıralama: Departman ID, Departman Adı, Kontenjan
                    Object[] row = {rs.getInt("departman_id"), rs.getString("departman_adi"), rs.getInt("kontenjan")};
                    tableModel.addRow(row);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


class DatabaseManager {
    private String url = "jdbc:postgresql://localhost:5432/yurt";
    private String username = "postgres";
    private String password = "123";
    private Connection con = null;

    public DatabaseManager() {
        try {
            // Veritabanına bağlanma
            con = DriverManager.getConnection(url, username, password);
            System.out.println("Veritabanına bağlandı.");
        } catch (SQLException e) {
            System.out.println("Veritabanına bağlanırken hata oluştu: " + e.getMessage());
        }
    }

    // Departman tablosundan verileri almak için
    public ResultSet getDepartments() {
        ResultSet rs = null;
        try {
            String query = "SELECT * FROM departman";
            PreparedStatement pst = con.prepareStatement(query);
            rs = pst.executeQuery();
        } catch (SQLException e) {
            System.out.println("Departman verilerini alırken hata oluştu: " + e.getMessage());
        }
        return rs;
    }

 // Yeni bir departman eklemek için
    public void addDepartment(String departmanAdi, int kontenjan) {
        try {
            String checkQuery = "SELECT * FROM departman WHERE departman_adi = ?";
            PreparedStatement checkPst = con.prepareStatement(checkQuery);
            checkPst.setString(1, departmanAdi);
            ResultSet rs = checkPst.executeQuery();
            
            if(rs.next()) {
                JOptionPane.showMessageDialog(null, "Bu departman zaten var.");
            } else {
                String query = "INSERT INTO departman (departman_adi, kontenjan) VALUES (?, ?)";
                PreparedStatement pst = con.prepareStatement(query);
                pst.setString(1, departmanAdi);
                pst.setInt(2, kontenjan);
                pst.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Departman eklenirken hata oluştu: " + e.getMessage());
        }
    }


    public void editDepartment(int departmanId, String yeniAd, int yeniKontenjan) {
        try {
            String query = "UPDATE departman SET departman_adi = ?, kontenjan = ? WHERE departman_id = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, yeniAd);
            pst.setInt(2, yeniKontenjan);
            pst.setInt(3, departmanId);
            pst.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Departman düzenlenirken hata oluştu: " + e.getMessage());
        }
    }

    // Bir departmanı silmek için
    public void deleteDepartment(int departmanId) {
        try {
            String query = "DELETE FROM departman WHERE departman_id = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, departmanId);
            pst.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Departman silinirken hata oluştu: " + e.getMessage());
        }
    }

    public void closeConnection() {
        if (con != null) {
            try {
                con.close();
                System.out.println("Veritabanı bağlantısı kapatıldı.");
            } catch (SQLException e) {
                System.out.println("Veritabanı bağlantısı kapatılırken hata oluştu: " + e.getMessage());
            }
        }
    }
}}
