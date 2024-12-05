package main;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class OdemeUpdate extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String URL = "jdbc:postgresql://localhost:5432/yurt";
    private static final String KULLANICI_ADI = "postgres";
    private static final String SIFRE = "123";

    private JPanel contentPane;
    private JButton duzenleButton;
    private JButton silButton;
    private JButton araButton;
    private JTextField adTextField;
    private JTextField soyadTextField;
    private JTable table;
    private DefaultTableModel model;
    private JButton Geri;



    public OdemeUpdate() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 719, 400);
        contentPane = new JPanel();
        contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();
        contentPane.add(panel, BorderLayout.NORTH);

        JLabel adLabel = new JLabel("İsim:");
        panel.add(adLabel);

        adTextField = new JTextField();
        panel.add(adTextField);
        adTextField.setColumns(10);

        JLabel soyadLabel = new JLabel("Soyad:");
        panel.add(soyadLabel);

        soyadTextField = new JTextField();
        panel.add(soyadTextField);
        soyadTextField.setColumns(10);

        araButton = new JButton("Ödeme Ara");
        araButton.setBackground(Color.PINK);
        panel.add(araButton);

        JScrollPane scrollPane = new JScrollPane();
        contentPane.add(scrollPane, BorderLayout.CENTER);

        model = new DefaultTableModel();
        table = new JTable(model);
        scrollPane.setViewportView(table);

        model.addColumn("Ödeme ID");
        model.addColumn("T.C.");
        model.addColumn("Ad");
        model.addColumn("Soyad");
        model.addColumn("Ödeme Miktarı");
        model.addColumn("Ödeme Tarihi");

        JPanel buttonPanel = new JPanel();
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        duzenleButton = new JButton("Ödeme Düzenle");
        duzenleButton.setBackground(Color.CYAN);
        buttonPanel.add(duzenleButton);

        silButton = new JButton("Ödeme Sil");
        silButton.setBackground(Color.RED);
        buttonPanel.add(silButton);
        
        Geri = new JButton("Geri");
        Geri.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		
        		MainMenu mainMenu = new MainMenu();
                mainMenu.setVisible(true);
                dispose();
        	}
        });
        Geri.setBackground(Color.LIGHT_GRAY);
        buttonPanel.add(Geri);

        duzenleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    String odemeID = model.getValueAt(selectedRow, 0).toString();
                    String tc = model.getValueAt(selectedRow, 1).toString();
                    String ad = model.getValueAt(selectedRow, 2).toString();
                    String soyad = model.getValueAt(selectedRow, 3).toString();
                    String miktar = model.getValueAt(selectedRow, 4).toString();
                    String tarih = model.getValueAt(selectedRow, 5).toString();

                    int confirm = JOptionPane.showConfirmDialog(null, "Ödeme ID: " + odemeID + "\nT.C.: " + tc +
                            "\nAd: " + ad + "\nSoyad: " + soyad + "\nÖdeme Miktarı: " + miktar + "\nÖdeme Tarihi: " + tarih +
                            "\n\nBu ödemeyi düzenlemek istediğinize emin misiniz?", "Düzenleme Onayı", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        String yeniMiktar = JOptionPane.showInputDialog(null, "Yeni ödeme miktarını giriniz:", miktar);
                        if (yeniMiktar != null && !yeniMiktar.isEmpty()) {
                            double miktarDouble = Double.parseDouble(yeniMiktar);
                            odemeDuzenle(odemeID, miktarDouble);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Lütfen düzenlemek istediğiniz ödemeyi seçiniz.");
                }
            }
        });

        silButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    String odemeID = model.getValueAt(selectedRow, 0).toString();
                    String tc = model.getValueAt(selectedRow, 1).toString();
                    String ad = model.getValueAt(selectedRow, 2).toString();
                    String soyad = model.getValueAt(selectedRow, 3).toString();
                    String miktar = model.getValueAt(selectedRow, 4).toString();
                    String tarih = model.getValueAt(selectedRow, 5).toString();

                    int confirm = JOptionPane.showConfirmDialog(null, "Ödeme ID: " + odemeID + "\nT.C.: " + tc +
                            "\nAd: " + ad + "\nSoyad: " + soyad + "\nÖdeme Miktarı: " + miktar + "\nÖdeme Tarihi: " + tarih +
                            "\n\nBu ödemeyi silmek istediğinize emin misiniz?", "Silme Onayı", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        int confirmDelete = JOptionPane.showConfirmDialog(null, "Ödeme ID: " + odemeID + " olan ödeme silinecektir. Onaylıyor musunuz?", "Silme Onayı", JOptionPane.YES_NO_OPTION);
                        if (confirmDelete == JOptionPane.YES_OPTION) {
                            odemeSil(odemeID);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Lütfen silmek istediğiniz ödemeyi seçiniz.");
                }
            }
        });

        araButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String ad = adTextField.getText().trim();
                String soyad = soyadTextField.getText().trim();
                if (!ad.isEmpty() && !soyad.isEmpty()) {
                    odemeleriAra(ad, soyad);
                } else {
                    JOptionPane.showMessageDialog(null, "Lütfen isim ve soyad alanlarını doldurun.");
                }
            }
        });

        odemeleriGetir();
    }

    private void odemeDuzenle(String odemeID, double yeniMiktar) {
        String sql = "UPDATE t_odeme SET odeme_miktari = ? WHERE odeme_id = ?";
        try (Connection conn = DriverManager.getConnection(URL, KULLANICI_ADI, SIFRE);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, yeniMiktar);
            pstmt.setInt(2, Integer.parseInt(odemeID));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(null, "Ödeme başarıyla güncellendi.");
                odemeleriGetir();
            } else {
                JOptionPane.showMessageDialog(null, "Ödeme güncellenirken bir hata oluştu.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Ödeme güncellenirken bir hata oluştu: " + ex.getMessage());
        }
    }

    private void odemeSil(String odemeID) {
        String sql = "DELETE FROM t_odeme WHERE odeme_id = ?";
        try (Connection conn = DriverManager.getConnection(URL, KULLANICI_ADI, SIFRE);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(odemeID));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(null, "Ödeme başarıyla silindi.");
                odemeleriGetir(); 
            } else {
                JOptionPane.showMessageDialog(null, "Ödeme silinirken bir hata oluştu.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Ödeme silinirken bir hata oluştu: " + ex.getMessage());
        }
    }

    private void odemeleriGetir() {
        model.setRowCount(0); // Tabloyu temizle

        String sql = "SELECT * FROM t_odeme ORDER BY odeme_id";
        try (Connection conn = DriverManager.getConnection(URL, KULLANICI_ADI, SIFRE);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int odemeID = rs.getInt("odeme_id");
                String tc = rs.getString("ogr_tc");
                String ad = getOgrenciAdi(tc);
                String soyad = getOgrenciSoyadi(tc);
                double odemeMiktari = rs.getDouble("odeme_miktari");
                String odemeTarihi = rs.getString("odeme_tarihi");

                model.addRow(new Object[]{odemeID, tc, ad, soyad, odemeMiktari, odemeTarihi});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Ödemeler getirilirken bir hata oluştu: " + ex.getMessage());
        }
    }

    private String getOgrenciAdi(String tc) {
        String ad = "";
        String sql = "SELECT ogr_isim FROM t_ogrenci WHERE ogr_tc = ?";
        try (Connection conn = DriverManager.getConnection(URL, KULLANICI_ADI, SIFRE);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tc);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                ad = rs.getString("ogr_isim");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Öğrenci adı getirilirken bir hata oluştu: " + ex.getMessage());
        }
        return ad;
    }

    private String getOgrenciSoyadi(String tc) {
        String soyad = "";
        String sql = "SELECT ogr_soyad FROM t_ogrenci WHERE ogr_tc = ?";
        try (Connection conn = DriverManager.getConnection(URL, KULLANICI_ADI, SIFRE);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tc);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                soyad = rs.getString("ogr_soyad");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Öğrenci soyadı getirilirken bir hata oluştu: " + ex.getMessage());
        }
        return soyad;
    }

    private void odemeleriAra(String ad, String soyad) {
        model.setRowCount(0); // Tabloyu temizle

        String sql = "SELECT * FROM t_odeme INNER JOIN t_ogrenci ON t_odeme.ogr_tc = t_ogrenci.ogr_tc WHERE ogr_isim LIKE ? AND ogr_soyad LIKE ?";
        try (Connection conn = DriverManager.getConnection(URL, KULLANICI_ADI, SIFRE);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + ad + "%");
            pstmt.setString(2, "%" + soyad + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int odemeID = rs.getInt("odeme_id");
                String tc = rs.getString("ogr_tc");
                double odemeMiktari = rs.getDouble("odeme_miktari");
                String odemeTarihi = rs.getString("odeme_tarihi");

                model.addRow(new Object[]{odemeID, tc, ad, soyad, odemeMiktari, odemeTarihi});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Ödemeler aranırken bir hata oluştu: " + ex.getMessage());
        }
    }
}
