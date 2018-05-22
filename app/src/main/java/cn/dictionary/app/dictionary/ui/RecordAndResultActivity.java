package cn.dictionary.app.dictionary.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.List;

import cn.dictionary.app.dictionary.R;
import cn.dictionary.app.dictionary.config.Table;
import cn.dictionary.app.dictionary.utils.NetWorkUtil;
import cn.dictionary.app.dictionary.view.RecordFragment;
import cn.dictionary.app.dictionary.view.ResultFragment;
import cn.dictionary.app.dictionary.widget.ClearEditText;

public class RecordAndResultActivity extends AppCompatActivity implements View.OnClickListener {

    public ClearEditText mInput;//搜索框
    private ImageButton mBack;//返回键
    private ImageButton mSearch;//搜索键
    public ResultFragment mResultFragment;//显示搜索结果的碎片


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_and_result);
        replaceFragment(new RecordFragment());
        initView();
        initEvent();

    }


    @Override
    public void onStart() {
        super.onStart();
        Intent intent = getIntent();
        String input = intent.getStringExtra("input");
        if (input != null) {
            mInput.setText(input);
        }
        if (!TextUtils.isEmpty(mInput.getText())) {
            replaceFragment(new ResultFragment());
            mResultFragment.handleInput(mInput.getText().toString());
        }
    }

    /**
     * 切换视图
     *
     * @param fragment
     */
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.ResultOrRecord_container, fragment);
        transaction.commitNow();
        if (fragment instanceof ResultFragment) {
            mResultFragment = (ResultFragment) getSupportFragmentManager().findFragmentById(R.id.ResultOrRecord_container);
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mBack = (ImageButton) findViewById(R.id.bt_wordbook);
        mSearch = (ImageButton) findViewById(R.id.im_query);
        mInput = (ClearEditText) findViewById(R.id.et_input_record);
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        mBack.setOnClickListener(this);
        mBack.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mBack.setBackgroundResource(R.drawable.im_back_pressed);
                        break;
                    case MotionEvent.ACTION_UP:
                        mBack.setBackgroundResource(R.drawable.im_back_normal);
                        break;
                }
                return false;
            }
        });
        mSearch.setOnClickListener(this);
        mSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mSearch.setBackgroundResource(R.drawable.im_querypressed);
                        break;
                    case MotionEvent.ACTION_UP:
                        mSearch.setBackgroundResource(R.drawable.im_query);
                        break;
                }
                return false;
            }
        });
        mInput.setOnClickListener(this);

    }

    /**
     * 将点击的单词设置到输入框中
     *
     * @param word
     */
    public void setInput(String word) {
        mInput.setText(word);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回上一个活动
            case R.id.bt_wordbook:
                Intent intent = getIntent();
                if (intent.hasExtra("from")) {
                    if (intent.getStringExtra("from").equals(Table.WORDBOOK)) {
                        //上一个活动为单词本活动，故返回单词本活动
                        Intent intent_1 = new Intent(this, WordBookActivity.class);
                        startActivity(intent_1);
                    }
                } else {
                    Intent intent_1 = new Intent(this, MainActivity.class);
                    startActivity(intent_1);
                }
                break;
            //搜索单词
            case R.id.im_query:
                //判断后台是否正在执行查词操作
                if (!isServiceWork(this, "cn.dictionary.app.dictionary.service.WordService")) {
                    //后台没有查词操作，继续判断输入是否为空
                    if (!TextUtils.isEmpty(mInput.getText().toString())) {
                        //关闭或开启软键盘
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                        //切换到查词结果页面
                        replaceFragment(new ResultFragment());
                        mResultFragment.handleInput(mInput.getText().toString().trim());
                    } else {
                        Toast.makeText(RecordAndResultActivity.this, "输入的文本不能为空！", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            //点击输入框进入搜索记录界面
            case R.id.et_input_record:
                replaceFragment(new RecordFragment());
                break;
        }
    }


    /**
     * 判断查词服务是否在运行
     *
     * @param mContext
     * @param serviceName 服务的包名+类名
     * @return
     */
    private boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        //当前正在运行的服务的集合
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            //获取正在运行服务的包名+类名
            String name = myList.get(i).service.getClassName();
            if (name.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

}
