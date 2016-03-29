package com.materlistview.test;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.materlistview.R;
import com.materlistview.cardModelView.CardItemView;

public class TestItemCardView extends
        CardItemView<TestItemCard> {
    private Context mContext;

    public TestItemCardView(Context context) {
        super(context);
        mContext = context;
    }

    public TestItemCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public TestItemCardView(Context context, AttributeSet attrs,
                            int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    @Override
    public void build(final TestItemCard card) {
        super.build(card);
        //这里写findView 和添加监听
        TextView textView = (TextView)findViewById(R.id.tv_text);
        textView.setText(card.getResult().getName());
        textView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                card.getOnDeleteFreshUIPressedListener().onButtonPressedListener(null,card);
                return true;
            }
        });
    }

}
