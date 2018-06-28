package com.dsouchon.wayo.visualfusion;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;

public class TssSetAppointment extends AppCompatActivity {

    private DBManager dbManager;
    private TextView tvDisplayTime;
    private TimePicker timePicker1;
    private Button btnChangeTime;
    private TextView pDisplayDate;
    private Button pPickDate;
    private int pYear;
    private int pMonth;
    private int pDay;



    private int hour;
    private int minute;



    static final int TIME_DIALOG_ID = 999;
    static final int DATE_DIALOG_ID = 0;


    /** Callback received when the user "picks" a date in the dialog */
    private DatePickerDialog.OnDateSetListener pDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                    pYear = year;
                    pMonth = monthOfYear;
                    pDay = dayOfMonth;
                    updateDisplay();
                    displayToast();
                }
            };

    /** Updates the date in the TextView */
    private void updateDisplay() {

        String month = "0";
        String day = "0";
        if(pMonth+1<10){ month = "0" + (pMonth+1);} else {month = "" + (pMonth+1);}
        if(pDay<10){ day = "0" + pDay;}else {day = "" + pDay;}

        pDisplayDate.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                        // Month is 0 based so add 1
                        .append(pYear).append("-")
                        .append(month).append("-")
                        .append(day).append(" ")
        );
    }

    /** Displays a notification when the date is updated */
    private void displayToast() {
        Toast.makeText(this, new StringBuilder().append("Date chosen is ").append(pDisplayDate.getText()),  Toast.LENGTH_LONG).show();

    }
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
    public void onBackPressed() {     }      @Override  protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);  dbManager = new DBManager(this);        dbManager.open();
        setContentView(R.layout.tss_set_appointment_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Intent me = getIntent();

        TextView ID =  (TextView)findViewById(R.id.txtID);
        Integer id = me.getIntExtra("ID", -1);
        ID.setText(id.toString());

        TextView StoreName =  (TextView)findViewById(R.id.txtStoreName);
        StoreName.setText(me.getStringExtra("StoreNameURN"));
        //TextView URN =  (TextView)findViewById(R.id.txtURN);
        //URN.setText(me.getStringExtra("URN"));
        //TextView CurrentPhase =  (TextView)findViewById(R.id.txtProgress);
        //CurrentPhase.setText(me.getStringExtra("CurrentPhase"));
        TextView ContactPerson =  (TextView)findViewById(R.id.txtContactPerson);
        ContactPerson.setText(me.getStringExtra("ContactPerson"));

        TextView CurrentPhase =  (TextView)findViewById(R.id.txtCurrentPhase);
        CurrentPhase.setText(me.getStringExtra("CurrentPhase"));


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


        String appointmentDateTime = me.getStringExtra("AppointmentDateTime");

        appointmentDateTime = appointmentDateTime.replace('T',' ');

        if(!appointmentDateTime.isEmpty()&& !appointmentDateTime.equals("null")) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar calendar = Calendar.getInstance();

            try {
                calendar.setTime(simpleDateFormat.parse(appointmentDateTime));


                pDay = calendar.get(Calendar.DAY_OF_MONTH);

                pMonth = calendar.get(Calendar.MONTH);

                pYear = calendar.get(Calendar.YEAR);

                hour = calendar.get(Calendar.HOUR_OF_DAY);

                minute = calendar.get(Calendar.MINUTE);

                int secondIn = calendar.get(Calendar.SECOND);



            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        /** Get the current date */
        final Calendar cal = Calendar.getInstance();
        if(me.getStringExtra("AppointmentDateTime").isEmpty()|| me.getStringExtra("AppointmentDateTime").equals("null")) {
            pYear = cal.get(Calendar.YEAR);
            pMonth = cal.get(Calendar.MONTH);
            pDay = cal.get(Calendar.DAY_OF_MONTH);
            hour = 0;//cal.get(Calendar.HOUR);
            minute = 0;//cal.get(Calendar.MINUTE);
        }
        setCurrentTimeOnView(hour, minute);

        addListenerOnButton();


        /** Capture our View elements */
        pDisplayDate = (TextView) findViewById(R.id.displayDate);
        pPickDate = (Button) findViewById(R.id.pickDate);

        /** Listener for click event of the button */
        pPickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });




        if(!me.getStringExtra("AppointmentDateTime").isEmpty()|| !me.getStringExtra("AppointmentDateTime").equals("null")) {
            /** Display the current date in the TextView */
            updateDisplay();
        }
    }

    public void HowDatesWork()
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(simpleDateFormat.parse("2014/11/20 8:10:00 AM"));

            Log.e("date", "" + calendar.get(Calendar.DAY_OF_MONTH));

            Log.e("month", ""+calendar.get(Calendar.MONTH));

            Log.e("year", ""+calendar.get(Calendar.YEAR));

            Log.e("hour", ""+calendar.get(Calendar.HOUR));

            Log.e("minutes", ""+calendar.get(Calendar.MINUTE));

            Log.e("seconds", ""+calendar.get(Calendar.SECOND));
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    /** Create a new dialog for date picker */








    public void setCurrentTimeOnView(int hourIn, int minuteIn) {

        tvDisplayTime = (TextView) findViewById(R.id.tvTime);
        timePicker1 = (TimePicker) findViewById(R.id.timePicker1);



        final Calendar c = Calendar.getInstance();

        hour = hourIn;
        minute = minuteIn;


        // set current time into textview
        tvDisplayTime.setText(
                new StringBuilder().append(pad(hour))
                        .append(":").append(pad(minute)));

        // set current time into timepicker
        timePicker1.setCurrentHour(hour);
        timePicker1.setCurrentMinute(minute);


    }

    public void addListenerOnButton() {

        btnChangeTime = (Button) findViewById(R.id.btnChangeTime);

        btnChangeTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                showDialog(TIME_DIALOG_ID);

            }

        });

    }

    // Pop up dialogs for date and time picker

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case TIME_DIALOG_ID:
                // set time picker as current time
                return new TimePickerDialog(this,
                        timePickerListener, hour, minute,false);


            case DATE_DIALOG_ID:
                return new DatePickerDialog(this,
                        pDateSetListener,
                        pYear, pMonth, pDay);



        }
        return null;
    }


    private TimePickerDialog.OnTimeSetListener timePickerListener =
            new TimePickerDialog.OnTimeSetListener()

            {
                public void onTimeSet(TimePicker view, int selectedHour,
                                      int selectedMinute)

                {


                    hour = selectedHour;
                    minute = selectedMinute;

                    // set current time into textview
                    tvDisplayTime.setText(new StringBuilder().append(pad(hour))
                            .append(":").append(pad(minute)));

                    // set current time into timepicker
                    timePicker1.setCurrentHour(hour);
                    timePicker1.setCurrentMinute(minute);




                }
            };



    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }


    public AndroidAppointment findAppointmentByID(int id, ArrayList<AndroidAppointment> requests){
        for (AndroidAppointment request : requests) {
            if (request.getID() == id) {
                return request;
            }
        }
        return null;
    }

    public void ConfirmAppointment(View view) throws JSONException {

        TextView txtID =  (TextView)findViewById(R.id.txtID);
        Integer ID = Integer.parseInt(txtID.getText().toString());

        //Save Appointment changes
        AndroidAppointment s = new AndroidAppointment();

        TextView storeName = (TextView) findViewById(R.id.txtStoreName);
        s.StoreName = storeName.getText().toString();


        TextView ContactPerson = (TextView) findViewById(R.id.txtContactPerson);
        s.ContactPerson = ContactPerson.getText().toString();
        TextView ContactEmail = (TextView) findViewById(R.id.txtContactEmail);
        s.ContactEmail = ContactEmail.getText().toString();
        TextView ContactPhone = (TextView) findViewById(R.id.txtContactPhone);
        s.ContactPhone = ContactPhone.getText().toString();




        TextView appointmentDate = (TextView) findViewById(R.id.displayDate);
        s.setAppointmentDateTime( appointmentDate.getText().toString());

        TextView appointmentTime = (TextView) findViewById(R.id.tvTime);
        s.setAppointmentDateTimeTime( appointmentTime.getText().toString());



        s.setUserName(dbManager.getValue( "UserName"));

        SaveAppointmetToArray(s, ID);

        SaveIfOnline();

                // startActivity(new Intent(this,MainLogin.class));
        Intent intent = new Intent(TssSetAppointment.this, TssHome.class );

        startActivity(intent); finish();

    }


    public void  SaveAppointmetToArray(AndroidAppointment appointment, Integer ID) throws JSONException {

        try {

            String storesString = dbManager.getValue( "AndroidTssAppointments");

            ArrayList<AndroidAppointment> appointments = JsonUtil.parseJsonArrayAndroidAppointment(storesString);

            //Convert request to appointment
            AndroidAppointment findappointment = findAppointmentByID(ID, appointments);

            appointment.setRequestTypeName( findappointment.getRequestTypeName());
            appointment.setID( findappointment.getID());
            appointment.setStoreID( findappointment.getStoreID());
            appointment.setDateRequested( findappointment.getDateRequested());
            appointment.setDateAccepted( findappointment.getDateAccepted());
            appointment.setStoreID( findappointment.getStoreID());

            appointments.remove(findappointment);

            Date recordChangedNow = new Date();

            appointment.setDateRecordChanged((String) android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss a", recordChangedNow));

            appointments.add(appointment);

            //Save Requests back to local
            dbManager.setValue( "AndroidTssAppointments", JsonUtil.convertAppointmentArrayToJson(appointments));


        } catch (Exception ex) {
            //
            //
        }
        //

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

            Intent intent = new Intent(TssSetAppointment.this, HomeMenu.class );

            startActivity(intent); finish();

            // return true;
        }



        if (id == R.id.logoutbutton){
            startActivity(new Intent(this,LoginActivity.class));
        }

        if (id == R.id.backbutton){
            startActivity(new Intent(this,TssAppointments.class));
        }

        return super.onOptionsItemSelected(item);
    }


    public void SaveIfOnline()
    {

        String isWebsiteAvailable = dbManager.getValue( "AmIOnline");

        if(isWebsiteAvailable.equals("True")) {
            //Save My Appointments - this will also 'delete' the request online
            MySOAPCallActivity cs1 = new MySOAPCallActivity();
            try {


                String myappointments = dbManager.getValue( "AndroidTssAppointments");
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


                dbManager.setValue( "AndroidTssAppointments", "");


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

