package com.phoenix.devicemonitor.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.phoenix.camera.CameraSave;
import com.phoenix.camera.NinjiaCamera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class CaptureService extends Service {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_FOO = "com.phoenix.devicemonitor.service.action.FOO";
    public static final String ACTION_BAZ = "com.phoenix.devicemonitor.service.action.BAZ";

    // TODO: Rename parameters
    public static final String EXTRA_PARAM1 = "com.phoenix.devicemonitor.service.extra.PARAM1";
    public static final String EXTRA_PARAM2 = "com.phoenix.devicemonitor.service.extra.PARAM2";

    private static final String TAG = "CaptureService";
    private static final int MODE_TAKE_SINGLE_PICTURE = 1;

    private Context mContext;
    private CameraSave mSave;
    private static Camera mCamera;
    private NinjiaCamera mNinjiaCamera;

    public CaptureService() {
    }
/*
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }
    }
*/
    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onCreate() {
        super.onCreate();

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
