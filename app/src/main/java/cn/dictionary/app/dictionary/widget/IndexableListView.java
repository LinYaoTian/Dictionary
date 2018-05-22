package cn.dictionary.app.dictionary.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * 封装了与索引条相关操作的ListView
 */

public class IndexableListView extends ListView {

    private boolean mIsFastScrollEnable = false;
    private IndexScroller mIndexScroller = null;
    private GestureDetector mGestureDetector = null;
    private int mListViewWidth;
    private int mListViewHeight;

    public IndexableListView(Context context) {
        super(context);
    }

    public IndexableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IndexableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean ismIsFastScrollEnable() {
        return mIsFastScrollEnable;
    }

    @Override
    public void setFastScrollEnabled(boolean enabled) {
        mIsFastScrollEnable = enabled;
        if (mIsFastScrollEnable) {
            if (mIndexScroller == null) {
                mIndexScroller = new IndexScroller(getContext(), this);
                onSizeChanged(mListViewWidth, mListViewHeight, 0, 0);
            }
        } else {
            if (mIndexScroller != null) {
                mIndexScroller.hide();
                mIndexScroller = null;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mIndexScroller != null) {
            mIndexScroller.draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mIndexScroller != null && mIndexScroller.onTouchEvent(ev)) {
            return true;
        }
        if (mGestureDetector == null) {
            mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    if (mIndexScroller != null) {
                        mIndexScroller.show();
                    }
                    return super.onFling(e1, e2, velocityX, velocityY);
                }
            });
        }
        mGestureDetector.onTouchEvent(ev);
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!mIsFastScrollEnable) {
            return super.onInterceptTouchEvent(ev);
        }
        if (mIndexScroller.contains(ev.getX(), ev.getY())) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        if (mIndexScroller != null) {
            mIndexScroller.setAdapter(adapter);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mListViewWidth = w;
        mListViewHeight = h;
        if (mIndexScroller != null) {
            mIndexScroller.onSizeChanged(w, h, oldw, oldh);
        }
    }
}
