package main;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EditPersonelDialog extends JDialog {
  
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
    private JTextField tcTextField;
    private JTextField isimTextField;
    private JTextField soyadTextField;
    private JTextField telefonTextField;
    private JTextField mailTextField;
    private JTextField maasTextField;
    private JComboBox<String> cinsiyetComboBox;
    private JTextField vardiyaTextField;
    private JTextField deneyimSuresiTextField;
    private JComboBox<String> departmanComboBox;
private Persistem persistem;
    private Connection connection;
    private String personelID;
    


    public EditPersonelDialog( String personelID, String tc, String isim, String soyad, String telefon,
                              String mail, String cinsiyet, String maas, String vardiya, String deneyimSuresi, String departmanAdi, Persistem persistem) {
    	 this.personelID = personelID; 
    	 this.persistem = persistem;
    	 
    	 //diğer
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/yurt", "postgres", "123");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initializeUI(tc, isim, soyad, telefon, mail, cinsiyet, maas, vardiya, deneyimSuresi, departmanAdi);
    }


    private void initializeUI(String tc, String isim, String soyad, String telefon, String mail, String cinsiyet,
                              String maas, String vardiya, String deneyimSuresi, String departmanAdi) {
        setSize(400, 400);
        setLocationRelativeTo(null);
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayout(0, 2));
        setContentPane(contentPane);

        JLabel tcLabel = new JLabel("TC Numarası:");
        contentPane.add(tcLabel);
        tcTextField = new JTextField(tc);
        contentPane.add(tcTextField);

        JLabel isimLabel = new JLabel("İsim:");
        contentPane.add(isimLabel);
        isimTextField = new JTextField(isim);
        contentPane.add(isimTextField);

        JLabel soyadLabel = new JLabel("Soyad:");
        contentPane.add(soyadLabel);
        soyadTextField = new JTextField(soyad);
        contentPane.add(soyadTextField);

        JLabel telefonLabel = new JLabel("Telefon:");
        contentPane.add(telefonLabel);
        telefonTextField = new JTextField(telefon);
        contentPane.add(telefonTextField);

        JLabel mailLabel = new JLabel("Mail:");
        contentPane.add(mailLabel);
        mailTextField = new JTextField(mail);
        contentPane.add(mailTextField);

        JLabel cinsiyetLabel = new JLabel("Cinsiyet:");
        contentPane.add(cinsiyetLabel);
        cinsiyetComboBox = new JComboBox<>(new String[]{"ERKEK", "KIZ"});
        cinsiyetComboBox.setSelectedItem(cinsiyet);
        contentPane.add(cinsiyetComboBox);

        JLabel maasLabel = new JLabel("Maaş:");
        contentPane.add(maasLabel);
        maasTextField = new JTextField(maas);
        contentPane.add(maasTextField);

        JLabel vardiyaLabel = new JLabel("Vardiya:");
        contentPane.add(vardiyaLabel);
        vardiyaTextField = new JTextField(vardiya);
        contentPane.add(vardiyaTextField);

        JLabel deneyimSuresiLabel = new JLabel("Deneyim Süresi:");
        contentPane.add(deneyimSuresiLabel);
        deneyimSuresiTextField = new JTextField(deneyimSuresi);
        contentPane.add(deneyimSuresiTextField);

        JLabel departmanLabel = new JLabel("Departman:");
        contentPane.add(departmanLabel);
        departmanComboBox = new JComboBox<>();
        fillDepartmanComboBox();
        departmanComboBox.setSelectedItem(departmanAdi);
        contentPane.add(departmanComboBox);

        JButton updateButton = new JButton("Güncelle");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    updatePersonel();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        contentPane.add(updateButton);

        JButton cancelButton = new JButton("İptal");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        contentPane.add(cancelButton);

        setVisible(true);
    }

    private void fillDepartmanComboBox() {
        String query = "SELECT departman_adi FROM Departman";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                departmanComboBox.addItem(resultSet.getString("departman_adi"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updatePersonel() throws SQLException {
        String tc = tcTextField.getText();
        String isim = isimTextField.getText();
        String soyad = soyadTextField.getText();
        String telefon = telefonTextField.getText();
        String mail = mailTextField.getText();
        String cinsiyet = (String) cinsiyetComboBox.getSelectedItem();
        String maas = maasTextField.getText();
        String vardiya = vardiyaTextField.getText();
        String deneyimSuresi = deneyimSuresiTextField.getText();
        String departmanAdi = (String) departmanComboBox.getSelectedItem();

        // personelID kontrolü
        if (personelID == null || personelID.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Personel ID bilgisi eksik.", "Hata", JOptionPane.ERROR_MESSAGE);
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

        if (tc.isEmpty() || isim.isEmpty() || soyad.isEmpty() || telefon.isEmpty() || mail.isEmpty() ||
                maas.isEmpty() || deneyimSuresi.isEmpty()) {
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
            if (maas.isEmpty())
                errorMessage.append("- Maaş\n");
            if (deneyimSuresi.isEmpty())
                errorMessage.append("- Deneyim Süresi\n");
            JOptionPane.showMessageDialog(null, errorMessage.toString(), "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double maasValue;
        try {
            maasValue = Double.parseDouble(maas);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Geçersiz maaş değeri.", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int parsedPersonelID;
        try {
            parsedPersonelID = Integer.parseInt(personelID);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Geçersiz personel ID formatı.", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String updateQuery = "UPDATE Personel SET tc_numarasi=?, isim=?, soyad=?, telefon=?, mail=?, cinsiyet=?, " +
                "maas=?, vardiya=?, deneyim_suresi=?, departman_id=? WHERE personel_id=?";
        PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
        updateStatement.setString(1, tc);
        updateStatement.setString(2, isim);
        updateStatement.setString(3, soyad);
        updateStatement.setString(4, telefon);
        updateStatement.setString(5, mail);
        updateStatement.setString(6, cinsiyet);
        updateStatement.setDouble(7, maasValue);
        updateStatement.setString(8, vardiya);
        updateStatement.setString(9, deneyimSuresi);
        updateStatement.setInt(10, departmanID);
        updateStatement.setInt(11, parsedPersonelID); // upda yaptm
        updateStatement.executeUpdate();

        JOptionPane.showMessageDialog(null, "Personel başarıyla güncellendi.");
       persistem. updateTable();
        dispose();
    }

    private int getDepartmanID(String departmanAdi) throws SQLException {
        String query = "SELECT departman_id FROM Departman WHERE departman_adi = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, departmanAdi);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getInt("departman_id");
    }

    public boolean isValidName(String name) {
        return name.matches("[a-zA-ZçÇğĞıİöÖşŞüÜ]+");
    }

    public boolean isValidPhone(String phone) {
        return phone.matches("[0-9]+");
    }
    
}
