package com.zhiduan.crowdclient.menuindex.filterlistview;

import java.util.List;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.util.Res;

/**
 * hexiuhui.
 */
@SuppressLint("NewApi")
public class FilterView extends LinearLayout implements View.OnClickListener {

	TextView tvCategory;
//	ImageView ivCategoryArrow;
	TextView tvSort;
	ImageView ivSortArrow;
	TextView tvFilter;
	ImageView ivFilterArrow;
	LinearLayout llCategory;
	LinearLayout llSort;
	LinearLayout llFilter;
//	ListView lvRight;
	LinearLayout llHeadLayout;
	LinearLayout llContentListView;
	View viewMaskBg;
	LinearLayout sort_layout;
	LinearLayout filter_layout;

	private Context mContext;
	private boolean isStickyTop = false; // 是否吸附在顶部
	private boolean isShowing = false;
	private int filterPosition = -1;
	private int panelHeight;
	
	private TextView sign_txt;
	private TextView confirm_btn;
	private TextView reset_btn;
	
	private RadioGroup number_filter_group;
	private RadioGroup sex_filter_group;
	
	private TextView money_sort_1;
	private TextView money_sort_2;
	private TextView time_sort_1;
	private TextView time_sort_2;
	private RadioButton sex_filter_1;
	private RadioButton sex_filter_2;
	private RadioButton number_filter_1;
	private RadioButton number_filter_2;

	public static final String ORDER_BY_MONEY = "1";
	public static final String ORDER_BY_TIME = "2";
	
	public static final String SORT_BY_DAO = "1";
	public static final String SORT_BY_ZHENG = "2";
	
//	private FilterTwoEntity selectedCategoryEntity; // 被选择的分类项

	// private FilterLeftAdapter leftAdapter;
	// private FilterRightAdapter rightAdapter;
	// private FilterOneAdapter sortAdapter;
	// private FilterOneAdapter filterAdapter;

	public FilterView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public FilterView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		this.mContext = context;
		View view = LayoutInflater.from(context).inflate(R.layout.view_filter_layout, this);
		tvCategory = (TextView) view.findViewById(R.id.tv_category);
		tvSort = (TextView) view.findViewById(R.id.tv_sort);
		ivSortArrow = (ImageView) view.findViewById(R.id.iv_sort_arrow);
		tvFilter = (TextView) view.findViewById(R.id.tv_filter);
		ivFilterArrow = (ImageView) view.findViewById(R.id.iv_filter_arrow);
		llCategory = (LinearLayout) view.findViewById(R.id.ll_category);
		llSort = (LinearLayout) view.findViewById(R.id.ll_sort);
		llFilter = (LinearLayout) view.findViewById(R.id.ll_filter);
//		lvRight = (ListView) view.findViewById(R.id.lv_right);
		filter_layout = (LinearLayout) view.findViewById(R.id.filter_layout);
		sort_layout = (LinearLayout) view.findViewById(R.id.sort_layout);
		llHeadLayout = (LinearLayout) view.findViewById(R.id.ll_head_layout);
		llContentListView = (LinearLayout) view.findViewById(R.id.ll_content_list_view);
		viewMaskBg = view.findViewById(R.id.view_mask_bg);
		
		sign_txt = (TextView) view.findViewById(R.id.sign_txt);
		reset_btn = (TextView) view.findViewById(R.id.reset_btn);
		confirm_btn = (TextView) view.findViewById(R.id.confirm_btn);
		
		number_filter_group = (RadioGroup) view.findViewById(R.id.filter_number_group);
		sex_filter_group = (RadioGroup) view.findViewById(R.id.sex_filter_group);
		
		time_sort_1 = (TextView) view.findViewById(R.id.time_sort_1);
		time_sort_2 = (TextView) view.findViewById(R.id.time_sort_2);
		money_sort_1 = (TextView) view.findViewById(R.id.money_sort_1);
		money_sort_2 = (TextView) view.findViewById(R.id.money_sort_2);
		sex_filter_1 = (RadioButton) view.findViewById(R.id.sex_filter_1);
		sex_filter_2 = (RadioButton) view.findViewById(R.id.sex_filter_2);
		number_filter_1 = (RadioButton) view.findViewById(R.id.number_filter_1);
		number_filter_2 = (RadioButton) view.findViewById(R.id.number_filter_2);

		initData();
		initView();
		initListener();
		
//		setFilterTitle();
	}

	private void initData() {

	}

	private void initView() {
		viewMaskBg.setVisibility(GONE);
		llContentListView.setVisibility(GONE);
	}

	private void initListener() {
		llCategory.setOnClickListener(this);
		llSort.setOnClickListener(this);
		llFilter.setOnClickListener(this);
		viewMaskBg.setOnClickListener(this);
		llContentListView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_category:
			filterPosition = 0;
			if (onFilterClickListener != null) {
				onFilterClickListener.onFilterClick(filterPosition);
			}
			break;
		case R.id.ll_sort:
			filterPosition = 1;
			if (onFilterClickListener != null) {
				onFilterClickListener.onFilterClick(filterPosition);
			}
			break;
		case R.id.ll_filter:
			filterPosition = 2;
			if (onFilterClickListener != null) {
				onFilterClickListener.onFilterClick(filterPosition);
			}
			break;
		case R.id.view_mask_bg:
			hide();
			
			//如果选了筛选条件，但是没有点击确认按钮就取消操作，把所有选择数据清空
			if (clickPosition == 2) {
				clearFilterData();
			}
			break;
		}

	}

	public void setCategoryTitle(FilterEntity entity) {
		tvCategory.setText(entity.getKey());
	}
	
	public void setCategoryColor(int color) {
		tvCategory.setTextColor(color);
	}
	
	public void setSortTitle(FilterEntity entity) {
		tvSort.setText(entity.getKey());
	}
	
	public void setFilterTitle(FilterEntity entity) {
		tvFilter.setText(entity.getKey());
	}
	
	// 复位筛选的显示状态
	public void resetFilterStatus() {
		if (isCheckFilter) {
			tvCategory.setTextColor(mContext.getResources().getColor(R.color.font_black_2));
//			ivCategoryArrow.setImageResource(R.drawable.home_down_arrow);
		} else {
			tvCategory.setTextColor(mContext.getResources().getColor(R.color.main_color));
		}
		tvSort.setTextColor(mContext.getResources().getColor(R.color.font_black_2));
		ivSortArrow.setImageResource(R.drawable.home_down_arrow);

		tvFilter.setTextColor(mContext.getResources().getColor(R.color.font_black_2));
		ivFilterArrow.setImageResource(R.drawable.home_down_arrow);
	}

	// 复位所有的状态
	public void resetAllStatus() {
		resetFilterStatus();
		hide();
	}
	
	private int clickPosition;

	// 显示筛选布局
	public void showFilterLayout(int position) {
		resetFilterStatus();
		switch (position) {
		case 0:
			clickPosition = 0;
			setCategoryAdapter();
			break;
		case 1:
			clickPosition = 1;
			setSortAdapter();
			break;
		case 2:
			clickPosition = 2;
			setFilterAdapter();
			break;
		}
		if (position != 0) {
			if (isShowing) {
				hide();
			} else {
				show();
			}
		}
	}
	
//	public void setSelectedEntity() {
//		for (int i = 0; i < mList.size(); i++) {
//        	FilterEntity entity = mList.get(i);
//        	entity.setSelected(entity.getKey().equals(selectedEntity.getKey()));
//		}
//	}
	
	//获取分类选择value
//	public FilterEntity getCategorySelect() {
//		FilterEntity entity = null;
//		
//		List<FilterEntity> category = filterData.getCategory();
//		for (int i = 0; i < category.size(); i++) {
//			FilterEntity filterEntity = category.get(i);
//			if (filterEntity.isSelected()) {
//				entity = filterEntity;
//				break;
//			}
//		}
//		
//		return entity;
//	}
	
	//清空排序条件
	public void clearSortData() {
		time_sort_1.setTextColor(Res.getColor(R.color.black));
		time_sort_2.setTextColor(Res.getColor(R.color.black));
		money_sort_1.setTextColor(Res.getColor(R.color.black));
		money_sort_2.setTextColor(Res.getColor(R.color.black));
	}
	
	//清空筛选条件
	public void clearFilterData() {
		sign_txt.setTextColor(Res.getColor(R.color.black));
		
		number_filter_group.clearCheck();
		sex_filter_group.clearCheck();
		number_filter_1.setTextColor(Res.getColor(R.color.black));
		number_filter_2.setTextColor(Res.getColor(R.color.black));
		
		sex_filter_1.setTextColor(Res.getColor(R.color.black));
		sex_filter_2.setTextColor(Res.getColor(R.color.black));
		
		sex = "";
		taskMode = 0;
		lookFloor = false;
	}
	
	// 设置分类数据
	private void setCategoryAdapter() {
		isCheckFilter = false;
		filter_layout.setVisibility(View.GONE);
		sort_layout.setVisibility(View.GONE);
		tvCategory.setTextColor(mContext.getResources().getColor(R.color.orange));
//		ivCategoryArrow.setImageResource(R.drawable.home_up_arrow);
//		lvRight.setVisibility(VISIBLE);
		resetAllStatus();
		if (onItemCategoryClickListener != null) {
			 onItemCategoryClickListener.onItemCategoryClick();
		 }
		clearSortData();
		clearFilterData();
	}

	private boolean isChangeGroup = false;
	
	 // 设置排序数据
	private void setSortAdapter() {
//		tvSort.setTextColor(mActivity.getResources().getColor(R.color.orange));
		filter_layout.setVisibility(View.GONE);
		sort_layout.setVisibility(View.VISIBLE);
		ivSortArrow.setImageResource(R.drawable.home_up_arrow);
		
		time_sort_1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				isCheckFilter = true;
				resetAllStatus();
				time_sort_1.setTextColor(Res.getColor(R.color.main_color));
				time_sort_2.setTextColor(Res.getColor(R.color.black));
				money_sort_1.setTextColor(Res.getColor(R.color.black));
				money_sort_2.setTextColor(Res.getColor(R.color.black));
				
				if (onItemCategoryClickListener != null) {
					onItemCategoryClickListener.onItemSortClick(SORT_BY_ZHENG, ORDER_BY_TIME);
				}
			}
		});
		
		time_sort_2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				isCheckFilter = true;
				resetAllStatus();
				time_sort_1.setTextColor(Res.getColor(R.color.black));
				time_sort_2.setTextColor(Res.getColor(R.color.main_color));
				money_sort_1.setTextColor(Res.getColor(R.color.black));
				money_sort_2.setTextColor(Res.getColor(R.color.black));
				
				if (onItemCategoryClickListener != null) {
					onItemCategoryClickListener.onItemSortClick(SORT_BY_DAO, ORDER_BY_TIME);
				}
			}
		});
		
		money_sort_1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				isCheckFilter = true;
				resetAllStatus();
				money_sort_1.setTextColor(Res.getColor(R.color.main_color));
				money_sort_2.setTextColor(Res.getColor(R.color.black));
				time_sort_1.setTextColor(Res.getColor(R.color.black));
				time_sort_2.setTextColor(Res.getColor(R.color.black));
				
				if (onItemCategoryClickListener != null) {
					onItemCategoryClickListener.onItemSortClick(SORT_BY_DAO, ORDER_BY_MONEY);
				}
			}
		});
		
		money_sort_2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				isCheckFilter = true;
				resetAllStatus();
				money_sort_1.setTextColor(Res.getColor(R.color.black));
				money_sort_2.setTextColor(Res.getColor(R.color.main_color));
				time_sort_1.setTextColor(Res.getColor(R.color.black));
				time_sort_2.setTextColor(Res.getColor(R.color.black));
				
				if (onItemCategoryClickListener != null) {
					onItemCategoryClickListener.onItemSortClick(SORT_BY_ZHENG, ORDER_BY_MONEY);
				}
			}
		});
		
//		lvRight.setVisibility(VISIBLE);
//		final FilterOneAdapter sortAdapter = new FilterOneAdapter(mContext, filterData.getSorts());
//		lvRight.setAdapter(sortAdapter);
//		lvRight.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				FilterEntity filterEntity = filterData.getSorts().get(position);
//				sortAdapter.setSelectedEntity(filterEntity.getValue());
//				hide();
//				if (onItemCategoryClickListener != null) {
//					onItemCategoryClickListener.onItemSortClick(filterEntity);
//				}
//				
////				tvSort.setText(getSortsSelect().getKey());
//			}
//		});
	}

	String sex = "";
	int taskMode = 0;
	boolean lookFloor = false;
	boolean isCheckFilter = false;
	
	 // 设置筛选数据
	private void setFilterAdapter() {
		filter_layout.setVisibility(View.VISIBLE);
		sort_layout.setVisibility(View.GONE);
//		tvFilter.setTextColor(mActivity.getResources().getColor(R.color.orange));
		ivFilterArrow.setImageResource(R.drawable.home_up_arrow);
		
		number_filter_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup arg0, int position) {
				if (position == number_filter_1.getId()) {
					number_filter_1.setTextColor(Res.getColor(R.color.main_color));
					number_filter_2.setTextColor(Res.getColor(R.color.black));
					
					taskMode = 1;
				} else if (position == number_filter_2.getId()) {
					number_filter_1.setTextColor(Res.getColor(R.color.black));
					number_filter_2.setTextColor(Res.getColor(R.color.main_color));
					taskMode = 2;
				}
			}
		});
		
		sex_filter_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup arg0, int position) {
				if (position == sex_filter_1.getId()) {
					sex_filter_1.setTextColor(Res.getColor(R.color.main_color));
					sex_filter_2.setTextColor(Res.getColor(R.color.black));
					
					sex = "1";
				} else if (position == sex_filter_2.getId()) {
					sex_filter_1.setTextColor(Res.getColor(R.color.black));
					sex_filter_2.setTextColor(Res.getColor(R.color.main_color));
					
					sex = "2";
				}
			}
		});
		
		sign_txt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				sign_txt.setTextColor(Res.getColor(R.color.main_color));
				lookFloor = true;
			}
		});
		
		reset_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				clearFilterData();
			}
		});
		
		confirm_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				isCheckFilter = true;
				resetAllStatus();
				if (onItemCategoryClickListener != null) {
					onItemCategoryClickListener.onItemFilterClick(taskMode, sex, lookFloor);
				}
			}
		});
		
//		lvRight.setVisibility(VISIBLE);
//		final FilterOneAdapter filterAdapter = new FilterOneAdapter(mContext, filterData.getFilters());
//		lvRight.setAdapter(filterAdapter);
//		lvRight.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				FilterEntity filterEntity = filterData.getFilters().get(position);
//				filterAdapter.setSelectedEntity(filterEntity.getValue());
//				hide();
//				if (onItemCategoryClickListener != null) {
//					onItemCategoryClickListener.onItemFilterClick(filterEntity);
//				}
////				tvFilter.setText(getFiltersSelect().getKey());
//			}
//		});
	}

	// 动画显示
	private void show() {
		isShowing = true;
		viewMaskBg.setVisibility(VISIBLE);
		llContentListView.setVisibility(VISIBLE);
		llContentListView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				llContentListView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				//容错处理，解决点击排序只有两个item时，再点击其他动画问题
				if (clickPosition == 1) {
					panelHeight = llContentListView.getHeight() * 2;
				} else {
					panelHeight = llContentListView.getHeight();
				}
				
				ObjectAnimator.ofFloat(llContentListView, "translationY", -panelHeight, 0).setDuration(200).start();
			}
		});
	}

	// 隐藏动画
	public void hide() {
		isShowing = false;
		resetFilterStatus();
		viewMaskBg.setVisibility(View.GONE);
		ObjectAnimator
				.ofFloat(llContentListView, "translationY", 0, -panelHeight)
				.setDuration(200).start();
	}

	// 是否吸附在顶部
	public void setStickyTop(boolean stickyTop) {
		isStickyTop = stickyTop;
	}

	// 是否显示
	public boolean isShowing() {
		return isShowing;
	}

	// 筛选视图点击
	private OnFilterClickListener onFilterClickListener;

	public void setOnFilterClickListener(
			OnFilterClickListener onFilterClickListener) {
		this.onFilterClickListener = onFilterClickListener;
	}

	public interface OnFilterClickListener {
		void onFilterClick(int position);
	}

	// 分类Item点击
	private OnItemFilterClickListener onItemCategoryClickListener;

	public void setOnItemCategoryClickListener(OnItemFilterClickListener onItemCategoryClickListener) {
		this.onItemCategoryClickListener = onItemCategoryClickListener;
	}

	public interface OnItemFilterClickListener {
		void onItemCategoryClick();
		void onItemSortClick(String sortBy, String orderBy);
		void onItemFilterClick(int number, String sex, boolean lookFloor);
	}
}
