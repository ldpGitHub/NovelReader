package com.example.ldp.ireader.model.local;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.ldp.ireader.App;
import com.example.ldp.ireader.model.gen.DaoMaster;
import com.example.ldp.ireader.model.gen.DaoSession;

import java.io.File;
import java.io.IOException;

/**
 * Created by ldp on 17-4-26.
 */

public class DaoDbHelper {
    private static final String TAG = DaoDbHelper.class.getSimpleName();

    private static final String DB_NAME = "IReader_DB";

    private static volatile DaoDbHelper sInstance;
    private SQLiteDatabase mDb;
    private static DaoMaster mDaoMaster;
    private DaoSession mSession;

    private DaoDbHelper(){
        //封装数据库的创建、更新、删除
        DaoMaster.DevOpenHelper openHelper = new MyOpenHelper(App.getContext(),DB_NAME,null);
        //获取数据库
//        mDb = openHelper.getWritableDatabase();
        //封装数据库中表的创建、更新、删除
//        mDaoMaster = new DaoMaster(mDb);  //合起来就是对数据库的操作
        mDaoMaster =getDaoMaster(App.getContext());
        //对表操作的对象。
        mSession = mDaoMaster.newSession(); //可以认为是对数据的操作
    }


    public static DaoDbHelper getInstance(){
        if (sInstance == null){
            synchronized (DaoDbHelper.class){
                if (sInstance == null){
                    sInstance = new DaoDbHelper();
                }
            }
        }
        return sInstance;
    }


    /**
     * 获取DaoMaster
     *
     * @param context
     * @return
     */
    public static DaoMaster getDaoMaster(Context context) {

        if (mDaoMaster == null) {
            try{
                ContextWrapper wrapper = new ContextWrapper(context) {
                    /**
                     * 获得数据库路径，如果不存在，则创建对象对象
                     *
                     * @param name
                     */
                    @Override
                    public File getDatabasePath(String name) {
                        // 判断是否存在sd卡
                        boolean sdExist = android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState());
                        if (!sdExist) {// 如果不存在,
                            Log.d(TAG, "SD卡不存在，请加载SD卡");
                            return null;
                        } else {// 如果存在
                            // 获取sd卡路径
                            String dbDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
                            dbDir += "/iReader/Database";// 数据库所在目录
                            String dbPath = dbDir + "/" + name;// 数据库路径
//                            Log.d("yq","dbPath--->>"+dbPath);
                            // 判断目录是否存在，不存在则创建该目录
                            File dirFile = new File(dbDir);
                            if (!dirFile.exists()){
                                dirFile.mkdirs();
                                Log.d(TAG, "getDatabasePath: ");
                            }
                            // 数据库文件是否创建成功
                            boolean isFileCreateSuccess = false;
                            // 判断文件是否存在，不存在则创建该文件
                            File dbFile = new File(dbPath);
                            if (!dbFile.exists()) {
                                try {
                                    isFileCreateSuccess = dbFile.createNewFile();// 创建文件
                                    Log.d(TAG, "getDatabasePath: 创建文件 ");

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                isFileCreateSuccess = true;
                            }
                            // 返回数据库文件对象
                            if (isFileCreateSuccess) {
                                return dbFile;
                            } else {
                                return super.getDatabasePath(name);
                            }
                        }
                    }

                    /**
                     * 重载这个方法，是用来打开SD卡上的数据库的，android 2.3及以下会调用这个方法。
                     *
                     * @param name
                     * @param mode
                     * @param factory
                     */
                    @Override
                    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
                        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
                    }

                    /**
                     * Android 4.0会调用此方法获取数据库。
                     *
                     * @
                     *      int,
                     *      SQLiteDatabase.CursorFactory,
                     *      DatabaseErrorHandler)
                     * @param name
                     * @param mode
                     * @param factory
                     * @param errorHandler
                     */
                    @Override
                    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
                        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
                    }
                };

//                DaoMaster.OpenHelper helper = new MyGreenDaoOpenHelper(wrapper,"snid.db",null);
                DaoMaster.DevOpenHelper openHelper = new MyOpenHelper(wrapper,DB_NAME,null);
                mDaoMaster = new DaoMaster(openHelper.getWritableDatabase()); //获取未加密的数据库

//                Log.e(TAG, "getDaoMaster: " + daoMaster);
                //适用于加密的数据库
//                MyEncryptedSQLiteOpenHelper helper = new MyEncryptedSQLiteOpenHelper(wrapper,"a.db",null);
//                daoMaster = new DaoMaster(helper.getEncryptedWritableDb("1234"));//获取可读写的加密数据库
                //daoMaster = new DaoMaster(helper.getEncryptedReadableDb("1234"));//获取只读的加密数据库

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return mDaoMaster;
    }


    public DaoSession getSession(){
        return mSession;
    }

    public SQLiteDatabase getDatabase(){
        return mDb;
    }

    public DaoSession getNewSession(){
        return mDaoMaster.newSession();
    }
}
