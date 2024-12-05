package main;

import java.awt.*;
import java.sql.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;


import java.util.Properties;

public class GirisCikisKontrol extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable table;

	private static final String DB_URL = "jdbc:postgresql://localhost:5432/yurt";
	private static final String USER = "postgres";
	private static final String PASS = "123";
	private String senderEmail;
	private String emailPassword;
	private String yurtAdi;
	private String yurtTelNo;
	private String yurtAdres = "Ankara / Çankaya Kavaklıdere Mah. Bardacık Sok. No 20";



	public GirisCikisKontrol() {
		setTitle("Giriş Çıkış Kontrol");
		setResizable(false);
		setIconImage(Toolkit.getDefaultToolkit().getImage(LoginWindow.class.getResource("/main/logg.png")));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 813, 416);
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

		contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JButton kontrolbutton = new JButton("Kontrol Et");
		kontrolbutton.setFont(new Font("Tahoma", Font.BOLD, 12));
		kontrolbutton.setBackground(Color.ORANGE);
		kontrolbutton.setBounds(181, 24, 118, 21);
		contentPane.add(kontrolbutton);
		JButton mailgonder = new JButton("Mail Gönder");
		mailgonder.setFont(new Font("Tahoma", Font.BOLD, 12));
		mailgonder.setBackground(Color.GREEN);
		mailgonder.setBounds(329, 24, 127, 21);
		contentPane.add(mailgonder);

		JButton yurtOgrencileriButton = new JButton("Yurttaki Öğrenciler");
		yurtOgrencileriButton.setFont(new Font("Tahoma", Font.BOLD, 12));
		yurtOgrencileriButton.setBackground(Color.ORANGE);
		yurtOgrencileriButton.setBounds(489, 24, 155, 21);
		contentPane.add(yurtOgrencileriButton);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(8, 153, 766, 202);
		contentPane.add(scrollPane);
	
		table = new JTable();
		scrollPane.setViewportView(table);

		kontrolbutton.addActionListener(e -> kontrolEt());
		yurtOgrencileriButton.addActionListener(e -> yurttakiOgrencileriGetir());
		mailgonder.addActionListener(e -> mailGonder());
		kontrolEt();
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				if (!event.getValueIsAdjusting() && table.getSelectedRow() != -1) {
					String tc = table.getValueAt(table.getSelectedRow(), 0).toString();
					showLastThreeEntriesAndExits(tc);
				}
			}
		});
	}

	private void kontrolEt() {
		String query = "WITH LatestStatus AS ( "
				+ "    SELECT t_ogrenci.ogr_tc, t_ogrenci.ogr_isim, t_ogrenci.ogr_soyad, t_ogrenci.ogr_email, "
				+ "           yurtgircik.cikis_tarih, yurtgircik.giris_tarih, yurtgircik.durum, "
				+ "           ROW_NUMBER() OVER (PARTITION BY t_ogrenci.ogr_tc ORDER BY "
				+ "                               GREATEST(yurtgircik.giris_tarih, yurtgircik.cikis_tarih) DESC) AS rn "
				+ "    FROM t_ogrenci " + "    JOIN yurtgircik ON t_ogrenci.ogr_tc = yurtgircik.ogr_tc " + ") "
				+ "SELECT ogr_tc, ogr_isim, ogr_soyad, ogr_email, cikis_tarih, 'Çıkış yapıp giriş yapmayan' AS durum "
				+ "FROM LatestStatus " + "WHERE rn = 1 AND durum = false";

		try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {

			DefaultTableModel model = new DefaultTableModel(
					new Object[] { "TC", "İsim", "Soyad", "Email", "Çıkış Tarihi", "Durum" }, 0);
			while (rs.next()) {
				String tc = rs.getString("ogr_tc");
				String isim = rs.getString("ogr_isim");
				String soyad = rs.getString("ogr_soyad");
				String email = rs.getString("ogr_email");
				String cikisTarih = rs.getString("cikis_tarih");
				String durum = rs.getString("durum");
				model.addRow(new Object[] { tc, isim, soyad, email, cikisTarih, durum });
			}
			table.setModel(model);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void yurttakiOgrencileriGetir() {
		String query = "WITH LatestStatus AS ( "
				+ "    SELECT t_ogrenci.ogr_tc, t_ogrenci.ogr_isim, t_ogrenci.ogr_soyad, "
				+ "           yurtgircik.durum, "
				+ "           ROW_NUMBER() OVER (PARTITION BY t_ogrenci.ogr_tc ORDER BY "
				+ "                               GREATEST(yurtgircik.giris_tarih, yurtgircik.cikis_tarih) DESC) AS rn "
				+ "    FROM t_ogrenci " + "    JOIN yurtgircik ON t_ogrenci.ogr_tc = yurtgircik.ogr_tc " + ") "
				+ "SELECT ogr_tc, ogr_isim, ogr_soyad, 'Yurtta' AS durum " + "FROM LatestStatus "
				+ "WHERE rn = 1 AND durum = true";

		try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {

			DefaultTableModel model = new DefaultTableModel(new Object[] { "TC", "İsim", "Soyad", "Durum" }, 0);
			while (rs.next()) {
				String tc = rs.getString("ogr_tc");
				String isim = rs.getString("ogr_isim");
				String soyad = rs.getString("ogr_soyad");
				String durum = rs.getString("durum");
				model.addRow(new Object[] { tc, isim, soyad, durum });
			}
			table.setModel(model);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void showLastThreeEntriesAndExits(String tc) {
		String query = "SELECT giris_tarih, cikis_tarih " + "FROM yurtgircik " + "WHERE ogr_tc = '" + tc + "' "
				+ "ORDER BY giris_tarih DESC, cikis_tarih DESC " + "LIMIT 3";

		try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {

			StringBuilder message = new StringBuilder("Son 3 Giriş-Çıkış Bilgisi:\n");
			while (rs.next()) {
				String girisTarih = rs.getString("giris_tarih");
				String cikisTarih = rs.getString("cikis_tarih");
				message.append("Giriş: ").append(girisTarih != null ? girisTarih : "Yok").append(", Çıkış: ")
						.append(cikisTarih != null ? cikisTarih : "Yok").append("\n");
			}
			JOptionPane.showMessageDialog(this, message.toString(), "Giriş Çıkış Bilgileri",
					JOptionPane.INFORMATION_MESSAGE);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void gonderEmailTekli(String email, String konu, String mesaj) {

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		Session session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(senderEmail, emailPassword);
			}
		});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(senderEmail));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
			message.setSubject(konu);
			message.setContent(mesaj, "text/html; charset=utf-8");

			Transport.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	private void mailGonder() {
		LoadingDialog loadingDialog = new LoadingDialog(this);
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() {

				try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
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

				}
				int rowCount = table.getRowCount();
				for (int i = 0; i < rowCount; i++) {
					String email = table.getValueAt(i, 3).toString(); // Email adresi 4. sütunda
					String isim = table.getValueAt(i, 1).toString();
					String konu = "Giriş Yapmama Uyarısı";
					String mesaj = "<html><head><style>" + "body {font-family: Arial, sans-serif;}"
							+ ".container {border: 1px solid #ccc; padding: 16px; border-radius: 8px;}"
							+ ".header {font-size: 18px; font-weight: bold; margin-bottom: 16px;}"
							+ ".content {margin-bottom: 16px;}" + ".footer {font-size: 12px; color: #888;}"
							+ "</style></head><body>" + "<div class='container'>" + "<div class='header'>" + yurtAdi
							+ "</div>" + "<div class='content'>" + "<p>" + isim
							+ ", yurda giriş yapmadığınız tespit edilmiştir. Lütfen en kısa sürede giriş yapınız.</p>"
							+ "</div>" + "<div class='footer'>" + "<p>Telefon: " + yurtTelNo + "</p>" + "<p>Adres: "
							+ yurtAdres + "</p>" + "</div>" + "</div>" + "</body></html>";

					gonderEmailTekli(email, konu, mesaj);
				}
				return null;
			}

			@Override
			protected void done() {
				loadingDialog.dispose();
				JOptionPane.showMessageDialog(null, "E-postalar başarıyla gönderildi.", "Başarılı",
						JOptionPane.INFORMATION_MESSAGE);
			}
		};
		worker.execute();
		loadingDialog.setVisible(true);
	}

}
