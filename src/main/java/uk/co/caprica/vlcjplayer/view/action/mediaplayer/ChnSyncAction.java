package uk.co.caprica.vlcjplayer.view.action.mediaplayer;

import static uk.co.caprica.vlcjplayer.Application.application;

import java.awt.event.ActionEvent;

import multiplayer.MultiScreensHandler;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcjplayer.view.action.Resource;

final class ChnSyncAction extends MediaPlayerAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3989488518028023534L;
	int chnSyncThreshold;
	
	ChnSyncAction(Resource resource, MediaPlayer mediaPlayer, int chnSyncThreshold) {
		super(resource, mediaPlayer);
		this.chnSyncThreshold = chnSyncThreshold;
	}
	
	@Override
    public void actionPerformed(ActionEvent e) {
    	application().setChnSyncThreshold(this.chnSyncThreshold);
    	application().updateEnabledControlls();
    }
}
