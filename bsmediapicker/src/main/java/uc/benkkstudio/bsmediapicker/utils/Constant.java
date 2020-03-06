package uc.benkkstudio.bsmediapicker.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import java.io.File;

public class Constant {
    public static String trimUrl;
    public static int video_upload_duration;
    public static void notifyMediaStore(Context context, String path){
        if (path != null && !TextUtils.isEmpty(path)) {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(new File(path)));
            context.sendBroadcast(mediaScanIntent);
        }
    }
}
