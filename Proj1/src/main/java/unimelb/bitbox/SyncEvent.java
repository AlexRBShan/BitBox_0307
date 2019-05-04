package unimelb.bitbox;

import java.util.logging.Logger;

import unimelb.bitbox.util.Configuration;
import unimelb.bitbox.util.FileSystemManager;

public class SyncEvent extends Thread{
	private Logger log = Logger.getLogger(SyncEvent.class.getName());
	private FileSystemManager fileSystemManager;
	private long interval;
	
	public SyncEvent(FileSystemManager fileSystemManager) {
		this.fileSystemManager = fileSystemManager;
		this.interval = Long.parseLong(Configuration.getConfigurationValue("syncInterval"));
	}

	@Override
	public void run() {
		while(true) {
			log.info("Generating Sync");
			PeerMaster.eventQueue.addAll(this.fileSystemManager.generateSyncEvents());
			PeerMaster.eventQueue2.addAll(this.fileSystemManager.generateSyncEvents());
			try {
				Thread.sleep(this.interval * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
}
