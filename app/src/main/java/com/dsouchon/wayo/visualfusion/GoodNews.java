package com.dsouchon.wayo.visualfusion;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;

public class GoodNews extends AppCompatActivity {
    private DBManager dbManager;
    public void onBackPressed() {
        //DO NOTHING
        Intent intent = new Intent(GoodNews.this, LoginActivity.class );
        startActivity(intent); finish();
    }/************SET AND GET GLOBAL VARIABLES ******************/
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
        try {
             super.onCreate(savedInstanceState);  dbManager = new DBManager(this);        dbManager.open();
            setContentView(R.layout.activity_good_news);
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(GoodNews.this, GoodNews2.class );
            startActivity(intent); finish();
        }

    }
    public void Continue(View view) {
        //Intent intent = new Intent(LoginActivity.this, RepHomeActivity.class );
        Intent intent = new Intent(GoodNews.this, GoodNews2.class );

        startActivity(intent); finish();
    }

}
