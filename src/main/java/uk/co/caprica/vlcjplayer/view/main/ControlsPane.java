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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.google.common.eventbus.Subscribe;

import net.miginfocom.swing.MigLayout;
import uk.co.caprica.vlcj.binding.LibVlcConst;
import uk.co.caprica.vlcjplayer.event.PausedEvent;
import uk.co.caprica.vlcjplayer.event.PlayingEvent;
import uk.co.caprica.vlcjplayer.event.ShowEffectsEvent;
import uk.co.caprica.vlcjplayer.event.StoppedEvent;
import uk.co.caprica.vlcjplayer.view.BasePanel;
import uk.co.caprica.vlcjplayer.view.action.mediaplayer.MediaPlayerActions;
import uk.co.caprica.vlcjplayer.view.image.ImagePane;

final class ControlsPane extends BasePanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2868260151135033903L;

	private final Icon playIcon = newIcon("play");

	private final Icon pauseIcon = newIcon("pause");

	private final Icon fullscreenIcon = newIcon("fullscreen");

	private final Icon extendedIcon = newIcon("extended");

	private final Icon volumeHighIcon = newIcon("volume-high");

	private final Icon volumeMutedIcon = newIcon("volume-muted");

	private final JButton playPauseButton;

	private final JButton stopButton;

	private final JButton fullscreenButton;

	private final JButton extendedButton;

	private final JButton snapshotButton;

	private final JButton muteButton;

	private final JSlider volumeSlider;

	private static final int FPS_MIN = 1;

	private static final int FPS_MAX = 5;

	private final JSlider speedSlider;

	private final JLabel speedLabel;

	private final ImagePane logoPane;

	private final PositionPane positionPane;

	ControlsPane(MediaPlayerActions mediaPlayerActions) {

		this.setMinimumSize(new Dimension(1200, 100));
		//this.setMaximumSize(new Dimension(1200, 100));

		positionPane = new PositionPane(application().getMediaPlayerComponent());

		playPauseButton = new BigButton();
		playPauseButton.setAction(mediaPlayerActions.playbackPlayAction());

		stopButton = new StandardButton();
		stopButton.setAction(mediaPlayerActions.playbackStopAction());

		fullscreenButton = new StandardButton();
		fullscreenButton.setIcon(fullscreenIcon);

		extendedButton = new StandardButton();
		extendedButton.setIcon(extendedIcon);

		snapshotButton = new StandardButton();
		snapshotButton.setAction(mediaPlayerActions.videoSnapshotAction());

		muteButton = new StandardButton();
		muteButton.setIcon(volumeHighIcon);

		volumeSlider = new JSlider();
		volumeSlider.setMinimum(LibVlcConst.MIN_VOLUME);
		volumeSlider.setMaximum(LibVlcConst.MAX_VOLUME);
		volumeSlider.setValue(LibVlcConst.MAX_VOLUME/2);


		speedLabel = new JLabel("Velocidade: ");

		speedSlider = new JSlider(JSlider.HORIZONTAL, FPS_MIN, FPS_MAX, 3);
		speedSlider.setMajorTickSpacing(1);
		speedSlider.setMinorTickSpacing(1);
		speedSlider.setPaintTicks(true);
		speedSlider.setPaintLabels(true);
		speedSlider.setName("speedRate");

		// FIXME Fix the layout of the Controll pane its ugly
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put( new Integer( 1 ), new JLabel("x1/4") );
		labelTable.put( new Integer( 2 ), new JLabel("x1/2") );
		labelTable.put( new Integer( 3 ), new JLabel("x1") );
		labelTable.put( new Integer( 4 ), new JLabel("x2") );
		labelTable.put( new Integer( 5 ), new JLabel("x4") );
		speedSlider.setLabelTable(labelTable);

		MigLayout layout = new MigLayout("fill, insets 0 0 0 0", "[]12[]10[]10[]12[]10[]12[]push[]10[]", "[]"); 
		setLayout(layout);

		logoPane = new ImagePane(ImagePane.Mode.FIT, getClass().getResource("/MultTecnologia-logo-name.png"), 1.0f);
		logoPane.setBackground(new Color(Color.TRANSLUCENT));
		logoPane.setPreferredSize(new Dimension(50, 50));
		logoPane.setIgnoreRepaint(true);
		add(logoPane, "West, wmax 120, hmax 80, gap 20 0");

		add(positionPane, "North, gap 30");

		add(playPauseButton, "Center, sg 2, al left, gap 30");
		add(stopButton, "Center, sg 2, al left, gap 5");

		add(fullscreenButton, "Center, sg 2, al left, gap 5");

		add(snapshotButton, "Center, sg 2, al left, gap 5");
		add(extendedButton, "Center, sg 2, al left, gap 5");


		add(speedLabel, "sg 2, al left, gap 5");
		add(speedSlider, "Center, sg 1, wmax 150, hmax 80, al left, gap 5");
		add(muteButton, "Center, sg 2, al left, gap 5");
		add(volumeSlider, "Center, sg 1, wmax 150, hmax 80, al center center");

		registerListeners();
		setEnabledComponents();
	}

	private void registerListeners() {
		speedSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				application().getMediaPlayerComponent().setRate( (float) Math.pow((double)2, (double)speedSlider.getValue())/8);
			}
		});

		volumeSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				application().getMediaPlayerComponent().setVolume(volumeSlider.getValue());
				((MainFrame) application().getMainFrame()).updateEnabledComponents();
			}
		});

		// FIXME really these should share common actions
		muteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MuteAction();
			}
		});

		fullscreenButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				application().getMediaPlayerComponent().getMediaPlayer().toggleFullScreen();
			}
		});

		extendedButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				application().post(ShowEffectsEvent.INSTANCE);
			}
		});
	}

	public void setSpeedRate(int newSpeed){
		this.speedSlider.setValue(newSpeed);
	}

	public void setVolumeSlider(int newVolume) {
		this.volumeSlider.setValue(newVolume);
	}

	@Subscribe
	public void onPlaying(PlayingEvent event) {
		playPauseButton.setIcon(pauseIcon); // FIXME best way to do this? should be via the action really?
	}

	@Subscribe
	public void onPaused(PausedEvent event) {
		playPauseButton.setIcon(playIcon); // FIXME best way to do this? should be via the action really?
	}

	@Subscribe
	public void onStopped(StoppedEvent event) {
		playPauseButton.setIcon(playIcon); // FIXME best way to do this? should be via the action really?
	}

	private class BigButton extends JButton {

		/**
		 * 
		 */
		private static final long serialVersionUID = 8144036361040943211L;

		private BigButton() {
			setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			setHideActionText(true);
		}
	}

	private class StandardButton extends JButton {

		/**
		 * 
		 */
		private static final long serialVersionUID = -2407454848235839869L;

		private StandardButton() {
			setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
			setHideActionText(true);
		}
	}

	private Icon newIcon(String name) {
		return new ImageIcon(getClass().getResource("/icons/buttons/" + name + ".png"), name);
	}

	private void MuteAction() {
		application().getMediaPlayerComponent().forceMute();
		((MainFrame) application().getMainFrame()).updateEnabledComponents();
	}

	public void setEnabledComponents() {
		boolean newState = application().getMediaPlayerComponent().isPlayerReady();
		if(newState){
			if(application().getMediaPlayerComponent().isPaused()) onPaused(new PausedEvent());
			else onPlaying(new PlayingEvent());
		}
		else onStopped(new StoppedEvent());
		positionPane.setEnabled(newState);
		stopButton.setEnabled(newState);
		fullscreenButton.setEnabled(newState);
		extendedButton.setEnabled(newState);
		snapshotButton.setEnabled(newState);
		speedSlider.setEnabled(newState);
		volumeSlider.setEnabled(!application().getMediaPlayerComponent().isMuteForced());
		if(application().getMediaPlayerComponent().isMuteForced()) muteButton.setIcon(volumeMutedIcon);
		else muteButton.setIcon(volumeHighIcon);
	}
}
