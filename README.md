Floating Search View
=============

An implementation of a floating search box with search suggestions.

![Alt text](/images/inaction.gif)

Usage
-----
Example:

```xml
   <com.arlib.floatingsearchview.FloatingSearchView
            android:id="@+id/floating_search_view"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            app:floatingSearch_showVoiceInput="true"
            app:floatingSearch_searchBarMarginLeft="@dimen/search_view_inset"
            app:floatingSearch_searchBarMarginTop="@dimen/search_view_inset"
            app:floatingSearch_searchBarMarginRight="@dimen/search_view_inset"
            app:floatingSearch_dismissOnOutsideTouch="true"
            app:floatingSearch_menu="@menu/menu_main"/>
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