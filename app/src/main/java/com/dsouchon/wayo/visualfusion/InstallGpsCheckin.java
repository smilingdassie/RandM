package com.dsouchon.wayo.visualfusion;

import android.Manifest;
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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.util.ArrayList;

public class InstallGpsCheckin extends AppCompatActivity  {
    private DBManager dbManager;
    private static final String TAG = "GpsGet";
    private TextView mLatitudeTextView;
    private TextView mLongitudeTextView;
    private TextView txtGpsLatLng;

    private Location mLocation;
    private LocationManager mLocationManager;
    ProgressDialog progressDialog;
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

    private String mInstallStatus;
    private Integer mMileage;

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
        setContentView(R.layout.new_checkin_layout);

        Intent me = getIntent();
        TextView txtStoreName = (TextView)findViewById(R.id.txtStoreName);
        TextView txtURN = (TextView)findViewById(R.id.txtURN);

        txtStoreName.setText(me.getStringExtra("StoreName"));
        txtURN.setText(me.getStringExtra("URN"));



    }



    public void CheckIn(View view) {
        dbManager.setValue( "IsCheckin", "true");
        Intent intent = new Intent(InstallGpsCheckin.this, MapsActivitySlim.class );
        Intent me = getIntent();
        intent.putExtra("StoreNameURN", me.getStringExtra("StoreNameURN"));
        intent.putExtra("StoreName", me.getStringExtra("StoreName"));
        intent.putExtra("URN",  me.getStringExtra("URN"));
        intent.putExtra("RequestID",  me.getIntExtra("RequestID",0));

        startActivity(intent); finish();
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
            progressDialog = new ProgressDialog(InstallGpsCheckin.this);
            progressDialog.setMessage("Saving Gps Checkin"); // Setting Message
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

    public void Home(View view) {
        if(!mGpsLatLng.isEmpty()) {

            String Today = dbManager.getValue( "Today");
            String CheckedIn = getIntent().getStringExtra("URN")+":CheckedIn:"+Today;
            if(dbManager.getValue(CheckedIn).equals("True")){
                Toast.makeText(this, "Only one checkin allowed per store per day.", Toast.LENGTH_LONG).show();
            }
            else {

                HandleGpsCheckIn(mIsCheckIn, mGpsLatLng, RequestID, "", 0);
            }
        }
        else
        {
            Toast.makeText(this, "Gps not collected yet. Move device and click checkout again", Toast.LENGTH_LONG).show();

        }
    }

    public void Cancel(View view) {
        Intent intent = new Intent(InstallGpsCheckin.this, InstallSetAppointment.class );
        ArrayList<AndroidAppointment> myappointments;

        myappointments = getAppointmentsData();

        //Sending data to another Activity
        //int ID = getIntent().getIntExtra("RequestID",0);
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
                RequestID = Integer.parseInt(dbManager.getValue( "RequestID"));


                Intent intent = new Intent(InstallGpsCheckin.this, InstallSetAppointment.class );
                intent.putExtra("Toast", "Check in successful.");
                String Today = dbManager.getValue( "Today");
                dbManager.setValue(  me.getStringExtra("URN") + ":CheckedIn:" + Today, "True");

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

                String StoreName = appointment.getStoreName();
                String StoreNameURN = appointment.getStoreNameURN();

                intent.putExtra("StoreName", StoreName);
                intent.putExtra("StoreNameURN", StoreNameURN);
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
