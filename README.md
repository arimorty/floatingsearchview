Floating Search View
=============

An implementation of a floating search box with search suggestions.

![Alt text](/images/inaction.gif)

Usage
-----

1. Add a [FloatingSearchView](/library/src/main/java/com/arlib/floatingsearchview/FloatingSearchView)FloatingSearchView to your view hierarchy, and make sure that it takes
   up the full width and height of the screen.
2. Listen to query changes and provide suggestion items that implement [SearchSuggestion](/library/src/main/java/com/arlib/floatingsearchview/suggestions/model/SearchSuggestion).

 Example:
 -------------

```xml
    <com.arlib.floatingsearchview.FloatingSearchView
               android:id="@+id/floating_search_view"
               android:layout_width="match_parent"
               android:layout_height="match_parent"
               app:floatingSearch_searchBarWidth="350dp"
               app:floatingSearch_searchBarMarginLeft="@dimen/search_view_inset"
               app:floatingSearch_searchBarMarginTop="@dimen/search_view_inset"
               app:floatingSearch_searchBarMarginRight="@dimen/search_view_inset"
               app:floatingSearch_showMenuAction="true"
               app:floatingSearch_searchHint="Search..."
               app:floatingSearch_voiceRecHint="Say something..."
               app:floatingSearch_showSearchHintWhenNotFocused="true"
               app:floatingSearch_showVoiceInput="true"
               app:floatingSearch_showOverFlowMenu="true"
               app:floatingSearch_hideOverflowMenuWhenFocused="true"
               app:floatingSearch_showSearchKey="false"
               app:floatingSearch_dismissOnOutsideTouch="true"
               app:floatingSearch_menu="@menu/menu_main"/>
```

```
  mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
              @Override
              public void onSearchTextChanged(String oldQuery, final String newQuery) {

                  //get suggestions based on newQuery

                  //pass them on to the search view
                  mSearchView.swapSuggestions(newSuggestions);
              }
          });
```

To enable voice recognition
-------------

In your xml, add
```xml
   app:floatingSearch_showVoiceInput="true"
```

In your Activity, add this single line in ```onActivityResult(int requestCode, int resultCode, Intent data)```
```
   mSearchView.onHostActivityResult(requestCode, resultCode, data);
```
Contributing
============

At this point we want to focus on stability and efficiency before adding new features. All suggestions
or bug reports are welcome.

License
=======

    Copyright (C) 2015 Arlib

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.