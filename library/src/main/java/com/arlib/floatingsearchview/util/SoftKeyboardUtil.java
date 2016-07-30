package com.arlib.floatingsearchview.util;

import android.app.Activity;
import android.content.Context;
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
                showSoftKeyboardImmediately(context, editText);
            }
        }, DELAY_FOR_SHOWING_KEYBOARD_MILLIS);
    }

    private static void showSoftKeyboardImmediately(@NonNull Context context, @NonNull EditText editText) {
        InputMethodManager inputMethodManager = getInputMethodManager(context);
        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    private static InputMethodManager getInputMethodManager(@NonNull Context context) {
        return (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    public static void closeSoftKeyboard(@NonNull Activity activity) {
        View currentFocusView = activity.getCurrentFocus();
        if (currentFocusView != null) {
            InputMethodManager imm = getInputMethodManager(activity);
            imm.hideSoftInputFromWindow(currentFocusView.getWindowToken(), 0);
        }
    }
}
