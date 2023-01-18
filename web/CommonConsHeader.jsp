<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.mwlib.tablo.EventUtils" %>
<%@ page import="com.mwlib.tablo.analit2.delay.DelayXML" %>
<%@ page import="com.mwlib.tablo.analit2.ref12.Ref12XML" %>
<%@ page import="com.mycompany.common.TablesTypes" %>
<%@ page import="com.mwlib.tablo.analit2.pred.NNodeXML" %>
<%@ page import="com.mwlib.tablo.analit2.places.PlacesXML" %>
<%@ page import="com.mwlib.tablo.analit2.warnv.WarnVXML" %>
<%@ page import="com.mwlib.tablo.analit2.winrep.WinRepXML" %>
<%@ page import="com.mwlib.tablo.analit2.winplan.WinPlanXML" %>
<%@ page import="com.mwlib.tablo.analit2.warnact.WarnACTXML" %>
<%@ page import="com.mwlib.tablo.analit2.oindex.OIndxXML" %>
<%@ page import="com.mwlib.tablo.analit2.warnagr.WarnAGRXML" %>
<%@ page import="com.mwlib.tablo.analit2.rsm.RSMXML" %>
<%@ page import="com.mwlib.tablo.analit2.loco.LOCXML" %>
<%--
  Created by IntelliJ IDEA.
  User: Vladislav.Mendelevic
  Date: 26.03.15
  Time: 18:39
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
    Map<String,String> tType2Header=new HashMap<String,String>();
    public void jspInit()
    {
        try
        {
            tType2Header.put(TablesTypes.STATEWARNV,EventUtils.toJson(WarnVXML.testHeaders()));
            tType2Header.put(TablesTypes.STATEWARNACT,EventUtils.toJson(WarnACTXML.testHeaders()));
            tType2Header.put(TablesTypes.STATEREF12,EventUtils.toJson(Ref12XML.testHeaders()));
            tType2Header.put(TablesTypes.STATEPLACES,EventUtils.toJson(PlacesXML.testHeaders()));
            tType2Header.put(TablesTypes.STATEDELAY, EventUtils.toJson(DelayXML.testHeaders()));
            tType2Header.put(TablesTypes.STATEDESC,EventUtils.toJson(NNodeXML.testHeaders()));
            tType2Header.put(TablesTypes.WINREP,EventUtils.toJson(WinRepXML.testHeaders()));
            tType2Header.put(TablesTypes.WINPLAN,EventUtils.toJson(WinPlanXML.testHeaders()));
            tType2Header.put(TablesTypes.OINDX,EventUtils.toJson(OIndxXML.testHeaders()));
            tType2Header.put(TablesTypes.STATEWARNAGR,EventUtils.toJson(WarnAGRXML.testHeaders()));
            tType2Header.put(TablesTypes.RSM_DATA,EventUtils.toJson(RSMXML.testHeaders()));
            tType2Header.put(TablesTypes.LOC_DATA,EventUtils.toJson(LOCXML.testHeaders()));

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
%>
<%
    Map map = request.getParameterMap();

    String[] tTypes = (String[]) map.get(TablesTypes.TTYPE);
    if (tTypes!=null && tTypes.length>0) {
        final String json = tType2Header.get(tTypes[0]);
        if (json!=null)
        {
            out.print(json);
            out.flush();
        }
        else
            throw new Exception("Can't find headers for table type:" + tTypes[0]);
    }
    else
        throw new Exception("No table type parameter, parameter with "+TablesTypes.TTYPE+" name is null or empty");

%>