package com.arlib.floatingsearchviewdemo.fragment;

import android.content.Context;
import androidx.fragment.app.Fragment;

import com.arlib.floatingsearchview.FloatingSearchView;

/**
 * Created by ari on 8/16/16.
 */
public abstract class BaseExampleFragment extends Fragment {


    private BaseExampleFragmentCallbacks mCallbacks;

    public interface BaseExampleFragmentCallbacks{

        void onAttachSearchViewToDrawer(FloatingSearchView searchView);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseExampleFragmentCallbacks) {
            mCallbacks = (BaseExampleFragmentCallbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement BaseExampleFragmentCallbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    protected void attachSearchViewActivityDrawer(FloatingSearchView searchView){
        if(mCallbacks != null){
            mCallbacks.onAttachSearchViewToDrawer(searchView);
        }
    }

    public abstract boolean onActivityBackPress();
}
