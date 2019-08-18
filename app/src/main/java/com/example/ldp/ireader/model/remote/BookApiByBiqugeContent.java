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

public interface BookApiByBiqugeContent {

    /**
     * 获取章节
     * 例如 https://content.1122dh.com/BookFiles/Html/9/8728/5477722.html
     * @param bookIdSalt:书籍ID salt
     * @param bookId:书籍ID
     * @param chapterId:章节名
     * @return
     */
    @GET("/BookFiles/Html/{bookIdSalt}/{bookId}/{chapterId}.html")
    Single<BookChapterBeanByBiquge> getChapterByBiquge(@Path("bookIdSalt") String bookIdSalt,@Path("bookId") String bookId, @Path("chapterId") String chapterId);

}
