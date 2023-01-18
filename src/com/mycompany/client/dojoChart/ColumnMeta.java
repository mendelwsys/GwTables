package com.mycompany.client.dojoChart;

import com.google.gwt.i18n.client.NumberFormat;
import com.mycompany.common.analit2.ColDef;
import com.smartgwt.client.types.ListGridFieldType;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 06.02.15
 * Time: 17:55
 * Описатель колонки по которой строится иерархия
 */
public class ColumnMeta extends ColDef
    implements IFormatValue
{
    public String getColId() {
        return colId;
    }

    private String colId;

    public String getTableType()
    {
        return tableType;
    }

    private String tableType;

    public ColumnMeta(String tableType,String colId,ColDef colDef)
    {
        super(colDef);
        this.colId=colId;
        this.tableType=tableType;
    }


    @Override
    public boolean isPresentableValue(Value value)
    {
       final Double mean = (Double) value.getMean();
       return mean!=null && mean>0;
    }

    @Override
    public Object presentationValue(Value value)
    {
        final Double mean = (Double) value.getMean();
        if (ListGridFieldType.INTEGER.getValue().equals(getFtype()))
            return (int) Math.round(mean);
        else
            return mean;
    }

    @Override
    public String formatValue(Value value)
    {

        String format=getFormat();
        Double mean = (Double) value.getMean();
        if (mean==null)
            mean=0d;

        if (format!=null )
        {
            try
            {
                return NumberFormat.getFormat(format).format(mean);
            } catch (IllegalArgumentException e) {
                //
            }
        }
        if (ListGridFieldType.INTEGER.getValue().equals(getFtype()))
            return String.valueOf((int) Math.round(mean));

        return String.valueOf(value);
    }
}
