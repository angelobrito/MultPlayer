package fileHandlers;

import java.io.File;
import java.util.Arrays;

import javax.swing.filechooser.FileSystemView;

public class FileBrowser {

	/** Title of the application */
	public static final String APP_TITLE = "FileBro";

	/** Used to open/edit/print files. */

	/** Provides nice icons and names for files. */
	private FileSystemView fileSystemView;
	
	public FileBrowser(){
		fileSystemView = FileSystemView.getFileSystemView();
	}

	public File[] getRoots() {
		
		// FIXME I supose the getRoots always return the files on same order
		return fileSystemView.getRoots();
	}
	
	public File[] getFiles(File directory){
		
		// Fetch all files on a directory
		File[] files = directory.listFiles();
		
		// Return the sorted Arraylist since listFiles and related methods doesn't guarantee order
		Arrays.sort(files);
		return files;
	}
	
	public boolean isValidVideoFile(File file){

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
