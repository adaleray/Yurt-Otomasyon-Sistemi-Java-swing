package main;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;
import javax.swing.BorderFactory;


import java.awt.Font;
import java.awt.Color;

public class AyarlarYurt extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_2;
	private JTextField textField_3;
	private JPasswordField passwordField;
	private JCheckBox showPasswordCheckBox;

	// Database connection details
	private static final String url = "jdbc:postgresql://localhost:5432/yurt";
	private static final String username = "postgres";
	private static final String password = "123";


	public AyarlarYurt() {
		setTitle("Yurt Bilgi Değiştir");
		setResizable(false);
		setIconImage(Toolkit.getDefaultToolkit().getImage(LoginWindow.class.getResource("/main/logg.png")));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				ImageIcon imageIcon = new ImageIcon(getClass().getResource("indir.jpg"));
				Image image = imageIcon.getImage();
				g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);
			}
		};

		contentPane.setForeground(SystemColor.inactiveCaptionText);
		contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNewLabel = new JLabel("Yurt Mail:");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel.setBounds(20, 30, 100, 25);
		contentPane.add(lblNewLabel);

		textField = new JTextField();
		textField.setBounds(130, 30, 200, 25);
		contentPane.add(textField);
		textField.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel("Mail Şifre:");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel_1.setBounds(20, 70, 100, 25);
		contentPane.add(lblNewLabel_1);

		passwordField = new JPasswordField();
		passwordField.setBounds(130, 70, 200, 25);
		contentPane.add(passwordField);
		passwordField.setColumns(10);

		showPasswordCheckBox = new JCheckBox("Göster");
		showPasswordCheckBox.setBounds(340, 70, 120, 25);
		contentPane.add(showPasswordCheckBox);
		showPasswordCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				togglePasswordVisibility();
			}
		});

		JLabel lblNewLabel_2 = new JLabel("Yurt İsim:");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel_2.setBounds(20, 110, 100, 25);
		contentPane.add(lblNewLabel_2);

		textField_2 = new JTextField();
		textField_2.setBounds(130, 110, 200, 25);
		contentPane.add(textField_2);
		textField_2.setColumns(10);

		JLabel lblNewLabel_3 = new JLabel("Telefon:");
		lblNewLabel_3.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel_3.setBounds(20, 150, 100, 25);
		contentPane.add(lblNewLabel_3);

		textField_3 = new JTextField();
		textField_3.setBounds(130, 150, 200, 25);
		contentPane.add(textField_3);
		textField_3.setColumns(10);

		JButton gncellebutton = new JButton("Güncelle");
		gncellebutton.setBackground(Color.GREEN);
		gncellebutton.setBounds(160, 200, 120, 30);
		contentPane.add(gncellebutton);
		gncellebutton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateSettings();
			}
		});

		loadSettingsFromDatabase();
	}

	private void togglePasswordVisibility() {
		if (showPasswordCheckBox.isSelected()) {
			passwordField.setEchoChar((char) 0);
		} else {
			passwordField.setEchoChar('*');
		}
	}

	private void loadSettingsFromDatabase() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			conn = DriverManager.getConnection(url, username, password);

			String query = "SELECT yurtemail, mailpassword, yurtadi, yurttelno FROM yurtbilgi WHERE id = 1";
			pstmt = conn.prepareStatement(query);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				textField.setText(rs.getString("yurtemail"));
				passwordField.setText(rs.getString("mailpassword"));
				textField_2.setText(rs.getString("yurtadi"));
				textField_3.setText(rs.getString("yurttelno"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void updateSettings() {
		int response = JOptionPane.showConfirmDialog(this, "Değişiklikleri kaydetmek istiyor musunuz?", "Onay",
				JOptionPane.YES_NO_OPTION);
		if (response == JOptionPane.YES_OPTION) {
			Connection conn = null;
			PreparedStatement pstmt = null;

			try {
				conn = DriverManager.getConnection(url, username, password);

				String query = "UPDATE yurtbilgi SET yurtemail = ?, mailpassword = ?, yurtadi = ?, yurttelno = ? WHERE id = 1";
				pstmt = conn.prepareStatement(query);
				pstmt.setString(1, textField.getText());
				pstmt.setString(2, new String(passwordField.getPassword()));
				pstmt.setString(3, textField_2.getText());
				pstmt.setString(4, textField_3.getText());

				pstmt.executeUpdate();
				JOptionPane.showMessageDialog(this, "Değişiklikler kaydedildi");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (pstmt != null)
						pstmt.close();
					if (conn != null)
						conn.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
