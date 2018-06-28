package com.dsouchon.wayo.visualfusion;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;

import org.json.JSONException;

import java.util.ArrayList;

public class RepUnitaryList extends AppCompatActivity {

    GridView gridView;
    RepGridViewUnitary grisViewCustomeAdapter;
    ArrayList<AndroidStoreUnit> myunits;
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

    @Override
    public void onBackPressed() {     }      @Override  protected void onCreate(Bundle savedInstanceState)
    {
         super.onCreate(savedInstanceState);  dbManager = new DBManager(this);        dbManager.open();
        setContentView(R.layout.rep_unitary_list_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Intent me = getIntent();
        String storeName = me.getStringExtra("StoreName");

        myunits = getOpenRequestsData(storeName);

        gridView=(GridView)findViewById(R.id.gridViewCustom);
        // Create the Custom Adapter Object
        grisViewCustomeAdapter = new RepGridViewUnitary(this, myunits);
        // Set the Adapter to GridView
        gridView.setAdapter(grisViewCustomeAdapter);




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

            Intent intent = new Intent(RepUnitaryList.this, HomeMenu.class );

            startActivity(intent); finish();

            // return true;
        }



        if (id == R.id.logoutbutton){
            startActivity(new Intent(this,LoginActivity.class));
        }

        if (id == R.id.backbutton){
            startActivity(new Intent(this,RepSelectStoreForUnitry.class));
        }

        return super.onOptionsItemSelected(item);
    }


    public void Close(View view) {

        Intent intent = new Intent(RepUnitaryList.this, RepSelectStoreForUnitry.class );

        startActivity(intent); finish();



    }

    private ArrayList<AndroidStoreUnit> getOpenRequestsData(String storeName) {
        ArrayList<AndroidStoreUnit> openRequests = new ArrayList<>();
        String json = "[]";
        json = dbManager.getValue( "AndroidStoreUnitsRep");
        try {
            openRequests = JsonUtil.parseJsonArrayAndroidStoreUnit(json, storeName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return openRequests;
    }



}