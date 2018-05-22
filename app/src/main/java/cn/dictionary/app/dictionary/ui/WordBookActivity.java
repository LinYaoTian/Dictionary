package cn.dictionary.app.dictionary.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import cn.dictionary.app.dictionary.R;
import cn.dictionary.app.dictionary.adapter.base.WordBookAdapter;
import cn.dictionary.app.dictionary.config.Table;
import cn.dictionary.app.dictionary.db.WordBookDao;
import cn.dictionary.app.dictionary.entity.Words;
import cn.dictionary.app.dictionary.utils.WordUtil;
import cn.dictionary.app.dictionary.widget.IndexableListView;

public class WordBookActivity extends AppCompatActivity implements View.OnClickListener {

    private IndexableListView mWordsListView;
    private Button mSortByLetter;//字母排序按钮
    private Button mSortByDefault;//默认排序按钮
    private Button mExplains;//隐藏或者显示释义
    private WordBookAdapter mWordBookAdapter;
    private List<Words> mWordsList;
    private ImageView mBack;//返回键
    private AlertDialog.Builder mDialog;//长按单词弹出的对话框
    private boolean mIsDefault = true;//判断当前单词列表是否是默认排序
    private Comparator<Words> mCompareByDefault;//单词列表的默认排序方法
    private Comparator<Words> mCompareByLetter;//单词列表的按字母排序方法

    private static final String SHOWEXPLAINS = "显示释义";
    private static final String HIDEEXPLAINS = "隐藏释义";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordbook);
        initView();
        initEvent();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //加载数据源
        List<Words> list = WordBookDao.getInstance().queryAllWords();
        if (list == null) {
            mWordsList.clear();
            mWordBookAdapter.notifyDataSetChanged();
        } else {
            if (mWordsList.size() != list.size() || (!mWordsList.containsAll(list))) {
                //若数据源发生变化，则重新加载数据源
                mWordsList.clear();
                mWordsList.addAll(list);
                mWordBookAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        mWordsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Words word = mWordsList.get(position);
                Intent intent = new Intent(WordBookActivity.this, RecordAndResultActivity.class);
                intent.putExtra("input", word.getQuery());
                intent.putExtra("from", Table.WORDBOOK);
                startActivity(intent);
            }
        });
        mWordsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                mDialog = new AlertDialog.Builder(WordBookActivity.this);
                mDialog.setMessage("是否删除此单词？");
                mDialog.setCancelable(false);
                mDialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        WordUtil.deleteWordFromWordBook(mWordsList.get(position));
                        mWordsList.remove(position);
                        mWordBookAdapter.notifyDataSetChanged();
                    }
                });
                mDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                mDialog.show();
                return true;
            }
        });
        mBack.setOnClickListener(this);
        mSortByLetter.setOnClickListener(this);
        mSortByDefault.setOnClickListener(this);
        mExplains.setOnClickListener(this);
        mBack.setOnClickListener(this);



        mCompareByDefault = new Comparator<Words>() {
            @Override
            public int compare(Words o1, Words o2) {
                int flag = o1.getOrder_number() > o2.getOrder_number() ? 1 : 0;
                if (flag == 0) {
                    flag = o1.getOrder_number() < o2.getOrder_number() ? -1 : 0;
                }
                return flag;
            }
        };
        mCompareByLetter = new Comparator<Words>() {
            @Override
            public int compare(Words o1, Words o2) {
                //将单词转换成大写后再进行比较
                return o1.getQuery().toUpperCase().compareTo(o2.getQuery().toUpperCase());
            }
        };
    }


    /**
     * 初始化UI控件
     */
    private void initView() {
        mSortByLetter = (Button) findViewById(R.id.btn_sortByLetter);
        mSortByDefault = (Button) findViewById(R.id.btn_sortByDefault);
        mExplains = (Button) findViewById(R.id.btn_explains);
        mBack = (ImageView) findViewById(R.id.im_back_Wordbook);

        mWordsList = WordBookDao.getInstance().queryAllWords();
        if (mWordsList == null) {
            mWordsList = new ArrayList<>();
            mSortByLetter.setVisibility(View.GONE);
            mSortByDefault.setVisibility(View.GONE);
            mExplains.setVisibility(View.GONE);

        }
        mWordBookAdapter = new WordBookAdapter(WordBookActivity.this,
                R.id.lv_RecentWord, R.layout.item_wordbook_listview, mWordsList);
        mWordsListView = (IndexableListView) findViewById(R.id.lv_RecentWord);
        mWordsListView.setAdapter(mWordBookAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sortByLetter:
                mWordBookAdapter.sort(mCompareByLetter);
                mWordsListView.setFastScrollEnabled(true);
                mWordBookAdapter.notifyDataSetChanged();
                mIsDefault = false;
                break;
            case R.id.im_back_Wordbook:
                Intent intent = new Intent(WordBookActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_sortByDefault:
                mWordBookAdapter.sort(mCompareByDefault);
                mWordsListView.setFastScrollEnabled(false);
                mWordBookAdapter.notifyDataSetChanged();
                mIsDefault = true;
                break;
            case R.id.btn_explains:
                if (mExplains.getText().toString().equals(HIDEEXPLAINS)) {
                    mExplains.setText(SHOWEXPLAINS);
                    List<Words> list = WordBookDao.getInstance().queryAllWordKey();
                    mWordsList.clear();
                    mWordsList.addAll(list);
                    if (mIsDefault) {
                        mWordBookAdapter.sort(mCompareByDefault);
                    } else {
                        mWordBookAdapter.sort(mCompareByLetter);
                    }
                    mWordBookAdapter.notifyDataSetChanged();
                } else {
                    mExplains.setText(HIDEEXPLAINS);
                    List<Words> list = WordBookDao.getInstance().queryAllWords();
                    mWordsList.clear();
                    mWordsList.addAll(list);
                    if (mIsDefault) {
                        mWordBookAdapter.sort(mCompareByDefault);
                    } else {
                        mWordBookAdapter.sort(mCompareByLetter);
                    }
                    mWordBookAdapter.notifyDataSetChanged();
                }
        }
    }
}