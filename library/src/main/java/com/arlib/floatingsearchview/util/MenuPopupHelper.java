package com.arlib.floatingsearchview.util;

/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Parcelable;
import android.support.v7.view.menu.ListMenuItemView;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuPresenter;
import android.support.v7.view.menu.MenuView;
import android.support.v7.view.menu.SubMenuBuilder;
import android.support.v7.widget.ListPopupWindow;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.PopupWindow;

import com.arlib.floatingsearchview.R;

import java.util.ArrayList;

/**
 * Presents a menu as a small, simple popup anchored to another view.
 */
public class MenuPopupHelper implements AdapterView.OnItemClickListener, View.OnKeyListener,
        ViewTreeObserver.OnGlobalLayoutListener, PopupWindow.OnDismissListener,
        MenuPresenter {
    private static final String TAG = "MenuPopupHelper";
    static final int ITEM_LAYOUT = R.layout.abc_popup_menu_item_layout;
    private final Context mContext;
    private final LayoutInflater mInflater;
    private final MenuBuilder mMenu;
    private final MenuAdapter mAdapter;
    private final boolean mOverflowOnly;
    private final int mPopupMaxWidth;
    private final int mPopupStyleAttr;
    private final int mPopupStyleRes;
    private View mAnchorView;
    private ListPopupWindow mPopup;
    private ViewTreeObserver mTreeObserver;
    private Callback mPresenterCallback;
    boolean mForceShowIcon;
    private ViewGroup mMeasureParent;
    /** Whether the cached content width value is valid. */
    private boolean mHasContentWidth;
    /** Cached content width from {@link #measureContentWidth}. */
    private int mContentWidth;
    private int mDropDownGravity = Gravity.NO_GRAVITY;
    public MenuPopupHelper(Context context, MenuBuilder menu) {
        this(context, menu, null, false, R.attr.popupMenuStyle);
    }
    public MenuPopupHelper(Context context, MenuBuilder menu, View anchorView) {
        this(context, menu, anchorView, false, R.attr.popupMenuStyle);
    }
    public MenuPopupHelper(Context context, MenuBuilder menu, View anchorView,
                           boolean overflowOnly, int popupStyleAttr) {
        this(context, menu, anchorView, overflowOnly, popupStyleAttr, 0);
    }
    public MenuPopupHelper(Context context, MenuBuilder menu, View anchorView,
                           boolean overflowOnly, int popupStyleAttr, int popupStyleRes) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mMenu = menu;
        mAdapter = new MenuAdapter(mMenu);
        mOverflowOnly = overflowOnly;
        mPopupStyleAttr = popupStyleAttr;
        mPopupStyleRes = popupStyleRes;
        final Resources res = context.getResources();
        mPopupMaxWidth = Math.max(res.getDisplayMetrics().widthPixels / 2,
                res.getDimensionPixelSize(R.dimen.abc_config_prefDialogWidth));
        mAnchorView = anchorView;
        // Present the menu using our context, not the menu builder's context.
        menu.addMenuPresenter(this, context);
    }

    public float mOffsetX;

    public float mOffsetY;

    public void setOffsetX(float x){
        this.mOffsetX = x;
    }

    public void setOffsetY(float y){
        this.mOffsetY = y;
    }

    public void setAnchorView(View anchor) {
        mAnchorView = anchor;
    }
    public void setForceShowIcon(boolean forceShow) {
        mForceShowIcon = forceShow;
    }
    public void setGravity(int gravity) {
        mDropDownGravity = gravity;
    }
    public int getGravity() {
        return mDropDownGravity;
    }
    public void show() {
        if (!tryShow()) {
            throw new IllegalStateException("MenuPopupHelper cannot be used without an anchor");
        }
    }
    public ListPopupWindow getPopup() {
        return mPopup;
    }
    public boolean tryShow() {
        mPopup = new ListPopupWindow(mContext, null, mPopupStyleAttr, mPopupStyleRes);
        mPopup.setOnDismissListener(this);
        mPopup.setOnItemClickListener(this);
        mPopup.setAdapter(mAdapter);
        mPopup.setModal(true);
        View anchor = mAnchorView;
        if (anchor != null) {
            final boolean addGlobalListener = mTreeObserver == null;
            mTreeObserver = anchor.getViewTreeObserver(); // Refresh to latest
            if (addGlobalListener) mTreeObserver.addOnGlobalLayoutListener(this);
            mPopup.setAnchorView(anchor);
            mPopup.setDropDownGravity(mDropDownGravity);
        } else {
            return false;
        }
        if (!mHasContentWidth) {
            mContentWidth = measureContentWidth();
            mHasContentWidth = true;
        }
        mPopup.setContentWidth(mContentWidth);
        mPopup.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);

        int vertOffset = -mAnchorView.getHeight() + Util.dpToPx(4);
        int horizontalOffset = -mContentWidth + mAnchorView.getWidth();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            vertOffset = -mAnchorView.getHeight() - Util.dpToPx(4);
            horizontalOffset = -mContentWidth+mAnchorView.getWidth()-Util.dpToPx(8);
        }
        mPopup.setVerticalOffset(vertOffset);
        mPopup.setHorizontalOffset(horizontalOffset);
        mPopup.show();
        mPopup.getListView().setOnKeyListener(this);
        return true;
    }
    public void dismiss() {
        if (isShowing()) {
            mPopup.dismiss();
        }
    }
    public void onDismiss() {
        mPopup = null;
        mMenu.close();
        if (mTreeObserver != null) {
            if (!mTreeObserver.isAlive()) mTreeObserver = mAnchorView.getViewTreeObserver();
            mTreeObserver.removeGlobalOnLayoutListener(this);
            mTreeObserver = null;
        }
    }
    public boolean isShowing() {
        return mPopup != null && mPopup.isShowing();
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MenuAdapter adapter = mAdapter;
        adapter.mAdapterMenu.performItemAction(adapter.getItem(position), 0);
    }
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_MENU) {
            dismiss();
            return true;
        }
        return false;
    }
    private int measureContentWidth() {
        // Menus don't tend to be long, so this is more sane than it looks.
        int maxWidth = 0;
        View itemView = null;
        int itemType = 0;
        final ListAdapter adapter = mAdapter;
        final int widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        final int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        final int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            final int positionType = adapter.getItemViewType(i);
            if (positionType != itemType) {
                itemType = positionType;
                itemView = null;
            }
            if (mMeasureParent == null) {
                mMeasureParent = new FrameLayout(mContext);
            }
            itemView = adapter.getView(i, itemView, mMeasureParent);
            itemView.measure(widthMeasureSpec, heightMeasureSpec);
            final int itemWidth = itemView.getMeasuredWidth();
            if (itemWidth >= mPopupMaxWidth) {
                return mPopupMaxWidth;
            } else if (itemWidth > maxWidth) {
                maxWidth = itemWidth;
            }
        }
        return maxWidth;
    }
    @Override
    public void onGlobalLayout() {
        if (isShowing()) {
            final View anchor = mAnchorView;
            if (anchor == null || !anchor.isShown()) {
                dismiss();
            } else if (isShowing()) {
                // Recompute window size and position
                mPopup.show();
            }
        }
    }
    @Override
    public void initForMenu(Context context, MenuBuilder menu) {
        // Don't need to do anything; we added as a presenter in the constructor.
    }
    @Override
    public MenuView getMenuView(ViewGroup root) {
        throw new UnsupportedOperationException("MenuPopupHelpers manage their own views");
    }
    @Override
    public void updateMenuView(boolean cleared) {
        mHasContentWidth = false;
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }
    @Override
    public void setCallback(Callback cb) {
        mPresenterCallback = cb;
    }
    @Override
    public boolean onSubMenuSelected(SubMenuBuilder subMenu) {
        if (subMenu.hasVisibleItems()) {
            MenuPopupHelper subPopup = new MenuPopupHelper(mContext, subMenu, mAnchorView);
            subPopup.setCallback(mPresenterCallback);
            boolean preserveIconSpacing = false;
            final int count = subMenu.size();
            for (int i = 0; i < count; i++) {
                MenuItem childItem = subMenu.getItem(i);
                if (childItem.isVisible() && childItem.getIcon() != null) {
                    preserveIconSpacing = true;
                    break;
                }
            }
            subPopup.setForceShowIcon(preserveIconSpacing);
            if (subPopup.tryShow()) {
                if (mPresenterCallback != null) {
                    mPresenterCallback.onOpenSubMenu(subMenu);
                }
                return true;
            }
        }
        return false;
    }
    @Override
    public void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
        // Only care about the (sub)menu we're presenting.
        if (menu != mMenu) return;
        dismiss();
        if (mPresenterCallback != null) {
            mPresenterCallback.onCloseMenu(menu, allMenusAreClosing);
        }
    }
    @Override
    public boolean flagActionItems() {
        return false;
    }
    public boolean expandItemActionView(MenuBuilder menu, MenuItemImpl item) {
        return false;
    }
    public boolean collapseItemActionView(MenuBuilder menu, MenuItemImpl item) {
        return false;
    }
    @Override
    public int getId() {
        return 0;
    }
    @Override
    public Parcelable onSaveInstanceState() {
        return null;
    }
    @Override
    public void onRestoreInstanceState(Parcelable state) {
    }
    private class MenuAdapter extends BaseAdapter {
        private MenuBuilder mAdapterMenu;
        private int mExpandedIndex = -1;
        public MenuAdapter(MenuBuilder menu) {
            mAdapterMenu = menu;
            findExpandedIndex();
        }
        public int getCount() {
            ArrayList<MenuItemImpl> items = mOverflowOnly ?
                    mAdapterMenu.getNonActionItems() : mAdapterMenu.getVisibleItems();
            if (mExpandedIndex < 0) {
                return items.size();
            }
            return items.size() - 1;
        }
        public MenuItemImpl getItem(int position) {
            ArrayList<MenuItemImpl> items = mOverflowOnly ?
                    mAdapterMenu.getNonActionItems() : mAdapterMenu.getVisibleItems();
            if (mExpandedIndex >= 0 && position >= mExpandedIndex) {
                position++;
            }
            return items.get(position);
        }
        public long getItemId(int position) {
            // Since a menu item's ID is optional, we'll use the position as an
            // ID for the item in the AdapterView
            return position;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(ITEM_LAYOUT, parent, false);
            }
            MenuView.ItemView itemView = (MenuView.ItemView) convertView;
            if (mForceShowIcon) {
                ((ListMenuItemView) convertView).setForceShowIcon(true);
            }
            itemView.initialize(getItem(position), 0);
            return convertView;
        }
        void findExpandedIndex() {
            final MenuItemImpl expandedItem = mMenu.getExpandedItem();
            if (expandedItem != null) {
                final ArrayList<MenuItemImpl> items = mMenu.getNonActionItems();
                final int count = items.size();
                for (int i = 0; i < count; i++) {
                    final MenuItemImpl item = items.get(i);
                    if (item == expandedItem) {
                        mExpandedIndex = i;
                        return;
                    }
                }
            }
            mExpandedIndex = -1;
        }
        @Override
        public void notifyDataSetChanged() {
            findExpandedIndex();
            super.notifyDataSetChanged();
        }
    }
}