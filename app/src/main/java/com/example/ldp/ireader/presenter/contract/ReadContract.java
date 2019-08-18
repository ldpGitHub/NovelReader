package com.example.ldp.ireader.presenter.contract;

import com.example.ldp.ireader.model.bean.BookChapterBean;
import com.example.ldp.ireader.ui.base.BaseContract;
import com.example.ldp.ireader.widget.page.TxtChapter;

import java.util.List;

/**
 * Created by ldp on 17-5-16.
 */

public interface ReadContract extends BaseContract{
    interface View extends BaseContract.BaseView {
        void showCategory(List<BookChapterBean> bookChapterList,String bookId ,boolean isBiqugeLoaded);
        void finishChapter();
        void errorChapter();
    }

    interface Presenter extends BaseContract.BasePresenter<View>{
        void loadCategory(String bookId);
        void loadChapter(String bookId,List<TxtChapter> bookChapterList);
        void loadCategoryInBiquge(String bookId);

    }
}
