package conorbreen.com.teamworkteaser.utils;

import android.graphics.Typeface;

import java.util.HashMap;

import conorbreen.com.teamworkteaser.MainApplication;

/**
 * Created by Conor Breen on 29/01/2018.
 */

public class TypefaceUtils {
    private static HashMap<String, Typeface> fontMap = new HashMap<>();

    public static synchronized Typeface resolveTypeface(String fontPath) {
        if (fontMap.containsKey(fontPath)) {
            return fontMap.get(fontPath);
        }

        Typeface typeFace = Typeface.createFromAsset(MainApplication.getContext().getAssets(), fontPath);
        fontMap.put(fontPath, typeFace);

        return typeFace;
    }
}
