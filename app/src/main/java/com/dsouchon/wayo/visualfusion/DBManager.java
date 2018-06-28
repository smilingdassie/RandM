package com.dsouchon.wayo.visualfusion;


/**
 * Created by anupamchugh on 19/10/15.
 */import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {

    private DatabaseHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(String name, String desc) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.KEY, name);
        contentValue.put(DatabaseHelper.VALUE, desc);
        database.insert(DatabaseHelper.TABLE_NAME, null, contentValue);
    }

    public Cursor fetch() {
        String[] columns = new String[] { DatabaseHelper.ID, DatabaseHelper.KEY, DatabaseHelper.VALUE };
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int update(long _id, String name, String desc) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.KEY, name);
        contentValues.put(DatabaseHelper.VALUE, desc);
        int i = database.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper.ID + " = " + _id, null);
        return i;
    }

    public void setValue(String key, String value) {

        try {
            Cursor c = this.get(key);
            if (c == null || c.getCount()==0) {

                ContentValues contentValue = new ContentValues();
                contentValue.put(DatabaseHelper.KEY, key);
                contentValue.put(DatabaseHelper.VALUE, value);
                database.insert(DatabaseHelper.TABLE_NAME, null, contentValue);
                c.close();
            }
            else {
                c.moveToFirst();
                String id =  c.getString(c.getColumnIndex(DatabaseHelper.ID));
                Long ID = Long.parseLong(id);
                update(ID, key, value);
                c.close();
            }
        }
        catch (Exception r)
        {

        }

    }

    public String getValue(String key)
    {
        try {
            Cursor c = this.get(key);
            if (c == null || c.getCount()==0) {

               return "";
            }
            else {
                c.moveToFirst();
                String value =  c.getString(c.getColumnIndex(DatabaseHelper.VALUE));
                c.close();
                return value;

            }
        }
        catch (Exception r)
        {

        }
        return "";
    }

    public Cursor get(String key)
    {
        String[] columns = new String[] { DatabaseHelper.ID, DatabaseHelper.KEY, DatabaseHelper.VALUE };
        Cursor c=null;

        if(key != null && key.length()>0 )
        {
            String sql="SELECT * FROM "+DatabaseHelper.TABLE_NAME+
                    " WHERE "+DatabaseHelper.KEY+" = '"+key+"'";

            c=database.rawQuery(sql,null);
            return c;

        }

        c=database.query(DatabaseHelper.TABLE_NAME,columns,null,null,null,null,null);
        return c;
    }


    public void delete(long _id) {
        database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.ID + "=" + _id, null);
    }

    public Cursor getLike(String searchTerm)
    {
        String[] columns = new String[] { DatabaseHelper.ID, DatabaseHelper.KEY, DatabaseHelper.VALUE };
        Cursor c=null;

        if(searchTerm != null && searchTerm.length()>0)
        {
            String sql="SELECT * FROM "+DatabaseHelper.TABLE_NAME+" WHERE "+DatabaseHelper.KEY+" LIKE '%"+searchTerm+"%'";
            c=database.rawQuery(sql,null);
            return c;

        }

        c=database.query(DatabaseHelper.TABLE_NAME,columns,null,null,null,null,null);
        return c;
    }

}
