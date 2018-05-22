package cn.dictionary.app.dictionary.adapter;

import android.content.Context;

import java.util.List;

import cn.dictionary.app.dictionary.adapter.base.WordBookAdapter;
import cn.dictionary.app.dictionary.entity.Words;

/**
 * 最近添加的单词的ListView适配器
 */

public class RecentWordAdapter extends WordBookAdapter {

    public RecentWordAdapter(Context context, int resource, int textViewResourceId, List<Words> objects) {
        super(context, resource, textViewResourceId, objects);
    }


}
