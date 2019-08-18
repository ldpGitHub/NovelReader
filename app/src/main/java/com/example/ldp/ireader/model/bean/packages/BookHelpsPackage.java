package com.example.ldp.ireader.model.bean.packages;

import com.example.ldp.ireader.model.bean.BaseBean;
import com.example.ldp.ireader.model.bean.BookHelpsBean;

import java.util.List;

/**
 * Created by ldp on 17-4-20.
 */

public class BookHelpsPackage extends BaseBean {

    private List<BookHelpsBean> helps;

    public List<BookHelpsBean> getHelps() {
        return helps;
    }

    public void setHelps(List<BookHelpsBean> helps) {
        this.helps = helps;
    }

}
