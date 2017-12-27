package com.arlib.floatingsearchview.suggestions;

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

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arlib.floatingsearchview.R;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.arlib.floatingsearchview.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchSuggestionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_ITEM = 0;
    private static final String TAG = "SearchSuggestionsAdapter";

    private List<? extends SearchSuggestion> mSearchSuggestions = new ArrayList<>();

    private Listener mListener;

    private Drawable mRightIconDrawable;
    private boolean mShowRightMoveUpBtn = false;
    private int mBodyTextSizePx;
    private @ColorInt int mTextColor = -1;
    private @ColorInt int mRightIconColor = -1;

    public interface OnBindSuggestionCallback {

        void onBindSuggestion(View suggestionView, ImageView leftIcon, TextView textView,
                              SearchSuggestion item, int itemPosition);
    }

    private OnBindSuggestionCallback mOnBindSuggestionCallback;

    public interface Listener {

        void onItemSelected(SearchSuggestion item);

        void onMoveItemToSearchClicked(SearchSuggestion item);
    }

    public static class SearchSuggestionViewHolder extends RecyclerView.ViewHolder {

        public TextView body;
        public ImageView leftIcon;
        public ImageView rightIcon;

        private Listener mListener;

        public interface Listener {

            void onItemClicked(int adapterPosition);

            void onMoveItemToSearchClicked(int adapterPosition);
        }

        public SearchSuggestionViewHolder(View v, Listener listener) {
            super(v);

            mListener = listener;
            body = (TextView) v.findViewById(R.id.body);
            leftIcon = (ImageView) v.findViewById(R.id.left_icon);
            rightIcon = (ImageView) v.findViewById(R.id.right_icon);

            rightIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int adapterPosition = getAdapterPosition();
                    if (mListener != null && adapterPosition != RecyclerView.NO_POSITION) {
                        mListener.onMoveItemToSearchClicked(getAdapterPosition());
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int adapterPosition = getAdapterPosition();
                    if (mListener != null && adapterPosition != RecyclerView.NO_POSITION) {
                        mListener.onItemClicked(adapterPosition);
                    }
                }
            });
        }
    }

    public SearchSuggestionsAdapter(Context context, int suggestionTextSize, Listener listener) {
        this.mListener = listener;
        this.mBodyTextSizePx = suggestionTextSize;

        mRightIconDrawable = Util.getWrappedDrawable(context, R.drawable.ic_arrow_back_black_24dp);
        DrawableCompat.setTint(mRightIconDrawable, Util.getColor(context, R.color.gray_active_icon));
    }

    public void swapData(List<? extends SearchSuggestion> searchSuggestions) {
        mSearchSuggestions = searchSuggestions;
        notifyDataSetChanged();
    }

    public List<? extends SearchSuggestion> getDataSet() {
        return mSearchSuggestions;
    }

    public void setOnBindSuggestionCallback(OnBindSuggestionCallback callback) {
        this.mOnBindSuggestionCallback = callback;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_ITEM :
                final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_suggestion_item, parent, false);
                final SearchSuggestionViewHolder holder = new SearchSuggestionViewHolder(view,
                        new SearchSuggestionViewHolder.Listener() {
                            @Override
                            public void onItemClicked(int adapterPosition) {
                                if (mListener != null) {
                                    mListener.onItemSelected(mSearchSuggestions.get(adapterPosition));
                                }
                            }

                            @Override
                            public void onMoveItemToSearchClicked(int adapterPosition) {
                                if (mListener != null) {
                                    mListener.onMoveItemToSearchClicked(mSearchSuggestions
                                            .get(adapterPosition));
                                }
                            }
                        });

                holder.rightIcon.setImageDrawable(mRightIconDrawable);
                holder.body.setTextSize(TypedValue.COMPLEX_UNIT_PX, mBodyTextSizePx);
                return holder;

            default :
                throw new UnsupportedOperationException("Unsupported viewType : " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final int viewType = getItemViewType(position);
        if (viewType == VIEW_TYPE_ITEM) {
            onBindSuggestionViewHolder((SearchSuggestionViewHolder)holder, position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return mSearchSuggestions != null ? mSearchSuggestions.size() : 0;
    }

    private void onBindSuggestionViewHolder(@NonNull SearchSuggestionViewHolder holder, int position) {
        setRightIconStatus(holder);
        setRightIconColor(holder);
        setBodyTextColor(holder);

        final SearchSuggestion suggestion = mSearchSuggestions.get(position);
        if (suggestion != null) {
            setBodyText(holder, suggestion);
            setSuggestionCallback(holder, suggestion, position);
        }
    }

    private void setRightIconStatus(@NonNull SearchSuggestionViewHolder holder) {
        if (mShowRightMoveUpBtn) {
            holder.rightIcon.setEnabled(true);
            holder.rightIcon.setVisibility(View.VISIBLE);
        } else {
            holder.rightIcon.setEnabled(false);
            holder.rightIcon.setVisibility(View.INVISIBLE);
        }
    }

    private void setRightIconColor(@NonNull SearchSuggestionViewHolder holder) {
        if (mRightIconColor != -1) {
            Util.setIconColor(holder.rightIcon, mRightIconColor);
        }
    }

    private void setBodyTextColor(@NonNull SearchSuggestionViewHolder holder) {
        if (mTextColor != -1) {
            holder.body.setTextColor(mTextColor);
        }
    }

    private void setBodyText(@NonNull SearchSuggestionViewHolder holder, @NonNull SearchSuggestion suggestion) {
        if (TextUtils.isEmpty(suggestion.getBody())) {
            holder.body.setText("");
        } else {
            holder.body.setText(suggestion.getBody());
        }
    }

    private void setSuggestionCallback(@NonNull SearchSuggestionViewHolder holder, @NonNull SearchSuggestion suggestion, int position) {
        if (mOnBindSuggestionCallback != null) {
            mOnBindSuggestionCallback.onBindSuggestion(holder.itemView, holder.leftIcon, holder.body, suggestion, position);
        }
    }

    public void setTextColor(@ColorInt int color) {
        if (mTextColor == color) {
            return;
        }

        mTextColor = color;
        notifyDataSetChanged();
    }

    public void setRightIconColor(@ColorInt int color) {
        if (mRightIconColor == color) {
            return;
        }

        mRightIconColor = color;
        notifyDataSetChanged();
    }

    public void setShowMoveUpIcon(boolean show) {
        if (mShowRightMoveUpBtn == show) {
            return;
        }

        mShowRightMoveUpBtn = show;
        notifyDataSetChanged();
    }

    public void reverseList() {
        Collections.reverse(mSearchSuggestions);
        notifyDataSetChanged();
    }
}
