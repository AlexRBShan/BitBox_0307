package testing;

import java.io.*;
import java.net.*;
import java.util.Queue;
import java.util.ArrayList;
import java.net.UnknownHostException;
import java.util.Scanner;

//Interactive client that reads input from the command line and sends it to 
//a server 
public class TestClient {

	public static void main(String[] args) {
		
		ArrayList<Socket> socketlist = new ArrayList<Socket>();

		Socket socket1 = null;
		Socket socket2 = null;
		try {
			// Create a stream socket bounded to any port and connect it to the
			// socket bound to localhost on port 4444
			socket1 = new Socket("localhost", 4444);
			System.out.println("Connection1 established");
			socket2 = new Socket("localhost", 4444);
			System.out.println("Connection2 established");
			
			socketlist.add(socket1);
			socketlist.add(socket2);
			
			for(int i = 0; i < 2; i++) {
				Socket socket = socketlist.get(i);
				// Get the input/output streams for reading/writing data from/to the socket
				DataInputStream in = new DataInputStream(socket.getInputStream());
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				
				String msg = "Hello - " + i;
				out.writeUTF(msg);
				out.flush();
				System.out.println("Message sent: " + msg);
			}
			socket1.close();
			socket2.close();
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	

	}

}
