package com.dsouchon.wayo.visualfusion;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import static com.dsouchon.wayo.visualfusion.R.id.UnitaryLists;

/**
 * Created by Donavan on 2017/01/16.
 */

public class RepHomeActivity extends AppCompatActivity  {

    private DBManager dbManager;

    @Override
    public void onBackPressed() {     }
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

        setContentView(R.layout.rep_home_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Button btnUnitaryLists = (Button) findViewById(R.id.UnitaryLists);
        btnUnitaryLists.setVisibility(View.INVISIBLE);
        String isWebsiteAvailable = dbManager.getValue( "AmIOnline");

        if (isWebsiteAvailable.equals("True")) {

            SaveMyStores();
            GetStoresForUser(dbManager.getValue( "UserName"), "Rep");
            GetUnitListsForUser1(dbManager.getValue( "UserName"), "Rep");
        }
        else{
            //I am Offline
            //Use locally saved lists
            btnUnitaryLists.setVisibility(View.VISIBLE);
        }
    }



    public void GetStoresForUser(String UserName, String Phase) {

        String isWebsiteAvailable = dbManager.getValue( "AmIOnline");

        if(isWebsiteAvailable.equals("True")) {

            final AlertDialog ad = new AlertDialog.Builder(this).create();
            MySOAPCallActivity cs = new MySOAPCallActivity();
            try {

                RepHomeActivity.GetStoresForUserParams params = new RepHomeActivity.GetStoresForUserParams(cs, UserName, Phase);

                new RepHomeActivity.CallSoapGetStoresForUser().execute(params);


            } catch (Exception ex) {


            }


        }
        else {
            //I am OFFLINE
            //Do Nothing - you'll just use the stores you have :)


        }
    }



    public void UnitaryList(View view) {

        Intent intent = new Intent(RepHomeActivity.this, RepSelectStoreForUnitry.class );
        startActivity(intent); finish();
    }

    public void SaveMyStores() {

        String isWebsiteAvailable = dbManager.getValue( "AmIOnline");

        if(isWebsiteAvailable.equals("True")) {


            //Save Stores
            MySOAPCallActivity cs = new MySOAPCallActivity();
            try {


                String mystores = dbManager.getValue( "AndroidStores");
                SaveMyStoresParams params = new SaveMyStoresParams(cs, mystores);

                new CallSoapSaveMyStores().execute(params);


            } catch (Exception ex) {


            }


        }
        else {
            //I am OFFLINE
            //Do Nothing - you'll just use the stores you have :)


        }
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

            Intent intent = new Intent(RepHomeActivity.this, HomeMenu.class );

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



    public void addStore(View view) {

            Intent intent = new Intent(RepHomeActivity.this, RepStoreDetailsActivity.class );

            startActivity(intent); finish();

    }

    public void progress(View view) {

        Intent intent = new Intent(RepHomeActivity.this, RepProgressActivity.class );

        startActivity(intent); finish();

    }

    public class CallSoapGetStoresForUser extends AsyncTask<RepHomeActivity.GetStoresForUserParams, Void, String> {

        private Exception exception;

        @Override
        protected String doInBackground(RepHomeActivity.GetStoresForUserParams... params) {
            return params[0].foo.GetStoresForUser(params[0].username, params[0].phase);
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
                    dbManager.setValue( "AndroidStores", result);
                }

            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
            }

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


                //   Intent intent = new Intent(LoginActivity.this, HomeMenu.class );

                //   startActivity(intent); finish();
                //


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


    public void GetUnitListsForUser1(String UserName, String PhaseMatters) {

        String isWebsiteAvailable = dbManager.getValue( "AmIOnline");

        if(isWebsiteAvailable.equals("True")) {

            final AlertDialog ad = new AlertDialog.Builder(this).create();
            MySOAPCallActivity cs = new MySOAPCallActivity();
            try {

                GetUnitListsForUserParams params = new GetUnitListsForUserParams(cs, UserName, PhaseMatters);

                new CallSoapGetUnitListsForUser().execute(params);


            } catch (Exception ex) {


            }


        }
        else {
            //I am OFFLINE
            //Do Nothing - you'll just use the stores you have :)


        }
    }

    public class CallSoapGetUnitListsForUser extends AsyncTask<GetUnitListsForUserParams, Void, String> {

        private Exception exception;

        @Override
        protected String doInBackground(GetUnitListsForUserParams... params) {
            return params[0].foo.GetUnitListsForUser1(params[0].username, params[0].phasematters);
        }

        protected void onPostExecute(String result) {
            // TODO: check this.exception
            // TODO: do something with the feed
            try {
                //process Json Result
                //Save results in local storage
                if(result.toLowerCase().contains("error")){
                    Button btnUnitaryLists = (Button) findViewById(R.id.UnitaryLists);
                    btnUnitaryLists.setVisibility(View.INVISIBLE);
                }
                else {
                    dbManager.setValue( "AndroidStoreUnitsRep", result);
                    Button btnUnitaryLists = (Button) findViewById(R.id.UnitaryLists);
                    btnUnitaryLists.setVisibility(View.VISIBLE);
                }

            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
            }

        }



    }
    private static class GetUnitListsForUserParams {
        MySOAPCallActivity foo;
        String username;
        String phasematters;



        GetUnitListsForUserParams(MySOAPCallActivity foo, String username, String phasematters) {
            this.foo = foo;
            this.username = username;
            this.phasematters = phasematters;


        }
    }


}
