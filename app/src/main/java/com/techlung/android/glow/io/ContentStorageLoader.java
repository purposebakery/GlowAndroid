package com.techlung.android.glow.io;

import android.app.Activity;
import android.content.res.AssetManager;

import com.techlung.android.glow.Common;
import com.techlung.android.glow.model.GlowData;
import com.techlung.android.glow.model.Tract;
import com.techlung.android.glow.settings.Preferences;

import java.io.IOException;

public class ContentStorageLoader {

    public static void load(Activity activity) {
        // validate
        try {
            AssetManager assetManager = activity.getResources().getAssets();

            String lang = Preferences.getLang();

            String[] pamphlets = assetManager.list(lang);

            // Reset Content
            GlowData.getInstance().clear();

            // Create Pamphlets and add to Content
            for (String files : pamphlets) {


                if (files.equals(Common.FILE_CONTACT)) {
                    GlowData.getInstance().loadContact(assetManager.open(lang + "/" + files));
                } else if (files.equals(Common.FILE_INFO)) {
                    GlowData.getInstance().loadInfo(assetManager.open(lang + "/" + files));
                } else if (files.equals(Common.FILE_ADDITIONAL)) {
                    GlowData.getInstance().loadAdditional(assetManager.open(lang + "/" + files));
                } else if (!files.endsWith(".png")){
                    Tract newPamphlet = new Tract(lang + "/" + files);

                    newPamphlet.loadMeta(assetManager.open(lang + "/" + files + "/" + Common.FILE_META));
                    newPamphlet.loadHtmlContent(assetManager.open(lang + "/" + files + "/" + Common.FILE_CONTENT));
                    newPamphlet.setCoverPath(lang + "/" + files + "/" + Common.FILE_COVER);

                    try {
                        newPamphlet.loadHtmlManual(assetManager.open(lang + "/" + files + "/" + Common.FILE_MANUAL));
                    } catch (IOException e) {
                        // all ok. Some tracts don't have manuals
                    }

                    GlowData.getInstance().addPamphlet(newPamphlet);
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
