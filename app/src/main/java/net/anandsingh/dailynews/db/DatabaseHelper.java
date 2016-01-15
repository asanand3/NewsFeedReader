package net.anandsingh.dailynews.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by Anand Singh
 */
public class DatabaseHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "news.db";
    private static final String TABLE_NAME = "news";
    private static final String TITLE = "TITLE";
    private static final String TYPE = "TYPE";
    private static final String CONTENT = "CONTENT";
    private static final String DATE = "DATE";
    private static final String LINK = "LINK";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        //Create Table
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,TYPE TEXT,TITLE TEXT NOT NULL UNIQUE,DATE TEXT,CONTENT TEXT,LINK TEXT)");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


        //Upgrade table
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    public boolean insertData(String type, String title, String date, String content, String link) {


        SQLiteDatabase db = this.getWritableDatabase();


        //Insert data in table
        ContentValues contentValues = new ContentValues();
        contentValues.put(TYPE, type);
        contentValues.put(TITLE, title);
        contentValues.put(DATE, date);
        contentValues.put(CONTENT, content);
        contentValues.put(LINK, link);


        long result = db.insertWithOnConflict(TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);


        db.close();
        if (result == -1)
            return false;
        else
            return true;


    }


    public Cursor getData(String type) {
        SQLiteDatabase db = this.getWritableDatabase();


        //Get data from table
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE TYPE = '" + type + "'";


        Cursor cursor = db.rawQuery(query, null);


        return cursor;
    }
}