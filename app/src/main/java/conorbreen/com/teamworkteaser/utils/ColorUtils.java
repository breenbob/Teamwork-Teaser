package conorbreen.com.teamworkteaser.utils;

import android.graphics.Color;
import android.support.annotation.ColorInt;

/**
 * Created by Conor Breen on 28/01/2018.
 */

public class ColorUtils {
    private static int[] MaterialPalette = new int[20];

    static
    {
        // Apple Green
        MaterialPalette[0] = Color.parseColor("#a4c400");

        // Purple
        MaterialPalette[1] = Color.parseColor("#aa00ff");

        // Yellow
        MaterialPalette[2] = Color.parseColor("#e3c800");

        // Brown
        MaterialPalette[3] = Color.parseColor("#825a2c");

        // Lilac
        MaterialPalette[4] = Color.parseColor("#f472d0");

        // Lime Green
        MaterialPalette[5] = Color.parseColor("#60a917");

        // Teal
        MaterialPalette[6] = Color.parseColor("#6d8764");

        // Magenta
        MaterialPalette[7] = Color.parseColor("#d80073");

        // Dark Green
        MaterialPalette[8] = Color.parseColor("#008a00");

        // Turqoise
        MaterialPalette[9] = Color.parseColor("#00aba9");

        // Blood Red
        MaterialPalette[10] = Color.parseColor("#a20025");

        // Slate Blue
        MaterialPalette[11] = Color.parseColor("#647687");

        // Jade
        MaterialPalette[12] = Color.parseColor("#76608a");

        // Red
        MaterialPalette[13] = Color.parseColor("#e51400");

        // Sky Blue
        MaterialPalette[14] = Color.parseColor("#1ba1e2");

        // Dark Blue
        MaterialPalette[15] = Color.parseColor("#0050ef");

        // Orange
        MaterialPalette[16] = Color.parseColor("#fa6800");

        // Light Brown
        MaterialPalette[17] = Color.parseColor("#a0522d");

        // Indigo
        MaterialPalette[18] = Color.parseColor("#6a00ff");

        // Mustard
        MaterialPalette[19] = Color.parseColor("#f0a30a");
    }

    public static @ColorInt
    int getMaterialPaletteColor(int index) {
        return MaterialPalette[index % MaterialPalette.length];
    }

    public static @ColorInt int[] getMaterialPalette() {
        return MaterialPalette;
    }
}
