package com.example.ldp.ireader.ui.adapter.view;

import android.widget.ImageView;
import android.widget.TextView;

import com.example.ldp.ireader.R;
import com.example.ldp.ireader.model.bean.SectionBean;
import com.example.ldp.ireader.ui.base.adapter.ViewHolderImpl;

/**
 * Created by ldp on 17-4-16.
 */

public class SectionHolder extends ViewHolderImpl<SectionBean>{

    private ImageView mIvIcon;
    private TextView mTvName;

    @Override
    public void initView(){
        mIvIcon =findById(R.id.section_iv_icon);
        mTvName =findById(R.id.section_tv_name);
    }

    @Override
    public void onBind(SectionBean value, int pos) {
        mIvIcon.setImageResource(value.getDrawableId());
        mTvName.setText(value.getName());
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_section;
    }
}
