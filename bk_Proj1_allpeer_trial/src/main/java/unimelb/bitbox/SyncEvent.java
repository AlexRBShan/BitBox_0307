package unimelb.bitbox;

import java.util.logging.Logger;

import unimelb.bitbox.util.Configuration;
import unimelb.bitbox.util.FileSystemManager;

public class SyncEvent extends Thread{
	private Logger log = Logger.getLogger(SyncEvent.class.getName());
	private FileSystemManager fileSystemManager;
	
	public SyncEvent(FileSystemManager fileSystemManager) {
		this.fileSystemManager = fileSystemManager;
	}

	@Override
	public void run() {
		while(true) {
			log.info("Generating Sync");
			PeerMaster.eventQueue.addAll(this.fileSystemManager.generateSyncEvents());
			try {
				Thread.sleep(PeerMaster.syncInterval * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
}
