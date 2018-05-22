package cn.dictionary.app.dictionary.utils;

import cn.dictionary.app.dictionary.db.SearchRecordDao;
import cn.dictionary.app.dictionary.db.WordBookDao;
import cn.dictionary.app.dictionary.entity.Words;

/**
 * 对单词操作的工具类
 */

public class WordUtil {

    /**
     * 保存单词到单词本
     *
     * @param word 单词对象
     */
    public static void saveWordToWordBook(Words word) {
        //添加此单词进单词本
        //音频文件名
        String uk_voiceName = "uk_" + word.getQuery() + ".mp3";
        String us_voiceName = "us_" + word.getQuery() + ".mp3";
        //获取音频在音频记录中的地址
        String olderPath_us = SDUtil.getHanderSD().
                getRecordVoiceAddress(us_voiceName);
        String olderPath_uk = SDUtil.getHanderSD().
                getRecordVoiceAddress(uk_voiceName);
        //将音频文件复制到WordBook目录中并更新Word对象中的地址
        if (olderPath_uk != null) {
            SDUtil.getHanderSD().copyToWordBook(olderPath_uk, uk_voiceName);
            word.setUk_speech(SDUtil.getHanderSD().
                    getWordBookVoiceAddress(uk_voiceName));
        }
        if (olderPath_us != null) {
            SDUtil.getHanderSD().copyToWordBook(olderPath_us, us_voiceName);
            word.setUs_speech(SDUtil.getHanderSD().
                    getWordBookVoiceAddress(us_voiceName));
        }
        //保存到WordBook表中
        WordBookDao.getInstance().addWord(word);
    }

    /**
     * 把单词从单词本中删除
     *
     * @param word 单词对象
     */
    public static void deleteWordFromWordBook(Words word) {
        //在数据库中删除此单词
        WordBookDao.getInstance().deleteWord(word.getQuery());
        //在SD卡中删除此单词的音频文件
        String uk_voiceName = "uk_" + word.getQuery() + ".mp3";
        String us_voiceName = "us_" + word.getQuery() + ".mp3";
        SDUtil.getHanderSD().deleteWordBookVoice(uk_voiceName);
        SDUtil.getHanderSD().deleteWordBookVoice(us_voiceName);
    }

    /***
     * 保存单词到搜索记录中
     *
     * @param word 单词对象
     */
    public static void saveWordToRecord(Words word) {
        //添加单词到记录
        //文件名
        String uk_voiceName = "uk_" + word.getQuery() + ".mp3";
        String us_voiceName = "us_" + word.getQuery() + ".mp3";
        //获取单词在WordVoice文件夹中的路径
        String olderPath_us = SDUtil.getHanderSD().
                getWordBookVoiceAddress(us_voiceName);
        String olderPath_uk = SDUtil.getHanderSD().
                getWordBookVoiceAddress(uk_voiceName);
        SDUtil.getHanderSD().copyToRecord(olderPath_us, us_voiceName);
        SDUtil.getHanderSD().copyToRecord(olderPath_uk, uk_voiceName);
        word.setUk_speech(SDUtil.getHanderSD().getRecordVoiceAddress(uk_voiceName));
        word.setUs_speech(SDUtil.getHanderSD().getRecordVoiceAddress(us_voiceName));
        SearchRecordDao.getInstance().addWord(word);
    }


}
