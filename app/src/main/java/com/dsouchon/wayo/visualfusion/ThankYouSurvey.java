package com.dsouchon.wayo.visualfusion;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.games.leaderboard.LeaderboardEntity;

import org.json.JSONException;

import java.util.ArrayList;

public class ThankYouSurvey extends AppCompatActivity {
    ProgressDialog progressDialog;
    public int RequestID;
    private DBManager dbManager;
    @Override
    public void onBackPressed() {
        //DO NOTHING
        WhatClickProceedDoes();
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
        setContentView(R.layout.activity_thank_you_survey);
    }

    public AndroidAppointment findAppointmentByID(int id, ArrayList<AndroidAppointment> requests){
        for (AndroidAppointment request : requests) {
            if (request.getID() == id) {
                return request;
            }
        }
        return null;
    }

    public void WhatClickProceedDoes(){
        try {
            progressDialog = new ProgressDialog(ThankYouSurvey.this);
            progressDialog.setMessage("Survey uploading ..."); // Setting Message
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

            String storesString = dbManager.getValue( "AndroidInsAppointments");

            ArrayList<AndroidAppointment> appointments = JsonUtil.parseJsonArrayAndroidAppointment(storesString);
            RequestID = Integer.parseInt(dbManager.getValue( "RequestID"));

            if(RequestID == 0) {
                Intent intent = new Intent(ThankYouSurvey.this, InstallHome.class);
                if(progressDialog !=null)
                {
                    progressDialog.dismiss();
                }
                startActivity(intent);
                finish();
            }
            else {
                Intent intent = new Intent(ThankYouSurvey.this, InstallSetAppointment.class);

                //Convert request to appointment
                AndroidAppointment appointment = findAppointmentByID(RequestID, appointments);
                intent.putExtra("RequestID", getIntent().getIntExtra("RequestID", 0));

                intent.putExtra("ID", appointment.getID());
                intent.putExtra("StoreID", appointment.getStoreID());
                intent.putExtra("RequestTypeName", appointment.getRequestTypeName());
                intent.putExtra("BrandName", appointment.getBrandName());
                intent.putExtra("OutletTypeName", appointment.getOutletTypeName());
                intent.putExtra("TierTypeName", appointment.getTierTypeName());
                intent.putExtra("JobCardCount", appointment.getJobCardCount());
                intent.putExtra("ExteriorImageCount", appointment.getExteriorImageCount());
                intent.putExtra("InteriorImageCount", appointment.getInteriorImageCount());
                intent.putExtra("SurveySignatureCount", appointment.getSurveySignatureCount());
                intent.putExtra("PerformanceReviewSignatureCount", appointment.getPerformanceReviewSignatureCount());

                intent.putExtra("StoreName", appointment.getStoreName());
                intent.putExtra("StoreNameURN", appointment.getStoreNameURN());
                intent.putExtra("URN", appointment.getURN());
                intent.putExtra("CurrentPhase", appointment.getCurrentPhase());
                intent.putExtra("ContactPerson", appointment.getContactPerson());
                intent.putExtra("ContactEmail", appointment.getContactEmail());
                intent.putExtra("ContactPhone", appointment.getContactPhone());
                intent.putExtra("OpeningTime", appointment.getOpeningTime());
                intent.putExtra("ClosingTime", appointment.getClosingTime());
                intent.putExtra("TotalUnitCount", appointment.getTotalUnitCount());
                intent.putExtra("StoreID", appointment.getStoreID());
                intent.putExtra("DateRequested", appointment.getDateRequested());
                intent.putExtra("DateAccepted", appointment.getDateAccepted());
                intent.putExtra("AppointmentDateTime", appointment.getAppointmentDateTime());
                intent.putExtra("AppointmentDateDay", appointment.getAppointmentDateDay());
                intent.putExtra("AppointmentDateMonth", appointment.getAppointmentDateMonth());
                intent.putExtra("AppointmentDateYear", appointment.getAppointmentDateYear());
                intent.putExtra("AppointmentDateTimeTime", appointment.getAppointmentDateTimeTime());
                intent.putExtra("DateConfirmed", appointment.getDateConfirmed());
                intent.putExtra("DateRecordChanged", appointment.getDateRecordChanged());
                intent.putExtra("Address", appointment.getAddress());
                intent.putExtra("Region", appointment.getRegion());

                intent.putExtra("BrandName", appointment.getBrandName());
                intent.putExtra("OutletTypeName", appointment.getOutletTypeName());
                intent.putExtra("TierTypeName", appointment.getTierTypeName());
                intent.putExtra("JobCardCount", appointment.getJobCardCount());
                intent.putExtra("ExteriorImageCount", appointment.getExteriorImageCount());
                intent.putExtra("InteriorImageCount", appointment.getInteriorImageCount());
                intent.putExtra("SurveySignatureCount", appointment.getSurveySignatureCount());
                intent.putExtra("PerformanceReviewSignatureCount", appointment.getPerformanceReviewSignatureCount());
                if(progressDialog !=null)
                {
                    progressDialog.dismiss();
                }
                startActivity(intent);
                finish();
            }
        }
        catch(Exception e)
        {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();

        }
        if(progressDialog !=null)
        {
            progressDialog.dismiss();
        }
    }

    public void clickProceedToInstallHome(View view) throws JSONException
    {
        WhatClickProceedDoes();
    }

}
