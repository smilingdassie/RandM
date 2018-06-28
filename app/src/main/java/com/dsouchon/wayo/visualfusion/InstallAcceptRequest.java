package com.dsouchon.wayo.visualfusion;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class InstallAcceptRequest extends AppCompatActivity {
    private DBManager dbManager;
    GridView gridView;
    IntallGridAcceptRequest grisViewCustomeAdapter;
    protected String parseTimeForDisplay(String dateTimeWithT)
    {

        dateTimeWithT = dateTimeWithT.replace('T',' ');

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm");

        try {
            calendar.setTime(simpleDateFormat.parse(dateTimeWithT));

            String dateToShow = simpleTimeFormat.format(calendar.getTime());
            return dateToShow;

        } catch (ParseException e) {
            e.printStackTrace();
            return dateTimeWithT;
        }


    }
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
        setContentView(R.layout.install_accept_request_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        Intent me = getIntent();

        TextView ID =  (TextView)findViewById(R.id.txtID);
        Integer id = me.getIntExtra("ID", -1);
        ID.setText(id.toString());
        //TextView ID =  (TextView)findViewById(R.id.txtID);
        //ID.setText(me.getStringExtra("ID"));
        //TextView RequestTypeName =  (TextView)findViewById(R.id.txtRequestTypeName);
        //RequestTypeName.setText(me.getStringExtra("RequestTypeName"));
        TextView StoreName =  (TextView)findViewById(R.id.txtStoreName);
        StoreName.setText(me.getStringExtra("StoreNameURN"));
        //TextView URN =  (TextView)findViewById(R.id.txtURN);
        //URN.setText(me.getStringExtra("URN"));
        TextView CurrentPhase =  (TextView)findViewById(R.id.txtCurrentPhase);
        CurrentPhase.setText(me.getStringExtra("CurrentPhase"));
        TextView ContactPerson =  (TextView)findViewById(R.id.txtContactPerson);
        ContactPerson.setText(me.getStringExtra("ContactPerson"));
        TextView ContactEmail =  (TextView)findViewById(R.id.txtContactEmail);
        ContactEmail.setText(me.getStringExtra("ContactEmail"));
        TextView ContactPhone =  (TextView)findViewById(R.id.txtContactPhone);
        ContactPhone.setText(me.getStringExtra("ContactPhone"));
        TextView OpeningTime =  (TextView)findViewById(R.id.txtOpeningTime);
        OpeningTime.setText(parseTimeForDisplay(me.getStringExtra("OpeningTime")));
        TextView ClosingTime =  (TextView)findViewById(R.id.txtClosingTime);
        ClosingTime.setText(parseTimeForDisplay(me.getStringExtra("ClosingTime")));
        TextView TotalUnitCount =  (TextView)findViewById(R.id.txtTotalUnitCount);
        Integer totalUnitCount = me.getIntExtra("TotalUnitCount", -1);
        TotalUnitCount.setText(totalUnitCount.toString());

        //TextView StoreID =  (TextView)findViewById(R.id.txtStoreID);
        //StoreID.setText(me.getStringExtra("StoreID"));
        //TextView DateRequested =  (TextView)findViewById(R.id.txtDateRequested);
        //DateRequested.setText(me.getStringExtra("DateRequested"));
        //TextView DateAccepted =  (TextView)findViewById(R.id.txtDateAccepted);
        //DateAccepted.setText(me.getStringExtra("DateAccepted"));
        //TextView DateRecordChanged =  (TextView)findViewById(R.id.txtDateRecordChanged);
        //DateRecordChanged.setText(me.getStringExtra("DateRecordChanged"));


        /*gridView = (GridView) findViewById(R.id.gridViewCustom4);
        // Create the Custom Adapter Object
        grisViewCustomeAdapter = new IntallGridAcceptRequest(this);
        // Set the Adapter to GridView
        gridView.setAdapter(grisViewCustomeAdapter);*/

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

            Intent intent = new Intent(InstallAcceptRequest.this, HomeMenu.class);

            startActivity(intent); finish();

            // return true;
        }

       /* if (id == R.id.downloadBtn) {
            // startActivity(new Intent(this,MainLogin.class));
            Intent intent = new Intent(InstallAcceptRequest.this, DownloadActivity.class);

            startActivity(intent); finish();

        }*/


        if (id == R.id.logoutbutton) {
            startActivity(new Intent(this,LoginActivity.class));
        }
        if (id == R.id.backbutton){
            startActivity(new Intent(this,InstallOpenRequests.class));
        }
        return super.onOptionsItemSelected(item);
    }

    public AndroidOpenRequest findRequestByID(int id, ArrayList<AndroidOpenRequest> requests){
        for (AndroidOpenRequest request : requests) {
            if (request.getID() == id) {
                return request;
            }
        }
        return null;
    }

    public void  ConvertOpenRequestToAppointment(String requestFileName, String appointmentFileName, Integer ID) throws JSONException {

        try {

            String requestsString = dbManager.getValue( requestFileName);

            String appointmentsString = dbManager.getValue( appointmentFileName);

            ArrayList<AndroidOpenRequest> requests = JsonUtil.parseJsonArrayAndroidOpenRequest(requestsString);

            ArrayList<AndroidAppointment> appointments = JsonUtil.parseJsonArrayAndroidAppointment(appointmentsString);

            AndroidOpenRequest findrequest = findRequestByID(ID, requests);

            requests.remove(findrequest);

            //Convert request to appointment

            AndroidAppointment appointment = new AndroidAppointment();

            appointment.setID(findrequest.getID());
            appointment.setRequestTypeName(findrequest.getRequestTypeName());
            appointment.setStoreName(findrequest.getStoreName());
            appointment.setURN(findrequest.getURN());

            appointment.setUserName(dbManager.getValue( "UserName"));

            appointment.setCurrentPhase(findrequest.getCurrentPhase());
            appointment.setContactPerson(findrequest.getContactPerson());
            appointment.setContactEmail(findrequest.getContactEmail());
            appointment.setContactPhone(findrequest.getContactPhone());
            appointment.setOpeningTime(findrequest.getOpeningTime());
            appointment.setClosingTime(findrequest.getClosingTime());
            appointment.setTotalUnitCount(findrequest.getTotalUnitCount());
            appointment.setStoreID(findrequest.getStoreID());
            appointment.setDateRequested(findrequest.getDateRequested());

            Date acceptedDate = new Date();

            appointment.setDateAccepted((String) android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss a", acceptedDate));
            appointment.setDateRecordChanged((String) android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss a", acceptedDate));


            //Save Appointments back to local and append the new item
            dbManager.setValue( appointmentFileName, JsonUtil.appendJsonAndroidAppointmentToArray(appointments, appointment));
            //Save Requests back to local
            dbManager.setValue( requestFileName, JsonUtil.convertOpenRequestArrayToJson(requests));

        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
        }
        //

    }





    public void AcceptRequest(View view) throws JSONException {
        TextView txtID =  (TextView)findViewById(R.id.txtID);
        Integer ID = Integer.parseInt(txtID.getText().toString());

        ConvertOpenRequestToAppointment("AndroidInsOpenRequests", "AndroidInsAppointments", ID);

        //If I am online - Save online
        SaveIfOnline();
        Intent intent = new Intent(InstallAcceptRequest.this, InstallHome.class);
        startActivity(intent); finish();
    }

    public void SaveIfOnline()
    {

        String isWebsiteAvailable = dbManager.getValue( "AmIOnline");

        if(isWebsiteAvailable.equals("True")) {
            //Save My Appointments - this will also 'delete' the request online
            MySOAPCallActivity cs1 = new MySOAPCallActivity();
            try {


                String myappointments = dbManager.getValue( "AndroidInsAppointments");
                SaveMyAppointmentsParams params = new SaveMyAppointmentsParams(cs1, myappointments);

                new CallSoapSaveMyAppointments().execute(params);
            } catch (Exception ex) {


            }

        }

    }
    public class CallSoapSaveMyAppointments extends AsyncTask<SaveMyAppointmentsParams, Void, String> {

        private Exception exception;

        @Override
        protected String doInBackground(SaveMyAppointmentsParams... params) {
            return params[0].foo.SaveMyAppointments(params[0].jsonRequests);
        }

        protected void onPostExecute(String result) {
            // TODO: check this.exception
            // TODO: do something with the feed
            try {


                dbManager.setValue( "AnroidTssAppointments", "");


            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
            }

        }



    }
    private static class SaveMyAppointmentsParams {
        MySOAPCallActivity foo;
        String jsonRequests;



        SaveMyAppointmentsParams(MySOAPCallActivity foo, String jsonRequests) {
            this.foo = foo;
            this.jsonRequests = jsonRequests;


        }
    }


}
