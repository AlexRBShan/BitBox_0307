package unimelb.bitbox;

import org.kohsuke.args4j.Option;

public class CmdLineArgs {
	@Option(required = true, name = "-s", aliases = {"--server"}, usage = "ServerHostPort")
	private String hostPort;
	
	@Option(required = true, name = "-c", aliases = {"--command"}, usage = "Command")
	private String command;
	
	@Option(required = false, name = "-p", aliases = {"--peer"}, usage = "PeerHostPort")
	private String peer;
	
	
	public String getServerHost() {
		return hostPort.split(":")[0];
	}
	
	public int getServerPort() {
		return Integer.parseInt(hostPort.split(":")[1]);
	}
	
	public String getCmd() {
		return command;
	}
	
	public String getPeerHost() {
		return peer.split(":")[0];
	}
	
	public int getPeerPort() {
		return Integer.parseInt(peer.split(":")[1]);
	}

}
