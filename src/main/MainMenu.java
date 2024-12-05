package main;

import javax.swing.*;

import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainMenu extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
	@SuppressWarnings("unused")
	private JTable table;
	private JLabel clockLabel;
	private JLabel dateLabel;
	private JLabel studentCountLabel;
	private JLabel personnelCountLabel;
	public MainMenu() {
		String loggedInUsername = login.getUsername();
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainMenu.class.getResource("/main/logg.png")));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 841, 424);
		setLocationRelativeTo(null);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu ogrenci_menu = new JMenu("Öğrenci");
		ogrenci_menu.setBackground(new Color(153, 204, 0));
		menuBar.add(ogrenci_menu);

		JMenu OgrenciBilgiMenu = new JMenu("Bilgi Sistemi");
		OgrenciBilgiMenu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				ogrencidetay ogrencidetay = new ogrencidetay();
				ogrencidetay.setVisible(true);
			}
		});
		ogrenci_menu.add(OgrenciBilgiMenu);
		
		JMenu kontrolmenu = new JMenu("Giriş Çıkış Kontrol");
		kontrolmenu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				GirisCikisKontrol girisCikisKontrol = new GirisCikisKontrol();
				girisCikisKontrol.setVisible(true);
			}
		});
		ogrenci_menu.add(kontrolmenu);

		JMenu PersonelMenu = new JMenu("Personel");
		menuBar.add(PersonelMenu);

		JMenu Perbilgimenu = new JMenu("Bilgi Sistem");
		Perbilgimenu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Persistem persistem = new Persistem();
				persistem.setVisible(true);

			}
		});
		PersonelMenu.add(Perbilgimenu);

		JMenu perara = new JMenu("Personel Ara");
		perara.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				PerAra perAra = new PerAra();
				perAra.setVisible(true);

			}
		});
		PersonelMenu.add(perara);

		JMenu odemesistemmenu = new JMenu("Ödeme");
		menuBar.add(odemesistemmenu);

		JMenu odemegir = new JMenu("Ödeme Gir");
		odemegir.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				OgrenciOdemeEkle ogrenciOdemeEkle = new OgrenciOdemeEkle();
				ogrenciOdemeEkle.setVisible(true);
			}
		});
		odemesistemmenu.add(odemegir);

		JMenu odemeupdatemenu = new JMenu("Ödeme Sil Düzenle");
		odemeupdatemenu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				OdemeUpdate odemeUpdate = new OdemeUpdate();
				odemeUpdate.setVisible(true);
			}
		});
		odemesistemmenu.add(odemeupdatemenu);

		JMenu odemeara = new JMenu("Ödeme ara");
		odemeara.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Odemeara odemeara = new Odemeara();
				odemeara.setVisible(true);
			}
		});
		odemesistemmenu.add(odemeara);

		JMenu gelirmenu = new JMenu("Gelir Kontrol");
		menuBar.add(gelirmenu);
		
		JMenu gelireklemenu = new JMenu("Gelir Ekle-Güncelle");
		gelireklemenu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				GelirEkle gelirEkle = new GelirEkle();
				gelirEkle.setVisible(true);
			}
		});
		gelirmenu.add(gelireklemenu);
		
		JMenu mnNewMenu_2 = new JMenu("Gelir Takip");
		mnNewMenu_2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				GelirKontrol gelirKontrol = new GelirKontrol();
				gelirKontrol.setVisible(true);
			}
		});
		gelirmenu.add(mnNewMenu_2);

		JMenu gidermenu = new JMenu("Gider Kontrol");
		menuBar.add(gidermenu);

		JMenu giderekle = new JMenu("Gider Ekle-Güncelle");
		giderekle.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				GiderEkle giderEkle = new GiderEkle();
				giderEkle.setVisible(true);
			}
		});
		gidermenu.add(giderekle);

		JMenu giderkontrol = new JMenu("Gider Takip");
		giderkontrol.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				GiderKontrol giderKontrol= new GiderKontrol();
				giderKontrol.setVisible(true);
			}
		});
		gidermenu.add(giderkontrol);

		JMenu yemekmenu = new JMenu("Yemek Sistem");
		yemekmenu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Yemenuadmin yemenuadmin = new Yemenuadmin();
				yemenuadmin.setVisible(true);
			}
		});
		
		JMenu Karbutton = new JMenu("Kar Kontrol");
		Karbutton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				KarKontrol karKontrol = new KarKontrol();
				karKontrol.setVisible(true);
			}
		});
		menuBar.add(Karbutton);
		menuBar.add(yemekmenu);

		JMenu cikismenu = new JMenu("Çıkış (" + loggedInUsername + ")");
		cikismenu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String[] options = { "Oturumu Kapat", "Uygulamayı Kapat", "İptal" };
				int choice = JOptionPane.showOptionDialog(null, "Çıkış yapmak istiyor musunuz?", "Çıkış",
						JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

				if (choice == 0) {
					LoginWindow loginWindow = new LoginWindow();
					loginWindow.setVisible(true);
					dispose(); 
				} else if (choice == 1) {
					System.exit(0); 
				}
			}
		});

		JMenu sikayetadmin = new JMenu("Şikayet");
		sikayetadmin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				SikayetAdminMenu sikayetAdminMenu = new SikayetAdminMenu();
				sikayetAdminMenu.setVisible(true);
			}
		});
		menuBar.add(sikayetadmin);

		JMenu mailmenu = new JMenu("Mail Sistem");
		mailmenu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				MailGönder mailGönder = new MailGönder();
				mailGönder.setVisible(true);

			}
		});
		menuBar.add(mailmenu);

		JMenu mnNewMenu = new JMenu("Ayarlar");
		menuBar.add(mnNewMenu);

		JMenu odaayar = new JMenu("Odalar");
		odaayar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				AyarOdalar ayarOdalar = new AyarOdalar();
				ayarOdalar.setVisible(true);

			}
		});

		JMenu hesapmenu = new JMenu("Hesap");

		mnNewMenu.add(hesapmenu);

		JMenu bilgimenu = new JMenu("Bilgi Değiştir");
		bilgimenu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				AyarlarBilgi ayarlarBilgi = new AyarlarBilgi();
				ayarlarBilgi.setVisible(true);
			}
		});
		hesapmenu.add(bilgimenu);

		JMenu sifremenu = new JMenu("Şifre Değiştir");
		sifremenu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				AyarlarHesap ayarlarHesap = new AyarlarHesap();
				ayarlarHesap.setVisible(true);

			}
		});
		hesapmenu.add(sifremenu);
		mnNewMenu.add(odaayar);

		JMenu departman = new JMenu("Departmanlar");
		departman.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				AyarlarDepartman ayarlarDepartman = new AyarlarDepartman();
				ayarlarDepartman.setVisible(true);

			}
		});
		mnNewMenu.add(departman);

		JMenu yurtayarmenu = new JMenu("Yurt Bilgi");
		yurtayarmenu.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
		        if (isAdmin(loggedInUsername)) {
		            AyarlarYurt ayarlarYurt = new AyarlarYurt();
		            ayarlarYurt.setVisible(true);
		        } else {
		            JOptionPane.showMessageDialog(MainMenu.this, "Erişim izniniz yoktur", "Hata", JOptionPane.ERROR_MESSAGE);
		        }
		    }
		});
		mnNewMenu.add(yurtayarmenu);
		
		JMenu useradminmenu = new JMenu("Yönetim Ekle");
		useradminmenu.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
		        if (isAdmin(loggedInUsername)) {
		            AyarlarYöneticiEkle ayarlarYöneticiEkle = new AyarlarYöneticiEkle();
		            ayarlarYöneticiEkle.setVisible(true);
		        } else {
		            JOptionPane.showMessageDialog(MainMenu.this, "Erişim izniniz yoktur", "Hata", JOptionPane.ERROR_MESSAGE);
		        }
		    }
		});
		mnNewMenu.add(useradminmenu);
		menuBar.add(cikismenu);

		cikismenu.setForeground(Color.RED);
		cikismenu.setBackground(Color.RED);
		menuBar.add(cikismenu);
		contentPane = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				URL url = getClass().getResource("indir.jpg");
				super.paintComponent(g);
				ImageIcon imageIcon = new ImageIcon(url);
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

		dateLabel = new JLabel();
		dateLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
		dateLabel.setForeground(Color.WHITE);
		dateLabel.setBounds(647, 278, 150, 20);
		contentPane.add(dateLabel);

		clockLabel = new JLabel();
		clockLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
		clockLabel.setForeground(Color.WHITE);
		clockLabel.setBounds(647, 229, 150, 20);
		contentPane.add(clockLabel);

		// Öğrenci sayısı etiketi
		studentCountLabel = new JLabel();
		studentCountLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
		studentCountLabel.setForeground(Color.WHITE);
		studentCountLabel.setBounds(384, 150, 200, 20);
		contentPane.add(studentCountLabel);

		personnelCountLabel = new JLabel();
		personnelCountLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
		personnelCountLabel.setForeground(Color.WHITE);
		personnelCountLabel.setBounds(69, 150, 200, 20);
		contentPane.add(personnelCountLabel);

		Timer timer = new Timer(1000, e -> {
			updateClock();
			updateCounts();
		});
		timer.start();

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(22, 180, 602, 171);
		contentPane.add(scrollPane);

		DefaultTableModel model = new DefaultTableModel();
		model.addColumn("Öğrenci TC");
		model.addColumn("Ad");
		model.addColumn("Soyad");
		model.addColumn("Oda Numarası");

		NonEditableTable table = new NonEditableTable(model);
		scrollPane.setViewportView(table);

		JLabel lblNewLabel = new JLabel("Öğrenci Bilgileri");
		lblNewLabel.setForeground(new Color(245, 245, 245));
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 39));
		lblNewLabel.setBounds(163, 38, 372, 44);
		contentPane.add(lblNewLabel);

		try {
			Connection conn = DatabaseConnection.getConnection();

			String sql = "SELECT ogr_tc, ogr_isim, ogr_soyad, ogr_odanumber FROM t_ogrenci";
			PreparedStatement statement = conn.prepareStatement(sql);
			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {
				Object[] row = new Object[4];
				row[0] = resultSet.getString("ogr_tc");
				row[1] = resultSet.getString("ogr_isim");
				row[2] = resultSet.getString("ogr_soyad");
				row[3] = resultSet.getInt("ogr_odanumber");
				model.addRow(row);
			}

			resultSet.close();
			statement.close();
			conn.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}



	private void updateClock() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		clockLabel.setText(sdf.format(cal.getTime()));

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		dateLabel.setText(dateFormat.format(cal.getTime()));
	}

	private void updateCounts() {
		try {
			// Bağlantıyı al
			Connection conn = DatabaseConnection.getConnection();

			// Öğrenci sayısını sorgula
			String studentSql = "SELECT COUNT(*) AS count FROM t_ogrenci";
			PreparedStatement studentStatement = conn.prepareStatement(studentSql);
			ResultSet studentResultSet = studentStatement.executeQuery();

			if (studentResultSet.next()) {
				int studentCount = studentResultSet.getInt("count");
				studentCountLabel.setText("Öğrenci Sayısı: " + studentCount);
			}

			// Personel sayısını sorgula
			String personnelSql = "SELECT COUNT(*) AS count FROM personel";
			PreparedStatement personnelStatement = conn.prepareStatement(personnelSql);
			ResultSet personnelResultSet = personnelStatement.executeQuery();

			if (personnelResultSet.next()) {
				int personnelCount = personnelResultSet.getInt("count");
				personnelCountLabel.setText("Personel Sayısı: " + personnelCount);
			}

			studentResultSet.close();
			studentStatement.close();
			personnelResultSet.close();
			personnelStatement.close();
			conn.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	static class NonEditableTable extends JTable {
		private static final long serialVersionUID = 1L;

		public NonEditableTable(DefaultTableModel dm) {
			super(dm);
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			return false; // Hücreleri düzenlenebilir yapma
		}
	}
	private boolean isAdmin(String username) {
	    boolean isAdmin = false;
	    try {
	        Connection conn = DatabaseConnection.getConnection();
	        String sql = "SELECT admin FROM t_login WHERE username = ?";
	        PreparedStatement statement = conn.prepareStatement(sql);
	        statement.setString(1, username);
	        ResultSet resultSet = statement.executeQuery();

	        if (resultSet.next()) {
	            isAdmin = resultSet.getBoolean("admin");
	        }

	        resultSet.close();
	        statement.close();
	        conn.close();
	    } catch (SQLException ex) {
	        ex.printStackTrace();
	    }
	    return isAdmin;
	}

	static class DatabaseConnection {
		private static final String url = "jdbc:postgresql://localhost:5432/yurt";
		private static final String username = "postgres";
		private static final String password = "123";

		public static Connection getConnection() {
			Connection conn = null;
			try {
				Class.forName("org.postgresql.Driver");
				conn = DriverManager.getConnection(url, username, password);
			} catch (SQLException | ClassNotFoundException e) {
				e.printStackTrace();
			}
			return conn;
		}
	}
}
