package main;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.sql.*;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;


public class MailGönder extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField aramaText;
	private JComboBox<String> aramaKriter;
	private JTable table;
	private JTextField basliktextfield;
	private JTextField textField;
	private JTextArea textArea;
	private static String senderEmail; 
	private static String emailPassword = "";
	@SuppressWarnings("unused")
	private static String yurtAdi = "";
	@SuppressWarnings("unused")
	private static String yurtTelNo = "";

	private static final String URL = "jdbc:postgresql://localhost:5432/yurt";
	private static final String KULLANICI_ADI = "postgres";
	private static final String SIFRE = "123";

	
	public MailGönder() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(PerAra.class.getResource("/main/logg.png")));
		setTitle("Mail Gönder");
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
		scrollPane.setBounds(32, 249, 748, 126);
		contentPane.add(scrollPane);

		table = new JTable();
		scrollPane.setViewportView(table);

		JButton yenile = new JButton("Yenile");
		yenile.setBackground(Color.YELLOW);
		yenile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aramaText.setText("");
				verileriGetir();
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

		JButton mailgonder = new JButton("Mail Gönder");
		mailgonder.setBounds(559, 151, 157, 21);
		contentPane.add(mailgonder);

		basliktextfield = new JTextField();
		basliktextfield.setBounds(32, 152, 128, 19);
		contentPane.add(basliktextfield);
		basliktextfield.setColumns(10);

		JLabel lblNewLabel_2 = new JLabel("Mail Konusu");
		lblNewLabel_2.setForeground(Color.WHITE);
		lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel_2.setBackground(Color.WHITE);
		lblNewLabel_2.setBounds(51, 121, 112, 13);
		contentPane.add(lblNewLabel_2);

		JLabel lblNewLabel_3 = new JLabel("Mail");
		lblNewLabel_3.setForeground(Color.WHITE);
		lblNewLabel_3.setBackground(Color.WHITE);
		lblNewLabel_3.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel_3.setBounds(345, 121, 33, 13);
		contentPane.add(lblNewLabel_3);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(211, 151, 303, 82);
		contentPane.add(scrollPane_1);

		textArea = new JTextArea();
		scrollPane_1.setViewportView(textArea);

		textField = new JTextField();
		textField.setBounds(30, 207, 130, 19);
		contentPane.add(textField);
		textField.setColumns(10);

		JLabel lblNewLabel_4 = new JLabel("Gönderilecek Kişi");
		lblNewLabel_4.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel_4.setForeground(Color.WHITE);
		lblNewLabel_4.setBounds(51, 184, 137, 13);
		contentPane.add(lblNewLabel_4);

		JButton toplubutton = new JButton("Toplu Mail Gönder");
		toplubutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						LoadingDialog loadingDialog = new LoadingDialog(MailGönder.this);
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								loadingDialog.setVisible(true);
							}
						});

						Set<String> gonderilenMailler = new HashSet<>();
						int rowCount = table.getRowCount();
						if (rowCount > 0) {
							for (int i = 0; i < rowCount; i++) {
								String email = table.getValueAt(i, 3).toString();
								if (!gonderilenMailler.contains(email)) {
									String konu = basliktextfield.getText();
									String mesaj = textArea.getText();
									gonderEmailToplu(email, konu, mesaj);
									gonderilenMailler.add(email);
								}
							}
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									loadingDialog.dispose();
									JOptionPane.showMessageDialog(null, "Toplu mail gönderme işlemi tamamlandı.", "Başarılı",
											JOptionPane.INFORMATION_MESSAGE);
								}
							});
						} else {
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									loadingDialog.dispose();
									JOptionPane.showMessageDialog(null, "Gönderilecek mail adresi bulunamadı.", "Uyarı",
											JOptionPane.WARNING_MESSAGE);
								}
							});
						}
					}
				});
				thread.start();
			}
		});
		toplubutton.setBounds(559, 206, 157, 21);
		contentPane.add(toplubutton);

		verileriGetir();

		araButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String kriter = null;
				switch (aramaKriter.getSelectedIndex()) {
				case 0:
					kriter = "ogr_isim";
					break;
				case 1:
					kriter = "ogr_soyad";
					break;
				case 2:
					kriter = "ogr_tc";
					break;
				}
				String metin = aramaText.getText();
				aramaYap(kriter, metin);
			}
		});

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int selectedRow = table.getSelectedRow();
				if (selectedRow != -1) {
					String isim = table.getValueAt(selectedRow, 1).toString();
					String soyad = table.getValueAt(selectedRow, 2).toString();
					textField.setText(isim + " " + soyad);
				}
			}
		});

		mailgonder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selectedRow = table.getSelectedRow();
				if (selectedRow != -1) {
					Thread thread = new Thread(new Runnable() {
						@Override
						public void run() {
							LoadingDialog loadingDialog = new LoadingDialog(MailGönder.this);
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									loadingDialog.setVisible(true);
								}
							});

							String email = table.getValueAt(selectedRow, 3).toString();
							String konu = basliktextfield.getText();
							String mesaj = textArea.getText();
							gonderEmailTekli(email, konu, mesaj);

							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									loadingDialog.dispose();
								}
							});
						}
					});
					thread.start();
				}
			}
		});
	}

	private void verileriGetir() {
		try (Connection conn = DriverManager.getConnection(URL, KULLANICI_ADI, SIFRE)) {
			String sql = "SELECT ogr_tc, ogr_isim, ogr_soyad, ogr_email FROM t_ogrenci";
			PreparedStatement pst = conn.prepareStatement(sql);
			ResultSet rs = pst.executeQuery();

			DefaultTableModel model = new DefaultTableModel(new String[] { "TC", "İsim", "Soyad", "Email" }, 0);
			while (rs.next()) {
				Vector<String> row = new Vector<>();
				row.add(rs.getString("ogr_tc"));
				row.add(rs.getString("ogr_isim"));
				row.add(rs.getString("ogr_soyad"));
				row.add(rs.getString("ogr_email"));
				model.addRow(row);
			}
			table.setModel(model);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void aramaYap(String kriter, String metin) {
		try (Connection conn = DriverManager.getConnection(URL, KULLANICI_ADI, SIFRE)) {
			String sql = "SELECT ogr_tc, ogr_isim, ogr_soyad, ogr_email FROM t_ogrenci WHERE " + kriter + " ILIKE ?";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, "%" + metin + "%");
			ResultSet rs = pst.executeQuery();

			DefaultTableModel model = new DefaultTableModel(new String[] { "TC", "İsim", "Soyad", "Email" }, 0);
			while (rs.next()) {
				Vector<String> row = new Vector<>();
				row.add(rs.getString("ogr_tc"));
				row.add(rs.getString("ogr_isim"));
				row.add(rs.getString("ogr_soyad"));
				row.add(rs.getString("ogr_email"));
				model.addRow(row);
			}
			table.setModel(model);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void gonderEmailTekli(String email, String konu, String mesaj) {
		try (Connection conn = DriverManager.getConnection(URL, KULLANICI_ADI, SIFRE)) {
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
			message.setText(mesaj);

			Transport.send(message);

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(null, email + " adresine mail gönderildi.", "Başarılı",
							JOptionPane.INFORMATION_MESSAGE);
					aramaText.setText("");
					textField.setText("");
					basliktextfield.setText("");
					textArea.setText("");
				}
			});
		} catch (MessagingException e) {
			e.printStackTrace();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(null, "Mail gönderilirken bir hata oluştu.", "Hata",
							JOptionPane.ERROR_MESSAGE);
				}
			});
		}
	}

	private void gonderEmailToplu(String email, String konu, String mesaj) {
		try (Connection conn = DriverManager.getConnection(URL, KULLANICI_ADI, SIFRE)) {
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
			message.setText(mesaj);

			Transport.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
}
