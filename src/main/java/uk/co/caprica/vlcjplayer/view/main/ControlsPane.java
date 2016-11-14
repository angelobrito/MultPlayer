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
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.layout.AC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import uk.co.caprica.vlcj.binding.LibVlcConst;
import uk.co.caprica.vlcjplayer.event.PausedEvent;
import uk.co.caprica.vlcjplayer.event.PlayingEvent;
import uk.co.caprica.vlcjplayer.event.ShowEffectsEvent;
import uk.co.caprica.vlcjplayer.event.StoppedEvent;
import uk.co.caprica.vlcjplayer.view.BasePanel;
import uk.co.caprica.vlcjplayer.view.action.mediaplayer.MediaPlayerActions;
import uk.co.caprica.vlcjplayer.view.image.ImagePane;

import com.google.common.eventbus.Subscribe;

final class ControlsPane extends BasePanel {

	private final Icon playIcon = newIcon("play");

	private final Icon pauseIcon = newIcon("pause");

	private final Icon previousIcon = newIcon("previous");

	private final Icon nextIcon = newIcon("next");

	private final Icon fullscreenIcon = newIcon("fullscreen");

	private final Icon extendedIcon = newIcon("extended");

	private final Icon snapshotIcon = newIcon("snapshot");

	private final Icon volumeHighIcon = newIcon("volume-high");

	private final Icon volumeMutedIcon = newIcon("volume-muted");

	private final JButton playPauseButton;

	private final JButton previousButton;

	private final JButton stopButton;

	private final JButton nextButton;

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

	ControlsPane(MediaPlayerActions mediaPlayerActions) {

		playPauseButton = new BigButton();
		playPauseButton.setAction(mediaPlayerActions.playbackPlayAction());

		previousButton = new StandardButton();
		previousButton.setIcon(previousIcon);

		stopButton = new StandardButton();
		stopButton.setAction(mediaPlayerActions.playbackStopAction());

		nextButton = new StandardButton();
		nextButton.setIcon(nextIcon);

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


		speedLabel = new JLabel("Velocidade: ");

		speedSlider = new JSlider(JSlider.HORIZONTAL, FPS_MIN, FPS_MAX, 3);
		speedSlider.setMajorTickSpacing(10);
		speedSlider.setMinorTickSpacing(1);
		speedSlider.setPaintTicks(true);
		speedSlider.setPaintLabels(true);
		speedSlider.setName("Velocidade");

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
		logoPane.setPreferredSize(new Dimension(50, 50));
		logoPane.setBackground(Color.WHITE);
		add(logoPane, "wmax 120, hmax 80");
		
		add(playPauseButton);
		add(previousButton, "sg 1");
		add(stopButton, "sg 1");
		add(nextButton, "sg 1");

		add(fullscreenButton, "sg 1");
		add(extendedButton, "sg 1");

		add(snapshotButton, "sg 1");

		add(speedLabel, "sg 2");
		add(speedSlider, "wmax 150, hmax 80, al center center");
		speedSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				//application().mediaPlayerComponent().getMediaPlayer().setVolume(volumeSlider.getValue());
				System.out.println("speedSlider=" + speedSlider.getValue());
			}
		});

		add(muteButton, "sg 1");
		add(volumeSlider, "wmax 150, hmax 80, al center center");

		volumeSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				application().mediaPlayerComponent().getMediaPlayer().setVolume(volumeSlider.getValue());
			}
		});

		// FIXME really these should share common actions
		muteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				application().mediaPlayerComponent().forceMute();
				if(application().mediaPlayerComponent().getMediaPlayer().isMute()) muteButton.setIcon(volumeMutedIcon);
				else muteButton.setIcon(volumeHighIcon);
			}
		});

		fullscreenButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				application().mediaPlayerComponent().getMediaPlayer().toggleFullScreen();
			}
		});

		extendedButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				application().post(ShowEffectsEvent.INSTANCE);
			}
		});
	}

	public void setSpeedValue(int newSpeed){
		this.speedSlider.setValue(newSpeed);
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

		private BigButton() {
			setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			setHideActionText(true);
		}
	}

	private class StandardButton extends JButton {

		private StandardButton() {
			setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
			setHideActionText(true);
		}
	}

	private Icon newIcon(String name) {
		return new ImageIcon(getClass().getResource("/icons/buttons/" + name + ".png"));
	}

	public void setEnabledComponents(boolean newState) {
		previousButton.setEnabled(newState);
		stopButton.setEnabled(newState);
		nextButton.setEnabled(newState);
		fullscreenButton.setEnabled(newState);
		extendedButton.setEnabled(newState);
		snapshotButton.setEnabled(newState);
		speedSlider.setEnabled(newState);

	}
}
