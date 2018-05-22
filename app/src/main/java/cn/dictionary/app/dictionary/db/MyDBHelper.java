package cn.dictionary.app.dictionary.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cn.dictionary.app.dictionary.config.Table;

/**
 * SQLite帮助类
 */

public class MyDBHelper extends SQLiteOpenHelper {
    public MyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Table.CREATE_DAILYSENTENCE);
        db.execSQL(Table.CREATE_WORDBOOK);
        db.execSQL(Table.CREATE_SEARCHRECORD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
