package com.example.ldp.ireader.ui.adapter;

import com.example.ldp.ireader.ui.adapter.view.KeyWordHolder;
import com.example.ldp.ireader.ui.base.adapter.BaseListAdapter;
import com.example.ldp.ireader.ui.base.adapter.IViewHolder;

/**
 * Created by ldp on 17-6-2.
 */

public class KeyWordAdapter extends BaseListAdapter<String> {
    @Override
    protected IViewHolder<String> createViewHolder(int viewType) {
        return new KeyWordHolder();
    }
}
