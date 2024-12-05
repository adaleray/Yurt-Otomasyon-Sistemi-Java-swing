package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatLightLaf;

import java.awt.Toolkit;
import java.awt.Font;

public class İlkMenu extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {

			public void run() {
				try {
					 UIManager.setLookAndFeel(new FlatLightLaf());
					İlkMenu frame = new İlkMenu();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public İlkMenu() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(İlkMenu.class.getResource("/main/logg.png")));
        setResizable(false);
        setTitle("Yurt Otomasyonu");
       
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 804, 424);
        setLocationRelativeTo(null);

        contentPane = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(400, 400);
            }
        };
        
        contentPane.setLayout(null);
        contentPane.setBackground(new Color(104, 210, 232));
        
        JPanel panel = new JPanel();
        panel.setOpaque(false); // Transparent panel
        panel.setLayout(null);
        panel.setBounds(0, 0, 804, 424);

        JButton yemekbutton = createButton("restaurant.png", 43, 104, 165, 157);
        yemekbutton.addActionListener(e -> {
            YemekMenu yemekMenu = new YemekMenu();
            yemekMenu.setVisible(true);
            dispose();
        });
        panel.add(yemekbutton);
        createLabel("YEMEK-KAHVALTI", 43, 261, 165, 20, panel);

        JButton sikayetbutton = createButton("/main/complain.png", 567, 109, 165, 147);
        sikayetbutton.addActionListener(e -> {
            SikayetMenu sikayetMenu = new SikayetMenu();
            sikayetMenu.setVisible(true);
            dispose();
        });
        panel.add(sikayetbutton);
        createLabel("ŞİKAYET", 557, 256, 165, 20, panel);

        JButton loginbutton = createButton("/main/login1.png", 690, 00, 80, 80);
        loginbutton.addActionListener(e -> {
            LoginWindow loginWindow = new LoginWindow();
            loginWindow.setVisible(true);
            dispose();
        });
        panel.add(loginbutton);

        JButton giriscikisbutton = createButton("/main/key.png", 320, 109, 165, 147);
        giriscikisbutton.addActionListener(e -> {
            GirisKontrol girisKontrol= new GirisKontrol();
            girisKontrol.setVisible(true);
            dispose();
        });
        panel.add(giriscikisbutton);
        createLabel("GİRİŞ-ÇIKIŞ", 320, 230, 165, 80, panel);

        contentPane.add(panel);
        contentPane.setComponentZOrder(panel, 0);

        setContentPane(contentPane);
    }

	private JButton createButton(String iconPath, int x, int y, int width, int height) {
		JButton button = new JButton() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				if (getModel().isPressed()) {
					g.setColor(getBackground().darker());
				} else if (getModel().isRollover()) {
					g.setColor(getBackground().brighter());
				} else {
					g.setColor(getBackground());
				}
				g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
				super.paintComponent(g);
			}
		};

		button.setBounds(x, y, width, height);
		button.setBackground(new Color(0, 0, 0, 0)); 
		button.setForeground(Color.BLACK);
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setContentAreaFilled(false);
		button.setOpaque(false);

		URL iconURL = getClass().getResource(iconPath);
		if (iconURL != null) {
			ImageIcon icon = new ImageIcon(iconURL);
			Image img = icon.getImage();
			Image resizedImg = getScaledImage(img, width - 20, height - 20);
			button.setIcon(new ImageIcon(resizedImg));
		}

		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button.setBackground(Color.LIGHT_GRAY); // Light gray 
			}

			@Override
			public void mouseExited(MouseEvent e) {
				button.setBackground(new Color(0, 0, 0, 0)); // Transparent 
			}
		});
		return button;
	}

	private void createLabel(String text, int x, int y, int width, int height, JPanel panel) {
		JLabel label = new JLabel(text, SwingConstants.CENTER);
		label.setFont(new Font("Tahoma", Font.BOLD, 15));
		label.setBounds(x, y, width, height);
		label.setForeground(Color.WHITE);
		panel.add(label);
	}

	private Image getScaledImage(Image srcImg, int w, int h) {
		Image resizedImg = srcImg.getScaledInstance(w, h, Image.SCALE_SMOOTH);
		return resizedImg;
	}
}
