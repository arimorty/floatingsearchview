package com.arlib.floatingsearchview.util.view;

/**
 * Copyright (C) 2015 Ari C.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.view.SupportMenuInflater;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuItemImpl;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.arlib.floatingsearchview.R;
import com.arlib.floatingsearchview.util.MenuPopupHelper;
import com.arlib.floatingsearchview.util.Util;
import com.bartoszlipinski.viewpropertyobjectanimator.ViewPropertyObjectAnimator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A view that shows menu items as actions or
 * as items in a overflow popup.
 */
public class MenuView extends LinearLayout {

    private final int HIDE_IF_ROOM_ITEMS_ANIM_DURATION = 400;
    private final int SHOW_IF_ROOM_ITEMS_ANIM_DURATION = 450;

    private final float ACTION_DIMENSION_PX;

    private int mMenu = -1;
    private MenuBuilder mMenuBuilder;
    private SupportMenuInflater mMenuInflater;
    private MenuPopupHelper mMenuPopupHelper;

    private MenuBuilder.Callback mMenuCallback;

    private int mActionIconColor;
    private int mOverflowIconColor;

    //all menu items
    private List<MenuItemImpl> mMenuItems;

    //items that are currently presented as actions
    private List<MenuItemImpl> mActionItems = new ArrayList<>();

    private List<MenuItemImpl> mActionShowAlwaysItems = new ArrayList<>();

    private boolean mHasOverflow = false;

    private OnVisibleWidthChangedListener mOnVisibleWidthChangedListener;
    private int mVisibleWidth;

    private List<ObjectAnimator> anims = new ArrayList<>();

    public interface OnVisibleWidthChangedListener {
        void onItemsMenuVisibleWidthChanged(int newVisibleWidth);
    }

    public MenuView(Context context) {
        this(context, null);
    }

    public MenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ACTION_DIMENSION_PX = context.getResources().getDimension(R.dimen.square_button_size);
        init();
    }

    public List<MenuItemImpl> getCurrentMenuItems(){
        return mMenuItems;
    }

    private void init() {
        mMenuBuilder = new MenuBuilder(getContext());
        mMenuPopupHelper = new MenuPopupHelper(getContext(), mMenuBuilder, this);
        mActionIconColor = Util.getColor(getContext(), R.color.gray_active_icon);
        mOverflowIconColor = Util.getColor(getContext(), R.color.gray_active_icon);
    }

    public void setActionIconColor(int actionColor) {
        this.mActionIconColor = actionColor;
        refreshColors();
    }

    public void setOverflowColor(int overflowColor) {
        this.mOverflowIconColor = overflowColor;
        refreshColors();
    }

    private void refreshColors() {
        for (int i = 0; i < getChildCount(); i++) {
            Util.setIconColor(((ImageView) getChildAt(i)), mActionIconColor);
            if (mHasOverflow && i == getChildCount() - 1) {
                Util.setIconColor(((ImageView) getChildAt(i)), mOverflowIconColor);
            }
        }
    }

    /**
     * Set the callback that will be called when menu
     * items a selected.
     *
     * @param menuCallback
     */
    public void setMenuCallback(MenuBuilder.Callback menuCallback) {
        this.mMenuCallback = menuCallback;
    }

    /**
     * Resets the the view to fit into a new
     * available width.
     * <p/>
     * <p>This clears and then re-inflates the menu items
     * , removes all of its associated action views, and re-creates
     * the menu and action items to fit in the new width.</p>
     *
     * @param availWidth the width available for the menu to use. If
     *                   there is room, menu items that are flagged with
     *                   android:showAsAction="ifRoom" or android:showAsAction="always"
     *                   will show as actions.
     */
    public void reset(int menu, int availWidth) {
        mMenu = menu;
        if (mMenu == -1) {
            return;
        }

        mActionShowAlwaysItems = new ArrayList<>();
        mActionItems = new ArrayList<>();
        mMenuItems = new ArrayList<>();
        mMenuBuilder = new MenuBuilder(getContext());
        mMenuPopupHelper = new MenuPopupHelper(getContext(), mMenuBuilder, this);

        //clean view and re-inflate
        removeAllViews();
        getMenuInflater().inflate(mMenu, mMenuBuilder);

        mMenuItems = mMenuBuilder.getActionItems();
        mMenuItems.addAll(mMenuBuilder.getNonActionItems());

        Collections.sort(mMenuItems, new Comparator<MenuItemImpl>() {
            @Override
            public int compare(MenuItemImpl lhs, MenuItemImpl rhs) {
                return ((Integer) lhs.getOrder()).compareTo(rhs.getOrder());
            }
        });

        List<MenuItemImpl> localActionItems = filter(mMenuItems, new MenuItemImplPredicate() {
            @Override
            public boolean apply(MenuItemImpl menuItem) {
                return menuItem.getIcon() != null && (menuItem.requiresActionButton() || menuItem.requestsActionButton());
            }
        });


        int availItemRoom = availWidth / (int) ACTION_DIMENSION_PX;

        //determine if to show overflow menu
        boolean addOverflowAtTheEnd = false;
        if (((localActionItems.size() < mMenuItems.size()) || availItemRoom < localActionItems.size())) {
            addOverflowAtTheEnd = true;
            availItemRoom--;
        }

        ArrayList<Integer> actionItemsIds = new ArrayList<>();
        if (availItemRoom > 0) {
            for (int i = 0; i < localActionItems.size(); i++) {

                final MenuItemImpl menuItem = localActionItems.get(i);
                if (menuItem.getIcon() != null) {

                    ImageView action = createActionView();
                    action.setContentDescription(menuItem.getTitle());
                    action.setImageDrawable(menuItem.getIcon());
                    Util.setIconColor(action, mActionIconColor);
                    addView(action);
                    mActionItems.add(menuItem);
                    actionItemsIds.add(menuItem.getItemId());

                    action.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (mMenuCallback != null) {
                                mMenuCallback.onMenuItemSelected(mMenuBuilder, menuItem);
                            }
                        }
                    });

                    availItemRoom--;
                    if (availItemRoom == 0) {
                        break;
                    }
                }
            }
        }

        mHasOverflow = addOverflowAtTheEnd;
        if (addOverflowAtTheEnd) {

            ImageView overflowAction = getOverflowActionView();
            overflowAction.setImageResource(R.drawable.ic_more_vert_black_24dp);
            Util.setIconColor(overflowAction, mOverflowIconColor);
            addView(overflowAction);

            overflowAction.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMenuPopupHelper.show();
                }
            });

            mMenuBuilder.setCallback(mMenuCallback);
        }

        //remove all menu items that will be shown as icons (the action items) from the overflow menu
        for (int id : actionItemsIds) {
            mMenuBuilder.removeItem(id);
        }
        actionItemsIds = null;

        if (mOnVisibleWidthChangedListener != null) {
            mVisibleWidth = ((int) ACTION_DIMENSION_PX * getChildCount()) - (mHasOverflow ? Util.dpToPx(8) : 0);
            mOnVisibleWidthChangedListener.onItemsMenuVisibleWidthChanged(mVisibleWidth);
        }
    }

    public int getVisibleWidth() {
        return mVisibleWidth;
    }

    private ImageView createActionView() {
        return (ImageView) LayoutInflater.from(getContext()).inflate(R.layout.action_item_layout, this, false);
    }

    private ImageView getOverflowActionView() {
        return (ImageView) LayoutInflater.from(getContext()).inflate(R.layout.overflow_action_item_layout, this, false);
    }

    /**
     * Hides all the menu items flagged with "ifRoom"
     *
     * @param withAnim
     */
    public void hideIfRoomItems(boolean withAnim) {

        if (mMenu == -1) {
            return;
        }

        mActionShowAlwaysItems.clear();
        cancelChildAnimListAndClear();

        List<MenuItemImpl> showAlwaysActionItems = filter(mMenuItems, new MenuItemImplPredicate() {
            @Override
            public boolean apply(MenuItemImpl menuItem) {
                return  menuItem.getIcon() != null && menuItem.requiresActionButton();
            }
        });

        int actionItemIndex;
        for (actionItemIndex = 0;
             actionItemIndex < mActionItems.size() && actionItemIndex < showAlwaysActionItems.size();
             actionItemIndex++) {

            final MenuItemImpl showAlwaysActionItem = showAlwaysActionItems.get(actionItemIndex);

            //reset action item image if needed
            if (mActionItems.get(actionItemIndex).getItemId() != showAlwaysActionItem.getItemId()) {

                ImageView action = (ImageView) getChildAt(actionItemIndex);
                action.setImageDrawable(showAlwaysActionItem.getIcon());
                Util.setIconColor(action, mOverflowIconColor);
                action.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (mMenuCallback != null) {
                            mMenuCallback.onMenuItemSelected(mMenuBuilder, showAlwaysActionItem);
                        }
                    }
                });
            }
            mActionShowAlwaysItems.add(showAlwaysActionItem);
        }

        final int diff = mActionItems.size() - actionItemIndex + (mHasOverflow ? 1 : 0);

        anims = new ArrayList<>();

        //add anims for moving showAlwaysItem views to the right
        for (int i = 0; i < actionItemIndex; i++) {

            final View currentChild = getChildAt(i);
            final float destTransX = (ACTION_DIMENSION_PX * diff) - (mHasOverflow ? Util.dpToPx(8) : 0);
            anims.add(ViewPropertyObjectAnimator.animate(currentChild)
                    .setDuration(withAnim ? HIDE_IF_ROOM_ITEMS_ANIM_DURATION : 0)
                    .setInterpolator(new AccelerateInterpolator())
                    .addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {

                            currentChild.setTranslationX(destTransX);
                        }
                    })
                    .translationXBy(destTransX).get());
        }

        //add anims for moving to right and/or zooming out previously shown items
        for (int i = actionItemIndex; i < (diff + actionItemIndex); i++) {

            final View currentView = getChildAt(i);
            currentView.setClickable(false);

            //move to right
            if (i != (getChildCount() - 1)) {
                anims.add(ViewPropertyObjectAnimator.animate(currentView).setDuration(withAnim ? HIDE_IF_ROOM_ITEMS_ANIM_DURATION : 0)
                        .addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {

                                currentView.setTranslationX(ACTION_DIMENSION_PX);
                            }
                        }).translationXBy(ACTION_DIMENSION_PX).get());
            }

            //scale and zoom out
            anims.add(ViewPropertyObjectAnimator.animate(currentView)
                    .setDuration(withAnim ? HIDE_IF_ROOM_ITEMS_ANIM_DURATION : 0)
                    .addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {

                            currentView.setScaleX(0.5f);
                        }
                    }).scaleX(.5f).get());
            anims.add(ViewPropertyObjectAnimator.animate(currentView)
                    .setDuration(withAnim ? HIDE_IF_ROOM_ITEMS_ANIM_DURATION : 0)
                    .addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {

                            currentView.setScaleY(0.5f);
                        }
                    }).scaleY(.5f).get());
            anims.add(ViewPropertyObjectAnimator.animate(currentView)
                    .setDuration(withAnim ? HIDE_IF_ROOM_ITEMS_ANIM_DURATION : 0)
                    .addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {

                            currentView.setAlpha(0.0f);
                        }
                    }).alpha(0.0f).get());
        }

        final int actionItemsCount = actionItemIndex;

        //finally, run animation
        if (!anims.isEmpty()) {

            AnimatorSet animSet = new AnimatorSet();
            if (!withAnim) {
                //temporary, from laziness
                animSet.setDuration(0);
            }
            animSet.playTogether(anims.toArray(new ObjectAnimator[anims.size()]));
            animSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {

                    if (mOnVisibleWidthChangedListener != null) {
                        mVisibleWidth = ((int) ACTION_DIMENSION_PX * actionItemsCount);
                        mOnVisibleWidthChangedListener.onItemsMenuVisibleWidthChanged(mVisibleWidth);
                    }
                }
            });
            animSet.start();
        }
    }

    /**
     * Shows all the menu items that were hidden by hideIfRoomItems(boolean withAnim)
     *
     * @param withAnim
     */
    public void showIfRoomItems(boolean withAnim) {

        if (mMenu == -1) {
            return;
        }

        cancelChildAnimListAndClear();

        if (mMenuItems.isEmpty()) {
            return;
        }

        anims = new ArrayList<>();

        for (int i = 0; i < getChildCount(); i++) {

            final View currentView = getChildAt(i);

            //reset all the action item views
            if (i < mActionItems.size()) {
                ImageView action = (ImageView) currentView;
                final MenuItem actionItem = mActionItems.get(i);
                action.setImageDrawable(actionItem.getIcon());
                Util.setIconColor(action, mActionIconColor);
                action.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (mMenuCallback != null) {
                            mMenuCallback.onMenuItemSelected(mMenuBuilder, actionItem);
                        }
                    }
                });
            }

            Interpolator interpolator = new DecelerateInterpolator();
            if (i > (mActionShowAlwaysItems.size() - 1)) {
                interpolator = new LinearInterpolator();
            }

            currentView.setClickable(true);

            //simply animate all properties of all action item views back to their default/visible state
            anims.add(ViewPropertyObjectAnimator.animate(currentView)
                    .addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {

                            currentView.setTranslationX(0);
                        }
                    })
                    .setInterpolator(interpolator)
                    .translationX(0).get());
            anims.add(ViewPropertyObjectAnimator.animate(currentView)
                    .addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {

                            currentView.setScaleX(1.0f);
                        }
                    })
                    .setInterpolator(interpolator)
                    .scaleX(1.0f).get());
            anims.add(ViewPropertyObjectAnimator.animate(currentView)
                    .addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {

                            currentView.setScaleY(1.0f);
                        }
                    })
                    .setInterpolator(interpolator)
                    .scaleY(1.0f).get());
            anims.add(ViewPropertyObjectAnimator.animate(currentView)
                    .addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {

                            currentView.setAlpha(1.0f);
                        }
                    })
                    .setInterpolator(interpolator)
                    .alpha(1.0f).get());
        }

        if(anims.isEmpty()){
            return;
        }

        AnimatorSet animSet = new AnimatorSet();
        if (!withAnim) {
            //temporary, from laziness
            animSet.setDuration(0);
        }
        animSet.playTogether(anims.toArray(new ObjectAnimator[anims.size()]));
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

                if (mOnVisibleWidthChangedListener != null) {
                    mVisibleWidth = (getChildCount() * (int) ACTION_DIMENSION_PX) - (mHasOverflow ? Util.dpToPx(8) : 0);
                    mOnVisibleWidthChangedListener.onItemsMenuVisibleWidthChanged(mVisibleWidth);
                }
            }
        });
        animSet.start();
    }

    private interface MenuItemImplPredicate {

        boolean apply(MenuItemImpl menuItem);
    }

    private List<MenuItemImpl> filter(List<MenuItemImpl> target, MenuItemImplPredicate predicate) {
        List<MenuItemImpl> result = new ArrayList<>();
        for (MenuItemImpl element : target) {
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

    public void setOnVisibleWidthChanged(OnVisibleWidthChangedListener listener) {
        this.mOnVisibleWidthChangedListener = listener;
    }

    private void cancelChildAnimListAndClear() {
        for (ObjectAnimator animator : anims) {
            animator.cancel();
        }
        anims.clear();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        //clear anims if any to avoid leak
        cancelChildAnimListAndClear();
    }
}
