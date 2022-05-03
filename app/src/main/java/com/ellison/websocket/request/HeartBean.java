package com.ellison.websocket.request;



/**
 * @author ellison
 * @date 2018年11月26日
 * @desc 用一句话描述这个类的作用
 */
public class HeartBean extends WsRequest {


    private String connect;


    public String getConnect() {
        return connect;
    }

    public void setConnect(String connect) {
        this.connect = connect;
    }
}
