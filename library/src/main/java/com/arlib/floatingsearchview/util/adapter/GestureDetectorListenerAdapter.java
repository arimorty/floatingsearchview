package com.arlib.floatingsearchview.util.adapter;

import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Copyright (c) 2015 Ari C
 * <p/>
 * GestureDetectorListenerAdapter ---
 * <p/>
 * Author Ari
 * Created on 11/17/2015.
 */
public abstract class GestureDetectorListenerAdapter implements
        GestureDetector.OnGestureListener{

    private static final String TAG = "GestureDetectorListenerAdapter";

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
