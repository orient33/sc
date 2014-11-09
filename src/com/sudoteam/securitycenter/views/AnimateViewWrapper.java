package com.sudoteam.securitycenter.Views;

import android.view.View;

/**
 * Created by huayang on 14-11-5.
 */
public class AnimateViewWrapper {


    View targetView;

    public View getTargetView() {
        return targetView;
    }

    public void setTargetView(View targetView) {
        this.targetView = targetView;
    }

    public int getWidth() {
        return targetView.getLayoutParams().width;
    }

    public void setWidth(int width) {

        targetView.getLayoutParams().width = width;
        targetView.requestLayout();
    }

    public int getHeight() {
        return targetView.getLayoutParams().height;
    }

    public void setHeight(int height) {

        targetView.getLayoutParams().height = height;
        targetView.requestLayout();

    }

    public AnimateViewWrapper(View view) {


        targetView = view;
    }

}
