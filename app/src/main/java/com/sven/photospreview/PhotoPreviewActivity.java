package com.sven.photospreview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

import static com.sven.photospreview.MainActivity.EXTRA_CURRENT_ALBUM_POSITION;
import static com.sven.photospreview.MainActivity.EXTRA_STARTING_ALBUM_POSITION;

/*
*大图浏览的页面
*
* */
public class PhotoPreviewActivity extends AppCompatActivity {

    @Bind(R.id.viewpager)
    ViewPager viewpager;
    @Bind(R.id.indicate_textview)
    TextView indicateTextview;
    @Bind(R.id.rootview)
    FrameLayout rootview;

    private mPageAdapter mPageAdapter;
    private ArrayList<String> mdatas = new ArrayList<>();
    private int startIndex = 0;
    private int endIndex = 0;
    private boolean mIsReturning;
    private PhotoView mCurrentShareView;
    public static final String DATAS_PHOTO_URLS = "DATAS_PHOTO_URLS";
    public static final String DATAS_PHOTO_INDEX = "DATAS_PHOTO_INDEX";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_preview);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initData() {
        startIndex = getIntent().getIntExtra(DATAS_PHOTO_INDEX, 0);
        mdatas.addAll(getIntent().getStringArrayListExtra(DATAS_PHOTO_URLS));
    }

    @SuppressLint("InlinedApi")
    private void initView() {
        setEnterSharedElementCallback(mCallback);
        rootview.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        indicateTextview.setText(startIndex + "/" + mdatas.size());
        mPageAdapter = new mPageAdapter();
        viewpager.setAdapter(mPageAdapter);
        viewpager.setCurrentItem(startIndex, true);
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                endIndex = position;
                indicateTextview.setText(position + "/" + mdatas.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    class mPageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mdatas == null ? 0 : mdatas.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            container.removeView((View) object);
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = (PhotoView) LayoutInflater.from(PhotoPreviewActivity.this).inflate(R.layout.item_viewpager, container, false);
            Glide.with(PhotoPreviewActivity.this)
                    .load(mdatas.get(position))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .crossFade()
                    .fitCenter()
                    .into(photoView);
            photoView.setTransitionName(mdatas.get(position));
            photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    supportFinishAfterTransition();
                }

                @Override
                public void onOutsidePhotoTap() {

                }
            });
            container.addView(photoView);
            return photoView;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            mCurrentShareView = (PhotoView) object;
        }
    }

    @Override
    public void supportFinishAfterTransition() {
        mIsReturning = true;
        Intent data = new Intent();
        data.putExtra(EXTRA_STARTING_ALBUM_POSITION, startIndex);
        data.putExtra(EXTRA_CURRENT_ALBUM_POSITION, endIndex);
        setResult(RESULT_OK, data);
        super.supportFinishAfterTransition();
    }

    /*这个回调进入activity和退出activity都会执行所以要判断下是不是返回前一个Activity*/
    private final SharedElementCallback mCallback = new SharedElementCallback() {
        @SuppressLint("NewApi")
        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            if (mIsReturning) {
                PhotoView sharedElement = mCurrentShareView;
                if (sharedElement == null) {
                    // If shared element is null, then it has been scrolled off screen and
                    // no longer visible. In this case we cancel the shared element transition by
                    // removing the shared element from the shared elements map.
                    names.clear();
                    sharedElements.clear();
                } else if (startIndex != endIndex) {
                    // If the user has swiped to a different ViewPager page, then we need to
                    // remove the old shared element and replace it with the new shared element
                    // that should be transitioned instead.
                    names.clear();
                    names.add(sharedElement.getTransitionName());
                    sharedElements.clear();
                    sharedElements.put(sharedElement.getTransitionName(), sharedElement);
                }
            }
        }
    };
}
