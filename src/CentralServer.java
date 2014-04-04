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
		
	    @Override
	    public String toString() {
	        return hostName + ": " + listeningPort;
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
		
	    @Override
	    public String toString() {
	        return rfcNum + "-" + title + " at " + host;
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
			
			
			// In one session of communication, reads all the available lines in one packet. 
			String clientSentence = null;
			String packet = "";
			while((clientSentence = inFromClient.readLine()) != null){
				packet += clientSentence + "\n";
			}
			
			System.out.println(packet);
			
			if (packet.substring(0, 3).equals("Hi!")){
				addPeer(packet);
				
			}
			if (packet.substring(0, 3).equals("ADD")){
				addRfc(packet);
				
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
		
		String packetLines[] = packet.split("\\n");

		String rfcNumString = packetLines[0].substring(8, 11);
		int rfcNum = Integer.valueOf(rfcNumString);

		String host = packetLines[1].substring(6);
		String title = packetLines[3].substring(7);


	
		Rfc rfc = new Rfc(rfcNum, title, host);
		index.add(rfc);
		System.out.println("Index looks like this now:- \n");
		System.out.println(this.index.toString());
	}
	
	
		
	private void lookupRfc(String packet) 
	{
		
		// TODO Auto-generated method stub
		
	}

	private void getIndex(String packet) {
		// TODO Auto-generated method stub
		
	}

	// Adds the new peer to the ActivePeers list
	// This method is called when the listening socket receives a new peer
	// Adds peer to ActivePeers
	public void addPeer(String packet) {

		String packetLines[] = packet.split("\\n");
		//System.out.println(packetLines[1]);
		//System.out.println(packetLines[2]);
		
		String hostName = packetLines[1];
		int port = Integer.valueOf(packetLines[2].trim());

		ActivePeer newPeer = new ActivePeer(hostName, port);
		peerList.add(newPeer);
		
		System.out.println("PeerList looks like this now:- \n");
		System.out.println(this.peerList.toString());
	}

	// Removes peer from peer list
	// emoves all his RFCs from the index
	public void removePeer() {

	}

	
	public static void main(String args[]) throws Exception {
		CentralServer CS = new CentralServer();
		CS.startListening();

	}
}
