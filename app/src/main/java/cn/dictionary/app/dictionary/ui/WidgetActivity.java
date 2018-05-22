package cn.dictionary.app.dictionary.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import cn.dictionary.app.dictionary.R;
import cn.dictionary.app.dictionary.view.WidgetResultFrament;
import cn.dictionary.app.dictionary.widget.ClearEditText;

public class WidgetActivity extends AppCompatActivity implements View.OnClickListener {

    private ClearEditText et_Input;
    private ImageButton im_query;
    private WidgetResultFrament widgetResultFrament;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget);
        replace(new WidgetResultFrament());
        initView();
        initEvents();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    /**
     * 添加碎片
     *
     * @param fragment
     */
    public void replace(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.widget_container, fragment);
        transaction.commitNow();
        widgetResultFrament = (WidgetResultFrament) getSupportFragmentManager().findFragmentById(R.id.widget_container);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        et_Input = (ClearEditText) findViewById(R.id.et_input_widget);
        im_query = (ImageButton) findViewById(R.id.im_query_widget);
    }

    /**
     * 初始化事件
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initEvents() {
        im_query.setOnClickListener(this);
        im_query.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        im_query.setBackgroundResource(R.drawable.im_querypressed);
                        break;
                    case MotionEvent.ACTION_UP:
                        im_query.setBackgroundResource(R.drawable.im_query);
                        break;
                }
                return false;
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.im_query_widget:
                if (!TextUtils.isEmpty(et_Input.getText().toString())) {
                    //关闭或开启软键盘
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    String input = et_Input.getText().toString().trim();
                    widgetResultFrament.handleInput(input);
                } else {
                    Toast.makeText(WidgetActivity.this, "输入的文本不能为空！", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }


}
