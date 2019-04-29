package unimelb.bitbox;

import unimelb.bitbox.util.*;
import java.io.*;
import java.net.*;

public class Client {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Read configurations
		Configuration.getConfiguration();
		int portnum = Integer.parseInt(Configuration.getConfigurationValue("port"));
		String[] peers = Configuration.getConfigurationValue("peers").split(",");
		String ip = "localhost";
		
		try(Socket client = new Socket(ip,portnum)){
			// create input and output streams for 
			BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF8"));
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(client.getOutputStream(), "UTF8"));
			Document doc1 = new Document();
			doc1.append("command", "REQUEST_HANDSHAKE");
			doc1.append("message","hahahahah");
			System.out.println("Command: " + doc1.toJson());
			writer.println(doc1.toJson());
			writer.flush();
			while(true) {
				Document msg = Document.parse(reader.readLine());
				System.out.println("MSG FROM Server: "+msg.toJson());
			}
			
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

}
