package com.example.ldp.ireader.model.remote;

import com.example.ldp.ireader.model.bean.BookChapterBeanByBiquge;
import com.example.ldp.ireader.model.bean.BookDetailBeanInBiquge;
import com.example.ldp.ireader.model.bean.packages.BookChapterPackageByBiquge;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by ldp on 18-10-19.
 */

public interface BookApiByBiqugeInfo {


    /**
     * 获取书籍详情
     * 如 https://infos.1122dh.com/BookFiles/Html/449/448419/info.html
     *
     * @param bookId:书籍ID
     * @param bookIdSalt:书籍ID去掉后三位后加1,如448419 448 + 1 = 449
     * @return
     */
    @GET("/BookFiles/Html/{bookIdSalt}/{bookId}/info.html")
    Single<BookDetailBeanInBiquge> geBookDetailByBiquge(@Path("bookIdSalt") String bookIdSalt,@Path("bookId") String bookId);

    /**
     * 获取目录
     * 如 https://infos.1122dh.com/BookFiles/Html/249/248872/index.html
     *
     * @param bookId:书籍ID
     * @param bookIdSalt:书籍ID去掉后三位后加1,如448419 448 + 1 = 449
     * @return
     */
    @GET("/BookFiles/Html/{bookIdSalt}/{bookId}/index.html")
    Single<BookChapterPackageByBiquge> getChapterListByBiquge(@Path("bookIdSalt") String bookIdSalt,@Path("bookId") String bookId);



}
