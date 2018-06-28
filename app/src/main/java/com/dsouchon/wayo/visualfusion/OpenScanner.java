package com.dsouchon.wayo.visualfusion;

/**
 * Created by Daniel on 2018/05/02.
 */


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class OpenScanner extends AppCompatActivity {
    private DBManager dbManager;
    public String mPurpose;
    public String QrOrBarcode = "Barcode";
    public String mURN;
    public Integer mStoreItemID;


    @Override
    public void onBackPressed() {
        //DO NOTHING

    }



    /************SET AND GET GLOBAL VARIABLES ******************/
    public static void setDefaults(Context context, String key, String value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }
    public static String getDefaultsOld(Context context,String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "");
    }
    /******************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);  dbManager = new DBManager(this);        dbManager.open();
        setContentView(R.layout.activity_open_scanner);
        mURN = getIntent().getStringExtra("URN");
        mStoreItemID = getIntent().getIntExtra("StoreItemID", 0);
    }


    public void scanCustomScanner(View view) {

        String Beep = "true";

        QrOrBarcode = getIntent().getStringExtra("QrOrBarcode");
        Beep = getIntent().getStringExtra("Beep");


        IntentIntegrator integrator = new IntentIntegrator(this);

        if(QrOrBarcode.equals("Barcode")) {
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);

            integrator.setPrompt("Scan a Barcode");
        }
        else {
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
            integrator.setPrompt("Scan a QR Code");
        }

        if(Beep.equals("false")) {
            integrator.setBeepEnabled(false);
        }
        else {
            integrator.setBeepEnabled(true);
        }

        //integrator.setCameraId(0);  // Use a specific camera of the device

        integrator.setBarcodeImageEnabled(true);
        integrator.setOrientationLocked(false);
        integrator.setCaptureActivity(CaptureActivityPortrait.class);
        //Intent intent = new Intent("com.google.zxing.client.android.SCAN");

        integrator.initiateScan();


//        new IntentIntegrator(this).setOrientationLocked(false).setCaptureActivity(CustomScannerActivity.class).initiateScan();
    }







    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.d("MainActivity", "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {

                Log.d("MainActivity", "Scanned");
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();

                if(QrOrBarcode.equals("Barcode")) {
                    Intent intent = new Intent(OpenScanner.this, BarcodeScanner.class);
                    intent.putExtra("Barcode", result.getContents());
                    intent.putExtra("Purpose", mPurpose);
                    intent.putExtra("URN", mURN);
                    intent.putExtra("QrOrBarcode", QrOrBarcode);
                    intent.putExtra("StoreItemID", mStoreItemID);

                    Intent me = getIntent();
                    intent.putExtra("StoreName", me.getStringExtra("StoreName"));
                    intent.putExtra("StoreNameURN", me.getStringExtra("StoreNameURN"));
                    intent.putExtra("URN", me.getStringExtra("URN"));

                    Integer storeID = me.getIntExtra("StoreID", 0);
                    intent.putExtra("StoreID", storeID);
                    intent.putExtra("RequestID", me.getIntExtra("RequestID",0));

                    startActivity(intent);
                    finish();
                }


            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);

        }
    }

    /**
     * Sample of scanning from a Fragment
     */
    public static class ScanFragment extends Fragment {
        private String toast;

        public ScanFragment() {
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            displayToast();
        }



        public void scanFromFragment() {
            IntentIntegrator.forSupportFragment(this).initiateScan();
        }

        private void displayToast() {
            if(getActivity() != null && toast != null) {
                Toast.makeText(getActivity(), toast, Toast.LENGTH_LONG).show();
                toast = null;
            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            IntentResult labelScanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if(labelScanResult != null) {
                if(labelScanResult.getContents() == null) {
                    toast = "Cancelled from fragment";
                } else {
                    toast = "Scanned from fragment: " + labelScanResult.getContents();


                }

                // At this point we may or may not have a reference to the activity
                displayToast();
            }
        }
    }
}

