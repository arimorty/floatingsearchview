package com.arlib.floatingsearchview.util;

import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.googlecode.catchexception.throwable.ThrowingCallable;
import com.googlecode.catchexception.throwable.apis.BDDCatchThrowable;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.assertj.android.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
public class EditTextUtilTest {

    private EditText editText;

    @Before
    public void setUp() {
        editText = new EditText(RuntimeEnvironment.application);
    }

    @Test
    public void shouldThrowExceptionWhenInvalidImeActionSet() {
        // given
        final int invalidImeAction = EditorInfo.IME_FLAG_NO_FULLSCREEN;

        // when
        BDDCatchThrowable.when(new ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                EditTextUtil.setImeAction(editText, invalidImeAction);
            }
        });

        // then
        BDDCatchThrowable.thenCaughtThrowable().isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void shouldSetImeActionWithoutChangingOtherImeOptions() {
        // given
        editText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_ACTION_NONE);

        // when
        EditTextUtil.setImeAction(editText, EditorInfo.IME_ACTION_GO);

        // then
        assertThat(editText).hasImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_ACTION_GO);
    }

    @Test
    public void shouldAddImeFlagKeepingAllExistingFlags() {
        // given
        editText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

        // when
        EditTextUtil.addImeFlag(editText, EditorInfo.IME_FLAG_NAVIGATE_NEXT);

        // then
        assertThat(editText).hasImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_FLAG_NAVIGATE_NEXT);
    }
}