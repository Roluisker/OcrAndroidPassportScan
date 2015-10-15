package views;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import models.AutoFocusEngine;
import ocr.bejarano.luis.mrzandroidscanner.OCRActivity;
import ocr.bejarano.luis.mrzandroidscanner.OpticalRecognitionAsyn;
import utils.OcrUtils;


/**
 * Created by luis.bejarano on 9/23/15.
 */
@SuppressWarnings("deprecation")
public class CameraView extends SurfaceView implements SurfaceHolder.Callback,
        Camera.PictureCallback,
        Camera.ShutterCallback,
        Camera.PreviewCallback {

    private SurfaceHolder mHolder;
    private Camera mCamera;
    private AutoFocusEngine mAutoFocus;
    private OpticalRecognitionAsyn opticalRecognition;
    private boolean isGoodFrame = false;

    CameraViewCallback mOrcCallback;

    public interface CameraViewCallback {
        public void onOcrResult(boolean valid, String[] data);
    }

    public CameraView(Context context, Camera camera) {
        super(context);

        mCamera = camera;
        mCamera.setDisplayOrientation(90);

        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);

        mAutoFocus = AutoFocusEngine.New(mCamera);

    }


    public void setOcrCallback(CameraViewCallback cb) {
        this.mOrcCallback = cb;
    }

    private void doOCR(final Bitmap bitmap) {

        opticalRecognition = new OpticalRecognitionAsyn();
        opticalRecognition.execute(bitmap);

        try {
            String result = opticalRecognition.get();
            Log.d("RESULT OCR:", result);
            String[] parseado = parseMrz(result);
            if (parseado != null) {
                mOrcCallback.onOcrResult(true, parseado);
            }


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d("ERROR", "Camera error on surfaceCreated " + e.getMessage());
        }
    }

    private String[] parseMrz(String mrz) {

        String[] result = null;

        try {

            //if (resultTwo.length() >= 88) {
            int indexP = mrz.indexOf("P<");
            if (indexP != -1) {
                result = new String[3];
                String firstRow = mrz.substring(indexP, indexP + 44);
                //Log.d("RESULT OCR:", firstRow);
                String country = firstRow.substring(2, 5);
                result[0] = country;
                //Log.d("RESULT OCR:", country);
                String surnames = firstRow.substring(5, firstRow.indexOf("<<"));
                //Log.d("RESULT OCR:", surnames);
                String surnamesArray[] = surnames.split("<");
                result[1] = surnamesArray[0];
                for (String SName : surnamesArray) {
                    //  Log.d("RESULT OCR:", SName);
                }
                String names = firstRow.substring(firstRow.indexOf("<<") + 2, 44);

                //Log.d("RESULT OCR:", names);
                String namesArray[] = names.split("<");
                result[2] = namesArray[0];
                for (String Name : namesArray) {
                    //Log.d("RESULT OCR:", Name);
                }                    //String secondRow = result.substring(indexP + 43, indexP + 43 + 43);
                //Log.e("secondRow", secondRow);
            }
            //}

            return result;

        } catch (Exception error) {
            Log.d("RESULT OCR: ", error.getMessage());
        }

        return result;

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        //before changing the application orientation, you need to stop the preview, rotate and then start it again
        if (mHolder.getSurface() == null)//check if the surface is ready to receive camera data
            return;
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            //this will happen when you are trying the camera if it's not running
        }

        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.setPreviewCallback(this);
            mCamera.startPreview();
            mAutoFocus.start();
        } catch (IOException e) {
            Log.d("ERROR", "Camera error on surfaceChanged " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mCamera.setPreviewCallback(null);
        if (mAutoFocus != null && mAutoFocus.isRunning()) {
            mAutoFocus.stop();
        }
        mCamera.stopPreview();
        mCamera.release();
    }

    @Override
    public void onPictureTaken(byte[] bytes, Camera camera) {
        if (bytes == null) {
            return;
        }
        mCamera.stopPreview();
        mCamera.startPreview();
    }


    @Override
    public void onPreviewFrame(final byte[] data, Camera camera) {

        if (isGoodFrame) {

            Camera.Parameters parameters = camera.getParameters();
            int width = parameters.getPreviewSize().width;
            int height = parameters.getPreviewSize().height;

            YuvImage yuv = new YuvImage(data, parameters.getPreviewFormat(), width, height, null);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            yuv.compressToJpeg(new Rect(0, 0, width, height), 50, out);

            byte[] bytes = out.toByteArray();
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            final Bitmap finalB = OcrUtils.getFocusedBitmap(getContext(), mCamera, bitmap, OCRActivity.focusBox.getBox());
            bitmap.recycle();

            doOCR(finalB);

            isGoodFrame = false;

        }

    }


    @Override
    public void onShutter() {
    }

    public void autoFocus() {
        mCamera.autoFocus(autoFocusCallback);
    }

    Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (success) {

                if (opticalRecognition != null && !opticalRecognition.isCancelled()) {
                    opticalRecognition.cancel(true);
                }
                isGoodFrame = true;
            }
        }
    };

}