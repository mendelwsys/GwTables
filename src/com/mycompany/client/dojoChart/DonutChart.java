package com.mycompany.client.dojoChart;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 29.01.15
 * Time: 20:10
 *
 */
public class DonutChart extends PieChart
{

    public DonutChart(int width, int height)
    {
        super(width, height);
        setChartType(ChartType.Donut);
    }


    protected JavaScriptObject  _load(String libName,JavaScriptObject bindObject)
    {
        JavaScriptObject  rv;
        if (libName.endsWith(ChartType.Donut.getValue()))
        {
            rv=super._load("dojox.charting.plot2d." + ChartType.Pie,bindObject);
            rv=__loadDount(ChartType.Pie.getValue(),"dojox.charting.plot2d." + ChartType.Donut,rv,bindObject);
        }
        else
            rv=super._load(libName,bindObject);
        return rv;
    }

    private  native JavaScriptObject __loadDount(String parentName,String libName,JavaScriptObject pie,JavaScriptObject bindObject) /*-{
        bindObject[parentName]=pie;
        return bindObject.declare(libName,pie,
                {
            render: function (dim, offsets) {
                // Call the Pie's render method
                this.inherited(arguments);



                // Draw a white circle in the middle
                var rx = (dim.width - offsets.l - offsets.r) / 2,
                    ry = (dim.height - offsets.t - offsets.b) / 2,
                    r = Math.min(rx, ry) / 2;


                    var radius;
                    if (bindObject && bindObject.chart)
                    {
                        var stack=bindObject.chart.stack;
                        if (stack && stack[0] && stack[0].opt)
                            radius=stack[0].opt.radius;

                        if (!radius)
                            radius=r;
                    }
                    r = Math.min(r, radius*0.7); //TODO Изменить алгоритм когда будет ясно как формируется радиус окружности

                    var circle = {
                        cx: offsets.l + rx,
                        cy: offsets.t + ry,
                        r: r
                    };

                    var s = this.group;

                s.createCircle(circle).setFill("#fff").setStroke("#fff");
            }
        });
    }-*/;

}
