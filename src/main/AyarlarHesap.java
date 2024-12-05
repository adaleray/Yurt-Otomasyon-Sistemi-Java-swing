package main;

import javax.swing.*;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AyarlarHesap extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField usernameField;
    private JPasswordField oldPasswordField;
    private JPasswordField newPasswordField;

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/yurt";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "123";

 

    public AyarlarHesap() {
        setTitle("Şifre Değiştir");
        setResizable(false);
        setIconImage(Toolkit.getDefaultToolkit().getImage(LoginWindow.class.getResource("/main/logg.png")));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 407, 264);
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

        JLabel lblUsername = new JLabel("Kullanıcı Adı:");
        lblUsername.setBounds(50, 30, 100, 20);
        contentPane.add(lblUsername);

        String username = login.getUsername();
        if (username != null) {
            usernameField = new JTextField();
            usernameField.setText(username);
        } else {
            usernameField = new JTextField("Kullanıcı Adı Alınamadı");
            usernameField.setEditable(false); // Değiştirilemez hale getirir
        }
        usernameField.setBounds(160, 30, 150, 20);
        contentPane.add(usernameField);
        usernameField.setColumns(10);

        JLabel lblOldPassword = new JLabel("Eski Şifre:");
        lblOldPassword.setBounds(50, 60, 100, 20);
        contentPane.add(lblOldPassword);

        oldPasswordField = new JPasswordField();
        oldPasswordField.setBounds(160, 60, 150, 20);
        contentPane.add(oldPasswordField);

        JCheckBox oldPasswordCheckBox = new JCheckBox("Göster");
        oldPasswordCheckBox.setBounds(320, 60, 70, 20);
        oldPasswordCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (oldPasswordCheckBox.isSelected()) {
                    oldPasswordField.setEchoChar((char) 0);
                } else {
                    oldPasswordField.setEchoChar('*');
                }
            }
        });
        contentPane.add(oldPasswordCheckBox);

        JLabel lblNewPassword = new JLabel("Yeni Şifre:");
        lblNewPassword.setBounds(50, 90, 100, 20);
        contentPane.add(lblNewPassword);

        newPasswordField = new JPasswordField();
        newPasswordField.setBounds(160, 90, 150, 20);
        contentPane.add(newPasswordField);

        JCheckBox newPasswordCheckBox = new JCheckBox("Göster");
        newPasswordCheckBox.setBounds(320, 90, 70, 20);
        newPasswordCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (newPasswordCheckBox.isSelected()) {
                    newPasswordField.setEchoChar((char) 0);
                } else {
                    newPasswordField.setEchoChar('*');
                }
            }
        });
        contentPane.add(newPasswordCheckBox);

        JButton btnKaydet = new JButton("Kaydet");
        btnKaydet.setBackground(Color.GREEN);
        btnKaydet.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = login.getUsername();
                if (username != null) {
                    String oldPassword = new String(oldPasswordField.getPassword());
                    String newPassword = new String(newPasswordField.getPassword());

                    try {
                        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                        String query = "SELECT password FROM public.t_login WHERE username = ?";
                        PreparedStatement statement = conn.prepareStatement(query);
                        statement.setString(1, username);
                        ResultSet rs = statement.executeQuery();
                        if (rs.next()) {
                            String currentPassword = rs.getString("password");
                            if (currentPassword.equals(oldPassword)) {
                                query = "UPDATE public.t_login SET password = ? WHERE username = ?";
                                statement = conn.prepareStatement(query);
                                statement.setString(1, newPassword);
                                statement.setString(2, username);
                                int rowsUpdated = statement.executeUpdate();
                                if (rowsUpdated > 0) {
                                    JOptionPane.showMessageDialog(null, "Şifre başarıyla değiştirildi!");
                                } else {
                                    JOptionPane.showMessageDialog(null, "Şifre değiştirilirken bir hata oluştu!");
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "Eski şifreniz yanlış!");
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Kullanıcı bulunamadı!");
                        }
                        conn.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Kullanıcı verisi alınamadı!");
                }
            }
        });
        btnKaydet.setBounds(160, 130, 100, 30);
        contentPane.add(btnKaydet);
    }
}
