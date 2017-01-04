package com.sven.photospreview;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sven.photospreview.adapter.CommonAdapter;
import com.sven.photospreview.adapter.CommonViewHolder;
import com.sven.photospreview.util.DensityUtil;
import com.sven.photospreview.widget.GridSpacingItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.activity_main)
    RelativeLayout activityMain;
    private CommonAdapter<String> mAdapter;
    private ArrayList<String> mDatas = new ArrayList<>();
    private Bundle mTmpReenterState;
    static final String EXTRA_STARTING_ALBUM_POSITION = "extra_starting_item_position";
    static final String EXTRA_CURRENT_ALBUM_POSITION = "extra_current_item_position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initData() {
        mDatas.add("http://reso2.yiihuu.com/987409-z.jpg");
        mDatas.add("http://pic23.nipic.com/20120903/7341593_144515270000_2.jpg");
        mDatas.add("http://img2.niutuku.com/desk/1207/1005/ntk122712.jpg");
        mDatas.add("http://img2.niutuku.com/desk/1208/1936/ntk-1936-38242.jpg");
        mDatas.add("http://img2.niutuku.com/desk/1207/1037/ntk125970.jpg");
    }

    private void initView() {
        setExitSharedElementCallback(mCallback);
        GridLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 2, GridLayoutManager.VERTICAL, false);
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.addItemDecoration(new GridSpacingItemDecoration(2, DensityUtil.dip2px(10), false, 0));
        mAdapter = new CommonAdapter<String>(MainActivity.this, mDatas, R.layout.item_recyclerview) {
            @SuppressLint("NewApi")
            @Override
            public void init(CommonViewHolder holder, String bean, int position) {
                int imageWH = (DensityUtil.getScreenWidth(MainActivity.this) - DensityUtil.dip2px(30)) / 2;
                ImageView imageView = holder.getView(R.id.imageview);
                imageView.setTransitionName(bean);
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) imageView.getLayoutParams();
                layoutParams.height = imageWH;
                layoutParams.width = imageWH;
                Glide.with(MainActivity.this)
                        .load(bean)
                        .placeholder(R.drawable.icon_placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .skipMemoryCache(false)
                        .crossFade()
                        .override(imageWH, imageWH)
                        .centerCrop()
                        .into(imageView);
            }
        };
        recyclerview.setAdapter(mAdapter);
        mAdapter.setOnItemClickLinster(new CommonAdapter.OnItemClickLinster() {
            @Override
            public void onItemClick(View view, int position) {
                PreviewPhotos(view, position);
            }
        });
    }

    @SuppressLint("NewApi")
    @Override
    public void onActivityReenter(int requestCode, Intent data) {
        super.onActivityReenter(requestCode, data);
        mTmpReenterState = new Bundle(data.getExtras());
        int startingPosition = mTmpReenterState.getInt(EXTRA_STARTING_ALBUM_POSITION);
        int currentPosition = mTmpReenterState.getInt(EXTRA_CURRENT_ALBUM_POSITION);
        if (startingPosition != currentPosition) {
            recyclerview.scrollToPosition(currentPosition);
        }
        postponeEnterTransition();
        recyclerview.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                recyclerview.getViewTreeObserver().removeOnPreDrawListener(this);
                // TODO: figure out why it is necessary to request layout here in order to get a smooth transition.
                recyclerview.requestLayout();
                startPostponedEnterTransition();
                return true;
            }
        });
    }

    private void PreviewPhotos(View view, int position) {
        Intent intent = new Intent(MainActivity.this, PhotoPreviewActivity.class);
        intent.putStringArrayListExtra(PhotoPreviewActivity.DATAS_PHOTO_URLS, mDatas);
        intent.putExtra(PhotoPreviewActivity.DATAS_PHOTO_INDEX, position);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, view, mDatas.get(position));
            startActivity(intent, optionsCompat.toBundle());
        } else {
            startActivity(intent);
        }
    }


    private final SharedElementCallback mCallback = new SharedElementCallback() {
        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            if (mTmpReenterState != null) {
                int startingPosition = mTmpReenterState.getInt(EXTRA_STARTING_ALBUM_POSITION);
                int currentPosition = mTmpReenterState.getInt(EXTRA_CURRENT_ALBUM_POSITION);
                if (startingPosition != currentPosition) {
                    String newTransitionName = mDatas.get(currentPosition);
                    View newSharedElement = recyclerview.getChildAt(currentPosition);
                    if (newSharedElement != null) {
                        names.clear();
                        names.add(newTransitionName);
                        sharedElements.clear();
                        sharedElements.put(newTransitionName, newSharedElement);
                    }
                }
                mTmpReenterState = null;
            }
        }
    };
}
