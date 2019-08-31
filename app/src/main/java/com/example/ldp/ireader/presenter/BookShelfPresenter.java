package com.example.ldp.ireader.presenter;

import android.os.Vibrator;
import android.util.Log;

import com.example.ldp.ireader.App;
import com.example.ldp.ireader.RxBus;
import com.example.ldp.ireader.model.bean.BookChapterBean;
import com.example.ldp.ireader.model.bean.BookDetailBean;
import com.example.ldp.ireader.model.bean.BookDetailBeanInBiquge;
import com.example.ldp.ireader.model.bean.CollBookBean;
import com.example.ldp.ireader.model.bean.DownloadTaskBean;
import com.example.ldp.ireader.model.bean.packages.BookChapterPackageByBiquge;
import com.example.ldp.ireader.model.local.BookRepository;
import com.example.ldp.ireader.model.remote.RemoteRepository;
import com.example.ldp.ireader.presenter.contract.BookShelfContract;
import com.example.ldp.ireader.ui.base.RxPresenter;
import com.example.ldp.ireader.utils.Constant;
import com.example.ldp.ireader.utils.LogUtils;
import com.example.ldp.ireader.utils.MD5Utils;
import com.example.ldp.ireader.utils.RxUtils;
import com.example.ldp.ireader.utils.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ldp on 17-5-8.
 */

public class BookShelfPresenter extends RxPresenter<BookShelfContract.View>
        implements BookShelfContract.Presenter {
    private static final String TAG = "BookShelfPresenter";

    @Override
    public void refreshCollBooks() {
        List<CollBookBean> collBooks = BookRepository
                .getInstance().getCollBooks();
        for(CollBookBean bookBean: collBooks){
            Log.d("+书名",bookBean.getTitle());
        }
        mView.finishRefresh(collBooks);
    }

    @Override
    public void createDownloadTask(CollBookBean collBookBean) {
        DownloadTaskBean task = new DownloadTaskBean();
        task.setTaskName(collBookBean.getTitle());
        task.setBookId(collBookBean.get_id());
        task.setBookChapters(collBookBean.getBookChapters());
        task.setLastChapter(collBookBean.getBookChapters().size());

        RxBus.getInstance().post(task);
    }


    @Override
    public void loadRecommendBooks(String gender) {
        Disposable disposable = RemoteRepository.getInstance()
                .getRecommendBooks(gender)
                .doOnSuccess(new Consumer<List<CollBookBean>>() {
                    @Override
                    public void accept(List<CollBookBean> collBooks) throws Exception{
                        //更新目录
                        updateCategory(collBooks);
                        //异步存储到数据库中
                        BookRepository.getInstance()
                                .saveCollBooksWithAsync(collBooks);
                    }
                })
                .compose(RxUtils::toSimpleSingle)
                .subscribe(
                        beans -> {
                            mView.finishRefresh(beans);
                            mView.complete();
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable e) throws Exception {
                                //提示没有网络
                                LogUtils.e(e);
                                mView.showErrorTip(e.toString());
                                mView.complete();
                            }
                        }
                );
        addDisposable(disposable);
    }


    //需要修改
    @Override
    public void updateCollBooks(List<CollBookBean> collBookBeans) {
        Log.e(TAG,"+updateCollBooks ");

//        List<CollBookBean> collBookBeans = BookRepository
//                .getInstance().getCollBooks();
        if (collBookBeans == null || collBookBeans.isEmpty()){
            return ;
        }
        List<CollBookBean> collBooks = new ArrayList<>(collBookBeans);
        List<Single<BookDetailBean>> observables = new ArrayList<>(collBooks.size());
        List<Single<BookDetailBeanInBiquge>> observablesInBiquge = new ArrayList<>();

        Iterator<CollBookBean> it = collBooks.iterator();
        while (it.hasNext()){
            CollBookBean collBook = it.next();
            //删除本地文件
            if (collBook.isLocal()) {
                it.remove();
            }
            else {
                observables.add(RemoteRepository.getInstance().getBookDetail(collBook.get_id()));
//                if (null == collBook.getBookIdInBiquge()|| collBook.getBookIdInBiquge().isEmpty()) {
//
//                    continue;
//                }

                Log.d(TAG,"+笔趣阁ID "+  collBook.getBookIdInBiquge());
                observablesInBiquge.add(RemoteRepository.getInstance().getBookDetailByBiquge(collBook.get_id()));
            }
        }

        List<CollBookBean> newCollBooksMerge = new ArrayList<CollBookBean>(observables.size());
        Single.concat(observablesInBiquge)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<BookDetailBeanInBiquge>() {
                    @Override
                    public void accept(BookDetailBeanInBiquge bookDetailBeanInBiquge) throws Exception {

                        CollBookBean oldCollBook = collBooks.get(0);

                        for (CollBookBean collBookItem : collBooks) {
                            if (collBookItem.getTitle().equals(bookDetailBeanInBiquge.getData().getName())) {
                                oldCollBook = collBookItem;
                                break;
                            }
                        }

                        //如果是oldBook是update状态，或者newCollBook与oldBook章节数不同
//                        showNotification(oldCollBook);
                        updateCategory(oldCollBook);
                        oldCollBook.setUpdated(bookDetailBeanInBiquge.getData().getLastTime());
                        if (!oldCollBook.getLastChapter().equals(bookDetailBeanInBiquge.getData().getLastChapter())) {
                            oldCollBook.setLastChapter(bookDetailBeanInBiquge.getData().getLastChapter());
                            Log.d(TAG,"+更新书籍 "+oldCollBook.getTitle() + oldCollBook.getLastChapter());

                            oldCollBook.setUpdate(true);
//                            showNotification(oldCollBook);
                            updateCategory(oldCollBook);
                            Vibrator vibrator = (Vibrator) App.getContext().getSystemService(App.getContext().VIBRATOR_SERVICE);
                            vibrator.vibrate(4000);
                        } else {
                            oldCollBook.setUpdate(false);
                        }
                        newCollBooksMerge.add(oldCollBook);
                        Log.d(TAG,"+检查更新" );
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
//                        updateCategory(newCollBooksMerge);
//                                //异步存储到数据库中
                        BookRepository.getInstance().saveCollBooks(newCollBooksMerge);
//                                Log.d("+?5缓存", "运行");//
                                mView.complete();
                                mView.finishUpdate();
                    }
                });





//        if (collBookBeans == null || collBookBeans.isEmpty()) return;
//        List<CollBookBean> collBooks = new ArrayList<>(collBookBeans);
//        List<Single<BookDetailBean>> observables = new ArrayList<>(collBooks.size());
//        Iterator<CollBookBean> it = collBooks.iterator();
//        while (it.hasNext()){
//            CollBookBean collBook = it.next();
//            //删除本地文件
//            if (collBook.isLocal()) {
//                it.remove();
//            }
//            else {
//                observables.add(RemoteRepository.getInstance()
//                        .getBookDetail(collBook.get_id()));
//            }
//        }
//        //zip可能不是一个好方法。
//        Single.zip(observables, new Function<Object[], List<CollBookBean>>() {
//            @Override
//            public List<CollBookBean> apply(Object[] objects) throws Exception {
//                List<CollBookBean> newCollBooks = new ArrayList<CollBookBean>(objects.length);
//                for (int i=0; i<collBooks.size(); ++i){
//                    CollBookBean oldCollBook = collBooks.get(i);
//                    CollBookBean newCollBook = ((BookDetailBean)objects[i]).getCollBookBean();
//                    //如果是oldBook是update状态，或者newCollBook与oldBook章节数不同
//                    if (oldCollBook.isUpdate() ||
//                            !oldCollBook.getLastChapter().equals(newCollBook.getLastChapter())){
//                        newCollBook.setUpdate(true);
//                    }
//                    else {
//                        newCollBook.setUpdate(false);
//                    }
//                    newCollBook.setLastRead(oldCollBook.getLastRead());
//                    newCollBooks.add(newCollBook);
//                    //存储到数据库中
//                    BookRepository.getInstance().saveCollBooks(newCollBooks);
//                }
//                return newCollBooks;
//            }
//        })
//                .compose(RxUtils::toSimpleSingle)
//                .subscribe(new SingleObserver<List<CollBookBean>>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        addDisposable(d);
//                    }
//
//                    @Override
//                    public void onSuccess(List<CollBookBean> value) {
//                        //跟原先比较
//                        mView.finishUpdate();
//                        List<String> bookNames = new ArrayList<>();
//                        for (CollBookBean collBookBean: value) {
//                            bookNames.add(collBookBean.getTitle());
//                        }
//                        mView.complete();
//
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        //提示没有网络
//                        mView.showErrorTip(e.toString());
//                        mView.complete();
//                        LogUtils.e(e);
//                    }
//                });
    }

    //更新每个CollBook的目录
    private void updateCategory(List<CollBookBean> collBookBeans){

        List<Single<List<BookChapterBean>>> observables = new ArrayList<>(collBookBeans.size());
        for (CollBookBean bean : collBookBeans){
            observables.add(
                    RemoteRepository.getInstance().getBookChapters(bean.get_id())
            );
        }
        Iterator<CollBookBean> it = collBookBeans.iterator();
        //执行在上一个方法中的子线程中
        Single.concat(observables)
                .subscribe(
                        new Consumer<List<BookChapterBean>>() {
                            @Override
                            public void accept(List<BookChapterBean> chapterList) throws Exception {

                                for (BookChapterBean bean : chapterList) {
                                    bean.setId(MD5Utils.strToMd5By16(bean.getLink()));
                                }

                                CollBookBean bean = it.next();
                                bean.setLastRead(StringUtils.
                                        dateConvert(System.currentTimeMillis(), Constant.FORMAT_BOOK_DATE));
                                bean.setBookChapters(chapterList);
                            }
                        }
                );
    }

    //更新每个CollBook的目录
    private void updateCategory(CollBookBean collBookBean) {
        List<Single<BookDetailBeanInBiquge>> observablesInBiquge = new ArrayList<>();

        List<BookChapterBean> bookChapterBeans = new ArrayList<>();


        Disposable disposable = RemoteRepository.getInstance()
                .getChapterListByBiquge(collBookBean.get_id())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(bookChapterPackageByBiquge -> {

                    List<BookChapterPackageByBiquge.DataBean.ListBeanX.ListBean> listBeans = new ArrayList<>();
                    for (BookChapterPackageByBiquge.DataBean.ListBeanX mlistBeanX : bookChapterPackageByBiquge.getData().getList()) {
                        if (null != mlistBeanX && null != mlistBeanX.getList()) {
                            listBeans.addAll(mlistBeanX.getList());
                        }
                    }
                    for (int i = 0; i <=listBeans.size() - 1; i++) {
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
//                        mView.succeedToBookShelf();

                }, throwable -> {

                    Log.e(TAG, throwable.getCause() + "   " + throwable.getMessage());
//                        mView.errorToBookShelf();

                });


        addDisposable(disposable);
    }



}
