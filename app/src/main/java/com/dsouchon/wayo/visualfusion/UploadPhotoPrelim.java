package com.dsouchon.wayo.visualfusion;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Iterator;

public class UploadPhotoPrelim extends AppCompatActivity {
    int mStoreItemID =0;
    int mStoreID =0;
    public String QrOrBarcode = "None";
    public String mStoreNameURN;
    private DBManager dbManager;

    @Override
    public void onBackPressed() {

        Intent me = getIntent();
       Intent intent = new Intent(this,UnitryUploadsOnly.class);

        intent.putExtra("StoreName", me.getStringExtra("StoreName"));
        intent.putExtra("StoreNameURN", me.getStringExtra("StoreNameURN"));
        intent.putExtra("URN", me.getStringExtra("URN"));

        Integer storeID = me.getIntExtra("StoreID", 0);
        intent.putExtra("StoreID", storeID);
        intent.putExtra("RequestID", me.getIntExtra("RequestID",0));

        finish();
        startActivity(intent);


        finish();

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
        setContentView(R.layout.activity_upload_photo_prelim);


        Intent me = getIntent();
        mStoreNameURN = me.getStringExtra("StoreNameURN");
        mStoreItemID = me.getIntExtra("StoreItemID", 0);
        mStoreID = me.getIntExtra("StoreID", 0);
        int Enumerate = me.getIntExtra("Enumerate", 0);
        QrOrBarcode = me.getStringExtra("QrOrBarcode");


    if(Enumerate ==0){
    Button btn = (Button)findViewById(R.id.btnBarcode);
        btn.setVisibility(View.GONE);
    }


    }

    public void UploadBarcode(View view) {

        //TODO: put barcode scanning functionality here DANIEL


            Intent me = getIntent();
            Intent intent = new Intent(UploadPhotoPrelim.this, OpenScanner.class);
            intent.putExtra("StoreItemID", mStoreItemID );
            intent.putExtra("QrOrBarcode", "Barcode");
            intent.putExtra("Beep", "true");

        intent.putExtra("StoreName", me.getStringExtra("StoreName"));
        intent.putExtra("StoreNameURN", me.getStringExtra("StoreNameURN"));
        intent.putExtra("URN", me.getStringExtra("URN"));

        Integer storeID = me.getIntExtra("StoreID", 0);
        intent.putExtra("StoreID", storeID);
        intent.putExtra("RequestID", me.getIntExtra("RequestID",0));


            finish();

            startActivity(intent);



       /* Intent intent = new Intent(UploadPhotoPrelim.this, UploadPhoto.class );


        intent.putExtra("Enumerate", getIntent().getStringExtra("Enumerate"));

        Integer storeItemID =  getIntent().getIntExtra("StoreItemID",0);
        intent.putExtra("StoreItemID", storeItemID);

        intent.putExtra("StoreName", getIntent().getStringExtra("StoreName"));
        intent.putExtra("StoreNameURN", getIntent().getStringExtra("StoreNameURN"));
        intent.putExtra("URN", getIntent().getStringExtra("URN"));

        intent.putExtra("StoreID", getIntent().getStringExtra("StoreID"));

        intent.putExtra("ImageType", "BarcodeOnUnit");
        intent.putExtra("Barcode",getIntent().getStringExtra("Barcode"));

        intent.putExtra("RequestID", getIntent().getIntExtra("RequestID",0));

        startActivity(intent); finish();*/
    }



    public void UnitryComplaint(View view) {

        Intent intent = new Intent(UploadPhotoPrelim.this, UploadPhoto.class );


        intent.putExtra("Enumerate", getIntent().getStringExtra("Enumerate"));

        Integer storeItemID =  getIntent().getIntExtra("StoreItemID",0);
        intent.putExtra("StoreItemID", storeItemID);

        intent.putExtra("StoreName", getIntent().getStringExtra("StoreName"));
        intent.putExtra("StoreNameURN", getIntent().getStringExtra("StoreNameURN"));
        intent.putExtra("URN", getIntent().getStringExtra("URN"));

        intent.putExtra("StoreID", getIntent().getStringExtra("StoreID"));

        intent.putExtra("ImageType", "UnitryComplaint");
        intent.putExtra("Barcode",getIntent().getStringExtra("Barcode"));

        intent.putExtra("RequestID", getIntent().getIntExtra("RequestID",0));

        startActivity(intent); finish();
    }


    public void UploadUnitImage(View view) {

        Intent intent = new Intent(UploadPhotoPrelim.this, UploadPhoto.class );


        intent.putExtra("Enumerate", getIntent().getStringExtra("Enumerate"));

        Integer storeItemID =  getIntent().getIntExtra("StoreItemID",0);
        intent.putExtra("StoreItemID", storeItemID);

        intent.putExtra("StoreName", getIntent().getStringExtra("StoreName"));
        intent.putExtra("StoreNameURN", getIntent().getStringExtra("StoreNameURN"));
        intent.putExtra("URN", getIntent().getStringExtra("URN"));

        intent.putExtra("StoreID", getIntent().getStringExtra("StoreID"));

        intent.putExtra("ImageType", "StoreItemInStore");
        intent.putExtra("Barcode",getIntent().getStringExtra("Barcode"));

        intent.putExtra("RequestID", getIntent().getIntExtra("RequestID",0));

        startActivity(intent); finish();
    }


    public void  SaveStoreItemToArray(AndroidStoreUnitExplicit androidStoreUnitExplicit, Integer ID) throws JSONException {

        try {

            String storeUnitExplicits = dbManager.getValue( "AndroidStoreUnitsExplicit");
            String storeNameURN = mStoreNameURN;

            ArrayList<AndroidStoreUnitExplicit> androidStoreUnitExplicitsAll = JsonUtil.parseJsonArrayAndroidStoreUnitExplicitAll(storeUnitExplicits);

            ArrayList<AndroidStoreUnitExplicit> androidStoreUnitExplicits = JsonUtil.parseJsonArrayAndroidStoreUnitExplicit(storeUnitExplicits, storeNameURN);

            //Convert request to appointment
            AndroidStoreUnitExplicit findStoreUnit = findAndroidStoreUnitExplicitByID(ID, androidStoreUnitExplicits);

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
            String message = ex.toString();
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

}
