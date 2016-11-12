package MixTrackerPlayer;

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
	private Border contourLine;

	private TitledBorder titled;

	public Screen(String screenTitle, String mediaPath){
		super();

		//A border that puts 10 extra pixels at the sides and
		//bottom of each pane.
		contourLine = BorderFactory.createLineBorder(Color.WHITE);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBackground(Color.BLACK);
		//super.canvas.setBackground(Color.BLACK);
		//this.add(videoCanvas, BorderLayout.CENTER);
		
		titled = BorderFactory.createTitledBorder(contourLine, this.screenTitle);
		addCompForTitledBorder(titled,
				"Screen Holder for " + this.screenTitle,
				TitledBorder.CENTER,
				TitledBorder.DEFAULT_POSITION,
				this);
		
		this.addMouseListener(this);
	}
	

	public void activateScreen() {
		this.titled.setTitleColor(Color.YELLOW);
		System.out.println("Activated?");
	}
	
	public void deactivateScreen() {
		this.titled.setTitleColor(Color.WHITE);
		System.out.println("Deactivated?");
	}
	
	void addCompForTitledBorder(TitledBorder border,
			String description,
			int justification,
			int position,
			Container container) {
		border.setTitleJustification(justification);
		border.setTitlePosition(position);
		border.setTitleColor(Color.WHITE);
		addCompForBorder(border, description,
				container);
	}

	void addCompForBorder(Border border,
			String description,
			Container container) {
		JPanel comp = new JPanel(new GridLayout(1, 1), false);
		comp.setBackground(Color.BLACK);
		//comp.add(this.getMediaPlayer()); TODO
		comp.setBorder(border);
		comp.setName(description);
		container.add(comp);
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
		System.out.println("MouseEvent=" + "Pressed on Screen" + this.screenTitle);
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		System.out.println("MouseEvent=" + "Released Screen" + this.screenTitle);
		// TODO Auto-generated method stub
	}

	public void setNewMedia(String newMediaPath) {
		this.mediaPath = newMediaPath;
	}
}
