package com.mycompany.client.test.informer.ref;

import com.google.gwt.core.client.JavaScriptException;
import com.mycompany.client.GUIStateDesc;
import com.mycompany.client.GridCtrl;
import com.mycompany.client.apps.App.NSI;
import com.mycompany.client.apps.App.OptionsViewers;
import com.mycompany.client.apps.App.api.CreateInformerOperation;
import com.mycompany.client.apps.App.api.NewViolInformer;
import com.mycompany.client.test.fbuilder.*;
import com.mycompany.client.test.informer.DetailViewerInfo;
import com.mycompany.client.test.informer.Informer;
import com.mycompany.client.test.informer.InformerUpdater;
import com.mycompany.client.test.informer.SetRoadDlg;
import com.mycompany.client.updaters.IMetaTableConstructor;
import com.mycompany.client.updaters.TimeUpdaterCtrl;
import com.mycompany.client.utils.PostponeOperationProvider;
import com.mycompany.common.DescOperation;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.Criterion;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.OperatorId;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 11.03.15
 * Time: 15:51
 *
 */
public class ViolInformer implements Runnable {


    native void setParentWindowTitle(String id, String title) throws JavaScriptException/*-{
        //$wnd.alert($wnd.parent);


        if ($wnd.parentframe) {
            //  $wnd.alert('inside');
            $wnd.parentframe.parent.setWindowTitle(id, title);
            //  $wnd.alert('after');
        }

    }-*/;
    public void initInformer(final Canvas parentCanvas,final DescOperation descInformer)
    {
        if (descInformer.get("MODE") != null && ((String) descInformer.get("MODE")).equalsIgnoreCase(GUIStateDesc.DESKTOP)) {
            setParentWindowTitle("" + ((Long) descInformer.get(CreateInformerOperation.CREATED_INFORMER_ID)).longValue(), "??????????????????: " + getDorNameByCode((Integer) descInformer.get(CreateInformerOperation.DOR_KOD)));

        }
        final Pair<Map, Map<String, Pair<String, ListGridFieldType>>> pr = getNormaValues();
        final DetailViewerInfo viewInfo = new DetailViewerInfo(pr.second, pr.first,descInformer)
        {


            @Override
            public void performAnalysisOnUpdate(Pair<Record, Record> data) {
                Record oldRecord = data.first;
                Record newRecord = data.second;
//SC.say("Old record = "+oldRecord==null?null:oldRecord.toMap()+" New record = "+newRecord.toMap());
                Pair<Map, Map<String, Pair<String, ListGridFieldType>>> hm = getNormaValues();
                Map m = hm.getKey();
                String s[] = null;
                s = (String[]) m.keySet().toArray(new String[m.keySet().size()]);

                String violatedParam = "";

                for (int i = 0; i < s.length; i++) {
                    String key = s[i];
                    Integer oldValue = oldRecord.getAttributeAsInt(key + "_ORIG");
                    Integer newValue = newRecord.getAttributeAsInt(key + "_ORIG");

                    if ((oldValue != null && newValue != null && newValue > oldValue)) {
                        violatedParam = hm.getValue().get(key).getKey();
                        // shouldWarn = true;
                        warnNative("" + ((Long) descInformer.get(CreateInformerOperation.CREATED_INFORMER_ID)).longValue(), "????????????????!!!", "???????????????????? ???????????????????? ???? ?????????? ?????????????????? " + violatedParam + ". ???????????? ????????????????: " + oldValue + ", ?????????? ????????????????: " + newValue, this.isHighLevelWarning());

                        break;

                    }

                }
            }

            native void warnNative(String s, String s1, String s2, boolean highLevelWarning)/*-{

                if ($wnd.parentframe) {
                    //$wnd.alert('inside');
                    $wnd.parentframe.parent.warn(s, s1, s2, highLevelWarning);
                    //  $wnd.alert('after');
                }

            }-*/;

            public Integer getDorKod()
            {
                DescOperation descInformer=detailViewer.getDescInformer();
                if (descInformer!=null)
                {
                    Integer in_dor_kod= (Integer) descInformer.get(NewViolInformer.DOR_KOD);
                    if (in_dor_kod!=null)
                        return in_dor_kod;
                }
                return -1;
            }

            public void setDorKod(Integer dorKod)
            {
                if (getCtrl()!=null)
                {
                    DescOperation descInformer=detailViewer.getDescInformer();
                    if (descInformer!=null)
                        descInformer.put(NewViolInformer.DOR_KOD,dorKod);
                    setCtrlCriteria();
                    this.viewValues(ViolInformer.getNoDataValues().first);
                    ctrl.setFullDataUpdate();
                }
                else
                {
                    if (descInformer!=null)
                        descInformer.put(NewViolInformer.DOR_KOD,dorKod);
                    buildAndStartInformer(parentCanvas, this);
                }
            }
        };

        Integer w= (Integer) descInformer.get(NewViolInformer.CRD_LEFT);
        Integer h= (Integer) descInformer.get(NewViolInformer.CRD_TOP);
        if (w==null)
        {
            w=parentCanvas.getWidth()/2;
            int wch=viewInfo.getInformer().getWidth()/2;
            w=Math.max(w-wch/2,0);
        }
        if (h ==null)
        {
            h=parentCanvas.getHeight()/3;
            int hch=viewInfo.getInformer().getHeight()/2;
            h=Math.max(h-hch/2,0);
        }
        descInformer.put(NewViolInformer.CRD_LEFT,w);
        descInformer.put(NewViolInformer.CRD_TOP, h);

        if (descInformer.get("MODE") == null || descInformer.get("MODE").equals(GUIStateDesc.BROWSER)) {
            viewInfo.getInformer().setLeft(w);
            viewInfo.getInformer().setTop(h);
        }

        viewInfo.getInformer().setWidth(200);
//        viewInfo.getInformer().setHeight(300);


        int dor_kod=viewInfo.getDorKod();
        if (dor_kod>0)
            buildAndStartInformer(parentCanvas, viewInfo);
        else
            SetRoadDlg.createViewOptionsDlg(viewInfo).show();

        if (!(descInformer.get("MODE") == null || descInformer.get("MODE").equals(GUIStateDesc.BROWSER)))
            finishLoading("" + ((Long) descInformer.get(CreateInformerOperation.CREATED_INFORMER_ID)).longValue());
    }

    native void finishLoading(String id)/*-{

        if ($wnd.parentframe) {
            //$wnd.alert('inside');
            $wnd.parentframe.parent.finishLoading(id);
            //  $wnd.alert('after');
        }

    }-*/;

    /*
    ???????????? ?????????????????? 1.

    ?????????????????? ?????????????????????????? ???? ??????????.

    ???????? ???? ??????-???? ?????????? ?????? N - ??????????????????????????, ?????????? ??????????????
    ???????? ???? ??????-???? ?????????? ?????? M - ??????????????????????????, ?????????? ????????????

    ?????????????????? -

        ????????????, ???????????????????? - ??????????????
        ????????????, ???????????????????? - ??????????????


    ???????????? ?????????????????? 2.


       ???????? ???????? ?????????????????????????? ?????????? ???? ?????????? (???????? ????????, ?????????? ???????????????????? ????=)
       ???????? ???? 5-???? - ?????????? ????????????????????????
            ???????? ???? ???????? ?????????? - ?????????? ?????????? ????
    */

    private void buildAndStartInformer(final Canvas parentCanvas, final DetailViewerInfo viewInfo)
    {
        String table= TablesTypes.VIOLATIONS;
        String fName1 ="?????????????????? I ??????.";
        //???????????? ????????????
//        final String filterCriteria1 = "{\n" +
//                "    \"_constructor\":\"AdvancedCriteria\", \n" +
//                "    \"operator\":\"and\", \n" +
//                "    \"criteria\":[\n" +
//                "        {\n" +
//                "            \"fieldName\":\"ID_KIND\", \n" +
//                "            \"operator\":\"equals\", \n" +
//                "            \"value\":1\n" +
//                "        }, \n" +
//                "        {\n" +
//                "            \"operator\":\"isNull\", \n" +
//                "            \"fieldName\":\"KD\"\n" +
//                "        }\n" +
//                "    ]\n" +
//                "}";


        final String filterCriteria1 = "{\n" +
                "    \"_constructor\":\"AdvancedCriteria\", \n" +
                "    \"operator\":\"and\", \n" +
                "    \"criteria\":[\n" +
                "        {\n" +
                "            \"fieldName\":\"ID_KIND\", \n" +
                "            \"operator\":\"equals\", \n" +
                "            \"value\":1\n" +
                "        }, \n" +
                "        {\n" +
                        "            \"_constructor\":\"AdvancedCriteria\", \n" +
                        "            \"operator\":\"or\", \n" +
                        "            \"criteria\":[\n" +
                        "                {\n" +
                        "                    \"operator\":\"isNull\", \n" +
                        "                    \"fieldName\":\"KD\"\n" +
                        "                }, \n" +
                        "                {\n" +
                        "                    \"operator\":\"lessThanField\", \n" +
                        "                    \"fieldName\":\"TIMESTAMP_FIELD\", \n" +
                        "                    \"value\":\"KD\"\n" +
                        "                }\n" +
                        "            ]\n" +
                "        }\n"+
                "    ]\n" +
                "}";


        String fName2 ="?????????????????? II ??????.";
        final String filterCriteria2 = "{\n" +
                "    \"_constructor\":\"AdvancedCriteria\", \n" +
                "    \"operator\":\"and\", \n" +
                "    \"criteria\":[\n" +
                "        {\n" +
                "            \"fieldName\":\"ID_KIND\", \n" +
                "            \"operator\":\"equals\", \n" +
                "            \"value\":2\n" +
                "        }, \n" +
                "        {\n" +
                        "            \"_constructor\":\"AdvancedCriteria\", \n" +
                        "            \"operator\":\"or\", \n" +
                        "            \"criteria\":[\n" +
                        "                {\n" +
                        "                    \"operator\":\"isNull\", \n" +
                        "                    \"fieldName\":\"KD\"\n" +
                        "                }, \n" +
                        "                {\n" +
                        "                    \"operator\":\"lessThanField\", \n" +
                        "                    \"fieldName\":\"TIMESTAMP_FIELD\", \n" +
                        "                    \"value\":\"KD\"\n" +
                        "                }\n" +
                        "            ]\n" +
                "        }\n"+
                "    ]\n" +
                "}";

        final FilterDet filterDet1 = new FilterDet(table, KnownTables.getTableMap().get(table), filterCriteria1, fName1);
        final FilterDet filterDet2 = new FilterDet(table, KnownTables.getTableMap().get(table), filterCriteria2, fName2);

        final TableFunction tblFunction = createTableFunction(filterDet1, filterDet2,viewInfo);
        final Pair<TableFunction, Informer> function2Informer=new Pair<TableFunction, Informer>(tblFunction,viewInfo);

        Pair<GridCtrl, IMetaTableConstructor> ctrl2Meta = InformerUpdater.initCtrl(function2Informer, table);
        final IMetaTableConstructor iMetaConstructor = ctrl2Meta.second;
        final GridCtrl ctrl = ctrl2Meta.first;
        viewInfo.setCtrl(ctrl);//?????????????????? ???????????????? ????????????
        ctrl.updateMeta(null);
        new PostponeOperationProvider(new PostponeOperationProvider.IPostponeOperation()
        {

            @Override
            public boolean operate()
            {

                if (iMetaConstructor.isMetaWasSet()) //?????????? ???????????????? ???????????????????? ???????????????????????????? ?????????????? ?? ???????????????? ???????????? ???????????? ??????????????
                {
                    filterDet1.setFilterDS(iMetaConstructor.getFilterDS());
                    filterDet1.setFieldMetaDS(iMetaConstructor.getFieldsMetaDS());

                    filterDet2.setFilterDS(iMetaConstructor.getFilterDS());
                    filterDet2.setFieldMetaDS(iMetaConstructor.getFieldsMetaDS());

                    parentCanvas.addChild(viewInfo.getInformer());

                    viewInfo.viewValues(getNoDataValues().first);
                    viewInfo.startUpdateData();


                    return true;
                }
                return false;
            }
        });
    }



    private TableFunction createTableFunction(FilterDet filterDet1, FilterDet filterDet2,DetailViewerInfo viewInfo)
    {

        FunctionDet functionDet1 = new FunctionDet(Aggregates.COUNT, "", filterDet1);
        FunctionDet functionDet2 = new FunctionDet(Aggregates.COUNT, "", filterDet2);

        List<TableFunctionElem> elements= new LinkedList<TableFunctionElem>();
        {
            final Map values = getNormaValues().first;
            values.put("eventImg", "info/semafor_r.gif");
            values.put("Comment", ">5-???? I ??????.");

            HashMap styleMap=new HashMap<String,String>();
            styleMap.put("Comment","MyRedStyle");
            styleMap.put("VIOL_I","MyRedStyle");
            styleMap.put("VIOL_II","MyYellowStyle");
            values.put(DetailViewerInfo.STYLE, styleMap);


            elements.add(new RefTableFunctionElem(new String[]{"VIOL_I","VIOL_II"},new FunctionDet[]{functionDet1,functionDet2},new Criterion("value", OperatorId.GREATER_THAN,5),values,viewInfo));
        }

        {
            final Map values = getNormaValues().first;
            values.put("eventImg", "info/semafor_r.gif");
            values.put("Comment", "?????????????????? I ??????.");

            HashMap styleMap=new HashMap<String,String>();
            styleMap.put("Comment","MyRedStyle");
            styleMap.put("VIOL_I","MyYellowStyle");
            styleMap.put("VIOL_II","MyYellowStyle");
            values.put(DetailViewerInfo.STYLE, styleMap);
            elements.add(new RefTableFunctionElem(new String[]{"VIOL_I","VIOL_II"},new FunctionDet[]{functionDet1,functionDet2},new Criterion("value", OperatorId.GREATER_THAN, 0),values,viewInfo));
        }

        {
            final Map values = getNormaValues().first;
            values.put("eventImg", "info/semafor_y.gif");
            values.put("Comment", ">5-???? II ??????.");
            values.put("VIOL_I", "??????????????:0");

            HashMap styleMap=new HashMap<String,String>();
            styleMap.put("Comment","MyYellowStyle");
            styleMap.put("VIOL_I",null);
            styleMap.put("VIOL_II",null);
            values.put(DetailViewerInfo.STYLE, styleMap);

            elements.add(new RefTableFunctionElem(new String[]{"VIOL_II"},new FunctionDet[]{functionDet2}, new Criterion("value", OperatorId.GREATER_THAN, 5), values,viewInfo));
        }

        {
            final Map values = getNormaValues().first;
            values.put("eventImg", "info/semafor_y.gif");
            values.put("Comment", "?????????????????? II ??????.");
            values.put("VIOL_I", "??????????????:0");

            HashMap styleMap=new HashMap<String,String>();
            styleMap.put("Comment",null);
            styleMap.put("VIOL_I",null);
            styleMap.put("VIOL_II",null);
            values.put(DetailViewerInfo.STYLE, styleMap);
            elements.add(new RefTableFunctionElem(new String[]{"VIOL_II"},new FunctionDet[]{functionDet2}, new Criterion("value", OperatorId.GREATER_THAN, 0), values,viewInfo));
        }

        return new TableFunction(elements, new DefTableFunctionElem(getNormaValues().first,viewInfo))
        {
            private TimeUpdaterCtrl timerCtrl= new TimeUpdaterCtrl(3,0);

            @Override
            public boolean isNeed2Recalculate()
            {
                return timerCtrl.isNeedCalcFilter();
            }

            public Object transmit(Record[] records)
            {
                for (Record record : records)
                    record.setAttribute(TablesTypes.TIMESTAMP_FIELD,timerCtrl.getCurrentDT());
                return super.transmit(records);
            }
        };
    }

    private static String getDorName(DetailViewerInfo viewInfo)
    {
        Integer in_dor_kod = viewInfo.getDorKod();

        return getDorNameByCode(in_dor_kod);
    }

    private static String getDorNameByCode(Integer in_dor_kod) {

        String dorName="?????? ????????????";
        {
            DataSource ways = NSI.getWayNSI();
            Record[] recs = ways.getCacheData();
            for (Record rec : recs)
            {
                final Integer dor_kod = rec.getAttributeAsInt(NSI.DOR_KOD);
                if (dor_kod.equals(in_dor_kod))
                {
                    dorName = rec.getAttribute(NSI.DOR_NAME);
                    break;
                }
            }
        }
        return dorName;
    }


    public static Pair<Map,Map<String,Pair<String, ListGridFieldType>>> getNoDataValues() {
        Map values=new HashMap();
        Map<String,Pair<String, ListGridFieldType>> meta= new LinkedHashMap<String,Pair<String, ListGridFieldType>>();
        values.put("dorName", "?????? ????????????");
        meta.put("dorName",new Pair<String, ListGridFieldType>("1",ListGridFieldType.TEXT));
        values.put("eventName", "??????????????????");
        meta.put("eventName",new Pair<String, ListGridFieldType>("2",ListGridFieldType.TEXT));
        values.put("eventImg", "info/semafor_w.gif");
        meta.put("eventImg",new Pair<String, ListGridFieldType>("3",ListGridFieldType.IMAGE));
        values.put("Comment", "?????? ????????????");
        meta.put("Comment",new Pair<String, ListGridFieldType>("4",ListGridFieldType.TEXT));
        values.put("VIOL_I", "?????? ????????????");
        meta.put ("VIOL_I",new Pair<String, ListGridFieldType>("I",ListGridFieldType.TEXT));
        values.put("VIOL_II",  "?????? ????????????");
        meta.put("VIOL_II",new Pair<String, ListGridFieldType>("II",ListGridFieldType.TEXT));

        HashMap styleMap=new HashMap<String,String>();
        styleMap.put("Comment",null);
        styleMap.put("VIOL_I",null);
        styleMap.put("VIOL_II",null);
        values.put(DetailViewerInfo.STYLE, styleMap);

        return new Pair<Map,Map<String,Pair<String, ListGridFieldType>>>(values,meta);
    }

    public static Pair<Map,Map<String,Pair<String, ListGridFieldType>>> getNormaValues()
    {
        Map values=new HashMap();
        Map<String,Pair<String, ListGridFieldType>> meta= new LinkedHashMap<String,Pair<String, ListGridFieldType>>();
        values.put("dorName", "?????? ????????????");//TODO ?????????????????????????????? ?????? ???????????? ??????????????
        meta.put("dorName",new Pair<String, ListGridFieldType>("1",ListGridFieldType.TEXT));
        values.put("eventName", "??????????????????");
        meta.put("eventName",new Pair<String, ListGridFieldType>("2",ListGridFieldType.TEXT));
        values.put("eventImg", "info/semafor_g.gif");
        meta.put("eventImg",new Pair<String, ListGridFieldType>("3",ListGridFieldType.IMAGE));
        values.put("Comment", "??????????");
        meta.put("Comment",new Pair<String, ListGridFieldType>("4",ListGridFieldType.TEXT));
        values.put("VIOL_I", "??????????");
        meta.put("VIOL_I",new Pair<String, ListGridFieldType>("I",ListGridFieldType.TEXT));
        values.put("VIOL_II", "??????????");
        meta.put("VIOL_II",new Pair<String, ListGridFieldType>("II",ListGridFieldType.TEXT));
        HashMap styleMap=new HashMap<String,String>();
        styleMap.put("Comment",null);
        styleMap.put("VIOL_I",null);
        styleMap.put("VIOL_II",null);
        values.put(DetailViewerInfo.STYLE, styleMap);

        return new Pair<Map,Map<String,Pair<String, ListGridFieldType>>>(values,meta);
    }



    public void run()
    {

        final Window winModal = OptionsViewers.createEmptyWindow("TECT");
        winModal.setAutoSize(false);
        winModal.setCanDragResize(true);


        final Canvas gridCanvas = new Canvas();
        gridCanvas.setBorder("1px solid blue");
        gridCanvas.setWidth100();
        gridCanvas.setHeight100();

        initInformer(gridCanvas,null);

        winModal.addItem(gridCanvas);
        winModal.draw();

    }



    private static class DefTableFunctionElem extends TableFunctionElem
    {
        private DetailViewerInfo viewInfo;

        public DefTableFunctionElem(Map values,DetailViewerInfo viewInfo)
        {
            super(null,(Criteria)null,values);
            this.viewInfo = viewInfo;
        }

        public Object getOutValue(Record[] records, Double defValue)
        {
            final Object outValue = super.getOutValue(records, defValue);
            ((Map)outValue).put("dorName", getDorName(viewInfo));
            return outValue;
        }
    }

    private static class RefTableFunctionElem extends TableFunctionElem
    {
        private DetailViewerInfo viewInfo;
        private FunctionDet[] functionDet;
        private String[] colNames;
        private Map styleMap;


        public RefTableFunctionElem(String[] colNames, FunctionDet[] functionDet, Criteria criteria, Map values, DetailViewerInfo viewInfo)
        {
            super(functionDet[0],criteria, values);
            this.viewInfo = viewInfo;
            this.functionDet=functionDet;
            this.colNames=colNames;

            Map styleMap= (Map) values.get(DetailViewerInfo.STYLE);
            if (styleMap!=null)
                this.styleMap=new HashMap(styleMap);

        }

        public Object getOutValue(Record[] records, Double defValue)
        {
            final Object outValue = super.getOutValue(records, defValue);
            if (defValue!=null)
            {
                if (styleMap!=null)
                    ((Map)outValue).put(DetailViewerInfo.STYLE, new HashMap(styleMap));

                ((Map)outValue).put(colNames[0],"??????????????:"+String.valueOf(defValue.intValue()));
                ((Map) outValue).put(colNames[0] + "_ORIG", defValue.intValue());
                for (int i = 1, colNamesLength = colNames.length; i < colNamesLength; i++)
                {
                    final Double value = functionDet[i].getValue(records);
                    if (value != null) {
                        ((Map) outValue).put(colNames[i], "??????????????:" + String.valueOf(value.intValue()));
                        ((Map) outValue).put(colNames[i] + "_ORIG", value.intValue());
                    }
                    if (value==null || value<=0)
                        ((Map)(((Map)outValue).get(DetailViewerInfo.STYLE))).put("VIOL_II",null);

                }
                ((Map)outValue).put("dorName", getDorName(viewInfo));
            }
            return outValue;
        }
    }
}
