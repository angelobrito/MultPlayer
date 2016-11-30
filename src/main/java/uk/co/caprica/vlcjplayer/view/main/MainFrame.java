/*
 * This file is part of VLCJ.
 *
 * VLCJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VLCJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VLCJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2015 Caprica Software Limited.
 */

package uk.co.caprica.vlcjplayer.view.main;

import static uk.co.caprica.vlcjplayer.Application.application;
import static uk.co.caprica.vlcjplayer.Application.resources;
import static uk.co.caprica.vlcjplayer.view.action.Resource.resource;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import com.google.common.eventbus.Subscribe;

import multiplayer.MultiScreensHandler;
import net.miginfocom.swing.MigLayout;
import uk.co.caprica.vlcj.binding.internal.libvlc_state_t;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcjplayer.event.AfterExitFullScreenEvent;
import uk.co.caprica.vlcjplayer.event.BeforeEnterFullScreenEvent;
import uk.co.caprica.vlcjplayer.event.PausedEvent;
import uk.co.caprica.vlcjplayer.event.PlayingEvent;
import uk.co.caprica.vlcjplayer.event.ShowDebugEvent;
import uk.co.caprica.vlcjplayer.event.ShowEffectsEvent;
import uk.co.caprica.vlcjplayer.event.ShowMessagesEvent;
import uk.co.caprica.vlcjplayer.event.SnapshotImageEvent;
import uk.co.caprica.vlcjplayer.event.StoppedEvent;
import uk.co.caprica.vlcjplayer.view.BaseFrame;
import uk.co.caprica.vlcjplayer.view.MouseMovementDetector;
import uk.co.caprica.vlcjplayer.view.action.StandardAction;
import uk.co.caprica.vlcjplayer.view.action.mediaplayer.MediaPlayerActions;
import uk.co.caprica.vlcjplayer.view.snapshot.SnapshotView;

@SuppressWarnings("serial")
public final class MainFrame extends BaseFrame {

	private static final String ACTION_EXIT_FULLSCREEN = "exit-fullscreen";

	private static final KeyStroke KEYSTROKE_ESCAPE = KeyStroke.getKeyStroke("ESCAPE");

	private static final KeyStroke KEYSTROKE_TOGGLE_FULLSCREEN = KeyStroke.getKeyStroke("F11");

	private final MultiScreensHandler multiMediaPlayerComponent;

	private final Action mediaOpenAction;
	private final Action mediaQuitAction;

	private final StandardAction videoFullscreenAction;
	private final StandardAction videoAlwaysOnTopAction;

	private final Action toolsEffectsAction;
	private final Action toolsMessagesAction;
	private final Action toolsDebugAction;

	private final Action helpAboutAction;

	private final JMenuBar menuBar;

	private final JMenu fileMenu;
	private final JMenu mediaRecentMenu;

	private final JMenu playbackMenu;
	private final JMenu playbackSpeedMenu;

	private final JMenu audioMenu;
	private final JMenu audioDeviceMenu;

	private final JMenu videoMenu;
	private final JMenu videoZoomMenu;
	private final JMenu videoCropMenu;
	private final JMenuItem videoSnapshot;

	private final JMenu toolsMenu;

	private final JMenu helpMenu;

	private final JFileChooser fileChooser;

	private final PlaylistPane playlistPane;

	private final ControlsPane controlsPane;

	private final VideoContentPane videoContentPane;

	private final JPanel bottomPane;

	private final MouseMovementDetector mouseMovementDetector;

	private final MediaPlayerActions mediaPlayerActions;


	public MainFrame() {
		super(resource("main.aplication.name").name());

		super.setMinimumSize(new Dimension(1600, 1300));
		this.setMinimumSize(new Dimension(1600, 600));
		this.setPreferredSize(new Dimension(1600, 600));

		this.multiMediaPlayerComponent = application().getNewMediaPlayerComponent(this);
		this.multiMediaPlayerComponent.setMinimumSize(new Dimension(1100, 600));

		mediaPlayerActions = application().mediaNewPlayerActions();

		JPopupMenu.setDefaultLightWeightPopupEnabled(false);

		mediaOpenAction = new StandardAction(resource("menu.media.item.openFile")) {
			@Override
			public void actionPerformed(ActionEvent e) {

				// Fetch the Video folder (it depends on user interaction after the click Open Folder)
				fetchWorkingVideoFolder(e);
			}
		};

		mediaQuitAction = new StandardAction(resource("menu.media.item.quit")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				System.exit(0);
			}
		};

		videoFullscreenAction = new StandardAction(resource("menu.video.item.fullscreen")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				multiMediaPlayerComponent.getSelectedScreen().toggleFullScreen();
			}
		};

		videoAlwaysOnTopAction = new StandardAction(resource("menu.video.item.alwaysOnTop")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean onTop;
				Object source = e.getSource();
				if (source instanceof JCheckBoxMenuItem) {
					JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem)source;
					onTop = menuItem.isSelected();
				}
				else { 
					throw new IllegalStateException("Don't know about source " + source);
				}
				setAlwaysOnTop(onTop);
			}
		};

		toolsEffectsAction = new StandardAction(resource("menu.tools.item.effects")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				application().post(ShowEffectsEvent.INSTANCE);
			}
		};

		toolsMessagesAction = new StandardAction(resource("menu.tools.item.messages")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				application().post(ShowMessagesEvent.INSTANCE);
			}
		};

		toolsDebugAction = new StandardAction(resource("menu.tools.item.debug")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				application().post(ShowDebugEvent.INSTANCE);
			}
		};

		helpAboutAction = new StandardAction(resource("menu.help.item.about")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				AboutDialog dialog = new AboutDialog(MainFrame.this);
				dialog.setLocationRelativeTo(MainFrame.this);
			}
		};

		menuBar = new JMenuBar();

		fileMenu = new JMenu(resource("menu.media").name());
		fileMenu.setMnemonic(resource("menu.media").mnemonic());
		fileMenu.add(new JMenuItem(mediaOpenAction));

		mediaRecentMenu = new RecentMediaMenu(resource("menu.media.item.recent")).menu();
		fileMenu.add(mediaRecentMenu);
		fileMenu.add(new JSeparator());
		fileMenu.add(new JMenuItem(mediaQuitAction));
		menuBar.add(fileMenu);

		playbackMenu = new JMenu(resource("menu.playback").name());
		playbackMenu.setMnemonic(resource("menu.playback").mnemonic());
		playbackMenu.add(new JSeparator());

		playbackSpeedMenu = new JMenu(resource("menu.playback.item.speed").name());
		playbackSpeedMenu.setMnemonic(resource("menu.playback.item.speed").mnemonic());

		//FIXME Update the speedSlideBar whenever the buttons are pressed
		for (Action action : mediaPlayerActions.playbackSpeedActions()) {
			playbackSpeedMenu.add(new JMenuItem(action));
		}
		playbackMenu.add(playbackSpeedMenu);
		playbackMenu.add(new JSeparator());
		for (Action action : mediaPlayerActions.playbackSkipActions()) {
			playbackMenu.add(new JMenuItem(action));
		}

		playbackMenu.add(new JSeparator());
		for (Action action : mediaPlayerActions.playbackControlActions()) {
			playbackMenu.add(new JMenuItem(action) { // FIXME need a standardmenuitem that disables the tooltip like this, very poor show...
				@Override
				public String getToolTipText() {
					return null;
				}
			});
		}
		menuBar.add(playbackMenu);

		audioMenu = new JMenu(resource("menu.audio").name());
		audioMenu.setMnemonic(resource("menu.audio").mnemonic());

		audioDeviceMenu = new AudioDeviceMenu().menu();
		audioMenu.add(audioDeviceMenu);

		audioMenu.add(new JSeparator());
		for (Action action : mediaPlayerActions.audioControlActions()) {
			audioMenu.add(new JMenuItem(action));
		}
		menuBar.add(audioMenu);

		videoMenu = new JMenu(resource("menu.video").name());
		videoMenu.setMnemonic(resource("menu.video").mnemonic());
		videoMenu.add(new JCheckBoxMenuItem(videoFullscreenAction));
		videoMenu.add(new JCheckBoxMenuItem(videoAlwaysOnTopAction));
		videoMenu.add(new JSeparator());
		videoZoomMenu = new JMenu(resource("menu.video.item.zoom").name());
		videoZoomMenu.setMnemonic(resource("menu.video.item.zoom").mnemonic());
		addActions(mediaPlayerActions.videoZoomActions(), videoZoomMenu/*, true*/); // FIXME how to handle zoom 1:1 and fit to window - also, probably should not use addActions to select
		videoMenu.add(videoZoomMenu);

		videoCropMenu = new JMenu(resource("menu.video.item.crop").name());
		videoCropMenu.setMnemonic(resource("menu.video.item.crop").mnemonic());
		addActions(mediaPlayerActions.videoCropActions(), videoCropMenu, true);
		videoMenu.add(videoCropMenu);
		videoMenu.add(new JSeparator());
		videoSnapshot = new JMenuItem(mediaPlayerActions.videoSnapshotAction());
		videoMenu.add(videoSnapshot);
		menuBar.add(videoMenu);

		toolsMenu = new JMenu(resource("menu.tools").name());
		toolsMenu.setMnemonic(resource("menu.tools").mnemonic());
		toolsMenu.add(new JMenuItem(toolsEffectsAction));
		toolsMenu.add(new JMenuItem(toolsMessagesAction));
		toolsMenu.add(new JSeparator());
		toolsMenu.add(new JMenuItem(toolsDebugAction));
		menuBar.add(toolsMenu);

		helpMenu = new JMenu(resource("menu.help").name());
		helpMenu.setMnemonic(resource("menu.help").mnemonic());
		helpMenu.add(new JMenuItem(helpAboutAction));
		menuBar.add(helpMenu);

		setJMenuBar(menuBar);

		videoContentPane = new VideoContentPane();

		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		
		// FIXME the videoContentPane is not working properly then I set permanently the video screen on
        contentPane.add(videoContentPane, BorderLayout.CENTER);
		//contentPane.add(application().getMediaPlayerComponent(), BorderLayout.CENTER);
		contentPane.setTransferHandler(new MediaTransferHandler() {
			@Override
			protected void onMediaDropped(String[] uris) {
				multiMediaPlayerComponent.getMediaPlayer().playMedia(uris[0]);
			}
		});

		setContentPane(contentPane);

		fileChooser = new JFileChooser();

		playlistPane = new PlaylistPane();
		contentPane.add(playlistPane.getGui(), BorderLayout.WEST);

		bottomPane = new JPanel();
		bottomPane.setLayout(new BorderLayout());

		JPanel bottomControlsPane = new JPanel();
		bottomControlsPane.setLayout(new MigLayout("fill, insets 0 n n n", "[grow]", "[]0[]"));

		controlsPane = new ControlsPane(mediaPlayerActions);
		bottomPane.add(bottomControlsPane, BorderLayout.CENTER);
		bottomControlsPane.add(controlsPane, "grow");

		contentPane.add(bottomPane, BorderLayout.SOUTH);

		getActionMap().put(ACTION_EXIT_FULLSCREEN, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				multiMediaPlayerComponent.getMediaPlayer().toggleFullScreen();
				videoFullscreenAction.select(false);
				updateEnabledComponents();
			}
		});

		this.pack();

		applyPreferences();

		mouseMovementDetector = new VideoMouseMovementDetector(multiMediaPlayerComponent.getVideoSurface(), 500, multiMediaPlayerComponent);

		multiMediaPlayerComponent.getMediaPlayer().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {

			@Override
			public void playing(MediaPlayer mediaPlayer) {
				videoContentPane.showVideo();
				updateEnabledComponents();
				application().post(PlayingEvent.INSTANCE);
			}

			@Override
			public void paused(MediaPlayer mediaPlayer) {
				mouseMovementDetector.stop();
				updateEnabledComponents();
				application().post(PausedEvent.INSTANCE);
			}

			@Override
			public void stopped(MediaPlayer mediaPlayer) {
				mouseMovementDetector.stop();
				videoContentPane.showDefault();
				updateEnabledComponents();
				application().post(StoppedEvent.INSTANCE);
			}

			@Override
			public void finished(MediaPlayer mediaPlayer) {
				videoContentPane.showDefault();
				mouseMovementDetector.stop();
				updateEnabledComponents();
				application().post(StoppedEvent.INSTANCE);
			}

			@Override
			public void error(MediaPlayer mediaPlayer) {
				videoContentPane.showDefault();
				mouseMovementDetector.stop();
				updateEnabledComponents();
				application().post(StoppedEvent.INSTANCE);
				JOptionPane.showMessageDialog(MainFrame.this, MessageFormat.format(resources().getString("error.errorEncountered"), fileChooser.getSelectedFile().toString()), resources().getString("dialog.errorEncountered"), JOptionPane.ERROR_MESSAGE);
			}
		});
		
		setMinimumSize(new Dimension(900, 580));
		updateEnabledComponents();
	}

	public void updateDirectoryTree(File newDirectory){
		this.multiMediaPlayerComponent.setMediaDirectory(newDirectory.getAbsolutePath());
		// FIXME the JTree is not updating its self when a recent file or a new open file is used
		//this.playlistPane.updateDirectoryTree(newDirectory);
	}
	
	private void fetchWorkingVideoFolder(ActionEvent e) {
		if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(MainFrame.this)) {
			File newDirectory = fileChooser.getSelectedFile();
			actOpenNewMedia(e, newDirectory);
		}
	}

	public void actOpenNewMedia(ActionEvent e, File selectedFile) {
		File newDirectory = selectedFile;
		while(newDirectory.isFile() || newDirectory.toString().contains("cam") || newDirectory.toString().contains("channel")) {
			newDirectory = newDirectory.getParentFile();
		}
		
		// Update working Media Directory to match the last selection (both from JTree or FileChooser)
		updateDirectoryTree(newDirectory);
		
		// FIXME to show the screens before starting playback, is here the best place for this?
		if(selectedFile.isFile()) { 
			multiMediaPlayerComponent.setSelectedFile(selectedFile.getName());
			if(e != null) mediaPlayerActions.playbackPlayAction().actionPerformed(e);
			updateEnabledComponents();
			application().addRecentMedia(selectedFile.getAbsolutePath());
			showScreens(selectedFile);
		}
	}
	
	private ButtonGroup addActions(List<Action> actions, JMenu menu, boolean selectFirst) {
		ButtonGroup buttonGroup = addActions(actions, menu);
		if (selectFirst) {
			Enumeration<AbstractButton> en = buttonGroup.getElements();
			if (en.hasMoreElements()) {
				StandardAction action = (StandardAction) en.nextElement().getAction();
				action.select(true);
			}
		}
		return buttonGroup;
	}

	private ButtonGroup addActions(List<Action> actions, JMenu menu) {
		ButtonGroup buttonGroup = new ButtonGroup();
		for (Action action : actions) {
			JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(action);
			buttonGroup.add(menuItem);
			menu.add(menuItem);
		}
		return buttonGroup;
	}

	private void applyPreferences() {
		Preferences prefs = Preferences.userNodeForPackage(MainFrame.class);
		setBounds(
				prefs.getInt("frameX"     , 100),
				prefs.getInt("frameY"     , 100),
				prefs.getInt("frameWidth" , 800),
				prefs.getInt("frameHeight", 600)
				);
		boolean alwaysOnTop = prefs.getBoolean("alwaysOnTop", false);
		setAlwaysOnTop(alwaysOnTop);
		videoAlwaysOnTopAction.select(alwaysOnTop);
		fileChooser.setCurrentDirectory(new File(prefs.get("chooserDirectory", ".")));
		
		String recentMedia = prefs.get("recentMedia", "");
		if (recentMedia.length() > 0) {
			List<String> mrls = Arrays.asList(prefs.get("recentMedia", "").split("\\|"));
			Collections.reverse(mrls);
			for (String mrl : mrls) {
				application().addRecentMedia(mrl);
			}
		}
	}

	@Override
	protected void onShutdown() {
		if (wasShown()) {
			Preferences prefs = Preferences.userNodeForPackage(MainFrame.class);
			prefs.putInt    ("frameX"          , getX     ());
			prefs.putInt    ("frameY"          , getY     ());
			prefs.putInt    ("frameWidth"      , getWidth ());
			prefs.putInt    ("frameHeight"     , getHeight());
			prefs.putBoolean("alwaysOnTop"     , isAlwaysOnTop());
			prefs.put       ("chooserDirectory", fileChooser.getCurrentDirectory().toString());

			String recentMedia;
			List<String> mrls = application().recentMedia();
			if (!mrls.isEmpty()) {
				StringBuilder sb = new StringBuilder();
				for (String mrl : mrls) {
					if (sb.length() > 0) {
						sb.append('|');
					}
					sb.append(mrl);
				}
				recentMedia = sb.toString();
			}
			else {
				recentMedia = "";
			}
			prefs.put("recentMedia", recentMedia);
		}
	}

	@Subscribe
	public void onBeforeEnterFullScreen(BeforeEnterFullScreenEvent event) {
		menuBar.setVisible(false);
		bottomPane.setVisible(false);
		// As the menu is now hidden, the shortcut will not work, so register a temporary key-binding
		registerEscapeBinding();
	}

	@Subscribe
	public void onAfterExitFullScreen(AfterExitFullScreenEvent event) {
		deregisterEscapeBinding();
		menuBar.setVisible(true);
		bottomPane.setVisible(true);
	}

	@Subscribe
	public void onSnapshotImage(SnapshotImageEvent event) {
		new SnapshotView(event.image());
	}

	private void registerEscapeBinding() {
		getInputMap().put(KEYSTROKE_ESCAPE, ACTION_EXIT_FULLSCREEN);
		getInputMap().put(KEYSTROKE_TOGGLE_FULLSCREEN, ACTION_EXIT_FULLSCREEN);
	}

	private void deregisterEscapeBinding() {
		getInputMap().remove(KEYSTROKE_ESCAPE);
		getInputMap().remove(KEYSTROKE_TOGGLE_FULLSCREEN);
	}

	private InputMap getInputMap() {
		JComponent c = (JComponent) getContentPane();
		return c.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
	}

	private ActionMap getActionMap() {
		JComponent c = (JComponent) getContentPane();
		return c.getActionMap();
	}

	public File getMediaDirectory() {
		return this.multiMediaPlayerComponent.getMediaDirectory();
	}

	public void updateEnabledComponents() {

		System.out.println("Update Enabled Components");
		
		// FIXME to avoid thread problems sleep a little to give time to other threads to run
		try {
			Thread.sleep(40);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		boolean playerRunning = multiMediaPlayerComponent.isPlayerReady();
		System.out.println("Are There any players running? " + playerRunning);
		
		playbackMenu.setEnabled(playerRunning);
		setEnabledMenuItemVolumeDecrease(0 < multiMediaPlayerComponent.getVolume() && 
				!application().getMediaPlayerComponent().isMuteForced());
		setEnabledMenuItemVolumeIncrease(multiMediaPlayerComponent.getVolume() < 200 && 
				!application().getMediaPlayerComponent().isMuteForced());

		videoFullscreenAction.setEnabled(playerRunning);
		videoZoomMenu.setEnabled(playerRunning);
		videoCropMenu.setEnabled(playerRunning);
		videoSnapshot.setEnabled(playerRunning);
		controlsPane.setEnabledComponents();
		multiMediaPlayerComponent.setVisible(playerRunning);
		
		showScreens(null);
	}

	public void showScreens(File selectedFile) {
		libvlc_state_t state = multiMediaPlayerComponent.getMediaPlayerState();
		System.out.println("showScreens state=" + state);
		if(state != null) {
			switch(state){
			case libvlc_Playing:
				videoContentPane.showVideo();
				//mouseMovementDetector.start();
				application().post(PlayingEvent.INSTANCE);
				break;

			case libvlc_Paused:
				mouseMovementDetector.stop();
				videoContentPane.showVideo();
				application().post(PausedEvent.INSTANCE);
				break;

			case libvlc_Stopped:
				mouseMovementDetector.stop();
				videoContentPane.showDefault();
				application().post(StoppedEvent.INSTANCE);
				break;

			case libvlc_Ended:
				videoContentPane.showDefault();
				mouseMovementDetector.stop();
				application().post(StoppedEvent.INSTANCE);
				break;

			default:
				// libvlc_Buffering
				// libvlc_Error 
				// libvlc_NothingSpecial 
				// libvlc_Opening 
				videoContentPane.showDefault();
				mouseMovementDetector.stop();
				application().post(StoppedEvent.INSTANCE);
				if (selectedFile != null) 
					JOptionPane.showMessageDialog(MainFrame.this, MessageFormat.format(resources().getString("error.errorEncountered"), selectedFile.toString()), resources().getString("dialog.errorEncountered"), JOptionPane.ERROR_MESSAGE);
				break;
			}
		}
		else {
			videoContentPane.showDefault();
			mouseMovementDetector.stop();
			application().post(StoppedEvent.INSTANCE);
		}
	}

	public Component getPlayerHandler(){
		return this.multiMediaPlayerComponent;
	}

	public PlaylistPane getPlaylistPane() {
		return this.playlistPane;
	}

	public ActionListener playbackPlayAction() {
		return this.mediaPlayerActions.playbackPlayAction();
	}

	public void setSpeedRate(int rate) {
		this.controlsPane.setSpeedRate(rate);
	}

	public void setVolumeSlider(int newVolume) {
		this.controlsPane.setVolumeSlider(newVolume);
	}

	public void setEnabledMenuItemVolumeDecrease(boolean b) {
		this.audioMenu.getItem(3).setEnabled(b);
	}

	public void setEnabledMenuItemVolumeIncrease(boolean b) {
		this.audioMenu.getItem(2).setEnabled(b);
	}
}
