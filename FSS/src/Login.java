import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class Login 
{

	private JFrame frame;
	private JTextField username;
	private JPasswordField passTF;
	private JLabel lblNotAUser;
	private JButton signUp;
	private JLabel lblEnterPortNumber;
	private JTextField server;
	private JTextField port;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login window = new Login();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	public Login() {
		initialize();
	}

	private void initialize() 
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
		}
		catch(ClassNotFoundException e) 
		{
			System.out.println("CNFE");
		}
		try
		{
			frame = new JFrame();
			frame.setBounds(100, 100, 450, 300);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.getContentPane().setLayout(null);
			
			JLabel lblUsername = new JLabel("Enter Username:");
			lblUsername.setBounds(38, 11, 148, 31);
			frame.getContentPane().add(lblUsername);
			
			JLabel lblPassword = new JLabel("Enter Password:");
			lblPassword.setBounds(38, 48, 168, 31);
			frame.getContentPane().add(lblPassword);
			
			username = new JTextField();
			username.setBounds(227, 11, 148, 31);
			frame.getContentPane().add(username);
			username.setColumns(10);
			
			passTF = new JPasswordField();
			passTF.setBounds(226, 49, 149, 31);
			frame.getContentPane().add(passTF);
			
			JButton login = new JButton("Login");
			login.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					String name=username.getText();
					String pass=passTF.getText();
					int portNo=Integer.parseInt(port.getText());
					String serverAdd=server.getText();
                    if(name!=null && pass!=null && portNo!=0 && serverAdd!=null){
							ClientGUI cg=new ClientGUI(serverAdd,portNo,name,pass,null,"1");
							cg.setVisible(true);
					}
					else
						JOptionPane.showMessageDialog(frame,"Error while logging in!" );
				}
			});
			login.setBounds(137, 174, 148, 31);
			frame.getContentPane().add(login);
			
			lblNotAUser = new JLabel("Not a User?");
			lblNotAUser.setBounds(120, 216, 63, 22);
			frame.getContentPane().add(lblNotAUser);
			
			signUp = new JButton("Sign Up!");
			signUp.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					SignUp s=new SignUp();
					s.setVisible(true);
				}
			});
			signUp.setBounds(193, 216, 89, 23);
			frame.getContentPane().add(signUp);
			
			JLabel lblEnterServerAddress = new JLabel("Enter Server Address:");
			lblEnterServerAddress.setBounds(38, 90, 168, 31);
			frame.getContentPane().add(lblEnterServerAddress);
			
			lblEnterPortNumber = new JLabel("Enter Port Number:");
			lblEnterPortNumber.setBounds(37, 133, 168, 31);
			frame.getContentPane().add(lblEnterPortNumber);
			
			server = new JTextField();
			server.setText("localhost");
			server.setColumns(10);
			server.setBounds(227, 91, 148, 31);
			frame.getContentPane().add(server);
			
			port = new JTextField();
			port.setText("1500");
			port.setColumns(10);
			port.setBounds(227, 133, 148, 31);
			frame.getContentPane().add(port);
		}
		catch (Exception ex) 
		{
			System.out.println("An error occurred. Maybe user/password is invalid");
			ex.printStackTrace();
		}
	}
}
