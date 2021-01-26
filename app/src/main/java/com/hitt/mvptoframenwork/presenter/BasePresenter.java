package com.hitt.mvptoframenwork.presenter;


public interface BasePresenter {

    /**
     * 回收视图防止内存泄漏
     */
    void dischargeView();
}
