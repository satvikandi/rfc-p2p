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
	
	public Peer(){
	
		Date date = new Date();
		port = 1111;
		hostname = "8.8.8.8";
		version = "P2P-CI/1.0";
	}

	public void publishInfo(int RFCNum)
	{
		//this is nto a prot.Actual empty method
	}
	
	
	private void lookupRfc(String packet) 
	{
		// Step 1 :- buolding the Lookup RFC packet
		//Step 2 :- Sending the packet across to the Server.
		
	}
	
	public void requestRFC(){
		
	}
	
	
	
	public void startListening(){
		
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
		outToServer.writeBytes(version+"\n\n");
		
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
		    		System.out.println(packet);
		    		
		    		
		    		/* Sending the packet to the server.		    		 */
		    	
		    		//Open a talking port
		    		Socket clientSocket = new Socket("127.0.0.1",SERVER_LISTENING_PORT);
		    		
		    		//Send the 4 line packet
		    		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		    		outToServer.writeBytes(packet);
		    		
		    		clientSocket.close();
		    	} 
		    }
		
		
		//String filename = ".\\rfcs\\rfc813.txt";
		//addRfc(filename);
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
					+ "Title: " + rfcTitle + "\n";

		//System.out.println(packet);
		
		return packet;
		
		
	}
	

	public static void main(String[] args) throws Exception {
		System.out.println("This program represents one of the peers of the system");
		
		Peer p1=new Peer();
		p1.contactServer(); // Tells the server I am alive.
		p1.addAllRfcs(); //Adds all the RFCS in rfcs folder to the CS's 'index'
	}

}
