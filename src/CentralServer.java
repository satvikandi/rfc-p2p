import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
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
	        return hostName + ": " + listeningPort + '\n';
	    }
	}

	private class Rfc {
		private Integer rfcNum;
		String title;
		private String host;

		public Rfc(Integer rfcNum, String title, String host) {
			this.setRfcNum(rfcNum);
			this.title = title;
			this.setHost(host);
		}
		
	    @Override
	    public String toString() {
	        return getRfcNum() + " \"" + title + "\" at " + getHost() + "\n";
	    }

		/**
		 * @return the host
		 */
		String getHost() {
			return host;
		}

		/**
		 * @param host the host to set
		 */
		void setHost(String host) {
			this.host = host;
		}

		/**
		 * @return the rfcNum
		 */
		Integer getRfcNum() {
			return rfcNum;
		}

		/**
		 * @param rfcNum the rfcNum to set
		 */
		void setRfcNum(Integer rfcNum) {
			this.rfcNum = rfcNum;
		}
	}



	LinkedList<ActivePeer> peerList;

	LinkedList<Rfc> index;

	static final int LISTENINGPORT = 7134;
	static final String END_OF_PACKET = "END_OF_PACKET\n";
	String version = "P2P-CI/1.0";

	// Constructor
	public CentralServer() {
		peerList = new LinkedList<ActivePeer>();
		index = new LinkedList<Rfc>();
		version = "P2P-CI/1.0";

		System.out
				.println("SERVER:- \n \n");
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
			
			
			DataOutputStream  outToClient = 
		             new DataOutputStream(connectionSocket.getOutputStream()); 
			
			
			// In one session of communication, reads all the available lines in one packet. 
			String clientSentence = null;
			String request = "";
			String response = "";
			while (!((clientSentence = inFromClient.readLine().trim()).equals(END_OF_PACKET.trim()))){
				request += clientSentence + "\n";
			}
			
			System.out.println(request);
			
			if (request.substring(0, 3).equals("Hi!")){
				response = "This is default 'HI' response";
				addPeer(request);
				//outToClient.writeBytes(response);
			}
			if (request.substring(0, 3).equals("ADD")){
				response = "This is default ADD response.";
				addRfc(request);
				//outToClient.writeBytes(response);
			}
			
			if (request.substring(0, 6).equals("LOOKUP")){
				response = "This is default LOOKUP response.\n";
				response = lookupRfc(request);
				System.out.print(response);
				outToClient.writeBytes(response);
				outToClient.flush();
				
			}
			
			if (request.substring(0, 4).equals("LIST")){
				getIndex(request);
				outToClient.writeBytes(response + '\n');
				
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
		//System.out.println("Index looks like this now:- \n");
		//System.out.println(this.index.toString());
	}
	
	
		
	private String lookupRfc(String packet) 
	{
		System.out.println(packet);
		String packetLines[] = packet.split("\\n");
		String rfcNumString = packetLines[0].split(" ")[2];
		int rfcNum = Integer.valueOf(rfcNumString);
		
		String response = version + " 200 OK\n";
		
		ListIterator indexIterator = index.listIterator();

		while(indexIterator.hasNext()){
			Rfc currentRfc = (Rfc) indexIterator.next(); 
			if (currentRfc.getRfcNum() == rfcNum){
				//If rfc is present in index, get its host's listening port
				ListIterator peerListIterator = peerList.listIterator();
				while(peerListIterator.hasNext()){
					ActivePeer currentPeer = (ActivePeer) peerListIterator.next(); 
					if (currentPeer.hostName.equals(currentRfc.host)){
						response += "RFC " + currentRfc.rfcNum + " " + currentRfc.title + " " + currentRfc.host + " " + currentPeer.listeningPort + '\n';
						break;
					}
				}
			}
		}
		
		response += END_OF_PACKET;

		//System.out.println(response);
		return response;
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
