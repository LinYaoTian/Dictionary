package cn.dictionary.app.dictionary.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cn.dictionary.app.dictionary.application.MyApplication;
import cn.dictionary.app.dictionary.config.Table;
import cn.dictionary.app.dictionary.entity.Words;

/**
 * 对搜索记录进行操作的实体类
 */

public class SearchRecordDao {

    private static SearchRecordDao mSearchRecordDao;

    /**
     * 私有化构造器
     */
    private SearchRecordDao() {

    }

    /**
     * 获取单实例方法
     *
     * @return SearchRecordDao
     */
    public static SearchRecordDao getInstance() {
        //双重检验锁
        if (mSearchRecordDao == null) {
            synchronized (SearchRecordDao.class) {
                if (mSearchRecordDao == null) {
                    mSearchRecordDao = new SearchRecordDao();
                }
            }
        }
        return mSearchRecordDao;
    }

    /**
     * 添加一条记录
     */
    public void addWord(Words word) {
        SQLiteDatabase db =
                new MyDBHelper(MyApplication.getContext(), Table.DB, null, 1).getWritableDatabase();
        //若单词存在记录,则更新它
        if (queryWord(word.getQuery()) != null) {
            updateWord(word);
            db.close();
            return;
        }
        //否则添加这个单词进历史记录
        ContentValues values = new ContentValues();
        //组装数据
        values.put("uk_speech", word.getUk_speech());
        values.put("us_speech", word.getUk_speech());
        values.put("query", word.getQuery());
        values.put("translation", word.getTranslation());
        values.put("uk_phonetic", word.getUk_phonetic());
        values.put("us_phonetic", word.getUs_phonetic());
        values.put("explains", word.getExplainsAfterDeal());
        values.put("webs", word.getWebsAfterDeal());
        //插入数据
        db.insert(Table.SEARCHRECORD, null, values);
        db.close();
    }

    /**
     * 在历史记录中更新某个单词
     */
    public void updateWord(Words word) {
        SQLiteDatabase db =
                new MyDBHelper(MyApplication.getContext(), Table.DB, null, 1).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("uk_speech", word.getUk_speech());
        values.put("us_speech", word.getUs_speech());
        values.put("query", word.getQuery());
        values.put("translation", word.getTranslation());
        values.put("uk_phonetic", word.getUk_phonetic());
        values.put("us_phonetic", word.getUs_phonetic());
        values.put("explains", word.connectExplains(word.getExplains()));
        values.put("webs", word.connectWebs(word.getWebs()));
        db.update(Table.SEARCHRECORD, values, "query = ?", new String[]{word.getQuery()});
        db.close();
    }


    /**
     * 查询全部记录
     *
     * @return 单词的集合
     */
    public List<Words> queryAllWord() {
        SQLiteDatabase db =
                new MyDBHelper(MyApplication.getContext(), Table.DB, null, 1).getWritableDatabase();
        String sql = "select * from " + Table.SEARCHRECORD;
        Cursor cursor = db.rawQuery(sql, null);
        List<Words> wordsList = null;
        if (cursor.moveToFirst()) {
            wordsList = new ArrayList<Words>();
            Words words;
            int i = 0;
            do {
                words = new Words();
                words.setQuery(cursor.getString(cursor.getColumnIndex("query")));
                words.setTranslation(cursor.getString(cursor.getColumnIndex("translation")));
                words.setUk_speech(cursor.getString(cursor.getColumnIndex("uk_speech")));
                words.setUk_phonetic(cursor.getString(cursor.getColumnIndex("uk_phonetic")));
                words.setUs_speech(cursor.getString(cursor.getColumnIndex("us_speech")));
                words.setUs_phonetic(cursor.getString(cursor.getColumnIndex("us_phonetic")));
                words.setExplainsAfterDeal(cursor.getString(cursor.getColumnIndex("explains")));
                words.setWebsAfterDeal(cursor.getString(cursor.getColumnIndex("webs")));
                wordsList.add(words);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return wordsList;
    }

    /**
     * 判断单词是否有记录
     *
     * @param word
     * @return 若单词存在，返回单词对象,否则，返回null
     */
    public Words queryWord(String word) {
        SQLiteDatabase db =
                new MyDBHelper(MyApplication.getContext(), Table.DB, null, 1).getWritableDatabase();
        String sql = "select * from '" + Table.SEARCHRECORD + "' where query = '" + word + "'";
        Cursor cursor = db.rawQuery(sql, null);
        Words words = null;
        if (cursor.moveToFirst()) {
            words = new Words();
            do {
                words.setQuery(cursor.getString(cursor.getColumnIndex("query")));
                words.setTranslation(cursor.getString(cursor.getColumnIndex("translation")));
                words.setUk_speech(cursor.getString(cursor.getColumnIndex("uk_speech")));
                words.setUk_phonetic(cursor.getString(cursor.getColumnIndex("uk_phonetic")));
                words.setUs_speech(cursor.getString(cursor.getColumnIndex("us_speech")));
                words.setUs_phonetic(cursor.getString(cursor.getColumnIndex("us_phonetic")));
                words.setExplainsAfterDeal(cursor.getString(cursor.getColumnIndex("explains")));
                words.setWebsAfterDeal(cursor.getString(cursor.getColumnIndex("webs")));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return words;
    }

    /**
     * 删除全部记录
     */
    public void deleteAllWord() {
        SQLiteDatabase db =
                new MyDBHelper(MyApplication.getContext(), Table.DB, null, 1).getWritableDatabase();
        db.delete(Table.SEARCHRECORD, null, null);
        db.close();
    }

    /**
     * 模糊查询搜索记录
     *
     * @param s 文本框中的文本
     * @return 查询结果的List, 若无记录，返回null
     */
    public List<Words> fuzzyQuery(String s) {
        SQLiteDatabase db =
                new MyDBHelper(MyApplication.getContext(), Table.DB, null, 1).getWritableDatabase();
        String sql = "select * from '" + Table.SEARCHRECORD + "' where query like '" + s + "%'";
        Cursor cursor = db.rawQuery(sql, null);
        List<Words> wordsList = null;
        if (cursor.moveToFirst()) {
            wordsList = new ArrayList<Words>();
            Words words;
            int i = 0;
            do {
                words = new Words();
                words.setQuery(cursor.getString(cursor.getColumnIndex("query")));
                words.setExplainsAfterDeal(cursor.getString(cursor.getColumnIndex("explains")));
                wordsList.add(words);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return wordsList;
    }
}
