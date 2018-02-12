import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

//import com.mysql.jdbc.PreparedStatement;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JPasswordField;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
public class SignUp extends JFrame {

	private JPanel contentPane;
	private JTextField username;
	private JTextField emailTF;
	private JPasswordField password;
	private JTextField server;
	private JTextField port;
	private Client client;
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SignUp frame = new SignUp();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public SignUp() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Enter username :");
		lblNewLabel.setBounds(60, 13, 104, 28);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Enter Email ID :");
		lblNewLabel_1.setBounds(60, 44, 107, 28);
		contentPane.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("Enter Password :");
		lblNewLabel_2.setBounds(60, 77, 104, 28);
		contentPane.add(lblNewLabel_2);
		
		username = new JTextField();
		username.setBounds(194, 11, 156, 28);
		contentPane.add(username);
		username.setColumns(10);
		
		emailTF = new JTextField();
		emailTF.setColumns(10);
		emailTF.setBounds(194, 43, 156, 28);
		contentPane.add(emailTF);
		
		JButton signUp = new JButton("Sign Up");
		signUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name=username.getText();
				String email=emailTF.getText();
				String pass=password.getText();
				String serverAdd=server.getText();
				int portNo=Integer.parseInt(port.getText());
				if(name!=null && email!=null && pass!=null) 
				{
					client = new Client(serverAdd, portNo, name, pass, email,null,"0");
					if(!client.start()) 
						new Login().main(null);
				}
		}});
			
		signUp.setBounds(133, 202, 163, 36);
		contentPane.add(signUp);
		
		password = new JPasswordField();
		password.setBounds(194, 76, 156, 28);
		contentPane.add(password);
		
		JLabel lblEnterServerAddress = new JLabel("Enter Server Address :");
		lblEnterServerAddress.setBounds(60, 111, 124, 28);
		contentPane.add(lblEnterServerAddress);
		
		JLabel lblEnterPortNumber = new JLabel("Enter Port Number :");
		lblEnterPortNumber.setBounds(60, 144, 104, 28);
		contentPane.add(lblEnterPortNumber);
		
		server = new JTextField();
		server.setText("localhost");
		server.setColumns(10);
		server.setBounds(194, 111, 156, 28);
		contentPane.add(server);
		
		port = new JTextField();
		port.setText("1500");
		port.setColumns(10);
		port.setBounds(194, 145, 156, 28);
		contentPane.add(port);
	}
		
}