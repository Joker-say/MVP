package com.hitt.mvptoframenwork.view;

public interface BaseView<T> {
    /**
     * 显示加载进度
     * @param s 文本
     */
    void showDialog(String s);

    /**
     * 结束加载
     */
    void visibleDialog();

    /**
     * 异常
     * @param o
     */
    void onError(Object o);
    void setPresenter(T presenter);
}
