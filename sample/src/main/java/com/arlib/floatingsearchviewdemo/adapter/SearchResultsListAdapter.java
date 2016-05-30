package com.arlib.floatingsearchviewdemo.adapter;

import android.graphics.Color;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arlib.floatingsearchviewdemo.R;
import com.arlib.floatingsearchviewdemo.data.ColorWrapper;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsListAdapter  extends RecyclerView.Adapter<SearchResultsListAdapter.ViewHolder>  {

    private List<ColorWrapper> mDataSet = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mColorName;
        public final TextView mColorValue;
        public final View mTextContainer;

        public ViewHolder(View view) {
            super(view);
            mColorName = (TextView) view.findViewById(R.id.color_name);
            mColorValue = (TextView) view.findViewById(R.id.color_value);
            mTextContainer = view.findViewById(R.id.text_container);
        }
    }

    public void swapData(List<ColorWrapper> mNewDataSet){
        mDataSet = mNewDataSet;
        Log.d("dcscds", mNewDataSet.size()+"");

        notifyDataSetChanged();
    }

    @Override
    public SearchResultsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_results_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchResultsListAdapter.ViewHolder holder, int position) {

        ColorWrapper colorSuggestion = mDataSet.get(position);
        holder.mColorName.setText(colorSuggestion.getName());
        holder.mColorValue.setText(colorSuggestion.getHex());

        int color = Color.parseColor(colorSuggestion.getHex());
        Palette.Swatch swatch = new Palette.Swatch(color, 0);
        holder.mTextContainer.setBackgroundColor(color);
        holder.mColorName.setTextColor(swatch.getTitleTextColor());
        holder.mColorValue.setTextColor(swatch.getBodyTextColor());
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
