package com.example.ldp.ireader.presenter.contract;

import com.example.ldp.ireader.model.bean.BookListBean;
import com.example.ldp.ireader.model.bean.CollBookBean;
import com.example.ldp.ireader.model.bean.HotCommentBean;
import com.example.ldp.ireader.model.bean.BookDetailBeanInBiquge;
import com.example.ldp.ireader.ui.base.BaseContract;

import java.util.List;

/**
 * Created by ldp on 17-5-4.
 */

public interface BookDetailContract {
    interface View extends BaseContract.BaseView{
        /**
         * @param bean
         */
//        void finishRefresh(BookDetailBean bean);
      void finishRefresh(BookDetailBeanInBiquge bean);

        void finishHotComment(List<HotCommentBean> beans);
        void finishRecommendBookList(List<BookListBean> beans);

        void waitToBookShelf();
        void errorToBookShelf();
        void succeedToBookShelf();
    }

    interface Presenter extends BaseContract.BasePresenter<View>{
        void refreshBookDetail(String bookId);
        //添加到书架上
        void addToBookShelf(CollBookBean collBook);
    }
}
