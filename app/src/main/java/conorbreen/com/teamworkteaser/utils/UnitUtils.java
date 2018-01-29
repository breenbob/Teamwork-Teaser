package conorbreen.com.teamworkteaser.utils;

import android.content.res.Resources;

/**
 * Created by Conor Breen on 28/01/2018.
 */

public class UnitUtils {
    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px)
    {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }
}
