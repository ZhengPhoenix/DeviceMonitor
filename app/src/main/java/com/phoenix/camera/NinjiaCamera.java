package com.phoenix.camera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import java.io.IOException;

/**
 * Created by hui1.zheng on 9/6/2015.
 */
public class NinjiaCamera {

    private static final String TAG = "NinjiaCamera";

    private Context mContext;
    private Camera mCamera;
    SurfaceTexture mTexture;


    public NinjiaCamera(Context context) {
        mContext = context;
    }

    public NinjiaCamera(Camera camera) {
        mCamera = camera;
    }

    public NinjiaCamera(Context context, Camera camera) {
        this(context);
        mCamera = camera;

        mTexture = new SurfaceTexture(0);

        try {
            mCamera.setPreviewTexture(mTexture);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.e(TAG, "initiate camera failed, e: " + e.getMessage());
        }

    }


    protected boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {

            return true;
        } else
            return false;
    }




}
