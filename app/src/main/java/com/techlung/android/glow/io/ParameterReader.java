package com.techlung.android.glow.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class ParameterReader {
	
	File f;
	public ParameterReader(File f) {
		this.f = f;
	}
	
	public int readParameterInt( String keyParam) {
		String res =readParameterString(keyParam);
		try {
			int result = Integer.parseInt(res);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public String readParameterString(String keyParam) {
		String param = "";
		String key = "";
		FileReader r = null;
		BufferedReader b = null;
		try {
			r = new FileReader(f);
			b = new BufferedReader(r);
			
			String line;
			while((line = b.readLine()) != null)  {
				if (line.contains("=")) {
					key = line.substring(0, line.indexOf("=")).trim();
					param = line.substring(line.indexOf("=") +1, line.length()).trim();

					if ((int) key.charAt(0) == 65279) {
						key = key.substring(1, key.length());
					}
					if (!key.equals("") && !param.equals("") && keyParam.equals(key)) {
						b.close();
						return param;
					}
				}
			}

			b.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		throw new IllegalStateException("Error parsing param " + keyParam + " from  " + f.getAbsolutePath());
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
