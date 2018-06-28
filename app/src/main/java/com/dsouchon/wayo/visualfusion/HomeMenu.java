package com.dsouchon.wayo.visualfusion;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class HomeMenu extends AppCompatActivity {

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
    public void onBackPressed() {

        startActivity(new Intent(this,LoginActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);  dbManager = new DBManager(this);        dbManager.open();
        setContentView(R.layout.homemenu_layout);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        String roles = dbManager.getValue( "Roles");


        String UserName =  dbManager.getValue("UserName" );
        TextView textView = (TextView)findViewById(R.id.textView1);
        textView.setText(UserName);

        int counter = 0;
        for( int i=0; i<roles.length(); i++ ) {
            if( roles.charAt(i) == '\n' ) {
                counter++;
            }
        }


        Button btnRep = (Button) findViewById(R.id.btnRep);
        btnRep.setVisibility(View.GONE);


        Button btnTss = (Button) findViewById(R.id.btnTss);
        btnTss.setVisibility(View.GONE);


            Button btnWhs = (Button) findViewById(R.id.btnWhs);
            btnWhs.setVisibility(View.GONE);


        if(counter > 0)//more than one role so show menu
        {
            if (roles.contains("Fmn")) {
                dbManager.setValue( "IsForeman", "True");
            }
            if (!roles.contains("Ins")) {
                Button btnIns = (Button) findViewById(R.id.btnIns);
                btnIns.setVisibility(View.GONE);
            }
            if (!roles.contains("Srv")) {
                Button btnIns = (Button) findViewById(R.id.btnSrv);
                btnIns.setVisibility(View.GONE);
            }
        }
        else {
            //New fast track for Ins
            if (roles.contains("Ins")) {
                dbManager.setValue( "PrimaryRole", "Ins");
                Intent intent = new Intent(HomeMenu.this, InstallHome.class);
                startActivity(intent);
                finish();
            }

            //New fast track for Srv
            if (roles.contains("Srv")) {
                dbManager.setValue( "PrimaryRole", "Srv");
                Intent intent = new Intent(HomeMenu.this, InstallHome.class);
                startActivity(intent);
                finish();
            }
        }

    }

    public void Login(View view) {
        //Intent intent = new Intent(LoginActivity.this, RepHomeActivity.class );
        Intent intent = new Intent(HomeMenu.this, RepHomeActivity.class );

        startActivity(intent); finish();
    }

    public void TssLogin(View view) {

        Intent intent = new Intent(HomeMenu.this, TssHome.class );
        startActivity(intent); finish();
    }

    public void WareHouseLogin(View view) {

        Intent intent = new Intent(HomeMenu.this, WareHouseHomeActivity.class );
        startActivity(intent); finish();
    }

    public void InstLogin(View view) {

        dbManager.setValue( "PrimaryRole", "Ins");
        Intent intent = new Intent(HomeMenu.this, InstallHome.class);
        startActivity(intent);
        finish();
    }

    public void SrvLogin(View view) {

        dbManager.setValue( "PrimaryRole", "Srv");
        Intent intent = new Intent(HomeMenu.this, InstallHome.class);
        startActivity(intent);
        finish();
    }




}
