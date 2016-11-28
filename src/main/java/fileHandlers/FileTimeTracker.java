package fileHandlers;

import java.util.Date;

public class FileTimeTracker {

	private long startTimestamp; // since January 1, 1970 UTC
	private long allVideosLength;
	private long firstStartTimestamp;
	private long runningTime;
	private boolean running;
	
	public FileTimeTracker() {
		this.firstStartTimestamp = 0; 
		this.startTimestamp = 0;
		this.allVideosLength = 0;
		this.runningTime = 0;
		this.running = false;
	}
	
	public void setAllVideosLength(long length) {
		this.allVideosLength = length;
	}
	
	public long getAllVideosLength() {
		return this.allVideosLength;
	}
	
	public long getRunningTime() {
		return runningTime;
	}
	
	public void setTrackTime(long timstamp) {
		this.firstStartTimestamp = timstamp;
	}
	
	public long getTrackTime() {
		return this.runningTime + this.firstStartTimestamp;
	}
	
	public boolean isInTrack(long timestamp) {
		// TODO
		return false;
	}
	
	public void setFirstStartTimestamp() {
		// TODO this class should not be concerned with file name formats
	}
}
