package fileHandlers;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import multiplayer.MultiPlayerInstance;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class FileTimeTracker {

	private long startTimestamp; // since January 1, 1970 UTC
	private long allVideosLength;
	private long runningTime;
	private ArrayList<FileAdditionalInfo> timedPlaylist;
	private FileAdditionalInfo runningItem;
	
	public FileTimeTracker() {
		this.startTimestamp = 0;
		this.allVideosLength = 0;
		this.runningTime = 0;
		timedPlaylist = new ArrayList<FileAdditionalInfo>();
	}
	
	public void setAllVideosLength(long length) {
		this.allVideosLength = length;
	}
	
	public long getAllVideosLength() {
		return this.allVideosLength;
	}
	
	public long getRunningTime() {
		return this.runningTime;
	}
	
	public void setRunningTime(long newRunningTime) {
		this.runningTime = newRunningTime;
	}
	
	public boolean hasPreviousVideo() {
		return (this.startTimestamp < this.runningItem.getTimestamp());
	}
	
	public boolean hasNextVideo() {
		return ((this.runningItem.getTimestamp() + this.getVideoLength(this.runningItem)) < this.allVideosLength);
	}
	
	public boolean isInTrack(long timestamp) {
		// TODO
		return false;
	}
	
	public void setFirstStartTimestamp(FileAdditionalInfo firstFile) {
		this.startTimestamp = firstFile.getTimestamp();
	}

	public void addFoldersToTrack(File mediaDirectory) {
		System.out.println("Start to track file times from " + mediaDirectory);
		for(File child : mediaDirectory.listFiles()) {
			System.out.println("Tracking time looking into " + child);
			if(child.isDirectory()) addFoldersToTrack(child);
			else {
				
				FileAdditionalInfo childFileInfo = new FileAdditionalInfo(child.getAbsolutePath());
				if(childFileInfo.getChannel() >= 0) this.insertOnTimedList(childFileInfo);
				System.out.println("Added " + childFileInfo.getFileName() + " to time tracked list");
			}
		}
	}

	private void insertOnTimedList(FileAdditionalInfo childFileInfo) {
		int index = this.timedPlaylist.size();
		System.out.println("Inserting " + childFileInfo.getFileName() + " on index=" + index);
		
		// search for the point to insert this file
		while(index > 0 && this.timedPlaylist.get(index-1).getTimestamp() > childFileInfo.getTimestamp()) {
			index--;
			System.out.println("No! No! index=" + index);
		}
		
		System.out.println("Final index=" + index);
		
		if(index >= 0) {
			this.timedPlaylist.add(index, childFileInfo);
			System.out.println("Inserted @ index");
		}
		else {
			
			this.timedPlaylist.add(childFileInfo);
			System.out.println("Inserted @ tail");
		}
		
		// Whenever its the first element to be inserted update the first timestamp
		if(index == 0) this.startTimestamp = childFileInfo.getTimestamp();
		
		// Also whenever its the last file that was added lets updated the all videos Length 
		else if(index == (this.timedPlaylist.size()-1)) {
			this.allVideosLength = childFileInfo.getTimestamp() + this.getVideoLength(childFileInfo);
		}
	}
	
	private long getVideoLength(FileAdditionalInfo inputFile) {
		long returnLength = 0;
		EmbeddedMediaPlayer player = new MediaPlayerFactory().newEmbeddedMediaPlayer(null);
		MultiPlayerInstance playerInstance = new MultiPlayerInstance(player, "#ToFetchLength");
		player.prepareMedia(inputFile.getFilePath());
		returnLength = player.getLength();
		System.out.println("getVideoLength(" + inputFile.getFileName() + ")=" + returnLength);
		return returnLength;
	}
}
