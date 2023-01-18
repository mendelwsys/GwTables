package com.mycompany.client.dojoChart;

import com.smartgwt.client.types.ValueEnum;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 29.01.15
 * Time: 14:39
 * Список возможных типов графиков
 */
public enum ChartType implements ValueEnum
{

    Pie("Pie"),
    Donut("Donut"),
    Bar("Bar"),
    Columns("Columns"),
    ClusteredColumns("ClusteredColumns");

    private String value;
    ChartType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static String getRusVal(ChartType chartType)
    {
        switch (chartType)
        {
            case Pie:
                return "Круговой";
            case Donut:
                return "Кольцевой";
//            case Bar:
//                return "Линейный";
//            case Columns:
//                return "Столбиковый";
//            case ClusteredColumns:
//                return "Столбиковый( с группировкой)";
            default:
                return "Столбиковый";
        }
    }

    public static ChartType[] getAllTypes()
    {
        return new ChartType[]{Pie,Donut};//,ClusteredColumns};
    }

}
