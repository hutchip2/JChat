import java.net.ServerSocket;


public class ListenForConnection {

	
	public static void main(String[] args) throws Exception
	{		
		ServerSocket serveSocket = new ServerSocket(31200);
		
		new ChatThread( serveSocket.accept(), "ListenForConnection" );
		
	}

}
