package com.arlib.floatingsearchview.util.actionmenu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.view.SupportMenuInflater;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuItemImpl;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.arlib.floatingsearchview.R;
import com.arlib.floatingsearchview.util.MenuPopupHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.zip.Inflater;

/**
 * Created by ari on 12/5/2015.
 */
public class MenuView extends LinearLayout {

    private final float ACTION_DIMENSION;

    private MenuBuilder mMenuBuilder;
    private SupportMenuInflater mMenuInflater;
    private MenuPopupHelper mMenuPopupHelper;

    private int mWidth = -1;

    public MenuView(Context context) {
        super(context);
        ACTION_DIMENSION = context.getResources().getDimension(R.dimen.square_button_size);
    }

    public MenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ACTION_DIMENSION = context.getResources().getDimension(R.dimen.square_button_size);
    }

    public MenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ACTION_DIMENSION = context.getResources().getDimension(R.dimen.square_button_size);
    }

    private void init(){
        mMenuBuilder = new MenuBuilder(getContext());
        mMenuPopupHelper = new MenuPopupHelper(getContext(), mMenuBuilder, this);
    }

    public void setMenuBuilder(MenuBuilder menuBuilder){
        this.mMenuBuilder = menuBuilder;
    }

    private MenuInflater getMenuInflater() {
        if (mMenuInflater == null) {
            mMenuInflater = new SupportMenuInflater(getContext());
        }
        return mMenuInflater;
    }

    public void invalidate(int availWidth){

        ArrayList<MenuItemImpl> menuItems =  mMenuBuilder.getActionItems();
        menuItems.addAll(mMenuBuilder.getNonActionItems());

        Collections.sort(menuItems, new Comparator<MenuItemImpl>() {
            @Override
            public int compare(MenuItemImpl lhs, MenuItemImpl rhs) {
                return Integer.compare(lhs.getOrder(), rhs.getOrder());
            }
        });

        int availItemRoom = availWidth/(int)ACTION_DIMENSION;
        boolean addOverflowAtTheEnd = false;
        if(availItemRoom<menuItems.size()){
            addOverflowAtTheEnd = true;
            availItemRoom--;
        }

        if(availItemRoom>0)
            for(MenuItemImpl menuItem: menuItems){

                if(menuItem.requestsActionButton() && menuItem.getIcon()!=null){

                    ImageView action = getActionHolder();
                    action.setImageDrawable(menuItem.getIcon());
                    addView(action);

                    availItemRoom--;
                    if(availItemRoom==0)
                        break;
                }
            }

        if(addOverflowAtTheEnd){

            ImageView overflowAction = getActionHolder();
            overflowAction.setImageDrawable(getResources().getDrawable(R.drawable.ic_more_vert_black_24dp));
            addView(overflowAction);
        }
    }

    private ImageView getActionHolder(){

        ImageView result = (ImageView)LayoutInflater.from(getContext()).inflate(R.layout.action_item_layout, null, false);

        return result;
    }
}
