
/**
 * 
 * A multi-threaded server for a chat instance.
 * 
 * @author Paul Hutchinson
 *
 */
public class ChatServer {

	public static final int SERVER_PORT = 32100;		//port number used to connect to the server

	/**
	 * Constructor for the server.
	 */
	public ChatServer()	{
		ServerImplementer r1 = new ServerImplementer();	//creates an instance of the server thread
		Thread t = new Thread(r1);						//creates a thread using the runnable for the server
		t.start();										//starts the thread
	}

	/**
	 * Main method for the server, creates an instance of a chat server.
	 * @param args
	 */
	public static void main(String args[]){
		new ChatServer();
	}
}
