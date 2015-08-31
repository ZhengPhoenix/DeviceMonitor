package com.phoenix.camera;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.phoenix.devicemonitor.R;

public class CameraTestActivity extends Activity implements  Button.OnClickListener{
    private static final String TAG = "CameraTestAct";

    private Camera mCamera;
    private MonitorCameraView mCameraPreview;
    Button mTakePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_test);

        mTakePicture = (Button) findViewById(R.id.take_picture_btn);
        mTakePicture.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mCamera = getCameraInstance();
        mCameraPreview = new MonitorCameraView(this, mCamera);

        FrameLayout previewLayout = (FrameLayout) findViewById(R.id.camera_preview);
        previewLayout.addView(mCameraPreview);
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

        try {
            c = Camera.open();
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
    public void onClick(View view) {
        if(view.getId() == R.id.take_picture_btn) {
            mCamera.takePicture(null, null, mPictureCallback);
        }
    }

    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            //TODO: implement picture callback
        }
    };
}
