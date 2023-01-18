package com.mycompany.common.tables;

import java.io.Serializable;

/**
 * Created by Anton.Pozdnev on 20.08.2015.
 */
public class HeaderSpanMimic implements Serializable {
    HeaderSpanMimic[] subs;
    String[] fieldNames;

    public HeaderSpanMimic(String name) {
        this.name = name;
    }

    public HeaderSpanMimic() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String name;

    public String[] getFieldNames() {
        return fieldNames;
    }

    public void setFieldNames(String[] fieldNames) {
        this.fieldNames = fieldNames;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public HeaderSpanMimic[] getSubs() {
        return subs;
    }

    public void setSubs(HeaderSpanMimic[] subs) {
        this.subs = subs;
    }

    int height;

    public int getRowspan() {
        return rowspan;
    }

    public void setRowspan(int rowspan) {
        this.rowspan = rowspan;
    }

    int rowspan;

    public int getColspan() {
        return colspan;
    }

    public void setColspan(int colspan) {
        this.colspan = colspan;
    }

    int colspan;


}
