package com.hitt.mvptoframenwork;


import com.hitt.mvptoframenwork.fragment.JokerFragment;
import com.hitt.mvptoframenwork.presenter.JokerPresenter;
import com.hitt.mvptoframenwork.tools.ActivityUtils;

public class JokerActivity extends BaseActivity {
    public static final String EXTRA_ID = "JOKER_ID";

    @Override
    protected int bindLayout() {
        return R.layout.activity_joker;
    }

    @Override
    protected void initView() {
        JokerFragment jokerFragment = (JokerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.jokerFrame);

        String id = getIntent().getStringExtra(EXTRA_ID);
        if (jokerFragment == null) {
            jokerFragment = JokerFragment.newInstance(id);
        }
        new JokerPresenter(jokerFragment);
        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                jokerFragment, R.id.jokerFrame);
    }

    @Override
    protected void initData() {

    }
}