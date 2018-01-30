package com.copasso.cocobook.model.bean.packages;

import com.copasso.cocobook.model.bean.BaseBean;
import com.copasso.cocobook.model.bean.SwipePictureBean;

import java.util.List;

public class SwipePicturePackage extends BaseBean{

    private List<SwipePictureBean> data;

    public List<SwipePictureBean> getData() {
        return data;
    }

    public void setData(List<SwipePictureBean> data) {
        this.data = data;
    }

}
