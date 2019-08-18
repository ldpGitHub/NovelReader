package com.example.ldp.ireader.model.local;

import com.example.ldp.ireader.model.bean.AuthorBean;
import com.example.ldp.ireader.model.bean.ReviewBookBean;
import com.example.ldp.ireader.model.bean.BookCommentBean;
import com.example.ldp.ireader.model.bean.BookHelpfulBean;
import com.example.ldp.ireader.model.bean.BookHelpsBean;
import com.example.ldp.ireader.model.bean.BookReviewBean;

import java.util.List;

/**
 * Created by ldp on 17-4-28.
 */

public interface DeleteDbHelper {
    void deleteBookComments(List<BookCommentBean> beans);
    void deleteBookReviews(List<BookReviewBean> beans);
    void deleteBookHelps(List<BookHelpsBean> beans);
    void deleteAuthors(List<AuthorBean> beans);
    void deleteBooks(List<ReviewBookBean> beans);
    void deleteBookHelpful(List<BookHelpfulBean> beans);
    void deleteAll();
}
