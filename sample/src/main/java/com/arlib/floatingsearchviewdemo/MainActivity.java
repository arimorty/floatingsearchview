package com.arlib.floatingsearchviewdemo;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.arlib.floatingsearchview.util.view.BodyTextView;
import com.arlib.floatingsearchview.util.view.IconImageView;
import com.arlib.floatingsearchviewdemo.data.ColorSuggestion;
import com.arlib.floatingsearchviewdemo.data.DataHelper;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    private FloatingSearchView mSearchView;

    private ViewGroup mParentView;
    private TextView mColorNameText;
    private TextView mColorValueText;

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mParentView = (ViewGroup)findViewById(R.id.parent_view);

        mSearchView = (FloatingSearchView)findViewById(R.id.floating_search_view);
        mColorNameText = (TextView)findViewById(R.id.color_name_text);
        mColorValueText = (TextView)findViewById(R.id.color_value_text);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        //sets the background color
        refreshBackgroundColor("Blue", "#1976D2");

        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                if (!oldQuery.equals("") && newQuery.equals("")) {
                    mSearchView.clearSuggestions();
                } else {

                    //this shows the top left circular progress
                    //you can call it where ever you want, but
                    //it makes sense to do it when loading something in
                    //the background.
                    mSearchView.showProgress();

                    //simulates a query call to a data source
                    //with a new query.
                    DataHelper.find(MainActivity.this, newQuery, new DataHelper.OnFindResultsListener() {

                        @Override
                        public void onResults(List<ColorSuggestion> results) {

                            //this will swap the data and
                            //render the collapse/expand animations as necessary
                            mSearchView.swapSuggestions(results);

                            //let the users know that the background
                            //process has completed
                            mSearchView.hideProgress();
                        }
                    });
                }

                Log.d(TAG, "onSearchTextChanged()");
            }
        });

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {

                ColorSuggestion colorSuggestion = (ColorSuggestion) searchSuggestion;
                refreshBackgroundColor(colorSuggestion.getColor().getName(), colorSuggestion.getColor().getHex());

                Log.d(TAG, "onSuggestionClicked()");

            }

            @Override
            public void onSearchAction() {

                Log.d(TAG, "onSearchAction()");
            }
        });

        mSearchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {

                //show suggestions when search bar gains focus (typically history suggestions)
                mSearchView.swapSuggestions(DataHelper.getHistory(MainActivity.this, 3));

                Log.d(TAG, "onFocus()");
            }

            @Override
            public void onFocusCleared() {

                Log.d(TAG, "onFocusCleared()");
            }
        });

        //handle menu clicks the same way as you would
        //in a regular activity
        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {

                if (item.getItemId() == R.id.action_change_colors) {

                    //demonstrate setting colors for items
                    mSearchView.setBackgroundColor(Color.parseColor("#ECE7D5"));
                    mSearchView.setViewTextColor(Color.parseColor("#657A81"));
                    mSearchView.setHintTextColor(Color.parseColor("#596D73"));
                    mSearchView.setActionMenuOverflowColor(Color.parseColor("#B58900"));
                    mSearchView.setMenuItemIconColor(Color.parseColor("#2AA198"));
                    mSearchView.setLeftActionIconColor(Color.parseColor("#657A81"));
                    mSearchView.setClearBtnColor(Color.parseColor("#D30102"));
                    mSearchView.setSuggestionRightIconColor(Color.parseColor("#BCADAD"));
                    mSearchView.setDividerColor(Color.parseColor("#dfd7b9"));

                } else {

                    //just print action
                    Toast.makeText(getApplicationContext(), item.getTitle(),
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

        //use this listener to listen to menu clicks when app:floatingSearch_leftAction="showHamburger"
        mSearchView.setOnLeftMenuClickListener(new FloatingSearchView.OnLeftMenuClickListener() {
            @Override
            public void onMenuOpened() {
                Log.d(TAG, "onMenuOpened()");

                mDrawerLayout.openDrawer(GravityCompat.START);
            }

            @Override
            public void onMenuClosed() {
                Log.d(TAG, "onMenuClosed()");

                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        //use this listener to listen to menu clicks when app:floatingSearch_leftAction="showHome"
        mSearchView.setOnHomeActionClickListener(new FloatingSearchView.OnHomeActionClickListener() {
            @Override
            public void onHomeClicked() {

                Log.d(TAG, "onHomeClicked()");
            }
        });

        /*
         * Here you have access to the left icon and the text of a given suggestion
         * item when as it is bound to the suggestion list. You can utilize this
         * callback to change some properties of the left icon and the text. For example, you
         * can load left icon images using your favorite image loading library, or change text color.
         *
         * Some restrictions:
         * 1. You can modify the height, eidth, margin, or padding of the text and left icon.
         * 2. You can't modify the text's size.
         *
         * Modifications to these properties will be ignored silently.
         */
        mSearchView.setOnBindSuggestionCallback(new SearchSuggestionsAdapter.OnBindSuggestionCallback() {
            @Override
            public void onBindSuggestion(IconImageView leftIcon, BodyTextView bodyText, SearchSuggestion item, int itemPosition) {

                ColorSuggestion colorSuggestion = (ColorSuggestion) item;

                if (colorSuggestion.getIsHistory()) {
                    leftIcon.setImageDrawable(leftIcon.getResources().getDrawable(R.drawable.ic_history_black_24dp));
                    leftIcon.setAlpha(.36f);
                } else
                    leftIcon.setImageDrawable(new ColorDrawable(Color.parseColor(colorSuggestion.getColor().getHex())));
            }

        });

        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {

                //since the drawer might have opened as a results of
                //a click on the left menu, we need to make sure
                //to close it right after the drawer opens, so that
                //it is closed when the drawer is  closed.
                mSearchView.closeMenu(false);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
    }

    private void refreshBackgroundColor(String colorName, String colorValue){

        int color = Color.parseColor(colorValue);
        Palette.Swatch swatch = new Palette.Swatch(color, 0);

        mColorNameText.setTextColor(swatch.getTitleTextColor());
        mColorNameText.setText(colorName);

        mColorValueText.setTextColor(swatch.getBodyTextColor());
        mColorValueText.setText(colorValue);

        mParentView.setBackgroundColor(color);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(getDarkerColor(color, .8f));

    }

    private static int getDarkerColor(int color, float factor) {

        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        return Color.argb(a, Math.max((int)(r * factor), 0), Math.max((int)(g * factor), 0),
                Math.max((int)(b * factor), 0));
    }

}
