package com.hitt.mvptoframenwork.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hitt.mvptoframenwork.BaseFragment;
import com.hitt.mvptoframenwork.R;
import com.hitt.mvptoframenwork.contract.JokerContract;
import com.hitt.mvptoframenwork.entity.JokerEntity;

import java.util.List;

public class JokerFragment extends BaseFragment implements JokerContract.View {
    @NonNull
    private static final String ARGUMENT_JOKER_ID = "JOKER_ID";
    private JokerContract.Presenter mPresenter;

    public static JokerFragment newInstance(@Nullable String id) {
        Bundle arguments = new Bundle();
        arguments.putString(ARGUMENT_JOKER_ID, id);
        JokerFragment fragment = new JokerFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    protected int bindLayout() {
        return R.layout.fragment_joker;
    }

    @Override
    protected void initView(View view) {
        Button loadBtn = view.findViewById(R.id.loading);
        loadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.getList();
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    public void getListData(JokerEntity result) {
        List<JokerEntity.ResultBean.DataBean> data = result.getResult().getData();
        for (JokerEntity.ResultBean.DataBean item : data) {
            Log.e("getListData", "getListData: " + item.getContent());
        }
    }

    @Override
    public void showDialog(String s) {
        Log.e("getListData", "显示进度: ");
    }

    @Override
    public void visibleDialog() {
        Log.e("getListData", "关闭进度: ");
    }

    @Override
    public void onError(Object t) {
        Log.e("getListData", "异常信息: ");
    }

    @Override
    public void setPresenter(JokerContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.dischargeView();
    }
}