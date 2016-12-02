package fileHandlers;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JPanel;

import multiplayer.MultiPlayerInstance;
import static uk.co.caprica.vlcjplayer.Application.application;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaMeta;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class FileTimeTracker {

	private long startTimestamp; // since January 1, 1970 UTC
	private long allVideosLength;
	private long runningTime;
	private ArrayList<FileAdditionalInfo> timedPlaylist;
	private ArrayList<FileAdditionalInfo> runningItem;
	
	public FileTimeTracker() {
		this.startTimestamp = 0;
		this.allVideosLength = 0;
		this.runningTime = 0;
		timedPlaylist = new ArrayList<FileAdditionalInfo>();
		runningItem   = new ArrayList<FileAdditionalInfo>();
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
		boolean result = false;
		for(FileAdditionalInfo item : this.runningItem){
			if(item != null && this.startTimestamp < item.getTimestamp()) result = true;
		}
		return result;
	}
	
	public boolean hasNextVideo() {
		boolean result = false;
		for(FileAdditionalInfo item : this.runningItem){
			if(item != null && (item.getTimestamp() + this.getVideoLength(item)) < this.allVideosLength) {
				result = true;
			}
		}
		return result;
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
		
		// search for the point to insert this file
		while(index > 0 && this.timedPlaylist.get(index-1).getTimestamp() > childFileInfo.getTimestamp()) {
			index--;
		}
		
		if(index >= 0) {
			this.timedPlaylist.add(index, childFileInfo);
		}
		else {
			
			this.timedPlaylist.add(childFileInfo);
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
		MediaMeta mediaMeta = application().getMediaPlayerComponent().getMediaPlayerFactory().getMediaMeta(inputFile.getFilePath(), true);
		mediaMeta.parse();
		returnLength = mediaMeta.getLength();
		mediaMeta.release();
		System.out.println("getVideoLength(" + inputFile.getFilePath() + ")=" + returnLength);
		return returnLength;
	}

	public void addRunningItem(FileAdditionalInfo info) {
		this.runningItem.add(info);
	}

	public FileAdditionalInfo getNextVideoItem() {
		FileAdditionalInfo result = null;
		int index = 0;
		for(FileAdditionalInfo item : this.timedPlaylist) {
			index++;
			if(item.equals(this.runningItem)) break;
		}
		if(index+1 < this.timedPlaylist.size()) result = this.timedPlaylist.get(index+1);
		return result;
	}

	public FileAdditionalInfo getPreviousVideoItem() {
		FileAdditionalInfo result = null;
		int index = 0;
		for(FileAdditionalInfo item : this.timedPlaylist){
			index++;
			if(item.equals(this.runningItem)) break;
		}
		if(index > 0) result = this.timedPlaylist.get(index-1);
		return result;
	}
}
