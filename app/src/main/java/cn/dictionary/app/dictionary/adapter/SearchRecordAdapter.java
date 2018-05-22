package cn.dictionary.app.dictionary.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import cn.dictionary.app.dictionary.R;
import cn.dictionary.app.dictionary.entity.Words;

/**
 * 搜索记录适配器
 */

public class SearchRecordAdapter extends ArrayAdapter<Words> {

    private int mtextViewResourecId;
    private Context mcontext;
    private List<Words> mwordsList;


    public SearchRecordAdapter(Context context, int resource, int textViewResourceId, List<Words> objects) {
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
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Words word = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(mcontext).inflate(mtextViewResourecId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.word = (TextView) view.findViewById(R.id.tv_record_item_word);
            viewHolder.explains = (TextView) view.findViewById(R.id.tv_record_item_explains);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.word.setText(word.getQuery());
        if (word.getExplainsAfterDeal() != null) {
            viewHolder.explains.setText(word.getExplainsAfterDeal());
        }
        return view;
    }

    private class ViewHolder {
        TextView word;
        TextView explains;
    }
}