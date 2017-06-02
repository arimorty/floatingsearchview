package com.arlib.floatingsearchview.rxbinding

import com.arlib.floatingsearchbiew.rxbinding.RxFloatingSearchView
import com.arlib.floatingsearchview.FloatingSearchView
import com.jakewharton.rxbinding2.InitialValueObservable

inline fun FloatingSearchView.queryChanges(minQueryLimit: Int = 1): InitialValueObservable<CharSequence> = RxFloatingSearchView.queryChanges(this, minQueryLimit)