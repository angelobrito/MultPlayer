package multiplayer;

import static uk.co.caprica.vlcjplayer.Application.application;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import fileHandlers.PathFinder;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.DefaultFullScreenStrategy;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.FullScreenStrategy;
import uk.co.caprica.vlcj.test.multi.PlayerInstance;
import uk.co.caprica.vlcjplayer.Application;
import uk.co.caprica.vlcjplayer.event.SnapshotImageEvent;


public class MultiScreensHandler extends EmbeddedMediaPlayerComponent implements MediaPlayerEventListener, MouseListener, MouseMotionListener, MouseWheelListener, KeyListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6723807719374506428L;

	private File mediaDirectory;
	private List<String> mediaFilePath;
	private String selectedFile;

	private int rowsNumber = 2;
	private int collumsNumber = 2;
	private List<PlayerInstance> players;

	private MediaPlayerFactory factory;

	FullScreenStrategy fullScreenStrategy;

	private int selectedScreen;
	private boolean forcedMute;

	public MultiScreensHandler(Window container) {
		JPanel contentPane = new JPanel();
		contentPane.setBackground(Color.black);
		contentPane.setLayout(new GridLayout(rowsNumber, collumsNumber, 16, 16));
		contentPane.setBorder(new EmptyBorder(16, 16, 16, 16));

		players = new ArrayList<PlayerInstance>();
		mediaFilePath = new ArrayList<String>();

		container = new Frame("Screens");
		container.setLayout(new BorderLayout());
		container.setBackground(Color.black);
		container.add(contentPane, BorderLayout.CENTER);
		container.setBounds(100, 100, 1600, 300);
		container.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent evt) {
				for(PlayerInstance pi : players) {
					pi.mediaPlayer().release();
				}
				factory.release();
				System.exit(0);
			}
		});

		container.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				for(int i = 0; i < players.size(); i ++ ) {
					players.get(i).mediaPlayer().pause();
				}
			}
		});

		factory = new MediaPlayerFactory();

		FullScreenStrategy fullScreenStrategy = new DefaultFullScreenStrategy(container);

		for(int i = 0; i < application().getScreenQtt(); i ++ ) {
			EmbeddedMediaPlayer player = factory.newEmbeddedMediaPlayer(fullScreenStrategy);
			PlayerInstance playerInstance = new PlayerInstance(player);
			// FIXME for debug reason all screens have sound enabled as default but shouldn't
			//playerInstance.mediaPlayer().mute(true);
			players.add(playerInstance);

			JPanel playerPanel = new JPanel();
			playerPanel.setLayout(new BorderLayout());
			playerPanel.setBorder(new LineBorder(Color.white, 2));
			playerPanel.add(playerInstance.videoSurface());

			contentPane.add(playerPanel);
		}
		container.setVisible(true);
	}

	public MediaPlayerFactory getFactory() {
		return this.factory;
	}

	public List<PlayerInstance> getPlayers() {
		return this.players;
	}

	public void screensRelease() {
		for(PlayerInstance pi : this.players) {
			pi.mediaPlayer().release();
		}
		this.factory.release();
	}

	private void updateScreensMedia() {
		for(int i = 0; i < mediaFilePath.size(); i ++ ) {
			players.get(i).mediaPlayer().prepareMedia(mediaFilePath.get(i));
			players.get(i).mediaPlayer().setVideoSurface(factory.newVideoSurface(players.get(i).videoSurface()));
		}
	}

	public void setMediaDirectory(String newDirectoryPath) {
		this.mediaDirectory = new File(newDirectoryPath);
	}

	public MediaPlayer getHeadPlayer() {
		return this.players.get(this.selectedScreen).mediaPlayer();
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

	public void activateScreen(int screenNumber) {
		//TODO Change screen color and unmute
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

				if(!this.players.get(i).mediaPlayer().isMute()) {
					this.players.get(i).mediaPlayer().mute();
				}
				// Else do nothing
			}
			else {

				// Otherwise don't care and toggle mute state 
				this.players.get(i).mediaPlayer().mute();
			}
	}

	public boolean isMuteForced() {
		return this.forcedMute;
	}

	public void forceMute() {
		if(!this.forcedMute) this.forcedMute = true;
		else this.forcedMute = false;
		for(int i = 0; i < this.players.size(); i++) 
			this.players.get(i).mediaPlayer().mute();
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
			this.players.get(i).mediaPlayer().start();
	}

	public boolean isPlaying() {
		return this.players.get(this.selectedScreen).mediaPlayer().isPlaying();
	}

	public boolean isPlayable() {
		return this.players.get(this.selectedScreen).mediaPlayer().isPlayable();
	}

	public EmbeddedMediaPlayer getSelectedScreen() {
		return this.players.get(this.selectedScreen).mediaPlayer();
	}

	public String getSelectedFile() {
		return selectedFile;
	}

	public void setSelectedFile(String selectedFile) {
		this.selectedFile = selectedFile;

		// FIXME maybe an improvement on how to handle these files is needed
		Vector<String> foundFiles = this.getRelatedFiles(this.mediaDirectory, selectedFile);
		this.mediaFilePath.clear();
		for(int i = 0; i < this.players.size(); i++) {
			if(i < foundFiles.size()) this.mediaFilePath.add(foundFiles.get(i));
			else this.mediaFilePath.add("");
		}
		this.updateScreensMedia();
	}

	public Vector<String> getRelatedFiles(File workingDirectory, String fileName) {

		Vector<String> result = new Vector<String>();

		try {

			Path startingDir = Paths.get(workingDirectory.getAbsolutePath());
			PathFinder finder = new PathFinder(fileName);
			Files.walkFileTree(startingDir, finder);
			result = finder.getPathsAsArray();
			finder.done(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public boolean isPlayerReady() {
		return this.isPlaying() || 
				this.isPlayable();
	}

	public void resume() {
		this.setVisible(true);
		for(int i = 0; i < this.players.size(); i++){

			if(!this.players.get(i).mediaPlayer().isPlaying()){
				System.out.println("Resume for=" + i);
				this.players.get(i).mediaPlayer().play();
			}
			else {
				System.out.println("Paused for=" + i);
				this.players.get(i).mediaPlayer().pause();
			}
			System.out.println("Player screen running?" + (this.players.get(i).mediaPlayer().isPlaying()));
		}
	}

	public int getRowsNumber() {
		return rowsNumber;
	}

	public int getCollumsNumber() {
		return collumsNumber;
	}

	public void stop() {
		for(int i = 0; i < this.players.size(); i++){

			System.out.println("Stop for=" + i);
			this.players.get(i).mediaPlayer().stop();
			System.out.println("Player screen running?" + (this.players.get(i).mediaPlayer().isPlaying()));
		}
	}

	public void setRate(float rate) {
		for(int i = 0; i < this.players.size(); i++){

			System.out.println("setRate for=" + i);
			this.players.get(i).mediaPlayer().setRate(rate);
			System.out.println("Player running?" + (this.players.get(i).mediaPlayer().isPlaying()) + " with new rate " + this.players.get(i).mediaPlayer().getRate());
		}
	}

	public void setVolume(int value) {
		for(int i = 0; i < this.players.size(); i++){

			System.out.println("setVolume for=" + i);
			this.players.get(i).mediaPlayer().setVolume(value);
			System.out.println("Player running?" + (this.players.get(i).mediaPlayer().isPlaying()) + " with new volume " + this.players.get(i).mediaPlayer().getVolume());
		}
	}

	public void getSnapshots() {
		for(int i = 0; i < this.players.size(); i++){
			if(this.players.get(i).mediaPlayer().isPlaying())
				this.snapshotTaken(this.players.get(i).mediaPlayer(), this.mediaFilePath.get(i));
		}
	}

	@Override
	public void snapshotTaken(MediaPlayer mediaPlayer, String outputFileDirectoryPath) {
		String fileName;
		int incremental = 0;

		// check if a screenshot already exists and increment name
		fileName = outputFileDirectoryPath + "\\screenshot.png"; 
		while((new File(fileName)).exists()) {
			incremental++;
			fileName = outputFileDirectoryPath + "\\screenshot" + incremental + ".png"; 
		}
		BufferedImage image = mediaPlayer.getSnapshot();
		if (image != null) {
			
			// FIXME This method as is doesn't uses the filename generated above it should be upgraded in near future.
			application().post(new SnapshotImageEvent(image));
		}

	}

}