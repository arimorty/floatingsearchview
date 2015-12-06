package com.arlib.floatingsearchview.util.actionmenu;

import android.content.Context;
import android.support.v7.view.menu.MenuBuilder;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by ari on 12/5/2015.
 */
public class MenuView extends LinearLayout {

    private MenuBuilder mMenuBuilder;

    public MenuView(Context context) {
        super(context);
    }

    public MenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setMenuBuilder(MenuBuilder menuBuilder){
        this.mMenuBuilder = menuBuilder;
    }
}
