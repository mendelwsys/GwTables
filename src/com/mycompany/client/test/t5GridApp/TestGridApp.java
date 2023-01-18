package com.mycompany.client.test.t5GridApp;

import com.mycompany.client.test.TestBuilder;
import com.mycompany.common.analit.NNode;
import com.mycompany.common.cache.CacheException;
import com.mycompany.common.cache.IKeyGenerator;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.grid.HeaderSpan;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.PortalLayout;
import com.smartgwt.client.widgets.layout.Portlet;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 18.10.14
 * Time: 10:37
 * Реализация саморазвертывающегося грида по дата сорсу.

 */
public class TestGridApp implements TestBuilder
{


    NNode root = new NNode("");
    Set<String> res;
    {
        TestData_bu.addAllToNode(root, 0);
        res=TestData_bu.getFieldNamesByNode(root);
    }

    int buildSpans(HeaderSpan span,NNode root,String fname)
    {
        int rv=0;
        List<String> fields= null;
        List<HeaderSpan> headerSpans = null;

        for (NNode node : root.nodes.values())
        {

             if (node.nodes.size()==0)
             {
                 if (fields==null)
                     fields=new LinkedList<String>();
                 fields.add(fname+"#"+node.name);
             }
             else
             {
                 if (headerSpans==null)
                     headerSpans=new LinkedList<HeaderSpan>();

                 HeaderSpan span1 = new HeaderSpan();
                 int lrv=buildSpans(span1,node,fname+"#"+node.name);
                 if (rv<lrv)
                     rv=lrv;
                 headerSpans.add(span1);
             }
        }

        span.setTitle(root.header);
        if (fields!=null)
            span.setFields(fields.toArray(new String[fields.size()]));
        else //if (headerSpans!=null)
            span.setSpans(headerSpans.toArray(new HeaderSpan[headerSpans.size()]));

        return rv+10;
    }


    void setHeaders(ListGrid grid) throws CacheException
    {

        List<ListGridField> ll= new LinkedList<ListGridField>();
        IKeyGenerator keyGenerator = TestData_bu.getKeyGenrator();
        String[] colsn=keyGenerator.getKeyCols();
        for (String colName : colsn)
        {
            ListGridField pdID = new ListGridField(colName,colName);
            ll.add(pdID);
        }

        for (String re : res)
        {
            ListGridField pdID = new ListGridField(re, TestData_bu.getHeader(re));
            ll.add(pdID);
        }


        grid.setFields(ll.toArray(new ListGridField[ll.size()]));

        HeaderSpan span = new HeaderSpan();
        int headerHeight=buildSpans(span,root,"");

        HeaderSpan[] spans = span.getSpans();
        if (spans!=null && spans.length>0)
        {
            grid.setHeaderSpans(spans);

            spans[0].getSpans();
        }
        grid.setHeaderHeight(headerHeight);


        Map[] testData = TestData_bu.getTestDatas();
        Map<Object,Map<String,Object>> tuplesInTable=new HashMap<Object,Map<String,Object>>();
        for (Map tuple : testData)
        {
            Object key=keyGenerator.getKeyByTuple(tuple);
            Map<String, Object> newTuple=tuplesInTable.get(key);
            if (newTuple==null)
                tuplesInTable.put(key,newTuple = new HashMap<String,Object>());

            TestData_bu.conVertMapByNode("", root, tuple, newTuple);
            for (String colName : colsn)
                newTuple.put(colName,tuple.get(colName));
        }

        List<Record> recordList = new LinkedList<Record>();
        for (Map<String, Object> tuple : tuplesInTable.values())
        {
            recordList.add(new Record(tuple));
        }
        grid.setData(recordList.toArray(new Record[recordList.size()]));
    }


    void setHeaders_S(ListGrid grid)
    {

        List<ListGridField> ll= new LinkedList<ListGridField>();
//        for (String re : res)
//        {
//            ListGridField pdID = new ListGridField(re, TestData.getHeader(re));
//            ll.add(pdID);
//        }
//        grid.setFields(ll.toArray(new ListGridField[ll.size()]));


       HeaderSpan france = new HeaderSpan("France", new String[] {"Paris", "Lyon"});
       HeaderSpan uk = new HeaderSpan("UK", new String[] {"London", "Glasgow"});
       HeaderSpan spain = new HeaderSpan("Spain", new String[] {"Barcelona"});

       HeaderSpan europe = new HeaderSpan();
       europe.setTitle("Europe");
       europe.setSpans(france, uk,spain);



        ListGridField f1 = new ListGridField("Paris", "Paris");
        ll.add(f1);
        ListGridField f2 = new ListGridField("Lyon", "Lyon");
        ll.add(f2);
        ListGridField f3 = new ListGridField("London", "London");
        ll.add(f3);
        ListGridField f4 = new ListGridField("Glasgow", "Glasgow");
        ll.add(f4);

        ListGridField f5 = new ListGridField("Barcelona", "Barcelona");
        ll.add(f5);
        ListGridField f6 = new ListGridField("countryCode","countryCode");
        ll.add(f6);
        ListGridField f7 = new ListGridField("countryName", "countryName");
        ll.add(f7);

        ListGridField f8 = new ListGridField("population", "population");
        ll.add(f8);
        ListGridField f9 = new ListGridField("area","area");
        ll.add(f9);
        ListGridField f10 = new ListGridField("gdp", "gdp");
        ll.add(f10);


        Map<String,Object> mp=new HashMap<String,Object>();
       grid.setFields(ll.toArray(new ListGridField[ll.size()]));
        for (int i = 0; i < ll.size(); i++)
        {
            ListGridField listGridField = ll.get(i);
            mp.put(listGridField.getName(),String.valueOf(i));
        }

        grid.setData(new Record[]{new Record(mp)});

       grid.setHeaderSpans
               (
               new HeaderSpan("Identification", new String[]{"countryCode", "countryName"}),
               europe
                       ,
               new HeaderSpan("Demographics", new String[]{"population", "area", "gdp"})
               );

    }



//    void setHeaders_S(ListGrid grid)
//    {
//
//        ListGridField pdID = new ListGridField("PRED_ID", "Предприятие");
//        ListGridField pdName = new ListGridField("PRED_NAME", "Наименование");
//
//
//
//        ListGridField I_WIND_CNT = new ListGridField("I#WIND##CNT", "Кол-во"); //Заметим что поле состоит из вариантов// данных и заканичается только заголовком
//        ListGridField I_WIND_LN = new ListGridField("I#WIND##LN", "Длинна");
//        ListGridField I_WIND_ADL = new ListGridField("I#WIND##ADL", "Длительность");
//        ListGridField I_WR_CNT = new ListGridField("I#WR##CNT", "Кол-во");
//        ListGridField I_WR_LN = new ListGridField("I#WR##LN", "Длинна");
//
//
//        ListGridField II_WIND_CNT = new ListGridField("II#WIND##CNT", "Кол-во"); //Заметим что поле состоит из вариантов// данных и заканичается только заголовком
//        ListGridField II_WIND_LN = new ListGridField("II#WIND##LN", "Длинна");
//        ListGridField II_WIND_ADL = new ListGridField("II#WIND##ADL", "Длительность");
//        ListGridField II_WR_CNT = new ListGridField("II#WR##CNT", "Кол-во");
//        ListGridField II_WR_LN = new ListGridField("II#WR##LN", "Длинна");
//
//
//        grid.setFields();
//
///*
//       countryGrid.setHeaderSpans(
//               new HeaderSpan("Identification", new String[]{"countryCode", "countryName"}),
//               new HeaderSpan("Government & Politics", new String[]{"capital", "government", "independence"}),
//               new HeaderSpan("Demographics", new String[]{"population", "area", "gdp"}));
//
//*/
//
//    }






    @Override
    public void setComponents(Layout mainLayout)
    {

        //TXML.test();
        final PortalLayout portalLayout = new PortalLayout(0);
        portalLayout.setWidth100();
        portalLayout.setHeight100();

        {
            Portlet portlet = new Portlet();
            ListGrid lg = new ListGrid();
            lg.setWidth100();
            lg.setHeight100();
            try {
                setHeaders(lg);
            } catch (CacheException cacheException) {
                cacheException.printStackTrace();
            }
            lg.setHeaderHeight(50);


            portlet.addItem(lg);
            portalLayout.addPortlet(portlet);
        }

        {

            Portlet portlet = new Portlet();
            ListGrid lg = new ListGrid();
            lg.setWidth100();
            lg.setHeight100();
            lg.setCanMultiGroup(true);


            Map<String,Object>[] datas = TestData_bu.getTestDatas();


            if (datas.length>0)
            {
                Map<String,ListGridField> key2lg= new HashMap<String,ListGridField> ();
                for (int i = 0, datasLength = datas.length; i < datasLength; i++)
                    for (String key : datas[i].keySet())
                        key2lg.put(key,new ListGridField(key, key));

                lg.setFields(key2lg.values().toArray(new ListGridField[key2lg.size()]));

                Record[] recs=new Record[datas.length];
                for (int i = 0, datasLength = datas.length; i < datasLength; i++)
                    recs[i] = new Record(datas[i]);
                lg.setData(recs);
                portlet.addItem(lg);
                portalLayout.addPortlet(portlet);
            }

        }
        mainLayout.addMembers(portalLayout);
    }
}
