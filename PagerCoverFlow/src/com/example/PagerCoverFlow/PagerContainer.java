/*
 * Copyright (c) 2012 Wireless Designs, LLC
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.example.PagerCoverFlow;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.graphics.Point;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * PagerContainer: A layout that displays a ViewPager with its children that are outside
 * the typical pager bounds.
 */
public class PagerContainer extends FrameLayout implements ViewPager.OnPageChangeListener {

    private CoverFlow mPager;
    boolean mNeedsRedraw = false;
    int mCoveflowCenter;
    private Camera mCamera = new Camera();
    private int mMaxRotationAngle = 60;
    private int mMaxZoom = -120;

    int windowWidth;
    int width = windowWidth - 100;

    public PagerContainer(Context context) {
        super(context);
        init();
    }

    public PagerContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PagerContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        //Disable clipping of children so non-selected pages are visible
        setClipChildren(false);
        windowWidth  = this.getResources().getDisplayMetrics().widthPixels;

        //Child clipping doesn't work with hardware acceleration in Android 3.x/4.x
        //You need to set this value here if using hardware acceleration in an
        // application targeted at these releases.
//        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onFinishInflate() {
        try {
            mPager = (CoverFlow) getChildAt(0);
            mPager.setOnPageChangeListener(this);
        } catch (Exception e) {
            throw new IllegalStateException("The root child of PagerContainer must be a ViewPager");
        }
    }

    public CoverFlow getViewPager() {
        return mPager;
    }

    private Point mCenter = new Point();
    private Point mInitialTouch = new Point();

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mCenter.x = w / 2;
        mCenter.y = h / 2;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //We capture any touches not already handled by the ViewPager
        // to implement scrolling from a touch outside the pager bounds.
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mInitialTouch.x = (int)ev.getX();
                mInitialTouch.y = (int)ev.getY();
            default:
                ev.offsetLocation(mCenter.x - mInitialTouch.x, mCenter.y - mInitialTouch.y);
                break;
        }

        return mPager.dispatchTouchEvent(ev);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //Force the container to redraw on scrolling.
        //Without this the outer pages render initially and then stay static

        int rotationAngle = (int) (positionOffset *  mMaxRotationAngle);
        if (Math.abs(rotationAngle) > mMaxRotationAngle) {
            rotationAngle = (rotationAngle < 0) ? -mMaxRotationAngle : mMaxRotationAngle;
        }
        FrameLayout frameLayout = (FrameLayout)mPager.getChildAt(position);
        ImageView imageview = (ImageView)frameLayout.getChildAt(0);
        Log.i("Test", "positino2:" + rotationAngle);
        
        
        transformImageBitmap((ImageView)imageview , imageview.getImageMatrix(), rotationAngle);

//        Matrix m = mPager.getMatrix();
//        RectF drawableRect = new RectF(0, 0, width - rotationAngle, width - rotationAngle);
//        RectF viewRect = new RectF(0, 0, imageview.getWidth(), imageview.getHeight());
//        m.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.CENTER);
//        mPager.setMa


        if (mNeedsRedraw) invalidate();
    }

    @Override
    public void onPageSelected(int position) {
        Log.i("Test", "positino:" + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        mNeedsRedraw = (state != ViewPager.SCROLL_STATE_IDLE);
    }


    private void transformImageBitmap(ImageView child, Matrix imageMatrix, int rotationAngle) {
        mCamera.save();
//        final Matrix imageMatrix = t.getMatrix();;
        final int imageHeight = child.getLayoutParams().height;
        final int imageWidth = child.getLayoutParams().width;
        final int rotation = Math.abs(rotationAngle);

        //		mCamera.translate(0.0f, 0.0f, 100.0f);
        Log.i("Select", "imageWidth:" + imageWidth + " " + "imageHeight:" + imageHeight);
        //As the angle of the view gets less, zoom in
        //		if ( rotation < mMaxRotationAngle )
        {
            float zoomAmount = (float) (mMaxZoom +  (rotation * 1.5));
            Log.i("Select", "rotation:" + rotation + " zoomAmount:" + zoomAmount);
            mCamera.translate(0.0f, 0.0f, zoomAmount);
        }
        
        

        //Alpha
        int alphaVal = 255 - rotation * 3;
        //		child.setAlpha(alphaVal); //[0,255]
        child.getDrawable().setAlpha(alphaVal);
        //		mCamera.rotateY(rotationAngle);
        mCamera.getMatrix(imageMatrix);
        imageMatrix.preTranslate(-(imageWidth/2), -(imageHeight/2));
        imageMatrix.postTranslate((imageWidth/2), (imageHeight/2));
        mCamera.restore();
        
        float[] values = new float[9];
        imageMatrix.getValues(values);
		float globalX = values[Matrix.MTRANS_X];
		float globalY = values[Matrix.MTRANS_Y];
//		float width = values[Matrix.MSCALE_X]*CommonValue.menuWidth ;
//		float height = values[Matrix.MSCALE_Y]*CommonValue.menuWidth;
		values[Matrix.MSCALE_X] = 2;
		values[Matrix.MSCALE_Y] = 2;
		imageMatrix.setValues(values);
    }

}