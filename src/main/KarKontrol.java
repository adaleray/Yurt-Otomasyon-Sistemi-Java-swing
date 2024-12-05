package main;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.YearMonth;
import java.util.concurrent.ExecutionException;

import javax.swing.table.DefaultTableModel;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;


public class KarKontrol extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> comboBoxAy;
    private JComboBox<String> comboBoxYil;
    private JLabel lblToplamNetKar;
    private JProgressBar progressBar;
    private JButton pdfOlusturButton;
    private static final String url = "jdbc:postgresql://localhost:5432/yurt";
    private static final String username = "postgres";
    private static final String password = "123";
    private static final double VERGI_ORANI = 0.12;
    private boolean isYearlyData = false;



    public KarKontrol() {
        setTitle("Kar Kontrol");
        setResizable(false);
        setIconImage(Toolkit.getDefaultToolkit().getImage(LoginWindow.class.getResource("/main/logg.png")));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 900, 500);

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

        GroupLayout layout = new GroupLayout(contentPane);
        contentPane.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        JLabel lblAy = new JLabel("Ay:");
        lblAy.setFont(new Font("Tahoma", Font.BOLD, 13));
        comboBoxAy = new JComboBox<>();
        String[] aylar = {"Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran", "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"};
        for (String ay : aylar) {
            comboBoxAy.addItem(ay);
        }

        JLabel lblYil = new JLabel("Yıl:");
        lblYil.setFont(new Font("Tahoma", Font.BOLD, 13));
        comboBoxYil = new JComboBox<>();
        int currentYear = YearMonth.now().getYear();
        for (int year = currentYear; year >= currentYear - 10; year--) {
            comboBoxYil.addItem(String.valueOf(year));
        }
        comboBoxYil.setSelectedItem(String.valueOf(currentYear));
        comboBoxAy.setSelectedItem(aylar[YearMonth.now().getMonthValue() - 1]);

        JButton btnAyGetir = new JButton("Ay Getir");
        btnAyGetir.setBackground(Color.ORANGE);
        JButton btnYilGetir = new JButton("Yıl Getir");
        btnYilGetir.setBackground(Color.GREEN);

        model = new DefaultTableModel();
        table = new JTable(model);
        model.addColumn("Ay");
        model.addColumn("Gelir (tl)");
        model.addColumn("Gider (tl)");
        model.addColumn("Vergi (tl)");
        model.addColumn("Net Kar (tl)");
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(table);

        lblToplamNetKar = new JLabel("Toplam Net Kar(tl): 0.0");
        lblToplamNetKar.setForeground(Color.WHITE);
        lblToplamNetKar.setFont(new Font("Tahoma", Font.BOLD, 13));

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);

        btnAyGetir.addActionListener(e -> {
            String selectedAy = (String) comboBoxAy.getSelectedItem();
            String selectedYil = (String) comboBoxYil.getSelectedItem();
            isYearlyData = false;
            loadMonthlyData(selectedAy, selectedYil);
        });

        btnYilGetir.addActionListener(e -> {
            String selectedYil = (String) comboBoxYil.getSelectedItem();
            isYearlyData = true;
            loadYearlyData(selectedYil);
        });

        pdfOlusturButton = new JButton("Makbuz");
        pdfOlusturButton.setBackground(Color.ORANGE);
        pdfOlusturButton.addActionListener(e -> {
            String[] yurtBilgi = getYurtBilgi();
            if (isYearlyData) {
                createPDFReceiptsForYear(yurtBilgi);
            } else {
                createPDFReceiptForMonth(yurtBilgi);
            }
        });


        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(lblAy)
                    .addComponent(comboBoxAy, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblYil)
                    .addComponent(comboBoxYil, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAyGetir)
                    .addComponent(btnYilGetir)
                    .addComponent(pdfOlusturButton))
                .addComponent(scrollPane)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(lblToplamNetKar)
                    .addComponent(progressBar))
        );

        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAy)
                    .addComponent(comboBoxAy, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblYil)
                    .addComponent(comboBoxYil, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAyGetir)
                    .addComponent(btnYilGetir)
                    .addComponent(pdfOlusturButton))
                .addComponent(scrollPane)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblToplamNetKar)
                    .addComponent(progressBar))
        );

        loadMonthlyData((String) comboBoxAy.getSelectedItem(), (String) comboBoxYil.getSelectedItem());
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    private void loadMonthlyData(String ay, String yil) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                progressBar.setVisible(true);
                try (Connection conn = getConnection()) {
                    String queryGelir = "SELECT SUM(ogrenci_geliri + devlet_destegi + camasir_yikama) AS toplam_gelir FROM gelir g JOIN yillar y ON g.yil_id = y.yil_id WHERE g.ay = ? AND y.yil = ?";
                    String queryGider = "SELECT SUM(elektrik + su + isinma + internet + yemek + personel + diger) AS toplam_gider FROM gider g JOIN yillar y ON g.yil_id = y.yil_id WHERE g.ay = ? AND y.yil = ?";
                    PreparedStatement statementGelir = conn.prepareStatement(queryGelir);
                    PreparedStatement statementGider = conn.prepareStatement(queryGider);
                    statementGelir.setString(1, ay);
                    statementGelir.setInt(2, Integer.parseInt(yil));
                    statementGider.setString(1, ay);
                    statementGider.setInt(2, Integer.parseInt(yil));

                    ResultSet resultSetGelir = statementGelir.executeQuery();
                    ResultSet resultSetGider = statementGider.executeQuery();

                    double toplamGelir = 0;
                    double toplamGider = 0;

                    if (resultSetGelir.next()) {
                        toplamGelir = resultSetGelir.getDouble("toplam_gelir");
                    }

                    if (resultSetGider.next()) {
                        toplamGider = resultSetGider.getDouble("toplam_gider");
                    }

                    double vergi = toplamGelir * VERGI_ORANI;
                    double netKar = toplamGelir - toplamGider - vergi;

                    model.setRowCount(0);
                    model.addRow(new Object[]{ay, toplamGelir, toplamGider, vergi, netKar});

                    lblToplamNetKar.setText("Toplam Net Kar(tl): " + netKar);
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    progressBar.setVisible(false);
                }
                return null;
            }
        };
        worker.execute();
    }

    private void loadYearlyData(String yil) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                progressBar.setVisible(true);
                try (Connection conn = getConnection()) {
                    String queryGelir = "SELECT g.ay, SUM(g.ogrenci_geliri + g.devlet_destegi + g.camasir_yikama) AS toplam_gelir FROM gelir g JOIN yillar y ON g.yil_id = y.yil_id WHERE y.yil = ? GROUP BY g.ay ORDER BY g.ay";
                    String queryGider = "SELECT g.ay, SUM(g.elektrik + g.su + g.isinma + g.internet + g.yemek + g.personel + g.diger) AS toplam_gider FROM gider g JOIN yillar y ON g.yil_id = y.yil_id WHERE y.yil = ? GROUP BY g.ay ORDER BY g.ay";
                    PreparedStatement statementGelir = conn.prepareStatement(queryGelir);
                    PreparedStatement statementGider = conn.prepareStatement(queryGider);
                    statementGelir.setInt(1, Integer.parseInt(yil));
                    statementGider.setInt(1, Integer.parseInt(yil));

                    ResultSet resultSetGelir = statementGelir.executeQuery();
                    ResultSet resultSetGider = statementGider.executeQuery();

                    model.setRowCount(0);
                    double toplamNetKar = 0;

                    while (resultSetGelir.next() && resultSetGider.next()) {
                        String ay = resultSetGelir.getString("ay");
                        double toplamGelir = resultSetGelir.getDouble("toplam_gelir");
                        double toplamGider = resultSetGider.getDouble("toplam_gider");
                        double vergi = toplamGelir * VERGI_ORANI;
                        double netKar = toplamGelir - toplamGider - vergi;
                        toplamNetKar += netKar;

                        model.addRow(new Object[]{ay, toplamGelir, toplamGider, vergi, netKar});
                    }

                    lblToplamNetKar.setText("Toplam Net Kar(tl): " + toplamNetKar);
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    progressBar.setVisible(false);
                }
                return null;
            }
        };
        worker.execute();
    }
    private String[] getYurtBilgi() {
        String[] yurtBilgi = new String[2];
        try (Connection conn = getConnection()) {
            String query = "SELECT yurtadi, yurttelno FROM yurtbilgi LIMIT 1";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                yurtBilgi[0] = resultSet.getString("yurtadi");
                yurtBilgi[1] = resultSet.getString("yurttelno");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return yurtBilgi;
    }

    private void createPDFReceiptForMonth(String[] yurtBilgi) {
    	
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
        	
            @Override
            protected Void doInBackground() {
            	
                progressBar.setVisible(true);
                String selectedAy = (String) comboBoxAy.getSelectedItem();
                String selectedYil = (String) comboBoxYil.getSelectedItem();
                String fileName = "KarRaporu_" + selectedAy + "_" + selectedYil + ".pdf";

                try (PDDocument document = new PDDocument()) {
                    PDPage page = new PDPage();
                    document.addPage(page);

                    PDImageXObject logoImage = PDImageXObject.createFromFile("src/main/logg.png", document);
                    PDType0Font font = PDType0Font.load(document, new File("src/main/Roboto-Bold.ttf"));

                    PDPageContentStream contentStream = new PDPageContentStream(document, page);
                    // Adjust the Y coordinate to make the logo appear lower on the page
                    contentStream.drawImage(logoImage, 50, 700, logoImage.getWidth() / 4, logoImage.getHeight() / 4);
                    contentStream.setFont(font, 12);
                    contentStream.beginText();
                    contentStream.setLeading(14.5f);
                    contentStream.newLineAtOffset(50, 650);

                    contentStream.showText("KAR MAKBUZU");
                    contentStream.newLine();
                    contentStream.newLine();
                    contentStream.showText("Yurt Adı: " + yurtBilgi[0]);
                    contentStream.newLine();
                    contentStream.showText("Yurt Telefon: " + yurtBilgi[1]);
                    contentStream.newLine();
                    contentStream.showText("Yurt Adres: Bardacık Sokak No: 20 Kızılay/ANKARA");
                    contentStream.newLine();
                    contentStream.newLine();
                    contentStream.showText("Tarih: " + selectedAy + " " + selectedYil);
                    contentStream.newLine();
                    contentStream.newLine();

                    for (int i = 0; i < table.getRowCount(); i++) {
                        for (int j = 0; j < table.getColumnCount(); j++) {
                            contentStream.showText(table.getColumnName(j) + ": " + table.getValueAt(i, j).toString());
                            contentStream.newLine();
                        }
                        contentStream.newLine();
                    }

                    contentStream.endText();
                    contentStream.close();

                    document.save(fileName);
                    JOptionPane.showMessageDialog(null, "PDF başarıyla oluşturuldu: " + fileName);
                    Desktop.getDesktop().open(new File(fileName));
                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "PDF oluşturulurken bir hata oluştu: " + e.getMessage());
                } finally {
                    progressBar.setVisible(false);
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "PDF oluşturulurken bir hata oluştu: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void createPDFReceiptsForYear(String[] yurtBilgi) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                progressBar.setVisible(true);
                String selectedYil = (String) comboBoxYil.getSelectedItem();
                String fileName = "KarRaporu_" + selectedYil + ".pdf";

                try (PDDocument document = new PDDocument()) {
                    PDPage page = new PDPage();
                    document.addPage(page);

                    PDImageXObject logoImage = PDImageXObject.createFromFile("src/main/logg.png", document);
                    PDType0Font font = PDType0Font.load(document, new File("src/main/Roboto-Bold.ttf"));

                    PDPageContentStream contentStream = new PDPageContentStream(document, page);
                    contentStream.drawImage(logoImage, 50, 700, logoImage.getWidth() / 4, logoImage.getHeight() / 4);
                    contentStream.setFont(font, 12);
                    contentStream.beginText();
                    contentStream.setLeading(14.5f);
                    contentStream.newLineAtOffset(50, 650);

                    contentStream.showText("KAR MAKBUZU");
                    contentStream.newLine();
                    contentStream.newLine();
                    contentStream.showText("Yurt Adı: " + yurtBilgi[0]);
                    contentStream.newLine();
                    contentStream.showText("Yurt Telefon: " + yurtBilgi[1]);
                    contentStream.newLine();
                    contentStream.showText("Yurt Adres: Bardacık Sokak No: 20 Kızılay/ANKARA");
                    contentStream.newLine();
                    contentStream.newLine();
                    contentStream.showText("Yıl: " + selectedYil);
                    contentStream.newLine();
                    contentStream.newLine();

                    for (int i = 0; i < table.getRowCount(); i++) {
                        for (int j = 0; j < table.getColumnCount(); j++) {
                            contentStream.showText(table.getColumnName(j) + ": " + table.getValueAt(i, j).toString());
                            contentStream.newLine();
                        }
                        contentStream.newLine();
                    }

                    contentStream.endText();
                    contentStream.close();

                    document.save(fileName);
                    JOptionPane.showMessageDialog(null, "PDF başarıyla oluşturuldu: " + fileName);
                    Desktop.getDesktop().open(new File(fileName));
                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "PDF oluşturulurken bir hata oluştu: " + e.getMessage());
                } finally {
                    progressBar.setVisible(false);
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "PDF oluşturulurken bir hata oluştu: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

}