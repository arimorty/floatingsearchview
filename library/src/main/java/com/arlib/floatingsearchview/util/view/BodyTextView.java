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
    public void setTextSize(float size) {
        if(!mIsLocked)
           super.setTextSize(size);
    }

    @Override
    public void setTextSize(int unit, float size) {
        if(!mIsLocked)
          super.setTextSize(unit, size);
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {

        if(!mIsLocked)
           super.setPadding(left, top, right, bottom);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {

        if(!mIsLocked)
           super.setText(text, type);
    }

    @Override
    public void setTextScaleX(float size) {

        if(!mIsLocked)
           super.setTextScaleX(size);
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
        else return new ViewGroup.LayoutParams(0,0);
    }

    public void lock(){
        this.mIsLocked = true;
    }

    public void unlock(){
        this.mIsLocked = false;
    }
}
