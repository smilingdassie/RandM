package com.dsouchon.wayo.visualfusion;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;

public class OtherUploads extends AppCompatActivity {
    public boolean mIsSurvey = false;
    public  Integer mDocCount =0;
int RequestID;
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
        setContentView(R.layout.activity_other_uploads);

        String primaryRole = dbManager.getValue( "PrimaryRole");
        if (primaryRole.equals("Srv")){
            mIsSurvey = true;
        }
        Intent me = getIntent();
        if(mIsSurvey){
            //getActionBar().setTitle("Survey Unitary List");
            //getSupportActionBar().setTitle("Survey Unitry List");  //
            Button btnExteriorImage = (Button) findViewById(R.id.btnExteriorImage);
            btnExteriorImage.setVisibility(View.VISIBLE);
            Button btnInteriorImage = (Button) findViewById(R.id.btnInteriorImage);
            btnInteriorImage.setVisibility(View.VISIBLE);



            TextView ExteriorImageCount =  (TextView)findViewById(R.id.txtExteriorImageCount);

            ExteriorImageCount.setText(String.format("Exterior images uploaded: %d", me.getIntExtra("ExteriorImageCount",0)));
            TextView InteriorImageCount =  (TextView)findViewById(R.id.txtInteriorImageCount);
            InteriorImageCount.setText(String.format("Interior images uploaded: %d", me.getIntExtra("InteriorImageCount",0)));
            ExteriorImageCount.setVisibility(View.VISIBLE);
            InteriorImageCount.setVisibility(View.VISIBLE);
         //   TextView SurveySignatureCount =  (TextView)findViewById(R.id.txtSurveySignatureCount);
          //  SurveySignatureCount.setText(me.getStringExtra("SurveySignatureCount"));
          //  TextView PerformanceReviewSignatureCount =  (TextView)findViewById(R.id.txtPerformanceReviewSignatureCount);
          //  PerformanceReviewSignatureCount.setText(me.getStringExtra("PerformanceReviewSignatureCount"));

        }
        else{

            TextView JobCardCount =  (TextView)findViewById(R.id.txtJobCardCount);
            JobCardCount.setText(String.format("JobCard pages uploaded: %d",me.getIntExtra("JobCardCount",0)));
            JobCardCount.setVisibility(View.VISIBLE);

            Button btnUploadJobCard = (Button) findViewById(R.id.btnUploadJobCard);
            btnUploadJobCard.setVisibility(View.VISIBLE);
            Button btnProductComplaint = (Button) findViewById(R.id.btnProductComplaint);
            btnProductComplaint.setVisibility(View.VISIBLE);
            int storeID = getIntent().getIntExtra("StoreID", 0);
            String strDocCount = "0";
            ArrayList<AndroidDocumentType> docs = getDocs();
            AndroidDocumentType docCount = findDocumentCountByStoreIDAndType(storeID, "JobCardIns", docs);
            if(docCount != null) {
                mDocCount = docCount.getCount();
            }
            else
            {
                mDocCount = 0;
            }
            strDocCount = mDocCount.toString();
            btnUploadJobCard.setText(btnUploadJobCard.getText().toString() + " (" + strDocCount + " pages)" );

        }

        mDocCount = 0;//TODO
        RequestID = me.getIntExtra("RequestID",0);
    }
    public AndroidDocumentType findDocumentCountByStoreIDAndType(int storeid, String doctype, ArrayList<AndroidDocumentType> jobcards){
        for (AndroidDocumentType jobcard : jobcards) {
            if (jobcard.getStoreID() == storeid && jobcard.getDocumentType().equals(doctype)) {
                return jobcard;
            }
        }
        return null;
    }

    private ArrayList<AndroidDocumentType> getDocs() {
        ArrayList<AndroidDocumentType> androidDocumentTypes = new ArrayList<>();
        String json = "[]";
        json = dbManager.getValue( "AndroidDocumentTypes");
        try {
            androidDocumentTypes = JsonUtil.parseJsonArrayAndroidDocumentTypes(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return androidDocumentTypes;
    }


    public void Back(View view) throws JSONException {

        WhenUploadIsComplete ();

    }


    public void UploadJobCard(View view)
    {

        Intent intent = new Intent(OtherUploads.this, UploadPhoto.class );
        //Sending data to another Activity

        Intent me = getIntent();



        intent.putExtra("StoreName", me.getStringExtra("StoreName"));
        intent.putExtra("StoreNameURN", me.getStringExtra("StoreNameURN"));
        intent.putExtra("StoreID", me.getIntExtra("StoreID", 0));
        intent.putExtra("DocCount", mDocCount);
        intent.putExtra("StoreItemID", 0);
        intent.putExtra("ImageType", "JobCardIns");
        intent.putExtra("RequestID",RequestID);
        startActivity(intent); finish();

    }



    public void SurveyStoreExterior(View view)
    {

        Intent intent = new Intent(OtherUploads.this, UploadPhoto.class );
        //augment upload photo to include text box for complaint

        //Sending data to another Activity

        Intent me = getIntent();

        //String storeNameURN = me.getStringExtra("StoreName") + " " + me.getStringExtra("URN");

        intent.putExtra("StoreName", me.getStringExtra("StoreName"));
        intent.putExtra("StoreNameURN", me.getStringExtra("StoreNameURN"));
        intent.putExtra("StoreID", me.getIntExtra("StoreID", 0));
        intent.putExtra("StoreItemID", 0);
        intent.putExtra("ImageType", "SurveyStoreExterior");
        intent.putExtra("RequestID", RequestID);
        startActivity(intent); finish();

    }

    public void WhenUploadIsComplete ()throws JSONException {

                Intent intent = new Intent(OtherUploads.this, InstallSetAppointment.class);


                String storesString = dbManager.getValue( "AndroidInsAppointments");

                ArrayList<AndroidAppointment> appointments = JsonUtil.parseJsonArrayAndroidAppointment(storesString);

                RequestID = Integer.parseInt(dbManager.getValue( "RequestID"));

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

                startActivity(intent); finish();



    }


    public AndroidAppointment findAppointmentByID(int id, ArrayList<AndroidAppointment> requests){
        for (AndroidAppointment request : requests) {
            if (request.getID() == id) {
                return request;
            }
        }
        return null;
    }
    public void SurveyStoreInterior(View view)
    {

        Intent intent = new Intent(OtherUploads.this, UploadPhoto.class );
        //augment upload photo to include text box for complaint

        //Sending data to another Activity

        Intent me = getIntent();

        //String storeNameURN = me.getStringExtra("StoreName") + " " + me.getStringExtra("URN");

        intent.putExtra("StoreName", me.getStringExtra("StoreName"));
        intent.putExtra("StoreNameURN", me.getStringExtra("StoreNameURN"));
        intent.putExtra("StoreID", me.getIntExtra("StoreID", 0));
        intent.putExtra("StoreItemID", 0);
        intent.putExtra("ImageType", "SurveyStoreInterior");
        intent.putExtra("RequestID", RequestID);
        startActivity(intent); finish();

    }



    public void GeneralComplaint(View view)
    {

        Intent intent = new Intent(OtherUploads.this, UploadPhoto.class );
        //augment upload photo to include text box for complaint

        //Sending data to another Activity

        Intent me = getIntent();

        //String storeNameURN = me.getStringExtra("StoreName") + " " + me.getStringExtra("URN");

        intent.putExtra("StoreName", me.getStringExtra("StoreName"));
        intent.putExtra("StoreNameURN", me.getStringExtra("StoreNameURN"));
        intent.putExtra("StoreID", me.getIntExtra("StoreID", 0));
        intent.putExtra("StoreItemID", 0);
        intent.putExtra("ImageType", "ProductComplaint");
        intent.putExtra("RequestID", RequestID);
        startActivity(intent); finish();

    }




}
