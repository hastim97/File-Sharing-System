import java.net.*;
import java.nio.file.Files;
import java.awt.FileDialog;
import java.io.*;
import java.util.*;

public class Client  {

	private ObjectInputStream sInput;		// to read from the socket
	private ObjectOutputStream sOutput;		// to write on the socket
	private Socket socket;

	private ClientGUI cg;
	
	private String server, username, password;
	private int port;
	private String type;

	ChatMessage cm;
	String email;
	Client(String server, int port, String username, String password,String email, ClientGUI cg,String type) {
		this.server = server;
		this.port = port;
		this.username = username;
		this.password=password;
		this.cg = cg;
		this.type=type;
		this.email=email;
	}
	
	public boolean start() {
		try {
			socket = new Socket(server, port);
		} 
		catch(Exception ec) {
			display("Error connectiong to server" + "\nCheck your username and password or the server address and port number.");
			return false;
		}
		
		String msg = "\nConnection accepted " + socket.getInetAddress() + ":" + socket.getPort();
		if(type.equals("1"))
			display(msg);

		try
		{
			sInput  = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
		}
		catch (IOException eIO) {
			if(type.equals("1"))
			display("\nException creating new Input/output Streams: " + eIO);
			return false;
		}

		new ListenFromServer(socket,type).start();
		try
		{
			sOutput.writeObject(type);
			if(type.equals("0"))
			{
				sOutput.writeObject(username);
				sOutput.writeObject(password);
				sOutput.writeObject(email);
				disconnect();
				return false;
			}
			else
			{
				sOutput.writeObject(username);
				sOutput.writeObject(password);
			}
		}
		catch (Exception eIO) {
			if(type.equals("1"))
				display("Exception doing login : " + eIO);
			disconnect();
			return false;
		}		// success we inform the caller that it worked
		return true;
	}

	private void display(String msg) {
		if(cg == null)
			System.out.println(msg);      // println in console mode
		else if(type.equals("1"))
			cg.append(msg + "\n");		// append to the ClientGUI JTextArea (or whatever)
	}
	
	void sendMessage(ChatMessage msg) {
		try {
			sOutput.writeObject(msg);
		}
		catch(IOException e) {
			display("Exception writing to server: " + e);
		}
	}

	private void disconnect() {
		try { 
			if(sInput != null) sInput.close();
		}
		catch(Exception e) {} // not much else I can do
		try {
			if(sOutput != null) sOutput.close();
		}
		catch(Exception e) {} // not much else I can do
        try{
			if(socket != null) socket.close();
		}
		catch(Exception e) {} // not much else I can do
		
		if(cg != null)
			cg.connectionFailed();
			
	}	
	
	class ListenFromServer extends Thread 
	{
		Socket socket;
		String type;
		ListenFromServer(Socket socket,String type){
			this.socket=socket;
			this.type=type;
		}
		
		private void getFile(String targetFileName) throws IOException 
		{
			try
			{
				File receiveFile=new File(targetFileName);
				byte[] content=(byte[])sInput.readObject();
				Files.write(receiveFile.toPath(),content);
				System.out.println("File " + targetFileName + " downloaded"); // (" + current + " bytes read)");
			}
			catch(Exception e) {}
		}
		
		public void run() {
			int receive=0;
			File myFile;
			while(true) {
				try {
					String msg = (String) sInput.readObject();
					if(msg.equals("SENDING")) {
						receive=1;
					}
					else if(receive==1) {
						receive=0;
						FileDialog choo = new FileDialog(cg,"Choose File Destination",FileDialog.SAVE);
						choo.setDirectory(null);
						choo.setFile("Enter file name (use the same extension)");
						choo.setVisible(true);
						String targetFileName = choo.getDirectory()+choo.getFile() ;
						System.out.println("File will be saved to: " + targetFileName);
						getFile(targetFileName);
					}
					if(cg == null) {
						System.out.println(msg);
						System.out.print("> ");
					}
					else {
						cg.append(msg+"\n");
					}
				}
				catch(IOException e) {
					if(type.equals("0"))
						display("User successfully signed up!");
					else
						display("\nServer has closed the connection: " + e);
					if(cg != null) 
						cg.connectionFailed();
					break;
				}
				catch(ClassNotFoundException e2) {
				}
			}
		}
	}
}