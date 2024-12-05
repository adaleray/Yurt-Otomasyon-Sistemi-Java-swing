package main;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AyarlarYöneticiEkle extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable table;
	private DefaultTableModel tableModel;
	private JTextField usernameField;
	private JTextField passwordField;
	private JTextField adField;
	private JTextField soyadField;
	private JTextField emailField;
	private JTextField telefonField;
	private JCheckBox adminCheckBox;
	private Connection connection;

	private String url = "jdbc:postgresql://localhost:5432/yurt";
	private String dbUsername = "postgres";
	private String dbPassword = "123";



	public AyarlarYöneticiEkle() {
		setTitle("Yönetici Ekleme Sistemi");
		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/main/logg.png")));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 800, 500);
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
				return new Dimension(800, 500);
			}
		};

		contentPane.setForeground(SystemColor.inactiveCaptionText);
		contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		initializeUI();
		initializeDB();
		loadUserData();
	}

	private void initializeUI() {
		JLabel lblUsername = new JLabel("Kullanıcı Adı:");
		lblUsername.setBounds(10, 10, 100, 25);
		contentPane.add(lblUsername);

		usernameField = new JTextField();
		usernameField.setBounds(120, 10, 150, 25);
		contentPane.add(usernameField);

		JLabel lblPassword = new JLabel("Şifre:");
		lblPassword.setBounds(10, 40, 100, 25);
		contentPane.add(lblPassword);

		passwordField = new JTextField();
		passwordField.setBounds(120, 40, 150, 25);
		contentPane.add(passwordField);

		JLabel lblAd = new JLabel("Ad:");
		lblAd.setBounds(10, 70, 100, 25);
		contentPane.add(lblAd);

		adField = new JTextField();
		adField.setBounds(120, 70, 150, 25);
		contentPane.add(adField);

		JLabel lblSoyad = new JLabel("Soyad:");
		lblSoyad.setBounds(10, 100, 100, 25);
		contentPane.add(lblSoyad);

		soyadField = new JTextField();
		soyadField.setBounds(120, 100, 150, 25);
		contentPane.add(soyadField);

		JLabel lblEmail = new JLabel("E-posta:");
		lblEmail.setBounds(10, 130, 100, 25);
		contentPane.add(lblEmail);

		emailField = new JTextField();
		emailField.setBounds(120, 130, 150, 25);
		contentPane.add(emailField);

		JLabel lblTelefon = new JLabel("Telefon:");
		lblTelefon.setBounds(10, 160, 100, 25);
		contentPane.add(lblTelefon);

		telefonField = new JTextField();
		telefonField.setBounds(120, 160, 150, 25);
		contentPane.add(telefonField);

		JButton btnAdd = new JButton("Ekle");
		btnAdd.setBounds(146, 248, 100, 25);
		contentPane.add(btnAdd);

		JButton btnUpdate = new JButton("Güncelle");
		btnUpdate.setBounds(146, 195, 100, 25);
		contentPane.add(btnUpdate);

		JButton btnDelete = new JButton("Sil");
		btnDelete.setBounds(146, 314, 100, 25);
		contentPane.add(btnDelete);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(300, 10, 470, 440);
		contentPane.add(scrollPane);

		adminCheckBox = new JCheckBox("Admin");
		adminCheckBox.setBounds(10, 190, 100, 25);
		contentPane.add(adminCheckBox);

		tableModel = new DefaultTableModel(
				new Object[] { "ID", "Kullanıcı Adı", "Şifre", "Ad", "Soyad", "E-posta", "Telefon", "Rol" }, 0);
		table = new JTable(tableModel);
		scrollPane.setViewportView(table);
		table.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				int selectedRow = table.getSelectedRow();
				if (selectedRow >= 0) {
					usernameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
					passwordField.setText(tableModel.getValueAt(selectedRow, 2).toString());
					adField.setText(tableModel.getValueAt(selectedRow, 3).toString());
					soyadField.setText(tableModel.getValueAt(selectedRow, 4).toString());
					emailField.setText(tableModel.getValueAt(selectedRow, 5).toString());
					telefonField.setText(tableModel.getValueAt(selectedRow, 6).toString());
					adminCheckBox.setSelected(tableModel.getValueAt(selectedRow, 7).equals("Admin"));
				}
			}
		});
		btnAdd.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        if (usernameField.getText().trim().isEmpty() || passwordField.getText().trim().isEmpty() ||
		            adField.getText().trim().isEmpty() || soyadField.getText().trim().isEmpty() ||
		            emailField.getText().trim().isEmpty() || telefonField.getText().trim().isEmpty()) {
		            JOptionPane.showMessageDialog(null, "Lütfen gerekli tüm alanları doldurunuz.", "Eksik Bilgi", JOptionPane.WARNING_MESSAGE);
		        } else {
		            addUser();
		        }
		    }
		});

		btnUpdate.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        if (table.getSelectedRow() >= 0) {
		            updateUser();
		        } else {
		            JOptionPane.showMessageDialog(null, "Lütfen bir satır seçiniz.", "Hata", JOptionPane.WARNING_MESSAGE);
		        }
		    }
		});


		btnDelete.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        if (table.getSelectedRow() >= 0) {
		            deleteUser();
		        } else {
		            JOptionPane.showMessageDialog(null, "Lütfen bir satır seçiniz.", "Hata", JOptionPane.WARNING_MESSAGE);
		        }
		    }
		});

	}

	private void initializeDB() {
		try {
			connection = DriverManager.getConnection(url, dbUsername, dbPassword);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void loadUserData() {
		try {
			tableModel.setRowCount(0);

			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM t_login");
			while (rs.next()) {
				boolean isAdmin = rs.getBoolean("admin");
				String role = isAdmin ? "Admin" : "Yönetim";
				tableModel.addRow(new Object[] { rs.getInt("id"), rs.getString("username"), rs.getString("password"),
						rs.getString("ad"), rs.getString("soyad"), rs.getString("email"), rs.getString("telefon"),
						role });
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void addUser() {
		try {
			String sql = "INSERT INTO t_login (username, password, ad, soyad, email, telefon, admin) VALUES (?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, usernameField.getText());
			pstmt.setString(2, passwordField.getText());
			pstmt.setString(3, adField.getText());
			pstmt.setString(4, soyadField.getText());
			pstmt.setString(5, emailField.getText());
			pstmt.setString(6, telefonField.getText());
			pstmt.setBoolean(7, adminCheckBox.isSelected());

			pstmt.executeUpdate();

			loadUserData();
			clearFields();
			adminCheckBox.setSelected(false);

			JOptionPane.showMessageDialog(this, "Yeni kullanıcı başarıyla eklendi.", "Başarılı",
					JOptionPane.INFORMATION_MESSAGE);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void updateUser() {
		try {
			int selectedRow = table.getSelectedRow();
			if (selectedRow >= 0) {
				int id = (int) tableModel.getValueAt(selectedRow, 0);
				String sql = "UPDATE t_login SET username = ?, password = ?, ad = ?, soyad = ?, email = ?, telefon = ?, admin = ? WHERE id = ?";
				PreparedStatement pstmt = connection.prepareStatement(sql);
				int option = JOptionPane.showConfirmDialog(null, "Seçili kullanıcıyı güncellemek istiyor musunuz?",
						"Güncelleme İşlemi", JOptionPane.YES_NO_OPTION);
				if (option == JOptionPane.YES_OPTION) {
					pstmt.setString(1, usernameField.getText());
					pstmt.setString(2, passwordField.getText());
					pstmt.setString(3, adField.getText());
					pstmt.setString(4, soyadField.getText());
					pstmt.setString(5, emailField.getText());
					pstmt.setString(6, telefonField.getText());
					pstmt.setBoolean(7, adminCheckBox.isSelected());
					pstmt.setInt(8, id);

					pstmt.executeUpdate();
					tableModel.setValueAt(usernameField.getText(), selectedRow, 1);
					tableModel.setValueAt(passwordField.getText(), selectedRow, 2);
					tableModel.setValueAt(adField.getText(), selectedRow, 3);
					tableModel.setValueAt(soyadField.getText(), selectedRow, 4);
					tableModel.setValueAt(emailField.getText(), selectedRow, 5);
					tableModel.setValueAt(telefonField.getText(), selectedRow, 6);
					tableModel.setValueAt(adminCheckBox.isSelected() ? "Admin" : "Yönetim", selectedRow, 7);

					clearFields();

					JOptionPane.showMessageDialog(this, "Seçili kullanıcı başarıyla güncellendi.", "Başarılı",
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void deleteUser() {
		try {
			int selectedRow = table.getSelectedRow();
			if (selectedRow >= 0) {
				int id = (int) tableModel.getValueAt(selectedRow, 0);
				String ad = (String) tableModel.getValueAt(selectedRow, 3);
				String soyad = (String) tableModel.getValueAt(selectedRow, 4);

				int option = JOptionPane.showConfirmDialog(this,
						ad + " " + soyad + " kişinin yönetim kaydı silinecektir. Onaylıyor musunuz?",
						"Kullanıcı Silme İşlemi", JOptionPane.YES_NO_OPTION);
				if (option == JOptionPane.YES_OPTION) {
					String sql = "DELETE FROM t_login WHERE id = ?";
					PreparedStatement pstmt = connection.prepareStatement(sql);
					pstmt.setInt(1, id);
					pstmt.executeUpdate();
					tableModel.removeRow(selectedRow);
					clearFields();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void clearFields() {
		usernameField.setText("");
		passwordField.setText("");
		adField.setText("");
		soyadField.setText("");
		emailField.setText("");
		telefonField.setText("");
		adminCheckBox.setSelected(false);
	}
}
