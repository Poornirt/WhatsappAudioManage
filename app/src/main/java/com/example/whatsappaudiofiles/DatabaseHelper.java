package com.example.whatsappaudiofiles;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.whatsappaudiofiles.jdo.Audio;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "WhatsAppDB";
    private static final String AUDIO_ID = "audio_id";
    private static final String AUDIO_TABLE = "audio_table";
    private static final String AUDIO_NAME = "audio_name";
    private static final String AUDIO_DATE = "audio_date";
    private static final String AUDIO_DURATION = "audio_duration";
    private static final String AUDIO_PATH = "audio_path";

    String TAG = "DatabaseHelper";

    private SQLiteDatabase sqLiteDatabase;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, 3);
        Log.d(TAG, "Constructor: ");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + AUDIO_TABLE + "(" + AUDIO_ID + " INTEGER " + "primary key,"
                + AUDIO_NAME + " TEXT,"
                + AUDIO_DATE + " TEXT,"
                + AUDIO_PATH + " TEXT,"
                + AUDIO_DURATION + " TEXT )"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: "+oldVersion+" "+newVersion);
        if (newVersion > oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + AUDIO_TABLE);
            onCreate(db);
        }
    }

    public List<Audio> getAllAudio() {
        List<Audio> lAudioFiles = new ArrayList<>();
        Audio lAudio ;
        sqLiteDatabase = this.getReadableDatabase();
        Cursor lCursor = sqLiteDatabase.rawQuery("SELECT * FROM " + AUDIO_TABLE, null);
        if (lCursor != null && lCursor.moveToFirst()) {
            do {
                lAudio = new Audio();
                lAudio.setName(lCursor.getString(lCursor.getColumnIndex(AUDIO_NAME)));
                lAudio.setDate(lCursor.getString(lCursor.getColumnIndex(AUDIO_DATE)));
                lAudio.setPath(lCursor.getString(lCursor.getColumnIndex(AUDIO_PATH)));
                lAudio.setDuration(lCursor.getString(lCursor.getColumnIndex(AUDIO_DURATION)));
                lAudioFiles.add(lAudio);
            } while (lCursor.moveToNext());
        }
        return lAudioFiles;
    }

    public boolean insertContacts(List<Audio> pAudioList) {
        sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        for (Audio lAudio : pAudioList) {
            contentValues.put(AUDIO_NAME, lAudio.getName());
            contentValues.put(AUDIO_DATE, lAudio.getDate());
            contentValues.put(AUDIO_DURATION, lAudio.getDuration());
            contentValues.put(AUDIO_PATH,lAudio.getPath());
            sqLiteDatabase.insert(AUDIO_TABLE, null, contentValues);
            Log.d(TAG, String.valueOf(contentValues));
            return true;
        }
        sqLiteDatabase.close();
        return false;
    }
}
