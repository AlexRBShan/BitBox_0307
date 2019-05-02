package unimelb.bitbox;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import javax.net.ServerSocketFactory;
import java.nio.charset.*;
import unimelb.bitbox.util.*;
import unimelb.bitbox.Protocol;

public class TestServer {
	private static int i = 0;

	public static void main(String[] args) {
		
		ServerSocketFactory factory = ServerSocketFactory.getDefault();
		
		try(ServerSocket server = factory.createServerSocket(8112)) {

			//Listen for incoming connections for ever 
			while (true) {
				System.out.println("Server listening on port 8112 for a connection");
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
	
	private static void RlyMsg(Socket socket, int i) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
			
			
			System.out.println("geting data from client " + i);
			System.out.println("Client("+i+") says: " + reader.readLine());
			Document doc = new Document();
			HostPort host = new HostPort("localhost", 8112);
			
			doc.append("hostPort", host.toDoc());
			doc.append("command", "HANDSHAKE_RESPONSE");
			
			writer.println(doc.toJson());
			System.out.println("Response is sent: " + doc.toJson());
			while(true){
				String rec = reader.readLine();
				if (rec != null) {
					System.out.println("Client("+i+") says: " + reader.readLine());
				}
				
			}
						
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
