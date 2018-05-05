package com.tenxgames.aisd.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public final class SQLiteHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String LOG_TAG = "SQLite";
    private static final String DATABASE_NAME = "sortlogs.db";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS `" +
                SQLiteDBContract.DBEntry.TABLE_NAME + "` (`" +
                SQLiteDBContract.DBEntry._ID + "` INTEGER PRIMARY KEY AUTOINCREMENT, `" +
                SQLiteDBContract.DBEntry.COLUMN_TIME + "` TEXT NOT NULL, `" +
                SQLiteDBContract.DBEntry.COLUMN_SORTTIME + "` TEXT NOT NULL, `" +
                SQLiteDBContract.DBEntry.COLUMN_SEQ_START + "` TEXT NOT NULL, `" +
                SQLiteDBContract.DBEntry.COLUMN_SEQ_SORTED + "` TEXT NOT NULL);";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Note in journal
        Log.w(LOG_TAG, "Обновляемся с версии " + oldVersion + " на версию " + newVersion);

        // Delete old table and create new
        db.execSQL("DROP TABLE IF EXISTS " + SQLiteDBContract.DBEntry.TABLE_NAME);

        // Create new
        onCreate(db);
    }

    public void getAllSortRecords(ArrayList<SortRecord> listRecords) {
        SQLiteDatabase db = getReadableDatabase();
        /// Create query
        Cursor cursor = db.rawQuery("SELECT * FROM " +
                SQLiteDBContract.DBEntry.TABLE_NAME + ";", null);

        /// Parse answer
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(SQLiteDBContract.DBEntry._ID);
            int timeIndex = cursor.getColumnIndex(SQLiteDBContract.DBEntry.COLUMN_TIME);
            int sortTimeIndex = cursor.getColumnIndex(SQLiteDBContract.DBEntry.COLUMN_SORTTIME);
            int seqStartIndex = cursor.getColumnIndex(SQLiteDBContract.DBEntry.COLUMN_SEQ_START);
            int seqSortedIndex = cursor.getColumnIndex(SQLiteDBContract.DBEntry.COLUMN_SEQ_SORTED);

            /// Parse all rows of answer
            do {
                listRecords.add(new SortRecord(cursor.getInt(idIndex),
                        cursor.getString(timeIndex),
                        cursor.getString(sortTimeIndex),
                        cursor.getString(seqStartIndex),
                        cursor.getString(seqSortedIndex)));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
    }

    public SortRecord addSortRecord(SortRecord sortRecord)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(SQLiteDBContract.DBEntry.COLUMN_TIME, sortRecord.time);
        cv.put(SQLiteDBContract.DBEntry.COLUMN_SORTTIME, sortRecord.sortTime);
        cv.put(SQLiteDBContract.DBEntry.COLUMN_SEQ_START, sortRecord.sequenceStart);
        cv.put(SQLiteDBContract.DBEntry.COLUMN_SEQ_SORTED, sortRecord.sequenceSorted);

        long id = db.insert(SQLiteDBContract.DBEntry.TABLE_NAME, null, cv);
        if (id > 0) {

            sortRecord.id = (int) id;
            return sortRecord;
        }
        else return null;
    }

    /**
     * Closes Database connection
     */
    public void closeDatabase()
    {
        getReadableDatabase().close();
    }

    public boolean isDatabaseOpen()
    {
        return getReadableDatabase().isOpen();
    }
}
