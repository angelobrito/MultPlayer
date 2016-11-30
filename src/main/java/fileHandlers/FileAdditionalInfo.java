package fileHandlers;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

public class FileAdditionalInfo {

	private String nameSeparator = "-";  // FIXME could be -, /, ' ' or '' then ? works as wild card? 
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

	public boolean hasPath(String filePath2) {
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
		 * 3 - Single unformated string name
		 */
		
		Pattern pattern1 = Pattern.compile("[cC][hH]\\d{2}[-]\\d{4}\\d{2}\\d{2}-\\d{2}\\d{2}\\d{2}[.].{3}");
		Pattern pattern2 = Pattern.compile("\\d{4}[-]\\d{2}[-]\\d{2}[_]\\d{2}[hH]\\d{2}[mM]\\d{2}[sS][.].{3}");
		
		System.out.println("processFileName file=" + fileName);
		if(pattern1.matcher(fileName).matches()) {
			
			// Pattern 1 fileName style: CHxx-YYYYMMDD-hhmmss.typ
			this.prefix  = "CH??";
			this.channel = Integer.parseInt(fileName.substring(2, 4));
			this.type    = fileName.substring(21, 24);

			// Calculate Date
			this.date = Calendar.getInstance();
			int year   = Integer.parseInt(fileName.substring(5, 9));
			int month  = Integer.parseInt(fileName.substring(9, 11));
			int date   = Integer.parseInt(fileName.substring(11, 13));
			int hours  = Integer.parseInt(fileName.substring(14, 16));
			int minute = Integer.parseInt(fileName.substring(16, 18));
			int second = Integer.parseInt(fileName.substring(18, 20));
			this.date.set(year, (month-1), date, hours, minute, second);
			
			//  Regex = Prefix +      date      + sufix (timestamp.fileType)
			this.fileRegex = this.prefix  + fileName.substring(4, 24);
			System.out.println("Pattern1={" + this.prefix + ", " + this.getChannel() + ", " + this.getType() + ", " + this.getDate() + ", " + this.getTime() + "}");
		}
		else if(pattern2.matcher(fileName).matches()) {
			
			// Pattern 2 fileName style: YYYY-MM-DD_HHhMMmSSs.typ
			this.prefix  = "";
			this.channel = 0;
			this.type    = fileName.substring((fileName.length()-3), fileName.length());
		
			this.date = Calendar.getInstance();
			int year   = Integer.parseInt(fileName.substring(0, 4));
			int month  = Integer.parseInt(fileName.substring(5, 7));
			int date   = Integer.parseInt(fileName.substring(8, 10));
			int hours  = Integer.parseInt(fileName.substring(11, 13));
			int minute = Integer.parseInt(fileName.substring(14, 16));
			int second = Integer.parseInt(fileName.substring(17, 19));
			this.date.set(year, (month-1), date, hours, minute, second);

			//  Regex = Prefix +      date      + sufix (timestamp.fileType)
			this.fileRegex = fileName;
			System.out.println("Pattern2={" + this.prefix + ", " + this.getChannel() + ", " + this.getType() + ", " + this.getDate() + ", " + this.getTime() + "}");
		}
		else {
			
			// No Pattern Matched (Single FileName)
			this.prefix  = "";
			this.channel = -1;
			if(!fileName.equals("")) this.type = fileName.substring(fileName.length()-3, fileName.length());
			else this.type = "";
			this.fileRegex = fileName;
			System.out.println("No Pattern={" + fileName + "}");
		}
	}

	private String getType() {
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
}
