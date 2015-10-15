package ocr.bejarano.luis.mrzandroidscanner;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;


import org.opencv.android.OpenCVLoader;

import utils.AppHelper;
import views.CameraView;
import widgets.CameraBoxWidget;


/**
 * Created by luis.bejarano
 */
@SuppressWarnings("deprecation")
public class OCRActivity extends Activity implements OnClickListener, CameraView.CameraViewCallback {

    static {
        if (!OpenCVLoader.initDebug()) {
        } else {
        }
    }

    private Camera mCamera;
    private CameraView mCameraView;
    private Button mRequesFocus;
    private ImageButton mImgClose;
    public static CameraBoxWidget focusBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);

        initCamera();

        focusBox = (CameraBoxWidget) findViewById(R.id.focus_box);
        mImgClose = (ImageButton) findViewById(R.id.orcImgClose);
        mRequesFocus = (Button) findViewById(R.id.requesFocus);

        mImgClose.setOnClickListener(this);
        mRequesFocus.setOnClickListener(this);
        focusBox.setOnClickListener(this);

        mCameraView.setOcrCallback(this);

    }


    private void initCamera() {

        try {

            mCamera = Camera.open();

            if (mCamera != null) {
                mCameraView = new CameraView(this, mCamera);
                FrameLayout cameraView = (FrameLayout) findViewById(R.id.camera_view);
                cameraView.addView(mCameraView);
            }

        } catch (Exception e) {
            Log.d("ERROR", "Failed to get camera: " + e.getMessage());
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.orcImgClose:
                AppHelper.screenManager.showFormScreen(this, null);
                break;

            case R.id.requesFocus:
                mCameraView.autoFocus();
                break;

        }
    }


    @Override
    public void onOcrResult(boolean valid, String[] data) {
        if (valid) {
            Log.d("onOcrResult: ", data[0] + "---" + data[1]);
            AppHelper.screenManager.showFormScreen(this, data);
        }
    }

}
