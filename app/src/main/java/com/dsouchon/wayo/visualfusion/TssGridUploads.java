package com.dsouchon.wayo.visualfusion;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.dsouchon.wayo.visualfusion.R;
// Custom Adapters for RepUpload Activity
//style xml files are grid_row_uploads.xml


public class TssGridUploads extends  ArrayAdapter {

    Context context;




    public TssGridUploads(Context context)
    {
        super(context, 0);
        this.context=context;

    }

    public int getCount()
    {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View row = convertView;

        if (row == null)
        {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.tss_row_uploads, parent, false);


            TextView textViewTitle = (TextView) row.findViewById(R.id.textView2);
            ImageView imageViewIte = (ImageView) row.findViewById(R.id.imageView2);





            textViewTitle.setText("New_IMG_123443");
            imageViewIte.setImageResource(R.drawable.dis1);



        }





        return row;

    }

}
