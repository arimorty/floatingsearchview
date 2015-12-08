package com.arlib.floatingsearchview.util.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by ari on 12/7/2015.
 */
public class IconImageView extends ImageView {

    private boolean mIsLocked;

    public IconImageView(Context context) {
        super(context);
    }

    public IconImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IconImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {

        if(!mIsLocked)
            super.setPadding(left, top, right, bottom);
    }

    @Override
    public void setScaleX(float scaleX) {

        if(!mIsLocked)
            super.setScaleX(scaleX);
    }

    @Override
    public void setScaleY(float scaleY) {

        if(!mIsLocked)
            super.setScaleY(scaleY);
    }

    @Override
    public ViewGroup.LayoutParams getLayoutParams() {

        if(!mIsLocked)
            return super.getLayoutParams();
        else return null;
    }

    public void lock(){
        this.mIsLocked = true;
    }

    public void unlock(){
        this.mIsLocked = false;
    }
}
