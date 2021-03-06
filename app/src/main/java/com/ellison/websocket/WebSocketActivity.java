package com.ellison.websocket;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ellison.websocket.request.SendMsgBean;
import com.ellison.websocket.request.WsStringRequest;
import com.ellison.websocket.socket.SocketConstants;
import com.ellison.websocket.socket.WebSocketService;
import com.ellison.websocket.socket.WsListener;
import com.ellison.websocket.socket.WsStatusListener;
import com.ellison.websocket.utils.AppUtils;
import com.ellison.websocket.utils.RxLifecycleUtils;
import com.google.gson.Gson;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import io.reactivex.annotations.Nullable;
import io.reactivex.functions.Consumer;
import okhttp3.Response;
import okhttp3.WebSocket;
import okio.ByteString;

/**
 * @author ellison
 */
public class WebSocketActivity extends AppCompatActivity {

    public static final String TAG = "WebSocketActivity";

    /**
     * 来判断是否Service是否连接
     */
    private boolean isConnected = false;

    /**
     * ws://192.168.101.227:1024/channel
     * WebSocket服务
     */
    @Nullable
    private WebSocketService mWebSocketService;
    private EditText mEt;
    private Button mBtnConnect;
    private Button mBtnDisConnect;
    private TextView mTvInfo;
    private EditText mEtData;
    private Button mBtnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEt = findViewById(R.id.et);
        mEt.setText("ws://192.168.101.227:1024/channel");
      //   mEt.setText("ws://192.168.101.9:1024/channel");

        mBtnConnect = findViewById(R.id.btn_connect);
        mBtnDisConnect = findViewById(R.id.btn_dis_connect);
        mTvInfo = findViewById(R.id.tv_info);
        mTvInfo.setMovementMethod(ScrollingMovementMethod.getInstance());

        mEtData = findViewById(R.id.et_data);
        mBtnSend = findViewById(R.id.btn_send);

        RxView.clicks(mBtnConnect)
                .throttleFirst(1, TimeUnit.SECONDS)
                .as(RxLifecycleUtils.bindLifecycle(this))
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if (TextUtils.isEmpty(mEt.getText().toString()) || isConnected) {
                            return;
                        }
                        // 连接结果
                        isConnected = bindService(WebSocketService.createIntent(WebSocketActivity.this, mEt.getText().toString()), wsConnection, BIND_AUTO_CREATE);
                    }
                });

        RxView.clicks(mBtnDisConnect)
                .throttleFirst(1, TimeUnit.SECONDS)
                .as(RxLifecycleUtils.bindLifecycle(this))
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if (mWebSocketService != null) {
                            mWebSocketService.prepareShutDown();
                            isConnected = false;
                        }
                        String s = mTvInfo.getText().toString();
                        if (!TextUtils.isEmpty(s)) {
                            mTvInfo.setText(s + "\n" + "关闭连接");
                        } else {
                            mTvInfo.setText("关闭连接");
                        }

                    }
                });

        RxView.clicks(mBtnSend)
                .throttleFirst(1, TimeUnit.SECONDS)
                .as(RxLifecycleUtils.bindLifecycle(this))
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if (TextUtils.isEmpty(mEtData.getText().toString()) || !isConnected) {
                            return;
                        }
                        String s = mTvInfo.getText().toString();
                        if (!TextUtils.isEmpty(s)) {
                            mTvInfo.setText(s + "\n" + "客户端发送数据: " + mEtData.getText().toString());
                        } else {
                            mTvInfo.setText("客户端发送数据: " + mEtData.getText().toString());
                        }
                        String packageName = AppUtils.getPackageName(MyApp.getApplication());
                        int uid = AppUtils.getUid(MyApp.getApplication(), packageName);
                        SendMsgBean mSendMsgBean = new SendMsgBean();
                        mSendMsgBean.setToken("");
                        mSendMsgBean.setModel("accountShare");
                        mSendMsgBean.setUserId("minApp113988");
                        mSendMsgBean.setShareId("minApp103043");
                        mSendMsgBean.setUid(String.valueOf(uid));
                        mWebSocketService.sendRequest(mSendMsgBean);  //不是 这个是点击发送的具体信息  这个没什么问题
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, throwable.toString());
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unRegisterSocketAndBroadcast();
    }

    /**
     * 将Socket置空 取消Broadcast接受
     */
    private void unRegisterSocketAndBroadcast() {
        if (mWebSocketService != null) {
            mWebSocketService.prepareShutDown();
            if (isConnected) {
                unbindService(wsConnection);
                isConnected = false;
            }
        }
    }

    /**
     * Ws连接成功回调
     */
    private ServiceConnection wsConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "Service connected.");
            mWebSocketService = ((WebSocketService.ServiceBinder) service).getService();

            mWebSocketService.registerListener(SocketConstants.ResponseType.RESPONSE_STRING_MESSAGE, new WsListener() {
                @Override
                public void handleData(Object o) {
                    String s = mTvInfo.getText().toString();
                    if (!TextUtils.isEmpty(s)) {
                        mTvInfo.setText(s + "\n" + "接收服务器数据: " + o.toString());
                    } else {
                        mTvInfo.setText("接受服务器数据: " + o.toString());
                    }
                }
            });

            mWebSocketService.setWebSocketStatusListener(new WsStatusListener() {
                @Override
                public void onOpen(WebSocket webSocket, Response response) {
                    isConnected = true;
                }

                @Override
                public void onMessage(WebSocket webSocket, String text) {

                }

                @Override
                public void onMessage(WebSocket webSocket, ByteString bytes) {

                }

                @Override
                public void onClosing(WebSocket webSocket, int code, String reason) {
                }

                @Override
                public void onClosed(WebSocket webSocket, int code, String reason) {
                    isConnected = false;
                }

                @Override
                public void onFailure(WebSocket webSocket, Throwable t, @javax.annotation.Nullable Response response) {
                    isConnected = false;
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "Service disconnected.");
            mWebSocketService = null;
        }
    };
}
