package edu.hitsz.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ScoreDbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "aircraft_war.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_SCORE = "scores";

    public static final String COL_ID = "id";
    public static final String COL_PLAYER_NAME = "player_name";
    public static final String COL_SCORE = "score";
    public static final String COL_DIFFICULTY = "difficulty";
    public static final String COL_PLAY_TIME = "play_time";

    private static final String CREATE_TABLE_SCORE =
            "CREATE TABLE " + TABLE_SCORE + " ("
                    + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_PLAYER_NAME + " TEXT NOT NULL, "
                    + COL_SCORE + " INTEGER NOT NULL, "
                    + COL_DIFFICULTY + " TEXT, "
                    + COL_PLAY_TIME + " TEXT NOT NULL"
                    + ");";

    public ScoreDbHelper(Context context) {
        super(context.getApplicationContext(), DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SCORE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 当前先简单处理
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORE);
        onCreate(db);
    }
}