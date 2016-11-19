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

package uk.co.caprica.vlcjplayer.view.action.mediaplayer;

import java.awt.event.ActionEvent;

import static uk.co.caprica.vlcjplayer.Application.application;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcjplayer.view.action.Resource;
import uk.co.caprica.vlcjplayer.view.main.MainFrame;

final class VolumeAction extends MediaPlayerAction {

    private final int delta;

    VolumeAction(Resource resource, MediaPlayer mediaPlayer, int delta) {
        super(resource, mediaPlayer);
        this.delta = delta;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    	int newVolume = mediaPlayer.getVolume() + delta;
        if(newVolume < 0) newVolume = 0; 
        else if(newVolume > 200) newVolume = 200;

        ((MainFrame) application().getMainFrame()).setVolumeSlider(newVolume);
//    	System.out.println("Menu newVolume=" + newVolume + ", delta=" + delta + ", action e=" + e);
    }
}
