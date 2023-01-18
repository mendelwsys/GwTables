package com.mycompany.common.tables;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 18.07.14
 * Time: 20:27
 *
 */
public class GridOptionsSender implements Serializable
{
    private ColumnHeadBean[] chs;

    public ColumnHeadBean[] getChs() {
        return chs;
    }

    public void setChs(ColumnHeadBean[] chs) {
        this.chs = chs;
    }

    public int getHeaderHeight() {
        return headerHeight;
    }

    public void setHeaderHeight(int headerHeight) {
        this.headerHeight = headerHeight;
    }

    public int getCellHeight() {
        return cellHeight;
    }

    public void setCellHeight(int cellHeight) {
        this.cellHeight = cellHeight;
    }

    private int headerHeight;
    private int cellHeight;

    public boolean getFixedRecordHeights() {
        return fixedRecordHeights;
    }

    public boolean isFixedRecordHeights() {
        return fixedRecordHeights;
    }

    public void setFixedRecordHeights(boolean fixedRecordHeights) {
        this.fixedRecordHeights = fixedRecordHeights;
    }

    public String getTableType() {
        return tableType;
    }

    public void setTableType(String tableType) {
        this.tableType = tableType;
    }

    public String tableType;


    private boolean fixedRecordHeights;


}
