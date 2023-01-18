package com.mycompany.client.apps.App.reps;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.mycompany.client.CliFilterByCriteria;
import com.mycompany.client.IDataFlowCtrl;
import com.mycompany.client.ListGridWithDesc;
import com.mycompany.client.ReportListGridWithDesc;
import com.mycompany.client.apps.App.api.CreateEventTable;
import com.mycompany.client.apps.App.api.DorOperationFactory;
import com.mycompany.client.apps.App.api.DrillRepOperation;
import com.mycompany.client.apps.App.api.NewFilterOperation;
import com.mycompany.client.apps.SimpleOperation;
import com.mycompany.client.operations.IOperation;
import com.mycompany.client.operations.OperationCtx;
import com.mycompany.client.utils.JScriptUtils;
import com.mycompany.client.utils.MyDSCallback;
import com.mycompany.client.utils.PostponeOperationProvider;
import com.mycompany.common.DescOperation;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.analit2.GrpDef;
import com.mycompany.common.analit2.IAnalisysDesc;
import com.mycompany.common.analit2.NNode2;
import com.mycompany.common.analit2.UtilsData;
import com.mycompany.common.cache.CacheException;
import com.mycompany.common.tables.CommonGridUtils;
import com.smartgwt.client.data.*;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.types.OperatorId;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

import java.util.*;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 04.08.15
 * Time: 15:54
 * класс связывет содержит описатель отчета и его соотношения ключей и узлов
 */
abstract public class AReportCreator implements IReportCreator
{

    protected String headerURL="CommonConsHeader.jsp";
    protected  String dataURL = "transport/dataPlacesCons";


    protected ListGridWithDesc grid;

    protected NNode2 root;
    protected Map<String,Integer> key2Number = new HashMap<String,Integer>();
    protected Map<String,NNode2> key2NNode = new HashMap<String,NNode2>();
    protected boolean completely=false;


    public Map<Integer, String> getNumber2Key() {
        return number2Key;
    }

    protected Map<Integer, String> number2Key = new HashMap<Integer, String>();
    protected IAnalisysDesc desc;

    @Override
    public boolean isCompletely() {
        return completely;
    }

    public Map<String, Integer> getKey2Number() {
        return key2Number;
    }

    public Map<String, NNode2> getKey2NNode() {
        return key2NNode;
    }

    public IAnalisysDesc getDesc(){return desc;}


    public void allocateHeaders(final ListGridWithDesc grid, IAnalisysDesc desc) throws Exception
    {
        this.desc=desc;
        if (((ReportListGridWithDesc) grid).getiAnalisysDesc() == null)
            ((ReportListGridWithDesc) grid).setiAnalisysDesc(desc);

//        root=new NNode2("ROOT","","ROOT",NNode2.NNodeType,null,false,null,desc.getNodes(),null);
        root = CommonGridUtils.findRootNode(desc);

        key2Number.clear();
        key2NNode.clear();
        UtilsData.getKey2key2Number2(root.getNodes(), "", key2Number, key2NNode, 0);
        number2Key = UtilsData.number2Key(key2Number);
    }


    protected void setCtrlOnDataFlow(ListGridWithDesc grid, final MyDSCallback dataCallBack)
    {
        grid.setCtrl(new IDataFlowCtrl() {//TODO подпорка для остановки опроса сервера от таблицы отчетности
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


    protected void initData(final ListGridWithDesc grid, final Pair<DSCallback, MyDSCallback> dataCallBack,final String tType)
    {

        setCtrlOnDataFlow(grid,dataCallBack.second);

        //получение данных и инициализация ими таблицы
        com.google.gwt.user.client.Timer t=new com.google.gwt.user.client.Timer()
        {
            @Override
            public void run()
            {

                DSRequest request = new DSRequest();
                request.setShowPrompt(false);

                String dataId = dataURL.replace(".", "_");
                dataId = dataId.replace("/", "$");

                final Criteria criteria = new Criteria(TablesTypes.TTYPE, tType);
                criteria.addCriteria(TablesTypes.ID_TM, dataCallBack.second.getTimeStamp());
                criteria.addCriteria(TablesTypes.ID_TN, dataCallBack.second.getTimeStampN());
                criteria.addCriteria(TablesTypes.ID_REQN, String.valueOf(dataCallBack.second.getSrvCnt()));
                String tblId = dataCallBack.second.getTblId();
                if (tblId!=null)
                    criteria.addCriteria(TablesTypes.TBLID,tblId);

                DataSource.get(dataId).fetchData
                (
                        criteria, //Среди прочих параметров передается идентифкатор таблицы

                        createDataDSCallBack(dataCallBack, grid),
                        request
                );
//*/
            }
        };


//        if (dynamicUpdate)
        dataCallBack.second.setTimer(t);
        t.schedule(200);



    }

    protected DSCallback createDataDSCallBack(final Pair<DSCallback, MyDSCallback> dataCallBack, final ListGridWithDesc grid) {
        return new DSCallback()
        {
            @Override
            public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest)
            {
                dataCallBack.second.execute(dsResponse,data,dsRequest);

                setRepTitle(dataCallBack, grid);


                grid.setShowGridSummary(true);
            }
        };
    }

    protected int dT=-1;
    protected void setRepTitle(Pair<DSCallback, MyDSCallback> dataCallBack, ListGridWithDesc grid)
    {

        if (grid.isDrawn() && grid.isVisible())
        {
            long lastTimeStamp = dataCallBack.second.getLastTimeStamp();
            Date dt=new Date(lastTimeStamp);
            int mm=dt.getMinutes();
            if (this.dT!=mm)
            {
                DateTimeFormat format = DateTimeFormat.getFormat("dd.MM.yyyy HH:mm");
                String strDate=format.format(dt);
                String title=getTitle(strDate);
                final Window target = grid.getTarget();
                if (target!=null &&  !title.equals(target.getTitle()))
                   target.setTitle(title);
                this.dT=mm;
            }
        }
    }


    public void setGrid(Window portlet, ListGridWithDesc grid)
    {
        this.grid=grid;
        if (((ReportListGridWithDesc) grid).getiAnalisysDesc() == null)
        {
            ((ReportListGridWithDesc) grid).setiAnalisysDesc(desc);
            ((ReportListGridWithDesc) grid).setCreator(this);
        }


        grid.setTarget(portlet);

//        Date date = new Date();
//        DateTimeFormat format = DateTimeFormat.getFormat("dd.MM.yyyy");
//        String strDate=format.format(date);
//        portlet.setTitle(getTitle(strDate)); //TODO !!!Определение Даты перенести на сервер!!!!
//        grid.setViewName(getTitle(strDate));
//TODO        portlet.setHeaderControls(HeaderControls.HEADER_LABEL);
        try {
            setHeaders(portlet,grid);
        } catch (CacheException cacheException) {
            cacheException.printStackTrace();
        }
    }

    public boolean isGroupClickAble(final Integer rowNum, final Integer colNum)
    {
        try {
            ListGridField fld = grid.getField(colNum);
            Integer ix=Integer.parseInt(fld.getName());
            String key = number2Key.get(ix);

            String noDrill=null;
            {
                NNode2 node = key2NNode.get(key);
                while (node!=null && ((noDrill=node.getNoDrill())==null || noDrill.length()==0))
                {
                    if (node.getParent()==root)
                        break;
                    node=node.getParent();
                }
            }
            return !"true".equalsIgnoreCase(noDrill);
        } catch (NumberFormatException e) {
            //
        }
        return false;
    }

    @Override
    public boolean isCellClickAble(final Integer rowNum, final Integer colNum)
    {
        if (rowNum==-1)
            return true;
        ListGridRecord rollOverRecord=grid.getRecord(rowNum);
        ListGridField field = grid.getField(colNum);
        Object val=rollOverRecord.getAttributeAsObject(field.getName());
        return val!=null && (val instanceof Number) && ((Number)val).doubleValue()>0;
    }

    @Override
    public void onCellClickEvent(final Integer rowNum, final Integer colNum, IFilterUpdater updater)
    {
       if (isGroupClickAble(rowNum, colNum) && isCellClickAble(rowNum,colNum))
            openEventsOnGroupClick(rowNum,colNum, updater);
    }


    public void openEventsOnGroupClick(final Integer rowNum, final Integer colNum, IFilterUpdater updater)
    {
        try {
            ListGridField fld = grid.getField(colNum);
            Integer ix=Integer.parseInt(fld.getName());
            String key = number2Key.get(ix);

            String tblName=null;
            {
                NNode2 node = key2NNode.get(key);
                while (node!=null && ((tblName=node.getTblName())==null || tblName.length()==0))
                {
                    if (node.getParent()==root)
                    {
                        tblName=node.getVal();
                        break;
                    }
                    node=node.getParent();
                }
            }

            String filter=null;
            {
                NNode2 node = key2NNode.get(key);
                while (node!=null && ((filter=node.getFilter())==null || filter.length()==0))
                {
                    if (node.getParent()==root)
                        break;
                    node=node.getParent();
                }
            }



            if (tblName!=null)
            {

                Record rec;
                if (rowNum>=0)
                    rec = grid.getRecord(rowNum);
                else
                    rec = grid.getRecord(0);

                Map mapTuple = rec.toMap();
                GrpDef[] grpDef = desc.getGrpXHierarchy();
                int deep=grpDef.length;
                {
                    List members;
                    while ((members = (List) mapTuple.get("groupMembers"))!=null)
                    {
                        mapTuple= (Map) members.get(0);
                        deep--;
                    }
                }

                String title;
                if (rowNum>=0)
                    title = getNodeTitle(key, mapTuple, deep);
                else
                    title = getNodeTitle(key, mapTuple, 0);


                AdvancedCriteria aFilter = createFilterByCriteria(filter, mapTuple, grpDef, deep);
                if (updater!=null)
                    aFilter=updater.updateFiler(aFilter);

                final Object dorCode;
                if (rowNum>=0)
                    dorCode = mapTuple.get(TablesTypes.DOR_CODE);
                else
                    dorCode=null;

                final Window owner = grid.getTarget();
                createEventTable(tblName, title, mapTuple, dorCode, owner, aFilter, deep);


            }

        } catch (NumberFormatException e) {
            //
        }

    }

    protected String getNodeTitle(String key, Map mapTuple, int deep)
    {
        String title="";
        {
            NNode2 node = key2NNode.get(key);
            final LinkedList<String> names = new LinkedList<String>();
            UtilsData.getValuesByNodeTree(node, names);

            GrpDef[] grpDef = desc.getGrpXHierarchy();

            for (int i = 0; i < deep; i++)
                names.add(String.valueOf(mapTuple.get(grpDef[i].gettColId())));
            title=UtilsData.getNameByList(names,"->");
            if (title.contains("<br>"))
                title=title.replace("<br>"," ");
        }

        return title;
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

        for (int i=0;i<deep;i++)
        {
            String tid=grpDef[i].getTid();
            Object val = mapTuple.get(tid);
            if (aFilter==null)
                aFilter= new AdvancedCriteria(OperatorId.AND);
            if (val==null)
                aFilter.appendToCriterionList(new Criterion(tid,OperatorId.IS_NULL));
            else if (val instanceof Integer)
                aFilter.appendToCriterionList(new Criterion(tid,OperatorId.EQUALS,(Integer)val));
            else if (val instanceof Long)
                aFilter.appendToCriterionList(new Criterion(tid,OperatorId.EQUALS,(Long)val));
            else if (val instanceof Float)
                aFilter.appendToCriterionList(new Criterion(tid,OperatorId.EQUALS,(Float)val));
            else
                aFilter.appendToCriterionList(new Criterion(tid,OperatorId.EQUALS,String.valueOf(val)));
        }
        return aFilter;
    }

    protected void createEventTable(String tblName, String title, Map mapTuple, final Object dorCode, final Window owner, final AdvancedCriteria aFilter, int deep)
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



        new PostponeOperationProvider(new PostponeOperationProvider.IPostponeOperation()
        {

            @Override
            public boolean operate()
            {
                Canvas[] items = owner.getItems();

                if (items!=null && items.length>0)
                {
                    final ListGridWithDesc tabGrid = (ListGridWithDesc) items[0];
                    if (tabGrid.isMetaWasSet())
                    {
                        new DorOperationFactory(-100, -100, "")
                        {
                            {
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
//                        DescOperation descRepGrid = grid.getDescOperation();
//                        DescOperation descTabOperation = tabGrid.getDescOperation();
//                        ((DescOperation)descRepGrid.getSubOperation().get(0)).getSubOperation().add(descTabOperation);
//                        tabGrid.setDescOperation(descRepGrid);
                        return true;
                    }
                }
                return false;
            }
        });

//        owner.setTitle(title);
    }




    abstract public String getTitle(String strDate);
}
