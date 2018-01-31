package com.copasso.cocobook.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import butterknife.BindView;
import com.bumptech.glide.Glide;
import com.copasso.cocobook.R;
import com.copasso.cocobook.model.bean.FeatureBean;
import com.copasso.cocobook.model.bean.SectionBean;
import com.copasso.cocobook.model.bean.SwipePictureBean;
import com.copasso.cocobook.model.type.FeatureType;
import com.copasso.cocobook.model.type.FindType;
import com.copasso.cocobook.presenter.BookStorePresenter;
import com.copasso.cocobook.presenter.contract.BookStoreContract;
import com.copasso.cocobook.ui.activity.*;
import com.copasso.cocobook.ui.adapter.FeatureAdapter;
import com.copasso.cocobook.ui.adapter.SquareAdapter;
import com.copasso.cocobook.ui.base.BaseMVPFragment;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouas666 on 18-1-23.
 * 讨论区
 */

public class BookDiscoverFragment extends BaseMVPFragment<BookStoreContract.Presenter> implements BookStoreContract.View {
    /***************view******************/
    @BindView(R.id.banner)
    Banner banner;
    @BindView(R.id.store_rv_find)
    RecyclerView storeRvFind;
    @BindView(R.id.store_rv_content)
    RecyclerView storeRvContent;

    private SquareAdapter mSquareAdapter;
    private FeatureAdapter mFeatureAdapter;

    List<SwipePictureBean> mSwipePictures;
    List<FeatureBean> mFeatures=new ArrayList<>();
    private List<String> mImages = new ArrayList<>();
    private List<String> mTitles = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_discover;
    }

    /***********************************init method*************************************************/

    @Override
    protected void initWidget(Bundle savedInstanceState) {

        //设置banner样式
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        //设置图片加载器
        banner.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                Glide.with(context).load(path).into(imageView);
            }
        });
        //设置banner动画效果
        banner.setBannerAnimation(Transformer.DepthPage);
        //设置自动轮播，默认为true
        banner.isAutoPlay(true);
        //设置轮播时间
        banner.setDelayTime(5000);
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.CENTER);

        mSquareAdapter = new SquareAdapter();
        storeRvFind.setHasFixedSize(true);
        storeRvFind.setLayoutManager(new GridLayoutManager(mContext,4));
        storeRvFind.setAdapter(mSquareAdapter);

        mFeatureAdapter = new FeatureAdapter();
        storeRvContent.setHasFixedSize(true);
        storeRvContent.setLayoutManager(new LinearLayoutManager(mContext));
        storeRvContent.setAdapter(mFeatureAdapter);

        setUpAdapter();
    }

    private void setUpAdapter() {

        ArrayList<SectionBean> finds = new ArrayList<>();
        for (FindType type : FindType.values()){
            finds.add(new SectionBean(type.getTypeName(),type.getIconId()));
        }
        mSquareAdapter.addItems(finds);

        for (FeatureType type : FeatureType.values()){
            mFeatures.add(new FeatureBean(type.getTypeId(),type.getTypeName()));
        }
        mFeatureAdapter.addItems(mFeatures);

    }


    /****************************click method********************************/

    @Override
    protected void initClick() {
        banner.setOnBannerListener(
                (pos) -> {
                    SwipePictureBean bean = mSwipePictures.get(pos);
                    if (bean.getType().equals("c-bookdetail"))
                        BookDetailActivity.startActivity(mContext, bean.getLink());
                    if (bean.getType().equals("c-booklist"))
                        BookListDetailActivity.startActivity(mContext, bean.getLink());
                });

        mSquareAdapter.setOnItemClickListener((view, pos) -> {
            //跳转
            switch (FindType.values()[pos]){
                case TOP:
                    startActivity(new Intent(getContext(),BillboardActivity.class));
                    break;
                case SORT:
                    startActivity(new Intent(getContext(), BookSortActivity.class));
                    break;
                case TOPIC:
                    startActivity(new Intent(getContext(), BookListActivity.class));
                    break;
            }
        });

        mFeatureAdapter.setOnItemClickListener((view, pos) -> {
            FeatureBean bean=mFeatureAdapter.getItem(pos);
            FeatureBookActivity.startActivity(mContext,bean.getTitle(),bean.get_id());
        });
    }

    @Override
    public void finishRefreshSwipePictures(List<SwipePictureBean> swipePictureBeans) {

        mSwipePictures = swipePictureBeans;

        for (SwipePictureBean bean : swipePictureBeans) {
            mImages.add(bean.getImg());
            mTitles.add(bean.getTitle());
        }
        //设置图片集合
        banner.setImages(mImages);
        //设置标题集合（当banner样式有显示title时）
        banner.setBannerTitles(mTitles);

        banner.start();
    }

    @Override
    public void finishRefreshFeatures(List<FeatureBean> featureBeans) {
        mFeatures = featureBeans;
        setUpAdapter();
    }

    @Override
    public void finishUpdate() {

    }

    @Override
    public void showErrorTip(String error) {

    }

    @Override
    protected void processLogic() {
        super.processLogic();
        mPresenter.refreshSwipePictures();
        //mPresenter.refreshFeatures();
    }

    @Override
    protected BookStoreContract.Presenter bindPresenter() {
        return new BookStorePresenter();
    }

    @Override
    public void showError() {

    }

    @Override
    public void complete() {

    }
}