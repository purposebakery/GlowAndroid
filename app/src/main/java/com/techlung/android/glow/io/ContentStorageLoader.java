package com.techlung.android.glow.io;

import android.app.Activity;
import android.content.res.AssetManager;

import com.techlung.android.glow.Common;
import com.techlung.android.glow.model.GlowData;
import com.techlung.android.glow.model.Tract;

import java.io.IOException;

public class ContentStorageLoader {

    public static void load(Activity activity) {
        // validate
        try {
            AssetManager assetManager = activity.getResources().getAssets();

            String lang = "de";

            String[] pamphlets = assetManager.list(lang);

            // Reset Content
            GlowData.getInstance().clear();

            // Create Pamphlets and add to Content
            for (String pamphletDir : pamphlets) {


                if (pamphletDir.equals(Common.FILE_CONTACT)) {
                    GlowData.getInstance().loadContact(assetManager.open(lang + "/" + pamphletDir));
                } else if (pamphletDir.equals(Common.FILE_INFO)) {
                    GlowData.getInstance().loadInfo(assetManager.open(lang + "/" + pamphletDir));
                } else {
                    Tract newPamphlet = new Tract(lang + "/" + pamphletDir);

                    newPamphlet.loadMeta(assetManager.open(lang + "/" + pamphletDir + "/" + Common.FILE_META));
                    newPamphlet.loadHtmlContent(assetManager.open(lang + "/" + pamphletDir + "/" + Common.FILE_CONTENT));
                    newPamphlet.loadHtmlAdditional(assetManager.open(lang + "/" + pamphletDir + "/" + Common.FILE_ADDITIONAL));
                    newPamphlet.setCoverPath(lang + "/" + pamphletDir + "/" + Common.FILE_COVER);

                    try {
                        newPamphlet.loadHtmlManual(assetManager.open(lang + "/" + pamphletDir + "/" + Common.FILE_MANUAL));
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
