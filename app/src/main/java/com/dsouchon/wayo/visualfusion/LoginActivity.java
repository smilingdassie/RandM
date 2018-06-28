package com.dsouchon.wayo.visualfusion;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;

import android.widget.Toast;


public class LoginActivity extends AppCompatActivity {
    ProgressDialog progressDialog;
    private DBManager dbManager;

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

    public boolean mSkipNews = false;
    private void checkWritePermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.CAMERA}, 2);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
   @Override  protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);  dbManager = new DBManager(this);        dbManager.open();
       dbManager = new DBManager(this);
       dbManager.open();
       AmIOnlineAtAll();
       //GetGoodNewsfromWS();

        checkCameraPermission();
        checkWritePermission();

        setContentView(R.layout.login_layout);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        EditText userName = (EditText) findViewById(R.id.editUserName);



        if(Local.isSet(getApplicationContext(),"UserName")) {
            userName.setText(dbManager.getValue( "UserName"));
        }
        boolean isNetworkAvailable = isNetworkAvailable();







    }


    @Override
    public void onBackPressed() {
        //DO NOTHING

    }




    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public void AmIOnlineAtAll()
    {

        MySOAPCallActivity cs1 = new MySOAPCallActivity();
        try {

            AmIOnlineParams params = new AmIOnlineParams(cs1);
            new CallSoapAmIOnline().execute(params);



        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();

        }
    }

    public void Reset(View view)
    {
       //   Intent intent = new Intent(LoginActivity.this, UnitryUploadsOnly.class);
       // startActivity(intent); finish();

      dbManager.setValue( "AndroidTssOpenRequests", "");
        dbManager.setValue( "AndroidInsOpenRequests", "");
        dbManager.setValue( "AndroidWhsOpenRequests", "");
        dbManager.setValue( "AndroidTssAppointments", "");
        dbManager.setValue( "AndroidInsAppointments", "");
        dbManager.setValue( "AndroidWhsAppointments", "");
        dbManager.setValue( "AndroidStoreUnits", "");
        dbManager.setValue( "AndroidStoreUnitsRep", "");
        dbManager.setValue( "AndroidStoreUnitsIns", "");
        dbManager.setValue( "AndroidStoreUnitsTss", "");
        dbManager.setValue( "AndroidStoreUnitsWhs", "");
        dbManager.setValue( "UserName", "");

        dbManager.setValue( "AndroidStores", "");
        dbManager.setValue( "AndroidStoresIns", "");
        dbManager.setValue( "AndroidStoresWhs", "");
        dbManager.setValue( "AndroidStoresTss", "");

        dbManager.setValue( "AndroidSurveys", "");

        String Today = dbManager.getValue( "Today");
        dbManager.setValue( "ResetAllCheckins", "False");

        String resetcheckins = dbManager.getValue( "ResetAllCheckins");
        if(resetcheckins.equals("True"))
        {
            dbManager.setValue( "ResetAllCheckins", "False");
        }
        else        {
            dbManager.setValue( "ResetAllCheckins", "True");
        }


    }




    public void Login(View view) {

        String isWebsiteAvailable = dbManager.getValue( "AmIOnline");
        dbManager.getValue("AmIOnline");

        isWebsiteAvailable = "True"; //TODO remove hardcode

        EditText userName = (EditText) findViewById(R.id.editUserName);
        String user = userName.getText().toString();

        dbManager.setValue("UserName", user );
        dbManager.setValue("UserName", user);

        CheckBox skipNews = (CheckBox)findViewById(R.id.checkBox);
        if(skipNews.isChecked())
        {
            mSkipNews = true;
        }

        if(isWebsiteAvailable.equals("True")) {

            OnlineSync();


        }

        if (isWebsiteAvailable.equals("True")) {
            final AlertDialog ad = new AlertDialog.Builder(this).create();
            MySOAPCallActivity cs = new MySOAPCallActivity();
            try {
                 userName = (EditText) findViewById(R.id.editUserName);
                EditText password = (EditText) findViewById(R.id.editPassword);
                user = userName.getText().toString();
                String pwd = password.getText().toString();
                LoginParams params = new LoginParams(cs, user, pwd);

                dbManager.setValue( "UserName", user);
                dbManager.setValue( "Password", pwd);



                new CallSoapLogin().execute(params);
                // new CallSoapGetCurrentEvents().execute(params);


                //set and remove prrogress bar
                progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setMessage("Verifying ..."); // Setting Message
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

        } else {
            //Offline Login
             userName = (EditText) findViewById(R.id.editUserName);
            EditText password = (EditText) findViewById(R.id.editPassword);
            user = userName.getText().toString();
            String pwd = password.getText().toString();

            if (user.equals(dbManager.getValue( "UserName")) && pwd.equals(dbManager.getValue( "Password"))) {
                Intent intent = new Intent(LoginActivity.this, GoodNews.class);

                if(mSkipNews)
                {
                    intent = new Intent(LoginActivity.this, HomeMenu.class);

                }

                startActivity(intent); finish();
                finish();
                //

            } else {
                final AlertDialog ad = new AlertDialog.Builder(this).create();
                ad.setTitle("Login Error");
                ad.setMessage("Offline saved username and password invalid. Please login online first.");

            }
        }


    }

    public void OnlineSync() {

        //Save Stores
        MySOAPCallActivity cs = new MySOAPCallActivity();
        try {


            String mystores = dbManager.getValue( "AndroidStores");
            SaveMyStoresParams params = new SaveMyStoresParams(cs, mystores);

            new CallSoapSaveMyStores().execute(params);


        } catch (Exception ex) {

            Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();

        }



        //Save Appointments
        MySOAPCallActivity cs1 = new MySOAPCallActivity();
        try {


            String myappointments = dbManager.getValue( "AndroidTssAppointments");
            SaveMyAppointmentsParams params = new SaveMyAppointmentsParams(cs1, myappointments);

            new CallSoapSaveMyAppointments().execute(params);

            myappointments = dbManager.getValue( "AndroidInsAppointments");
            params = new SaveMyAppointmentsParams(cs1, myappointments);

            new CallSoapSaveMyAppointments().execute(params);

            myappointments = dbManager.getValue( "AndroidWhsAppointments");
            params = new SaveMyAppointmentsParams(cs1, myappointments);

            new CallSoapSaveMyAppointments().execute(params);


        } catch (Exception ex) {

            Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();

        }


        //Save Open Requests
        MySOAPCallActivity cs2 = new MySOAPCallActivity();
        try {


            String myopenrequests = dbManager.getValue( "AndroidTssOpenRequests");
            SaveMyOpenRequestsParams params = new SaveMyOpenRequestsParams(cs2, myopenrequests);

            new CallSoapSaveMyOpenRequests().execute(params);



            myopenrequests = dbManager.getValue( "AndroidInsOpenRequests");
            params = new SaveMyOpenRequestsParams(cs2, myopenrequests);

            new CallSoapSaveMyOpenRequests().execute(params);

            myopenrequests = dbManager.getValue( "AndroidWhsOpenRequests");
            params = new SaveMyOpenRequestsParams(cs2, myopenrequests);

            new CallSoapSaveMyOpenRequests().execute(params);



        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();


        }

    }
    public void GetGoodNewsfromWS(){

        MySOAPCallActivity cs1 = new MySOAPCallActivity();
        try {
            String UserName;

            GetGoodNewsParams params = new GetGoodNewsParams(cs1);

            new CallSoapGetGoodNews().execute(params);




        } catch (Exception ex) {

            Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();

        }



    }


    public class CallSoapGetGoodNews extends AsyncTask<GetGoodNewsParams, Void, String> {

        private Exception exception;

        @Override
        protected String doInBackground(GetGoodNewsParams... params) {
            return params[0].foo.GetGoodNews();
        }

        protected void onPostExecute(String result) {
            // TODO: check this.exception
            // TODO: do something with the feed
            try {


                //TODO: return to Home?
                dbManager.setValue( "ContentManagementPages", result);




            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
            }

        }



    }
    private static class GetGoodNewsParams {
        MySOAPCallActivity foo;

        GetGoodNewsParams(MySOAPCallActivity foo) {
            this.foo = foo;


        }
    }



    public class CallSoapLogin extends AsyncTask<LoginParams, Void, String> {

        private Exception exception;

        @Override
        protected String doInBackground(LoginParams... params) {
            return params[0].foo.Login(params[0].username, params[0].password);
        }

        protected void onPostExecute(String result) {
            // TODO: check this.exception
            // TODO: do something with the feed
            try {

                //TextView loginResult = (TextView) findViewById(R.id.labelLoginResult);
                //loginResult.setVisibility(View.VISIBLE);
                //loginResult.setText(result);

                // Button buttonUnsetEvent = (Button)findViewById(R.id.buttonUnsetEvent);
                // buttonUnsetEvent.setEnabled(true);

                //Spinner spinner2 = (Spinner)findViewById(R.id.spinner2);
                //spinner2.setEnabled(true);

                boolean LoginSuccessful = false;
                dbManager.setValue( "Roles", result);

                if (result.toLowerCase().contains("success")) {
                    LoginSuccessful = true;
                }

                if (LoginSuccessful) {
                    String user = dbManager.getValue( "UserName");
                    dbManager.setValue( "LoggedIn", user);
                    //if successful:
                    Intent intent = new Intent(LoginActivity.this, GoodNews.class);

                    CheckBox skipNews = (CheckBox)findViewById(R.id.checkBox);
                    if(skipNews.isChecked())
                    {
                        mSkipNews = true;
                    }

                    if(mSkipNews) {
                        intent = new Intent(LoginActivity.this, HomeMenu.class);
                    }

                    startActivity(intent); finish();


                } else {
                    //Do nothing
                    Toast.makeText(getApplicationContext(), "Invalid Login Credentials. Please check.", Toast.LENGTH_LONG).show();
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

    private static class LoginParams {
        MySOAPCallActivity foo;
        String username;
        String password;


        LoginParams(MySOAPCallActivity foo, String username, String password) {
            this.foo = foo;
            this.username = username;
            this.password = password;

        }
    }

    public class CallSoapAmIOnline extends AsyncTask<AmIOnlineParams, Void, String> {

        private Exception exception;

        @Override
        protected String doInBackground(AmIOnlineParams... params) {
            return params[0].foo.AmIOnline();
        }

        protected void onPostExecute(String result) {

            boolean OnlineYes = false;

            try {


                if (result.toLowerCase().contains("success")) {
                    OnlineYes = true;

                    dbManager.setValue( "AmIOnline", "True");
                    //if successful:


                } else {
                    dbManager.setValue( "AmIOnline", "False");




                }
            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
                dbManager.setValue( "AmIOnline", "False");
            }

        }


    }

    private static class AmIOnlineParams {
        MySOAPCallActivity foo;


        AmIOnlineParams(MySOAPCallActivity foo) {
            this.foo = foo;


        }

    }



    public class CallSoapSaveMyOpenRequests extends AsyncTask<SaveMyOpenRequestsParams, Void, String> {

        private Exception exception;

        @Override
        protected String doInBackground(SaveMyOpenRequestsParams... params) {
            return params[0].foo.SaveMyOpenRequests(params[0].jsonRequests);
        }

        protected void onPostExecute(String result) {

            try {


            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
            }

        }



    }
    private static class SaveMyOpenRequestsParams {
        MySOAPCallActivity foo;
        String jsonRequests;



        SaveMyOpenRequestsParams(MySOAPCallActivity foo, String jsonRequests) {
            this.foo = foo;
            this.jsonRequests = jsonRequests;


        }
    }


    public class CallSoapSaveMyStores extends AsyncTask<SaveMyStoresParams, Void, String> {

        private Exception exception;

        @Override
        protected String doInBackground(SaveMyStoresParams... params) {
            return params[0].foo.SaveMyStores(params[0].jsonStores);
        }

        protected void onPostExecute(String result) {
            // TODO: check this.exception
            // TODO: do something with the feed
            try {


                dbManager.setValue( "AnroidStores", "");


            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
            }

        }



    }
    private static class SaveMyStoresParams {
        MySOAPCallActivity foo;
        String jsonStores;



        SaveMyStoresParams(MySOAPCallActivity foo, String jsonStores) {
            this.foo = foo;
            this.jsonStores = jsonStores;


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


                dbManager.setValue( "AnroidTssAppointments", "");


            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
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



}


