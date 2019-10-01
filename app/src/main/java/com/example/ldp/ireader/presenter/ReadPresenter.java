package com.example.ldp.ireader.presenter;


import android.util.Log;

import com.example.ldp.ireader.model.bean.BookChapterBean;
import com.example.ldp.ireader.model.bean.BookChapterBeanByBiquge;
import com.example.ldp.ireader.model.bean.ChapterInfoBean;
import com.example.ldp.ireader.model.bean.CollBookBean;
import com.example.ldp.ireader.model.bean.packages.BookChapterPackageByBiquge;
import com.example.ldp.ireader.model.local.BookRepository;
import com.example.ldp.ireader.model.remote.RemoteRepository;
import com.example.ldp.ireader.presenter.contract.ReadContract;
import com.example.ldp.ireader.ui.base.RxPresenter;
import com.example.ldp.ireader.utils.LogUtils;
import com.example.ldp.ireader.utils.MD5Utils;
import com.example.ldp.ireader.utils.RxUtils;
import com.example.ldp.ireader.utils.SimilarityCharacterUtils;
import com.example.ldp.ireader.widget.page.TxtChapter;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ldp on 17-5-16.
 */

public class ReadPresenter extends RxPresenter<ReadContract.View>
        implements ReadContract.Presenter {
    private static final String TAG = ReadPresenter.class.getSimpleName();

    private Subscription mChapterSub;

//原有
//    @Override
//    public void loadCategory(String bookId) {
//        Disposable disposable = RemoteRepository.getInstance()
//                .getBookChapters(bookId)
//                .doOnSuccess(new Consumer<List<BookChapterBean>>() {
//                    @Override
//                    public void accept(List<BookChapterBean> bookChapterBeen) throws Exception {
//                        //进行设定BookChapter所属的书的id。
//                        for (BookChapterBean bookChapter : bookChapterBeen) {
//                            bookChapter.setId(MD5Utils.strToMd5By16(bookChapter.getLink()));
//                            bookChapter.setBookId(bookId);
//                        }
//                    }
//                })
//                .compose(RxUtils::toSimpleSingle)
//                .subscribe(
//                        new Consumer<List<BookChapterBean>>() {
//                            @Override
//                            public void accept(List<BookChapterBean> beans) throws Exception {
//                                mView.showCategory(beans);
//                            }
//                        }
//                        ,
//                        e -> {
//                            //TODO: Haven't grate conversation method.
//                            LogUtils.e(e);
//                        }
//                );
//        addDisposable(disposable);
//    }

    private Map<String,String> IdZhuishuToBiquge = new LinkedHashMap();
    String bookIdInBiquge = "";
    List<BookChapterBean> beansInZhuishu = new ArrayList<>();

    @Override
    public void loadCategory(CollBookBean collBookBean) {
        List<BookChapterBean> bookChapterBeans = new ArrayList<>();
//        CollBookBean collBookBean = BookRepository.getInstance().getCollBook(bookId);
        Log.d("+加载章节",collBookBean+"");

        RemoteRepository.getInstance()
                .getChapterListByBiquge(collBookBean.get_id())
                .compose(RxUtils::toSimpleSingle)
                .subscribe(new Consumer<BookChapterPackageByBiquge>() {
                    @Override
                    public void accept(BookChapterPackageByBiquge bookChapterPackageByBiquge) throws Exception {
                        List<BookChapterPackageByBiquge.DataBean.ListBeanX.ListBean> listBeans = new ArrayList<>();
                        for (BookChapterPackageByBiquge.DataBean.ListBeanX mlistBeanX : bookChapterPackageByBiquge.getData().getList()) {
                            if (null != mlistBeanX && null != mlistBeanX.getList()) {
                                listBeans.addAll(mlistBeanX.getList());
                            }
                        }
                        for (int i = 0; i <= listBeans.size() - 1; i++) {
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
//                        BookRepository.getInstance().saveChapterInfo(
//                                        bookId, title, chapterInfoBean.getBody());
                        mView.showCategory(bookChapterBeans,collBookBean.get_id(),true);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });

    }

    @Deprecated
    @Override
    public void loadCategoryInBiquge(String bookId) {

        CollBookBean bean = BookRepository.getInstance().getCollBook(bookId);
        List<BookChapterBean> bookChapterBeans = bean.getBookChapterList();
        bookIdInBiquge = bean.getBookIdInBiquge();
        if (null == bookIdInBiquge || bookIdInBiquge.isEmpty()) {
            mView.showCategory(bookChapterBeans,bookId,true);
            return;
        }
        IdZhuishuToBiquge.put(bookId, bookIdInBiquge);
        Log.d("+笔趣阁ID", "bookIdInBiquge" + bookIdInBiquge);
        RemoteRepository.getInstance()
                .getChapterListByBiquge(bookIdInBiquge)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BookChapterPackageByBiquge>() {
                    @Override
                    public void accept(BookChapterPackageByBiquge bookChapterPackageByBiquge) throws Exception {
//                        beansInZhuishu.clear();
                        List<BookChapterPackageByBiquge.DataBean.ListBeanX.ListBean> listBeans = new ArrayList<>();
                        for (BookChapterPackageByBiquge.DataBean.ListBeanX mlistBeanX : bookChapterPackageByBiquge.getData().getList()) {
                            if (null != mlistBeanX && null != mlistBeanX.getList()) {
                                listBeans.addAll(mlistBeanX.getList());
                            }
                        }
                        Log.d("+追书章节总数", "    追书:" + bookChapterBeans.size() +"笔趣阁" + listBeans.size());
//                        Log.d("+笔趣阁章节最新/追书章节最新", "笔趣阁:" + listBeans.get(listBeans.size() - 1).getName() + "    追书:" +bookChapterBeans.get( bookChapterBeans.size()).getTitle());




//                        if(bookChapterBeans.size() < listBeans.size()||true){
//                           String titleWithoutPre = bookChapterBeans.get(bookChapterBeans.size() - 1).getTitle().split(" ")[bookChapterBeans.get(bookChapterBeans.size() - 1).getTitle().split(" ").length - 1];
//                            titleWithoutPre = titleWithoutPre.replaceAll("[^\u4e00-\u9fa5a-zA-Z0-9]", "");
//                            Log.d("+纯章节名" , titleWithoutPre);
//
//                            int latestPos = listBeans.size() - 1;
//                            for (int i = listBeans.size() - 1; i >= 0; i--) {
//                                if(null != listBeans.get(i)&&(listBeans.get(i).getName().replaceAll("[^\u4e00-\u9fa5a-zA-Z0-9]", "").contains(titleWithoutPre)|| bookChapterBeans.get(bookChapterBeans.size() - 1).getTitle().replaceAll("[^\u4e00-\u9fa5a-zA-Z0-9]", "").contains(listBeans.get(i).getName().split(" ")[listBeans.get(i).getName().split(" ").length -1].replaceAll("[^\u4e00-\u9fa5a-zA-Z0-9]", ""))))
//                                {
//                                    latestPos = i;
//                                    break;
//                                }
//
//                            }
//                            for (int i = latestPos + 1; i < listBeans.size() - 1; i++) {
//                                if (null != listBeans.get(i)) {
//                                    Log.d("+章节名" + i, listBeans.get(i).getName());
////                    Log.d("+追书神器总章节数,章节名",beansInZhuishu.size() + beansInZhuishu.get(i).getTitle() );
//
//
//                                    BookChapterBean bookChapterBeanTemp = new BookChapterBean();
//                                    bookChapterBeanTemp.setLink("BQG"+String.valueOf(listBeans.get(i).getId()));
//                                    bookChapterBeanTemp.setTitle(listBeans.get(i).getName());
//                                    bookChapterBeanTemp.setValidInZhuishu(false);
//                                    Log.d("+多出的章节名", listBeans.get(i).getName());
//
//                                    bookChapterBeans.add(bookChapterBeanTemp);
//                                    Log.d("+笔趣阁复制章节总数", beansInZhuishu.size() + "");
//
//                                }
//                            }
//                        }




                        if(bookChapterBeans.size() < listBeans.size()||true){
                            String lastTitleInZhuishu = bookChapterBeans.get(bookChapterBeans.size() - 1).getTitle();

                            int latestPos = listBeans.size() - 1;
                            double highestSimilarity = 0;
                            for (int i = listBeans.size() - 1; i >= listBeans.size() - 100; i--) {

                                if (null == listBeans.get(i)) {
                                    continue;
                                }
                                if(listBeans.get(i).getName().contains(lastTitleInZhuishu)||lastTitleInZhuishu.contains(listBeans.get(i).getName())){
                                    Log.d(TAG,"直接包含,不用计算相似度");
                                    latestPos = i;
                                    break;
                                }
                                double titleSimilarity  = SimilarityCharacterUtils.getSimilarity(listBeans.get(i).getName(),lastTitleInZhuishu);
                                Log.d(TAG,"titleSimilarity : "+titleSimilarity +  "    " + lastTitleInZhuishu  + "    " + listBeans.get(i).getName() + " positionInBiquge  " + i);

                                if(titleSimilarity >highestSimilarity){
                                    highestSimilarity = titleSimilarity;
                                    latestPos = i;
                                }
                            }

                            Log.d(TAG,"highestSimilarity:  " + highestSimilarity);

                            for (int i = latestPos + 1; i < listBeans.size() - 1; i++) {
                                if (null != listBeans.get(i)) {

                                    BookChapterBean bookChapterBeanTemp = new BookChapterBean();
                                    bookChapterBeanTemp.setLink("BQG"+String.valueOf(listBeans.get(i).getId()));
                                    bookChapterBeanTemp.setTitle(listBeans.get(i).getName());
                                    bookChapterBeanTemp.setValidInZhuishu(false);
                                    Log.d(TAG,"+多出的章节名  "+i+ " "+listBeans.get(i).getName());
                                    bookChapterBeans.add(bookChapterBeanTemp);

                                }
                            }
                        }




                        mView.showCategory(bookChapterBeans,bookId,true);
                    }
                });

    }


    // 原有
//    @Override
//    public void loadChapter(String bookId, List<TxtChapter> bookChapters) {
//        int size = bookChapters.size();
//
//        //取消上次的任务，防止多次加载
//        if (mChapterSub != null) {
//            mChapterSub.cancel();
//        }
//
//        List<Single<ChapterInfoBean>> chapterInfos = new ArrayList<>(bookChapters.size());
//        ArrayDeque<String> titles = new ArrayDeque<>(bookChapters.size());
//
//        // 将要下载章节，转换成网络请求。
//        for (int i = 0; i < size; ++i) {
//            TxtChapter bookChapter = bookChapters.get(i);
//            // 网络中获取数据
//            Single<ChapterInfoBean> chapterInfoSingle = RemoteRepository.getInstance()
//                    .getChapterInfo(bookChapter.getLink());
//
//            chapterInfos.add(chapterInfoSingle);
//
//            titles.add(bookChapter.getTitle());
//        }
//
//        Single.concat(chapterInfos)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                        new Subscriber<ChapterInfoBean>() {
//                            String title = titles.poll();
//
//                            @Override
//                            public void onSubscribe(Subscription s) {
//                                s.request(Integer.MAX_VALUE);
//                                mChapterSub = s;
//                            }
//
//                            @Override
//                            public void onNext(ChapterInfoBean chapterInfoBean) {
//                                //存储数据
//                                BookRepository.getInstance().saveChapterInfo(
//                                        bookId, title, chapterInfoBean.getBody()
//                                );
//                                mView.finishChapter();
//                                //将获取到的数据进行存储
//                                title = titles.poll();
//
//
//                                Log.e("+chapterBody","title" + chapterInfoBean.getTitle() +  titles  +  " " + chapterInfoBean.getBody());
//                            }
//
//                            @Override
//                            public void onError(Throwable t) {
//                                //只有第一个加载失败才会调用errorChapter
//                                if (bookChapters.get(0).getTitle().equals(title)) {
//                                    mView.errorChapter();
//                                }
//                                LogUtils.e(t);
//                            }
//
//                            @Override
//                            public void onComplete() {
//                            }
//                        }
//                );
//    }

    long lastTime =  System.currentTimeMillis();
    boolean firstTime = true;

    @Override
    public synchronized void loadChapter(String bookId, List<TxtChapter> bookChapters) {

//        if(firstTime || System.currentTimeMillis()-lastTime>2000){
//            lastTime =  System.currentTimeMillis();
//            firstTime = false;
//        }else{
//            Log.e(TAG, "loadChapter被拦截");
//            return;
//        }
        int size = bookChapters.size();
        Log.e(TAG, "loadChapter  列表大小" + size + Arrays.asList(bookChapters).toString());

        //取消上次的任务，防止多次加载
        if (mChapterSub != null) {
            mChapterSub.cancel();
        }

        List<Single<ChapterInfoBean>> chapterInfos = new ArrayList<>();
        List<Single<BookChapterBeanByBiquge>> bookChapterSBeanByBiquge = new ArrayList<>(bookChapters.size());

        ArrayDeque<String> titles = new ArrayDeque<>();
        ArrayDeque<String> titlesInBiquge = new ArrayDeque<>();

        // 将要下载章节，转换成网络请求。
        for (int i = 0; i < size; ++i) {
            TxtChapter bookChapter = bookChapters.get(i);
            if (bookChapter.getLink().contains("BQG")) {
                String pureLink = bookChapter.getLink().split("BQG")[1];
                CollBookBean bean = BookRepository.getInstance().getCollBook(bookId);
                bookIdInBiquge = bean.get_id();
                Log.d("+收到的章节笔趣阁Id", bookIdInBiquge);

                Single<BookChapterBeanByBiquge> bookChapterBeanByBiqugeSingle = RemoteRepository.getInstance()
                        .getChapterByBiquge(bookIdInBiquge, pureLink);
                Log.d("+收到的章节ID", bookChapter.getLink());

                bookChapterSBeanByBiquge.add(bookChapterBeanByBiqugeSingle);

                titlesInBiquge.add(bookChapter.getTitle());

            } else {
//                // 网络中获取数据
//                Single<ChapterInfoBean> chapterInfoSingle = RemoteRepository.getInstance()
//                        .getChapterInfo(bookChapter.getLink());
//                chapterInfos.add(chapterInfoSingle);
//                titles.add(bookChapter.getTitle());
//                Log.d("+收到的章节追书神器章节", bookChapter.getLink());

            }

        }

//        Single.concat(chapterInfos)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                        new Subscriber<ChapterInfoBean>() {
//                            String title = titles.poll();
//
//                            @Override
//                            public void onSubscribe(Subscription s) {
//                                s.request(Integer.MAX_VALUE);
//                                mChapterSub = s;
//                            }
//
//                            @Override
//                            public void onNext(ChapterInfoBean chapterInfoBean) {
//                                //存储数据
//                                BookRepository.getInstance().saveChapterInfo(
//                                        bookId, title, chapterInfoBean.getBody()
//                                );
//                                mView.finishChapter();
//                                //将获取到的数据进行存储
//                                title = titles.poll();
//
//
//                                Log.e("+chapterBody", "title" + chapterInfoBean.getTitle() + titles + " " + chapterInfoBean.getBody());
//                            }
//
//                            @Override
//                            public void onError(Throwable t) {
//                                //只有第一个加载失败才会调用errorChapter
//                                if (bookChapters.get(0).getTitle().equals(title)) {
//                                    mView.errorChapter();
//                                }
//                                LogUtils.e(t);
//                            }
//
//                            @Override
//                            public void onComplete() {
//                            }
//                        }
//                );


        Single.concat(bookChapterSBeanByBiquge)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BookChapterBeanByBiquge>() {
                    String titleInBiquge = titlesInBiquge.poll();

                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Integer.MAX_VALUE);
                        mChapterSub = s;
                    }

                    @Override
                    public void onNext(BookChapterBeanByBiquge bookChapterBeanByBiquge) {
                        //存储数据
                        BookRepository.getInstance().saveChapterInfo(
                                bookId, titleInBiquge, bookChapterBeanByBiquge.getData().getContent()
                        );
                        Log.e("+chapterBody", "title" + titleInBiquge + titlesInBiquge + " " + bookChapterBeanByBiquge.getData().getContent());
                        mView.finishChapter();
                        //将获取到的数据进行存储
                        titleInBiquge = titlesInBiquge.poll();

                    }

                    @Override
                    public void onError(Throwable t) {
                        //只有第一个加载失败才会调用errorChapter
                        if (bookChapters.get(0).getTitle().equals(titleInBiquge)) {
                            mView.errorChapter();
                        }
                        LogUtils.e(t);

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    @Override
    public void detachView() {
        super.detachView();
        if (mChapterSub != null) {
            mChapterSub.cancel();
        }
    }

}
