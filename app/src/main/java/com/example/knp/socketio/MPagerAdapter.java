package com.example.knp.socketio;

import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by knp on 2/6/17.
 */

public class MPagerAdapter extends android.support.v4.view.PagerAdapter {

    List<View> pages = null;
    public MPagerAdapter(List<View> pages){
        this.pages = pages;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View v = pages.get(position);
        container.addView(v,0);
        return v;
    }

    @Override
    public int getCount() {
        return pages.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}
