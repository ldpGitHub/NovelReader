package com.example.ldp.ireader.presenter;

import com.example.ldp.ireader.model.remote.RemoteRepository;
import com.example.ldp.ireader.presenter.contract.BookListDetailContract;
import com.example.ldp.ireader.ui.base.RxPresenter;
import com.example.ldp.ireader.utils.LogUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ldp on 17-5-2.
 */

public class BookListDetailPresenter extends RxPresenter<BookListDetailContract.View> implements BookListDetailContract.Presenter {
    @Override
    public void refreshBookListDetail(String detailId) {
        Disposable refreshDispo = RemoteRepository.getInstance()
                .getBookListDetail(detailId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (beans)-> {
                            mView.finishRefresh(beans);
                            mView.complete();
                        }
                        ,
                        (e) ->{
                            mView.showError();
                            LogUtils.e(e);
                        }
                );
        addDisposable(refreshDispo);
    }
}
