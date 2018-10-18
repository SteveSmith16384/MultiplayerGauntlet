package ssmith.io;

import java.io.FileNotFoundException;
import java.io.IOException;

public class CSVFile extends TextFile {
	
	private String[] line;
	private String seperator;
	
	public CSVFile() {
		super();
	}
	
	public void openFile(String Filename, String sep) throws FileNotFoundException, IOException {
		super.openFile(Filename, CSVFile.READ);
		this.seperator = sep;
	}
	
	public String[] readCSVLine() throws IOException {
		line = super.readLine().split(seperator);
		// Strip start/end quotes
		for (int i=0 ; i<line.length ; i++) {
			if (line[i].startsWith("\"") && line[i].endsWith("\"")) {
				line[i] = line[i].substring(1, line[i].length() - 1);
			}
		}
		return line;
	}

}
