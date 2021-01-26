package com.hitt.mvptoframenwork.presenter;

import com.hitt.mvptoframenwork.contract.JokerContract;
import com.hitt.mvptoframenwork.entity.JokerEntity;
import com.hitt.mvptoframenwork.http.NetworkCallBack;
import com.hitt.mvptoframenwork.model.JokerModel;


public class JokerPresenter implements JokerContract.Presenter, NetworkCallBack {
    private JokerModel model;
    private JokerContract.View view;

    public JokerPresenter(JokerContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
        this.model = new JokerModel();
    }

    @Override
    public void getList() {
        view.showDialog("加载中");
        model.getList(this);
    }

    @Override
    public void getDetailed() {

    }

    @Override
    public void dischargeView() {
        view = null;
    }

    @Override
    public void onSuccess(int what, Object result) {
        if (result instanceof JokerEntity) {
            JokerEntity entity = (JokerEntity) result;
            view.getListData(entity);
        }
        view.visibleDialog();
    }

    @Override
    public void onFail(int what, String result) {
        view.onError(result);
        view.visibleDialog();
    }
}
