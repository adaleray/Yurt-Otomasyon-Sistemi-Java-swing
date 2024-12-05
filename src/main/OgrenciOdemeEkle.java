package main;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class OgrenciOdemeEkle extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final String URL = "jdbc:postgresql://localhost:5432/yurt";
    private static final String KULLANICI_ADI = "postgres";
    private static final String SIFRE = "123";
    
    @SuppressWarnings("unused")
	private login loggedInUser; 

    private JPanel contentPane;
    private JTextField tcTextField;
    private JTextField odemeMiktariTextField;
    private JButton ekleButton;
    private JTable table;
    private DefaultTableModel model;
    private JButton geributton;



    public OgrenciOdemeEkle(login loggedInUser) {
        this.loggedInUser = loggedInUser;
        initialize();
    }

    public OgrenciOdemeEkle() {
        initialize();
    }

    private void initialize() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(OgrenciOdemeEkle.class.getResource("/main/logg.png")));
        setTitle("ÖDEME AL\r\n");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 818, 490);
        contentPane = new JPanel();
        contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();
        contentPane.add(panel, BorderLayout.NORTH);

        JLabel tcLabel = new JLabel("T.C.");
        panel.add(tcLabel);

        tcTextField = new JTextField();
        tcTextField.setBackground(Color.CYAN);
        panel.add(tcTextField);
        tcTextField.setColumns(10);

        JLabel odemeMiktariLabel = new JLabel("Ödeme Miktarı");
        panel.add(odemeMiktariLabel);
        
                odemeMiktariTextField = new JTextField();
                odemeMiktariTextField.setBackground(Color.CYAN);
                odemeMiktariTextField. setEnabled(true);
                panel.add(odemeMiktariTextField);
                odemeMiktariTextField.setColumns(10);

        ekleButton = new JButton("Ödeme Ekle");
        ekleButton.setBackground(Color.GREEN);
        panel.add(ekleButton);
        
        geributton = new JButton("Geri");
        geributton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MainMenu mainMenu = new MainMenu();
                mainMenu.setVisible(true);
                dispose();
            }
        });
        geributton.setBackground(Color.LIGHT_GRAY);
        panel.add(geributton);

        JScrollPane scrollPane = new JScrollPane();
        contentPane.add(scrollPane, BorderLayout.CENTER);

        model = new DefaultTableModel();
        table = new JTable(model);
        table.setForeground(Color.BLACK);
        scrollPane.setViewportView(table);

        model.addColumn("Ödeme ID");
        model.addColumn("T.C.");
        model.addColumn("Ad");
        model.addColumn("Soyad");
        model.addColumn("Ödeme Miktarı");
        model.addColumn("Ödeme Tarihi");
        model.addColumn("Yönetici"); 

        ekleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String tc = tcTextField.getText();
                String odemeMiktariStr = odemeMiktariTextField.getText();

                if (!tc.isEmpty() && !odemeMiktariStr.isEmpty()) {
                    double odemeMiktari = Double.parseDouble(odemeMiktariStr);
                    odemeYap(tc, odemeMiktari, login.getUsername()); 
                } else {
                    JOptionPane.showMessageDialog(null, "T.C. ve Ödeme Miktarı alanları boş olamaz.");
                }
            }
        });

        odemeleriGetir();
    }

    private void odemeYap(String tc, double odemeMiktari, String kullaniciAdi) {
        if (!ogrVarMi(tc)) {
            JOptionPane.showMessageDialog(null, "Belirtilen T.C. numarasına sahip öğrenci bulunamadı.");
            return;
        }

        String ad = "";
        String soyad = "";

        String sqlAdSoyad = "SELECT ogr_isim, ogr_soyad FROM t_ogrenci WHERE ogr_tc = ?";
        try (Connection conn = DriverManager.getConnection(URL, KULLANICI_ADI, SIFRE);
             PreparedStatement pstmt = conn.prepareStatement(sqlAdSoyad)) {
            pstmt.setString(1, tc);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                ad = rs.getString("ogr_isim");
                soyad = rs.getString("ogr_soyad");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Öğrenci bilgileri getirilirken bir hata oluştu: " + e.getMessage());
            return;
        }

        String sql = "INSERT INTO t_odeme (ogr_tc, odeme_miktari, odeme_tarihi, ekleyen_kullanici) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, KULLANICI_ADI, SIFRE);
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, tc);
            pstmt.setDouble(2, odemeMiktari);
            pstmt.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
            pstmt.setString(4, kullaniciAdi);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int odemeID = generatedKeys.getInt(1);
                    Object[] row = new Object[]{odemeID, tc, ad, soyad, odemeMiktari, java.time.LocalDate.now(), kullaniciAdi};
                    model.addRow(row);
                    JOptionPane.showMessageDialog(null, "Ödeme başarıyla eklendi.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Ödeme eklenirken bir hata oluştu.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Ödeme eklenirken bir hata oluştu: " + ex.getMessage());
        }
    }

    private boolean ogrVarMi(String tc) {
        String sql = "SELECT COUNT(*) AS count FROM t_ogrenci WHERE ogr_tc = ?";
        try (Connection conn = DriverManager.getConnection(URL, KULLANICI_ADI, SIFRE);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tc);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("count");
                return count > 0;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Öğrenci varlık kontrolünde hata oluştu: " + e.getMessage());
        }
        return false;
    }

    private void odemeleriGetir() {
        model.setRowCount(0); 

        String sql = "SELECT t_odeme.odeme_id, t_odeme.ogr_tc, t_ogrenci.ogr_isim, t_ogrenci.ogr_soyad, " +
                     "t_odeme.odeme_miktari, t_odeme.odeme_tarihi, t_odeme.ekleyen_kullanici " +
                     "FROM t_odeme INNER JOIN t_ogrenci ON t_odeme.ogr_tc = t_ogrenci.ogr_tc";
        try (Connection conn = DriverManager.getConnection(URL, KULLANICI_ADI, SIFRE);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int odemeID = rs.getInt("odeme_id");
                String tc = rs.getString("ogr_tc");
                String ad = rs.getString("ogr_isim");
                String soyad = rs.getString("ogr_soyad");
                double odemeMiktari = rs.getDouble("odeme_miktari");
                String odemeTarihi = rs.getString("odeme_tarihi");
                String ekleyenKullanici = rs.getString("ekleyen_kullanici"); 
                model.addRow(new Object[]{odemeID, tc, ad, soyad, odemeMiktari, odemeTarihi, ekleyenKullanici});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Ödemeler getirilirken bir hata oluştu: " + ex.getMessage());
        }
    }
}
