package com.materlistview.cardModel;


import android.content.Context;

import com.materlistview.event.BusProvider;


/**
 * The ExtendedCard set two Buttons (right and left).
 */
public abstract class ExtendedCard extends SimpleCard {
    protected String leftButtonText;
    protected String rightButtonText;
    protected int mRightButtonTextColor = -1;
    protected OnButtonPressListener onLeftButtonPressedListener;
    protected OnButtonPressListener onRightButtonPressedListener;
    protected OnButtonPressListener onNormalButtonPressedListener;
    protected OnButtonPressListener onDeleteFreshUIPressedListener;
    protected OnButtonPressListener onAddFreshUIPressedListener;
    protected OnButtonPressListener onGoTOHtml5PressedListener;
    protected boolean dividerVisible = false;
    protected boolean fullWidthDivider = false;

    public ExtendedCard(final Context context) {
        super(context);
    }

    public String getLeftButtonText() {
        return leftButtonText;
    }

    public void setLeftButtonText(int leftButtonTextId) {
        setLeftButtonText(getString(leftButtonTextId));
    }

    public void setLeftButtonText(String leftButtonText) {
        this.leftButtonText = leftButtonText;
        BusProvider.dataSetChanged();
    }

    public String getRightButtonText() {
        return rightButtonText;
    }

    public void setRightButtonText(int rightButtonTextId) {
        setRightButtonText(getString(rightButtonTextId));
    }

    public void setRightButtonText(String rightButtonText) {
        this.rightButtonText = rightButtonText;
        BusProvider.dataSetChanged();
    }

    public int getRightButtonTextColor() {
        return mRightButtonTextColor;
    }

    public void setRightButtonTextColor(int color) {
        this.mRightButtonTextColor = color;
        BusProvider.dataSetChanged();
    }

    public void setRightButtonTextColorRes(int colorId) {
        setRightButtonTextColor(getResources().getColor(colorId));
    }

    public boolean isDividerVisible() {
        return dividerVisible;
    }

    public boolean isFullWidthDivider() {
        return fullWidthDivider;
    }

    public void setFullWidthDivider(boolean fullWidthDivider) {
        this.fullWidthDivider = fullWidthDivider;
        BusProvider.dataSetChanged();
    }

    public void setDividerVisible(boolean visible) {
        this.dividerVisible = visible;
        BusProvider.dataSetChanged();
    }

    public OnButtonPressListener getOnLeftButtonPressedListener() {
        return onLeftButtonPressedListener;
    }

    public void setOnLeftButtonPressedListener(OnButtonPressListener onLeftButtonPressedListener) {
        this.onLeftButtonPressedListener = onLeftButtonPressedListener;
    }

    public OnButtonPressListener getOnRightButtonPressedListener() {
        return onRightButtonPressedListener;
    }

    public void setOnRightButtonPressedListener(OnButtonPressListener onRightButtonPressedListener) {
        this.onRightButtonPressedListener = onRightButtonPressedListener;
    }

	public int getmRightButtonTextColor() {
		return mRightButtonTextColor;
	}

	public void setmRightButtonTextColor(int mRightButtonTextColor) {
		this.mRightButtonTextColor = mRightButtonTextColor;
	}

	public OnButtonPressListener getOnNormalButtonPressedListener() {
		return onNormalButtonPressedListener;
	}

	public void setOnNormalButtonPressedListener(
			OnButtonPressListener onNormalButtonPressedListener) {
		this.onNormalButtonPressedListener = onNormalButtonPressedListener;
	}

	public OnButtonPressListener getOnDeleteFreshUIPressedListener() {
		return onDeleteFreshUIPressedListener;
	}

	public void setOnDeleteFreshUIPressedListener(
			OnButtonPressListener onDeleteFreshUIPressedListener) {
		this.onDeleteFreshUIPressedListener = onDeleteFreshUIPressedListener;
	}

	public OnButtonPressListener getOnAddFreshUIPressedListener() {
		return onAddFreshUIPressedListener;
	}

	public void setOnAddFreshUIPressedListener(
			OnButtonPressListener onAddFreshUIPressedListener) {
		this.onAddFreshUIPressedListener = onAddFreshUIPressedListener;
	}

	public OnButtonPressListener getOnGoTOHtml5PressedListener() {
		return onGoTOHtml5PressedListener;
	}

	public void setOnGoTOHtml5PressedListener(
			OnButtonPressListener onGoTOHtml5PressedListener) {
		this.onGoTOHtml5PressedListener = onGoTOHtml5PressedListener;
	}
    
	
	
}
