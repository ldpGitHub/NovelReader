package com.example.ldp.ireader.ui.adapter;

import com.example.ldp.ireader.model.bean.SectionBean;
import com.example.ldp.ireader.ui.adapter.view.SectionHolder;
import com.example.ldp.ireader.ui.base.adapter.BaseListAdapter;
import com.example.ldp.ireader.ui.base.adapter.IViewHolder;

/**
 * Created by ldp on 17-4-16.
 */

public class SectionAdapter extends BaseListAdapter<SectionBean> {
    @Override
    protected IViewHolder<SectionBean> createViewHolder(int viewType) {
        return new SectionHolder();
    }
}
