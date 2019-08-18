package com.example.ldp.ireader.ui.adapter;

import com.example.ldp.ireader.model.bean.BillBookBean;
import com.example.ldp.ireader.ui.adapter.view.BillBookHolder;
import com.example.ldp.ireader.ui.base.adapter.BaseListAdapter;
import com.example.ldp.ireader.ui.base.adapter.IViewHolder;

/**
 * Created by ldp on 17-5-3.
 */

public class BillBookAdapter extends BaseListAdapter<BillBookBean> {
    @Override
    protected IViewHolder<BillBookBean> createViewHolder(int viewType) {
        return new BillBookHolder();
    }
}
