package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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

public class Persistem extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField tc_text;
	private JTextField isim_text;
	private JTextField soyad_text;
	private JTextField telefon_text;
	private JTextField mail_text;
	private JTextField maas_text;
	private JComboBox<String> vardiyacombo;
	private JComboBox<String> cinsiyetcombo;
	private JTextField textField;
	private JTable table;
	private JComboBox<String> departmanCombo;
	private Connection connection;
	private EditPersonelDialog editDialog;



	public Persistem() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(YemekMenu.class.getResource("/main/logg.png")));
		setTitle("PERSONEL SİSTEM");
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

		JLabel per_tc = new JLabel("TC Numarası");
		per_tc.setForeground(new Color(255, 255, 255));
		per_tc.setBounds(8, 7, 91, 24);
		contentPane.add(per_tc);

		tc_text = new JTextField();
		tc_text.setBounds(8, 31, 76, 19);
		contentPane.add(tc_text);
		tc_text.setColumns(10);

		JLabel per_ad = new JLabel("İsim");
		per_ad.setForeground(new Color(255, 255, 255));
		per_ad.setBounds(130, 10, 53, 19);
		contentPane.add(per_ad);

		isim_text = new JTextField();
		isim_text.setBounds(107, 31, 76, 19);
		contentPane.add(isim_text);
		isim_text.setColumns(10);

		JLabel per_soyad = new JLabel("Soyad");
		per_soyad.setForeground(new Color(255, 255, 255));
		per_soyad.setBounds(211, 13, 40, 13);
		contentPane.add(per_soyad);

		soyad_text = new JTextField();
		soyad_text.setBounds(194, 31, 76, 19);
		contentPane.add(soyad_text);
		soyad_text.setColumns(10);

		JLabel cinsiyet = new JLabel("Cinsiyet");
		cinsiyet.setForeground(new Color(255, 255, 255));
		cinsiyet.setBounds(301, 13, 55, 13);
		contentPane.add(cinsiyet);

		cinsiyetcombo = new JComboBox<>();
		cinsiyetcombo.setModel(new DefaultComboBoxModel<>(new String[] { "ERKEK", "KIZ" }));
		cinsiyetcombo.setBounds(289, 30, 82, 21);
		contentPane.add(cinsiyetcombo);

		JLabel telefon = new JLabel("Telefon");
		telefon.setForeground(new Color(255, 255, 255));
		telefon.setBounds(406, 13, 57, 13);
		contentPane.add(telefon);

		telefon_text = new JTextField();
		telefon_text.setBounds(396, 31, 76, 19);
		contentPane.add(telefon_text);
		telefon_text.setColumns(10);

		JLabel mail = new JLabel("Mail");
		mail.setForeground(new Color(255, 255, 255));
		mail.setBounds(519, 13, 40, 13);
		contentPane.add(mail);

		mail_text = new JTextField();
		mail_text.setBounds(501, 31, 76, 19);
		contentPane.add(mail_text);
		mail_text.setColumns(10);

		JLabel maas = new JLabel("Maaş");
		maas.setForeground(new Color(255, 255, 255));
		maas.setBounds(27, 71, 63, 13);
		contentPane.add(maas);

		maas_text = new JTextField();
		maas_text.setBounds(8, 94, 82, 19);
		contentPane.add(maas_text);
		maas_text.setColumns(10);

		JLabel gecegündüz = new JLabel("Vardiya");
		gecegündüz.setForeground(new Color(255, 255, 255));
		gecegündüz.setBounds(116, 70, 53, 13);
		contentPane.add(gecegündüz);

		vardiyacombo = new JComboBox<>();
		vardiyacombo.setModel(new DefaultComboBoxModel<String>(new String[] { "GÜNDÜZ", "GECE" }));
		vardiyacombo.setBounds(107, 93, 76, 21);
		contentPane.add(vardiyacombo);

		departmanCombo = new JComboBox<String>();
		departmanCombo.setBounds(616, 30, 96, 21);
		contentPane.add(departmanCombo);

		JLabel lblNewLabel = new JLabel("Deneyim Süresi");
		lblNewLabel.setForeground(new Color(255, 255, 255));
		lblNewLabel.setBackground(new Color(255, 255, 255));
		lblNewLabel.setBounds(211, 71, 96, 13);
		contentPane.add(lblNewLabel);

		textField = new JTextField();
		textField.setBounds(211, 94, 89, 19);
		contentPane.add(textField);
		textField.setColumns(10);

		JButton eklebutton = new JButton("Ekle");
		eklebutton.setBackground(Color.GREEN);
		eklebutton.setBounds(382, 93, 81, 21);
		contentPane.add(eklebutton);

		JButton btnNewButton_1 = new JButton("Sil");
		btnNewButton_1.setBackground(Color.RED);
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					deletePersonel();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}

			}
		});
		btnNewButton_1.setBounds(501, 93, 81, 21);
		contentPane.add(btnNewButton_1);

		JButton btnNewButton_2 = new JButton("Düzenle");
		btnNewButton_2.setBackground(Color.CYAN);
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					showEditDialog(); 
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		});

		btnNewButton_2.setBounds(633, 93, 91, 21);
		contentPane.add(btnNewButton_2);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(8, 181, 772, 194);
		contentPane.add(scrollPane);

		table = new JTable();
		scrollPane.setViewportView(table);

		JLabel lblNewLabel_1 = new JLabel("Departman");
		lblNewLabel_1.setForeground(new Color(255, 255, 255));
		lblNewLabel_1.setBounds(633, 13, 79, 13);
		contentPane.add(lblNewLabel_1);

		try {
			connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/yurt", "postgres", "123");
			fillDepartmanComboBox();
			updateTable();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Ekle butonuna action listener ekleme
		eklebutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					addPersonel();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		});
	}

	private void fillDepartmanComboBox() throws SQLException {
		String query = "SELECT departman_adi FROM Departman";
		PreparedStatement statement = connection.prepareStatement(query);
		ResultSet resultSet = statement.executeQuery();

		List<String> departmanlar = new ArrayList<>();
		while (resultSet.next()) {
			departmanlar.add(resultSet.getString("departman_adi"));
		}

		departmanCombo.setModel(new DefaultComboBoxModel<>(departmanlar.toArray(new String[0])));
	}

	private void addPersonel() throws SQLException {

		String tc = tc_text.getText();

		String isim = isim_text.getText();
		String soyad = soyad_text.getText();
		String telefon = telefon_text.getText();
		String mail = mail_text.getText();
		String cinsiyet = (String) cinsiyetcombo.getSelectedItem();
		String maasText = maas_text.getText();
		String vardiya = (String) vardiyacombo.getSelectedItem();
		String deneyimSuresi = textField.getText();
		String departmanAdi = (String) departmanCombo.getSelectedItem();
		if (tc.isEmpty() || isim.isEmpty() || soyad.isEmpty() || telefon.isEmpty() || mail.isEmpty()
				|| maasText.isEmpty() || deneyimSuresi.isEmpty()) {
			StringBuilder errorMessage = new StringBuilder("Lütfen aşağıdaki alanları doldurun:\n");
			if (tc.isEmpty())
				errorMessage.append("- Tc Numarası\n");
			if (isim.isEmpty())
				errorMessage.append("- İsim\n");
			if (soyad.isEmpty())
				errorMessage.append("- Soyad\n");
			if (telefon.isEmpty())
				errorMessage.append("- Telefon\n");
			if (mail.isEmpty())
				errorMessage.append("- E-posta\n");
			if (maasText.isEmpty())
				errorMessage.append("- Maaş\n");
			if (deneyimSuresi.isEmpty())
				errorMessage.append("- Deneyim Süresi\n");
			JOptionPane.showMessageDialog(null, errorMessage.toString(), "Uyarı", JOptionPane.WARNING_MESSAGE);
			return; // Veri eksik olduğunda işlemi durdur
		}
		if (tc.length() != 11 || !tc.matches("[0-9]+")) {
			JOptionPane.showMessageDialog(null,
					"Geçersiz TC numarası. TC numarası 11 haneli olmalı ve sadece rakam içermelidir.");
			return;
		}
		int departmanID = getDepartmanID(departmanAdi);
		if (!isValidName(isim) || !isValidName(soyad)) {
			JOptionPane.showMessageDialog(null, "Geçersiz giriş.Lütfen geçerli bir isim ve soyad girin.");
			return;
		}

		if (!isValidPhone(telefon)) {
			JOptionPane.showMessageDialog(null, "Lütfen geçerli bir telefon numarası girin.Örnek : 0545 654 4835");
			return;
		}

		double maas;
		try {
			maas = Double.parseDouble(maasText);
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null, "Geçersiz maaş değeri.", "Hata", JOptionPane.ERROR_MESSAGE);
			return;
		}

		String insertQuery = "INSERT INTO Personel (tc_numarasi, isim, soyad, telefon, mail, cinsiyet, maas, vardiya, deneyim_suresi, departman_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
		insertStatement.setString(1, tc);
		insertStatement.setString(2, isim);
		insertStatement.setString(3, soyad);
		insertStatement.setString(4, telefon);
		insertStatement.setString(5, mail);
		insertStatement.setString(6, cinsiyet);
		insertStatement.setDouble(7, maas);
		insertStatement.setString(8, vardiya);
		insertStatement.setString(9, deneyimSuresi);
		insertStatement.setInt(10, departmanID);
		insertStatement.executeUpdate();

		updateTable();
		JOptionPane.showMessageDialog(null, "Personel başarıyla eklendi.");

		tc_text.setText("");
		isim_text.setText("");
		soyad_text.setText("");
		telefon_text.setText("");
		mail_text.setText("");
		maas_text.setText("");
		textField.setText("");
	}

	private int getDepartmanID(String departmanAdi) throws SQLException {
		String query = "SELECT departman_id FROM Departman WHERE departman_adi = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, departmanAdi);
		ResultSet resultSet = statement.executeQuery();
		resultSet.next();
		return resultSet.getInt("departman_id");
	}

	public void updateTable() throws SQLException {
		String query = "SELECT p.personel_id, p.tc_numarasi, p.isim, p.soyad, p.telefon, p.mail, p.cinsiyet, p.maas, p.vardiya, p.deneyim_suresi, d.departman_adi "
				+ "FROM Personel p " + "INNER JOIN Departman d ON p.departman_id = d.departman_id";
		PreparedStatement statement = connection.prepareStatement(query);
		ResultSet resultSet = statement.executeQuery();

		// Yeni veri tablosunu oluştur
		DefaultTableModel model = new DefaultTableModel();
		table.setModel(model);

		// Sütunları ekle
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

	public boolean isValidName(String name) {
		return name.matches("[a-zA-ZçÇğĞıİöÖşŞüÜ]+");
	}

	public boolean isValidPhone(String phone) {
		return phone.matches("[0-9]+");
	}

	private void deletePersonel() throws SQLException {
		int selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Lütfen bir personel seçin.", "Hata", JOptionPane.ERROR_MESSAGE);
			return;
		}

		String personelID = table.getModel().getValueAt(selectedRow, 0).toString();
		String isim = table.getModel().getValueAt(selectedRow, 2).toString();
		String soyad = table.getModel().getValueAt(selectedRow, 3).toString();

		int option = JOptionPane.showConfirmDialog(this,
				"Seçili personeli silmek istediğinizden emin misiniz?\n" + "İsim: " + isim + " Soyad: " + soyad,
				"Personel Silme Onayı", JOptionPane.YES_NO_OPTION);

		if (option == JOptionPane.YES_OPTION) {
			String deleteQuery = "DELETE FROM Personel WHERE personel_id = ?";
			PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
			deleteStatement.setInt(1, Integer.parseInt(personelID));
			deleteStatement.executeUpdate();

			updateTable();

			JOptionPane.showMessageDialog(this, "Personel başarıyla silindi.");
		}
	}

	private void showEditDialog() throws SQLException {
		int selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Lütfen bir personel seçin.", "Hata", JOptionPane.ERROR_MESSAGE);
			return;
		}
		String tc = table.getModel().getValueAt(selectedRow, 1).toString();
		String personelID = table.getModel().getValueAt(selectedRow, 0).toString();
		String isim = table.getModel().getValueAt(selectedRow, 2).toString();
		String soyad = table.getModel().getValueAt(selectedRow, 3).toString();
		String telefon = table.getModel().getValueAt(selectedRow, 4).toString();
		String mail = table.getModel().getValueAt(selectedRow, 5).toString();
		String cinsiyet = table.getModel().getValueAt(selectedRow, 6).toString();
		String maas = table.getModel().getValueAt(selectedRow, 7).toString();
		String vardiya = table.getModel().getValueAt(selectedRow, 8).toString();
		String deneyimSuresi = table.getModel().getValueAt(selectedRow, 9).toString();
		String departmanAdi = table.getModel().getValueAt(selectedRow, 10).toString();

		if (editDialog != null && editDialog.isVisible()) {
			editDialog.dispose();
		}
		System.out.println("Selected row ID: " + table.getModel().getValueAt(selectedRow, 0));

		// Yeni bir düzenleme ekranı oluştur ve göster
		editDialog = new EditPersonelDialog(personelID, tc, isim, soyad, telefon, mail, cinsiyet, maas, vardiya,
				deneyimSuresi, departmanAdi, this);

		editDialog.setVisible(true);
	}
}
