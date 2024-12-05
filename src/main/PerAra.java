package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.DefaultComboBoxModel;
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
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class PerAra extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField aramaText;
    private JComboBox<String> aramaKriter;
    private JTable table;
    private Connection connection;



    public PerAra() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(PerAra.class.getResource("/main/logg.png")));
        setTitle("Personel Ara");
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

        JLabel lblNewLabel = new JLabel("Arama Kriteri");
        lblNewLabel.setForeground(new Color(255, 255, 255));
        lblNewLabel.setBounds(51, 28, 109, 13);
        contentPane.add(lblNewLabel);

        aramaKriter = new JComboBox<String>();
        aramaKriter.setModel(new DefaultComboBoxModel<String>(new String[] { "İsim", "Soyad", "TC Numarası" }));
        aramaKriter.setBounds(23, 51, 137, 21);
        contentPane.add(aramaKriter);

        JLabel lblNewLabel_1 = new JLabel("Arama Metni");
        lblNewLabel_1.setForeground(new Color(255, 255, 255));
        lblNewLabel_1.setBounds(281, 28, 97, 13);
        contentPane.add(lblNewLabel_1);

        aramaText = new JTextField();
        aramaText.setBounds(256, 52, 122, 19);
        contentPane.add(aramaText);
        aramaText.setColumns(10);

        JButton araButton = new JButton("Ara");
        araButton.setBackground(Color.GREEN);
        araButton.setBounds(477, 51, 85, 21);
        contentPane.add(araButton);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(23, 130, 757, 245);
        contentPane.add(scrollPane);

        table = new JTable();
        scrollPane.setViewportView(table);

        JButton yenile = new JButton("Yenile");
        yenile.setBackground(Color.YELLOW);
        yenile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    searchPersonel("", ""); 
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        yenile.setBounds(672, 51, 81, 21);
        contentPane.add(yenile);

        JButton btnNewButton = new JButton("Geri");
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MainMenu mainMenu = new MainMenu();
                mainMenu.setVisible(true);
                dispose();
            }
        });
        btnNewButton.setBounds(571, 51, 81, 21);
        contentPane.add(btnNewButton);

        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/yurt", "postgres", "123");
            searchPersonel("", ""); 
        } catch (SQLException e) {
            e.printStackTrace();
        }

        araButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String kriter = aramaKriter.getSelectedItem().toString();
                    String metin = aramaText.getText();
                    if (metin.isEmpty()) {
                        JOptionPane.showMessageDialog(contentPane, "Lütfen arama metni giriniz.", "Uyarı", JOptionPane.WARNING_MESSAGE);
                    } else {
                        searchPersonel(kriter, metin);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                StringBuilder message = new StringBuilder();
                for (int i = 0; i < model.getColumnCount(); i++) {
                    String columnName = model.getColumnName(i);
                    Object value = model.getValueAt(selectedRow, i);
                    message.append(columnName).append(": ").append(value).append("\n");
                }
                JOptionPane.showMessageDialog(contentPane, message.toString(), "Seçilen Veri", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    private void searchPersonel(String kriter, String metin) throws SQLException {
        String query = "";
        if (kriter.equals("İsim")) {
            query = "SELECT p.personel_id, p.tc_numarasi, p.isim, p.soyad, p.telefon, p.mail, p.cinsiyet, p.maas, p.vardiya, p.deneyim_suresi, d.departman_adi " +
                    "FROM personel p " +
                    "INNER JOIN departman d ON p.departman_id = d.departman_id " +
                    "WHERE p.isim ILIKE ? ";
        } else if (kriter.equals("Soyad")) {
            query = "SELECT p.personel_id, p.tc_numarasi, p.isim, p.soyad, p.telefon, p.mail, p.cinsiyet, p.maas, p.vardiya, p.deneyim_suresi, d.departman_adi " +
                    "FROM personel p " +
                    "INNER JOIN departman d ON p.departman_id = d.departman_id " +
                    "WHERE p.soyad ILIKE ? ";
        } else if (kriter.equals("TC Numarası")) {
            query = "SELECT p.personel_id, p.tc_numarasi, p.isim, p.soyad, p.telefon, p.mail, p.cinsiyet, p.maas, p.vardiya, p.deneyim_suresi, d.departman_adi " +
                    "FROM personel p " +
                    "INNER JOIN departman d ON p.departman_id = d.departman_id " +
                    "WHERE p.tc_numarasi ILIKE ? ";
        } else {
            query = "SELECT p.personel_id, p.tc_numarasi, p.isim, p.soyad, p.telefon, p.mail, p.cinsiyet, p.maas, p.vardiya, p.deneyim_suresi, d.departman_adi " +
                    "FROM personel p " +
                    "INNER JOIN departman d ON p.departman_id = d.departman_id ";
        }

        PreparedStatement statement = connection.prepareStatement(query);
        if (!kriter.equals("")) {
            statement.setString(1, "%" + metin + "%");
        }
        ResultSet resultSet = statement.executeQuery();

        DefaultTableModel model = new DefaultTableModel();
        table.setModel(model);

        model.addColumn("Personel ID");
        model.addColumn("TC Numarası");
        model.addColumn("İsim");
        model.addColumn("Soyad");
        model.addColumn("Telefon");
        model.addColumn("Mail");
        model.addColumn("Cinsiyet");
        model.addColumn("Maaş");
        model.addColumn("Vardiya");
        model.addColumn("Deneyim Süresi");
        model.addColumn("Departman Adı");

        while (resultSet.next()) {
            Object[] row = { resultSet.getInt("personel_id"), resultSet.getString("tc_numarasi"),
                    resultSet.getString("isim"), resultSet.getString("soyad"), resultSet.getString("telefon"),
                    resultSet.getString("mail"), resultSet.getString("cinsiyet"), resultSet.getDouble("maas"),
                    resultSet.getString("vardiya"), resultSet.getString("deneyim_suresi"),
                    resultSet.getString("departman_adi") };
            model.addRow(row);
        }
    }
}
