package com.example.ldp.ireader.ui.adapter;

import com.example.ldp.ireader.model.bean.BookSortBean;
import com.example.ldp.ireader.ui.adapter.view.BookSortHolder;
import com.example.ldp.ireader.ui.base.adapter.BaseListAdapter;
import com.example.ldp.ireader.ui.base.adapter.IViewHolder;

/**
 * Created by ldp on 17-4-23.
 */

public class BookSortAdapter extends BaseListAdapter<BookSortBean>{

    @Override
    protected IViewHolder<BookSortBean> createViewHolder(int viewType) {
        return new BookSortHolder();
    }
}
