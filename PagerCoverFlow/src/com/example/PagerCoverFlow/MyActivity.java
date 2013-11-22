package com.example.PagerCoverFlow;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MyActivity extends Activity {
    PagerContainer mContainer;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        int windowWidth = this.getResources().getDisplayMetrics().widthPixels;
        int width = windowWidth - 100;

        
        mContainer = (PagerContainer) findViewById(R.id.pager_container);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, width);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        mContainer.setLayoutParams(layoutParams);

        
        CoverFlow pager = mContainer.getViewPager();
        pager.setLayoutParams(new FrameLayout.LayoutParams(width, width, Gravity.CENTER_HORIZONTAL));
        PagerAdapter adapter = new MyPagerAdapter();
        pager.setAdapter(adapter);
        //Necessary or the pager will only have one extra page to show
        // make this at least however many pages you can see
        pager.setOffscreenPageLimit(adapter.getCount());
        //A little space between pages
        pager.setPageMargin(50);
		
        //If hardware acceleration is enabled, you should also remove
        // clipping on the pager for its children.
//        pager.setClipChildren(false);
    }

    //Nothing special about this adapter, just throwing up colored views for demo
    private class MyPagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
//            TextView view = new TextView(MyActivity.this);
//            view.setText("Item "+position);
//            view.setGravity(Gravity.CENTER);
//            view.setBackgroundColor(Color.argb(255, position * 50, position * 10, position * 50));
//
//            container.addView(view);
//            FrameLayout view;
        	FrameLayout layout = new FrameLayout(MyActivity.this);
            ImageView menuMain = new ImageView(MyActivity.this);
            menuMain.setScaleType(ImageView.ScaleType.FIT_XY);
            if(position < 3)
            	menuMain.setImageResource(R.drawable.menu);
            else
            	menuMain.setImageResource(R.drawable.menu_setting_default);
//            menuMain.setAdjustViewBounds(true);
            FrameLayout.LayoutParams menuMainParams = new FrameLayout.LayoutParams(200, 200);
//            menuMainParams.width = 300;
//            menuMainParams.height = 300;
            menuMain.setLayoutParams(menuMainParams);

            layout.addView(menuMain);
            container.addView(layout);
//                        container.addView(menuMain);
//            return menuMain;
              
              return layout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }
    }
}
