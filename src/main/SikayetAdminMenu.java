package main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.sql.*;

public class SikayetAdminMenu extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/yurt";
    private static final String DB_USER = "postgres";
    private static final String DB_PASS = "123";

    private JPanel contentPane;
    private JTable table;
    private JTextField textField;
    
    

    public SikayetAdminMenu() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(SikayetMenu.class.getResource("/main/logg.png")));
        setTitle("Yurt Otomasyonu");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 804, 424);
        contentPane = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                URL url = getClass().getResource("erayy.jpg");
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
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane = new JScrollPane();
        contentPane.add(scrollPane, BorderLayout.CENTER);

        table = new JTable();
        scrollPane.setViewportView(table);

        JPanel panel = new JPanel();
        contentPane.add(panel, BorderLayout.SOUTH);

        JButton btnSil = new JButton("Seçileni Sil");
        btnSil.setBackground(Color.RED);
        btnSil.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(SikayetAdminMenu.this, "Lütfen silmek istediğiniz bir şikayeti seçin.", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int sikayetID = (int) table.getValueAt(row, 0);
                deleteSikayet(sikayetID);
            }
        });
        panel.add(btnSil);

        JButton btnDurumGuncelle = new JButton("Durum Güncelle");
        btnDurumGuncelle.setBackground(Color.GREEN);
        btnDurumGuncelle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(SikayetAdminMenu.this, "Lütfen durumunu güncellemek istediğiniz bir şikayeti seçin.", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int sikayetID = (int) table.getValueAt(row, 0);
                updateDurum(sikayetID);
            }
        });
        panel.add(btnDurumGuncelle);

        JButton btnYenile = new JButton("Yenile");
        btnYenile.setBackground(Color.ORANGE);
        btnYenile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadTableData(); // Tabloyu yenile
            }
        });
        panel.add(btnYenile);

   

        JPanel panel_1 = new JPanel();
        panel_1.setBackground(Color.LIGHT_GRAY);
        contentPane.add(panel_1, BorderLayout.NORTH);

        JLabel lblNewLabel = new JLabel("İsim veya Soyisim:");
        panel_1.add(lblNewLabel);

        textField = new JTextField();
        panel_1.add(textField);
        textField.setColumns(20);

        JButton btnAra = new JButton("Ara");
        btnAra.setBackground(Color.GREEN);
        btnAra.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String searchText = textField.getText().trim();
                searchSikayet(searchText);
            }
        });
        panel_1.add(btnAra);

        loadTableData();
        setColumnColors(); // Tablodaki hücre renklerini ayarla
    }

    private void loadTableData() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            String query = "SELECT id, ogr_isim, ogr_soyad, ogr_odanumber, sorun, durum, tarih::date FROM t_sikayet ORDER BY tarih DESC";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("ID");
            model.addColumn("İsim");
            model.addColumn("Soyad");
            model.addColumn("Oda Numarası");
            model.addColumn("Şikayet/Sorun");
            model.addColumn("Durum");
            model.addColumn("Oluşturulma Tarihi");

            while (resultSet.next()) {
                Object[] row = new Object[7];
                row[0] = resultSet.getInt("id");
                row[1] = resultSet.getString("ogr_isim");
                row[2] = resultSet.getString("ogr_soyad");
                row[3] = resultSet.getInt("ogr_odanumber");
                row[4] = resultSet.getString("sorun");
                row[5] = resultSet.getString("durum");
                row[6] = resultSet.getDate("tarih");
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

    private void searchSikayet(String searchText) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            String query = "SELECT id, ogr_isim, ogr_soyad, ogr_odanumber, sorun, durum, tarih::date FROM t_sikayet WHERE ogr_isim LIKE ? OR ogr_soyad LIKE ? ORDER BY tarih DESC";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, "%" + searchText + "%");
            statement.setString(2, "%" + searchText + "%");
            ResultSet resultSet = statement.executeQuery();

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("ID");
            model.addColumn("İsim");
            model.addColumn("Soyad");
            model.addColumn("Oda Numarası");
            model.addColumn("Şikayet/Sorun");
            model.addColumn("Durum");
            model.addColumn("Tarih");

            while (resultSet.next()) {
                Object[] row = new Object[7];
                row[0] = resultSet.getInt("id");
                row[1] = resultSet.getString("ogr_isim");
                row[2] = resultSet.getString("ogr_soyad");
                row[3] = resultSet.getInt("ogr_odanumber");
                row[4] = resultSet.getString("sorun");
                row[5] = resultSet.getString("durum");
                row[6] = resultSet.getDate("tarih");
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

    private void deleteSikayet(int sikayetID) {
        int confirm = JOptionPane.showConfirmDialog(SikayetAdminMenu.this, "Seçilen şikayeti silmek istediğinize emin misiniz?", "Şikayet Silme", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                String query = "DELETE FROM t_sikayet WHERE id=?";
                PreparedStatement statement = conn.prepareStatement(query);
                statement.setInt(1, sikayetID);
                int affectedRows = statement.executeUpdate();
                if (affectedRows > 0) {
                    JOptionPane.showMessageDialog(SikayetAdminMenu.this, "Şikayet başarıyla silindi.");
                    loadTableData();
                } else {
                    JOptionPane.showMessageDialog(SikayetAdminMenu.this, "Şikayet silinirken bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
                }
                statement.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateDurum(int sikayetID) {
        String[] options = {"Çözüldü", "Hala Yanıt Bekliyor"};
        int selection = JOptionPane.showOptionDialog(SikayetAdminMenu.this, "Şikayet durumunu güncelleyin:", "Durum Güncelleme", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (selection != JOptionPane.CLOSED_OPTION) {
            try {
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                String query = "UPDATE t_sikayet SET durum=? WHERE id=?";
                PreparedStatement statement = conn.prepareStatement(query);
                statement.setString(1, options[selection]);
                statement.setInt(2, sikayetID);
                int affectedRows = statement.executeUpdate();
                if (affectedRows > 0) {
                    JOptionPane.showMessageDialog(SikayetAdminMenu.this, "Şikayet durumu başarıyla güncellendi.");
                    loadTableData();
                } else {
                    JOptionPane.showMessageDialog(SikayetAdminMenu.this, "Şikayet durumu güncellenirken bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
                }
                statement.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void setColumnColors() {
        table.setDefaultRenderer(Object.class, new TableCellRenderer() {
            private final DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component renderer = DEFAULT_RENDERER.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String durum = (String) table.getValueAt(row, 5);
                if (durum != null && durum.equals("Çözüldü")) {
                    renderer.setBackground(Color.GREEN);
                } else {
                    renderer.setBackground(Color.RED);
                }
                return renderer;
            }
        });
    }

   
    
}
