package ssmith.lang;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IOFunctions {

	private IOFunctions() {

	}


	public static List<String> ReadTextFile(String path) throws IOException {
		List<String> list = new ArrayList<String>();

		BufferedReader br = new BufferedReader(new FileReader(path));
		try {
			while (true) {
				String line = br.readLine();
				if (line != null) {
					list.add(line);
				} else {
					break;
				}
			}

		} finally {
			br.close();
		}
		return list;
	}

}
