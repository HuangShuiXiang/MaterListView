package com.materlistview.materListView;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;

import com.materlistview.R;
import com.materlistview.cardModel.Card;


public class SuperSwipeRefresh extends SwipeRefreshLayout implements OnRefreshListener {
	 /**
     * 当前页码，默认是1
     */
    private int page=1;
    /**
     * 每页取多少个
     */
    private int pageSize=30;
    /**
     * 滑动到最下面时的上拉操作
     */
    private int mTouchSlop;
    /**
     * RecyclerView 实例
     */
    private MaterialListView mRecyclerView;
    /**
     * 上拉监听器, 到了最底部的上拉加载操作
     */
    public OnLoadMoreListener onLoadMoreListener;
    /**
     * 下拉监听器, 到了最顶部的上拉加载操作
     */
    public OnRefreshReceyerListener onRefreshReceyerListener;

	/**
     * ListView的加载中footer
     */
    private View mFooterView;
    /**
     * 按下时的y坐标
     */
    private int mYDown;
    /**
     * 抬起时的y坐标, 与mYDown一起用于滑动到底部时判断是上拉还是下拉
     */
//    private int mLastY;
    /**
     * 是否在加载中 ( 上拉加载更多 )
     */
    private boolean isLoading = false;
    private float mLastY = -1;
    /**
     * 最后一行
     */
    boolean isLastRow = false;
    /**
     * 是否还有数据可以加载
     */
    private Handler mHandler = new Handler();
    
	private int lastVisibleItemPosition;
	protected MaterialListView.LAYOUT_MANAGER_TYPE layoutManagerType;
	private int currentScrollState;
	private boolean isLoadingMore;
	private Context mContext;
	/**
     * 
     * 是否还有数据可以加载
     */
	private boolean enableLoadMore;
	public SuperSwipeRefresh(Context context) {
        super(context);
        mContext=context;
    }
    public SuperSwipeRefresh(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mFooterView = LayoutInflater.from(context).inflate(R.layout.footer_layout, null, false);
        setColorScheme(R.color.color_refresh1, R.color.color_refresh2, R.color.color_refresh3, R.color.color_refresh4);
        setOnRefreshListener(this);

    }
    public void setEnableRefresh(boolean enableRefresh)
    {
        if (!enableRefresh)
        super.setProgressBackgroundColor(R.color.transparent);
    }
    private final Runnable mRefreshDone = new Runnable() {

        @Override
        public void run() {
        	if (onRefreshReceyerListener!=null) {
        		setPage(1);
        		onRefreshReceyerListener.Refresh(mRecyclerView);
			}
        }

    };
    /**
     * 设置默认的childview-必须要设置
     *
     * @param context
     * @param recyclerView
     */
    public void setView(Context context, RecyclerView recyclerView) {

    	    mRecyclerView=(MaterialListView) recyclerView;
            // 设置滚动监听器给ListView, 使得滚动的情况下也可以自动加载
            recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {


				private int[] lastPositions;
				
				@Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    currentScrollState = newState;
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    int visibleItemCount = layoutManager.getChildCount();
                   // int totalItemCount = layoutManager.getItemCount();
                    int totalItemCount = recyclerView.getAdapter().getItemCount();
                    if ((visibleItemCount > 0 && newState == RecyclerView.SCROLL_STATE_IDLE &&
                            (lastVisibleItemPosition) >= totalItemCount - 1) && !isLoadingMore && enableLoadMore) {
                        
                        if (onLoadMoreListener != null) {
                        	setPage(getPage()+1);
                        	isLoadingMore = true;
                        	//瀑布流模式先不加圈圈
                        	if (!(mRecyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager)) {
                        		mRecyclerView.addLoadMoreView();
							}
                            onLoadMoreListener.loadMore(totalItemCount, lastVisibleItemPosition);
                        }
                    }
                }
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

                    //  int lastVisibleItemPosition = -1;
                    if (layoutManagerType == null) {
                        if (layoutManager instanceof LinearLayoutManager) {
                            layoutManagerType = MaterialListView.LAYOUT_MANAGER_TYPE.LINEAR;
                        } else if (layoutManager instanceof GridLayoutManager) {
                            layoutManagerType = MaterialListView.LAYOUT_MANAGER_TYPE.GRID;
                        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                            layoutManagerType = MaterialListView.LAYOUT_MANAGER_TYPE.STAGGERED_GRID;
                        } else {
                            throw new RuntimeException("Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
                        }
                    }
                    switch (layoutManagerType) {
                    case LINEAR:
                        lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                        break;
                    case GRID:
                        lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                        break;
                    case STAGGERED_GRID:
                        StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                        //注销此处 每次重新创建
//                        if (lastPositions == null)
                            lastPositions = new int[staggeredGridLayoutManager.getSpanCount()];

                        staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions);
                        lastVisibleItemPosition = findMax(lastPositions);
                        break;
                }
//                    Log.d("onScrolled", "dx=" + dx + "---dy=" + dy);
                }
            });

    }
    private int findMax(int[] lastPositions) {
        int max = Integer.MIN_VALUE;
        for (int value : lastPositions) {
            if (value > max)
                max = value;
        }
        return max;
    }
  
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }
    /**
     * 是否可以加载更多, listview不在加载中, 且为上拉操作.
     *
     * @param canload 是否还有可以加载的数据
     * @return
     */
    private boolean canLoad(boolean canload) {
        return canload && !isLoading && isPullUp();
    }
    /**
     * 判断是否到了最底部
     */
    private boolean isBottom() {
        /*if (mListView != null && mListView.getAdapter() != null) {
            return mListView.getLastVisiblePosition() == (mListView.getAdapter().getCount() - 1);
        }*/
        return false;
    }
    /**
     * 是否是上拉操作
     *
     * @return
     */
    private boolean isPullUp() {
        Log.d("VRefresh", "isPullUp--->");
        return (mYDown - mLastY) >= mTouchSlop;
    }
   
    /**
     * @param loading
     */
    public void setLoading(boolean loading) {
        isLoading = loading;
        if (isLoading) {
//            mListView.addFooterView(mFooterView);
            mFooterView.setVisibility(View.VISIBLE);
        } else {
//            mListView.removeFooterView(mFooterView);
            mFooterView.setVisibility(View.GONE);
            mYDown = 0;
            mLastY = 0;
        }
    }
   
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
   

   
    private void refresh() {
        mHandler.removeCallbacks(mRefreshDone);
        mHandler.postDelayed(mRefreshDone, 1000);
    }
	@Override
	public void onRefresh() {
		refresh();
	}
    public interface OnLoadMoreListener {

        public void loadMore(int itemsCount, int maxLastVisiblePosition);
    }
    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
		this.onLoadMoreListener = onLoadMoreListener;
	}
    public interface OnRefreshReceyerListener {

        public void Refresh(MaterialListView recyclerView);
    }
    public void setOnRefreshReceyerListener(OnRefreshReceyerListener onRefreshListener) {
		this.onRefreshReceyerListener = onRefreshListener;
	}
    public void setEnableLoadMore(boolean b)
    {
    	enableLoadMore=b;
    }
    public void setLoadMoreFinish()
    {
    	
    	
    	//((MaterialListAdapter)mRecyclerView.getAdapter()).getAllList().remove(card);
    	
    	new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(300);
					((Activity)mContext).runOnUiThread(new Runnable() {
						public void run() {
							final Card card=mRecyclerView.getCardByCard(MaterialListView.TAG_LOADMORE);
							if (card!=null) {
					    		((MaterialListAdapter)mRecyclerView.getAdapter()).remove(card, false);
							}
						}
					});
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
    	
    	
		//((MaterialListAdapter)mRecyclerView.getAdapter()).notifyItemRemoved(((MaterialListAdapter)mRecyclerView.getAdapter()).getAllList().size());
    	
    	isLoadingMore = false;
    }
    public void setLoadMoreFail()
    {
    	
    	final Card card=mRecyclerView.getCardByCard(MaterialListView.TAG_LOADMORE);
    	//((MaterialListAdapter)mRecyclerView.getAdapter()).getAllList().remove(card);
    	if (card!=null) {
    		((MaterialListAdapter)mRecyclerView.getAdapter()).remove(card, false);
		}
    	
		//((MaterialListAdapter)mRecyclerView.getAdapter()).notifyItemRemoved(((MaterialListAdapter)mRecyclerView.getAdapter()).getAllList().size());
    	
    	isLoadingMore = false;
    	setPage(getPage()-1);
    }
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	/**
	 * 请求成功时调用
	 * @param
	 * @param isLoadMore 
	 */

	public void updateSwipeRefreshSuccess(int loadMoreCount,final boolean isLoadMore) {

		if (isLoadMore) {
			setLoadMoreFinish();
		}else
		{
			setRefreshing(false);
		}
		if (isLoadMore && loadMoreCount<getPageSize() || isLoadMore && loadMoreCount==0) {
			setEnableLoadMore(false);
		}else{
			setEnableLoadMore(true);
		}
	}
	/**
	 * 请求失败时调用
	 * @param
	 * @param isLoadMore
	 */
	public void updateSwipeRefreshFail(final boolean isLoadMore) {
		if (isLoadMore) {
			setLoadMoreFail();
		}else
		{
			setRefreshing(false);
		}
	}

}