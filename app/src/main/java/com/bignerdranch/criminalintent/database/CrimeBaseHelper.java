package com.bignerdranch.criminalintent.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.bignerdranch.criminalintent.database.CrimeDbSchema.CrimeTable;

public class CrimeBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "crimeBase.db";

    public CrimeBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + CrimeTable.NAME + " (" + " _id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CrimeTable.Cols.UUID + " TEXT, " + CrimeTable.Cols.TITLE + " TEXT, "
                + CrimeTable.Cols.DATE + " TEXT, " + CrimeTable.Cols.SOLVED + "INTEGER" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CrimeTable.NAME);
        onCreate(db);
    }
}
