package com.example.ldp.ireader.ui.adapter.view;

import android.widget.TextView;

import com.example.ldp.ireader.R;
import com.example.ldp.ireader.ui.base.adapter.ViewHolderImpl;

/**
 * Created by ldp on 17-5-5.
 */

public class TagGroupHolder extends ViewHolderImpl<String> {
    private TextView mTvGroupName;

    @Override
    public void initView() {
        mTvGroupName = findById(R.id.tag_group_name);
    }

    @Override
    public void onBind(String value, int pos) {
        mTvGroupName.setText(value);
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_tag_group;
    }
}
