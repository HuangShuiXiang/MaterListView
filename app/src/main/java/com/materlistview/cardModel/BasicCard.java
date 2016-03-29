package com.materlistview.cardModel;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;

import com.materlistview.event.BusProvider;


public abstract class BasicCard extends Card {
    private final Context mContext;
    private int mBackgroundColor = Color.WHITE;

    public BasicCard(Context context) {
        mContext = context;
    }

    public void setBackgroundColor(int color) {
        mBackgroundColor = color;
        BusProvider.dataSetChanged();
    }

    public void setBackgroundColorRes(int colorId) {
        setBackgroundColor(getResources().getColor(colorId));
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    protected String getString(int id) {
        return mContext.getString(id);
    }

    protected Resources getResources() {
        return mContext.getResources();
    }
}
