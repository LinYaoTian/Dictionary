package cn.dictionary.app.dictionary.view;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.dictionary.app.dictionary.R;
import cn.dictionary.app.dictionary.adapter.SearchRecordAdapter;
import cn.dictionary.app.dictionary.db.SearchRecordDao;
import cn.dictionary.app.dictionary.entity.Words;
import cn.dictionary.app.dictionary.ui.RecordAndResultActivity;
import cn.dictionary.app.dictionary.utils.SDUtil;
import cn.dictionary.app.dictionary.application.MyApplication;

/**
 *
 */
public class RecordFragment extends Fragment implements View.OnClickListener {


    private ListView mListView;//显示搜索记录
    private LinearLayout mListViewLayout;//包含ListView的布局
    private List<Words> mWordsList;//数据源
    private ImageView mClear;//清除历史记录键
    private SearchRecordAdapter mRecordAdapter;//自定义的ArrayAdapter适配器
    private View view;
    private RecordAndResultActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (RecordAndResultActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_record, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initView();
        initEvent();
    }

    /**
     * 初始化UI控件
     */
    private void initView() {
        //加载数据源
        mWordsList = SearchRecordDao.getInstance().queryAllWord();
        if (mWordsList == null) {
            mWordsList = new ArrayList<Words>();
        }
        mRecordAdapter = new SearchRecordAdapter(MyApplication.getContext(), R.id.layout_ListView,
                R.layout.item_record_listview, mWordsList);
        mListViewLayout = (LinearLayout) view.findViewById(R.id.layout_ListView);
        if (mRecordAdapter.getCount() == 0) {
            mListViewLayout.setVisibility(View.GONE);
            mRecordAdapter.notifyDataSetChanged();
        }
        mListView = (ListView) view.findViewById(R.id.lv_Record);
        mListView.setAdapter(mRecordAdapter);
        mClear = (ImageView) view.findViewById(R.id.tv_delete);
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        mClear.setOnClickListener(this);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //把点击的单词设为搜索关键字
                Words word = mWordsList.get(position);
                activity.setInput(word.getQuery());
                activity.replaceFragment(new ResultFragment());
                activity.mResultFragment.handleInput(word.getQuery());
            }
        });
        activity.mInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    //从SearchRecord表中模糊查询
                    List<Words> list = SearchRecordDao.getInstance().fuzzyQuery(s.toString());
                    if (list != null) {
                        mWordsList.clear();
                        mWordsList.addAll(list);
                        mRecordAdapter.notifyDataSetChanged();
                    }
                } else {
                    //编辑框为空，则显示所有记录
                    List<Words> list = SearchRecordDao.getInstance().queryAllWord();
                    mWordsList.clear();
                    if (list != null) {
                        mWordsList.addAll(list);
                    }
                    mRecordAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //删除全部记录
            case R.id.tv_delete:
                SearchRecordDao.getInstance().deleteAllWord();
                SDUtil.getHanderSD().deleteAllRecordVoice();
                mWordsList.clear();
                mRecordAdapter.notifyDataSetChanged();
                mListViewLayout.setVisibility(View.GONE);
                break;
        }

    }

}
