package com.phoenix.camera;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.phoenix.devicemonitor.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CameraTestActivity extends Activity implements Button.OnClickListener{
    private static final String TAG = "CameraTestAct";

    private Context mContext = this;
    private static Camera mCamera;
    private MonitorCameraView mCameraPreview;
    private NinjiaCamera mNinjiaCamera;
    private CameraSave mSave;

    private Button mBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_test);

        mBtn = (Button) findViewById(R.id.take_picture_btn);
        mBtn.setOnClickListener(this);

        mSave =  new CameraSave(this);

        if(mCamera == null) {
            mCamera = getCameraInstance();
        }

        if(mCameraPreview == null) {
            mCameraPreview = new MonitorCameraView(this, mCamera);
        }

        setCameraDiaplayOrientation(mContext, 0, mCamera);

        FrameLayout previewLayout = (FrameLayout) findViewById(R.id.camera_preview);
        previewLayout.addView(mCameraPreview);
        /*
        if(mNinjiaCamera == null) {
            mNinjiaCamera = new NinjiaCamera(this, mCamera);
        }
        */


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_camera_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static Camera getCameraInstance() {
        Camera c = null;

        if(mCamera != null) {
            return mCamera;
        }

        try {
            c = Camera.open(1);
        } catch (Exception e) {
            Log.e(TAG, "intitate Camera failed");
            e.printStackTrace();
        }

        return c;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.take_picture_btn) {

            if(mCamera != null) {
                mCamera.takePicture(null, null, mPictureCallback);
            }
            /*
            if(mNinjiaCamera != null) {
                mCamera.takePicture(null, null, mPictureCallback);
            }
            */
        }
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
                    new MediaScannerConnection.OnScanCompletedListener(){
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

            Toast.makeText(mContext, "Take Pic Succeeded", Toast.LENGTH_SHORT).show();
        }
    };

    private void setCameraDiaplayOrientation(Context context, int cameraId, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);

        int orientation;
        if(cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            orientation = 0;
        } else {
            orientation = 0;
        }

        camera.setDisplayOrientation(orientation);
    }
}
