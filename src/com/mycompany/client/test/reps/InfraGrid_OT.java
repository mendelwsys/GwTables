package com.mycompany.client.test.reps;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Timer;
import com.mycompany.client.IDataFlowCtrl;
import com.mycompany.client.ListGridWithDesc;
import com.mycompany.client.operations.ICliFilter;
import com.mycompany.client.updaters.BGridConstructor;
import com.mycompany.client.utils.AnalitGridUtils;
import com.mycompany.client.utils.IGridMetaProvider;
import com.mycompany.client.utils.MyDSCallback;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.analit2.*;
import com.mycompany.common.cache.CacheException;
import com.smartgwt.client.data.*;
import com.smartgwt.client.types.*;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.grid.*;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 23.10.14
 * Time: 18:29
 * Грид для доступа к консолидационным данным
 */
public class InfraGrid_OT
{


    Map<String,Record> dorCode2SumTotal = new HashMap<String,Record>();
    Map<String,Map<String,Record>> dorCode2Vid2SumTotal = new HashMap<String,Map<String,Record>>();
    Record summTotal;


    private String headerURL="CommonConsHeader.jsp";
    private String dataURL = "transport/dataCons";
    private IGridMetaProvider gridMetaProvider;

    public InfraGrid_OT(String headerURL, String dataURL, IGridMetaProvider gridMetaProvider)
    {
        this.gridMetaProvider = gridMetaProvider;
        if (headerURL!=null)
            this.headerURL=headerURL;
        if (dataURL!=null)
            this.dataURL=dataURL;
    }

    public InfraGrid_OT(IGridMetaProvider gridMetaProvider)
    {
        this.gridMetaProvider = gridMetaProvider;
    }


    private void updateRecord(Record newRect,Record oldRecord)
    {
        Map mp=newRect.toMap();
        for (Object key : mp.keySet())
            oldRecord.setAttribute((String) key, mp.get(key));
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
                String dor_code=record.getAttribute(TablesTypes.DOR_CODE);
                String vid_id = record.getAttribute(TablesTypes.VID_ID);
                String pred_id=record.getAttribute(TablesTypes.PRED_ID);

                if (dor_code!=null && dor_code.equals(TablesTypes.DOR_CODE_4_DELAY_TRAINS_SUM_TOTAL))
                {
                    if (summTotal==null)
                        summTotal=record;
                    else
                        updateRecord(record,summTotal);
                }
                else
                if (dor_code!=null && vid_id!=null && vid_id.equals("-1"))
                {
                    Record oldRecord = dorCode2SumTotal.get(dor_code);
                    if (oldRecord==null)
                        dorCode2SumTotal.put(dor_code, record);
                    else
                        updateRecord(record,oldRecord);
                }
                else if (dor_code!=null && pred_id!=null && pred_id.contains(TablesTypes.HIDE_ATTR))
                {
                    Map<String, Record> vid2SumTotal = dorCode2Vid2SumTotal.get(dor_code);
                    if (vid2SumTotal==null)
                        dorCode2Vid2SumTotal.put(dor_code,vid2SumTotal=new HashMap<String, Record>());
                    Record oldRecord = vid2SumTotal.get(vid_id);
                    if (oldRecord==null)
                        vid2SumTotal.put(vid_id,record);
                    else
                        updateRecord(record,oldRecord);
                }
                else
                    rv.add(record);
            }
            return rv.toArray(new Record[rv.size()]);
        }

        @Override
        public Criteria getCriteria() {
            return null;
        }
    };

    public void allocateHeaders(final ListGridWithDesc grid,IAnalisysDesc desc)
    {
        try
        {
            NNode2 root=new NNode2("ROOT","","ROOT",NNode2.NNodeType,null,false,null,desc.getNodes(),null);


            final GrpDef[] keyCols=desc.getGrpXHierarchy();//Получим ключевые поля по X



            List<ListGridField> ll= new LinkedList<ListGridField>();
            List<String> grpNames=new LinkedList<String>();

            HeaderSpan grpSpan = new HeaderSpan();
            Map<String, ColDef> tupleDef = desc.getTupleDef();

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
//                    pdID.setGroupSummaryCustomizer(new GroupSummaryCustomizer() {
//                        @Override
//                        public Object[] getGroupSummary(ListGridRecord[] records, ListGridField field, GroupNode groupNode) {
//                            return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
//                        }
//                    });

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
                    ll.add(pdID);
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
                        ll.add(pdID);

                        pdID.setSortNormalizer(new SortNormalizer()
                        {
                            @Override
                            public Object normalize(ListGridRecord record, String fieldName)
                            {

                                //Record[] rc = record.getAttributeAsRecordArray("groupMembers");
                                Record[] rc=new Record[]{record};
                                //while (rc!=null && rc.length!=0)
                                int i=0;
                                for(;;)
                                {
                                    Record[] _rc=rc[0].getAttributeAsRecordArray("groupMembers");
                                    if (_rc==null || _rc.length==0)
                                    {
                                        break;
                                    }
                                    else
                                        rc=_rc;
                                    i++;
                                }
                                String dor_kod = rc[0].getAttribute("DOR_KOD");
                                if (dor_kod.length()<2)
                                    dor_kod="0"+dor_kod;

                                if (i==2)
                                    return dor_kod;

                                String vid=rc[0].getAttribute("VID_ID");
                                String vid_name=rc[0].getAttribute("VID_NAME");
                                if ("0".equals(vid))
                                    dor_kod+=";Я";
                                else
                                    dor_kod+=";"+vid_name;

                                if (i==1)
                                    return dor_kod;

                                String pred_name = rc[0].getAttribute("PRED_NAME");
                                if (TablesTypes.Z_PREDVAL.equals(pred_name))
                                    return dor_kod+=";Я"+ pred_name;

                                if (pred_name!=null)
                                {
                                    String pred_name1=pred_name;
                                    int ix1 = pred_name.indexOf("-") +1;
                                    if (ix1>0)
                                    {
                                        int ix2= ix1;
                                        String numbers="0000";
                                        while(ix2<pred_name.length() && Character.isDigit(pred_name.charAt(ix2)))
                                            ix2++;
                                        if (ix2!=ix1 && numbers.length()>=(ix2-ix1))
                                            pred_name1=pred_name.substring(0,ix1)+
                                                numbers.substring(0,numbers.length()-(ix2-ix1))+((ix1<pred_name.length())?pred_name.substring(ix1):"");
                                    }

                                    dor_kod+=";"+ pred_name1;
                                }
                                return dor_kod;
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



            Map<String,Integer> key2Number = new HashMap<String,Integer>();
            Map<String,NNode2> key2NNode = new HashMap<String,NNode2>();
            UtilsData.getKey2key2Number2(root.getNodes(),"", key2Number,key2NNode,0);


            Map<Integer, String> number2Key = UtilsData.number2Key(key2Number);

            //Разворачиваем Консолидационные поля (Показатели)

            for (final Integer ix : number2Key.keySet())
            {
                NNode2 nnode = key2NNode.get(number2Key.get(ix));

                ListGridField pdID = new ListGridField(String.valueOf(ix),String.valueOf(nnode.getColN()));

                final ColDef def=tupleDef.get(nnode.getColId());

                String nVal=def.getNval();




                pdID.setEmptyCellValue(nVal);
                pdID.setCellFormatter(new CellFormatter()
                {
                    @Override
                    public String format(Object value, ListGridRecord record, int rowNum, int colNum)
                    {
                        final String zVal=def.getZval();
                        final String format=def.getFormat();

                        if (value!=null)
                        {
                            if (value.equals(0) || value.equals(0l)
                                    || value.equals(0f)|| value.equals(0d))
                                return zVal;

                            String ftype1 = def.getFtype();
                            if (ftype1!=null && format!=null)
                            {

                                ListGridFieldType ftype = ListGridFieldType.valueOf(ftype1.toUpperCase());

                                if (
                                     ftype.equals(ListGridFieldType.INTEGER)  || ftype.equals(ListGridFieldType.FLOAT)
                                   )
                                   return NumberFormat.getFormat(format).format((Number) value);

//                                    return testRef2(record,NumberFormat.getFormat(format).format((Number)value));
//                                else if (
//                                     ftype.equals(ListGridFieldType.TIME)  || ftype.equals(ListGridFieldType.DATE) ||
//                                        ftype.equals(ListGridFieldType.DATETIME)
//                                   )
//                                return DateTimeFormat.getFormat(format).format((Date)value);//TODO Отладить формат времени обязателно
                            }

//                            if (grid.getSelectedRecord()==record)
//                                return testRef2(record,value.toString());
//                            else
                                return value.toString();

                        }
                        return def.getNval();
                    }
                });

                if (ix!=15 && ix!=16 && ix!=17 && ix!=18 && ix!=19 && ix!=20)  //TODO Передавать индексы в аттрибутах
                    pdID.setSummaryFunction(SummaryFunctionType.SUM);
                else
                    pdID.setSummaryFunction(new SummaryFunction()
                    {
                        @Override
                        public Object getSummaryValue(Record[] records, ListGridField field)
                        {
                            Integer summ=0;

                            Set<String> dor_codes=new HashSet<String>();
                            Set<String> vids=new HashSet<String>();
                            Set<String> preds=new HashSet<String>();

                            String dor_code=null;
                            String vid_id=null;

                            for (Record record : records)
                            {
                                Integer sum = record.getAttributeAsInt(String.valueOf(ix));
                                dor_code=record.getAttribute(TablesTypes.DOR_CODE);
                                if (dor_code!=null)
                                    dor_codes.add(dor_code);

                                if (dor_codes.size()<=1)
                                {
                                    vid_id=record.getAttribute("VID_ID");
                                    if (sum!=null && sum!=0)
                                    {
                                        if (vid_id!=null)
                                            vids.add(vid_id);
                                        String pred_id = record.getAttribute("PRED_ID");
                                        if (pred_id!=null)
                                            preds.add(pred_id);
                                    }
                                }

                                if (sum!=null && sum!=0)
                                    summ+=sum;
                            }

                            if (dor_codes.size()<=1)
                            {
                                if (vids.size()>1)
                                {
                                    if (dor_code!=null)
                                    {
                                        Record sumrec = dorCode2SumTotal.get(dor_code);
                                        if (sumrec!=null)
                                            return testRef(sumrec.getAttributeAsObject(String.valueOf(ix)));
                                    }
                                }

                                if (preds.size()>1)
                                {
                                    if (dor_code!=null && vid_id!=null)
                                    {
                                        Map<String, Record> vid2SumTotal = dorCode2Vid2SumTotal.get(dor_code);
                                        if (vid2SumTotal!=null)
                                        {
                                            Record sumrec = vid2SumTotal.get(vid_id);
                                            if (sumrec!=null)
                                                return testRef(sumrec.getAttributeAsObject(String.valueOf(ix)));
                                        }
                                    }
                                }
                            }
                            else if (summTotal!=null) {
                                Object summO = summTotal.getAttributeAsObject(String.valueOf(ix));
                                if (summO!=null)
                                    return testRef(summO);
                            }

                            return testRef(summ);
                            //return summ;
                        }
                    });

                pdID.setShowGridSummary(true);

                pdID.setAlign(Alignment.CENTER);
                pdID.setCellAlign(Alignment.CENTER);
                ll.add(pdID);
            }

            grid.setFields(ll.toArray(new ListGridField[ll.size()]));

            grid.setShowGroupSummary(true);
            grid.setShowGroupSummaryInHeader(true);



            HeaderSpan rootSpan = new HeaderSpan();

            int dHeight=30;
            int headerHeight= AnalitGridUtils.buildSpans2(rootSpan, root, "",key2Number,dHeight);

            grpSpan.setHeight(headerHeight - dHeight);
            
            HeaderSpan[] viewValSpans = rootSpan.getSpans();

            List<HeaderSpan> allHeadSpans=new LinkedList<HeaderSpan>();
            allHeadSpans.add(grpSpan);
            allHeadSpans.addAll(Arrays.asList(viewValSpans));

            //grid.setHeaderSpanHeight(0);


            ListGridField[] fields = grid.getAllFields();
            rootSpan.setSpans(allHeadSpans.toArray(new HeaderSpan[allHeadSpans.size()]));
            for (ListGridField field : fields)
            {
                HeaderSpan foundSpan = AnalitGridUtils.findEdgeSpansByName(rootSpan, field.getName());
                if (foundSpan!=null)
                {

                    field.setTitle(foundSpan.getTitle());
                    foundSpan.setHeight(1);//TODO при прорисовке выдаются ошибки если установить в ноль
//                    foundSpan.setTitle(null);
                }
            }

            if (viewValSpans!=null && viewValSpans.length>0)
                grid.setHeaderSpans(allHeadSpans.toArray(new HeaderSpan[allHeadSpans.size()]));
            grid.setHeaderHeight(headerHeight-dHeight);






        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private String testRef(Object summ) {
//        return "<a href='http://www.lenta.ru'>"+String.valueOf(summ)+"</a></p>";
//    }

    private Object testRef(Object summ) {
        return summ;
    }

    private String testRef2(ListGridRecord record,String summ)
    {
        String grp = record.getAttribute("isGroupSummary");
        if (grp!=null)
            return summ;
        else
            return "<a style='text-decoration:none;' href='#'>"+String.valueOf(summ)+"</a>";
    }


    private void initData(final ListGridWithDesc grid,final Pair<DSCallback, MyDSCallback> dataCallBack)
    {

        setCtrlOnDataFlow(grid,dataCallBack.second);

        //получение данных и инициализация ими таблицы
        Timer t=new Timer()
        {
            @Override
            public void run()
            {

                DSRequest request = new DSRequest();
                request.setShowPrompt(false);

                String dataId = dataURL.replace(".", "_");
                dataId = dataId.replace("/", "$");

                final Criteria criteria = new Criteria(TablesTypes.TTYPE, TablesTypes.STATEDESC);
                criteria.addCriteria(TablesTypes.ID_TM, dataCallBack.second.getTimeStamp());
                criteria.addCriteria(TablesTypes.ID_TN, dataCallBack.second.getTimeStampN());
                criteria.addCriteria(TablesTypes.ID_REQN, String.valueOf(dataCallBack.second.getSrvCnt()));
                String tblId = dataCallBack.second.getTblId();
                if (tblId!=null)
                    criteria.addCriteria(TablesTypes.TBLID,tblId);

                DataSource.get(dataId).fetchData
                (
                        criteria, //Среди прочих параметров передается идентифкатор таблицы

                        new DSCallback()
                        {

                            @Override
                            public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest)
                            {
                                dataCallBack.second.execute(dsResponse,data,dsRequest);
//                                grid.setShowGridSummary(true); //!!!TODO Не работает хоть тресни!!!
                            }
                        }
                        ,
                        request
                );
            }
        };

//        if (dynamicUpdate)
        dataCallBack.second.setTimer(t);
        t.schedule(200);



    }

    private void setCtrlOnDataFlow(ListGridWithDesc grid, final MyDSCallback dataCallBack) {
        grid.setCtrl(new IDataFlowCtrl() {//TODO подпорка для оставновки опроса сервера от таблицы отчетности
            @Override
            public Criteria getCriteria() {
                return null;
            }

            @Override
            public void setCriteria(Criteria criteria) {

            }

            @Override
            public int getPeriod() {
                return 0;
            }

            @Override
            public void setPeriod(int period) {

            }

            @Override
            public void stopUpdateData() {
                dataCallBack.setTimer(null);
            }

            @Override
            public void startUpdateData(boolean dynamicUpdate, DSCallback afterDataUpdate, int delayMillis) {

            }

            @Override
            public void startUpdateMeta(DSCallback afterHeaderUpdate, int delayMillis) {

            }

            @Override
            public void updateMetaAndData(boolean dynamicUpdate, boolean noData) {

            }

            @Override
            public void updateMetaAndData(boolean dynamicUpdate, boolean noData, DSCallback afterDataUpdate, DSCallback afterHeaderUpdate) {

            }

            @Override
            public void updateMetaAndData(boolean dynamicUpdate, int delayHeaderMillis, boolean noData, DSCallback afterDataUpdate, DSCallback afterHeaderUpdate, int delayDataMillis) {

            }

            @Override
            public void updateMeta(DSCallback afterHeaderUpdate) {

            }

            @Override
            public void updateData() {

            }

            @Override
            public void startUpdateData(boolean dynamicUpdate) {

            }

            @Override
            public void startUpdateData(boolean dynamicUpdate, DSCallback afterDataUpdate) {

            }

            @Override
            public void setFullDataUpdate() {

            }

            @Override
            public void removeAfterUpdater(DSCallback updater) {

            }

            @Override
            public void addAfterUpdater(DSCallback updater) {

            }

            @Override
            public boolean isTimer() {
                return false;
            }
        });
    }


    //Получим распрсенные ноды, уже связанные
    public void setHeaders(final Window portlet,final ListGridWithDesc grid) throws CacheException
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
        DataSource.get(dataId).fetchData
        (
            new Criteria(),
            new DSCallback()
            {
                @Override
                public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest)
                {
            //                                    try {
            //                                    получение данных из запросы

                       Record[] testData = dsResponse.getData();
                       if (testData!=null && testData.length==1)
                       {
                           IAnalisysDesc desc = new IAnalisysDescImpl(testData[0].getJsObj());
                           allocateHeaders(grid,desc);
                           portlet.addItem(grid);
                           initData(grid,dataCallBack);
                       }

            //                                    } catch (CacheException cacheExcpetion) {
            //                                        cacheExcpetion.printStackTrace();
            //                                    }
                }
            },
            request
        );

    }

    public void setGrid(Window portlet,ListGridWithDesc grid)
    {
        Date date = new Date();
        DateTimeFormat format = DateTimeFormat.getFormat("dd.MM.yyyy");
        String strDate=format.format(date);

        portlet.setTitle("Состояние инфраструктуры. Службы. на "+strDate); //TODO !!!Определение Даты перенести на сервер!!!!

//TODO        portlet.setHeaderControls(HeaderControls.HEADER_LABEL);
        try {
            setHeaders(portlet,grid);
        } catch (CacheException cacheException) {
            cacheException.printStackTrace();
        }
    }
}
