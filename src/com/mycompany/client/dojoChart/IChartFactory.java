package com.mycompany.client.dojoChart;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 09.02.15
 * Time: 20:31
 * Фабрика графиков
 */
public interface IChartFactory
{
    BaseDOJOChart createChart(ChartType type, int width, int height);
}
