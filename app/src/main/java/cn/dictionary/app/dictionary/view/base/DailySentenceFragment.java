package cn.dictionary.app.dictionary.view.base;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import cn.dictionary.app.dictionary.R;
import cn.dictionary.app.dictionary.db.DailySentenceDao;
import cn.dictionary.app.dictionary.entity.DailySentence;
import cn.dictionary.app.dictionary.ui.MainActivity;
import cn.dictionary.app.dictionary.utils.DailySentenceUtil;
import cn.dictionary.app.dictionary.utils.NetWorkUtil;

/**
 *
 */
public class DailySentenceFragment extends Fragment implements View.OnClickListener {


    private View view;
    private DailySentence mdailySentence = null;//储存每日一句的信息数据
    private int position = 0;//每日一句的位置
    private MainActivity activity;
    private int flag = 0;//判断音频是否装载，0表示未加载，1表示已经加载
    public MediaPlayer mediaPlayer;//装载音频

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dailysentence, container, false);
        List<DailySentence> dailySentenceList = DailySentenceDao.getInstance().queryAllDailySentence();
        if (dailySentenceList != null && (dailySentenceList.size() >= 5)) {
            //若数据库有五天的每日一句，则直接使用
            mdailySentence = getDailySentence(position);
            initView(mdailySentence);
        } else {
            //否则，显示默认的图片
            ImageView dailySentencePicture = (ImageView) view.findViewById(R.id.iv_dailyPicture);
            dailySentencePicture.setImageResource(R.drawable.iv_default);
        }
        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mdailySentence != null) {
            if (mdailySentence.getVoice() != null) {
                mdailySentence.getVoice().release();
            }
        }
    }

    /**
     * 设置每日一句的位置
     *
     * @param i 位置0-4
     */
    public void setPosition(int i) {
        position = i;
    }

    /**
     * 获取特定位置的每日一句
     *
     * @param position 位置0-4
     * @return
     */
    protected DailySentence getDailySentence(int position) {
        List<DailySentence> dailySentenceList = DailySentenceUtil.sortDailySentence();
        return dailySentenceList.get(position);
    }

    /**
     * 初始化控件
     *
     * @param dailySentence
     */
    private void initView(DailySentence dailySentence) {
        //图片
        if (dailySentence.getPicturePath() != null) {
            view.findViewById(R.id.layout_dailySentence).setVisibility(View.VISIBLE);
            ImageView dailyPicture = (ImageView) view.findViewById(R.id.iv_dailyPicture);
            Bitmap bitmap = BitmapFactory.decodeFile(dailySentence.getPicturePath());
            dailyPicture.setImageBitmap(bitmap);
        } else {
            view.findViewById(R.id.layout_dailySentence).setVisibility(View.GONE);
        }
        //中文
        if (dailySentence.getChineseContent() != null) {
            TextView dailyChinese = (TextView) view.findViewById(R.id.tv_dailyChinese);
            dailyChinese.setText(dailySentence.getChineseContent());
        } else {
            view.findViewById(R.id.layout_dailySentence).setVisibility(View.GONE);
        }
        //英文
        if (dailySentence.getEnglishContent() != null) {
            TextView dailyEnglish = (TextView) view.findViewById(R.id.tv_dailyEnglish);
            dailyEnglish.setText(dailySentence.getEnglishContent());
        } else {
            view.findViewById(R.id.layout_dailySentence).setVisibility(View.GONE);
        }
        //时间
        if (dailySentence.getDateline() != null) {
            TextView dateline = (TextView) view.findViewById(R.id.tv_dateline);
            dateline.setText(dailySentence.getDateline());
        } else {
            view.findViewById(R.id.layout_dailySentence).setVisibility(View.GONE);
        }

        //音频
        if (dailySentence.getVoicePath() != null) {
            final ImageButton voice = (ImageButton) view.findViewById(R.id.im_voice);
            voice.setVisibility(View.VISIBLE);
            dailySentence.setVoice(new MediaPlayer());
            mediaPlayer = dailySentence.getVoice();
            try {
                mediaPlayer.setDataSource(dailySentence.getVoicePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            voice.setOnClickListener(this);
            voice.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            voice.setBackgroundResource(R.drawable.im_voice_pressed);
                            break;
                        case MotionEvent.ACTION_UP:
                            voice.setBackgroundResource(R.drawable.im_voice_normal);
                            break;
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.im_voice:
                //加载音频
                if (flag == 0) {
                    if (NetWorkUtil.hasNetwork()) {
                        Toast.makeText(activity, "正在联网获取音频，请稍候再次点击播放！", Toast.LENGTH_SHORT).show();
                        mediaPlayer.prepareAsync();
                        flag = 1;//设置标志值
                        break;
                    }
                }
                //如果音频已经加载
                if (flag == 1) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.seekTo(0);
                        mediaPlayer.pause();
                    } else {
                        mediaPlayer.start();
                    }
                } else if (flag == 0) {
                    //flag==0说明无网络
                    Toast.makeText(activity, "网络不可用，无法播放音频！", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
