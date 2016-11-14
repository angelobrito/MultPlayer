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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import fileHandlers.CreateChildNodes;
import fileHandlers.FileNode;
import static uk.co.caprica.vlcjplayer.Application.application;

final class PlaylistPane extends JScrollPane implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6341111068079660226L;

	private JList<String> playlist;
	private File          workingDir;
	private JTree 		  tree;

	public PlaylistPane() {

		this.setBackground(Color.WHITE);
		this.setMinimumSize(new Dimension(300, 400));
		this.setPreferredSize(new Dimension(200, 400));

		this.updateWorkingDirTree(System.getProperty("user.home"));

		this.playlist = new JList<String>();
		this.add(this.playlist);
	}    

	public void updateWorkingDirTree(String workingDirectoryPath) {

		this.workingDir = new File(workingDirectoryPath);
		DefaultMutableTreeNode newTreeRoot = new DefaultMutableTreeNode(new FileNode(this.workingDir));
		TreeModel treeModel = new DefaultTreeModel(newTreeRoot);
		this.tree = new JTree(treeModel);
		this.tree.setShowsRootHandles(true);
		this.tree.setMinimumSize(new Dimension(100, 100));
		this.tree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
			public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
				jTree1ValueChanged(evt);
			}
		});
		this.setViewportView(tree);

		CreateChildNodes cnn = new CreateChildNodes(workingDir, newTreeRoot);
		new Thread(cnn).start();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// FIXME Auto-generated method stub
		System.out.println("PlaylistPane e=" + e.toString());
	}

	public void jTree1ValueChanged(TreeSelectionEvent tse ) {
		String node = tse.getNewLeadSelectionPath().getLastPathComponent().toString();
		System.out.println("tse" + tse.toString());
		System.out.println("LeadSelection=" + tse.getNewLeadSelectionPath().getPath()[0]);
		System.out.println("LeadSelection=" + tse.getNewLeadSelectionPath().getPath()[1]);
		System.out.println("LeadSelection=" + tse.getNewLeadSelectionPath().getPath()[2]);
		System.out.println("Node =" + node.toString());
		application().mediaPlayerComponent().setSelectedFile(node.toString());;
	}
}
