package com.example.mochy_acer1.weixincheck.Entity;

/**
 * Created by mochy-acer1 on 2016/6/28.
 */
public class WxGetMoudle {
    private int id;
    private int did;
    private int type;
    private String operateTime;
    private String createDate;
    private String equipmentName;
    private String date;
    private String url;
    private String updateTime;
    private int state;

    public void setId(int id) {
        this.id = id;
    }

    public void setDid(int did) {
        this.did = did;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setoperateTime(String oprerateTime) {
        this.operateTime = oprerateTime;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public int getDid() {
        return did;
    }

    public int getType() {
        return type;
    }

    public String getoperateTime() {
        return operateTime;
    }

    public String getCreateDate() {
        return createDate;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public String getDate() {
        return date;
    }

    public String getUrl() {
        return url;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public int getState() {
        return state;
    }

    @Override
    public String toString() {
        return "{" +
                "Id=" + id +
                ", Did='" + did + '\'' +
                ", Type='" + type + '\'' +
                ", OperateTime='" + operateTime + '\'' +
                ", CreateDate='" + createDate + '\'' +
                ", EquipmentName='" + equipmentName + '\'' +
                ", Date='" + date + '\'' +
                ", Url='" + url + '\'' +
                ", UpdateTime='" + updateTime + '\'' +
                ", State='" + state + '\'' +
                '}';
    }
}
