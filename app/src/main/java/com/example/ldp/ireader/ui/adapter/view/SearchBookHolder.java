package com.example.ldp.ireader.ui.adapter.view;

import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ldp.ireader.R;
import com.example.ldp.ireader.model.bean.packages.SearchBookPackageByBiquge;
import com.example.ldp.ireader.ui.base.adapter.ViewHolderImpl;

/**
 * Created by ldp on 17-6-2.
 */

public class SearchBookHolder extends ViewHolderImpl<SearchBookPackageByBiquge.DataBean> {

    private ImageView mIvCover;
    private TextView mTvName;
    private TextView mTvBrief;

    @Override
    public void initView() {
        mIvCover = findById(R.id.search_book_iv_cover);
        mTvName = findById(R.id.search_book_tv_name);
        mTvBrief = findById(R.id.search_book_tv_brief);
    }

    @Override
    public void onBind(SearchBookPackageByBiquge.DataBean book, int pos) {
        //显示图片
//        Glide.with(getContext())
//                .load(Constant.IMG_BASE_URL + data.getCover())
//                .placeholder(R.drawable.ic_book_loading)
//                .error(R.drawable.ic_load_error)
//                .into(mIvCover);
        Glide.with(getContext())
                .load(book.getImg())
                .placeholder(R.drawable.ic_book_loading)
                .error(R.drawable.ic_load_error)
                .into(mIvCover);
        mTvName.setText(book.getName());

//        mTvBrief.setText(getContext().getString(R.string.nb_search_book_brief,
//                data.getLatelyFollower(),data.getRetentionRatio(),data.getAuthor()));
        mTvBrief.setText(getContext().getString(R.string.nb_search_book_brief,
               book.getCName(),book.getLastChapter(),book.getAuthor()));
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_search_book;
    }
}
