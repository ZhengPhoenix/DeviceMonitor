package com.phoenix.devicemonitor.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.phoenix.camera.CameraSave;
import com.phoenix.camera.NinjiaCamera;
import com.phoenix.devicemonitor.PreferenceFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CaptureService extends Service {

    private static final String TAG = "CaptureService";
    public Context mContext;

    public Camera mCamera;
    private NinjiaCamera mNinjiaCamera;
    private CameraSave mSave;
    private int cameraId;

    public static final String ACTION_SINGLE_PIC = "com.phoenix.devicemonitor.SINGLE_PIC";
    public static final String ACTION_MULTI_PIC = "com.phoenix.devicemonitor.MULTI_PIC";

    private static boolean mSaving = false;

    public CaptureService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand, saving: " + mSaving);
        if(mContext == null) {
            mContext = getApplicationContext();
        }

        if(mCamera == null) {
            Log.d(TAG, "getCameraInstance");
            mCamera = getCameraInstance();
        }

        if(mNinjiaCamera == null) {
            Log.d(TAG, "init NinjiaCamera");
            mNinjiaCamera = new NinjiaCamera(this, mCamera);
            setCameraDiaplayOrientation(mContext, cameraId, mCamera);
        }

        String action = intent.getAction();

        if(ACTION_SINGLE_PIC.equals(action)) {
            if(!mSaving) {
                mSaving = true;
                mCamera.takePicture(null, null, mPictureCallback);
                Log.d(TAG, "take pic");
            }
        } else if(ACTION_MULTI_PIC.equals(action)) {

        }
        /*
        switch(action) {
            case ACTION_SINGLE_PIC:
                if(!mSaving) {
                    mSaving = true;
                    mCamera.takePicture(null, null, mPictureCallback);
                    Log.d(TAG, "take pic");
                }
                break;
            case ACTION_MULTI_PIC:
            default :
                break;
        }
        */
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
            mSaving = false;
            //Toast.makeText(mContext, "Take Pic Succeeded", Toast.LENGTH_SHORT).show();
            mCamera.release();
            mCamera = null;
            mNinjiaCamera = null;

            SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(mContext);
            String receiver = pre.getString(PreferenceFragment.RECEIVER_ACCOUNT, "");
            MailSender sender = new MailSender("342972949@qq.com", receiver, "Subject", "Text Body", "<b>Html Body<b>", outputFile.toString());
            sender.execute();

            Log.d(TAG, "Take Pic Succeeded");
        }
    };

    public Camera getCameraInstance() {
        Camera c = null;

        if(mCamera != null) {
            return mCamera;
        }

        int cameraNum = Camera.getNumberOfCameras();
        Log.d(TAG, "camera number: " + cameraNum);
        try {
            if(cameraNum > 1) {
                //more than one camera
                c = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
            } else {
                c = Camera.open();
                cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
            }
        } catch (Exception e) {
            Log.e(TAG, "initiate Camera failed");
            e.printStackTrace();
        }

        return c;
    }

    private void setCameraDiaplayOrientation(Context context, int cameraId, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation  = info.orientation;

        Camera.Parameters param = mCamera.getParameters();
        param.setRotation(rotation);

        mCamera.setParameters(param);
    }
}
