package com.phoenix.devicemonitor.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import com.phoenix.camera.CameraSave;
import com.phoenix.camera.NinjiaCamera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CaptureService extends Service {

    private static final String TAG = "CaptureService";
    public Context mContext;

    public static Camera mCamera;
    private NinjiaCamera mNinjiaCamera;
    private CameraSave mSave;

    public static final String ACTION_SINGLE_PIC = "com.phoenix.devicemonitor.SINGLE_PIC";

    public CaptureService() {
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        if(mContext == null) {
            mContext = getApplicationContext();
        }

        if(mCamera == null) {
            mCamera = getCameraInstance();
        }

        if(mNinjiaCamera == null) {
            mNinjiaCamera = new NinjiaCamera(this, mCamera);
        }

        mCamera.takePicture(null, null, mPictureCallback);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mCamera.release();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static Camera getCameraInstance() {
        Camera c = null;

        if(mCamera != null) {
            return mCamera;
        }

        try {
            c = Camera.open();
        } catch (Exception e) {
            Log.e(TAG, "intitate Camera failed");
            e.printStackTrace();
        }

        return c;
    }

    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "pic taken");

            File outputFile = mSave.getOutputMediaFile(CameraSave.DIRECTORY_PUBLIC);

            if(outputFile == null) {
                Log.e(TAG, "generate output file failed");
                return;
            }

            try{
                FileOutputStream fops = new FileOutputStream(outputFile);
                fops.write(data);
                fops.close();

                MediaScannerConnection.scanFile(mContext, new String[]{outputFile.toString()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {
                                Log.d(TAG, "Scanned completed with path : " + path);
                            }
                        });
            } catch (FileNotFoundException e) {
                Log.e(TAG, "generate picture failed : " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "write picture failed : " + e.getMessage());
            }

            mCamera.startPreview();

            //Toast.makeText(mContext, "Take Pic Succeeded", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Take Pic Succeeded");
        }
    };
}
