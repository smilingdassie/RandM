package com.dsouchon.wayo.visualfusion;
import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.util.ArrayList;

public class MapsActivitySlim extends AppCompatActivity implements LocationListener {
    ProgressDialog progressDialog;
    private Boolean mIsCheckIn;
    private String mGpsLatLng;
    private Integer RequestID;
    public double Lat;
    public double Lng;
    public boolean locationHasBeenCaptured;
    public String mInstallOrSurveyStatus;
    private DBManager dbManager;

    LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);  dbManager = new DBManager(this);        dbManager.open();
        setContentView(R.layout.activity_maps_slim);
        locationHasBeenCaptured = false;
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        //LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            }

        Button btnDisplay = (Button) findViewById(R.id.send_my_location_btn);
       // btnDisplay.setText("Wait 5 seconds for Map to Zoom to your location or click here.");
        Intent me = getIntent();
        mIsCheckIn = me.getBooleanExtra("IsCheckIn", true );
        RequestID = me.getIntExtra("RequestID", 0);

        mInstallOrSurveyStatus = me.getStringExtra("mInstallOrSurveyStatus");

    }


    @Override
    public void onBackPressed() {
        //DO NOTHING

    }


    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        if(!locationHasBeenCaptured) {
            double latitude = (location.getLatitude());
            double longitude = (location.getLongitude());
            Lat = latitude;
            Lng = longitude;
            //Log.i("Geo_Location", "Latitude: " + latitude + ", Longitude: " + longitude);
            Toast.makeText(getApplicationContext(), String.format("Geo_Location: Latitude: %f, Longitude: %f", latitude, longitude), Toast.LENGTH_LONG).show();

            try {
                if (Lat == 0 && Lng == 0) {

                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);


                        if (location == null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            String huh2 = "";
                            if (location != null) {
                                Lat = location.getLatitude();
                                Lng = location.getLongitude();
                            }
                        } else {
                            Lat = location.getLatitude();
                            Lng = location.getLongitude();
                        }
                    }
                }
                if(Lat !=0 && Lng!=0)
                {
                    locationHasBeenCaptured = true;
                }
                Toast.makeText(getApplicationContext(), String.format("GPS: Lat: %f, Lng: %f", Lat, Lng), Toast.LENGTH_LONG).show();

                String Today = dbManager.getValue( "Today");
                if (mIsCheckIn) {
                    String CheckedIn = getIntent().getStringExtra("URN") + ":CheckedIn:" + Today;


                    if (dbManager.getValue( CheckedIn).equals("True")) {
                        Toast.makeText(this, "Only one checkin allowed per store per day.", Toast.LENGTH_LONG).show();
                    } else {

                        HandleGpsCheckIn(mIsCheckIn, String.format("%f,%f", Lat, Lng), RequestID, mInstallOrSurveyStatus, 0);
                    }
                } else {
                    String CheckedOut = getIntent().getStringExtra("URN") + ":CheckedOut:" + Today;


              /*TODO RESTORE  if (dbManager.getValue( CheckedOut).equals("True")) {
                    Toast.makeText(this, "Only one check out allowed per store per day.", Toast.LENGTH_LONG).show();
                } else {*/

                    HandleGpsCheckIn(mIsCheckIn, String.format("%f,%f", Lat, Lng), RequestID, mInstallOrSurveyStatus, 0);
                    //}
                }

            } catch (Exception e) {

                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
        Log.i("Location Manager", "onPause, done");
    }



    public void DisplayGps2(View view) {

        try {
            if (Lat == 0 && Lng == 0) {
                LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);


                    if (location == null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        String huh2 = "";
                        if (location != null) {
                            Lat = location.getLatitude();
                            Lng = location.getLongitude();
                        }
                    } else {
                        Lat = location.getLatitude();
                        Lng = location.getLongitude();
                    }
                }
            }
            Toast.makeText(getApplicationContext(), String.format("GPS: Lat: %f, Lng: %f", Lat, Lng), Toast.LENGTH_LONG).show();

            String Today = dbManager.getValue( "Today");
            if (mIsCheckIn) {
                String CheckedIn = getIntent().getStringExtra("URN") + ":CheckedIn:" + Today;


                if (dbManager.getValue( CheckedIn).equals("True")) {
                    Toast.makeText(this, "Only one checkin allowed per store per day.", Toast.LENGTH_LONG).show();
                } else {

                    HandleGpsCheckIn(mIsCheckIn, String.format("%f,%f", Lat, Lng), RequestID, mInstallOrSurveyStatus, 0);
                }
            }
            else
                {
                String CheckedOut = getIntent().getStringExtra("URN") + ":CheckedOut:" + Today;


              /*TODO RESTORE  if (dbManager.getValue( CheckedOut).equals("True")) {
                    Toast.makeText(this, "Only one check out allowed per store per day.", Toast.LENGTH_LONG).show();
                } else {*/

                HandleGpsCheckIn(mIsCheckIn, String.format("%f,%f", Lat, Lng), RequestID, mInstallOrSurveyStatus, 0);
                //}
            }

        }
        catch(Exception e){

            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
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
            progressDialog = new ProgressDialog(this);
            if(IsCheckIn) {
                progressDialog.setMessage("Saving Gps Checkin"); // Setting Message
            }
            else{
                progressDialog.setMessage("Saving Gps Checkout"); // Setting Message
            }
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
        Intent intent = new Intent(MapsActivitySlim.this, InstallSetAppointment.class );
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


                RequestID = Integer.parseInt(dbManager.getValue( "RequestID"));

                Intent me = getIntent();

                Intent intent = new Intent(MapsActivitySlim.this, InstallSetAppointment.class );

                String Today = dbManager.getValue( "Today");

                if(mIsCheckIn)
                {
                    intent.putExtra("Toast", "Check In successful.");
                    dbManager.setValue(  me.getStringExtra("URN") + ":CheckedIn:" + Today, "True");
                }
                else
                {
                    intent.putExtra("Toast", "Check Out successful.");
                    dbManager.setValue(  me.getStringExtra("URN") + ":CheckedOut:" + Today, "True");
                }

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


                if(progressDialog !=null)
                {
                    progressDialog.dismiss();
                }
                startActivity(intent); finish();


            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();

                if(progressDialog !=null)
                {
                    progressDialog.dismiss();
                }
            }

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