package unimelb.bitbox;

import org.kohsuke.args4j.CmdLineParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

import org.kohsuke.args4j.CmdLineException;

import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.HostPort;

public class Client {
	private static Logger log = Logger.getLogger(Client.class.getName());

	public static void main(String[] args) {
		
		CmdLineArgs argsBean = new CmdLineArgs();
		CmdLineParser parser = new CmdLineParser(argsBean);
		
		try {
			parser.parseArgument(args);
			String command = argsBean.getCmd();
			HostPort server = new HostPort(argsBean.getServer());
			String identity = argsBean.getIdentity();
	
			System.out.println("ServerHostPort: " + server.toString());
			System.out.println("Command: " + command);
			System.out.println("identity: " + identity);
			
			Socket client = new Socket(server.host, server.port);
			BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF8"));
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(client.getOutputStream(), "UTF8"),true);	
			
			//sending authority request
			Document authRequest = Protocol.AUTH_REQUEST(identity);
			writer.println(authRequest.toJson());
			
			//receiving authority response
			Document authResponse = Document.parse(reader.readLine());
			log.info("Received:" + authResponse.toJson());
			boolean status = authResponse.getBoolean("status");
			String encryption = "";
			if (status) {
				encryption=authResponse.getString("AES128");
			}
			
			Document cmdDoc = new Document();
			//sending secure command
			switch(command) {
			case "list_peers":
				cmdDoc = Protocol.LIST_PEERS_REQUEST();
				break;
			case "connect_peer":
				HostPort peerCon = new HostPort(argsBean.getPeer());
				cmdDoc = Protocol.CONNECT_PEER_REQUEST(peerCon);
				break;
			case "disconnect_peer":
				HostPort peerDiscon = new HostPort(argsBean.getPeer());
				cmdDoc = Protocol.DISCONNECT_PEER_REQUEST(peerDiscon);
				break;
			default:
				System.out.println("unrecognized command, quit!");
				break;
			}
			byte[] tempKey = Secure.RSADecrypt(encryption, Secure.loadPrivateKey());
			writer.println(Secure.AESEncrypt(tempKey, cmdDoc.toJson()));
			
			// get the response of command from server
			Document cmdResponse = Document.parse(Secure.AESDecrypt(tempKey, reader.readLine()));
			System.out.println(cmdResponse.toJson());
			
			// close the socket
			client.close();
			
		} catch (CmdLineException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
