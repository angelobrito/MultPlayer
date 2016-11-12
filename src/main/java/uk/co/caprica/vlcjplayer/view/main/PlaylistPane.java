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
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import fileHandlers.CreateChildNodes;
import fileHandlers.FileNode;

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
		this.setMinimumSize(new Dimension(200, 200));
		
		this.updateWorkingDirTree("");
		
		this.playlist = new JList<String>();
		this.add(this.playlist);
    }    
    
	public void updateWorkingDirTree(String workingDirectoryPath) {

		this.workingDir = new File(workingDirectoryPath);
		DefaultMutableTreeNode newTreeRoot = new DefaultMutableTreeNode(new FileNode(this.workingDir));
		TreeModel treeModel = new DefaultTreeModel(newTreeRoot);
		this.tree = new JTree(treeModel);
		this.tree.setShowsRootHandles(true);
		this.tree.setMinimumSize(new Dimension(100, 0));
		this.setViewportView(tree);

		CreateChildNodes cnn = new CreateChildNodes(workingDir, newTreeRoot);
		new Thread(cnn).start();
		this.tree.expandRow(newTreeRoot.getLevel()+1);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		System.out.println("PlaylistPane e=" + e.toString());
	}
}
