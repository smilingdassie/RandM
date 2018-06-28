package com.dsouchon.wayo.visualfusion;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;

import static java.lang.Integer.parseInt;

public class PreSignPerformanceReview extends AppCompatActivity {
    private DBManager dbManager;
    Button Fav1btn;
    Button Fav2btn;
    Button Fav3btn;
    Button Fav4btn;
    Button Fav5btn;
    Button Continue;
    ProgressDialog progressDialog;


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }


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
        setContentView(R.layout.pressignperformancereview);




    }



    public void Continue(View view) throws JSONException {



        dbManager.setValue( "SignaturePurpose", "PerformanceReview");

        AndroidPerformanceReview review = new AndroidPerformanceReview();
        review.setScore1(Integer.parseInt(dbManager.getValue( "PRS1")));
        review.setScore2(Integer.parseInt(dbManager.getValue( "PRS2")));
        review.setScore3(Integer.parseInt(dbManager.getValue( "PRS3")));
        review.setScore4(Integer.parseInt(dbManager.getValue( "PRS4")));
        review.setScore5(Integer.parseInt(dbManager.getValue( "PRS5")));
        review.setScore6(Integer.parseInt(dbManager.getValue( "PRS6")));
        review.setScore7(Integer.parseInt(dbManager.getValue( "PRS7")));

        EditText editPersonName = (EditText)  findViewById(R.id.editPersonName);
        EditText editPersonDesignation = (EditText)  findViewById(R.id.editPersonDesignation);

        review.setSignaturePersonName( editPersonName.getText().toString() );
        review.setSignatureDesignation( editPersonDesignation.getText().toString() );
        review.setInstallationUserName( dbManager.getValue( "UserName"));
        review.setRequestID(Integer.parseInt(dbManager.getValue( "RequestID")));

        //save review locally

        SavePerformanceReviewToArray(review);


        String isWebsiteAvailable = dbManager.getValue( "AmIOnline");

        if(isWebsiteAvailable.equals("True")) {
            //Save My Appointments - this will also 'delete' the request online
            MySOAPCallActivity cs1 = new MySOAPCallActivity();
            try {


                String reviews = dbManager.getValue( "AndroidPerformanceReviews");
                SavePerformanceReviewParams params = new SavePerformanceReviewParams(cs1, reviews);

                new CallSoapSavePerformanceReview().execute(params);



                //set and remove prrogress bar
                progressDialog = new ProgressDialog(PreSignPerformanceReview.this);
                progressDialog.setMessage("Saving ..."); // Setting Message
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

        }
        
        
        
        
        
        

        
        
        

    }


    public void  SavePerformanceReviewToArray(AndroidPerformanceReview review) throws JSONException {

        try {

            String reviewsString = dbManager.getValue( "AndroidPerformanceReviews");

            ArrayList<AndroidPerformanceReview> reviews = JsonUtil.parseJsonArrayAndroidPerformanceReview(reviewsString);
            //reviews.clear();
            reviews.add(review);

            //Save Requests back to local
            dbManager.setValue( "AndroidPerformanceReviews", JsonUtil.convertPerformanceReviewArrayToJson(reviews));


        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
        }
        //

    }



    public class CallSoapSavePerformanceReview extends AsyncTask<SavePerformanceReviewParams, Void, String> {

        private Exception exception;

        @Override
        protected String doInBackground(SavePerformanceReviewParams... params) {
            return params[0].foo.SavePerformanceReview(params[0].jsonPerformanceReviews);
        }

        protected void onPostExecute(String result) {

            try {
                EditText yourName = (EditText) findViewById(R.id.editPersonName);
                EditText designation = (EditText) findViewById(R.id.editPersonDesignation);

                Intent intent = new Intent(PreSignPerformanceReview.this, SignatureActivity.class );
                int RequestID = Integer.parseInt(dbManager.getValue( "RequestID"));
                int globalStoreID = Integer.parseInt(dbManager.getValue( "StoreID"));
                intent.putExtra("RequestID", RequestID );
                intent.putExtra("SignatureType", "PerformanceScoreCardSignature");
                intent.putExtra("YourName", yourName.getText().toString());
                intent.putExtra("Designation", designation.getText().toString());

                intent.putExtra("FromScreen", "PerformanceReview");
                //intent.putExtra("ID", globalStoreID);
                //intent.putExtra("StoreNameURN", globalStoreNameURN);

                intent.putExtra("StoreID", globalStoreID);


                dbManager.setValue( "YourName", yourName.getText().toString());
                dbManager.setValue( "Designation", designation.getText().toString());

                //clear reviews after upload to server
                dbManager.setValue( "AndroidPerformanceReviews", "");

                startActivity(intent); finish();
                if(progressDialog !=null)
                {
                    progressDialog.dismiss();
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
    private static class SavePerformanceReviewParams {
        MySOAPCallActivity foo;
        String jsonPerformanceReviews;



        SavePerformanceReviewParams(MySOAPCallActivity foo, String jsonPerformanceReviews) {
            this.foo = foo;
            this.jsonPerformanceReviews = jsonPerformanceReviews;


        }
    }



}
