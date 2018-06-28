package com.dsouchon.wayo.visualfusion;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;


public class YesNoUnit extends AppCompatActivity {
    private DBManager dbManager;
    public int mAcceptedByStore;
    public int mItemTypeID;
    public int mStoreItemID;
    public String mStoreNameURN;
    public String mWhyNo;
    public int mQuantity;
    public int mQuantitySelected;
    public int mImageCount;
    public int NotAccessory;

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
        setContentView(R.layout.activity_yes_no_unit);
        mItemTypeID = getIntent().getIntExtra("ItemTypeID",0);
        mStoreItemID = getIntent().getIntExtra("StoreItemID",0);
        mStoreNameURN = getIntent().getStringExtra("StoreNameURN");
        mWhyNo = getIntent().getStringExtra("WhyNo");
        mQuantitySelected = getIntent().getIntExtra("QuantitySelected",0);
        mQuantity = getIntent().getIntExtra("MaxQuantity",0);
        mImageCount = getIntent().getIntExtra("ImageCount",0);


        NotAccessory = getIntent().getIntExtra("Enumerate", 1);

        RadioButton radioYes = (RadioButton)findViewById(R.id.radioYes);
        RadioButton radioNo = (RadioButton)findViewById(R.id.radioNo);

        int acceptedByStore = getIntent().getIntExtra("AcceptedByStore",-1);

        TextView txtWhy = (TextView)  findViewById(R.id.txtWhyNo);
        TextView txtWhat = (TextView)  findViewById(R.id.txtWhat);
        Button btnUnitLocationUpload = (Button) findViewById(R.id.btnUnitLocationUpload);
        EditText editWhyNo = (EditText)  findViewById(R.id.editWhyNo);
        Button btnSubmit = (Button) findViewById(R.id.btnSubmit);
        editWhyNo.setText(mWhyNo);

        final EditText editQuantity = (EditText)  findViewById(R.id.editQuantity);

        editQuantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    //Toast.makeText(getApplicationContext(), "unfocus", Toast.LENGTH_SHORT).show();
                    if(mAcceptedByStore == 1 && NotAccessory==0)
                    {
                        editQuantity.setText(Integer.toString(mQuantity));
                    }
                    if(mAcceptedByStore == 0 && NotAccessory==0)
                    {
                        editQuantity.setText("0");
                    }

                }
            }
        });


        if(mQuantitySelected == 0)
        {
            editQuantity.setText(Integer.toString(0));
        }
        else
        {
            editQuantity.setText(Integer.toString(mQuantitySelected));

        }


        editQuantity.setHint("[" + Integer.toString(mQuantity) + "]");
        editQuantity.setFilters(new InputFilter[]{ new InputFilterMinMax("0", Integer.toString(mQuantity))});

        switch(acceptedByStore)
        {
            case -1:  mAcceptedByStore = -1;
                //Hide
                btnUnitLocationUpload.setVisibility(View.INVISIBLE);
                txtWhat.setVisibility(View.INVISIBLE);

                //Hide
                txtWhy.setVisibility(View.INVISIBLE);
                editWhyNo.setVisibility(View.INVISIBLE);
                btnSubmit.setVisibility(View.INVISIBLE);


                break;
            case 1: radioYes.setChecked(true);
                radioNo.setChecked(false);
                mAcceptedByStore = 1;

                //Show
                btnUnitLocationUpload.setVisibility(View.VISIBLE);
                txtWhat.setVisibility(View.VISIBLE);

                //Hide
                txtWhy.setVisibility(View.INVISIBLE);
                editWhyNo.setVisibility(View.INVISIBLE);
                if(mImageCount > 0) {
                    btnSubmit.setVisibility(View.VISIBLE);
                }
                else {

                    btnSubmit.setVisibility(View.INVISIBLE);
                }
                //if(NotAccessory==0){
                    //btnSubmit.setVisibility(View.VISIBLE);
                    //btnUnitLocationUpload.setVisibility(View.INVISIBLE);
                //}

                break;
            case 0:  radioYes.setChecked(false);
                radioNo.setChecked(true);
                mAcceptedByStore = 0;
                //Show
                txtWhy.setVisibility(View.VISIBLE);
                editWhyNo.setVisibility(View.VISIBLE);
                btnSubmit.setVisibility(View.VISIBLE);
                //Hide
                btnUnitLocationUpload.setVisibility(View.INVISIBLE);
                txtWhat.setVisibility(View.INVISIBLE);
                if(NotAccessory==0){
                    btnSubmit.setVisibility(View.VISIBLE);
                    btnUnitLocationUpload.setVisibility(View.INVISIBLE);
                    //Hide
                    txtWhy.setVisibility(View.INVISIBLE);
                    editWhyNo.setVisibility(View.INVISIBLE);
                }
                break;

            default: break;
        }


    }

    public void onRadioButtonClicked(View view) throws JSONException {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        TextView txtWhy = (TextView)  findViewById(R.id.txtWhyNo);
        TextView txtWhat = (TextView)  findViewById(R.id.txtWhat);
        Button btnUnitLocationUpload = (Button) findViewById(R.id.btnUnitLocationUpload);
        EditText editWhyNo = (EditText)  findViewById(R.id.editWhyNo);
        Button btnSubmit = (Button) findViewById(R.id.btnSubmit);
        EditText editQuantity = (EditText)  findViewById(R.id.editQuantity);
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioYes:
                if (checked)
                    mAcceptedByStore = 1;
                //Show
                btnUnitLocationUpload.setVisibility(View.VISIBLE);
                txtWhat.setVisibility(View.VISIBLE);

                //Hide
                txtWhy.setVisibility(View.INVISIBLE);
                editWhyNo.setVisibility(View.INVISIBLE);
                if(mImageCount > 0) {
                    btnSubmit.setVisibility(View.VISIBLE);
                }
                else {

                    btnSubmit.setVisibility(View.INVISIBLE);
                }
               //Accessories jump to max qty if accepted = Yes
                if(NotAccessory==0) {
                    editQuantity.setText(Integer.toString(mQuantity));


                }
                //if(NotAccessory==0){
                //    btnSubmit.setVisibility(View.VISIBLE);
                //    btnUnitLocationUpload.setVisibility(View.INVISIBLE);
                //}
                break;
            case R.id.radioNo:
                if (checked)
                    mAcceptedByStore = 0;
                //Show
                txtWhy.setVisibility(View.VISIBLE);
                editWhyNo.setVisibility(View.VISIBLE);
                btnSubmit.setVisibility(View.VISIBLE);
                //Hide
                btnUnitLocationUpload.setVisibility(View.INVISIBLE);
                txtWhat.setVisibility(View.INVISIBLE);

                editQuantity.setText("0");
                mQuantitySelected = 0;
                try {
                    mQuantitySelected = Integer.parseInt(editQuantity.getText().toString());
                    if(mQuantitySelected>0)
                    {
                        editQuantity.setText("0");
                    }
                    //Accessories jump to max qty if accepted = Yes
                    if(NotAccessory==0) {
                        editQuantity.setText("0");
                        txtWhy.setVisibility(View.INVISIBLE);
                        editWhyNo.setVisibility(View.INVISIBLE);
                        btnSubmit.setVisibility(View.INVISIBLE);



                    }
                    if(NotAccessory==0){
                        btnSubmit.setVisibility(View.VISIBLE);
                        btnUnitLocationUpload.setVisibility(View.INVISIBLE);
                    }
                }
                catch(Exception c){
                    Toast.makeText(getApplicationContext(),c.toString(),Toast.LENGTH_LONG).show();
                }

                break;
            default: mAcceptedByStore = -1;
                break;
        }

      //  FindUnitAndChange(mStoreItemID, mStoreNameURN);

    }

    public void ClearRadio(View view) {


        RadioGroup radioCommitted = (RadioGroup)findViewById(R.id.radioCommitted);
        radioCommitted.clearCheck();

        TextView txtWhy = (TextView)  findViewById(R.id.txtWhyNo);
        TextView txtWhat = (TextView)  findViewById(R.id.txtWhat);
        Button btnUnitLocationUpload = (Button) findViewById(R.id.btnUnitLocationUpload);
        EditText editWhyNo = (EditText)  findViewById(R.id.editWhyNo);
        Button btnSubmit = (Button) findViewById(R.id.btnSubmit);

        //Hide
        btnUnitLocationUpload.setVisibility(View.INVISIBLE);
        txtWhat.setVisibility(View.INVISIBLE);

        //Hide
        txtWhy.setVisibility(View.INVISIBLE);
        editWhyNo.setVisibility(View.INVISIBLE);
        btnSubmit.setVisibility(View.INVISIBLE);

        mAcceptedByStore = -1;
    }

    public void UploadUnitImage(View view) {

        EditText editQuantity = (EditText)  findViewById(R.id.editQuantity);
        if(mAcceptedByStore==1 && NotAccessory ==0 && mQuantitySelected<mQuantity)
        {
            Toast.makeText(getApplicationContext(),String.format("Accessory quantity must be %d if selected = YES. Quantity will be changed from %d to %d. Click Upload Unit Image again to continue.", mQuantity, mQuantitySelected, mQuantity), Toast.LENGTH_LONG).show();
            editQuantity.setText(Integer.toString(mQuantity));

            mQuantitySelected = mQuantity;
        }
        else {

            //Intent intent = new Intent(YesNoUnit.this, UploadPhoto.class );


            //intent.putExtra("Enumerate", getIntent().getStringExtra("Enumerate"));

            Integer storeItemID = getIntent().getIntExtra("StoreItemID", 0);
            //intent.putExtra("StoreItemID", storeItemID);


            EditText editWhyNo = (EditText) findViewById(R.id.editWhyNo);

            mWhyNo = editWhyNo.getText().toString();

            mQuantitySelected = 0;
            try {
                mQuantitySelected = Integer.parseInt(editQuantity.getText().toString());
            } catch (Exception c) {
                mQuantitySelected = 0;
            }
            if (mAcceptedByStore == 1 && mQuantitySelected == 0 && NotAccessory == 1) {
                Toast.makeText(getApplicationContext(), "Please enter Quantity.", Toast.LENGTH_LONG).show();
            } else {
                try {
                    FindUnitAndChange(mStoreItemID, mStoreNameURN);
                    SaveChanges();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // startActivity(intent);
                // finish();
            }
        }
    }

    public void Uploads(View view) {

        EditText editQuantity = (EditText)  findViewById(R.id.editQuantity);
        if(mAcceptedByStore==1 && NotAccessory ==0 && mQuantitySelected<mQuantity)
        {
            Toast.makeText(getApplicationContext(),String.format("Accessory quantity must be %d if selected = YES. Quantity will be changed from %d to %d. Click Submit again to continue.", mQuantity, mQuantitySelected, mQuantity), Toast.LENGTH_LONG).show();
            editQuantity.setText(Integer.toString(mQuantity));

            mQuantitySelected = mQuantity;
        }
        else {
            Intent me = getIntent();
            Intent intent = new Intent(YesNoUnit.this, UnitryUploadsOnly.class);
            //Sending data to another Activity

            intent.putExtra("StoreName", me.getStringExtra("StoreName"));
            intent.putExtra("StoreNameURN", me.getStringExtra("StoreNameURN"));
            intent.putExtra("URN", me.getStringExtra("URN"));

            Integer storeID = me.getIntExtra("StoreID", 0);
            intent.putExtra("StoreID", storeID);
            intent.putExtra("RequestID", me.getIntExtra("RequestID", 0));


            intent.putExtra("AcceptedByStore", mAcceptedByStore);
            EditText editWhyNo = (EditText) findViewById(R.id.editWhyNo);
            intent.putExtra("WhyNo", editWhyNo.getText().toString());
            mWhyNo = editWhyNo.getText().toString();

            mQuantitySelected = 0;
            try {
                mQuantitySelected = Integer.parseInt(editQuantity.getText().toString());
                if (mAcceptedByStore == 0) {
                    mQuantitySelected = 0;
                }

            } catch (Exception c) {
                Toast.makeText(getApplicationContext(), c.toString(), Toast.LENGTH_LONG).show();
            }

            if (mWhyNo.isEmpty() && mAcceptedByStore == 0 && NotAccessory == 1) {
                Toast.makeText(getApplicationContext(), "Please enter the reason.", Toast.LENGTH_LONG).show();

            } else {

                if (mAcceptedByStore == 1 && mQuantitySelected == 0 && NotAccessory == 1) {
                    Toast.makeText(getApplicationContext(), "Please enter Quantity.", Toast.LENGTH_LONG).show();
                } else {
                    Integer storeItemID = getIntent().getIntExtra("StoreItemID", 0);
                    intent.putExtra("StoreItemID", storeItemID);

                    try {
                        FindUnitAndChange(mStoreItemID, mStoreNameURN);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    startActivity(intent);
                    finish();
                }
            }
        }
    }

    public void Cancel(View view) {



        Intent me = getIntent();
        Intent intent = new Intent(YesNoUnit.this, UnitryUploadsOnly.class );
        //Sending data to another Activity

        intent.putExtra("StoreName", me.getStringExtra("StoreName"));
        intent.putExtra("StoreNameURN", me.getStringExtra("StoreNameURN"));
        intent.putExtra("URN", me.getStringExtra("URN"));

        Integer storeID = me.getIntExtra("StoreID", 0);
        intent.putExtra("StoreID", storeID);
        intent.putExtra("RequestID", me.getIntExtra("RequestID",0));

        intent.putExtra("AcceptedByStore", mAcceptedByStore);

        Integer storeItemID =  getIntent().getIntExtra("StoreItemID",0);
        intent.putExtra("StoreItemID", storeItemID);
        EditText editQuantity = (EditText)  findViewById(R.id.editQuantity);
        mQuantitySelected = 0;
        try {
            mQuantitySelected = Integer.parseInt(editQuantity.getText().toString());
        }
        catch(Exception c){
            mQuantitySelected =0;
        }
        try {
            FindUnitAndChange(mStoreItemID, mStoreNameURN);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        startActivity(intent); finish();
    }





    public AndroidStoreUnitExplicit findStoreUnitByID(int id, ArrayList<AndroidStoreUnitExplicit> requests){
        for (AndroidStoreUnitExplicit request : requests) {
            if (request.getStoreItemID() == id) {
                return request;
            }
        }
        return null;
    }
    public void FindUnitAndChange( Integer ID, String storeNameURN) throws JSONException {

        try {

            //get them all
            String storesString = dbManager.getValue( "AndroidStoreUnitsExplicit");
            ArrayList<AndroidStoreUnitExplicit> androidStoreUnitExplicits = JsonUtil.parseJsonArrayAndroidStoreUnitExplicit(storesString, storeNameURN);
            //Get this one
            AndroidStoreUnitExplicit unitExplicit = findStoreUnitByID(ID, androidStoreUnitExplicits);

            //get pos
            Integer position = 0;
            Integer foundposition = 0;

            Iterator<AndroidStoreUnitExplicit> it = androidStoreUnitExplicits.iterator();
            while (it.hasNext()) {
                AndroidStoreUnitExplicit user = it.next();
                Integer findstoreItemID = unitExplicit.getStoreItemID();
                Integer nextStoreItemID = user.getStoreItemID();
                position++;
                if (findstoreItemID.equals(nextStoreItemID)) {
                    it.remove();
                    foundposition=position-1;
                }
            }

            //Clone

            AndroidStoreUnitExplicit storeUnitExplicit = new AndroidStoreUnitExplicit();
            storeUnitExplicit.setStoreName(unitExplicit.getStoreName());
            storeUnitExplicit.setStoreID( unitExplicit.getStoreID());
            storeUnitExplicit.setMaxQuantityFromMatrix( unitExplicit.getMaxQuantityFromMatrix());
            storeUnitExplicit.setImagePath( unitExplicit.getImagePath());
            storeUnitExplicit.setStoreItemID( unitExplicit.getStoreItemID());
            storeUnitExplicit.setIsSurvey(unitExplicit.getIsSurvey());

            storeUnitExplicit.setChkInstalled(unitExplicit.getChkInstalled());
            storeUnitExplicit.setChkDelivered(unitExplicit.getChkDelivered());

            storeUnitExplicit.setBarcode(unitExplicit.getBarcode());
            storeUnitExplicit.setEnumerate(unitExplicit.getEnumerate());
            storeUnitExplicit.setImageCount(unitExplicit.getImageCount());
            storeUnitExplicit.setItemTypeName(unitExplicit.getItemTypeName());
            storeUnitExplicit.setURN(unitExplicit.getURN());
            
            storeUnitExplicit.setQuantitySelected(mQuantitySelected);
            
            //storeUnitExplicit.setWhyNo(unitExplicit.getWhyNo());
            //delete original
            androidStoreUnitExplicits.remove(unitExplicit);
            //make changes to clone
            storeUnitExplicit.setAcceptedByStore(mAcceptedByStore);
            storeUnitExplicit.setWhyNo(mWhyNo);
            //add to list

            if(foundposition == position) {foundposition = position -1;}

            androidStoreUnitExplicits.add(foundposition, storeUnitExplicit);
            //Save back to local
            dbManager.setValue( "AndroidStoreUnitsExplicit", JsonUtil.convertAndroidStoreUnitExplicitArrayToJson(androidStoreUnitExplicits));


        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
        }
        //

    }



    private void SaveChanges() {
        String isWebsiteAvailable = dbManager.getValue( "AmIOnline");

        if(isWebsiteAvailable.equals("True")) {
            //Save My Appointments - this will also 'delete' the request online
            MySOAPCallActivity cs1 = new MySOAPCallActivity();
            try {

                SaveAndroidUnitExplicitsParams params = new SaveAndroidUnitExplicitsParams(cs1, dbManager.getValue("AndroidStoreUnitsExplicit"));

                new CallSoapSaveAndroidUnitExplicits().execute(params);
            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();


            }

        }
    }




    public class CallSoapSaveAndroidUnitExplicits extends AsyncTask<SaveAndroidUnitExplicitsParams, Void, String> {

        private Exception exception;

        @Override
        protected String doInBackground(SaveAndroidUnitExplicitsParams... params) {
            return params[0].foo.SaveAndroidUnitExplicits(params[0].jsonStoreItems);
        }

        protected void onPostExecute(String result) {
            // TODO: check this.exception
            // TODO: do something with the feed
            try {
                Intent intent = new Intent(YesNoUnit.this, UploadPhoto.class );


                intent.putExtra("Enumerate", getIntent().getStringExtra("Enumerate"));

                Integer storeItemID =  getIntent().getIntExtra("StoreItemID",0);
                intent.putExtra("StoreItemID", storeItemID);

                intent.putExtra("StoreName", getIntent().getStringExtra("StoreName"));
                intent.putExtra("StoreNameURN", getIntent().getStringExtra("StoreNameURN"));
                intent.putExtra("URN", getIntent().getStringExtra("URN"));

                intent.putExtra("StoreID", getIntent().getStringExtra("StoreID"));

                intent.putExtra("ImageType", "SurveyStoreItemLocation");
                intent.putExtra("Barcode",getIntent().getStringExtra("Barcode"));

                intent.putExtra("RequestID", getIntent().getIntExtra("RequestID",0));

                intent.putExtra("AcceptedByStore", mAcceptedByStore);
                EditText editWhyNo = (EditText)  findViewById(R.id.editWhyNo);
                intent.putExtra("WhyNo", editWhyNo.getText().toString());
                mWhyNo = editWhyNo.getText().toString();
                EditText editQuantity = (EditText)  findViewById(R.id.editQuantity);
                mQuantitySelected = 0;

                startActivity(intent);
                finish();

            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();

            }

        }



    }
    private static class SaveAndroidUnitExplicitsParams {
        MySOAPCallActivity foo;
        String jsonStoreItems;



        SaveAndroidUnitExplicitsParams(MySOAPCallActivity foo, String jsonStoreItems) {
            this.foo = foo;
            this.jsonStoreItems = jsonStoreItems;


        }
    }

}
