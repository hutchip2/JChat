import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import javax.swing.JOptionPane;

/**
 * 
 * The thread class for the client.
 * 
 * @author Paul Hutchinson
 *
 */
public class ClientImplementer implements Runnable {

	int response;
	String ip;
	String sn;
	ServerSocket serverSocket;
	Socket socket;
	DataOutputStream dos;
	Scanner scan;

	/**
	 * Override of the runnable class for the client thread.
	 */
	public void run() {
		System.out.println("CLIENT");
		scan = new Scanner(System.in);
		System.out.println("Enter server ip (blank for localhost): ");	//ask for ip
		ip = scan.nextLine();	
		if (ip.equals(""))	{											//if ip is left blank, use localhost
			ip = new String("127.0.0.1");
		};
		System.out.println("Enter your screen name: ");					//ask for users screen name
		sn = scan.nextLine();											//read-in users screen name

		try {
			System.out.println("Contacting server at: " + ip + " on port " + ChatServer.SERVER_PORT);
			socket = new Socket(InetAddress.getByName(ip), ChatServer.SERVER_PORT);	//create the socket to connect to server
			System.out.println("Connected to chat server");
			dos = new DataOutputStream(socket.getOutputStream());
			chooseOperation();
			dos.writeUTF(ip);		//write the ip to the server
			dos.writeUTF(sn);		//write the screen name to the server
			startChat();
		} catch (IOException e1) {
			e1.printStackTrace();
		}	
	}	

	/**
	 * Opens an option pane for the client to choose an action.
	 */
	public void chooseOperation()	{
		String[] choices = {"Login to server...", "Logout of server", "Request user contact info...", "Quit"};
		response = JOptionPane.showOptionDialog(null, "What operation would you like to perform?"
				, "Perform task with chat server...", JOptionPane.YES_NO_OPTION  
				, JOptionPane.PLAIN_MESSAGE, null, choices, "Default");
		try	{
			if (response == 0)	{
				dos.writeInt(0);	//tell the server that the client wants to be added
			}
			if (response == 1)	{
				dos.writeInt(1);	//tell the server that the client wants to be removed
			}
			if (response == 2)	{
				dos.writeInt(2);	//tell the server that the client wants to request information on another user
			}
			if (response == 3)	{
				dos.writeInt(3);	//tell the server that the client wants to quit
				socket.close();		//close the socket
				System.exit(0);		//exit the program
			}
		} catch (IOException e)	{
			e.printStackTrace();
		}
	}

	/**
	 * Starts the chat for the client.
	 */
	public void startChat()	{
		try	{
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			int mapSize = dis.readInt();				//reads in how many clients are added to the server currently
			int listeningPort = socket.getLocalPort();	//gets the port that the socket is listening on
			if (mapSize==0)	{									//if no one is logged in to the server...
				socket.close();
				serverSocket = new ServerSocket(listeningPort);	//allow the client to listen for other clients
				System.out.println("listening for clients...");
				socket = serverSocket.accept();					//listen
				new ChatThread(socket, sn);						//create a chat thread when connected
			}	else	{										//if other clients are logged into the server
				System.out.println("Enter a screen name to connect with: ");
				String contact = scan.nextLine();				//reads in the contact that the client wants to contact
				dos.writeUTF(contact);							//writes the contact to the server
				String ip = dis.readUTF();						//reads in the ip of the requested contact
				ip = ip.substring(1, ip.length());				//removes the '/' from the ip
				int port = dis.readInt();						//reads in the port of the requested contact
				socket.close();									//close the socket with the server
				socket = new Socket(ip, port);					//open the socket for another client
				new ChatThread(socket, sn);						//create chat thread
			}	
		} catch (IOException e)	{
			e.printStackTrace();
		}
	}
}
