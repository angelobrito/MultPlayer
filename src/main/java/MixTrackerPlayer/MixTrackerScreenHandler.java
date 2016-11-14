package MixTrackerPlayer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;

import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;


public class MixTrackerScreenHandler extends EmbeddedMediaPlayerComponent implements MediaPlayerEventListener, MouseListener, MouseMotionListener, MouseWheelListener, KeyListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3731874145026049612L;
	//Our File chooser to help find a file to play
	private File mediaDir;
	private String[] mediaFiles;
	private String selectedFile;
	private Screen screens[];
	private int screensQtt;
	private int selectedScreen;
	private boolean forcedMute;

	public MixTrackerScreenHandler(int screensQtt) {

		this.screensQtt = screensQtt;
		this.selectedScreen = 0;
		this.forcedMute = false;

		this.mediaFiles = new String[this.screensQtt];
		this.setLayout(new GridLayout((this.screensQtt/2), (this.screensQtt/2), 0, 0)); 
		this.setMinimumSize(new Dimension(600, 400));
		this.setBackground(Color.BLACK);

		this.screens = new Screen[this.screensQtt];
		for(int i = 0; i < this.screensQtt; i++) {
			this.screens[i] = new Screen("Camera #" + (i + 1), this.mediaFiles[i]);
			this.add(this.screens[i]);
		}
	}

	private void updateScreensMedia() {
		for(int i = 0; i < this.screensQtt; i++)  {
			this.screens[i].setNewMedia(this.mediaFiles[i]);
		}
	}

	public void setMediaDirectory(String newDirectoryPath) {
		this.mediaDir = new File(newDirectoryPath);
	}

	public File getMediaDirectory() {
		return this.mediaDir;
	}

	public boolean isLibVLCPresent() {

		boolean found = new NativeDiscovery().discover();

		return found;
	}

	public String[] getVideosNames() {
		return new String[4];
	}

	public void run(){
		this.start();
	}

	public void activateScreen(int screenNumber) {
		this.screens[screenNumber].start();
	}


	@Override
	public void mediaChanged(MediaPlayer mediaPlayer, libvlc_media_t media, String mrl) {
		for(int i = 0; i < this.screensQtt; i++) 
			this.screens[i].mediaChanged(mediaPlayer, media, mrl);
	}

	@Override
	public void opening(MediaPlayer mediaPlayer) {
		for(int i = 0; i < this.screensQtt; i++) 
			this.screens[i].opening(mediaPlayer);
	}

	@Override
	public void buffering(MediaPlayer mediaPlayer, float newCache) {
		for(int i = 0; i < this.screensQtt; i++) 
			this.screens[i].buffering(mediaPlayer, newCache);
	}

	@Override
	public void playing(MediaPlayer mediaPlayer) {
		for(int i = 0; i < this.screensQtt; i++) 
			this.screens[i].playing(mediaPlayer);
	}

	@Override
	public void paused(MediaPlayer mediaPlayer) {
		for(int i = 0; i < this.screensQtt; i++) 
			this.screens[i].paused(mediaPlayer);
	}

	@Override
	public void stopped(MediaPlayer mediaPlayer) {
		for(int i = 0; i < this.screensQtt; i++) 
			this.screens[i].stopped(mediaPlayer);
	}

	@Override
	public void forward(MediaPlayer mediaPlayer) {
		for(int i = 0; i < this.screensQtt; i++) 
			this.screens[i].forward(mediaPlayer);
	}

	@Override
	public void backward(MediaPlayer mediaPlayer) {
		for(int i = 0; i < this.screensQtt; i++) 
			this.screens[i].backward(mediaPlayer);
	}

	@Override
	public void finished(MediaPlayer mediaPlayer) {
		for(int i = 0; i < this.screensQtt; i++) 
			this.screens[i].finished(mediaPlayer);
	}

	@Override
	public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
		for(int i = 0; i < this.screensQtt; i++) 
			this.screens[i].timeChanged(mediaPlayer, newTime);
	}

	@Override
	public void positionChanged(MediaPlayer mediaPlayer, float newPosition) {
		for(int i = 0; i < this.screensQtt; i++) 
			this.screens[i].positionChanged(mediaPlayer, newPosition);
	}

	@Override
	public void seekableChanged(MediaPlayer mediaPlayer, int newSeekable) {
		for(int i = 0; i < this.screensQtt; i++) 
			this.screens[i].seekableChanged(mediaPlayer, newSeekable);
	}

	@Override
	public void pausableChanged(MediaPlayer mediaPlayer, int newPausable) {
		for(int i = 0; i < this.screensQtt; i++) 
			this.screens[i].pausableChanged(mediaPlayer, newPausable);
	}

	@Override
	public void titleChanged(MediaPlayer mediaPlayer, int newTitle) {
		for(int i = 0; i < this.screensQtt; i++) 
			this.screens[i].titleChanged(mediaPlayer, newTitle);
	}

	@Override
	public void snapshotTaken(MediaPlayer mediaPlayer, String filename) {
		// TODO is this method to be forwarded for all individual? Or should it be treated for the whole group
		for(int i = 0; i < this.screensQtt; i++) 
			this.screens[i].snapshotTaken(mediaPlayer, filename);
	}

	@Override
	public void lengthChanged(MediaPlayer mediaPlayer, long newLength) {
		for(int i = 0; i < this.screensQtt; i++) 
			this.screens[i].lengthChanged(mediaPlayer, newLength);
	}

	@Override
	public void videoOutput(MediaPlayer mediaPlayer, int newCount) {
		for(int i = 0; i < this.screensQtt; i++) 
			this.screens[i].videoOutput(mediaPlayer, newCount);
	}

	@Override
	public void scrambledChanged(MediaPlayer mediaPlayer, int newScrambled) {
		for(int i = 0; i < this.screensQtt; i++) 
			this.screens[i].scrambledChanged(mediaPlayer, newScrambled);
	}

	@Override
	public void elementaryStreamAdded(MediaPlayer mediaPlayer, int type, int id) {
		for(int i = 0; i < this.screensQtt; i++) 
			this.screens[i].elementaryStreamAdded(mediaPlayer, type, id);
	}

	@Override
	public void elementaryStreamDeleted(MediaPlayer mediaPlayer, int type, int id) {
		for(int i = 0; i < this.screensQtt; i++) 
			this.screens[i].elementaryStreamDeleted(mediaPlayer, type, id);
	}

	@Override
	public void elementaryStreamSelected(MediaPlayer mediaPlayer, int type, int id) {
		for(int i = 0; i < this.screensQtt; i++) 
			this.screens[i].elementaryStreamSelected(mediaPlayer, type, id);
	}

	@Override
	public void corked(MediaPlayer mediaPlayer, boolean corked) {
		for(int i = 0; i < this.screensQtt; i++) 
			this.screens[i].corked(mediaPlayer, corked);
	}

	public void mute() {
		for(int i = 0; i < this.screensQtt; i++) 
			
			if(this.forcedMute) {
			
				if(!this.screens[i].getMediaPlayer().isMute()) {
					this.screens[i].getMediaPlayer().mute();
				}
				// Else do nothing
			}
			else {
				
				// Otherwise don't care and toggle mute state 
				this.screens[i].getMediaPlayer().mute();
			}
	}

	public void forceMute() {
		if(!this.forcedMute) this.forcedMute = true;
		else this.forcedMute = false;
		for(int i = 0; i < this.screensQtt; i++) 
			this.screens[i].getMediaPlayer().mute();
	}
	
	@Override
	public void volumeChanged(MediaPlayer mediaPlayer, float volume) {
		for(int i = 0; i < this.screensQtt; i++) 
			this.screens[i].volumeChanged(mediaPlayer, volume);
	}

	@Override
	public void audioDeviceChanged(MediaPlayer mediaPlayer, String audioDevice) {
		for(int i = 0; i < this.screensQtt; i++) 
			this.screens[i].audioDeviceChanged(mediaPlayer, audioDevice);
	}

	@Override
	public void chapterChanged(MediaPlayer mediaPlayer, int newChapter) {
		for(int i = 0; i < this.screensQtt; i++) 
			this.screens[i].chapterChanged(mediaPlayer, newChapter);
	}

	@Override
	public void error(MediaPlayer mediaPlayer) {
		for(int i = 0; i < this.screensQtt; i++) 
			this.screens[i].error(mediaPlayer);
	}

	@Override
	public void mediaMetaChanged(MediaPlayer mediaPlayer, int metaType) {
		for(int i = 0; i < this.screensQtt; i++) 
			this.screens[i].mediaMetaChanged(mediaPlayer, metaType);
	}

	@Override
	public void mediaSubItemAdded(MediaPlayer mediaPlayer, libvlc_media_t subItem) {
		for(int i = 0; i < this.screensQtt; i++) 
			this.screens[i].mediaSubItemAdded(mediaPlayer, subItem);
	}

	@Override
	public void mediaDurationChanged(MediaPlayer mediaPlayer, long newDuration) {
		for(int i = 0; i < this.screensQtt; i++) 
			this.screens[i].mediaDurationChanged(mediaPlayer, newDuration);
	}

	@Override
	public void mediaParsedChanged(MediaPlayer mediaPlayer, int newStatus) {
		for(int i = 0; i < this.screensQtt; i++) 
			this.screens[i].mediaParsedChanged(mediaPlayer, newStatus);
	}

	@Override
	public void mediaFreed(MediaPlayer mediaPlayer) {
		for(int i = 0; i < this.screensQtt; i++) 
			this.screens[i].mediaFreed(mediaPlayer);
	}

	@Override
	public void mediaStateChanged(MediaPlayer mediaPlayer, int newState) {
		for(int i = 0; i < this.screensQtt; i++) 
			this.screens[i].mediaStateChanged(mediaPlayer, newState);
	}

	@Override
	public void mediaSubItemTreeAdded(MediaPlayer mediaPlayer, libvlc_media_t item) {
		for(int i = 0; i < this.screensQtt; i++) 
			this.screens[i].mediaSubItemTreeAdded(mediaPlayer, item);
	}

	@Override
	public void newMedia(MediaPlayer mediaPlayer) {
		for(int i = 0; i < this.screensQtt; i++) 
			this.screens[i].newMedia(mediaPlayer);
	}

	@Override
	public void subItemPlayed(MediaPlayer mediaPlayer, int subItemIndex) {
		for(int i = 0; i < this.screensQtt; i++) 
			this.screens[i].subItemPlayed(mediaPlayer, subItemIndex);
	}

	@Override
	public void subItemFinished(MediaPlayer mediaPlayer, int subItemIndex) {
		for(int i = 0; i < this.screensQtt; i++) 
			this.screens[i].subItemFinished(mediaPlayer, subItemIndex);
	}

	@Override
	public void endOfSubItems(MediaPlayer mediaPlayer) {
		for(int i = 0; i < this.screensQtt; i++) 
			this.screens[i].endOfSubItems(mediaPlayer);
	}

	public void setScreenQuantity(int newScreenQuantity) {
		this.screensQtt = newScreenQuantity;
	}

	public void start() {
		for(int i = 0; i < this.screensQtt; i++) 
			this.screens[i].start();
	}

	public boolean isPlaying() {
		return this.screens[0].getMediaPlayer().isPlaying();
	}

	public boolean isPlayable() {
		return this.screens[0].getMediaPlayer().isPlayable();
	}

	public EmbeddedMediaPlayer getSelectedScreen() {
		return this.screens[this.selectedScreen].getMediaPlayer();
	}

	public String getSelectedFile() {
		return selectedFile;
	}

	public void setSelectedFile(String selectedFile) {
		this.selectedFile = selectedFile;
		System.out.println("Set new media file based on " + selectedFile);

		// TODO Update the file fetching algorithm
		this.mediaFiles[0] = this.mediaDir.getAbsolutePath() + "\\cam1\\" + selectedFile;
		this.mediaFiles[1] = this.mediaDir.getAbsolutePath() + "\\cam2\\" + selectedFile;
		this.mediaFiles[2] = this.mediaDir.getAbsolutePath() + "\\cam3\\" + selectedFile;
		this.mediaFiles[3] = this.mediaDir.getAbsolutePath() + "\\cam4\\" + selectedFile;

		this.updateScreensMedia();
	}
}
