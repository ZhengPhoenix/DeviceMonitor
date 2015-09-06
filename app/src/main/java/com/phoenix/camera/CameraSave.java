package com.phoenix.camera;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hui1.zheng on 9/2/2015.
 */
public class CameraSave {

    private Context mContext;
    private static final String TAG = "CameraSave";

    public static final int DIRECTORY_PUBLIC = 1;
    public static final int DIRECTORY_PRIVATE = 2;

    private static final String tempDir = "data/media/0/Pictures/";

    public CameraSave(Context context) {
        mContext = context;
    }

    public static File getOutputMediaFile(int type){

        File mediaStorageDir;


        switch (type) {
            case DIRECTORY_PRIVATE:
                mediaStorageDir = new File(Environment.getExternalStorageDirectory(), TAG);
                break;

            case DIRECTORY_PUBLIC:
            default:
                mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), TAG);
                break;
        }

        if(!mediaStorageDir.exists()) {
            if(!mediaStorageDir.mkdirs()) {
                Log.e(TAG, "create output directory failed: " + mediaStorageDir.toString());
            }
        }

        //use temporary dir
        /*
        mediaStorageDir = new File(tempDir);
        if(!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs())
                Log.e(TAG, "create temp dir failed");
        }
        */

        String timeStamp = new SimpleDateFormat("yyMMdd_HHmmss").format(new Date());

        File mediaFile =  new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

        Log.d(TAG, "output dir : " + mediaFile.getPath());

        return mediaFile;
    }
}
