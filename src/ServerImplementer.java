import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * The thread class for the server.
 * 
 * @author Paul Hutchinson
 *
 */
public class ServerImplementer implements Runnable {

	ConcurrentHashMap<String, InetSocketAddress> clientMap = new ConcurrentHashMap<String, InetSocketAddress>();
	ServerSocket serverSocket;
	Socket socket;
	String sn;
	String ip;
	int action;
	DataInputStream dis;
	DataOutputStream dos;

	/**
	 * Override of the runnable class for the server thread.
	 */
	public void run() {
		while (true)	{
			try {
				System.out.println("SERVER");
				serverSocket = new ServerSocket(ChatServer.SERVER_PORT);				//create the server socket
				System.out.println("listening...");
				socket = serverSocket.accept();											//wait for a connection			
				System.out.println("Connected to port " + socket.getLocalPort() + " on " + socket.getLocalAddress());
				dis = new DataInputStream(socket.getInputStream());		
				dos = new DataOutputStream(socket.getOutputStream());
				action = dis.readInt();												//read the user's choice
				ip = dis.readUTF();														//read the user's ip
				sn = dis.readUTF();														//read the user's screen name
				dos.writeInt(clientMap.size());											//write the current map size
				readAction();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Reads the action from the client.
	 */
	public void readAction()	{
		try	{
			if (action == 0){														//login to server
				if (clientMap.size() > 0)	{
					String queryName = dis.readUTF();								//read in the query
					dos.writeUTF(requestContact(queryName).getAddress().toString());//write the query results
					dos.writeInt(requestContact(queryName).getPort());
				}
				InetSocketAddress address = new InetSocketAddress(socket.getInetAddress(), socket.getPort());
				addToServer(sn, address);											//add the user to the server
			}
			if (action == 1){	//logout of server
				removeFromServer(sn);
			}
			if (action == 2){	//request user contact
				System.out.println("Enter a screen name to lookup: ");
				Scanner scan = new Scanner(System.in);
				String name = scan.nextLine();
				System.out.println("Contact info for " + name + ": " + requestContact(name));
				socket.close();
				serverSocket.close();
			}
		} catch (IOException e)	{
			e.printStackTrace();
		}
	}

	/**
	 * Adds a user and their information to the server.
	 * @param screenName
	 * @param address
	 */
	public void addToServer(String screenName, InetSocketAddress address)	{
		try {
			clientMap.put(screenName, address);	//stores the client's InetAddress using their userName as the key
			System.out.println(screenName + " has been added to the server!");
			socket.close();						//closes the Socket
			serverSocket.close();				//closes the ServerSocket
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Removes a user and their information from the server.
	 * @param screenName
	 */
	public void removeFromServer(String screenName)	{
		if (screenName != null)	{				//remove the screen name if it isn't null
			clientMap.remove(screenName);
			System.out.println(screenName + " has been removed from the server!");
		}
		try {
			socket.close();						//closes the Socket
			serverSocket.close();				//closes the ServerSocket
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the contact information of a user who is logged into the server.
	 * @param sn
	 * @return InetSocketAddress of the requested user
	 */
	public InetSocketAddress requestContact(String sn)	{
		if (sn!=null)	{
			return clientMap.get(sn);			
		} else	{
			return null;
		}
	}

}
