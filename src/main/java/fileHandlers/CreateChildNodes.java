package fileHandlers;

import java.io.File;

import javax.swing.tree.DefaultMutableTreeNode;

public class CreateChildNodes implements Runnable {

	private DefaultMutableTreeNode root;

	private File fileRoot;

	public CreateChildNodes(File fileRoot, 
			DefaultMutableTreeNode root) {
		this.fileRoot = fileRoot;
		this.root = root;
	}

	@Override
	public void run() {
		createChildren(fileRoot, root);
	}

	private void createChildren(File fileRoot, DefaultMutableTreeNode node) {
		File[] files = fileRoot.listFiles();
		DefaultMutableTreeNode childNode;
		
		// TODO Just Put Folders and File names.
		// Also ignore folders with name cam* because they should be composed into a single file
		
		childNode = new DefaultMutableTreeNode();
		if (files == null) {
			childNode.setUserObject("Sem arquivos suportados");
			node.add(childNode); //FIXME Change Empty NodeIcon childNode.
		}
		else {
			
			// First Run just for Folders
			for (File file : files) {

				if (file.isDirectory()) {	

					childNode = new DefaultMutableTreeNode(new FileNode(file));
					node.add(childNode);
					createChildren(file, childNode);
				}
			}

			// Then run just for files
			for (File file : files) {

				if(isValidVideoFile(file) && !file.isDirectory()) {

					childNode = new DefaultMutableTreeNode(new FileNode(file));
					node.add(childNode);
				}
			}
			
			// Check if the node has siblings
			if(childNode.getSiblingCount() == 1) {
				childNode = new DefaultMutableTreeNode();
				childNode.setUserObject("Sem arquivos suportados");
				node.add(childNode);
			}
		}
	}

	private boolean isValidVideoFile(File file){

		String fileName;      
		String fileExtension = ""; 
		fileName = file.getName();

		//System.out.println("fileName=" + fileName);
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
