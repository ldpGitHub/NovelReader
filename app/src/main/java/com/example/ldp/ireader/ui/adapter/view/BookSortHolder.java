package com.example.ldp.ireader.ui.adapter.view;

import android.widget.TextView;

import com.example.ldp.ireader.R;
import com.example.ldp.ireader.model.bean.BookSortBean;
import com.example.ldp.ireader.ui.base.adapter.ViewHolderImpl;

/**
 * Created by ldp on 17-4-23.
 */

public class BookSortHolder extends ViewHolderImpl<BookSortBean>{

    private TextView mTvType;
    private TextView mTvCount;

    @Override
    public void initView() {
        mTvType = findById(R.id.sort_tv_type);
        mTvCount = findById(R.id.sort_tv_count);
    }

    @Override
    public void onBind(BookSortBean value, int pos) {
        mTvType.setText(value.getName());
        mTvCount.setText(getContext().getResources().getString(R.string.nb_sort_book_count,value.getBookCount()));
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_sort;
    }
}
