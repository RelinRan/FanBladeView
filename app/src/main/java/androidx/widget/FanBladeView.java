package androidx.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 扇叶统计
 */
public class FanBladeView extends View {

    //控件宽高
    private int width, height;
    //坐标中心
    private float centerX, centerY;
    //最大半径
    private float radius;
    //数据
    private List<Fan> data;
    //文字水平偏移量
    private float fanTextHorizontalOffset = dip(35);
    //文字大小
    private float fanTextSize = dip(12);
    //半径偏移量
    private float radiusOffset = dip(20);
    //半径幂指数
    private int radiusExponent = 10;

    public FanBladeView(Context context) {
        super(context);
        initAttributeSet(context, null);
    }

    public FanBladeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttributeSet(context, attrs);
    }

    public FanBladeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributeSet(context, attrs);
    }

    private void initAttributeSet(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FanBladeView);
            fanTextHorizontalOffset = array.getDimension(R.styleable.FanBladeView_fanTextHorizontalOffset, fanTextHorizontalOffset);
            fanTextSize = array.getDimension(R.styleable.FanBladeView_fanTextSize, fanTextSize);
            radiusOffset = array.getDimension(R.styleable.FanBladeView_radiusOffset, radiusOffset);
            radiusExponent = array.getInt(R.styleable.FanBladeView_radiusExponent, radiusExponent);
            array.recycle();
        }
    }

    /**
     * @param value px值
     * @return dip值
     */
    protected float dip(int value) {
        return Resources.getSystem().getDisplayMetrics().density * value;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //单元测试
        data = new ArrayList<>();
        data.add(new Fan("其他", 120, Color.parseColor("#3ACACC")));
        data.add(new Fan("钢筋工", 110, Color.parseColor("#3ABDCC")));
        data.add(new Fan("架子工", 226, Color.parseColor("#37A5C0")));
        data.add(new Fan("木工", 168, Color.parseColor("#3267B6")));
        data.add(new Fan("砖工", 362, Color.parseColor("#327AB6")));
        data.add(new Fan("水泥工", 148, Color.parseColor("#3791C0")));
        data.add(new Fan("木工", 268, Color.parseColor("#3267B6")));
        data.add(new Fan("砖工", 102, Color.parseColor("#327AB6")));
        data.add(new Fan("水泥工", 200, Color.parseColor("#3791C0")));
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        int diameter = width <= height ? width : height;
        int requiredWidth = diameter;
        int requiredHeight = diameter;
        int measureSpecWidth = MeasureSpec.getSize(widthMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int measureSpecHeight = MeasureSpec.getSize(heightMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int measureWidth = measureSpecWidth;
        int measureHeight = measureSpecHeight;
        if ((widthSpecMode == MeasureSpec.AT_MOST || widthSpecMode == MeasureSpec.UNSPECIFIED) && heightSpecMode == MeasureSpec.AT_MOST || heightSpecMode == MeasureSpec.UNSPECIFIED) {
            measureWidth = requiredWidth;
            measureHeight = requiredWidth;
        } else if (widthSpecMode == MeasureSpec.AT_MOST || widthSpecMode == MeasureSpec.UNSPECIFIED) {
            measureWidth = requiredWidth;
            measureHeight = measureSpecHeight;
        } else if (heightSpecMode == MeasureSpec.AT_MOST || heightSpecMode == MeasureSpec.UNSPECIFIED) {
            measureWidth = measureSpecWidth;
            measureHeight = requiredHeight;
        }
        setMeasuredDimension(measureWidth, measureHeight);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        diameter = width <= height ? width : height;
        if (width <= height) {
            diameter -= getPaddingLeft() + getPaddingRight();
        } else {
            diameter -= getPaddingTop() + getPaddingBottom();
        }
        centerX = width / 2F;
        centerY = height / 2F;
        radius = diameter / 2F;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawArc(canvas);
    }

    /**
     * 设置数据源
     *
     * @param data
     */
    public void setDatasource(List<Fan> data) {
        this.data = data;
        invalidate();
    }

    /**
     * @return 数据源
     */
    public List<Fan> getDatasource() {
        return data;
    }

    /**
     * 获取数据个数
     *
     * @return
     */
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    /**
     * @param position 位置
     * @return item数据
     */
    public Fan getItem(int position) {
        return data.get(position);
    }

    /**
     * @return 最大值
     */
    public double getFanTotal() {
        double total = 0;
        for (int i = 0; i < getCount(); i++) {
            total += getItem(i).getValue();
        }
        return total;
    }

    /**
     * @return 最大值
     */
    public double getFanMax() {
        List<Double> list = new ArrayList<>();
        for (int i = 0; i < getCount(); i++) {
            list.add(getItem(i).getValue());
        }
        return Collections.max(list);
    }

    /**
     * 绘制扇形数据图
     *
     * @param canvas
     */
    private void drawArc(Canvas canvas) {
        int size = getCount();
        double total = getFanTotal();
        float startAngle = -90F, swipeAngle;
        for (int i = 0; i < size; i++) {
            Fan item = data.get(i);
            float scale = (float) (item.getValue() * 1.0F / total * 1.0f);
            swipeAngle = 360.0F * scale;
            float acRds = radiusOffset == 0 ? radius : (float) (radius - radiusOffset * Math.pow(1 - scale, radiusExponent));
            drawArc(canvas, item.getColor(), acRds, startAngle, swipeAngle);
            float angle = startAngle + swipeAngle / 1.7F;
            float[] coordinates = calculateSwipeCoordinates(angle, radius);
            drawText(canvas, item.getName(), item.getTextColor(), coordinates[0], coordinates[1]);
            startAngle += swipeAngle;
        }
    }

    /**
     * 计算需要扫过的坐标
     *
     * @param angle  角度
     * @param radius 半径
     * @return
     */
    private float[] calculateSwipeCoordinates(float angle, float radius) {
        float[] coordinates = new float[2];
        double radians = Math.toRadians(angle - 2 * Math.PI + 90);
        coordinates[0] = (float) (Math.sin(radians) * radius);
        coordinates[1] = -(float) (Math.cos(radians) * radius);
        return coordinates;
    }

    /**
     * 绘制文本
     *
     * @param canvas    画布
     * @param text      文字
     * @param textColor 文字颜色
     * @param moveX     移动坐标x
     * @param moveY     移动坐标y
     */
    private void drawText(Canvas canvas, String text, int textColor, float moveX, float moveY) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(fanTextSize);
        paint.setColor(textColor);
        paint.setStyle(Paint.Style.FILL);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        Path path = new Path();
        path.moveTo(centerX, centerY);
        path.lineTo(centerX + moveX, centerY + moveY);
        canvas.drawTextOnPath(text, path, fanTextHorizontalOffset, bounds.height() / 2F, paint);
    }

    /**
     * 绘制扇形
     *
     * @param canvas     画布
     * @param color      颜色
     * @param radius     半径
     * @param startAngle 开始角度
     * @param swipeAngle 角度
     */
    private void drawArc(Canvas canvas, int color, float radius, float startAngle, float swipeAngle) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        RectF oval = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        canvas.drawArc(oval, startAngle, swipeAngle, true, paint);
    }

    /**
     * @return 扇叶文字水平偏移量
     */
    public float getFanTextHorizontalOffset() {
        return fanTextHorizontalOffset;
    }

    /**
     * 设置扇叶文字水平偏移量
     *
     * @param fanTextHorizontalOffset
     */
    public void setFanTextHorizontalOffset(float fanTextHorizontalOffset) {
        this.fanTextHorizontalOffset = fanTextHorizontalOffset;
        invalidate();
    }

    /**
     * @return 扇叶文字大小
     */
    public float getFanTextSize() {
        return fanTextSize;
    }

    /**
     * 设置扇叶文字大小
     *
     * @param fanTextSize
     */
    public void setFanTextSize(float fanTextSize) {
        this.fanTextSize = fanTextSize;
        invalidate();
    }

    /**
     * @return 半径偏移量
     */
    public float getRadiusOffset() {
        return radiusOffset;
    }

    /**
     * 设置半径偏移量
     *
     * @param radiusOffset
     */
    public void setRadiusOffset(float radiusOffset) {
        this.radiusOffset = radiusOffset;
        invalidate();
    }

    /**
     * @return 半径幂指数
     */
    public int getRadiusExponent() {
        return radiusExponent;
    }

    /**
     * 设置半径幂指数
     *
     * @param radiusExponent
     */
    public void setRadiusExponent(int radiusExponent) {
        this.radiusExponent = radiusExponent;
        invalidate();
    }

}


