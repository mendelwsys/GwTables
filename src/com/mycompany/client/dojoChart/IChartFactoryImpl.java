package com.mycompany.client.dojoChart;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 09.02.15
 * Time: 20:32
 * Простая фабрика классов
 */
public class IChartFactoryImpl implements IChartFactory {
    @Override
    public BaseDOJOChart createChart(ChartType type, int width, int height)
    {
        switch (type)
        {
            case Pie:
                return new PieChart(width,height);
            case Donut:
                return new DonutChart(width,height);
            default:
                return new ClusteredColumns(width,height);
        }
    }
}
