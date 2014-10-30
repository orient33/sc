package com.sudoteam.securitycenter.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by huayang on 14-10-30.
 * add a var called "active" ,use this var to
 * check if we can click the button
 */
public class SingleClickButton extends Button {

    public SingleClickButton(Context context) {
        super(context);
    }

    public SingleClickButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SingleClickButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private boolean active = true;

    public boolean isActive(){
        return active;
    }

    public void setActive(boolean active){
        this.active = active;
    }
}
