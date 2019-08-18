package com.example.ldp.ireader.ui.adapter;

import com.example.ldp.ireader.model.bean.HotCommentBean;
import com.example.ldp.ireader.ui.adapter.view.HotCommentHolder;
import com.example.ldp.ireader.ui.base.adapter.BaseListAdapter;
import com.example.ldp.ireader.ui.base.adapter.IViewHolder;

/**
 * Created by ldp on 17-5-4.
 */

public class HotCommentAdapter extends BaseListAdapter<HotCommentBean>{
    @Override
    protected IViewHolder<HotCommentBean> createViewHolder(int viewType) {
        return new HotCommentHolder();
    }
}
