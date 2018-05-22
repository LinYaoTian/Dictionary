package cn.dictionary.app.dictionary.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;

/**
 * 具有删除按钮的EditText
 */

public class ClearEditText extends android.support.v7.widget.AppCompatEditText implements OnFocusChangeListener, TextWatcher {

    private Drawable mCleanDrawable;//清除键的引用
    private boolean hasFoucs;//判断EditText是否有焦点

    public ClearEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
        intiEvents();
    }

    public ClearEditText(Context context) {
        super(context);
    }

    private void initViews() {
        mCleanDrawable = getCompoundDrawables()[2];
        //设置按钮的大小和位置
        mCleanDrawable.setBounds(0, 2, 80, 80);
        //默认隐藏删除键
        setClearDrawableVisible(false);
    }

    private void intiEvents() {
        setOnFocusChangeListener(this);
        addTextChangedListener(this);
    }

    /**
     * 设置删除键的显示与隐藏
     *
     * @param visible
     */
    private void setClearDrawableVisible(boolean visible) {
        Drawable right = null;//清除键的引用
        if (visible) {
            right = mCleanDrawable;
        }
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1],
                right, getCompoundDrawables()[3]);
    }

    /**
     * 当手指抬起的位置在clean的图标的区域
     * 将此视为进行清除操作
     * getWidth():得到控件的宽度
     * event.getX():抬起时的坐标(改坐标是相对于控件本身而言的
     * getTotalPaddingRight():clean的图标左边缘至控件右边缘的距离
     * getPaddingRight():clean的图标右边缘至控件右边缘的距离
     * 于是:
     * getWidth() - getTotalPaddingRight()表示:
     * 控件左边到clean的图标左边缘的区域
     * getWidth() - getPaddingRight()表示:
     * 控件左边到clean的图标右边缘的区域
     * 所以这两者之间的区域刚好是clean的图标的区域
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            //一个手势完成时
            if (getCompoundDrawables()[2] != null) {
                boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
                        && (event.getX() < (getWidth() - getPaddingRight()));
                if (touchable) {
                    setText("");
                }
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if (hasFoucs) {
            setClearDrawableVisible(text.length() > 0);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    /**
     * 当编辑框焦点改变时,根据字符串的长度和是否有焦点显示和隐藏删除键
     *
     * @param v
     * @param hasFocus
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.hasFoucs = hasFocus;
        if (hasFocus) {
            setClearDrawableVisible(getText().length() > 0);
        } else {
            setClearDrawableVisible(false);
        }
    }
}
