package com.materlistview.test;

import android.content.Context;

import com.materlistview.R;
import com.materlistview.cardModel.ExtendedCard;


public class TestItemCard extends ExtendedCard {
	private TestModel result;
	private int position;

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public TestModel getResult() {
		return result;
	}

	public void setResult(TestModel result) {
		this.result = result;
	}

	public TestItemCard(Context context) {
		super(context);
	}

	@Override
	public int getLayout() {
		// TODO Auto-generated method stub
		return R.layout.card_test_model;
	}

}
