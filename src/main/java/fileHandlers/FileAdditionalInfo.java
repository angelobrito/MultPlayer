package fileHandlers;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;
import static uk.co.caprica.vlcjplayer.Application.application;

public class FileAdditionalInfo {

	private String nameSeparator = "[-_.]";  // FIXME could be -, /, ' ' or '' then ? works as wild card? 
	private int channel;
	private Calendar date;
	private String type;
	private String filePath;
	private String fileName;
	private String prefix;
	private String fileRegex;
	
	public FileAdditionalInfo(String filePath) {
		this.channel = -1;
		this.date = null;
		this.type = "";
		this.prefix = "";
		this.fileRegex = "";
		this.filePath = filePath;
		
		Path path = Paths.get(filePath);
		this.fileName = path.getFileName().toString();
		
		this.processFileName(this.fileName);
	}
	
	public FileAdditionalInfo(File file) {
		this.channel = -1;
		this.date = null;
		this.type = "";
		this.prefix = "";
		this.fileRegex = "";
		this.filePath = file.getAbsolutePath();
		
		Path path = Paths.get(this.filePath);
		this.fileName = path.getFileName().toString();
		
		this.processFileName(this.fileName);
	}
	
	public boolean hasPath(String filePath) {
		return false;
	}

	public String getNameSeparator() {
		return nameSeparator;
	}

	public void setNameSeparator(String nameSeparator) {
		this.nameSeparator = nameSeparator;
	}
	
	public void processFileName(String fileName) {
		
		/*
		 * A fileName can have multiple formats and this program must support a few of them
		 * 1 - CHxx-YYYYMMDD-hhmmss.typ
		 * 2 - YYYY-MM-DD_HHhMMmSSs.typ
		 * 3 - CHxx_YYYYMMDDHHMMSS_WIDTH_X_HEIGHT_x.typ // where width and height can vary 
		 * 4 - XX_CH_R_DDMMYYYYHHMMSS.typ
		 * 5 - Single unformated string name
		 */
		
		Pattern pattern1 = Pattern.compile("[cC][hH]\\d{2}"+nameSeparator+"\\d{4}\\d{2}\\d{2}"+nameSeparator+"\\d{2}\\d{2}\\d{2}[.].{3}");
		Pattern pattern2 = Pattern.compile("\\d{4}"+nameSeparator+"\\d{2}"+nameSeparator+"\\d{2}"+nameSeparator+"\\d{2}[hH]\\d{2}[mM]\\d{2}[sS][.].{3}");
		Pattern pattern3 = Pattern.compile("[cC][hH]\\d{1}"+nameSeparator+"\\d{4}\\d{2}\\d{2}\\d{2}\\d{2}\\d{2}"+nameSeparator+"\\d{3,4}"+nameSeparator+"[xX]"+nameSeparator+"\\d{3,4}"+nameSeparator+"\\d[.].{3}");
		Pattern pattern4 = Pattern.compile("\\d{1,3}"+nameSeparator+"\\d{2}"+nameSeparator+"[R]"+nameSeparator+"\\d{2}\\d{2}\\d{4}\\d{2}\\d{2}\\d{2}[.].{3,4}");
		
		System.out.println("processFileName file=" + fileName);
		if(pattern1.matcher(fileName).matches()) {
			
			// Pattern 1 fileName style: CHxx-YYYYMMDD-hhmmss.typ
			String[] substrings = fileName.split(nameSeparator);
			this.prefix  = "[cC][hH][0-9][0-9]";
			this.channel = Integer.parseInt(substrings[0].substring(2, 4));
			this.type    = substrings[3];

			// Calculate Date
			this.date = Calendar.getInstance();
			int year   = Integer.parseInt(substrings[1].substring(0, 4));
			int month  = Integer.parseInt(substrings[1].substring(4, 6));
			int day    = Integer.parseInt(substrings[1].substring(6, 8));
			
			int hours  = Integer.parseInt(substrings[2].substring(0, 2));
			int minute = Integer.parseInt(substrings[2].substring(2, 4));
			int second = Integer.parseInt(substrings[2].substring(4, 6));
			this.date.set(year, (month-1), day, hours, minute, second);
			
			//  Regex = Prefix +      date      + sufix (timestamp.fileType)
			String sufix = "";
			for(int i = 0; i < application().getchannelSyncThreshold(); i++) {
				if(i == 6) sufix = "?" + nameSeparator + sufix;
				else sufix = "?" + sufix;
			}
			if(sufix.length() < 6) sufix = substrings[1] + nameSeparator + substrings[2].substring(0, substrings[2].length() - sufix.length()) + sufix;
			else sufix = substrings[1].substring(0, substrings[1].length() - sufix.length()) + sufix;
			this.fileRegex = this.prefix + nameSeparator + sufix + nameSeparator + this.type;
			System.out.println("Pattern1=" + this.toString());
		}
		else if(pattern2.matcher(fileName).matches()) {
			
			// Pattern 2 fileName style: YYYY-MM-DD_HHhMMmSSs.typ
			String[] substrings = fileName.split(nameSeparator);
			this.prefix  = "";
			this.channel = 0;
			this.type    = substrings[4];
		
			this.date = Calendar.getInstance();
			int year   = Integer.parseInt(substrings[0]);
			int month  = Integer.parseInt(substrings[1]);
			int day    = Integer.parseInt(substrings[2]);
			int hours  = Integer.parseInt(substrings[3].substring(0, 2));
			int minute = Integer.parseInt(substrings[3].substring(3, 5));
			int second = Integer.parseInt(substrings[3].substring(6, 8));
			this.date.set(year, (month-1), day, hours, minute, second);

			//  Regex = Prefix +      date      + sufix (timestamp.fileType)
			String sufix = "";
			for(int i = 0; i < application().getchannelSyncThreshold(); i++) {
				if(i == 0) sufix = "?s";
				else if(i == 2) sufix = "?m" + sufix;
				else if(i == 4) sufix = "?h" + sufix;
				else if(i == 6 || i == 8 || i == 10) sufix = "?" + nameSeparator + sufix;
				else sufix = "?" + sufix;
			}
			this.fileRegex = fileName.substring(0, (fileName.length()-(8 - sufix.length()))) + sufix + fileName.substring((fileName.length()-4), fileName.length());;
			
			// Try to fetch this file channel from the parent folder
			this.getChannelFromParentFolders();
			
			System.out.println("Pattern2=" + this.toString());
		}
		else if(pattern3.matcher(fileName).matches()) {
			
			// Pattern 3 fileName style: CHx_YYYYMMDDHHMMSS_WIDTH_X_HEIGHT_x.typ
			String[] substrings = fileName.split(nameSeparator);
			this.prefix  = "[cC][hH]*[0-9]";
			this.channel = Integer.parseInt(substrings[0].substring(2, substrings[0].length()));
			this.type    = substrings[6];

			// Calculate Date
			this.date  = Calendar.getInstance();
			int year   = Integer.parseInt(substrings[1].substring( 0, 4));
			int month  = Integer.parseInt(substrings[1].substring( 4, 6));
			int day    = Integer.parseInt(substrings[1].substring( 6, 8));
			int hours  = Integer.parseInt(substrings[1].substring( 8, 10));
			int minute = Integer.parseInt(substrings[1].substring(10, 12));
			int second = Integer.parseInt(substrings[1].substring(12, 14));
			this.date.set(year, (month-1), day, hours, minute, second);
			
			this.fileRegex = this.prefix + nameSeparator + this.getDateRegex(substrings[1]) + nameSeparator + substrings[2] + nameSeparator + substrings[3] + nameSeparator + substrings[4] + nameSeparator + substrings[5] + nameSeparator + substrings[6];
			System.out.println("Pattern3=" + this.toString());
		}
		else if(pattern4.matcher(fileName).matches()) {
			
			// Pattern 4 fileName style: XXX_CH_R_DDMMYYYYHHMMSS.typ
			String[] substrings = fileName.split(nameSeparator);
			this.prefix  = "*" + nameSeparator + "[0-9][0-9]" + nameSeparator + "[" + substrings[2] + "]"; // XXX_
			this.channel = Integer.parseInt(substrings[1]); // _CH_ excluding _
			this.type    = substrings[4]; // typ

			// Calculate Date
			this.date  = Calendar.getInstance();
			int day    = Integer.parseInt(substrings[3].substring( 0, 2));
			int month  = Integer.parseInt(substrings[3].substring( 2, 4));
			int year   = Integer.parseInt(substrings[3].substring( 4, 8));
			int hours  = Integer.parseInt(substrings[3].substring( 8, 10));
			int minute = Integer.parseInt(substrings[3].substring(10, 12));
			int second = Integer.parseInt(substrings[3].substring(12, 14));
			this.date.set(year, (month-1), day, hours, minute, second);
			
			this.fileRegex = this.prefix + nameSeparator + this.getDateRegex(substrings[3]) + nameSeparator + type;
			System.out.println("Pattern4=" + this.toString());
		}
		else {
			
			// No Pattern Matched (Single FileName)
			this.prefix  = "";
			this.channel = -1;
			if(!fileName.equals("")) this.type = fileName.substring(fileName.length()-3, fileName.length());
			else this.type = "";
			this.fileRegex = fileName;

			// Try to fetch this file channel from the parent folder
			this.getChannelFromParentFolders();
			
			System.out.println("No Pattern=" + this.toString());
		}
	}

	private String getDateRegex(String dateStr) {
		// TODO Auto-generated method stub
		//  Regex = Prefix +      date      + sufix (timestamp.fileType)
		String sufix = "";
		for(int i = 0; i < application().getchannelSyncThreshold(); i++) sufix = sufix + "?";
		return dateStr.substring(0, (dateStr.length() - sufix.length())) + sufix;
	}

	public void getChannelFromParentFolders() {
		File parentFile = new File(this.filePath).getParentFile();
		if(parentFile == null){
			System.out.println("First Parent folder does not identify a channel.");
		}
		else if(parentFile.getName().contains("channel")) {
			System.out.println("Parent file with channel" + parentFile.getName());
		}
		else if(parentFile.getName().contains("camera")) {
			String operation = parentFile.getName();
			operation = operation.replace("camera", "");
			this.channel = Integer.parseInt(operation);
			System.out.println("Parent file with camera:" + parentFile.getName() + ", operation:" + operation);
		}
		else if(parentFile.getName().contains("cam")) {
			String operation = parentFile.getName();
			operation = operation.replace("cam", "");
			this.channel = Integer.parseInt(operation);
			System.out.println("Parent file with cam:" + parentFile.getName() + ", operation:" + operation);
		}
		else if(parentFile.getName().contains("Camera")) {
			String operation = parentFile.getName();
			operation = operation.replace("Camera", "");
			this.channel = Integer.parseInt(operation);
			System.out.println("Parent file with Camera:" + parentFile.getName() + ", operation:" + operation);
		}
		else if(parentFile.getName().contains("Cam")) {
			String operation = parentFile.getName();
			operation = operation.replace("Cam", "");
			this.channel = Integer.parseInt(operation);
			System.out.println("Parent file with Cam:" + parentFile.getName() + ", operation:" + operation);
		}
		else if(parentFile.getName().contains("CAM")) {
			String operation = parentFile.getName();
			operation = operation.replace("CAM", "");
			this.channel = Integer.parseInt(operation);
			System.out.println("Parent file with CAM:" + parentFile.getName() + ", operation:" + operation);
		}
		else {
			System.out.println("First Parent folder does not identify a channel.");
		}
	}

	public String getType() {
		return this.type;
	}

	public int getChannel() {
		return this.channel;
	}

	public String getDate() {
		DateFormat formatDate = DateFormat.getDateInstance();
		return formatDate.format(this.date.getTime());
	}
	
	public String getTime() {
		DateFormat time = DateFormat.getTimeInstance();
		return time.format(this.date.getTime());
	}
	
	public String getFileRegex() {
		return this.fileRegex;
	}

	public String getFileName() {
		return this.fileName;
	}

	public String getFilePath() {
		return this.filePath;
	}

	public long getTimestamp() {
		if(this.date != null) return this.date.getTimeInMillis();
		else return 0;
	}
	
	@Override
	public String toString() {
		return "{FileName:" + this.fileName + ", Channel:" + this.channel + ", Timestamp:" + this.getTimestamp() + ", Regex:" + this.fileRegex  + ", Path:" + this.filePath + "}";
	}
	
	public boolean equals(FileAdditionalInfo toTest) {
		if(this.channel != toTest.channel)                    return false;
		else if(!this.fileName.equals(toTest.getFileName()))  return false;
		else if(this.filePath.equals(toTest.getFilePath()))   return false;
		else if(this.fileRegex.equals(toTest.getFileRegex())) return false;
		else if(this.type.equals(toTest.getType()))           return false;
		else                                                  return true;
	}
}
