package fileHandlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JPanel;
import javax.swing.Timer;

import com.google.common.eventbus.Subscribe;

import multiplayer.MultiPlayerInstance;
import static uk.co.caprica.vlcjplayer.Application.application;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaMeta;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcjplayer.event.TickEvent;

public class FileTimeTracker {

	private static final int TIMER_DELAY = 1000;
	private long startTimestamp; // since January 1, 1970 UTC
	private long allVideosLength;
	private long runningTime;
	private ArrayList<ArrayList<FileAdditionalInfo>> timedPlaylist;
	private ArrayList<FileAdditionalInfo> runningItems;
	private Timer timer;
	
	public FileTimeTracker() {
		this.startTimestamp = 0;
		this.allVideosLength = 0;
		this.runningTime = 0;
		resetRunningList();
		
		// the timer variable must be a javax.swing.Timer
		// TIMER_DELAY is a constant int and = 35;
		timer = new javax.swing.Timer(TIMER_DELAY, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onTick();
			}
		});
	}
	
	public void resetRunningList() {
		this.timedPlaylist = new ArrayList<ArrayList<FileAdditionalInfo>>();
		this.runningItems  = new ArrayList<FileAdditionalInfo>(application().getScreenQtt()+1);
		for(int channel = 0; channel <= application().getScreenQtt(); channel++) {
			this.timedPlaylist.add(new ArrayList<FileAdditionalInfo>());
			this.runningItems.add(null);
		}
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
		for(int channel = 0; channel < this.timedPlaylist.size(); channel++) {
			for(FileAdditionalInfo item : this.timedPlaylist.get(channel)){
				if(item != null && this.startTimestamp < item.getTimestamp()) result = true;
				if(result) break;
			}
			if(result) break;
		}
		return result;
	}
	
	public boolean hasPreviousVideo(int channel) {
		boolean result = false;
		for(FileAdditionalInfo item : this.timedPlaylist.get(channel)){
			if(item != null && this.startTimestamp < item.getTimestamp()) result = true;
			if(result) break;
		}
		return result;
	}

	public boolean hasNextVideo() {
		boolean result = false;
		for(int channel = 0; channel < this.timedPlaylist.size(); channel++) {
			for(FileAdditionalInfo item : this.timedPlaylist.get(channel)){
				if(item != null && (item.getTimestamp() + this.getVideoLength(item)) < this.allVideosLength) {
					result = true;
				}
				if(result) break;
			}
			if(result) break;
		}
		return result;
	}

	public boolean hasNextVideo(int channel) {
		boolean result = false;
		for(FileAdditionalInfo item : this.timedPlaylist.get(channel)){
			if(item != null && (item.getTimestamp() + this.getVideoLength(item)) < this.allVideosLength) {
				result = true;
			}
			if(result) break;
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
				
				if(FileBrowser.isValidVideoFile(child)) {
					
					FileAdditionalInfo childFileInfo = new FileAdditionalInfo(child);
					if(childFileInfo.getChannel() >= 0) {
						this.insertOnTimedList(childFileInfo);
						System.out.println("Time tracked list added " + childFileInfo.toString());
					}
					else {
						this.insertOnTimedList(childFileInfo);
						System.out.println("Sequential list added " + childFileInfo.toString());
					}
				}
			}
		}
	}

	private void insertOnTimedList(FileAdditionalInfo childFileInfo) {
		int channel = childFileInfo.getChannel();
		if(channel < 0 || channel >= this.timedPlaylist.size()) channel = 0;
		
		int index = this.timedPlaylist.get(channel).size();
		
		// search for the point to insert this file
		while(index > 0 && this.timedPlaylist.get(channel).get(index-1).getTimestamp() > childFileInfo.getTimestamp()) {
			index--;
		}
		
		if(index >= 0) {
			this.timedPlaylist.get(channel).add(index, childFileInfo);
		}
		else {
			
			this.timedPlaylist.get(channel).add(childFileInfo);
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
		int channel = -1;
		if(info != null) {
			channel = info.getChannel();
			if(!(channel > 0 && channel < this.runningItems.size())) channel = 0;
			this.runningItems.add(channel, info);
		}
	}

	public FileAdditionalInfo getNextVideoItem(int channel) {
		FileAdditionalInfo result = null;
		int index = 0;
		for(FileAdditionalInfo item : this.timedPlaylist.get(channel)) {
			FileAdditionalInfo toCheck = this.runningItems.get(channel);
			if(item.equals(toCheck)) break;
			index++;
		}
		if(index+1 < this.timedPlaylist.size()) {
			result = this.timedPlaylist.get(channel).get(index+1);
		}
		return result;
	}

	public FileAdditionalInfo getPreviousVideoItem(int channel) {
		FileAdditionalInfo result = null;
		int index = 0;
		for(FileAdditionalInfo item : this.timedPlaylist.get(channel)){
			index++;
			if(item.equals(this.runningItems)) break;
		}
		if(index > 0) {
			result = this.timedPlaylist.get(channel).get(index-1);
		}
		return result;
	}

    public void onTick() {

    	// Update timer
    	this.runningTime++;
    	System.out.println("FileTimeTracker: TickEvent@" + this.runningTime);
    	System.out.println("RunningItems:");
    	for(int channel = 0; channel < application().getScreenQtt(); channel++){
    		for(FileAdditionalInfo item : this.runningItems) {
    			System.out.println("   * Item[" + channel + "]:" + item.toString());
    		}
    		
    		// Check if there is next Videos to start
    		if(this.hasNextVideo(channel)) {
    			System.out.println("Channel #" + channel + " has next Video to play");
    			FileAdditionalInfo item = null;
    			System.out.println("Next Items:");
    			while(this.hasNextVideo(channel)) {
    				
    				int index = 0;
    				while(item == null) {
    					item = this.getNextVideoItem(index);
    					index++;
    				}
    				if(item != null) System.out.println("   * item:" + item.getFileName().toString());
    				else break;
    			}
    		}
    		else {
    			System.out.println("There isn't next videos to play on Channel #" + channel);
    		}
    	}
    	
    }
	
    public void start() {
    	this.timer.start();
    }
    
    public void stop() {
    	this.timer.stop();
    }
    
    public boolean isRunning() {
    	return this.timer.isRunning();
    }

	public void resetTimer() {
		this.runningTime = 0;
		this.timer.restart();
		this.timer.stop();
	}

	public void removeFromRunningItems(String mediaPath) {
		FileAdditionalInfo removeItem = new FileAdditionalInfo(mediaPath);
		System.out.println("To remove item:" + removeItem.toString());
		int channel = removeItem.getChannel();
		if(!(channel > 0 && channel < this.runningItems.size())) channel = 0;

		FileAdditionalInfo itemToRemove = this.runningItems.get(channel);
		if(removeItem.equals(itemToRemove)) {
			if(this.runningItems.remove(itemToRemove)) System.out.println("   ---Removed");
			this.runningItems.add(channel, null);
		}
		
		// FIXME just for debug should be removed
		System.out.println("After remove RunningItems:");
    	for(FileAdditionalInfo item : this.runningItems) {
    		System.out.println("   * Item:" + item.toString());
    	}
	}
}
