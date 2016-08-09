package com.arlib.floatingsearchview.util;

import android.view.View;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.assertj.android.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
public class UtilTest {

    @Test
    public void shouldSetBottomPaddingWithoutTouchingOtherPadding() {
        // given
        View view = new View(RuntimeEnvironment.application);
        view.setPadding(1, 2, 3, 4);

        // when
        Util.setPaddingBottom(view, 5);

        // then
        assertThat(view)
                .hasPaddingLeft(1)
                .hasPaddingTop(2)
                .hasPaddingRight(3)
                .hasPaddingBottom(5);
    }
}