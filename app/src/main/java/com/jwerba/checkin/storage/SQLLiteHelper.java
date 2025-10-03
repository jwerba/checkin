package com.jwerba.checkin.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jwerba.checkin.model.Day;
import com.jwerba.checkin.model.DayType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SQLLiteHelper extends SQLiteOpenHelper  implements Storage {
    // Database Info
    private static final String DATABASE_NAME = "checkInDatabase";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String DATES_TABLE = "dates";


    // Post Table Columns
    private static final String DATES_COLUMN_ID = "id";
    private static final String DATES_COLUMN_TYPE = "type";


    private static SQLLiteHelper sInstance;

    public static synchronized SQLLiteHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new SQLLiteHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private SQLLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_DATES_TABLE = "CREATE TABLE " + DATES_TABLE +
                "(" + DATES_COLUMN_ID + " INTEGER PRIMARY KEY," + // Define a primary key
                DATES_COLUMN_TYPE + " INTEGER)";
        db.execSQL(CREATE_DATES_TABLE);
    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + DATES_TABLE);
            onCreate(db);
        }
    }



    @Override
    public List<Day> getMonthData(int year, int month) throws Exception {
        long firstDay = LocalDate.of(year, month, 1).toEpochDay();
        long lastDay = LocalDate.of(year, month, 1).plusMonths(1).minusDays(1).toEpochDay();
        String POSTS_SELECT_QUERY = String.format("SELECT * FROM %s WHERE id >= %s AND id <= %s", DATES_TABLE, String.valueOf(firstDay), String.valueOf(lastDay));
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(POSTS_SELECT_QUERY, null);
        List<Day> days = fromCursorToObjects(cursor);
        return days;
    }

    @Override
    public void add(Day day) throws Exception {
        // The database connection is cached so it's not expensive to call getWriteableDatabase() multiple times.
        SQLiteDatabase db = getWritableDatabase();
        long epochLong = day.getDate().toEpochDay();
        if (day.getDayType() == DayType.REGULAR_DAY){
            ContentValues values = new ContentValues();
            values.put(DATES_COLUMN_ID, epochLong);
            db.delete(DATES_TABLE, DATES_COLUMN_ID + "= ?", new String[]{String.valueOf(epochLong)});
        }else {
            ContentValues values = new ContentValues();
            values.put(DATES_COLUMN_TYPE, day.getDayType().getId());

            int rows = db.update(DATES_TABLE, values, DATES_COLUMN_ID + "= ?", new String[]{String.valueOf(epochLong)});

            // Check if update succeeded
            if (rows == 0) {
                values.put(DATES_COLUMN_ID, epochLong);
                // Get the primary key of the user we just updated
                epochLong = db.insertOrThrow(DATES_TABLE, null, values);
            }
        }
    }

    public List<Day> getAll() {
        String POSTS_SELECT_QUERY =
                String.format("SELECT * FROM %s", DATES_TABLE);

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(POSTS_SELECT_QUERY, null);
        List<Day> days = fromCursorToObjects(cursor);
        return days;
    }

    private List<Day> fromCursorToObjects(Cursor cursor) {
        List<Day> posts = new ArrayList<>();
        try {
            if (cursor.moveToFirst()) {
                do {
                    Day newDay = new Day(LocalDate.ofEpochDay(cursor.getLong(0)), DayType.fromId(cursor.getInt(1)));
                    posts.add(newDay);
                } while(cursor.moveToNext());
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return posts;
    }


}
