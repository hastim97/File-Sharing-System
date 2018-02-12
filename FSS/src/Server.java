import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.JOptionPane;

public class Server {
	
	static Server server;
	private static int uniqueId;
	private ArrayList<ClientThread> al;
	private ServerGUI sg;
	private SimpleDateFormat sdf;
	private int port;
	private boolean keepGoing;
	ArrayList<String> list;
	private static int flag=0;
	private File myFile;
	public Server(int port,ArrayList<String> list) {
		this(port, null,list);
	}
	
	public Server(int port, ServerGUI sg,ArrayList<String> list) {
		this.sg = sg;
		this.port = port;
		this.list=list;
		sdf = new SimpleDateFormat("HH:mm:ss");
		al = new ArrayList<ClientThread>();
	}
	
	public void start() {
		keepGoing = true;
		ServerSocket serverSocket;
		Socket socket;
		try 
		{
			serverSocket = new ServerSocket(port);
			while(keepGoing) 
			{
				display("Server waiting for Clients on port " + port + "."+"\n");
				
				socket = serverSocket.accept();  	// accept connection
				if(!keepGoing)
					break;
				ClientThread t = new ClientThread(socket,list);  // make a thread of it
				al.add(t);									// save it in the ArrayList
				t.start();
			}
			try {
				serverSocket.close();
				for(int i = 0; i < al.size(); ++i) {
					ClientThread tc = al.get(i);
					try {
					tc.sInput.close();
					tc.sOutput.close();
					tc.socket.close();
					}
					catch(IOException ioE) {}
				}
			}
			catch(Exception e) {
				display("\nException closing the server and clients: " + e);
			}
		}
		catch (IOException e) {
            String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
			display(msg);
		}
	}		
	protected void stop() {
		keepGoing = false;
		try {
			new Socket("localhost", port);
		}
		catch(Exception e) {}
	}

	private void display(String msg) {
		String time = sdf.format(new Date()) + " " + msg;
		if(sg == null)
			System.out.println(time);
		else
			sg.appendEvent(time+" ");
	}
	
	synchronized void remove(int id) {
		for(int i = 0; i < al.size(); ++i) {
			ClientThread ct = al.get(i);
			if(ct.id == id) {
				al.remove(i);
				return;
			}
		}
	}
	class ClientThread extends Thread {
		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
		int id;
		int sign=1;
		String username,password;
		ChatMessage cm;
		String date;
		ArrayList<String> list;
		String type;
		String email;
		ClientThread(Socket socket,ArrayList<String> list) {
			id = ++uniqueId;
			this.socket = socket;
			this.list=list;
			System.out.println("Thread trying to create Object Input/Output Streams");
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
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput  = new ObjectInputStream(socket.getInputStream());
				
				String url1 = "jdbc:mysql://localhost:5555/test";
				String user1 = "root";
				String password1 = "root";
				final Connection conn1 = DriverManager.getConnection(url1, user1, password1);
				if (conn1 != null) 
				System.out.println("Connected to the database");				
				type=(String)sInput.readObject();
				System.out.println(type);
				if(type.equals("0"))
				{
					sign=0;
					username = (String) sInput.readObject();
					password = (String) sInput.readObject();
					email=(String) sInput.readObject();
					String query = "insert into users (name,email,password) values (?, ?, ?)";
					PreparedStatement preparedStmt = conn1.prepareStatement(query);
					preparedStmt.setString(1, username);
					preparedStmt.setString(2, email);
					preparedStmt.setString(3, password);
					
					int flag=preparedStmt.executeUpdate();
					if(flag>0)
						display("User data entered successfully");
					else
						display("Data could not be inserted");
					conn1.close();
					remove(id);
					close();
					return;
					
				}
				else{
					sign=1;
					username = (String) sInput.readObject();
					password = (String) sInput.readObject();	
					try 
					{
						String query1="Select * from users;";  	
	                    PreparedStatement ps=conn1.prepareStatement(query1);  	
	                    ResultSet rs=ps.executeQuery(query1);  	
	                    boolean flag=false;
	                    while(rs.next())  	
	                    {  	
	                        String name1=rs.getString("name");  	
	                        String pass1=rs.getString("password");
	                        if(name1.equals(username) && pass1.equals(password))  	
	                        {
	                        	flag=true;
	                        	break;
	                        }
	                        else  	
	                            flag=false;  	
	                    }  	
	                    
						if(flag==true) 
						{
							display(username + " just connected.\n");
							keepGoing=true;
						}
						else
						{
							close();
							remove(id);
							display("Not authenticated");
							return;
						}
		                rs.close();  	
		                conn1.close();  	
					}
					catch(Exception e) {}
				}
			}
			catch (Exception e) {
				if(sign!=0)
					display("\nException creating new Input/output Streams: " + e);
			}
            date = new Date().toString();
		}
		
		public void run() {
			boolean keepGoing = true;
			boolean flag=false;
			for(int i=0;i<list.size();i++)
				writeMsg(list.get(i)+"\n");
			while(keepGoing) {
				try {
					cm = (ChatMessage) sInput.readObject();
				}
				catch (IOException e) {
					if(sign!=0)
					display(username + " Exception reading Streams: " + e);
					break;				
				}
				catch(ClassNotFoundException e2) {
					break;
				}
				String message = cm.getFileName();
				switch(cm.getType()) {
					case ChatMessage.LOGOUT:
						display("\n"+username + " disconnected with a LOGOUT message.");
						keepGoing = false;
						break;
					case ChatMessage.WHOISIN:
						writeMsg("\nList of the users connected at " + sdf.format(new Date()) + "\n");
						// scan al the users connected
						for(int i = 0; i < al.size(); ++i) {
							ClientThread ct = al.get(i);
							writeMsg((i+1) + ") " + ct.username + " since " + ct.date);
						}
						break;
					case ChatMessage.FILE:
						flag=false;
						for(int i=0;i<list.size();i++)
						{
							if(message.equals(list.get(i)))
							{
								writeMsg("SENDING");
								writeMsg(message);
								File myFile=new File("C:\\Users\\Mehta\\eclipse-workspace\\FSS\\Files\\"+message);
								sendFile(myFile);
								flag=true;
								break;
							}
						}
						if(!flag)
							writeMsg("\nFile not found");
						break;
				}
			}
			remove(id);
			close();
		}
		public void sendFile(File myFile)
		{
			BufferedInputStream bis=null;
			FileInputStream  fis=null;
			try
			{
				byte[] content=Files.readAllBytes(myFile.toPath());
				sOutput.writeObject(content);
		        System.out.println("Done.");
			}
			catch(Exception ei) {}
		}

		private void close() {
			try {
				if(sOutput != null) sOutput.close();
			}
			catch(Exception e) {}
			try {
				if(sInput != null) sInput.close();
			}
			catch(Exception e) {};
			try {
				if(socket != null) socket.close();
			}
			catch (Exception e) {}
		}

		private boolean writeMsg(File myFile) {
			if(!socket.isConnected()) {
				close();
				return false;
			}
			try {
				sOutput.writeObject(myFile);
			}
			catch(Exception e) {}
			return true;
		}
		private boolean writeMsg(String msg) {
			if(!socket.isConnected()) {
				close();
				return false;
			}
			try {
				sOutput.writeObject(msg);
			}
			catch(IOException e) {
				if(sign!=0)
					display("\nError sending message to " + username);
			}
			return true;
		}
	}
}