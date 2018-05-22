package cn.dictionary.app.dictionary.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cn.dictionary.app.dictionary.application.MyApplication;
import cn.dictionary.app.dictionary.config.Table;
import cn.dictionary.app.dictionary.entity.DailySentence;

/**
 * 对每日一句操作的实体类
 */

public class DailySentenceDao {

    private static DailySentenceDao mDailySentenceDao;

    /**
     * 私有化构造器
     */
    private DailySentenceDao() {
    }

    /**
     * 获取单实例方法
     *
     * @return SearchRecordDao
     */
    public static DailySentenceDao getInstance() {
        //双重检验锁
        if (mDailySentenceDao == null) {
            synchronized (DailySentenceDao.class) {
                if (mDailySentenceDao == null) {
                    mDailySentenceDao = new DailySentenceDao();
                }
            }
        }
        return mDailySentenceDao;
    }

    /**
     * 添加每日一句
     *
     * @param dailySentence
     */
    public void addDailySentence(DailySentence dailySentence) {
        if (queryDailySentence(dailySentence.getDateline()) != null) {
            updateDailySentence(dailySentence);
        } else {
            SQLiteDatabase db =
                    new MyDBHelper(MyApplication.getContext(), Table.DB, null, 1).getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("dateline", dailySentence.getDateline());
            values.put("englishContent", dailySentence.getEnglishContent());
            values.put("chineseContent", dailySentence.getChineseContent());
            values.put("picturePath", dailySentence.getPicturePath());
            values.put("voicePath", dailySentence.getVoicePath());
            db.insert(Table.DAILYSENTENCE, null, values);
            db.close();
        }
    }

    /**
     * 删除某一天的每日一句
     *
     * @param dateline
     */
    public void deleteDailySentence(String dateline) {
        SQLiteDatabase db =
                new MyDBHelper(MyApplication.getContext(), Table.DB, null, 1).getWritableDatabase();
        db.execSQL("delete from " + Table.DAILYSENTENCE + " where dateline = '" + dateline + "'");
        db.close();

    }

    /**
     * 删除所有的每日一句
     */
    public void deleteAllDailySentence() {
        SQLiteDatabase db =
                new MyDBHelper(MyApplication.getContext(), Table.DB, null, 1).getWritableDatabase();
        db.execSQL("delete from " + Table.DAILYSENTENCE);
        db.close();
    }


    /**
     * 查询某个日期的每日一句
     *
     * @param dateline
     * @return
     */
    public DailySentence queryDailySentence(String dateline) {
        SQLiteDatabase db =
                new MyDBHelper(MyApplication.getContext(), Table.DB, null, 1).getWritableDatabase();
        String sql = "select * from " + Table.DAILYSENTENCE + " where dateline = '" + dateline + "'";
        Cursor cursor = db.rawQuery(sql, null);
        DailySentence ds = null;
        if (cursor.moveToFirst()) {
            ds = new DailySentence();
            do {
                ds.setPicturePath(cursor.getString(cursor.getColumnIndex("picturePath")));
                ds.setDateline(cursor.getString(cursor.getColumnIndex("dateline")));
                ds.setEnglishContent(cursor.getString(cursor.getColumnIndex("englishContent")));
                ds.setChineseContent(cursor.getString(cursor.getColumnIndex("chineseContent")));
                ds.setVoicePath(cursor.getString(cursor.getColumnIndex("voicePath")));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return ds;
    }

    /**
     * 查询本地所有的每日一句的数据
     *
     * @return 若有数据则返回装有每日一句的数据，否则返回null
     */
    public List<DailySentence> queryAllDailySentence() {
        SQLiteDatabase db =
                new MyDBHelper(MyApplication.getContext(), Table.DB, null, 1).getWritableDatabase();
        String sql = "select * from " + Table.DAILYSENTENCE;
        Cursor cursor = db.rawQuery(sql, null);
        List<DailySentence> dailySentenceList = null;
        DailySentence ds;
        if (cursor.moveToFirst()) {
            dailySentenceList = new ArrayList<>();
            do {
                ds = new DailySentence();
                ds.setPicturePath(cursor.getString(cursor.getColumnIndex("picturePath")));
                ds.setDateline(cursor.getString(cursor.getColumnIndex("dateline")));
                ds.setEnglishContent(cursor.getString(cursor.getColumnIndex("englishContent")));
                ds.setChineseContent(cursor.getString(cursor.getColumnIndex("chineseContent")));
                ds.setVoicePath(cursor.getString(cursor.getColumnIndex("voicePath")));
                dailySentenceList.add(ds);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return dailySentenceList;
    }

    /**
     * 更新每日一句
     *
     * @param dailySentence 每日一句的对象
     */
    public void updateDailySentence(DailySentence dailySentence) {
        SQLiteDatabase db =
                new MyDBHelper(MyApplication.getContext(), Table.DB, null, 1).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("dateline", dailySentence.getDateline());
        values.put("picturePath", dailySentence.getPicturePath());
        values.put("englishContent", dailySentence.getEnglishContent());
        values.put("chineseContent", dailySentence.getChineseContent());
        values.put("voicePath", dailySentence.getVoicePath());
        db.update(Table.DAILYSENTENCE, values, "dateline = ?", new String[]{dailySentence.getDateline()});
        db.close();
    }


}
