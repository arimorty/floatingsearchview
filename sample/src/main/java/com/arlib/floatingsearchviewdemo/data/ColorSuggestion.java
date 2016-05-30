package com.arlib.floatingsearchviewdemo.data;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

/**
 * Copyright (c) 2015 Ari C
 * <p/>
 * ColorSuggestion ---
 * <p/>
 * Author Ari
 * Created on 10/19/2015.
 */
public class ColorSuggestion implements SearchSuggestion {

    private String mColorName;
    private boolean mIsHistory = false;

    public ColorSuggestion(String suggestion) {
        this.mColorName = suggestion;
    }

    public ColorSuggestion(Parcel source) {
        this.mColorName = source.readString();
        this.mIsHistory = source.readInt() != 0;
    }

    public void setIsHistory(boolean isHistory) {
        this.mIsHistory = isHistory;
    }

    public boolean getIsHistory() {
        return this.mIsHistory;
    }

    @Override
    public String getBody() {
        return mColorName;
    }

    public static final Creator<ColorSuggestion> CREATOR = new Creator<ColorSuggestion>() {
        @Override
        public ColorSuggestion createFromParcel(Parcel in) {
            return new ColorSuggestion(in);
        }

        @Override
        public ColorSuggestion[] newArray(int size) {
            return new ColorSuggestion[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mColorName);
        dest.writeInt(mIsHistory ? 1 : 0);
    }
}