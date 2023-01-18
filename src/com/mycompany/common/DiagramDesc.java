package com.mycompany.common;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 03.06.14
 * Time: 13:29
 * Описатель диаграмыы
 */
public class DiagramDesc
{
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map[] getTuples() {
        return tuples;
    }

    public void setTuples(Map[] tuples) {
        this.tuples = tuples;
    }


    public String[][] getColumnDesc() {
        return columnDesc;
    }

    public void setColumnDesc(String[][] titles) {
        this.columnDesc = titles;
    }

    private String[][] columnDesc;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;
    private String type;//Тип диаграммы (Столбцы, еще что-то)
    private Map[] tuples;

    public String getwType() {
        return wType;
    }

    public void setwType(String wType) {
        this.wType = wType;
    }

    private String wType;//Тип окна, скорее всего понадобиться поскольку разная возможно обработка для разных окон

}
