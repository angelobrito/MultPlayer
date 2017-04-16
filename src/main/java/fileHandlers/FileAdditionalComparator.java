package fileHandlers;

import java.io.File;
import java.util.Comparator;

public class FileAdditionalComparator implements Comparator<File>{

	@Override
	public int compare(File file1, File file2) {
		int result = 0;
		FileAdditionalInfo arg1 = new FileAdditionalInfo(file1);
		FileAdditionalInfo arg2 = new FileAdditionalInfo(file2);
		
		if(arg1.getDate() != null && arg2.getDate() != null) {
			result = arg1.getDate().compareTo(arg2.getDate());
			//System.out.print(arg1.getFileName()+".compareToByDates("+arg2.getFileName()+")="+result);
			if(arg1.getDate().equals(arg2.getDate())) {
				result += Integer.compare(arg1.getChannel(), arg2.getChannel());
				//System.out.println(" also by channel: arg1.channel=" + arg1.getChannel() + ", arg2.channel=" + arg2.getChannel() + ", result=" + result);
			}
			else {
				//System.out.println();
			}
		}
		else {
			result = arg1.getFileName().compareTo(arg2.getFileName());
			//System.out.println(arg1.getFileName()+".compareToByName("+arg2.getFileName()+")="+result);
		}
		return result;
	}
}
