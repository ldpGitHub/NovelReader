package com.example.ldp.ireader.ui.adapter;

import com.example.ldp.ireader.model.bean.CommentBean;
import com.example.ldp.ireader.ui.adapter.view.CommentHolder;
import com.example.ldp.ireader.ui.base.adapter.BaseListAdapter;
import com.example.ldp.ireader.ui.base.adapter.IViewHolder;

/**
 * Created by ldp on 17-4-29.
 */

public class GodCommentAdapter extends BaseListAdapter<CommentBean>{
    @Override
    protected IViewHolder<CommentBean> createViewHolder(int viewType) {
        return new CommentHolder(true);
    }
}
