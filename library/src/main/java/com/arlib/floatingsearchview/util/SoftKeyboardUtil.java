package com.arlib.floatingsearchview.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class SoftKeyboardUtil {

    private static final int DELAY_FOR_SHOWING_KEYBOARD_MILLIS = 100;

    private SoftKeyboardUtil() {
        // no instances
    }

    public static void showSoftKeyboard(@NonNull final Context context, @NonNull final EditText editText) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        }, DELAY_FOR_SHOWING_KEYBOARD_MILLIS);
    }

    public static void closeSoftKeyboard(@NonNull Activity activity) {
        View currentFocusView = activity.getCurrentFocus();
        if (currentFocusView != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(currentFocusView.getWindowToken(), 0);
        }
    }

    public static int getSoftKeyboardHeight(@NonNull View view) {
        Rect windowDisplayFrame = new Rect();
        view.getWindowVisibleDisplayFrame(windowDisplayFrame);

        int screenHeight = view.getRootView().getHeight();
        return screenHeight - (windowDisplayFrame.bottom - windowDisplayFrame.top);
    }
}
