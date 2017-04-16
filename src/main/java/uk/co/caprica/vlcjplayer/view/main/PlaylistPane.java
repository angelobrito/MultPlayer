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

package uk.co.caprica.vlcjplayer.view.main;

import static uk.co.caprica.vlcjplayer.Application.application;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.net.SyslogAppender;

import fileHandlers.FileBrowser;
import fileHandlers.FileTreeCellRenderer;

public class PlaylistPane extends FileBrowser {

	/** Main GUI container */
	private JPanel gui;

	/** File-system tree. Built Lazily */
	private JTree tree;
	private DefaultTreeModel treeModel;

	/** Directory listing */
	private JProgressBar progressBar;

	private static final int TIMER_DELAY = 5000;
	private Timer updater;
	private JScrollPane treeScroll;
	private TreeSelectionListener treeSelectionListener;
	private File[] roots;

	public PlaylistPane() {
		super();
		application().getMediaPlayerComponent().setMediaDirectory(System.getProperty("user.home"));

		treeSelectionListener = new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent tse){
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)tse.getPath().getLastPathComponent();
				System.out.println("TreeselectedAction=" + tse.toString());
				showChildren(node);

				// This node.toString serves as the Absolut path to a then could be used in the exact same way as filechooser
				File newSelectedFile = new File(node.toString());
				if(newSelectedFile != null) {
					((MainFrame) application().getMainFrame()).actOpenNewMedia(
							new ActionEvent(
									tse,
									0,
									"TreeSelectionEvent"
									)
							, newSelectedFile);
				}
			}
		};

		// the timer variable must be a javax.swing.Timer
		// TIMER_DELAY is a constant int and = 35;
		updater = new javax.swing.Timer(TIMER_DELAY, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onTimer();
			}
		});
		updater.start();
	}    

	public void jTree1ValueChanged(TreeSelectionEvent tse ) {

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if (node.isLeaf()) application().getMediaPlayerComponent().setSelectedFile(node.toString());;
	}

	public Container getGui() {
		if (gui==null) {
			gui = new JPanel(new BorderLayout(3,3));
			gui.setBorder(new EmptyBorder(5,5,5,5));
			treeScroll = new JScrollPane();

			// the File tree
			createTree();

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

	public void createTree() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		treeModel = new DefaultTreeModel(root);

		// show the file system roots.
		roots = this.getRoots();
		for (File fileSystemRoot : roots) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileSystemRoot);
			root.add( node );
			File[] files = this.getFiles(fileSystemRoot);
			for (File file : files) {
				if (file.isDirectory()) {
					node.add(new DefaultMutableTreeNode(file));
				}
			}
		}

		tree = new JTree(treeModel);
		tree.setEditable(true);
		tree.setRootVisible(false);
		tree.setExpandsSelectedPaths(true);
		tree.addTreeSelectionListener(treeSelectionListener);
		tree.setCellRenderer(new FileTreeCellRenderer());
		tree.expandRow(1);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		treeScroll.setViewportView(tree);

		// as per trashgod tip
		tree.setVisibleRowCount(15);
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
					File[] files = getFiles(file);
					ArrayList<File> videoFiles = new ArrayList<File>();
					if (node.isLeaf()) {

						// First run once to include all Folders
						System.out.println("Files:");
						int i = 0;
						for (File child : files) {
							i++;
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
	
	

	private void onTimer() {
		tree.setEnabled(false);
		progressBar.setVisible(true);
		progressBar.setIndeterminate(true);

//		System.out.println("Updating the tree");
//		DefaultMutableTreeNode goalNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
//		TreePath path = tree.getSelectionPath();
//		createTree();
//		
//		if(path != null) {
//			System.out.println("Path=" + path.toString());
//			for(int i = 0; i < path.getPathCount(); i++) {
//				System.out.println("i=" + i + "/" + path.getPathCount());
//				TreeSelectionEvent me = new TreeSelectionEvent(tree, path, true, null, path); 
//			}
//		}
//		
//		if(goalNode != null){
//			File file = (File) goalNode.getUserObject();
//			System.out.println("Selected node=" + goalNode.toString());
//			for(int i = 0; i < tree.getRowCount(); i++){
//				TreePath pathNew = tree.getPathForRow(i);
//				if(path != null && 
//						file.getAbsolutePath().contains(pathNew.getLastPathComponent().toString()) ||
//						file.getAbsolutePath().equals(pathNew.getLastPathComponent().toString())) {
//					System.out.println("Path[" + i + "]=" + pathNew.toString());
//					tree.setSelectionRow(i);
//				}
//			}
//		}
		
		progressBar.setIndeterminate(false);
		progressBar.setVisible(false);
		tree.setEnabled(true);
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

	public TreePath findTreePath(File find) {
		for (int ii=0; ii < tree.getRowCount(); ii++) {
			TreePath treePath = tree.getPathForRow(ii);
			Object object = treePath.getLastPathComponent();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) object;
			File nodeFile = (File) node.getUserObject();

			if (nodeFile==find) {
				return treePath;
			}
		}
		// not found!
		return null;
	}

	public void clickNextItem() {
		// TODO Auto-generated method stub
		System.out.println("Click next Item?");
	}

	public void clickPreviousItem() {
		// TODO Auto-generated method stub
		System.out.println("Click previous Item?");
	}

	public boolean hasNextToPlay() {
		// TODO Auto-generated method stub
		System.out.println("Has Next? Path=" + this.tree.getSelectionPath());
		//DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.tree.getSelectionPath().getLastPathComponent();
		return false;
	}

	public boolean hasPreviousToPlay() {
		// TODO Auto-generated method stub
		System.out.println("Has Previous?");
		return false;
	}	
}
