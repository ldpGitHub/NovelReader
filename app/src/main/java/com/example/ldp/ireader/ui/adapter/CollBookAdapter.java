package com.example.ldp.ireader.ui.adapter;

import com.example.ldp.ireader.model.bean.CollBookBean;
import com.example.ldp.ireader.ui.adapter.view.CollBookHolder;
import com.example.ldp.ireader.ui.base.adapter.IViewHolder;
import com.example.ldp.ireader.widget.adapter.WholeAdapter;

/**
 * Created by ldp on 17-5-8.
 */

public class CollBookAdapter extends WholeAdapter<CollBookBean> {

    @Override
    protected IViewHolder<CollBookBean> createViewHolder(int viewType) {
        return new CollBookHolder();
    }

}
