package com.zhiduan.crowdclient.view;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.zhiduan.crowdclient.R;

/**
 * @author hexiuhui
 * 
 */
public class DropDownListView extends ListView {

	private float mLastY = -1; // save event y
	private Scroller mScroller; // used for scroll back
	private OnScrollListener mScrollListener; // user's scroll listener

	// the interface to trigger refresh and load more.
	private IXListViewListener mListViewListener;

	// -- header view
	private RefreshHeader mHeaderView;
	// header view content, use it to calculate the Header's height. And hide it
	// when disable pull refresh.
	private RelativeLayout mHeaderViewContent;
	private TextView mHeaderTimeView;
	private int mHeaderViewHeight; // header view's height
	private boolean mEnablePullRefresh = true;
	private boolean mPullRefreshing = false; // is refreashing.

	// -- footer view
	private RefreshFooter mFooterView = null;
	private boolean mEnablePullLoad = false;
	private boolean mPullLoading = false;

	// total list items, used to detect is at the bottom of listview.
	private int mTotalItemCount;

	// for mScroller, scroll back from header or footer.
	private int mScrollBack;
	private final static int SCROLLBACK_HEADER = 0;
	private final static int SCROLLBACK_FOOTER = 1;

	private final static int SCROLL_DURATION = 400; // scroll back duration
	private final static int PULL_LOAD_MORE_DELTA = 50; // when pull up >= 50px
	// at bottom, trigger
	// load more.
	private final static float OFFSET_RADIO = 1.8f; // support iOS like pull
	// feature.
	private Context m_context;

	// -- class fields

	// fields used for handling touch events
	private final PointF mTouchPoint = new PointF();
	private int mTouchSlop;

	// fields used for drawing shadow under a pinned section
	private GradientDrawable mShadowDrawable;

	/** Delegating listener, can be null. */
	// OnScrollListener mDelegateOnScrollListener;

	/**
	 * Pinned view Y-translation. We use it to stick pinned view to the next
	 * section.
	 */
	int mTranslateY;

	/** Scroll listener which does the magic */
	private final OnScrollListener mOnScrollListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// if (mDelegateOnScrollListener != null) { // delegate
			// mDelegateOnScrollListener.onScrollStateChanged(view,
			// scrollState);
			// }
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {

			// if (mDelegateOnScrollListener != null) { // delegate
			// mDelegateOnScrollListener.onScroll(view, firstVisibleItem,
			// visibleItemCount, totalItemCount);
			// }

			// get expected adapter or fail fast
			ListAdapter adapter = getAdapter();
			if (adapter == null || visibleItemCount == 0)
				return; // nothing to do

		};

	};

	// -- constructors

	public DropDownListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		m_context = context;
		initView();
	}

	public DropDownListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		m_context = context;
		if (isInEditMode()) {
			return;
		}
		initView();
	}

	private void initView() {
		setOnScrollListener(mOnScrollListener);
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
		initShadow(true);

		mScroller = new Scroller(m_context, new DecelerateInterpolator());
		// XListView need the scroll event, and it will dispatch the event to
		// user's listener (as a proxy).
		// super.setOnScrollListener(this);

		// init header view
		mHeaderView = new RefreshHeader(m_context);
		if (isInEditMode()) {
			return;
		}
		mHeaderViewContent = (RelativeLayout) mHeaderView
				.findViewById(R.id.refresh_header_content);
		mHeaderTimeView = (TextView) mHeaderView
				.findViewById(R.id.refresh_header_time);
//		addHeaderView(mHeaderView);
		addHeaderView(mHeaderView ,null,false);
		// init header height
		mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						mHeaderViewHeight = mHeaderViewContent.getHeight();
						getViewTreeObserver()
						.removeGlobalOnLayoutListener(this);
					}
				});
	}

	// -- pinned section drawing methods
	public void initShadow(boolean visible) {

		if (visible) {
			if (mShadowDrawable == null) {
				mShadowDrawable = new GradientDrawable(Orientation.TOP_BOTTOM,
						new int[] { Color.parseColor("#ffa0a0a0"),
						Color.parseColor("#50a0a0a0"),
						Color.parseColor("#00a0a0a0") });
			}
		} else {
			if (mShadowDrawable != null) {
				mShadowDrawable = null;
			}
		}
	}

	int findFirstVisibleSectionPosition(int firstVisibleItem,
			int visibleItemCount) {
		ListAdapter adapter = getAdapter();

		if (firstVisibleItem + visibleItemCount >= adapter.getCount())
			return -1; // dataset has changed, no candidate

		for (int childIndex = 0; childIndex < visibleItemCount; childIndex++) {
			int position = firstVisibleItem + childIndex;
			int viewType = adapter.getItemViewType(position);
		}
		return -1;
	}

	int findCurrentSectionPosition(int fromPosition) {
		ListAdapter adapter = getAdapter();

		if (fromPosition >= adapter.getCount())
			return -1; // dataset has changed, no candidate

		if (adapter instanceof SectionIndexer) {
			// try fast way by asking section indexer
			SectionIndexer indexer = (SectionIndexer) adapter;
			int sectionPosition = indexer.getSectionForPosition(fromPosition);
			int itemPosition = indexer.getPositionForSection(sectionPosition);
			int typeView = adapter.getItemViewType(itemPosition);
		}

		// try slow way by looking through to the next section item above
		for (int position = fromPosition; position >= 0; position--) {
			int viewType = adapter.getItemViewType(position);
		}
		return -1; // no candidate found
	}

	private ScrollViewListener scrollViewListener = null;
	
	public interface ScrollViewListener {
		void onScrollChanged(DropDownListView dropDownListView, int x, int y,
				int oldx, int oldy);
	}
	
	public void setScrollViewListener(ScrollViewListener indexActivity) {
		this.scrollViewListener = indexActivity;
	}
	
	@Override
	protected void onScrollChanged(int x, int y, int oldx, int oldy) {
		// TODO Auto-generated method stub
		super.onScrollChanged(x, y, oldx, oldy);
		if (scrollViewListener != null) {
			scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
		}
	}
	
	@Override
	public void setOnScrollListener(OnScrollListener listener) {
		if (listener == mOnScrollListener) {
			super.setOnScrollListener(listener);
		} else {
			// mDelegateOnScrollListener = listener;
		}
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		super.onRestoreInstanceState(state);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {

		final float x = ev.getX();
		final float y = ev.getY();
		final int action = ev.getAction();

		if (action == MotionEvent.ACTION_DOWN) { // create
			// touch
			// target

			// user touched pinned view
			mTouchPoint.x = x;
			mTouchPoint.y = y;

		}

		if (action == MotionEvent.ACTION_UP) { // perform onClick on pinned
			// view
			super.dispatchTouchEvent(ev);

		} else if (action == MotionEvent.ACTION_CANCEL) { // cancel

		} else if (action == MotionEvent.ACTION_MOVE) {
			if (Math.abs(y - mTouchPoint.y) > mTouchSlop) {

				// cancel sequence on touch target
				MotionEvent event = MotionEvent.obtain(ev);
				event.setAction(MotionEvent.ACTION_CANCEL);
				event.recycle();

				// provide correct sequence to super class for further
				// handling
				super.dispatchTouchEvent(ev);

			}

			return true;
		}

		// call super if this was not our pinned view
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * enable or disable pull down refresh feature.
	 * 
	 * @param enable
	 */
	public void setPullRefreshEnable(boolean enable) {
		mEnablePullRefresh = enable;
		if (!mEnablePullRefresh) { // disable, hide the content
			mHeaderViewContent.setVisibility(View.INVISIBLE);
		} else {
			mHeaderViewContent.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * enable or disable pull up load more feature.
	 * 
	 * @param enable
	 */
	public void setPullLoadEnable(boolean enable) {
		if (mEnablePullLoad == enable)
			return;

		mEnablePullLoad = enable;
		if (!mEnablePullLoad) {
			if (mFooterView != null) {
				this.removeFooterView(mFooterView);
			}
		} else {
			// mPullLoading = false;
			if (mFooterView == null) {
				mFooterView = new RefreshFooter(m_context);
				mFooterView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						startLoadMore();
					}
				});
			}
			this.addFooterView(mFooterView);
			mFooterView.setState(RefreshFooter.STATE_NORMAL);
			// both "pull up" and "click" will invoke load more.
		}
	}

	/**
	 * stop refresh, reset header view.
	 */
	public void stopRefresh() {
		if (mPullRefreshing == true) {
			mPullRefreshing = false;
			// 重设当前View的高度
			resetHeaderHeight();
		}
	}

	/**
	 * stop load more, reset footer view.
	 */
	public void stopLoadMore() {
		
		if (mPullLoading == true) {
			mPullLoading = false;
			mFooterView.setState(RefreshFooter.STATE_NORMAL);
		}
	}

	private void startLoadMore() {
		mPullLoading = true;
		mFooterView.setState(RefreshFooter.STATE_LOADING);
		if (mListViewListener != null) {
			mListViewListener.onLoadMore();
		}
	}

	private void invokeOnScrolling() {
		if (mScrollListener instanceof OnXScrollListener) {
			OnXScrollListener l = (OnXScrollListener) mScrollListener;
			l.onXScrolling(this);
		}
	}

	private void updateHeaderHeight(float delta) {
		mHeaderView.setVisiableHeight((int) delta
				+ mHeaderView.getVisiableHeight());
		if (mEnablePullRefresh && !mPullRefreshing) { // 未处于刷新状态，更新箭头
			if (mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
				mHeaderView.setState(RefreshHeader.STATE_READY);
			} else {
				mHeaderView.setState(RefreshHeader.STATE_NORMAL);
			}
		}
		setSelection(0); // scroll to top each time
	}

	/**
	 * reset header view's height.
	 */
	private void resetHeaderHeight() {
		int height = mHeaderView.getVisiableHeight();
		if (height == 0) // not visible.
			return;
		// refreshing and header isn't shown fully. do nothing.
		if (mPullRefreshing && height <= mHeaderViewHeight) {
			return;
		}
		int finalHeight = 0; // default: scroll back to dismiss header.
		// is refreshing, just scroll back to show all the header.
		if (mPullRefreshing && height > mHeaderViewHeight) {
			finalHeight = mHeaderViewHeight;
		}

		mScrollBack = SCROLLBACK_HEADER;
		mScroller.startScroll(0, height, 0, finalHeight - height,
				SCROLL_DURATION);
		// trigger computeScroll
		invalidate();
	}

	private void updateFooterHeight(float delta) {
		int height = mFooterView.getBottomMargin() + (int) delta;
		if (mEnablePullLoad && !mPullLoading) {
			if (height > PULL_LOAD_MORE_DELTA) { // height enough to invoke load
				// more.
				mFooterView.setState(RefreshFooter.STATE_READY);
			} else {
				mFooterView.setState(RefreshFooter.STATE_NORMAL);
			}
		}
		mFooterView.setBottomMargin(height);

		// setSelection(mTotalItemCount - 1); // scroll to bottom
	}

	private void resetFooterHeight() {
		int bottomMargin = mFooterView.getBottomMargin();
		if (bottomMargin > 0) {
			mScrollBack = SCROLLBACK_FOOTER;
			mScroller.startScroll(0, bottomMargin, 0, -bottomMargin,
					SCROLL_DURATION);
			invalidate();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mLastY == -1) {
			mLastY = ev.getRawY();
		}

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastY = ev.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float deltaY = ev.getRawY() - mLastY;
			mLastY = ev.getRawY();
			if (getFirstVisiblePosition() == 0
					&& (mHeaderView.getVisiableHeight() > 0 || deltaY > 0)) {
				// the first item is showing, header has shown or pull down.
				updateHeaderHeight(deltaY / OFFSET_RADIO);
				invokeOnScrolling();
			} else if (mEnablePullLoad
					&& (getLastVisiblePosition() == mTotalItemCount - 1)
					&& (mFooterView.getBottomMargin() > 0 || deltaY < 0)) {
				// last item, already pulled up or want to pull up.
				updateFooterHeight(-deltaY / OFFSET_RADIO);
			}
			break;
		default:
			mLastY = -1; // reset
			if (getFirstVisiblePosition() == 0) {
				// invoke refresh
				if (mEnablePullRefresh
						&& mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
					mPullRefreshing = true;
					mHeaderView.setState(RefreshHeader.STATE_REFRESHING);
					if (mListViewListener != null) {

						//两秒内禁止重复刷新
						if (isQuit == false) {
							isQuit = true;

							TimerTask task = null;
							task = new TimerTask() {
								@Override
								public void run() {
									isQuit = false;
								}
							};
							
							timerBack.schedule(task, 2000);
						} else {
							mListViewListener.onRefresh();
						}

					}
				}
				resetHeaderHeight();
			} else if (getLastVisiblePosition() == mTotalItemCount - 1) {
				// invoke load more.
				if (mEnablePullLoad) {
					if (mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA) {
						startLoadMore();
					}
					resetFooterHeight();
				}
			}
			break;
		}
		return super.onTouchEvent(ev);
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			if (mScrollBack == SCROLLBACK_HEADER) {
				mHeaderView.setVisiableHeight(mScroller.getCurrY());
			} else {
				mFooterView.setBottomMargin(mScroller.getCurrY());
			}
			postInvalidate();
			invokeOnScrolling();
		}
		super.computeScroll();
	}

	public void setXListViewListener(IXListViewListener l) {
		mListViewListener = l;
	}

	/**
	 * you can listen ListView.OnScrollListener or this one. it will invoke
	 * onXScrolling when header/footer scroll back.
	 */
	public interface OnXScrollListener extends OnScrollListener {
		public void onXScrolling(View view);
	}

	/**
	 * implements this interface to get refresh/load more event.
	 */
	public interface IXListViewListener {
		public void onRefresh();

		public void onLoadMore();
	}

	// 设置加载更多隐藏
	public void setLoadHide() {
		if (mFooterView != null) {
			// 隐藏底部的view
			mFooterView.setState(RefreshFooter.STATE_FINISH);
			// 取消底部的点击事件
			mFooterView.setOnClickListener(null);
		}
	}

	// 设置加载更多显示
	public void setLoadShow() {
		if (mFooterView != null) {
			// 隐藏底部的view
			mFooterView.setState(RefreshFooter.STATE_NORMAL);
			// 取消底部的点击事件
			mFooterView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startLoadMore();
				}
			});
		}
	}
	
	/**
	 * 设置数据加载完毕
	 * 暂无更多的数据
	 */
	public void setLoadFinished(){
		
		if(mFooterView != null){
			
			mFooterView.setState(RefreshFooter.STATE_FINISH);
		}
	}

	/**
	 * 返回
	 */
	private static Boolean isQuit = false;
	private Timer timerBack = new Timer();

	/**
	 * 设置显示的刷新时间
	 */
	public void setRefreshTime(String time) {
		mHeaderTimeView.setText(time);
	}
	@Override
	public void setOnItemClickListener(
			android.widget.AdapterView.OnItemClickListener listener) {
		// TODO Auto-generated method stub
		
		super.setOnItemClickListener(listener);
	}
}
