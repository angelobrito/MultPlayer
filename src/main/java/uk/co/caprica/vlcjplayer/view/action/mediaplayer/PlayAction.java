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

import static uk.co.caprica.vlcjplayer.Application.application;

import java.awt.event.ActionEvent;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.w3c.dom.events.EventException;

import MixTrackerPlayer.MixTrackerScreenHandler;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcjplayer.view.action.Resource;

final class PlayAction extends MediaPlayerAction {

	PlayAction(Resource resource, MediaPlayer mediaPlayer) {
		super(resource, mediaPlayer);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("Play Action:" + e.toString());
		MixTrackerScreenHandler mediaPlayerComponent = application().getMediaPlayerComponent();
		mediaPlayerComponent.resume();
		mediaPlayerComponent.getSelectedScreen().start();
//		FIXME UPDATE buttons and menuItems
//		application().getMainFrame().updateEnabledComponents();
	}
}
