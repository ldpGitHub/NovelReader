package com.example.ldp.ireader.presenter.contract;

import com.example.ldp.ireader.model.bean.BookListDetailBean;
import com.example.ldp.ireader.ui.base.BaseContract;

/**
 * Created by ldp on 17-5-1.
 */

public interface BookListDetailContract {

    interface View extends BaseContract.BaseView{
        void finishRefresh(BookListDetailBean bean);
    }

    interface Presenter extends BaseContract.BasePresenter<View>{
        void refreshBookListDetail(String detailId);
    }
}
