import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;


public class Peer {
	
	String hostname;
	String version;
	int port;
	
	
	static final int SERVER_LISTENING_PORT = 7134;
	static final String END_OF_PACKET = "END_OF_PACKET\n";
	public static Socket clientSocket;
	public Peer(){
	
		port = 1111;
		hostname = "8.8.8.8";
		version = "P2P-CI/1.0";		
	}

	public void publishInfo(int RFCNum)
	{
	}
	
	/*private void startListening() throws Exception 
	{
		@SuppressWarnings("resource")
		ServerSocket welcomeSocket = new ServerSocket(this.port);

		// Communicates to one socket in one iteration.
		// Seems to be atomic for that session of communication. 
		while (true) 
		{
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
			
		}
	}
	*/
	
	private void requestRfcList() throws Exception
	{
		String request = "LIST ALL P2P-CI/1.0 \n" 
				+ "Host: " + this.hostname + "\n"
				+ "Port: " + this.port  + "\n"
				+ END_OF_PACKET;
		
		//clientSocket = new Socket("127.0.0.1",SERVER_LISTENING_PORT);
		
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader inFromServer =  new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		
		outToServer.writeBytes(request);
		System.out.println("TO SERVER:");
		System.out.println(request);
		
		String responseLine;
		
		responseLine = inFromServer.readLine();
		String response = responseLine;
		while(!(responseLine = inFromServer.readLine()).equals(END_OF_PACKET.trim())){
			response += responseLine + "\n";
		}
		
		System.out.println("FROM SERVER:\n" + response+ "\n");
		
		//clientSocket.close();
	}
		
	
	private void lookupRfc(String line) throws Exception
	{
		// Step 1 :- assembling the lookup packet from various data elements.
		
		String rfcNumber = line.substring(0,3);
		String rfcTitle = line.substring(4);
		String request ="";
		String response = "No response from server yet";
		
		request = "LOOKUP RFC " + rfcNumber + " " + this.version + "\n"
					+ "Host: " + this.hostname + "\n"
					+ "Port: " + this.port  + "\n"
					+ "Title: " + rfcTitle + "\n"
					+ END_OF_PACKET;
		
		
		//Step 2 :- Sending the packet to the Server.

		//clientSocket = new Socket("127.0.0.1",SERVER_LISTENING_PORT);
		
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader inFromServer =  new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		
		outToServer.writeBytes(request);
		System.out.println("TO SERVER:");
		System.out.println(request);
		
		String responseLine;
		
		responseLine = inFromServer.readLine();
		response = responseLine+"\n";
		while(!(responseLine = inFromServer.readLine()).equals(END_OF_PACKET.trim())){
			response += responseLine + "\n";
		}
		
		System.out.println("FROM SERVER:\n" + response+ "\n");
		
		//clientSocket.close();
	}
	
	public void readRfcReqList() throws Exception
	{
		
		String filename = "needed_rfcs.txt";
		
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		String line = null;
	
		while((line = reader.readLine())!=null)
		{
			lookupRfc(line);
		}
		
		reader.close();
	}
	
	
	
	// Whenever a peer comes alive, it contacts server.
	// This method only tells the server that the peer is alice. 
	// That is, this method ONLY opens the connection, mentions it's hostname, and port. 
	
	public void contactServer() throws Exception{
		//Open a talking port
		clientSocket = new Socket("127.0.0.1",SERVER_LISTENING_PORT);
		
		//Send 3 main attributes
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		
		outToServer.writeBytes("Hi! I am up and running: \n");
		outToServer.writeBytes(hostname+"\n");
		outToServer.writeBytes(port+"\n");
		outToServer.writeBytes(version+"\n");
		outToServer.writeBytes(END_OF_PACKET);
		
		//clientSocket.close();
	}
	
	/* This method looks for an 'rfcs' directory,
	 * And then calls addRfc() on every rfc (file) in that directory
	 */
	
	public void addAllRfcs() throws Exception
	{
		//String filePath = new File("").getAbsolutePath();
		//System.out.println(filePath);
		File folder = new File(".//rfcs");
		File[] listOfFiles = folder.listFiles();
		
		String filename = "";
		for (int i = 0; i < listOfFiles.length; i++) 
		    {
		    	if (listOfFiles[i].isFile()) {
		    		filename = (".\\\\rfcs\\\\" + listOfFiles[i].getName());
		    		String packet = buildAddPacket(filename);
		    		//System.out.println(packet);
		    		// Sending the packet to the server.		    		 
		    		//Open a talking port
		    		//Socket clientSocket = new Socket("127.0.0.1",SERVER_LISTENING_PORT);
		    		
		    		//Send the 4 line packet
		    		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		    		outToServer.writeBytes(packet);
		    		
		    		
		    		//Receiving server response
		    		/*BufferedReader inFromServer = 
		    		          new BufferedReader(new
		    		          InputStreamReader(clientSocket.getInputStream()));
		    		String response = inFromServer.readLine(); 
		    		
		    		System.out.println("FROM SERVER: " + response);*/
		    		
		    		//clientSocket.close();
		    	} 
		    }
	}
	
	
	/* Informs the server about Which RFCs the peer has locally. 
	 * It called this method once for every RFC it has
	 * Corresponds to ADD in the specs */
	 
	public String buildAddPacket(String filename) throws Exception {
		
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		String line = null;
		String rfcNumber = "";
		String rfcTitle = "";
		String packet ="";
		
		//Reading first non-empty line
		while ((line = reader.readLine()) != null) {			
			String trimmed_line = line.trim();
			if (trimmed_line.length() > 0) {
				rfcNumber = trimmed_line.substring(trimmed_line.length()-3);
				break;
			} else {
				continue;
			}
		}
		
		//Reading second non-empty line
		while ((line = reader.readLine()) != null) {			
			if (line.trim().length() > 0) {
				rfcTitle = line.trim();
				break;
			} else {
				continue;
			}
		}
		
		reader.close();
		packet = "ADD RFC " + rfcNumber + " " + this.version + "\n"
					+ "Host: " + this.hostname + "\n"
					+ "Port: " + this.port  + "\n"
					+ "Title: " + rfcTitle + "\n"
					+ END_OF_PACKET;

		return packet;
	}
	

	public void closeConnectionToServer() throws Exception
	{
		clientSocket.close();
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println("PEER:-");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		Peer p1=new Peer();
		
		do
		{
		System.out.println("Menu \n 1. Contact the server by opening a socket. \n 2. Inform the server about all the stored RFCs(ADD) \n 3. Request rfcs from server \n 4. Download RFCs from the server(LOOKUP) \n 5. Close connection to the server. \n ");
			
		String s = br.readLine();
		
		int option = Integer.parseInt(s);
		
		switch(option)
		{
		case 1:
			p1.contactServer(); // Tells the server I am alive.
			break;
			
		case 2:
			p1.addAllRfcs(); //Adds all the RFCS in rfcs folder to the CS's 'index'
			break;
			
		case 3:	
			p1.readRfcReqList(); // Reads what all Rfcs have to be requested
			p1.requestRfcList();   // Sends a list request to the server
			break;
			
		case 4:
			// Method to download RFCs
			
		case 5:
			System.out.println("Connection closed");
			p1.closeConnectionToServer();
			break;
			
		default:
			System.out.println("Please enter a valid option: \n");
			break;
		}
		}while(true);
	}

}
