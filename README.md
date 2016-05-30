Floating Search View [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Floating%20Search%20View-green.svg?style=true)](https://android-arsenal.com/details/1/2842)
=============

An implementation of a floating search box with search suggestions.

![Alt text](/images/150696.gif)
![Alt text](/images/1506tq.gif)
![Alt text](/images/1508kn.gif)

Usage
-----

1. In your dependencies, add
    ```
         compile 'com.github.arimorty:floatingsearchview:2.0.0'
    ```
2. Add a FloatingSearchView to your view hierarchy, and make sure that it takes
   up the full width and height of the screen   
3. Listen to query changes and provide suggestion items that implement SearchSuggestion

**Example:**

```xml
       <com.arlib.floatingsearchview.FloatingSearchView
                android:id="@+id/floating_search_view"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                app:floatingSearch_searchBarMarginLeft="@dimen/search_view_inset"
                app:floatingSearch_searchBarMarginTop="@dimen/search_view_inset"
                app:floatingSearch_searchBarMarginRight="@dimen/search_view_inset"
                app:floatingSearch_searchHint="Search..."
                app:floatingSearch_showSearchHintWhenNotFocused="true"
                app:floatingSearch_showSearchKey="false"
                app:floatingSearch_dismissOnOutsideTouch="true"
                app:floatingSearch_leftActionMode="showHamburger"
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
<br/>

**Left action mode:**

The left action can be configured as follows:

Add 
```xml
   app:floatingSearch_leftActionMode="[insert one of the options from table below]"
```

<table>
    <tr>
        <td>showHamburger</td>
        <td><img src="https://github.com/arimorty/floatingsearchview/blob/develop/images/vf2oi.gif"/></td>       
    </tr>    
    <tr>
       <td>showSearch</td>
       <td><img src="https://github.com/arimorty/floatingsearchview/blob/develop/images/vf91i.gif"/></td>        
    <tr>
        <td>showHome</td>
        <td><img src="https://github.com/arimorty/floatingsearchview/blob/develop/images/vf9cp.gif"/></td>       
    </tr>   
    <tr>
        <td>noLeftAction</td>
        <td><img src="https://github.com/arimorty/floatingsearchview/blob/develop/images/vf2ii.gif"/></td>       
    </tr>
</table>

Listen to *hamburger* button clicks:
```
 mSearchView.setOnLeftMenuClickListener(
        new FloatingSearchView.OnLeftMenuClickListener() { ...} );          
```

Listen to home (back arrow) button clicks:
```
  mSearchView.setOnHomeActionClickListener(
         new FloatingSearchView.OnHomeActionClickListener() { ... });       
```

<br/>

**Configure menu items:**

Add a menu resource
```xml
    app:floatingSearch_menu="@menu/menu_main"
```

In the menu resource, set items' ```app:showAsAction="[insert one of the options described in the table below]"```

<table>
    <tr>
        <td>never</td>
        <td>Puts the menu item in the overflow options popup</td>
    </tr>
    <tr>
       <td>ifRoom</td>
       <td>Shows an action icon for the menu if the following conditions are met:
       1. The search is not focused.
       2. There is enough room for it.
       </td>
    </tr>
    <tr>
        <td>always</td>
        <td>Shows an action icon for the menu if there is room, regardless of whether the search is focused or not.</td>
    </tr>   
</table>

Listen for item selections 
```  
   mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
      @Override
      public void onMenuItemSelected(MenuItem item) {                  
            
      }
   });
```

<br/>


**Configure suggestion item:**

First, implement [SearchSuggestion](https://github.com/arimorty/floatingsearchview/blob/master/library/src/main/java/com/arlib/floatingsearchview/suggestions/model/SearchSuggestion.java) 

*Optional*:

Set a callback for when a given suggestion is bound to the suggestion list.

``` 
   mSearchView.setOnBindSuggestionCallback(new SearchSuggestionsAdapter.OnBindSuggestionCallback() {
            @Override
            public void onBindSuggestion(View suggestionView, ImageView leftIcon, TextView textView, SearchSuggestion item, int itemPosition) {

                       //here you can set some attributes for the suggestion's left icon and text. For example,
                       //you can choose your favorite image-loading library for setting the left icon's image. 
            }

        });
``` 

<br/>

**Styling:**

<img src="https://github.com/arimorty/floatingsearchview/blob/develop/images/device-2015-12-08-123103.png"/>


Available styling:

```xml
   <style name="SearchView">
           <item name="floatingSearch_backgroundColor"></item>
           <item name="floatingSearch_viewTextColor"></item>
           <item name="floatingSearch_hintTextColor"></item>
           <item name="floatingSearch_dividerColor"></item>
           <item name="floatingSearch_clearBtnColor"></item>
           <item name="floatingSearch_leftActionColor"></item>
           <item name="floatingSearch_menuItemIconColor"></item>
           <item name="floatingSearch_suggestionRightIconColor"></item>
           <item name="floatingSearch_actionMenuOverflowColor"></item>
    </style>
```


License
=======

    Copyright (C) 2015 Ari C.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
