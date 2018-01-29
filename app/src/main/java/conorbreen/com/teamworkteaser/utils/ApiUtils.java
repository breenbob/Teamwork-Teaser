package conorbreen.com.teamworkteaser.utils;

import android.os.Build;

/**
 * Created by Conor Breen on 28/01/2018.
 */

public class ApiUtils {
    /**
     * Api level 16 or greater
     * @return
     */
    public static boolean JellyBeanOrNewer()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    /**
     * Api Level 21 or greater
     * @return
     */
    public static boolean LollipopOrNewer()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }


    /**
     * Api level 23 or greater
     * @return
     */
    public static boolean MarshmallowOrNewer()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static boolean SameOrNewer(int apiLevel) {
        return Build.VERSION.SDK_INT >= apiLevel;
    }
}
