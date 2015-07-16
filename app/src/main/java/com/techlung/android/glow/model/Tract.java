package com.techlung.android.glow.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import com.techlung.android.glow.GlowActivity;
import com.techlung.android.glow.io.ParameterReader;
import com.techlung.android.glow.settings.Common;
import com.techlung.android.glow.utils.ToolBox;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;

public class Tract {
	private String id;
	private String title;
	private String url;

	//private Drawable cover;
	private String coverPath;
	private String htmlContent;
	private Spanned htmlContentSpanned;
	private String htmlAdditional;
	private Spanned htmlAdditionalSpanned;
	
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

	public class ImageGetter implements Html.ImageGetter {

		float height = 0.0f;
		float width = 0.0f;

		float width_src = 0.0f;
		float height_src = 0.0f;

		float factor = 0.0f;

		@Override
		public Drawable getDrawable(String source) {

			try {
				BitmapFactory.Options options = new BitmapFactory.Options();
				Bitmap bitmap = BitmapFactory.decodeFile(getImagePath(source), options);
				Drawable d = new BitmapDrawable(GlowActivity.getInstance().getResources(), bitmap);
				DisplayMetrics metrics = new DisplayMetrics();
				GlowActivity.getInstance().getWindowManager().getDefaultDisplay()
						.getMetrics(metrics);

				height = metrics.heightPixels;
				width = metrics.widthPixels;

				if (Common.isXLargeScreen(GlowActivity.getInstance())) {
					width -= ToolBox.convertDpToPixel(300, GlowActivity.getInstance());
				}

				height *= 0.7;
				width *= 0.7;

				width_src = d.getIntrinsicWidth();
				height_src = d.getIntrinsicHeight();

				if (width_src <= height_src) {
					factor = height / height_src;
				} else {
					factor = width / width_src;
				}

				d.setBounds(0, 0, (int) (width_src * factor),
						(int) (height_src * factor));

				return d;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;

		}
	}
}
