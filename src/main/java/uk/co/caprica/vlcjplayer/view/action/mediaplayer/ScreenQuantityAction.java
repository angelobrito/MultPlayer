package uk.co.caprica.vlcjplayer.view.action.mediaplayer;

import static uk.co.caprica.vlcjplayer.Application.application;

import java.awt.event.ActionEvent;

import multiplayer.MultiScreensHandler;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcjplayer.view.action.Resource;

final class ScreenQuantityAction extends MediaPlayerAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3989488518028023534L;
	int screenQuantity;
	
	ScreenQuantityAction(Resource resource, MediaPlayer mediaPlayer, int screenQuantity) {
		super(resource, mediaPlayer);
		this.screenQuantity = screenQuantity;
	}
	
	@Override
    public void actionPerformed(ActionEvent e) {
    	application().setScreenQuantity(this.screenQuantity);
    	application().updateEnabledControlls();
    }
}
