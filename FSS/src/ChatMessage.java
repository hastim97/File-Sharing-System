import java.io.*;
/*
 * This class defines the different type of messages that will be exchanged between the
 * Clients and the Server. 
 * When talking from a Java Client to a Java Server a lot easier to pass Java objects, no 
 * need to count bytes or to wait for a line feed at the end of the frame
 */
public class ChatMessage implements Serializable {

	protected static final long serialVersionUID = 1112122200L;

	// The different types of message sent by the Client
	// WHOISIN to receive the list of the users connected
	// MESSAGE an ordinary message
	// LOGOUT to disconnect from the Server
	static final int WHOISIN = 0, MESSAGE = 1, LOGOUT = 2, FILE = 3;
	private int type;
//	private String message;
	private String filename;
	
	// constructor
	ChatMessage(int type, String message) {
		this.type = type;
		this.filename = message;
	}
	
	// getters
	int getType() {
		return type;
	}
	String getFileName() {
		return filename;
	}
	
}