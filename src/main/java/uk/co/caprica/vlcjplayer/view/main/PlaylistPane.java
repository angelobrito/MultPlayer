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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

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

	public PlaylistPane() {
		super();
		application().getMediaPlayerComponent().setMediaDirectory(System.getProperty("user.home"));
	}    

	public void updateDirectoryTree(File workingDirectoryPath) {

		this.navitageToDirectory(workingDirectoryPath);
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(workingDirectoryPath);
		this.showChildren(node);
	}

	public void jTree1ValueChanged(TreeSelectionEvent tse ) {
		
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if (node.isLeaf()) application().getMediaPlayerComponent().setSelectedFile(node.toString());;
	}
	
	public Container getGui() {
		if (gui==null) {
			gui = new JPanel(new BorderLayout(3,3));
			gui.setBorder(new EmptyBorder(5,5,5,5));

			// the File tree
			DefaultMutableTreeNode root = new DefaultMutableTreeNode();
			treeModel = new DefaultTreeModel(root);
			JScrollPane treeScroll = new JScrollPane();

			TreeSelectionListener treeSelectionListener = new TreeSelectionListener() {
				@Override
				public void valueChanged(TreeSelectionEvent tse){
					DefaultMutableTreeNode node =
							(DefaultMutableTreeNode)tse.getPath().getLastPathComponent();
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

			// show the file system roots.
			File[] roots = this.getRoots();
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
			tree.setRootVisible(false);
			tree.addTreeSelectionListener(treeSelectionListener);
			tree.setCellRenderer(new FileTreeCellRenderer());
			tree.expandRow(0);
			tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			treeScroll.setViewportView(tree);

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
					File[] files = getFiles(file);
					List<File> videoFiles = new ArrayList<File>();
					if (node.isLeaf()) {

						// First run once to include all Folders
						System.out.println("Files:");
						int i = 0;
						for (File child : files) {
							i++;
							System.out.println("File[" + i + "]={" + child.getAbsolutePath() + "}");

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
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.tree.getSelectionPath().getLastPathComponent();
		return false;
	}

	public boolean hasPreviousToPlay() {
		// TODO Auto-generated method stub
		System.out.println("Has Previous?");
		return false;
	}	
}
