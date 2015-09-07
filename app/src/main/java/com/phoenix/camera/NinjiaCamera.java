package com.phoenix.camera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;

/**
 * Created by hui1.zheng on 9/6/2015.
 */
public class NinjiaCamera {

    private static final String TAG = "NinjiaCamera";

    private Context mContext;
    private static Camera mCamera;
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
        Log.d(TAG, "PreView constructor, texture:" + mTexture);

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


    public static Camera getCameraInstance() {
        Camera c = null;

        if(mCamera != null) {
            return mCamera;
        }


        try {
            if(Camera.getNumberOfCameras() > 1) {
                //more than one camera
                c = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            } else {
                c = Camera.open();
            }
        } catch (Exception e) {
            Log.e(TAG, "intitate Camera failed");
            e.printStackTrace();
        }

        return c;
    }


}
