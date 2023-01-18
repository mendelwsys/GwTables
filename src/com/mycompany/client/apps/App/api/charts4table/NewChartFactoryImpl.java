package com.mycompany.client.apps.App.api.charts4table;

import com.google.gwt.event.shared.HandlerRegistration;
import com.mycompany.client.apps.App.NodesHolder;
import com.mycompany.client.dojoChart.*;
import com.smartgwt.client.widgets.HeaderControl;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.DrawEvent;
import com.smartgwt.client.widgets.events.DrawHandler;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 01.04.15
 * Time: 17:23
 * Фабрика графиков для отображения в окнах (Введение связано с тем что удобно массив контролов создавать в одном месте, а заполнять в другом когда почти все объекты инициализированы)
 */
abstract public class NewChartFactoryImpl extends IChartFactoryImpl
{
    private final HeaderControl[] headerControls;
    private final Window chartWidgetWrapper;

    public NewChartFactoryImpl(HeaderControl[] headerControls, Window chartWidgetWrapper) {
        this.headerControls = headerControls;
        this.chartWidgetWrapper = chartWidgetWrapper;
    }

    public BaseDOJOChart createChart(ChartType type, int width, int height)
    {
        BaseDOJOChart dojoChartPane=super.createChart(type,width,height);
        initHandlers(dojoChartPane);
        return dojoChartPane;
    }

    private HeaderControl[] initHandlers(final BaseDOJOChart dojoChartPane)
    {
        fillHeaderControls(headerControls, dojoChartPane);

        if (chartWidgetWrapper.isCreated() && chartWidgetWrapper.isDrawn())
            NodesHolder.addHeaderCtrl(chartWidgetWrapper, headerControls);
         else
        {
            final HandlerRegistration[] rs = new HandlerRegistration[1];
            rs[0] = chartWidgetWrapper.addDrawHandler(new DrawHandler() {
                @Override
                public void onDraw(DrawEvent event) {
                    NodesHolder.addHeaderCtrl(chartWidgetWrapper, headerControls);
                    rs[0].removeHandler();
                }
            });
        }


        return headerControls;
    }

    /**
     * Заполнить массив контролами для управления отображения
     * @param headerControls - массив контролов
     * @param dojoChartPane - панель графика
     */
    abstract protected void fillHeaderControls(HeaderControl[] headerControls, BaseDOJOChart dojoChartPane);

}
