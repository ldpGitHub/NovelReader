package com.example.ldp.ireader.presenter.contract;

import com.example.ldp.ireader.model.bean.packages.SearchBookPackageByBiquge;
import com.example.ldp.ireader.ui.base.BaseContract;

import java.util.List;

/**
 * Created by ldp on 17-6-2.
 */

public interface SearchContract extends BaseContract {

    interface View extends BaseView{
        void finishHotWords(List<String> hotWords);
        void finishKeyWords(List<String> keyWords);
//        void finishBooks(List<SearchBookPackage.BooksBean> books);
        void finishBooks(List<SearchBookPackageByBiquge.DataBean> dataBeans);


        void errorBooks();
    }

    interface Presenter extends BasePresenter<View>{
        void searchHotWord();
        //搜索提示
        void searchKeyWord(String query);
        //搜索书籍
        void searchBook(String query);
    }
}
