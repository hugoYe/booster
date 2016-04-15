package com.cooeeui.nanobooster;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cooeeui.nanobooster.common.util.AccessibilityServiceUtil;
import com.cooeeui.nanobooster.common.util.MemoryUtil;
import com.cooeeui.nanobooster.model.NanoboosterAdpter;
import com.cooeeui.nanobooster.model.domain.AppInfo;
import com.cooeeui.nanobooster.views.FloatWindow;
import com.cooeeui.nanobooster.views.RippleView;

import java.util.ArrayList;
import java.util.List;

import static com.cooeeui.nanobooster.common.util.CreateShortCutUtil.createShortCut;

public class MainActivity extends Activity implements View.OnClickListener {

    //codes of qixiangxiang (start)
    private ListView mLv_1;
    public boolean ignore = false;
    public boolean running = false;
    public boolean isTitleChecked = true;
    public List<AppInfo> items_ignore = new ArrayList<AppInfo>();
    public List<AppInfo> items_running = new ArrayList<AppInfo>();
    public List<AppInfo> items_total = new ArrayList<AppInfo>();
    public List<AppInfo> items_delFrom_running = new ArrayList<AppInfo>();
    public List<AppInfo> items_current = new ArrayList<AppInfo>();
    private NanoboosterAdpter mNanoboosterAdpter;
    public int items_total_size;
    public int items_ignore_size;
    public int items_running_size;
    private SharedPreferences mSp;

    private RelativeLayout mRl_ignore_label;
    public TextView tv_ignore_size_label;

    private RelativeLayout mLl_running_label_first;
    public CheckBox ck_running_first;
    private TextView tv_running_manager_first;
    private TextView tv_running_total_first;

    private RelativeLayout ll_runnuing_label_second;
    public CheckBox ck_running_second;
    private TextView tv_running_manager_second;
    private TextView tv_running_total_second;

    private TextView tv_line;


    //大圆部分
    public ImageView iv_inner;
    private ImageView iv_outter;
    private RotateAnimation raInner;
    private RotateAnimation raOutter;
    private AnimationSet asInner;
    private AlphaAnimation aaInner;
    private AnimationSet asOutter;
    private AlphaAnimation aaOutter;
    private ImageView iv_inner_total;
    private AlphaAnimation aaInner_total;
    private TranslateAnimation ta_root;
    private ScaleAnimation sa_root;
    private AnimationSet as_root;
    private RelativeLayout fl_root;

    //三段文字
    private TextView tv_size;
    private TextView tv_name;
    private TextView tv_desc;
    private ObjectAnimator mAnim_tv_size;
    private ObjectAnimator mAnim_tv_desc;
    private ObjectAnimator mAnim_tv_name;
    private AnimatorSet animatorSet_threeParagraph;
    private AlphaAnimation aa_mAnim_tv_size;

    //提示小箭头
    private RelativeLayout mRl_guide_arrow;
    private ImageView guide_arrow_1;
    private ImageView guide_arrow_2;
    private ImageView guide_arrow_3;
    private AlphaAnimation aa_guide_arrow_1_in;
    private AlphaAnimation aa_guide_arrow_1_out;
    private AlphaAnimation aa_guide_arrow_2_in;
    private AlphaAnimation aa_guide_arrow_2_out;
    private AlphaAnimation aa_guide_arrow_3_in;
    private AlphaAnimation aa_guide_arrow_3_out;

    //小圆部分描述信息
    private LinearLayout mLl_root_small_desc;
    private AlphaAnimation aa_small_desc;

    //露出listview
    private FrameLayout mFl_child_include_list;
    private ObjectAnimator oa_fl_child_include_list_part;
    private ObjectAnimator oa_fl_child_include_list_total;
    private FrameLayout mFl_button_false;
    private Button mBt_booster_false;
    private RippleView mFl_button_true;
    private Button mBt_booster_true;
    private ObjectAnimator oa_mBt_booster_false;

    //codes of qixiangxiang (end)

    private RelativeLayout mTitlebar_icon;
    private LinearLayout mLinearLayout;

    private ImageView mIv_background;
    Boolean flag = true;
    //动画变化
    float anim1 = 0f, anim2 = 0.5f;
    //动画持续时间
    long duration = 500;

    // 返回键退出时间控制
    private long mExitTime;

    private FrameLayout mAccessibilityDialog;
    private RelativeLayout mAccessibilityAlertDialog;
    private ImageView mAccessibilityAlertDialogBg;
    private int mAccessibilityAnimDuration = 2000;
    private RippleView mAccessibilityEnableNowBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_view);
        mTitlebar_icon = (RelativeLayout) findViewById(R.id.titlebar_icon);
        mTitlebar_icon.setOnClickListener(this);
        mLinearLayout = (LinearLayout) findViewById(R.id.ll_layout);
        // mIv_background = (ImageView)findViewById(R.id.iv_background);

        //codes of qixiangxiang (start)
        init();
        initView();
        initData();
        initAnimation();
        startAnimation();
        initListener();
        //codes of qixiangxiang (end)
    }


    //初始化动画
    private void initAnimation() {

        initBigPicAnimation();
        initThreeDescparagraphAnimation();
        initRemindArrowAniamtion();
        initSmallPicAnimation();
        initExposeListViewAnimation();
    }

    private void initExposeListViewAnimation() {

        //listView部分露出
        int
            transition_part_end =
            (int) (getResources().getDimension(R.dimen.transition_part_end) + 0.5f);

        oa_fl_child_include_list_part =
            ObjectAnimator.ofFloat(mFl_child_include_list, "translationY", 0,
                                   transition_part_end);
        oa_fl_child_include_list_part.setDuration(2000);

        oa_fl_child_include_list_part.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

                mFl_button_false.setVisibility(View.INVISIBLE);
                mFl_button_true.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        //listView全部露出

        int
            tansition_total_start =
            (int) (getResources().getDimension(R.dimen.tansition_total_start) + 0.5f);
        int
            tansition_total_end =
            (int) (getResources().getDimension(R.dimen.tansition_total_end) + 0.5f);

        oa_fl_child_include_list_total =
            ObjectAnimator.ofFloat(mFl_child_include_list, "translationY",
                                   tansition_total_start, tansition_total_end);

        oa_fl_child_include_list_total.setDuration(2000);


    }


    private void initSmallPicAnimation() {
        aa_small_desc = new AlphaAnimation(0, 1);
        aa_small_desc.setDuration(2000);
        aa_small_desc.setFillAfter(true);
    }

    private void initRemindArrowAniamtion() {
        //箭头1渐入
        aa_guide_arrow_1_in = new AlphaAnimation(0, 1);
        aa_guide_arrow_1_in.setDuration(100);
        aa_guide_arrow_1_in.setFillAfter(true);

        aa_guide_arrow_1_in.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                guide_arrow_2.startAnimation(aa_guide_arrow_2_in);
            }
        });

        aa_guide_arrow_1_out = new AlphaAnimation(1, 0);
        aa_guide_arrow_1_out.setDuration(100);
        aa_guide_arrow_1_out.setFillAfter(true);
        aa_guide_arrow_1_out.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //guide_arrow_1.startAnimation(aa_guide_arrow_1_in);
                guide_arrow_2.startAnimation(aa_guide_arrow_2_out);
            }
        });

		/*####################箭头二的系类操作###########################*/

        aa_guide_arrow_2_in = new AlphaAnimation(0, 1);
        aa_guide_arrow_2_in.setDuration(100);
        aa_guide_arrow_2_in.setFillAfter(true);
        aa_guide_arrow_2_in.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                guide_arrow_3.startAnimation(aa_guide_arrow_3_in);
            }
        });

        aa_guide_arrow_2_out = new AlphaAnimation(1, 0);
        aa_guide_arrow_2_out.setDuration(100);
        aa_guide_arrow_2_out.setFillAfter(true);
        aa_guide_arrow_2_out.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //guide_arrow_1.startAnimation(aa_guide_arrow_1_out);
                guide_arrow_3.startAnimation(aa_guide_arrow_3_out);
            }
        });

		/*####################箭头三的系类操作###########################*/

        aa_guide_arrow_3_in = new AlphaAnimation(0, 1);
        aa_guide_arrow_3_in.setDuration(100);
        aa_guide_arrow_3_in.setFillAfter(true);
        aa_guide_arrow_3_in.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //guide_arrow_3.startAnimation(aa_guide_arrow_3_out);
                guide_arrow_1.startAnimation(aa_guide_arrow_1_out);
            }
        });

        aa_guide_arrow_3_out = new AlphaAnimation(1, 0);
        aa_guide_arrow_3_out.setDuration(100);
        aa_guide_arrow_3_out.setFillAfter(true);
        aa_guide_arrow_3_out.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //guide_arrow_2.startAnimation(aa_guide_arrow_2_out);
                guide_arrow_1.startAnimation(aa_guide_arrow_1_in);
            }
        });


    }

    //初始化描述的三段文字的动画
    public void initThreeDescparagraphAnimation() {

        int
            tansition_threeDesc_tv_size =
            (int) (getResources().getDimension(R.dimen.tansition_threeDesc_tv_size) + 0.5f);
        int
            tansition_threeDesc_tv_name =
            (int) (getResources().getDimension(R.dimen.tansition_threeDesc_tv_name) + 0.5f);
        int
            tansition_threeDesc_tv_desc =
            (int) (getResources().getDimension(R.dimen.tansition_threeDesc_tv_desc) + 0.5f);

        aa_mAnim_tv_size = new AlphaAnimation(1, 0);
        aa_mAnim_tv_size.setDuration(2000);
        aa_mAnim_tv_size.setFillAfter(true);

        mAnim_tv_size =
            ObjectAnimator.ofFloat(tv_size, "translationY", tansition_threeDesc_tv_size);
        mAnim_tv_size.setInterpolator(new BounceInterpolator());
        mAnim_tv_size.setDuration(2000);

        mAnim_tv_name =
            ObjectAnimator.ofFloat(tv_name, "translationY", tansition_threeDesc_tv_name);
        mAnim_tv_name.setInterpolator(new BounceInterpolator());
        mAnim_tv_name.setStartDelay(200);
        mAnim_tv_name.setDuration(2000);

        mAnim_tv_desc =
            ObjectAnimator.ofFloat(tv_desc, "translationY", tansition_threeDesc_tv_desc);
        mAnim_tv_desc.setInterpolator(new OvershootInterpolator(4));
        mAnim_tv_desc.setStartDelay(400);
        mAnim_tv_desc.setDuration(2000);

        animatorSet_threeParagraph = new AnimatorSet();

        animatorSet_threeParagraph.playTogether(mAnim_tv_size, mAnim_tv_name, mAnim_tv_desc);

        animatorSet_threeParagraph.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mRl_guide_arrow.setVisibility(View.VISIBLE);
                guide_arrow_1.startAnimation(aa_guide_arrow_1_in);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    //初始化大图的动画
    public void initBigPicAnimation() {
        int transtionEndX = (int) (getResources().getDimension(R.dimen.transtionEndX) + 0.5f);
        int transtionEndY = (int) (getResources().getDimension(R.dimen.transtionEndY) + 0.5f);
        //位移+缩放

        ta_root = new TranslateAnimation(0, transtionEndX, 0, transtionEndY);
        sa_root = new ScaleAnimation(1.0f, 0.65f, 1.0f, 0.65f);

        as_root = new AnimationSet(false);
        as_root.addAnimation(ta_root);
        as_root.addAnimation(sa_root);
        as_root.setDuration(2000);
        as_root.setFillAfter(true);

        aaInner_total = new AlphaAnimation(0, 1);
        aaInner_total.setDuration(7000);

        aaInner_total = new AlphaAnimation(0, 1);
        aaInner_total.setDuration(7000);

        //里面圈的动画效果
        raInner =
            new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f,
                                Animation.RELATIVE_TO_SELF, 0.5f);
        raInner.setDuration(5000);
        raInner.setFillAfter(true);

        aaInner = new AlphaAnimation(1, 0);
        aaInner.setDuration(5000);

        asInner = new AnimationSet(false);
        asInner.addAnimation(raInner);
        asInner.addAnimation(aaInner);
        asInner.setFillAfter(true);
        asInner.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //iv_inner.setBackgroundResource(R.drawable.solid);
                        /*###################改变了这里###################*/
                iv_inner.setVisibility(View.GONE);
            }
        });

        //外面圈的动画效果
        raOutter =
            new RotateAnimation(360, 0, Animation.RELATIVE_TO_SELF, 0.5f,
                                Animation.RELATIVE_TO_SELF, 0.5f);
        raOutter.setDuration(5000);
        //raOutter.setRepeatCount(Animation.INFINITE);

        aaOutter = new AlphaAnimation(1, 0);
        aaOutter.setDuration(5000);
        aaOutter.setFillAfter(true);

        asOutter = new AnimationSet(false);
        asOutter.addAnimation(raOutter);
        asOutter.addAnimation(aaOutter);
        asOutter.setFillAfter(true);

        asOutter.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                /*###################改变了这里###################*/
                iv_outter.setVisibility(View.INVISIBLE);

                animatorSet_threeParagraph.start();
                //lisview出现
                oa_fl_child_include_list_part.start();
            }
        });

    }

    //初始化监听事件
    private void initListener() {

        mRl_guide_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), "进来了", Toast.LENGTH_LONG).show();
                doAnimation();
            }
        });
    }

    //code  of qixiangxiang (end)
    private void init() {
        items_total = MemoryUtil.getRunningAppInfos(MainActivity.this);//真实数据
        //过滤数据 分别将白名单和运行名单的数据 分门别类
        String packageNames = getIgnorePacknameFromSP();
        for (AppInfo appInfo : items_total) {
            if (packageNames.contains(appInfo.getPackName())) {//是白名单的条目
                appInfo.setIgonreApp(true);
                appInfo.setIsCouldUse(true);
                items_ignore.add(appInfo);
            } else {//是运行的条目
                appInfo.setIsCouldUse(true);
                appInfo.setIgonreApp(false);
                appInfo.setIsChecked(true);
                items_running.add(appInfo);
            }
        }

        items_current = items_total;
        items_total_size = items_total.size();
        items_ignore_size = items_ignore.size();
        items_running_size = items_running.size();
    }


    private void initView() {

        //真假button的获取
        mFl_button_false = (FrameLayout) findViewById(R.id.fl_button_false);
        mBt_booster_false = (Button) findViewById(R.id.bt_booster_false);

        mFl_button_true = (RippleView) findViewById(R.id.fl_button_true);
        mBt_booster_true = (Button) findViewById(R.id.bt_booster_true);
        mBt_booster_true.setOnClickListener(this);
        //listview露出
        mFl_child_include_list = (FrameLayout) findViewById(R.id.fl_child_include_list);

        //小圆部分描述信息
        mLl_root_small_desc = (LinearLayout) findViewById(R.id.ll_root_small_desc);

        //提示小箭头
        mRl_guide_arrow = (RelativeLayout) findViewById(R.id.guide_arrow);
        guide_arrow_1 = (ImageView) findViewById(R.id.guide_arrow_1);
        guide_arrow_2 = (ImageView) findViewById(R.id.guide_arrow_2);
        guide_arrow_3 = (ImageView) findViewById(R.id.guide_arrow_3);

        // 三段文字
        tv_size = (TextView) findViewById(R.id.tv_size);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_desc = (TextView) findViewById(R.id.tv_desc);

        //大圆部分
        iv_inner = (ImageView) findViewById(R.id.iv_inner);
        iv_outter = (ImageView) findViewById(R.id.iv_outter);
        iv_inner_total = (ImageView) findViewById(R.id.iv_inner_total);
        fl_root = (RelativeLayout) findViewById(R.id.fl_root);

        //点击动画开始小箭头
        mRl_guide_arrow = (RelativeLayout) findViewById(R.id.guide_arrow);


        /*#########第二次提交修改 start ###########*/
        mRl_ignore_label = (RelativeLayout) findViewById(R.id.rl_ignore_label);
        tv_ignore_size_label = (TextView) findViewById(R.id.tv_ignore_size_label);

        mLl_running_label_first = (RelativeLayout) findViewById(R.id.ll_running_label_first);
        ck_running_first = (CheckBox) findViewById(R.id.ck_running_first);
        tv_running_manager_first = (TextView) findViewById(R.id.tv_running_manager_first);
        tv_running_total_first = (TextView) findViewById(R.id.tv_running_total_first);

        ll_runnuing_label_second = (RelativeLayout) findViewById(R.id.ll_runnuing_label_second);
        ck_running_second = (CheckBox) findViewById(R.id.ck_running_second);
        tv_running_manager_second = (TextView) findViewById(R.id.tv_running_manager_second);
        tv_running_total_second = (TextView) findViewById(R.id.tv_running_total_second);

        tv_line = (TextView) findViewById(R.id.tv_line);
        /*#########第二次提交修改 end ###########*/

        mLv_1 = (ListView) findViewById(R.id.lv_1);
        mNanoboosterAdpter = new NanoboosterAdpter(MainActivity.this);
        if (!hasShortcut(this)) {
            createShortCut(this);
        }
        // 辅助功能弹框提示
        mAccessibilityDialog = (FrameLayout) findViewById(R.id.fl_accessibility_dialog);
        mAccessibilityAlertDialog =
            (RelativeLayout) findViewById(R.id.rl_accessibility_alert_dialog);
        mAccessibilityAlertDialogBg =
            (ImageView) findViewById(R.id.iv_accessibility_alert_dialog_bg);
        mAccessibilityEnableNowBtn = (RippleView) findViewById(R.id.btn_accessibility_enable_now);
        mAccessibilityEnableNowBtn.setOnClickListener(this);
    }

    private void initData() {

          /*###########第二次提交修改 start##############*/
        tv_ignore_size_label.setText(items_ignore_size + "");

        tv_running_manager_first.setText(items_running_size + "");
        tv_running_total_first.setText("(" + items_total_size + ")");

        tv_running_manager_second.setText(items_running_size + "");
        tv_running_total_second.setText("(" + items_total_size + ")");

         /*###########第二次提交修改 end  ##############*/

        mLv_1.setAdapter(mNanoboosterAdpter);

        mLv_1.setOnScrollListener(new AbsListView.OnScrollListener() {//实现动态布局的改变
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if (!ignore && !running) {
                    if (mLv_1.getFirstVisiblePosition() >= items_ignore.size()) {
                        tv_line.setVisibility(View.VISIBLE);
                        mLl_running_label_first.setVisibility(View.VISIBLE);
                        tv_running_manager_first.setText(items_running_size + "");
                        tv_running_total_first.setText("(" + items_total_size + ")");


                    }
                    if (mLv_1.getFirstVisiblePosition() < items_ignore.size()) {
                        tv_line.setVisibility(View.GONE);
                        mLl_running_label_first.setVisibility(View.GONE);

                    }
                    if (mLv_1.getLastVisiblePosition() >= items_ignore.size() + 1) {
                        ll_runnuing_label_second.setVisibility(View.GONE);

                    }
                    if (mLv_1.getLastVisiblePosition() <= items_ignore.size() + 1) {
                        ll_runnuing_label_second.setVisibility(View.VISIBLE);
                        tv_running_manager_second.setText(items_running_size + "");
                        tv_running_total_second.setText("(" + items_total_size + ")");

                    }
                }
            }
        });

        mLv_1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if ((ignore == false) && (running == false)) {//全部显示

                    if (position == 0) {//白名单头标题
                        //ignore = !ignore;
                    }
                    if (position == items_ignore.size() + 1) {//运行名单头标题
                        // running = !running;
                    }
                    if (position > items_ignore.size() + 1
                        && position < items_current.size() + 2) {//运行条目
                        //点击的就是内容条目
                        mNanoboosterAdpter.updateItemView(view, position);//实现局部的刷新

                    }
                    if (position < items_ignore.size() + 1 && position > 0) {
                        // TO DO
                    }

                }

            }
        });
    }


    public void changeIngoreAndRuningSet(AppInfo app) {

        //对两个集合进行操作了
        // mIterms_Running, mIterms_Ignore, mIterms_total,mIterms_delFrom_running
        if (app != null) {
            AppInfo af = new AppInfo();
            if (items_ignore.contains(app)) {

                af = app;

                items_ignore.remove(app);
                af.setIgonreApp(false);
                af.setIsChecked(true);
                af.setIsCouldUse(true);

                items_running.add(0, af);
                items_total.clear();
                items_total.addAll(items_ignore);
                items_total.addAll(items_running);

            }
        } else {

            if (items_delFrom_running != null && items_delFrom_running.size() > 0) {
                for (AppInfo appInfo : items_delFrom_running) {
                    if (items_running.contains(appInfo)) {

                        items_running.remove(appInfo);
                    }
                    if (!items_ignore.contains(appInfo)) {
                        appInfo.setIsCouldUse(true);
                        appInfo.setIgonreApp(true);
                        appInfo.setIsChecked(false);
                        items_ignore.add(0, appInfo);

                    }
                }
            }
            items_total.clear();
            items_total.addAll(items_ignore);
            items_total.addAll(items_running);
            items_delFrom_running = null;
            items_delFrom_running = new ArrayList<AppInfo>();
            /*###########改变############*/
            mNanoboosterAdpter.notifyDataSetChanged();
        }
        /*###########改变############*/
        // myadpter.notifyDataSetChanged();

    }


    public void selectAll(View view) {
            /*###########改变##############*/
        //running = !running;
        CheckBox cb = (CheckBox) view;
        isTitleChecked = !isTitleChecked;
        cb.setChecked(isTitleChecked);

        if (cb.isChecked()) {
            getRunningItemSize();//首先将当前选中条目的个数计算出来
            for (AppInfo app : items_running) {
                if (!app.isIgonreApp()) {
                    app.setIsChecked(true);
                }
            }
        }

        /*###########改变##############*/
        //mMyAdpter.notifyDataSetChanged();
    }

    /*###########第二次提交修改 start##############*/
    public void selectAll_first(View view) {
        selectAll(mNanoboosterAdpter.ck_running);
        SystemClock.sleep(200);
        mNanoboosterAdpter.notifyDataSetChanged();
    }

    public void selectAll_second(View view) {
        selectAll(mNanoboosterAdpter.ck_running);
    }

    /*###########第二次提交修改 end  ##############*/


    public void getRunningItemSize() {
        int temp = 0;
        for (AppInfo appInfo : items_running) {
            if (!appInfo.isChecked() && appInfo.isCouldUse()) {
                temp++;
            }
        }

        items_running_size += temp;
    }


    public void putIgnorePackname2SP(String appPackname) {
        if (mSp == null) {
            mSp = getSharedPreferences("config_packageName", MODE_PRIVATE);
        }

        String packageNames = mSp.getString("packageNames", "");

        if (!packageNames.contains(appPackname)) {
            appPackname = packageNames + appPackname + ",";
            mSp.edit().putString("packageNames", appPackname).commit();
        }

    }


    public void updateIgore(String packageNames) {
        if (mSp == null) {
            mSp = getSharedPreferences("config_packageName", MODE_PRIVATE);
        }

        mSp.edit().putString("packageNames", packageNames).commit();
    }

    public String getIgnorePacknameFromSP() {
        if (mSp == null) {
            mSp = getSharedPreferences("config_packageName", MODE_PRIVATE);
        }

        return mSp.getString("packageNames", "");
    }


    public void jumpToSecondActivity(View view) {
        Intent intent = new Intent(this, SecondTestActivity.class);
        startActivity(intent);
    }

    public void deleteFromSP(AppInfo appInfo) {

        String currentPackage = appInfo.getPackName() + ",";
        String packageNames = getIgnorePacknameFromSP();
        if (packageNames.contains(currentPackage)) {

            //int totalLen = packageNames.length();

            String firstPart = "";
            String secondPart = "";

            int firstEnd = packageNames.indexOf(currentPackage);
            int secondStart = firstEnd + currentPackage.length();

            firstPart = packageNames.substring(0, firstEnd);//不包括最后一个
            secondPart = packageNames.substring(secondStart);

            packageNames = firstPart + secondPart;

            updateIgore(packageNames);
        }
    }


    public boolean runningItermIsChecked() {

        for (AppInfo appInfo : items_running) {
            if (!(appInfo.isChecked() && appInfo.isCouldUse())) {
                return false;
            }
        }

        return true;
    }

    //code  of qixiangxiang (end)
    @Override
    protected void onResume() {
        super.onResume();

        //code of xiangxiang (start)
        changeIngoreAndRuningSet(null);

        if (mNanoboosterAdpter.ck_running != null) {
            isTitleChecked = runningItermIsChecked();
            mNanoboosterAdpter.ck_running.setChecked(isTitleChecked);
        }
        //code of xiangxiang (end)

        // 如果辅助功能已经打开，并且弹框显示，则隐藏弹框
        if (AccessibilityServiceUtil.isAccessibleEnabled(getApplicationContext())) {
            if (mAccessibilityDialog.getVisibility() == View.VISIBLE) {
                mAccessibilityDialog.setVisibility(View.GONE);
            }
        }
        if (FloatWindow.deepCleaning) {
            FloatWindow.getInstance(MainActivity.this).removeDeepCleanWindow();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.titlebar_icon:
                if (flag) {
                    flag = false;
//                    Animation animation = new TranslateAnimation(-mLinearLayout.getWidth(), 0, 0,
//                                                                 0);
                    Animation animation = new TranslateAnimation(0, mLinearLayout.getWidth(), 0,
                                                                 0);
                    animation.setDuration(duration);
                   /*   //设置背景颜色变暗
                      WindowManager.LayoutParams lp=getWindow().getAttributes();
                      lp.alpha=0.3f;
                      getWindow().setAttributes(lp);*/

                    animation.setFillAfter(true);
                    mLinearLayout.startAnimation(animation);

                    Animation alphaAnimation = new AlphaAnimation(anim1, anim2);
                    alphaAnimation.setDuration(duration);
                    alphaAnimation.setFillAfter(true);
                    //mIv_background.startAnimation(alphaAnimation);

                } else {
                    flag = true;
                    Animation animation =
                        new TranslateAnimation(mLinearLayout.getWidth(), 0, 0, 0);
                    //  rl_view.getBackground().setAlpha(60);
                    animation.setDuration(duration);
                    animation.setFillAfter(true);
                    mLinearLayout.startAnimation(animation);

                    Animation alphaAnimation = new AlphaAnimation(anim2, anim1);
                    alphaAnimation.setDuration(duration);
                    alphaAnimation.setFillAfter(true);
                    //mIv_background.startAnimation(alphaAnimation);

                }

                break;
            case R.id.ll_setting:
                break;
            case R.id.ll_update:
                break;
            case R.id.ll_feedback:
                break;
            case R.id.ll_about:
                break;
            case R.id.ll_good:
                break;
            case R.id.guide_arrow:
                //Toast.makeText(getApplicationContext(),"进来了",Toast.LENGTH_LONG).show();

                break;

            case R.id.bt_booster_true:
                if (AccessibilityServiceUtil.isAccessibleEnabled(getApplicationContext())) {
                    FloatWindow.getInstance(MainActivity.this).showDeepCleanWindow();
                    MemoryUtil.deepCleanMemory(MainActivity.this, items_running);
                } else {
                    mAccessibilityDialog.setVisibility(View.VISIBLE);

                    ObjectAnimator translateYAnim =
                        ObjectAnimator.ofFloat(mAccessibilityAlertDialog, "translationY",
                                               mAccessibilityAlertDialog.getMeasuredHeight(),
                                               0);//Y轴平移旋转
                    ObjectAnimator alphaAnim =
                        ObjectAnimator.ofFloat(mAccessibilityAlertDialogBg, "alpha", 0, 1);

                    AnimatorSet set = new AnimatorSet();
                    set.playTogether(translateYAnim, alphaAnim);
                    set.setDuration(mAccessibilityAnimDuration);
                    set.setInterpolator(new DecelerateInterpolator());
                    set.start();
                }
                break;

            case R.id.btn_accessibility_enable_now:
                AccessibilityServiceUtil.openAccessibilitySetting(MainActivity.this);
                break;
        }
    }


    //开始初始动画
    private void startAnimation() {

        iv_inner.startAnimation(asInner);
        iv_inner_total.startAnimation(aaInner_total);
        iv_outter.startAnimation(asOutter);

    }

    @Override
    public void onBackPressed() {
        FloatWindow.getInstance(MainActivity.this).removeDeepCleanWindow();
        if (System.currentTimeMillis() - mExitTime > 2000) {
            Toast.makeText(this, R.string.exit_warn, Toast.LENGTH_SHORT)
                .show();
            mExitTime = System.currentTimeMillis();
        } else {
            super.onBackPressed();
            finish();
        }
    }

    public void doAnimation() {

        //大圆动画完成之后需要执行以下动画

        //大图位移与缩放
        iv_inner.clearAnimation();
        iv_inner_total.clearAnimation();
        iv_outter.clearAnimation();
        fl_root.startAnimation(as_root);

        //小圆部分的描述信息的渐变-->显示
        mLl_root_small_desc.startAnimation(aa_small_desc);

        //三段文字描述信息的 渐变-->隐藏
        tv_size.startAnimation(aa_mAnim_tv_size);

        //提示小箭头的隐藏

        //listView全部显示出来
        oa_fl_child_include_list_total.start();


    }

    /**
     * 判断桌面是否已经有了快捷方式
     */
    public boolean hasShortcut(Activity activity) {
        String url = "";
        url =
            "content://" + getAuthorityFromPermission(activity,
                                                      "com.android.launcher.permission.READ_SETTINGS")
            + "/favorites?notify=true";
        ContentResolver resolver = activity.getContentResolver();
        Cursor
            cursor =
            resolver.query(Uri.parse(url), new String[]{"title", "iconResource"}, "title=?",
                           new String[]{getString(R.string.app_name).trim()}, null);
        if (cursor != null && cursor.moveToFirst()) {
            cursor.close();
            return true;
        }
        return false;
    }

    private String getAuthorityFromPermission(Context context, String permission) {
        if (permission == null) {
            return null;
        }
        List<PackageInfo>
            packs =
            context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
        if (packs != null) {
            for (PackageInfo pack : packs) {
                ProviderInfo[] providers = pack.providers;
                if (providers != null) {
                    for (ProviderInfo provider : providers) {
                        if (permission.equals(provider.readPermission)) {
                            return provider.authority;
                        }
                        if (permission.equals(provider.writePermission)) {
                            return provider.authority;
                        }
                    }
                }
            }
        }
        return null;
    }
}
