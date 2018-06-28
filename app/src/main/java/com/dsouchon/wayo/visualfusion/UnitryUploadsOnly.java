package com.dsouchon.wayo.visualfusion;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;

public class UnitryUploadsOnly extends AppCompatActivity {
    public boolean mIsSurvey = false;
    public  Integer mDocCount =0;
    GridView gridView;
    InstallGridViewUnitary gridViewCustomAdapter;
    ProgressDialog progressDialog;
    public Integer RequestID;
    public static ArrayList<AndroidStoreUnitExplicit> staticUnits;
    private DBManager dbManager;
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
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
    public void onCreate(Bundle savedInstanceState) {
//         super.onCreate(savedInstanceState);  dbManager = new DBManager(this);        dbManager.open();
//        setContentView(R.layout.activity_unitry_uploads_only);
//
//        GridView gridview = (GridView) findViewById(R.id.gridview);
//        gridview.setAdapter(new ImageAdapter(this));
//
//        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View v,
//                                    int position, long id) {
//                Toast.makeText(UnitryUploadsOnly.this, "" + position,
//                        Toast.LENGTH_SHORT).show();
//            }
//        });


         super.onCreate(savedInstanceState);  dbManager = new DBManager(this);        dbManager.open();

        setContentView(R.layout.activity_unitry_uploads_only);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        String primaryRole = dbManager.getValue( "PrimaryRole");
        if (primaryRole.equals("Srv")){
            mIsSurvey = true;
        }

        if(mIsSurvey){
            //getActionBar().setTitle("Survey Unitary List");
            getSupportActionBar().setTitle("Survey Unitry List");  //


        }
        else{

        }

        Intent me = getIntent();

        String ToastMessage = me.getStringExtra("Toast");
        if(!TextUtils.isEmpty(ToastMessage)) {
            Toast.makeText(this, ToastMessage, Toast.LENGTH_LONG).show();
        }

    //    TextView txtStoreName = (TextView)findViewById(R.id.txtStoreName);
        //TextView txtURN = (TextView)findViewById(R.id.txtURN);

   //     txtStoreName.setText(me.getStringExtra("StoreNameURN"));

        int StoreID = me.getIntExtra("StoreID",0);


        RequestID = me.getIntExtra("RequestID",0);

        staticUnits = getstoreUnitsExplicit(me.getStringExtra("StoreNameURN"));

        Collections.sort(staticUnits, new CustomComparator());


        gridView=(GridView)findViewById(R.id.gridview);
        // Create the Custom Adapter Object
        //gridViewCustomAdapter = new InstallGridViewUnitary(this, data, mIsSurvey);
        // Set the Adapter to GridView
        //gridView.setAdapter(gridViewCustomAdapter);
        gridView.setAdapter(new ImageAdapter(this, staticUnits, mIsSurvey));




        //sets where list item is clicking too
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int pos, long id) {

                Intent intent = new Intent();

                if(!mIsSurvey) {
                    intent = new Intent(UnitryUploadsOnly.this, UploadPhotoPrelim.class);
                }
                else {
                    intent = new Intent(UnitryUploadsOnly.this, YesNoUnit.class );
                }

                //Sending data to another Activity
                AndroidStoreUnitExplicit storeunit = staticUnits.get(pos);

                Integer Enumerate = storeunit.getEnumerate();
                intent.putExtra("Enumerate", Enumerate);

                Integer storeItemID = storeunit.getStoreItemID();

                intent.putExtra("StoreName", storeunit.getStoreName());
                intent.putExtra("StoreNameURN", storeunit.getStoreNameURN());


                intent.putExtra("AcceptedByStore", storeunit.getAcceptedByStore());
                intent.putExtra("WhyNo", storeunit.getWhyNo());
                intent.putExtra("URN", storeunit.getURN());

                intent.putExtra("StoreID", storeunit.getStoreID());
                intent.putExtra("StoreItemID", storeItemID);
                intent.putExtra("ImageType", "StoreItemInStore");
                intent.putExtra("Barcode", storeunit.getBarcode());

                intent.putExtra("RequestID", RequestID);
                intent.putExtra("ID", RequestID);

                intent.putExtra("MaxQuantity", storeunit.getMaxQuantityFromMatrix());
                intent.putExtra("QuantitySelected", storeunit.getQuantitySelected());
                intent.putExtra("ImageCount", storeunit.getImageCount());

                Intent me = getIntent();
                intent.putExtra("StoreName", me.getStringExtra("StoreName"));
                intent.putExtra("StoreNameURN", me.getStringExtra("StoreNameURN"));
                intent.putExtra("URN", me.getStringExtra("URN"));

                Integer storeID = me.getIntExtra("StoreID", 0);
                intent.putExtra("StoreID", storeID);
                intent.putExtra("RequestID", me.getIntExtra("RequestID",0));


                startActivity(intent); finish();

            }

        } );


    }
    private ArrayList<AndroidStoreUnitExplicit> getstoreUnitsExplicit(String storeNameURN) {
        ArrayList<AndroidStoreUnitExplicit> storeUnitExplicits = new ArrayList<>();
        String json = "[]";
        json = dbManager.getValue( "AndroidStoreUnitsExplicit");
        try {
            storeUnitExplicits = JsonUtil.parseJsonArrayAndroidStoreUnitExplicit(json, storeNameURN);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return storeUnitExplicits;
    }


    public void Close (View view){

        Intent intent = new Intent(UnitryUploadsOnly.this, InstallSetAppointment.class );
        ArrayList<AndroidAppointment> myappointments;

        myappointments = getAppointmentsData();

        //Sending data to another Activity
        RequestID = Integer.parseInt(dbManager.getValue( "RequestID"));

        AndroidAppointment appointment = findAppointmentByID(RequestID, myappointments);

        intent.putExtra("ID", appointment.getID());
        intent.putExtra("Mileage", appointment.getMileage());
        intent.putExtra("StoreID", appointment.getStoreID());
        intent.putExtra("BrandName", appointment.getBrandName());
        intent.putExtra("OutletTypeName", appointment.getOutletTypeName());
        intent.putExtra("TierTypeName", appointment.getTierTypeName());
        intent.putExtra("JobCardCount", appointment.getJobCardCount());
        intent.putExtra("ExteriorImageCount", appointment.getExteriorImageCount());
        intent.putExtra("InteriorImageCount", appointment.getInteriorImageCount());
        intent.putExtra("SurveySignatureCount", appointment.getSurveySignatureCount());
        intent.putExtra("PerformanceReviewSignatureCount", appointment.getPerformanceReviewSignatureCount());


        intent.putExtra("RequestTypeName", appointment.getRequestTypeName());
        intent.putExtra("StoreName", appointment.getStoreName());
        intent.putExtra("StoreNameURN", appointment.getStoreNameURN());

        intent.putExtra("URN", appointment.getURN());
        intent.putExtra("CurrentPhase", appointment.getCurrentPhase());
        intent.putExtra("ContactPerson", appointment.getContactPerson());
        intent.putExtra("ContactEmail", appointment.getContactEmail());
        intent.putExtra("ContactPhone", appointment.getContactPhone());
        intent.putExtra("OpeningTime", appointment.getOpeningTime());
        intent.putExtra("ClosingTime", appointment.getClosingTime());
        intent.putExtra("TotalUnitCount", appointment.getTotalUnitCount());
        intent.putExtra("StoreID", appointment.getStoreID());
        intent.putExtra("DateRequested", appointment.getDateRequested());
        intent.putExtra("DateAccepted", appointment.getDateAccepted());
        intent.putExtra("AppointmentDateTime", appointment.getAppointmentDateTime());
        intent.putExtra("AppointmentDateDay", appointment.getAppointmentDateDay());
        intent.putExtra("AppointmentDateMonth", appointment.getAppointmentDateMonth());
        intent.putExtra("AppointmentDateYear", appointment.getAppointmentDateYear());
        intent.putExtra("AppointmentDateTimeTime", appointment.getAppointmentDateTimeTime());
        intent.putExtra("DateConfirmed", appointment.getDateConfirmed());
        intent.putExtra("DateRecordChanged", appointment.getDateRecordChanged());

        intent.putExtra("Address", appointment.getAddress());
        intent.putExtra("Region", appointment.getRegion());

        intent.putExtra("BrandName", appointment.getBrandName());
        intent.putExtra("OutletTypeName", appointment.getOutletTypeName());
        intent.putExtra("TierTypeName", appointment.getTierTypeName());

        intent.putExtra("JobCardCount", appointment.getJobCardCount());
        intent.putExtra("ExteriorImageCount", appointment.getExteriorImageCount());
        intent.putExtra("InteriorImageCount", appointment.getInteriorImageCount());
        intent.putExtra("SurveySignatureCount", appointment.getSurveySignatureCount());
        intent.putExtra("PerformanceReviewSignatureCount", appointment.getPerformanceReviewSignatureCount());

        startActivity(intent); finish();


    }

    @Override
    public void onBackPressed() {

        SaveChanges();

    }



    public void clickGoToSetAppointment(View view) throws JSONException {

        SaveChanges();



    }

    private void SaveChanges() {
        String isWebsiteAvailable = dbManager.getValue( "AmIOnline");

        if(isWebsiteAvailable.equals("True")) {
            //Save My Appointments - this will also 'delete' the request online
            MySOAPCallActivity cs1 = new MySOAPCallActivity();
            try {

                SaveAndroidUnitExplicitsParams params = new SaveAndroidUnitExplicitsParams(cs1, dbManager.getValue("AndroidStoreUnitsExplicit"));

                new CallSoapSaveAndroidUnitExplicits().execute(params);

                //set and remove prrogress bar
                progressDialog = new ProgressDialog(UnitryUploadsOnly.this);
                progressDialog.setMessage("Saving Unitry Changes"); // Setting Message
                progressDialog.setTitle("Please Wait..."); // Setting Title
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                progressDialog.show(); // Display Progress Dialog

                progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                progressDialog.setCancelable(false);
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            Thread.sleep(10000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        // progressDialog.dismiss();
                    }
                }).start();



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
                Intent intent = new Intent(UnitryUploadsOnly.this, InstallSetAppointment.class);

                //Surprise: Now We Do the Performance Check instead if its an installation
                // if(!mIsSurvey) {
                //    intent = new Intent(InstallUnitaryList.this, NewFavorites1.class);
                //}

                //Sending data to another Activity
                String storesString = dbManager.getValue( "AndroidInsAppointments");

                ArrayList<AndroidAppointment> appointments = JsonUtil.parseJsonArrayAndroidAppointment(storesString);

                Intent me = getIntent();
                RequestID = Integer.parseInt(dbManager.getValue( "RequestID"));


                //Convert request to appointment
                AndroidAppointment appointment = findAppointmentByID(RequestID, appointments);
                intent.putExtra("ID", appointment.getID());
                intent.putExtra("RequestID", appointment.getID());
                intent.putExtra("Mileage", appointment.getMileage());
                intent.putExtra("StoreID", appointment.getStoreID());


                intent.putExtra("RequestTypeName", appointment.getRequestTypeName());
                intent.putExtra("StoreName", appointment.getStoreName());
                intent.putExtra("BrandName", appointment.getBrandName());
                intent.putExtra("OutletTypeName", appointment.getOutletTypeName());
                intent.putExtra("TierTypeName", appointment.getTierTypeName());

                intent.putExtra("StoreNameURN", appointment.getStoreNameURN());

                intent.putExtra("URN", appointment.getURN());

                intent.putExtra("JobCardCount", appointment.getJobCardCount());
                intent.putExtra("ExteriorImageCount", appointment.getExteriorImageCount());
                intent.putExtra("InteriorImageCount", appointment.getInteriorImageCount());
                intent.putExtra("SurveySignatureCount", appointment.getSurveySignatureCount());
                intent.putExtra("PerformanceReviewSignatureCount", appointment.getPerformanceReviewSignatureCount());

                intent.putExtra("CurrentPhase", appointment.getCurrentPhase());
                intent.putExtra("ContactPerson", appointment.getContactPerson());
                intent.putExtra("ContactEmail", appointment.getContactEmail());
                intent.putExtra("ContactPhone", appointment.getContactPhone());
                intent.putExtra("OpeningTime", appointment.getOpeningTime());
                intent.putExtra("ClosingTime", appointment.getClosingTime());
                intent.putExtra("TotalUnitCount", appointment.getTotalUnitCount());
                intent.putExtra("StoreID", appointment.getStoreID());
                intent.putExtra("DateRequested", appointment.getDateRequested());
                intent.putExtra("DateAccepted", appointment.getDateAccepted());
                intent.putExtra("AppointmentDateTime", appointment.getAppointmentDateTime());
                intent.putExtra("AppointmentDateDay", appointment.getAppointmentDateDay());
                intent.putExtra("AppointmentDateMonth", appointment.getAppointmentDateMonth());
                intent.putExtra("AppointmentDateYear", appointment.getAppointmentDateYear());
                intent.putExtra("AppointmentDateTimeTime", appointment.getAppointmentDateTimeTime());
                intent.putExtra("DateConfirmed", appointment.getDateConfirmed());
                intent.putExtra("DateRecordChanged", appointment.getDateRecordChanged());

                intent.putExtra("Address", appointment.getAddress());
                intent.putExtra("Region", appointment.getRegion());

                intent.putExtra("BrandName", appointment.getBrandName());
                intent.putExtra("OutletTypeName", appointment.getOutletTypeName());
                intent.putExtra("TierTypeName", appointment.getTierTypeName());
                intent.putExtra("JobCardCount", appointment.getJobCardCount());
                intent.putExtra("ExteriorImageCount", appointment.getExteriorImageCount());
                intent.putExtra("InteriorImageCount", appointment.getInteriorImageCount());
                intent.putExtra("SurveySignatureCount", appointment.getSurveySignatureCount());
                intent.putExtra("PerformanceReviewSignatureCount", appointment.getPerformanceReviewSignatureCount());

                if(progressDialog !=null)
                {
                    progressDialog.dismiss();
                }


                startActivity(intent); finish();



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




    private ArrayList<AndroidAppointment> getAppointmentsData() {
        ArrayList<AndroidAppointment> appointments = new ArrayList<>();
        String json = "";
        json = dbManager.getValue( "AndroidInsAppointments");
        try {
            appointments = JsonUtil.parseJsonArrayAndroidAppointment(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return appointments;
    }
    public AndroidAppointment findAppointmentByID(int id, ArrayList<AndroidAppointment> requests){
        for (AndroidAppointment request : requests) {
            if (request.getID() == id) {
                return request;
            }
        }
        return null;
    }





    //END LOADER


}

