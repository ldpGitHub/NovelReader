package com.example.ldp.ireader.presenter.contract;

import com.example.ldp.ireader.model.bean.BookListBean;
import com.example.ldp.ireader.model.flag.BookListType;
import com.example.ldp.ireader.ui.base.BaseContract;

import java.util.List;

/**
 * Created by ldp on 17-5-1.
 */

public interface BookListContract {
    interface View extends BaseContract.BaseView{
        void finishRefresh(List<BookListBean> beans);
        void finishLoading(List<BookListBean> beans);
        void showLoadError();
    }

    interface Presenter extends BaseContract.BasePresenter<View>{
        void refreshBookList(BookListType type, String tag,int start, int limited);
        void loadBookList(BookListType type, String tag,int start, int limited);
    }
}
