package com.arlib.floatingsearchview.util;

import android.app.Activity;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewTreeObserver;

import static com.arlib.floatingsearchview.util.Util.removeGlobalLayoutObserver;
import static com.arlib.floatingsearchview.util.Util.setPaddingBottom;

/**
 * Based on the solution found here:
 * http://stackoverflow.com/questions/8398102/androidwindowsoftinputmode-adjustresize-doesnt-make-any-difference
 */
public class SoftKeyboardViewAdjuster {

    private final View decorView;
    private final View targetView;
    private final PaddingUpdatingKeyboardListener paddingUpdatingKeyboardListener;

    public SoftKeyboardViewAdjuster(@NonNull Activity activity, @NonNull View targetView) {
        this.decorView = activity.getWindow().getDecorView();
        this.targetView = targetView;
        paddingUpdatingKeyboardListener = new PaddingUpdatingKeyboardListener();
    }

    public void startAdjustingViewForKeyboard() {
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(paddingUpdatingKeyboardListener);
    }

    public void stopAdjustingViewForKeyboard() {
        removeGlobalLayoutObserver(decorView, paddingUpdatingKeyboardListener);
    }

    private class PaddingUpdatingKeyboardListener implements ViewTreeObserver.OnGlobalLayoutListener {

        private final Rect visibleDisplayFrame = new Rect();
        private final int initialTargetViewBottomPadding;

        private PaddingUpdatingKeyboardListener() {
            initialTargetViewBottomPadding = targetView.getPaddingBottom();
        }

        @Override
        public void onGlobalLayout() {
            decorView.getWindowVisibleDisplayFrame(visibleDisplayFrame);

            int screenHeight = Util.getScreenHeight(decorView.getContext());
            int notVisibleScreenHeight = screenHeight - visibleDisplayFrame.bottom;

            setPaddingBottom(targetView, initialTargetViewBottomPadding + notVisibleScreenHeight);
        }
    }
}