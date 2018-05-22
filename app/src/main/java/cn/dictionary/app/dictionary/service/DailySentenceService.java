package cn.dictionary.app.dictionary.service;

import android.app.Service;
import android.content.Intent;

import android.os.IBinder;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import cn.dictionary.app.dictionary.config.Broadcast;
import cn.dictionary.app.dictionary.config.HttpPath;
import cn.dictionary.app.dictionary.db.DailySentenceDao;
import cn.dictionary.app.dictionary.entity.DailySentence;
import cn.dictionary.app.dictionary.https.HttpCallbackListener;
import cn.dictionary.app.dictionary.https.HttpCallbackListenerForImage;
import cn.dictionary.app.dictionary.utils.SDUtil;
import cn.dictionary.app.dictionary.utils.HttpUtil;
import cn.dictionary.app.dictionary.application.MyApplication;
import cn.dictionary.app.dictionary.utils.ParserUtil;

public class DailySentenceService extends Service {

    private String[] mDate;


    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //联网获取每日一句
        //先获取5天的日期
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        mDate = new String[5];
        for (int i = 0; i < 5; i++) {
            mDate[i] = sf.format(c.getTime());
            c.add(Calendar.DAY_OF_MONTH, -1);
        }
        List<DailySentence> dailySentenceList = DailySentenceDao.getInstance().queryAllDailySentence();
        if (dailySentenceList == null || (dailySentenceList.size() < 5)) {
            //本地的每日一句不足5天，则联网获取5天的每日一句
            for (int i = 0; i < 5; i++) {
                //5次联网获取数据
                HttpUtil.getHttp().sendHttpRequestForDailyEnglish(HttpPath.PATH_DAILYENGLISH + mDate[i], httpCallbackListenForDailyEnglish);
            }
        } else {
            //本地有5天的每日一句，联网更新一天即可
            HttpUtil.getHttp().sendHttpRequestForDailyEnglish(HttpPath.PATH_DAILYENGLISH, httpCallbackListenForDailyEnglish);
        }
        return super.onStartCommand(intent, flags, startId);
    }


    /**
     * 每日一句
     * 根据服务器响应结果做出逻辑判断
     */
    HttpCallbackListener httpCallbackListenForDailyEnglish = new HttpCallbackListener() {

        @Override
        public void onFinish(String response) {
            //请求成功
            //开始解析数据
            DailySentence ds = ParserUtil.ParseJSONForDailyEnglish(response);
            if (ds != null)
                if (ds.getPicturePath() != null) {
                    //解析图片
                    HttpUtil.getHttp().sendHttpRequestForImage(ds, httpCallbackListenForImage);
                }
        }

        @Override
        public void onError(int resultCode) {
            Toast.makeText(MyApplication.getContext(), "获取每日一句出错：" + resultCode, Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 每日一句的图片
     * 根据服务器响应结果做出逻辑判断
     */
    HttpCallbackListenerForImage httpCallbackListenForImage = new HttpCallbackListenerForImage() {

        @Override
        public void onFinish(DailySentence dailySentence) {
            if (DailySentenceDao.getInstance().queryDailySentence(dailySentence.getDateline()) == null) {
                //此本地不存在此日期的每日一句
                //删除本地保存最早的的一张图片
                DailySentenceDao.getInstance().deleteDailySentence(mDate[4]);
                SDUtil.getHanderSD().deleteImg(mDate[4] + ".png");
                //并保存这张新图片
                String imgName = dailySentence.getDateline() + ".png";
                SDUtil.getHanderSD().saveImg(imgName, dailySentence.getPicture());
                dailySentence.setPicturePath(SDUtil.getHanderSD().getDailySentenceImgAddress(imgName));
                DailySentenceDao.getInstance().addDailySentence(dailySentence);
            } else {
                //本地存在此日期的每日一句
                //在SD卡和数据库更新这张新图片
                String imgName = dailySentence.getDateline() + ".png";
                SDUtil.getHanderSD().saveImg(imgName, dailySentence.getPicture());
                dailySentence.setPicturePath(SDUtil.getHanderSD().getDailySentenceImgAddress(imgName));
                DailySentenceDao.getInstance().updateDailySentence(dailySentence);
            }
            //判断是否发送完成广播
            List<DailySentence> dailySentenceList = DailySentenceDao.getInstance().queryAllDailySentence();
            if (dailySentenceList.size() < 5) {
                //若本地的每日一句不足5天，不发送广播
                return;
            }
            for (int i = 0; i < dailySentenceList.size(); i++) {
                String s = dailySentenceList.get(i).getDateline();
                if (s.equals(mDate[0])) {
                    //如果当前日期存在，则发送任务完成广播
                    Intent intent = new Intent(Broadcast.DAILYSENTENCESERVICE_COMPLETE);
                    sendBroadcast(intent);
                    return;
                }
            }
        }

        @Override
        public void onError(int resultCode) {
            Toast.makeText(MyApplication.getContext(), "获取图片出错：" + resultCode, Toast.LENGTH_SHORT).show();
        }
    };
}
