package main;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.*;

public class AyarlarBilgi extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final String DB_URL = "jdbc:postgresql://localhost:5432/asdasdada";
	private static final String DB_USER = "postgres";
	private static final String DB_PASSWORD = "123";

	private JPanel contentPane;
	private JTextField usernameField;
	private JTextField emailField;
	private JTextField telefonField;
	private JTextField adField;
	private JTextField soyadField;
	private JPanel resimPanel; //resim
	private JLabel kullaniciResimLabel;
	private JButton resimEkleButton;
	private byte[] kullaniciResimBytes;

	public AyarlarBilgi() {
		setTitle("BİLGİ DEĞİŞTİR");
		setResizable(false);
		setIconImage(Toolkit.getDefaultToolkit().getImage(LoginWindow.class.getResource("/main/logg.png")));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 605, 334);
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
		lblUsername.setBounds(8, 29, 100, 20);
		contentPane.add(lblUsername);

		usernameField = new JTextField();
		usernameField.setBounds(100, 30, 150, 20);
		contentPane.add(usernameField);
		usernameField.setColumns(10);
		usernameField.setEditable(false);

		JLabel lblEmail = new JLabel("E-mail:");
		lblEmail.setBounds(8, 59, 100, 20);
		contentPane.add(lblEmail);

		emailField = new JTextField();
		emailField.setBounds(100, 60, 150, 20);
		contentPane.add(emailField);
		emailField.setColumns(10);

		JLabel lblTelefon = new JLabel("Telefon:");
		lblTelefon.setBounds(8, 89, 100, 20);
		contentPane.add(lblTelefon);

		telefonField = new JTextField();
		telefonField.setBounds(100, 90, 150, 20);
		contentPane.add(telefonField);
		telefonField.setColumns(10);

		JLabel lblAd = new JLabel("Ad:");
		lblAd.setBounds(8, 119, 100, 20);
		contentPane.add(lblAd);

		adField = new JTextField();
		adField.setBounds(100, 120, 150, 20);
		contentPane.add(adField);
		adField.setColumns(10);

		JLabel lblSoyad = new JLabel("Soyad:");
		lblSoyad.setBounds(8, 149, 100, 20);
		contentPane.add(lblSoyad);

		soyadField = new JTextField();
		soyadField.setBounds(100, 150, 150, 20);
		contentPane.add(soyadField);
		soyadField.setColumns(10);

		// Resim Paneli
		resimPanel = new JPanel();
		resimPanel.setBounds(364, 10, 200, 200);
		resimPanel.setLayout(new BorderLayout()); // Resmi merkezlemek için BorderLayout kullanıyoruz
		contentPane.add(resimPanel);

		kullaniciResimLabel = new JLabel();
		resimPanel.add(kullaniciResimLabel, BorderLayout.CENTER);

		resimEkleButton = new JButton("Resim Ekle");
		resimEkleButton.setBackground(Color.CYAN);
		resimEkleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				int result = fileChooser.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					try {
						ImageIcon imageIcon = new ImageIcon(selectedFile.getPath());
						Image image = imageIcon.getImage();
						Image scaledImage = image.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
						ImageIcon scaledImageIcon = new ImageIcon(scaledImage);
						kullaniciResimLabel.setIcon(scaledImageIcon);
						ByteArrayOutputStream bos = new ByteArrayOutputStream();
						ImageIO.write((BufferedImage) scaledImage, "jpg", bos);
						kullaniciResimBytes = bos.toByteArray();
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		});
		resimEkleButton.setBounds(117, 198, 112, 30);
		contentPane.add(resimEkleButton);

		JButton btnGuncelle = new JButton("Güncelle");
		btnGuncelle.setBackground(Color.GREEN);
		btnGuncelle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String username = usernameField.getText();
				String email = emailField.getText();
				String telefon = telefonField.getText();
				String ad = adField.getText();
				String soyad = soyadField.getText();

				try {
					Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
					String query = "UPDATE public.t_login SET email = ?, telefon = ?, ad = ?, soyad = ?, kullanici_resim = ? WHERE username = ?";
					PreparedStatement statement = conn.prepareStatement(query);
					statement.setString(1, email);
					statement.setString(2, telefon);
					statement.setString(3, ad);
					statement.setString(4, soyad);
					statement.setBytes(5, kullaniciResimBytes);
					statement.setString(6, username);
					int rowsUpdated = statement.executeUpdate();
					if (rowsUpdated > 0) {
						JOptionPane.showMessageDialog(null, "Bilgiler başarıyla güncellendi!");
					} else {
						JOptionPane.showMessageDialog(null, "Bilgiler güncellenirken bir hata oluştu!");
					}
					conn.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		btnGuncelle.setBounds(117, 249, 112, 30);
		contentPane.add(btnGuncelle);

		// Kullanıcı adını alıp ilgili bilgileri getirme işlemi
		String username = login.getUsername();
		if (username != null) {
			try {
				Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
				String query = "SELECT email, telefon, ad, soyad, kullanici_resim FROM public.t_login WHERE username = ?";
				PreparedStatement statement = conn.prepareStatement(query);
				statement.setString(1, username);
				ResultSet rs = statement.executeQuery();
				if (rs.next()) {
					usernameField.setText(username);
					emailField.setText(rs.getString("email"));
					telefonField.setText(rs.getString("telefon"));
					adField.setText(rs.getString("ad"));
					soyadField.setText(rs.getString("soyad"));
					byte[] resimBytes = rs.getBytes("kullanici_resim");
					if (resimBytes != null) {
						ImageIcon resimIcon = new ImageIcon(resimBytes);
						Image image = resimIcon.getImage();
						Image scaledImage = image.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
						ImageIcon scaledResimIcon = new ImageIcon(scaledImage);
						kullaniciResimLabel.setIcon(scaledResimIcon);
						kullaniciResimBytes = resimBytes;
					}
				}
				conn.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
