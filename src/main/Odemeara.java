package main;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.*;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class Odemeara extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String URL = "jdbc:postgresql://localhost:5432/yurt";
	private static final String KULLANICI_ADI = "postgres";
	private static final String SIFRE = "123";

	private JPanel contentPane;
	private JTextField tcTextField;
	private JTextField adTextField;
	private JTextField soyadTextField;
	private JButton araButton;
	private JTable table;
	private DefaultTableModel model;
	private JButton geri;
	private JButton yenile;
	private JButton pdfOlusturButton;



	public Odemeara() {
		setTitle("Ödeme Ara");
		setIconImage(Toolkit.getDefaultToolkit().getImage(Odemeara.class.getResource("/main/logg.png")));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 847, 482);
		contentPane = new JPanel();
		contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);

		JLabel tcLabel = new JLabel("T.C.:");
		panel.add(tcLabel);

		tcTextField = new JTextField();
		panel.add(tcTextField);
		tcTextField.setColumns(10);

		JLabel adLabel = new JLabel("İsim:");
		panel.add(adLabel);

		adTextField = new JTextField();
		panel.add(adTextField);
		adTextField.setColumns(10);

		JLabel soyadLabel = new JLabel("Soyad:");
		panel.add(soyadLabel);

		soyadTextField = new JTextField();
		panel.add(soyadTextField);
		soyadTextField.setColumns(10);

		araButton = new JButton("Ödeme Ara");
		araButton.setBackground(Color.ORANGE);
		panel.add(araButton);

		yenile = new JButton("Sıfırla");
		yenile.setBackground(Color.GREEN);
		yenile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				odemeleriGetir();
				tcTextField.setText("");
				adTextField.setText("");
				soyadTextField.setText("");
			}
		});
		panel.add(yenile);

		geri = new JButton("Geri");
		geri.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainMenu mainMenu = new MainMenu();
                mainMenu.setVisible(true);
                dispose();
			}
		});
		geri.setBackground(Color.LIGHT_GRAY);
		panel.add(geri);
		
		pdfOlusturButton = new JButton("Makbuz");
		pdfOlusturButton.setBackground(Color.ORANGE);
		pdfOlusturButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    int selectedRow = table.getSelectedRow();
			    if (selectedRow == -1) {
			        JOptionPane.showMessageDialog(null, "Lütfen bir ödeme seçin.");
			        return;
			    }
			    
			    int odemeID = (int) model.getValueAt(selectedRow, 0);
			    String odemeAd = (String) model.getValueAt(selectedRow, 2);
			    String odemeSoyad = (String) model.getValueAt(selectedRow, 3);
			    double odemeMiktari = (double) model.getValueAt(selectedRow, 4);
			    String odemeTarihi = (String) model.getValueAt(selectedRow, 5);
			    
			    String sirketAdi = "Hızlı Erkek Yurtları A.Ş.";
			    String sirketAdresi = "kavaklıdere mah. bardacık sokak no : 20 Ankara/çankaya";
			    String sirketTelefon = "+90545 654 4835";
			    
			    File logoFile = new File("C:\\Users\\Eray\\Desktop\\Gör_Proje\\er.png");
			    
			    String ekleyenAdSoyad = (String) model.getValueAt(selectedRow, 6);
			    
			    try {
			        PDDocument document = new PDDocument();
			        PDPage page = new PDPage();
			        document.addPage(page);
			        PDPageContentStream contentStream = new PDPageContentStream(document, page);
			        PDType0Font font = PDType0Font.load(document, new File("C:\\Users\\Eray\\Desktop\\Sistem_Analizi\\Roboto-Bold.ttf"));
			        contentStream.setFont(font, 12);
			        
			        PDImageXObject logoImage = PDImageXObject.createFromFileByContent(logoFile, document);
			        contentStream.drawImage(logoImage, 50, 750, logoImage.getWidth() / 4, logoImage.getHeight() / 4);
			        
			        contentStream.beginText();
			        contentStream.newLineAtOffset(50, 700); // Metin başlangıç yüksekliği
			        contentStream.showText("TAHSİLAT MAKBUZU");
			        contentStream.newLineAtOffset(0, -20);
			        contentStream.newLineAtOffset(0, -20);
			        
			        
			        contentStream.showText(sirketAdi);
			        contentStream.newLineAtOffset(0, -20);
			        contentStream.showText(sirketAdresi);
			        contentStream.newLineAtOffset(0, -20);
			        contentStream.showText(sirketTelefon);
			        contentStream.newLineAtOffset(0, -20);
			        contentStream.newLineAtOffset(0, -20);
			        contentStream.showText("Tarih: "+odemeTarihi);
			        contentStream.newLineAtOffset(0, -20);
			        contentStream.showText("Ödeme İd:"+odemeID);
			        contentStream.newLineAtOffset(0, -20);
			        contentStream.newLineAtOffset(0, -20);
			        contentStream.showText("Sayın; " + odemeAd + " " + odemeSoyad + "’dan " + odemeMiktari + " Konaklama bedeli olarak yalnız "+"  "+odemeMiktari+ "  "+"tahsil edilmiştir.");
			        contentStream.newLineAtOffset(0, -20);
			        // Sıra numaralarını ve tutar bilgilerini doldurmak için buraya ek kodlar eklenebilir
			        contentStream.newLineAtOffset(0, -20);
			        contentStream.newLineAtOffset(0, -20);
			        contentStream.showText("Yalnız; " +"  "+ odemeMiktari + "  "+" tahsil edilmiştir.");
			        contentStream.newLineAtOffset(0, -40);
			        contentStream.showText("Ödemeyi Alan:" + "  "+ekleyenAdSoyad);
			        contentStream.newLineAtOffset(350, -10);
			        contentStream.showText("Ödeme Yapan:"+"  "+odemeAd+"  "+odemeSoyad);
			        
			        contentStream.endText();
			        
			        contentStream.close();
			        
			        File file = new File("odeme_makbuzu_" + odemeID + ".pdf");
			        document.save(file);
			        document.close();
			        
			        Desktop.getDesktop().open(file);
			    } catch (IOException ex) {
			        JOptionPane.showMessageDialog(null, "PDF oluşturulurken bir hata oluştu: " + ex.getMessage());
			    }
			}


		});

		panel.add(pdfOlusturButton);

		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);

		model = new DefaultTableModel();
		table = new JTable(model);
		scrollPane.setViewportView(table);

		model.addColumn("Ödeme ID");
		model.addColumn("T.C.");
		model.addColumn("Ad");
		model.addColumn("Soyad");
		model.addColumn("Ödeme Miktarı");
		model.addColumn("Ödeme Tarihi");
		model.addColumn("Ekleyen Yönetici");

		araButton.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent e) {
			        String tc = tcTextField.getText().trim();
			        String ad = adTextField.getText().trim();
			        String soyad = soyadTextField.getText().trim();
			        if (!tc.isEmpty() || !ad.isEmpty() || !soyad.isEmpty()) {
			            odemeleriAra(tc, ad, soyad);
			        } else {
			            JOptionPane.showMessageDialog(null, "Lütfen en az bir arama kriteri girin.");
			        }
			        
			        pdfOlusturButton.setEnabled(true);
			    }
			});
		odemeleriGetir();
	}

	private void odemeleriGetir() {
	    model.setRowCount(0); 

	    String sql = "SELECT t_odeme.*, t_login.ad, t_login.soyad " +
	                 "FROM t_odeme " +
	                 "LEFT JOIN t_login ON t_odeme.ekleyen_kullanici = t_login.username " +
	                 "ORDER BY odeme_id";

	    try (Connection conn = DriverManager.getConnection(URL, KULLANICI_ADI, SIFRE);
	         Statement stmt = conn.createStatement();
	         ResultSet rs = stmt.executeQuery(sql)) {
	        while (rs.next()) {
	            int odemeID = rs.getInt("odeme_id");
	            String tc = rs.getString("ogr_tc");
	            String ad = getOgrenciAdi(tc);
	            String soyad = getOgrenciSoyadi(tc);
	            double odemeMiktari = rs.getDouble("odeme_miktari");
	            String odemeTarihi = rs.getString("odeme_tarihi");
	            String ekleyenAd = rs.getString("ad"); // Ekleyen kullanıcı adı
	            String ekleyenSoyad = rs.getString("soyad"); // Ekleyen kullanıcı soyadı

	            model.addRow(new Object[]{odemeID, tc, ad, soyad, odemeMiktari, odemeTarihi, ekleyenAd != null && ekleyenSoyad != null ? ekleyenAd + " " + ekleyenSoyad : "Bilinmiyor"});
	        }
	    } catch (SQLException ex) {
	        JOptionPane.showMessageDialog(null, "Ödemeler getirilirken bir hata oluştu: " + ex.getMessage());
	    }
	}



	private String getOgrenciAdi(String tc) {
		String ad = "";
		String sql = "SELECT ogr_isim FROM t_ogrenci WHERE ogr_tc = ?";
		try (Connection conn = DriverManager.getConnection(URL, KULLANICI_ADI, SIFRE);
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, tc);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				ad = rs.getString("ogr_isim");
			}
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(null, "Öğrenci adı getirilirken bir hata oluştu: " + ex.getMessage());
		}
		return ad;
	}

	private String getOgrenciSoyadi(String tc) {
		String soyad = "";
		String sql = "SELECT ogr_soyad FROM t_ogrenci WHERE ogr_tc = ?";
		try (Connection conn = DriverManager.getConnection(URL, KULLANICI_ADI, SIFRE);
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, tc);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				soyad = rs.getString("ogr_soyad");
			}
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(null, "Öğrenci soyadı getirilirken bir hata oluştu: " + ex.getMessage());
		}
		return soyad;
	}

	private void odemeleriAra(String tc, String ad, String soyad) {
		model.setRowCount(0); // Tabloyu temizle

		String sql = "SELECT * FROM t_odeme INNER JOIN t_ogrenci ON t_odeme.ogr_tc = t_ogrenci.ogr_tc WHERE";
		boolean isFirst = true;
		if (!tc.isEmpty()) {
			sql += " t_odeme.ogr_tc = '" + tc + "'";
			isFirst = false;
		}
		if (!ad.isEmpty()) {
			if (!isFirst) {
				sql += " AND";
			}
			sql += " t_ogrenci.ogr_isim LIKE '%" + ad + "%'";
			isFirst = false;
		}
		if (!soyad.isEmpty()) {
			if (!isFirst) {
				sql += " AND";
			}
			sql += " t_ogrenci.ogr_soyad LIKE '%" + soyad + "%'";
		}
		sql += " ORDER BY t_odeme.odeme_id";

		try (Connection conn = DriverManager.getConnection(URL, KULLANICI_ADI, SIFRE);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				int odemeID = rs.getInt("odeme_id");
				String odemeTC = rs.getString("ogr_tc");
				String odemeAd = rs.getString("ogr_isim");
				String odemeSoyad = rs.getString("ogr_soyad");
				double odemeMiktari = rs.getDouble("odeme_miktari");
				String odemeTarihi = rs.getString("odeme_tarihi");

				model.addRow(new Object[] { odemeID, odemeTC, odemeAd, odemeSoyad, odemeMiktari, odemeTarihi });
			}
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(null, "Ödemeler aranırken bir hata oluştu: " + ex.getMessage());
		}
		
		
	}
	
	 
	


	}

