package com.ellison.websocket.request;


/**
 * @author ellison
 * @date 2018年11月26日
 * @desc 用一句话描述这个类的作用
 */
public class SendMsgBean extends WsRequest {

    private String model;
    private String shareId;
    private String token;
    private String userId;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

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
