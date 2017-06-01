package com.arlib.floatingsearchbiew.rxbinding;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.jakewharton.rxbinding2.InitialValueObservable;

import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

final public class QueryObservable extends InitialValueObservable<CharSequence> {

    private final FloatingSearchView view;

    public QueryObservable(FloatingSearchView view) {
        this.view = view;
    }

    @Override
    protected void subscribeListener(Observer<? super CharSequence> observer) {
        Listener listener = new Listener(view, observer);
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

        public Listener(FloatingSearchView view, Observer<? super CharSequence> observer) {
            this.view = view;
            this.observer = observer;
        }

        @Override
        public void onSearchTextChanged(String oldQuery, String newQuery) {
            if(!isDisposed()) {
                observer.onNext(newQuery);
            }
        }

        @Override
        protected void onDispose() {
            view.setOnQueryChangeListener(null);
        }
    }
}
