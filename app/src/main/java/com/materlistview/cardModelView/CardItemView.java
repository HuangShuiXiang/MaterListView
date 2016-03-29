package com.materlistview.cardModelView;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.materlistview.cardModel.Card;


public abstract class CardItemView<T extends Card> extends LinearLayout {

    private T card;
    private boolean isNeedScale=true;//动态不局不需要缩放，调用方法设置false
    private boolean isScaled=false;//判断是否缩放过
   

	public CardItemView(Context context) {
        super(context);
    }

    public CardItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public CardItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void build(T card){
        this.card = card;
    }

    public Object getTag(){
        return card.getTag();
    }

	public T getCard() {
		return card;
	}

	public void setCard(T card) {
		this.card = card;
	}

	public boolean isNeedScale() {
		return isNeedScale;
	}

	public void setNeedScale(boolean isNeedScale) {
		this.isNeedScale = isNeedScale;
	}

	 public boolean isScaled() {
			return isScaled;
	}

	public void setScaled(boolean isScaled) {
			this.isScaled = isScaled;
	}
    
}