package com.mycompany.client.apps.App.reps;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.mycompany.client.ListGridWithDesc;
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
 * Грид для доступа к данным по действющим предпрждения по сравнению с заложенным графиком
 */
public class LOCGrid extends AReportCreator
{
//    private String headerURL="CommonConsHeader.jsp";
//    private String dataURL = "transport/dataCons";

    private IGridMetaProvider gridMetaProvider;

    public LOCGrid(String headerURL, String dataURL, IGridMetaProvider gridMetaProvider)
    {
        this.gridMetaProvider = gridMetaProvider;
        if (headerURL!=null)
            this.headerURL=headerURL;
        if (dataURL!=null)
            this.dataURL=dataURL;
    }

    public LOCGrid(IGridMetaProvider gridMetaProvider)
    {
        this.gridMetaProvider = gridMetaProvider;
    }


    private void updateRecord(Record newRect,Record oldRecord)
    {
        Map mp=newRect.toMap();
        for (Object key : mp.keySet())
            oldRecord.setAttribute((String) key, mp.get(key));
    }


    public void allocateHeaders(final ListGridWithDesc grid,IAnalisysDesc desc)
    {
        try
        {

            super.allocateHeaders(grid,desc);

            final GrpDef[] keyCols=desc.getGrpXHierarchy();//Получим ключевые поля по X



            List<ListGridField> ll= new LinkedList<ListGridField>();
            List<String> grpNames=new LinkedList<String>();

//            HeaderSpan grpSpan = new HeaderSpan();
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

                    String tColId = grpDef.gettColId();
                    if (!tid.equals(tColId ))
                    {
                        colDef = tupleDef.get(tColId);
                        pdID = new ListGridField(tColId, colDef.getTitle());
                        pdID.setHidden(colDef.isHide());
                        ll.add(pdID);
                    }

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
                                return record.getAttribute("DOR_KOD_NAME_P_ID");
                            }
                        });


                        if (!colDef.isHide())
                        {
                            pdID.setTitle(pdID.getTitle());
                            grid.setGroupTitleField(tColId);//TODO подпорка поле должно описываться явно
                        }
                        grid.setInitialSort(new SortSpecifier(tColId, SortDirection.ASCENDING));
                    }
                }

            }

            grid.setCanMultiGroup(true);
            grid.setGroupByField(grpNames.toArray(new String[grpNames.size()]));
            grid.setGroupStartOpen(GroupStartOpen.NONE);

//            Map<String,Integer> key2Number = new HashMap<String,Integer>();
//            Map<String,NNode2> key2NNode = new HashMap<String,NNode2>();
//            UtilsData.getKey2key2Number2(root.getNodes(),"", key2Number,key2NNode,0);
//            Map<Integer, String> number2Key = UtilsData.number2Key(key2Number);

            HeaderSpan rootSpan = new HeaderSpan();
            int dHeight=30;
            int headerHeight= AnalitGridUtils.buildSpans2(rootSpan, root, "",key2Number,dHeight);

            List<HeaderSpan> spans = new LinkedList<HeaderSpan>();
            AnalitGridUtils.removeEdgeSpans(rootSpan,spans);





            //Разворачиваем Консолидационные поля (Показатели)
            for (final Integer ix : number2Key.keySet())
            {
                NNode2 nnode = key2NNode.get(number2Key.get(ix));
                final ColDef def=tupleDef.get(nnode.getColId());

                ListGridField pdID = new ListGridField(String.valueOf(ix),spans.get(ix).getTitle());
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
                                    )
                                    return NumberFormat.getFormat(format).format((Number) value);
                            }
                            return value.toString();
                        }
                        return def.getNval();
                    }
                });

                pdID.setSummaryFunction(SummaryFunctionType.SUM);
                pdID.setShowGridSummary(true);

                pdID.setAlign(Alignment.CENTER);
                pdID.setCellAlign(Alignment.CENTER);
                ll.add(pdID);
            }


            grid.setFields(ll.toArray(new ListGridField[ll.size()]));

            grid.setShowGroupSummary(true);
            grid.setShowGroupSummaryInHeader(true);


            HeaderSpan[] viewValSpans = rootSpan.getSpans();

            List<HeaderSpan> allHeadSpans=new LinkedList<HeaderSpan>();

//            grpSpan.setHeight(headerHeight - dHeight);
//            allHeadSpans.add(grpSpan);

            allHeadSpans.addAll(Arrays.asList(viewValSpans));

            if (viewValSpans!=null && viewValSpans.length>0)
            {
                grid.setHeaderSpans(allHeadSpans.toArray(new HeaderSpan[allHeadSpans.size()]));
//                for (HeaderSpan allHeadSpan : allHeadSpans) {
//                    allHeadSpan.setHeight(60);
//                }
            }
            grid.setMetaWasSet(true);
            grid.setHeaderHeight(headerHeight - dHeight);


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


//    protected void initData(final ListGridWithDesc grid, final Pair<DSCallback, MyDSCallback> dataCallBack, final String tType)
//    {
//
//        setCtrlOnDataFlow(grid,dataCallBack.second);
//
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
//                            int hh=-1;
//
//                            @Override
//                            public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest)
//                            {
//                                dataCallBack.second.execute(dsResponse,data,dsRequest);
//                                {
//
//                                    long lastTimeStamp = dataCallBack.second.getLastTimeStamp();
//                                    Date dt=new Date(lastTimeStamp);
//                                    int hh = dt.getHours();
//                                    int mm = dt.getMinutes();
//
////                                    if (this.hh!=hh)
//                                    {
//                                        if ((hh*60+mm)>=TablesTypes.LOC_TR_MM)
//                                            dt=new Date(dt.getTime()+TablesTypes.DAY_MILS);
//
//                                        DateTimeFormat format = DateTimeFormat.getFormat("dd.MM.yyyy");
//                                        String strDate=format.format(dt);
//                                        String title=getTitle(strDate);
//                                        final Window target = grid.getTarget();
//                                        if (target!=null &&  !title.equals(target.getTitle()))
//                                           target.setTitle(title);
//                                        this.hh=hh;
//                                    }
//
////                                   Record[] recData = dsResponse.getData();
////                                   if (recData.length>0)
////                                   {
////                                       String strDate=recData[0].getAttribute("NDAY");
////                                       if (strDate!=null)
////                                       {
////                                           String title=getTitle(strDate);
////                                           final Window target = grid.getTarget();
////                                           if (target!=null &&  !strDate.equals(target.getTitle()))
////                                               target.setTitle(title);
////                                       }
////                                   }
//                                }
//                                grid.setShowGridSummary(true);
//                            }
//                        }
//                        ,
//                        request
//                );
//            }
//        };
//
////        if (dynamicUpdate)
//        dataCallBack.second.setTimer(t);
//        t.schedule(200);
//
//
//
//    }

//    protected DSCallback createDataDSCallBack(final Pair<DSCallback, MyDSCallback> dataCallBack, final ListGridWithDesc grid) {
//        return new DSCallback()
//        {
//            @Override
//            public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest)
//            {
//                dataCallBack.second.execute(dsResponse,data,dsRequest);
//
//                setRepTitle(dataCallBack, grid);
//
//
//                grid.setShowGridSummary(true);
//            }
//        };
//    }


    protected void setRepTitle(Pair<DSCallback, MyDSCallback> dataCallBack, ListGridWithDesc grid)
    {
        if (grid.isDrawn() && grid.isVisible())
        {
            long lastTimeStamp = dataCallBack.second.getLastTimeStamp();
            Date dt=new Date(lastTimeStamp);

            int mm = dt.getMinutes();

            if (this.dT!=mm)
            {
                int hh=dt.getHours();

                if ((hh*60+mm)>=TablesTypes.LOC_TR_MM)
                    dt=new Date(dt.getTime()+TablesTypes.DAY_MILS);

                DateTimeFormat format = DateTimeFormat.getFormat("dd.MM.yyyy");
                String strDate=format.format(dt);
                String title=getTitle(strDate);
                final Window target = grid.getTarget();
                if (target!=null &&  !title.equals(target.getTitle()))
                   target.setTitle(title);
                this.dT=mm;
            }
        }
    }


//    protected void setCtrlOnDataFlow(ListGridWithDesc grid, final MyDSCallback dataCallBack) {
//        grid.setCtrl(new IDataFlowCtrl() {//TODO подпорка для оставновки опроса сервера от таблицы отчетности
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
//            public void addAfterUpdater(DSCallback updater)
//            {
//
//            }
//
//            @Override
//            public boolean isTimer() {
//                return false;
//            }
//        });
//    }


    //Получим распарсеные ноды, уже связанные
    public void setHeaders(final Window portlet,final ListGridWithDesc grid) throws CacheException
    {
        grid.setGroupByMaxRecords(10000);
        grid.setGroupByAsyncThreshold(5000);

        grid.setWidth100();
        grid.setHeight100();

        final Pair<DSCallback, MyDSCallback> dataCallBack = gridMetaProvider.initGrid2(new BGridConstructor(grid), headerURL, dataURL, 3000);

        gridMetaProvider.registerDataSource(dataURL,false,null);


        //TODO Установка фильтров dataCallBack.second.setFilters(new LinkedList<ICliFilter>(Arrays.asList(sumFilter)));

        DSRequest request = new DSRequest();
        request.setShowPrompt(false);

        String dataId = headerURL.replace(".", "_");
        dataId = dataId.replace("/", "$");
        final Criteria criteria = new Criteria(TablesTypes.TTYPE, TablesTypes.LOC_DATA);
        DataSource.get(dataId).fetchData
        (
            criteria,
            new DSCallback()
            {
                @Override
                public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest)
                {
            //                                    try {
            //                                    получение данных из запросы

                       Record[] descData = dsResponse.getData();
                       if (descData!=null && descData.length==1)
                       {
                           try {
                               IAnalisysDesc desc = new IAnalisysDescImpl(descData[0].getJsObj());
                               allocateHeaders(grid, desc);
                               portlet.addItem(grid);
                               initData(grid,dataCallBack, TablesTypes.LOC_DATA);
                           }
                           finally
                           {
                               completely=true;
                           }
                       }

            //                                    } catch (CacheException cacheExcpetion) {
            //                                        cacheExcpetion.printStackTrace();
            //                                    }
                }
            },
            request
        );

    }

    public String getTitle(String strDate)
    {
//        Date dt = new Date(new Date().getTime());
//        DateTimeFormat format = DateTimeFormat.getFormat("dd.MM.yyyy");
//        strDate=format.format(dt);
        return "Оперативный контроль выдачи локомотивов на "+strDate;
    }
}
