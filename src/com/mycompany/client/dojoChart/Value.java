package com.mycompany.client.dojoChart;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 03.02.15
 * Time: 12:06
 * Консолидированное значение вместе с метаинформцией колонки по которой это значение получено
 */
public class Value
{
    public Value(Object mean,IFormatValue formatValue) {
        this.mean = mean;
        this.formatValue = formatValue;
    }

    public Value(Object mean,IFormatValue formatValue, String color)
    {
        this(mean, formatValue);
        this.color = color;
    }

    public Object getMean() {
        return mean;
    }

//    public void setMean(Double mean) {
//        this.mean = mean;
//    }

    public IFormatValue getFormatValue() {
        return formatValue;
    }
    IFormatValue formatValue;

    private Object mean;
    private String color;


    public String getColor()
    {
        return color;
    }

    public void setColor(String color)
    {
        this.color=color;
    }
}
