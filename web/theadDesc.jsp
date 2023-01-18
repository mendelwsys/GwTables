<%@ page import="com.mwlib.tablo.db.TableDescSwitcher" %>
<%@ page import="com.mwlib.tablo.db.BaseTableDesc" %>
<%@ page import="com.mwlib.tablo.EventUtils" %>
<%@ page import="com.mycompany.common.tables.GridOptionsSender" %>
<%@ page import="com.mycompany.common.tables.ColumnHeadBean" %>
<%--
  Created by IntelliJ IDEA.
  User: User
  Date: 15.05.14
  Time: 16:04
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="application/json;charset=UTF-8" language="java" %>
<%
//    Thread.sleep(5000); //Проверка того что данные в таблицу будут подкачиваться после того как таблица получит метаинформацию
    final BaseTableDesc[] insts = TableDescSwitcher.getInstance().getDescInstance(request.getParameterMap());
    GridOptionsSender[] gridOptionses = new GridOptionsSender[insts.length];

    for (int i = 0, instsLength = insts.length; i < instsLength; i++)
    {

        ColumnHeadBean[] chs = insts[i].getMeta();
        gridOptionses[i]=new GridOptionsSender();
        gridOptionses[i].setChs(chs);
        gridOptionses[i].setCellHeight(insts[i].getCellHeight());
        gridOptionses[i].setHeaderHeight(insts[i].getHeaderHeight());
        gridOptionses[i].setFixedRecordHeights(insts[i].isFixedRecordHeights());
        gridOptionses[i].setTableType(insts[i].getTableType());
    }

    out.print(EventUtils.toJson(gridOptionses));
    out.flush();
//    String gridOptionses="[{\"title\":\"PRICH_V_ID\",\"name\":\"PRICH_V_ID\",\"type\":\"INTEGER\"},{\"title\":\"VEL\",\"name\":\"VEL\",\"type\":\"INTEGER\"},{\"title\":\"N_PRK_NOM\",\"name\":\"N_PRK_NOM\",\"type\":\"TEXT\"},{\"title\":\"STAN1_NAME\",\"name\":\"STAN1_NAME\",\"type\":\"TEXT\"},{\"title\":\"id\",\"name\":\"id\",\"type\":\"TEXT\"},{\"title\":\"VGRPOR\",\"name\":\"VGRPOR\",\"type\":\"INTEGER\"},{\"title\":\"PUT_TXT\",\"name\":\"PUT_TXT\",\"type\":\"TEXT\"},{\"title\":\"V\",\"name\":\"V\",\"type\":\"INTEGER\"},{\"title\":\"TLG_NUM\",\"name\":\"TLG_NUM\",\"type\":\"TEXT\"},{\"title\":\"DOR_KOD\",\"name\":\"DOR_KOD\",\"type\":\"INTEGER\"},{\"title\":\"actual\",\"name\":\"actual\",\"type\":\"INTEGER\"},{\"title\":\"VPAS\",\"name\":\"VPAS\",\"type\":\"INTEGER\"},{\"title\":\"STAN2_NAME\",\"name\":\"STAN2_NAME\",\"type\":\"TEXT\"},{\"title\":\"o_text\",\"name\":\"o_text\",\"type\":\"TEXT\"},{\"title\":\"N_PRK_DATE\",\"name\":\"N_PRK_DATE\",\"type\":\"INTEGER\"},{\"title\":\"VGR\",\"name\":\"VGR\",\"type\":\"INTEGER\"}]";
//    out.print(gridOptionses);
%>