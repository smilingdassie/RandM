package com.dsouchon.wayo.visualfusion;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class ContractLast extends AppCompatActivity {
    Integer globalStoreID;
    String globalStoreNameURN;  String  globalDMSKe; String globalSignatureType;
    private DBManager dbManager;
    public int RequestID;
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
    public void onBackPressed() {
        //DO NOTHING

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);  dbManager = new DBManager(this);        dbManager.open();
        setContentView(R.layout.layout_contract_last);
        Intent me = getIntent();
        globalStoreID = me.getIntExtra("StoreID", 0);
        globalStoreNameURN = me.getStringExtra("StoreNameURN");
        globalDMSKe= me.getStringExtra("DMSKeNo");
        globalSignatureType = me.getStringExtra("SignatureType");
        RequestID = me.getIntExtra("RequestID", 0);


        TextView storenametext = (TextView) findViewById(R.id.storenametext);
        storenametext.setText(String.format("For Outlet: %s - (Ke: %s)",globalStoreNameURN, globalDMSKe));


    }
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

            Intent intent = new Intent(ContractLast.this, HomeMenu.class );

            startActivity(intent); finish();

            // return true;
        }



        if (id == R.id.logoutbutton){
            startActivity(new Intent(this,LoginActivity.class));
        }

        if (id == R.id.backbutton){
            startActivity(new Intent(this,InstallSetAppointment.class));
        }

        return super.onOptionsItemSelected(item);
    }

    public void GoToSignatureNew(View view) {
        try {
            EditText yourName = (EditText) findViewById(R.id.yourname);
            EditText designation = (EditText) findViewById(R.id.Designation);

            if (yourName.getText().toString().equals("") || designation.getText().toString().equals("")) {
                Toast.makeText(this, "Please enter your name and designation.", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(ContractLast.this, SignatureActivity.class);

                intent.putExtra("FromScreen", "Contract");
                intent.putExtra("StoreID", globalStoreID);
                intent.putExtra("StoreNameURN", globalStoreNameURN);
                intent.putExtra("SignatureType", globalSignatureType);
                intent.putExtra("RequestID", RequestID);

                intent.putExtra("YourName", yourName.getText().toString());
                intent.putExtra("Designation", designation.getText().toString());

                dbManager.setValue( "YourName", yourName.getText().toString());
                dbManager.setValue( "Designation", designation.getText().toString());

                startActivity(intent);
                finish();
            }
        }
        catch(Exception e)
        {
            Toast.makeText(getApplicationContext(), e.toString(),Toast.LENGTH_LONG).show();
        }
    }
}
