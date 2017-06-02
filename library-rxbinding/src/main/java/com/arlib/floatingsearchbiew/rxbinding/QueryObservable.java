package com.arlib.floatingsearchbiew.rxbinding;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.jakewharton.rxbinding2.InitialValueObservable;

import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

final public class QueryObservable extends InitialValueObservable<CharSequence> {

    private final FloatingSearchView view;

    private final int minQueryLength;

    public QueryObservable(FloatingSearchView view) {
        this(view, 1);
    }

    public QueryObservable(FloatingSearchView view, int minQueryLength) {
        this.view = view;
        this.minQueryLength = minQueryLength;
    }

    @Override
    protected void subscribeListener(Observer<? super CharSequence> observer) {
        Listener listener = new Listener(view, observer, minQueryLength);
        observer.onSubscribe(listener);
        view.setOnQueryChangeListener(listener);
    }

    @Override
    protected CharSequence getInitialValue() {
        return view.getQuery();
    }

    final static class Listener extends MainThreadDisposable implements FloatingSearchView.OnQueryChangeListener {

        private final FloatingSearchView view;
        private final Observer<? super CharSequence> observer;
        private final int minQueryLength;

        public Listener(FloatingSearchView view, Observer<? super CharSequence> observer, int minQueryLength) {
            this.view = view;
            this.observer = observer;
            this.minQueryLength = minQueryLength;
        }

        @Override
        public void onSearchTextChanged(String oldQuery, String newQuery) {
            if(!isDisposed() && newQuery != null && newQuery.length() >= minQueryLength) {
                observer.onNext(newQuery);
            }
        }

        @Override
        protected void onDispose() {
            view.setOnQueryChangeListener(null);
        }
    }
}
