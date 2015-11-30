/*
 * Copyright (C) 2015 Arlib
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.arlib.floatingsearchview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.speech.RecognizerIntent;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.view.SupportMenuInflater;
import android.support.v7.view.ViewPropertyAnimatorCompatSet;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.arlib.floatingsearchview.util.MenuPopupHelper;
import com.arlib.floatingsearchview.util.Util;
import com.arlib.floatingsearchview.util.adapter.GestureDetectorListenerAdapter;
import com.arlib.floatingsearchview.util.adapter.OnItemTouchListenerAdapter;
import com.arlib.floatingsearchview.util.adapter.TextWatcherAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A search UI widget that implements a floating search box also called persistent
 * search.
 */
public class FloatingSearchView extends FrameLayout {

    private static final String TAG = "FloatingSearchView";

    private final int BACKGROUND_DRAWABLE_ALPHA_SEARCH_ACTIVE = 150;

    private final int BACKGROUND_DRAWABLE_ALPHA_SEARCH_INACTIVE = 0;

    private final int MENU_ICON_ANIM_DURATION = 250;

    private final int BACKGROUND_FADE__ANIM_DURATION = 250;

    private final int ATTRS_SEARCH_BAR_MARGIN_DEFAULT = 0;

    private final boolean ATTRS_SEARCH_BAR_SHOW_MENU_ACTION_DEFAULT = true;

    private final boolean ATTRS_DISMISS_ON_OUTSIDE_TOUCH_DEFAULT = false;

    private final boolean ATTRS_SEARCH_BAR_SHOW_VOICE_ACTION_DEFAULT = false;

    private final boolean ATTRS_SEARCH_BAR_SHOW_SEARCH_KEY_DEFAULT = true;

    private final boolean ATTRS_SEARCH_BAR_SHOW_SEARCH_HINT_NOT_FOCUSED_DEFAULT = true;

    private final boolean ATTRS_HIDE_OVERFLOW_MENU_FOCUSED_DEFAULT = true;

    private final boolean ATTRS_SHOW_OVERFLOW_MENU_DEFAULT = true;

    private final int ATTRS_SUGGESTION_TEXT_SIZE_SP_DEFAULT = 18;

    private final int SUGGEST_LIST_COLLAPSE_ANIM_DURATION = 200;

    private final Interpolator SUGGEST_LIST_COLLAPSE_ANIM_INTERPOLATOR = new LinearInterpolator();

    private final int SUGGEST_ITEM_ADD_ANIM_DURATION = 250;

    private final Interpolator SUGGEST_ITEM_ADD_ANIM_INTERPOLATOR = new LinearInterpolator();

    private final int VOICE_REC_DEFAULT_REQUEST_CODE = 1024;

    private final int SUGGESTION_ITEM_ANIM_DURATION = 120;

    private final int OVERFLOW_ICON_WIDTH_DP = 36;

    private Activity mHostActivity;

    private Drawable mBackgroundDrawable;
    private boolean mDismissOnOutsideTouch = true;

    private View mQuerySection;
    private ImageView mVoiceInputOrClearButton;
    private EditText mSearchInput;
    private boolean mShowSearchKey;
    private boolean mIsFocused;
    private TextView mSearchBarTitle;
    private OnQueryChangeListener mQueryListener;
    private ProgressBar mSearchProgress;
    private ImageView mMenuSearchOrExitButton;
    private OnLeftMenuClickListener mOnMenuClickListener;
    private DrawerArrowDrawable mMenuBtnDrawable;
    private Drawable mIconClear;
    private Drawable mIconMic;
    private Drawable mIconOverflowMenu;
    private Drawable mIconBackArrow;
    private Drawable mIconSearch;
    private OnFocusChangeListener mFocusChangeListener;
    private boolean mShowMenuAction;
    private String mOldQuery = "";
    private OnSearchListener mSearchListener;
    private int mVoiceRecRequestCode = VOICE_REC_DEFAULT_REQUEST_CODE;
    private String mVoiceRecHint;
    private boolean mShowVoiceInput;
    private boolean mShowHintNotFocused;
    private String mSearchHint;
    private boolean mMenuOpen = false;
    private boolean mIsActiveOnClick;
    private ImageView mOverflowMenu;
    private MenuBuilder mMenuBuilder;
    private MenuPopupHelper mMenuPopupHelper;
    private SupportMenuInflater mMenuInflater;
    private OnMenuItemClickListener mOnOverflowMenuItemListener;
    private boolean mHideOverflowMenuFocused;
    private boolean mShowOverFlowMenu;
    private boolean mSearchEnabled;
    private boolean mSkipQueryFocusChangeEvent;
    private boolean mSkipTextChangeEvent;

    private View mDivider;

    private RelativeLayout mSuggestionsSection;
    private View mSuggestionListContainer;
    private RecyclerView mSuggestionsList;
    private SearchSuggestionsAdapter mSuggestionsAdapter;
    private boolean mIsCollapsing = false;
    private int mSuggestionsTextSizePx;

    /**
     * Interface for implementing a callback to be
     * invoked when the left menu (navigation menu) is
     * clicked.
     */
    public interface OnLeftMenuClickListener{

        /**
         * Called when the menu button was
         * clicked and the menu's state is now opened.
         */
        void onMenuOpened();

        /**
         * Called when the back button was
         * clicked and the menu's state is now closed.
         */
        void onMenuClosed();
    }

    /**
     * Interface for implementing a listener to listen
     * to when the current search has completed.
     */
    public interface OnSearchListener {

        /**
         * Called when a suggestion was clicked indicating
         * that the current search has completed.
         *
         * @param searchSuggestion
         */
        void onSuggestionClicked(SearchSuggestion searchSuggestion);

        /**
         * Called when the current search has completed
         * as a result of pressing search key in the keyboard.
         */
        void onSearchAction();
    }

    /**
     * Interface for implementing a listener to listen
     * when a menu in the overflow menu has been selected.
     */
    public interface OnMenuItemClickListener{

        /**
         * Called when a menu item in has been
         * selected.
         *
         * @param item the selected menu item.
         */
        void onMenuItemSelected(MenuItem item);
    }

    /**
     * Interface for implementing a listener to listen
     * to for state changes in the query text.
     */
    public interface OnQueryChangeListener{

        /**
         * Called when the query has changed. it will
         * be invoked when one or more characters in the
         * query was changed.
         *
         * @param oldQuery the previous query
         * @param newQuery the new query
         */
        void onSearchTextChanged(String oldQuery, String newQuery);
    }

    /**
     * Interface for implementing a listener to listen
     * to for focus state changes.
     */
    public interface OnFocusChangeListener{

        /**
         * Called when the search bar has gained focus
         * and listeners are now active.
         */
        void onFocus();

        /**
         * Called when the search bar has lost focus
         * and listeners are no more active.
         */
        void onFocusCleared();
    }

    public FloatingSearchView(Context context) {
        this(context, null);
    }

    public FloatingSearchView(Context context, AttributeSet attrs){
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs){

        mHostActivity = getHostActivity();

        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflate(getContext(), R.layout.floating_search_layout, this);

        mBackgroundDrawable = new ColorDrawable(Color.BLACK);

        mQuerySection = findViewById(R.id.search_query_section);
        mVoiceInputOrClearButton = (ImageView)findViewById(R.id.search_bar_mic_or_ex);
        mSearchInput = (EditText)findViewById(R.id.search_bar_text);
        mSearchBarTitle = (TextView)findViewById(R.id.search_bar_title);
        mMenuSearchOrExitButton = (ImageView)findViewById(R.id.search_bar_exit);
        mSearchProgress = (ProgressBar)findViewById(R.id.search_bar_search_progress);
        mShowMenuAction = true;
        mOverflowMenu = (ImageView)findViewById(R.id.search_bar_overflow_menu);
        mMenuBuilder = new MenuBuilder(getContext());
        mMenuPopupHelper = new MenuPopupHelper(getContext(), mMenuBuilder, mOverflowMenu);
        initDrawables();
        mVoiceInputOrClearButton.setImageDrawable(mIconMic);
        mOverflowMenu.setImageDrawable(mIconOverflowMenu);

        mDivider = findViewById(R.id.divider);

        mSuggestionsSection = (RelativeLayout)findViewById(R.id.search_suggestions_section);
        mSuggestionListContainer = findViewById(R.id.suggestions_list_container);
        mSuggestionsList = (RecyclerView)findViewById(R.id.suggestions_list);

        setupViews(attrs);
    }

    private void initDrawables(){

        mMenuBtnDrawable = new DrawerArrowDrawable(getContext());

        mIconClear = getResources().getDrawable(R.drawable.ic_clear_black_24dp);
        mIconClear = DrawableCompat.wrap(mIconClear);

        mIconMic = getResources().getDrawable(R.drawable.ic_mic_black_24dp);
        mIconMic = DrawableCompat.wrap(mIconMic);

        mIconOverflowMenu = getResources().getDrawable(R.drawable.ic_more_vert_black_24dp);
        mIconOverflowMenu = DrawableCompat.wrap(mIconOverflowMenu);

        mIconBackArrow = getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp);
        mIconBackArrow = DrawableCompat.wrap(mIconBackArrow);

        mIconSearch = getResources().getDrawable(R.drawable.ic_search_black_24dp);
        mIconSearch = DrawableCompat.wrap(mIconSearch);

        setIconsColor(getResources().getColor(R.color.gray_active_icon));
    }

    private void setIconsColor(int color){

        mMenuBtnDrawable.setColor(color);
        DrawableCompat.setTint(mIconClear, color);
        DrawableCompat.setTint(mIconMic, color);
        DrawableCompat.setTint(mIconOverflowMenu, color);
        DrawableCompat.setTint(mIconBackArrow, color);
        DrawableCompat.setTint(mIconSearch, color);
    }

    private boolean mIsInitialLayout = true;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        if(mIsInitialLayout) {


            int addedHeight = Util.dpToPx(5*3);

            //we need to add 5dp to the mSuggestionsSection because we are
            //going to move it up by 5dp in order o cover the search bar's
            //rounded corners. We also need to add an additional 10dp to
            //mSuggestionsSection in order to hide mSuggestionListContainer
            //rounded corners and top/bottom padding.
            mSuggestionsSection.getLayoutParams().height = mSuggestionsSection.getMeasuredHeight()
                  +addedHeight;

            mIsInitialLayout = false;
        }

        //todo check if this is safe here
        adjustSearchInputPadding();

        //pass on the layout
        super.onLayout(changed, l, t, r, b);
    }

    private void setupViews(AttributeSet attrs){

        if(attrs!=null)
            applyXmlAttributes(attrs);

        mBackgroundDrawable.setAlpha(BACKGROUND_DRAWABLE_ALPHA_SEARCH_INACTIVE);

        int sdkVersion = Build.VERSION.SDK_INT;
        if(sdkVersion < Build.VERSION_CODES.JELLY_BEAN) {
            setBackgroundDrawable(mBackgroundDrawable);
        } else {
            setBackground(mBackgroundDrawable);
        }

        setupQueryBar();

        if(!isInEditMode())
           setupSuggestionSection();
    }

    private void applyXmlAttributes(AttributeSet attrs){

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.FloatingSearchView);

        try {

            setDismissOnOutsideClick(true);

            int searchBarWidth = a.getDimensionPixelSize(R.styleable.FloatingSearchView_floatingSearch_searchBarWidth, ViewGroup.LayoutParams.MATCH_PARENT);

            mQuerySection.getLayoutParams().width = searchBarWidth;
            mDivider.getLayoutParams().width = searchBarWidth;
            mSuggestionListContainer.getLayoutParams().width = searchBarWidth;

            int searchBarLeftMargin = a.getDimensionPixelSize(R.styleable.FloatingSearchView_floatingSearch_searchBarMarginLeft, ATTRS_SEARCH_BAR_MARGIN_DEFAULT);
            int searchBarTopMargin = a.getDimensionPixelSize(R.styleable.FloatingSearchView_floatingSearch_searchBarMarginTop, ATTRS_SEARCH_BAR_MARGIN_DEFAULT);
            int searchBarRightMargin = a.getDimensionPixelSize(R.styleable.FloatingSearchView_floatingSearch_searchBarMarginRight, ATTRS_SEARCH_BAR_MARGIN_DEFAULT);

            LayoutParams querySectionLP = (LayoutParams)mQuerySection.getLayoutParams();
            LayoutParams dividerLP = (LayoutParams)mDivider.getLayoutParams();
            LinearLayout.LayoutParams suggestListSectionLP = (LinearLayout.LayoutParams)mSuggestionsSection.getLayoutParams();

            querySectionLP.setMargins(searchBarLeftMargin, searchBarTopMargin, searchBarRightMargin, 0);
            dividerLP.setMargins(searchBarLeftMargin, 0, searchBarRightMargin, ((MarginLayoutParams) mDivider.getLayoutParams()).bottomMargin);
            suggestListSectionLP.setMargins(searchBarLeftMargin, 0, searchBarRightMargin, 0);

            mQuerySection.setLayoutParams(querySectionLP);
            mDivider.setLayoutParams(dividerLP);
            mSuggestionsSection.setLayoutParams(suggestListSectionLP);

            setSearchHint(a.getString(R.styleable.FloatingSearchView_floatingSearch_searchHint));

            setShowHintWhenNotFocused(a.getBoolean(R.styleable.FloatingSearchView_floatingSearch_showSearchHintWhenNotFocused, ATTRS_SEARCH_BAR_SHOW_SEARCH_HINT_NOT_FOCUSED_DEFAULT));

            setLeftShowMenu(a.getBoolean(R.styleable.FloatingSearchView_floatingSearch_showMenuAction, ATTRS_SEARCH_BAR_SHOW_MENU_ACTION_DEFAULT));

            setShowVoiceInput(a.getBoolean(R.styleable.FloatingSearchView_floatingSearch_showVoiceInput, ATTRS_SEARCH_BAR_SHOW_VOICE_ACTION_DEFAULT));

            setShowSearchKey(a.getBoolean(R.styleable.FloatingSearchView_floatingSearch_showSearchKey, ATTRS_SEARCH_BAR_SHOW_SEARCH_KEY_DEFAULT));

            setVoiceSearchHint(a.getString(R.styleable.FloatingSearchView_floatingSearch_voiceRecHint));

            setDismissOnOutsideClick(a.getBoolean(R.styleable.FloatingSearchView_floatingSearch_dismissOnOutsideTouch, ATTRS_DISMISS_ON_OUTSIDE_TOUCH_DEFAULT));

            setShowOverflowMenu(a.getBoolean(R.styleable.FloatingSearchView_floatingSearch_showOverFlowMenu, ATTRS_SHOW_OVERFLOW_MENU_DEFAULT));

            setSuggestionItemTextSize(a.getDimensionPixelSize(R.styleable.FloatingSearchView_floatingSearch_searchSuggestionTextSize, Util.spToPx(ATTRS_SUGGESTION_TEXT_SIZE_SP_DEFAULT)));

            if (a.hasValue(R.styleable.FloatingSearchView_floatingSearch_menu)) {
                inflateOverflowMenu(a.getResourceId(R.styleable.FloatingSearchView_floatingSearch_menu, 0));
            }

            setHideOverflowMenuWhenFocused(a.getBoolean(R.styleable.FloatingSearchView_floatingSearch_hideOverflowMenuWhenFocused, ATTRS_HIDE_OVERFLOW_MENU_FOCUSED_DEFAULT));

        } finally {

            a.recycle();
        }
    }

    private Activity getHostActivity() {
        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity)context;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }

    private void setupQueryBar(){

        if(!isInEditMode() && mHostActivity!=null)
            mHostActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mOverflowMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                mMenuPopupHelper.show();
            }
        });

        mMenuBuilder.setCallback(new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {


                if (mOnOverflowMenuItemListener != null)
                    mOnOverflowMenuItemListener.onMenuItemSelected(item);

                //todo check if we should care about this return or not
                return false;
            }

            @Override
            public void onMenuModeChange(MenuBuilder menu) {
            }

        });

        mVoiceInputOrClearButton.setVisibility(mShowVoiceInput ? View.VISIBLE : View.INVISIBLE);
        mVoiceInputOrClearButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mSearchInput.getText().length() == 0) {

                    startVoiceInput();
                } else {

                    mSearchInput.setText("");
                }
            }
        });

        mSearchBarTitle.setVisibility(GONE);
        mSearchBarTitle.setTextColor(getResources().getColor(R.color.gray_active_icon));

        mSearchInput.addTextChangedListener(new TextWatcherAdapter() {

            public void onTextChanged(final CharSequence s, int start, int before, int count) {

                if (mSkipTextChangeEvent) {
                    mSkipTextChangeEvent = false;
                } else {

                    if (mSearchInput.getText().length() == 0) {

                        if (mShowVoiceInput)
                            changeIcon(mVoiceInputOrClearButton, mIconMic, true);
                        else
                            mVoiceInputOrClearButton.setVisibility(View.INVISIBLE);

                    } else if (mOldQuery.length() == 0) {
                        changeIcon(mVoiceInputOrClearButton, mIconClear, true);
                        mVoiceInputOrClearButton.setVisibility(View.VISIBLE);
                    }

                    if (mQueryListener != null && mIsFocused)
                        mQueryListener.onSearchTextChanged(mOldQuery, mSearchInput.getText().toString());

                    mOldQuery = mSearchInput.getText().toString();
                }

            }


        });

        mSearchInput.setOnFocusChangeListener(new TextView.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(mSkipQueryFocusChangeEvent){
                    mSkipQueryFocusChangeEvent = false;
                }else {

                    if (hasFocus != mIsFocused)
                        setSearchFocused(hasFocus);
                }
            }
        });

        mSearchInput.setOnKeyListener(new OnKeyListener() {

            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {

                if (mShowSearchKey && keyCode == KeyEvent.KEYCODE_ENTER) {

                    setSearchFocused(false);

                    if (mSearchListener != null)
                        mSearchListener.onSearchAction();

                    return true;
                }
                return false;
            }
        });

        refreshLeftIcon();

        mMenuSearchOrExitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mSearchInput.isFocused()) {

                    setSearchFocused(false);
                } else {

                    if (mShowMenuAction) {

                        toggleMenu();
                    } else {

                        setSearchFocused(true);
                    }
                }

            }
        });
    }

    private void refreshLeftIcon(){
        if(mShowMenuAction)
            mMenuSearchOrExitButton.setImageDrawable(mMenuBtnDrawable);
        else
            mMenuSearchOrExitButton.setImageDrawable(mIconSearch);
    }

    /**
     * Sets the menu button's color.
     *
     * @param color the color to be applied to the
     *              left menu button.
     */
    public void setLeftMenuIconColor(int color){

        mMenuBtnDrawable.setColor(color);
    }

    /**
     * Set if search is enabled.
     *
     * <p>When enabled, the search
     * input will gain focus when clicking on it and action
     * items that are associated with search only will become
     * visible.</p>
     *
     * @param enabled True to enable search
     */
    public void setSearchEnabled(boolean enabled){

        //todo avoid unnecessary work

        if(enabled)
            showSearchDependentActions();
        else
            hideSearchDependentActions();

        this.mSearchEnabled = enabled;
        mSearchInput.setEnabled(enabled);
    }

    private void showSearchDependentActions(){

        ViewCompat.animate(mSearchBarTitle).alpha(0.0f).setListener(new ViewPropertyAnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(View view) {
                mSearchBarTitle.setVisibility(INVISIBLE);
            }

        }).start();

        ViewCompat.animate(mSearchInput).alpha(1.0f).setListener(new ViewPropertyAnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(View view) {
                mSearchInput.setVisibility(VISIBLE);
            }

        }).start();

        ViewCompat.animate(mVoiceInputOrClearButton).alpha(1.0f).setListener(new ViewPropertyAnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(View view) {
                mVoiceInputOrClearButton.setVisibility(VISIBLE);
            }

        }).start();
    }

    private void hideSearchDependentActions(){

        ViewCompat.animate(mSearchBarTitle).alpha(1.0f).setListener(new ViewPropertyAnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(View view) {
                mSearchBarTitle.setVisibility(VISIBLE);
            }

        }).start();

        ViewCompat.animate(mSearchInput).alpha(0.0f).setListener(new ViewPropertyAnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(View view) {
                mSearchInput.setVisibility(INVISIBLE);
            }

        }).start();

        ViewCompat.animate(mVoiceInputOrClearButton).alpha(0.0f).setListener(new ViewPropertyAnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(View view) {
                mVoiceInputOrClearButton.setVisibility(INVISIBLE);
            }

        }).start();
    }

    /*
     * Sets the padding for the search input TextView. This
     * will set the available space for text before the TextView
     * needs to scroll to make space for the text.
     */
    private void adjustSearchInputPadding(){

        int newPaddingEnd = 0;

        newPaddingEnd += mVoiceInputOrClearButton.getWidth();

        if(mShowOverFlowMenu) {

            if(!(mHideOverflowMenuFocused && mSearchInput.isFocused()))
                newPaddingEnd += mOverflowMenu.getWidth();
        }

        mSearchInput.setPadding(0, 0, newPaddingEnd, 0);
        mSearchBarTitle.setPadding(0, 0, newPaddingEnd, 0);
    }

    /**
     * Mimics a menu click that opens the menu. Useful when for navigation
     * drawers when they open as a result of dragging.
     */
    public void openMenu(boolean withAnim) {

        openMenu(true, withAnim, false);
    }

    /**
     * Mimics a menu click that closes. Useful when fo navigation
     * drawers when they close as a result of selecting and item.
     *
     * @param withAnim true, will close the menu button with
     *                 the  Material animation
     */
    public void closeMenu(boolean withAnim) {

        closeMenu(true, withAnim, false);
    }

    /**
     * Set the visibility of the menu action
     * button.
     *
     * @param show true to make the menu button visible, false
     *             to make it invisible and place a search icon
     *             instead.
     */
    public void setLeftShowMenu(boolean show){

        mShowMenuAction = show;
        refreshLeftIcon();
    }

    /**
     * Sets whether the overflow menu icon
     * will slide off the search bar when search
     * gains focus.
     *
     * @param hide
     */
    public void setHideOverflowMenuWhenFocused(boolean hide){

        this.mHideOverflowMenuFocused = hide;
    }

    /**
     * Control whether the overflow menu is
     * present or not.
     *
     * @param show
     */
    public void setShowOverflowMenu(boolean show){

        this.mShowOverFlowMenu = show;

        if(mShowVoiceInput)
            if(show)showOverflowMenuWithAnim(false);
        else hideOverflowMenu(false);
    }

    private void showOverflowMenuWithAnim(boolean withAnim){

        mOverflowMenu.setClickable(true);

        if(withAnim) {
            ViewPropertyAnimatorCompatSet hidAnimSet = new ViewPropertyAnimatorCompatSet();
            hidAnimSet.playSequentially(ViewCompat.animate(mVoiceInputOrClearButton).translationX(0),
                    ViewCompat.animate(mOverflowMenu).alpha(1.0f)).setDuration(150).start();
        } else{

            mOverflowMenu.setAlpha(1.0f);
            mVoiceInputOrClearButton.setTranslationX(0);
        }
    }

    private void hideOverflowMenu(boolean withAnim){

        //this is needed because we're going to move
        //mVoiceInputOrClearButton right into the position of
        //mOverflowMenu, and we don't want to receive click events
        //from mOverflowMenu.
        mOverflowMenu.setClickable(false);

        //accounts for anim direction based on the language direction
        int deltaX = isRTL() ? -Util.dpToPx(OVERFLOW_ICON_WIDTH_DP)
                 : Util.dpToPx(OVERFLOW_ICON_WIDTH_DP);

        if(withAnim) {
            ViewPropertyAnimatorCompatSet hidAnimSet = new ViewPropertyAnimatorCompatSet();
            hidAnimSet.playSequentially(ViewCompat.animate(mOverflowMenu).alpha(0.0f),
                    ViewCompat.animate(mVoiceInputOrClearButton).translationXBy(deltaX)).setDuration(150).start();
        }else {

            mOverflowMenu.setAlpha(0.0f);
            mVoiceInputOrClearButton.setTranslationX(deltaX);
        }
    }

    /**
     * Inflates the menu items from
     * an xml resource.
     *
     * @param menuId a menu xml resource reference
     */
    public void inflateOverflowMenu(int menuId){
        mMenuBuilder.clearAll();
        getMenuInflater().inflate(menuId, mMenuBuilder);
    }

    private MenuInflater getMenuInflater() {
        if (mMenuInflater == null) {
            mMenuInflater = new SupportMenuInflater(getContext());
        }
        return mMenuInflater;
    }

    private void toggleMenu(){

        if(mMenuOpen){
            closeMenu(true, true, true);
        }else{
            openMenu(true, true, true);
        }
    }

    private void openMenu(boolean changeMenuIcon, boolean withAnim, boolean notifyListener){

        if (mOnMenuClickListener != null && notifyListener)
            mOnMenuClickListener.onMenuOpened();

        mMenuOpen = true;
        if (changeMenuIcon)
            openMenuDrawable(mMenuBtnDrawable, withAnim);
    }

    private void closeMenu(boolean changeMenuIcon, boolean withAnim, boolean notifyListener) {

        if (mOnMenuClickListener != null && notifyListener)
            mOnMenuClickListener.onMenuClosed();

        mMenuOpen = false;
        if(changeMenuIcon)
            closeMenuDrawable(mMenuBtnDrawable, withAnim);
    }

    /**
     * Enables clients to directly manipulate
     * the menu icon's progress.
     *
     * <p>Useful for custom animation/behaviors.</p>
     *
     * @param progress the desired progress of the menu
     *                 icon's rotation. 0.0 == hamburger
     *                 shape, 1.0 == back arrow shape
     */
    public void setMenuIconProgress(float progress){

        mMenuBtnDrawable.setProgress(progress);

        if(progress == 0)
            closeMenu(false, true, true);
        else if(progress == 1.0)
            openMenu(false);
    }

    /**
     * Set a hint that will appear in the
     * search input. Default hint is R.string.abc_search_hint
     * which is "search..." (when device language is set to english)
     *
     * @param searchHint
     */
    public void setSearchHint(String searchHint){

        mSearchHint = searchHint != null ? searchHint : getResources().getString(R.string.abc_search_hint);

        if(mShowHintNotFocused || mSearchInput.isFocused())
            mSearchInput.setHint(mSearchHint);
        else
            mSearchInput.setHint("");
    }

    /**
     * Control whether the hint will be shown
     * when the search is not focused.
     *
     * @param show true to show hint when search
     *             is inactive
     */
    public void setShowHintWhenNotFocused(boolean show){

        mShowHintNotFocused = show;

        if(mShowHintNotFocused)
            mSearchInput.setHint(mSearchHint);
    }

    /**
     * Sets the title for the search bar.
     *
     * <p>Note that this is the regular text, not the
     * hint. It won't have any effect if called when
     * mShowHintInactive is true.</p>
     *
     * @param title the title to be shown when search
     *              is not focused
     */
    public void setSearchBarTitle(CharSequence title){

        mSearchBarTitle.setText(title);
    }

    /**
     * Set the visibility of the voice recognition action
     * button.
     *
     * @param show true to make the voice button visible, false
     *             to make it invisible.
     */
    public void setShowVoiceInput(boolean show){

        mShowVoiceInput = show;
        mVoiceInputOrClearButton.setVisibility(mShowVoiceInput ? View.VISIBLE : View.GONE);
    }

    /**
     * Sets the request code with which the voice
     * recognition intent will be fired.
     *
     * <p>
     * The default request code is 1024. Clients should use
     * this methods to provide a unique request code if the
     * default code conflicts with one of their codes in order
     * to avoid undesired results.
     * </p>
     *
     * @param requestCode
     */
    public void setVoiceRecRequestCode(int requestCode){

        mVoiceRecRequestCode = requestCode;
    }

    private void startVoiceInput(){

        Intent voiceIntent = createVoiceRecIntent(mHostActivity, mVoiceRecHint);

        if(mHostActivity!=null)
            mHostActivity.startActivityForResult(voiceIntent, mVoiceRecRequestCode);
    }

    private Intent createVoiceRecIntent(Activity activity, String hint){

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, hint);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

        return intent;
    }

    /**
     * Sets the hit that will appear in the
     * voice recognition dialog.
     *
     * @param hint
     */
    public void setVoiceSearchHint(String hint){
        mVoiceRecHint = hint != null ? hint : getResources().getString(R.string.abc_search_hint);
    }

    /**
     * Handles voice recognition activity return.
     *
     * <p>In order for voice rec to work, this must be called from
     * the client activity's onActivityResult()</p>
     *
     * @param requestCode  the code with which the voice recognition intent
     *                     was started.
     * @param resultCode
     * @param data
     */
    public void onHostActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == mVoiceRecRequestCode){

            if(resultCode == Activity.RESULT_OK){

                ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (!matches.isEmpty()) {

                    String newQuery = matches.get(0);

                    mSearchInput.requestFocus();

                    mSearchInput.setText(newQuery);
                    mSearchInput.setSelection(mSearchInput.getText().length());
                }
            }
        }
    }

    /**
     * Sets whether the the button with the search icon
     * will appear in the soft-keyboard or not.
     *
     * <p>Notice that if this is set to false,
     * {@link OnSearchListener#onSearchAction()} onSearchAction}, will
     * not get called.</p>
     *
     * @param show to show the search button in
     *             the soft-keyboard.
     */
    public void setShowSearchKey(boolean show){
        mShowSearchKey = show;
        if(show)
            mSearchInput.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        else
            mSearchInput.setImeOptions(EditorInfo.IME_ACTION_NONE);
    }

    /**
     * Returns the current query text.
     *
     * @return the current query
     */
    public String getQuery(){

        return mSearchInput.getText().toString();
    }

    /**
     * Shows a circular progress on top of the
     * menu action button.
     *
     * <p>Call hidProgress()
     * to change back to normal and make the menu
     * action visible.</p>
     */
    public void showProgress(){

        mMenuSearchOrExitButton.setVisibility(View.GONE);
        mSearchProgress.setVisibility(View.VISIBLE);
        ObjectAnimator fadeInProgress = new ObjectAnimator().ofFloat(mSearchProgress, "alpha", 0.0f, 1.0f);
        fadeInProgress.start();
    }

    /**
     * Hides the progress bar after
     * a prior call to showProgress()
     */
    public void hideProgress() {

        mMenuSearchOrExitButton.setVisibility(View.VISIBLE);
        mSearchProgress.setVisibility(View.GONE);
        ObjectAnimator fadeInExit = new ObjectAnimator().ofFloat(mMenuSearchOrExitButton, "alpha", 0.0f, 1.0f);
        fadeInExit.start();
    }

    private void setSuggestionItemTextSize(int sizePx){

        this.mSuggestionsTextSizePx = sizePx;
        //setup adapter
    }

    private void setupSuggestionSection() {

        boolean showItemsFromBottom = true;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, showItemsFromBottom);
        mSuggestionsList.setLayoutManager(layoutManager);
        mSuggestionsList.setItemAnimator(null);

        final GestureDetector gestureDetector = new GestureDetector(getContext(),new GestureDetectorListenerAdapter(){

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

                if(mHostActivity!=null)
                  Util.closeSoftKeyboard(mHostActivity);

                return false;
            }
        });

        mSuggestionsList.addOnItemTouchListener(new OnItemTouchListenerAdapter() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                gestureDetector.onTouchEvent(e);
                return false;
            }
        });

        mSuggestionsAdapter = new SearchSuggestionsAdapter(getContext(), new SearchSuggestionsAdapter.Listener() {

            @Override
            public void onItemSelected(SearchSuggestion item) {

                setSearchFocused(false);

                if(mSearchListener!=null)
                    mSearchListener.onSuggestionClicked(item);
            }

            @Override
            public void onMoveItemToSearchClicked(SearchSuggestion item) {

                mSearchInput.setText(item.getBody());

                //move cursor to end of text
                mSearchInput.setSelection(mSearchInput.getText().length());
            }
        });

        mSuggestionsList.setAdapter(mSuggestionsAdapter);

        int cardViewBottomPadding = Util.dpToPx(5);

        //move up the suggestions section enough to cover the search bar
        //card's bottom left and right corners
        mSuggestionsSection.setTranslationY(-cardViewBottomPadding);

    }

    private void moveSuggestListToInitialPos(){

        //move the suggestions list to the collapsed position
        //which is translationY of -listHeight
        mSuggestionListContainer.setTranslationY(-mSuggestionListContainer.getMeasuredHeight());
    }

    /**
     * Clears the current suggestions and replaces it
     * with the provided list of new suggestions.
     *
     * @param newSearchSuggestions a list containing the new suggestions
     */
    public void swapSuggestions(final List<? extends SearchSuggestion> newSearchSuggestions){
        Collections.reverse(newSearchSuggestions);
        swapSuggestions(newSearchSuggestions, true);
    }

    private void swapSuggestions(final List<? extends SearchSuggestion> newSearchSuggestions, boolean withAnim){
        mDivider.setVisibility(View.GONE);
        //update adapter
        mSuggestionsAdapter.swapData(newSearchSuggestions);

        //todo inspect line
        //this is needed because the list gets populated
        //from bottom up.
        mSuggestionsList.scrollBy(0, -(newSearchSuggestions.size() * getTotalItemsHeight(newSearchSuggestions)));

        int fiveDp = Util.dpToPx(6);
        int threeDp = Util.dpToPx(3);
        ViewCompat.animate(mSuggestionListContainer).cancel();
        float translationY = (-mSuggestionListContainer.getHeight())+getVisibleItemsHeight(newSearchSuggestions);

        //todo refactor go over and make more clear
        final float newTranslationY = translationY<0 ?
                newSearchSuggestions.size()==0 ? translationY : translationY+threeDp
                : -fiveDp;

        if(withAnim) {
            ViewCompat.animate(mSuggestionListContainer).
                    setStartDelay(SUGGESTION_ITEM_ANIM_DURATION).
                    setInterpolator(SUGGEST_ITEM_ADD_ANIM_INTERPOLATOR).
                    setDuration(SUGGEST_ITEM_ADD_ANIM_DURATION).
                    translationY(newTranslationY).
                    setListener(new ViewPropertyAnimatorListenerAdapter() {


                        @Override
                        public void onAnimationCancel(View view) {

                            mSuggestionListContainer.setTranslationY(newTranslationY);
                        }
                    }).start();
        }else{

            //todo refactor
            //the extra -3*fiveDp is because this will only
            //get called from onRestoreSavedState(), and when it is called,
            //the full height of mSuggestionListContainer is not known.
            //*refactor* as soon as possible to eliminate confusion.
             float transY = translationY<0 ?
                     newSearchSuggestions.size()==0 ? translationY : translationY+threeDp-3*fiveDp
                    : -fiveDp;
            mSuggestionListContainer.setTranslationY(transY);
        }

        if(newSearchSuggestions.size()>0)
            mDivider.setVisibility(View.VISIBLE);
        else
            mDivider.setVisibility(View.GONE);
    }

    //returns the height that a given suggestion list's items
    //will take up.
    private int getVisibleItemsHeight(List<? extends SearchSuggestion> suggestions){

        int visibleItemsHeight = 0;

        for(SearchSuggestion suggestion: suggestions) {
            visibleItemsHeight += getSuggestionItemHeight(suggestion);

            //if the current total is more than the list container's height, we
            //don't care about the rest of the items' heights because they won't be
            //visible.
            if(visibleItemsHeight>mSuggestionListContainer.getHeight())
                break;
        }

        return visibleItemsHeight;
    }

    private int getTotalItemsHeight(List<? extends SearchSuggestion> suggestions){

        int totalItemHeight = 0;

        for(SearchSuggestion suggestion: suggestions)
            totalItemHeight += getSuggestionItemHeight(suggestion);

        return totalItemHeight;
    }

    //returns the height of a given suggestion item based on it's text length
    private int getSuggestionItemHeight(SearchSuggestion suggestion) {
        int leftRightMarginsWidth = Util.dpToPx(124);

        TextView textView = new TextView(getContext());
        textView.setTypeface(Typeface.DEFAULT);
        textView.setText(suggestion.getBody(), TextView.BufferType.SPANNABLE);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mSuggestionsTextSizePx);
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(mSuggestionsList.getWidth()-leftRightMarginsWidth, View.MeasureSpec.AT_MOST);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        textView.measure(widthMeasureSpec, heightMeasureSpec);
        int heightPlusPadding = textView.getMeasuredHeight()+Util.dpToPx(8);
        int minHeight = Util.dpToPx(48);
        int height = heightPlusPadding >= minHeight ? heightPlusPadding : minHeight;
        return heightPlusPadding >= minHeight ? heightPlusPadding : minHeight;
    }

    /**
     * Collapses the suggestions list and
     * then clears its suggestion items.
     */
    public void clearSuggestions(){
        clearSuggestions(null);
    }

    private interface OnSuggestionsClearListener{

        void onCleared();
    }

    private void clearSuggestions(final OnSuggestionsClearListener listener) {

        if(!mIsCollapsing) {

            collapseSuggestionsSection(new OnSuggestionsCollapsedListener() {
                @Override
                public void onCollapsed() {

                    mSuggestionsAdapter.clearDataSet();

                    if (listener != null)
                        listener.onCleared();

                    mDivider.setVisibility(GONE);
                }
            });
        }
    }

    private interface OnSuggestionsCollapsedListener{

        void onCollapsed();
    }

    private void collapseSuggestionsSection(final OnSuggestionsCollapsedListener listener){

        mIsCollapsing = true;

        final int destTranslationY = -(mSuggestionListContainer.getHeight()+Util.dpToPx(3));

        ViewCompat.animate(mSuggestionListContainer).
                translationY(destTranslationY).
                setDuration(SUGGEST_LIST_COLLAPSE_ANIM_DURATION).
                setListener(new ViewPropertyAnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(View view) {

                        if (listener != null)
                            listener.onCollapsed();

                        mIsCollapsing = false;
                    }

                    @Override
                    public void onAnimationCancel(View view) {

                        mSuggestionListContainer.setTranslationY(destTranslationY);
                    }
                }).start();
    }

    public void clearSearchFocus(){
        setSearchFocused(false);
    }

    public boolean isSearchBarFocused(){
        return mIsFocused;
    }

    private void setSearchFocused(boolean focused){

        this.mIsFocused = focused;

        if(focused){

            if(mShowMenuAction && !mMenuOpen)
                openMenuDrawable(mMenuBtnDrawable, true);
            else if(!mShowMenuAction)
                changeIcon(mMenuSearchOrExitButton, mIconBackArrow, true);

            if(mMenuOpen)
                closeMenu(false, true, true);

            moveSuggestListToInitialPos();
            mSuggestionsSection.setVisibility(VISIBLE);

            fadeInBackground();

            mSearchInput.requestFocus();

            if(mShowOverFlowMenu && mHideOverflowMenuFocused)
                hideOverflowMenu(true);

            adjustSearchInputPadding();

            Util.showSoftKeyboard(getContext(), mSearchInput);

            if(mFocusChangeListener!=null)
                mFocusChangeListener.onFocus();
        }else{

            if(mShowMenuAction)
                closeMenuDrawable(mMenuBtnDrawable, true);
            else
                changeIcon(mMenuSearchOrExitButton, mIconSearch, true);

            clearSuggestions(new OnSuggestionsClearListener() {
                @Override
                public void onCleared() {

                    mSuggestionsSection.setVisibility(View.INVISIBLE);
                }
            });

            fadeOutBackground();

            findViewById(R.id.search_bar).requestFocus();

            if(mHostActivity!=null)
               Util.closeSoftKeyboard(mHostActivity);

            if(mShowVoiceInput && !mHideOverflowMenuFocused)
                changeIcon(mVoiceInputOrClearButton, mIconMic, true);

            if(mSearchInput.length()!=0)
                mSearchInput.setText("");

            if(mShowOverFlowMenu && mHideOverflowMenuFocused)
                showOverflowMenuWithAnim(true);

            adjustSearchInputPadding();

            if(mFocusChangeListener!=null) {
                mFocusChangeListener.onFocusCleared();
            }
        }
    }

    private void changeIcon(ImageView imageView, Drawable newIcon, boolean withAnim) {

        imageView.setImageDrawable(newIcon);
        if(withAnim) {
            ObjectAnimator fadeInVoiceInputOrClear = new ObjectAnimator().ofFloat(imageView, "alpha", 0.0f, 1.0f);
            fadeInVoiceInputOrClear.start();
        }else{
            imageView.setAlpha(1.0f);
        }
    }

    /**
     * Sets the listener that will listen for query
     * changes as they are being typed.
     *
     * @param listener listener for query changes
     */
    public void setOnQueryChangeListener(OnQueryChangeListener listener){
        this.mQueryListener = listener;
    }

    /**
     * Sets the listener that will be called when
     * an action that completes the current search
     * session has occurred and the search lost focus.
     *
     * <p>When called, a client would ideally grab the
     * search or suggestion query from the callback parameter or
     * from {@link #getQuery() getquery} and perform the necessary
     * query against its data source.</p>
     *
     * @param listener listener for query completion
     */
    public void setOnSearchListener(OnSearchListener listener) {
        this.mSearchListener = listener;
    }

    /**
     * Sets the listener that will be called when the focus
     * of the search has changed.
     *
     * @param listener listener for search focus changes
     */
    public void setOnFocusChangeListener(OnFocusChangeListener listener){
        this.mFocusChangeListener = listener;
    }

    /**
     * Sets the listener that will be called when the
     * left/start menu (or navigation menu) is clicked.
     *
     * <p>Note that this is different from the overflow menu
     * that has a separate listener.</p>
     *
     * @param listener
     */
    public void setOnLeftMenuClickListener(OnLeftMenuClickListener listener){
        this.mOnMenuClickListener = listener;
    }

    /**
     * Sets the listener that will be called when
     * an item in the overflow menu is clicked.
     *
     * @param listener listener to listen to menu item clicks
     */
    public void setOnMenuItemClickListener(OnMenuItemClickListener listener){
        this.mOnOverflowMenuItemListener = listener;
    }

    private void openMenuDrawable(final DrawerArrowDrawable drawerArrowDrawable, boolean withAnim){

        if(withAnim){
            ValueAnimator anim = ValueAnimator.ofFloat(0.0f, 1.0f);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {

                    float value = (Float) animation.getAnimatedValue();
                    drawerArrowDrawable.setProgress(value);
                }
            });
            anim.setDuration(MENU_ICON_ANIM_DURATION);
            anim.start();
        }else{
            drawerArrowDrawable.setProgress(1.0f);
        }
    }

    private void closeMenuDrawable(final DrawerArrowDrawable drawerArrowDrawable, boolean withAnim) {

        if(withAnim) {
            ValueAnimator anim = ValueAnimator.ofFloat(1.0f, 0.0f);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {

                    float value = (Float) animation.getAnimatedValue();
                    drawerArrowDrawable.setProgress(value);
                }
            });
            anim.setDuration(MENU_ICON_ANIM_DURATION);
            anim.start();
        }else{
            drawerArrowDrawable.setProgress(0.0f);
        }
    }

    private void fadeOutBackground(){

        ValueAnimator anim = ValueAnimator.ofInt(BACKGROUND_DRAWABLE_ALPHA_SEARCH_ACTIVE, BACKGROUND_DRAWABLE_ALPHA_SEARCH_INACTIVE);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                int value = (Integer) animation.getAnimatedValue();
                mBackgroundDrawable.setAlpha(value);
            }
        });
        anim.setDuration(BACKGROUND_FADE__ANIM_DURATION);
        anim.start();
    }

    private void fadeInBackground(){

        ValueAnimator anim = ValueAnimator.ofInt(BACKGROUND_DRAWABLE_ALPHA_SEARCH_INACTIVE, BACKGROUND_DRAWABLE_ALPHA_SEARCH_ACTIVE);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                int value = (Integer) animation.getAnimatedValue();
                mBackgroundDrawable.setAlpha(value);
            }
        });
        anim.setDuration(BACKGROUND_FADE__ANIM_DURATION);
        anim.start();
    }

    /**
     * Set whether a touch outside of the
     * search bar's bounds will cause the search bar to
     * loos focus.
     *
     * @param enable true to dismiss on outside touch, false otherwise.
     */
    public void setDismissOnOutsideClick(boolean enable){

        mDismissOnOutsideTouch = enable;

        mSuggestionsSection.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //todo check if this is called twice
                if (mDismissOnOutsideTouch && mIsFocused)
                    setSearchFocused(false);

                return true;
            }
        });
    }

    private boolean isRTL(){

        Configuration config =  getResources().getConfiguration();
        return ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState, mIsFocused, mSuggestionsAdapter.getDataSet(), getQuery());
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        final SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());

        if(savedState.isFocused) {

            mBackgroundDrawable.setAlpha(BACKGROUND_DRAWABLE_ALPHA_SEARCH_ACTIVE);
            mSkipTextChangeEvent = savedState.isFocused;
            mSkipQueryFocusChangeEvent = savedState.isFocused;
            mIsFocused = savedState.isFocused;

            mSuggestionsSection.setVisibility(VISIBLE);

            ViewTreeObserver vto = mSuggestionListContainer.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT < 16) {
                        mSuggestionListContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        mSuggestionListContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }

                    swapSuggestions(savedState.suggestions, false);
                }
            });

            if (savedState.mQuery.length() == 0) {

                if (mShowVoiceInput)
                    changeIcon(mVoiceInputOrClearButton, mIconMic, false);
                else
                    mVoiceInputOrClearButton.setVisibility(View.INVISIBLE);

            } else if (mOldQuery.length() == 0) {
                changeIcon(mVoiceInputOrClearButton, mIconClear, false);
                mVoiceInputOrClearButton.setVisibility(View.VISIBLE);
            }

            if(mShowOverFlowMenu && mHideOverflowMenuFocused)
                hideOverflowMenu(false);

            if(mShowMenuAction && !mMenuOpen)
                openMenuDrawable(mMenuBtnDrawable, false);
            else if(!mShowMenuAction)
                changeIcon(mMenuSearchOrExitButton, mIconBackArrow, false);

            adjustSearchInputPadding();

            Util.showSoftKeyboard(getContext(), mSearchInput);
        }
    }

    static class SavedState extends BaseSavedState {

        List<? extends SearchSuggestion> suggestions = new ArrayList<>();
        Creator SUGGEST_CREATOR;

        boolean isFocused;

        String mQuery;

        SavedState(Parcelable superState, boolean isFocused, List<? extends SearchSuggestion> suggestions, String query){
            super(superState);
            this.isFocused = isFocused;
            this.suggestions = suggestions;

            if(!suggestions.isEmpty())
               SUGGEST_CREATOR = suggestions.get(0).getCreator();

            this.mQuery = query;
        }

        private SavedState(Parcel in) {
            super(in);
            isFocused = (in.readInt() != 0);

            if(SUGGEST_CREATOR!=null)
               in.readTypedList(suggestions, SUGGEST_CREATOR);

            mQuery = in.readString();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(isFocused ? 1 : 0);
            out.writeTypedList(suggestions);
            out.writeString(mQuery);
        }

        public static final Creator<SavedState> CREATOR
                = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        //remove any ongoing animations to prevent leaks
        ViewCompat.animate(mSuggestionListContainer).cancel();
    }
}
