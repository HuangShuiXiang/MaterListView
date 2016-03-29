package com.materlistview.materListView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.materlistview.cardModel.Card;
import com.materlistview.cardModelView.CardItemView;
import com.materlistview.event.BusProvider;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class MaterialListAdapter extends RecyclerView.Adapter<MaterialListAdapter.ViewHolder> implements IMaterialListAdapter {
    private final List<Card> mCardList = new ArrayList<>();
    public static boolean isNeedSacleItem = false;
    public Context mContext;
    public MaterialListView materialListView;

    private int mDuration = 300;
    private Interpolator mInterpolator = new LinearInterpolator();
    private int mLastPosition = 0;
    private boolean isFirstOnly = true;
    private int overScreenPosition = -1;//这个是第一次进来超过平屏幕的位置数

    public MaterialListAdapter(Context context, MaterialListView materialListView) {
        mContext = context;
        this.materialListView = materialListView;

    }

    public static class ViewHolder<T extends Card> extends RecyclerView.ViewHolder {
        private final CardItemView<T> view;

        public ViewHolder(View v) {
            super(v);
            view = (CardItemView<T>) v;
        }

        public void build(T card) {
            view.build(card);

			/*//判断是否调用set720，是否动态布局，是否已经设置过
            if (isNeedSacleItem && view.isNeedScale() && !view.isScaled()) {

			}*/
            view.setScaled(true);

        }

    }


    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.build(mCardList.get(position));
        holder.itemView.post(new Runnable() {

            @Override
            public void run() {
                if (overScreenPosition < 0 || position < 4) {
                    int[] locations = new int[2];
                    holder.itemView.getLocationInWindow(locations);
                    if (locations[1] + holder.itemView.getHeight() > AppConfig.getInstance(mContext).SCREEN_WIDTH) {
                        overScreenPosition = position;
                    }
                }
				/*if (position<4) {
					
				}*/
                else {
                    //没有双层嵌套的listview才执行
                    if (position > mLastPosition && !materialListView.isTwoMaterialListView() && materialListView.isNeedAdapterAnim()) {
                        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(holder.itemView, "scaleX", 0.2f, 1f);
                        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(holder.itemView, "scaleY", 0.2f, 1f);
                        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(holder.itemView, "alpha", 0.f, 1f);
                        ObjectAnimator objectAnimator[] = new ObjectAnimator[]{alphaAnimator, scaleXAnimator, scaleYAnimator};
                        for (Animator anim : objectAnimator) {
                            anim.setDuration(mDuration).start();
                            anim.setInterpolator(mInterpolator);
                        }
                        mLastPosition = position;
                    } else {
                        //ViewHelperAnim.clear(holder.itemView);
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mCardList.size();
    }

    @Override
    public int getItemViewType(final int position) {
        return mCardList.get(position).getLayout();
    }

    public void add(final Card card) {
        mCardList.add(card);
        notifyItemInserted(getItemCount() - 1);
		/*materialListView.post(new Runnable() {
			
			@Override
			public void run() {
				//mCardList.add(card);
				// TODO Auto-generated method stub
				//notifyItemInserted(getItemCount()-1);
			}
		});*/
		/*mCardList.add(card);
		notifyDataSetChanged();*/
    }

    public void addAll(Card... cards) {
        addAll(Arrays.asList(cards));
    }

    public void addAll(Collection<Card> cards) {
        for (Card card : cards) {
            add(card);
        }
    }

    public void remove(Card card, boolean withAnimation) {
        if (card.isDismissible()) {
            if (withAnimation) {
                BusProvider.dismiss(card);
            } else {
                mCardList.remove(card);
                notifyDataSetChanged();
            }
        } else {
            if (withAnimation) {
                BusProvider.dismiss(card);
            } else {
                mCardList.remove(card);
                notifyDataSetChanged();
            }
        }
    }

    public void clear() {
        mCardList.clear();
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return mCardList.isEmpty();
    }

    public Card getCard(int position) {
        return mCardList.get(position);
    }

    public int getPosition(Card card) {
        return mCardList.indexOf(card);
    }

    public List<Card> getAllList() {
        return mCardList;
    }

    public Card getItemById(String id) {
        for (int i = 0; i < mCardList.size(); i++) {
            if (id.equals(mCardList.get(i).getId())) {
                return mCardList.get(i);
            }
        }
        return null;
    }

}
