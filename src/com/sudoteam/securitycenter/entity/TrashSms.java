
package com.sudoteam.securitycenter.entity;

import java.lang.String;
import com.lidroid.xutils.db.annotation.Table;

@Table(name = "trashsms_v2")
public class TrashSms{

    private int id;

    private String content;

    private String addrFrom;

    private String addrTo;

    private String recvTime;

    private int errorCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAddrFrom() {
        return addrFrom;
    }

    public void setAddrFrom(String addrFrom) {
        this.addrFrom = addrFrom;
    }

    public String getAddrTo() {
        return addrTo;
    }

    public void setAddrTo(String addrTo) {
        this.addrTo = addrTo;
    }

    public String getRecvTime() {
        return recvTime;
    }

    public void setRecvTime(String recvTime) {
        this.recvTime = recvTime;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}