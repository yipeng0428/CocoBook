package com.copasso.cocobook.model.gen;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

import org.greenrobot.greendao.AbstractDaoMaster;
import org.greenrobot.greendao.database.StandardDatabase;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseOpenHelper;
import org.greenrobot.greendao.identityscope.IdentityScopeType;


// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/**
 * Master of DAO (schema version 4): knows all DAOs.
 */
public class DaoMaster extends AbstractDaoMaster {
    public static final int SCHEMA_VERSION = 4;

    /** Creates underlying database table using DAOs. */
    public static void createAllTables(Database db, boolean ifNotExists) {
        AuthorBeanDao.createTable(db, ifNotExists);
        BookChapterBeanDao.createTable(db, ifNotExists);
        BookCommentBeanDao.createTable(db, ifNotExists);
        BookHelpfulBeanDao.createTable(db, ifNotExists);
        BookHelpsBeanDao.createTable(db, ifNotExists);
        BookRecordBeanDao.createTable(db, ifNotExists);
        BookReviewBeanDao.createTable(db, ifNotExists);
        BookSearchBeanDao.createTable(db, ifNotExists);
        CollBookBeanDao.createTable(db, ifNotExists);
        DownloadTaskBeanDao.createTable(db, ifNotExists);
        ReviewBookBeanDao.createTable(db, ifNotExists);
    }

    /** Drops underlying database table using DAOs. */
    public static void dropAllTables(Database db, boolean ifExists) {
        AuthorBeanDao.dropTable(db, ifExists);
        BookChapterBeanDao.dropTable(db, ifExists);
        BookCommentBeanDao.dropTable(db, ifExists);
        BookHelpfulBeanDao.dropTable(db, ifExists);
        BookHelpsBeanDao.dropTable(db, ifExists);
        BookRecordBeanDao.dropTable(db, ifExists);
        BookReviewBeanDao.dropTable(db, ifExists);
        BookSearchBeanDao.dropTable(db, ifExists);
        CollBookBeanDao.dropTable(db, ifExists);
        DownloadTaskBeanDao.dropTable(db, ifExists);
        ReviewBookBeanDao.dropTable(db, ifExists);
    }

    /**
     * WARNING: Drops all table on Upgrade! Use only during development.
     * Convenience method using a {@link DevOpenHelper}.
     */
    public static DaoSession newDevSession(Context context, String name) {
        Database db = new DevOpenHelper(context, name).getWritableDb();
        DaoMaster daoMaster = new DaoMaster(db);
        return daoMaster.newSession();
    }

    public DaoMaster(SQLiteDatabase db) {
        this(new StandardDatabase(db));
    }

    public DaoMaster(Database db) {
        super(db, SCHEMA_VERSION);
        registerDaoClass(AuthorBeanDao.class);
        registerDaoClass(BookChapterBeanDao.class);
        registerDaoClass(BookCommentBeanDao.class);
        registerDaoClass(BookHelpfulBeanDao.class);
        registerDaoClass(BookHelpsBeanDao.class);
        registerDaoClass(BookRecordBeanDao.class);
        registerDaoClass(BookReviewBeanDao.class);
        registerDaoClass(BookSearchBeanDao.class);
        registerDaoClass(CollBookBeanDao.class);
        registerDaoClass(DownloadTaskBeanDao.class);
        registerDaoClass(ReviewBookBeanDao.class);
    }

    public DaoSession newSession() {
        return new DaoSession(db, IdentityScopeType.Session, daoConfigMap);
    }

    public DaoSession newSession(IdentityScopeType type) {
        return new DaoSession(db, type, daoConfigMap);
    }

    /**
     * Calls {@link #createAllTables(Database, boolean)} in {@link #onCreate(Database)} -
     */
    public static abstract class OpenHelper extends DatabaseOpenHelper {
        public OpenHelper(Context context, String name) {
            super(context, name, SCHEMA_VERSION);
        }

        public OpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory, SCHEMA_VERSION);
        }

        @Override
        public void onCreate(Database db) {
            Log.i("greenDAO", "Creating tables for schema version " + SCHEMA_VERSION);
            createAllTables(db, false);
        }
    }

    /** WARNING: Drops all table on Upgrade! Use only during development. */
    public static class DevOpenHelper extends OpenHelper {
        public DevOpenHelper(Context context, String name) {
            super(context, name);
        }

        public DevOpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(Database db, int oldVersion, int newVersion) {
            Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
            dropAllTables(db, true);
            onCreate(db);
        }
    }

}
