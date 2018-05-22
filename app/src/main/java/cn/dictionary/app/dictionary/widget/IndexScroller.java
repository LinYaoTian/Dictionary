package cn.dictionary.app.dictionary.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.SectionIndexer;

/**
 *  实现索引条类
 */

public class IndexScroller {
    private float mIndexbarWidth;     //索引条的宽度（高度由文本自己决定）
    private float mIndexbarMargin;    //索引条第一个文本项距离索引条顶部的距离
    private float mPreviewPadding;    //在中心显示的预览文本到四周的距离(不是屏幕)
    private float mDensity;            //表示当前屏幕密度/160（View）
    private float mScaleDensity;      //表示当前屏幕密度/160（字体）
    private float mAlphaRate;         //透明度0-1
    private int mState= STATE_HIDING; //索引条的状态
    private int mListViewWidth;
    private int mListViewHeight;
    private int           mCurrentSection=-1;   //索引条选择的位置，-1为未选择
    private boolean       mIsIndexing =false;
    private ListView       mListView=null;
    private SectionIndexer mIndexer=null;
    private String []      mSections=null;//索引条上的文本
    private RectF mIndexbarRect;//整个索引条的区域

    private static final int STATE_HINDDEN = 0;
    private static final int STATE_SHOWING = 1;
    private static final int STATE_SHOWN   = 2;
    private static final int STATE_HIDING  = 3;

    public IndexScroller(Context context,ListView listView){
        //获取屏幕的密度比
        mDensity = context.getResources().getDisplayMetrics().density;
        mScaleDensity=context.getResources().getDisplayMetrics().scaledDensity;
        mListView=listView;
        setAdapter(mListView.getAdapter());
        mIndexbarWidth  = 20*mDensity;
        mIndexbarMargin = 10*mDensity;
        mPreviewPadding = 5*mDensity;
    }


    public void setAdapter(Adapter adapter){
        //设置的适配器必须实现SectionIndexer
        if (adapter instanceof SectionIndexer){
            mIndexer=(SectionIndexer) adapter;
            mSections= (String[]) mIndexer.getSections();
        }
    }

    /**
     * 1.绘制索引条，包括索引条的背景和文本
     * 2.绘制预览文本和背景
     * @param canvas
     */
    public void draw(Canvas canvas){
        //如果索引条是隐藏状态则不进行绘制
        if (mState==STATE_HINDDEN){
            return;
        }
        //设置索引条背景绘制属性
        Paint indexbarPaint = new Paint();
        indexbarPaint.setColor(Color.BLACK);
        indexbarPaint.setAlpha((int) (64*mAlphaRate));

        //绘制索引条(四个角都为圆角）
        canvas.drawRoundRect(mIndexbarRect,5*mDensity,5*mDensity,indexbarPaint);

        //绘制section
        if (mSections!=null&&mSections.length>0){
            //绘制预览文文本和背景
            if (mCurrentSection>=0){
                //预览背景
                Paint previewPaint=new Paint();
                previewPaint.setColor(Color.BLACK);
                previewPaint.setAlpha(96);
                //预览文
                Paint previewTextPaint = new Paint();
                previewTextPaint.setColor(Color.WHITE);
                previewTextPaint.setTextSize(50*mScaleDensity);
                //测量文本的宽度
                float previewTextWidth = previewTextPaint.measureText(mSections[mCurrentSection]);
                //预览文本高度 = 文本上边距+文本下边距+文字高度
                float previewSize = 2*mPreviewPadding
                        +previewTextPaint.descent()-previewTextPaint.ascent();
                //预览文本的背景区域
                RectF previewRectF = new RectF((mListViewWidth-previewSize)/2
                        ,(mListViewHeight-previewSize)/2
                        ,(mListViewWidth-previewSize)/2+previewSize
                        ,(mListViewHeight-previewSize)/2+previewSize);
                //绘制背景
                canvas.drawRoundRect(previewRectF,5*mDensity,5*mDensity,previewPaint);
                //绘制文本
                canvas.drawText(mSections[mCurrentSection]
                        ,previewRectF.left+(previewSize-previewTextWidth)/2-1
                        ,previewRectF.top+mPreviewPadding-previewTextPaint.ascent()+1
                        ,previewTextPaint);
            }
        }

        //设置每个索引文本的绘制属性
        Paint indexPaint =new Paint();
        indexPaint.setColor(Color.WHITE);
        indexPaint.setAlpha((int) (255*mAlphaRate));
        indexPaint.setTextSize(12*mScaleDensity);
        indexPaint.setAntiAlias(true);
        //每个索引文本项的高度
        float sectionHeight = (mIndexbarRect.height()-2*mIndexbarMargin)/mSections.length;
        //每个索引文本距离文本项顶部的距离
        float paddingTop = (sectionHeight-(indexPaint.descent()-indexPaint.ascent()))/2;
        for (int i=0;i<mSections.length;i++){
            //索引文本距离文本项左侧边缘的距离
            float paddingLeft=(mIndexbarWidth-indexPaint.measureText(mSections[i]))/2;
            canvas.drawText(mSections[i]
                    ,mIndexbarRect.left+paddingLeft
                    ,mIndexbarRect.top+mIndexbarMargin+sectionHeight*i+paddingTop-indexPaint.ascent()
                    ,indexPaint);
        }
    }

    public boolean onTouchEvent(MotionEvent ev){
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (mState!=STATE_HINDDEN && contains(ev.getX(),ev.getY())){
                    mIsIndexing=true;
                    mCurrentSection=getSectionByPoint(ev.getY());
                    mListView.setSelection(mIndexer.getPositionForSection(mCurrentSection));
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mIsIndexing){
                    if (contains(ev.getX(),ev.getY())){
                        mCurrentSection=getSectionByPoint(ev.getY());
                        mListView.setSelection(mIndexer.getPositionForSection(mCurrentSection));
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mIsIndexing){
                    mIsIndexing=false;
                    mCurrentSection=-1;
                }
                if (mState==STATE_SHOWN){
                    setState(STATE_HIDING);
                }
                break;
        }
        return false;
    }

    /**
     * 判断手指是否落在索引条上
     * @param x 手指落下位置的x坐标
     * @param y 手指落下位置的y坐标
     */
    public boolean contains(float x, float y) {
        return x >= mIndexbarRect.left &&
                y >= mIndexbarRect.top && y <= mIndexbarRect.top + mIndexbarRect.height();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (mState) {
                case STATE_HIDING:
                    mAlphaRate += (1 - mAlphaRate) * 0.2;
                    if (mAlphaRate > 0.9) {
                        mAlphaRate = 1;
                        setState(STATE_SHOWN);
                    }
                    mListView.invalidate();
                    fade(10);
                    break;
                case STATE_HINDDEN:
                    setState(STATE_HIDING);
                    break;
                case STATE_SHOWING:
                    mAlphaRate -= mAlphaRate * 0.2;
                    if (mAlphaRate < 0.1) {
                        mAlphaRate = 0;
                        setState(STATE_SHOWN);
                    }
                    mListView.invalidate();
                    fade(10);
                    break;
            }
        }
    };

    /**
     * 渐变效果
     * @param delay
     */
    private void fade(long delay) {
        mHandler.removeMessages(0);
        mHandler.sendEmptyMessageAtTime(0, SystemClock.uptimeMillis()+delay);
    }

    /**
     * 获取手指点击的索引位置
     * @param y
     * @return
     */
    private int getSectionByPoint(float y) {
        if (mSections==null||mSections.length==0){
            return 0;
        }
        if (y<mIndexbarRect.top+mIndexbarMargin){
            return 0;
        }
        if (y>=mIndexbarRect.top+mIndexbarRect.height()-mIndexbarMargin){
            return mSections.length-1;
        }
        return (int) ((y-mIndexbarRect.top-mIndexbarMargin)/((mIndexbarRect.height()-2*mIndexbarMargin)/mSections.length));
    }

    /**
     * 设置索引条状态
     * @param state
     */
    private void setState(int state) {
        if (state<STATE_HINDDEN||state>STATE_HIDING){
            return;
        }
        mState=state;
        switch (mState){
            case STATE_HIDING:
                mAlphaRate=1;
                fade(3000);
                break;

            case STATE_HINDDEN:
                mHandler.removeMessages(0);
                break;
            case STATE_SHOWING:
                mAlphaRate=0;
                fade(0);
                break;
            case STATE_SHOWN:
                mHandler.removeMessages(0);
                break;
        }
    }

    /**
     * 呈现索引条的状态
     */
    public void show(){
        if (mState==STATE_HINDDEN){
            setState(STATE_SHOWING);
        }else if (mState==STATE_HIDING){
            setState(STATE_HIDING);
        }
    }

    /**
     * 隐藏索引条状态
     */
    public void hide(){
        if (mState==STATE_SHOWN){
            setState(STATE_HIDING);
        }
    }

    public void onSizeChanged(int w,int h,int oldw,int oldh){
        mListViewWidth=w;
        mListViewHeight=h;
        mIndexbarRect=new RectF(w-mIndexbarMargin-mIndexbarWidth
        , mIndexbarMargin
        ,w-mIndexbarMargin
        , h-mIndexbarMargin);
    }
}
