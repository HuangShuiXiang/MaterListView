package com.materlistview.materListView;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.materlistview.R;
import com.materlistview.cardModel.Card;
import com.materlistview.cardModel.RecycleViewLoadMore;
import com.materlistview.event.BusProvider;
import com.materlistview.event.DataSetChangedEvent;
import com.materlistview.event.DismissEvent;
import com.squareup.otto.Subscribe;

import java.util.Collection;


public class MaterialListView extends RecyclerView {

	private static final int DEFAULT_COLUMNS_PORTRAIT = 1;
	private static final int DEFAULT_COLUMNS_LANDSCAPE = 2;
	public static final String TAG_LOADMORE = "TAG_LOADMORE";
	private OnDismissCallback mDismissCallback;
	private SwipeDismissRecyclerViewTouchListener mDismissListener;
	private View emptyView;
	
	private int mColumnCount;
	private int mColumnCountLandscape = DEFAULT_COLUMNS_LANDSCAPE;
	private int mColumnCountPortrait = DEFAULT_COLUMNS_PORTRAIT;
	private Context mContext;
	private boolean isTwoMaterialListView=false;
	private boolean isNeedAdapterAnim=true;
	final AdapterDataObserver observer = new AdapterDataObserver() {
		@Override public void onChanged() {
			super.onChanged();
			checkIfEmpty();
		}
	};

	public MaterialListView(Context context) {
		this(context, null);
		mContext=context;
	}

	public MaterialListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		mContext=context;
	}

	public MaterialListView(Context context, AttributeSet attrs, int defStyle) {
	        super(context, attrs, defStyle);
	        mContext=context;
		mDismissListener = new SwipeDismissRecyclerViewTouchListener(this, new SwipeDismissRecyclerViewTouchListener.DismissCallbacks() {
			@Override
			public boolean canDismiss(final int position) {
				return ((IMaterialListAdapter) getAdapter()).getCard(position).isDismissible();
			}

			@Override
			public void onDismiss(final RecyclerView recyclerView, final int[] reverseSortedPositions) {
				for (int reverseSortedPosition : reverseSortedPositions) {
					final Card card = ((IMaterialListAdapter) getAdapter()).getCard(reverseSortedPosition);
					((IMaterialListAdapter) getAdapter()).remove(card, false);
					if (mDismissCallback!=null) {
						mDismissCallback.onDismiss(card, reverseSortedPosition);
					}
					Log.d("DissmissListener", "delete: " + card.getClass());
				}
			}
		});
		setOnTouchListener(mDismissListener);
		setOnScrollListener(mDismissListener.makeScrollListener());
		setItemAnimator(new DefaultItemAnimator());
		MaterialListAdapter materialListAdapter=new MaterialListAdapter(context, this);
		//AlphaInAnimationAdapter alphaInAnimationAdapter=new AlphaInAnimationAdapter(materialListAdapter);
		//ScaleInAnimationAdapter scaleInAnimationAdapter=new ScaleInAnimationAdapter(alphaInAnimationAdapter);
		setAdapter(materialListAdapter);
        
		Log.d(getClass().getSimpleName(), "Setup...");

		if(attrs != null) {
			// get the number of columns
			TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MaterialListView, defStyle, 0);

			if(typedArray.hasValue(R.styleable.MaterialListView_column_count_landscape_materialListView) ||
					typedArray.hasValue(R.styleable.MaterialListView_column_count_portrait_materialListView) ||
					typedArray.hasValue(R.styleable.MaterialListView_column_count_materialListView)) {
				Log.d(getClass().getSimpleName(), "Has ColumnCount set");
			}

			mColumnCount = typedArray.getInteger(R.styleable.MaterialListView_column_count_materialListView, 0);
			if (mColumnCount > 0) {
				mColumnCountPortrait = mColumnCount;
				mColumnCountLandscape = mColumnCount;
			}
			else {
				mColumnCountPortrait = typedArray.getInteger(
						R.styleable.MaterialListView_column_count_portrait_materialListView,
						DEFAULT_COLUMNS_PORTRAIT);
				mColumnCountLandscape = typedArray.getInteger(
						R.styleable.MaterialListView_column_count_landscape_materialListView,
						DEFAULT_COLUMNS_LANDSCAPE);
			}

			boolean isLandscape = isLandscape();
			mColumnCount = isLandscape ? mColumnCountLandscape : mColumnCountPortrait;
			setColumnLayout(mColumnCount);

			typedArray.recycle();
		}
		
		
    }

	public void remove(Card card) {
	       /* if (card.isDismissible()) {
			BusProvider.dismiss(card);
	        }else{
	        BusProvider.dismiss(card);
	        }*/
		onCardDismiss(card, false);
	}

    public void add(Card card) {
		((IMaterialListAdapter) getAdapter()).add(card);

    }
	public void add(int position,Card card) {
		((MaterialListAdapter)getAdapter()).getAllList().add(position,card);
		getAdapter().notifyDataSetChanged();
	}
    public void addAll(Card... cards) {
		((IMaterialListAdapter) getAdapter()).addAll(cards);
    }

    public void addAll(Collection<Card> cards) {
		((IMaterialListAdapter) getAdapter()).addAll(cards);
    }

    public void clear(){
        ((MaterialListAdapter)getAdapter()).clear();
    }

	@Override
	public void setAdapter(final Adapter adapter) {
		final Adapter oldAdapter = getAdapter();
		if (oldAdapter != null) {
			oldAdapter.unregisterAdapterDataObserver(observer);
		}
		/*if(adapter != null) {
			if(adapter instanceof IMaterialListAdapter) {
				super.setAdapter(adapter);
				adapter.registerAdapterDataObserver(observer);
			} else {
				throw new IllegalArgumentException("The Adapter must implement IMaterialListAdapter");
			}
		}*/
		super.setAdapter(adapter);
		adapter.registerAdapterDataObserver(observer);
	}

    public void setOnDismissCallback(OnDismissCallback callback) {
        mDismissCallback = callback;
    }

    @Subscribe
    public void onNotifyDataSetChanged(DataSetChangedEvent event) {
        getAdapter().notifyDataSetChanged();
    }

    @Subscribe
    public void onCardDismiss(DismissEvent event) {
		int position = ((IMaterialListAdapter) getAdapter()).getPosition(event.getDismissedCard());
		
		ViewHolder holder = findViewHolderForPosition(position);
		//如果卡为不可见时
		if (holder==null) {
			final Card card = ((IMaterialListAdapter) getAdapter()).getCard(position);
			((IMaterialListAdapter) getAdapter()).remove(card, false);
		}else{
		mDismissListener.dismissCard(holder.itemView, position);
		}
    }
    public void  onCardDismiss(Card currentCard,boolean havaAnimtion) {
		int position = ((IMaterialListAdapter) getAdapter()).getPosition(currentCard);
		
		ViewHolder holder = findViewHolderForPosition(position);
		//如果卡为不可见时
		if (holder==null) {
			final Card card = ((IMaterialListAdapter) getAdapter()).getCard(position);
			((IMaterialListAdapter) getAdapter()).remove(card, false);
		}else{
			if (havaAnimtion) {
				mDismissListener.dismissCard(holder.itemView, position);
			}else
			{
			((IMaterialListAdapter) getAdapter()).remove(currentCard, false);
			}
		
		}
    }
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        BusProvider.register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        BusProvider.unregister(this);
    }

	@Override
	protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		boolean isLandscape = isLandscape();
		int newColumnCount = isLandscape ? mColumnCountLandscape : mColumnCountPortrait;
		if (mColumnCount != newColumnCount) {
			mColumnCount = newColumnCount;
			setColumnLayout(mColumnCount);
		}
	}

	private void setColumnLayout(int columnCount) {
		if(columnCount > 1) {
			setLayoutManager(new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL));
		} else {
			setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
		}

		Log.d(getClass().getSimpleName(), "ColumnCount="+columnCount);
	}

	private boolean isLandscape() {
		return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
	}
 
	void checkIfEmpty() {
		if (emptyView != null) {
			emptyView.setVisibility(getAdapter().getItemCount() > 0 ? GONE : VISIBLE);
		}
	}
	
	public void setEmptyView(View emptyView) {
		this.emptyView = emptyView;
		checkIfEmpty();
	}

    public void addOnItemTouchListener(RecyclerItemClickListener.OnItemClickListener listener){

        RecyclerItemClickListener itemClickListener = new RecyclerItemClickListener(getContext(), listener);

        itemClickListener.setRecyclerView(this);
        super.addOnItemTouchListener(itemClickListener);
    }
    public static enum LAYOUT_MANAGER_TYPE {
        LINEAR,
        GRID,
        STAGGERED_GRID
    }
    
    public void addLoadMoreView(){
         
         Card card=new RecycleViewLoadMore(mContext);
         card.setDismissible(true);
         card.setTag(TAG_LOADMORE);
        // ((MaterialListAdapter)getAdapter()).getAllList().add(card);
		//getAdapter().notifyItemInserted(((MaterialListAdapter)getAdapter()).getAllList().size()-1);
         add(card);
    }
    public Card getCardByCard(Object tag)
    {
    	for (int i = 0; i < ((MaterialListAdapter)getAdapter()).getAllList().size(); i++) {
    		Card c=((MaterialListAdapter)getAdapter()).getAllList().get(i);
			if (c.getTag()!=null && tag.toString().equals(c.getTag().toString())) {
				return c;
			}
		}
    	return null;
    }

	public boolean isTwoMaterialListView() {
		return isTwoMaterialListView;
	}

	public void setTwoMaterialListView(boolean isTwoMaterialListView) {
		this.isTwoMaterialListView = isTwoMaterialListView;
	}

	public boolean isNeedAdapterAnim() {
		return isNeedAdapterAnim;
	}

	/**
	 * @param isNeedAdapterAnim 是否显示列表上下拉出现的动画  而listview.setItemAnimator是设置从左往右的动画
	 */
	public void setIsNeedAdapterAnim(boolean isNeedAdapterAnim) {
		this.isNeedAdapterAnim = isNeedAdapterAnim;
	}
}
