package com.example.ldp.ireader.ui.base;

/**
 * Created by ldp on 17-4-26.
 */

public interface BaseContract {

    interface BasePresenter<T> {

        void attachView(T view);

        void detachView();
    }

    interface BaseView {

        void showError();

        void complete();
    }
}
