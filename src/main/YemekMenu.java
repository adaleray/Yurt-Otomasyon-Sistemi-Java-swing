package main;

import java.awt.*;

import java.net.URL;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import javax.swing.border.LineBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class YemekMenu extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable yemektablo;
    private JLabel lblNewLabel;
    private JButton btnNewButton;



    public YemekMenu() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(YemekMenu.class.getResource("/main/logg.png")));
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

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(25, 155, 702, 220);
        contentPane.add(scrollPane);

        yemektablo = new JTable() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        yemektablo.setBorder(new LineBorder(new Color(0, 0, 0)));
        yemektablo.setBackground(new Color(0, 206, 209));
        yemektablo.setForeground(new Color(0, 0, 0));
        yemektablo.setRowSelectionAllowed(false); // Satır seçimini engelle
        yemektablo.setColumnSelectionAllowed(false); // Sütun seçimini engelle
        scrollPane.setViewportView(yemektablo);
        
        lblNewLabel = new JLabel("HAFTALIK YEMEK - KAHVALTI LİSTEMİZ");
        lblNewLabel.setForeground(Color.RED);
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        lblNewLabel.setBounds(198, 52, 465, 54);
        contentPane.add(lblNewLabel);
        
        btnNewButton = new JButton("Geri Dön");
        btnNewButton.setBackground(Color.GREEN);
        btnNewButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		İlkMenu ilkMenu = new İlkMenu();
        		ilkMenu.setVisible(true);
        		dispose(); // Giriş ekranını kapat
        	}
        });
        btnNewButton.setBounds(25, 25, 96, 21);
        contentPane.add(btnNewButton);

        loadDataFromDatabase();
    }

    private void loadDataFromDatabase() {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();

            String sql = "SELECT * FROM yemekmenu ORDER BY CASE WHEN gun='Pazartesi' THEN 1 WHEN gun='Salı' THEN 2 WHEN gun='Çarşamba' THEN 3 WHEN gun='Perşembe' THEN 4 WHEN gun='Cuma' THEN 5 WHEN gun='Cumartesi' THEN 6 WHEN gun='Pazar' THEN 7 END";
            ResultSet rs = stmt.executeQuery(sql);

            DefaultTableModel model = new DefaultTableModel();
            yemektablo.setModel(model);

            model.addColumn("Gün");
            model.addColumn("Kahvaltı Listesi");
            model.addColumn("Yemek Listesi");

            while (rs.next()) {
                String gun = rs.getString("gun");
                String kahvaltiListesi = rs.getString("kahvaltiListesi");
                String yemekListesi = rs.getString("yemekListesi");
                model.addRow(new Object[]{gun, kahvaltiListesi, yemekListesi});
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

class DatabaseConnectionEray {
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
