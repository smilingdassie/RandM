package com.dsouchon.wayo.visualfusion;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;

import android.content.DialogInterface;
import android.location.Location;

import android.location.LocationManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;


public class InstallSetAppointment extends AppCompatActivity   {
    ProgressDialog progressDialog;
    private DBManager dbManager;
    private static final String TAG = "GpsGet";
    private TextView mLatitudeTextView;
    private TextView mLongitudeTextView;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager mLocationManager;
    public int RequestID;
    private LocationRequest mLocationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 1000 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    private LocationManager locationManager;

    private int hour;
    private int minute;

    public boolean mIsSurvey = false;

    static final int TIME_DIALOG_ID = 999;
    static final int DATE_DIALOG_ID = 0;

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

    @Override  protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);  dbManager = new DBManager(this);        dbManager.open();
        //setContentView(R.layout.install_set_appointment_layout);

        //new check for is survey
        String primaryRole = dbManager.getValue( "PrimaryRole");
        if (primaryRole.equals("Srv")){
            mIsSurvey = true;
        }



        setContentView(R.layout.install_set_appointment_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if(mIsSurvey){
            Button btnSurvey =  (Button) findViewById(R.id.btnSurvey);
            btnSurvey.setVisibility(View.VISIBLE);
            // getActionBar().setTitle("Survey Appointment");
            getSupportActionBar().setTitle("Survey Appointment");  //

        }
        else
        {
            if(dbManager.getValue("IsForeman").equals("True")) {
                Button btnPerformanceReview = (Button) findViewById(R.id.btnPerformanceReview);
                btnPerformanceReview.setVisibility(View.VISIBLE);
            }
        }

        String Today = dbManager.getValue( "Today");
        String CheckedIn = getIntent().getStringExtra("URN")+":CheckedIn:"+Today;
        Button btnCheckin =  (Button) findViewById(R.id.btnCheckin);
        if(dbManager.getValue(CheckedIn).equals("True") && btnCheckin.getVisibility()== View.VISIBLE){
            //Toast.makeText(this, "Only one checkin allowed per store per day.", Toast.LENGTH_LONG).show();

            btnCheckin.setVisibility(View.GONE);
        }
        else {

            btnCheckin.setVisibility(View.VISIBLE);
        }

        //DS MOVED THIS TO INSTALL HOME GetSpinnerDataFromWS();


        Button btnCheckout =  (Button) findViewById(R.id.btnCheckout);
        String CheckedOut = getIntent().getStringExtra("URN")+":CheckedOut:"+Today;
        if(dbManager.getValue(CheckedOut).equals("True")&& btnCheckout.getVisibility()== View.VISIBLE){
            //Toast.makeText(this, "Only one checkout allowed per store per day.", Toast.LENGTH_LONG).show();

            btnCheckout.setVisibility(View.GONE);
        }
        else {
            if(btnCheckin.getVisibility()==View.GONE) { //Only show checkout button if checkin is complete
                btnCheckout.setVisibility(View.VISIBLE);
            }
        }



        Intent me = getIntent();

        String ToastMessage = me.getStringExtra("Toast");
        if(!TextUtils.isEmpty(ToastMessage)) {
            if(ToastMessage.contains("Thank"))
            {
                Toast.makeText(this, ToastMessage, Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, ToastMessage, Toast.LENGTH_LONG).show();
            }
          /*  if(ToastMessage.toLowerCase().contains("check out successful")){
                //GO TO LIST
                Intent intent = new Intent(InstallSetAppointment.this, InstallHome.class );

                startActivity(intent); finish();
            }*/
        }



        RequestID = Integer.parseInt(dbManager.getValue( "RequestID"));




        Integer storeid = me.getIntExtra("StoreID", -1);
        GetUnitListsForUserExplicit(dbManager.getValue( "UserName"), mIsSurvey, storeid);//installation only - for survey true)


        TextView StoreName =  (TextView)findViewById(R.id.txtStoreName);
        StoreName.setText(me.getStringExtra("StoreName"));
        TextView URN =  (TextView)findViewById(R.id.txtURN);
        URN.setText(me.getStringExtra("URN"));
        //TextView CurrentPhase =  (TextView)findViewById(R.id.txtProgress);
        //CurrentPhase.setText(me.getStringExtra("CurrentPhase"));
        TextView txtCurrentPhase =  (TextView)findViewById(R.id.txtCurrentPhase);
        txtCurrentPhase.setText(me.getStringExtra("CurrentPhase"));

        TextView ContactPerson =  (TextView)findViewById(R.id.txtContactPerson);
        ContactPerson.setText(me.getStringExtra("ContactPerson"));
        TextView ContactEmail =  (TextView)findViewById(R.id.txtContactEmail);
        ContactEmail.setText(me.getStringExtra("ContactEmail"));
        TextView ContactPhone =  (TextView)findViewById(R.id.txtContactPhone);
        ContactPhone.setText(me.getStringExtra("ContactPhone"));
        TextView OpeningTime =  (TextView)findViewById(R.id.txtOpeningTime);
        OpeningTime.setText(parseTimeForDisplay(me.getStringExtra("OpeningTime")));
        TextView ClosingTime =  (TextView)findViewById(R.id.txtClosingTime);
        ClosingTime.setText(parseTimeForDisplay(me.getStringExtra("ClosingTime")));
        TextView TotalUnitCount =  (TextView)findViewById(R.id.txtTotalUnitCount);
        Integer totalUnitCount = me.getIntExtra("TotalUnitCount", -1);
        TotalUnitCount.setText(totalUnitCount.toString());

        TextView txtRegion =  (TextView)findViewById(R.id.txtRegion);
        txtRegion.setText(me.getStringExtra("Region"));

        TextView txtBrand =  (TextView)findViewById(R.id.txtBrand);
        txtBrand.setText(me.getStringExtra("BrandName"));
        TextView txtOutletType =  (TextView)findViewById(R.id.txtOutletType);
        txtOutletType.setText(me.getStringExtra("OutletTypeName"));
        TextView txtTierTypeName =  (TextView)findViewById(R.id.txtTierTypeName);
        txtTierTypeName.setText(me.getStringExtra("TierTypeName"));

        TextView txtAddress =  (TextView)findViewById(R.id.txtAddress);
        txtAddress.setText(me.getStringExtra("Address"));


        //  EditText editMileage = (EditText)findViewById(R.id.editMileage);
        // Integer mileage = me.getIntExtra("Mileage",0);
        // editMileage.setText(mileage.toString());

        String appointmentDateTime = me.getStringExtra("AppointmentDateTime");
        appointmentDateTime = appointmentDateTime.replace('T',' ');

        TextView AppointmentDateTime = (TextView)findViewById(R.id.txtAppointmentDateTime);
        AppointmentDateTime.setText(appointmentDateTime);

        MySOAPCallActivity cs1 = new MySOAPCallActivity();
        GetSurveysParams params = new GetSurveysParams(cs1, RequestID);

        new CallSoapGetSurveys().execute(params);

    }




    protected String parseTimeForDisplay(String dateTimeWithT)
    {

        dateTimeWithT = dateTimeWithT.replace('T',' ');

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm");

        try {
            calendar.setTime(simpleDateFormat.parse(dateTimeWithT));

            String dateToShow = simpleTimeFormat.format(calendar.getTime());
            return dateToShow;

        } catch (ParseException e) {
            e.printStackTrace();
            return dateTimeWithT;
        }


    }

    @Override
    public void onBackPressed() {


        startActivity(new Intent(this,InstallAppointments.class));
        finish();

    }

    public void Survey(View view) {

        Intent me = getIntent();
        Intent intent = new Intent(InstallSetAppointment.this, SurveyActivity.class );
        //Sending data to another Activity

        intent.putExtra("StoreName", me.getStringExtra("StoreName"));
        intent.putExtra("StoreNameURN", me.getStringExtra("StoreNameURN"));
        intent.putExtra("URN", me.getStringExtra("URN"));

        Integer storeID = me.getIntExtra("StoreID", 0);
        intent.putExtra("StoreID", storeID);
        intent.putExtra("RequestID", me.getIntExtra("RequestID",0));


        startActivity(intent); finish();
    }

    public void PerformanceReview(View view) {

        if(!mIsSurvey) {
            try {

                //Surprise: Now We Do the Performance Check instead if its an installation

                Intent intent = new Intent(InstallSetAppointment.this, NewFavorites1.class);


                //Sending data to another Activity
                String storesString = dbManager.getValue( "AndroidInsAppointments");

                ArrayList<AndroidAppointment> appointments = JsonUtil.parseJsonArrayAndroidAppointment(storesString);


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

                dbManager.setValue( "StoreID", Integer.toString(appointment.getStoreID()));

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


                startActivity(intent);
                finish();


            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
            }


        }
    }

    public void Uploads(View view) {

        Intent me = getIntent();
        Intent intent = new Intent(InstallSetAppointment.this, UnitryUploadsOnly.class );
        //Sending data to another Activity

        intent.putExtra("StoreName", me.getStringExtra("StoreName"));
        intent.putExtra("StoreNameURN", me.getStringExtra("StoreNameURN"));
        intent.putExtra("URN", me.getStringExtra("URN"));

        Integer storeID = me.getIntExtra("StoreID", 0);
        intent.putExtra("StoreID", storeID);
        intent.putExtra("RequestID", me.getIntExtra("RequestID",0));


        startActivity(intent); finish();
    }

    public void Uploads2(View view) {

        Intent me = getIntent();
        Intent intent = new Intent(InstallSetAppointment.this, UnitryUploadsOnly.class );
        //Sending data to another Activity

        intent.putExtra("StoreName", me.getStringExtra("StoreName"));
        intent.putExtra("StoreNameURN", me.getStringExtra("StoreNameURN"));
        intent.putExtra("URN", me.getStringExtra("URN"));

        Integer storeID = me.getIntExtra("StoreID", 0);
        intent.putExtra("StoreID", storeID);

        intent.putExtra("RequestID", RequestID);


        startActivity(intent); finish();
    }

    public void OtherUploads(View view) throws JSONException {
        Intent me = getIntent();
        Intent intent = new Intent(InstallSetAppointment.this, OtherUploads.class );
        intent.putExtra("StoreName", me.getStringExtra("StoreName"));
        intent.putExtra("StoreNameURN", me.getStringExtra("StoreNameURN"));
        intent.putExtra("URN", me.getStringExtra("URN"));

        Integer storeID = me.getIntExtra("StoreID", 0);
        intent.putExtra("StoreID", storeID);
        intent.putExtra("RequestID", RequestID);

        String storesString = dbManager.getValue( "AndroidInsAppointments");

        ArrayList<AndroidAppointment> appointments = JsonUtil.parseJsonArrayAndroidAppointment(storesString);

        RequestID = Integer.parseInt(dbManager.getValue( "RequestID"));
        //Convert request to appointment
        AndroidAppointment appointment = findAppointmentByID(RequestID, appointments);

        intent.putExtra("JobCardCount", appointment.getJobCardCount());
        intent.putExtra("ExteriorImageCount", appointment.getExteriorImageCount());
        intent.putExtra("InteriorImageCount", appointment.getInteriorImageCount());
        intent.putExtra("SurveySignatureCount", appointment.getSurveySignatureCount());
        intent.putExtra("PerformanceReviewSignatureCount", appointment.getPerformanceReviewSignatureCount());


        startActivity(intent); finish();
    }

    public void SignOff(View view) {
        Intent me = getIntent();
        Intent intent = new Intent(InstallSetAppointment.this, ContractLast.class );
        intent.putExtra("StoreName", me.getStringExtra("StoreName"));
        intent.putExtra("StoreNameURN", me.getStringExtra("StoreNameURN"));
        intent.putExtra("URN", me.getStringExtra("URN"));

        Integer storeID = me.getIntExtra("StoreID", 0);
        intent.putExtra("StoreID", storeID);
        intent.putExtra("RequestID", RequestID);
        intent.putExtra("IsSurvey", mIsSurvey);

        if(mIsSurvey){
            intent.putExtra("SignatureType", "SurveySignature");
        }
        else
        {
            intent.putExtra("SignatureType", "InstallationSignature");
        }

        startActivity(intent); finish();
    }




    public void HowDatesWork()
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(simpleDateFormat.parse("2014/11/20 8:10:00 AM"));

            Log.e("date", "" + calendar.get(Calendar.DAY_OF_MONTH));

            Log.e("month", ""+calendar.get(Calendar.MONTH));

            Log.e("year", ""+calendar.get(Calendar.YEAR));

            Log.e("hour", ""+calendar.get(Calendar.HOUR));

            Log.e("minutes", ""+calendar.get(Calendar.MINUTE));

            Log.e("seconds", ""+calendar.get(Calendar.SECOND));
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    /** Create a new dialog for date picker */




    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    public AndroidAppointment findAppointmentByID(int id, ArrayList<AndroidAppointment> requests){
        for (AndroidAppointment request : requests) {
            if (request.getID() == id) {
                return request;
            }
        }
        return null;
    }




    public void GpsCheckin(View view)
    {
        Intent intent = new Intent(InstallSetAppointment.this, MapsActivitySlim.class );
        Intent me = getIntent();
        //String storeNameURN = me.getStringExtra("StoreName") + " " + me.getStringExtra("URN");
        //intent.putExtra("StoreNameURN", storeNameURN);
        intent.putExtra("StoreNameURN", me.getStringExtra("StoreNameURN"));
        intent.putExtra("StoreName", me.getStringExtra("StoreName"));
        intent.putExtra("URN",  me.getStringExtra("URN"));
        intent.putExtra("RequestID",  me.getIntExtra("RequestID",0));
        intent.putExtra("IsCheckIn", true);
        startActivity(intent); finish();

    }
    public void GpsCheckOut(View view)
    {
        //First check install status

        String isWebsiteAvailable = dbManager.getValue( "AmIOnline");

        if(isWebsiteAvailable.equals("True")) {
           
            if(!mIsSurvey) {
                MySOAPCallActivity cs1 = new MySOAPCallActivity();
                try {

                    Intent me = getIntent();
                    Integer StoreID = me.getIntExtra("StoreID", 0);

                    IsInstallCompleteParams params = new IsInstallCompleteParams(cs1, StoreID);

                    new CallSoapIsInstallComplete().execute(params);



                    //set and remove prrogress bar
                    progressDialog = new ProgressDialog(InstallSetAppointment.this);
                    progressDialog.setMessage("Saving Gps Checkout"); // Setting Message
                    progressDialog.setTitle("Please Wait..."); // Setting Title
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                    progressDialog.show(); // Display Progress Dialog

                    progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    progressDialog.setCancelable(true);
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
            else{
                MySOAPCallActivity cs1 = new MySOAPCallActivity();
                try {

                    Intent me = getIntent();
                    Integer StoreID = me.getIntExtra("StoreID", 0);

                    IsSurveyCompleteParams params = new IsSurveyCompleteParams(cs1, StoreID);

                    new CallSoapIsSurveyComplete().execute(params);



                    //set and remove prrogress bar
                    progressDialog = new ProgressDialog(InstallSetAppointment.this);
                    progressDialog.setMessage("Saving Survey"); // Setting Message
                    progressDialog.setTitle("Please Wait..."); // Setting Title
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                    progressDialog.show(); // Display Progress Dialog

                    progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    progressDialog.setCancelable(true);
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

    }

    public void GoToCheckout(String isComplete)
    {
        Intent intent = new Intent(InstallSetAppointment.this, InstallGpsCheckOut.class );

        Intent me = getIntent();

        intent.putExtra("StoreNameURN", me.getStringExtra("StoreNameURN"));
        intent.putExtra("StoreName", me.getStringExtra("StoreName"));
        intent.putExtra("URN",  me.getStringExtra("URN"));
        intent.putExtra("RequestID",  me.getIntExtra("RequestID",0));
        intent.putExtra("IsCheckIn", false);

        if(!mIsSurvey) {
            intent.putExtra("IsInstallComplete", isComplete);
        }
        else {
            intent.putExtra("IsSurveyComplete", isComplete);
        }
        startActivity(intent); finish();


    }


    public void ConfirmAppointment(View view) throws JSONException {

        TextView txtID =  (TextView)findViewById(R.id.txtID);
        Integer ID = Integer.parseInt(txtID.getText().toString());

        //Save Appointment changes
        AndroidAppointment s = new AndroidAppointment();

        TextView storeName = (TextView) findViewById(R.id.txtStoreName);
        s.StoreName = storeName.getText().toString();


        TextView ContactPerson = (TextView) findViewById(R.id.txtContactPerson);
        s.ContactPerson = ContactPerson.getText().toString();
        TextView ContactEmail = (TextView) findViewById(R.id.txtContactEmail);
        s.ContactEmail = ContactEmail.getText().toString();
        TextView ContactPhone = (TextView) findViewById(R.id.txtContactPhone);
        s.ContactPhone = ContactPhone.getText().toString();

        //EditText editMileage = (EditText)findViewById(R.id.editMileage);
        //s.setMileage(Integer.parseInt( editMileage.getText().toString()));

        s.setUserName(dbManager.getValue( "UserName"));

        SaveAppointmentToArray(s, ID);

        SaveIfOnline();
        // startActivity(new Intent(this,MainLogin.class));
        Intent intent = new Intent(InstallSetAppointment.this, InstallHome.class );

        startActivity(intent); finish();

    }


    public void SaveAppointmentToArray(AndroidAppointment appointment, Integer ID) throws JSONException {

        try {

            String storesString = dbManager.getValue( "AndroidInsAppointments");

            ArrayList<AndroidAppointment> appointments = JsonUtil.parseJsonArrayAndroidAppointment(storesString);


            AndroidAppointment findappointment = findAppointmentByID(ID, appointments);
            appointment.setRequestTypeName( findappointment.getRequestTypeName());
            appointment.setID( findappointment.getID());
            //appointment.setMileage( findappointment.getMileage());
            appointment.setStoreID( findappointment.getStoreID());
            appointment.setDateRequested( findappointment.getDateRequested());
            appointment.setDateAccepted( findappointment.getDateAccepted());
            appointment.setStoreID( findappointment.getStoreID());
            appointment.setAppointmentDateTime(findappointment.getAppointmentDateTime());
            appointments.remove(findappointment);

            Date recordChangedNow = new Date();

            appointment.setDateRecordChanged((String) android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss a", recordChangedNow));

            appointments.add(appointment);

            //Save Requests back to local
            dbManager.setValue( "AndroidInsAppointments", JsonUtil.convertAppointmentArrayToJson(appointments));


        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();

        }
        //

    }



    //this is the main menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        if (id == R.id.homebutton) {

            Intent intent = new Intent(InstallSetAppointment.this, HomeMenu.class );

            startActivity(intent); finish();

            // return true;
        }



        if (id == R.id.logoutbutton){
            startActivity(new Intent(this,LoginActivity.class));
        }

        if (id == R.id.backbutton){
            startActivity(new Intent(this,InstallAppointments.class));
        }

        return super.onOptionsItemSelected(item);
    }


    public void SaveIfOnline()
    {

        String isWebsiteAvailable = dbManager.getValue( "AmIOnline");

        if(isWebsiteAvailable.equals("True")) {
            //Save My Appointments - this will also 'delete' the request online
            MySOAPCallActivity cs1 = new MySOAPCallActivity();
            try {


                String myappointments = dbManager.getValue( "AndroidInsAppointments");
                SaveMyAppointmentsParams params = new SaveMyAppointmentsParams(cs1, myappointments);

                new CallSoapSaveMyAppointments().execute(params);



                //set and remove prrogress bar
                progressDialog = new ProgressDialog(InstallSetAppointment.this);
                progressDialog.setMessage("Saving ..."); // Setting Message
                progressDialog.setTitle("Please Wait..."); // Setting Title
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                progressDialog.show(); // Display Progress Dialog

                progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                progressDialog.setCancelable(true);
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
    public class CallSoapSaveMyAppointments extends AsyncTask<SaveMyAppointmentsParams, Void, String> {

        private Exception exception;

        @Override
        protected String doInBackground(SaveMyAppointmentsParams... params) {
            return params[0].foo.SaveMyAppointments(params[0].jsonRequests);
        }

        protected void onPostExecute(String result) {
            // TODO: check this.exception
            // TODO: do something with the feed
            try {


                dbManager.setValue( "AndroidInsAppointments", "");


            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
            }
            if(progressDialog !=null)
            {
                progressDialog.dismiss();
            }
        }



    }
    private static class SaveMyAppointmentsParams {
        MySOAPCallActivity foo;
        String jsonRequests;



        SaveMyAppointmentsParams(MySOAPCallActivity foo, String jsonRequests) {
            this.foo = foo;
            this.jsonRequests = jsonRequests;


        }
    }

    public class CallSoapIsInstallComplete extends AsyncTask<IsInstallCompleteParams, Void, String> {

        private Exception exception;

        @Override
        protected String doInBackground(IsInstallCompleteParams... params) {
            return params[0].foo.IsInstallComplete(params[0].StoreID);
        }

        protected void onPostExecute(String result) {
            // TODO: check this.exception
            // TODO: do something with the feed
            try {


               //now go to new intent
                GoToCheckout(result);

            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
            }
            if(progressDialog !=null)
            {
                progressDialog.dismiss();
            }
        }



    }
    private static class IsInstallCompleteParams {
        MySOAPCallActivity foo;
        Integer StoreID;



        IsInstallCompleteParams(MySOAPCallActivity foo, Integer StoreID) {
            this.foo = foo;
            this.StoreID = StoreID;


        }
    }


    public class CallSoapGetSurveys extends AsyncTask<GetSurveysParams, Void, String> {

        private Exception exception;

        @Override
        protected String doInBackground(GetSurveysParams... params) {
            return params[0].foo.GetSurveys(params[0].RequestID);
        }

        protected void onPostExecute(String result) {
            // TODO: check this.exception
            // TODO: do something with the feed
            try {
                //now go to new intent
                //TODO: SAVE SURVEYS TO ARRAY
                ArrayList<AndroidSurvey> stores = JsonUtil.parseJsonArrayAndroidSurvey(result);

                //Save Requests back to local
                dbManager.setValue( "AndroidSurveys", JsonUtil.convertSurveyArrayToJson(stores));


            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
            }
            if(progressDialog !=null)
            {
                progressDialog.dismiss();
            }
        }



    }
    private static class GetSurveysParams {
        MySOAPCallActivity foo;
        Integer RequestID;



        GetSurveysParams(MySOAPCallActivity foo, Integer RequestID) {
            this.foo = foo;
            this.RequestID = RequestID;


        }
    }





    public class CallSoapIsSurveyComplete extends AsyncTask<IsSurveyCompleteParams, Void, String> {

        private Exception exception;

        @Override
        protected String doInBackground(IsSurveyCompleteParams... params) {
            return params[0].foo.IsSurveyComplete(params[0].StoreID);
        }

        protected void onPostExecute(String result) {
            // TODO: check this.exception
            // TODO: do something with the feed
            try {


                //now go to new intent
                GoToCheckout(result);

            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
            }
            if(progressDialog !=null)
            {
                progressDialog.dismiss();
            }
        }



    }
    private static class IsSurveyCompleteParams {
        MySOAPCallActivity foo;
        Integer StoreID;



        IsSurveyCompleteParams(MySOAPCallActivity foo, Integer StoreID) {
            this.foo = foo;
            this.StoreID = StoreID;


        }
    }

    public void GetUnitListsForUserExplicit(String UserName, Boolean IsSurvey, Integer StoreID) {

        String isWebsiteAvailable = dbManager.getValue( "AmIOnline");

        if(isWebsiteAvailable.equals("True")) {

            final AlertDialog ad = new AlertDialog.Builder(this).create();
            MySOAPCallActivity cs = new MySOAPCallActivity();
            try {

                GetUnitListsForUserExplicitParams params = new GetUnitListsForUserExplicitParams(cs, UserName, IsSurvey, StoreID);

                new CallSoapGetUnitListsForUserExplicit().execute(params);


                //set and remove prrogress bar
                progressDialog = new ProgressDialog(InstallSetAppointment.this);
                progressDialog.setMessage("Retrieving data ..."); // Setting Message
                progressDialog.setTitle("Please Wait..."); // Setting Title
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                progressDialog.show(); // Display Progress Dialog

                progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                progressDialog.setCancelable(true);
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
        else {
            //I am OFFLINE
            //Do Nothing - you'll just use the stores you have :)


        }
    }


    public class CallSoapGetUnitListsForUserExplicit extends AsyncTask<GetUnitListsForUserExplicitParams, Void, String> {

        private Exception exception;

        @Override
        protected String doInBackground(GetUnitListsForUserExplicitParams... params) {
            return params[0].foo.GetUnitListsForUserExplicit(params[0].username, params[0].issurvey, params[0].storeid);
        }

        protected void onPostExecute(String result) {
            // TODO: check this.exception
            // TODO: do something with the feed
            try {
                //process Json Result
                //Save results in local storage
                if(result.toLowerCase().contains("error") || result.toLowerCase().contains("exception")){
                    Button btnUnitaryLists = (Button) findViewById(R.id.UnitaryLists);
                    btnUnitaryLists.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Getting data timed out. Please try again.", Toast.LENGTH_LONG).show();
                }
                else {
                    dbManager.setValue( "AndroidStoreUnitsExplicit", result);
                    //Button btnUnitaryLists = (Button) findViewById(R.id.UnitaryLists);
                    //btnUnitaryLists.setVisibility(View.VISIBLE);
                }

            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
            }
            if(progressDialog !=null)
            {
                progressDialog.dismiss();
            }

        }



    }
    private static class GetUnitListsForUserExplicitParams {
        MySOAPCallActivity foo;
        String username;
        Boolean issurvey;
        Integer storeid;


        GetUnitListsForUserExplicitParams(MySOAPCallActivity foo, String username, Boolean issurvey, Integer storeid) {
            this.foo = foo;
            this.username = username;
            this.issurvey = issurvey;
            this.storeid=storeid;
        }
    }


}

