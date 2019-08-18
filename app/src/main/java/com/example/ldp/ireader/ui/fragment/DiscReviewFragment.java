package com.example.ldp.ireader.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.example.ldp.ireader.R;
import com.example.ldp.ireader.RxBus;
import com.example.ldp.ireader.event.SelectorEvent;
import com.example.ldp.ireader.model.bean.BookReviewBean;
import com.example.ldp.ireader.model.flag.BookDistillate;
import com.example.ldp.ireader.model.flag.BookSort;
import com.example.ldp.ireader.model.flag.BookType;
import com.example.ldp.ireader.model.flag.CommunityType;
import com.example.ldp.ireader.presenter.DiscReviewPresenter;
import com.example.ldp.ireader.presenter.contract.DiscReviewContract;
import com.example.ldp.ireader.ui.activity.DiscDetailActivity;
import com.example.ldp.ireader.ui.adapter.DiscReviewAdapter;
import com.example.ldp.ireader.ui.base.BaseMVPFragment;
import com.example.ldp.ireader.utils.Constant;
import com.example.ldp.ireader.widget.adapter.WholeAdapter;
import com.example.ldp.ireader.widget.itemdecoration.DividerItemDecoration;
import com.example.ldp.ireader.widget.refresh.ScrollRefreshRecyclerView;

import java.util.List;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by ldp on 17-4-21.
 */

public class DiscReviewFragment extends BaseMVPFragment<DiscReviewContract.Presenter> implements DiscReviewContract.View {
    private static final String BUNDLE_BOOK = "bundle_book";
    private static final String BUNDLE_SORT = "bundle_sort";
    private static final String BUNDLE_DISTILLATE = "bundle_distillate";
    /*******************View**********************/
    @BindView(R.id.scroll_refresh_rv_content)
    ScrollRefreshRecyclerView mRvContent;
    /*******************Object*********************/
    private DiscReviewAdapter mDiscReviewAdapter;
    /*******************Params**********************/
    private BookSort mBookSort = BookSort.DEFAULT;
    private BookType mBookType = BookType.ALL;
    private BookDistillate mDistillate = BookDistillate.ALL;
    private int mStart = 0;
    private int mLimited = 20;

    /**********************init method****************************/
    @Override
    protected int getContentId() {
        return R.layout.fragment_scroll_refresh_list;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        if (savedInstanceState != null) {
            mBookType = (BookType) savedInstanceState.getSerializable(BUNDLE_BOOK);
            mBookSort = (BookSort) savedInstanceState.getSerializable(BUNDLE_SORT);
            mDistillate = (BookDistillate) savedInstanceState.getSerializable(BUNDLE_DISTILLATE);
        }
    }

    @Override
    protected void initWidget(Bundle savedInstanceState) {
        super.initWidget(savedInstanceState);
        setUpAdapter();
    }

    private void setUpAdapter() {
        mRvContent.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvContent.addItemDecoration(new DividerItemDecoration(getContext()));
        mDiscReviewAdapter = new DiscReviewAdapter(getContext(), new WholeAdapter.Options());
        mRvContent.setAdapter(mDiscReviewAdapter);
    }

    @Override
    protected DiscReviewContract.Presenter bindPresenter() {
        return new DiscReviewPresenter();
    }

    /*************************click method************************/

    @Override
    protected void initClick() {
        super.initClick();

        mRvContent.setOnRefreshListener(() -> refreshData());
        mDiscReviewAdapter.setOnLoadMoreListener(
                () -> {
                    mPresenter.loadingBookReview(mBookSort, mBookType, mStart, mLimited, mDistillate);
                }
        );
        mDiscReviewAdapter.setOnItemClickListener(
                (view, pos) -> {
                    BookReviewBean bean = mDiscReviewAdapter.getItem(pos);
                    String detailId = bean.get_id();
                    DiscDetailActivity.startActivity(getContext(), CommunityType.REVIEW, detailId);
                }
        );

        addDisposable(RxBus.getInstance()
                .toObservable(Constant.MSG_SELECTOR, SelectorEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (event) -> {
                            mBookSort = event.sort;
                            mBookType = event.type;
                            mDistillate = event.distillate;
                            refreshData();
                        }
                ));
    }

    /****************************logic method*********************************/
    @Override
    protected void processLogic() {
        super.processLogic();
        //首次自动刷新
        mRvContent.startRefresh();
        mPresenter.firstLoading(mBookSort, mBookType, mStart, mLimited, mDistillate);
    }

    private void refreshData() {
        mStart = 0;
        mRvContent.startRefresh();
        mPresenter.refreshBookReview(mBookSort, mBookType, mStart, mLimited, mDistillate);
    }

    /****************************rewrite method******************************************/
    @Override
    public void finishRefresh(List<BookReviewBean> beans) {
        mDiscReviewAdapter.refreshItems(beans);
        mStart = beans.size();
    }

    @Override
    public void finishLoading(List<BookReviewBean> beans) {
        mDiscReviewAdapter.addItems(beans);
        mStart += beans.size();
    }

    @Override
    public void showErrorTip() {
        mRvContent.showTip();
    }


    @Override
    public void showError() {
        mDiscReviewAdapter.showLoadError();
    }

    @Override
    public void complete() {
        mRvContent.finishRefresh();
    }

    /****************************lifecycle method************************************/
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(BUNDLE_BOOK, mBookType);
        outState.putSerializable(BUNDLE_SORT, mBookSort);
        outState.putSerializable(BUNDLE_DISTILLATE, mDistillate);
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.saveBookReview(mDiscReviewAdapter.getItems());
    }
}
