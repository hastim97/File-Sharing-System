import javax.swing.*;
import javax.swing.filechooser.FileView;

import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class ClientGUI extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JLabel label;
	private JTextField tf;
	private JButton logout, whoIsIn,getFile;
	private JTextArea ta;
	private boolean connected;
	private Client client;

	String username,password,host;
	int port;
	public File myFile;
	public static String fileName;
	private String type;
	String email;
	ClientGUI(String host, int port,String username,String password,String email,String type) {

		super("File Sharing Client");
		this.host=host.trim();
		this.port=port;
		this.username=username.trim();
		this.password=password.trim();
		this.type=type;
		this.email=email;
		JPanel northPanel = new JPanel(new GridLayout(3,1));
		JPanel serverAndPort = new JPanel(new GridLayout(1,5, 1, 3));
		northPanel.add(serverAndPort);
		label = new JLabel("Enter your username below", SwingConstants.CENTER);
		northPanel.add(label);
		tf = new JTextField(username);
		tf.setBackground(Color.WHITE);
		northPanel.add(tf);
		add(northPanel, BorderLayout.NORTH);
		ta = new JTextArea("Welcome to the File Sharing Center\n", 80, 80);
		JPanel centerPanel = new JPanel(new GridLayout(1,1));
		centerPanel.add(new JScrollPane(ta));
		ta.setEditable(false);
		add(centerPanel, BorderLayout.CENTER);
		logout = new JButton("Logout");
		logout.addActionListener(this);
		logout.setEnabled(false);		// you have to login before being able to logout
		whoIsIn = new JButton("Who is in");
		whoIsIn.addActionListener(this);
		whoIsIn.setEnabled(false);		// you have to login before being able to Who is in
		getFile=new JButton("Get File");
		getFile.addActionListener(this);
		getFile.setEnabled(false);
		JPanel southPanel = new JPanel();
		southPanel.add(logout);
		southPanel.add(whoIsIn);
		southPanel.add(getFile);
		add(southPanel, BorderLayout.SOUTH);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(600, 600);
		setVisible(true);
		tf.requestFocus();

		client = new Client(host, port, username, password, email,this,type);
		if(!client.start()) 
			return;
		
		tf.setText("");
		label.setText("Enter the required file name below");
		connected = true;
		
		logout.setEnabled(true);
		whoIsIn.setEnabled(true);
		getFile.setEnabled(true);
		tf.addActionListener(this);
	}

	void append(String str) {
		ta.append(str);
		ta.setCaretPosition(ta.getText().length() - 1);
	}

	void connectionFailed() {
		logout.setEnabled(false);
		whoIsIn.setEnabled(false);
		getFile.setEnabled(false);
		label.setText("Enter your username below");
		tf.setText(username);
		tf.removeActionListener(this);
		connected = false;
	}

	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		if(o == logout) {
			client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
			System.exit(0);
			logout.setEnabled(false);
			whoIsIn.setEnabled(false);
			getFile.setEnabled(false);
			label.setText("Enter your username below");
			tf.setText(username);
			connected = false;
			return;
		}

		if(o == whoIsIn) {
			client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));				
			return;
		}
		if(o == getFile) {			
		}

		if(connected) {
			tf.setEnabled(true);
			tf.setEditable(true);
			client.sendMessage(new ChatMessage(ChatMessage.FILE, tf.getText()));				
			tf.setText("");
			return;
		}
	}
		
	public static void main(String[] args) {
		Login l=new Login();
		l.main(null);
	}
}