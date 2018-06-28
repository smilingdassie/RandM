package com.dsouchon.wayo.visualfusion;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.dsouchon.wayo.visualfusion.R;

import java.util.ArrayList;

public class TssGridViewAppointments extends  ArrayAdapter <AndroidAppointment>{

    Context context;
    private ArrayList<AndroidAppointment> data = new ArrayList<AndroidAppointment>();



    public TssGridViewAppointments(Context context, ArrayList<AndroidAppointment> data)
    {
        super(context, 0);
        this.context=context;
        this.data = data;
    }

    @Override
    public int getCount()
    {
        return data.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        View row;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.tss_row_appointments, parent, false);
        }
        else{
            row = (View) convertView;
        }

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.tss_row_appointments, parent, false);


            TextView txtURN = (TextView) row.findViewById(R.id.txtURN);

            TextView txtStoreName = (TextView) row.findViewById(R.id.txtStoreName);
            TextView txtProgress = (TextView) row.findViewById(R.id.txtProgress);



            //Get class from data
            AndroidAppointment item = data.get(position);

            //Set control values DANIEL these must change
            txtURN.setText(item.getURN());
            txtStoreName.setText(item.getStoreName());
            txtProgress.setText(item.getCurrentPhase());




        return row;



    }




}
