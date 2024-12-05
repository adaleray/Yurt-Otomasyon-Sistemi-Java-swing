package main;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.mail.*;
import javax.mail.internet.*;

import java.net.URL;
import java.sql.*;
import java.util.Properties;
import javax.swing.border.EmptyBorder;

public class ogrencidetay extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JComboBox<Integer> odaNumaralariComboBox;
	private JTextField tcTextField;
	private JTextField isimTextField;
	private JTextField soyadTextField;
	private JTextField telefonNumarasiTextField;
	private JTextField ucretTextField;
	private JTextField emailTextField;
	private JButton aramaButton;
	private static JTable table;
	private static DefaultTableModel model;
	private static String senderEmail; 
	private static String emailPassword = "";
	private static String yurtAdi = "";
	private static String yurtTelNo = "";



	public class VeritabaniBaglantisi {
		private static final String URL = "jdbc:postgresql://localhost:5432/yurt";
		private static final String KULLANICI_ADI = "postgres";
		private static final String SIFRE = "123";

		public static Connection baglan() throws SQLException {
			return DriverManager.getConnection(URL, KULLANICI_ADI, SIFRE);
		}

		public static void ogrenciEkle(String tc, String isim, String soyad, int odaNumarasi, String telefonNumarasi,
				double ucret, String email) {

			String baslik = "YURT KAYIT İŞLEMİ ";
			String mailmesaj = "<html><head><style>" + "body {font-family: Arial, sans-serif;}"
					+ ".container {max-width: 600px; margin: 0 auto;}"
					+ ".header {background-color: #f4f4f4; padding: 20px;}" + ".content {padding: 20px;}"
					+ ".footer {background-color: #f4f4f4; padding: 20px;}"
					+ ".info-box {border: 1px solid #ddd; background-color: #f9f9f9; padding: 15px; margin-bottom: 10px;}"
					+ "</style></head><body>" + "<div class='container'>"
					+ "<div class='header'><h2>YURT KAYDINIZ ALINMIŞTIR</h2></div>" + "<div class='content'>"
					+ "<div class='info-box'>" + "<p><strong>Sayın " + isim + " " + soyad + ",</strong></p>"
					+ "<p>KTÜ Yurtlarına hoş geldiniz! Kaydınız başarıyla oluşturulmuştur. Lütfen aşağıdaki detayları kontrol ediniz:</p>"
					+ "</div>" + "<div class='info-box'>" + "<p><strong>Kişisel Bilgiler:</strong></p>"
					+ "<p>T.C. Kimlik Numarası: " + tc + "</p>" + "<p>Telefon Numarası: " + telefonNumarasi + "</p>"
					+ "<p>E-posta Adresi: " + email + "</p>" + "</div>" + "<div class='info-box'>"
					+ "<p><strong>Yurt Bilgileri:</strong></p>" + "<p>Aylık Ücret: " + ucret + "</p>"
					+ "<p>Oda Numarası: " + odaNumarasi + "</p>" +

					"<div class='footer'>" + "<p>ANKARA /ÇANKAYA</p>" + "<p>Yurt İletişim: "
					+ yurtTelNo + "</p>" +

					"</div>" + "<p>İyi günler dileriz.</p>" + "</div>" + "</div></div></body></html>";

			if (odaDolu(odaNumarasi)) {
				JOptionPane.showMessageDialog(null, "Seçilen oda dolu. Lütfen başka bir oda seçin.");
				return;
			}

			String sql = "INSERT INTO t_ogrenci (ogr_tc, ogr_isim, ogr_soyad, ogr_odanumber, ogr_telefonnumber, ogr_ucret, ogr_email) VALUES (?, ?, ?, ?, ?, ?, ?)";

			try (Connection conn = baglan(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
				pstmt.setString(1, tc);
				pstmt.setString(2, isim);
				pstmt.setString(3, soyad);
				pstmt.setInt(4, odaNumarasi);
				pstmt.setString(5, telefonNumarasi);
				pstmt.setDouble(6, ucret);
				pstmt.setString(7, email);

				pstmt.executeUpdate();
				JOptionPane.showMessageDialog(null, "Öğrenci başarıyla eklendi.");

				odaKapasitesiniGuncelle(odaNumarasi);
				gonderEmail(tc, isim, soyad, email, telefonNumarasi, ucret, odaNumarasi, mailmesaj, baslik);
				model.setRowCount(0); 
				verileriGetir(); 
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, "Öğrenci eklenirken bir hata oluştu: " + e.getMessage());
			}
		}

		private static boolean odaDolu(int odaNumarasi) {
			try (Connection conn = baglan()) {
				String sql = "SELECT kapasite, bosyer FROM oda_kapasiteleri WHERE oda_numarasi = ?";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, odaNumarasi);
				ResultSet rs = pstmt.executeQuery();
				if (rs.next()) {
					int kapasite = rs.getInt("kapasite");
					int bosyer = rs.getInt("bosyer");
					return bosyer >= kapasite; 
				} else {
					JOptionPane.showMessageDialog(null, "Oda numarası bulunamadı.");
					return false;
				}
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null,
						"Oda doluluk durumu kontrol edilirken bir hata oluştu: " + e.getMessage());
				return false;
			}
		}

		private static void odaKapasitesiniGuncelle(int odaNumarasi) {
			try (Connection conn = baglan()) {
				String sql = "UPDATE oda_kapasiteleri SET bosyer = bosyer + 1 WHERE oda_numarasi = ?";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, odaNumarasi);
				pstmt.executeUpdate();
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, "Oda kapasitesi güncellenirken bir hata oluştu: " + e.getMessage());
			}
		}

		private static void odemeleriSil(String ogrTc) {
			String sql = "DELETE FROM t_odeme WHERE ogr_tc = ?";
			try (Connection conn = baglan(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
				pstmt.setString(1, ogrTc);
				pstmt.executeUpdate();

			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, "Ödemeler silinirken bir hata oluştu: " + e.getMessage());
			}
		}

		public static void ogrenciSil(int row) {
			String tc = (String) model.getValueAt(row, 0);

			int odemeSayisi = odemeBilgisiSayisi(tc);
			String isim = (String) model.getValueAt(row, 1);
			String soyad = (String) model.getValueAt(row, 2);
			// int odaNumarasi = (int) model.getValueAt(row, 3);
			String telefonNumarasi = (String) model.getValueAt(row, 4);
			double ucret = (double) model.getValueAt(row, 5);
			String email = (String) model.getValueAt(row, 6);

			if (odemeSayisi > 0) {
				int result = JOptionPane.showConfirmDialog(null,
						"\nİsim:" + "  " + isim + "\nSoyad: " + " " + soyad + "\n\n" + tc
								+ " TC kimlik numaralı öğrenciye ait " + odemeSayisi
								+ " ödeme bilgisi bulunmaktadır.\n\n Silmek istediğinize emin misiniz?",
						"Ödeme Bilgisi Var", JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.NO_OPTION) {
					return; 
				}
			}

			odemeleriSil(tc);

			// Öğrenciyi t_ogrenci tablosundan sil
			String sql = "DELETE FROM t_ogrenci WHERE ogr_tc = ?";
			try (Connection conn = baglan(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
				pstmt.setString(1, tc);
				int affectedRows = pstmt.executeUpdate();

				// başarılı
				if (affectedRows > 0) {
					int odaNumarasi = (int) model.getValueAt(row, 3);
					azaltBosyer(odaNumarasi);
					String mailmesaj = yurtAdi + "\n\n" + "YURT KAYDINIZ SİLİNDİ" + "\n\n" + "SÖZLEŞMENİZ SOLANDIRILDI."
							+ "\n\n" + "Sayın " + isim + " " + soyad + ",\n\n" + " T.C. kimlik numaranız: " + tc
							+ "\n\n" + " Telefon Numaranız: " + telefonNumarasi + "\n\n" + " Email Bilginiz: " + email
							+ "\n\n" + "Yeni dönem kayıtları için başvuruda bulunabilirsiniz.\n\n"
							+ "İyi günler dileriz.\n\n" + "Yurt Adresimiz: Bardacık Sokak No: 20 Kızılay/ANKARA\n"
							+ "Telefon Numaramız: " + yurtTelNo + "\n";

					String baslik = "YURT KAYIT SİLME İŞLEMİ";
					model.removeRow(row);

					gonderEmail(tc, isim, soyad, email, telefonNumarasi, ucret, odaNumarasi, mailmesaj, baslik);
					JOptionPane.showMessageDialog(null, "Öğrenci başarıyla silindi.Mail gönderiliyor.");
				} else {
					JOptionPane.showMessageDialog(null, "Öğrenci silinirken bir hata oluştu.");
				}
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, "Öğrenci silinirken bir hata oluştu: " + e.getMessage());
			}
		}

		private static int odemeBilgisiSayisi(String ogrTc) {
			int odemeSayisi = 0;
			String sql = "SELECT COUNT(*) AS odeme_sayisi FROM t_odeme WHERE ogr_tc = ?";
			try (Connection conn = VeritabaniBaglantisi.baglan();
					PreparedStatement pstmt = conn.prepareStatement(sql)) {
				pstmt.setString(1, ogrTc);
				ResultSet rs = pstmt.executeQuery();
				if (rs.next()) {
					odemeSayisi = rs.getInt("odeme_sayisi");
				}
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null,
						"Ödeme bilgisi sayısı alınırken bir hata oluştu: " + e.getMessage());
			}
			return odemeSayisi;
		}

		// Bosyer değerini azaltan metot
		private static void azaltBosyer(int odaNumarasi) {
			try (Connection conn = baglan()) {
				String sql = "UPDATE oda_kapasiteleri SET bosyer = bosyer - 1 WHERE oda_numarasi = ?";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, odaNumarasi);
				pstmt.executeUpdate();
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, "Bosyer değeri azaltılırken bir hata oluştu: " + e.getMessage());
			}
		}
	}

	public ogrencidetay() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(ogrencidetay.class.getResource("/main/logg.png")));
		setTitle("Yurt Otomasyonu");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 804, 424);
		odaNumaralariComboBox = new JComboBox<>();
		odaNumaralariComboBox.setBounds(315, 37, 99, 19);

		odaNumaralariCek();

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
		contentPane.add(odaNumaralariComboBox);
		contentPane.setForeground(SystemColor.inactiveCaptionText);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		tcTextField = new JTextField();
		tcTextField.setBackground(Color.WHITE);
		tcTextField.setBounds(8, 37, 76, 19);
		contentPane.add(tcTextField);
		tcTextField.setColumns(10);

		JLabel tcLabel = new JLabel("T.C");
		tcLabel.setForeground(Color.BLACK);
		tcLabel.setBackground(Color.RED);
		tcLabel.setBounds(35, 14, 59, 13);
		contentPane.add(tcLabel);

		JLabel isimLabel = new JLabel("İsim");
		isimLabel.setForeground(Color.BLACK);
		isimLabel.setBackground(Color.RED);
		isimLabel.setBounds(145, 14, 40, 13);
		contentPane.add(isimLabel);

		isimTextField = new JTextField();
		isimTextField.setBounds(123, 37, 69, 19);
		contentPane.add(isimTextField);
		isimTextField.setColumns(10);

		JLabel soyadLabel = new JLabel("Soyad");
		soyadLabel.setForeground(Color.BLACK);
		soyadLabel.setBounds(237, 14, 40, 13);
		contentPane.add(soyadLabel);

		soyadTextField = new JTextField();
		soyadTextField.setBounds(218, 37, 76, 19);
		contentPane.add(soyadTextField);
		soyadTextField.setColumns(10);

		JLabel odaNumarasiLabel = new JLabel("Oda Numarası");
		odaNumarasiLabel.setBounds(315, 14, 88, 13);
		contentPane.add(odaNumarasiLabel);

		telefonNumarasiTextField = new JTextField();
		telefonNumarasiTextField.setBounds(445, 37, 76, 19);
		contentPane.add(telefonNumarasiTextField);
		telefonNumarasiTextField.setColumns(10);

		JLabel telefonNumarasiLabel = new JLabel("Telefon Numarası");
		telefonNumarasiLabel.setBounds(440, 10, 119, 21);
		contentPane.add(telefonNumarasiLabel);

		JLabel ucretLabel = new JLabel("Aylık Ücret");
		ucretLabel.setBounds(585, 14, 99, 13);
		contentPane.add(ucretLabel);

		ucretTextField = new JTextField();
		ucretTextField.setBounds(585, 37, 76, 19);
		contentPane.add(ucretTextField);
		ucretTextField.setColumns(10);

		JLabel emailLabel = new JLabel("E-mail");
		emailLabel.setBounds(700, 14, 88, 13);
		contentPane.add(emailLabel);

		emailTextField = new JTextField();
		emailTextField.setBounds(680, 37, 100, 19);
		contentPane.add(emailTextField);
		emailTextField.setColumns(10);
		JButton duzenleButton = new JButton("Düzenle");
		duzenleButton.setBackground(Color.YELLOW);
		duzenleButton.setBounds(541, 113, 99, 21);
		contentPane.add(duzenleButton);
		duzenleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selectedRow = table.getSelectedRow();
				int selectedColumn = table.getSelectedColumn();
				if (selectedRow != -1 && selectedColumn != -1) {
					// Sadece sayısal veri girişi beklenen alanları kontrol et
					if (selectedColumn == 0 || selectedColumn == 3 || selectedColumn == 4 || selectedColumn == 5) {
						Object currentValue = model.getValueAt(selectedRow, selectedColumn);
						if (currentValue != null) {
							String newValue = JOptionPane.showInputDialog(null, "Yeni değeri girin:",
									currentValue.toString());
							if (newValue != null && !newValue.isEmpty()) {
								// Sayısal olmayan veri girilirse uyarı ver
								if (!isNumeric(newValue)) {
									JOptionPane.showMessageDialog(null, "Geçerli bir sayı girin.");
									return;
								}
								int result = JOptionPane.showConfirmDialog(null,
										"Bu hücrenin değerini \"" + newValue
												+ "\" ile değiştirmek istediğinize emin misiniz?",
										"Değer Değiştirme Onayı", JOptionPane.YES_NO_OPTION);
								if (result == JOptionPane.YES_OPTION) {
									model.setValueAt(newValue, selectedRow, selectedColumn);
								}
							} else {
								JOptionPane.showMessageDialog(null, "Lütfen geçerli bir değer girin.");
							}
						} else {
							JOptionPane.showMessageDialog(null, "Lütfen geçerli bir değer girin.");
						}
					} else {
						// Diğer alanlar için doğrudan düzenleme yapılabilir
						Object currentValue = model.getValueAt(selectedRow, selectedColumn);
						String newValue = JOptionPane.showInputDialog(null, "Yeni değeri girin:",
								currentValue.toString());
						if (newValue != null && !newValue.isEmpty()) {
							int result = JOptionPane.showConfirmDialog(null,
									"Bu hücrenin değerini \"" + newValue
											+ "\" ile değiştirmek istediğinize emin misiniz?",
									"Değer Değiştirme Onayı", JOptionPane.YES_NO_OPTION);
							if (result == JOptionPane.YES_OPTION) {
								model.setValueAt(newValue, selectedRow, selectedColumn);
							}
						} else {
							JOptionPane.showMessageDialog(null, "Lütfen geçerli bir değer girin.");
						}
					}
				} else {
					JOptionPane.showMessageDialog(null, "Lütfen düzenlemek istediğiniz hücreyi seçin.");
				}
			}
		});

		JButton ekleButton = new JButton("Ekle");
		ekleButton.setBackground(Color.GREEN);
		ekleButton.setBounds(452, 113, 81, 21);
		contentPane.add(ekleButton);
		ekleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String tc = tcTextField.getText();
				if (tc.length() != 11 || !tc.matches("[0-9]+")) {
					JOptionPane.showMessageDialog(null,
							"Geçersiz TC numarası. TC numarası 11 haneli olmalı ve sadece rakam içermelidir.");
					return;
				}
				String isim = isimTextField.getText();
				String soyad = soyadTextField.getText();
				String odaNumarasiStr = odaNumaralariComboBox.getSelectedItem().toString(); // ComboBox'tan seçilen
																							// öğeyi al
				String telefonNumarasi = telefonNumarasiTextField.getText();
				String ucretStr = ucretTextField.getText();
				String email = emailTextField.getText();

				int odaNumarasi = Integer.parseInt(odaNumarasiStr); // Artık seçilen öğe bir String olduğu için doğrudan
																	// parçalayabilirsiniz

				// Sayısal olmayan veri girilirse uyarı ver
				if (!isNumeric(ucretStr)) {
					JOptionPane.showMessageDialog(null, "Aylık ücret sayısal olmalıdır.");
					return;
				}

				double ucret = Double.parseDouble(ucretStr);

				VeritabaniBaglantisi.ogrenciEkle(tc, isim, soyad, odaNumarasi, telefonNumarasi, ucret, email);
				tcTextField.setText("");
				isimTextField.setText("");
				soyadTextField.setText("");
				telefonNumarasiTextField.setText("");
				ucretTextField.setText("");
				emailTextField.setText("");
			}

		});

		aramaButton = new JButton("İsim Ara");
		aramaButton.setBackground(Color.BLUE);
		aramaButton.setBounds(8, 113, 86, 21);
		contentPane.add(aramaButton);
		aramaButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aramaYap();

			}
		});

		JButton silButton = new JButton("Sil");
		silButton.setBackground(Color.RED);
		silButton.setBounds(351, 113, 81, 21);
		contentPane.add(silButton);
		silButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int dialogResult = JOptionPane.showConfirmDialog(null, "Bu Öğrenciyi gerçekten silmek istiyor musunuz?",
						"Uyarı", JOptionPane.YES_NO_OPTION);
				// Kullanıcı "Evet" seçeneğini seçerse, seçili satırı sil
				if (dialogResult == JOptionPane.YES_OPTION) {
					int selectedRow = table.getSelectedRow();
					if (selectedRow != -1) {
						VeritabaniBaglantisi.ogrenciSil(selectedRow);
					} else {
						JOptionPane.showMessageDialog(null, "Lütfen silmek istediğiniz öğrenciyi seçin.");
					}
				}
			}
		});

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(8, 160, 780, 215);
		contentPane.add(scrollPane);

		model = new DefaultTableModel();
		table = new JTable(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(table);

	

		JButton btnNewButton = new JButton("Oda Ara");
		btnNewButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        String odaNumarasiStr = JOptionPane.showInputDialog("Aranacak oda numarasını girin:");
		        if (odaNumarasiStr != null && !odaNumarasiStr.isEmpty()) { // Kullanıcı bir değer girdiyse
		            try {
		                int odaNumarasiAranan = Integer.parseInt(odaNumarasiStr);
		                odaAra(odaNumarasiAranan);
		            } catch (NumberFormatException ex) {
		                // Kullanıcı geçersiz bir oda numarası girdiğinde yapılacak işlem
		                JOptionPane.showMessageDialog(null, "Geçersiz oda numarası girdiniz. Lütfen tekrar deneyin.");
		            }
		        } else {
		            // Kullanıcı bir değer girmeden butona tıkladığında yapılacak işlem
		            JOptionPane.showMessageDialog(null, "Oda numarası boş olamaz. Lütfen bir oda numarası girin.");
		        }
		    }
		});

		btnNewButton.setBackground(Color.CYAN);
		btnNewButton.setBounds(123, 113, 88, 21);
		contentPane.add(btnNewButton);

		model.addColumn("T.C.");
		model.addColumn("İsim");
		model.addColumn("Soyad");
		model.addColumn("Oda Numarası");
		model.addColumn("Telefon Numarası");
		model.addColumn("Aylık Ücret");
		model.addColumn("E-mail");

		verileriGetir();
	}

	private void odaNumaralariCek() {
		String sql = "SELECT oda_numarasi FROM oda_kapasiteleri";

		try (Connection conn = VeritabaniBaglantisi.baglan();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				int odaNumarasi = rs.getInt("oda_numarasi");
				odaNumaralariComboBox.addItem(odaNumarasi);
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Oda numaraları getirilirken bir hata oluştu: " + e.getMessage());
		}
	}

	private static void verileriGetir() {
		String sql = "SELECT * FROM t_ogrenci";

		try (Connection conn = VeritabaniBaglantisi.baglan();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				String tc = rs.getString("ogr_tc");
				String isim = rs.getString("ogr_isim");
				String soyad = rs.getString("ogr_soyad");
				int odaNumarasi = rs.getInt("ogr_odanumber");
				String telefonNumarasi = rs.getString("ogr_telefonnumber");
				double ucret = rs.getDouble("ogr_ucret");
				String email = rs.getString("ogr_email");

				Object[] row = { tc, isim, soyad, odaNumarasi, telefonNumarasi, ucret, email };
				model.addRow(row);
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null,
					"Veritabanından veriler getirilirken bir hata oluştu: " + e.getMessage());
		}
	}

	private void aramaYap() {
		String aramaMetni = JOptionPane.showInputDialog(null, "İsim yada Soyad girişi yapınız:");
		if (aramaMetni != null && !aramaMetni.isEmpty()) {
			String sql = "SELECT * FROM t_ogrenci WHERE ogr_tc LIKE ? OR ogr_isim LIKE ? OR ogr_soyad LIKE ?";
			try (Connection conn = VeritabaniBaglantisi.baglan();
					PreparedStatement pstmt = conn.prepareStatement(sql)) {
				pstmt.setString(1, "%" + aramaMetni + "%");
				pstmt.setString(2, "%" + aramaMetni + "%");
				pstmt.setString(3, "%" + aramaMetni + "%");
				ResultSet rs = pstmt.executeQuery();
				model.setRowCount(0);
				int kayitSayisi = 0;
				while (rs.next()) {
					kayitSayisi++;
					String tc = rs.getString("ogr_tc");
					String isim = rs.getString("ogr_isim");
					String soyad = rs.getString("ogr_soyad");
					int odaNumarasi = rs.getInt("ogr_odanumber");
					String telefonNumarasi = rs.getString("ogr_telefonnumber");
					double ucret = rs.getDouble("ogr_ucret");
					String email = rs.getString("ogr_email");
					Object[] row = { tc, isim, soyad, odaNumarasi, telefonNumarasi, ucret, email };
					model.addRow(row);
				}
				if (kayitSayisi > 0) {
					JOptionPane.showMessageDialog(null, kayitSayisi + " kayıt bulundu.");
				} else {
					JOptionPane.showMessageDialog(null, "Arama sonucunda hiçbir kayıt bulunamadı.");
				}
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, "Arama işlemi sırasında bir hata oluştu: " + e.getMessage());
			}
		} else {
			JOptionPane.showMessageDialog(null, "Lütfen geçerli bir arama metni girin.");
		}
	}

	public void odaAra(int odaNumarasi) {
		model.setRowCount(0);

		String sql = "SELECT * FROM t_ogrenci WHERE ogr_odanumber = ?";
		try (Connection conn = VeritabaniBaglantisi.baglan(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, odaNumarasi);
			ResultSet rs = pstmt.executeQuery();

			int count = 0;
			while (rs.next()) {
				count++;
				String tc = rs.getString("ogr_tc");
				String isim = rs.getString("ogr_isim");
				String soyad = rs.getString("ogr_soyad");
				@SuppressWarnings("unused")
				int odaNum = rs.getInt("ogr_odanumber");
				String telefonNumarasi = rs.getString("ogr_telefonnumber");
				double ucret = rs.getDouble("ogr_ucret");
				String email = rs.getString("ogr_email");

				Object[] row = { tc, isim, soyad, odaNumarasi, telefonNumarasi, ucret, email };
				model.addRow(row);
			}

			if (count > 0) {
				JOptionPane.showMessageDialog(null, "Toplam " + count + " sonuç bulundu.");
			} else {
				JOptionPane.showMessageDialog(null, "Aranan odada kimse kalmamış.");
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null,
					"Veritabanından veriler getirilirken bir hata oluştu: " + e.getMessage());
		}
	}

	private static void gonderEmail(String tc, String isim, String soyad, String email, String telefonNumarasi,
			double ucret, int odaNum, String mailmesaj, String baslik) {

		try (Connection conn = DatabaseConnection.getConnection()) {
			String sql = "SELECT yurtemail, mailpassword, yurtadi, yurttelno FROM yurtbilgi WHERE id = 1";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				senderEmail = rs.getString("yurtemail");
				emailPassword = rs.getString("mailpassword");
				yurtAdi = rs.getString("yurtadi");
				yurtTelNo = rs.getString("yurttelno");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true"); // Enable STARTTLS for secure communication
		props.put("mail.smtp.host", "smtp.gmail.com"); // Gmail's SMTP server address
		props.put("mail.smtp.port", "587");
		Session session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(senderEmail, emailPassword);
			}
		});

		try {
			Message message = new MimeMessage(session);
			session.setDebug(false);

			message.setFrom(new InternetAddress(senderEmail));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
			message.setSubject(baslik);
			message.setContent(mailmesaj, "text/html; charset=utf-8");

			Transport.send(message);

			try (Transport transport = session.getTransport("smtp")) {
				transport.connect();
				transport.sendMessage(message, message.getAllRecipients());
			}

			System.out.println("E-posta gönderildi.");

		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	private static boolean isNumeric(String str) {
		if (str == null) {
			return false;
		}
		try {
			@SuppressWarnings("unused")
			double d = Double.parseDouble(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}
}