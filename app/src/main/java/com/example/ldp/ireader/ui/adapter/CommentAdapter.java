package com.example.ldp.ireader.ui.adapter;

import android.content.Context;

import com.example.ldp.ireader.model.bean.CommentBean;
import com.example.ldp.ireader.ui.adapter.view.CommentHolder;
import com.example.ldp.ireader.ui.base.adapter.IViewHolder;
import com.example.ldp.ireader.widget.adapter.WholeAdapter;

/**
 * Created by ldp on 17-4-29.
 */

public class CommentAdapter extends WholeAdapter<CommentBean> {

    public CommentAdapter(Context context, Options options) {
        super(context, options);
    }

    @Override
    protected IViewHolder<CommentBean> createViewHolder(int viewType) {
        return new CommentHolder(false);
    }
}
