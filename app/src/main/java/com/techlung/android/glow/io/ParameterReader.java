package com.techlung.android.glow.io;

import com.techlung.android.glow.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ParameterReader {
	
	String fileContent = "";
	public ParameterReader(InputStream is) {
		try {
			this.fileContent = IOUtils.readStream(is);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public int readParameterInt(String keyParam) {
		String res = readParameterString(keyParam);
		try {
			int result = Integer.parseInt(res);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public String readParameterString(String keyParam) {
		String[] lines = fileContent.split("\n");

		for (String line : lines) {
			if (line.contains("=")) {
				String key = line.substring(0, line.indexOf("=")).trim();
				String param = line.substring(line.indexOf("=") +1, line.length()).trim();

				if ((int) key.charAt(0) == 65279) {
					key = key.substring(1, key.length());
				}
				if (!key.equals("") && !param.equals("") && keyParam.equals(key)) {
					return param;
				}
			}
		}

		throw new IllegalStateException("Error parsing param " + keyParam + " from  " + fileContent);
	}
	
	public List<String> readParameterStringList(String keyParam) {
		List<String> results = new ArrayList<String>();
		
		String fullResult = readParameterString(keyParam);
		
		String[] fullResultSplitted = fullResult.split(",");
		for (int i = 0; i < fullResultSplitted.length; ++i) {
			results.add(fullResultSplitted[i].trim());
		}
		
		return results;
	}
}
