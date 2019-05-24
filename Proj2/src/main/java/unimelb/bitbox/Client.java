package unimelb.bitbox;

import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.CmdLineException;

import unimelb.bitbox.util.HostPort;

public class Client {

	public static void main(String[] args) {
		
		CmdLineArgs argsBean = new CmdLineArgs();
		CmdLineParser parser = new CmdLineParser(argsBean);
		
		try {
			parser.parseArgument(args);
			HostPort server = new HostPort(argsBean.getServerHost(), argsBean.getServerPort());
			String command = argsBean.getCmd();
			HostPort peer = new HostPort(argsBean.getPeerHost(), argsBean.getPeerPort());
			
			System.out.println("ServerHostPort: " + server.toString());
			System.out.println("Command: " + command);
			System.out.println("PeerHostPort: " + peer.toString());
		} catch (CmdLineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
