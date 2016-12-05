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
	private ArrayList<FileAdditionalInfo> timedPlaylist;
	private ArrayList<ArrayList<FileAdditionalInfo>> runningItems;
	private Timer timer;
	
	public FileTimeTracker() {
		this.startTimestamp = 0;
		this.allVideosLength = 0;
		this.runningTime = 0;
		this.timedPlaylist = new ArrayList<FileAdditionalInfo>();
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
		this.runningItems = new ArrayList<ArrayList<FileAdditionalInfo>>(application().getScreenQtt()+1);
		for(int channel = 0; channel < application().getScreenQtt(); channel++) {
			this.runningItems.add(new ArrayList<FileAdditionalInfo>());
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
		for(int channel = 0; channel < application().getScreenQtt(); channel++) {
			for(FileAdditionalInfo item : this.runningItems.get(channel)){
				if(item != null && this.startTimestamp < item.getTimestamp()) result = true;
			}
		}
		return result;
	}
	
	public boolean hasNextVideo() {
		boolean result = false;
		for(int channel = 0; channel < application().getScreenQtt(); channel++) {
			for(FileAdditionalInfo item : this.runningItems.get(channel)){
				if(item != null && (item.getTimestamp() + this.getVideoLength(item)) < this.allVideosLength) {
					result = true;
				}
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
		int channel = -1;
		if(info != null) {
			channel = info.getChannel();
			if(channel > 0 && channel < application().getScreenQtt()) this.runningItems.get(channel).add(info);
			else this.runningItems.get(0).add(info);
		}
	}

	public FileAdditionalInfo getNextVideoItem() {
		FileAdditionalInfo result = null;
		int index = 0;
		for(FileAdditionalInfo item : this.timedPlaylist) {
			index++;
			if(item.equals(this.runningItems)) break;
		}
		if(index+1 < this.timedPlaylist.size()) {
			result = this.timedPlaylist.get(index+1);
		}
		return result;
	}

	public FileAdditionalInfo getPreviousVideoItem() {
		FileAdditionalInfo result = null;
		int index = 0;
		for(FileAdditionalInfo item : this.timedPlaylist){
			index++;
			if(item.equals(this.runningItems)) break;
		}
		if(index > 0) {
			result = this.timedPlaylist.get(index-1);
		}
		return result;
	}

    public void onTick() {

    	// Update timer
    	this.runningTime++;
    	System.out.println("FileTimeTracker: TickEvent@" + this.runningTime);
    	System.out.println("RunningItems:");
    	for(int channel = 0; channel < application().getScreenQtt(); channel++){
    		for(FileAdditionalInfo item : this.runningItems.get(channel)) {
    			System.out.println("   * Item[" + channel + "]:" + item.toString());
    		}
    		
    		// Check if there is next Videos to start
    		if(this.hasNextVideo()) {
    			System.out.println("Channel #" + channel + " has next Video to play");
    			FileAdditionalInfo item;
    			System.out.println("Next Items:");
    			while(this.hasNextVideo()) {
    				item = this.getNextVideoItem();
    				if(item != null) System.out.println("   * item:" + item.getFileName() + "@" + item.getTimestamp());
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
		for(FileAdditionalInfo item : this.runningItems.get(removeItem.getChannel())) {
			if(removeItem.equals(item)) {
				if(this.runningItems.remove(item)) System.out.println("   ---Removed");
			}
		}
		System.out.println("After remove RunningItems:");
    	for(FileAdditionalInfo item : this.runningItems.get(removeItem.getChannel())) {
    		System.out.println("   * Item:" + item.toString());
    	}
	}
}
