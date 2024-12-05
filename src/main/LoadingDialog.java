package main;
import javax.swing.*;
import java.awt.*;

public class LoadingDialog extends JDialog {
  
	private static final long serialVersionUID = 1L;

	public LoadingDialog(Frame parent) {
        super(parent, "İşlem Devam Ediyor...", true);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Lütfen bekleyiniz..."), BorderLayout.CENTER);
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        panel.add(progressBar, BorderLayout.PAGE_END);
        getContentPane().add(panel);
        setSize(300, 100);
        setLocationRelativeTo(parent);
    }
}
