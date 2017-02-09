package testing;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


public class ErrorPanel extends JFrame {

	
	JPanel panel = new JPanel();

	public ErrorPanel(String s){
		super("Login Autentification");
		setSize(500,500);
		setLocation(500,280);
				
		JTextArea textArea = new JTextArea(s);	
		textArea.setBounds(0,0,500,500);
		//textArea.setsetEscapeModelStrings(false);		
		//textArea.setFont(new Font("Serif", Font.ITALIC, 16));
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		panel.add(textArea);
		
		
		getContentPane().add(panel);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	
}
