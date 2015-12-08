package com.arlib.floatingsearchviewdemo.data;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Parcel;
import android.widget.ImageView;
import android.widget.TextView;

import com.arlib.floatingsearchview.R;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

/**
 * Copyright (c) 2015 Ari C
 * <p/>
 * ColorSuggestion ---
 * <p/>
 * Author Ari
 * Created on 10/19/2015.
 */
public  class ColorSuggestion implements SearchSuggestion {

    private ColorWrapper mColor;

    private String mColorName;

    private boolean mIsHistory;

    public ColorSuggestion(ColorWrapper color){

        this.mColor = color;
        this.mColorName = mColor.getName();
    }

    public ColorSuggestion(Parcel source) {
        this.mColorName = source.readString();
    }

    public ColorWrapper getColor(){
        return mColor;
    }

    public void setIsHistory(boolean isHistory){
        this.mIsHistory = isHistory;
    }

    public boolean getIsHistory(){return this.mIsHistory;}

    @Override
    public String getBody() {
        return mColor.getName();
    }

    @Override
    public Creator getCreator() {
        return CREATOR;
    }

    ///////

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
    }
}