import java.io.*;
import java.io.ObjectInputStream.GetField;
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
	
	private class Listener implements Runnable{

		@SuppressWarnings("resource")
		@Override
		
		public void run() {
		
			try {
				ServerSocket listener = new ServerSocket(1111);
				
				while(true) {
				
					
					Uploader uploader = new Uploader(listener.accept());
					Thread uploaderThread = new Thread(uploader);
					uploaderThread.start(); 
					
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				
				e.printStackTrace();
			}
		}
		
	}
	
	private class Uploader implements Runnable {
		
		InputStreamReader in;
		DataOutputStream out;
		
		public Uploader(Socket socket) throws Exception {
			this.out = new DataOutputStream(socket.getOutputStream());
			this.in = new InputStreamReader(socket.getInputStream());
			String response = buildResponsePacket();
			this.out.writeBytes("THIS IS MY FILE");
		}
		
		private String buildResponsePacket() {
			String packet = version + " 200 OK\n"
					+ "Date: " + "Thu, 21 Jan 2001 9:23:46 GMT " + "\n"
					+ "OS: Windows NT 7.6 \n"
					+ "Last Modified: Thu, 21 Jan 2001 9:23:46 GMT"
					+ "Content-Length: 12345" //TODO Insert RFC length here
					+ "Content-Type: text/text"
					+ "Data Data Data"
					+ END_OF_PACKET;

		return packet;
		}

		@Override
		public void run() {
		}
		
	}
	
private class Downloader implements Runnable {
		
		
		InputStreamReader in;
		DataOutputStream out;
		
		public Downloader(Socket socket) throws Exception {
			this.in = new InputStreamReader(socket.getInputStream());
			this.out = new DataOutputStream(socket.getOutputStream());
			String request = buildRequestPacket();
			this.out.writeBytes("THIS IS MY FILE");
		}
		
		private String buildRequestPacket() {
			String packet = "GET RFC 814 " + version
			+ "Host: somehost.ncsu.edu"
			+ "OS: Windows NT 5.8";
			
		return packet;
		}

		@Override
		public void run() {
		}
		
	}
	
	
	public Peer(){
	
		port = 1111;
		hostname = "8.8.8.8";
		version = "P2P-CI/1.0";	
		
		Listener l = new Listener();
		Thread listenerThread = new Thread(l);
		listenerThread.start();
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
		outToServer.flush();
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
		    		//System.out.println(packet);
		    		outToServer.writeBytes(packet);
		    		//outToServer.writeBytes(END_OF_PACKET);
		    		
		    		
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
	


	public String getRfc(String hostname,String rfcnum)

	{
		System.out.println("This method is being called \n");
	
		String packet = "GET RFC " + rfcnum + " " + this.version + "\n"
				+ "Host: " + hostname + "\n"
				//+ "Port: " + this.port  + "\n"
				//+ "Title: " + rfcTitle + "\n"
				+ "OS : WINDOWS 8 \n"
				+ END_OF_PACKET;
		System.out.println(packet);
		return packet;
		
		// Contact the peer and download the file. 
	}
	
	public void closeConnectionToServer() throws Exception
	{
		//String message = "CLOSE" + END_OF_PACKET + "\n";
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		//System.out.println(message);
		//outToServer.writeBytes(message);
		String packet = "CLOSE " + "\n" + hostname + "\n"
				+ port + "\n"
				+ version + "\n"
				+ END_OF_PACKET;
		
		outToServer.writeBytes(packet);
		clientSocket.close();
	}
	
	@SuppressWarnings("finally")
	public static void main(String[] args) throws Exception {
		System.out.println("PEER:-");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		Peer p1=new Peer();
		try
		{
		clientSocket = new Socket("127.0.0.1",SERVER_LISTENING_PORT);
		
		do
		{
		System.out.println("Select an option between 1 and 5 for the below actions \n 1. Inform server of active status \n 2. Inform the server about all the stored RFCs(ADD) \n 3. Request peers having particular RFC (LOOKUP) \n 4. List the whole index of RFCs from the server (LIST) \n 5. Get RFC from a particular peer \n 6. Close connection to the server. \n ");
			
		//String s = br.readLine();
		
		int option = Integer.parseInt(br.readLine());
		
		switch(option)
		{
		case 1:
			p1.contactServer(); // Tells the server I am alive.
			System.out.println("Server has been notified ... Active Peer's List has been updated \n");
			break;
			
		case 2:
			p1.addAllRfcs(); //Adds all the RFCS in rfcs folder to the CS's 'index'
			System.out.println("RFCs present with this peer have been added to server RFC list \n");
			break;
			
		case 3:	
			p1.readRfcReqList(); // Reads what all Rfcs have to be requested
			break;
			
		case 4:
			p1.requestRfcList();   // Sends a list request to the server
			break;
		
		case 5:
			System.out.println("Enter the peer hostname \n");
			String hostname = br.readLine();
			System.out.println("Enter the rfc num: \n"); 
			String portnum = br.readLine();
			String request = p1.getRfc(hostname, portnum);
			/*Socket dwnldSocket;
			
			//Downloader d = new Downloader(.accept());
			Thread downloaderThread = new Thread(d);
			downloaderThread.start(); */
			
			break;
		
		case 6:
			System.out.println("Connection closed");
			p1.closeConnectionToServer();
			//System.exit();
			return;
		
		default:
			System.out.println("Please enter a valid option: \n");
			break;
		}
		}while(true);
		
		} catch (Exception e)
		{
			//e.printStackTrace();
			System.out.println("Server has not yet started running ... Start server and then run the peer \n");
		} finally
		{
			return;
		}
	
	}

}
