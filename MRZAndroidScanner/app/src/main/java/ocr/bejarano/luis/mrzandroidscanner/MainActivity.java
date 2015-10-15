package ocr.bejarano.luis.mrzandroidscanner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import utils.AppHelper;
import utils.OcrUtils;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG_AUTO_COMPLETE = "autocomplete";


    private EditText mEtFirstName;
    private EditText mEtLastName;
    private EditText mEtCountry;

    private Button scannPassport;

    private String[] autoCompleteData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_form);

        scannPassport = (Button) findViewById(R.id.btn_scan_passport);
        mEtFirstName = (EditText) findViewById(R.id.edt_first_name);
        mEtLastName = (EditText) findViewById(R.id.edt_last_name);
        mEtCountry = (EditText) findViewById(R.id.edt_country);
        mEtCountry.setText("COLOMBIA");

        scannPassport.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {

            autoCompleteData = bundle.getStringArray(TAG_AUTO_COMPLETE);

            if (autoCompleteData != null) {
                mEtCountry.setText(autoCompleteData[0]);
                mEtLastName.setText(autoCompleteData[1]);
                mEtFirstName.setText(autoCompleteData[2]);
            }


        }

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_scan_passport:
                if (OcrUtils.hasTrainedData()) {
                    AppHelper.screenManager.showScanPassport(this);
                }
                break;
            default:
                break;

        }
    }


}
