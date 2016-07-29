package com.example.mochy_acer1.weixincheck.Entity;

/**
 * Created by mochy-acer1 on 2016/6/27.
 */
public class WxUrl {
    private String url;
    private boolean result;

    public String getUrl() {
        return url;
    }

    public boolean getResult() {
        return result;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "WxUrl{" +
                "url=" + url +
                ", result='" + result +
                '}';
    }
}
