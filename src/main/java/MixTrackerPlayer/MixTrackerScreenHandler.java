package MixTrackerPlayer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;

import com.sun.jna.NativeLibrary;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;


public class MixTrackerScreenHandler extends EmbeddedMediaPlayerComponent implements MediaPlayerEventListener, MouseListener, MouseMotionListener, MouseWheelListener, KeyListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3731874145026049612L;
	//Our File chooser to help find a file to play
	private File mediaDir;
	private String[] mediaFiles;
	private String vlcPath;
	private Screen screens[];
	private int screensQtt;

	public MixTrackerScreenHandler(int screensQtt) {

		this.screensQtt = screensQtt;
		//videoSurface = mediaPlayerFactory.newVideoSurface(canvas);
		if(this.isLibVLCPresent()) {

			this.mediaFiles = new String[this.screensQtt];
			this.setLayout(new GridLayout((this.screensQtt/2), (this.screensQtt/2))); 
			this.setMinimumSize(new Dimension(600, 400));
			this.setBackground(Color.BLACK);

			this.screens = new Screen[this.screensQtt];
			for(int i = 0; i < this.screensQtt; i++) {
				this.screens[i] = new Screen("Camera #" + (i + 1), this.mediaFiles[i]);
				this.add(this.screens[i]);
			}
		}
		else {

			// TODO
			System.out.println("Puglin Not found. Create an error JPanel");
		}
	}

	private void updateScreensMedia() {
		for(int i = 0; i < this.screensQtt; i++)  {
			this.screens[i].setNewMedia(this.mediaFiles[i]);
		}
	}

	public void setNewMediaDirectory(String newDirectoryPath) {

		this.mediaDir = new File(newDirectoryPath);

		// TODO Update the file fetching algorithm
		this.mediaFiles[0] = this.mediaDir.getAbsolutePath() + "\\bourne.mp4";
		this.mediaFiles[1] = this.mediaDir.getAbsolutePath() + "\\legend.mp4";
		this.mediaFiles[2] = this.mediaDir.getAbsolutePath() + "\\sample.mp4";
		this.mediaFiles[3] = this.mediaDir.getAbsolutePath() + "\\simpsons.mp4";

		this.updateScreensMedia();
	}

	public File getMediaDirectory() {
		return this.mediaDir;
	}

	public boolean isLibVLCPresent() {

		boolean found = new NativeDiscovery().discover();

		// TODO find VLC automatically
		vlcPath = "C:\\Program Files\\VideoLAN\\VLC";
		NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), vlcPath);

		System.setProperty("VLC_PLUGIN_PATH", "");
		System.out.println("  version: {" + LibVlc.INSTANCE.libvlc_get_version() + "}");
		System.out.println(" compiler: {" + LibVlc.INSTANCE.libvlc_get_compiler() + "}");
		System.out.println("changeset: {" + LibVlc.INSTANCE.libvlc_get_changeset() + "}");

		return found;
	}

	public String[] getVideosNames() {
		return new String[4];
	}

	public void run(){
		this.startScreens();
	}

	private void startScreens() {
		for(int i = 0; i < this.screensQtt; i++) {
			//this.screens[i].start();
		}
	}

	public void activateScreen(int screenNumber) {
		//this.screens[screenNumber].start();
	}

	public void resume() {

		for(int i = 0; i < this.screensQtt; i++) 
			//this.screens[i].play();
			System.out.println("Play { " +this.screens[i].toString() + "}?");
	}

	public void pause() {

		for(int i = 0; i < this.screensQtt; i++) 
			//this.screens[i].pause();
			System.out.println("Pause { " +this.screens[i].toString() + "}?");
	}
	
	public void stop() {
		
		for(int i = 0; i < this.screensQtt; i++) 
			//this.screens[i].stop();
			System.out.println("Stop { " +this.screens[i].toString() + "}?");
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mediaChanged(MediaPlayer mediaPlayer, libvlc_media_t media, String mrl) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void opening(MediaPlayer mediaPlayer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void buffering(MediaPlayer mediaPlayer, float newCache) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playing(MediaPlayer mediaPlayer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void paused(MediaPlayer mediaPlayer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopped(MediaPlayer mediaPlayer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void forward(MediaPlayer mediaPlayer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void backward(MediaPlayer mediaPlayer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void finished(MediaPlayer mediaPlayer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void positionChanged(MediaPlayer mediaPlayer, float newPosition) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void seekableChanged(MediaPlayer mediaPlayer, int newSeekable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pausableChanged(MediaPlayer mediaPlayer, int newPausable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void titleChanged(MediaPlayer mediaPlayer, int newTitle) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void snapshotTaken(MediaPlayer mediaPlayer, String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void lengthChanged(MediaPlayer mediaPlayer, long newLength) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void videoOutput(MediaPlayer mediaPlayer, int newCount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void scrambledChanged(MediaPlayer mediaPlayer, int newScrambled) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void elementaryStreamAdded(MediaPlayer mediaPlayer, int type, int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void elementaryStreamDeleted(MediaPlayer mediaPlayer, int type, int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void elementaryStreamSelected(MediaPlayer mediaPlayer, int type, int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void corked(MediaPlayer mediaPlayer, boolean corked) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void muted(MediaPlayer mediaPlayer, boolean muted) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void volumeChanged(MediaPlayer mediaPlayer, float volume) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void audioDeviceChanged(MediaPlayer mediaPlayer, String audioDevice) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void chapterChanged(MediaPlayer mediaPlayer, int newChapter) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(MediaPlayer mediaPlayer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mediaMetaChanged(MediaPlayer mediaPlayer, int metaType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mediaSubItemAdded(MediaPlayer mediaPlayer, libvlc_media_t subItem) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mediaDurationChanged(MediaPlayer mediaPlayer, long newDuration) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mediaParsedChanged(MediaPlayer mediaPlayer, int newStatus) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mediaFreed(MediaPlayer mediaPlayer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mediaStateChanged(MediaPlayer mediaPlayer, int newState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mediaSubItemTreeAdded(MediaPlayer mediaPlayer, libvlc_media_t item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newMedia(MediaPlayer mediaPlayer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void subItemPlayed(MediaPlayer mediaPlayer, int subItemIndex) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void subItemFinished(MediaPlayer mediaPlayer, int subItemIndex) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endOfSubItems(MediaPlayer mediaPlayer) {
		// TODO Auto-generated method stub
		
	}
}
