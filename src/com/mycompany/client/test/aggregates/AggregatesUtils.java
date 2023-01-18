package com.mycompany.client.test.aggregates;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.i18n.client.NumberFormat;
import com.mycompany.client.test.fbuilder.Aggregates;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SummaryFunctionType;
import com.smartgwt.client.util.DateUtil;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.widgets.grid.*;

import java.util.Date;
import java.util.Map;

/**
 * Created by Anton.Pozdnev on 07.05.2015.
 */
public class AggregatesUtils {

    public static final String FORMAT_CELL_VALUE_ATTR = "formatCellValue";
    public static final String OLD_FORMAT_CELL_VALUE_ATTR = "oldFormatCellValue";

    public static void setAggregates(ListGrid lg, Map<String, String> m) {
        if (m == null) return;
        boolean oneFunctionSet = false;
        ListGridField[] ldf = lg.getAllFields();
        for (int i = 0; i < ldf.length; i++) {
            String function = m.get(ldf[i].getName());
            SummaryFunctionType sft = function == null || function.equalsIgnoreCase("null") ? null : SummaryFunctionType.valueOf(function.toUpperCase());
            SummaryFunction sf = FieldTypeToFunctionMapper.getCustomSummaryFunctionForType(ldf[i].getType(), sft);
            if (sf != null) {
                ldf[i].setAttribute(TablesTypes.AGGREGATE_FUNCTIONS_TYPES_KEY, sft.getValue());
                ldf[i].setSummaryFunction(sf);
            } else {
                ldf[i].setAttribute(TablesTypes.AGGREGATE_FUNCTIONS_TYPES_KEY, (String) null);
                ldf[i].setSummaryFunction(sft);
            }
            ldf[i].setShowGroupSummary(sft != null);
            oneFunctionSet = oneFunctionSet || sft != null;

            applyFormat(lg, ldf[i]);
        }
        if (oneFunctionSet) {
            lg.setShowGroupSummary(true);
            lg.setShowGroupSummaryInHeader(true);

//            if (lg.getGroupByFields() != null && lg.getGroupByFields().length > 0) {
//                lg.groupBy(lg.getGroupByFields());
//
//            } else {
//                Tree t = lg.getGroupTree();
//                TreeNode[] tn = t.getAllNodes();
//
//                Map<String, String> gr = new HashMap<String, String>();
//                for (int i = 0, tnLength = tn.length; i < tnLength; i++) {
//                    if (tn[i].getAttributeAsBoolean("isFolder")) {
//                        String groupName = tn[i].getAttribute("groupName");
//                        gr.put(groupName, groupName);
//                    }
//                }
//                lg.groupBy(gr.keySet().toArray(new String[gr.size()]));
//            }//gr.clear();

            lg.recalculateSummaries();

        } else {
            lg.setShowGroupSummary(false);
            lg.setShowGroupSummaryInHeader(false);
        }
    }

    private static void applyFormat(final ListGrid lg, final ListGridField listGridField) {
        final String format = listGridField.getAttribute(TablesTypes.AGGREGATE_FIELD_FORMAT_KEY);
        JavaScriptObject oldFormater = listGridField.getAttributeAsJavaScriptObject(OLD_FORMAT_CELL_VALUE_ATTR);

        if ((format == null || format.equals("")))
        {
            if (oldFormater!=null)
            {
                if (JSOHelper.isArray(oldFormater))
                    oldFormater=null;
                listGridField.setAttribute(FORMAT_CELL_VALUE_ATTR,oldFormater);
                listGridField.setAttribute(OLD_FORMAT_CELL_VALUE_ATTR,(String)null);
            }
        }
        else
        {

            final JavaScriptObject formater=listGridField.getAttributeAsJavaScriptObject(FORMAT_CELL_VALUE_ATTR);
            if (oldFormater==null)
            {
                if (formater!=null)
                    listGridField.setAttribute(OLD_FORMAT_CELL_VALUE_ATTR,formater);
                else
                    listGridField.setAttribute(OLD_FORMAT_CELL_VALUE_ATTR,JavaScriptObject.createArray());
            }


            listGridField.setCellFormatter(new CellFormatter()
            {

                private native String _format(ListGridField javaScriptObject,Object value, JavaScriptObject record, int rowNum, int colNum)
                /*-{

                    var f = javaScriptObject.@com.smartgwt.client.core.DataClass::getAttributeAsObject(Ljava/lang/String;)(@com.mycompany.client.test.aggregates.AggregatesUtils::OLD_FORMAT_CELL_VALUE_ATTR);
                    return f(value,record,rowNum,colNum);
                }-*/;

                @Override
                public String format(Object value, ListGridRecord record, int rowNum, int colNum)
                {
                    if (record.getIsGroupSummary())
                        return getStringFormatByValue(listGridField, value, format);
                    else
                    {
                        JavaScriptObject defFormatter = listGridField.getAttributeAsJavaScriptObject(OLD_FORMAT_CELL_VALUE_ATTR);
                        if (JSOHelper.isArray(defFormatter))
                            return lg.getDefaultFormattedValue(record,rowNum,colNum);
                        else
                            return _format(listGridField,value,record.getJsObj(),rowNum,colNum);
                    }
                }
            });
        }

    }

    public static String getStringFormatByValue(ListGridField listGridField, Object value, String format) {
        final String retFormatString;

//        if (listGridField.getType().getValue().equals(ListGridFieldType.INTEGER.getValue()) || listGridField.getType().getValue().equals(ListGridFieldType.FLOAT.getValue()))
//        {
//            String decimalSeparator = LocaleInfo.getCurrentLocale().getNumberConstants().decimalSeparator();
//            NumberFormat nf = NumberFormat.getFormat(format);
//            retFormatString = nf.format((Number) value).replace(decimalSeparator,".");
//        }
//        else
//        if (listGridField.getType().getValue().equals(ListGridFieldType.DATETIME.getValue()) || listGridField.getType().getValue().equals(ListGridFieldType.DATE.getValue()) || listGridField.getType().getValue().equals(ListGridFieldType.TIME.getValue())) {
//            retFormatString = DateUtil.format((Date) value, format);
//        }
//        else
//            retFormatString=null;


        if (value instanceof Number)
        {
            String decimalSeparator = LocaleInfo.getCurrentLocale().getNumberConstants().decimalSeparator();
            NumberFormat nf = NumberFormat.getFormat(format);
            retFormatString = nf.format((Number) value).replace(decimalSeparator,".");
        }
        else if ( value instanceof Date) {
            retFormatString = DateUtil.format((Date) value, format);
        }
        else if ( value instanceof String )
            retFormatString=String.valueOf(value);
        else
            retFormatString=null;

        return retFormatString;
    }


    public static String getSummaryFunction(String summaryFunction) {
        SummaryFunctionType sft = SummaryFunctionType.valueOf(summaryFunction.toUpperCase());
        switch (sft) {
            case COUNT:
                return Aggregates.COUNT.getValue();
            case AVG:
                return Aggregates.ARG.getValue();
            case MIN:
                return Aggregates.MIN.getValue();
            case MAX:
                return Aggregates.MAX.getValue();
            case SUM:
                return Aggregates.SUM.getValue();
            case MULTIPLIER:
                return Aggregates.MULTIPLIER.getValue();

        }
        return "";
    }

    public static SummaryFunctionType getSummaryFunctionType(String function) {
        if (function == null || function.trim().equals("")) return null;
        Aggregates a = Aggregates.getAggregate(function);
        switch (a) {
            case COUNT:
                return SummaryFunctionType.COUNT;
            case ARG:
                return SummaryFunctionType.AVG;
            case MIN:
                return SummaryFunctionType.MIN;
            case MAX:
                return SummaryFunctionType.MAX;
            case SUM:
                return SummaryFunctionType.SUM;
            case MULTIPLIER:
                return SummaryFunctionType.MULTIPLIER;

        }
        return null;
    }
}
