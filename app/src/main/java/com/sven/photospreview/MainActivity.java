package com.sven.photospreview;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sven.photospreview.adapter.CommonAdapter;
import com.sven.photospreview.adapter.CommonViewHolder;
import com.sven.photospreview.util.DensityUtil;
import com.sven.photospreview.widget.GridSpacingItemDecoration;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {


    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.activity_main)
    RelativeLayout activityMain;
    private CommonAdapter<String> mAdapter;
    private ArrayList<String> mDatas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initData() {
        for (int i = 0; i < 1000; i++) {
            mDatas.add("http://reso2.yiihuu.com/987409-z.jpg");
        }
        mAdapter.notifyDataSetChanged();
    }

    private void initView() {
        GridLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 2, GridLayoutManager.VERTICAL, false);
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.addItemDecoration(new GridSpacingItemDecoration(2, DensityUtil.dip2px(10), false, 0));
        mAdapter = new CommonAdapter<String>(MainActivity.this, mDatas, R.layout.view_simpleview) {
            @Override
            public void init(CommonViewHolder holder, String bean, int position) {
                SimpleDraweeView simpleView = holder.getView(R.id.simpleview);
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) simpleView.getLayoutParams();
                layoutParams.height = simpleView.getWidth();
                Uri imageUri = Uri.parse(bean);
                simpleView.setImageURI(imageUri);//这里开始下载
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setUri(imageUri)
                        .setTapToRetryEnabled(true)
                        .setOldController(simpleView.getController())
                        .build();
                simpleView.setController(controller);
            }
        };
        recyclerview.setAdapter(mAdapter);
        mAdapter.setOnItemClickLinster(new CommonAdapter.OnItemClickLinster() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this, PhotoPreviewActivity.class);
                intent.putStringArrayListExtra(PhotoPreviewActivity.DATAS_PHOTO_URLS, mDatas);
                intent.putExtra(PhotoPreviewActivity.DATAS_PHOTO_INDEX, position);
                startActivity(intent);
            }
        });
    }
}
