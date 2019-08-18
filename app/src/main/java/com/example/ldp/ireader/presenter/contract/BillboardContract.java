package com.example.ldp.ireader.presenter.contract;

import com.example.ldp.ireader.model.bean.packages.BillboardPackage;
import com.example.ldp.ireader.ui.base.BaseContract;

/**
 * Created by ldp on 17-4-23.
 */

public interface BillboardContract {

    interface View extends BaseContract.BaseView{
        void finishRefresh(BillboardPackage beans);
    }

    interface Presenter extends BaseContract.BasePresenter<View>{
        void loadBillboardList();
    }
}
