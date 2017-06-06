package com.arlib.floatingsearchview.util;

import android.support.annotation.NonNull;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import java.util.Locale;

public class EditTextUtil {

    private EditTextUtil() {
        // no instances
    }

    public static void setImeAction(@NonNull EditText editText, int imeAction) {
        if ((imeAction & ~EditorInfo.IME_MASK_ACTION) != 0) {
            throw new IllegalArgumentException(
                    String.format(Locale.US, "The value %x is not a valid ime action", imeAction)
            );
        }
        int currentImeOptions = editText.getImeOptions();
        int imeOptionsWithoutAction = currentImeOptions & ~EditorInfo.IME_MASK_ACTION;
        editText.setImeOptions(imeOptionsWithoutAction | imeAction);
    }

    public static void addImeFlag(@NonNull EditText editText, int imeOption) {
        editText.setImeOptions(editText.getImeOptions() | imeOption);
    }
}
