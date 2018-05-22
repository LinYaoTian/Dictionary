package cn.dictionary.app.dictionary.entity;

import android.graphics.Bitmap;
import android.media.MediaPlayer;

/**
 * 每日一句英语
 */

public class DailySentence {

    private String voicePath;//音频的地址

    private MediaPlayer voice;//音频

    private String englishContent;//英文内容

    private String chineseContent;//中文内容

    private String picturePath;//图片的地址

    private Bitmap picture;//图片

    private String dateline;//时间

    public DailySentence() {
        this.voicePath = null;
        this.englishContent = null;
        this.chineseContent = null;
        this.picturePath = null;
        this.dateline = null;
        this.picture = null;
    }


    public MediaPlayer getVoice() {
        return voice;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public String getVoicePath() {
        return voicePath;
    }

    public String getEnglishContent() {
        return englishContent;
    }

    public String getChineseContent() {
        return chineseContent;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public String getDateline() {
        return dateline;
    }

    public void setVoice(MediaPlayer voice) {
        this.voice = voice;
    }

    public void setVoicePath(String voicePath) {
        this.voicePath = voicePath;
    }

    public void setEnglishContent(String englishContent) {
        this.englishContent = englishContent;
    }

    public void setChineseContent(String chineseContent) {
        this.chineseContent = chineseContent;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }
}
