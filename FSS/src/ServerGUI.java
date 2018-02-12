import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;

public class ServerGUI extends JFrame implements ActionListener, WindowListener {
	
	private static final long serialVersionUID = 1L;
	private JButton stopStart;
	private JTextArea event;
	private JTextField tPortNumber;
	private Server server;
	ArrayList<String> listFiles;
	
	ServerGUI(int port) 
	{
		super("File Server");
		server = null;
		JPanel north = new JPanel();
		north.add(new JLabel("Port number: "));
		tPortNumber = new JTextField("  " + port);
		north.add(tPortNumber);
		stopStart = new JButton("Start");
		stopStart.addActionListener(this);
		north.add(stopStart);
		add(north, BorderLayout.NORTH);
		JPanel center = new JPanel(new GridLayout(1,1));
		event = new JTextArea(80,80);
		event.setEditable(false);
		appendEvent("Events log.\n");
		appendEvent("\nFiles on the server:\n");
		File folder=new File("C:\\Users\\Mehta\\eclipse-workspace\\FSS\\Files");
		File[] listOfFiles = folder.listFiles();
		listFiles=new ArrayList<String>();
		for(int i=0;i< listOfFiles.length;i++)
		{
			if(listOfFiles[i].isFile())
			{
				listFiles.add(listOfFiles[i].getName());
				appendEvent(listOfFiles[i].getName()+"\n");
			}
		}
		
		center.add(new JScrollPane(event));	
		add(center);
		addWindowListener(this);
		setSize(400, 600);
		setVisible(true);
	}		

	void appendEvent(String str) {
		event.append(str);
		event.setCaretPosition(event.getText().length() - 1);		
	}
	
	public void actionPerformed(ActionEvent e) {
		if(server != null) {
			server.stop();
			server = null;
			tPortNumber.setEditable(true);
			stopStart.setText("Start");
			return;
		}
		int port;
		try {
			port = Integer.parseInt(tPortNumber.getText().trim());
		}
		catch(Exception er) {
			appendEvent("\nInvalid port number");
			return;
		}
		server = new Server(port, this,listFiles);
		new ServerRunning().start();
		stopStart.setText("Stop");
		tPortNumber.setEditable(false);
	}
	
	public static void main(String[] arg) {
		new ServerGUI(1500);
	}

	public void windowClosing(WindowEvent e) {
		if(server != null) {
			try {
				server.stop();			// ask the server to close the conection
			}
			catch(Exception eClose) {
			}
			server = null;
		}
		dispose();
		System.exit(0);
	}
	public void windowClosed(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}

	class ServerRunning extends Thread {
		public void run() {
			server.start();         // should execute until if fails
			stopStart.setText("Start");
			tPortNumber.setEditable(true);
			appendEvent("Server stopped\n");
			server = null;
		}
	}
}