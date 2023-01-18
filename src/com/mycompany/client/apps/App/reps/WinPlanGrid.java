package com.mycompany.client.apps.App.reps;

import com.google.gwt.i18n.client.NumberFormat;
import com.mycompany.client.CliFilterByCriteria;
import com.mycompany.client.ListGridWithDesc;
import com.mycompany.client.ReportListGridWithDesc;
import com.mycompany.client.apps.App.api.*;
import com.mycompany.client.apps.SimpleOperation;
import com.mycompany.client.operations.ICliFilter;
import com.mycompany.client.operations.IOperation;
import com.mycompany.client.operations.OperationCtx;
import com.mycompany.client.test.evalf.JSFormula;
import com.mycompany.client.updaters.BGridConstructor;
import com.mycompany.client.utils.*;
import com.mycompany.common.DescOperation;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.analit2.*;
import com.mycompany.common.cache.CacheException;
import com.smartgwt.client.data.*;
import com.smartgwt.client.types.*;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.grid.*;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 23.10.14
 * Time: 18:29
 * Грид для доступа к данным по местам событий
 * com.mycompany.client.apps.App.reps.WinPlanGrid
 */
public class WinPlanGrid extends AReportCreator
{

    public static final String INTERVAL_ND_KD = "zInterval_ND_KD_";
    public static final String INTERVAL_DT_ND_DT_KD = "zInterval_DT_ND_DT_KD_";

    Map<Integer,Record> dorCode2SumTotal = new HashMap<Integer,Record>();
    Record summTotal;


//    private String headerURL="CommonConsHeader.jsp";
//    private String dataURL = "transport/dataPlacesCons";

    private IGridMetaProvider gridMetaProvider;

    public WinPlanGrid(String headerURL, String dataURL, IGridMetaProvider gridMetaProvider)
    {
        this.gridMetaProvider = gridMetaProvider;
        if (headerURL!=null)
            this.headerURL=headerURL;
        if (dataURL!=null)
            this.dataURL=dataURL;
    }

    public WinPlanGrid(IGridMetaProvider gridMetaProvider)
    {
        this.gridMetaProvider = gridMetaProvider;
    }


    private void updateRecord(Record newRect,Record oldRecord)
    {
        String[] attrs=newRect.getAttributes();
        for (String attr : attrs)
        {
            Object value = newRect.getAttributeAsObject(attr);
            oldRecord.setAttribute(attr, value);
        }
    }



    ICliFilter sumFilter =new ICliFilter()
    {
        @Override
        public void setCriteria(Criteria cr) {

        }

        @Override
        public Record[] filter(Record[] records)
        {
            List<Record> rv = new LinkedList<Record>();
            for (Record record : records)
            {
                String place_id=record.getAttribute(TablesTypes.PLACE_ID);

                if (place_id==null)
                    continue;
                if (place_id.endsWith("##"))
                {
                    if (summTotal==null)
                        summTotal=record;
                    else
                        updateRecord(record,summTotal);
                }
                else
                if (place_id.endsWith("##00"))
                {
                    Integer dor_code=record.getAttributeAsInt(TablesTypes.DOR_CODE);
                    if (dor_code==null)
                        continue;

                    Record oldRecord = dorCode2SumTotal.get(dor_code);
                    if (oldRecord==null)
                        dorCode2SumTotal.put(dor_code, record);
                    else
                        updateRecord(record,oldRecord);
                }
                else
                    rv.add(record);
            }

            for (Record record : rv) {
                final Map map = record.toMap();
                final Object o = map.get("1");
                if (o !=null && !o.toString().equals("0"))
                {
                    rv.size();
                }
            }

            return rv.toArray(new Record[rv.size()]);
        }

        @Override
        public Criteria getCriteria() {
            return null;
        }
    };

    public static void callFunction(String id,int rowNum,int colNum,int ITotal)
    {
        boolean total= (ITotal > 0);

        Canvas rv = Canvas.getById(id);
        if (rv!=null && rv instanceof ReportListGridWithDesc)
        {
             IReportCreator creator=((ReportListGridWithDesc)rv).getCreator();


            IFilterUpdater updater = null;
            if (!total)
            {
                updater = new IFilterUpdater()
                {

                    @Override
                    public AdvancedCriteria updateFiler(AdvancedCriteria filter)
                    {
//                        filter.addCriteria(new Criterion("ID_TUCH", OperatorId.NOT_NULL));
//                        filter.addCriteria(new Criterion("ID_TUCH",OperatorId.NOT_EQUAL,0));
                        filter.addCriteria(new Criterion("F_Y_PLAN",OperatorId.NOT_EQUAL,0));
                        return filter;
                    }
                };
            }
            creator.onCellClickEvent(rowNum,colNum, updater);
        }
    }



    public static native void setFunction() /*-{


        if (!$wnd.window.fWinPlanGridGate)
            $wnd.window.fWinPlanGridGate= function(idGrid,rowNum,colNum,total)
            {
                if (!total)
                    total=0;
                @com.mycompany.client.apps.App.reps.WinPlanGrid::callFunction(Ljava/lang/String;III)(idGrid,rowNum,colNum,total)
            }

    }-*/;



    @Override
    public void allocateHeaders(final ListGridWithDesc grid, IAnalisysDesc desc)
    {
        try
        {
            super.allocateHeaders(grid,desc);

            final GrpDef[] keyCols=desc.getGrpXHierarchy();//Получим ключевые поля по X
            Map<String, ColDef> tupleDef = desc.getTupleDef();



            HeaderSpan grpSpan = new HeaderSpan();
            List<ListGridField> listFields= new LinkedList<ListGridField>();
            List<String> grpNames=new LinkedList<String>();

            //Ключевые поля
            for (int i = 0, keyColsLength = keyCols.length; i < keyColsLength; i++)
            {
                final GrpDef grpDef = keyCols[i];
                final String tid = grpDef.getTid();
                ColDef colDef = tupleDef.get(tid);
                ListGridField pdID = new ListGridField(tid, colDef.getTitle());

                pdID.setHidden(colDef.isHide());

                if (i<keyColsLength-1)
                {
                    pdID.setGroupValueFunction(new GroupValueFunction()
                    {
                        @Override
                        public Object getGroupValue(Object value, ListGridRecord record, ListGridField field, String fieldName, ListGrid grid) {
                            String tColId = grpDef.gettColId();
                            if (!tid.equals(tColId ))
                                return record.getAttribute(tColId );//Возврат группировки по заголовку группы
                            return value;//Возврат самого значения
                        }
                    });
                    pdID.setShowGridSummary(false);
                    grpNames.add(tid);
                    listFields.add(pdID);
                }
                else
                {
                    String tColId = grpDef.gettColId();
                    if (!tid.equals(tColId))
                    {
                        colDef = tupleDef.get(tColId);
                        pdID = new ListGridField(tColId, colDef.getTitle());
                        pdID.setHidden(colDef.isHide());
                        pdID.setShowGridSummary(false);
                        listFields.add(pdID);
                        pdID.setSortNormalizer(new SortNormalizer()
                        {
                            @Override
                            public Object normalize(ListGridRecord record, String fieldName)
                            {
                                return record.getAttributeAsInt("NUM");
                            }
                        });

                        if (!colDef.isHide())
                        {
                            grpSpan.setTitle(pdID.getTitle());
                            grpSpan.setFields(pdID.getName());

                            pdID.setTitle(String.valueOf(keyCols[0].getColN()));
                            grid.setGroupTitleField(tColId);//TODO подпорка поле должно описываться явно
                        }
                        grid.setInitialSort(new SortSpecifier(tColId, SortDirection.ASCENDING));
                    }
                }

            }

            grid.setCanMultiGroup(true);
            grid.setGroupByField(grpNames.toArray(new String[grpNames.size()]));
            grid.setGroupStartOpen(GroupStartOpen.NONE);




            HeaderSpan rootSpan = new HeaderSpan();
            int dHeight=30;
            int headerHeight= AnalitGridUtils.buildSpans2(rootSpan, root, "",key2Number,dHeight);

            //Разворачиваем Консолидационные поля (Показатели)
            for (final Integer ix : number2Key.keySet())
            {
                final String key=number2Key.get(ix);

                NNode2 nnode = key2NNode.get(key);
                //ListGridField pdID = new ListGridField(String.valueOf(ix),spans.get(ix).getTitle());
                final Integer colN = nnode.getColN();



                final Integer ix2;
                if (!key.endsWith("P#CNT2"))
                {
                    final String key2=key.replace("#CNT","P#CNT2");
                    ix2 = key2Number.get(key2);
                }
                else
                {
//                    pdID.setHidden(true);
//                    ix2=-1;
                    continue;
                }

                ListGridField pdID = new ListGridField(String.valueOf(ix),String.valueOf(colN));
                final ColDef def=tupleDef.get(nnode.getColId());
                pdID.setEmptyCellValue(def.getNval());


                pdID.setCellFormatter(new CellFormatter() {
                    @Override
                    public String format(Object value, ListGridRecord record, int rowNum, int colNum) {


                        final String zVal = def.getZval();
                        final String format = def.getFormat();
                        if (value != null)
                        {
                            if (value.equals(0) || value.equals(0l)
                                    || value.equals(0f) || value.equals(0d))
                                return zVal;

                            String ftype1 = def.getFtype();
                            if (ftype1 != null && format != null)
                            {

                                ListGridFieldType ftype = ListGridFieldType.valueOf(ftype1.toUpperCase());
                                if (
                                        value instanceof Number
                                                &&
                                        (ftype.equals(ListGridFieldType.INTEGER) || ftype.equals(ListGridFieldType.FLOAT))
                                    ) {
                                    return NumberFormat.getFormat(format).format((Number) value);
                                }
                            }


                            if (ix2!=null && ix2>=0)
                            {

                                final String isGridSummary = record.getAttribute("isGridSummary");
                                if (isGridSummary !=null && "true".equalsIgnoreCase(isGridSummary))
                                {
                                    String key2Val=summTotal.getAttribute(String.valueOf(ix2));
                                    if (key2Val!=null && key2Val.length()>0 && !"0".equals(key2Val))
                                    {
                                     setFunction();
                                     //return value.toString()+"  (<a href=\"#\" onclick=\"fWinPlanGridGate('"+grid.getID()+"',-1,"+colNum+");return false;\">"+key2Val+"</a>)";
                                      return "<a href=\"#\" onclick=\"fWinPlanGridGate('"+grid.getID()+"',-1,"+colNum+",1 );return false;\">"+value.toString()+"</a>"+"  (<a href=\"#\" onclick=\"fWinPlanGridGate('"+grid.getID()+"',-1,"+colNum+");return false;\">"+key2Val+"</a>)";


                                    }
                                }
                                else
                                {
                                    String key2Val =null;
                                    String grp = record.getAttribute("isGroupSummary");
                                    if (grp!=null && "true".equals(grp))
                                    {
                                        Record[] rs = record.getAttributeAsRecordArray("groupMembers");
                                        if (rs!=null && rs.length>0)
                                        {
                                            Integer dor_code=rs[0].getAttributeAsInt(TablesTypes.DOR_CODE);
                                            Record sumRecord = dorCode2SumTotal.get(dor_code);
                                            key2Val=sumRecord.getAttribute(String.valueOf(ix2));
                                        }
                                    }
                                    else
                                        key2Val=record.getAttribute(String.valueOf(ix2));
                                    if (key2Val!=null && key2Val.length()>0 && !"0".equals(key2Val))// && record.getAttribute("groupMembers")==null
                                    {
                                        setFunction();
                                        //return value.toString()+"  (<a href=\"javascript:fWinPlanGridGate('"+grid.getID()+"',"+rowNum+","+colNum+")\">"+key2Val+"</a>)";
                                        return value.toString()+"  (<a href=\"#\" onclick=\"fWinPlanGridGate('"+grid.getID()+"',"+rowNum+","+colNum+");return false;\">"+key2Val+"</a>)";
                                    }
                                }
                            }
                            return value.toString();
                        }
                        return def.getNval();
                    }
                });


//                pdID.setSummaryFunction(SummaryFunctionType.SUM);
                pdID.setSummaryFunction(new SummaryFunction() {
                    @Override
                    public Object getSummaryValue(Record[] records, ListGridField field)
                    {
                        Integer dor_code=records[0].getAttributeAsInt(TablesTypes.DOR_CODE);
                        int ln=grid.getCacheData().getLength();
                        if (dor_code!=null && ln!=records.length)
                        {
                            Record sumRecord = dorCode2SumTotal.get(dor_code);
                            if (sumRecord!=null)
                                return sumRecord.getAttributeAsObject(String.valueOf(ix));
                        }
                        else
                        {
                            if (summTotal!=null)
                                return summTotal.getAttributeAsObject(String.valueOf(ix));
                        }
                        return null;
                    }
                });
                pdID.setShowGridSummary(true);

                pdID.setAlign(Alignment.CENTER);
                pdID.setCellAlign(Alignment.CENTER);

                listFields.add(pdID);
            }


            grid.setFields(listFields.toArray(new ListGridField[listFields.size()]));

            grid.setShowGroupSummary(true);
            grid.setShowGroupSummaryInHeader(true);



            HeaderSpan[] viewValSpans = rootSpan.getSpans();
            List<HeaderSpan> allHeadSpans=new LinkedList<HeaderSpan>();
            allHeadSpans.add(grpSpan);
            allHeadSpans.addAll(Arrays.asList(viewValSpans));


            ListGridField[] fields = grid.getAllFields();
            rootSpan.setSpans(allHeadSpans.toArray(new HeaderSpan[allHeadSpans.size()]));
            for (ListGridField field : fields)
            {
                HeaderSpan foundSpan = AnalitGridUtils.findEdgeSpansByName(rootSpan, field.getName());
                if (foundSpan!=null)
                {

                    field.setTitle(foundSpan.getTitle());
                    foundSpan.setHeight(1);//При прорисовке выдаются ошибки если установить в ноль
                }
            }

            if (viewValSpans!=null && viewValSpans.length>0)
                grid.setHeaderSpans(allHeadSpans.toArray(new HeaderSpan[allHeadSpans.size()]));

            grid.setMetaWasSet(true);
            grid.setHeaderHeight(headerHeight - dHeight);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    protected void initData(final ListGridWithDesc grid, final Pair<DSCallback, MyDSCallback> dataCallBack, final String tType)
//    {
//
//        setCtrlOnDataFlow(grid,dataCallBack.second);
//
//        //получение данных и инициализация ими таблицы
//        Timer t=new Timer()
//        {
//            @Override
//            public void run()
//            {
//
//                DSRequest request = new DSRequest();
//                request.setShowPrompt(false);
//
//                String dataId = dataURL.replace(".", "_");
//                dataId = dataId.replace("/", "$");
//
//                final Criteria criteria = new Criteria(TablesTypes.TTYPE, tType);
//                criteria.addCriteria(TablesTypes.ID_TM, dataCallBack.second.getTimeStamp());
//                criteria.addCriteria(TablesTypes.ID_TN, dataCallBack.second.getTimeStampN());
//                criteria.addCriteria(TablesTypes.ID_REQN, String.valueOf(dataCallBack.second.getSrvCnt()));
//                String tblId = dataCallBack.second.getTblId();
//                if (tblId!=null)
//                    criteria.addCriteria(TablesTypes.TBLID,tblId);
//
//                DataSource.get(dataId).fetchData
//                (
//                        criteria, //Среди прочих параметров передается идентифкатор таблицы
//
//                        new DSCallback()
//                        {
//
//                            @Override
//                            public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest)
//                            {
//                                dataCallBack.second.execute(dsResponse,data,dsRequest);
//                                grid.setShowGridSummary(true);
//                            }
//                        }
//                        ,
//                        request
//                );
////*/
//            }
//        };
//
//
////        if (dynamicUpdate)
//        dataCallBack.second.setTimer(t);
//        t.schedule(200);
//
//
//
//    }

//    protected void setCtrlOnDataFlow(ListGridWithDesc grid, final MyDSCallback dataCallBack)
//    {
//        grid.setCtrl(new IDataFlowCtrl() {//TODO подпорка для остановки опроса сервера от таблицы отчетности
//            @Override
//            public Criteria getCriteria() {
//                return null;
//            }
//
//            @Override
//            public void setCriteria(Criteria criteria) {
//
//            }
//
//            @Override
//            public int getPeriod() {
//                return 0;
//            }
//
//            @Override
//            public void setPeriod(int period) {
//
//            }
//
//            @Override
//            public void stopUpdateData() {
//                dataCallBack.setTimer(null);
//            }
//
//            @Override
//            public void startUpdateData(boolean dynamicUpdate, DSCallback afterDataUpdate, int delayMillis) {
//
//            }
//
//            @Override
//            public void startUpdateMeta(DSCallback afterHeaderUpdate, int delayMillis) {
//
//            }
//
//            @Override
//            public void updateMetaAndData(boolean dynamicUpdate, boolean noData) {
//
//            }
//
//            @Override
//            public void updateMetaAndData(boolean dynamicUpdate, boolean noData, DSCallback afterDataUpdate, DSCallback afterHeaderUpdate) {
//
//            }
//
//            @Override
//            public void updateMetaAndData(boolean dynamicUpdate, int delayHeaderMillis, boolean noData, DSCallback afterDataUpdate, DSCallback afterHeaderUpdate, int delayDataMillis) {
//
//            }
//
//            @Override
//            public void updateMeta(DSCallback afterHeaderUpdate) {
//
//            }
//
//            @Override
//            public void updateData() {
//
//            }
//
//            @Override
//            public void startUpdateData(boolean dynamicUpdate) {
//
//            }
//
//            @Override
//            public void startUpdateData(boolean dynamicUpdate, DSCallback afterDataUpdate) {
//
//            }
//
//            @Override
//            public void setFullDataUpdate() {
//
//            }
//
//            @Override
//            public void removeAfterUpdater(DSCallback updater) {
//
//            }
//
//            @Override
//            public void addAfterUpdater(DSCallback updater) {
//
//            }
//
//            @Override
//            public boolean isTimer() {
//                return false;
//            }
//        });
//    }

    //protected Map<String,DescOperation> subTblName2SubDesc =new HashMap<String,DescOperation>();


    //Получим распарсеные ноды, уже связанные
    @Override
    public void setHeaders(final Window portlet, final ListGridWithDesc grid) throws CacheException
    {
        grid.setGroupByMaxRecords(10000);
        grid.setGroupByAsyncThreshold(5000);

        grid.setWidth100();
        grid.setHeight100();

        final Pair<DSCallback, MyDSCallback> dataCallBack = gridMetaProvider.initGrid2(new BGridConstructor(grid), headerURL, dataURL, 3000);
        gridMetaProvider.registerDataSource(dataURL,false,null);


        dataCallBack.second.setFilters(new LinkedList<ICliFilter>(Arrays.asList(sumFilter)));

        DSRequest request = new DSRequest();
        request.setShowPrompt(false);

        String dataId = headerURL.replace(".", "_");
        dataId = dataId.replace("/", "$");
        final Criteria criteria = new Criteria(TablesTypes.TTYPE, TablesTypes.WINPLAN);
        DataSource.get(dataId).fetchData
        (
            criteria,
            new DSCallback()
            {
                @Override
                public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest)
                {
                       Record[] descData = dsResponse.getData();
                       if (descData!=null && descData.length==1)
                       {
                           try {
                               IAnalisysDesc desc = new IAnalisysDescImpl(descData[0].getJsObj());
                               allocateHeaders(grid,desc);
                               portlet.addItem(grid);
                               initData(grid,dataCallBack, TablesTypes.WINPLAN);
                           }
                           finally
                           {
                               completely=true;
                           }
                       }
                }
            },
            request
        );

    }



    protected AdvancedCriteria createFilterByCriteria(String filter, Map mapTuple, GrpDef[] grpDef, int deep)
    {
        //Наложение фильтра
        AdvancedCriteria aFilter=null;
        if (filter!=null && filter.length()>0)
        {
            if (!filter.contains("_constructor"))
                filter="{\n" +
                        "    \"_constructor\":\"AdvancedCriteria\", \n" +
                        "    \"operator\":\"and\", \n" +
                        "    \"criteria\":["
                            +filter+
                        "    ]\n" +
                        "}";

             aFilter= new AdvancedCriteria(JScriptUtils.s2j(filter));
        }

        for (int i=1;i<deep;i++)
        {
            String tid=grpDef[i].getTid();
            Object val = mapTuple.get(tid);
            if (aFilter==null)
                aFilter= new AdvancedCriteria(OperatorId.AND);
            if (val==null)
                aFilter.appendToCriterionList(new Criterion(ExtendGridByPlaces.PLACE_CODE_NAME,OperatorId.IS_NULL));
            else
            {
                final String value = String.valueOf(val);
                String[] vals=value.split(TablesTypes.KEY_SEPARATOR);
                if (vals.length>1)
                    aFilter.appendToCriterionList(new Criterion(ExtendGridByPlaces.PLACE_CODE_NAME,OperatorId.ICONTAINS, ","+vals[1]+","));
            }
        }
        return aFilter;
    }



    protected void createEventTable(String tblName, String title, Map mapTuple, final Object dorCode, final Window owner, final AdvancedCriteria aFilter, final int deep)
    {

        DescOperation descRepGrid = grid.getDescOperation();
        Map<String,DescOperation> descMapParams = (Map<String, DescOperation>) descRepGrid.getDescMapParams();
        if (descMapParams==null)
            descRepGrid.setDescMapParams(descMapParams=new HashMap<String,DescOperation>());

        final OperationCtx ctx = new OperationCtx(null, owner);

        new DrillRepOperation(-100, -100,"Вверх", IOperation.TypeOperation.addEventPortlet,false).operate(owner,ctx);

        DescOperation savedSubTable=descMapParams.get(tblName);
        if (savedSubTable!=null)
        {
            savedSubTable.put(SimpleOperation.VIEW_NAME, title);
            new CreateEventTable(-100, -100, title, IOperation.TypeOperation.addEventPortlet, tblName).createOperation(savedSubTable).operate(null,ctx);
        }
        else
            new CreateEventTable(-100, -100, title, IOperation.TypeOperation.addEventPortlet, tblName).operate(null, ctx);


        new ExtendGridByPlaces(-100, -100, "", IOperation.TypeOperation.extendGridByPlace).operate(owner,ctx); //Добавляем поле для фильтрации по месту событий

        new PostponeOperationProvider(new PostponeOperationProvider.IPostponeOperation(){

            @Override
            public boolean operate()
            {
                Canvas[] items = owner.getItems();

                if (items!=null && items.length>0)
                {
                    final ListGridWithDesc tabGrid = (ListGridWithDesc) items[0];
                    if (tabGrid.isMetaWasSet() && tabGrid.getField(ExtendGridByPlaces.PLACE_CODE_NAME)!=null)
                    {


                        {

                            JSFormula jsf = tabGrid.createEmptyJSFormula(INTERVAL_ND_KD);
                            JSFormula jsf1 = tabGrid.createEmptyJSFormula(INTERVAL_DT_ND_DT_KD);
                            Map<String, String> letter2Name = jsf.getVarName2NameInRecord();


                            final String ndName = "ND";
                            final String kdName = "KD";

                            final String dt_ndName = "DT_ND";
                            final String dt_kdName = "DT_KD";

                            Map<String,String> name2l=new HashMap<String,String>();

                            br:
                            {

                                for (String letter : letter2Name.keySet())
                                {

                                    if (letter2Name.get(letter).equals(ndName))
                                        name2l.put(ndName,letter);
                                    else if (letter2Name.get(letter).equals(kdName))
                                        name2l.put(kdName,letter);
                                    else if (letter2Name.get(letter).equals(dt_ndName))
                                        name2l.put(dt_ndName,letter);
                                    else if (letter2Name.get(letter).equals(dt_kdName))
                                        name2l.put(dt_kdName,letter);
                                    if (name2l.size()==4)
                                    {
                                        {
                                            jsf.setExpressionValue("return srv.dHh("+name2l.get(ndName)+","+name2l.get(kdName)+")+Math.floor((srv.dMin("+name2l.get(ndName)+","+name2l.get(kdName)+")%60)*10/6)/100;");
                                            jsf.setFieldType(ListGridFieldType.FLOAT);
                                            jsf.setReCalcFormula(true);
                                            jsf.setPeriod(60000);
                                            jsf.setTitle("План (час)");
                                            tabGrid.addJSFormula(jsf,false,tabGrid.getFields().length-1);
                                        }

                                        {
                                            jsf1.setExpressionValue("return srv.dHh(" + name2l.get(dt_ndName) + "," + name2l.get(dt_kdName) + ")+Math.floor((srv.dMin(" + name2l.get(dt_ndName) + "," + name2l.get(dt_kdName) + ")%60)*10/6)/100;");
                                            jsf1.setFieldType(ListGridFieldType.FLOAT);
                                            jsf1.setReCalcFormula(true);
                                            jsf1.setPeriod(60000);
                                            jsf1.setTitle("Факт (час)");
                                            tabGrid.addJSFormula(jsf1,false,tabGrid.getFields().length-1);
                                        }

                                        break br;
                                    }

                                }

                                SC.say("Ошибка при создании поля интервала с момента начала отказа фильтрация событий работает не верно!");
                            }
                        }


                        new DorOperationFactory(-100, -100, "")
                        {
                            {
//                                this.getParams().add(new Criterion(TablesTypes.DOR_CODE, OperatorId.EQUALS,String.valueOf(dorCode)));//Добавляем код дороги
                                if (dorCode instanceof Integer)
                                    this.getParams().add(new Criterion(TablesTypes.DOR_CODE, OperatorId.EQUALS,String.valueOf(dorCode)));//Добавляем код дороги
                                else
                                    this.getParams().add(new Criterion(TablesTypes.DOR_CODE, OperatorId.NOT_NULL));//Дорога не пуста
                            }
                        }.operate(owner,ctx);

                        if (aFilter!=null)
                        {
                            NewFilterOperation operation = new NewFilterOperation(-100, -100, "", IOperation.TypeOperation.addClientFilter);
                            operation.setJustInit(false);
                            operation.setFilterByCriteria(new CliFilterByCriteria(null, aFilter));
                            operation.operate(owner,ctx);
                        }

                        //Модифицируем дескриптор
                        new DrillRepOperation(true,grid.getDescOperation()).operate(owner,ctx);

                        return true;
                    }
                }
                return false;
            }
        });
//        owner.setTitle(title);
    }



    public String getTitle(String strDate) {
        return "План проведения окон на "+strDate;
    }
}
