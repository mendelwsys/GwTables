package com.mycompany.client.test.staf;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.visualizations.corechart.AxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.ColumnChart;
import com.google.gwt.visualization.client.visualizations.corechart.CoreChart;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.mycompany.client.test.demoHandlers.OnMouseOutDemo;
import com.mycompany.client.test.demoHandlers.OnMouseOverDemo;
import com.mycompany.client.test.demoHandlers.ReadyDemo;
import com.mycompany.client.test.demoHandlers.SelectionDemo;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 29.05.14
 * Time: 13:13
 * To change this template use File | Settings | File Templates.
 */
public class GetWidget
{
    static DataTable getCompanyPerformance() {
      DataTable data = getCompanyPerformanceWithNulls();
      data.setValue(2, 1, 660);
      data.setValue(2, 2, 1120);
      return data;
    }

    static DataTable getCompanyPerformanceWithNulls() {

      DataTable data = DataTable.create();

      data.addColumn(AbstractDataTable.ColumnType.STRING, "Year");
      data.addColumn(AbstractDataTable.ColumnType.NUMBER, "Sales");
      data.addColumn(AbstractDataTable.ColumnType.NUMBER, "Expenses");

      data.addRows(4);
      data.setValue(0, 0, "2004");
      data.setValue(0, 1, 1000);
      data.setValue(0, 2, 400);

      data.setValue(1, 0, "2005");
      data.setValue(1, 1, 1170);
      data.setValue(1, 2, 460);

      data.setValue(2, 0, "2006");
      data.setValue(2, 1,22);
      data.setValueNull(2, 2);

      data.setValue(3, 0, "2007");
      data.setValue(3, 1, 1030);
      data.setValue(3, 2, 540);

      return data;
    }


    public static Widget getWidget(int width,int height)
    {
      final VerticalPanel result = new VerticalPanel();
      final Options options = CoreChart.createOptions();
      options.setHeight(height);
      options.setTitle("Company Performance");
      options.setWidth(width);

      AxisOptions vAxisOptions = AxisOptions.create();
      vAxisOptions.setMinValue(0);
      vAxisOptions.setMaxValue(2000);
      options.setVAxisOptions(vAxisOptions);

      final DataTable data = getCompanyPerformance();
      final ColumnChart viz = new ColumnChart(data, options);




      Label status = new Label();
      Label onMouseOverAndOutStatus = new Label();
      viz.addSelectHandler(new SelectionDemo(viz, status));
      viz.addReadyHandler(new ReadyDemo(status));
      viz.addOnMouseOverHandler(new OnMouseOverDemo(onMouseOverAndOutStatus));
      viz.addOnMouseOutHandler(new OnMouseOutDemo(onMouseOverAndOutStatus));

      result.add(status);
      result.add(viz);
      result.add(onMouseOverAndOutStatus);


        Timer t=new Timer()
        {

            boolean[] trig=new boolean[4];
            int [] dx=new int[]{50,30,70,22};
            @Override
            public void run()
            {

                for (int rowIndex = 0;rowIndex<4;rowIndex++)
                {
                    int val=data.getValueInt(rowIndex,1);
                    int dx = this.dx[rowIndex];
                    if (trig[rowIndex])
                    {
                        if (val<=2000-dx)
                            val+= dx;
                        else
                            trig[rowIndex]=!trig[rowIndex];
                    }
                    else
                    {
                        if (val> dx)
                            val-= dx;
                        else
                            trig[rowIndex]=!trig[rowIndex];
                    }
                    data.setValue(rowIndex, 1, val);
                }

                viz.draw(data,options);
            }
        };
        t.scheduleRepeating(1000);



      return result;
    }

}
