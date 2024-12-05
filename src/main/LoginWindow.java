package main;

import javax.swing.*;


import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginWindow extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField usernameTextField;
    private JPasswordField passwordField;

    private String url = "jdbc:postgresql://localhost:5432/yurt";
    private String username = "postgres";
    private String password = "123";
    private Connection con = null;


    public LoginWindow() {
        setResizable(false);
        setIconImage(Toolkit.getDefaultToolkit().getImage(LoginWindow.class.getResource("/main/logg.png")));
        setTitle("Yurt Otomasyonu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 804, 424);
        setLocationRelativeTo(null);
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
        

        JLabel username_label = new JLabel("Kullanıcı Adı:");
        username_label.setFont(new Font("Tahoma", Font.BOLD, 16));
        username_label.setForeground(Color.BLACK);
        username_label.setBounds(193, 105, 136, 38);
        contentPane.add(username_label);

        JLabel sifre_label = new JLabel("Şifre:");
        sifre_label.setForeground(Color.BLACK);
        sifre_label.setFont(new Font("Tahoma", Font.BOLD, 16));
        sifre_label.setBounds(235, 154, 80, 38);
        contentPane.add(sifre_label);

        usernameTextField = new JTextField();
        usernameTextField.setBackground(Color.ORANGE);
        usernameTextField.setColumns(10);
        usernameTextField.setBounds(314, 117, 108, 19);
        contentPane.add(usernameTextField);

        passwordField = new JPasswordField();
        passwordField.setBackground(Color.ORANGE);
        passwordField.setColumns(10);
        passwordField.setBounds(314, 165, 108, 19);
        contentPane.add(passwordField);

        JButton btnNewButton = new AnimatedButton("Giriş"); 
        btnNewButton.setText("Giriş Yap");
        btnNewButton.setBackground(Color.GREEN);
        btnNewButton.setForeground(new Color(0, 128, 0));
        btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 16));
        btnNewButton.setBounds(314, 218, 108, 44);
        contentPane.add(btnNewButton);

        btnNewButton.addActionListener(e -> login());

        JLabel lblNewLabel_2 = new JLabel("GİRİŞ YAP");
        lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 26));
        lblNewLabel_2.setBounds(297, 43, 191, 38);
        contentPane.add(lblNewLabel_2);
        
        JButton geributton = new JButton("Geri Dön");
        geributton.setFont(new Font("Tahoma", Font.PLAIN, 13));
        geributton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                İlkMenu ilkMenu= new İlkMenu();
                ilkMenu.setVisible(true);
                dispose();
            }
        });
        geributton.setBackground(new Color(135, 206, 235));
        geributton.setBounds(314, 297, 108, 21);
        contentPane.add(geributton);

        ActionListener loginAction = e -> login();
        usernameTextField.addActionListener(loginAction);
        passwordField.addActionListener(loginAction);
    }

    private void login() {
        String enteredUsername = usernameTextField.getText();
        String enteredPassword = new String(passwordField.getPassword());
        try {
            con = DriverManager.getConnection(url, username, password);
            PreparedStatement pst = con.prepareStatement("SELECT * FROM t_login WHERE username=? AND password=?");
            pst.setString(1, enteredUsername);
            pst.setString(2, enteredPassword);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                String ad = rs.getString("ad");
                String soyad = rs.getString("soyad");
                // İlgili verileri Login sınıfına set edin
                login.setPassword(enteredPassword);
                login.setUsername(enteredUsername);
                login.setAd(ad);
                login.setSoyad(soyad);
                JOptionPane.showMessageDialog(null, "Hoş geldiniz " + enteredUsername + "!");
                openMainMenu();
                dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Hatalı kullanıcı adı veya şifre!");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Veritabanı hatası: " + ex.getMessage());
        }
    }

    private void openMainMenu() {
        MainMenu mainMenu = new MainMenu();
        mainMenu.setVisible(true);
    }
}

class AnimatedButton extends JButton {
    private static final long serialVersionUID = 1L;

    private static final Color DEFAULT_COLOR = Color.green;
    private static final Color HOVER_COLOR = Color.YELLOW;
    private static final Color PRESSED_COLOR = Color.RED;

    private Color currentColor;

    public AnimatedButton(String label) {
        super(label);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setFocusPainted(false);

        currentColor = DEFAULT_COLOR;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                animateToColor(HOVER_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                animateToColor(DEFAULT_COLOR);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                animateToColor(PRESSED_COLOR);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                animateToColor(HOVER_COLOR);
            }
        });
    }

    private void animateToColor(Color targetColor) {
        new Thread(() -> {
            while (!currentColor.equals(targetColor)) {
                int diffRed = targetColor.getRed() - currentColor.getRed();
                int diffGreen = targetColor.getGreen() - currentColor.getGreen();
                int diffBlue = targetColor.getBlue() - currentColor.getBlue();

                if (Math.abs(diffRed) < 3 && Math.abs(diffGreen) < 3 && Math.abs(diffBlue) < 3) {
                    currentColor = targetColor;
                    break;
                }

                if (diffRed != 0) {
                    currentColor = new Color(currentColor.getRed() + diffRed / 3, currentColor.getGreen(), currentColor.getBlue());
                }
                if (diffGreen != 0) {
                    currentColor = new Color(currentColor.getRed(), currentColor.getGreen() + diffGreen / 3, currentColor.getBlue());
                }
                if (diffBlue != 0) {
                    currentColor = new Color(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue() + diffBlue / 3);
                }

                repaint();

                try {
                    Thread.sleep(16); // ~60 FPS
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(currentColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        g2.dispose();
        super.paintComponent(g);
    }
}
