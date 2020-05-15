package com.taobao.fleamarket.ponds;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.TextView;

import com.taobao.android.notificationcenter.Notification;
import com.taobao.android.notificationcenter.NotificationCenter;
import com.taobao.android.notificationcenter.NotificationReceiver;
import com.taobao.android.notificationcenter.Observer;
import com.taobao.fleamarket.R;
import com.taobao.fleamarket.activity.base.BaseFragmentActivity;
import com.taobao.fleamarket.activity.login.UserLoginInfo;
import com.taobao.fleamarket.activity.search.HistoryAndSuggestActivity;
import com.taobao.fleamarket.annotation.function.pagetype.PageTypeCategory;
import com.taobao.fleamarket.annotation.type.DataManager;
import com.taobao.fleamarket.annotation.type.PageName;
import com.taobao.fleamarket.annotation.type.PageType;
import com.taobao.fleamarket.bean.FishPondInfo;
import com.taobao.fleamarket.bean.FishPondParameter;
import com.taobao.fleamarket.bean.PondTopicInfo;
import com.taobao.fleamarket.bean.SearchRequestParameter;
import com.taobao.fleamarket.datamanage.callback.CallBack;
import com.taobao.fleamarket.floatingLayer.FloatingViewActivity;
import com.taobao.fleamarket.function.archive.TBSUtil;
import com.taobao.fleamarket.guide.GuideDo;
import com.taobao.fleamarket.ponds.model.AdminUserInfoItemDO;
import com.taobao.fleamarket.ponds.model.InfoItemDO;
import com.taobao.fleamarket.ponds.service.IPondService;
import com.taobao.fleamarket.ponds.service.PondServiceImpl;
import com.taobao.fleamarket.ponds.view.InfoItemView;
import com.taobao.fleamarket.ponds.view.PondInfo;
import com.taobao.fleamarket.ponds.view.PondPullToRefreshListView;
import com.taobao.fleamarket.ponds.view.PondTabHost;
import com.taobao.fleamarket.ponds.view.PondTitleBar;
import com.taobao.fleamarket.post.bean.ItemPostDO;
import com.taobao.fleamarket.post.publish.v3.PostAction;
import com.taobao.fleamarket.post.util.PostController;
import com.taobao.fleamarket.share.ShareParam;
import com.taobao.fleamarket.share.ShareSDK;
import com.taobao.fleamarket.ui.CommonPageStateView;
import com.taobao.fleamarket.ui.listview.pulltorefresh.OnScrollFishListener;
import com.taobao.fleamarket.ui.tabview.OnTabStateChangedListener;
import com.taobao.fleamarket.ui.widget.FishTextView;
import com.taobao.fleamarket.user.person.userinfo.UserInfoActivity;
import com.taobao.fleamarket.util.AlertDialogUtil;
import com.taobao.fleamarket.util.ApplicationUtil;
import com.taobao.fleamarket.util.DensityUtil;
import com.taobao.fleamarket.util.IntentUtils;
import com.taobao.fleamarket.util.Log;
import com.taobao.fleamarket.util.MediaPlayer;
import com.taobao.fleamarket.util.NotificationConstants;
import com.taobao.fleamarket.util.SafeDeprecatedMethodUtil;
import com.taobao.fleamarket.util.StringUtil;
import com.taobao.fleamarket.util.Toast;
import com.taobao.fleamarket.webview.WebViewController;
import com.taobao.statistic.CT;
import com.taobao.statistic.TBS;

import java.util.HashMap;
import java.util.Map;

@PageName("FishPool")
@PageType(PageTypeCategory.FEEDS)
public class PondActivity extends BaseFragmentActivity implements View.OnClickListener,
        OnTabStateChangedListener,
        CommonPageStateView.ActionExecutor, InfoItemView.OnGotoListener, PondAction.PondActionCallback {
    private static final String TAG = "PondActivity";
    private static final String TAB_INDEX = "tab_index";

    public static final int PUSH_2_TOP = 0x101;
    public static final int SCREEN_ITEM = 0x102;
    public static final int DELETE_ITEM = 0x103;

    private View rlJoinButton;
    private View mJoinButton;
    private FishTextView mJoinTextView;
    private View mPublishButton;
    private View lnPublishButton;
    private PondPullToRefreshListView mListView;
    private PondInfo mPondInfoViewGroup;
    private TextView mPondTip;
    private View mScrollToTopButton;
//    private PondInfoBoard mBillboard;
//    protected PondInfoMembers mPondInfoMembers;
    private PondTabHost mFixedTabHost;
    private PondTabHost mSuspendedTabHost;
    private PondTitleBar mTitleBar;
    private PondTitleBar mTitleBarTransparent;
    private PondHeaderController mPondHeaderController;

    private PondItemsProvider mItemsProvider;

    @DataManager(PondServiceImpl.class)
    private IPondService mPondService;

    private int mCurrentTab = 0;
    private Long fishPondId;
    private String publishedItemId;
    private FishPondInfo mFishPondInfo;
    private CommonPageStateView mStateView;

    private String mCurrentLoginUserId;
    private String mCurrentTabName;

    private PondAction mPondAction;
    private boolean isStarPond;
    private boolean firstCreated;
    private String createSuccessTitle;
    private String createSuccessTip;

    private Observer itemPublishedFromPondObserver;
    private boolean needRefresh;
    private boolean mShouldAutoPopShareWindow;
    private int mheight = 0;

    @Override
    public void parseSchemeIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        Uri uri = intent.getData();
        if (uri == null) {
            return;
        }
        String pondId = uri.getQueryParameter("id");
        if (!StringUtil.isEmpty(pondId)) {
            try {
                fishPondId = Long.parseLong(pondId);
            } catch (NumberFormatException e) {
                //ignore
            }
        }
        String tabIndex = uri.getQueryParameter("index");
        if (!StringUtil.isEmpty(tabIndex)) {
            try {
                mCurrentTab = Integer.parseInt(tabIndex);
            } catch (NumberFormatException e) {
                //ignore
            }
        }

        //从鱼塘分享列表过来，需要判断是否需要自动拉起分享window
        String share = uri.getQueryParameter("share");
        if (!StringUtil.isEmpty(share) && "1".equals(share)) {
            mShouldAutoPopShareWindow = true;
        }

        publishedItemId = uri.getQueryParameter("publishedItemId");
        String firstCreated = uri.getQueryParameter("CreateSuccess");
        if (StringUtil.isNotBlank(firstCreated)) {
            this.firstCreated = true;
        }
        createSuccessTitle = uri.getQueryParameter("msg");
        createSuccessTip = uri.getQueryParameter("subMsg");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        initCurrentLoginUser();
        try {
            initParam();
        } catch (Exception e) {
        }
        initView();
        loadData();
        initObserver();

        if (firstCreated) {
            FloatingViewActivity.startCreatePondSuccess(this, createSuccessTitle, createSuccessTip);
        }
        TBSUtil.ctrlClicked(PondActivity.this, "EnterFishPool", "Fish_Pool_id=" + fishPondId);

    }

    private void initObserver() {
        if (itemPublishedFromPondObserver == null) {
            itemPublishedFromPondObserver = NotificationCenter.defaultCenter().addObserver(NotificationConstants.PUBLISH_FROM_POND,
                    new NotificationReceiver() {
                        @Override
                        public void receive(Notification notification) {
                            if (notification == null) {
                                return;
                            }
                            if (notification.userInfo().containsKey("pond_id") &&
                                    notification.userInfo().get("pond_id") instanceof Long) {
                                long pondId = (Long) notification.userInfo().get("pond_id");
                                if (pondId == fishPondId) {
                                    publishedItemId = (String) notification.userInfo().get("item_id");
                                    needRefresh = true;
                                }
                            }
                        }
                    });
        }
    }

    private void initCurrentLoginUser() {
        if (UserLoginInfo.getInstance().isLogin()) {
            mCurrentLoginUserId = UserLoginInfo.getInstance().getUserId();
        } else {
            mCurrentLoginUserId = StringUtil.EMPTY;
        }
    }

    private void initParam() {
        String pondId = getIntent().getStringExtra("id");
        if (!StringUtil.isEmpty(pondId)) {
            try {
                fishPondId = Long.parseLong(pondId);
            } catch (NumberFormatException e) {
                //ignore
            }
        }
        if (getIntent().hasExtra("publishedItemId")) {
            publishedItemId = IntentUtils.getStringExtra(getIntent(), "publishedItemId");
        }
    }

    private void initView() {
        setContentView(R.layout.pond_main);
        initStateView();
        initListView();
        initPostButton();
        initJoinButton();
        initActionBar();
        initPondAction();
    }

    private void initPondAction() {
        mPondAction = new PondAction(this);
        mPondAction.setPondActionCallback(this);
    }

    private void initStateView() {
        mStateView = (CommonPageStateView) findViewById(R.id.pond_state_view);
        mStateView.setActionExecutor(this);
    }

    private void initListView() {
        mListView = (PondPullToRefreshListView) findViewById(R.id.list_view);

        //TODO mPondTip 这个是显示在鱼塘顶部的，需要确认在鱼塘改版后该显示在哪里
        mPondTip = (TextView) findViewById(R.id.pond_tip);

        mScrollToTopButton = findViewById(R.id.scroll_to_top_button);

        //浮动的titleBar
        mTitleBar = (PondTitleBar)findViewById(R.id.title_bar);

        initListViewHeader();

        mListView.setOnScrollListener(new OnScrollFishListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (view.getAdapter() == null) {
                    showFixedTab();
                    showFixedTitlebar();
                    return;
                }
                if (firstVisibleItem >= 1) {
                    android.util.Log.d("andymao2",String.format("onScroll,firstVisibleItem=%d,mListView.getHeaderViewsCount()=%d" +
                            ",mFixedTabHost.getTop()=%d,first.getTop()=%d",firstVisibleItem,1,mFixedTabHost.getTop(),0));
                    showSuspendedTab();
                    showSuspendedTitlebar();
                } else {
                    View first = view.getChildAt(0);
                    android.util.Log.d("andymao2",String.format("onScroll,firstVisibleItem=%d,mListView.getHeaderViewsCount()=%d" +
                            ",mFixedTabHost.getTop()=%d,first.getTop()=%d",firstVisibleItem,1,mFixedTabHost.getTop(),first.getTop()));
                    if (first != null) {
                        int top = first.getTop();
                        int tabTop = mFixedTabHost.getTop()-DensityUtil.dip2px(PondActivity.this,48) + top;

                        Log.d("andymao2","tapTop="+tabTop);
                        if (tabTop < 0) {
                            showSuspendedTab();
                        } else {
                            showFixedTab();
                        }

                        if(Math.abs(top)<DensityUtil.dip2px(PondActivity.this,160)){
                            showFixedTitlebar();
                        }else{
                            showSuspendedTitlebar();
                        }
                    }
                }

                if (visibleItemCount + firstVisibleItem == totalItemCount) {
                    if (mItemsProvider != null) {
                        mItemsProvider.addMore();
                    }
                }

                if (firstVisibleItem >= 10) {
                    mScrollToTopButton.setVisibility(View.VISIBLE);
                } else {
                    mScrollToTopButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void loadBigImage(AbsListView absListView) {
                if (mItemsProvider != null) {
                    mItemsProvider.loadImage(absListView);
                }
            }
        });

        mListView.addDecoratedRefreshListener(new PondPullToRefreshListView.RefreshListener(){

            @Override
            public void onPullToRefresh(int value) {

            }

            @Override
            public void onRefreshing() {
                clearState();
                if (mFishPondInfo != null && mFishPondInfo.isAdmin()) {
                    refreshTop(fishPondId, false, false);
                } else if (mItemsProvider != null) {
                    mItemsProvider.refreshTop();
                    TBS.Adv.ctrlClicked(CT.Button, "Refresh", "topic_name=" + mCurrentTabName);
                }

                mListView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mListView.onRefreshComplete();
                    }
                },1000);
            }

            @Override
            public void onReset() {

            }
        });

//        mPullRefreshView.listener(new OnRefreshListener() {
//            @Override
//            public void onRefreshStarted() {
//                clearState();
//                if (mFishPondInfo != null && mFishPondInfo.isAdmin()) {
//                    refreshTop(fishPondId, false, false);
//                } else if (mItemsProvider != null) {
//                    mItemsProvider.refreshTop();
//                    TBS.Adv.ctrlClicked(CT.Button, "Refresh", "topic_name=" + mCurrentTabName);
//                }
//            }
//        });

        mScrollToTopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollToTop();
            }
        });
    }

    private void showSuspendedTab() {
        mIsHeaderVisible = false;
        mSuspendedTabHost.setVisibility(View.VISIBLE);
        mFixedTabHost.setVisibility(View.INVISIBLE);
    }

    private void showFixedTab() {
        mIsHeaderVisible = true;
        mFixedTabHost.setVisibility(View.VISIBLE);
        mSuspendedTabHost.setVisibility(View.INVISIBLE);
    }
    private void showSuspendedTitlebar() {
        mIsHeaderVisible = false;
        mTitleBar.setVisibility(View.VISIBLE);
        mTitleBarTransparent.setVisibility(View.INVISIBLE);
    }

    private void showFixedTitlebar() {
        mIsHeaderVisible = true;
        mTitleBarTransparent.setVisibility(View.VISIBLE);
        mTitleBar.setVisibility(View.INVISIBLE);
    }

    private void initListViewHeader() {
        View header = LayoutInflater.from(this).inflate(R.layout.pond_header, null);


        //设置titleBar成为透明的背景
        View titleBarRoot =  mTitleBarTransparent.findViewById(R.id.title_root);
        ColorDrawable drawable = new ColorDrawable(getResources().getColor(R.color.transparent));
        SafeDeprecatedMethodUtil.setImageDrawable(titleBarRoot,drawable);

        mPondHeaderController = new PondHeaderController();
        mPondHeaderController.init(this,header);

        //mPondHeaderController 会根据下拉刷新的状态决定是否展示loadingView, loadingTips
        mListView.addDecoratedRefreshListener(mPondHeaderController);

        mPondInfoViewGroup = (PondInfo) header.findViewById(R.id.pond_info);

        //去掉移动到群聊页面
        //initBillboard(header);

        //干掉，换成一个icon
        //initPondMembers(header);
        initCategory(header);

        mListView.getRefreshableView().addHeaderView(header);
    }

//    private void initBillboard(View headerView) {
//        mBillboard = (PondInfoBoard) headerView.findViewById(R.id.pond_billboard);
//    }

//    private void initPondMembers(View headerView) {
//        mPondInfoMembers= (PondInfoMembers) headerView.findViewById(R.id.pond_info_members);
//    }

    private void initCategory(View headerView) {
        mFixedTabHost = (PondTabHost) headerView.findViewById(R.id.pond_tab_host_fixed);
        mFixedTabHost.setTabStateListener(this);

        mSuspendedTabHost = (PondTabHost) findViewById(R.id.pond_tab_host_suspended);
        mSuspendedTabHost.setTabStateListener(this);

        mFixedTabHost.addMirrorImage(mSuspendedTabHost);
    }

    private void initPostButton() {
        mPublishButton = findViewById(R.id.pond_publish_button);
        lnPublishButton = findViewById(R.id.ln_pond_publish_button);
        if (mPublishButton != null) {
            mPublishButton.setOnClickListener(this);
        }
    }

    private void initJoinButton() {
        mJoinButton = findViewById(R.id.pond_join_button);
        rlJoinButton = findViewById(R.id.rl_pond_join_button);
        mJoinTextView = (FishTextView) findViewById(R.id.pond_join_textView);
        if (mJoinButton != null) {
            mJoinButton.setOnClickListener(this);
        }
    }

    /**
     * 加载页面数据
     */
    private void loadData() {
        if (fishPondId != null && fishPondId > 0) {
            refreshTop(fishPondId, false, !StringUtil.isEmpty(publishedItemId));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pond_publish_button:
                gotoPost();
                break;
            case R.id.pond_join_button:
                joinPond();
                break;
        }
    }

    private void gotoPost() {
        if (mFishPondInfo == null) {
            return;
        }
        if (mFishPondInfo.isInPondSilenceList != null && mFishPondInfo.isInPondSilenceList) {
            Toast.showText(PondActivity.this, "塘主禁止您在该鱼塘发布\r\n" +
                    "7天内不能在鱼塘内发布宝贝");
            return;
        }

        ItemPostDO itemPostDO = new ItemPostDO();
        itemPostDO.setFishPoolId(fishPondId);
        itemPostDO.setFishPoolName(mFishPondInfo.poolName);
        itemPostDO.setTitle("#" + mCurrentTabName + "#");
        itemPostDO.setFromPond(true);
        //明星鱼塘默认不谈钱
        if (isStarPond) {
            itemPostDO.setAuctionType(PostAction.AUCTION_TYPE_TIE);
        }
        PostController.startActivityMultiChoice(this, itemPostDO);
        TBS.Adv.ctrlClicked(CT.Button, "Release", "Fish_Pool_id=" + fishPondId);
    }

    private void joinPond() {
        if(mFishPondInfo != null && StringUtil.isNotBlank(mFishPondInfo.signInUrl)) {
            //需要报名
            FloatingWebView.startActivity(this, mFishPondInfo.signInUrl);
        } else {
            mPondAction.join();
        }
    }

    private String getCurrentTabName() {
        String tabName = StringUtil.EMPTY;

        if (mFishPondInfo != null && mFishPondInfo.topicList != null && mCurrentTab < mFishPondInfo.topicList.size()) {
            PondTopicInfo topicInfo = mFishPondInfo.topicList.get(mCurrentTab);
            if (topicInfo != null) {
                tabName = topicInfo.topicName;
            }
        }
        PondTrackAids.getInstance().setTrackInfo(PondTrackAids.INFO_CURRENT_TAB_NAME, tabName);
        return tabName;
    }

    public void refresh(){
        loadData();
    }

    private void refreshTop(Long pondId, boolean onlyPondInfo, boolean defaultTopic) {
        if (!onlyPondInfo) {
            mStateView.setPageLoading();
        }
        loadPondInfo(pondId, onlyPondInfo, defaultTopic);
    }

    private void loadPondInfo(Long fishPondId, final boolean onlyPondInfo, final boolean defaultTopic) {
        FishPondParameter parameter = new FishPondParameter();
        parameter.setFishPoolId(fishPondId);
        parameter.setLat(ApplicationUtil.getFishApplicationInfo().getLat());
        parameter.setLang(ApplicationUtil.getFishApplicationInfo().getLon());

        mPondService.getPondInfo(parameter, new CallBack<IPondService.FishPondInfoResponse>(this) {
            @Override
            public void callBack(IPondService.FishPondInfoResponse responseParameter) {
//                mPullRefreshView.onRefreshComplete();
                mListView.onRefreshComplete();
                if (responseParameter != null && responseParameter.getData() != null) {
                    fillPondInfo(responseParameter.getData().defaultFishPool, onlyPondInfo, defaultTopic);
                    mPondAction.updatePondData(responseParameter.getData().defaultFishPool);
                    mStateView.setVisibility(View.GONE);

                    updateTitleBar();
                } else {
                    mStateView.setPageError();
                }
            }
        });
    }

    public void updateTitleBar(){
        if (StringUtil.isEmptyOrNullStr(mPondAction.getAvailableAction())) {
            mTitleBar.hideBarMore(true);
            mTitleBarTransparent.hideBarMore(true);
        }else{
            mTitleBar.hideBarMore(false);
            mTitleBarTransparent.hideBarMore(false);
        }

    }

    private void fillPondInfo(FishPondInfo fishPondInfo, boolean onlyPondInfo, final boolean defaultTopic) {
        mFishPondInfo = fishPondInfo;

        //自动弹出分享window
        if(mShouldAutoPopShareWindow ){
            //防止用户下拉刷新，又弹出分享window
            mShouldAutoPopShareWindow = false;

            //从鱼塘分享列表跳过来的，需要自动拉起分享window
            share();
        }

        isStarPond = mFishPondInfo.isStartPond();

        mPondInfoViewGroup.updatePondInfo(fishPondInfo);
        mPondHeaderController.setPondInfo(fishPondInfo);
        mPondHeaderController.setPondAction(mPondAction);
//        mPondInfoMembers.setPondInfo(fishPondInfo);
//        mPondInfoMembers.setPondAction(mPondAction);

//        mPondInfoMembers.setPondAction(mPondAction);

//        mBillboard.updateBillboard(fishPondInfo);
//        for (int i = 0; i < mBillboard.getChildCount(); i++) {
//            ((InfoItemView) (mBillboard.getChildAt(i))).setOnGotoListener(this);
//        }
        if (!onlyPondInfo) {
            mFixedTabHost.initTab(fishPondInfo.topicList);
            mSuspendedTabHost.initTab(fishPondInfo.topicList);

            if (mItemsProvider != null) {
                mItemsProvider.clearListState();
            }

            mItemsProvider = new PondItemsProvider(this, mListView.getRefreshableView(), fishPondInfo, mPondService);

            if (fishPondInfo.bannerList != null) {
                mItemsProvider.setBannerList(fishPondInfo.bannerList);
            }
            if (!StringUtil.isEmpty(publishedItemId)) {
                mItemsProvider.setPublishedItemId(publishedItemId);
            }

            if (defaultTopic) {
                mCurrentTab = mFixedTabHost.getDefaultTopicTab();
            }

            //加载第一个tab的第一页数据
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mFixedTabHost.setCurrentTab(mCurrentTab);
                    mCurrentTabName = getCurrentTabName();
                }
            }, 300);

        } else if (mItemsProvider != null) {
            mItemsProvider.updatePondInfo(mFishPondInfo);
        }

        if (fishPondInfo.isAlreadyLike || fishPondInfo.isAdmin()) {
            rlJoinButton.setVisibility(View.GONE);
            mJoinButton.setVisibility(View.GONE);
        } else {
            rlJoinButton.setVisibility(View.VISIBLE);
            mJoinButton.setVisibility(View.VISIBLE);
            if (mFishPondInfo.signInUrl != null && !mFishPondInfo.signInUrl.isEmpty()){
                mJoinTextView.setText("申请加入鱼塘");
            }
            if(GuideDo.getInstance().getGuidePondJoin()<1){
                findViewById(R.id.ptiv_pond_join_button).setVisibility(View.VISIBLE);
                GuideDo.getInstance().updateGuidePondJoin();
            }
        }

        if (fishPondInfo.isAllowPublish) {
            lnPublishButton.setVisibility(View.VISIBLE);
            mPublishButton.setVisibility(View.VISIBLE);
            if(GuideDo.getInstance().getGuidePondPublish()<1){
                findViewById(R.id.ptiv_pond_publish_button).setVisibility(View.VISIBLE);
                GuideDo.getInstance().updateGuidePondPublish();
            }
        } else {
            lnPublishButton.setVisibility(View.GONE);
            mPublishButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onTabSelected(int position) {
        Log.d("andymao6","onTabSelected");
        stopVoice();

        setCurrentTab(position);
    }

    @Override
    public void onTabReselected(int position) {
        Log.d("andymao6","onTabReselected");
        if (!mIsHeaderVisible) {
            scrollToTop();
        }
    }

    private void scrollToTop() {
        int index = mListView.getRefreshableView().getHeaderViewsCount() - 1;
        Log.d("andymao6","index="+index);
        if (mListView.getRefreshableView().getFirstVisiblePosition() < 15) {
            Log.d("andymao6","index="+index);
            mListView.getRefreshableView().smoothScrollToPositionFromTop(index, -mFixedTabHost.getTop());
        } else {
            mListView.getRefreshableView().setSelectionFromTop(index, -mFixedTabHost.getTop() - 10);
            mListView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mItemsProvider != null) {
                        mItemsProvider.loadImage(mListView.getRefreshableView());
                    }
                }
            }, 50);
        }
    }

    @Override
    public void onTabUnselected(int position) {
        if (position < 0) {
            return;
        }
        int index = mListView.getRefreshableView().getFirstVisiblePosition();
        View v = mListView.getRefreshableView().getChildAt(0);
        int top = (v == null) ? 0 : v.getTop();
        Log.d("andymao5", "onTabUnselected==>first visible : " + index+",top = "+top+",mIsHeaderVisible="+mIsHeaderVisible);

        Position currentPosition = new Position();
        currentPosition.index = index;
        currentPosition.top = top;
        mPositions.put(position, currentPosition);

        if (mIsHeaderVisible) {
            mCommonPosition.index = index;
            mCommonPosition.top = top;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isLoginUserChanged() || needRefresh) {
            loadData();
            needRefresh = false;
        }

        PondTrackAids.getInstance().start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PondTrackAids.getInstance().destroy();
        NotificationCenter.defaultCenter().removeObserver(itemPublishedFromPondObserver);
        mPondHeaderController = null;
    }

    @Override
    public void onBarMoreClick() {
        if (mFishPondInfo == null || mPondAction == null) {
            return;
        }
        if (!StringUtil.isEmptyOrNullStr(mPondAction.getAvailableAction())) {
            AlertDialogUtil.builderItemsSelect(this, null, new String[]{mPondAction.getAvailableAction()},
                    new AlertDialogUtil.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, String name, int which) {
                            switch (which) {
                                case 0: {
                                    //管理或关注相关
                                    mPondAction.doAction();
                                }
                                break;
                                default:
                                    break;
                            }
                        }
                    });
        }
    }

    @Override
    public void onBarRightExtraClick() {
        fishPondSearch();
    }
    @Override
    public void onBarRightClick() {
        share();
    }
    private void fishPondSearch() {
        SearchRequestParameter searchRequestParameter = new SearchRequestParameter();
        try {
            searchRequestParameter.fishpoolId = String.valueOf(mFishPondInfo.id);
        } catch (Exception e) {
            return;
        }

        TBSUtil.ctrlClicked(PondActivity.this, "Search");
        HistoryAndSuggestActivity.startHistoryActivity(this, searchRequestParameter, null);
    }

    private void share() {
        TBS.Adv.ctrlClicked(CT.Button, "ShareFishPool", "Fish_Pool_id=" + fishPondId);
        if (fishPondId == null || mFishPondInfo == null || mFishPondInfo.shareUrl == null) {
            return;
        }

        ShareParam shareParam = new ShareParam();
        shareParam.setImageUrl(mFishPondInfo.iconUrl);
        shareParam.setText("" + mFishPondInfo.poolName);
        shareParam.setTitle("好炫酷的鱼塘！快猛戳进来鱼塘一起玩儿！");
        shareParam.setUrl(mFishPondInfo.shareUrl);
        TBS.Adv.ctrlClicked(CT.Button, "Sharing");
        ShareSDK.getInstance(this, ShareSDK.FISHPOOL,mFishPondInfo.id+"",shareParam).show();
    }

    public void initActionBar() {

        mTitleBar = (PondTitleBar) findViewById(R.id.title_bar);
        mTitleBar.setBarClickInterface(this);
        mTitleBar.setTitle("鱼塘");
    }

    public static void startActivity(Context context, String pondId) {
        if (context == null || StringUtil.isEmptyOrNullStr(pondId)) return;
        Intent intent = new Intent(context, PondActivity.class);
        intent.putExtra("id", pondId);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, String pondId, String itemId) {
        if (context == null || StringUtil.isEmptyOrNullStr(pondId)) return;
        Intent intent = new Intent(context, PondActivity.class);
        intent.putExtra("id", pondId);
        intent.putExtra("publishedItemId", itemId);
        context.startActivity(intent);
    }

    private void clearState() {
        mCommonPosition.top = 0;
        if (mPositions.containsKey(mCurrentTab)) {
            mPositions.remove(mCurrentTab);
        }
    }

    private void stopVoice() {
        MediaPlayer.getInstance(this).stopAudio();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopVoice();
        PondTrackAids.getInstance().pause();
    }

    private void setCurrentTab(int index) {
        int selection = 0;
        int selectionTop = 0;

        if (mIsHeaderVisible && (mCommonPosition.top + mFixedTabHost.getTop()) > 0) {
            selection = mCommonPosition.index;
            selectionTop = mCommonPosition.top;
        } else if (mPositions.containsKey(index)) {
            Position position = mPositions.get(index);
            if (position != null) {
                selection = position.index;
                selectionTop = position.top;
                Log.d("andymao5","position.index="+position.index+",position.top="+position.top);
                if(selection==0){
                    if(mheight==0){
                        mheight = mFixedTabHost.getBottom()-mFixedTabHost.getHeight()-mTitleBar.getHeight();
                    }
                    selectionTop = -mheight;
                }
            }
        } else {
            selection = 0;
            if(mheight==0){
                mheight = mFixedTabHost.getBottom()-mFixedTabHost.getHeight()-mTitleBar.getHeight();
            }
            selectionTop = -mheight;
        }
        int paddingBottom = 0;
        if (mPublishButton != null && mPublishButton.getVisibility() == View.VISIBLE) {
            paddingBottom = mPublishButton.getHeight();
        }
        Log.d("andymao5","selectionTop="+selectionTop+",paddingBottom="+paddingBottom+",selection="+selection+",mCommonPosition.top="
                +mCommonPosition.top+",mFixedTabHost.getTop()="+mFixedTabHost.getTop());
        mItemsProvider.setCurrentAdapter(index, selection, selectionTop, paddingBottom);
        mCurrentTab = index;
        mCurrentTabName = getCurrentTabName();

        //切换tab埋点
        TBS.Adv.ctrlClicked(CT.Button, "Topic", "Fish_Pool_id=" + fishPondId,
                "topic_name=" + mCurrentTabName);
    }

    @Override
    public void onActionRefresh() {
        Log.d("andymao","onActionRefresh");
        loadData();
    }

    @Override
    public void onGotoItemDetail(InfoItemDO infoItemDO) {
        switch (infoItemDO.getItemType()) {
            case InfoItemDO.POND_ANNONCEMENT:
                if (!StringUtil.isEmpty(infoItemDO.getJumpUrl())) {
                    WebViewController.startActivity(this, infoItemDO.getJumpUrl(), StringUtil.EMPTY);
                }
                break;
            case InfoItemDO.POND_ADMIN_INFO:
                if (mPondAction.hasAdmin()) {
                    AdminUserInfoItemDO adminUserInfoItemDO = (AdminUserInfoItemDO) infoItemDO;
                    Intent intent = UserInfoActivity.createIntent(this, adminUserInfoItemDO.getUserNick());
                    startActivity(intent);
                } else {
                    mPondAction.tryToSignUpAdmin();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onJoin(IPondService.JoinResponse.ResultData resultData) {
        if (resultData.likeResult) {
            hideJoinButton();
            if (resultData.allowPublish != null && resultData.allowPublish) {
                showPublishButton();
            }

            startActivityForResult(WelcomePondActivity.createIntent(PondActivity.this, mFishPondInfo), WelcomePondActivity.REQUEST_CODE);
        } else if (StringUtil.isNotBlank(resultData.msg)) {
            Toast.showText(PondActivity.this, resultData.msg);
        } else {
            Toast.showText(PondActivity.this, "加入失败，请稍后再试");
        }
    }


    @Override
    public void onQuit(IPondService.JoinResponse.ResultData resultData) {
        if (resultData.likeResult) {
            Toast.showText(PondActivity.this, "您已退出此鱼塘");
            refresh();
//            showJoinButton();
//            hidePublishButton();
        } else if (StringUtil.isNotBlank(resultData.msg)) {
            Toast.showText(PondActivity.this, resultData.msg);
        } else {
            Toast.showText(PondActivity.this, "退出失败，请稍后再试");
        }
    }

    private class Position {
        int index;
        int top;
    }

    private Position mCommonPosition = new Position();
    private Map<Integer, Position> mPositions = new HashMap<Integer, Position>();
    private boolean mIsHeaderVisible;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == SCREEN_ITEM) {
            if (data != null && data.getExtras() != null) {
                String itemId = data.getExtras().getString("itemId");
                if (itemId != null && mItemsProvider != null) {
                    mItemsProvider.deleteItem(itemId);
                }
            }
        }

        if (resultCode == PUSH_2_TOP) {
            if (data != null && data.getExtras() != null) {
                Bundle bundle = data.getExtras();
                String itemId = bundle.getString("itemId");
                boolean isTop = bundle.getBoolean("isTop");

                if (itemId != null && mItemsProvider != null) {
                    mItemsProvider.pushToTop(itemId, isTop);
                }
            }
        }

        if (resultCode == DELETE_ITEM) {
            if (data != null && data.getExtras() != null) {
                String itemId = data.getExtras().getString("itemId");
                if (itemId != null && mItemsProvider != null) {
                    mItemsProvider.deleteItem(itemId);
                }
            }
        }

        if(requestCode==WelcomePondActivity.REQUEST_CODE){
            refresh();
        }
    }

    private boolean isLoginUserChanged() {
        String currentUserId = StringUtil.defaultIfBlank(UserLoginInfo.getInstance().getUserId(), StringUtil.EMPTY);
        return !currentUserId.equals(mCurrentLoginUserId);
    }

    private void showJoinButton() {
        showWidgetFromBottom(mJoinButton, 60);
    }

    private void hideJoinButton() {
        hideWidgetToBottom(mJoinButton, 60);
    }

    private void showPublishButton() {
        showWidgetFromBottom(mPublishButton, 85);
    }

    private void hidePublishButton() {
        hideWidgetToBottom(mPublishButton, 85);
    }

    private void showWidgetFromBottom(final View widget, int height) {
        int offset = DensityUtil.dip2px(this, height);
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, offset, 0);
        translateAnimation.setDuration(500);
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                widget.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        widget.setVisibility(View.INVISIBLE);
        widget.startAnimation(translateAnimation);
    }

    private void hideWidgetToBottom(final View widget, int height) {
        int offset = DensityUtil.dip2px(this, height);
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, offset);
        translateAnimation.setDuration(500);
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                widget.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        widget.startAnimation(translateAnimation);
    }
}
