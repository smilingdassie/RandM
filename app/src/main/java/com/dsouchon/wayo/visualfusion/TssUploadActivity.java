package com.dsouchon.wayo.visualfusion;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import com.dsouchon.wayo.visualfusion.R;

import com.dsouchon.wayo.visualfusion.R;
/**
 * Created by Donavan on 2017/01/16.
 */

public class TssUploadActivity extends AppCompatActivity {



    // Activity for image uploads page for reps
    //style xml files are grid_row_uploads.xml



    GridView gridView;
    TssGridUploads grisViewCustomeAdapter;


    @Override
    public void onBackPressed() {     }      @Override  protected void onCreate(Bundle savedInstanceState)
    {
         super.onCreate(savedInstanceState);  //dbManager = new DBManager(this);        dbManager.open();
        setContentView(R.layout.tss_upload_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        gridView=(GridView)findViewById(R.id.gridViewCustom2);
        // Create the Custom Adapter Object
        grisViewCustomeAdapter = new TssGridUploads(this);
        // Set the Adapter to GridView
        gridView.setAdapter(grisViewCustomeAdapter);




    }


    public void stockinput(View view) {

        Intent intent = new Intent(TssUploadActivity.this, RepItemsActivity.class );

        startActivity(intent); finish();
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

            Intent intent = new Intent(TssUploadActivity.this, HomeMenu.class );

            startActivity(intent); finish();

            // return true;
        }



        if (id == R.id.logoutbutton){
            startActivity(new Intent(this,LoginActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }




    public void NewValidate(View view) {
        Intent intent = new Intent(TssUploadActivity.this, TssValidateStore.class );
        startActivity(intent); finish();
    }
}












