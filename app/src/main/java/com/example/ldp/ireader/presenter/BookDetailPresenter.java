package com.example.ldp.ireader.presenter;

import android.util.Log;

import com.example.ldp.ireader.model.bean.BookChapterBean;
import com.example.ldp.ireader.model.bean.CollBookBean;
import com.example.ldp.ireader.model.bean.BookDetailBeanInBiquge;
import com.example.ldp.ireader.model.bean.packages.BookChapterPackageByBiquge;
import com.example.ldp.ireader.model.local.BookRepository;
import com.example.ldp.ireader.model.remote.RemoteRepository;
import com.example.ldp.ireader.presenter.contract.BookDetailContract;
import com.example.ldp.ireader.ui.base.RxPresenter;
import com.example.ldp.ireader.utils.MD5Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ldp on 17-5-4.
 */

public class BookDetailPresenter extends RxPresenter<BookDetailContract.View>
        implements BookDetailContract.Presenter{
    private static final String TAG = "BookDetailPresenter";
    private String bookId;

    @Override
    public void refreshBookDetail(String bookId) {
        this.bookId = bookId;
        refreshBook();
//        refreshComment();
//        refreshRecommend();

    }
//原有
//    @Override
//    public void addToBookShelf(CollBookBean collBookBean)  {
//        Disposable disposable = RemoteRepository.getInstance()
//                .getBookChapters(collBookBean.get_id())
//                .subscribeOn(Schedulers.io())
//                .doOnSubscribe(
//                        (d) -> mView.waitToBookShelf() //等待加载
//                )
//                .subscribeOn(AndroidSchedulers.mainThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                        new Consumer<List<BookChapterBean>>() {
//                            @Override
//                            public void accept(List<BookChapterBean> beans) throws Exception {
//
//                                //设置 id
//                                for (BookChapterBean bean : beans) {
//                                    bean.setId(MD5Utils.strToMd5By16(bean.getLink()));
//                                    Log.d("+加入书架", bean.getLink() + bean.getTitle());
//                                }
//
//                                //设置目录
//                                collBookBean.setBookChapters(beans);
//                                //存储收藏
//                                BookRepository.getInstance()
//                                        .saveCollBookWithAsync(collBookBean);
//
//                                mView.succeedToBookShelf();
//                            }
//                        }
//                        ,
//                        e -> {
//                            mView.errorToBookShelf();
//                            LogUtils.e(e);
//                        }
//                );
//        addDisposable(disposable);
//    }

//    @Override
//    public void addToBookShelf(CollBookBean collBookBean)  {
//        RemoteRepository.getInstance()
//                .getBookChapters(collBookBean.get_id())
//                .subscribeOn(Schedulers.io())
//                .doOnSubscribe(
//                        (d) -> mView.waitToBookShelf()
//                        //等待加载
//                )
//                .subscribeOn(AndroidSchedulers.mainThread())
//                .observeOn(Schedulers.io())
//                .flatMap(new Function<List<BookChapterBean>, SingleSource<List<SearchBookPackageByBiquge.DataBean>>>() {
//                    @Override
//                    public SingleSource<List<SearchBookPackageByBiquge.DataBean>> apply(List<BookChapterBean> bookChapterBeans) throws Exception {
//
//                        //设置 id
//                        for (BookChapterBean bean : bookChapterBeans) {
//                            bean.setId(MD5Utils.strToMd5By16(bean.getLink()));
//                        }
//                        Log.d(TAG,"+加入书架"+bookChapterBeans.size() + "   " + collBookBean.getTitle());
//
//                        //设置目录
//                        collBookBean.setBookChapters(bookChapterBeans);
//                        //存储收藏
//                        BookRepository.getInstance()
//                                .saveCollBookWithAsync(collBookBean);
//
//
//                        return RemoteRepository.getInstance().getSearchBooksByBiqugeSearch(collBookBean.getTitle());
//                    }})
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<List<SearchBookPackageByBiquge.DataBean>>() {
//                    @Override
//                    public void accept(List<SearchBookPackageByBiquge.DataBean> dataBeans) throws Exception {
//
//                        for(int i =0;i<dataBeans.size();i++){
//
//                            if (dataBeans.get(i).getAuthor().equals(collBookBean.getAuthor()) && dataBeans.get(i).getName().equals(collBookBean.getTitle())){
//                                collBookBean.setBookIdInBiquge(dataBeans.get(i).getId());
//                                BookRepository.getInstance().saveCollBook(collBookBean);
//                                break;
//                            }
//                        }
//
//                        mView.succeedToBookShelf();
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        Log.d(TAG,"+网络错误"+throwable.getMessage() +throwable.getCause());
//                    }
//                });
//
////        addDisposable(disposable);
//    }

    @Override
    public void addToBookShelf(CollBookBean collBookBean) {

        List<BookChapterBean> bookChapterBeans = new ArrayList<>();
        Log.d(TAG, "addToBookShelf: " + collBookBean);
        Disposable disposable = RemoteRepository.getInstance()
                .getChapterListByBiquge(collBookBean.get_id())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(
                        (d) -> mView.waitToBookShelf()
                        //等待加载
                )
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BookChapterPackageByBiquge>() {
                    @Override
                    public void accept(BookChapterPackageByBiquge bookChapterPackageByBiquge) throws Exception {
                        Log.d(TAG, "accept:bookChapterPackageByBiquge " + bookChapterPackageByBiquge);
                        List<BookChapterPackageByBiquge.DataBean.ListBeanX.ListBean> listBeans = new ArrayList<>();
                        for (BookChapterPackageByBiquge.DataBean.ListBeanX mlistBeanX : bookChapterPackageByBiquge.getData().getList()) {
                            if (null != mlistBeanX && null != mlistBeanX.getList()) {
                                listBeans.addAll(mlistBeanX.getList());
                            }
                        }
                        for (int i = 0; i < listBeans.size() - 1; i++) {
                            if (null != listBeans.get(i)) {

                                BookChapterBean bookChapterBeanTemp = new BookChapterBean();
                                bookChapterBeanTemp.setLink("BQG" + String.valueOf(listBeans.get(i).getId()));
                                bookChapterBeanTemp.setTitle(listBeans.get(i).getName());
                                bookChapterBeanTemp.setValidInZhuishu(false);
                                bookChapterBeanTemp.setId(MD5Utils.strToMd5By16(bookChapterBeanTemp.getLink()));
                                Log.d(TAG, "+章节名  " + i + " " + listBeans.get(i).getName());
                                bookChapterBeanTemp.setBookId(collBookBean.get_id());

                                bookChapterBeans.add(bookChapterBeanTemp);

                            }
                        }
//                        BookRepository.getInstance()
//                                .saveBookChaptersWithAsync(bookChapterBeans);
                        //设置目录
                        collBookBean.setBookChapters(bookChapterBeans);
                        //存储收藏
                        BookRepository.getInstance()
                                .saveCollBookWithAsync(collBookBean);
                        mView.succeedToBookShelf();

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        Log.e(TAG, throwable.getCause() + "   " + throwable.getMessage());
                        mView.errorToBookShelf();

                    }
                });


        addDisposable(disposable);
    }

//    private void refreshBook(){
//        RemoteRepository
//                .getInstance()
//                .getBookDetail(bookId)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new SingleObserver<BookDetailBean>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        addDisposable(d);
//                    }
//
//                    @Override
//                    public void onSuccess(BookDetailBean value){
//                        mView.finishRefresh(value);
//                        mView.complete();
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        mView.showError();
//                    }
//                });
//    }

    private void refreshBook() {
        Disposable disposable = RemoteRepository
                .getInstance()
                .getBookDetailByBiquge(bookId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BookDetailBeanInBiquge>() {
                    @Override
                    public void accept(BookDetailBeanInBiquge bookDetailBeanInBiquge) throws Exception {
                        mView.finishRefresh(bookDetailBeanInBiquge);
                        mView.complete();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.showError();
                    }
                });
        addDisposable(disposable);

    }

    private void refreshComment(){
        Disposable disposable = RemoteRepository
                .getInstance()
                .getHotComments(bookId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (value) -> mView.finishHotComment(value)
                );
        addDisposable(disposable);
    }

    private void refreshRecommend(){
        Disposable disposable = RemoteRepository
                .getInstance()
                .getRecommendBookList(bookId,3)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (value) -> mView.finishRecommendBookList(value)
                );
        addDisposable(disposable);
    }
}
