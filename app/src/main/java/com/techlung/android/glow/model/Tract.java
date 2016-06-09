package com.techlung.android.glow.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import com.techlung.android.glow.utils.IOUtils;
import com.techlung.android.glow.io.ParameterReader;
import com.techlung.android.glow.settings.Common;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spanned;
import android.util.Log;

public class Tract {
	private String id;
	private String title;
	private String htmlTitle;
	private String url;

	//private Drawable cover;
	private String coverPath;
	private String htmlContent;
	private String htmlAdditional;

	private float scrollPosition = 0;
	
	private HashMap<String, String> imagePaths = new HashMap<String, String>();

	public Tract(String id) {
		for (Tract p : GlowData.getInstance().getPamphlets()) {
			if (p.getId().equals(id)) {
				throw new IllegalStateException("Pamphlet already added to Content...");
			}
		}
		this.id = id;
	}

	public void loadMeta(InputStream is) {
		ParameterReader pr = new ParameterReader(is);

		setTitle(pr.readParameterString(Common.META_TITLE));
		setHtmlTitle(pr.readParameterString(Common.META_TITLE_HTML));
		setUrl(pr.readParameterString(Common.META_URL));
	}


	public void loadHtmlContent(InputStream is) {
		
		try {
			String content = IOUtils.readStream(is);
			
			setHtmlContent(content);
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public void loadHtmlAdditional(InputStream is) {
		
		try {
			String additional = IOUtils.readStream(is);
			
			setHtmlAdditional(additional);
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	/*
	public void loadImages(File[] fs) {
		for (File f : fs) {
			getImagePaths().put(f.getNameResource(), f.getAbsolutePath());
		}
	}*/

	/*
	public String getImagePath(String name) {
		return imagePaths.get(name);
	}*/

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
/*

	public HashMap<String, String> getImagePaths() {
		return imagePaths;
	}
	public void setImagePaths(HashMap<String, String> imagePaths) {
		this.imagePaths = imagePaths;
	}*/

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

	public Drawable getCoverDrawable(Context context) {
		try
		{
			InputStream ims = context.getAssets().open(getCoverPath());
			Drawable d = Drawable.createFromStream(ims, null);
			return d;
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public Drawable getImageDrawable(Context context, String imageName) {
		try
		{
			InputStream ims = context.getAssets().open(getId() + "/" + imageName);
			Drawable d = Drawable.createFromStream(ims, null);
			return d;
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public String getHtmlTitle() {
		return htmlTitle;
	}

	public void setHtmlTitle(String htmlTitle) {
		this.htmlTitle = htmlTitle;
	}

	/*
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
	}*/

	public float getScrollPosition() {
		return scrollPosition;
	}

	public void setScrollPosition(float scrollPosition) {
		Log.d("TAG", "Save scrol at" + scrollPosition);
		this.scrollPosition = scrollPosition;
	}
}
