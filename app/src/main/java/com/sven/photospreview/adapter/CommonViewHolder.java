package com.sven.photospreview.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class CommonViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> views;

    public CommonViewHolder(View itemView) {
        super(itemView);
        views = new SparseArray<>();

    }

    public <T extends View> T getView(int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            views.put(viewId, view);
        }
        return (T) view;
    }

    public CommonViewHolder setText(int viewId, String text) {
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }

    public CommonViewHolder setTextColor(int viewId, int colorResId) {
        TextView view = getView(viewId);
        view.setTextColor(colorResId);
        return this;
    }




    public CommonViewHolder setVisible(int viewId, boolean visible) {
        View view = getView(viewId);
        view.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        return this;
    }

    public CommonViewHolder setVisibleOrGone(int viewId, boolean visible) {
        View view = getView(viewId);
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    public CommonViewHolder setbg(int viewId, int resId) {
        View view = getView(viewId);
        view.setBackgroundResource(resId);
        return this;

    }

    public CommonViewHolder setClickListener(int viewId, View.OnClickListener listener) {
        View view = getView(viewId);
        view.setOnClickListener(listener);
        return this;

    }

    public CommonViewHolder setImgRes(int viewId, int res) {
        ImageView view = getView(viewId);
        view.setImageResource(res);
        return this;

    }




}
