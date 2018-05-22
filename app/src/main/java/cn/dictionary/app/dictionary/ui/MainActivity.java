package cn.dictionary.app.dictionary.ui;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import cn.dictionary.app.dictionary.service.DailySentenceService;
import cn.dictionary.app.dictionary.R;
import cn.dictionary.app.dictionary.adapter.RecentWordAdapter;
import cn.dictionary.app.dictionary.config.Broadcast;
import cn.dictionary.app.dictionary.db.DailySentenceDao;
import cn.dictionary.app.dictionary.db.WordBookDao;
import cn.dictionary.app.dictionary.entity.DailySentence;
import cn.dictionary.app.dictionary.entity.Words;
import cn.dictionary.app.dictionary.utils.DailySentenceUtil;
import cn.dictionary.app.dictionary.utils.NetWorkUtil;
import cn.dictionary.app.dictionary.utils.SDUtil;
import cn.dictionary.app.dictionary.utils.WordUtil;
import cn.dictionary.app.dictionary.view.base.DailySentenceFragment;
import cn.dictionary.app.dictionary.view.daily_fragment.FiveFragment;
import cn.dictionary.app.dictionary.view.daily_fragment.FourFragment;
import cn.dictionary.app.dictionary.view.daily_fragment.OneFragment;
import cn.dictionary.app.dictionary.view.daily_fragment.ThreeFragment;
import cn.dictionary.app.dictionary.view.daily_fragment.TwoFragment;
import cn.dictionary.app.dictionary.widget.DynamicStopSlidingViewPage;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private TextView mInput;//输入框
    private ImageButton mMenu;//菜单按钮
    private RelativeLayout mWordBook;//单词本按钮所在的布局
    private RelativeLayout mSetting;//设置按钮所在布局
    private ImageButton mSearch;//搜索键
    private ImageButton mOne;//按键1
    private ImageButton mTwo;//按键2
    private ImageButton mThree;//按键3
    private ImageButton mFour;//按键4
    private ImageButton mFive;//按键5

    private DynamicStopSlidingViewPage mViewPager;//5页每日一句
    private FragmentPagerAdapter mFragmentPagerAdapter;
    private List<Fragment> mFragmentList;//ViewPage的数据源
    private LinearLayout mBottomLayout;//底部按钮所在的布局
    private LinearLayout mOneLayout;//按键1对应的布局
    private LinearLayout mTwoLayout;//按键2对应的布局
    private LinearLayout mThreeLayout;//按键3对应的布局
    private LinearLayout mFourLayout;//按键4对应的布局
    private LinearLayout mFiveLayout;//按键5对应的布局

    private DrawerLayout mDrawerLayout;//MainActivity的布局
    private LinearLayout mDrawerMenu;//侧滑菜单所在布局
    private ListView mRecentWordListView;//最近添加的单词
    private RecentWordAdapter mRecentWordAdapter;
    private List<Words> mRecentWordList;
    private AlertDialog.Builder mDialog;//长按单词弹出的对话框

    private Intent mDSServiceIntent;//开启后台服务的Intent
    private boolean isLongPressed = false;//判断Tab选项卡是否被长按
    private int currentItem;//当前的显示的是第几个item
    private Handler handler;//处理定时切换每日一句

    private NetworkChangeReceiver mNetworkChangeReceiver;//网络变化广播接收器
    private DailySentenceServiceCompleteReceiver mDSServiceCompleteReceiver;//每日一句请求后台完成的广播接收器


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
        isReceiver();
        selectFragment(0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        List<Words> wordbookList = WordBookDao.getInstance().queryAllWords();
        if (wordbookList == null) {
            mRecentWordList.clear();
            mRecentWordAdapter.notifyDataSetChanged();
        } else {
            if (mRecentWordList.size() != wordbookList.size() || (!mRecentWordList.containsAll(wordbookList))) {
                //若数据源发生变化，则重新加载数据源
                //倒序，让最后添加的单词显示到最上边
                Collections.reverse(wordbookList);
                mRecentWordList.clear();
                mRecentWordList.addAll(wordbookList);
                mRecentWordAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 初始化UI控件
     */
    public void initView() {

        //设置沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(Color.TRANSPARENT);
                getWindow()
                        .getDecorView()
                        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            } else {
                getWindow()
                        .setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }

        mInput = (TextView) findViewById(R.id.tv_input);
        mMenu = (ImageButton) findViewById(R.id.im_menu);
        mSearch = (ImageButton) findViewById(R.id.im_query);

        mOne = (ImageButton) findViewById(R.id.im_one);
        mTwo = (ImageButton) findViewById(R.id.im_two);
        mThree = (ImageButton) findViewById(R.id.im_three);
        mFour = (ImageButton) findViewById(R.id.im_four);
        mFive = (ImageButton) findViewById(R.id.im_five);

        mOneLayout = (LinearLayout) findViewById(R.id.layout_one);
        mTwoLayout = (LinearLayout) findViewById(R.id.layout_two);
        mThreeLayout = (LinearLayout) findViewById(R.id.layout_three);
        mFourLayout = (LinearLayout) findViewById(R.id.layout_four);
        mFiveLayout = (LinearLayout) findViewById(R.id.layout_five);
        mBottomLayout = (LinearLayout) findViewById(R.id.layout_bottom_main);
        if (DailySentenceDao.getInstance().queryAllDailySentence() == null) {
            //若无每日一句的数据，则隐藏底部的按钮
            mBottomLayout.setVisibility(View.GONE);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.layout_DrawerLayout);
        mDrawerMenu = (LinearLayout) findViewById(R.id.layout_DrawerMenu);
        mWordBook = (RelativeLayout) findViewById(R.id.layout_wordbook);
        mSetting = (RelativeLayout) findViewById(R.id.layout_setting);
        mRecentWordList = WordBookDao.getInstance().queryAllWords();
        if (mRecentWordList == null) {
            mRecentWordList = new ArrayList<Words>();
        }
        mRecentWordListView = (ListView) findViewById(R.id.lv_RecentWord);
        mRecentWordAdapter = new RecentWordAdapter(MainActivity.this, R.id.lv_RecentWord
                , R.layout.item_menu_listview, mRecentWordList);
        mRecentWordListView.setAdapter(mRecentWordAdapter);

        initViewPage();
        autoReplaceImg();
    }

    /**
     * 加载ViewPage
     */
    private void initViewPage() {
        List<Fragment> flist = new ArrayList<>();
        Fragment oneFragment = new OneFragment();
        Fragment twoFragment = new TwoFragment();
        Fragment threeFragment = new ThreeFragment();
        Fragment fourFragment = new FourFragment();
        Fragment fiveFragment = new FiveFragment();
        flist.add(oneFragment);
        flist.add(twoFragment);
        flist.add(threeFragment);
        flist.add(fourFragment);
        flist.add(fiveFragment);
        mFragmentList = flist;

        mViewPager = (DynamicStopSlidingViewPage) findViewById(R.id.id_ViewPager);
        mFragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return mFragmentList.size();
            }

        };
        mViewPager.setAdapter(mFragmentPagerAdapter);
        //监听翻页
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                currentItem = mViewPager.getCurrentItem();
                setImgView(currentItem);
                //把所有音频暂停播放
                List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
                for (int i = 0; i < fragmentList.size(); i++) {
                    DailySentenceFragment dailySentenceFragment = ((DailySentenceFragment) fragmentList.get(i));
                    if (dailySentenceFragment != null) {
                        if (dailySentenceFragment.mediaPlayer != null) {
                            if (dailySentenceFragment.mediaPlayer.isPlaying()) {
                                dailySentenceFragment.mediaPlayer.pause();
                            }
                        }
                    }
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        // 预加载5个Fragment
        mViewPager.setOffscreenPageLimit(5);
    }


    /**
     * 初始化事件
     */
    private void initEvent() {
        mInput.setOnClickListener(this);
        mMenu.setOnClickListener(this);
        mMenu.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mMenu.setBackgroundResource(R.drawable.im_menu_pressed);
                        break;
                    case MotionEvent.ACTION_UP:
                        mMenu.setBackgroundResource(R.drawable.im_menu_normal);
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
                }
                return false;
            }
        });

        mOneLayout.setOnClickListener(this);
        mTwoLayout.setOnClickListener(this);
        mThreeLayout.setOnClickListener(this);
        mFourLayout.setOnClickListener(this);
        mFiveLayout.setOnClickListener(this);

        mOneLayout.setOnLongClickListener(this);
        mTwoLayout.setOnLongClickListener(this);
        mThreeLayout.setOnLongClickListener(this);
        mFourLayout.setOnLongClickListener(this);
        mFiveLayout.setOnLongClickListener(this);

        mWordBook.setOnClickListener(this);
        mSetting.setOnClickListener(this);
        mRecentWordListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Words word = mRecentWordList.get(position);
                Intent intent = new Intent(MainActivity.this, RecordAndResultActivity.class);
                intent.putExtra("input", word.getQuery());
                startActivity(intent);
            }
        });
        mRecentWordListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                mDialog = new AlertDialog.Builder(MainActivity.this);
                mDialog.setMessage("是否删除此单词？");
                mDialog.setCancelable(false);
                mDialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //从单词本删除此单词，并更新ListView
                        WordUtil.deleteWordFromWordBook(mRecentWordList.get(position));
                        mRecentWordList.remove(position);
                        mRecentWordAdapter.notifyDataSetChanged();
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

        //自动跳转页面
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                selectFragment(msg.what);
            }
        };
    }

    /**
     * 判断是否需要监听网络和后台任务的广播
     * 若需要，则注册的广播
     */
    private void isReceiver() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        DailySentence dailySentence = DailySentenceDao.getInstance().queryDailySentence(sf.format(c.getTime()));
        if (dailySentence != null){
            //已经存在今天的每日一句
            return;
        }
        //删除数据库和SD卡中的全部记录，发起网络请求
        DailySentenceDao.getInstance().deleteAllDailySentence();
        SDUtil.getHanderSD().deleteAllImg();
        //隐藏底部翻页按钮
        mBottomLayout.setVisibility(View.GONE);
        //禁止ViewPage滑动
        mViewPager.isScrollale(false);
        //注册接收网络的变化的广播
        IntentFilter intentFilter_1 = new IntentFilter();
        intentFilter_1.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        mNetworkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(mNetworkChangeReceiver, intentFilter_1);
        //注册监听后台完成任务的广播
        IntentFilter intentFilter_2 = new IntentFilter();
        intentFilter_2.addAction(Broadcast.DAILYSENTENCESERVICE_COMPLETE);
        mDSServiceCompleteReceiver = new DailySentenceServiceCompleteReceiver();
        registerReceiver(mDSServiceCompleteReceiver, intentFilter_2);
    }

    /**
     * 接收网络变化的广播接收器
     */
    class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //判断是否有网络
            if (NetWorkUtil.hasNetwork()) {
                //有网络，联网更新每日一句数据
                Toast.makeText(MainActivity.this, "正在联网更新每日一句！", Toast.LENGTH_SHORT).show();
                mDSServiceIntent = new Intent(MainActivity.this, DailySentenceService.class);
                startService(mDSServiceIntent);
                unregisterReceiver(mNetworkChangeReceiver);
            } else {
                Toast.makeText(MainActivity.this, "网络不可用！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 接收后台完成任务的广播接收器
     */
    class DailySentenceServiceCompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //停止后台服务
            stopService(mDSServiceIntent);
            //更新UI
            if (mBottomLayout.getVisibility() == View.GONE) {
                mBottomLayout.setVisibility(View.VISIBLE);
                //允许ViewPage滑动
                mViewPager.isScrollale(true);
            }
            initViewPage();
            unregisterReceiver(mDSServiceCompleteReceiver);
        }
    }

    @Override
    public void onClick(View v) {
        //把长按状态取消,并把ViewPage设为可滑动
        if (v.getId() != R.id.tv_input && v.getId() != R.id.im_query && v.getId() != R.id.im_menu) {
            isLongPressed = false;
            mViewPager.isScrollale(true);
        }
        switch (v.getId()) {
            case R.id.tv_input:
                Intent intent_1 = new Intent(MainActivity.this, RecordAndResultActivity.class);
                startActivity(intent_1);
                break;
            case R.id.im_query:
                Intent intent_2 = new Intent(MainActivity.this, RecordAndResultActivity.class);
                startActivity(intent_2);
                break;
            case R.id.im_menu:
                if (mDrawerLayout.isDrawerOpen(mDrawerMenu)) {
                    //若菜单打开则关闭
                    mDrawerLayout.closeDrawer(mDrawerMenu);
                } else {
                    //菜单关闭则打开
                    mDrawerLayout.openDrawer(mDrawerMenu);
                }
                break;
            case R.id.layout_wordbook:
                Intent intent = new Intent(MainActivity.this, WordBookActivity.class);
                startActivity(intent);
                break;
            case R.id.layout_setting:
                Intent intent1 = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent1);
                break;
            case R.id.layout_one:
                selectFragment(0);
                break;
            case R.id.layout_two:
                selectFragment(1);
                break;
            case R.id.layout_three:
                selectFragment(2);
                break;
            case R.id.layout_four:
                selectFragment(3);
                break;
            case R.id.layout_five:
                selectFragment(4);
                break;
        }

    }

    @Override
    public boolean onLongClick(View v) {
        retImgView();
        Toast.makeText(this, "已锁定当前每日一句！", Toast.LENGTH_SHORT).show();
        switch (v.getId()) {
            case R.id.layout_one:
                selectFragment(0);
                mOne.setBackgroundResource(R.drawable.im_1_longpressed);
                break;
            case R.id.layout_two:
                selectFragment(1);
                mTwo.setBackgroundResource(R.drawable.im_2_longpressed);
                break;
            case R.id.layout_three:
                selectFragment(2);
                mThree.setBackgroundResource(R.drawable.im_3_longpressed);
                break;
            case R.id.layout_four:
                selectFragment(3);
                mFour.setBackgroundResource(R.drawable.im_4_longpressed);
                break;
            case R.id.layout_five:
                selectFragment(4);
                mFive.setBackgroundResource(R.drawable.im_5_longpressed);
                break;
        }
        isLongPressed = true;//设置长按标志值
        //禁止ViewPage滑动
        mViewPager.isScrollale(false);
        return true;
    }

    /**
     * 把按键全设置为未点击颜色
     */
    public void retImgView() {
        mOne.setBackgroundResource(R.drawable.im_1_normal);
        mTwo.setBackgroundResource(R.drawable.im_2_normal);
        mThree.setBackgroundResource(R.drawable.im_3_normal);
        mFour.setBackgroundResource(R.drawable.im_4_normal);
        mFive.setBackgroundResource(R.drawable.im_5_normal);
    }

    /**
     * 点击按钮后切换视图，并把按钮设为点击颜色
     *
     * @param i 0-4
     */
    public void selectFragment(int i) {
        setImgView(i);
        mViewPager.setCurrentItem(i);
    }

    /**
     * 设置被点击按键为点击颜色
     *
     * @param i 第几个按钮被设置
     */
    private void setImgView(int i) {
        retImgView();
        switch (i) {
            case 0:
                mOne.setBackgroundResource(R.drawable.im_1_pressed);
                break;
            case 1:
                mTwo.setBackgroundResource(R.drawable.im_2_pressed);
                break;
            case 2:
                mThree.setBackgroundResource(R.drawable.im_3_pressed);
                break;
            case 3:
                mFour.setBackgroundResource(R.drawable.im_4_pressed);
                break;
            case 4:
                mFive.setBackgroundResource(R.drawable.im_5_pressed);
                break;
        }
    }

    /**
     * 轮播图片
     */
    private void autoReplaceImg() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (true) {
                    while (i == currentItem) {
                        //页面暂停12s
                        try {
                            Thread.sleep(12000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    //判断用户是否手动去选择了页面
                    while (i != currentItem) {
                        //若有，取得当前页面的页数,并10s后再滑动
                        i = currentItem;
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (isLongPressed) {
                        //用户有对Tab选项卡长按，则不跳转页面
                        continue;
                    }
                    //判断页面是否是最后一页
                    if (i++ == mFragmentList.size() - 1) {
                        //若是，则将跳转到第一页
                        i = 0;
                    }
                    //到主线程中跳转页面
                    Message message = new Message();
                    message.what = i;
                    handler.sendMessage(message);
                }
            }
        }).start();
    }


}
