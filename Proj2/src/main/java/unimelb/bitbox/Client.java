package unimelb.bitbox;

import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.CmdLineException;

public class Client {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CmdLineArgs argsBean = new CmdLineArgs();
		CmdLineParser parser = new CmdLineParser(argsBean);
		
		try {
			parser.parseArgument(args);
			System.out.println("ServerHostPort: " + argsBean.getServerHost() + ":" + argsBean.getServerPort());
			System.out.println("Command: " + argsBean.getCmd());
			System.out.println("PeerHostPort: " + argsBean.getPeerHost() + ":" + argsBean.getPeerPort());
		} catch (CmdLineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
