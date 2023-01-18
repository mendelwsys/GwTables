<%@ page import="java.util.Map" %>
<%@ page import="com.mwlib.tablo.tables.*" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="com.mycompany.common.TablesTypes" %>
<%@ page import="com.mycompany.common.DiagramDesc" %>
<%@ page import="com.mwlib.tablo.test.tables.TableSwitcher" %>
<%@ page import="com.mwlib.tablo.test.tables.BaseTable" %>
<%--
  Created by IntelliJ IDEA.
  User: User
  Date: 15.05.14
  Time: 16:04
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="application/json;charset=UTF-8" language="java" %>
<%


//    Set keys = map.keySet();
//    if (keys!=null)
//    {
//        System.out.println("keys = " + keys.size());
//        Iterator it = keys.iterator();
//        while (it.hasNext())
//            System.out.println("key = " + it.next());
//    }
//    else
//        System.out.println("keys = " + keys);
/*
    TupleBean[] tuples =new TupleBean[2];
    tuples[0]=new TupleBean(TupleBean.ix++,1,"Col"+TupleBean.ix,"col"+TupleBean.ix);
    tuples[1]=new TupleBean(TupleBean.ix++,1,"Col"+TupleBean.ix,"col"+TupleBean.ix);
//    tuples[2]=new TupleBean(String.valueOf(TupleBean.ix++),"Column"+TupleBean.ix,"col"+TupleBean.ix);

    DataSender[] sender=new DataSender[]{new DataSender()};
    sender[0].setUpdateStamp(10);
    sender[0].setTuples(tuples);
*/
//    DataSupply dsupply = DataSupply.getInstance("warn");
//    Map[] chs = dsupply.getData();

    Map map = request.getParameterMap();
    map = new HashMap(map);

    final BaseTable inst = TableSwitcher.getInstance(map, TableSwitcher.isTest());
    final HashMap<String, Object> outParams = new HashMap<String, Object>();
    final Map[] chs = inst.getData(map, outParams);

    TransactSender trans = new TransactSender(0, null,0);
    DataSender4[] sender=new DataSender4[]{new DataSender4(null, trans,null)};

    trans.setUpdateStamp((Long)outParams.get(TablesTypes.ID_TM));
    trans.setUpdateStampN((Integer)outParams.get(TablesTypes.ID_TN));
    sender[0].setDesc((DiagramDesc)outParams.get(TablesTypes.DIAGRAM_DESC));

    sender[0].setTuples(chs);
    out.print(BaseTable.toJson(sender));

//    String ln=new BufferedReader(new FileReader("C:\\data.json")).readLine();
//    out.print(ln);

%>