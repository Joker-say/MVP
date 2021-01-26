package com.hitt.mvptoframenwork.contract;

import com.hitt.mvptoframenwork.entity.JokerEntity;
import com.hitt.mvptoframenwork.presenter.BasePresenter;
import com.hitt.mvptoframenwork.view.BaseView;


public interface JokerContract {

    interface View extends BaseView<Presenter> {
        void getListData(JokerEntity result);
    }

    interface Presenter extends BasePresenter {
        void getList();
        void getDetailed();
    }
}
