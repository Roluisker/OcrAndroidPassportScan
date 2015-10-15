package utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import ocr.bejarano.luis.mrzandroidscanner.MainActivity;
import ocr.bejarano.luis.mrzandroidscanner.OCRActivity;

/**
 * Created by luis.bejarano
 */
public class ScreenManager {


    public void showFormScreen(Activity origin, String[] data) {

        Intent intent = new Intent(origin, MainActivity.class);
        if (data != null) {
            Bundle params = new Bundle();
            params.putStringArray(MainActivity.TAG_AUTO_COMPLETE, data);
            intent.putExtras(params);
        }
        origin.startActivity(intent);
        origin.finish();

    }


    public void showScanPassport(Activity origin) {
        Intent intent = new Intent(origin, OCRActivity.class);
        origin.startActivity(intent);
        origin.finish();
    }


}
