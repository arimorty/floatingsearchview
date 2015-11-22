/*
 * Copyright (C) 2015 Ari C
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

package com.arlib.floatingsearchview.suggestions.model;

import android.os.Parcelable;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * An object that represents a single suggestion item
 * in the suggestions drop down generated in response
 * to an entered query in the {@link com.arlib.floatingsearchview.FloatingSearchView}
 */
public interface SearchSuggestion extends Parcelable{

    /**
     * Returns the text that should be displayed
     * for the suggestion represented by this object.
     *
     * @return the text for this suggestion
     */
    String getBody();

    /**
     * Sets the TextView that holds the suggestion text.
     *
     * <p>This allows for applying special properties to the
     * TextView based on the client's needs.</p>
     *
     * @param textView
     */
    void setBodyText(TextView textView);

    /**
     * Sets the ImageView that will holds the icon that will
     * be displayed to the left/start of the suggestion text.
     *
     * <p>This allows clients the freedom to choose their desired
     * image loading library and customize the way the icon's look</p>      *
     *
     * @param imageView The imageView that holds the icon
     * @return True, if the ImageView was set and the icon should
     *         be displayed. Returning false will cause the icon to
     *         be invisible.
     */
    boolean setLeftIcon(ImageView imageView);

    /**
     * Returns a creator object that will be used
     * for saving state.
     *
     * <p>Classes that implement this object have
     * the responsibility to include getBody() value
     * in their Parcelable implementation. Failure to
     * do so will result in empty suggestion items after
     * a configuration change</p>
     *
     * @return a {@link Creator Creator} that
     *         will be used to save state.
     */
    Creator getCreator();

}
