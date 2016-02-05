package com.bignerdranch.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.bignerdranch.criminalintent.database.CrimeBaseHelper;
import com.bignerdranch.criminalintent.database.CrimeCursorWrapper;
import com.bignerdranch.criminalintent.database.CrimeDbSchema.CrimeTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private Context mContext;
    private SQLiteDatabase mSQLiteDatabase;

    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        mSQLiteDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
    }

    private static ContentValues getContentValues(Crime crime) {
        ContentValues cv = new ContentValues();
        cv.put(CrimeTable.Cols.UUID, crime.getId().toString());
        cv.put(CrimeTable.Cols.TITLE, crime.getTitle());
        cv.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        cv.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        return cv;
    }

    public static CrimeLab getCrimeLab(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    public List<Crime> getCrimeList() {
        List<Crime> crimeList = new ArrayList<>();
        CrimeCursorWrapper crimeCursorWrapper = queryCrimes(null, null);
        try {
            crimeCursorWrapper.moveToFirst();
            while(!crimeCursorWrapper.isAfterLast()) {
                crimeList.add(crimeCursorWrapper.getCrime());
                crimeCursorWrapper.moveToNext();
            }
        } finally {
            crimeCursorWrapper.close();
        }
        return crimeList;
    }

    public Crime getCrime(UUID id) {
        CrimeCursorWrapper crimeCursorWrapper = queryCrimes(CrimeTable.Cols.UUID + " = ?", new String[]{id.toString()});
        try {
            if (crimeCursorWrapper.getCount() == 0) {
                return null;
            }
            crimeCursorWrapper.moveToFirst();
            return crimeCursorWrapper.getCrime();
        } finally {
            crimeCursorWrapper.close();
        }
    }

    public void addCrime(Crime crime) {
        ContentValues cv = getContentValues(crime);
        mSQLiteDatabase.insert(CrimeTable.NAME, null, cv);
    }

    public void deleteCrime(String whereClause, String[] whereArgs) {
        int rowsDeleted = mSQLiteDatabase.delete(CrimeTable.NAME, whereClause, whereArgs);
        Toast.makeText(mContext, "Number of rows deleted: " + rowsDeleted, Toast.LENGTH_SHORT).show();
    }

    public void updateCrime(Crime crime) {
        String uuidString = crime.getId().toString();
        ContentValues cv = getContentValues(crime);
        mSQLiteDatabase.update(CrimeTable.NAME, cv, CrimeTable.Cols.UUID + " = ?", new String[]{uuidString});
    }

    public CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mSQLiteDatabase.query(CrimeTable.NAME, null, whereClause, whereArgs, null, null, null);
        return new CrimeCursorWrapper(cursor);
    }
}
