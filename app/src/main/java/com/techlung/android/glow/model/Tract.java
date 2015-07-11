package com.techlung.android.glow.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import com.techlung.android.glow.io.ParameterReader;
import com.techlung.android.glow.settings.Common;

import android.graphics.drawable.Drawable;
import android.net.Uri;

public class Tract {
	private String id;
	private String title;
	private String url;

	//private Drawable cover;
	private String coverPath;
	private String htmlContent;
	private String htmlAdditional;
	
	private HashMap<String, String> imagePaths = new HashMap<String, String>();

	/*
	 * Constructor
	 */

	public Tract(String id) {
		for (Tract p : GlowData.getInstance().getPamphlets()) {
			if (p.getId().equals(id)) {
				throw new IllegalStateException("Pamphlet already added to Content...");
			}
		}
		this.id = id;
	}

	/*
	 * Functions
	 */
	public void loadMeta(File f) {
		ParameterReader pr = new ParameterReader(f);

		setTitle(pr.readParameterString(Common.META_TITLE));
		setUrl(pr.readParameterString(Common.META_URL));
	}

	/*
	public void loadCover(File f) {
		Drawable cover = Drawable.createFromPath(f.getAbsolutePath());
		setCover(cover);
	}*/

	public void loadHtmlContent(File f) {
		
		try {			
			FileReader fileReader = new FileReader(f);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			StringBuffer stringBuffer = new StringBuffer();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuffer.append(line);
				stringBuffer.append("\n");
			}
			fileReader.close();
			
			setHtmlContent(stringBuffer.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public void loadHtmlAdditional(File f) {
		
		try {			
			FileReader fileReader = new FileReader(f);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			StringBuffer stringBuffer = new StringBuffer();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuffer.append(line);
				stringBuffer.append("\n");
			}
			fileReader.close();
			
			setHtmlAdditional(stringBuffer.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	public void loadImages(File[] fs) {
		for (File f : fs) {
			getImagePaths().put(f.getName(), f.getAbsolutePath());
		}
	}

	public String getImagePath(String name) {
		return imagePaths.get(name);
	}

	/*
	 * GETTER SETTER
	 */
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getHtmlContent() {
		return htmlContent;
	}

	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}

	/*
	public Drawable getCover() {
		return cover;
	}

	public void setCover(Drawable cover) {
		this.cover = cover;
	}*/

	public String getHtmlAdditional() {
		return htmlAdditional;
	}

	public void setHtmlAdditional(String htmlAdditional) {
		this.htmlAdditional = htmlAdditional;
	}

	public HashMap<String, String> getImagePaths() {
		return imagePaths;
	}

	public void setImagePaths(HashMap<String, String> imagePaths) {
		this.imagePaths = imagePaths;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCoverPath() {
		return coverPath;
	}

	public void setCoverPath(String coverPath) {
		this.coverPath = coverPath;
	}

	public Uri getCoverPathUri() {
		return Uri.fromFile(new File(getCoverPath()));
	}
}
