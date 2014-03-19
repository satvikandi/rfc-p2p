import java.io.*;
import java.net.Socket;

public class Peer {
	
	String hostName;
	String RFCTitle;
	int RFCNum;
	int portNum;
	
	static final int SERVER_LISTENING_PORT = 7134;
	
	public Peer(){
		hostName = "saad";
		RFCTitle = "This is my RFC Title";
	}

	public void publishInfo(int RFCNum)
	{
		//this is nto a prot.Actual empty method
	}
	
	public void requestRFC(){
		
	}
	
	
	public void startListening(){
		
	}
	
	public void contactServer() throws Exception{
		//Open a talking port
		Socket clientSocket = new Socket("127.0.0.1",SERVER_LISTENING_PORT);
		
		//Send 3 main attributes
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		outToServer.writeBytes(hostName+"\n");
		outToServer.writeBytes(RFCTitle+"\n");
		
		clientSocket.close();
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println("This program represents one of the peers of the system");
		
		Peer p1=new Peer();
		p1.contactServer();
	}

}
