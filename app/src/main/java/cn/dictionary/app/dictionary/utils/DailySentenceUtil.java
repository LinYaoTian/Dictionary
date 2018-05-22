package cn.dictionary.app.dictionary.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import cn.dictionary.app.dictionary.db.DailySentenceDao;
import cn.dictionary.app.dictionary.entity.DailySentence;



public class DailySentenceUtil {

    public static List<DailySentence> sortDailySentence() {
        List<DailySentence> dailySentenceList = DailySentenceDao.getInstance().queryAllDailySentence();
        if (dailySentenceList != null) {
            //定义一个Comparator,根据每日一句的日期比较大小
            Comparator<DailySentence> comparator = new Comparator<DailySentence>() {
                @Override
                public int compare(DailySentence o1, DailySentence o2) {
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Date dt1 = df.parse(o1.getDateline());
                        Date dt2 = df.parse(o2.getDateline());
                        if (dt1.getTime() > dt2.getTime()) {
                            //日期大，即日期较早，应该排后边
                            return -1;
                        } else if (dt1.getTime() < dt2.getTime()) {
                            //日期小，即日期较晚，应该排前边
                            return 1;
                        } else {
                            return 0;
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    return 0;
                }
            };
            Collections.sort(dailySentenceList, comparator);
            return dailySentenceList;
        }
        return new ArrayList<>();
    }



}
