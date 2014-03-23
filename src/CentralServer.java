import java.util.LinkedList;
import java.net.*;
import java.io.*;

public class CentralServer {

	private class ActivePeer {
		String hostName;
		Integer listeningPort;

		public ActivePeer(String host, Integer pnum) {
			hostName = host;
			listeningPort = pnum;
		}
	}

	private class Rfc {
		Integer rfcNum;
		String title;
		String host;

		public Rfc(Integer rfcNum, String title, String host) {
			this.rfcNum = rfcNum;
			this.title = title;
			this.host = host;
		}
	}



	LinkedList<ActivePeer> peerList;

	LinkedList<Rfc> index;

	static final int LISTENINGPORT = 7134;

	// Constructor
	public CentralServer() {
		peerList = new LinkedList<ActivePeer>();
		index = new LinkedList<Rfc>();

		System.out
				.println("This is the java class that represents the central server \n \n");
	}

	public void startListening() throws Exception {
		@SuppressWarnings("resource")
		ServerSocket welcomeSocket = new ServerSocket(LISTENINGPORT);

		// Communicates to one socket in one iteration.
		// Seems to be atomic for that session of communication. 
		while (true) {
			Socket connectionSocket = welcomeSocket.accept();
			BufferedReader inFromClient = new BufferedReader(
					new InputStreamReader(connectionSocket.getInputStream()));
			
			
			// In one session of communication, reads all the available lines. 
			String clientSentence = null;
			String packet = "";
			while((clientSentence = inFromClient.readLine()) != null){
				packet += clientSentence + "\n";
			}
			
			//System.out.println(packet);
			if (packet.substring(0, 3).equals("ADD")){
				addRfc(packet);
				//addPeer();
				
			}
			
			if (packet.substring(0, 6).equals("LOOKUP")){
				lookupRfc(packet);
				
			}
			
			if (packet.substring(0, 4).equals("LIST")){
				getIndex(packet);
				
			}
	

		}

	}
	
	public void addRfc(String packet) {

		String rfcNumString = packet.substring(8, 11);
		int rfcNum = Integer.valueOf(rfcNumString);

		int indexOfTitle = packet.indexOf("Title:");
		String title = packet.substring(indexOfTitle + 7);

		int indexOfHost = packet.indexOf("Host:");
		String host = packet.substring(indexOfHost + 6);
	
		Rfc rfc = new Rfc(rfcNum, title, host);
		index.add(rfc);
	}
	
	private void lookupRfc(String packet) {
		// TODO Auto-generated method stub
		
	}

	private void getIndex(String packet) {
		// TODO Auto-generated method stub
		
	}

	// Adds the new peer to the ActivePeers list
	// This method is called when the listening socket receives a new peer
	// Adds peer to ActivePeers
	public void addPeer() {

	}

	// Removes peer from peer list
	// emoves all his RFCs from the index
	public void removePeer() {

	}

	
	public static void main(String args[]) throws Exception {
		CentralServer CS = new CentralServer();
		CS.startListening();
		System.out.println(CS.peerList.toString());

	}
}
