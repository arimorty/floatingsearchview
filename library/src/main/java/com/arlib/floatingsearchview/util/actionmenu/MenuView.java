package com.arlib.floatingsearchview.util.actionmenu;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v7.view.SupportMenuInflater;
import android.support.v7.view.ViewPropertyAnimatorCompatSet;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuItemImpl;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.arlib.floatingsearchview.R;
import com.arlib.floatingsearchview.util.MenuPopupHelper;
import com.arlib.floatingsearchview.util.Util;
import com.bartoszlipinski.viewpropertyobjectanimator.ViewPropertyObjectAnimator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by ari on 12/5/2015.
 */
public class MenuView extends LinearLayout {

    private final float ACTION_DIMENSION_PX;

    private int mMenuId;

    private MenuBuilder mMenuBuilder;
    private SupportMenuInflater mMenuInflater;
    private MenuPopupHelper mMenuPopupHelper;

    private int mWidth = -1;

    private MenuBuilder.Callback mMenuCallback;

    private int mIconColor;

    public MenuView(Context context) {
        this(context, null);
    }

    public MenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ACTION_DIMENSION_PX = context.getResources().getDimension(R.dimen.square_button_size);
        init();
    }

    private void init(){
        mMenuBuilder = new MenuBuilder(getContext());
        mMenuPopupHelper = new MenuPopupHelper(getContext(), mMenuBuilder, this);

        mIconColor = getResources().getColor(R.color.gray_active_icon);
    }

    public void resetMenuResource(int menuId){

        this.mMenuId = menuId;
    }

    public void setMenuCallback( MenuBuilder.Callback menuCallback){
        this.mMenuCallback = menuCallback;
    }

    private void showItems(int availWidth, MenuItemImplPredicate menuItemImplPredicate, boolean showOverflowMenu){

        removeAllViews();

        mMenuBuilder.clearAll();
        getMenuInflater().inflate(mMenuId, mMenuBuilder);

        int holdAllItemsCount;

        List<MenuItemImpl> menuItems =  mMenuBuilder.getActionItems();
        menuItems.addAll(mMenuBuilder.getNonActionItems());

        holdAllItemsCount = menuItems.size();

        Collections.sort(menuItems, new Comparator<MenuItemImpl>() {
            @Override
            public int compare(MenuItemImpl lhs, MenuItemImpl rhs) {
                return ((Integer) lhs.getOrder()).compareTo(rhs.getOrder());
            }
        });

        menuItems = filter(menuItems, menuItemImplPredicate);

        int availItemRoom = availWidth/(int)ACTION_DIMENSION_PX;
        boolean addOverflowAtTheEnd = false;
        if(((menuItems.size()<holdAllItemsCount) || availItemRoom<menuItems.size()) && showOverflowMenu){
            addOverflowAtTheEnd = true;
            availItemRoom--;
        }

        ArrayList<Integer> actionMenuItems = new ArrayList<>();

        if(availItemRoom>0)
            for(final MenuItemImpl menuItem: menuItems){

                if(menuItem.getIcon()!=null){

                    ImageView action = getActionHolder();
                    action.setImageDrawable(setIconColor(menuItem.getIcon(), mIconColor));
                    addView(action);

                    action.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(mMenuCallback!=null)
                                mMenuCallback.onMenuItemSelected(mMenuBuilder, menuItem);
                        }
                    });

                    actionMenuItems.add(menuItem.getItemId());

                    availItemRoom--;
                    if(availItemRoom==0)
                        break;
                }
            }

        if(addOverflowAtTheEnd){

            ImageView overflowAction = getOverflowActionHolder();
            overflowAction.setImageDrawable(setIconColor(
                    getResources().getDrawable(R.drawable.ic_more_vert_black_24dp), mIconColor));
            addView(overflowAction);

            overflowAction.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    mMenuPopupHelper.show();
                }
            });

            mMenuBuilder.setCallback(mMenuCallback);
        }

        for(int id: actionMenuItems)
            mMenuBuilder.removeItem(id);

        actionMenuItems.clear();
    }

    private ImageView getActionHolder(){
        return (ImageView)LayoutInflater.from(getContext()).inflate(R.layout.action_item_layout, this, false);
    }

    private ImageView getOverflowActionHolder(){
        return (ImageView)LayoutInflater.from(getContext()).inflate(R.layout.overflow_action_item_layout, this, false);
    }

    private MenuInflater getMenuInflater() {
        if (mMenuInflater == null) {
            mMenuInflater = new SupportMenuInflater(getContext());
        }
        return mMenuInflater;
    }

    private Drawable setIconColor(Drawable icon, int color){
        DrawableCompat.wrap(icon);
        DrawableCompat.setTint(icon, color);
        return icon;
    }

    public void showAlwaysIfRoomItems(int availWidth){

        showItems(availWidth, new MenuItemImplPredicate() {
            @Override
            public boolean apply(MenuItemImpl menuItem) {
                return menuItem.requiresActionButton();
            }
        }, false);
    }

    public void showIfRoomItems(int availWidth, boolean withAnim){

        showItems(availWidth, new MenuItemImplPredicate() {
            @Override
            public boolean apply(MenuItemImpl menuItem) {
                return menuItem.requestsActionButton() || menuItem.requiresActionButton();
            }
        }, true);
    }

    private interface MenuItemImplPredicate{

        boolean apply(MenuItemImpl menuItem);
    }

    private List<MenuItemImpl> filter(List<MenuItemImpl> target, MenuItemImplPredicate predicate) {
        List<MenuItemImpl> result = new ArrayList<>();
        for (MenuItemImpl element: target) {
            if (predicate.apply(element)) {
                result.add(element);
            }
        }
        return result;
    }
}
