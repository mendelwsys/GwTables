package com.mycompany.client.test.t5GridApp;

import com.mycompany.client.test.TestBuilder;
import com.mycompany.client.utils.AnalitGridUtils;
import com.mycompany.common.analit.*;
import com.mycompany.common.cache.CacheException;
import com.mycompany.common.cache.IKeyGenerator;
import com.mycompany.common.cache.INm2Ix;
import com.mycompany.common.cache.SimpleKeyGenerator;
import com.smartgwt.client.data.*;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.GroupStartOpen;
import com.smartgwt.client.widgets.grid.*;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.PortalLayout;
import com.smartgwt.client.widgets.layout.Portlet;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 21.10.14
 * Time: 18:52
 *
 */
public class TXML2  implements TestBuilder
{
    NNode root = new NNode("");
    Set<String> res;

    void setHeadGrid(ListGrid grid,IAnalisysDesc desc) throws CacheException
    {

        UtilsData.addAllY2Node(root, 0, desc); //Инициализирем ноды
        res=UtilsData.getFieldNamesByNode(root);//получим все прописанные поля.

        final GrpDef[] keyCols=desc.getGrpXHierarchy();//Получим ключевые поля по X
//        final Map<String,GrpDef> key2GrpDef = new HashMap<String,GrpDef>();
//        for (GrpDef grpDef : keyCols)
//            key2GrpDef.put(grpDef.getTid(),grpDef);

        List<ListGridField> ll= new LinkedList<ListGridField>();
        List<String> grpNames=new LinkedList<String>();
        //Ключевые поля
        for (int i = 0, keyColsLength = keyCols.length; i < keyColsLength; i++)
        {
            final GrpDef grpDef = keyCols[i];
            final String tid = grpDef.getTid();
            ColDef colDef = desc.getTupleDef().get(tid);
            ListGridField pdID = new ListGridField(tid, colDef.getTitle());
            pdID.setHidden(colDef.isHide());

            if (i<keyColsLength-1)
            {
                pdID.setGroupValueFunction(new GroupValueFunction() {
                    @Override
                    public Object getGroupValue(Object value, ListGridRecord record, ListGridField field, String fieldName, ListGrid grid) {
                        String colid = grpDef.gettColId();
                        if (!tid.equals(colid))
                            return record.getAttribute(colid);
                        return value;
                    }
                });
                grpNames.add(tid);
                ll.add(pdID);
            }
            else
            {
                String colid = grpDef.gettColId();
                if (!tid.equals(colid))
                {
                    colDef = desc.getTupleDef().get(colid);
                    pdID = new ListGridField(colid, colDef.getTitle());
                    //pdID.setSortDirection(SortDirection.ASCENDING); //TODO проверить чего-то не работает
                    pdID.setHidden(colDef.isHide());
                    ll.add(pdID);
                }
            }
        }
        grid.setCanMultiGroup(true);
        grid.setGroupByField(grpNames.toArray(new String[grpNames.size()]));
        grid.setGroupStartOpen(GroupStartOpen.NONE);

        //Консолидационные поля
        for (String re : res)
        {
            String colName=UtilsData.getHeader(re);
            ColDef colDef = desc.getTupleDef().get(colName);
            ListGridField pdID = new ListGridField(re,  colDef.getTitle());
            ll.add(pdID);
        }

        grid.setFields(ll.toArray(new ListGridField[ll.size()]));



        HeaderSpan span = new HeaderSpan();
        int headerHeight= AnalitGridUtils.buildSpans(span, root, "");

        HeaderSpan[] spans = span.getSpans();
        if (spans!=null && spans.length>0)
            grid.setHeaderSpans(spans);
        grid.setHeaderHeight(10+headerHeight);
    }

    void initTable(final ListGrid lg) throws CacheException
    {

        //Код получения xml описателя
        IAnalisysDesc desc = new AnalisysXML().initByXML(TXMLDesc.xml);//парсим его
        final GrpDef[] keyCols=desc.getGrpXHierarchy();//Получим ключевые поля по X
        final IKeyGenerator keyGenerator =new SimpleKeyGenerator(GrpDef.getTidsByGrpDef(keyCols) ,new INm2Ix()
        {
            @Override
            public Map<String, Integer> getColName2Ix()
            {
                Map<String, Integer> rv = new HashMap<String, Integer>();
                for (int i = 0; i < keyCols.length; i++) {
                    String keyCol = keyCols[i].getTid();
                    rv.put(keyCol,i);
                }
                return rv;
            }

            @Override
            public Map<Integer, String> getIx2ColName() {
                Map<Integer,String> rv = new HashMap<Integer,String>();
                for (int i = 0; i < keyCols.length; i++) {
                    String keyCol = keyCols[i].getTid();
                    rv.put(i,keyCol);
                }
                return rv;
            }
        });
        setHeadGrid(lg,desc);


        String dataUrl="consolidate.jsp";
        String dataId = dataUrl.replace(".", "_");
        dataId = dataId.replace("/", "$");

        DataSource dataSource = new DataSource(dataUrl);
        dataSource.setID(dataId);
        dataSource.setDataFormat(DSDataFormat.JSON);



            DSRequest request = new DSRequest();
            request.setShowPrompt(false);

            DataSource.get(dataId).fetchData
                    (
                            new Criteria(),
                            new DSCallback() {
                                @Override
                                public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest) {
                                    try {
//получение данных из запросы
                                        Record[] testData = dsResponse.getData();
                                        testData = testData[0].getAttributeAsRecordArray("tuples");
                                        //String[] keyCols = keyGenerator.getKeyCols();

                                        //Заполнение структуры для того что бы заполнить аналитический грид
                                        Map<Object, Map<String, Object>> tuplesInTable = new HashMap<Object, Map<String, Object>>();
                                        for (Record record : testData) {
                                            Map tuple = record.toMap();

                                            Object key = keyGenerator.getKeyByTuple(tuple);
                                            Map<String, Object> newTuple = tuplesInTable.get(key);
                                            if (newTuple == null)
                                                tuplesInTable.put(key, newTuple = new HashMap<String, Object>());

                                            UtilsData.conVertMapByNode("", root, tuple, newTuple);
                                            for ( GrpDef colName :keyCols )
                                            {
                                                String tid = colName.getTid();
                                                newTuple.put(tid, tuple.get(tid));
                                                if (!tid.equals(colName.gettColId()))
                                                    newTuple.put(colName.gettColId(), tuple.get(colName.gettColId()));
                                            }
                                        }

                                        //Установка грида
                                        List<Record> recordList = new LinkedList<Record>();
                                        for (Map<String, Object> tuple : tuplesInTable.values())
                                            recordList.add(new Record(tuple));
                                        lg.setData(recordList.toArray(new Record[recordList.size()]));
                                    } catch (CacheException cacheException) {
                                        cacheException.printStackTrace();
                                    }


                                }
                            },
                            request
                    );
    }

    @Override
    public void setComponents(Layout mainLayout)
    {
        final PortalLayout portalLayout = new PortalLayout(0);
        portalLayout.setWidth100();
        portalLayout.setHeight100();


        {
            Portlet portlet = new Portlet();
            ListGrid lg = new ListGrid();
            lg.setWidth100();
            lg.setHeight100();
            lg.setCanMultiGroup(true);

            try {
                initTable(lg);
            } catch (CacheException cacheException) {
                cacheException.printStackTrace();
            }

            portlet.addItem(lg);
            portalLayout.addPortlet(portlet);
        }

        mainLayout.addMembers(portalLayout);
    }
}