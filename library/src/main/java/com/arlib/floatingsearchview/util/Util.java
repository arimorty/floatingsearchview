package com.arlib.floatingsearchview.util;

/*
 * Copyright (C) 2015 Arlib
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class Util {

    private static final String TAG = "Util";

    public static void showSoftKeyboard(final Context context, final EditText editText){

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 100);
    }

    public static void closeSoftKeyboard(Activity activity){

        View currentFocusView = activity.getCurrentFocus();
        if (currentFocusView != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(currentFocusView.getWindowToken(), 0);
        }
    }

    public static int dpToPx(Context context, int dp){
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }


    public static int pxToDp(Context context,int px){
        return (int) (px / context.getResources().getDisplayMetrics().density);
    }

    public static int getScreenWidth(Activity activity){

        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);

        return outMetrics.widthPixels;
    }

    public static int getScreenHeight(Activity activity){

        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);

        return outMetrics.heightPixels;
    }
}
