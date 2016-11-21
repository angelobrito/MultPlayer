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
import static uk.co.caprica.vlcjplayer.time.Time.formatTime;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.google.common.eventbus.Subscribe;

import multiplayer.MultiScreensHandler;
import net.miginfocom.swing.MigLayout;
import uk.co.caprica.vlcjplayer.event.TickEvent;
import uk.co.caprica.vlcjplayer.view.StandardLabel;

final class PositionPane extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8157822719326602228L;

	private final JLabel timeLabel;

    private final JSlider positionSlider;

    private final JLabel durationLabel;

    private long time;

    private final MultiScreensHandler player;

    private final AtomicBoolean sliderChanging = new AtomicBoolean();

    private final AtomicBoolean positionChanging = new AtomicBoolean();
    
    private boolean mousePressedPlaying = false;

    PositionPane(MultiScreensHandler mediaPlayer) {
        this.player = mediaPlayer;

        timeLabel = new StandardLabel("hh:mm:ss");
        timeLabel.setText("-:--:--");

        UIManager.put("Slider.paintValue", false); // FIXME how to do this for a single component?

        positionSlider = new JSlider();
        positionSlider.setMinimum(0);
        positionSlider.setMaximum(1000);
        positionSlider.setValue(0);
        positionSlider.setToolTipText("Position");
        
        durationLabel = new StandardLabel("hh:mm:ss");
        durationLabel.setText("-:--:--");

        positionSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!positionChanging.get()) {
                    JSlider source = (JSlider) e.getSource();
                    if (source.getValueIsAdjusting()) {
                        sliderChanging.set(true);
                    }
                    else {
                        sliderChanging.set(false);
                    }
                    mediaPlayer.setPosition(source.getValue() / 1000.0f);
                }
            }
        });

        setLayout(new MigLayout("fill, insets 0 0 0 0", "[][grow][]", "[]"));

        add(timeLabel, "shrink");
        add(positionSlider, "grow");
        add(durationLabel, "shrink");

        application().subscribe(this);
    }

    private void refresh() {

    	this.updateTime();

        if (!sliderChanging.get()) {
            int value = (int) (player.getPosition() * 1000.0f);
            positionChanging.set(true);
            positionSlider.setValue(value);
            positionChanging.set(false);
        }
    }

    private void updateTime() {
        this.setTime(this.player.getTime());
        this.setDuration(this.player.getLength());
    }

    public void setTime(long time) {
        this.time = time;
        this.timeLabel.setText(formatTime(time));
    }

    public void setDuration(long duration) {
        durationLabel.setText(formatTime(duration));
    }

    @Subscribe
    public void onTick(TickEvent tick) {
        refresh();
    }
    
    @Override
    public void setEnabled(boolean enabled){
    	super.setEnabled(enabled);
    	positionSlider.setEnabled(enabled);
    }    
}
