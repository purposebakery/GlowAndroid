package com.techlung.android.glow.utils;

import com.techlung.android.glow.model.GlowData;
import com.techlung.android.glow.model.Tract;

import java.util.List;

public class SharingUtil {
    public static final int NO_POSITION = -1;

    public static int getTractPositionForSharingUrl(String url) {
        List<Tract> tracts = GlowData.getInstance().getPamphlets();
        String tractName = url.substring(url.lastIndexOf("/") + 1, url.length());

        for (int i = 0; i < tracts.size(); ++i) {
            Tract tract = tracts.get(i);

            if (tract.getId().contains(tractName)) {
                return i;
            }
        }

        return NO_POSITION;
    }

}
