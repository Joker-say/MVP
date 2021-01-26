package com.hitt.mvptoframenwork.http;

/**
 *  监听网络回调
 * Created by Shinelon on 2018/1/12.
 */

public interface NetworkCallBack {
    /**
     * 成功的回调对象
     *
     * @param what
     * @param result
     */
    void onSuccess(int what, Object result) ;

    /**
     * 失败的回调
     *
     * @param what
     * @param result
     */
    void onFail(int what, String result);

}
