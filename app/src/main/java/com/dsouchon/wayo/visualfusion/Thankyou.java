package com.dsouchon.wayo.visualfusion;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Thankyou extends AppCompatActivity {

    Button Fav1btn;
    Button Fav2btn;
    Button Fav3btn;
    Button Fav4btn;
    Button Fav5btn;
    Button Continue;

    private DBManager dbManager;

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
        setContentView(R.layout.thankyou);




    }



    public void Continue(View view) {

        Intent intent = new Intent(Thankyou.this, InstallHome.class );

        startActivity(intent);
    }
}
