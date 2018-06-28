package com.dsouchon.wayo.visualfusion;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.view.Menu;
import android.widget.Toast;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;


public class InstallHome extends AppCompatActivity {
    public boolean mIsSurvey = false;
    ProgressDialog progressDialog;
    private DBManager dbManager;


    @Override
    public void onBackPressed() {
        //DO NOTHING

    }
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

    @Override  protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);  dbManager = new DBManager(this);        dbManager.open();
        setContentView(R.layout.install_home);

        //new check for is survey
        String primaryRole = dbManager.getValue( "PrimaryRole");
        if (primaryRole.equals("Srv")){
            mIsSurvey = true;
        }

        if(mIsSurvey) {
            //getActionBar().setTitle("Survey Unitary List");
            getSupportActionBar().setTitle("Survey Team Home");  //
        }

        Date date = new Date();
        String modifiedDate= new SimpleDateFormat("yyyy-MM-dd").format(date);
        dbManager.setValue( "Today", modifiedDate);

        Button btnUnitaryLists = (Button) findViewById(R.id.UnitaryLists);
        btnUnitaryLists.setVisibility(View.GONE);

        Button InstallAppoint = (Button) findViewById(R.id.InstallAppoint);
        InstallAppoint.setVisibility(View.GONE);

        String isWebsiteAvailable = dbManager.getValue( "AmIOnline");

        if (isWebsiteAvailable.equals("True")) {

            if(!mIsSurvey) {
                GetAppointmentsForUser(dbManager.getValue( "UserName"), "openinsappointment");
                AndroidDocumentTypes();
            }
            else {
                GetAppointmentsForUser(dbManager.getValue( "UserName"), "opensrvappointment");
            }


            if(!mIsSurvey) {

                GetStoresForUser(dbManager.getValue( "UserName"), "installation scheduled");
            }
            else {
                GetStoresForUser(dbManager.getValue( "UserName"), "ess survey scheduled");
            }
            GetSpinnerDataFromWS();//NEW
        }
        else{
            //I am Offline
            //Use locally saved lists

        }
    }

    private ArrayList<AndroidStore> getStoresData() {
        ArrayList<AndroidStore> stores = new ArrayList<>();
        String json = "[\n" +
                "  {\n" +
                "    \"ID\": 1041,\n" +
                "    \"StoreName\": \"TEST Daniel\",\n" +
                "    \"UserName\": \"dsouchon@gmail.com\",\n" +
                "    \"ContactPerson\": \"Daniel Souchon\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"ID\": 1042,\n" +
                "    \"StoreName\": \"Test Don1\",\n" +
                "    \"UserName\": \"dsouchon@gmail.com\",\n" +
                "    \"ContactPerson\": \"Donavan\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"ID\": 1043,\n" +
                "    \"StoreName\": \"TEST Roadhouse Grill Hurlingham\",\n" +
                "    \"UserName\": \"dsouchon@gmail.com\",\n" +
                "    \"ContactPerson\": \"Robert Kingori\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"ID\": 1044,\n" +
                "    \"StoreName\": \"TEST Soggybottom INN\",\n" +
                "    \"UserName\": \"dsouchon@gmail.com\",\n" +
                "    \"ContactPerson\": \"Saggy Sogbottom\"\n" +
                "  }]";


        json = dbManager.getValue( "AndroidStoresIns");

        try {
            stores = JsonUtil.parseJsonArrayAndroidStore(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return stores;
    }

    public void GetSpinnerDataFromWS(){

        MySOAPCallActivity cs1 = new MySOAPCallActivity();
        try {
            String UserName;

            InstallStatusListParams params = new InstallStatusListParams(cs1);

            new CallSoapInstallStatusList().execute(params);




        } catch (Exception ex) {


        }



    }



    public class CallSoapInstallStatusList extends AsyncTask<InstallStatusListParams, Void, String> {

        private Exception exception;

        @Override
        protected String doInBackground(InstallStatusListParams... params) {
            return params[0].foo.InstallStatusList();
        }

        protected void onPostExecute(String result) {
            // TODO: check this.exception
            // TODO: do something with the feed
            try {


                //TODO: return to Home?
                dbManager.setValue( "InstallStatusList", result);




            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
            }

        }



    }
    private static class InstallStatusListParams {
        MySOAPCallActivity foo;

        InstallStatusListParams(MySOAPCallActivity foo) {
            this.foo = foo;


        }
    }

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

            Intent intent = new Intent(InstallHome.this, HomeMenu.class );

            startActivity(intent); finish();

            // return true;
        }


        if (id == R.id.logoutbutton){
            startActivity(new Intent(this,LoginActivity.class));
        }

        if (id == R.id.backbutton){
            startActivity(new Intent(this,HomeMenu.class));
        }

        return super.onOptionsItemSelected(item);
    }


    public void InstallAppointments(View view) {

        Intent intent = new Intent(InstallHome.this, InstallAppointments.class );
        startActivity(intent); finish();
    }

    public void UnitaryList(View view) {

        Intent intent = new Intent(InstallHome.this, InstallSelectStoreForUnitry.class );
        startActivity(intent); finish();
    }


    public void GetStoresForUser(String UserName, String Phase) {

        String isWebsiteAvailable = dbManager.getValue( "AmIOnline");

        if(isWebsiteAvailable.equals("True")) {

            final AlertDialog ad = new AlertDialog.Builder(this).create();
            MySOAPCallActivity cs = new MySOAPCallActivity();
            try {

                GetStoresForUserParams params = new GetStoresForUserParams(cs, UserName, Phase);

                new CallSoapGetStoresForUser().execute(params);


                //set and remove prrogress bar
                progressDialog = new ProgressDialog(InstallHome.this);
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



            }
            catch (Exception ex) {


            }


        }
        else {
            //I am OFFLINE
            //Do Nothing - you'll just use the stores you have :)


        }
    }

    public void GetAppointmentsForUser(String UserName, String RequestType) {


        MySOAPCallActivity cs = new MySOAPCallActivity();
        try {

            GetAppointmentsForUserParams params = new GetAppointmentsForUserParams(cs, UserName, RequestType);

            new CallSoapGetAppointmentsForUser().execute(params);


        } catch (Exception ex) {


        }



    }

    public void AndroidDocumentTypes() {


        MySOAPCallActivity cs = new MySOAPCallActivity();
        try {

            AndroidDocumentTypesParams params = new AndroidDocumentTypesParams(cs);

            new CallSoapAndroidDocumentTypes().execute(params);


        } catch (Exception ex) {


        }



    }

//Soap region

    public class CallSoapGetAppointmentsForUser extends AsyncTask<InstallHome.GetAppointmentsForUserParams, Void, String> {

        private Exception exception;

        @Override
        protected String doInBackground(InstallHome.GetAppointmentsForUserParams... params) {
            return params[0].foo.GetAppointmentsForUser(params[0].username, params[0].requestType);
        }

        protected void onPostExecute(String result) {
            // TODO: check this.exception
            // TODO: do something with the feed
            try {
                //process Json Result
                //Save results in local storage
                if(result.toLowerCase().contains("error")){

                }
                else {
                    dbManager.setValue( "AndroidInsAppointments", result);
                }

            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
            }

        }



    }
    private static class GetAppointmentsForUserParams {
        MySOAPCallActivity foo;
        String username;
        String requestType;



        GetAppointmentsForUserParams(MySOAPCallActivity foo, String username, String requestType) {
            this.foo = foo;
            this.username = username;
            this.requestType = requestType;


        }
    }

    public class CallSoapGetStoresForUser extends AsyncTask<GetStoresForUserParams, Void, String> {

        private Exception exception;

        @Override
        protected String doInBackground(GetStoresForUserParams... params) {
            return params[0].foo.GetStoresForUser(params[0].username, params[0].phase);
        }

        protected void onPostExecute(String result) {
            // TODO: check this.exception
            // TODO: do something with the feed
            try {
                //process Json Result
                //Save results in local storage
                if(result.toLowerCase().contains("error"))
                {

                }
                else {
                    dbManager.setValue( "AndroidStoresIns", result);

                    String Today =  dbManager.getValue( "Today");
                    String resetcheckins = dbManager.getValue( "ResetAllCheckins");
                    if(resetcheckins.equals("True")) {
                        ArrayList<AndroidStore> stores = getStoresData();
                        Iterator<AndroidStore> it = stores.iterator();
                        while (it.hasNext()) {
                            AndroidStore user = it.next();
                            String URN = user.getURN();
                            dbManager.setValue(URN+":CheckedIn:"+Today, "");
                            dbManager.setValue(URN+":CheckedOut:"+Today, "");
                        }
                    }


                    Intent intent = new Intent(InstallHome.this, InstallAppointments.class );
                    startActivity(intent); finish();

                }

            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
            }
            progressDialog.dismiss();
        }



    }
    private static class GetStoresForUserParams {
        MySOAPCallActivity foo;
        String username;
        String phase;



        GetStoresForUserParams(MySOAPCallActivity foo, String username, String phase) {
            this.foo = foo;
            this.username = username;
            this.phase = phase;


        }
    }

    public class CallSoapAndroidDocumentTypes extends AsyncTask<AndroidDocumentTypesParams, Void, String> {

        private Exception exception;

        @Override
        protected String doInBackground(AndroidDocumentTypesParams... params) {
            return params[0].foo.AndroidDocumentTypes();
        }

        protected void onPostExecute(String result) {
            // TODO: check this.exception
            // TODO: do something with the feed
            try {
                //process Json Result
                //Save results in local storage
                if(result.toLowerCase().contains("error")){

                }
                else {
                    dbManager.setValue( "AndroidDocumentTypes", result);
                }

            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
            }

        }



    }
    private static class AndroidDocumentTypesParams {
        MySOAPCallActivity foo;




        AndroidDocumentTypesParams (MySOAPCallActivity foo) {
            this.foo = foo;


        }
    }


}
