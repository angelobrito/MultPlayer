package uk.co.caprica.vlcjplayer.view.main;

import static uk.co.caprica.vlcjplayer.Application.application;

import java.awt.CardLayout;

import javax.swing.JPanel;

import uk.co.caprica.vlcjplayer.view.image.ImagePane;

final class VideoContentPane extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = -6221988282365733223L;

	private static final String NAME_DEFAULT = "default";

    private static final String NAME_VIDEO = "video";

    private final CardLayout cardLayout;
    
    private final ImagePane  videoBackground;

    VideoContentPane() {
        cardLayout = new CardLayout();
        setLayout(cardLayout);
        
        videoBackground = new ImagePane(
        		ImagePane.Mode.CENTER, 
				getClass().getResource("/MultTecnologia-logo.png"), 
				0.3f);
        
        add(videoBackground, NAME_DEFAULT);
        add(application().getMediaPlayerComponent(), NAME_VIDEO);
    }

    public void showDefault() {
    	System.out.println("VideoContentPane.showDefault()");
    	videoBackground.setVisible(true);
        cardLayout.show(this, NAME_DEFAULT);
    }

    public void showVideo() {
    	System.out.println("VideoContentPane.showVideo()");
    	videoBackground.setVisible(false);
        cardLayout.show(this, NAME_VIDEO);
    }
}
