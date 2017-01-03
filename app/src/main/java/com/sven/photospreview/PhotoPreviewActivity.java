package com.sven.photospreview;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/*
*大图浏览的页面
*
* */
public class PhotoPreviewActivity extends AppCompatActivity {

    @Bind(R.id.viewpager)
    ViewPager viewpager;
    @Bind(R.id.indicate_textview)
    TextView indicateTextview;

    private mPageAdapter mPageAdapter;
    private ArrayList<String> mdatas = new ArrayList<>();
    private int startIndex = 0;
    private int endIndex = 0;
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

    private void initView() {
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

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            SimpleDraweeView simpleView = (SimpleDraweeView) LayoutInflater.from(PhotoPreviewActivity.this).inflate(R.layout.view_simpleview, container, false);
            Uri imageUri = Uri.parse(mdatas.get(position));
            simpleView.setImageURI(imageUri);//这里开始下载
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setUri(imageUri)
                    .setTapToRetryEnabled(true)
                    .setOldController(simpleView.getController())
                    .build();
            simpleView.setController(controller);
            container.addView(simpleView);
            return simpleView;
        }
    }
}
