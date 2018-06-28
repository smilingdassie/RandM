package com.dsouchon.wayo.visualfusion;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageBase64;
import com.kosalgeek.android.photoutil.ImageLoader;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.regex.*;

import static android.view.View.GONE;
import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;

public class UploadPhoto extends AppCompatActivity {
    private DBManager dbManager;
    private final String TAG = this.getClass().getName();
    public Integer mDocCount;
    public Integer mJobCardPage=1;
    public Integer mStoreID;
    public Integer RequestID;
    public String mStoreName;
    public String mStoreNameURN;
    public String mURN;
    public String mNewBarcode;
    public boolean mItsABarcodeImage;
    public int mAcceptedByStore;
    public String mJobCardDocumentType;

    public Uri cameraPhotoFile;
    Button ivCamera, ivGallery, ivUpload;
    ImageView ivImage;


    CameraPhoto cameraPhoto;
    GalleryPhoto galleryPhoto;
    ProgressDialog progressDialog;
    final int CAMERA_REQUEST = 13323;
    final int GALLERY_REQUEST = 22131;

    public int mQuantitySelected;
    String selectedPhoto;
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }



    private void checkReadPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
        }
    }

    private void checkWritePermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA}, 2);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        BitmapDrawable drawable = (BitmapDrawable) ivImage.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        outState.putParcelable("image", bitmap);



        super.onSaveInstanceState(outState);
    }

protected boolean IsBitMapEmpty(Bitmap myBitmap){

    Bitmap emptyBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), myBitmap.getConfig());
    if (myBitmap.sameAs(emptyBitmap)) {
        // myBitmap is empty/blank
        return true;
    }

      return false;
}

    @Override
    public void onBackPressed() {}

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



        setContentView(R.layout.activity_upload_photo);

        mJobCardDocumentType = "JobCardInsPage1";

        Intent me = getIntent();

        RadioGroup radioGroup1 = (RadioGroup)findViewById(R.id.radioJobCardType);
        radioGroup1.setVisibility(View.GONE);

        TextView txtJobCardPageNo = (TextView)findViewById(R.id.txtJobCardPageNo);
        txtJobCardPageNo.setVisibility(View.GONE);
        mStoreNameURN = me.getStringExtra("StoreNameURN");
        mNewBarcode = me.getStringExtra("Barcode");
        /*   show and hide stuff
            --hide product text on storeitem and jobcard photo
            --hide barcode on product complaint and jobcard photo
            --hide reason on site complete checkout
        */
        EditText  editProductComplaint = (EditText) findViewById(R.id.editProductComplaint);
        EditText  editBarcode = (EditText) findViewById(R.id.editBarcode);
        String ImageType = getIntent().getStringExtra("ImageType");
        Integer ID = getIntent().getIntExtra("ID", 0);
        if(ID > 0) RequestID = ID;
        RequestID = getIntent().getIntExtra("RequestID", 0);
        mStoreID = getIntent().getIntExtra("StoreID", 0);



        Integer Enumerate = getIntent().getIntExtra("Enumerate", 0);

        if(ImageType.equals("BarcodeOnUnit")) {
            editBarcode = (EditText) findViewById(R.id.editBarcode);
            editBarcode.setVisibility(View.VISIBLE);
            editBarcode.setText(getIntent().getStringExtra("Barcode"));
            //indicate that this is a barcode image as well
            mItsABarcodeImage = true;
            editProductComplaint.setVisibility(GONE);
        }
        if(ImageType.equals("StoreItemInStore")) {

            editProductComplaint.setVisibility(GONE);
            editBarcode.setVisibility(View.GONE);
            mItsABarcodeImage = false;


        }

        if(ImageType.equals("SurveyStoreItemLocation")) {

            editProductComplaint.setVisibility(GONE);
            editBarcode.setVisibility(View.GONE);
            mItsABarcodeImage = false;
            mAcceptedByStore = getIntent().getIntExtra("AcceptedByStore", -1);
        }
        if(ImageType.equals("JobCardIns")||ImageType.equals("SurveyStoreInterior")||ImageType.equals("SurveyStoreExterior")) {

            RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radioJobCardType);
            radioGroup.setVisibility(View.VISIBLE);
            editProductComplaint.setVisibility(GONE);
            editBarcode.setVisibility(View.GONE);

            mDocCount = getIntent().getIntExtra("DocCount", 0);
        }
        if(ImageType.equals("ProductComplaint") || ImageType.equals("UnitryComplaint")) {
            editProductComplaint.setVisibility(View.VISIBLE);
            editBarcode.setVisibility(View.GONE);
        }

        if(ImageType.equals("JobCardIns")) {
           //TODO: Make radio buttons visible

            radioGroup1.setVisibility(View.VISIBLE);
            txtJobCardPageNo.setVisibility(View.VISIBLE);
        }
        else {
            radioGroup1.setVisibility(View.GONE);
            txtJobCardPageNo.setVisibility(View.GONE);
        }

        checkCameraPermission();
        checkWritePermission();

        cameraPhoto = new CameraPhoto(getApplicationContext());
        galleryPhoto = new GalleryPhoto(getApplicationContext());

        ivImage = (ImageView)findViewById(R.id.ivImage);

        ivCamera = (Button)findViewById(R.id.ivCamera);
        ivGallery = (Button)findViewById(R.id.ivGallery);
        ivUpload = (Button)findViewById(R.id.ivUpload);

        //Get your bitmap back from saved instance because someone changed phone orientation
        if(savedInstanceState != null) {
            Bitmap bitmap = savedInstanceState.getParcelable("image");
            if(!IsBitMapEmpty(bitmap)) {
                ivImage.setImageBitmap(bitmap);
            }


        }


        ivCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dispatchTakePictureIntent();




                    //startActivityForResult(cameraPhoto.takePhotoIntent(), CAMERA_REQUEST);
                    //cameraPhoto.addToGallery();

               /*     Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraPhotoFile = Uri.fromFile(getOutputMediaFile());

                dbManager.setValue( "cameraPhotoFile", cameraPhotoFile.toString());

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPhotoFile);

                    startActivityForResult(intent, 100);*/



            }
        });

        ivGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(galleryPhoto.openGalleryIntent(), GALLERY_REQUEST);
            }
        });

        ivUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPhoto = dbManager.getValue( "SelectedPhoto");



                if(TextUtils.isEmpty(selectedPhoto))
                {
                    Toast.makeText(getApplicationContext(),
                            "Please select a photo before attempting upload.", Toast.LENGTH_LONG).show();
                }
                else {

                    try {
                        Bitmap bitmap = ImageLoader.init().from(selectedPhoto).requestSize(300, 300).getBitmap();//1024,1024).getBitmap();
                        String encodedImage = ImageBase64.encode(bitmap);
                        Log.d(TAG, encodedImage);

                        Integer StoreID = getIntent().getIntExtra("StoreID", 0);
                        Integer StoreItemID = getIntent().getIntExtra("StoreItemID", 0);
                        String UserName = dbManager.getValue( "UserName");
                        String ImageType = getIntent().getStringExtra("ImageType");

                        if(ImageType.equals("JobCardIns"))
                        {
                            ImageType = mJobCardDocumentType;

                        }

                        EditText editProductComplaint = (EditText) findViewById(R.id.editProductComplaint);
                        String ProductComplaint = editProductComplaint.getText().toString();

                        String Barcode = "";
                        if(mItsABarcodeImage) {
                            EditText editBarcode = (EditText) findViewById(R.id.editBarcode);
                            Barcode = editBarcode.getText().toString();
                            mNewBarcode = Barcode;
                        }

                        if(mItsABarcodeImage && !validateBarcode(Barcode)) {
                            Toast.makeText(getApplicationContext(),
                                    "Barcode invalid. Must be in format A1234567B. Please check.", Toast.LENGTH_LONG).show();
                        }
                        else {

                            //on ws if barcode is blank and its a store item image then its not a barcode image
                            UploadStoreItemImage(StoreItemID, StoreID, encodedImage, UserName, ImageType, ProductComplaint, Barcode);
                        }

                    } catch (FileNotFoundException e) {
                        Toast.makeText(getApplicationContext(),
                                "Something Wrong while encoding photos", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio1: if (checked) mJobCardDocumentType = "JobCardInsPage1";
                mJobCardPage = 1;
                break;
            case R.id.radio2: if (checked) mJobCardDocumentType = "JobCardInsPage2"; mJobCardPage = 2;break;
            case R.id.radio3: if (checked) mJobCardDocumentType = "JobCardInsPage3"; mJobCardPage = 3;break;
            case R.id.radio4: if (checked) mJobCardDocumentType = "JobCardInsPage4"; mJobCardPage = 4;break;
            case R.id.radio5: if (checked) mJobCardDocumentType = "JobCardInsPage5"; mJobCardPage = 5;break;

            default: mJobCardDocumentType  = "JobCardInsPage1";  mJobCardPage = 1;
                break;
        }
    }


    public void ShowBarcode(View view) {
        //Show the barcode box
        EditText editBarcode = (EditText)findViewById(R.id.editBarcode);
        editBarcode.setVisibility(View.VISIBLE);
        editBarcode.setText(getIntent().getStringExtra("Barcode"));
        //indicate that this is a barcode image as well
        mItsABarcodeImage = true;
    }

    public Boolean validateBarcode (String barcode)
    {
        String txt=barcode;//"T1234567C";

        String re1="(.)";	// Any Single Character 1
        String re2="(\\d)";	// Any Single Digit 1
        String re3="(\\d)";	// Any Single Digit 2
        String re4="(\\d)";	// Any Single Digit 3
        String re5="(\\d)";	// Any Single Digit 4
        String re6="(\\d)";	// Any Single Digit 5
        String re7="(\\d)";	// Any Single Digit 6
        String re8="(\\d)";	// Any Single Digit 7
        String re9="(.)";	// Any Single Character 2

        Pattern p = Pattern.compile(re1+re2+re3+re4+re5+re6+re7+re8+re9,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(txt);
        if (m.find())
        {
          /*  String c1=m.group(1);
            String d1=m.group(2);
            String d2=m.group(3);
            String d3=m.group(4);
            String d4=m.group(5);
            String d5=m.group(6);
            String d6=m.group(7);
            String d7=m.group(8);
            String c2=m.group(9);*/
            return true;
            //System.out.print("("+c1.toString()+")"+"("+d1.toString()+")"+"("+d2.toString()+")"+"("+d3.toString()+")"+"("+d4.toString()+")"+"("+d5.toString()+")"+"("+d6.toString()+")"+"("+d7.toString()+")"+"("+c2.toString()+")"+"\n");
        }
        return false;
    }

    public Boolean validateBarcodePhase1 (String barcode)
    {
        String txt=barcode;//"T 1234567";
        Boolean result = false;
        String re1="(.)";	// Any Single Character 1
        String re2="(\\s)";	// Space
        String re3="(\\d)";	// Any Single Digit 2
        String re4="(\\d)";	// Any Single Digit 3
        String re5="(\\d)";	// Any Single Digit 4
        String re6="(\\d)";	// Any Single Digit 5
        String re7="(\\d)";	// Any Single Digit 6
        String re8="(\\d)";	// Any Single Digit 7
        String re9="(\\d)";	// Any Single Digit 7

        Pattern p = Pattern.compile(re1+re2+re3+re4+re5+re6+re7+re8+re9,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(txt);
        if (m.find())
        {
          /*  String c1=m.group(1);
            String d1=m.group(2);
            String d2=m.group(3);
            String d3=m.group(4);
            String d4=m.group(5);
            String d5=m.group(6);
            String d6=m.group(7);
            String d7=m.group(8);
            String c2=m.group(9);*/
            result =  true;
            //System.out.print("("+c1.toString()+")"+"("+d1.toString()+")"+"("+d2.toString()+")"+"("+d3.toString()+")"+"("+d4.toString()+")"+"("+d5.toString()+")"+"("+d6.toString()+")"+"("+d7.toString()+")"+"("+c2.toString()+")"+"\n");
        }
        else
        {
            //Check if its an old barcode
            result = validateBarcodePhase1(barcode);


        }
        return result;
    }


    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }




    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Bitmap imageBitmapSmaller = getResizedBitmap(imageBitmap, 512);

            ImageView ivImage = (ImageView)findViewById(R.id.ivImage);
            ivImage.setImageBitmap(imageBitmapSmaller);

            Uri uri = null;// data.getData();

           if(uri == null) {
               // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
              uri = getImageUri(getApplicationContext(), imageBitmap);

               // CALL THIS METHOD TO GET THE ACTUAL PATH
               Toast.makeText(UploadPhoto.this, "Here " + getRealPathFromURI(uri), Toast.LENGTH_LONG).show();
           }

            if(uri !=null) {
                galleryPhoto.setPhotoUri(uri);
                String photoPath = galleryPhoto.getPath();
                selectedPhoto = photoPath;
                dbManager.setValue( "SelectedPhoto", selectedPhoto);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            else
            {
                Toast.makeText(getApplicationContext(),
                        "Something went wrong while taking a photo. Please try again.", Toast.LENGTH_LONG).show();
            }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),
                        "Something went wrong while choosing photos. Please try again.", Toast.LENGTH_LONG).show();
            }
        }

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){

                Uri uri = data.getData();

                galleryPhoto.setPhotoUri(uri);
                String photoPath = galleryPhoto.getPath();
                selectedPhoto = photoPath;
                dbManager.setValue( "SelectedPhoto", selectedPhoto);
                try {
                    Bitmap bitmap = ImageLoader.init().from(photoPath).requestSize(512, 512).getBitmap();
                    ivImage.setImageBitmap(bitmap);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            "Something went wrong while choosing photos. Please try again.", Toast.LENGTH_LONG).show();
                }
            }

    }

    //Daniel TODO: Go back to unit list
    public void WhenUploadIsComplete ()throws JSONException {
        Intent me = getIntent();


        String ImageType = getIntent().getStringExtra("ImageType");
        RequestID = Integer.parseInt(dbManager.getValue( "RequestID"));
        mQuantitySelected = me.getIntExtra("QuantitySelected",0);

        if(ImageType.equals("StoreItemInStore")) {

            AndroidStoreUnitExplicit s = new AndroidStoreUnitExplicit();

            s.setBarcode(mNewBarcode);
            Integer storeItemID = me.getIntExtra("StoreItemID",0);
            SaveStoreItemToArray(s, storeItemID);



            Intent intent = new Intent(UploadPhoto.this, UnitryUploadsOnly.class);// InstallUnitaryList.class);
            intent.putExtra("Toast", "Upload unit image successful.");
            intent.putExtra("StoreName", mStoreName);
            intent.putExtra("URN", mURN);
            intent.putExtra("StoreNameURN", mStoreNameURN);
            intent.putExtra("RequestID", RequestID);
            startActivity(intent); finish();
        }
        else {

            //SurveyStoreItemLocation
             //       SurveyStoreExterior
            //SurveyStoreInterior

            if(ImageType.equals("BarcodeOnUnit")||ImageType.equals("SurveyStoreItemLocation")) {

                AndroidStoreUnitExplicit s = new AndroidStoreUnitExplicit();

                s.setBarcode(mNewBarcode);
                s.setAcceptedByStore(me.getIntExtra("AcceptedByStore", mAcceptedByStore));
                Integer storeItemID = me.getIntExtra("StoreItemID",0);
                SaveStoreItemToArray(s, storeItemID);

                Intent intent = new Intent(UploadPhoto.this, UnitryUploadsOnly.class);// InstallUnitaryList.class);
                intent.putExtra("Toast", "Upload successful.");
                intent.putExtra("StoreName", mStoreName);
                intent.putExtra("URN", mURN);
                intent.putExtra("StoreNameURN", mStoreNameURN);



                intent.putExtra("RequestID", RequestID);
                startActivity(intent); finish();
            }
            else {

                if(ImageType.equals("SurveyStoreExterior")||ImageType.equals("SurveyStoreInterior"))
                {

                    String storesString = dbManager.getValue( "AndroidInsAppointments");

                    ArrayList<AndroidAppointment> appointments = JsonUtil.parseJsonArrayAndroidAppointment(storesString);
                    RequestID = Integer.parseInt(dbManager.getValue( "RequestID"));

                    //Convert request to appointment
                    AndroidAppointment appointment = findAppointmentByID(RequestID, appointments);

                     AndroidAppointment s = new AndroidAppointment();
                     if(ImageType.equals("SurveyStoreExterior")) {
                         s.setExteriorImageCount(appointment.getExteriorImageCount() + 1);
                         SaveAppointmentToArray(s,RequestID,0,appointment.getExteriorImageCount() + 1,0,0,0  );
                     }
                    if(ImageType.equals("SurveyStoreInterior")) {
                        s.setInteriorImageCount(appointment.getInteriorImageCount() + 1);
                        SaveAppointmentToArray(s,RequestID,0,0,appointment.getInteriorImageCount() + 1,0,0  );
                    }

                }


                    Intent intent = new Intent(UploadPhoto.this, InstallSetAppointment.class);

                    if (ImageType.equals("JobCardIns")) {

                        AndroidDocumentType documentType = new AndroidDocumentType();
                        SaveDocumentTypeToArray(documentType, mStoreID); // this will increment the count
                        mDocCount++;
                        intent.putExtra("Toast", "Upload Job card page "+  mJobCardPage.toString() +" successful. Total Job card pages = "+ mDocCount.toString());
                    }
                    if (ImageType.equals("ProductComplaint")) {
                        intent.putExtra("Toast", "Upload general complaint successful.");
                    }
                if (ImageType.equals("UnitryComplaint")) {
                    intent.putExtra("Toast", "Upload unitry complaint successful.");
                }
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
                //}
            }
        }
        //Sending data to another Activity



    }

    public AndroidDocumentType findDocumentCountByStoreIDAndType(int storeid, String doctype, ArrayList<AndroidDocumentType> jobcards){
        for (AndroidDocumentType jobcard : jobcards) {
            if (jobcard.getStoreID() == storeid && jobcard.getDocumentType().equals(doctype)) {
                return jobcard;
            }
        }
        return null;
    }

    public void  SaveDocumentTypeToArray(AndroidDocumentType androidDocumentType, Integer ID) throws JSONException {

        try {

            String docs = dbManager.getValue( "AndroidDocumentTypes");

            ArrayList<AndroidDocumentType> androidDocumentTypes = JsonUtil.parseJsonArrayAndroidDocumentTypes(docs);


            AndroidDocumentType androidDocumentType1 = findDocumentCountByStoreIDAndType(mStoreID,"JobCardIns", androidDocumentTypes);

            if(androidDocumentType1 != null) {
                androidDocumentType.setStoreID(androidDocumentType1.getStoreID());
                androidDocumentType.setDocumentType(androidDocumentType1.getDocumentType());
                androidDocumentType.setCount(androidDocumentType1.getCount() + 1);
            }
            else
            {
                androidDocumentType.setStoreID(mStoreID);
                androidDocumentType.setDocumentType("JobCardIns");
                androidDocumentType.setCount(1);

            }

            //barcode changes by itself
            //imagecount? androidStoreUnitExplicit.setImageCount(findStoreUnit.getImageCount());
            //androidStoreUnitExplicitsAll.remove(findStoreUnit);
            //Remove the found storeitem id so it can be replaced
            Integer position = 0;
            Integer foundposition = 0;

            Iterator<AndroidDocumentType> it = androidDocumentTypes.iterator();
            while (it.hasNext()) {
                AndroidDocumentType user = it.next();
                Integer findStoreID = mStoreID;
                Integer nextStoreID = user.getStoreID();
                position++;
                if (findStoreID.equals(nextStoreID)) {
                    it.remove();
                    foundposition=position;
                }
            }

            androidDocumentTypes.add(foundposition, androidDocumentType);

            //Save Requests back to local
            dbManager.setValue( "AndroidDocumentTypes","");
            String test = dbManager.getValue( "AndroidDocumentTypes");
            dbManager.setValue( "AndroidDocumentTypes", JsonUtil.convertAndroidDocumentTypeArrayToJson(androidDocumentTypes));
            test = dbManager.getValue( "AndroidDocumentTypes");

        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
        }
        //

    }


    public void  SaveAppointmentToArray(AndroidAppointment appointment, Integer ID, Integer JobcardCount,
                                       Integer ExteriorImageCount,
                                       Integer InteriorImageCount,
                                       Integer SurveySignatureCount,
                                       Integer PerformanceReviewSignatureCount
                                       ) throws JSONException {

        try {

            String storesString = dbManager.getValue( "AndroidInsAppointments");

            ArrayList<AndroidAppointment> appointments = JsonUtil.parseJsonArrayAndroidAppointment(storesString);


            AndroidAppointment findappointment = findAppointmentByID(ID, appointments);
            appointment.setRequestTypeName( findappointment.getRequestTypeName());
            appointment.setID( findappointment.getID());
            appointment.setStoreID( findappointment.getStoreID());
            appointment.setDateRequested( findappointment.getDateRequested());
            appointment.setDateAccepted( findappointment.getDateAccepted());
            appointment.setStoreID( findappointment.getStoreID());
            appointment.setAppointmentDateDay( findappointment.getAppointmentDateDay());
            appointment.setAppointmentDateMonth(findappointment.getAppointmentDateMonth());
            appointment.setAppointmentDateTime(findappointment.getAppointmentDateTime());
            appointment.setAppointmentDateTimeTime(findappointment.getAppointmentDateTimeTime());


            appointment.setMileage( findappointment.getID());
            appointment.setRequestTypeName( findappointment.getRequestTypeName());
            appointment.setStoreName( findappointment.getStoreName());
            appointment.setBrandName( findappointment.getBrandName());
            appointment.setOutletTypeName( findappointment.getOutletTypeName());
            appointment.setTierTypeName( findappointment.getTierTypeName());
            appointment.setStoreNameURN( findappointment.getStoreNameURN());
           

         
            appointment.setURN(findappointment.getURN());
            appointment.setCurrentPhase(findappointment.getCurrentPhase());
            appointment.setContactPerson(findappointment.getContactPerson());
            appointment.setContactEmail(findappointment.getContactEmail());
            appointment.setContactPhone(findappointment.getContactPhone());
            appointment.setOpeningTime(findappointment.getOpeningTime());
            appointment.setClosingTime(findappointment.getClosingTime());
            appointment.setTotalUnitCount(findappointment.getTotalUnitCount());
            appointment.setStoreID(findappointment.getStoreID());
            appointment.setDateRequested(findappointment.getDateRequested());
            appointment.setDateAccepted(findappointment.getDateAccepted());
            appointment.setAppointmentDateTime(findappointment.getAppointmentDateTime());
            appointment.setAppointmentDateDay(findappointment.getAppointmentDateDay());
            appointment.setAppointmentDateMonth(findappointment.getAppointmentDateMonth());
            appointment.setAppointmentDateYear(findappointment.getAppointmentDateYear());
            appointment.setAppointmentDateTimeTime(findappointment.getAppointmentDateTimeTime());
            appointment.setDateConfirmed(findappointment.getDateConfirmed());
            appointment.setDateRecordChanged(findappointment.getDateRecordChanged());
            appointment.setAddress(findappointment.getAddress());
            appointment.setRegion(findappointment.getRegion());


         





            if(JobcardCount>0){appointment.setJobCardCount(JobcardCount);}else{appointment.setJobCardCount( findappointment.getJobCardCount());}
                    if(ExteriorImageCount>0){appointment.setExteriorImageCount(ExteriorImageCount);}else{appointment.setExteriorImageCount( findappointment.getExteriorImageCount());}
                        if(InteriorImageCount>0){appointment.setInteriorImageCount(InteriorImageCount);}else{appointment.setInteriorImageCount( findappointment.getInteriorImageCount());}
                            if(SurveySignatureCount>0){appointment.setSurveySignatureCount(SurveySignatureCount);}else{appointment.setSurveySignatureCount( findappointment.getSurveySignatureCount());}
                                if(PerformanceReviewSignatureCount>0){appointment.setPerformanceReviewSignatureCount(PerformanceReviewSignatureCount);}else{appointment.setPerformanceReviewSignatureCount( findappointment.getPerformanceReviewSignatureCount());}


            appointments.remove(findappointment);

            Date recordChangedNow = new Date();

            appointment.setDateRecordChanged((String) android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss a", recordChangedNow));

            appointments.add(appointment);

            //Save Requests back to local
            dbManager.setValue( "AndroidInsAppointments", JsonUtil.convertAppointmentArrayToJson(appointments));


        } catch (Exception ex) {
            //
            //
        }
        //

    }



    public void  SaveStoreItemToArray(AndroidStoreUnitExplicit androidStoreUnitExplicit, Integer ID) throws JSONException {

        try {

            String storeUnitExplicits = dbManager.getValue( "AndroidStoreUnitsExplicit");
            String storeNameURN = mStoreNameURN;

            ArrayList<AndroidStoreUnitExplicit> androidStoreUnitExplicitsAll = JsonUtil.parseJsonArrayAndroidStoreUnitExplicitAll(storeUnitExplicits);

            ArrayList<AndroidStoreUnitExplicit> androidStoreUnitExplicits = JsonUtil.parseJsonArrayAndroidStoreUnitExplicit(storeUnitExplicits, storeNameURN);

            //Convert request to appointment
            AndroidStoreUnitExplicit findStoreUnit = findAndroidStoreUnitExplicitByID(ID, androidStoreUnitExplicits);

            androidStoreUnitExplicit.setURN(findStoreUnit.getURN());
            androidStoreUnitExplicit.setImagePath(findStoreUnit.getImagePath());
            Integer Max = findStoreUnit.getMaxQuantityFromMatrix();
            androidStoreUnitExplicit.setMaxQuantityFromMatrix(Max);
            androidStoreUnitExplicit.setStoreName(findStoreUnit.getStoreName());
            androidStoreUnitExplicit.setStoreItemID( findStoreUnit.getStoreItemID());
            androidStoreUnitExplicit.setStoreID( findStoreUnit.getStoreID());
            androidStoreUnitExplicit.setItemTypeName( findStoreUnit.getItemTypeName());
            androidStoreUnitExplicit.setImagePath( findStoreUnit.getImagePath());
            androidStoreUnitExplicit.setEnumerate(findStoreUnit.getEnumerate());
            androidStoreUnitExplicit.setWhyNo(findStoreUnit.getWhyNo());
            androidStoreUnitExplicit.setIsSurvey(findStoreUnit.getIsSurvey());

            androidStoreUnitExplicit.setChkDelivered(findStoreUnit.getChkDelivered());
            androidStoreUnitExplicit.setChkInstalled(findStoreUnit.getChkInstalled());

            androidStoreUnitExplicit.setAcceptedByStore(mAcceptedByStore);

            if(mQuantitySelected ==0) {
                androidStoreUnitExplicit.setQuantitySelected(findStoreUnit.getQuantitySelected());

            }
            else {
                androidStoreUnitExplicit.setQuantitySelected(mQuantitySelected);
            }
            if(!mItsABarcodeImage) {
                androidStoreUnitExplicit.setImageCount(findStoreUnit.getImageCount() + 1);
            }

            //barcode changes by itself
            //imagecount? androidStoreUnitExplicit.setImageCount(findStoreUnit.getImageCount());
            //androidStoreUnitExplicitsAll.remove(findStoreUnit);
            //Remove the found storeitem id so it can be replaced
            Integer position = 0;
            Integer foundposition = 0;

            Iterator<AndroidStoreUnitExplicit> it = androidStoreUnitExplicitsAll.iterator();
            while (it.hasNext()) {
                AndroidStoreUnitExplicit user = it.next();
                Integer findstoreItemID = findStoreUnit.getStoreItemID();
                Integer nextStoreItemID = user.getStoreItemID();
                position++;
                if (findstoreItemID.equals(nextStoreItemID)) {
                    it.remove();
                    foundposition=position;
                }
            }

            androidStoreUnitExplicitsAll.add(foundposition-1, androidStoreUnitExplicit);

            //Save Requests back to local
            dbManager.setValue( "AndroidStoreUnitsExplicit","");
            String test = dbManager.getValue( "AndroidStoreUnitsExplicit");
            dbManager.setValue( "AndroidStoreUnitsExplicit", JsonUtil.convertAndroidStoreUnitExplicitArrayToJson(androidStoreUnitExplicitsAll));
            test = dbManager.getValue( "AndroidStoreUnitsExplicit");

        } catch (Exception ex) {
            String message = ex.toString();
        }
        //

    }





    public void UploadStoreItemImage(Integer StoreItemID,
                                     Integer StoreID,
                                     String Base64Image,
                                     String UserName,
                                     String Type,
                                     String ProductComplaint,
                                     String Barcode
                                     ) {


        MySOAPCallActivity cs = new MySOAPCallActivity();
        try {

            UploadStoreItemImageParams params = new UploadStoreItemImageParams(cs, StoreItemID, StoreID,Base64Image,UserName,Type, ProductComplaint, Barcode);

            new CallSoapUploadStoreItemImage().execute(params);


            //set and remove prrogress bar
            progressDialog = new ProgressDialog(UploadPhoto.this);
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
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                }
                else {
                    result = result.replace("\n", "").replace("\r", "");
                    WhenUploadIsComplete();
                }

            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();

            }
            if(progressDialog !=null)
            {
                progressDialog.dismiss();
                progressDialog = null;
            }
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

    public AndroidAppointment findAppointmentByID(int id, ArrayList<AndroidAppointment> requests){
        for (AndroidAppointment request : requests) {
            if (request.getID() == id) {
                return request;
            }
        }
        return null;
    }

    public AndroidStoreUnitExplicit findAndroidStoreUnitExplicitByID(int id, ArrayList<AndroidStoreUnitExplicit> requests){
        for (AndroidStoreUnitExplicit request : requests) {
            if (request.getStoreItemID() == id) {
                return request;
            }
        }
        return null;
    }
}
