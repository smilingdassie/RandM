package com.dsouchon.wayo.visualfusion;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.collect.Maps;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    ProgressDialog progressDialog;
    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    private static float MAP_ZOOM_MAX = 3;
    private static float MAP_ZOOM_MIN = 21;
    public LatLng sydney;
    public String mMyAddress;
    private DBManager dbManager;
    private SupportMapFragment mSupportFrag;
    private final static LatLng PARIS_LATLNG = new LatLng(48.858023, 2.294855);
    private List<Marker> mListMarkers = new ArrayList<Marker>();
    private Boolean mIsCheckIn;
    private String mGpsLatLng;
    private Integer RequestID;

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);




        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            // we have permission
            if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if(locationListener!=null) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
            }

        }

    }


    @Override
    public void onBackPressed() {
        //DO NOTHING

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);  dbManager = new DBManager(this);        dbManager.open();
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button btnDisplay = (Button) findViewById(R.id.send_my_location_btn);
        btnDisplay.setText("Wait 5 seconds for Map to Zoom to your location or click here.");
        Intent me = getIntent();
        mIsCheckIn = me.getBooleanExtra("IsCheckIn", true );
        RequestID = me.getIntExtra("RequestID", 0);
    }

    protected void DisplayGps(View view){

        Button btnDisplay = (Button) findViewById(R.id.send_my_location_btn);
        if(btnDisplay.getText().equals("Wait 5 seconds for Map to Zoom to your location or click here."))
        {
            LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                String huh = "";
                if(location ==null)
                {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    String huh2 = "";

                }
            }
        }
        else {
            if (mMyAddress != null) {
                Toast.makeText(getApplicationContext(), mMyAddress, Toast.LENGTH_LONG).show();
            }
            if (sydney != null) {
                Toast.makeText(getApplicationContext(), sydney.toString(), Toast.LENGTH_LONG).show();


                if (!sydney.toString().isEmpty()) {

                    String Today = dbManager.getValue( "Today");
                    if (mIsCheckIn) {
                        String CheckedIn = getIntent().getStringExtra("URN") + ":CheckedIn:" + Today;


                        if (dbManager.getValue( CheckedIn).equals("True")) {
                            Toast.makeText(this, "Only one checkin allowed per store per day.", Toast.LENGTH_LONG).show();
                        } else {

                            HandleGpsCheckIn(mIsCheckIn, sydney.toString(), RequestID, "", 0);
                        }
                    } else {
                        String CheckedOut = getIntent().getStringExtra("URN") + ":CheckedOut:" + Today;


              /*TODO RESTORE  if (dbManager.getValue( CheckedOut).equals("True")) {
                    Toast.makeText(this, "Only one check out allowed per store per day.", Toast.LENGTH_LONG).show();
                } else {*/

                        HandleGpsCheckIn(mIsCheckIn, sydney.toString(), RequestID, "", 0);
                        //}
                    }

                }
            } else {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                } else {
                    Location lastknownlocation = getLastKnownLocation();
                    if (lastknownlocation != null) {
                        sydney = new LatLng(lastknownlocation.getLatitude(), lastknownlocation.getLongitude());
                    } else {
                        Toast.makeText(this, "Gps not found. Move device to try again or click cancel.", Toast.LENGTH_LONG).show();
                    }
                }


            }
        }

    }
    LocationManager mLocationManager;

    private Location getLastKnownLocation() {

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        } else {

            mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            List<String> providers = mLocationManager.getProviders(true);
            Location bestLocation = null;
            for (String provider : providers) {
                Location l = mLocationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
            }
            return bestLocation;
        }
        return null;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        MAP_ZOOM_MAX = (float)18.0;//mMap.getMaxZoomLevel();
        MAP_ZOOM_MIN = (float)5.0;//mMap.getMinZoomLevel();

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        } else {
            Location lastknownlocation = getLastKnownLocation();//= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            mMap.clear();
            if(lastknownlocation !=null) {
                sydney = new LatLng(lastknownlocation.getLatitude(), lastknownlocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, MAP_ZOOM_MIN));
            }
          /*  TimerTask task = new TimerTask() {

                public void run() {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            setProperZoomLevel(sydney, 7, 1);
                        }
                    });

                }
            };

            Timer timer = new Timer();
            timer.schedule(task, 1000);// schedule Map display in 1 seconds
            */
        }


        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                //Log.i("Location:", location.toString());
                //Toast.makeText(getApplicationContext(),location.toString(), Toast.LENGTH_LONG).show();
                // Add a marker in Sydney and move the camera
                mMap.clear();

                LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, MAP_ZOOM_MAX));
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if(listAddresses != null && listAddresses.size()>0){
                        AssembleAddress(listAddresses.get(0));
                       // Toast.makeText(getApplicationContext(),"Address: "+ mMyAddress, Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Button btnDisplay = (Button) findViewById(R.id.send_my_location_btn);
                btnDisplay.setBackgroundColor(Color.GREEN);


                if(mIsCheckIn) {
                    btnDisplay.setText("Check in my GPS");
                }
                else
                {
                    btnDisplay.setText("Check out my GPS");
                }

            }

            public String AssembleAddress(Address a)
            {
                String result = "";

                if(a.getSubThoroughfare() != null)
                {
                    result += a.getSubThoroughfare() + " ";
                }
                if(a.getThoroughfare() != null)
                {
                    result += a.getThoroughfare() + ", ";
                }
                if(a.getLocality() != null)
                {
                    result += a.getLocality() + ", ";
                }
                if(a.getPostalCode() != null)
                {
                    result += a.getPostalCode() + ", ";
                }
                if(a.getCountryName() != null)
                {
                    result += a.getCountryName() + ", ";
                }
                mMyAddress = result;
                return result;
            }


            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                //when service enabled or disabled

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        //if device is running SDK < 23
        if (Build.VERSION.SDK_INT < 23) {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        } else {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastknownlocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                mMap.clear();
                if(lastknownlocation!=null) {
                    LatLng sydney = new LatLng(lastknownlocation.getLatitude(), lastknownlocation.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                    //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, MAP_ZOOM_MIN));
                }
                Button btnDisplay = (Button) findViewById(R.id.send_my_location_btn);
                btnDisplay.setText("Check in my GPS");

            }

        }




    }
    /**
     * Add some POI, in our case a list of some monuments inside Paris :)
     */
    private void addMarkers() {
        MarkerOptions options = new MarkerOptions();

        mListMarkers.add(mMap.addMarker(options.position(new LatLng(48.858814,2.299018)).title("American Library").snippet("American Library in Paris")));
        mListMarkers.add(mMap.addMarker(options.position(new LatLng(48.855878,2.298074)).title("Champ de Mars").snippet("Champ de Mars 7th arr")));
        mListMarkers.add(mMap.addMarker(options.position(new LatLng(48.861807,2.288933)).title("Trocadéro").snippet("Jardins du Trocadéro")));
        mListMarkers.add(mMap.addMarker(options.position(new LatLng(48.866183,2.307816)).title("Champs-Elysées").snippet("Champs-Elysées 8th arr")));
        mListMarkers.add(mMap.addMarker(options.position(new LatLng(48.845457,2.304876)).title("UNESCO").snippet("UNESCO 15th arr")));
        mListMarkers.add(mMap.addMarker(options.position(new LatLng(48.851924,2.317472)).title("Conseil Régional").snippet("Conseil Régional IDF")));
    }
    void setProperZoomLevel(LatLng loc, int radius, int nbPoi) {
        // [1] init zoom & move camera & result
        float currentZoomLevel = MAP_ZOOM_MAX;
        int currentFoundPoi = 0;
        LatLngBounds bounds = null;
        List<Marker> foundMarkers = new ArrayList<Marker>();
        Location location = latlngToLocation(loc);

        boolean keepZoomingOut = true;
        boolean keepSearchingForWithinRadius = true;// this is true if we keep looking
        // within a radius of 100km for ex:

        while (keepZoomingOut) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, currentZoomLevel--));
            bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
            keepSearchingForWithinRadius = (Math.round(location.distanceTo(latlngToLocation(bounds.northeast)) / 1000) > radius) ? false : true;

            // [2] find out if we have POI (Markers)
            for (Marker k : mListMarkers) {
                if (bounds.contains(k.getPosition())) {
                    if (!foundMarkers.contains(k)) {
                        currentFoundPoi++;
                        foundMarkers.add(k);
                    }
                }
                // [3] we stop if we have nbPoi so far
                if (keepSearchingForWithinRadius) {
                    if (currentFoundPoi > nbPoi) {
                        keepZoomingOut = false;
                        break;

                    }// else keep looking

                } else if (currentFoundPoi > 0) {// [4] We are beyond radius if we found one POI we are good
                    keepZoomingOut = false;
                    break;

                } else if (currentZoomLevel < MAP_ZOOM_MIN) {// [5] keep looking but
                    // within MIN_ZOOM
                    // limit (we don't
                    // want to go outer
                    // space do we ? :)
                    keepZoomingOut = false;
                    break;
                }
                // [6] if we didn't found nbPoi keep zooming out (within the limit of radius)
            }
            keepZoomingOut = ((currentZoomLevel > 0) && keepZoomingOut) ? true : false;

        }
    }

    private Location latlngToLocation(LatLng dest) {
        Location loc = new Location("");
        loc.setLatitude(dest.latitude);
        loc.setLongitude(dest.longitude);
        return loc;
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
        Intent intent = new Intent(MapsActivity.this, InstallSetAppointment.class );
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

                Intent me = getIntent();
                RequestID = Integer.parseInt(dbManager.getValue( "RequestID"));


                Intent intent = new Intent(MapsActivity.this, InstallSetAppointment.class );

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
