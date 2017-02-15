package uk.co.caprica.vlcjplayer.view.action.mediaplayer;

import static uk.co.caprica.vlcjplayer.Application.application;

import java.awt.event.ActionEvent;

import multiplayer.MultiScreensHandler;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcjplayer.view.action.Resource;
import uk.co.caprica.vlcjplayer.view.main.MainFrame;

final class RecordAction extends MediaPlayerAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2720851085587740008L;

	RecordAction(Resource resource, MediaPlayer mediaPlayer) {
		super(resource, mediaPlayer);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		((MainFrame) application().getMainFrame()).record();
		application().updateEnabledControlls();
	}
}