package cn.dictionary.app.dictionary.view;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import cn.dictionary.app.dictionary.R;
import cn.dictionary.app.dictionary.config.Broadcast;
import cn.dictionary.app.dictionary.service.WordService;
import cn.dictionary.app.dictionary.db.SearchRecordDao;
import cn.dictionary.app.dictionary.db.WordBookDao;
import cn.dictionary.app.dictionary.entity.Words;
import cn.dictionary.app.dictionary.ui.RecordAndResultActivity;
import cn.dictionary.app.dictionary.utils.NetWorkUtil;
import cn.dictionary.app.dictionary.utils.WordUtil;

import static cn.dictionary.app.dictionary.service.WordService.sWord;

/**
 *
 */
public class ResultFragment extends Fragment implements View.OnClickListener {


    private View view;
    private Activity activity;
    private Words mWord = null;//存放单词数据的实例
    private ImageButton mAddWord;//添加单词的按钮
    private MediaPlayer mUK_MP = null;//播放英式发音
    private MediaPlayer mUS_MP = null;//播放美式发音
    private WordServiceCompleteReceiver mWordServiceCompleteReceiver;//查询单词完毕的广播
    private WordServiceisNullReceiver mWordServiceisNullReceiver;//查询不到输入的词汇
    private Intent startIntent;//开启查词后台服务的广播


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getCurrentActivity();
        IntentFilter mWordServiceCompleteIntent =
                new IntentFilter(Broadcast.WORDSERVICECOMPLETE);
        IntentFilter mWordServiceisNullIntent =
                new IntentFilter(Broadcast.WORDSERVICEISNULL);
        mWordServiceCompleteReceiver = new WordServiceCompleteReceiver();
        mWordServiceisNullReceiver = new WordServiceisNullReceiver();
        activity.registerReceiver(mWordServiceCompleteReceiver, mWordServiceCompleteIntent);
        activity.registerReceiver(mWordServiceisNullReceiver, mWordServiceisNullIntent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_result, container, false);
        initView();
        initEvent();
        return view;
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mUK_MP != null) {
            mUK_MP.release();
        }
        if (mUS_MP != null) {
            mUS_MP.release();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        activity.unregisterReceiver(mWordServiceCompleteReceiver);
        activity.unregisterReceiver(mWordServiceisNullReceiver);
    }

    /**
     * 获取与碎片相关的Activity
     *
     * @return 与碎片相关的Activity
     */
    public Activity getCurrentActivity() {
        RecordAndResultActivity activity = (RecordAndResultActivity) getActivity();
        return activity;
    }

    /**
     * 初始化UI控件
     */
    private void initView() {
        mAddWord = (ImageButton) view.findViewById(R.id.im_addWord);
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        mAddWord.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.im_addWord:
                if (WordBookDao.getInstance().queryWord(mWord.getQuery()) == null) {
                    WordUtil.saveWordToWordBook(mWord);
                    mAddWord.setBackgroundResource(R.drawable.im_addsuccess);
                } else {
                    WordUtil.deleteWordFromWordBook(mWord);
                    mAddWord.setBackgroundResource(R.drawable.im_addword);
                }
                break;
        }
    }


    /**
     * 对用户输入值进行转码并判断本地是否已有此单词
     * 若有，则直接使用本地的数据
     * 否则，发送查词请求
     * @param input 用户输入的关键词
     */
    public void handleInput(String input) {
        input = deleteContinuousSpace(input);
        //查询记录中是否存在此单词
        Words wr = SearchRecordDao.getInstance().queryWord(input);
        if (wr == null) {
            //有时候服务器返回查询值的与输入值不相等，例如小写变成大写，这里进行判断处理
            wr = SearchRecordDao.getInstance().queryWord(input.toUpperCase());
            if (wr == null) {
                //有时候服务器返回查询值的与输入值不相等，例如小写变成大写，这里进行判断处理
                wr = SearchRecordDao.getInstance().queryWord(input.toLowerCase());
            }
        }
        if (wr != null) {
            //本地有记录，直接显示
            showData(wr);
            //赋值给mWord，以便用户对它进行加入生词本和从生词本中删除等操作
            mWord = wr;
            return;
        }
        //查询单词本中是否存在此单词
        Words wwb = WordBookDao.getInstance().queryWord(input);
        if (wwb == null) {
            wwb = SearchRecordDao.getInstance().queryWord(input.toUpperCase());
            if (wwb == null) {
                wwb = SearchRecordDao.getInstance().queryWord(input.toLowerCase());
            }
        }
        if (wwb != null) {
            //单词本中存在，直接显示
            showData(wwb);
            //赋值给mWord，以便用户对它进行加入生词本和从生词本中删除等操作
            mWord = wwb;
            WordUtil.saveWordToRecord(wwb);
            return;
        }
        //单词本和搜索记录中都不存在，则进行联网查词
        if (NetWorkUtil.hasNetwork()) {
            //对用户输入的数据进行转码
            try {
                input = URLEncoder.encode(input, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            startIntent = new Intent(activity, WordService.class);
            startIntent.putExtra("input", input);
            activity.startService(startIntent);
        } else {
            Toast.makeText(activity, "网络不可用!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 删除字符串中的连续空格
     */
    private String deleteContinuousSpace(String input) {
        input=input.trim();
        int num = 0;//空格重复的次数
        int length = input.length();
        for (int i = 0; i < length - 2; i++) {
            if (input.charAt(i) == ' ' && input.charAt(i) == input.charAt(i + 1)) {
                int j = i;
                do {
                    num++;
                    j++;
                } while (input.charAt(j) == input.charAt(j - 1));
                input = input.substring(0, i + 1) + input.substring(j);
                length -= num;
                num = 0;
            }
        }
        return input;
    }

    /**
     * 若用户输入第一个字母是否为为英文,若是则显示单词本添加键
     *
     * @param input 用户的输入值
     */
    private void isShowAddBtn(String input) {
        for (char c : input.toCharArray()) {
            if (c >= 0x4E00 && c <= 0x9FA5) {
                //包含中文，直接不显示添加单词本的按钮
                return;
            }
        }
        //进一步判断是否显示添加单词的按钮
        isExist(input);
    }

    /**
     * 判断单词本中是否存在该单词
     * 若不存在，则显示添加按钮,存在,则更新按钮图片
     *
     * @param input 用户输入的值
     */
    private void isExist(String input) {
        if (WordBookDao.getInstance().queryWord(input) != null) {
            //显示单词本控件
            mAddWord.setBackgroundResource(R.drawable.im_addsuccess);
            mAddWord.setVisibility(View.VISIBLE);
        } else {
            mAddWord.setVisibility(View.VISIBLE);
        }

    }


    /**
     * 显示单词数据
     *
     * @param word 装有数据的单词实例
     */
    public void showData(Words word) {
        //判断添加单词按钮的状态
        isShowAddBtn(word.getQuery());
        //关键字
        if (word.getQuery() != null) {
            TextView query = (TextView) view.findViewById(R.id.tv_query);
            query.setText(word.getQuery());
        }
        //有道翻译
        if (word.getTranslation() != null) {
            view.findViewById(R.id.layout_translation).setVisibility(View.VISIBLE);
            TextView translation = (TextView) view.findViewById(R.id.tv_translation);
            translation.setText(word.getTranslation());
        } else {
            view.findViewById(R.id.layout_translation).setVisibility(View.GONE);
        }
        //英式音标
        if (word.getUk_phonetic() != null) {
            view.findViewById(R.id.layout_speech_Phonetic).setVisibility(View.VISIBLE);
            view.findViewById(R.id.uk).setVisibility(View.VISIBLE);
            TextView uk_phonetic = (TextView) view.findViewById(R.id.tv_uk_phonetic);
            uk_phonetic.setVisibility(View.VISIBLE);
            uk_phonetic.setText(word.getUk_phonetic());
        } else {
            view.findViewById(R.id.uk).setVisibility(View.GONE);
            view.findViewById(R.id.tv_uk_phonetic).setVisibility(View.GONE);
        }
        //英式发音
        if (word.getUk_speech() != null) {
            view.findViewById(R.id.layout_speech_Phonetic).setVisibility(View.VISIBLE);
            final ImageButton uk_speech = (ImageButton) view.findViewById(R.id.im_uk_speech);
            uk_speech.setVisibility(View.VISIBLE);
            mUK_MP = new MediaPlayer();
            try {
                mUK_MP.setDataSource(word.getUk_speech());
                mUK_MP.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            uk_speech.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mUK_MP.start();
                }
            });
            uk_speech.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            uk_speech.setBackgroundResource(R.drawable.im_voice_pressed);
                            break;
                        case MotionEvent.ACTION_UP:
                            uk_speech.setBackgroundResource(R.drawable.im_voice_normal);
                            break;
                    }
                    return false;
                }
            });
        } else {
            view.findViewById(R.id.im_uk_speech).setVisibility(View.GONE);
            view.findViewById(R.id.layout_speech_Phonetic).setVisibility(View.GONE);
        }
        //美式音标
        if (word.getUs_phonetic() != null) {
            view.findViewById(R.id.layout_speech_Phonetic).setVisibility(View.VISIBLE);
            view.findViewById(R.id.us).setVisibility(View.VISIBLE);
            TextView us_phonetic = (TextView) view.findViewById(R.id.tv_us_phonetic);
            us_phonetic.setVisibility(View.VISIBLE);
            us_phonetic.setText(word.getUs_phonetic());
        } else {
            view.findViewById(R.id.us).setVisibility(View.GONE);
            view.findViewById(R.id.tv_us_phonetic).setVisibility(View.GONE);
        }
        //美式发音
        if ((word.getUs_speech() != null) && (word.getUs_phonetic() != null)) {
            view.findViewById(R.id.layout_speech_Phonetic).setVisibility(View.VISIBLE);
            final ImageButton us_speech = (ImageButton) view.findViewById(R.id.im_us_speech);
            us_speech.setVisibility(View.VISIBLE);
            mUS_MP = new MediaPlayer();
            try {
                mUS_MP.setDataSource(word.getUs_speech());
                mUS_MP.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            us_speech.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mUS_MP.start();
                }
            });
            us_speech.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            us_speech.setBackgroundResource(R.drawable.im_voice_pressed);
                            break;
                        case MotionEvent.ACTION_UP:
                            us_speech.setBackgroundResource(R.drawable.im_voice_normal);
                            break;
                    }
                    return false;
                }
            });
        } else {
            view.findViewById(R.id.im_us_speech).setVisibility(View.GONE);
        }
        //基本释义
        if (word.getExplainsAfterDeal() != null) {
            view.findViewById(R.id.layout_explains).setVisibility(View.VISIBLE);
            TextView explains = (TextView) view.findViewById(R.id.tv_explains);
            //传入拼接处理后的基本释义
            explains.setText(word.getExplainsAfterDeal());
        } else {
            view.findViewById(R.id.layout_explains).setVisibility(View.GONE);
        }
        //网络释义
        if (word.getWebsAfterDeal() != null) {
            view.findViewById(R.id.layout_web).setVisibility(View.VISIBLE);
            TextView webs = (TextView) view.findViewById(R.id.tv_web);
            //传入拼接处理后的网络释义
            webs.setText(word.getWebsAfterDeal());
        } else {
            view.findViewById(R.id.layout_web).setVisibility(View.GONE);
        }

    }


    /**
     * 查询单词完毕的广播
     */
    private class WordServiceCompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            activity.stopService(startIntent);
            mWord = sWord;
            showData(mWord);
        }
    }

    /**
     * 查询不到输入的词汇的广播
     */
    private class WordServiceisNullReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            activity.stopService(startIntent);
            TextView tv = (TextView) view.findViewById(R.id.tv_query);
            tv.setText("找不到该词汇！");
        }
    }


}
