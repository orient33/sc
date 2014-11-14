package com.sudoteam.securitycenter.entity;

/**
 * Created by huayang on 14-11-3.
 *
 */
public class SmsEntity {

    private String content;

    private boolean show;

    private String number ;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
