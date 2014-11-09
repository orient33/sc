package com.sudoteam.securitycenter.Entity;

import android.view.View;

/**
 * Created by huayang on 14-11-5.
 */
public class ItemData {

    private boolean isTheLastOne;

    private String title;

    private float contentHeight;

    private boolean anim;

    private View view;

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public boolean isTheLastOne() {
        return isTheLastOne;
    }

    public void setTheLastOne(boolean isTheLastOne) {
        this.isTheLastOne = isTheLastOne;
    }

    public boolean isAnim() {
        return anim;
    }

    public void setAnim(boolean anim) {
        this.anim = anim;
    }

    public float getContentHeight() {
        return contentHeight;
    }

    public void setContentHeight(float contentHeight) {
        this.contentHeight = contentHeight;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
