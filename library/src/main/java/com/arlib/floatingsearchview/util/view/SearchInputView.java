package com.arlib.floatingsearchview.util.view;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Field;

public class SearchInputView extends EditText {

    private OnKeyboardSearchKeyClickListener mSearchKeyListener;

    private OnKeyboardDismissedListener mOnKeyboardDismissedListener;

    private OnKeyListener mOnKeyListener = new OnKeyListener() {
        public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {

            if (keyCode == KeyEvent.KEYCODE_ENTER && mSearchKeyListener != null) {
                mSearchKeyListener.onSearchKeyClicked();
                return true;
            }
            return false;
        }
    };

    public SearchInputView(Context context) {
        super(context);
        init();
    }

    public SearchInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SearchInputView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setOnKeyListener(mOnKeyListener);
    }

    public void setCursorColor(int color) {
        try {
            // Get the cursor resource id
            Field field = TextView.class.getDeclaredField("mCursorDrawableRes");
            field.setAccessible(true);
            int drawableResId = field.getInt(this);

            // Get the editor
            field = TextView.class.getDeclaredField("mEditor");
            field.setAccessible(true);
            Object editor = field.get(this);

            // Get the drawable and set a color filter
            Drawable drawable = ContextCompat.getDrawable(this.getContext(), drawableResId);
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            Drawable[] drawables = {drawable, drawable};

            // Set the drawables
            field = editor.getClass().getDeclaredField("mCursorDrawable");
            field.setAccessible(true);
            field.set(editor, drawables);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent ev) {
        if (ev.getKeyCode() == KeyEvent.KEYCODE_BACK && mOnKeyboardDismissedListener != null) {
            mOnKeyboardDismissedListener.onKeyboardDismissed();
        }
        return super.onKeyPreIme(keyCode, ev);
    }

    public void setOnKeyboardDismissedListener(OnKeyboardDismissedListener onKeyboardDismissedListener) {
        mOnKeyboardDismissedListener = onKeyboardDismissedListener;
    }

    public void setOnSearchKeyListener(OnKeyboardSearchKeyClickListener searchKeyListener) {
        mSearchKeyListener = searchKeyListener;
    }

    public interface OnKeyboardDismissedListener {
        void onKeyboardDismissed();
    }

    public interface OnKeyboardSearchKeyClickListener {
        void onSearchKeyClicked();
    }
}
