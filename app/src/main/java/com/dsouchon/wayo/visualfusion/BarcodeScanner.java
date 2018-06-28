package com.dsouchon.wayo.visualfusion;

/**
 * Created by Daniel on 2018/05/02.
 */


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BarcodeScanner extends AppCompatActivity {
    private DBManager dbManager;
    public String mPurpose;
    public String mThisBarcode;
    //public String mThisBarcode;
    public String mURN = "";
    public Integer mStoreItemID;
    public String mStoreNameURN;

    public Boolean mBarcodeValid;
  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }*/
    private static final String BARCODE_PATTERN ="K\\d\\d\\d\\d\\d\\d\\d[A-Z]";

    private static final String BARCODE_PATTERN2 ="K\\s\\d\\d\\d\\d\\d\\d\\d";

    public boolean ValidateBarcode(String barcode)
    {
        //First character must be K
        //Last character must be a letter
        //Middle 7 must be digits

        Pattern p = Pattern.compile(BARCODE_PATTERN);
        Matcher m = p.matcher(barcode);
        boolean b = m.matches();

        if(!b)
        {
            Pattern p2 = Pattern.compile(BARCODE_PATTERN2);
            Matcher m2 = p2.matcher(barcode);
            b = m2.matches();

        }

        return b;

    }


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

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);  dbManager = new DBManager(this);        dbManager.open();
        setContentView(R.layout.barcode_resault);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        TextView editTagNumber = (TextView) findViewById(R.id.editTagNumber);
        TextView txtPurpose = (TextView) findViewById(R.id.txtPurpose);
        Intent intent = getIntent();

        Bundle bd = intent.getExtras();
        mPurpose = dbManager.getValue( "Purpose");
        mURN = getIntent().getStringExtra("URN");
        mStoreItemID = getIntent().getIntExtra("StoreItemID", 0);
        mStoreNameURN = getIntent().getStringExtra("StoreNameURN");
        txtPurpose.setText(mPurpose);

        if (bd != null) {
            String barcode = (String) bd.get("Barcode");
            editTagNumber.setText(barcode);
        }


        //get the extras that are returned from the intent
        String tagNo = getIntent().getStringExtra("Barcode");
        editTagNumber.setText(tagNo);
        mThisBarcode = editTagNumber.getText().toString();

       // mContentatedbarcodes = dbManager.getValue( "BarcodeList");

        if (ValidateBarcode(mThisBarcode)) {
           // mContentatedbarcodes = String.format("%s;%s", mContentatedbarcodes, mThisBarcode);
           // dbManager.setValue( "BarcodeList", mContentatedbarcodes);
           // Toast.makeText(BarcodeScanner.this,String.format("Barcodes scanned so far: %s", mContentatedbarcodes), Toast.LENGTH_LONG).show();
            mBarcodeValid = true;
        }
        else {
            Toast.makeText(BarcodeScanner.this, String.format("Invalid Barcode: %s - Please check and scan again. Barcode will not be saved. Valid formats are: K 1234567 and K1234567X.", mThisBarcode), Toast.LENGTH_LONG).show();

            mBarcodeValid = false;
        }
    }



    public void scanBar(View view) {

        Intent intent = new Intent(BarcodeScanner.this, OpenScanner.class );
        intent.putExtra("Purpose", mPurpose);
        intent.putExtra("URN", mURN);
        intent.putExtra("QrOrBarcode", "Barcode");
        intent.putExtra("Beep", "true");
        intent.putExtra("StoreItemID", mStoreItemID);
        dbManager.setValue( "Purpose",  mPurpose);
        Intent me = getIntent();
        intent.putExtra("StoreName", me.getStringExtra("StoreName"));
        intent.putExtra("StoreNameURN", me.getStringExtra("StoreNameURN"));
        intent.putExtra("URN", me.getStringExtra("URN"));

        Integer storeID = me.getIntExtra("StoreID", 0);
        intent.putExtra("StoreID", storeID);
        intent.putExtra("RequestID", me.getIntExtra("RequestID",0));

        finish();
        startActivity(intent);

        // Button scancode = (Button)findViewById(R.id.scanner2);
        // scancode.setVisibility(View.VISIBLE);



    }


    public void scanningComplete(View view) throws JSONException {

        if(mBarcodeValid) {
            SaveStoreItemToArray(mThisBarcode, mStoreItemID);
        }
        //return to unitry list - now showing barcode
        Intent me = getIntent();
        Intent intent = new Intent(BarcodeScanner.this, UnitryUploadsOnly.class );




        intent.putExtra("StoreName", me.getStringExtra("StoreName"));
        intent.putExtra("StoreNameURN", me.getStringExtra("StoreNameURN"));
        intent.putExtra("URN", me.getStringExtra("URN"));

        Integer storeID = me.getIntExtra("StoreID", 0);
        intent.putExtra("StoreID", storeID);
        intent.putExtra("RequestID", me.getIntExtra("RequestID",0));

        finish();
        startActivity(intent);

        // Button scancode = (Button)findViewById(R.id.scanner2);
        // scancode.setVisibility(View.VISIBLE);



    }
    public void errorDelete(View view) {

        //remove latest barcode from the csv list
       // mContentatedbarcodes = mContentatedbarcodes.replace(mThisBarcode + ";", "");
        //overwrite the saved csvList
        dbManager.setValue( "BarcodeList", mThisBarcode);

        //Proceed with new scan

        Intent intent = new Intent(BarcodeScanner.this, OpenScanner.class );
        intent.putExtra("Purpose", mPurpose);
        intent.putExtra("URN", mURN);
        intent.putExtra("QrOrBarcode", "Barcode");
        intent.putExtra("Beep", "true");
        dbManager.setValue( "Purpose",  mPurpose);
        Intent me = getIntent();
        intent.putExtra("StoreName", me.getStringExtra("StoreName"));
        intent.putExtra("StoreNameURN", me.getStringExtra("StoreNameURN"));
        intent.putExtra("URN", me.getStringExtra("URN"));

        Integer storeID = me.getIntExtra("StoreID", 0);
        intent.putExtra("StoreID", storeID);
        intent.putExtra("RequestID", me.getIntExtra("RequestID",0));
        finish();
        startActivity(intent);

        // Button scancode = (Button)findViewById(R.id.scanner2);
        // scancode.setVisibility(View.VISIBLE);



    }

    public void  SaveStoreItemToArray(String Barcode, Integer ID) throws JSONException {

        try {
            AndroidStoreUnitExplicit androidStoreUnitExplicit = new AndroidStoreUnitExplicit();
            String storeUnitExplicits = dbManager.getValue( "AndroidStoreUnitsExplicit");
            String storeNameURN = mStoreNameURN;

            ArrayList<AndroidStoreUnitExplicit> androidStoreUnitExplicitsAll = JsonUtil.parseJsonArrayAndroidStoreUnitExplicitAll(storeUnitExplicits);

            ArrayList<AndroidStoreUnitExplicit> androidStoreUnitExplicits = JsonUtil.parseJsonArrayAndroidStoreUnitExplicit(storeUnitExplicits, storeNameURN);

            //Convert request to appointment
            AndroidStoreUnitExplicit findStoreUnit = findAndroidStoreUnitExplicitByID(ID, androidStoreUnitExplicits);

            androidStoreUnitExplicit.setBarcode(Barcode); //NEW DANIEL

            androidStoreUnitExplicit.setURN(findStoreUnit.getURN());
            androidStoreUnitExplicit.setImagePath(findStoreUnit.getImagePath());
            Integer Max = findStoreUnit.getMaxQuantityFromMatrix();
            androidStoreUnitExplicit.setMaxQuantityFromMatrix(Max);
            androidStoreUnitExplicit.setStoreName(findStoreUnit.getStoreName());
            androidStoreUnitExplicit.setStoreItemID( findStoreUnit.getStoreItemID());
            androidStoreUnitExplicit.setStoreID( findStoreUnit.getStoreID());
            androidStoreUnitExplicit.setItemTypeName( findStoreUnit.getItemTypeName());
            androidStoreUnitExplicit.setImagePath( findStoreUnit.getImagePath());
            androidStoreUnitExplicit.setEnumerate(findStoreUnit.getEnumerate());
            androidStoreUnitExplicit.setWhyNo(findStoreUnit.getWhyNo());
            androidStoreUnitExplicit.setIsSurvey(findStoreUnit.getIsSurvey());

            androidStoreUnitExplicit.setChkDelivered(findStoreUnit.getChkDelivered());
            androidStoreUnitExplicit.setChkInstalled(findStoreUnit.getChkInstalled());

            androidStoreUnitExplicit.setAcceptedByStore(findStoreUnit.getAcceptedByStore());



            androidStoreUnitExplicit.setImageCount(findStoreUnit.getImageCount());


            //barcode changes by itself
            //imagecount? androidStoreUnitExplicit.setImageCount(findStoreUnit.getImageCount());
            //androidStoreUnitExplicitsAll.remove(findStoreUnit);
            //Remove the found storeitem id so it can be replaced
            Integer position = 0;
            Integer foundposition = 0;

            Iterator<AndroidStoreUnitExplicit> it = androidStoreUnitExplicitsAll.iterator();
            while (it.hasNext()) {
                AndroidStoreUnitExplicit user = it.next();
                Integer findstoreItemID = findStoreUnit.getStoreItemID();
                Integer nextStoreItemID = user.getStoreItemID();
                position++;
                if (findstoreItemID.equals(nextStoreItemID)) {
                    it.remove();
                    foundposition=position;
                }
            }

            androidStoreUnitExplicitsAll.add(foundposition-1, androidStoreUnitExplicit);

            //Save Requests back to local
            dbManager.setValue( "AndroidStoreUnitsExplicit","");
            String test = dbManager.getValue( "AndroidStoreUnitsExplicit");
            dbManager.setValue( "AndroidStoreUnitsExplicit", JsonUtil.convertAndroidStoreUnitExplicitArrayToJson(androidStoreUnitExplicitsAll));
            test = dbManager.getValue( "AndroidStoreUnitsExplicit");

        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
        }
        //

    }


    public AndroidStoreUnitExplicit findAndroidStoreUnitExplicitByID(int id, ArrayList<AndroidStoreUnitExplicit> requests){
        for (AndroidStoreUnitExplicit request : requests) {
            if (request.getStoreItemID() == id) {
                return request;
            }
        }
        return null;
    }





    public void Home(View view) {

        //remove latest barcode from the csv list
        //mContentatedbarcodes = "";
        //overwrite the saved csvList
        dbManager.setValue( "BarcodeList", "");

        //Proceed with new scan

        Intent intent = new Intent(BarcodeScanner.this, InstallHome.class );


        finish();
        startActivity(intent);

        //Button scancode = (Button)findViewById(R.id.scanner2);
        //scancode.setVisibility(View.VISIBLE);



    }



}










