package com.mycompany.client;

import com.google.gwt.user.client.ui.Widget;
//import com.google.gwt.visualization.client.AbstractDataTable;
//import com.google.gwt.visualization.client.DataTable;
//import com.google.gwt.visualization.client.visualizations.corechart.AxisOptions;
//import com.google.gwt.visualization.client.visualizations.corechart.ColumnChart;
//import com.google.gwt.visualization.client.visualizations.corechart.CoreChart;
//import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.mycompany.common.DiagramDesc;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ResizedEvent;
import com.smartgwt.client.widgets.events.ResizedHandler;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 29.05.14
 * Time: 13:13
 *
 */
public class GetWidget
{
////TODO При обновлении надо учитывать, что данные могут быть отрицательными
//    static DataTable getDataByDescriptor(DiagramDesc desc, double[] agrig)
//    {
//        DataTable data = DataTable.create();
//
//        String[][] columnDescs=desc.getColumnDesc();
//
//        for (String[] atrr2title : columnDescs)
//            data.addColumn(AbstractDataTable.ColumnType.valueOf(atrr2title[3]), atrr2title[1]);
//
//        Map[] tuples=desc.getTuples();
//        data.addRows(tuples.length);
//
//        agrig[0]=0;
//        for (int i = 0, titleLength = columnDescs.length; i < titleLength; i++)
//        {
//            String[] columnDesc = columnDescs[i];
//            AbstractDataTable.ColumnType columnType = AbstractDataTable.ColumnType.valueOf(columnDesc[3]);
//            Object val = (tuples[0].get(columnDesc[0]));
//            switch (columnType)
//            {
//                case BOOLEAN:
//                    data.setValue(0, i, (Boolean)val);
//                break;
//                case NUMBER:
//                    if (val instanceof Integer)
//                    {
//                        data.setValue(0, i, (Integer)val);
//                        if ((Integer)val > agrig[0])
//                            agrig[0] = (Integer)val;
//                    }
//                    else if (val instanceof Double)
//                    {
//                        data.setValue(0, i, (Double)val);
//                        if ((Double)val > agrig[0])
//                            agrig[0] = ((Double)val);
//                    }
//                break;
//                case STRING:
//                    data.setValue(0, i, (String)val);
//                break;
////                case DATE:
////                    data.setValue(0, i,(Date)val);//TODO Доработать - переводить из long????
////                break;
////                case DATETIME:
////                    data.setValue(0, i,(Date)val);//TODO Доработать - переводить из long????
////                break;
//
//            }
//        }
//        return data;
//    }


    public static Widget getChartWidget(final Window window, int width, int height, DiagramDesc desc)
    {

//        if (desc==null)
            return null;

//        final Options options = CoreChart.createOptions();
//        options.setWidth(width);
//        options.setHeight(height);
//
//
//
//        options.setTitle(desc.getTitle());
//        double [] agrig = new double[1];
//        final DataTable data = getDataByDescriptor(desc, agrig);
//
//        String[][] titles = desc.getColumnDesc();
//        String[] colors=new String[titles.length-1];
//        for (int i = 1, titlesLength = titles.length; i < titlesLength; i++)
//            colors[i-1] = titles[i][2];
//        options.setColors(colors);
//
//
//        AxisOptions vAxisOptions = AxisOptions.create();
//        vAxisOptions.setMinValue(0); //TODO Может здесь передавать в дескрипторе
//        vAxisOptions.setMaxValue(1.1*Math.ceil(agrig[0]));//TODO Может здесь передавать в дескрипторе
//        options.setVAxisOptions(vAxisOptions);
//
//
//        final ColumnChart columnChart = new ColumnChart(data, options);
//
//        window.addResizedHandler(new ResizedHandler()
//        {
//            public void onResized(ResizedEvent event)
//            {
//                if (columnChart.isVisible())
//                {
//                    int h=window.getInnerContentHeight()-30;
//                    int w=window.getInnerContentWidth()-20;
//
//                    Canvas[] its=window.getItems();
//                    for (Canvas it : its)
//                        it.resizeTo(w,h);
//
//                    options.setHeight(h);
//                    options.setWidth(w);
//
//                    columnChart.draw(data,options);
//                    window.markForRedraw();
//                }
//            }
//        });
//        return columnChart;

///*
////        Label status = new Label();
////        Label onMouseOverAndOutStatus = new Label();
////
////        viz.addSelectHandler(new SelectionDemo(viz, status));
////        viz.addReadyHandler(new ReadyDemo(status));
////        viz.addOnMouseOverHandler(new OnMouseOverDemo(onMouseOverAndOutStatus));
////        viz.addOnMouseOutHandler(new OnMouseOutDemo(onMouseOverAndOutStatus));
////
////        result.add(status);
////        result.add(viz);
////        result.add(onMouseOverAndOutStatus);
////
////        return result;
////        return viz;
//*/
    }

}
