package com.sudoteam.securitycenter.Views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Handler;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sudoteam.securitycenter.R;
import com.sudoteam.securitycenter.utils.*;
/**
 * Created by huayang on 14-11-6.
 *
 */
public class BlurDialog extends Dialog {

    private Activity context;

    private Handler handler;

    private Bitmap bluredBm,
                   shotBm;

    private LinearLayout contenContainer;
    private LinearLayout contenContainerParent;
    private ImageView blurBg;


    public BlurDialog(Context context) {
        this(context,R.style.Translucent_NoTitle);
        init(context);

    }

    public BlurDialog(Context context, int theme) {
        super(context, theme);
        init(context);

    }

    public BlurDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        /**
         * dont use the super's onBackPressed() ,
         * because we need our own animation to dismiss dialog .
         */
        dissmissDialog();

    }

    private void init(Context context) {

        this.context = (Activity) context;
        handler = new Handler();
    }


    public void createDialog() {

        shotBm = takeScreenShot(context, 0);
        new Thread(new Runnable() {
            @Override
            public void run() {

                Log.i("Tag", "start time = " + System.currentTimeMillis());
                bluredBm = blurBitmap(shotBm,15.0f);

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        showDialog();

                    }
                });

            }
        }).start();

    }

    private void showDialog() {

        final Display display = context.getWindowManager().getDefaultDisplay();

        View v = ViewUtils.getView(context, R.layout.dialog_view);
        setContentView(v);

        blurBg = (ImageView) v.findViewById(R.id.blur_bg);
        blurBg.setImageBitmap(bluredBm);

        Animation anim = AnimationUtils.loadAnimation(context, R.anim.bg_in);
        blurBg.setAnimation(anim);
        anim.start();

        contenContainer = (LinearLayout) v.findViewById(R.id.dialog_content_container);
        contenContainerParent = (LinearLayout) v.findViewById(R.id.dialog_content_container_parent);

        /**
         * get subview from where you use ,this step must do !
         * so that you must override < onDataBind() > to tell me
         * what you want to show in the dialog .
         */
        View subView = onDataBind(v);
        if(subView != null)
            contenContainer.addView(subView);

        /**
         * measure the height of the content view which will to show ,
         * and compare with the 3/5 display.getHeight() .
         */
        contenContainer.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        ViewGroup.LayoutParams params = contenContainer.getLayoutParams();
        /**
         * always set the smallest height to contentContainer.
         * but the largest height is 3/5-height of fullscreen.
         * the fullscreen dont include statusbar and navigation(if have)
         */
        params.height = display.getHeight() >= contenContainer
                .getMeasuredHeight() ? contenContainer.getMeasuredHeight() : display.getHeight() * 3/5;

        contenContainer.setLayoutParams(params);
        Log.i("Tag","height = " + contenContainer.getMeasuredHeight());

        final Animation contentAnim = AnimationUtils.loadAnimation(context, R.anim.dialog_enter_anim);
        contenContainerParent.setAnimation(contentAnim);
        contentAnim.start();

        blurBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dissmissDialog();
            }
        });

        /**
         * set the dialog to fill fullscrenn
         */
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setGravity(Gravity.BOTTOM);
        lp.width = display.getWidth();
        lp.height = display.getHeight();
        lp.y = 0;

        getWindow().setAttributes(lp);

        show();

        Log.i("Tag", "end time = " + System.currentTimeMillis());


    }

    /**
     * exec the dismiss animation and destroy dialog
     */
    private void dissmissDialog(){

        blurBg.setVisibility(View.GONE);
        contenContainerParent.setVisibility(View.GONE);

        Animation aOut = AnimationUtils.loadAnimation(context, R.anim.bg_out);
        blurBg.setAnimation(aOut);
        aOut.start();

        Animation contentOut = AnimationUtils.loadAnimation(context, R.anim.dialog_exit_anim);
        contenContainerParent.setAnimation(contentOut);
        contentOut.start();

        aOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }


    /**
     * get data-bind view from sub class
     * MUST impements this method !
     * @param layoutView
     * @return
     */
    protected View onDataBind(View layoutView){
        return null;
    }


    /**
     * take a screenshot dont include statusbar and navigation(if have)
     * @param activity
     * @param subHeight
     * @return
     */
    private Bitmap takeScreenShot(Activity activity, int subHeight) {

        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();

        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay()
                .getHeight() - subHeight;

        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height
                - statusBarHeight);

        view.destroyDrawingCache();

        return b;
    }

    /**
     * use RenderScript to blur an bitmap
     * @param bitmap
     * @param radius
     * @return
     */
    public Bitmap blurBitmap(Bitmap bitmap,float radius) {

        //Let's create an empty bitmap with the same size of the bitmap we want to blur
        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        //Instantiate a new Renderscript
        RenderScript rs = RenderScript.create(context);

        //Create an Intrinsic Blur Script using the Renderscript
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        //Create the Allocations (in/out) with the Renderscript and the in/out bitmaps
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);

        //Set the radius of the blur
        blurScript.setRadius(radius);

        //Perform the Renderscript
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);

        //Copy the final bitmap created by the out Allocation to the outBitmap
        allOut.copyTo(outBitmap);

        //recycle the original bitmap
        bitmap.recycle();

        //After finishing everything, we destroy the Renderscript.
        rs.destroy();

       // Log.i("Tag", "after blur width = " + outBitmap.getWidth() + ", height = " + outBitmap.getHeight());
        return outBitmap;


    }


//    public static Bitmap fastblur(Context context, Bitmap sentBitmap, int radius) {
//
//
//        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
//
//        final RenderScript rs = RenderScript.create(context);
//        final Allocation input = Allocation.createFromBitmap(rs, sentBitmap, Allocation.MipmapControl.MIPMAP_NONE,
//                Allocation.USAGE_SCRIPT);
//        final Allocation output = Allocation.createTyped(rs, input.getType());
//        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
//        script.setRadius(radius /* e.g. 3.f */);
//        script.setInput(input);
//        script.forEach(output);
//        output.copyTo(bitmap);
//        return bitmap;
//
//
//    }



}
