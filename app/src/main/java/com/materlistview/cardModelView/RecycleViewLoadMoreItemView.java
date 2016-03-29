package com.materlistview.cardModelView;

import android.content.Context;
import android.util.AttributeSet;

import com.materlistview.cardModel.RecycleViewLoadMore;


public class RecycleViewLoadMoreItemView extends CardItemView<RecycleViewLoadMore>{
	private Context mContext;
	public RecycleViewLoadMoreItemView(Context context) {
		super(context);
		mContext=context;
		// TODO Auto-generated constructor stub
	}

	public RecycleViewLoadMoreItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext=context;
		// TODO Auto-generated constructor stub
	}

	public RecycleViewLoadMoreItemView(Context context,
			AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext=context;
		// TODO Auto-generated constructor stub
	}
	@Override
	public void build(RecycleViewLoadMore card) {
		// TODO Auto-generated method stub
		super.build(card);
		
	}
}
