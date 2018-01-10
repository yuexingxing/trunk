package com.zhiduan.crowdclient.menuindex;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.github.lzyzsd.jsbridge.DefaultHandler;
import com.github.lzyzsd.jsbridge.Err404;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.LoginActivity;
import com.zhiduan.crowdclient.activity.MainActivity;
import com.zhiduan.crowdclient.adapter.GrabOrderAdapter;
import com.zhiduan.crowdclient.adapter.HeaderChannelAdapter;
import com.zhiduan.crowdclient.banner.Banner;
import com.zhiduan.crowdclient.banner.BannerConfig;
import com.zhiduan.crowdclient.banner.OnBannerClickListener;
import com.zhiduan.crowdclient.completeinformation.CompleteInformationActivity;
import com.zhiduan.crowdclient.data.ADInfo;
import com.zhiduan.crowdclient.data.ChannelEntity;
import com.zhiduan.crowdclient.data.OrderInfo;
import com.zhiduan.crowdclient.data.UserInfo;
import com.zhiduan.crowdclient.im.ChatActivity;
import com.zhiduan.crowdclient.menuindex.filterlistview.ColorUtil;
import com.zhiduan.crowdclient.menuindex.filterlistview.DensityUtil;
import com.zhiduan.crowdclient.menuindex.filterlistview.DrawableCenterRadioButton;
import com.zhiduan.crowdclient.menuindex.filterlistview.FilterView;
import com.zhiduan.crowdclient.menuindex.filterlistview.FilterView.OnItemFilterClickListener;
import com.zhiduan.crowdclient.menuindex.filterlistview.FixedGridView;
import com.zhiduan.crowdclient.menuindex.filterlistview.HeaderFilterViewView;
import com.zhiduan.crowdclient.menuindex.filterlistview.SmoothListView;
import com.zhiduan.crowdclient.menuorder.DeliveryRemindActivity;
import com.zhiduan.crowdclient.menuorder.OrderDetailGrabActivity;
import com.zhiduan.crowdclient.menuorder.RunningDetailGrabActivity;
import com.zhiduan.crowdclient.menuorder.task.TaskDetailGrabActivity;
import com.zhiduan.crowdclient.menusetting.FeedBackActivity;
import com.zhiduan.crowdclient.net.AsyncNetTask;
import com.zhiduan.crowdclient.net.AsyncNetTask.OnPostExecuteListener;
import com.zhiduan.crowdclient.net.LoadTextNetTask;
import com.zhiduan.crowdclient.net.LoadTextResult;
import com.zhiduan.crowdclient.net.NetTaskResult;
import com.zhiduan.crowdclient.photoalbum.camera.MyCameraActivity;
import com.zhiduan.crowdclient.service.OrderService;
import com.zhiduan.crowdclient.share.InviteActivity;
import com.zhiduan.crowdclient.share.InviteCenter_Activity;
import com.zhiduan.crowdclient.task.TaskDetailActivity;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.GlobalInstanceStateHelper;
import com.zhiduan.crowdclient.util.Logs;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.util.OrderUtilTools;
import com.zhiduan.crowdclient.util.PayUtil;
import com.zhiduan.crowdclient.util.PayUtilParse;
import com.zhiduan.crowdclient.util.PopupWindows;
import com.zhiduan.crowdclient.util.PopupWindows.Callback;
import com.zhiduan.crowdclient.util.Util;
import com.zhiduan.crowdclient.util.Utils;
import com.zhiduan.crowdclient.view.CustomProgress;
import com.zhiduan.crowdclient.view.DropDownListView;
import com.zhiduan.crowdclient.view.GeneralDialog;
import com.zhiduan.crowdclient.view.dialog.DialogUtils;
import com.zhiduan.crowdclient.view.dialog.DialogUtils.DialogCallback;
import com.zhiduan.crowdclient.wallet.MyWalletActivity;

import de.greenrobot.event.EventBus;

/**
 * Author: hexiuhui
 */
public class IndexActivity extends ActivityGroup implements SmoothListView.ISmoothListViewListener, OnClickListener {
	private List<ADInfo> adInfos = new ArrayList<ADInfo>();
	private ADInfo adInfo;
	private Banner banner;
	private String[] images = null;
	private LinearLayout index_title;
	private SmoothListView mOrderListView; // 下拉刷新的Listview
	private GrabOrderAdapter mOrderAdapter;
	private List<OrderInfo> mDataList = new ArrayList<OrderInfo>(); // 订单信息集合
	private ImageView mClosePush;
	private int refresh = 0;
	private int m_currentPage = 1;
	private Handler mRefreshHandler;

	private LoadTextNetTask mTaskGetAdv; // 获取广告图
	private LoadTextNetTask mTaskGetDialogAdv; // 获取弹窗广告图
	private LoadTextNetTask mTaskRequestRobOrder;
	private LoadTextNetTask mTaskGetExperienceType;
	private LoadTextNetTask mTaskRequestGetOrderTask;

	private LinearLayout push_message_layout;
	private TextView push_assgned_message;

	public static final int REFRESH_GET_ORDER = 1; // 新订单消息
	public static final int REFRESH_PUSH_ASSIGNED = 2; // 分单转单消息
	public static final int REFRESH_PUSH_TIMEOUT_ASSIGNED = 3; // 转单超时
	public static final int REFRESH_PUSH_REVIEW_STATE_SUCCESS= 6; //审核成功
	public static final int REFRESH_PUSH_REVIEW_STATE_FAIL= 7; //审核失败

	private String bizId = ""; // 推送业务ID
	private String bizCode = ""; // 推送编码
	private String push_message = ""; // 推送消息内容

	private int filterViewPosition = 3; // 筛选视图的位置
	private int filterViewTopSpace; // 筛选视图距离顶部的距离
	private FilterView fvTopFilter;
	private HeaderFilterViewView headerFilterViewView; // 分类筛选视图
	private View itemHeaderFilterView; // 从ListView获取的筛选子View
	private boolean isScrollIdle = true; // ListView是否在滑动
	private boolean isStickyTop = false; // 是否吸附在顶部
	private boolean isSmooth = false; // 没有吸附的前提下，是否在滑动
	private int titleViewHeight = 50; // 标题栏的高度
	private int filterPosition = -1; // 点击FilterView的位置：分类(0)、排序(1)、筛选(2)
	private int adViewTopSpace; // 广告视图距离顶部的距离
	private int adViewHeight = 140; // 广告视图的高度
	private View itemHeaderAdView; // 从ListView获取的广告子View

	private String sortBy; // 倒序、正序
	private String sortsSelect; // 排序选择项
	private String filtersSex; // 性别筛选项
	private int filterTaskMode; // 人数筛选
	private boolean filterLookFlook; // 楼层筛选

	private static TabHost mTabHost;
	private RadioGroup mUserTabRg;
	private ProgressBar mWebviewProgressbar;
	public static BridgeWebView mUserWebView;
	private LinearLayout mReviewLayout;

	private int RESULT_LOAD_IMAGE = 0x1101;
	private int RESULT_TAKE_PHOTOE = 0x1102;
	private DrawableCenterRadioButton radioButtonUser;
	private DrawableCenterRadioButton radioButtonMoney;

	private EventBus mEventBus;
	//	private String webviewUrl = "http://192.168.3.5:9401/axp-web/index.htm?";

	private static Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_index);
		mContext = this;
		if (savedInstanceState != null) {
			// 因为用到了myapplication里的全局变量，被系统回收后要恢复
			GlobalInstanceStateHelper.restoreInstanceState(this, savedInstanceState);
		}

		initView();

//		mTaskGetAdv = requestGetAdv(OrderService.ADTYPE_INDEX);// 获取广告

		SharedPreferences sp = IndexActivity.this.getSharedPreferences(Constant.SAVE_SHOW_DIALOG_TIME, Context.MODE_PRIVATE);
		String time = sp.getString("showTime", "");

		if (!Util.isToday(time) || "".equals(time)) {
//			mTaskGetDialogAdv = requestGetAdv(OrderService.ADTYPE_DIALOG); // 获取弹窗广告图
		}

		setTitle("首页");

		createRefreshHandler();

		initData();

		setChannelData();

		initListeners();

		// 获取待抢订单数据
//		getOneData();
	}

	public void initView() {
		mOrderListView = (SmoothListView) findViewById(R.id.lv_main_list);
		mOrderListView.setRefreshEnable(true);
		mOrderListView.setLoadMoreEnable(true);
		mOrderListView.setSmoothListViewListener(this);

		itemHeaderAdView = View.inflate(this, R.layout.stick_header, null);// 头部内容
		mOrderListView.addHeaderView(itemHeaderAdView);// 添加头部

		mClosePush = (ImageView) findViewById(R.id.close_push_notif);
		push_assgned_message = (TextView) findViewById(R.id.push_assgned_message);
		push_message_layout = (LinearLayout) findViewById(R.id.push_message_layout);

		fvTopFilter = (FilterView) findViewById(R.id.fv_top_filter);
		index_title = (LinearLayout) findViewById(R.id.index_title);
		banner = (Banner) itemHeaderAdView.findViewById(R.id.index_ad);
		// viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
		banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);

		radioButtonUser = (DrawableCenterRadioButton) findViewById(R.id.rb_user_graph);
		radioButtonMoney = (DrawableCenterRadioButton) findViewById(R.id.rb_xiaopai_list);

		initTabHost();
//		initWebView();
		
		if (!"".equals(MyApplication.getInstance().m_userInfo.toKen) && MyApplication.getInstance().m_userInfo.toKen != null) {
//			mTaskGetExperienceType = getExperienceType();
		}
	}

	public void initTabHost() {
		m_userInfo = MyApplication.getInstance().m_userInfo;

		// Tab Host
		mTabHost = (TabHost) findViewById(R.id.tabhost);
		mTabHost.setup(this.getLocalActivityManager());

		// 用户
		TabSpec tsUsergraph = mTabHost.newTabSpec("user").setContent(R.id.tab_user).setIndicator("tab1");
		mTabHost.addTab(tsUsergraph);

		// 小派
		TabSpec tsXiaopaigraph = mTabHost.newTabSpec("xiaopai");
		tsXiaopaigraph.setContent(R.id.tab_xiaopai).setIndicator("tab2");
		mTabHost.addTab(tsXiaopaigraph);

		//审核界面
//		TabSpec tsReviewgraph = mTabHost.newTabSpec("shenghe");
//		tsReviewgraph.setContent(new Intent(IndexActivity.this, CompleteInformationActivity.class)).setIndicator("tab2");
//		mTabHost.addTab(tsReviewgraph);

		// 默认选中第1个Tab
		mTabHost.setCurrentTab(0);

		mReviewLayout = (LinearLayout) findViewById(R.id.review_layout);

//		if (m_userInfo.verifyStatus == Constant.REVIEW_STATE_SUBMITING) {
//			mReviewLayout.setVisibility(View.VISIBLE);
//			mOrderListView.setVisibility(View.GONE);
//			MainActivity.Sethide();
//		} else if (m_userInfo.verifyStatus == Constant.REVIEW_STATE_SUCCESS) {
			mReviewLayout.setVisibility(View.GONE);
			mOrderListView.setVisibility(View.VISIBLE);
			MainActivity.Setshow();
//		}
	}

	public void initData() {
		fvTopFilter.setVisibility(View.INVISIBLE);

		// 设置筛选数据
		headerFilterViewView = new HeaderFilterViewView(this);
		headerFilterViewView.fillView(new Object(), mOrderListView);

		mOrderAdapter = new GrabOrderAdapter(IndexActivity.this, mDataList);
		mOrderListView.setAdapter(mOrderAdapter);

		filterViewPosition = mOrderListView.getHeaderViewsCount();

		// 获取默认的筛选条件
		sortBy = "";
		sortsSelect = "";
		filtersSex = "";
		filterLookFlook = false;
		filterTaskMode = 0;
		headerFilterViewView.getFilterView().setCategoryColor(Color.RED);
		fvTopFilter.setCategoryColor(Color.RED);
	}

	private void initListeners() {
		mClosePush.setOnClickListener(this);
		push_message_layout.setOnClickListener(this);
		mOrderAdapter.setOnRobClickListener(onRobClickListener); // 抢单
		mOrderListView.setOnScrollListener(onSmoothScrollListener);
		mOrderAdapter.setOnAcceptClickListener(onAcceptClickListener); // 接任务
		mOrderAdapter.setOnTaskDetailListener(onTaskDetailClickListener);
		mOrderListView.setOnItemClickListener(onListViewItemClickListener);
		fvTopFilter.setOnItemCategoryClickListener(onItemFilterClickListener); // 分类Item点击

		// 标签切换
		mUserTabRg = (RadioGroup) findViewById(R.id.rg_tabhost);
		mUserTabRg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_user_graph:
					MainActivity.Sethide();

					fvTopFilter.setVisibility(View.INVISIBLE);
					if (fvTopFilter.isShowing()) {
						fvTopFilter.resetAllStatus();
					}

					mTabHost.setCurrentTab(0);
					break;
				case R.id.rb_xiaopai_list:
					setTabXiaoPao();
					break;
				}
			}
		});

		banner.setOnBannerClickListener(new OnBannerClickListener() {
			@Override
			public void OnBannerClick(int position) {
				if (adInfos.get(position - 1).getUrl_link() == null || adInfos.get(position - 1).getUrl_link().equals("")) {
					return;
				}

				if (adInfo.getOpen_type() == 1) {
					Intent intent = new Intent(IndexActivity.this, BannerDetailActivity.class);
					intent.putExtra("url", dialogAdInfos.get(position).getUrl_link());
					intent.putExtra("title", dialogAdInfos.get(position).getTitle());
					startActivity(intent);
				} else {
					if (adInfo.getOpen_url().equals("1001")) {
						Intent taskIntent = new Intent(IndexActivity.this, TaskOrderMenuActivity.class);
						taskIntent.putExtra("currIndex", 0);
						startActivity(taskIntent);
					} else if (adInfo.getOpen_url().equals("1002")) {
						Intent intent2 = new Intent(IndexActivity.this, BannerDetailActivity.class);
						intent2.putExtra("title", "帮助与反馈");
						intent2.putExtra("url", "http://dev.axpapp.com/question/toqalist.htm");
						intent2.putExtra("type", "question");
						startActivity(intent2);
					} else if (adInfo.getOpen_url().equals("1003")) {
						startActivity(new Intent(IndexActivity.this, InviteCenter_Activity.class));
					}
				}
			}
		});

		// (假的ListView头部展示的)筛选视图点击
		headerFilterViewView.setOnFilterClickListener(new HeaderFilterViewView.OnFilterClickListener() {
			@Override
			public void onFilterClick(int position) {
				if (position == 0) {
					sortBy = "";
					sortsSelect = "";
					filtersSex = "";
					filterLookFlook = false;
					filterTaskMode = 0;

					getOneData();
					headerFilterViewView.getFilterView().setCategoryColor(Color.RED);
					fvTopFilter.setCategoryColor(Color.RED);
				} else {
					filterPosition = position;
					isSmooth = true;
					// headerFilterViewView.getFilterView().showFilterLayout(position);
					mOrderListView.smoothScrollToPositionFromTop(filterViewPosition, DensityUtil.dip2px(IndexActivity.this, titleViewHeight - 10));
				}
			}
		});

		// (真正的)筛选视图点击
		fvTopFilter.setOnFilterClickListener(new FilterView.OnFilterClickListener() {
			@Override
			public void onFilterClick(int position) {
				if (isStickyTop) {
					filterPosition = position;

					fvTopFilter.showFilterLayout(position);
					
					if (titleViewHeight - 3 > filterViewTopSpace || filterViewTopSpace > titleViewHeight + 3) {
						mOrderListView.smoothScrollToPositionFromTop(filterViewPosition, DensityUtil.dip2px(IndexActivity.this, titleViewHeight - 10));
					}
				}
			}
		});
	}

	// 设置频道数据
	private void setChannelData() {
		FixedGridView fixedGridView = (FixedGridView) itemHeaderAdView.findViewById(R.id.gv_channel);

		List<ChannelEntity> channelList = new ArrayList<ChannelEntity>();
		channelList.add(new ChannelEntity("赏金猎人", "", R.drawable.home_bounty_hunter));
		channelList.add(new ChannelEntity("经验加成", "", R.drawable.home_experience_bonus));
		channelList.add(new ChannelEntity("分类查看", "", R.drawable.home_classification));

		HeaderChannelAdapter adapter = new HeaderChannelAdapter(IndexActivity.this, channelList);
		fixedGridView.setAdapter(adapter);

		fixedGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				switch (arg2) {
				case 0:
					Intent taskIntent = new Intent(IndexActivity.this, TaskOrderMenuActivity.class);
					startActivity(taskIntent);
					break;
				case 1:
					Intent exIntent = new Intent(IndexActivity.this, ExperienceAdditionActivity.class);
					exIntent.putExtra("categoryId", 0);
					exIntent.putExtra("ruleCode", OrderUtil.EXPERIENCE_RELE_CODE);
					startActivity(exIntent);
					break;
				case 2:
					Intent intent = new Intent(IndexActivity.this, ClassifyActivity.class);
					startActivity(intent);
					break;

				default:
					break;
				}
			}
		});
	}

	protected void onStart() {
		super.onStart();
		banner.isAutoPlay(true);
	}

	@Override
	protected void onStop() {
		super.onStop();
		banner.isAutoPlay(false);
	}
	
	public static void setTabXiaoPao() {
		MainActivity.Setshow();
		// 是否登录
//		if (TextUtils.isEmpty(MyApplication.getInstance().m_userInfo.toKen)) {
//			DialogUtils.showLoginDialog(mContext);
//			MainActivity.Sethide();
//		} else {
//			if (MyApplication.getInstance().m_userInfo.verifyStatus == Constant.REVIEW_STATE_NOT_SUBMIT || MyApplication.getInstance().m_userInfo.verifyStatus == Constant.REVIEW_STATE_FAIL || MyApplication.getInstance().m_userInfo.verifyStatus == Constant.REVIEW_STATE_USER_SUCCESS) {
//				mTabHost.setCurrentTab(2);
//				MainActivity.Sethide();
//			} else {
				mTabHost.setCurrentTab(1);
				if (MyApplication.getInstance().m_userInfo.verifyStatus == Constant.REVIEW_STATE_SUBMITING) {
					MainActivity.Sethide();
				}
//			}
//		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.push_message_layout:
			Intent intent = new Intent(IndexActivity.this, DeliveryRemindActivity.class);
			intent.putExtra("bizId", bizId);
			intent.putExtra("bizCode", bizCode);
			intent.putExtra("push_message", push_message);
			startActivity(intent);
			break;
		case R.id.close_push_notif:
			push_message_layout.setVisibility(View.GONE);
			break;
		default:
			break;
		}
	}

	// 任务详情
	GrabOrderAdapter.onTaskDetailInterface onTaskDetailClickListener = new GrabOrderAdapter.onTaskDetailInterface() {

		@Override
		public void onTaskDetailClick(View v, String orderId) {
			Intent intent = new Intent(IndexActivity.this, TaskDetailActivity.class);
			intent.putExtra("task_status", Constant.TASK_MAIN_DETAIL);
			intent.putExtra("orderid", orderId);
			startActivity(intent);
		}
	};

	// 接任务
	GrabOrderAdapter.onAcceptInterface onAcceptClickListener = new GrabOrderAdapter.onAcceptInterface() {
		@Override
		public void onAcceptClickClick(View v, String orderId, String type) {
			mTaskRequestRobOrder = requestGobOrder(orderId, type, v);
		}
	};

	// 抢单
	GrabOrderAdapter.onRobClickListener onRobClickListener = new GrabOrderAdapter.onRobClickListener() {
		@Override
		public void onRobClickClick(View v, String orderId, String type) {
			mTaskRequestRobOrder = requestGobOrder(orderId, type, v);
		}
	};

	// listview item点击事件
	AdapterView.OnItemClickListener onListViewItemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			OrderInfo orderInfo = mDataList.get(position - 3);

			Intent intent = null;
			if (orderInfo.getCategoryId().equals(OrderUtil.ORDER_TYPE_PACKET)) {
				intent = new Intent(IndexActivity.this, OrderDetailGrabActivity.class);
			} else if (orderInfo.getCategoryId().equals(OrderUtil.ORDER_TYPE_AGENT)) {
				intent = new Intent(IndexActivity.this, RunningDetailGrabActivity.class);
			} else if (orderInfo.getCategoryId().startsWith(OrderUtil.ORDER_TYPE_RUN)) {
				intent = new Intent(IndexActivity.this, RunningDetailGrabActivity.class);
			} else if (orderInfo.getCategoryId().startsWith(OrderUtil.ORDER_TYPE_RUN)) {
				intent = new Intent(IndexActivity.this, RunningDetailGrabActivity.class);
			} else if (orderInfo.getCategoryId().equals(OrderUtil.ORDER_TYPE_COMMON)) {
				intent = new Intent(IndexActivity.this, TaskDetailGrabActivity.class);
			} else {
				intent = new Intent(IndexActivity.this, RunningDetailGrabActivity.class);
			}

			intent.putExtra("orderType", orderInfo.getCategoryId());
			intent.putExtra("orderId", orderInfo.getOrderNo());
			startActivity(intent);
		}

	};

	// 监听listview滚动事件
	SmoothListView.OnSmoothScrollListener onSmoothScrollListener = new SmoothListView.OnSmoothScrollListener() {

		@Override
		public void onSmoothScrolling(View view) {
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			isScrollIdle = (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE);
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			// if (isScrollIdle && adViewTopSpace < 0) return;

			// 获取广告头部View、自身的高度、距离顶部的高度
			if (itemHeaderAdView == null) {
				itemHeaderAdView = mOrderListView.getChildAt(1 - firstVisibleItem);
			}

			if (itemHeaderAdView != null) {
				adViewTopSpace = DensityUtil.px2dip(IndexActivity.this, itemHeaderAdView.getTop());
				adViewHeight = DensityUtil.px2dip(IndexActivity.this, itemHeaderAdView.getHeight());
			}

			// 获取筛选View、距离顶部的高度
//			if (itemHeaderFilterView == null) {
				itemHeaderFilterView = mOrderListView.getChildAt(filterViewPosition - firstVisibleItem);
//			}
			
			if (itemHeaderFilterView != null) {
				filterViewTopSpace = DensityUtil.px2dip(IndexActivity.this, itemHeaderFilterView.getTop());
			}
			
			
			// 处理筛选是否吸附在顶部
			if (filterViewTopSpace > titleViewHeight) {
				isStickyTop = false; // 没有吸附在顶部
				fvTopFilter.setVisibility(View.INVISIBLE);
			} else {
				isStickyTop = true; // 吸附在顶部
				fvTopFilter.setVisibility(View.VISIBLE);
			}

			if (firstVisibleItem > filterViewPosition) {
				isStickyTop = true;
				fvTopFilter.setVisibility(View.VISIBLE);
			}

			if (isSmooth && isStickyTop) {
				isSmooth = false;
				fvTopFilter.showFilterLayout(filterPosition);
			}

			fvTopFilter.setStickyTop(isStickyTop);

			// 处理标题栏颜色渐变
//			handleTitleBarColorEvaluate();
		}
	};

	// 分类Item点击
	OnItemFilterClickListener onItemFilterClickListener = new OnItemFilterClickListener() {

		@Override
		public void onItemCategoryClick() {
			sortBy = "";
			sortsSelect = "";
			filtersSex = "";
			filterLookFlook = false;
			filterTaskMode = 0;

			getOneData();
			headerFilterViewView.getFilterView().setCategoryColor(Color.RED);
			fvTopFilter.setCategoryColor(Color.RED);
		}

		// 排序Item点击
		@Override
		public void onItemSortClick(String sort, String orderBy) {
			sortBy = sort;
			sortsSelect = orderBy;
			filtersSex = "";
			filterLookFlook = false;
			filterTaskMode = 0;

			getOneData();

			headerFilterViewView.getFilterView().setCategoryColor(IndexActivity.this.getResources().getColor(
					R.color.font_black_2));
			// 设置假筛选默认数据
			// headerFilterViewView.getFilterView().setSortTitle(entity);
		}

		// 筛选Item点击
		@Override
		public void onItemFilterClick(int number, String sex, boolean lookFloor) {
			sortBy = "";
			sortsSelect = "";
			filtersSex = sex;
			filterLookFlook = lookFloor;
			filterTaskMode = number;

			getOneData();

			headerFilterViewView.getFilterView().setCategoryColor(IndexActivity.this.getResources().getColor(R.color.font_black_2));

			// 设置假筛选默认数据
			// headerFilterViewView.getFilterView().setFilterTitle(entity);
		}
	};

	private List<ADInfo> dialogAdInfos = new ArrayList<ADInfo>();
	private String[] dialogImages = null;

	// 获取广告
	protected LoadTextNetTask requestGetAdv(final int adType) {
		OnPostExecuteListener listener = new OnPostExecuteListener() {
			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag) {
				mTaskGetAdv = null;

				CustomProgress.dissDialog();
				if (result.m_nResultCode == 0) {
					LoadTextResult mresult = (LoadTextResult) result;
					try {
						JSONObject jsonObj = new JSONObject(mresult.m_strContent);
						Logs.i("hexiuhui---", adType + "---" + jsonObj.toString());

						int success = jsonObj.getInt("success");
						if (success == 0) {
							JSONArray AdvArray = jsonObj.optJSONArray("data");

							if (adType == OrderService.ADTYPE_INDEX) {
								images = new String[AdvArray.length()];
								for (int i = 0; i < AdvArray.length(); i++) {
									JSONObject AdvObject = AdvArray.optJSONObject(i);
									adInfo = new ADInfo();
									adInfo.setUrl(AdvObject.optString("adImg"));
									adInfo.setUrl_link(AdvObject.optString("url"));
									adInfo.setTitle(AdvObject.optString("adTitle"));
									adInfo.setOpen_url(AdvObject.optString("url"));
									adInfo.setOpen_type(AdvObject.optInt("jumpType"));
									adInfos.add(adInfo);
									images[i] = AdvObject.optString("adImg");
								}
								banner.setImages(images);
							} else if (adType == OrderService.ADTYPE_DIALOG) {
								dialogImages = new String[AdvArray.length()];

								if (AdvArray.length() > 0) {
									for (int i = 0; i < AdvArray.length(); i++) {
										JSONObject AdvObject = AdvArray.optJSONObject(i);
										adInfo = new ADInfo();
										adInfo.setUrl(AdvObject.optString("adImg"));
										adInfo.setUrl_link(AdvObject.optString("url"));
										adInfo.setTitle(AdvObject.optString("adTitle"));
										adInfo.setOpen_url(AdvObject.optString("url"));
										adInfo.setOpen_type(AdvObject.optInt("jumpType"));
										dialogAdInfos.add(adInfo);
										dialogImages[i] = AdvObject.optString("adImg");
									}

									Message msg = new Message();
									msg.what = 5;
									mRefreshHandler.sendMessage(msg);
								}
							}
						} else {
							String message = jsonObj.getString("message");
							// CommandTools.showToast(message);
						}
					} catch (JSONException e) {
						Util.showJsonParseErrorMessage(IndexActivity.this);
						e.printStackTrace();
					}
				} else {
					Util.showNetErrorMessage(IndexActivity.this, result.m_nResultCode);
				}
			}
		};

		CustomProgress.showDialog(IndexActivity.this, "获取信息中...", false, null);
		LoadTextNetTask task = OrderService.getIndexAdv(adType, listener, null);

		return task;
	}

	private void createRefreshHandler() {
		mRefreshHandler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case REFRESH_GET_ORDER:
					getOneData();
					break;
				case REFRESH_PUSH_ASSIGNED:
					push_message_layout.setVisibility(View.VISIBLE);
					Bundle data = msg.getData();
					push_message = data.getString("content");
					bizId = data.getString("bizId");
					bizCode = data.getString("bizCode");
					push_assgned_message.setText(push_message);

					Util.saveDelivery(IndexActivity.this, push_message, bizId, bizCode);
					break;
				case REFRESH_PUSH_TIMEOUT_ASSIGNED:
					push_message_layout.setVisibility(View.GONE);
					Util.removeDelivery(IndexActivity.this);
					break;
				case 4:
					isRefresh = true;
					break;
				case 5:
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
					String str = formatter.format(curDate);
					Util.saveDialog(IndexActivity.this, str); // 保存弹出的时间

					openAdvPopupWindow();
					break;
				case REFRESH_PUSH_REVIEW_STATE_SUCCESS:
					mReviewLayout.setVisibility(View.GONE);
					mOrderListView.setVisibility(View.VISIBLE);
					int currentTab = mTabHost.getCurrentTab();
					if (currentTab != 0) {
						MainActivity.Setshow();
					}
					break;
				case REFRESH_PUSH_REVIEW_STATE_FAIL:
					mTabHost.setCurrentTab(2);
					MainActivity.Sethide();
					break;
				default:
					break;
				}
			}
		};

		MyApplication.getInstance().m_refreshGetOrderHandler = mRefreshHandler;
	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences sharedPreferences = getSharedPreferences(Constant.PUSH_ASSIGNED_MESSAGE, Context.MODE_PRIVATE);
		String message = sharedPreferences.getString("message", "");

		if ("".equals(message)) {
			push_message_layout.setVisibility(View.GONE);
		}
	}

	// 自定义的弹出框类
	AdvertisementPop menuWindow;

	public void openAdvPopupWindow() {
		// 实例化SelectPicPopupWindow
		menuWindow = new AdvertisementPop(IndexActivity.this, dialogImages, dialogAdInfos);
		// 显示窗口
		menuWindow.showAtLocation(IndexActivity.this.findViewById(R.id.layout_index), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
	}

	protected LoadTextNetTask requestGetOrderData(String sortBy, String orderBy, int pageNumber, String sex, boolean lookFloor, int number) {
		OnPostExecuteListener listener = new OnPostExecuteListener() {
			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag) {
				mTaskRequestGetOrderTask = null;

				if (result.m_nResultCode == 0) {
					LoadTextResult mresult = (LoadTextResult) result;
					try {
						JSONObject jsonObj = new JSONObject(mresult.m_strContent);

						Logs.i("hexiuhui---", jsonObj.toString());

						int success = jsonObj.getInt("success");
						if (success == 0) {
							// mOrderListView.setPullLoadEnable(true);

							JSONObject dataObj = jsonObj.getJSONObject("data");
							JSONArray jsonArray = dataObj.getJSONArray("responseDto");

							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject jsonObject = jsonArray.getJSONObject(i);
								String theme = jsonObject.getString("theme");
								String icon = jsonObject.getString("icon");
								String startDeliverTime = jsonObject.getString("serviceStartDate");
								String endDeliverTime = jsonObject.getString("serviceEndDate");
								String orderId = jsonObject.getString("orderId");
								String sex = jsonObject.getString("sex");
								String address = jsonObject.getString("address");
								String remark = jsonObject.getString("remark");
								String name = jsonObject.getString("name");
								int createUser = jsonObject.getInt("createUser");
								int categoryId = jsonObject.getInt("categoryId");
								long income = jsonObject.getLong("income");
								int needPplQty = jsonObject.getInt("needPplQty");

								// 提取日期中的小时分钟
								OrderInfo info = new OrderInfo();
								info.setOrder_theme(theme);
								info.setDeliverySex(sex);
								info.setDeliveryFee(income);
								info.setReceiveIcon(icon);
								info.setReceiveName(name);
								info.setRemark(remark);
								info.setNeedPplQty(needPplQty);
								info.setReceiveId(createUser);
								info.setCategoryId(categoryId+"");
								info.setReceiveAddress(address);
								info.setOrderNo(orderId);
								info.setDeliveryTime(OrderUtil.getBetweenTime(startDeliverTime, endDeliverTime));
								info.setServiceDate(jsonObject.optString("serviceDate"));

								mDataList.add(info);
							}

							// 容错处理,当数据未满一屏时，添加空数据使listview填充满一屏
							if (mDataList.size() < 5) {
								for (int i = 0; i < 4; i++) {
									mDataList.add(new OrderInfo());
								}
							}

							mOrderAdapter.notifyDataSetChanged();

							refreshInit();

							mOrderListView.setLoadMoreEnable(jsonArray.length() > 10);
						} else {
							String message = jsonObj.getString("message");
							// CommandTools.showToast(message);
							refreshInit();

							String code = jsonObj.getString("code");
							if (code.equals("105")) {
								mDataList.clear();
								// 容错处理,当数据未满一屏时，添加空数据使listview填充满一屏
								if (mDataList.size() < 4) {
									for (int i = 0; i < 5; i++) {
										mDataList.add(new OrderInfo());
									}
								}

								mOrderAdapter.notifyDataSetChanged();
								mOrderListView.setLoadMoreEnable(false);
								// mOrderListView.setVisibility(View.GONE);
							} else if (code.equals("010013")) {
								mOrderListView.setLoadMoreEnable(false);
							} else if (code.equals("102")) {
								Util.showNotifyMessage(IndexActivity.this, "", message, null);
								mDataList.clear();
								// 容错处理,当数据未满一屏时，添加空数据使listview填充满一屏
								if (mDataList.size() < 4) {
									for (int i = 0; i < 5; i++) {
										mDataList.add(new OrderInfo());
									}
								}

								mOrderAdapter.notifyDataSetChanged();
								mOrderListView.setLoadMoreEnable(false);
							}
						}
					} catch (JSONException e) {
						Util.showJsonParseErrorMessage(IndexActivity.this);
						e.printStackTrace();

						refreshInit();
					}
				} else {
					Util.showNetErrorMessage(IndexActivity.this, result.m_nResultCode);
					refreshInit();
				}
			}
		};

		LoadTextNetTask task = OrderService.getOrdertask(sortBy, orderBy, pageNumber, sex, lookFloor, number, listener, null);
		return task;
	}

	protected LoadTextNetTask requestGobOrder(String orderId, final String type, final View v) {
		OnPostExecuteListener listener = new OnPostExecuteListener() {
			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag) {
				mTaskRequestRobOrder = null;
				CustomProgress.dissDialog();
				if (result.m_nResultCode == 0) {
					LoadTextResult mresult = (LoadTextResult) result;
					try {
						JSONObject jsonObj = new JSONObject(mresult.m_strContent);

						Logs.i("hexiuhui---", jsonObj.toString());

						int success = jsonObj.getInt("success");
						if (success == 0) {
							CommandTools.showToast("抢单成功");
							OrderUtilTools.refreshDataList(mEventBus, 1);//抢单成功后刷新待处理订单列表
							getOneData();

							// 接单成功后设置未读消息数量
							SharedPreferences sp = IndexActivity.this.getSharedPreferences(Constant.SAVE_NOT_ORDER_NUMBER,
									Context.MODE_PRIVATE);
							int number = sp.getInt("OrderNumber", 0);
							Util.saveNotNumber(IndexActivity.this, number + 1, Util.order_type);
							MainActivity.mNotOrderNum.setText(number + 1 + "");
							MainActivity.mNotOrderNum.setVisibility(View.VISIBLE);

							// if (type.equals(Constant.TYPE_AGENT_SYSTEM)) {
							// startAnim(v);
							// }
						} else {
							String message = jsonObj.getString("message");

							String code = jsonObj.getString("code");
							if (code.equals("010011")) {
								CommandTools.showToast(message);
								getOneData();
							}

							// 抢单模式没有开启
							else if (code.equals("010003")) {
								showNotOpenTips(message);
							} else {
								CommandTools.showToast(message);
							}
						}
					} catch (JSONException e) {
						Util.showJsonParseErrorMessage(IndexActivity.this);
						e.printStackTrace();
					}
				} else {
				}
			}
		};

		CustomProgress.showDialog(IndexActivity.this, "获取数据中...", false, null);
		LoadTextNetTask task = OrderService.grabOrder(orderId, type, listener, null);
		return task;
	}
	
	/** 判断用户所在学校是否开启经验/悬赏加成**/
	protected LoadTextNetTask getExperienceType() {
		OnPostExecuteListener listener = new OnPostExecuteListener() {
			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag) {
				mTaskGetExperienceType = null;
				CustomProgress.dissDialog();
				if (result.m_nResultCode == 0) {
					LoadTextResult mresult = (LoadTextResult) result;
					try {
						JSONObject jsonObj = new JSONObject(mresult.m_strContent);

						Logs.i("hexiuhui---", jsonObj.toString());

						int success = jsonObj.getInt("success");
						if (success == 0) {
							int type = jsonObj.getInt("data");
							
						} else {
							String message = jsonObj.getString("message");
							CommandTools.showToast(message);
						}
					} catch (JSONException e) {
						Util.showJsonParseErrorMessage(IndexActivity.this);
						e.printStackTrace();
					}
				} else {
				}
			}
		};

		CustomProgress.showDialog(IndexActivity.this, "获取数据中...", false, null);
		LoadTextNetTask task = OrderService.getExperienceType(listener, null);
		return task;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mTaskRequestGetOrderTask != null) {
			mTaskRequestGetOrderTask.cancel(true);
			mTaskRequestGetOrderTask = null;
		}

		if (mTaskRequestRobOrder != null) {
			mTaskRequestRobOrder.cancel(true);
			mTaskRequestRobOrder = null;
		}

		if (mTaskGetAdv != null) {
			mTaskGetAdv.cancel(true);
			mTaskGetAdv = null;
		}

		if (mTaskGetDialogAdv != null) {
			mTaskGetDialogAdv.cancel(true);
			mTaskGetDialogAdv = null;
		}
		
		if (mTaskGetExperienceType != null) {
			mTaskGetExperienceType.cancel(true);
			mTaskGetExperienceType = null;
		}

	}

	// 下拉刷新
	private void refreshInit() {
		mOrderListView.stopRefresh();
		mOrderListView.stopLoadMore();
		// 也可以用系统时间
		mOrderListView.setRefreshTime("刚刚");

		refresh = 0;
		Message msg = new Message();
		msg.what = 4;
		mRefreshHandler.sendMessage(msg);
	}

	private void getOneData() {
		mDataList.clear();
		m_currentPage = 1;
		mTaskRequestGetOrderTask = requestGetOrderData(sortBy, sortsSelect,
				m_currentPage, filtersSex, filterLookFlook, filterTaskMode);
	}

	@Override
	public void onBackPressed() {
		if (!fvTopFilter.isShowing()) {
			super.onBackPressed();
		} else {
			fvTopFilter.resetAllStatus();
		}
	}

	private boolean isRefresh = true;
	private UserInfo m_userInfo;

	@Override
	public void onRefresh() {
		if (!isRefresh) {
			return;
		}

		isRefresh = false;
		if (refresh == 0) {
			refresh++;
			getOneData();
		}
	}

	@Override
	public void onLoadMore() {
		m_currentPage++;

		mTaskRequestGetOrderTask = requestGetOrderData(sortBy, sortsSelect,
				m_currentPage, filtersSex, filterLookFlook, filterTaskMode);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// 被系统回收时保存myapplication全局变量值
		GlobalInstanceStateHelper.saveInstanceState(outState);
		//
		// outState.putInt("mItem", mItem);
		// outState.putInt("mCurrentItem", mCurrentItem);
		// outState.putBoolean("isFrist", isFrist);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// 恢复被系统回收后的myapplication值
		GlobalInstanceStateHelper.restoreInstanceState(this, savedInstanceState);

		initView();
		//
		// mItem = savedInstanceState.getInt("mItem");
		// mCurrentItem = savedInstanceState.getInt("mCurrentItem");
		// isFrist = savedInstanceState.getBoolean("isFrist");
	}

	// 处理标题栏颜色渐变
	@SuppressLint("NewApi")
	private void handleTitleBarColorEvaluate() {
		float fraction;
		if (adViewTopSpace > 0) {
			fraction = 1f - adViewTopSpace * 1f / 60;
			if (fraction < 0f)
				fraction = 0f;
			index_title.setAlpha(fraction);
			return;
		}

		float space = Math.abs(adViewTopSpace) * 1f;
		fraction = space / (adViewHeight - titleViewHeight);
		if (fraction < 0f)
			fraction = 0f;
		if (fraction > 1f)
			fraction = 1f;
		index_title.setAlpha(1f);

		if (fraction >= 1f || isStickyTop) {
			isStickyTop = true;

			index_title.setBackgroundColor(IndexActivity.this.getResources()
					.getColor(R.color.orange));
		} else {
			index_title.setBackgroundColor(ColorUtil
					.getNewColorByStartEndColor(IndexActivity.this, fraction,
							R.color.transparent, R.color.orange));
		}
	}

	public int getScrollY(DropDownListView listView) {
		View c = listView.getChildAt(0);
		if (c == null) {
			return 0;
		}
		int firstVisiblePosition = listView.getFirstVisiblePosition();
		int top = c.getTop();
		return -top + firstVisiblePosition * c.getHeight();
	}

	/**
	 * 抢单模式未开启时，弹出提示对话框
	 */
	public void showNotOpenTips(String content) {
		DialogUtils.showOneButtonDialog(IndexActivity.this, GeneralDialog.DIALOG_ICON_TYPE_5, "提示", content, "确认", new DialogCallback() {
			@Override
			public void callback(int position) {

			}
		});
	}

	/**
	 * 初始化WebView
	 */
	public void initWebView() {
		if (mEventBus == null) {
			mEventBus = EventBus.getDefault();
			mEventBus.register(this);
		}

		PayUtilParse.initPayInfo(IndexActivity.this, mEventBus);
		mUserWebView = (BridgeWebView) findViewById(R.id.wv_user);
		final LinearLayout failNetLayout = (LinearLayout) findViewById(R.id.failed_net_layout);
		TextView click_retry_txt = (TextView) findViewById(R.id.click_retry_txt);
		mWebviewProgressbar = (ProgressBar) findViewById(R.id.webview_progressbar);

		WebSettings webSettings = mUserWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);// 支持javascrip必须有
		webSettings.setDefaultTextEncodingName("utf-8");
		mUserWebView.setWebChromeClient(new WebChromeClient() {});// 要加上这句，否则部分机型无法弹出窗口
		mUserWebView.getSettings().setDomStorageEnabled(true);
		// 修改ua使得web端正确判断
		String ua = mUserWebView.getSettings().getUserAgentString();
		mUserWebView.getSettings().setUserAgentString(ua + "APP_Android");

		mUserWebView.setDefaultHandler(new DefaultHandler());
		//		String webviewUrl = "http://192.168.3.5:9401/axp-web/index.htm?" + "token=" + MyApplication.getInstance().m_userInfo.toKen + "&timestamp=" + new java.util.Date().getTime();
		final String webviewUrl = Constant.WEBVIEW_H5_URL + "token=" + MyApplication.getInstance().m_userInfo.toKen + "&timestamp=" + new java.util.Date().getTime();

		mUserWebView.myPWV404 = new Err404() {

			@Override
			public void encounter(String arg0) {
				failNetLayout.setVisibility(View.VISIBLE);
				mUserWebView.setVisibility(View.GONE);
			}

			@Override
			public void encounter() {
				failNetLayout.setVisibility(View.VISIBLE);
				mUserWebView.setVisibility(View.GONE);
			}
		};

		mUserWebView.setWebChromeClient(new WebChromeClient(){
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				if (newProgress==100) {  
					mWebviewProgressbar.setVisibility(View.GONE);//加载完网页进度条消失  
				} else {  
					mWebviewProgressbar.setVisibility(View.VISIBLE);//开始加载网页时显示进度条  
					mWebviewProgressbar.setProgress(newProgress);//设置进度值  
				}  
			}
		});

		click_retry_txt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				failNetLayout.setVisibility(View.GONE);
				mUserWebView.setVisibility(View.VISIBLE);
				mUserWebView.loadUrl(webviewUrl);
			}
		});

		mUserWebView.loadUrl(webviewUrl);

		registerHandler();
	}

	/**
	 * 从图库选择
	 * 
	 * @param v
	 */
	public void choosePic(View v) {
		Intent chooserIntent = new Intent(Intent.ACTION_GET_CONTENT);
		chooserIntent.setType("image/*");
		startActivityForResult(chooserIntent, RESULT_LOAD_IMAGE);
	}

	/**
	 * 拍照
	 * 
	 * @param v
	 */
	public void takePhoto(View v) {
		Intent intent = new Intent(IndexActivity.this, MyCameraActivity.class);
		startActivityForResult(intent, RESULT_TAKE_PHOTOE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("hexiuhui-----------", requestCode + "---" + resultCode);
		// 获取当前活动的Activity实例
		Activity subActivity = getLocalActivityManager().getCurrentActivity();
		// 判断是否实现返回值接口
		if (subActivity instanceof OnTabActivityResultListener) {
			// 获取返回值接口实例
			OnTabActivityResultListener listener = (OnTabActivityResultListener) subActivity;
			// 转发请求到子Activity
			listener.onTabActivityResult(requestCode, resultCode, data);
		}

		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RESULT_TAKE_PHOTOE
				&& resultCode == Activity.RESULT_OK) {

			String pathCamera = data.getStringExtra("pathCamera");
			Bitmap bitmap = CommandTools.convertToBitmap(pathCamera, 100, 100);

			PayUtilParse.base64 = CommandTools.bitmapToBase64(bitmap);
			PayUtilParse.mHandler.sendEmptyMessage(PayUtilParse.TAKEPHOTO_H5);
		} else if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
			Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();

			Bitmap bitmap = CommandTools.getBitmapFromUri(IndexActivity.this, result);
			PayUtilParse.base64 = CommandTools.bitmapToBase64(bitmap);
			PayUtilParse.mHandler.sendEmptyMessage(PayUtilParse.TAKEPHOTO_H5);
		}
	}

	/**
	 * H5 java层提供的方法，提供给js调用 注册方法，提供给h5用
	 */
	private void registerHandler() {
		// 拍照
		mUserWebView.registerHandler("toTakePictures", new BridgeHandler() {
			@Override
			public void handler(String arg0, CallBackFunction arg1) {
				PayUtilParse.callBackFunction = arg1;

				new PopupWindows(IndexActivity.this, findViewById(R.id.tabhost), new Callback() {
					@Override
					public void callback(int pos) {
						if (pos == 1) {
							takePhoto(null);
						} else if (pos == 2) {
							choosePic(null);
						}
					}
				});
			}
		});

		// 返回到登陆界面
		mUserWebView.registerHandler("toLogin", new BridgeHandler() {
			@Override
			public void handler(String arg0, CallBackFunction arg1) {
				PayUtilParse.callBackFunction = arg1;
				MyApplication myApplication = MyApplication.getInstance();
				myApplication.m_userInfo.toKen = "";
				startActivity(new Intent(IndexActivity.this, LoginActivity.class));
				Utils.clearActivityList();
				finish();
			}
		});

		// 第三方支付(微信、支付宝)
		mUserWebView.registerHandler("thdPay", new BridgeHandler() {

			@Override
			public void handler(String arg0, CallBackFunction arg1) {
				PayUtilParse.callBackFunction = arg1;
				PayUtilParse.getAppPayInfo(arg0);
			}
		});

		// 调用分享接口
		mUserWebView.registerHandler("toShare", new BridgeHandler() {
			@Override
			public void handler(String arg0, CallBackFunction arg1) {

				Intent intent = new Intent(IndexActivity.this, InviteActivity.class);
				startActivity(intent);
			}
		});

		// 显示上部tab页
		mUserWebView.registerHandler("showNavView", new BridgeHandler() {

			@Override
			public void handler(String arg0, CallBackFunction arg1) {

				mHandler.sendEmptyMessage(0x0012);
			}
		});

		// 隐藏上部tab页
		mUserWebView.registerHandler("hideNavView", new BridgeHandler() {
			@Override
			public void handler(String arg0, CallBackFunction arg1) {

				mHandler.sendEmptyMessage(0x0011);
			}
		});

		// 我要赚钱接口
		mUserWebView.registerHandler("toMakeMoney", new BridgeHandler() {

			@Override
			public void handler(String arg0, CallBackFunction arg1) {

				radioButtonUser.setChecked(false);
				radioButtonMoney.setChecked(true);

				mTabHost.setCurrentTab(1);
			}
		});

		// 我的钱包接口
		mUserWebView.registerHandler("toWallet", new BridgeHandler() {

			@Override
			public void handler(String arg0, CallBackFunction arg1) {

				Intent intent = new Intent(IndexActivity.this, MyWalletActivity.class);
				Constant.is_h5=true;
				startActivity(intent);
			}
		});

		// 邀请好友接口
		mUserWebView.registerHandler("toInviteFriends", new BridgeHandler() {

			@Override
			public void handler(String arg0, CallBackFunction arg1) {

				Intent intent = new Intent(IndexActivity.this, InviteActivity.class);
				startActivity(intent);
			}
		});

		// 问题反馈接口
		mUserWebView.registerHandler("toFeedback", new BridgeHandler() {

			@Override
			public void handler(String arg0, CallBackFunction arg1) {

				Intent intent = new Intent(IndexActivity.this, FeedBackActivity.class);
				startActivity(intent);
			}
		});

		//拨打电话
		mUserWebView.registerHandler("toPhone", new BridgeHandler() {
			@Override
			public void handler(String arg0, CallBackFunction arg1) {
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(arg0);
					if(jsonObject.has("phone")){
						DialogUtils.showCallPhoneDialog(IndexActivity.this, jsonObject.opt("phone").toString(), null);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

		// 消息列表
		mUserWebView.registerHandler("toMessage", new BridgeHandler() {
			@Override
			public void handler(String arg0, CallBackFunction arg1) {
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(arg0);
					Log.i("hexiuhui----", "phone---" + jsonObject.has("phone"));
					if (jsonObject.has("phone")) {
						startActivity(new Intent(IndexActivity.this, ChatActivity.class).putExtra("userId", jsonObject.opt("phone").toString()));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void onEventMainThread(Message msg) {
		// 支付结果回调
		if (msg.what == PayUtil.PAY_RESULT) {
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("result", msg.arg1);// 0：支付成功，-1：支付失败 -2：用户取消支付
				jsonObject.put("bizId", PayUtilParse.bizId);// 订单号
				jsonObject.put("message", (String) msg.obj);// 支付结果提示信息
			} catch (JSONException e) {
				e.printStackTrace();
			}

			PayUtilParse.callBackFunction.onCallBack(jsonObject.toString());
		} else if (msg.what == OrderUtil.APP_CURRENT_PAGE_MONEY) {
			Log.v("pic", "onEventMainThread");

			radioButtonUser.setChecked(false);
			radioButtonMoney.setChecked(true);

			mTabHost.setCurrentTab(1);
		}
	}

	/** * 解决子Activity无法接收Activity回调的问题 * @author Administrator * */
	public interface OnTabActivityResultListener {
		public void onTabActivityResult(int requestCode, int resultCode,
				Intent data);
	}

	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0x0011) {
				//				CommandTools.showToast("GONE");
				mUserTabRg.setVisibility(View.GONE);
			} else if (msg.what == 0x0012) {
				//				CommandTools.showToast("VISIBLE");
				mUserTabRg.setVisibility(View.VISIBLE);
			}
		}
	};
}
