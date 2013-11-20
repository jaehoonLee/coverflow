package com.example.PagerCoverFlow;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.ImageView;

/**
 * Created with IntelliJ IDEA.
 * User: LeeJaeHoon
 * Date: 13. 11. 21
 * Time: 오전 2:06
 * To change this template use File | Settings | File Templates.
 */
public class CoverFlow extends ViewPager {

    int mCoveflowCenter;
    private Camera mCamera = new Camera();
    private int mMaxRotationAngle = 60;
    private int mMaxZoom = -120;

    public CoverFlow(Context context) {
        super(context);
//        this.setStaticTransformationsEnabled(true);
    }

    public CoverFlow(Context context, AttributeSet attrs) {
        super(context, attrs);    //To change body of overridden methods use File | Settings | File Templates.
//        this.setStaticTransformationsEnabled(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mCoveflowCenter = getCenterOfCoverflow();
        super.onSizeChanged(w, h, oldw, oldh);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    protected boolean getChildStaticTransformation(View child, Transformation t) {

        Log.i("Test", "Test");
        final int childCenter = getCenterOfView(child);
        final int childWidth = child.getWidth() ;
        int rotationAngle = 0;

        if (childCenter == mCoveflowCenter) {
            transformImageBitmap((ImageView) child, t, 0);
        } else {
            rotationAngle = (int) (((float) (mCoveflowCenter - childCenter)/ childWidth) *  mMaxRotationAngle);
            if (Math.abs(rotationAngle) > mMaxRotationAngle) {
                rotationAngle = (rotationAngle < 0) ? -mMaxRotationAngle : mMaxRotationAngle;
            }
            transformImageBitmap((ImageView) child, t, rotationAngle);
        }

        return true;
    }

    private void transformImageBitmap(ImageView child, Transformation t, int rotationAngle) {
        mCamera.save();
        final Matrix imageMatrix = t.getMatrix();;
        final int imageHeight = child.getLayoutParams().height;;
        final int imageWidth = child.getLayoutParams().width;
        final int rotation = Math.abs(rotationAngle);

        //		mCamera.translate(0.0f, 0.0f, 100.0f);

        //As the angle of the view gets less, zoom in
        //		if ( rotation < mMaxRotationAngle )
        {
            float zoomAmount = (float) (mMaxZoom +  (rotation * 1.5));
            //			Log.i("Select", "rotation:" + rotation + " zoomAmount:" + zoomAmount);
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
    }


    private int getCenterOfCoverflow() {
        return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
    }

    private static int getCenterOfView(View view) {
        return view.getLeft() + view.getWidth() / 2;
    }
}
