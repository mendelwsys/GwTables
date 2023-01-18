package com.mycompany.client.test.informer;

import com.google.gwt.core.client.JavaScriptException;
import com.mycompany.client.GUIStateDesc;
import com.mycompany.client.GridCtrl;
import com.mycompany.client.apps.App.NSI;
import com.mycompany.client.apps.App.api.CreateInformerOperation;
import com.mycompany.client.apps.App.api.NewWarnInformer;
import com.mycompany.client.test.fbuilder.*;
import com.mycompany.client.updaters.IMetaTableConstructor;
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

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 11.03.15
 * Time: 15:51
 *
 */
public class WarnInformer implements Runnable {


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
            setParentWindowTitle("" + ((Long) descInformer.get(CreateInformerOperation.CREATED_INFORMER_ID)).longValue(), "Предупреждения: " + getDorNameByCode((Integer) descInformer.get(CreateInformerOperation.DOR_KOD)));

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
                        warnNative("" + ((Long) descInformer.get(CreateInformerOperation.CREATED_INFORMER_ID)).longValue(), "Внимание!!!", "Обнаружено отклонение от нормы параметра " + violatedParam + ". Старое значение: " + oldValue + ", новое значение: " + newValue, this.isHighLevelWarning());

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
                    Integer in_dor_kod= (Integer) descInformer.get(NewWarnInformer.DOR_KOD);
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
                        descInformer.put(NewWarnInformer.DOR_KOD,dorKod);
                    setCtrlCriteria();
                    this.viewValues(WarnInformer.getNoDataValues().first);
                    ctrl.setFullDataUpdate();
                }
                else
                {
                    if (descInformer!=null)
                        descInformer.put(NewWarnInformer.DOR_KOD,dorKod);
                    buildAndStartInformer(parentCanvas, this);
                }
            }
        };

        Integer w= (Integer) descInformer.get(NewWarnInformer.CRD_LEFT);
        Integer h= (Integer) descInformer.get(NewWarnInformer.CRD_TOP);
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
        descInformer.put(NewWarnInformer.CRD_LEFT,w);
        descInformer.put(NewWarnInformer.CRD_TOP, h);

        if (descInformer.get("MODE") == null || descInformer.get("MODE").equals(GUIStateDesc.BROWSER)) {
            viewInfo.getInformer().setLeft(w);
            viewInfo.getInformer().setTop(h);
        }

        viewInfo.getInformer().setWidth(200);
//        viewInfo.getInformer().setHeight(300);


        int dor_kod=viewInfo.getDorKod();
        if (dor_kod>0)
            buildAndStartInformer(parentCanvas, viewInfo);
        else {
            SetRoadDlg.createViewOptionsDlg(viewInfo).show();

        }
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
    Пример информера 1.

    Фильтруем предупрждения по цвету.

    Если их кол-во более чем N - настраивается, тогда красный
    Если их кол-во более чем M - настраивается, тогда желтый

    Настройка -

        дорога, коментарий - красный
        дорога, коментарий - зеленый


    Пример информера 2.


       Если есть остодефектный рельс на путях (Если один, тогда коментарий ПЧ=)
       Если до 5-ти - тогда перечисление
            Если не один тогда - тогда много ПЧ
    */

    private void buildAndStartInformer(final Canvas parentCanvas, final DetailViewerInfo viewInfo)
    {
        String table= TablesTypes.WARNINGS;
        String fName1 ="Скорость < 15";
        //Задаем фильтр
        final String filterCriteria1 = "{\n" +
                "    \"_constructor\":\"AdvancedCriteria\", \n" +
                "    \"operator\":\"and\", \n" +
                "    \"criteria\":[\n" +
                "        {\n" +
                "            \"fieldName\":\"colorStatus\", \n" +
                "            \"operator\":\"equals\", \n" +
                "            \"value\":0\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        String fName2 ="Скорость < 25";
        final String filterCriteria2 = "{\n" +
                "    \"_constructor\":\"AdvancedCriteria\", \n" +
                "    \"operator\":\"and\", \n" +
                "    \"criteria\":[\n" +
                "        {\n" +
                "            \"fieldName\":\"colorStatus\", \n" +
                "            \"operator\":\"equals\", \n" +
                "            \"value\":1\n" +
                "        }\n" +
                "    ]\n" +
                "}";


        final FilterDet filterDet1 = new FilterDet(table, KnownTables.getTableMap().get(table), filterCriteria1, fName1);
        final FilterDet filterDet2 = new FilterDet(table, KnownTables.getTableMap().get(table), filterCriteria2, fName2);

        final TableFunction tblFunction = createTableFunction(filterDet1, filterDet2,viewInfo);
        final Pair<TableFunction, Informer> function2Informer=new Pair<TableFunction, Informer>(tblFunction,viewInfo);

        Pair<GridCtrl, IMetaTableConstructor> ctrl2Meta = InformerUpdater.initCtrl(function2Informer, table);
        final IMetaTableConstructor iMetaConstructor = ctrl2Meta.second;
        final GridCtrl ctrl = ctrl2Meta.first;
        viewInfo.setCtrl(ctrl);//Удалением управлет канвас
        ctrl.updateMeta(null);
        new PostponeOperationProvider(new PostponeOperationProvider.IPostponeOperation()
        {

            @Override
            public boolean operate()
            {

                if (iMetaConstructor.isMetaWasSet()) //после прибытия метаданных инициализирует функции и стартуем апдейт данных виджета
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
            values.put("Speed", "Скорость<15(> 5-ти событий)");

            HashMap styleMap=new HashMap<String,String>();
            styleMap.put("Speed","MyRedStyle");
            styleMap.put("SPEED15","MyRedStyle");
            styleMap.put("SPEED25","MyYellowStyle");
            values.put(DetailViewerInfo.STYLE, styleMap);
            elements.add(new WarnTableFunctionElem(new String[]{"SPEED15","SPEED25"},new FunctionDet[]{functionDet1,functionDet2},new Criterion("value", OperatorId.GREATER_THAN,5),values,viewInfo));
        }

        {
            final Map values = getNormaValues().first;
            values.put("eventImg", "info/semafor_r.gif");
            values.put("Speed", "Скорость<15");

            HashMap styleMap=new HashMap<String,String>();
            styleMap.put("Speed","MyRedStyle");
            styleMap.put("SPEED15","MyYellowStyle");
            styleMap.put("SPEED25","MyYellowStyle");
            values.put(DetailViewerInfo.STYLE, styleMap);
            elements.add(new WarnTableFunctionElem(new String[]{"SPEED15","SPEED25"},new FunctionDet[]{functionDet1,functionDet2},new Criterion("value", OperatorId.GREATER_THAN, 0),values,viewInfo));
        }

        {
            final Map values = getNormaValues().first;
            values.put("eventImg", "info/semafor_y.gif");
            values.put("Speed", "Скорость<25 (> 5-ти событий)");
            values.put("SPEED15", "Событий:0");

            HashMap styleMap=new HashMap<String,String>();
            styleMap.put("Speed","MyYellowStyle");
            styleMap.put("SPEED15",null);
            styleMap.put("SPEED25","MyYellowStyle");
            values.put(DetailViewerInfo.STYLE, styleMap);
            elements.add(new WarnTableFunctionElem(new String[]{"SPEED25"},new FunctionDet[]{functionDet2}, new Criterion("value", OperatorId.GREATER_THAN, 5), values,viewInfo));
        }

        {
            final Map values = getNormaValues().first;
            values.put("eventImg", "info/semafor_y.gif");
            values.put("Speed", "Скорость<25");
            values.put("SPEED15", "Событий:0");
            HashMap styleMap=new HashMap<String,String>();
            styleMap.put("Speed",null);
            styleMap.put("SPEED15",null);
            styleMap.put("SPEED25",null);
            values.put(DetailViewerInfo.STYLE, styleMap);
            elements.add(new WarnTableFunctionElem(new String[]{"SPEED25"},new FunctionDet[]{functionDet2}, new Criterion("value", OperatorId.GREATER_THAN, 0), values,viewInfo));
        }

        return new TableFunction(elements, new DefTableFunctionElem(getNormaValues().first,viewInfo));
    }

    private static String getDorName(DetailViewerInfo viewInfo)
    {
        Integer in_dor_kod = viewInfo.getDorKod();

        return getDorNameByCode(in_dor_kod);
    }

    private static String getDorNameByCode(Integer in_dor_kod) {

        String dorName="Нет данных";
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
        values.put("dorName", "Нет данных");
        meta.put("dorName",new Pair<String, ListGridFieldType>("1",ListGridFieldType.TEXT));
        values.put("eventName", "Предупреждения");
        meta.put("eventName",new Pair<String, ListGridFieldType>("2",ListGridFieldType.TEXT));
        values.put("eventImg", "info/semafor_w.gif");
        meta.put("eventImg",new Pair<String, ListGridFieldType>("3",ListGridFieldType.IMAGE));
        values.put("Speed", "Нет данных");
        meta.put("Speed",new Pair<String, ListGridFieldType>("4",ListGridFieldType.TEXT));
        values.put("SPEED15", "Нет данных");
        meta.put("SPEED15",new Pair<String, ListGridFieldType>("<15",ListGridFieldType.TEXT));
        values.put("SPEED25",  "Нет данных");
        meta.put("SPEED25",new Pair<String, ListGridFieldType>("<25",ListGridFieldType.TEXT));


        HashMap styleMap=new HashMap<String,String>();
        styleMap.put("Speed",null);
        styleMap.put("SPEED15",null);
        styleMap.put("SPEED25",null);
        values.put(DetailViewerInfo.STYLE, styleMap);

        return new Pair<Map,Map<String,Pair<String, ListGridFieldType>>>(values,meta);
    }

    public static Pair<Map,Map<String,Pair<String, ListGridFieldType>>> getNormaValues()
    {
        Map values=new HashMap();
        Map<String,Pair<String, ListGridFieldType>> meta= new LinkedHashMap<String,Pair<String, ListGridFieldType>>();
        values.put("dorName", "Нет данных");//TODO Устанавливается при вызове функции
        meta.put("dorName",new Pair<String, ListGridFieldType>("1",ListGridFieldType.TEXT));
        values.put("eventName", "Предупреждения");
        meta.put("eventName",new Pair<String, ListGridFieldType>("2",ListGridFieldType.TEXT));
        values.put("eventImg", "info/semafor_g.gif");
        meta.put("eventImg",new Pair<String, ListGridFieldType>("3",ListGridFieldType.IMAGE));
        values.put("Speed", "Норма");
        meta.put("Speed",new Pair<String, ListGridFieldType>("4",ListGridFieldType.TEXT));
        values.put("SPEED15", "Норма");
        meta.put("SPEED15",new Pair<String, ListGridFieldType>("<15",ListGridFieldType.TEXT));
        values.put("SPEED25", "Норма");
        meta.put("SPEED25",new Pair<String, ListGridFieldType>("<25",ListGridFieldType.TEXT));

        HashMap styleMap=new HashMap<String,String>();
        styleMap.put("Speed",null);
        styleMap.put("SPEED15",null);
        styleMap.put("SPEED25",null);
        values.put(DetailViewerInfo.STYLE, styleMap);

        return new Pair<Map,Map<String,Pair<String, ListGridFieldType>>>(values,meta);
    }



    public void run()
    {

        // final Window winModal = OptionsViewers.createEmptyWindow("TECT");
        //    winModal.setAutoSize(false);
        //   winModal.setCanDragResize(true);
        //    winModal.setIsModal(false);


        final Canvas gridCanvas = new Canvas();
        // gridCanvas.setBorder("1px solid blue");
        gridCanvas.setWidth(300);
        gridCanvas.setHeight(200);

        initInformer(gridCanvas, new DescOperation());

//        winModal.addItem(gridCanvas);
        gridCanvas.draw();

    }


//TODO Для удаления 19032015
//        public void _run()
//        {
//
//            final Window winModal = OptionsViewers.createEmptyWindow("TECT");
//            winModal.setAutoSize(false);
//            winModal.setCanDragResize(true);
//
//
//            final Canvas gridCanvas = new Canvas();
//            gridCanvas.setBorder("1px solid blue");
//            gridCanvas.setWidth100();
//            gridCanvas.setHeight100();
//
//
//            Map values=new HashMap();
//            Map<String,Pair<String, ListGridFieldType>> meta= new LinkedHashMap<String,Pair<String, ListGridFieldType>>();
//
//
//
//            String eventName= StripCNST.WIN_NAME;
//            String dorName="ОКТ";
//            String predId="ПЧ-1";
//            String SPEED15="Передержка";
//            final String place1 = "ПСКОВ-КЕБ";
//            final String place2 = "ПСКОВ-ТОРОШИНО";
//
//
//
//            values.put("eventImg", "info/semafor_g.gif");
//            meta.put("eventImg",new Pair<String, ListGridFieldType>("1",ListGridFieldType.IMAGE));
//            values.put("period", eventName + "<br><b>" + dorName + "," + predId);
//            meta.put("period",new Pair<String, ListGridFieldType>("2",ListGridFieldType.TEXT));
//            values.put("place", place1);
//            meta.put("place",new Pair<String, ListGridFieldType>("3",ListGridFieldType.TEXT));
//            values.put("SPEED15", SPEED15);
//            meta.put("SPEED15",new Pair<String, ListGridFieldType>("4",ListGridFieldType.TEXT));
//
//            final DetailViewerInfo viewInfo = new DetailViewerInfo(meta, values);
//
//
//            gridCanvas.addChild(viewInfo.getNormaValues());
//
//            new PostponeOperationProvider(new PostponeOperationProvider.IPostponeOperation()
//            {
//                int ix=0;
//
//                @Override
//                public boolean operate()
//                {
//                    Map values=new HashMap();
//
//                    if (ix%2==0)
//                    {
//                        values.put("eventImg", "info/semafor_r.gif");
//                        values.put("place", place1);
//                        values.put(DetailViewerInfo.STYLE, new Pair<String,String>("eventImg","MyRedStyle"));
//                    }
//                    else
//                    {
//                        values.put("eventImg", "info/semafor_g.gif");
//                        values.put("place", place2);
//                        values.put(DetailViewerInfo.STYLE, new Pair<String,String>("eventImg","MyGreenStyle"));
//                    }
//                    viewInfo.viewValues(values);
//                    ix++;
//                    return false;  //To change body of implemented methods use File | Settings | File Templates.
//                }
//            },3000);
//
//
//            winModal.addItem(gridCanvas);
//            winModal.draw();
//        }


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

    private static class WarnTableFunctionElem extends TableFunctionElem
    {
        private DetailViewerInfo viewInfo;
        private FunctionDet[] functionDet;
        private String[] colNames;
        private Map styleMap;


        public WarnTableFunctionElem(String[] colNames,FunctionDet[] functionDet,Criteria criteria,  Map values,DetailViewerInfo viewInfo)
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


                ((Map)outValue).put(colNames[0],"Событий:"+String.valueOf(defValue.intValue()));
                ((Map) outValue).put(colNames[0] + "_ORIG", defValue.intValue());
                for (int i = 1, colNamesLength = colNames.length; i < colNamesLength; i++)
                {
                    final Double value = functionDet[i].getValue(records);
                    if (value!=null)
                        ((Map)outValue).put(colNames[i], "Событий:" + String.valueOf(value.intValue()));
                    ((Map) outValue).put(colNames[i] + "_ORIG", value.intValue());
                    if (value==null || value<=0)
                        ((Map)(((Map)outValue).get(DetailViewerInfo.STYLE))).put("SPEED25",null);

                }
                ((Map)outValue).put("dorName", getDorName(viewInfo));
            }
            return outValue;
        }

    }
}
