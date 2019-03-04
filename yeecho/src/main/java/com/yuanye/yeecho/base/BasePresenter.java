package com.yuanye.yeecho.base;

public class BasePresenter<V> {

    protected V mView;

    protected void onAttach(V view){
        mView = view;
    }

    protected void onDetach(){
        mView = null;
    }

    protected boolean isViewAttached(){
        return mView != null;
    }

}
