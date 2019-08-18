package com.example.ldp.ireader.ui.adapter;

import android.content.Context;

import com.example.ldp.ireader.model.bean.SortBookBean;
import com.example.ldp.ireader.ui.adapter.view.BookSortListHolder;
import com.example.ldp.ireader.ui.base.adapter.IViewHolder;
import com.example.ldp.ireader.widget.adapter.WholeAdapter;

/**
 * Created by ldp on 17-5-3.
 */

public class BookSortListAdapter extends WholeAdapter<SortBookBean>{
    public BookSortListAdapter(Context context, Options options) {
        super(context, options);
    }

    @Override
    protected IViewHolder<SortBookBean> createViewHolder(int viewType) {
        return new BookSortListHolder();
    }
}
