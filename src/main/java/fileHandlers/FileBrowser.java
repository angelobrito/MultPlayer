package fileHandlers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.filechooser.FileSystemView;

import uk.co.caprica.vlcj.runtime.RuntimeUtil;

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
		ArrayList<File> rootList = new ArrayList<File>();
		if (RuntimeUtil.isNix()) {
			for(File dir : fileSystemView.getRoots()) {
				rootList.add(dir);
			}
		}
		else {
			for(File dir : fileSystemView.getRoots()) {
				for(File newDir : this.getFiles(dir)) {
					if(newDir.isDirectory()) rootList.add(newDir);
				}
			}
		}
		
		File[] cleanedRoots = new File[rootList.size()];
		cleanedRoots = rootList.toArray(cleanedRoots);
		return cleanedRoots;
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
		else if(fileExtension.equalsIgnoreCase("h264")) return true;
		//else if(fileExtension.equalsIgnoreCase("")) return true;
		else return false;
	}
	
	public static ArrayList<FileAdditionalInfo> getRelatedFiles(File workingDirectory, String fileName) {

		ArrayList<FileAdditionalInfo> result = new ArrayList<FileAdditionalInfo>();
		System.out.println("getRelatedFiles(" + fileName + ")");
		
		String fileRegex = FileBrowser.getFileRegex(fileName);
		ArrayList<String> filesPaths = new ArrayList<String>();
		
		try {
			Path startingDir = Paths.get(workingDirectory.getAbsolutePath());
			PathFinder finder = new PathFinder(fileRegex);
			Files.walkFileTree(startingDir, finder);
			filesPaths = finder.getPathsAsArray();
			finder.done(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int filesCounter = 0;
		for(String filePath : filesPaths) {
			result.add(new FileAdditionalInfo(filePath));
			filesCounter++;
			System.out.println("Found RelatedFile[" + filesCounter + "]={" + filePath + "}");
		}
		return result;
	}

	public static String getFileRegex(String fileName) {
		
		FileAdditionalInfo processedFile = new FileAdditionalInfo(fileName);
		String fileRegex = processedFile.getFileRegex();
		System.out.println("getFileRegex: Channel=#" + processedFile.getChannel() + ", Regex=" + fileRegex);
		return fileRegex;
	}
}
