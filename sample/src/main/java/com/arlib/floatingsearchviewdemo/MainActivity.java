package com.arlib.floatingsearchviewdemo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
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

                    mSearchView.showProgress();
                    DataHelper.find(MainActivity.this, newQuery, new DataHelper.OnFindResultsListener() {

                        @Override
                        public void onResults(List<ColorSuggestion> results) {

                            mSearchView.swapSuggestions(results);
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

                mSearchView.swapSuggestions(DataHelper.getHistory(MainActivity.this, 3));
            }

            @Override
            public void onFocusCleared() {

                Log.d(TAG, "onFocusCleared()");
            }
        });

        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemSelected(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.action_show_menu:
                        mSearchView.setLeftShowMenu(true);
                        break;
                    case R.id.action_hide_menu:
                        mSearchView.setLeftShowMenu(false);
                        break;
                }
            }
        });

        mSearchView.setOnLeftMenuClickListener(new FloatingSearchView.OnLeftMenuClickListener() {
            @Override
            public void onMenuOpened() {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }

            @Override
            public void onMenuClosed() {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mSearchView.onHostActivityResult(requestCode, resultCode, data);
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
