
import java.util.LinkedList;
import java.util.ListIterator;
import java.net.*;
import java.io.*;
import java.util.*;
//import Server.RFCServer;



public class Server {

	//static final int LISTENINGPORT = 7134;
	static final String END_OF_PACKET = "END_OF_PACKET\n";
	static final String version = "P2P-CI/1.0";

		static List<ActivePeer> peerList = new LinkedList<ActivePeer>();
	static List<Rfc>index = new LinkedList<Rfc>();
	
	
	public static void main(String args[]) throws Exception {
		
			System.out.println("Server");
	       /* if(args.length != 1) {
	            System.out.println("Server usage: Server #port");
	            System.exit(-1);
	        } */

	        ServerSocket listener = new ServerSocket(Integer.parseInt(args[0]));
	        while(true) {
	            new CentralServer(listener.accept()).start();
	        }
	}
	private static class ActivePeer {
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

	private static class Rfc {
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
	private static class CentralServer extends Thread
	{
		
	//	LinkedList<ActivePeer> peerList;
	//	LinkedList<Rfc> index;
	
	private Socket socket;
    BufferedReader in;
    DataOutputStream out;
	// Constructor
	public CentralServer(Socket sock) {
		
		System.out.println("A new Thread \n \n");
		 this.socket = sock;
         try {
             this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
             this.out = new DataOutputStream(this.socket.getOutputStream()); 
         } catch (IOException e) {
             e.printStackTrace();
         }
	}
		//version = "P2P-CI/1.0"
         
         @Override
         public void run() {
             System.out.println("Hi.. This is a new connection for a peer");

             try {
                 startListening();
             } catch (Exception e) {
                 e.printStackTrace();
             } finally {
                 try {
                     socket.close();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
         }
	

	public void startListening() throws Exception {
		//@SuppressWarnings("resource")
		// ServerSocket welcomeSocket = new ServerSocket(LISTENINGPORT);

		// Communicates to one socket in one iteration.
		// Seems to be atomic for that session of communication. 
		//while (true) {
		//	Socket connectionSocket = welcomeSocket.accept();
		//	BufferedReader inFromClient = new BufferedReader(
		//			new InputStreamReader(connectionSocket.getInputStream()));
			
			
		//	DataOutputStream  outToClient = 
		//             new DataOutputStream(connectionSocket.getOutputStream()); 
			
			
			// In one session of communication, reads all the available lines in one packet. 
			
			while(true)
			{
			String clientSentence = null;
			String request = "";
			String response = "";
			
			while (!((clientSentence = this.in.readLine().trim()).equals(END_OF_PACKET.trim())))
			{
				request += clientSentence + "\n";
			}
			
			
			
			if (request.substring(0, 3).equals("Hi!")){
				System.out.println("");
				System.out.println(request);  // Prints Hi I'm .....
				response = "This is default 'HI' response";
				addPeer(request);
				continue;
				//outToClient.writeBytes(response);
			}
			else if (request.substring(0, 3).equals("ADD")){
				//response = "This is default ADD response.";
				//System.out.println("The peer is adding all it's RFCs to the RFC List in the server one by one \n");
				addRfc(request);
				continue;
				//outToClient.writeBytes(response);
			}
			
			else if (request.substring(0, 6).equals("LOOKUP")){
			
				response = "This is default LOOKUP response.\n";
				response = lookupRfc(request);
				//System.out.print(response);
				this.out.writeBytes(response);
				this.out.flush();
				continue;
			}
			
			else if (request.substring(0, 4).equals("LIST")){
				response = getIndex(request);
				//System.out.println("This is the calling method");
				System.out.println(response);
				this.out.writeBytes(response + '\n');
				this.out.flush();
				
			}
			
			else
			{
				System.out.println("Server cannot recognize such a message \n");
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
		System.out.println(index.toString());
	}
	
	
		
	private String lookupRfc(String packet) 
	{
		//System.out.println(packet);
		String packetLines[] = packet.split("\\n");
		String rfcNumString = packetLines[0].split(" ")[2];
		int rfcNum = Integer.valueOf(rfcNumString);
		
		String response = version + " 200 OK\n";
		
		ListIterator<Rfc> indexIterator = index.listIterator();

		while(indexIterator.hasNext()){
			Rfc currentRfc = (Rfc) indexIterator.next(); 
			if (currentRfc.getRfcNum() == rfcNum){
				//If rfc is present in index, get its host's listening port
				ListIterator<ActivePeer> peerListIterator = peerList.listIterator();
				while(peerListIterator.hasNext()){
					ActivePeer currentPeer = (ActivePeer) peerListIterator.next(); 
					if (currentPeer.hostName.equals(currentRfc.host))
					{
						response += "RFC " + currentRfc.rfcNum + " " + currentRfc.title + " " + currentRfc.host + " " + currentPeer.listeningPort + '\n';
						//break;
					}
				
				}
				
			}
		}
		
		if(response.split("\n").length==1)
		{
			response = version + " 404 Not Found\n";
		}
		
		response += END_OF_PACKET;

		//System.out.println(response);
		return response;
	}

	private String getIndex(String packet) {
		System.out.println("This List method is being called");		
		String response = "";
		String response_line_1 = "";
		
		ListIterator<Rfc> indexIterator = index.listIterator();
		//if_server_is_active 
		//{
		response_line_1 += version + " 200 OK \n";
	    //}
		while(indexIterator.hasNext()){
			Rfc currentRfc = (Rfc) indexIterator.next(); 
			// Get host and listening port of the Rfc number present in the peer list
				ListIterator<ActivePeer> peerListIterator = peerList.listIterator();
				while(peerListIterator.hasNext()){
					ActivePeer currentPeer = (ActivePeer) peerListIterator.next(); 
					
					
						response += "RFC " + currentRfc.rfcNum + " " + currentRfc.title + " " + currentRfc.host + " " + currentPeer.listeningPort + '\n';
						break;
				}
			}
		return (response_line_1 +"\n" + response + END_OF_PACKET);
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
		System.out.println(peerList.toString());
	}

	// Removes peer from peer list
	// emoves all his RFCs from the index
	public void RemovePeer()
	{
		
	}
	
	/*private class Listening implements Runnable
	{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			
		}
		
	} */
	}
	
	
    
		
}


