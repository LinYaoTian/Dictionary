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
 * 对进行wordbook进行操作的类
 */

public class WordBookDao {


    private static WordBookDao mWordBookDao;

    /**
     * 私有化构造器
     */
    private WordBookDao() {

    }

    /**
     * 获取单实例方法
     *
     * @return
     */
    public static WordBookDao getInstance() {
        //双重检验锁
        if (mWordBookDao == null) {
            synchronized (WordBookDao.class) {
                if (mWordBookDao == null) {
                    mWordBookDao = new WordBookDao();
                }
            }
        }
        return mWordBookDao;
    }


    /**
     * 添加单词进单词本
     *
     * @param word
     */
    public void addWord(Words word) {
        SQLiteDatabase db =
                new MyDBHelper(MyApplication.getContext(), Table.DB, null, 1).getWritableDatabase();
        //若单词在单词本中存在,则更新它
        if ((queryWord(word.getQuery())) != null) {
            updateWord(word);
        } else {
            //否则添加这个单词进单词本
            ContentValues values = new ContentValues();
            //组装数据
            values.put("uk_speech", word.getUk_speech());
            values.put("us_speech", word.getUs_speech());
            values.put("query", word.getQuery());
            values.put("translation", word.getTranslation());
            values.put("uk_phonetic", word.getUk_phonetic());
            values.put("us_phonetic", word.getUs_phonetic());
            values.put("explains", word.getExplainsAfterDeal());
            values.put("webs", word.getWebsAfterDeal());
            //插入数据
            db.insert(Table.WORDBOOK, null, values);
        }
        db.close();
    }

    /**
     * 更新某个单词
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
        db.update(Table.WORDBOOK, values, "query = ?", new String[]{word.getQuery()});
        db.close();
    }

    /**
     * 查询所有单词
     *
     * @return 单词全部数据的集合
     */
    public List<Words> queryAllWords() {
        SQLiteDatabase db =
                new MyDBHelper(MyApplication.getContext(), Table.DB, null, 1).getWritableDatabase();
        String sql = "select * from " + Table.WORDBOOK;
        Cursor cursor = db.rawQuery(sql, null);
        List<Words> wordsList = null;
        if (cursor.moveToFirst()) {
            wordsList = new ArrayList<Words>();
            Words words;
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
                words.setOrder_number(cursor.getInt(cursor.getColumnIndex("order_number")));
                wordsList.add(words);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return wordsList;
    }

    /**
     * 查询所有单词
     *
     * @return 单词的集合（只有单词和添加的顺序）
     */
    public List<Words> queryAllWordKey() {
        SQLiteDatabase db =
                new MyDBHelper(MyApplication.getContext(), Table.DB, null, 1).getWritableDatabase();
        String sql = "select query,order_number from " + Table.WORDBOOK;
        Cursor cursor = db.rawQuery(sql, null);
        List<Words> wordsList = null;
        Words word;
        if (cursor.moveToFirst()) {
            wordsList = new ArrayList<Words>();
            do {
                word = new Words();
                word.setQuery((cursor.getString(cursor.getColumnIndex("query"))));
                word.setOrder_number(cursor.getInt(cursor.getColumnIndex("order_number")));
                wordsList.add(word);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return wordsList;
    }


    /**
     * 查询一个单词
     *
     * @return 若存在，则返回单词对象，否则，返回null
     */
    public Words queryWord(String word) {
        SQLiteDatabase db =
                new MyDBHelper(MyApplication.getContext(), Table.DB, null, 1).getWritableDatabase();
        String sql = "select * from " + Table.WORDBOOK + " where query = '" + word + "'";
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
     * 删除一个单词
     *
     * @param word
     */
    public void deleteWord(String word) {
        SQLiteDatabase db =
                new MyDBHelper(MyApplication.getContext(), Table.DB, null, 1).getWritableDatabase();
        db.execSQL("delete from " + Table.WORDBOOK + " where query = '" + word + "'");
        db.close();
    }

}
