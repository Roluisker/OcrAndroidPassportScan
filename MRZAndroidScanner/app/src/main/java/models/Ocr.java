package models;

import android.graphics.Bitmap;
import android.os.Environment;

import com.googlecode.tesseract.android.TessBaseAPI;

/**
 * Created by luisalfonsobejaranosanchez
 */
public class Ocr {

    private TessBaseAPI mOcr;
    private String dataPath;

    private static final String LANGUAGE = "eng";
    private static final String VAR_WHITELIST = "1234567890<ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String VAR_BLACKLIST = "!@#$%^&*()_+=-[]}{;:'\"\\|~`,./>?";

    public Ocr() {
        mOcr = new TessBaseAPI();
        initOcr();
    }

    public void initOcr() {
        //TODO: put trained data in assets
        dataPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).
                getAbsolutePath() + "/tesseract-ocr/";

        mOcr.init(dataPath, LANGUAGE);

        mOcr.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, VAR_WHITELIST);
        mOcr.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, VAR_BLACKLIST);
    }

    public String getOCRResult(Bitmap bitmap) {
        mOcr.setImage(bitmap);
        String result = mOcr.getUTF8Text();
        return result;
    }

    public void onDestroy() {
        if (mOcr != null)
            mOcr.end();
    }

}
