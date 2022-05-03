package com.ellison.websocket.request;



/**
 * @author ellison
 * @date 2018年11月26日
 * @desc 用一句话描述这个类的作用
 */
public class ConnectBean extends WsRequest {


    /**
     * _method_ : pong
     */

    private String token;
    private String userId;

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
