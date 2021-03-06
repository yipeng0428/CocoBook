package com.thmub.cocobook.presenter;

import com.thmub.cocobook.model.bean.BookChapterBean;
import com.thmub.cocobook.model.bean.BookDetailBean;
import com.thmub.cocobook.model.bean.CollBookBean;
import com.thmub.cocobook.model.bean.DownloadTaskBean;
import com.thmub.cocobook.model.local.BookRepository;
import com.thmub.cocobook.model.server.RemoteRepository;
import com.thmub.cocobook.presenter.contract.BookShelfContract;
import com.thmub.cocobook.base.RxPresenter;
import com.thmub.cocobook.service.DownloadService;
import com.thmub.cocobook.utils.Constant;
import com.thmub.cocobook.utils.LogUtils;
import com.thmub.cocobook.utils.MD5Utils;
import com.thmub.cocobook.utils.RxUtils;
import com.thmub.cocobook.utils.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

/**
 * Created by zhouas666 on 18-2-8.
 */

public class BookShelfPresenter extends RxPresenter<BookShelfContract.View>
        implements BookShelfContract.Presenter {
    private static final String TAG = "BookShelfPresenter";

    @Override
    public void refreshCollBooks() {
        List<CollBookBean> collBooks = BookRepository.getInstance().getCollBooks();
        mView.finishRefresh(collBooks);
    }

    @Override
    public void createDownloadTask(CollBookBean collBookBean) {
        DownloadTaskBean task = new DownloadTaskBean();
        task.setTaskName(collBookBean.getTitle());
        task.setBookId(collBookBean.get_id());
        task.setBookChapters(collBookBean.getBookChapters());
        task.setLastChapter(collBookBean.getBookChapters().size());
        //下载书籍
        DownloadService.post(task);
    }


    @Override
    public void loadRecommendBooks(String gender) {
        addDisposable(RemoteRepository.getInstance()
                .getRecommendBooksByGender(gender)
                .doOnSuccess((collBooks) -> {
                    //更新目录
                    updateCategory(collBooks);
                    //异步存储到数据库中
                    BookRepository.getInstance().saveCollBooksWithAsync(collBooks);
                })
                .compose(RxUtils::toSimpleSingle)
                .subscribe(
                        beans -> {
                            mView.finishRefresh(beans);
                            mView.complete();
                        },
                        e -> {
                            //提示没有网络
                            LogUtils.e(e);
                            mView.showErrorTip(e.toString());
                            mView.complete();
                        }
                ));
    }

    @Override
    public void updateCollBooks(List<CollBookBean> collBookBeans) {
        if (collBookBeans == null || collBookBeans.isEmpty()) return;

        List<CollBookBean> collBooks = new ArrayList<>(collBookBeans);
        List<Single<BookDetailBean>> observables = new ArrayList<>(collBooks.size());
        for (int i=0;i<collBooks.size();i++){
            CollBookBean collBook = collBooks.get(i);
            //删除本地文件
            if (collBook.isLocal()) {
                collBooks.remove(i);
            } else {
                observables.add(RemoteRepository.getInstance().getBookDetail(collBook.get_id()));
            }
        }
        //zip可能不是一个好方法。
        //https://blog.csdn.net/baidu_34012226/article/details/52438902?locationNum=5&fps=1
        Single.zip(observables, objects -> {
            List<CollBookBean> newCollBooks = new ArrayList<>(objects.length);
            for (int i = 0; i < collBooks.size(); ++i) {
                CollBookBean oldCollBook = collBooks.get(i);
                CollBookBean newCollBook = ((BookDetailBean) objects[i]).getCollBookBean();
                //如果是oldBook是update状态，或者newCollBook与oldBook章节数不同
                if (oldCollBook.isUpdate() || !oldCollBook.getLastChapter().equals(newCollBook.getLastChapter())) {
                    newCollBook.setUpdate(true);
                } else {
                    newCollBook.setUpdate(false);
                }
                newCollBook.setLastRead(oldCollBook.getLastRead());
                newCollBooks.add(newCollBook);
                //存储到数据库中
                BookRepository.getInstance().saveCollBooks(newCollBooks);
            }
            return newCollBooks;
        })
                .compose(RxUtils::toSimpleSingle)
                .subscribe(new SingleObserver<List<CollBookBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onSuccess(List<CollBookBean> value) {
                        //因为切换夜间模式会调用onCreate()
                        if (mView!=null) {
                            mView.finishUpdate();
                            mView.complete();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        //提示没有网络
                        mView.showErrorTip(e.toString());
                        mView.complete();
                        LogUtils.e(e);
                    }
                });
    }

    //更新每个CollBook的目录
    private void updateCategory(List<CollBookBean> collBookBeans) {
        List<Single<List<BookChapterBean>>> observables = new ArrayList<>(collBookBeans.size());
        for (CollBookBean bean : collBookBeans) {
            observables.add(RemoteRepository.getInstance().getBookChapters(bean.get_id()));
        }
        Iterator<CollBookBean> it = collBookBeans.iterator();
        //执行在上一个方法中的子线程中
        //连接多个Single和Observable发射的数据
        Single.concat(observables)
                .subscribe(
                        chapterList -> {
                            for (BookChapterBean bean : chapterList) {
                                bean.setId(MD5Utils.strToMd5By16(bean.getLink()));
                            }

                            CollBookBean bean = it.next();
                            bean.setLastRead(StringUtils.dateConvert(System.currentTimeMillis(), Constant.FORMAT_BOOK_DATE));
                            bean.setBookChapters(chapterList);
                        }
                );
    }
}
