package com.hitt.mvptoframenwork.model;

import com.hitt.mvptoframenwork.entity.JokerEntity;
import com.hitt.mvptoframenwork.http.NetworkCallBack;
import com.hitt.mvptoframenwork.http.RequestControl;
import com.hitt.mvptoframenwork.http.RetrofitService;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class JokerModel extends BaseModel {

    public void getList(NetworkCallBack callBack) {
        Map<String, Object> map = new HashMap<>();
        map.put("key", "e181e60669ebaa69ef3fdecdf35bce05");
        map.put("sort", "desc");
        map.put("time", "1418816972");
        Call<ResponseBody> call = RetrofitService.getAPIService().getRequestRouter("joke/content/list.php", map);
        RequestControl.request(0x001, call, JokerEntity.class, callBack);
    }
}
