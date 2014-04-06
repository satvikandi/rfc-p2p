import java.io.*;
//Saad Smart Ass
import java.net.Socket;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Peer {
	
	String hostname;
	String version;
	int port;
	
	static final int SERVER_LISTENING_PORT = 7134;
	static final String END_OF_PACKET = "END_OF_PACKET\n";
	
	public Peer(){
	
		port = 1111;
		hostname = "8.8.8.8";
		version = "P2P-CI/1.0";
	}

	public void publishInfo(int RFCNum)
	{
	}
	
	private void listRfc() throws Exception
	{
		String request = "LIST ALL P2P-CI/1.0 \n" 
				+ "Host: " + this.hostname + "\n"
				+ "Port: " + this.port  + "\n"
				+ END_OF_PACKET;
		
		Socket clientSocket = new Socket("127.0.0.1",SERVER_LISTENING_PORT);
		
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		//BufferedReader inFromServer =  new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		
		outToServer.writeBytes(request);
		System.out.println("TO SERVER:");
		System.out.println(request);
		
		clientSocket.close();
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

		Socket clientSocket = new Socket("127.0.0.1",SERVER_LISTENING_PORT);
		
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader inFromServer =  new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		
		outToServer.writeBytes(request);
		System.out.println("TO SERVER:");
		System.out.println(request);
		
		String responseLine;
		
		responseLine = inFromServer.readLine();
		response = responseLine;
		while(!(responseLine = inFromServer.readLine()).equals(END_OF_PACKET.trim())){
			response += responseLine + "\n";
		}
		
		System.out.println("FROM SERVER:\n" + response+ "\n");
		
		clientSocket.close();
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
		Socket clientSocket = new Socket("127.0.0.1",SERVER_LISTENING_PORT);
		
		//Send 3 main attributes
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		
		outToServer.writeBytes("Hi! I am up and running: \n");
		outToServer.writeBytes(hostname+"\n");
		outToServer.writeBytes(port+"\n");
		outToServer.writeBytes(version+"\n");
		outToServer.writeBytes(END_OF_PACKET);
		
		clientSocket.close();
	}
	
	/* This method looks for an 'rfcs' directory,
	 * And then calls addRfc() on every rfc (file) in that directory
	 */
	
	public void addAllRfcs() throws Exception{
		//String filePath = new File("").getAbsolutePath();
		//System.out.println(filePath);
		
		File folder = new File(".//rfcs");
		File[] listOfFiles = folder.listFiles();
		String filename = "";

		    for (int i = 0; i < listOfFiles.length; i++) {
		    	if (listOfFiles[i].isFile()) {
		    		filename = (".\\\\rfcs\\\\" + listOfFiles[i].getName());
		    		String packet = buildAddPacket(filename);
		    		//System.out.println(packet);
		    		
		    		
		    		/* Sending the packet to the server.		    		 */
		    	
		    		//Open a talking port
		    		Socket clientSocket = new Socket("127.0.0.1",SERVER_LISTENING_PORT);
		    		
		    		//Send the 4 line packet
		    		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		    		outToServer.writeBytes(packet);
		    		
		    		
		    		//Receiving server response
		    		/*BufferedReader inFromServer = 
		    		          new BufferedReader(new
		    		          InputStreamReader(clientSocket.getInputStream()));
		    		String response = inFromServer.readLine(); 
		    		
		    		System.out.println("FROM SERVER: " + response);*/
		    		
		    		clientSocket.close();
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
	

	public static void main(String[] args) throws Exception {
		System.out.println("PEER:-");
		
		Peer p1=new Peer();
		p1.contactServer(); // Tells the server I am alive.
		p1.addAllRfcs(); //Adds all the RFCS in rfcs folder to the CS's 'index'
		p1.readRfcReqList(); // Reads what all Rfcs have to be requested
		p1.listRfc();   // Sends a list request to the server
		
	}

}
