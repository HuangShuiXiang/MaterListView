package com.materlistview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.materlistview.CrashHandler.ServerLogActivity;
import com.materlistview.cardModel.Card;
import com.materlistview.cardModel.OnButtonPressListener;
import com.materlistview.materListView.MaterialListView;
import com.materlistview.materListView.SuperSwipeRefresh;
import com.materlistview.test.TestItemCard;
import com.materlistview.test.TestModel;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class TestActivity extends Activity implements SuperSwipeRefresh.OnLoadMoreListener, SuperSwipeRefresh.OnRefreshReceyerListener {
    private SuperSwipeRefresh swipeRefresh;
    private MaterialListView materialListView;
    private Button btn_add;
    private String Tag = "TestActivity";
    List<TestModel> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        swipeRefresh = (SuperSwipeRefresh) findViewById(R.id.superSwipeRefresh);
        btn_add = (Button) findViewById(R.id.btn_add);
        materialListView = (MaterialListView) findViewById(R.id.material_ListView);
        //这个可以不加，用来下拉刷新的，上啦加载的
        swipeRefresh.setEnableLoadMore(true);
        swipeRefresh.setView(this, materialListView);
        swipeRefresh.setOnLoadMoreListener(this);
        swipeRefresh.setOnRefreshReceyerListener(this);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TestItemCard card = new TestItemCard(TestActivity.this);
                TestModel test = new TestModel("item" + list.size());
                card.setResult(test);
                card.setPosition(list.size());
                card.setOnDeleteFreshUIPressedListener(listener);
                list.add(test);
                materialListView.add(card);
            }
        });
        //填充卡片
        fillArrayDateToListView();
        /*Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("hahha1");
                subscriber.onNext("hahha2");
                subscriber.onNext("hahha3");
                subscriber.onCompleted();
            }

        }).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {

            }
        });*/

        Observable.create(new Observable.OnSubscribe<TestModel>() {
            @Override
            public void call(Subscriber<? super TestModel> subscriber) {
                result = getResultFromNet();
                //下载图片
//                subscriber.onNext(result);
            }
        }).map(new Func1<TestModel, Bitmap>() {
            @Override
            public Bitmap call(TestModel testModel) {
                Bitmap bitmap = getBitmapForUrl(result.getName());
                return bitmap;
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap bitmap) {
//                        imageView.setImageBitmap(bitmap);
                        String s = "";
                    }
                });


        //如果是一个集合
        Observable.create(new Observable.OnSubscribe<List<TestModel>>() {
            @Override
            public void call(Subscriber<? super List<TestModel>> subscriber) {
                //网络请求
                list = getResultListFromNet();
                subscriber.onNext(list);
            }
        }).subscribeOn(Schedulers.io())
          .subscribeOn(AndroidSchedulers.mainThread())
          .subscribe(new Subscriber<List<TestModel>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<TestModel> testModels) {

            }
        });
        /*
                .flatMap(new Func1<TestModel, Observable<TestModel>>() {
                    @Override
                    public Observable<TestModel> call(TestModel testModel) {
                        //遍历集合，转换
                        return Observable.from(list);
                    }
                }).map(new Func1<TestModel, String>() {
            @Override
            public String call(TestModel testModel) {
                //bitmap转换
                return testModel.getName();
            }
        }).filter(new Func1<String, Boolean>() {
            @Override
            public Boolean call(String s) {
                return s.contains("5");
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String bitmap) {
                        //显示UI
//                imageView.setImageBitmap(bitmap);
                        Log.i(Tag, bitmap);
                    }
                });*/
    }

    private List<TestModel> getResultListFromNet() {
        List<TestModel> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(new TestModel("item" + i));
        }
        return list;
    }

    private Bitmap getBitmapForUrl(String name) {
        return null;
    }

    private TestModel getResultFromNet() {
        return new TestModel("网络请求");
    }

    private TestModel result;
//    常用的api（所有api...怎么可能一个一个说明白）

    /**
     * 填充数据
     */
    private void fillArrayDateToListView() {
        for (int i = 0; i < 10; i++) {
            list.add(new TestModel("item" + i));
        }
        for (int i = 0; i < list.size(); i++) {
            //新建卡片
            TestItemCard card = new TestItemCard(this);
            card.setPosition(i);
            //绑定数据
            card.setResult(list.get(i));
            //设定监听
            card.setOnDeleteFreshUIPressedListener(listener);
            //添加到recycleView
            materialListView.add(card);
        }
    }

    private OnButtonPressListener listener = new OnButtonPressListener() {
        @Override
        public void onButtonPressedListener(View view, Card card) {
            TestItemCard card_test = (TestItemCard) card;
            list.remove(card_test.getResult());
            materialListView.remove(card);
            Toast.makeText(TestActivity.this, "删除了" + card_test.getResult().getName(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "请求/响应日志");
        menu.add(1, 1, 1, "crash闪退日志");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent inient = new Intent(this,
                ServerLogActivity.class);
        inient.putExtra(ServerLogActivity.APPNAME, "text");
        switch (item.getItemId()) {
            case 0:
                startActivity(inient);
                break;
            case 1:
                inient.putExtra("crash", "crash");
                startActivity(inient);
                break;
        }

        return true;
    }

    @Override
    public void loadMore(int itemsCount, int maxLastVisiblePosition) {
        swipeRefresh.setLoadMoreFinish();
    }

    @Override
    public void Refresh(MaterialListView recyclerView) {
        swipeRefresh.onRefresh();
    }
}
