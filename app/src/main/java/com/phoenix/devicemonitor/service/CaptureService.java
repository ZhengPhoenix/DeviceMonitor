package com.phoenix.devicemonitor.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

import com.phoenix.camera.CameraSave;
import com.phoenix.camera.NinjiaCamera;
import com.phoenix.devicemonitor.PreferenceFragment;
import com.phoenix.devicemonitor.R;

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
    public static final String BLOCK_SEND_DELAY = "com.phoenix.devicemonitor.BLOCK_SEND_DELAY";
    public static final String RESEND_CONNECTED = "com.phoenix.devicemonitor.RESEND_CONNECTED";

    SharedPreferences mPre;
    private static String mReceiver;
    private String mSubject;
    private static boolean mSaving = false;
    private boolean mDelaying = false;
    private boolean mIsConnected = false;
    private Handler mHandler;
    private MailSender mSender;
    private ConnectivityManager mCm;

    public CaptureService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand, saving: " + mSaving);
        if(mContext == null) {
            mContext = getApplicationContext();
        }

        mSubject = mContext.getResources().getString(R.string.app_name);

        if(mCm == null) {
            mCm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        }

        mIsConnected = getConnectState();

        if(mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        mPre = PreferenceManager.getDefaultSharedPreferences(mContext);
        mReceiver  = mPre.getString(PreferenceFragment.RECEIVER_ACCOUNT, "");

        if(mCamera == null) {
            Log.d(TAG, "getCameraInstance");
            mCamera = getCameraInstance();
        }
        if(mNinjiaCamera == null) {
            Log.d(TAG, "init NinjiaCamera");
            mNinjiaCamera = new NinjiaCamera(this, mCamera);
            setCameraDiaplayOrientation(mContext, cameraId, mCamera);
        }

        if(mPre.getBoolean(PreferenceFragment.TEN_SEC_DELAY, false)) {
            mDelaying = true;
        }

        String action = intent.getAction();

        if(ACTION_SINGLE_PIC.equals(action)) {
            if(!mSaving) {
                mSaving = true;
                mCamera.takePicture(null, null, mPictureCallback);
                Log.d(TAG, "take pic");
            }
        } else if(ACTION_MULTI_PIC.equals(action)) {

        } else if(BLOCK_SEND_DELAY.equals(action)) {
            mDelaying = false;
        } else if (RESEND_CONNECTED.equals(action)) {
            Log.d(TAG, "resend, data connected: " +mIsConnected );
            if (mIsConnected) {
                File rootDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), CameraSave.TAG);
                if(rootDir.exists()){
                    File[] files = rootDir.listFiles();
                    Log.d(TAG, "file : " + files[0].getPath());

                    mSender = new MailSender(mContext, mReceiver, mSubject, files[0].getPath());
                    mSender.execute();
                } else
                    mContext.stopService(new Intent(mContext, CaptureService.class));
            }
        }

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
            Log.d(TAG, "pic taken, data connected: " +mIsConnected );

            File outputFile = mSave.getOutputMediaFile(CameraSave.DIRECTORY_PUBLIC);

            if(outputFile == null) {
                Log.e(TAG, "generate output file failed");
                return;
            }

            try{
                FileOutputStream fops = new FileOutputStream(outputFile);
                fops.write(data);
                fops.close();
            } catch (FileNotFoundException e) {
                Log.e(TAG, "generate picture failed : " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "write picture failed : " + e.getMessage());
            }
            mSaving = false;
            mCamera.release();
            mCamera = null;
            mNinjiaCamera = null;

            if(mIsConnected) {
                mSender = new MailSender(mContext, mReceiver, mSubject, outputFile.toString());

                if (!mDelaying ) {
                    mSender.execute();
                } else  {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mDelaying) {
                                Log.d(TAG, "delay send email in 10s");
                                mSender.execute();
                            }
                        }
                    }, 10000);
                }
            }
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

    private boolean getConnectState(){
        if(mCm != null) {
            NetworkInfo networkInfo = mCm.getActiveNetworkInfo();
            return (networkInfo != null && networkInfo.isConnectedOrConnecting());
        } else
            return false;
    }
}
