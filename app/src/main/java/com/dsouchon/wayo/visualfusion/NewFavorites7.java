package com.dsouchon.wayo.visualfusion;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;

public class NewFavorites7 extends AppCompatActivity {
    private DBManager dbManager;
    Button Fav1btn;
    Button Fav2btn;
    Button Fav3btn;
    Button Fav4btn;
    Button Fav5btn;
    Button Continue;
    int mPRS;


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
        setContentView(R.layout.activity_new_favorites7);


        Fav1btn = (Button) findViewById(R.id.fav1id);
        Fav2btn = (Button) findViewById(R.id.fav2id);
        Fav3btn = (Button) findViewById(R.id.fav3id);
        Fav4btn = (Button) findViewById(R.id.fav4id);
        Fav5btn = (Button) findViewById(R.id.fav5id);


        Fav1btn.setBackgroundResource(R.drawable.button_notactive_heart);
        Fav2btn.setBackgroundResource(R.drawable.button_notactive_heart);
        Fav3btn.setBackgroundResource(R.drawable.button_notactive_heart);
        Fav4btn.setBackgroundResource(R.drawable.button_notactive_heart);
        Fav5btn.setBackgroundResource(R.drawable.button_notactive_heart);

    }

    public void Favorites1(View view) {
        Fav1btn = (Button) findViewById(R.id.fav1id);
        Fav2btn = (Button) findViewById(R.id.fav2id);
        Fav3btn = (Button) findViewById(R.id.fav3id);
        Fav4btn = (Button) findViewById(R.id.fav4id);
        Fav5btn = (Button) findViewById(R.id.fav5id);
        Continue = (Button) findViewById(R.id.continue_btn);

        Fav1btn.setBackgroundResource(R.drawable.button_active_heart);
        Fav2btn.setBackgroundResource(R.drawable.button_notactive_heart);
        Fav3btn.setBackgroundResource(R.drawable.button_notactive_heart);
        Fav4btn.setBackgroundResource(R.drawable.button_notactive_heart);
        Fav5btn.setBackgroundResource(R.drawable.button_notactive_heart);
        Continue.setEnabled(true);



        TextView tv = (TextView)findViewById(R.id.answer);
         tv.setText("Very Poor"); mPRS = 1;
        mPRS = 1;

    }

    public void Favorites2(View view) {
        Fav1btn = (Button) findViewById(R.id.fav1id);
        Fav2btn = (Button) findViewById(R.id.fav2id);
        Fav3btn = (Button) findViewById(R.id.fav3id);
        Fav4btn = (Button) findViewById(R.id.fav4id);
        Fav5btn = (Button) findViewById(R.id.fav5id);
        Continue = (Button) findViewById(R.id.continue_btn);

        Fav1btn.setBackgroundResource(R.drawable.button_active_heart);
        Fav2btn.setBackgroundResource(R.drawable.button_active_heart);
        Fav3btn.setBackgroundResource(R.drawable.button_notactive_heart);
        Fav4btn.setBackgroundResource(R.drawable.button_notactive_heart);
        Fav5btn.setBackgroundResource(R.drawable.button_notactive_heart);
        Continue.setEnabled(true);

        TextView tv = (TextView)findViewById(R.id.answer);
         tv.setText("Poor"); mPRS = 2;

        mPRS = 2;
    }

    public void Favorites3(View view) {
        Fav1btn = (Button) findViewById(R.id.fav1id);
        Fav2btn = (Button) findViewById(R.id.fav2id);
        Fav3btn = (Button) findViewById(R.id.fav3id);
        Fav4btn = (Button) findViewById(R.id.fav4id);
        Fav5btn = (Button) findViewById(R.id.fav5id);
        Continue = (Button) findViewById(R.id.continue_btn);

        Fav1btn.setBackgroundResource(R.drawable.button_active_heart);
        Fav2btn.setBackgroundResource(R.drawable.button_active_heart);
        Fav3btn.setBackgroundResource(R.drawable.button_active_heart);
        Fav4btn.setBackgroundResource(R.drawable.button_notactive_heart);
        Fav5btn.setBackgroundResource(R.drawable.button_notactive_heart);
        Continue.setEnabled(true);

        TextView tv = (TextView)findViewById(R.id.answer);
         tv.setText("Average"); mPRS = 3;
        mPRS = 3;
    }

    public void Favorites4(View view) {
        Fav1btn = (Button) findViewById(R.id.fav1id);
        Fav2btn = (Button) findViewById(R.id.fav2id);
        Fav3btn = (Button) findViewById(R.id.fav3id);
        Fav4btn = (Button) findViewById(R.id.fav4id);
        Fav5btn = (Button) findViewById(R.id.fav5id);
        Continue = (Button) findViewById(R.id.continue_btn);

        Fav1btn.setBackgroundResource(R.drawable.button_active_heart);
        Fav2btn.setBackgroundResource(R.drawable.button_active_heart);
        Fav3btn.setBackgroundResource(R.drawable.button_active_heart);
        Fav4btn.setBackgroundResource(R.drawable.button_active_heart);
        Fav5btn.setBackgroundResource(R.drawable.button_notactive_heart);
        Continue.setEnabled(true);

        TextView tv = (TextView)findViewById(R.id.answer);
        tv.setText("Good"); mPRS = 4;

        mPRS = 4;

    }

    public void Favorites5(View view) {
        Fav1btn = (Button) findViewById(R.id.fav1id);
        Fav2btn = (Button) findViewById(R.id.fav2id);
        Fav3btn = (Button) findViewById(R.id.fav3id);
        Fav4btn = (Button) findViewById(R.id.fav4id);
        Fav5btn = (Button) findViewById(R.id.fav5id);
        Continue = (Button) findViewById(R.id.continue_btn);

        Fav1btn.setBackgroundResource(R.drawable.button_active_heart);
        Fav2btn.setBackgroundResource(R.drawable.button_active_heart);
        Fav3btn.setBackgroundResource(R.drawable.button_active_heart);
        Fav4btn.setBackgroundResource(R.drawable.button_active_heart);
        Fav5btn.setBackgroundResource(R.drawable.button_active_heart);
        Continue.setEnabled(true);


        TextView tv = (TextView)findViewById(R.id.answer);
        tv.setText("Exceptional"); mPRS = 5;


        mPRS = 5;

    }



    public void Continue(View view) {


        Intent intent = new Intent(NewFavorites7.this, PreSignPerformanceReview.class );
       //Put these in Global
       /* intent.putExtra("SurveyID", mSurveyID);
        intent.putExtra("StoreID", mStoreID);
        intent.putExtra("RequestID", RequestID);
        intent.putExtra("URN", mURN);
      */

       dbManager.setValue( "PRS7", Integer.toString(mPRS));




        startActivity(intent); finish();



    }





}
