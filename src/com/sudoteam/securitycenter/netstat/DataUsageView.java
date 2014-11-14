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
 * Chart view for data usage
 */
public class DataUsageView extends View {
    private static final int BG_COLOR = 0xfffbfbfb;
    private static final int MAIN_LINE_COLOR = 0xffd53844;
    private static final int HORIZONTAL_COLOR = Color.BLACK;
    private static final int VIRTICAL_COLOR = 0xfffca393;
    private static final int TEXT_COLOR = Color.WHITE;

    public DataUsageView(Context context) {
        super(context);
        init(context);
    }

    public DataUsageView(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    Paint mCurvePaint;
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
        for (Point du : list) {
            mPoints.add(new Point(du.x, du.y));
            Util.i("[DataUsageView]bindData() x=" + du.x + ", y=" + du.y);
        }
        requestLayout();
    }

    Rect mRect = new Rect();
    List<Point> mPoints = new ArrayList<Point>();
    List<Point> mAdjustPoints = new ArrayList<Point>();
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
        getDrawingRect(mRect);  //get rect of View
        Util.i("onDraw()  Rect is " + mRect);
        canvas.drawColor(BG_COLOR);
        if (mPoints == null || mPoints.size() == 0)
            return;
        adjustPoint(canvas);
        drawCurve(canvas);
        drawGrid(canvas);//draw grid
    }

    /**
     * calculate real point on android , depend on point in Math.
     */
    private void adjustPoint(Canvas canvas) {
        maxY = -1;
        maxX = mPoints.get(mPoints.size() - 1).x;
        for (Point p : mPoints) {  // find max y
            if (p.y > maxY)
                maxY = p.y;
        }
        maxY = Math.max(10, maxY);
        int iy = (mRect.bottom - mRect.top) / maxY;
        int ix = (mRect.right - mRect.left) / maxX;

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
        int lineSize = 5;// count of horizontal line.
        int space = maxY / lineSize; // space between each horizontal line
        int iiy = (mRect.bottom - mRect.top) / lineSize; //px between each horizontal line
        int x0 = mRect.left, x1 = mRect.right;
        for (int i = 0; i < size; ++i) {
            int y = mRect.bottom - (i * iiy);
            canvas.drawText(" " + (i * space), x0, y, mHorizoncalPaint);
            canvas.drawLine(x0, y, x1, y, mHorizoncalPaint); // draw horizontal line
            Point p = mAdjustPoints.get(i);
            canvas.drawLine(p.x, mRect.bottom, p.x, p.y, mVirtacalPaint);//draw vertical line
            canvas.drawCircle(p.x, p.y, 6, mVirtacalPaint); // draw small circle on every point
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
