
package com.sudoteam.securitycenter.entity;

import java.lang.String;
import com.lidroid.xutils.db.annotation.Table;

@Table(name = "blocknumber")
public class BlockNumbers {

    private int id;

    private String number;

    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}