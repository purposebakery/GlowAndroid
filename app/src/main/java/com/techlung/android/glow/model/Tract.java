package com.techlung.android.glow.model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.techlung.android.glow.Common;
import com.techlung.android.glow.io.ParameterReader;
import com.techlung.android.glow.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class Tract {
    private String id;
    private String title;
    private String htmlTitle;
    private String url;

    //private Drawable cover;
    private String coverPath;
    private String htmlContent;
    private String htmlAdditional;
    private String htmlManual;

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

    public void loadHtmlManual(InputStream is) {

        try {
            String manual = IOUtils.readStream(is);

            setHtmlManual(manual);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasManual() {
        if (getHtmlManual() != null && !getHtmlManual().trim().equals("")) {
            return true;
        } else {
            return false;
        }
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

    public String getHtmlAdditional() {
        return htmlAdditional;
    }

    public void setHtmlAdditional(String htmlAdditional) {
        this.htmlAdditional = htmlAdditional;
    }

    public String getHtmlManual() {
        return htmlManual;
    }

    public void setHtmlManual(String htmlManual) {
        this.htmlManual = htmlManual;
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

    public Drawable getCoverDrawable(Context context) {
        try {
            InputStream ims = context.getAssets().open(getCoverPath());
            Drawable d = Drawable.createFromStream(ims, null);
            return d;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Drawable getImageDrawable(Context context, String imageName) {
        try {
            InputStream ims = context.getAssets().open(getId() + "/" + imageName);
            Drawable d = Drawable.createFromStream(ims, null);
            return d;
        } catch (IOException ex) {
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

    public float getScrollPosition() {
        return scrollPosition;
    }

    public void setScrollPosition(float scrollPosition) {
        Log.d("TAG", "Save scrol at" + scrollPosition);
        this.scrollPosition = scrollPosition;
    }
}
