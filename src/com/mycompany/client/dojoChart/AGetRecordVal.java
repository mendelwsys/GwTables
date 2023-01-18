package com.mycompany.client.dojoChart;

import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.util.JSOHelper;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 06.02.15
 * Time: 19:04
 * To change this template use File | Settings | File Templates.
 */
abstract public class AGetRecordVal implements IGetRecordVal
{
    protected Object idVal;
    protected  IFormatValue formatValue;
    protected String chartTitle;
    protected  String color;
    protected  String baseTitle;

    @Override
    public Object getIdVal() {
        return idVal;
    }


    protected JavaScriptObject chartTitleObject;
    private ValDef _def;

    private Object oldVal;

    @Override
    public JavaScriptObject getCharTitle(ValDef def)
    {

        String _chartTitle=null;
        if (baseTitle!=null && chartTitle!=null)
            _chartTitle=this.baseTitle+this.chartTitle;

        Object db=def.getMeanValue(getIdVal());

        if (this._def!=def || (oldVal!=db))
        {
            this._def=def;
            oldVal=db;

            String resTitle=_chartTitle;
            if (_chartTitle!=null)
            {
                String viewText=def.getViewText();
                if (viewText==null)
                    viewText="";

                int indexP=0;
                indexP=remove$$(indexP,_chartTitle);

                int ix$val=_chartTitle.indexOf("$val",indexP);
                if (ix$val>=0)
                {
                    String replacement = getViewVal(def);
                    resTitle=_chartTitle.replace("$val", replacement);
                }

                int ix$=resTitle.indexOf("$",indexP);
                if (ix$>=0)
                    resTitle=resTitle.replace("$",viewText);
            }

            chartTitleObject=JavaScriptObject.createObject().cast();
            JSOHelper.setAttribute(chartTitleObject, "title", resTitle);
            JSOHelper.setAttribute(chartTitleObject,"titleFontColor",color);
        }
        return chartTitleObject;
    }

    @Override
    public  String getViewVal(ValDef def)
    {

        return def.getViewVal(getIdVal());

//        Double value = def.getValue(getIdVal());
//        if (value==null)
//            value=0d;
//        Number resNumber;
//        final ColumnMeta formatValue = def.getTypeDef(getIdVal());
//        if (ListGridFieldType.INTEGER.getValue().equals(formatValue.getFtype()))
//            resNumber= (int) Math.round(value);
//        else
//            resNumber= Math.round(value);
//
//        String format=formatValue.getFormat();
//
//        if (format!=null )
//        {
//            try
//            {
//                return NumberFormat.getFormat(format).format(resNumber);
//            } catch (IllegalArgumentException e) {
//                //
//            }
//        }
//        return String.valueOf(resNumber);
    }

    @Override
    public IFormatValue getFormatValue() {
        return formatValue;
    }

    @Override
    public void setBaseTitle(String baseTitle) {
        this.baseTitle=baseTitle;
        this._def=null;
    }

    private int remove$$(int indexP,String chartTitle) {
        final String prm = "$$";
        int preIndex=0;
        while (indexP<chartTitle.length() && (indexP=chartTitle.indexOf(prm,indexP))>=0)
            {
                indexP+=2;
                preIndex=indexP;
            }
        return preIndex;
    }

    public AGetRecordVal(Object idVal,IFormatValue formatValue,String baseTitle,String chartTitle,String color)
    {
        this.idVal = idVal;
        this.formatValue = formatValue;
        this.chartTitle=chartTitle;
        this.baseTitle=baseTitle;
        this.color = color;
    }


}
