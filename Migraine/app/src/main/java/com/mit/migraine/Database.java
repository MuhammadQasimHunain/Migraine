package com.mit.migraine;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database {

    public static final String DATABASE_NAME = "Migraine";
    public static final int DATABASE_VERSION = 1;

    public static final String MEDICINES_TABLE_NAME = "medicines";

    public static final String _ID = "id";
    public static final String NAME = "name";
    public static final String STRENGTH = "strength";


    public static final String COUNTRY_TABLE_CREATE_STMT = "create table "+ MEDICINES_TABLE_NAME + "( "
            + _ID + " integer primary key autoincrement not null, "
            + NAME + " text not null, "
            + STRENGTH + " text not null );";

    private DbHelper ourHelper;
    private final Context ourContext;
    private SQLiteDatabase ourDatabase;

    private static class DbHelper extends SQLiteOpenHelper{

        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub
            db.execSQL(COUNTRY_TABLE_CREATE_STMT);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            db.execSQL("DROP TABLE IF EXISTS "+ MEDICINES_TABLE_NAME);
            onCreate(db);
        }
    }
    public Database(Context c){
        ourContext = c;
    }

    public Database Open(){
        ourHelper = new DbHelper(ourContext);
        ourDatabase = ourHelper.getWritableDatabase();
        return this;
    }

    public void Close(){
        ourHelper.close();
    }

    public long addMedicines(Medicine med){
        ContentValues cv = new ContentValues();
        cv.put(NAME, med.getMed_name());
        cv.put(STRENGTH, med.getMed_strength());
        return this.ourDatabase.insert(MEDICINES_TABLE_NAME, null, cv);
    }

    public ArrayList<Medicine> getMedicines(String name) {
        // TODO Auto-generated method stub

        Cursor c;
        try {
            c = ourDatabase.rawQuery("SELECT * FROM medicines WHERE id = '"+ Integer.parseInt(name.trim()) +"'", null);
        } catch(NumberFormatException e) {
            c = ourDatabase.rawQuery("SELECT * FROM medicines WHERE name LIKE '%" + name.trim() + "%'", null);
        }

        ArrayList<Medicine> m = new ArrayList<Medicine>();
        while(c.moveToNext())
        {
            // Medicine med = new Medicine(c.getInt(0), c.getString(1), c.getString(2));
            // m.add(med);
        }
        c.close();
        return m;
    }

    public Medicine getMedicine(String name) {
        // TODO Auto-generated method stub

        Cursor c = ourDatabase.rawQuery("SELECT * FROM medicines WHERE name = '"+ name.trim() +"'", null);

        if(c.moveToNext())
        {
            //return new Medicine(c.getInt(0), c.getString(1), c.getString(2));
        }
        return null;
    }

}
