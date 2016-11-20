package fileHandlers;

import static uk.co.caprica.vlcjplayer.Application.application;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import uk.co.caprica.vlcjplayer.view.main.MainFrame;

/**
A basic File Browser.  Requires 1.6+ for the Desktop & SwingWorker
classes, amongst other minor things.

Includes support classes FileTableModel & FileTreeCellRenderer.

@TODO Bugs
<li>Fix keyboard focus issues - especially when functions like
rename/delete etc. are called that update nodes & file lists.
<li>Needs more testing in general.

@TODO Functionality
<li>Double clicking a directory in the table, should update the tree
<li>Move progress bar?
<li>Add other file display modes (besides table) in CardLayout?
<li>Menus + other cruft?
<li>Implement history/back
<li>Allow multiple selection
<li>Add file search

@author Andrew Thompson
@version 2011-06-08
@see http://codereview.stackexchange.com/q/4446/7784
@license LGPL
 */
public class FileBrowser {

	/** Title of the application */
	public static final String APP_TITLE = "FileBro";

	/** Used to open/edit/print files. */

	/** Provides nice icons and names for files. */
	private FileSystemView fileSystemView;

	/** Main GUI container */
	private JPanel gui;

	/** File-system tree. Built Lazily */
	private JTree tree;
	private DefaultTreeModel treeModel;

	/** Directory listing */
	//    private JTable table;
	private JProgressBar progressBar;

	public Container getGui() {
		if (gui==null) {
			gui = new JPanel(new BorderLayout(3,3));
			gui.setBorder(new EmptyBorder(5,5,5,5));

			fileSystemView = FileSystemView.getFileSystemView();

			// the File tree
			DefaultMutableTreeNode root = new DefaultMutableTreeNode();
			treeModel = new DefaultTreeModel(root);

			TreeSelectionListener treeSelectionListener = new TreeSelectionListener() {
				public void valueChanged(TreeSelectionEvent tse){
					DefaultMutableTreeNode node =
							(DefaultMutableTreeNode)tse.getPath().getLastPathComponent();
					showChildren(node);

					// FIXME this node.toString serves as the absolut path to a then could be used in the exat same way as filechooser
					File newSelectedFile = new File(node.toString());
					if(newSelectedFile != null) {
						System.out.println("Wololo node.tostring=" + node.toString());
//						((MainFrame) application().getMainFrame()).actOpenNewMedia(
//								new ActionEvent(this, 0, "treeSelectionEvent"),
//								newSelectedFile);
					}
				}
			};

			// show the file system roots.
			File[] roots = fileSystemView.getRoots();
			for (File fileSystemRoot : roots) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileSystemRoot);
				root.add( node );
				File[] files = fileSystemView.getFiles(fileSystemRoot, true);
				for (File file : files) {
					if (file.isDirectory()) {
						node.add(new DefaultMutableTreeNode(file));
					}
				}
				//
			}

			tree = new JTree(treeModel);
			tree.setRootVisible(false);
			tree.addTreeSelectionListener(treeSelectionListener);
			tree.setCellRenderer(new FileTreeCellRenderer());
			tree.expandRow(0);
			tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			JScrollPane treeScroll = new JScrollPane(tree);

			// as per trashgod tip
			tree.setVisibleRowCount(15);

			Dimension preferredSize = treeScroll.getPreferredSize();
			Dimension widePreferred = new Dimension(
					200,
					(int)preferredSize.getHeight());
			treeScroll.setPreferredSize( widePreferred );

			gui.add(treeScroll, BorderLayout.CENTER);

			JPanel simpleOutput = new JPanel(new BorderLayout(3,3));
			progressBar = new JProgressBar();
			simpleOutput.add(progressBar, BorderLayout.EAST);
			progressBar.setVisible(false);

			gui.add(simpleOutput, BorderLayout.SOUTH);
		}
		return gui;
	}

	public void showRootFile() {
		// ensure the main files are displayed
		tree.setSelectionInterval(0,0);
	}

	public void showThrowable(Throwable t) {
		t.printStackTrace();
		JOptionPane.showMessageDialog(
				gui,
				t.toString(),
				t.getMessage(),
				JOptionPane.ERROR_MESSAGE
				);
		gui.repaint();
	}

	/** Add the files that are contained within the directory of this node.
    Thanks to Hovercraft Full Of Eels for the SwingWorker fix. */
	public void showChildren(final DefaultMutableTreeNode node) {
		tree.setEnabled(false);
		progressBar.setVisible(true);
		progressBar.setIndeterminate(true);

		SwingWorker<Void, File> worker = new SwingWorker<Void, File>() {
			@Override
			public Void doInBackground() {
				File file = (File) node.getUserObject();
				if (file.isDirectory()) {
					File[] files = fileSystemView.getFiles(file, true); //!!
					List<File> videoFiles = new ArrayList<File>();
					if (node.isLeaf()) {

						// First run once to include all Folders
						for (File child : files) {
							if (child.isDirectory()) {
								publish(child);
							}

							// Also include supported Video files
							else if(isValidVideoFile(child)) {
								videoFiles.add(child);
							}
						}

						// Then run twice to include just video files
						for (File child : videoFiles) publish(child);
					}
				}
				return null;
			}

			@Override
			protected void process(List<File> chunks) {
				for (File child : chunks) {
					node.add(new DefaultMutableTreeNode(child));
				}
			}

			@Override
			protected void done() {
				progressBar.setIndeterminate(false);
				progressBar.setVisible(false);
				tree.setEnabled(true);
			}
		};
		worker.execute();
	}

	/** Add the files that are contained within the directory of this node.
    Thanks to Hovercraft Full Of Eels for the SwingWorker fix. */
	public void navitageToDirectory(File newDirectoryPath) {
		tree.setEnabled(false);
		progressBar.setVisible(true);
		progressBar.setIndeterminate(true);

		DefaultMutableTreeNode lastLeaf = new DefaultMutableTreeNode(newDirectoryPath);
		TreePath path = new TreePath(lastLeaf.getPath());
		tree.setSelectionPath(path);

		progressBar.setIndeterminate(false);
		progressBar.setVisible(false);
		tree.setEnabled(true);
		findTreePath(newDirectoryPath);
	}

	protected DefaultMutableTreeNode buildNodeFromString(File goal)
	{
		DefaultMutableTreeNode root = null;
		File fileWalker = goal;
		do {
			fileWalker = fileWalker.getParentFile();
		} while (fileWalker.getParentFile() != null);
		root = new DefaultMutableTreeNode(fileWalker);
		return root;        
	}

	//	public static void main(String[] args) {
	//		SwingUtilities.invokeLater(new Runnable() {
	//			public void run() {
	//				try {
	//					// Significantly improves the look of the output in
	//					// terms of the file names returned by FileSystemView!
	//					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	//				} catch(Exception weTried) {
	//				}
	//				JFrame f = new JFrame(APP_TITLE);
	//				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	//
	//				FileBrowser FileBrowser = new FileBrowser();
	//				f.setContentPane(FileBrowser.getGui());
	//
	//				try {
	//					URL urlBig = FileBrowser.getClass().getResource("fb-icon-32x32.png");
	//					URL urlSmall = FileBrowser.getClass().getResource("fb-icon-16x16.png");
	//					ArrayList<Image> images = new ArrayList<Image>();
	//					images.add( ImageIO.read(urlBig) );
	//					images.add( ImageIO.read(urlSmall) );
	//					f.setIconImages(images);
	//				} catch(Exception weTried) {
	//					System.err.println(weTried.toString());
	//				}
	//
	//				f.pack();
	//				f.setLocationByPlatform(true);
	//				f.setMinimumSize(f.getSize());
	//				f.setVisible(true);
	//
	//				FileBrowser.showRootFile();
	//			}
	//		});
	//	}

	public TreePath findTreePath(File find) {
		//    	System.out.println("findTreePath find=" + find.toString());
		for (int ii=0; ii < tree.getRowCount(); ii++) {
			TreePath treePath = tree.getPathForRow(ii);
			Object object = treePath.getLastPathComponent();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) object;
			File nodeFile = (File) node.getUserObject();

			//            System.out.println("findTreePath nodeFile[" + ii + "/" + tree.getRowCount() + "]=" + nodeFile.toString());
			if (nodeFile==find) {
				return treePath;
			}
		}
		// not found!
		return null;
	}

	private boolean isValidVideoFile(File file){

		String fileName;      
		String fileExtension = ""; 
		fileName = file.getName();

		if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
			fileExtension = fileName.substring(fileName.lastIndexOf(".")+1);
		}

		// FIXME use a videoFileFilter from the VLCJ library to better do this?
		if(fileExtension.equalsIgnoreCase("avi")) return true;
		else if(fileExtension.equalsIgnoreCase("mp4")) return true;
		else if(fileExtension.equalsIgnoreCase("rmvb")) return true;
		else if(fileExtension.equalsIgnoreCase("mkv")) return true;
		else if(fileExtension.equalsIgnoreCase("flv")) return true;
		else if(fileExtension.equalsIgnoreCase("vob")) return true;
		else if(fileExtension.equalsIgnoreCase("mov")) return true;
		else if(fileExtension.equalsIgnoreCase("wmv")) return true;
		//else if(fileExtension.equalsIgnoreCase("")) return true;
		else return false;
	}
}
