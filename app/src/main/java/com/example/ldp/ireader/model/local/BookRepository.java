package com.example.ldp.ireader.model.local;

import android.util.Log;

import com.example.ldp.ireader.model.bean.BookChapterBean;
import com.example.ldp.ireader.model.bean.BookRecordBean;
import com.example.ldp.ireader.model.bean.ChapterInfoBean;
import com.example.ldp.ireader.model.bean.CollBookBean;
import com.example.ldp.ireader.model.gen.BookChapterBeanDao;
import com.example.ldp.ireader.model.gen.BookRecordBeanDao;
import com.example.ldp.ireader.model.gen.CollBookBeanDao;
import com.example.ldp.ireader.model.gen.DaoSession;
import com.example.ldp.ireader.model.gen.DownloadTaskBeanDao;
import com.example.ldp.ireader.utils.BookManager;
import com.example.ldp.ireader.utils.Constant;
import com.example.ldp.ireader.utils.FileUtils;
import com.example.ldp.ireader.utils.IOUtils;
import com.example.ldp.ireader.utils.StringUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

/**
 * Created by ldp on 17-5-8.
 * 存储关于书籍内容的信息(CollBook(收藏书籍),BookChapter(书籍列表),ChapterInfo(书籍章节),BookRecord(记录))
 */

public class BookRepository {
    private static final String TAG = "CollBookManager";
    private static volatile BookRepository sInstance;
    private DaoSession mSession;
    private CollBookBeanDao mCollBookDao;
    private BookRepository(){
        mSession = DaoDbHelper.getInstance()
                .getSession();
        mCollBookDao = mSession.getCollBookBeanDao();
    }

    public static BookRepository getInstance(){
        if (sInstance == null){
            synchronized (BookRepository.class){
                if (sInstance == null){
                    sInstance = new BookRepository();
                }
            }
        }
        return sInstance;
    }

    //存储已收藏书籍
    public void saveCollBookWithAsync(CollBookBean bean){
        Log.d(TAG, "启动异步存储  saveCollBookWithAsync: " + bean);
        //启动异步存储
        mSession.startAsyncSession()
                .runInTx(
                        () -> {
                            if (bean.getBookChapters() != null){
                                // 存储BookChapterBean
                                mSession.getBookChapterBeanDao()
                                        .insertOrReplaceInTx(bean.getBookChapters());

                                Log.d(TAG, "saveCollBookWithAsync: "+"章节存储  " +  bean.getBookChapters());
                            }
                            //存储CollBook (确保先后顺序，否则出错)
                            //表示当前CollBook已经阅读
                            Log.d(TAG, "saveCollBookWithAsync: a");

                            bean.setIsUpdate(false);
                            Log.d(TAG, "saveCollBookWithAsync: b");

                            if (null == bean.getLastRead()||"".equals(bean.getLastRead())||"null".equals(bean.getLastRead()))
                            {
                                Log.d(TAG, "saveCollBookWithAsync: c");

                                bean.setLastRead(StringUtils.
                                        dateConvert(System.currentTimeMillis(), Constant.FORMAT_BOOK_DATE));
                                Log.d(TAG, "saveCollBookWithAsync: d");

                            }

//                            bean.setLastRead(StringUtils.
//                                        dateConvert(System.currentTimeMillis(), Constant.FORMAT_BOOK_DATE));
//                            Log.d(TAG, "saveCollBookWithAsync: 0");
                            //直接更新
                            CollBookBean collBookBeanOrigin = BookRepository.getInstance().getCollBook(bean.get_id());
                            if(null != collBookBeanOrigin) {
                                bean.setBookIdInBiquge(collBookBeanOrigin.getBookIdInBiquge());
                            }
                            Log.d(TAG, "saveCollBookWithAsync: 1");
                            BookRepository.getInstance().saveCollBook(bean);
//                            mCollBookDao.insertOrReplaceInTx(bean);
                            Log.d(TAG, "saveCollBookWithAsync: "+"存储完成      " + bean.getAuthor() +bean.getTitle()+bean.getShortIntro());
                            List<CollBookBean> collBooksTest = mCollBookDao
                                    .queryBuilder()
                                    .orderDesc(CollBookBeanDao.Properties.LastRead)
                                    .list();
                            for (CollBookBean collBookBean: collBooksTest ) {
                                Log.d(TAG, "+saveCollBookWithAsync存储后: "+"进行存储" +   collBookBean.getTitle());


                            }
                        }
                );
    }
    /**
     * 异步存储。
     * 同时保存BookChapter
     * @param beans
     */
    public void saveCollBooksWithAsync(List<CollBookBean> beans){
        mSession.startAsyncSession()
                .runInTx(
                        () -> {
                            Log.d(TAG, "111saveCollBookWithAsync : "+"进行存储" +  beans.toString());
                            for (CollBookBean bean : beans){
                                if (bean.getBookChapters() != null){
                                    //存储BookChapterBean(需要修改，如果存在id相同的则无视)
                                    mSession.getBookChapterBeanDao()
                                            .insertOrReplaceInTx(bean.getBookChapters());
                                }
                            }
                            //存储CollBook (确保先后顺序，否则出错)
                            for (CollBookBean bookBean: beans) {
                                CollBookBean collBookBeanOrigin = BookRepository.getInstance().getCollBook(bookBean.get_id());
                                bookBean.setBookIdInBiquge(collBookBeanOrigin.getBookIdInBiquge());
                            }
                            mCollBookDao.insertOrReplaceInTx(beans);
                        }
                );
    }

    public void saveCollBook(CollBookBean bean){
        Log.d(TAG, "saveCollBook : "+"进行存储 " +  bean.toString());
        CollBookBean collBookBeanOrigin = BookRepository.getInstance().getCollBook(bean.get_id());
        if(null != collBookBeanOrigin) {
            bean.setBookIdInBiquge(collBookBeanOrigin.getBookIdInBiquge());
        }
        mSession.clear();
        Log.d(TAG, "2saveCollBook : "+"进行存储" +  bean.toString());

        mCollBookDao.insertOrReplace(bean);
        Log.d(TAG, "3saveCollBook : "+"进行存储" +  bean.toString());

        for (int i = 0; i <800 ; i++) {
         i++;
        }
        Log.d(TAG, "进行存储后结果saveCollBook : "+"" +   mCollBookDao.queryBuilder().list());

    }
//    public void saveBook(BookDetailBeanInBiquge bean){
//        Log.d(TAG, "22saveCollBookWithAsync : "+"进行存储" +  bean.toString());
//        CollBookBean collBookBeanOrigin = BookRepository.getInstance().getCollBook(bean.getData().getId()+"");
//        if(null != collBookBeanOrigin) {
//            bean.setBookIdInBiquge(collBookBeanOrigin.getBookIdInBiquge());
//        }
//        mCollBookDao.insertOrReplace(bean);
//    }

    public void saveCollBooks(List<CollBookBean> beans){
        Log.d(TAG, "saveCollBooks : "+"进行存储" +  beans.toString());
        for (CollBookBean bookBean: beans) {
            CollBookBean collBookBeanOrigin = BookRepository.getInstance().getCollBook(bookBean.get_id());
            if(null != collBookBeanOrigin) {
                bookBean.setBookIdInBiquge(collBookBeanOrigin.getBookIdInBiquge());
            }
        }
        mCollBookDao.insertOrReplaceInTx(beans);
    }

    /**
     * 异步存储BookChapter
     * @param beans
     */
    public void saveBookChaptersWithAsync(List<BookChapterBean> beans){
        mSession.startAsyncSession()
                .runInTx(
                        () -> {

                            //存储BookChapterBean
                            mSession.getBookChapterBeanDao()
                                    .insertOrReplaceInTx(beans);
                            for (BookChapterBean bookChapterBean: beans ) {
                                Log.d("+存储", "saveBookChaptersWithAsync: "+bookChapterBean.getTitle());

                            }

                            Log.d(TAG, "saveBookChaptersWithAsync: "+"进行存储");
                        }
                );
    }

    /**
     * 存储章节
     * @param folderName
     * @param fileName
     * @param content
     */
    public void saveChapterInfo(String folderName,String fileName,String content){
        File file = BookManager.getBookFile(folderName, fileName);
        //获取流并存储
        Writer writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            IOUtils.close(writer);
        }
    }

    public void saveBookRecord(BookRecordBean bean){
        mSession.getBookRecordBeanDao()
                .insertOrReplace(bean);
    }

    /*****************************get************************************************/
    public CollBookBean getCollBook(String bookId){
        CollBookBean bean = mCollBookDao.queryBuilder()
                .where(CollBookBeanDao.Properties._id.eq(bookId))
                .unique();
        return bean;
    }


    public  List<CollBookBean> getCollBooks(){
        List<CollBookBean> mTemp = mCollBookDao
                .queryBuilder()
                .orderDesc(CollBookBeanDao.Properties.LastRead)
                .list();
        for (CollBookBean item : mTemp) {
            Log.d(TAG, "getCollBooksitem: " +item.getTitle() + item.getLastRead() );
        }
        return mCollBookDao
                .queryBuilder()
                .orderDesc(CollBookBeanDao.Properties.LastRead)
                .list();
    }



    //获取书籍列表
    public Single<List<BookChapterBean>> getBookChaptersInRx(String bookId){
        return Single.create(e -> {
            List<BookChapterBean> beans = mSession
                    .getBookChapterBeanDao()
                    .queryBuilder()
                    .where(BookChapterBeanDao.Properties.BookId.eq(bookId))
                    .list();
            e.onSuccess(beans);
        });
    }

    //获取阅读记录
    public BookRecordBean getBookRecord(String bookId){
        return mSession.getBookRecordBeanDao()
                .queryBuilder()
                .where(BookRecordBeanDao.Properties.BookId.eq(bookId))
                .unique();
    }

    //TODO:需要进行获取编码并转换的问题
    public ChapterInfoBean getChapterInfoBean(String folderName,String fileName){
        File file = new File(Constant.BOOK_CACHE_PATH + folderName
                + File.separator + fileName + FileUtils.SUFFIX_NB);
        if (!file.exists()) {
            return null;
        }
        Reader reader = null;
        String str = null;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);
            while ((str = br.readLine()) != null){
                sb.append(str);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            IOUtils.close(reader);
        }

        ChapterInfoBean bean = new ChapterInfoBean();
        bean.setTitle(fileName);
        bean.setBody(sb.toString());
        return bean;
    }

    /************************************************************/

    /************************************************************/
    public Single<Void> deleteCollBookInRx(CollBookBean bean) {
        return Single.create(new SingleOnSubscribe<Void>() {
            @Override
            public void subscribe(SingleEmitter<Void> e) throws Exception {
                //查看文本中是否存在删除的数据
                deleteBook(bean.get_id());
                //删除任务
                deleteDownloadTask(bean.get_id());
                //删除目录
                deleteBookChapter(bean.get_id());
                //删除CollBook
                mCollBookDao.delete(bean);
                e.onSuccess(new Void());
            }
        });
    }

    //这个需要用rx，进行删除
    public void deleteBookChapter(String bookId){
        mSession.getBookChapterBeanDao()
                .queryBuilder()
                .where(BookChapterBeanDao.Properties.BookId.eq(bookId))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();
    }

    public void deleteCollBook(CollBookBean collBook){
        mCollBookDao.delete(collBook);
    }

    //删除书籍
    public void deleteBook(String bookId){
        FileUtils.deleteFile(Constant.BOOK_CACHE_PATH+bookId);
    }

    public void deleteBookRecord(String id){
        mSession.getBookRecordBeanDao()
                .queryBuilder()
                .where(BookRecordBeanDao.Properties.BookId.eq(id))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();
    }

    //删除任务
    public void deleteDownloadTask(String bookId){
        mSession.getDownloadTaskBeanDao()
                .queryBuilder()
                .where(DownloadTaskBeanDao.Properties.BookId.eq(bookId))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();
    }

    public DaoSession getSession(){
        return mSession;
    }
}
