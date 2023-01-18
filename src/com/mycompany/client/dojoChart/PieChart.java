package com.mycompany.client.dojoChart;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayMixed;
import com.mycompany.client.utils.JScriptUtils;
import com.smartgwt.client.util.JSOHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 29.01.15
 * Time: 20:10
 *
 */
public class PieChart extends BaseDOJOChart
{

    public PieChart(int width, int height)
    {
        super(width, height);
        setChartType(ChartType.Pie);
    }

    public JsArrayMixed prepareData(ValDef levelDef)
    {
        Map<Object,ValDef> levels = levelDef.getChildDef();
        JsArrayMixed numbers= JsArray.createArray().cast();

        int ix=0;
        index2key.clear();
        for (Object levelKey : levels.keySet())
        {
            index2key.put(ix,levelKey);
            JavaScriptObject object=JavaScriptObject.createObject().cast();

            ValDef level=levels.get(levelKey);

            IFormatValue formatValue= level.getFormatValue(getBindName());
            if (formatValue==null)
                continue;
                //throw new RuntimeException("Can't define type of value");

            final Value value = level.getValue(getBindName());
            if (!formatValue.isPresentableValue(value)) continue;
            JSOHelper.setAttribute(object, "y", formatValue.presentationValue(value));

//            final Double value = level.getValue(getBindName());
//            if (value==null || value<=0)
//                continue;
//
//            String tooltipVal="";
//
//            ColumnMeta formatValue = level.getFormatValue(getBindName());
//            if (formatValue==null)
//                throw new RuntimeException("Can't define type of value");
//
//
//            if (ListGridFieldType.INTEGER.getValue().equals(formatValue.getFtype()))
//                JSOHelper.setAttribute(object, "y", (int) Math.round(value));
//            else
//                JSOHelper.setAttribute(object, "y", value);

            String tooltipVal=level.getViewVal(getBindName());
            String viewText=level.getViewText();
            if (viewText!=null)
                JSOHelper.setAttribute(object, "text", viewText+" ["+tooltipVal+"]");

            numbers.push(object);
            ix++;

        }
        return numbers;
    }

    public boolean reSizeChart(int width,int height)
    {
        final double min = Math.min(1.0*width/1.5, height);
        if (min >0)
        {
            _setRadius((int)Math.round(min/3));
            return super.reSizeChart(width,height);
        }
        return true;
    }


    protected native void  _setRadius(int radius)/*-{
        var bindObject=this.@com.mycompany.client.dojoChart.BaseDOJOChart::_getBindObject()();
        if (bindObject && bindObject.chart)
        {
            var chart=bindObject.chart;
            if (chart.stack)
            {
                var stack=chart.stack;
                if (stack[0] && stack[0].opt)
                    stack[0].opt.radius=radius;
            }
        }
    }-*/;


    protected JavaScriptObject getChartPlotKwArs()
    {
        Map<String,Object> param=new HashMap<String,Object>();
        final double min = Math.min(width, height);
        param.put("radius", (int)Math.round(min/3)); //TODO Не понятно как укоротить расстояние от надписей до окружности
        param.put("fontColor","black");
        param.put("labelOffset", 0);
        param.put("labelWiring","cc00");
        param.put("labelStyle","columns");

        return JScriptUtils.m2j(param);
    }

}
