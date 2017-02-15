package multiplayer;

import static uk.co.caprica.vlcjplayer.Application.application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import fileHandlers.FileAdditionalInfo;
import fileHandlers.FileBrowser;
import fileHandlers.FileTimeTracker;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.binding.internal.libvlc_state_t;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.Equalizer;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.DefaultFullScreenStrategy;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.FullScreenStrategy;
import uk.co.caprica.vlcjplayer.event.SnapshotImageEvent;
import uk.co.caprica.vlcjplayer.view.image.ImagePane;
import uk.co.caprica.vlcjplayer.view.main.MainFrame;


public class MultiScreensHandler extends EmbeddedMediaPlayerComponent implements MediaPlayerEventListener, MouseListener, MouseMotionListener, MouseWheelListener, KeyListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6723807719374506428L;

	private File mediaDirectory;
	private ArrayList<ArrayList<FileAdditionalInfo>> mediaFilePath;
	private String selectedFile;
	private FileTimeTracker timeTracker;
	private MainFrame containerFrame;

	private int rowsNumber = 2;
	private int columnsNumber = 2;
	private List<MultiPlayerInstance> players;

	private MediaPlayerFactory factory;

	private FullScreenStrategy fullScreenStrategy;
	private ImagePane screenLogo;

	private int selectedScreen;
	private boolean forcedMute;
	private boolean paused;

	private JPanel contentPane;
	
	public MultiScreensHandler(JFrame containerFrame) {

		this.containerFrame = (MainFrame) containerFrame;
		this.factory     = new MediaPlayerFactory();
		this.timeTracker = new FileTimeTracker();
		this.screenLogo = new ImagePane(
				ImagePane.Mode.CENTER, 
				getClass().getResource("/MultTecnologia-logo.png"), 
				0.3f);
		this.contentPane = new JPanel();
		this.contentPane.setBackground(Color.BLACK);
		this.contentPane.setVisible(false);
		this.contentPane.setBorder(new EmptyBorder(16, 16, 16, 16));
		this.fullScreenStrategy = new DefaultFullScreenStrategy(new Window(this.containerFrame));
		
		// To deactivate unwanted borders
		this.selectedScreen = -1;
		
		setNewScreensLayout(application().getScreenQtt());

		this.contentPane.setVisible(true);

		this.add(this.contentPane, BorderLayout.CENTER);
		this.setVisible(true);
	}

	public void setNewScreensLayout(int qttScreens) {

		// Calculate the number of Rows and Columns that
		this.rowsNumber = 0;
		this.columnsNumber = 0;
		boolean rows = false;

		do{
			if(!rows) {
				this.rowsNumber++;
				rows = true;
			}
			else {
				this.columnsNumber++;
				rows = false;
			}
		}
		while((this.rowsNumber * this.columnsNumber) < qttScreens);

		this.contentPane.removeAll();
		this.contentPane.setLayout(new GridLayout(this.rowsNumber, this.columnsNumber, 16, 16));
		this.mediaFilePath = new ArrayList<ArrayList<FileAdditionalInfo>>(application().getScreenQtt());
		this.players     = new ArrayList<MultiPlayerInstance>(qttScreens);

		for(int i = 0; i < application().getScreenQtt(); i++) {
			MultiPlayerInstance playerInstance = new MultiPlayerInstance(("Screen #" + (i + 1)), this.factory, this.fullScreenStrategy);
			playerInstance.setLogoImage(this.screenLogo.getImage());
			playerInstance.enableLogo(true);
			this.players.add(playerInstance);

			JPanel playerPanel = new JPanel();
			playerPanel.setLayout(new BorderLayout());
			if(i == this.selectedScreen) playerInstance.setBorder(new LineBorder(Color.YELLOW, 2));
			else playerInstance.setBorder(new LineBorder(Color.WHITE, 2));
			playerPanel.setBorder(playerInstance.getBorder());
			playerPanel.setSize(this.contentPane.getSize());
			playerInstance.setPlayerPanel(playerPanel);
			playerPanel.add(playerInstance.videoSurface());

			this.contentPane.add(playerPanel);
			this.mediaFilePath.add(new ArrayList<FileAdditionalInfo>());
		}
	}
	
	public void updateScreensLayout() {
		for(int i = 0; i < application().getScreenQtt(); i++) {
			MultiPlayerInstance playerInstance = this.players.get(i);

			JPanel playerPanel = playerInstance.getPlayerPanel();
			if(i == this.selectedScreen) playerInstance.setBorder(new LineBorder(Color.YELLOW, 2));
			else playerInstance.setBorder(new LineBorder(Color.WHITE, 2));
			playerPanel.setBorder(playerInstance.getBorder());
			playerPanel.setSize(this.contentPane.getSize());
		}
	}

	public MediaPlayerFactory getFactory() {
		return this.factory;
	}

	public List<MultiPlayerInstance> getPlayers() {
		return this.players;
	}

	public void screensRelease() {
		for(MultiPlayerInstance pi : this.players) {
			pi.release();
		}
		this.factory.release();
	}

	public void setMediaDirectory(String newDirectoryPath) {
		this.mediaDirectory = new File(newDirectoryPath);
	}

	public MediaPlayer getHeadPlayer() {
		return this.getMediaPlayer();
	}

	public File getMediaDirectory() {
		return this.mediaDirectory;
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

	@Override
	public void mediaChanged(MediaPlayer mediaPlayer, libvlc_media_t media, String mrl) {
		for(int i = 0; i < this.players.size(); i++) 
			this.players.get(i).mediaChanged(mediaPlayer, media, mrl);
	}

	@Override
	public void opening(MediaPlayer mediaPlayer) {
		for(int i = 0; i < this.players.size(); i++) 
			this.players.get(i).opening(mediaPlayer);
	}

	@Override
	public void buffering(MediaPlayer mediaPlayer, float newCache) {
		for(int i = 0; i < this.players.size(); i++) 
			this.players.get(i).buffering(mediaPlayer, newCache);
	}

	@Override
	public void playing(MediaPlayer mediaPlayer) {
		for(int i = 0; i < this.players.size(); i++) 
			this.players.get(i).playing(mediaPlayer);
	}

	@Override
	public void paused(MediaPlayer mediaPlayer) {
		for(int i = 0; i < this.players.size(); i++) 
			this.players.get(i).paused(mediaPlayer);
	}

	@Override
	public void stopped(MediaPlayer mediaPlayer) {
		for(int i = 0; i < this.players.size(); i++) 
			this.players.get(i).stopped(mediaPlayer);
	}

	@Override
	public void forward(MediaPlayer mediaPlayer) {
		for(int i = 0; i < this.players.size(); i++) 
			this.players.get(i).forward(mediaPlayer);
	}

	@Override
	public void backward(MediaPlayer mediaPlayer) {
		for(int i = 0; i < this.players.size(); i++) 
			this.players.get(i).backward(mediaPlayer);
	}

	@Override
	public void finished(MediaPlayer mediaPlayer) {
		for(int i = 0; i < this.players.size(); i++) 
			this.players.get(i).finished(mediaPlayer);
	}

	@Override
	public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
		for(int i = 0; i < this.players.size(); i++) 
			this.players.get(i).timeChanged(mediaPlayer, newTime);
	}

	@Override
	public void positionChanged(MediaPlayer mediaPlayer, float newPosition) {
		for(int i = 0; i < this.players.size(); i++) 
			this.players.get(i).positionChanged(mediaPlayer, newPosition);
	}

	@Override
	public void seekableChanged(MediaPlayer mediaPlayer, int newSeekable) {
		for(int i = 0; i < this.players.size(); i++) 
			this.players.get(i).seekableChanged(mediaPlayer, newSeekable);
	}

	@Override
	public void pausableChanged(MediaPlayer mediaPlayer, int newPausable) {
		for(int i = 0; i < this.players.size(); i++) 
			this.players.get(i).pausableChanged(mediaPlayer, newPausable);
	}

	@Override
	public void titleChanged(MediaPlayer mediaPlayer, int newTitle) {
		for(int i = 0; i < this.players.size(); i++) 
			this.players.get(i).titleChanged(mediaPlayer, newTitle);
	}

	@Override
	public void lengthChanged(MediaPlayer mediaPlayer, long newLength) {
		for(int i = 0; i < this.players.size(); i++) 
			this.players.get(i).lengthChanged(mediaPlayer, newLength);
	}

	@Override
	public void videoOutput(MediaPlayer mediaPlayer, int newCount) {
		for(int i = 0; i < this.players.size(); i++) 
			this.players.get(i).videoOutput(mediaPlayer, newCount);
	}

	@Override
	public void scrambledChanged(MediaPlayer mediaPlayer, int newScrambled) {
		for(int i = 0; i < this.players.size(); i++) 
			this.players.get(i).scrambledChanged(mediaPlayer, newScrambled);
	}

	@Override
	public void elementaryStreamAdded(MediaPlayer mediaPlayer, int type, int id) {
		for(int i = 0; i < this.players.size(); i++) 
			this.players.get(i).elementaryStreamAdded(mediaPlayer, type, id);
	}

	@Override
	public void elementaryStreamDeleted(MediaPlayer mediaPlayer, int type, int id) {
		for(int i = 0; i < this.players.size(); i++) 
			this.players.get(i).elementaryStreamDeleted(mediaPlayer, type, id);
	}

	@Override
	public void elementaryStreamSelected(MediaPlayer mediaPlayer, int type, int id) {
		for(int i = 0; i < this.players.size(); i++) 
			this.players.get(i).elementaryStreamSelected(mediaPlayer, type, id);
	}

	@Override
	public void corked(MediaPlayer mediaPlayer, boolean corked) {
		for(int i = 0; i < this.players.size(); i++) 
			this.players.get(i).corked(mediaPlayer, corked);
	}

	public void mute() {
		for(int i = 0; i < this.players.size(); i++) 

			if(this.forcedMute) {

				if(!this.players.get(i).isMute()) {
					this.players.get(i).mute();
				}
				// Else do nothing
			}
			else {

				// Otherwise don't care and toggle mute state 
				this.players.get(i).mute();
			}
	}

	public boolean isMuteForced() {
		return this.forcedMute;
	}

	public void forceMute() {
		if(!this.forcedMute) this.forcedMute = true;
		else this.forcedMute = false;
		for(int i = 0; i < this.players.size(); i++) 
			this.players.get(i).mute();
	}

	public void pauseScreens() {
		if(!this.paused) this.paused = true;
		else this.paused = false;
		for(int i = 0; i < this.players.size(); i++) 
			this.players.get(i).pause();
	}

	@Override
	public void volumeChanged(MediaPlayer mediaPlayer, float volume) {
		for(int i = 0; i < this.players.size(); i++) 
			this.players.get(i).volumeChanged(mediaPlayer, volume);
	}

	@Override
	public void audioDeviceChanged(MediaPlayer mediaPlayer, String audioDevice) {
		for(int i = 0; i < this.players.size(); i++) 
			this.players.get(i).audioDeviceChanged(mediaPlayer, audioDevice);
	}

	@Override
	public void chapterChanged(MediaPlayer mediaPlayer, int newChapter) {
		for(int i = 0; i < this.players.size(); i++) 
			this.players.get(i).chapterChanged(mediaPlayer, newChapter);
	}

	@Override
	public void error(MediaPlayer mediaPlayer) {
		for(int i = 0; i < this.players.size(); i++) 
			this.players.get(i).error(mediaPlayer);
	}

	@Override
	public void mediaMetaChanged(MediaPlayer mediaPlayer, int metaType) {
		for(int i = 0; i < this.players.size(); i++) 
			this.players.get(i).mediaMetaChanged(mediaPlayer, metaType);
	}

	@Override
	public void mediaSubItemAdded(MediaPlayer mediaPlayer, libvlc_media_t subItem) {
		for(int i = 0; i < this.players.size(); i++) 
			this.players.get(i).mediaSubItemAdded(mediaPlayer, subItem);
	}

	@Override
	public void mediaDurationChanged(MediaPlayer mediaPlayer, long newDuration) {
		for(int i = 0; i < this.players.size(); i++) 
			this.players.get(i).mediaDurationChanged(mediaPlayer, newDuration);
	}

	@Override
	public void mediaParsedChanged(MediaPlayer mediaPlayer, int newStatus) {
		for(int i = 0; i < this.players.size(); i++) 
			this.players.get(i).mediaParsedChanged(mediaPlayer, newStatus);
	}

	@Override
	public void mediaFreed(MediaPlayer mediaPlayer) {
		for(int i = 0; i < this.players.size(); i++) 
			this.players.get(i).mediaFreed(mediaPlayer);
	}

	@Override
	public void mediaStateChanged(MediaPlayer mediaPlayer, int newState) {
		for(int i = 0; i < this.players.size(); i++) 
			this.players.get(i).mediaStateChanged(mediaPlayer, newState);
	}

	@Override
	public void mediaSubItemTreeAdded(MediaPlayer mediaPlayer, libvlc_media_t item) {
		for(int i = 0; i < this.players.size(); i++) 
			this.players.get(i).mediaSubItemTreeAdded(mediaPlayer, item);
	}

	@Override
	public void newMedia(MediaPlayer mediaPlayer) {
		for(int i = 0; i < this.players.size(); i++) 
			this.players.get(i).newMedia(mediaPlayer);
	}

	@Override
	public void subItemPlayed(MediaPlayer mediaPlayer, int subItemIndex) {
		for(int i = 0; i < this.players.size(); i++) 
			this.players.get(i).subItemPlayed(mediaPlayer, subItemIndex);
	}

	@Override
	public void subItemFinished(MediaPlayer mediaPlayer, int subItemIndex) {
		for(int i = 0; i < this.players.size(); i++) 
			this.players.get(i).subItemFinished(mediaPlayer, subItemIndex);
	}

	@Override
	public void endOfSubItems(MediaPlayer mediaPlayer) {
		for(int i = 0; i < this.players.size(); i++) 
			this.players.get(i).endOfSubItems(mediaPlayer);
	}

	public void start() {
		for(int i = 0; i < this.players.size(); i++) 
			this.players.get(i).start();
	}

	public boolean isPlaying() {
		boolean result = false;
		for(MultiPlayerInstance player : this.players) {
			if(!result) result = player.isPlaying();
			else break;
		}
		return result;
	}

	public boolean isPlayable() {
		boolean result = false;
		for(MultiPlayerInstance player : this.players) {
			if(!result) result = player.isPlayable();
			else break;
		}
		return result;
	}

	public void setPosition(float position) {
		for(int i = 0; i < this.players.size(); i++) 
			// FIXME not all videos have the same position, must check on this to avoid errors
			this.players.get(i).setPosition(position);
	}

	public float getPosition() {
		return getLongestPlayer().getPosition();
	}

	private EmbeddedMediaPlayer getLongestPlayer() {
		int longestVideo = 0;
		for(int i = 0; i < this.players.size(); i++) {
			// FIXME not all videos have the same Length, must check on this to avoid errors
			if(this.players.get(i).isSeekable() &&
					this.players.get(i).getLength() > 
			this.players.get(longestVideo).getLength())
				longestVideo = i;
		}
		return this.players.get(longestVideo).mediaPlayer();
	}

	public long getTime() {
		return getLongestPlayer().getTime();
	}

	public long getLength(){
		return getLongestPlayer().getLength();
	}

	public EmbeddedMediaPlayer getSelectedScreen() {
		return this.players.get(this.selectedScreen).mediaPlayer();
	}
	
	public void setSelectedScreen(int screenNumber) {
		System.out.println("Updating screenNumber=" + screenNumber);
		this.selectedScreen = (screenNumber % application().getScreenQtt());

		//FIXME fix the updated Border lines 
		updateScreensLayout();
	}

	public String getSelectedFile() {
		return selectedFile;
	}

	public void setSelectedFile(String selectedFile) {

		// file name example: fileName=CH01-20161120-103622.avi
		this.selectedFile = selectedFile;
		System.out.println("setSelectedFile(" + selectedFile + ")");

		// Search the folders and add them to the time tracker list
		File startDirRoot = this.mediaDirectory;
		while(startDirRoot.getName().contains("channel") || 
				startDirRoot.getName().contains("cam") ||
				startDirRoot.getName().contains("camera") ||
				startDirRoot.getName().contains("Cam") ||
				startDirRoot.getName().contains("Camera") ||
				startDirRoot.getName().contains("Channel")) startDirRoot = startDirRoot.getParentFile(); 
		timeTracker.addFoldersToTrack(startDirRoot);

		// Get Related files to the selected one to be played now
		ArrayList<FileAdditionalInfo> foundFiles = FileBrowser.getRelatedFiles(this.mediaDirectory, selectedFile);
		
		// Clear Previous Media
		for(int i = 0; i < application().getScreenQtt(); i++) this.mediaFilePath.get(i).clear();

		for(int i = 0; i < this.players.size(); i++) {
			
			FileAdditionalInfo info;
			int channel = -1;
			
			if(foundFiles.size() > 0) {
				info = foundFiles.get(0);
				foundFiles.remove(0);
				channel = info.getChannel();
			}
			else info = null;

			if(channel > 0 && channel <= this.mediaFilePath.size()) {
				this.mediaFilePath.get(channel-1).add(info);
				timeTracker.addRunningItem(info);
			}
			else {
				if(!(this.mediaFilePath.get(i).size() > 0)) {
					this.mediaFilePath.get(i).add(info);
					timeTracker.addRunningItem(info);
				}
			}
		}
		this.updateScreensMedia();
	}

	private void updateScreensMedia() {
		
		ArrayList<Boolean> playersToVisit = new ArrayList<Boolean>(this.players.size());
		for(int i = 0; i < this.players.size(); i++) playersToVisit.add(true);
		
		int queued = 0;
		for (ArrayList<FileAdditionalInfo> channelFiles : this.mediaFilePath) {
			if(channelFiles.size() > 0) {
				FileAdditionalInfo info = channelFiles.get(0);
				int channelNumber = 0;
				String filePath = "";
				
				if(info != null) {
					filePath = info.getFilePath();
					channelNumber = info.getChannel();
				}
				
				if(channelNumber > 0 && channelNumber <= this.players.size()) {
					queued = channelNumber-1;
				}
				else {
					
					// For files that has no channels definition get the first free player 
					queued = 0;
					for(Boolean free : playersToVisit) {
						if(free) break;
						else queued = (queued+1)%this.players.size();
					}
				}

				this.players.get(queued).prepareMedia(filePath);
				this.players.get(queued).updateVideoSurface();
				this.players.get(queued).enableLogo(filePath.equals(""));
				if(playersToVisit.size() > 0) {
					playersToVisit.add(queued, false);
					playersToVisit.remove(queued+1);
				}
			}
		}
		
		for(int i = 0; i < playersToVisit.size(); i++) {

			if(playersToVisit.get(i)) {
				this.players.get(i).prepareMedia("");
				this.players.get(i).updateVideoSurface();
				this.players.get(i).enableLogo(true);
			}
		}
	}
	
	public boolean hasRunningPlayer() {
		return isPlayable() && isPlaying() && isRunningState();
	}
	
	private boolean isRunningState() {
		for(MultiPlayerInstance player : this.players) {
			if(player.isRunningState()) return true;
		}
		return false;
	}
	
	public libvlc_state_t getMediaPlayerState() {
		libvlc_state_t result = null;
		for(MultiPlayerInstance player : this.players){
			result = player.getMediaState();
			if(result != null && ( 
					result.toString().equalsIgnoreCase("libvlc_Playing") ||
					result.toString().equalsIgnoreCase("libvlc_Paused")
					) ) break;
		}
		return result;
	}
	
	public boolean isRecording(){
		for(MultiPlayerInstance player: this.players){
			if(player.isRecording()) return true;
		}
		return false;
	}
	
	public void resume() {
		this.setVisible(true);
		for(int i = 0; i < this.players.size(); i++){
			MediaPlayer mediaPlayer = this.players.get(i).mediaPlayer();

			// FIXME cut the cases where there is no media and the player is not ready
			System.out.print("Resume for Player[" + i + "].State= {" + mediaPlayer.getMediaPlayerState().toString() + "} - ");
			if(!mediaPlayer.isPlaying() && !mediaPlayer.getMediaPlayerState().toString().equalsIgnoreCase("libvlc_Ended")){
				System.out.println("Player Started...");
				this.players.get(i).start();
			}
			else if(mediaPlayer.isPlaying()) {
				System.out.print("Screen[" + (i+1) + "] is already playing ");
				if(!mediaPlayer.getMediaPlayerState().toString().equalsIgnoreCase("libvlc_Paused")){
					System.out.println("but not Paused then Pause!");
					this.players.get(i).pause();
				}
				else {
					System.out.println("and Paused then Play!");
					this.players.get(i).play();
				}
			}
			else {
				System.out.println("Screen[" + (i+1) + "] is not ready");
			}

			// Set a small delay to let the other threads to run and update the state
			try {
				Thread.sleep(40);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.print("Player[" + i + "].Screen[" + (i+1) + "] running? " + (this.players.get(i).mediaPlayer().isPlaying()));
			System.out.println(" State= {" + mediaPlayer.getMediaPlayerState().toString() + "}");
		}
	}

	public int getRowsNumber() {
		return rowsNumber;
	}

	public int getCollumsNumber() {
		return columnsNumber;
	}

	public void stop() {
		for(MultiPlayerInstance player : this.players){
			player.stop();
			if(player.isRecording()) player.record();
		}
	}

	public void setRate(float rate) {
		for(int i = 0; i < this.players.size(); i++){
			this.players.get(i).setRate(rate);
		}
	}
	
	public float getRate() {
		float result = 0;
		for(MultiPlayerInstance player : this.players) {
			if(result < player.getRate()) result = player.getRate();
		}
		return result;
	}

	public boolean isPaused() {
		return this.paused;
	}

	public void setContrast(float contrast) {
		for(int i = 0; i < this.players.size(); i++){
			this.players.get(i).setContrast(contrast);
		}
	}

	public void setVolume(int value) {
		for(int i = 0; i < this.players.size(); i++){
			this.players.get(i).setVolume(value);
		}
	}

	public void setBrightness(float brightness) {
		for(int i = 0; i < this.players.size(); i++){
			this.players.get(i).setBrightness(brightness);
		}
	}

	public void setSaturation(float saturation) {
		for(int i = 0; i < this.players.size(); i++){
			this.players.get(i).setSaturation(saturation);
		}
	}

	public void setAdjustVideo(boolean enable) {
		for(int i = 0; i < this.players.size(); i++){
			this.players.get(i).setAdjustVideo(enable);
		}
	}

	public void setGamma(float gamma) {
		for(int i = 0; i < this.players.size(); i++){
			this.players.get(i).setGamma(gamma);
		}
	}

	public void setHue(int hue) {
		for(int i = 0; i < this.players.size(); i++){
			this.players.get(i).setHue(hue);
		}
	}

	public void setEqualizer(Equalizer equalizer) {
		for(int i = 0; i < this.players.size(); i++){
			this.players.get(i).setEqualizer(equalizer);
		}
	}

	public float getBrightness() {
		float result = 0;
		for(int i = 0; i < this.players.size(); i++){
			float t = this.players.get(i).getBrightness();
			if(t > result) result = t;
		}
		return result;
	}

	public float getContrast() {
		float result = 0;
		for(int i = 0; i < this.players.size(); i++){
			float t = this.players.get(i).getContrast();
			if(t > result) result = t;
		}
		return result;
	}

	public int getHue() {
		int result = 0;
		for(int i = 0; i < this.players.size(); i++){
			int t = this.players.get(i).getHue();
			if(t > result) result = t;
		}
		return result;
	}

	public float getSaturation() {
		float result = 0;
		for(int i = 0; i < this.players.size(); i++){
			float t = this.players.get(i).getSaturation();
			if(t > result) result = t;
		}
		return result;
	}

	public float getGamma() {
		float result = 0;
		for(int i = 0; i < this.players.size(); i++){
			float t = this.players.get(i).getGamma();
			if(t > result) result = t;
		}
		return result;
	}

	public int getVolume() {
		int result = 0;
		for(int i = 0; i < this.players.size(); i++){
			int t = this.players.get(i).getVolume();
			if(t > result) result = t;
		}
		return result;
	}

	public void getSnapshots() {
		for( MultiPlayerInstance player : this.players){
			if(player.isPlaying())
				this.snapshotTaken(player.mediaPlayer(), player.getMediaPath());
		}
	}

	@Override
	public void snapshotTaken(MediaPlayer mediaPlayer, String outputFileDirectoryPath) {
		BufferedImage image = mediaPlayer.getSnapshot();
		if (image != null) application().post(new SnapshotImageEvent(image));
	}

	public void selectScreenshotFileName(String outputFileDirectoryPath) {
		String fileName;
		int incremental = 0;

		// check if a screenshot already exists and increment name
		fileName = outputFileDirectoryPath + "\\screenshot.png"; 
		while((new File(fileName)).exists()) {
			incremental++;
			fileName = outputFileDirectoryPath + "\\screenshot" + incremental + ".png"; 
		}
	}

	public boolean hasNextToPlay() {
		return this.timeTracker.hasNextVideo();
	}
	
	public FileAdditionalInfo getNextVideo() {
		return this.timeTracker.getNextVideoItem();
	}
	
	public FileAdditionalInfo getPreviousVideo() {
		return this.timeTracker.getPreviousVideoItem();
	}

	public boolean hasPreviousToPlay() {
		return this.timeTracker.hasPreviousVideo();
	}

	public void setScale(float zoom) {
		for(MultiPlayerInstance player : this.players) {
			player.setScale(zoom);
		}
	}

	public void setAspectRatio(String aspectRatio) {
		for(MultiPlayerInstance player : this.players) {
			player.setAspectRatio(aspectRatio);
		}
	}
	
	@Override
	public void mouseClicked(java.awt.event.MouseEvent e) {
		System.out.println("Clicked Screen #");
		super.mouseClicked(e);
	}

	@Override
	public void mousePressed(java.awt.event.MouseEvent e) {
		System.out.println("Pressed Screen #");
		super.mousePressed(e);
	}

	@Override
	public void mouseReleased(java.awt.event.MouseEvent e) {
		System.out.println("Released Screen #" );
		super.mouseReleased(e);
	}

	@Override
	public void mouseEntered(java.awt.event.MouseEvent e) {
		System.out.println("Entered Screen #");
		super.mouseEntered(e);
	}

	@Override
	public void mouseExited(java.awt.event.MouseEvent e) {
		System.out.println("Leaved Screen #");
		super.mouseExited(e);
	}

	// FIXME record just the selected screen and not just screen 0
	public void record() {
		this.players.get(0).record();
      
//		for(MultiPlayerInstance player : this.players) {
//			// TODO include an if or remove the for to just save the selected screen
//			player.record();
//		}
	}

	// FIXME record just the selected screen
	public void stopRecord() {
		for(int screen = 0; screen < application().getScreenQtt(); screen++) {
			this.players.get(screen).stopRecord();
		}
	}
}