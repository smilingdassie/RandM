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


public class WarehouseViewCustomAdapterUploads extends  ArrayAdapter {

    Context context;




    public WarehouseViewCustomAdapterUploads(Context context)
    {
        super(context, 0);
        this.context=context;

    }

    public int getCount()
    {
        return 13;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View row;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.warehouse_row_uploads, parent, false);
        }
        else{
            row = (View) convertView;
        }



            TextView textViewTitle = (TextView) row.findViewById(R.id.textView2);
            ImageView imageViewIte = (ImageView) row.findViewById(R.id.imageView2);





            textViewTitle.setText("IMG_123443");
            imageViewIte.setImageResource(R.drawable.dis1);







        return row;

    }

}