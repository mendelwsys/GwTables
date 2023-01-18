package com.mycompany.common;

import com.mycompany.common.tables.ColumnHeadBean;

import java.io.Serializable;


/**
 * Created by Anton.Pozdnev on 30.03.2015.
 */
public class FieldDescriptor extends ColumnHeadBean implements Serializable {


    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    String dateFormat;

    public String getLinkNameField() {
        return linkNameField;
    }

    public void setLinkNameField(String linkNameField) {
        this.linkNameField = linkNameField;
    }

    public String getLinkURLField() {
        return linkURLFiled;
    }

    public void setLinkURLField(String linkURLFiled) {
        this.linkURLFiled = linkURLFiled;
    }

    String linkNameField;
    String linkURLFiled;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    int width = 0;

}
