package com.example.mochy_acer1.weixincheck.Entity;

import java.io.Serializable;

/**
 * Created by mochy-acer1 on 2016/6/27.
 */
public class WxShare implements Serializable{
    private Long createTime;

    private String username;

    private String text;

    private String url;

    private String desc;

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "WxShare{" +
                "createTime=" + createTime +
                ", username='" + username + '\'' +
                ", text='" + text + '\'' +
                ", url='" + url + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }

}
