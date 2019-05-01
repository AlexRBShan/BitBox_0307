package unimelb.bitbox;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import javax.net.ServerSocketFactory;

public class TestServer {
	private static int i = 0;

	public static void main(String[] args) {
		
		ServerSocketFactory factory = ServerSocketFactory.getDefault();
		
		try(ServerSocket server = factory.createServerSocket(8112)) {

			//Listen for incoming connections for ever 
			while (true) {
				System.out.println("Server listening on port 4444 for a connection");
				//Accept an incoming client connection request 
				Socket clientSocket = server.accept(); //This method will block until a connection request is received
				i++;
				System.out.println("Client conection number " + i + " accepted:");
				System.out.println("Remote Port: " + clientSocket.getPort());
				System.out.println("Remote Hostname: " + clientSocket.getInetAddress().getHostName());
				System.out.println("Local Port: " + clientSocket.getLocalPort());
				
				Thread t = new Thread(() -> RlyMsg(clientSocket, i));
				t.start();
			}
		} catch (SocketException ex) {
			ex.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		} 
	
	}
	
	private static void RlyMsg(Socket client, int i) {
		try {
			DataInputStream in = new DataInputStream(client.getInputStream());
			while(in.available() > 0) {
				System.out.println("geting data from client " + i);
				System.out.println("Client("+i+") says: " + in.readUTF());
			}
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
