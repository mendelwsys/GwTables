<%@ page import="com.google.gson.Gson" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="com.mwlib.tablo.test.tables.TableSwitcher" %>
<%@ page import="com.mwlib.tablo.test.tables.BaseTable" %>
<%@ page import="com.mycompany.common.tables.ColumnHeadBean" %>
<%@ page import="com.mycompany.common.tables.GridOptionsSender" %>
<%--
  Created by IntelliJ IDEA.
  User: User
  Date: 15.05.14
  Time: 16:04
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="application/json;charset=UTF-8" language="java" %>
<%
/*
    ColumnHeadBean[] chs=new ColumnHeadBean[3];

    chs[0]=new ColumnHeadBean("id","id", ListGridFieldType.INTEGER.toString());
    chs[1]=new ColumnHeadBean("Колонка 1","col1", ListGridFieldType.TEXT.toString());
    chs[2]=new ColumnHeadBean("Колонка 2","col2", ListGridFieldType.TEXT.toString());
*/

//    DataSupply dsupply = DataSupply.getInstance("warn");
//    ColumnHeadBean[] chs = dsupply.getHeaders();

    final BaseTable inst = TableSwitcher.getInstance(new HashMap(request.getParameterMap()), TableSwitcher.isTest());
    ColumnHeadBean[] chs =inst.getMeta();

    GridOptionsSender[] gridOptionses = new GridOptionsSender[]{new GridOptionsSender()};
    gridOptionses[0].setChs(chs);
    gridOptionses[0].setCellHeight(inst.getCellHeight());
    gridOptionses[0].setHeaderHeight(inst.getHeaderHeight());
    gridOptionses[0].setFixedRecordHeights(inst.isFixedRecordHeights());

    out.print(BaseTable.toJson(gridOptionses));
//    String gridOptionses="[{\"title\":\"PRICH_V_ID\",\"name\":\"PRICH_V_ID\",\"type\":\"INTEGER\"},{\"title\":\"VEL\",\"name\":\"VEL\",\"type\":\"INTEGER\"},{\"title\":\"N_PRK_NOM\",\"name\":\"N_PRK_NOM\",\"type\":\"TEXT\"},{\"title\":\"STAN1_NAME\",\"name\":\"STAN1_NAME\",\"type\":\"TEXT\"},{\"title\":\"id\",\"name\":\"id\",\"type\":\"TEXT\"},{\"title\":\"VGRPOR\",\"name\":\"VGRPOR\",\"type\":\"INTEGER\"},{\"title\":\"PUT_TXT\",\"name\":\"PUT_TXT\",\"type\":\"TEXT\"},{\"title\":\"V\",\"name\":\"V\",\"type\":\"INTEGER\"},{\"title\":\"TLG_NUM\",\"name\":\"TLG_NUM\",\"type\":\"TEXT\"},{\"title\":\"DOR_KOD\",\"name\":\"DOR_KOD\",\"type\":\"INTEGER\"},{\"title\":\"actual\",\"name\":\"actual\",\"type\":\"INTEGER\"},{\"title\":\"VPAS\",\"name\":\"VPAS\",\"type\":\"INTEGER\"},{\"title\":\"STAN2_NAME\",\"name\":\"STAN2_NAME\",\"type\":\"TEXT\"},{\"title\":\"o_text\",\"name\":\"o_text\",\"type\":\"TEXT\"},{\"title\":\"N_PRK_DATE\",\"name\":\"N_PRK_DATE\",\"type\":\"INTEGER\"},{\"title\":\"VGR\",\"name\":\"VGR\",\"type\":\"INTEGER\"}]";
//    out.print(gridOptionses);
%>