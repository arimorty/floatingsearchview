package com.arlib.floatingsearchview.util.view;

import android.app.ActionBar;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by ari on 12/7/2015.
 */
public class BodyTextView extends TextView {

    private boolean mIsLocked;

    public BodyTextView(Context context) {
        super(context);
    }

    public BodyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BodyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setHeight(int pixels) {

        if(!mIsLocked)
           super.setHeight(pixels);
    }

    @Override
    public void setWidth(int pixels) {

        if(!mIsLocked)
           super.setWidth(pixels);
    }

    @Override
    public ViewGroup.LayoutParams getLayoutParams() {

        if(!mIsLocked)
           return super.getLayoutParams();
        else return new ViewGroup.LayoutParams(0,0);
    }

    public void lock(){
        this.mIsLocked = true;
    }

    public void unlock(){
        this.mIsLocked = false;
    }
}
