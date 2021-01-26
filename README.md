###### 目录说明
**contract** ：  V层和P层之间方法锲约，方便管理V和P之间的方法调用。
**entity** ：实体类，管理网络请求的数据封装类。
**fragment：** Android的fragment类。
**http**：网络请求工具类封装。
**model：** mvp的M层，负责数据处理。
**presenter：** mvp的P层，负责业务逻辑的处理。
**tools：** 工具类
**view：** mvp的V层，负责数据呈现。

###### M层基本描述

```java
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
```
这里使用聚合的api接口测试，使用时如果没有请求次数，可以换成自己申请的key。

###### V层基本描述

Activity：
```java
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
```
Fragment:

```java
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
```
这里采用Activity+Fragment的原因是为了减少视图层的代码，方便阅读和修改。

###### P层基本描述

```java
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

```
P层向M层请求数据，返回给NetworkCallBack ，在onSuccess把返回的数据传给V层呈现。

###### http工具类
RequestControl：

```java

public class RequestControl {
    private static final String SOCKET_TIME_OUT = "网络连接超时";
    private static final String CONNECT = "请求服务器异常";
    private static final String JSON_EXCEPTION = "数据解析错误";
    private static final String UNKNOWN = "未知错误";
    private static final String NETWORK_EXCEPTION = "请检查网络连接是否正常";

    public static void request(final int what, final Call<ResponseBody> call, final Class<?> tClass, final NetworkCallBack callBack) {
        call.enqueue(new Callback<ResponseBody>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        ResponseBody body = response.body();
                        Gson gson = new Gson();
                        try {
                            String json = body.string();
                            if (!TextUtils.isEmpty(json)) {
                                Object data = gson.fromJson(json, tClass);
                                callBack.onSuccess(what,  data);
                            } else {
                                callBack.onFail(what, null);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        callBack.onFail(what, NETWORK_EXCEPTION);
                    }
                }
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    callBack.onFail(what, SOCKET_TIME_OUT);
                } else if (t instanceof ConnectException) {
                    callBack.onFail(what, CONNECT);
                } else if (t instanceof JsonSyntaxException) {
                    callBack.onFail(what, JSON_EXCEPTION + "\n" + t.toString());
                } else {
                    callBack.onFail(what, "[ " + what + " ]" + UNKNOWN + "\n" + t.toString());
                }
            }
        });
    }
}

```
数据请求封装，这里你可以根据实际的业务来修改。

RequestRouter：

```java
public interface RequestRouter {
    @GET("{url}")
    Call<ResponseBody> getRequestBody(@Path("url") String url, @Body RequestBody route, @QueryMap Map<String, Object> map);

    @GET("{url}")
    Call<ResponseBody> getRequestRouter(@Path("url") String url, @QueryMap Map<String, Object> map);

    @POST("{url}")
    Call<ResponseBody> postRequestBody(@Path("url") String url, @Body RequestBody route, @QueryMap Map<String, Object> map);

    @POST("{url}")
    Call<ResponseBody> postRequestRouter(@Path("url") String url, @FieldMap Map<String, Object> map);

    @Multipart
    @POST("{url}")
    Call<ResponseBody> uploadFilesMultipartBodyParts(@Path("url") String url, @Part List<MultipartBody.Part> partList);
}
```
这里封装了五种请求方式，可根据业务协议选择使用。

RetrofitService：

```java
    public static RequestRouter getAPIService(String url) {
        retrofit = new Retrofit.Builder()
                .baseUrl(url) //设置Base的访问路径
                .addConverterFactory(GsonConverterFactory.create(gsonConverterFactory)) //设置默认的解析库：Gson
                .client(defaultOkHttpClient())
                .build();
        return retrofit.create(RequestRouter.class);
    }
```
Retorfit2工具封装。

```java
 private static final Interceptor LoggingInterceptor = new Interceptor() {
        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Request request = chain.request();
            long t1 = System.nanoTime();
            okhttp3.Response response = chain.proceed(chain.request());
            long t2 = System.nanoTime();
            okhttp3.MediaType mediaType = response.body().contentType();
            String content = response.body().string();
            Log.i("TAG", "-----LoggingInterceptor----- :\nrequest url:" +
                    request.url() + "\ntime:" + (t2 - t1) / 1e6d + "\nbody:" + content + "\n");
            return response.newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Cache-Control", "no-cache")
                    .addHeader("token", mToken)
                    .body(okhttp3.ResponseBody.create(mediaType, content))
                    .build();
        }
    };
```
拦截器，这里可以添加请求的token，以及查看数据返回。

```java
ClientManager.setCertificates(getAssets().open("xxx.cer"), getAssets().open("xxxx.cer"))
```
添加https证书，内部已处理Android9.0明文请求以及https请求问题。

直接通过git拉取到本地文件目录下，修改包名即可使用。
**框架地址：[https://github.com/Joker-say/MVP](https://github.com/Joker-say/MVP)**

**不足之处还望各位大佬指点**