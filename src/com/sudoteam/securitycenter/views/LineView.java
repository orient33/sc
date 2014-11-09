package com.sudoteam.securitycenter.Views;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sudoteam.securitycenter.Entity.*;

import java.util.ArrayList;
import java.util.List;

import com.sudoteam.securitycenter.R;
import com.sudoteam.securitycenter.utils.*;

/**
 * Created by huayang on 14-11-5.
 *
 */
public class LineView extends LinearLayout {

    private View problemView ;
    /**
     * use map to store it ! and get by tag ,tag is key
     */
    private List<ItemData> datas = new ArrayList<ItemData>();

    private Context context;

    private float itemHeightInPixels;

    public LineView(Context context) {
        super(context);

        init(context);
    }

    public LineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LineView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context){
        this.context = context;
    }


    public ItemData createAData(boolean isLast){

//        View view = ViewUtils.getView(context,R.layout.line_item);
        View view = ViewUtils.getView(context,R.layout.antivirus_item_show);

        ItemData d = new ItemData();


        d.setTheLastOne(isLast);


        d.setContentHeight(ViewUtils.getContentViewHeight(context,2*30));
        d.setView(view);

        setViewHeight(21,21,56);

        return d;
    }

    public void setProblemView(View view){
        this.problemView = view;
    }

    protected View getProblemView(){return problemView;}

    /**
     * add a data
     * @param data
     * @return
     */
    public LineView addItemData(ItemData data){

        datas.add(data);

        return this;
    }

    private int upLineHeight,
                downLineHeight,
                mainItemHeight,
                problemListHeight;

    /**
     * (mainItemHeight - 14)/2 = up = dowm
     * change mainItemHeight need to change xml also .
     * @param upLineHeight
     * @param downLineHeight
     * @param mainItemHeight
     * @return
     */
    public LineView setViewHeight(int upLineHeight,int downLineHeight,int mainItemHeight){

        this.upLineHeight = upLineHeight;
        this.downLineHeight = downLineHeight;
        this.mainItemHeight = mainItemHeight;
        this.itemHeightInPixels = ViewUtils.getContentViewHeight(context,mainItemHeight);
        return this;
    }


    public LineView addViewByAnimation(ItemData iData){

        View view = iData.getView();
        AnimateViewHolder holder = new AnimateViewHolder();

        holder.upLine = (Button) view.findViewById(R.id.up_line);
        holder.downLine = (Button) view.findViewById(R.id.down_line);
        holder.indicatorContainer = (LinearLayout) view.findViewById(R.id.indicator_container);
        holder.contentContainer = (RelativeLayout) view.findViewById(R.id.content_container);
        holder.downLineHeight = (Button) view.findViewById(R.id.down_line_height);
        holder.indicator = (Button) view.findViewById(R.id.incicator);
        holder.problemList = (LinearLayout)view.findViewById(R.id.app_problem_list);

        /**set name*/
        TextView appName = (TextView) holder.contentContainer.findViewById(R.id.app_name);
        appName.setText(iData.getTitle());

        if(iData.isTheLastOne()){
            holder.downLine.setVisibility(INVISIBLE);
        }

        addView(view);

        View problemView = getProblemView();

        if(problemView != null)
            holder.problemList.addView(problemView);

        holder.problemList.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        int height = holder.problemList.getMeasuredHeight();
        /**save the height value*/
        problemListHeight = height;
        Log.i("Tag", "measured height = " + height);

        /**set the height = 0, prepare for next step,the animation to show*/
        ViewGroup.LayoutParams params = holder.problemList.getLayoutParams();
        params.height = 0;
        holder.problemList.setLayoutParams(params);

        /**exec animation below */

        Animation zoom = AnimationUtils.loadAnimation(context,R.anim.zoom_out);
        holder.indicator.setAnimation(zoom);
        zoom.start();

        Animation rotate = AnimationUtils.loadAnimation(context,R.anim.check_rotate);
        holder.indicatorContainer.setAnimation(rotate);
        rotate.start();


        ObjectAnimator.ofInt(new AnimateViewWrapper(holder.upLine),
                "height",
                (int)ViewUtils.getContentViewHeight(context,upLineHeight))
                .setDuration(300)
                .start();

        ObjectAnimator.ofInt(new AnimateViewWrapper(holder.downLineHeight),
                "height",
                (int)ViewUtils.getContentViewHeight(context,downLineHeight) + problemListHeight)
                .setDuration(0)
                .start();

        ObjectAnimator.ofInt(new AnimateViewWrapper(holder.downLine),
                "height",
                (int)ViewUtils.getContentViewHeight(context,downLineHeight) + problemListHeight)
                .setDuration(300)
                .start();

        ObjectAnimator.ofInt(new AnimateViewWrapper(holder.problemList),"height",problemListHeight)
                .setDuration(300)
                .start();

        AnimateViewWrapper wrapper = new AnimateViewWrapper(holder.contentContainer);

        //PropertyValuesHolder w = PropertyValuesHolder.ofInt("width",(int)ViewUtils.getContentViewHeight(context,250));
        PropertyValuesHolder h = PropertyValuesHolder.ofInt("height",(int) (int)ViewUtils.getContentViewHeight(context,mainItemHeight));

        ObjectAnimator contentAnim = ObjectAnimator
                .ofPropertyValuesHolder(wrapper,/*w,*/h);

        AccelerateDecelerateInterpolator adi = new AccelerateDecelerateInterpolator();

        contentAnim.setInterpolator(adi);
        contentAnim.setDuration(300);
        contentAnim.start();

        return this;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float y = event.getY();

        Log.i("Tag","itemHeightInPixels = " + itemHeightInPixels);
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP :

                /**
                 *  perform a click event ...
                 */
                int position = 0;
                if(itemHeightInPixels > 0)
                    /**position from 0 to end*/
                    position = (int) (y / itemHeightInPixels);


                Log.i("Tag",", event y = " + y);
                Log.i("Tag","the position is : " + position);
                break;
        }

        Log.i("Tag","super.onTouchEvent(event) = " + super.onTouchEvent(event));

        /**
         *  return true for next action
         */
        return true/*super.onTouchEvent(event)*/;
    }

    interface OnItemClickListener {

        public void onItemClick(View view,Object data ,int position);
    }


    public final class AnimateViewHolder {

        Button upLine,downLine,downLineHeight,indicator;
        LinearLayout indicatorContainer,problemList;
        RelativeLayout contentContainer;

    }
}
