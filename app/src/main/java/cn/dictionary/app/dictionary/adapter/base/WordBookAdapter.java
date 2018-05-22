package cn.dictionary.app.dictionary.adapter.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.List;

import cn.dictionary.app.dictionary.R;
import cn.dictionary.app.dictionary.entity.Words;
import cn.dictionary.app.dictionary.widget.IndexScroller;

/**
 * 单词本的适配器
 */

public class WordBookAdapter extends ArrayAdapter<Words> implements SectionIndexer {

    private int mtextViewResourecId;//子项布局id
    private Context mcontext;
    private List<Words> mwordsList;//数据源
    private String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public WordBookAdapter(Context context, int resource, int textViewResourceId, List<Words> objects) {
        super(context, resource, textViewResourceId, objects);
        mcontext = context;
        mwordsList = objects;
        mtextViewResourecId = textViewResourceId;
    }

    @Override
    public int getCount() {
        if (mwordsList == null) {
            return 0;
        } else {
            return mwordsList.size();
        }
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Words word = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(mcontext).inflate(mtextViewResourecId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tv1 = (TextView) view.findViewById(R.id.tv_word_item);
            viewHolder.tv2 = (TextView) view.findViewById(R.id.tv_wordbook_explain_item);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.tv1.setText(word.getQuery());
        if (word.getExplainsAfterDeal() != null) {
            viewHolder.tv2.setText(word.getExplainsAfterDeal());
        } else {
            viewHolder.tv2.setText(word.getTranslation());
        }
        return view;
    }

    private class ViewHolder {
        TextView tv1;
        TextView tv2;
    }
    @Override
    public Object[] getSections() {
        String[] sections = new String[mSections.length()];
        for (int i = 0; i < mSections.length(); i++) {
            sections[i] = String.valueOf(mSections.charAt(i));
        }
        return sections;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        //从sectionIndex往前查，一直到遇到有对应的item为止，否则不进行定位
        for (int i = sectionIndex; i >= 0; i--) {
            //要返回第一个匹配的item，所以从0开始
            for (int j = 0; j < getCount(); j++) {
                if (getItem(j).getQuery().toUpperCase().charAt(0) == mSections.charAt(sectionIndex)) {
                    return j;
                }
            }
        }
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }


}
