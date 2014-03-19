import java.util.LinkedList;
import java.net.*;
import java.io.*;

public class CentralServer {

	LinkedList<ActivePeer> peerList;
	LinkedList index;
	static final int LISTENINGPORT = 7134;
		
	private class IndexElement
	{
		Integer RFCNum;
		String RFCTitle;
		String hostName;
		
		public IndexElement(Integer num, String title,String host)
		{
			RFCNum=num;
			RFCTitle=title;
			hostName=host;
		}
	}
	
	private class ActivePeer
	{
		String hostName;
		Integer listeningPort;
		
		public ActivePeer(String host, Integer pnum)
		{
			hostName=host;
			listeningPort=pnum;
		}
	}
	
	public CentralServer() 
	{
		peerList = new LinkedList<ActivePeer>();
		index = new LinkedList<IndexElement>();
				
		System.out.println("This is the java class that represents the central server");
	}
	
	// Adds the new peer to the ActivePeers list
	// This method is caelled when the listening socket receives a new peer
	// Adds peer to Activepeers and Adds RFCS to Index
	public void createPeer(){
	
	
	}
	
	// Removes peer from peer list
	// removes all his RFCs from the index
	public void removePeer(){
		
	}
	
	public static void main(String args[]) throws Exception 
	{
		@SuppressWarnings("resource")
		ServerSocket welcomeSocket = new ServerSocket(LISTENINGPORT);
		
		while(true)
		{
			Socket connectionSocket = welcomeSocket.accept();
			BufferedReader inFromClient = new BufferedReader(new
		             InputStreamReader(connectionSocket.getInputStream()));
			
			String clientSentence = inFromClient.readLine();
			
			System.out.println(clientSentence);
			
			

		}
	}
}




