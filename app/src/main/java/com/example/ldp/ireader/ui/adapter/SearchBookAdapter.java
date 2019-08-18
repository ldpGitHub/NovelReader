package com.example.ldp.ireader.ui.adapter;

import com.example.ldp.ireader.model.bean.packages.SearchBookPackageByBiquge;
import com.example.ldp.ireader.ui.adapter.view.SearchBookHolder;
import com.example.ldp.ireader.ui.base.adapter.BaseListAdapter;
import com.example.ldp.ireader.ui.base.adapter.IViewHolder;

/**
 * Created by ldp on 17-6-2.
 */

public class SearchBookAdapter extends BaseListAdapter<SearchBookPackageByBiquge.DataBean>{
    @Override
    protected IViewHolder<SearchBookPackageByBiquge.DataBean> createViewHolder(int viewType) {
        return new SearchBookHolder();
    }
}
