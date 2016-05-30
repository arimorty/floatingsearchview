package com.arlib.floatingsearchviewdemo.data;

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

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ColorWrapper implements Parcelable {

    @SerializedName("hex")
    @Expose
    private String hex;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("rgb")
    @Expose
    private String rgb;

    private ColorWrapper(Parcel in) {
        hex = in.readString();
        name = in.readString();
        rgb = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(hex);
        dest.writeString(name);
        dest.writeString(rgb);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     *
     * @return
     * The hex
     */
    public String getHex() {
        return hex;
    }

    /**
     *
     * @param hex
     * The hex
     */
    public void setHex(String hex) {
        this.hex = hex;
    }

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The rgb
     */
    public String getRgb() {
        return rgb;
    }

    /**
     *
     * @param rgb
     * The rgb
     */
    public void setRgb(String rgb) {
        this.rgb = rgb;
    }

    public static final Creator<ColorWrapper> CREATOR = new Creator<ColorWrapper>() {
        @Override
        public ColorWrapper createFromParcel(Parcel in) {
            return new ColorWrapper(in);
        }

        @Override
        public ColorWrapper[] newArray(int size) {
            return new ColorWrapper[size];
        }
    };
}