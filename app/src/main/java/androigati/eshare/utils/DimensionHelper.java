package androigati.eshare.utils;

import android.content.res.Resources;

/**
 * Created by Antonello Fodde on 06/05/16.
 * fodde.antonello@gmail.com
 */
public class DimensionHelper {

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }
}
