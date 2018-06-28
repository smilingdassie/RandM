package com.dsouchon.wayo.visualfusion;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class InstallGpsCheckoutSiteComplete extends AppCompatActivity   implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{
    private DBManager dbManager;
    private Spinner  spinner2;
    ProgressDialog progressDialog;
    private static final String TAG = "GpsGet";
    private TextView mLatitudeTextView;
    private TextView mLongitudeTextView;
    private TextView txtGpsLatLng;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager mLocationManager;

    private LocationRequest mLocationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 1000 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    private LocationManager locationManager;

    private int hour;
    private int minute;

    private Boolean mIsCheckIn;
    private String mGpsLatLng;
    private Integer RequestID;
    private String mInstallOrSurveyStatus;
    private Integer mMileage;
    private String incomingStatus;
    public boolean mIsSurvey = false;

    static final int TIME_DIALOG_ID = 999;
    static final int DATE_DIALOG_ID = 0;


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);  dbManager = new DBManager(this);        dbManager.open();
        setContentView(R.layout.new_store_checkout);

        String primaryRole = dbManager.getValue( "PrimaryRole");
        if (primaryRole.equals("Srv")){
            mIsSurvey = true;
        }

try {
    Intent me = getIntent();
    TextView txtStoreName = (TextView)findViewById(R.id.txtStoreName);
    TextView txtURN = (TextView)findViewById(R.id.txtURN);

    txtStoreName.setText(me.getStringExtra("StoreName"));
    txtURN.setText(me.getStringExtra("URN"));


    RequestID = Integer.parseInt(dbManager.getValue( "RequestID"));



    Spinner spinnerInstallStatus = (Spinner) findViewById(R.id.spinnerInstallStatus);

    if(!mIsSurvey) {
        incomingStatus = me.getStringExtra("InstallStatus");
        if (incomingStatus.equals("Installation Complete")) {
            //Hide spinner if site complete
            spinnerInstallStatus.setVisibility(View.INVISIBLE);
            EditText txtComment  = (EditText)findViewById(R.id.txtComment);
            txtComment.setVisibility(View.INVISIBLE);
        }
        else
        {
            TextView txtHeader = (TextView)findViewById(R.id.txtHeader);
            txtHeader.setText("Installation incomplete. Please select reason and add comments.");
            EditText txtComment  = (EditText)findViewById(R.id.txtComment);
            txtComment.setVisibility(View.VISIBLE);
        }

        populatespinner();
    }
    else {
        incomingStatus = me.getStringExtra("SurveyStatus");
        if (incomingStatus.equals("Survey Complete")) {
            //Hide spinner if site complete
            spinnerInstallStatus.setVisibility(View.INVISIBLE);
            EditText txtComment  = (EditText)findViewById(R.id.txtComment);
            txtComment.setVisibility(View.INVISIBLE);
        }
        else
        {
            TextView txtHeader = (TextView)findViewById(R.id.txtHeader);
            txtHeader.setText("Survey incomplete. Please select reason and add comments.");

            EditText txtComment  = (EditText)findViewById(R.id.txtComment);
            txtComment.setVisibility(View.VISIBLE);
        }
        addItemsOnSurveySpinner();
    }
    mGoogleApiClient = new GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build();

    mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


    checkLocation(); //check whether location service is enable or not in your  phone

}
catch(Exception ex)
{

}
    }

    public void addItemsOnSurveySpinner() {

        spinner2 = (Spinner) findViewById(R.id.spinnerInstallStatus);
        List<String> list = new ArrayList<String>();
        list.add(0, "Reason");
        list.add(1, "Could not complete - Store");
        spinner2.setPrompt("Choose Reason");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.spinnerstyle, list);
        dataAdapter.setDropDownViewResource(R.layout.spinnerstyledrop);
        spinner2.setAdapter(dataAdapter);
    }

    public void CheckOut(View view) {
        boolean valid = true;
        Spinner spinnerInstallStatus= (Spinner) findViewById(R.id.spinnerInstallStatus);
        String mOpen = spinnerInstallStatus.getSelectedItem().toString();
        TextView txtHeader = (TextView)findViewById(R.id.txtHeader);
        Intent me = getIntent();
        Boolean siteComplete = false;

        if(!mIsSurvey) {
            incomingStatus = me.getStringExtra("InstallStatus");
            if(incomingStatus.equals("Installation Complete"))
            {
                siteComplete = true;
            }
        }
        else        {
            incomingStatus = me.getStringExtra("SurveyStatus");
            if(incomingStatus.equals("Survey Complete"))
            {
                siteComplete = true;
            }
        }


        if (siteComplete) {
            WhatHomeDoes();
        }
        else{
        if(mOpen.equals("Reason"))
        {
            txtHeader.setError("Please select reason site is incomplete.");
            Toast.makeText(getApplicationContext(), "Please select reason site is incomplete.", Toast.LENGTH_LONG).show();
            spinnerInstallStatus.requestFocus();
            valid = false;
        }
        else
        {
            WhatHomeDoes();

        }
        }
       // dbManager.setValue( "IsCheckin", "false");
       // startLocationUpdates();

    }


    public void CheckoutSaveAppointmentMileage(View view)
    {
        Intent me = getIntent();

        String InstallStatus = me.getStringExtra("InstallStatus");
        String StoreName = me.getStringExtra("StoreName");

        //GPS checkout stuff with webservice call including mileage and emails
        //HandleGpsCheckIn();

        Intent intent = new Intent(InstallGpsCheckoutSiteComplete.this, InstallHome.class);
        startActivity(intent); finish();
    }


    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 3);
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 4);


            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
        Log.d("reque", "--->>>>");
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.CAMERA}, 2);
        }
    }



    //GPS Stuff

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
            return;
        }

        //startLocationUpdates();

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(mLocation == null){
            startLocationUpdates();
        }
        if (mLocation != null) {

            // mLatitudeTextView.setText(String.valueOf(mLocation.getLatitude()));
            //mLongitudeTextView.setText(String.valueOf(mLocation.getLongitude()));
        } else {
            Toast.makeText(this, "Location not Detected", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        //mLatitudeTextView.setText(String.valueOf(location.getLatitude()));
        //mLongitudeTextView.setText(String.valueOf(location.getLongitude() ));
        //Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        // You can now create a LatLng Object for use with maps
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        String strLatLng = String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude() );
        Boolean IsCheckin = Boolean.parseBoolean(dbManager.getValue( "IsCheckin"));

        Intent me = getIntent();
        RequestID = Integer.parseInt(dbManager.getValue( "RequestID"));


        //display GPS
        txtGpsLatLng =  (TextView) findViewById(R.id.txtGpsLatLng);
        txtGpsLatLng.setText(strLatLng);
        //set public variables
        mIsCheckIn = IsCheckin;
        mGpsLatLng = strLatLng;



        Toast.makeText(this, "GPS location found. Now click Home", Toast.LENGTH_LONG).show();
        //HandleGpsCheckIn(IsCheckin, strLatLng, RequestID);
    }

    private boolean checkLocation() {
        if(!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }



    public void HandleGpsCheckIn(Boolean IsCheckIn, String GpsLatLng, Integer RequestID, String InstallStatus, Integer Mileage)
    {

        MySOAPCallActivity cs1 = new MySOAPCallActivity();
        try {
            String UserName;

            UserName = dbManager.getValue( "UserName");

            GpsCheckinParams params = new GpsCheckinParams(cs1, UserName, RequestID, GpsLatLng, IsCheckIn, InstallStatus, Mileage);

            new CallSoapGpsCheckin().execute(params);



            //set and remove prrogress bar
            progressDialog = new ProgressDialog(InstallGpsCheckoutSiteComplete.this);
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

    public void Cancel(View view) {
        Intent intent = new Intent(InstallGpsCheckoutSiteComplete.this, InstallSetAppointment.class );
        ArrayList<AndroidAppointment> myappointments;

        myappointments = getAppointmentsData();

        //Sending data to another Activity

        RequestID = Integer.parseInt(dbManager.getValue( "RequestID"));
        AndroidAppointment appointment = findAppointmentByID(RequestID, myappointments);

        intent.putExtra("RequestID", appointment.getID());
        intent.putExtra("Mileage", appointment.getMileage());
        intent.putExtra("StoreID", appointment.getStoreID());


        intent.putExtra("RequestTypeName", appointment.getRequestTypeName());
        intent.putExtra("StoreName", appointment.getStoreName());
        intent.putExtra("StoreNameURN", appointment.getStoreNameURN());

        intent.putExtra("BrandName", appointment.getBrandName());
        intent.putExtra("OutletTypeName", appointment.getOutletTypeName());
        intent.putExtra("TierTypeName", appointment.getTierTypeName());

        intent.putExtra("JobCardCount", appointment.getJobCardCount());
        intent.putExtra("ExteriorImageCount", appointment.getExteriorImageCount());
        intent.putExtra("InteriorImageCount", appointment.getInteriorImageCount());
        intent.putExtra("SurveySignatureCount", appointment.getSurveySignatureCount());
        intent.putExtra("PerformanceReviewSignatureCount", appointment.getPerformanceReviewSignatureCount());

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

    public void WhatHomeDoes()
    {

            Spinner spinnerInstallStatus=(Spinner) findViewById(R.id.spinnerInstallStatus);

            if(spinner2.isShown())
            {
                EditText txtComments = (EditText)findViewById(R.id.txtComment);
                String comments = txtComments.getText().toString();
                mInstallOrSurveyStatus = String.format("%s | Comments: %s", spinnerInstallStatus.getSelectedItem().toString(), comments);

            }
            else
            {
                mInstallOrSurveyStatus = incomingStatus; //complete
            }

            if(mInstallOrSurveyStatus.equals("Reason"))
            {
                if(!mIsSurvey) {//none selected when they should have
                    Toast.makeText(this, "Please select reason installation is incomplete from dropdown.", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(this, "Please select reason survey is incomplete from dropdown.", Toast.LENGTH_LONG).show();
                }
            }
            else {

                String Today = dbManager.getValue( "Today");
                String CheckedOut = getIntent().getStringExtra("URN")+":CheckedOut:"+Today;
              /*TODO RESTORE CHECK  if(dbManager.getValue(CheckedOut).equals("True")){
                    Toast.makeText(this, "Only one checkout allowed per store per day.", Toast.LENGTH_LONG).show();
                }
                else {*/
                 //   HandleGpsCheckIn(mIsCheckIn, mGpsLatLng, RequestID, mInstallOrSurveyStatus, mMileage);

                    Intent intent = new Intent(InstallGpsCheckoutSiteComplete.this, MapsActivitySlim.class);
                    dbManager.setValue( "IsCheckin", "false");

                    Intent me = getIntent();
                    intent.putExtra("StoreNameURN", me.getStringExtra("StoreNameURN"));
                    intent.putExtra("StoreName", me.getStringExtra("StoreName"));
                    intent.putExtra("URN",  me.getStringExtra("URN"));
                    intent.putExtra("RequestID",  RequestID);
                    intent.putExtra("ID",  RequestID);
                    intent.putExtra("mInstallOrSurveyStatus",  mInstallOrSurveyStatus);


                    intent.putExtra("IsCheckIn", false);
                    startActivity(intent);
                    finish();

                //}
            }



    }


    public void Home(View view) {
        if(mGpsLatLng !=null && !mGpsLatLng.isEmpty()) {

            EditText editMileage = (EditText)findViewById(R.id.editMileage);
            String strMileage = editMileage.getText().toString();
            mMileage = Integer.parseInt(strMileage);
            Spinner spinnerInstallStatus=(Spinner) findViewById(R.id.spinnerInstallStatus);

            if(spinner2.isShown())
            {
                mInstallOrSurveyStatus = spinnerInstallStatus.getSelectedItem().toString();
            }
            else
            {
                mInstallOrSurveyStatus = incomingStatus; //complete
            }

            if(mInstallOrSurveyStatus.equals("Reason"))
            {
                if(!mIsSurvey) {//none selected when they should have
                    Toast.makeText(this, "Please select reason installation is incomplete from dropdown.", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(this, "Please select reason survey is incomplete from dropdown.", Toast.LENGTH_LONG).show();
                }
            }
            else {

                String Today = dbManager.getValue( "Today");
                String CheckedOut = getIntent().getStringExtra("URN")+":CheckedOut:"+Today;
                if(dbManager.getValue(CheckedOut).equals("True")){
                    Toast.makeText(this, "Only one checkout allowed per store per day.", Toast.LENGTH_LONG).show();
                }
                else {
                    HandleGpsCheckIn(mIsCheckIn, mGpsLatLng, RequestID, mInstallOrSurveyStatus, mMileage);
                }
            }
        }
        else
        {
            Toast.makeText(this, "Gps not collected yet. Move device and click checkout again", Toast.LENGTH_LONG).show();

        }

    }

public void populatespinner()
{
    ArrayList<AndroidInstallStatus> androidInstallStatuses;

    androidInstallStatuses = getInstallStatusData();

    spinner2 = (Spinner) findViewById(R.id.spinnerInstallStatus);
    List<String> list = new ArrayList<String>();
    list.add(0, "Reason");
    //list.add("Delivery Incomplete");
    //list.add("Units not Delivered");

    for (int i=0; i<androidInstallStatuses.size(); i++) {
        list.add(androidInstallStatuses.get(i).getStatusName());
    }


    spinner2.setPrompt("Choose Reason");
    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
            R.layout.spinnerstyle, list);
    dataAdapter.setDropDownViewResource(R.layout.spinnerstyledrop);
    spinner2.setAdapter(dataAdapter);


}




    public class CallSoapGpsCheckin extends AsyncTask<GpsCheckinParams, Void, String> {

        private Exception exception;

        @Override
        protected String doInBackground(GpsCheckinParams... params) {
            return params[0].foo.GpsCheckin(params[0].UserName,params[0].RequestID,params[0].GpsLatLng,params[0].IsCheckIn,params[0].InstallStatus,params[0].Mileage);
        }

        protected void onPostExecute(String result) {
            // TODO: check this.exception
            // TODO: do something with the feed
            try {



                //TODO: return to Home?
                dbManager.setValue( "CurrentGps", result);



                Intent me = getIntent();


                String Today = dbManager.getValue( "Today");
                dbManager.setValue(  me.getStringExtra("URN") + ":CheckedOut:" + Today, "True");

                RequestID = Integer.parseInt(dbManager.getValue( "RequestID"));
                Intent intent = new Intent(InstallGpsCheckoutSiteComplete.this, InstallSetAppointment.class );

                intent.putExtra("Toast", "Check out successful.");



                ArrayList<AndroidAppointment> myappointments;

                myappointments = getAppointmentsData();

                //Sending data to another Activity
                AndroidAppointment appointment = findAppointmentByID(RequestID, myappointments);

                intent.putExtra("ID", appointment.getID());
                intent.putExtra("Mileage", appointment.getMileage());
                intent.putExtra("StoreID", appointment.getStoreID());


                intent.putExtra("RequestTypeName", appointment.getRequestTypeName());
                intent.putExtra("BrandName", appointment.getBrandName());
                intent.putExtra("OutletTypeName", appointment.getOutletTypeName());
                intent.putExtra("TierTypeName", appointment.getTierTypeName());

                intent.putExtra("JobCardCount", appointment.getJobCardCount());
                intent.putExtra("ExteriorImageCount", appointment.getExteriorImageCount());
                intent.putExtra("InteriorImageCount", appointment.getInteriorImageCount());
                intent.putExtra("SurveySignatureCount", appointment.getSurveySignatureCount());
                intent.putExtra("PerformanceReviewSignatureCount", appointment.getPerformanceReviewSignatureCount());



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


            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
            }
            progressDialog.dismiss();
        }



    }
    private static class GpsCheckinParams {
        MySOAPCallActivity foo;
        String UserName; Integer RequestID; String GpsLatLng; Boolean IsCheckIn; String InstallStatus; Integer Mileage;


        GpsCheckinParams(MySOAPCallActivity foo, String UserName, Integer RequestID, String GpsLatLng, Boolean IsCheckIn, String InstallStatus, Integer Mileage) {
            this.foo = foo;
            this.UserName = UserName;
            this.RequestID = RequestID;
            this.GpsLatLng = GpsLatLng;
            this.IsCheckIn = IsCheckIn;
            this.InstallStatus = InstallStatus;
            this.Mileage = Mileage;


        }
    }

    private ArrayList<AndroidInstallStatus> getInstallStatusData() {
        ArrayList<AndroidInstallStatus> appointments = new ArrayList<>();
        String json = "";
        json = dbManager.getValue( "InstallStatusList");
        try {
            appointments = JsonUtil.parseJsonArrayAndroidInstallStatus(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return appointments;
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



}
