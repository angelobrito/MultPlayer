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

import java.io.File;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;

import fileHandlers.FileBrowser;

final class PlaylistPane extends FileBrowser {

	private JTree 		  tree;

	public PlaylistPane() {

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
}
