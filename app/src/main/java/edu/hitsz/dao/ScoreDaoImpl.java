package edu.hitsz.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import edu.hitsz.model.ScoreRecord;

public class ScoreDaoImpl implements ScoreDao {

    private final ScoreDbHelper dbHelper;

    public ScoreDaoImpl(Context context) {
        this.dbHelper = new ScoreDbHelper(context);
    }

    @Override
    public long insert(ScoreRecord record) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ScoreDbHelper.COL_PLAYER_NAME, record.getPlayerName());
        values.put(ScoreDbHelper.COL_SCORE, record.getScore());
        values.put(ScoreDbHelper.COL_DIFFICULTY, record.getDifficulty());
        values.put(ScoreDbHelper.COL_PLAY_TIME, record.getPlayTime());
        long id = db.insert(ScoreDbHelper.TABLE_SCORE, null, values);
        db.close();
        return id;
    }

    @Override
    public boolean deleteById(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(
                ScoreDbHelper.TABLE_SCORE,
                ScoreDbHelper.COL_ID + "=?",
                new String[]{String.valueOf(id)}
        );
        db.close();
        return rows > 0;
    }

    @Override
    public int deleteAll() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(ScoreDbHelper.TABLE_SCORE, null, null);
        db.close();
        return rows;
    }

    @Override
    public List<ScoreRecord> findAllOrderByScoreDesc() {
        List<ScoreRecord> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                ScoreDbHelper.TABLE_SCORE,
                null,
                null,
                null,
                null,
                null,
                ScoreDbHelper.COL_SCORE + " DESC, " + ScoreDbHelper.COL_ID + " ASC"
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                ScoreRecord record = new ScoreRecord();
                record.setId(cursor.getLong(cursor.getColumnIndexOrThrow(ScoreDbHelper.COL_ID)));
                record.setPlayerName(cursor.getString(cursor.getColumnIndexOrThrow(ScoreDbHelper.COL_PLAYER_NAME)));
                record.setScore(cursor.getInt(cursor.getColumnIndexOrThrow(ScoreDbHelper.COL_SCORE)));
                record.setDifficulty(cursor.getString(cursor.getColumnIndexOrThrow(ScoreDbHelper.COL_DIFFICULTY)));
                record.setPlayTime(cursor.getString(cursor.getColumnIndexOrThrow(ScoreDbHelper.COL_PLAY_TIME)));
                list.add(record);
            }
            cursor.close();
        }

        db.close();
        return list;
    }
}