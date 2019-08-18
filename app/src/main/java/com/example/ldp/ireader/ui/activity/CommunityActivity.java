package com.example.ldp.ireader.ui.activity;

import android.support.v7.widget.Toolbar;

import com.example.ldp.ireader.R;
import com.example.ldp.ireader.ui.base.BaseActivity;

/**
 * Created by ldp on 17-5-26.
 */

public class CommunityActivity extends BaseActivity{

    @Override
    protected int getContentId() {
        return R.layout.activity_community;
    }

    @Override
    protected void setUpToolbar(Toolbar toolbar) {
        super.setUpToolbar(toolbar);
        getSupportActionBar().setTitle("社区");
    }
}
