package com.example.ldp.ireader.ui.adapter;

import android.content.Context;

import com.example.ldp.ireader.model.bean.BookCommentBean;
import com.example.ldp.ireader.ui.adapter.view.DiscCommentHolder;
import com.example.ldp.ireader.ui.base.adapter.IViewHolder;
import com.example.ldp.ireader.widget.adapter.WholeAdapter;

/**
 * Created by ldp on 17-4-20.
 */

public class DiscCommentAdapter extends WholeAdapter<BookCommentBean> {

    public DiscCommentAdapter(Context context, Options options) {
        super(context, options);
    }

    @Override
    protected IViewHolder<BookCommentBean> createViewHolder(int viewType) {
        return new DiscCommentHolder();
    }
}
