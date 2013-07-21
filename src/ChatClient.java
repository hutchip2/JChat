
/**
 * 
 * A multi-threaded client for a chat instance.
 * 
 * @author Paul Hutchinson
 *
 */
public class ChatClient  {

	/**
	 * Constructor for the chat client.
	 */
	public ChatClient()	{
		ClientImplementer ci = new ClientImplementer();	//creates an instance of the client thread class
		Thread t = new Thread(ci);						//creates a thread using the client runnable
		t.start();										//starts the thread
	}

	/**
	 * Main method for the chat client, creates an instance of a chat client.
	 * @param args
	 */
	public static void main(String args[])	{
		new ChatClient();
	}
}
