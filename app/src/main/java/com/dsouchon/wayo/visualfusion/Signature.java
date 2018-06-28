package com.dsouchon.wayo.visualfusion;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import android.view.Menu;
import android.view.MenuItem;

import android.widget.Toast;

import com.kosalgeek.android.photoutil.ImageBase64;


import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Signature extends AppCompatActivity {
    private DBManager dbManager;
    private SignatureView signatureView;
    private int mSurveyID;
    private int RequestID;
    private String mURN;
    private String mSignaturePurpose;
    String selectedPhoto;
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
        setContentView(R.layout.activity_signature);//see xml layout
        signatureView = (SignatureView) findViewById(R.id.signature_view);


        mSignaturePurpose = dbManager.getValue( "SignaturePurpose");

        if(mSignaturePurpose.equals("PerformanceReview"))
        {
                mURN = "PerformanceReview";
        }
        else {

            mSurveyID = getIntent().getIntExtra("SurveyID", 0);
            RequestID = getIntent().getIntExtra("RequestID", 0);
            mURN = getIntent().getStringExtra("URN");

        }


        File directory = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File file = new File(directory, JsonUtil.flattenURN(mURN)+".jpg");

        signatureView.setCanvasFromFile(file);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_signature, menu);
        return true;
    }

    public void test (String encodedImage) throws JSONException {





                if(TextUtils.isEmpty(encodedImage))
                {
                    Toast.makeText(getApplicationContext(),
                            "Please sign before attempting upload or click green tick to Skip.", Toast.LENGTH_LONG).show();
                }
                if(TextUtils.equals(encodedImage, "Skip"))
                {
                    WhenUploadIsComplete("");

                }
                else {

                    try {


                        Integer StoreID = 0;
                        Integer StoreItemID = 0;
                        String UserName = dbManager.getValue( "UserName");
                        String ImageType = "";
                        String Barcode = "";
                        String ProductComplaint = "";

                        if(mSignaturePurpose.equals("PerformanceReview")) {
                            ImageType = "PerformanceScoreCardSignature";
                            StoreID = Integer.parseInt(dbManager.getValue( "StoreID"));
                            StoreItemID = Integer.parseInt(dbManager.getValue( "RequestID"));;
                        }
                        else {
                            StoreID = getIntent().getIntExtra("StoreID", 0);
                            StoreItemID = mSurveyID;
                            ImageType = "SurveySignature";
                        }


                                                 //on ws if barcode is blank and its a store item image then its not a barcode image
                        UploadStoreItemImage(StoreItemID, StoreID, encodedImage, UserName, ImageType, ProductComplaint, Barcode);

                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(),
                                "Something Wrong while encoding photos", Toast.LENGTH_LONG).show();
                    }
                }



    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.action_info:
//                InfoDialog();
//                return true;
            case R.id.action_clear:
                signatureView.clearCanvas();//Clear SignatureView
                Toast.makeText(getApplicationContext(),
                        "Clear signature",Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_skip:
                Toast.makeText(getApplicationContext(),
                        "Skip signature",Toast.LENGTH_LONG).show();
                try {
                    test("Skip");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.action_download:
                File directory = Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File file = new File(directory, JsonUtil.flattenURN(mURN)+".jpg");

                FileOutputStream out = null;
                Bitmap bitmap = signatureView.getSignatureBitmap();
                try {
                    out = new FileOutputStream(file);
                    if(bitmap!=null){
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

                        String encodedImage = ImageBase64.encode(bitmap);
                        test(encodedImage);
                    }else{
                        throw new FileNotFoundException();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                } finally {
                    try {
                        if (out != null) {
                            out.flush();
                            out.close();

                            if(bitmap!=null){
                                //Toast.makeText(getApplicationContext(),
                                //        "Image saved successfully at "+ file.getPath(),Toast.LENGTH_LONG).show();
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                                    new MyMediaScanner(this, file);
                                } else {
                                    ArrayList<String> toBeScanned = new ArrayList<String>();
                                    toBeScanned.add(file.getAbsolutePath());
                                    String[] toBeScannedStr = new String[toBeScanned.size()];
                                    toBeScannedStr = toBeScanned.toArray(toBeScannedStr);
                                    MediaScannerConnection.scanFile(this, toBeScannedStr, null, null);
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class MyMediaScanner implements
            MediaScannerConnection.MediaScannerConnectionClient {

        private MediaScannerConnection mSC;
        private File file;

        public MyMediaScanner(Context context, File file) {
            this.file = file;
            mSC = new MediaScannerConnection(context, this);
            mSC.connect();
        }

        @Override
        public void onMediaScannerConnected() {
            mSC.scanFile(file.getAbsolutePath(), null);
        }

        @Override
        public void onScanCompleted(String path, Uri uri) {
            mSC.disconnect();
        }
    }

    public void InfoDialog(){
        String infoMessage = "";
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager()
                    .getPackageInfo(getPackageName(), 0);
            if(pInfo!=null){
                infoMessage = "App Version : "+pInfo.versionName;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String svInfo = signatureView.getVersionInfo();
        if(svInfo!=null){
            infoMessage = infoMessage +"\n\n"+svInfo;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.vInfo)
                .setMessage(infoMessage)
                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        Dialog dialog = builder.create();
        dialog.show();
    }

    public void UploadStoreItemImage(Integer StoreItemID,
                                     Integer StoreID,
                                     String Base64Image,
                                     String UserName,
                                     String Type,
                                     String ProductComplaint,
                                     String Barcode
    ) {

        final android.app.AlertDialog ad=new android.app.AlertDialog.Builder(this).create();
        MySOAPCallActivity cs = new MySOAPCallActivity();
        try {

            UploadStoreItemImageParams params = new UploadStoreItemImageParams(cs, StoreItemID, StoreID,Base64Image,UserName,Type, ProductComplaint, Barcode);

            new CallSoapUploadStoreItemImage().execute(params);

            progressDialog = new ProgressDialog(Signature.this);
            progressDialog.setMessage("Saving Signature ..."); // Setting Message
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

    public class CallSoapUploadStoreItemImage extends AsyncTask<UploadStoreItemImageParams, Void, String> {

        private Exception exception;

        @Override
        protected String doInBackground(UploadStoreItemImageParams... params) {
            return params[0].foo.UploadStoreItemImage(params[0].StoreItemID, params[0].StoreID,params[0].Base64Image,params[0].UserName,params[0].Type, params[0].ProductComplaint, params[0].Barcode );
        }

        protected void onPostExecute(String result) {
            // TODO: check this.exception
            // TODO: do something with the feed
            try {
                //process Json Result
                //Save results in local storage
                if(result.toLowerCase().contains("error")){

                }
                else {
                    result = result.replace("\n", "").replace("\r", "");
                    if(mSignaturePurpose.equals("PerformanceReview"))
                    {
                        WhenUploadIsComplete("PerformanceReview");//TODO:
                    }
                    else {
                        WhenUploadIsComplete("SurveySignature");
                    }
                }

            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
            }
            progressDialog.dismiss();
        }



    }
    private static class UploadStoreItemImageParams {
        MySOAPCallActivity foo;
        Integer StoreItemID;
        Integer StoreID;
        String Base64Image;
        String UserName;
        String Type;
        String ProductComplaint;
        String Barcode;

        UploadStoreItemImageParams(MySOAPCallActivity foo, Integer StoreItemID,
                                   Integer StoreID,
                                   String Base64Image,
                                   String UserName,
                                   String Type,
                                   String ProductComplaint,
                                   String Barcode
        ) {
            this.foo = foo;
            this.StoreItemID = StoreItemID;
            this.StoreID = StoreID;
            this.Base64Image = Base64Image;
            this.UserName = UserName;
            this.Type = Type;
            this.ProductComplaint = ProductComplaint;
            this.Barcode = Barcode;

        }
    }


    public void WhenUploadIsComplete (String ImageType)throws JSONException {
        Intent me = getIntent();

        RequestID = Integer.parseInt(dbManager.getValue( "RequestID"));


                Intent intent = new Intent(Signature.this, InstallSetAppointment.class);

                if (ImageType.equals("SurveySignature")) {

                    intent = new Intent(Signature.this, ThankYouSurvey.class);
                    intent.putExtra("Toast", "Thank the outlet owner for their time and state that one member of the Project management team or his local representative from EABL will be in contact shortly to coordinate the installation process and date.");
                }

                String storesString = dbManager.getValue( "AndroidInsAppointments");

                ArrayList<AndroidAppointment> appointments = JsonUtil.parseJsonArrayAndroidAppointment(storesString);

                //Convert request to appointment
                AndroidAppointment appointment = findAppointmentByID(RequestID, appointments);
                intent.putExtra("RequestID", RequestID);
                intent.putExtra("ID", appointment.getID());
                intent.putExtra("StoreID", appointment.getStoreID());
                intent.putExtra("RequestTypeName", appointment.getRequestTypeName());
                intent.putExtra("BrandName", appointment.getBrandName());
                intent.putExtra("OutletTypeName", appointment.getOutletTypeName());
                intent.putExtra("TierTypeName", appointment.getTierTypeName());

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


        startActivity(intent); finish();
            }


    public void WhenUploadOfPerformanceReviewSignatureIsComplete()
    {




    }

    public AndroidAppointment findAppointmentByID(int id, ArrayList<AndroidAppointment> requests){
        for (AndroidAppointment request : requests) {
            if (request.getID() == id) {
                return request;
            }
        }
        return null;
    }

}

