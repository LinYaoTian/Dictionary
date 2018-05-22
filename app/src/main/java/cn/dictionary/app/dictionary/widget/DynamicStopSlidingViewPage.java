package cn.dictionary.app.dictionary.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 可以动态禁止滑动的ViewPage
 */

public class DynamicStopSlidingViewPage extends ViewPager {

    private boolean mScrollable = true;//是否可滑动

    public DynamicStopSlidingViewPage(Context context) {
        super(context);
    }

    public DynamicStopSlidingViewPage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 是否处理Viewpage上的事件
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!mScrollable) {
            //不可滑动时处理点击事件
            return true;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 拦截ViewPage上子View的Motion Events，比如点击事件
     *
     * @param ev
     * @return true, 拦截
     * false,不拦截
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!mScrollable) {
            //不可滑动时不拦截
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 调节ViewPage是否可滑动
     */
    public void isScrollale(boolean able) {
        this.mScrollable = able;
    }
}
