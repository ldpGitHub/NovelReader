package com.example.ldp.ireader.ui.adapter;

import android.content.Context;

import com.example.ldp.ireader.model.bean.BookReviewBean;
import com.example.ldp.ireader.ui.adapter.view.DiscReviewHolder;
import com.example.ldp.ireader.ui.base.adapter.IViewHolder;
import com.example.ldp.ireader.widget.adapter.WholeAdapter;

/**
 * Created by ldp on 17-4-21.
 */

public class DiscReviewAdapter extends WholeAdapter<BookReviewBean> {

    public DiscReviewAdapter(Context context, Options options) {
        super(context, options);
    }

    @Override
    protected IViewHolder<BookReviewBean> createViewHolder(int viewType) {
        return new DiscReviewHolder();
    }
}
