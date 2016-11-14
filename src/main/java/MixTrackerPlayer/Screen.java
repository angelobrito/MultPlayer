package MixTrackerPlayer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;

public class Screen extends EmbeddedMediaPlayerComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1699731673362485129L;

	private String screenTitle = "Cam 0";

	private String mediaPath = "";

	//Keep references to the next few borders,
	//for use in titles and compound borders.
	private Border contourLineWhite;
	private Border contourLineYellow;

	private TitledBorder titled;

	public Screen(String screenTitle, String mediaPath){
		super();
		
		this.screenTitle = screenTitle;
		this.mediaPath   = mediaPath;
		
		//A border that puts 10 extra pixels at the sides and
		//bottom of each pane.
		contourLineWhite = BorderFactory.createLineBorder(Color.WHITE);
		contourLineYellow = BorderFactory.createLineBorder(Color.YELLOW);
		
		this.setLayout(new GridLayout(1,0));
		this.setBackground(Color.BLACK);
		super.onGetCanvas().setBackground(Color.BLACK);

	    this.deactivateScreen();
		
		this.addMouseListener(this);
	}

	public void activateScreen() {
		this.titled = BorderFactory.createTitledBorder(contourLineYellow, this.screenTitle);
		this.titled.setTitleColor(Color.YELLOW);

		super.getMediaPlayer().mute();
		System.out.println("Activated?");
	}
	
	public void deactivateScreen() {
		this.titled = BorderFactory.createTitledBorder(contourLineWhite, this.screenTitle);
		this.titled.setTitleColor(Color.WHITE);

		super.getMediaPlayer().mute();
		System.out.println("Deactivated?");
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createImageIcon(String path,
			String description) {
		java.net.URL imgURL = Screen.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println("MouseEvent=" + "Clicked #" + e.getClickCount());
		if(e.getClickCount() > 1) { 
			// TODO maximize this screen
			if(e.getButton() == MouseEvent.BUTTON1) this.activateScreen();
			else this.deactivateScreen();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		System.out.println("MouseEvent=" + "Entered Screen" + this.screenTitle);
		this.getMediaPlayer().mute();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		System.out.println("MouseEvent=" + "Leave Screen" + this.screenTitle);
		this.getMediaPlayer().mute();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// FIXME Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// FIXME Auto-generated method stub

	}

	public void setNewMedia(String newMediaPath) {
		this.mediaPath = newMediaPath;
	}
	
	public void start(){
		this.getMediaPlayer().playMedia(this.mediaPath); 
	}
}
