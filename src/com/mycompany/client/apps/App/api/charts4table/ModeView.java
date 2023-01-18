package com.mycompany.client.apps.App.api.charts4table;

import com.smartgwt.client.types.ValueEnum;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 01.04.15
 * Time: 19:25
 * Режимы таблицы
 */
public enum ModeView implements ValueEnum
{


    Grid("Grid"),
    Chart("Chart");

    private String value;
    ModeView(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }


    public static ModeView[] getAllTypes()
    {
        return new ModeView[]{Grid,Chart};
    }

}
