package com.arlib.floatingsearchview.util.actionmenu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

    private HashMap<MenuItemImpl, Integer> mViewPosMap = new HashMap<>();

    private int mAvailWidth;

    private List<MenuItemImpl> mMenuItems;
    private List<MenuItemImpl> mActionItems = new ArrayList<>();

    private boolean mHasOverflow = false;

    public interface OnViewItemsTransXChangedListener{
        void onTranslationXChanged(int dx);
    }

    private OnViewItemsTransXChangedListener mOnViewItemsTransXChangedListener;

    public void setOnViewItemsTransXChangedListener(OnViewItemsTransXChangedListener listener){
        this.mOnViewItemsTransXChangedListener = listener;
    }

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

    public void reset(int availWidth){

        this.mAvailWidth = availWidth;

        removeAllViews();

        mMenuBuilder.clearAll();
        getMenuInflater().inflate(mMenuId, mMenuBuilder);

        int holdAllItemsCount;

        mMenuItems =  mMenuBuilder.getActionItems();
        mMenuItems.addAll(mMenuBuilder.getNonActionItems());

        holdAllItemsCount = mMenuItems.size();

        Collections.sort(mMenuItems, new Comparator<MenuItemImpl>() {
            @Override
            public int compare(MenuItemImpl lhs, MenuItemImpl rhs) {
                return ((Integer) lhs.getOrder()).compareTo(rhs.getOrder());
            }
        });

        List<MenuItemImpl> menuItems = filter(mMenuItems, new MenuItemImplPredicate() {
            @Override
            public boolean apply(MenuItemImpl menuItem) {
                return menuItem.requiresActionButton() || menuItem.requestsActionButton();
            }
        });

        int availItemRoom = availWidth/(int)ACTION_DIMENSION_PX;
        boolean addOverflowAtTheEnd = false;
        if(((menuItems.size()<holdAllItemsCount) || availItemRoom<menuItems.size())){
            addOverflowAtTheEnd = true;
            availItemRoom--;
        }

        ArrayList<Integer> actionMenuItems = new ArrayList<>();

        if(availItemRoom>0)
            for(int i=0; i<menuItems.size(); i++){

                final MenuItemImpl menuItem = menuItems.get(i);

                if(menuItem.getIcon()!=null){

                    ImageView action = getActionHolder();
                    action.setImageDrawable(setIconColor(menuItem.getIcon(), mIconColor));
                    addView(action);
                    mViewPosMap.put(menuItem, i);
                    mActionItems.add(menuItem);

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

            mHasOverflow = true;
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

    public void showIfRoomItems(){

        final int preAnimTranslationX = (int)getChildAt(0).getTranslationX();

        final List<ObjectAnimator> anims = new ArrayList<>();

        for(int i=0; i<getChildCount(); i++){

            if(i<mActionItems.size()){
                ImageView action = (ImageView)getChildAt(i);
                final MenuItem actionItem = mActionItems.get(i);
                action.setImageDrawable(setIconColor(actionItem.getIcon(), mIconColor));

                action.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(mMenuCallback!=null)
                            mMenuCallback.onMenuItemSelected(mMenuBuilder, actionItem);
                    }
                });
            }

            getChildAt(i).setClickable(true);
            anims.add(ViewPropertyObjectAnimator.animate(getChildAt(i)).translationX(0).get());
            anims.add(ViewPropertyObjectAnimator.animate(getChildAt(i)).scaleX(1.0f).get());
            anims.add(ViewPropertyObjectAnimator.animate(getChildAt(i)).scaleY(1.0f).get());
            anims.add(ViewPropertyObjectAnimator.animate(getChildAt(i)).alpha(1.0f).get());
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(500);
        animSet.playTogether(anims.toArray(new ObjectAnimator[anims.size()]));
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

                if(mOnViewItemsTransXChangedListener!=null)
                    mOnViewItemsTransXChangedListener.onTranslationXChanged(-preAnimTranslationX);
            }
        });
        animSet.start();
    }

    public void hideIfRoomItems(){

        List<MenuItemImpl> showAlwaysActionItems = filter(mMenuItems,new MenuItemImplPredicate() {
            @Override
            public boolean apply(MenuItemImpl menuItem) {
                return menuItem.requiresActionButton();
            }
        });

        int actionItemIndex;
        for(actionItemIndex=0;
            actionItemIndex<mActionItems.size() && actionItemIndex<showAlwaysActionItems.size();
            actionItemIndex++){

            if(mActionItems.get(actionItemIndex).getItemId()!=showAlwaysActionItems.get(actionItemIndex).getItemId()){

                ImageView action = (ImageView)getChildAt(actionItemIndex);
                final MenuItemImpl actionItem = showAlwaysActionItems.get(actionItemIndex);
                action.setImageDrawable(setIconColor(actionItem.getIcon(), mIconColor));

                action.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(mMenuCallback!=null)
                            mMenuCallback.onMenuItemSelected(mMenuBuilder, actionItem);
                    }
                });
            }
        }

        final int diff = mActionItems.size()-actionItemIndex+(mHasOverflow?1:0);

        final List<ObjectAnimator> anims = new ArrayList<>();

        for(int i=0; i<actionItemIndex; i++)
            anims.add(ViewPropertyObjectAnimator.animate(getChildAt(i))
                    .translationXBy(ACTION_DIMENSION_PX * diff - (mHasOverflow?Util.dpToPx(8):0)).get());

        for(int i=actionItemIndex; i<diff+actionItemIndex; i++){

            getChildAt(i).setClickable(false);

            if(i!=getChildCount()-1)
                anims.add(ViewPropertyObjectAnimator.animate(getChildAt(i)).translationXBy(ACTION_DIMENSION_PX).get());

            anims.add(ViewPropertyObjectAnimator.animate(getChildAt(i)).scaleX(.5f).get());
            anims.add(ViewPropertyObjectAnimator.animate(getChildAt(i)).scaleY(.5f).get());
            anims.add(ViewPropertyObjectAnimator.animate(getChildAt(i)).alpha(0.0f).get());
        }

        if(!anims.isEmpty()){

            AnimatorSet animSet = new AnimatorSet();
            animSet.setDuration(500);
            animSet.playTogether(anims.toArray(new ObjectAnimator[anims.size()]));
            animSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {

                    if (mOnViewItemsTransXChangedListener != null)
                        mOnViewItemsTransXChangedListener.onTranslationXChanged((int)getChildAt(0).getTranslationX());
                }
            });
            animSet.start();
        }

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
}
