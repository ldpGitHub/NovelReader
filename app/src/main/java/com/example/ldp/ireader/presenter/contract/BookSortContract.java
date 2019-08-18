package com.example.ldp.ireader.presenter.contract;

import com.example.ldp.ireader.model.bean.packages.BookSortPackage;
import com.example.ldp.ireader.model.bean.packages.BookSubSortPackage;
import com.example.ldp.ireader.ui.base.BaseContract;

/**
 * Created by ldp on 17-4-23.
 */

public interface BookSortContract {

    interface View extends BaseContract.BaseView{
        void finishRefresh(BookSortPackage sortPackage, BookSubSortPackage subSortPackage);
    }

    interface Presenter extends BaseContract.BasePresenter<View>{
        void refreshSortBean();
    }
}
