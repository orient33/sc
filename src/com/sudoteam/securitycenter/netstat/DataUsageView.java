package com.sudoteam.securitycenter.netstat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.sudoteam.securitycenter.R;
import com.sudoteam.securitycenter.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * 流量统计的图表, 横坐标为每个月的x号
 */
public class DataUsageView extends View {
    /**
     * 主背景颜色
     */
    private static final int BG_COLOR = 0xfffbfbfb;
    /**
     * 折线 以及 阴影区域的颜色
     */
    private static final int MAIN_LINE_COLOR = 0xffd53844;
    /**
     * 水平线 的颜色
     */
    private static final int HORIZONTAL_COLOR = Color.BLACK;
    /**
     * 垂直线的颜色
     */
    private static final int VIRTICAL_COLOR = 0xfffca393;
    /**
     * 文本的颜色
     */
    private static final int TEXT_COLOR = Color.WHITE;

    public DataUsageView(Context context) {
        super(context);
        init(context);
    }

    public DataUsageView(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    Paint mCurvePaint;//主曲线
    Paint mHorizoncalPaint, mVirtacalPaint, mTextPaint;
    Drawable mPaoPao;

    private void init(Context c) {
        mPaoPao = c.getResources().getDrawable(R.drawable.data_usage_paopao);
        mCurvePaint = new Paint(Paint.DITHER_FLAG);
        mCurvePaint.setStrokeWidth(8);
        mCurvePaint.setStrokeJoin(Paint.Join.ROUND);
        mCurvePaint.setDither(true);
        mCurvePaint.setColor(MAIN_LINE_COLOR);
        mCurvePaint.setAntiAlias(true);
        mCurvePaint.setStyle(Paint.Style.FILL);

        mHorizoncalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        PathEffect effect = new DashPathEffect(new float[]{3, 3, 3, 3}, 3);
        mHorizoncalPaint.setStyle(Paint.Style.STROKE);
        mHorizoncalPaint.setAntiAlias(true);
        mHorizoncalPaint.setPathEffect(effect);
        mHorizoncalPaint.setColor(HORIZONTAL_COLOR);
        mHorizoncalPaint.setAlpha(0x50);
        mHorizoncalPaint.setTextSize(30);

        mVirtacalPaint = new Paint();
        mVirtacalPaint.setColor(VIRTICAL_COLOR);
        mVirtacalPaint.setStyle(Paint.Style.STROKE);
        mVirtacalPaint.setStrokeWidth(5);

        mTextPaint = new Paint();
        mTextPaint.setStrokeWidth(2);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(TEXT_COLOR);
        mTextPaint.setTextSize(30);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    void bindData(List<Point> list) {
        mPoints.clear();
        int maxY = 0,maxX =0;
        for (Point du : list) {
            mPoints.add(new Point(du.x, du.y));
            Util.i("[DataUsageView]bindData() x=" + du.x + ", y=" + du.y);
            maxY = du.y;
            maxX = du.x;
        }
        //:TODO for test
        for (int i = 1; i < 6; ++i, maxY += 10)
            mPoints.add(new Point(++maxX, maxY));
        requestLayout();
    }

    Rect mRect = new Rect();
    List<Point> mPoints = new ArrayList<Point>();//数学意义上的坐标点 列表,按照x升序排列 且x为等差数列
    List<Point> mAdjustPoints = new ArrayList<Point>();//调整后 android的坐标点
    Path mCurvePath = new Path();
    int maxY, maxX;

    @Override
    protected void onMeasure(int w, int h) {// w=-2147483648, h=1073742724
        super.onMeasure(w, h);
        w = getMeasuredWidth();
        h = getMeasuredHeight();
        int minW = getSuggestedMinimumWidth(), minH = getSuggestedMinimumHeight();
        int width = 100 * mPoints.size();//should be max x.
        int ww = getPaddingLeft() + getPaddingRight() + (minW > width ? minW : width);
        int hh = getPaddingTop() + getPaddingBottom() + (minH > h ? minH : h);
        Util.i("onMeasure() set w = " + ww + ",,h = " + hh);
        setMeasuredDimension(ww, hh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        getDrawingRect(mRect);  //获取View的矩形范围,
        Util.i("onDraw()  Rect is " + mRect);
        canvas.drawColor(BG_COLOR);
        if (mPoints == null || mPoints.size() == 0)
            return;
        adjustPoint(canvas);//调整坐标点
        drawCurve(canvas);
        drawGrid(canvas);//draw grid
    }

    //根据数学上的坐标mPoints   计算出来android上的坐标mAdjustPoints
    private void adjustPoint(Canvas canvas) {
        maxY = -1;
        maxX = mPoints.get(mPoints.size() - 1).x;
        for (Point p : mPoints) {  //找出y最大值
            if (p.y > maxY)
                maxY = p.y;
        }
        int iy = (mRect.bottom - mRect.top) / maxY;  //单位1长度 对应的 高度
        int ix = (mRect.right - mRect.left) / maxX;  //单位1长度 对应的宽度

        int x0 = mRect.left;
        int newX = 0, newY = 0;
        mAdjustPoints.clear();
        for (Point p : mPoints) {
            newX = mRect.left + (p.x - x0) * ix;
            newY = mRect.bottom - (p.y * iy);
            mAdjustPoints.add(new Point(newX, newY));
        }
    }

    private void drawGrid(Canvas canvas) {
        int size = mAdjustPoints.size();
        int lineSize = 5;//画 5 条线
        int space = maxY / lineSize; //间隔 space表示y的等差值
        int iiy = (mRect.bottom - mRect.top) / lineSize; //间隔像素
        int x0 = mRect.left, x1 = mRect.right;
        for (int i = 0; i < size; ++i) {
            int y = mRect.bottom - (i * iiy);   //刻度
            canvas.drawText(" " + (i * space), x0, y, mHorizoncalPaint);
            canvas.drawLine(x0, y, x1, y, mHorizoncalPaint); //横向的虚线
            Point p = mAdjustPoints.get(i);
            canvas.drawLine(p.x, mRect.bottom, p.x, p.y, mVirtacalPaint);//竖向的线
            canvas.drawCircle(p.x, p.y, 6, mVirtacalPaint); //坐标处的小圆圈
        }

        //draw pao pao icon
        int ww = mPaoPao.getIntrinsicWidth(), hh = mPaoPao.getIntrinsicHeight();
        int x = mAdjustPoints.get(size - 2).x;
        int y = mAdjustPoints.get(size - 2).y;
        canvas.drawBitmap(((BitmapDrawable) mPaoPao).getBitmap(), x - ww / 2, y - hh, mVirtacalPaint);

        Paint.FontMetricsInt fm = mTextPaint.getFontMetricsInt();
        int base = y - hh + (hh - fm.bottom + fm.top) / 2 - fm.top;
        canvas.drawText(mPoints.get(size - 2).y + "M", x, base, mTextPaint);
    }

    private void drawCurve(Canvas canvas) {
        buildLinePath(mCurvePath);
        mCurvePath.lineTo(mRect.right, mRect.bottom);
        mCurvePath.lineTo(mRect.left, mRect.bottom);
        mCurvePath.lineTo(mRect.left, mAdjustPoints.get(0).y);
        mCurvePath.close();
        canvas.drawPath(mCurvePath, mCurvePaint);
    }

    private void buildLinePath(Path path) {
        path.reset();
        path.moveTo(mRect.left, mRect.bottom);
        for (Point p : mAdjustPoints) {
            path.lineTo(p.x, p.y);
        }
    }

    private void buildBezierPath(Path path) {
        path.reset(); //
        path.moveTo(mAdjustPoints.get(0).x, mAdjustPoints.get(0).y);

        int size = mAdjustPoints.size();
        for (int i = 0; i < size - 1; ++i) {
            float x1 = (mAdjustPoints.get(i).x + mAdjustPoints.get(i + 1).x) / 2;
            float y1 = (mAdjustPoints.get(i).y + mAdjustPoints.get(i + 1).y) / 2;
            float x0 = mAdjustPoints.get(i).x, y0 = mAdjustPoints.get(i).y;
            path.quadTo(x0, y0, x1, y1);
        }
        final int lastX = mAdjustPoints.get(size - 1).x, lastY = mAdjustPoints.get(size - 1).y;
        path.quadTo(lastX, lastY, lastX, lastY);
    }
}
