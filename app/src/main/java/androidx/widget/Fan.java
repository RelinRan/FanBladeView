package androidx.widget;

import android.graphics.Color;

/**
 * 扇叶
 */
public class Fan {

    /**
     * 名称
     */
    private String name;
    /**
     * 数据值
     */
    private double value;
    /**
     * 颜色
     */
    private int color;
    /**
     * 文字颜色
     */
    private int textColor = Color.WHITE;

    public Fan(String name, double value, int color) {
        this.name = name;
        this.value = value;
        this.color = color;
    }

    public Fan(String name, double value, int color, int textColor) {
        this.name = name;
        this.value = value;
        this.color = color;
        this.textColor = textColor;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }
}
