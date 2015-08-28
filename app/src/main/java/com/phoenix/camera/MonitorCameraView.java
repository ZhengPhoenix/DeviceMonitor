package com.phoenix.camera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by hui1.zheng on 8/28/2015.
 */
public class MonitorCameraView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "MonitorCameraView";

    private Context mContext;
    private Camera mCamera;
    private SurfaceHolder mHolder;


    public MonitorCameraView(Context context) {
        super(context);
        mContext = context;

        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public MonitorCameraView(Context context, Camera camera) {
        this(context);
        mCamera = camera;
    }


    protected boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {

            return true;
        } else
            return false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "start preview with exceptioin");
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
