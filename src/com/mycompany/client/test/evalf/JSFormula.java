package com.mycompany.client.test.evalf;


import com.google.gwt.core.client.JavaScriptObject;
import com.mycompany.client.apps.App.ICopyObject;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.util.JSOHelper;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 16.04.15
 * Time: 13:32
 *
 */
public class JSFormula implements ICopyObject,Serializable

{

    public static final String NEW_CALCULATE_FILED = "z$calFiled_$";

    public DSCallback getUpdater() {
        return updater;
    }

    public void setUpdater(DSCallback updater) {
        this.updater = updater;
    }

    private transient DSCallback updater;

    public ListGridFieldType getFieldType()
    {
        return fieldType;
    }

    public void setFieldType(ListGridFieldType fieldType)
    {
        this.fieldType = fieldType;
    }


    public void setPeriod(int period) {
        this.period = period;
    }


    public boolean isReCalcFormula() {
        return reCalcFormula;
    }

    public void setReCalcFormula(boolean reCalcFormula) {
        this.reCalcFormula = reCalcFormula;
    }



    public String getExpressionValue() {
        return expressionValue;
    }

    public void setExpressionValue(String expressionValue) {
        this.expressionValue = expressionValue;
    }

    public Map<String, String> getVarName2NameInRecord() {
        return varName2NameInRecord;
    }

    public void setVarName2NameInRecord(Map<String, String> varName2NameInRecord) {
        this.varName2NameInRecord = varName2NameInRecord;
    }



    public void setTitle(String title) {
        this.title = title;
    }




    public String getFName() {
        return fName;
    }

    public void setFName(String fName) {
        this.fName = fName;
    }

    private String fName= NEW_CALCULATE_FILED;
    private String title="Новое поле";
    private boolean reCalcFormula =false;
    private String expressionValue="";
    private ListGridFieldType fieldType=ListGridFieldType.TEXT;
    private int period=0;

    private Map<String, String> varName2NameInRecord;

    public JSFormula(JavaScriptObject jsObj)
    {
        this.title = JSOHelper.getAttribute(jsObj, "title");
        this.fName = JSOHelper.getAttribute(jsObj, "fName");
        this.reCalcFormula = JSOHelper.getAttributeAsBoolean(jsObj, "reCalcFormula");

        this.expressionValue = JSOHelper.getAttribute(jsObj, "expressionValue");
        this.fieldType = ListGridFieldType.valueOf(JSOHelper.getAttribute(jsObj, "fieldType"));

        this.period= JSOHelper.getAttributeAsInt(jsObj, "period");
        this.varName2NameInRecord = JSOHelper.getAttributeAsMap(jsObj, "varName2NameInRecord");
    }

    public JavaScriptObject getJsObj()
    {
        JavaScriptObject jsObj=JavaScriptObject.createObject();

        JSOHelper.setAttribute(jsObj, "title",this.title);
        JSOHelper.setAttribute(jsObj, "fName",this.fName);

        JSOHelper.setAttribute(jsObj, "reCalcFormula",this.reCalcFormula);

        JSOHelper.setAttribute(jsObj, "expressionValue",this.expressionValue);
        JSOHelper.setAttribute(jsObj, "fieldType",String.valueOf(this.fieldType));

        JSOHelper.setAttribute(jsObj, "period",this.period);
        JSOHelper.setAttribute(jsObj, "varName2NameInRecord",this.varName2NameInRecord);

        return jsObj;
    }


    public JSFormula(Map<String,String> varName2NameInRecord)
    {
        this.varName2NameInRecord=varName2NameInRecord;
    }

    public JSFormula(String title,String expressionValue,Map<String,String> varName2NameInRecord)
    {
        this(varName2NameInRecord);
        this.expressionValue = expressionValue;
        this.title=title;
    }

    private JSFormula(String title,String expressionValue,Map<String,String> varName2NameInRecord,String fName,ListGridFieldType fieldType,boolean reCalcFormula,int period)
    {
        this(title,expressionValue,varName2NameInRecord);
        this.period=period;
        this.reCalcFormula=reCalcFormula;
        this.fName=fName;
        this.fieldType=fieldType;
    }

    public String getTitle() {
        return title;
    }

    public int getPeriod() {

        return period;
    }

    @Override
    public JSFormula copy()
    {
        return new JSFormula(title, expressionValue, varName2NameInRecord,fName,fieldType, reCalcFormula, period);
    }
}
