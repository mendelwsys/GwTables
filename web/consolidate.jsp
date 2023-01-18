<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.mwlib.tablo.test.derby.CheckBusinessReq" %>
<%@ page import="com.mwlib.tablo.EventUtils" %>
<%@ page import="com.mwlib.tablo.tables.DataSender4" %>
<%--
  Created by IntelliJ IDEA.
  User: Vladislav.Mendelevic
  Date: 21.10.14
  Time: 18:39
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Map map = request.getParameterMap();
    map = new HashMap(map);

    Map[] chs=CheckBusinessReq.getTestData();

    DataSender4[] sender=new DataSender4[]{new DataSender4(null,null,null)};
    sender[0].setTuples(chs);
    out.print(EventUtils.toJson(sender));

%>