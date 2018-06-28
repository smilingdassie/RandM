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
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONException;

import java.util.ArrayList;

public class RepProgressActivity extends AppCompatActivity
{
    private DBManager dbManager;

    GridView gridView;
    RepGridViewCustomProgress grisViewCustomeAdapter;
    ArrayList<AndroidStore> mystores;

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
        setContentView(R.layout.rep_progress_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mystores = getStoresData();


        gridView=(GridView)findViewById(R.id.gridViewCustom3);
        // Create the Custom Adapter Object
        grisViewCustomeAdapter = new RepGridViewCustomProgress(this, mystores);
        // Set the Adapter to GridView
        gridView.setAdapter(grisViewCustomeAdapter);



        //sets where list item is clicking too
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int pos, long id) {
                //Toast.makeText(getApplicationContext(), names[pos], Toast.LENGTH_LONG).show();

                Intent intent = new Intent(RepProgressActivity.this, RepInfoActivity.class );

                AndroidStore store = mystores.get(pos);
                intent.putExtra("ID", store.getID());
                intent.putExtra("StoreName", store.getStoreName());
                intent.putExtra("URN", store.getURN());
                intent.putExtra("StoreNameURN", store.getStoreNameURN());

                intent.putExtra("CurrentPhase", store.getCurrentPhase());
                intent.putExtra("TerritoryName", store.getTerritoryName());
                intent.putExtra("BrandName", store.getBrandName());
                intent.putExtra("TierTypeName", store.getTierTypeName());
                intent.putExtra("OutletTypeName", store.getOutletTypeName());
                intent.putExtra("ContactPerson", store.getContactPerson());
                intent.putExtra("ContactEmail", store.getContactEmail());
                intent.putExtra("ContactPhone", store.getContactPhone());
                intent.putExtra("OpeningTime", store.getOpeningTime());
                intent.putExtra("ClosingTime", store.getClosingTime());
                intent.putExtra("TotalUnitCount", store.getTotalUnitCount());
                intent.putExtra("AddressLine1", store.getAddressLine1());
                intent.putExtra("AddressLine2", store.getAddressLine2());
                intent.putExtra("TownCity", store.getTownCity());
                intent.putExtra("RepFirstNameSurname", store.getRepFirstNameSurname());
                intent.putExtra("RepJobTitle", store.getRepJobTitle());
                intent.putExtra("RepCellNo", store.getRepCellNo());
                intent.putExtra("TssFirstNameSurname", store.getTssFirstNameSurname());
                intent.putExtra("TssJobTitle", store.getTssJobTitle());
                intent.putExtra("TssCellNo", store.getTssCellNo());
                intent.putExtra("InsFirstNameSurname", store.getInsFirstNameSurname());
                intent.putExtra("InsJobTitle", store.getInsJobTitle());
                intent.putExtra("InsCellNo", store.getInsCellNo());
                intent.putExtra("DateRecordChanged", store.getDateRecordChanged());
                intent.putExtra("GpsLat", store.getGpsLat());
                intent.putExtra("GpsLng", store.getGpsLng());



                startActivity(intent); finish();
            }

        } );

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

            Intent intent = new Intent(RepProgressActivity.this, HomeMenu.class );

            startActivity(intent); finish();

            // return true;
        }



        if (id == R.id.logoutbutton){
            startActivity(new Intent(this,LoginActivity.class));
        }

        if (id == R.id.backbutton){
            startActivity(new Intent(this,RepHomeActivity.class));
        }

        return super.onOptionsItemSelected(item);
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


        json = dbManager.getValue( "AndroidStores");

        try {
            stores = JsonUtil.parseJsonArrayAndroidStore(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return stores;
    }

}