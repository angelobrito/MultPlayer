package MixTrackerPlayer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import fileHandlers.CreateChildNodes;
import fileHandlers.FileNode;
import uk.co.caprica.vlcj.runtime.streams.NativeStreams;

public class MixTrackerFrame extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 931147812080763308L;
    private static final NativeStreams nativeStreams;

    // Redirect the native output streams to files, useful since VLC can generate a lot of noisy native logs we don't care about
    // (on the other hand, if we don't look at the logs we might won't see errors)
    static {
//        if (RuntimeUtil.isNix()) {
//            nativeStreams = new NativeStreams("stdout.log", "stderr.log");
//        }
//        else {
            nativeStreams = null;
//        }
    }
    
	private MixTrackerScreenHandler player;
	private int screensQtt;

	private JMenuBar  menuBar;
	private JMenu     fileMenu;
	private JMenuItem openFolder;
	private JMenuItem save;
	private JMenuItem exit;
	private JMenu     helpMenu;
	private JMenuItem about;

	private JScrollPane   playlistPanel;
	private JList<String> playlist;
	private File          workingDir;

	private JPanel controlPanel;
	private JButton playButton;
	private JButton stopButton;
	private JButton recordButton;

	public MixTrackerFrame() {

		// Set Player Main Window properties
		this.setSize(1200, 800);
		this.setTitle("MixTracker Camera Player");
		this.setMinimumSize(new Dimension(600, 400));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.menuBar  = new JMenuBar();
		this.fileMenu = new JMenu("Arquivo");

		// TODO Include Video Folder Icon
		this.openFolder = new JMenuItem("Abrir Pasta", new ImageIcon(""));
		this.openFolder.addActionListener(this);
		this.fileMenu.add(this.openFolder);

		//TODO include Disket Icon
		this.save = new JMenuItem("Salvar Grava��o", new ImageIcon(""));
		this.save.addActionListener(this);
		this.fileMenu.add(this.save);
		this.fileMenu.addSeparator();

		//TODO include exit Icon
		this.exit = new JMenuItem("Sair", new ImageIcon(""));
		this.exit.addActionListener(this);
		this.fileMenu.add(this.exit);

		this.menuBar.add(this.fileMenu);

		// TODO include Player Logo
		this.helpMenu = new JMenu("Ajuda");
		this.about = new JMenuItem("Sobre", new ImageIcon(""));
		this.about.addActionListener(this);
		this.helpMenu.add(this.about);
		this.menuBar.add(this.helpMenu);
		this.setJMenuBar(this.menuBar);

		this.controlPanel  = new JPanel();
		this.controlPanel.setLayout(new BoxLayout(this.controlPanel, BoxLayout.Y_AXIS));
		this.controlPanel.setMinimumSize(new Dimension(300, 100));

		this.playlistPanel = new JScrollPane();
		this.playlistPanel.setBackground(Color.WHITE);
		this.playlistPanel.setMinimumSize(new Dimension(200, 100));

		updateWorkingDirTree("");

		this.playlist = new JList<String>();
		this.playlistPanel.add(this.playlist);
		this.controlPanel.add(this.playlistPanel);

		this.playButton = new JButton("Play");
		this.playButton.addActionListener(this);

		this.stopButton = new JButton("Stop");
		this.stopButton.addActionListener(this);

		this.recordButton = new JButton("REC");
		this.recordButton.addActionListener(this);

		// Adding the Buttons to the control panel
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.add(this.playButton);
		buttonsPanel.add(this.stopButton);
		buttonsPanel.add(this.recordButton);
		buttonsPanel.setMinimumSize(new Dimension(100, 50));
		this.controlPanel.add(buttonsPanel);
		this.add("West", this.controlPanel);

		// TODO For now it only supports 4 screens
		this.screensQtt = 4; 
		this.player = new MixTrackerScreenHandler(this.screensQtt);
		this.player.setMinimumSize(new Dimension(800, 600));
		this.add(this.player);
		this.pack();
		this.setVisible(true);
	}

	private void updateWorkingDirTree(String workingDirectoryPath) {

		this.workingDir = new File(workingDirectoryPath);
		DefaultMutableTreeNode treeRoot = new DefaultMutableTreeNode(new FileNode(this.workingDir));
		TreeModel treeModel = new DefaultTreeModel(treeRoot);
		JTree tree = new JTree(treeModel);
		tree.setShowsRootHandles(true);
		tree.setMinimumSize(new Dimension(100, 0));
		this.playlistPanel.setViewportView(tree);

		CreateChildNodes cnn = new CreateChildNodes(workingDir, treeRoot);
		new Thread(cnn).start();
		tree.expandRow(treeRoot.getLevel()+1);
	}

	// TODO implement a smart search algorithm to grab the videos inside the folders
	public String getMediaDirectory() {

		// Fetch the Video folder (it depends on user interaction after the click Open Folder)
		JFileChooser ourFileSelector = new JFileChooser();
		ourFileSelector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		ourFileSelector.showSaveDialog(null);
		this.player.setNewMediaDirectory(ourFileSelector.getSelectedFile().getAbsolutePath());
		System.out.println("Videos Path: " + this.player.getMediaDirectory().getAbsolutePath());
		return this.player.getMediaDirectory().getAbsolutePath();
	}

	public void setSelectedScreen(int screenNumber) {
		this.player.activateScreen(screenNumber);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

		String action = arg0.getActionCommand();
		System.out.println("Action Performed:" + action);

		// MenuItem Open
		if(action.equals("Abrir Pasta")) {
			this.updateWorkingDirTree(this.getMediaDirectory());
			System.out.println("Fetched Root directory: {" + this.workingDir.getAbsolutePath() + "}");
		}

		// MenuItem Save 
		else if(action.equals("")) {

			//TODO implement this action
		}

		// MenuItem Exit
		else if(action.equals("Sair")) {

			System.exit(0);
		}

		// MenuItem About
		else if(action.equals("")) {

			//TODO implement this action
		}

		// Button Play
		else if(action.equals("Play")) {

			this.player.resume();
			this.playButton.setText("Pause");
			System.out.println("play action");
		}

		// Button Pause
		else if(action.equals("Pause")) {

			//TODO implement this action
			this.playButton.setText("Play");
			this.player.pause();
		}

		// Button Stop
		else if(action.equals("Stop")) {

			this.player.stop();
		}

		// Button Record
		else if(action.equals("Salvar Grava��o")) {

			//TODO implement this action
		}

		// Unidentified action
		else {
			System.out.println("Unrecognized Action? WTF?!"); // TODO
		}
	}

	public static void main(String[] args) {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			new MixTrackerFrame();
		} 
		catch (ClassNotFoundException e) {
			
			e.printStackTrace();
		}
		catch (InstantiationException e) {
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

	}
}
