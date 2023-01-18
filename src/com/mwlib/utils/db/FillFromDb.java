package com.mwlib.utils.db;


import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.tables.ColumnHeadBean;
import com.smartgwt.client.types.ListGridFieldType;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.sql.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 22.05.14
 * Time: 13:05
 * To change this template use File | Settings | File Templates.
 */
public class FillFromDb
{
/*
TODO (Подкачка данных)
1. Определиться как получать максимальноe cortime (Достаточно просто, анализируя во время передачи кортежей)
2. Передавать в качестве параметра long, который будет поднимать по кортайму последние изменения

TODO (Как бы выделять полученые данные на клиенте, т.е. нужно какое-то дизайнеровское решение по получению данных)
???Первое что можно сделать на верху показвать только обновлненные события например???
 */
    final static String GET_W_EVENT ="select toj1.data_obj_id,toj1.cor_time,da.attr_id,al.attr_type," +
        "da.value_s,da.value_i,da.value_f,da.value_t,da.cor_time as acor_time " +
        " ,toj1.text" +
//        ", " +
//        "toj1.comment "+
        " from "+
        "("+
        "  select toj.data_obj_id,toj.cor_time" +
        "," +
        " toj.text " +
        "," +
        " toj.datatype_id" +
//        ", " +
//        " toj.comment" +
        " from tablo_data_objects toj,tablo_data_attributes da"+
        "    where toj.datatype_id =? and toj.dor_kod=? "+
        "      and toj.cor_tip IN ('I','U')\n"+
        "      and toj.data_obj_id=da.data_obj_id " +
        "      and da.attr_id='TIM_OTM'"+
        "      and (da.value_t like '31.12.99%' or da.value_t>= ? )"+
        "      and toj.cor_time> ? "+
        ") toj1, tablo_data_attributes da ,tablo_data_attr_list al "+
        " where toj1.data_obj_id=da.data_obj_id " +
        " and toj1.datatype_id=da.datatype_id " +
        " and al.attr_id=da.attr_id " +
        " and al.DATATYPE_ID=da.datatype_id "+
        " order by  toj1.cor_time";



    public static Object getAttrValByAttrType(ResultSet rs, String attr_type) throws SQLException
    {
        if ("STRING".equalsIgnoreCase(attr_type))
            return rs.getString("VALUE_S");
        else if ("INTEGER".equalsIgnoreCase(attr_type))
            return rs.getInt("VALUE_I");
        else if ("FLOAT".equalsIgnoreCase(attr_type))
            return rs.getDouble("VALUE_F");
        else if ("TIMESTAMP".equalsIgnoreCase(attr_type))
            return rs.getTimestamp("VALUE_T");
        throw new SQLException("ERROR BROIDE TYPE");
    }

    public static String translate2AttrType(String attr_type) throws SQLException
    {
        if ("STRING".equalsIgnoreCase(attr_type))
            return ListGridFieldType.TEXT.toString();
        else if ("INTEGER".equalsIgnoreCase(attr_type))
            return ListGridFieldType.INTEGER.toString();
        else if ("FLOAT".equalsIgnoreCase(attr_type))
            return ListGridFieldType.FLOAT.toString();
        else if ("TIMESTAMP".equalsIgnoreCase(attr_type))
            return ListGridFieldType.DATETIME.toString();

        throw new SQLException("ERROR BROIDE TYPE:"+attr_type);
    }


    public static String DOR_COD_ATTR="DOR_COD";
    public static String EV_TYPE_ATTR="EV_TYPE";


    public static void main(String[] args) throws Exception
    {
        Pair<ColumnHeadBean[], Map[]> pr = _getDbTable(null, null);


        FileOutputStream fos = new FileOutputStream("C:\\PapaWK\\Projects\\JavaProj\\GWTProject\\warn.db");
        ObjectOutputStream obs = new ObjectOutputStream(fos);
        obs.writeObject(pr.first);
        obs.writeObject(pr.second);
        obs.close();

//        WarningsT wrt = new WarningsT();
//        ColumnHeadBean[] meta = wrt.getMeta();
//        Map[] res = wrt.getData();
//        System.out.println();


//        Gson gson = new Gson();
//        String res=gson.toJson(pr.first);
//        PrintStream printStream = new PrintStream(new FileOutputStream("C:\\header.json"));
//        printStream.println(res);
//        printStream.close();
//
//        DataSender2 ds = new DataSender2();
//        ds.setUpdateStamp(0);
//        ds.setTuples(pr.second);
//
//        printStream = new PrintStream(new FileOutputStream("C:\\data.json"));
//        printStream.println(gson.toJson(ds));
//        printStream.close();



    }


    public static class ParamVal
    {
        int index;
        Object val;
        int sqlType;

        public ParamVal(int index, Object val, int sqlType) {
            this.index = index;
            this.val = val;
            this.sqlType = sqlType;
        }

        public int getIndex() {
            return index;
        }

        public Object getVal() {
            return val;
        }

        public int getSqlType() {
            return sqlType;
        }
    }

    public static interface IRowOperation
    {
       void setObjectArc(Map<String,ColumnHeadBean> attrs,Timestamp cor_time,ResultSet rs,Map<String, Pair<Timestamp, Object>> obj) throws Exception;
    }

    /*
        Здесь каждый аттрибут каждого объекта по мере прохождения выборки заменяется более новым аттрибутом
        (с более новой временной меткой)
        В результате у нас объект с идентикатором имеет самые новые аттрибуты которые есть.
    */

    public static Pair< ColumnHeadBean[],Map[] > _getDbTable(String ReqSQL,Map<String,ParamVal> mapParams, Map<String, Object> outParams,IRowOperation rowOperation) throws Exception
    {

        Map<String,Map<String,Pair<Timestamp,Object> >> id2attrId2Time_Val = new HashMap<String,Map<String,Pair<Timestamp,Object> >>();
        Map<String,Map<String,Object> > id2attrId2val = new HashMap<String,Map<String,Object>>();
        Map<String,ColumnHeadBean> attrDesc = new HashMap<String, ColumnHeadBean>();

        Connection conn = null;
        PreparedStatement cs = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getOracleJdbcConnection();
            Timestamp maxTimestamp=(Timestamp)mapParams.get(TablesTypes.MAX_TIMESTAMP).getVal();

            cs = conn.prepareStatement(ReqSQL);

            for (ParamVal mapParam : mapParams.values())
                if (mapParam.getIndex()>0)
                    cs.setObject(mapParam.getIndex(),mapParam.getVal(),mapParam.getSqlType());

            rs = cs.executeQuery();
            while(rs.next())
            {

                String id=rs.getString(1); //TODO Индекс идентификатора должен быть первым
                Timestamp cor_time = rs.getTimestamp(2);//TODO Индекс времени должен быть второй

                if (maxTimestamp.before(cor_time))
                    maxTimestamp=cor_time;

                Map<String, Pair<Timestamp,Object>> attrId2Time_Val = id2attrId2Time_Val.get(id);
                Map<String, Object> attrId2Val=id2attrId2val.get(id);
                if (attrId2Time_Val==null)
                {
                    id2attrId2Time_Val.put(id, attrId2Time_Val = new HashMap<String, Pair<Timestamp, Object>>());
                    setObjectArc(attrDesc,cor_time, rs, attrId2Time_Val);
                    if (rowOperation!=null) rowOperation.setObjectArc(attrDesc,cor_time, rs, attrId2Time_Val);
                    id2attrId2val.put(id, attrId2Val = new HashMap<String, Object>());
                    for (String s : attrId2Time_Val.keySet())
                        attrId2Val.put(s,attrId2Time_Val.get(s).second);
                }
                else
                {
                    Timestamp l_cor_time=attrId2Time_Val.get("o_cor_time").first;
                    if (l_cor_time.before(cor_time))
                    {
                        setObjectArc(attrDesc,cor_time, rs, attrId2Time_Val);
                        if (rowOperation!=null) rowOperation.setObjectArc(attrDesc,cor_time, rs, attrId2Time_Val);
                        for (String s : attrId2Time_Val.keySet())
                            attrId2Val.put(s,attrId2Time_Val.get(s).second);
                    }
                }



                String attr_id=rs.getString(3);
                String attr_type=rs.getString(4);

                if (!attrDesc.containsKey(attr_id))
                    attrDesc.put(attr_id, new ColumnHeadBean(attr_id, attr_id, translate2AttrType(attr_type)));


                Object att_val= getAttrValByAttrType(rs, attr_type);

                if (att_val instanceof String && "NULL".equalsIgnoreCase((String)att_val))
                    att_val=null;


                Pair<Timestamp,Object> olst=attrId2Time_Val.get(attr_id);
                Timestamp acor_time = rs.getTimestamp("acor_time");
                if (olst==null)
                {
                    attrId2Time_Val.put(attr_id, new Pair<Timestamp, Object>(acor_time, att_val));
                    attrId2Val.put(attr_id, att_val);
                }
                else if (acor_time.after(olst.first))
                {
                    olst.setValue(att_val);
                    attrId2Val.put(attr_id, att_val);
                }
            } //while rs.next()


            Set<String> keys = id2attrId2val.keySet();
            for (String key : keys)
            {
                Map<String, Object> stringObjectMap = id2attrId2val.get(key);
                stringObjectMap.put(TablesTypes.KEY_FNAME, key);
                stringObjectMap.put(TablesTypes.ACTUAL,1);

                attrDesc.put(TablesTypes.KEY_FNAME, new ColumnHeadBean(TablesTypes.KEY_FNAME, TablesTypes.KEY_FNAME, ListGridFieldType.TEXT.toString()));
                attrDesc.put(TablesTypes.ACTUAL, new ColumnHeadBean(TablesTypes.ACTUAL, TablesTypes.ACTUAL, ListGridFieldType.INTEGER.toString()));
            }
            Collection<Map<String, Object>> values = id2attrId2val.values();
            Map[] arr = values.toArray(new Map[values.size()]);


            if (outParams!=null)
            {
                outParams.put(TablesTypes.ID_TM,maxTimestamp.getTime());
                outParams.put(TablesTypes.ID_TN,maxTimestamp.getNanos());
            }

            return new Pair<ColumnHeadBean[], Map[]>(attrDesc.values().toArray(new ColumnHeadBean[attrDesc.size()]),arr);
        }
        finally
        {
            DbUtil.closeAll(rs, cs, conn, false);
        }

    }



    public static Pair< ColumnHeadBean[],Map[] > _getDbTable(Map mapParams, Map<String, Object> outParams) throws Exception
    {


        Map<String,Map<String,Pair<Timestamp,Object> >> id2attr_id2val = new HashMap<String,Map<String,Pair<Timestamp,Object> >>();
        Map<String,Map<String,Object> > id2attr_id2val_2 = new HashMap<String,Map<String,Object>>();
        Map<String,ColumnHeadBean> attrs = new HashMap<String, ColumnHeadBean>();

        Connection conn = null;
        PreparedStatement cs = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getOracleJdbcConnection();


            Integer[] dor_code=(Integer[])mapParams.get(DOR_COD_ATTR);
            Integer[] ev_code=(Integer[])mapParams.get(EV_TYPE_ATTR);


            Calendar cl1 = Calendar.getInstance();
            Timestamp maxTimestamp = getMaxTimeStamp(mapParams);

            if (dor_code.length==1 && ev_code.length==1)
            {
                cs = conn.prepareStatement(GET_W_EVENT);

                cs.setInt(1, ev_code[0]);//Warnings
                cs.setInt(2, dor_code[0]); //DOR_COD Sys
                cs.setTimestamp(3, new Timestamp(cl1.getTimeInMillis()));
                cs.setTimestamp(4, maxTimestamp);
            }
            else if (dor_code.length!=0 && ev_code.length!=0)
            {

                String resSql=GET_W_EVENT;
                {
                    String reqString="toj.dor_kod=?";
                    String replaceString;
                    if (dor_code.length>1)
                    {
                        replaceString="toj.dor_kod IN (";
                        replaceString = getSets(dor_code, replaceString);
                        replaceString +=")";
                    }
                    else
                        replaceString="toj.dor_kod = "+dor_code[0];
                    resSql=resSql.replace(reqString,replaceString);
                }

                {
                    String reqString="datatype_id =?";
                    String replaceString;
                    if (ev_code.length>1)
                    {
                        replaceString="datatype_id IN (";
                        replaceString = getSets(ev_code, replaceString);
                        replaceString +=")";
                    }
                    else
                        replaceString="datatype_id = "+ev_code[0];
                    resSql=resSql.replace(reqString,replaceString);
                }

                cs = conn.prepareStatement(resSql);
                cs.setTimestamp(1, new Timestamp(cl1.getTimeInMillis()));
                cs.setTimestamp(2, maxTimestamp);
            }




            rs = cs.executeQuery();
            while(rs.next())
            {

                String id=rs.getString(1);

                Timestamp cor_time = rs.getTimestamp(2);

                if (maxTimestamp.before(cor_time))
                    maxTimestamp=cor_time;

                Map<String, Pair<Timestamp,Object>> obj = id2attr_id2val.get(id);
                Map<String, Object> obj2=id2attr_id2val_2.get(id);
                if (obj==null)
                {
                    id2attr_id2val.put(id,obj=new HashMap<String, Pair<Timestamp,Object>>());
                    setObjectArc(attrs,cor_time, rs, obj);
                    id2attr_id2val_2.put(id,obj2=new HashMap<String, Object>());
                    for (String s : obj.keySet())
                        obj2.put(s,obj.get(s).second);
                }
                else
                {
                    Timestamp l_cor_time=obj.get("o_cor_time").first;
                    if (l_cor_time.before(cor_time))
                    {
                        setObjectArc(attrs,cor_time, rs, obj);
                        for (String s : obj.keySet())
                            obj2.put(s,obj.get(s).second);
                    }
                }



                String attr_id=rs.getString(3);
                String attr_type=rs.getString(4);

                if (!attrs.containsKey(attr_id))
                    attrs.put(attr_id, new ColumnHeadBean(attr_id, attr_id, translate2AttrType(attr_type)));


                Object att_val= getAttrValByAttrType(rs, attr_type);

                if (att_val instanceof String && "NULL".equalsIgnoreCase((String)att_val))
                    att_val=null;


                Pair<Timestamp,Object> olst=obj.get(attr_id);
                Timestamp acor_time = rs.getTimestamp("acor_time");
                if (olst==null)
                {
                    obj.put(attr_id,new Pair<Timestamp,Object>(acor_time,att_val));
                    obj2.put(attr_id,att_val);
                }
                else if (acor_time.after(olst.first))
                {
                    olst.setValue(att_val);
                    obj2.put(attr_id,att_val);
                }
            }


            Set<String> ks = id2attr_id2val_2.keySet();
            for (String k : ks)
            {
                Map<String, Object> stringObjectMap = id2attr_id2val_2.get(k);
                stringObjectMap.put(TablesTypes.KEY_FNAME, k);
                stringObjectMap.put(TablesTypes.ACTUAL,1);

                attrs.put(TablesTypes.KEY_FNAME,new ColumnHeadBean(TablesTypes.KEY_FNAME,TablesTypes.KEY_FNAME,ListGridFieldType.TEXT.toString()));
                attrs.put(TablesTypes.ACTUAL,new ColumnHeadBean(TablesTypes.ACTUAL,TablesTypes.ACTUAL,ListGridFieldType.INTEGER.toString()));
            }
            Collection<Map<String, Object>> values = id2attr_id2val_2.values();
            Map[] arr = values.toArray(new Map[values.size()]);


            if (outParams!=null)
            {
                outParams.put(TablesTypes.ID_TM,maxTimestamp.getTime());
                outParams.put(TablesTypes.ID_TN,maxTimestamp.getNanos());
            }

//            int i=-1;
//            for (Map map : arr) {
//                if (i ==-1)
//                    i = map.size();
//                if (i!=map.size())
//                    System.out.println("wrong map = " + map);
//            }

            return new Pair<ColumnHeadBean[], Map[]>(attrs.values().toArray(new ColumnHeadBean[attrs.size()]),arr);

//            String recs = new Gson().toJson(arr);
//            System.out.println("recs = " + recs);
//            return recs;



//            Set<String> ks = id2attr_id2val_2.keySet();
//            for (String key : ks) {
//                Map<String,Object>  oo=id2attr_id2val_2.get(key);
//
//                Gson gson = new Gson();
//                String json = gson.toJson(oo);
//                System.out.println("json = " + json);
//
//                Type type = new TypeToken<Map<String, String>>(){}.getType();
//                Map<String, String> map = gson.fromJson(json, type);
//                for (String key1 : map.keySet()) {
//                    System.out.println("map.get = " + map.get(key1));
//                    }
//            }

//            System.out.println("i = " + i);

//            ResultSetMetaData metaData = rs.getFieldsMetaDS();
        }
        finally
        {
            DbUtil.closeAll(rs, cs, conn, false);
        }
    }

    public static Timestamp getMaxTimeStamp(Map mapParams) {
        Timestamp maxTimestamp=null;
        try
        {
            if (mapParams!=null)
            {
                String[] params=(String[])mapParams.get(TablesTypes.ID_TM);
                if (params!=null && params.length>0)
                    maxTimestamp = new Timestamp(Long.parseLong(params[0]));

                String[] params2=(String[])mapParams.get(TablesTypes.ID_TN);
                if (params2!=null && params2.length>0 && maxTimestamp!=null)
                    maxTimestamp.setNanos(Integer.parseInt(params2[0]));
            }
        }
        catch (NumberFormatException e)
        {
            //
        }
        if (maxTimestamp==null)
            maxTimestamp=new Timestamp(getDefaultTimeStamp());
        return maxTimestamp;
    }

    private static String getSets(Integer[] dor_code, String replaceString) {
        for (int i = 0, dor_codeLength = dor_code.length; i < dor_codeLength; i++)
        {
            if (i>0) replaceString +=",";
            replaceString +=dor_code[i];
        }
        return replaceString;
    }

    public static long getDefaultTimeStamp()
    {
        Calendar cl = Calendar.getInstance();
        cl.set(Calendar.YEAR,1999);
        cl.set(Calendar.MONTH,12);
        cl.set(Calendar.DAY_OF_MONTH,31);

        cl.set(Calendar.HOUR,0);
        cl.set(Calendar.MINUTE,0);
        cl.set(Calendar.SECOND,0);
        return cl.getTimeInMillis();
    }

    private static void setObjectArc(Map<String,ColumnHeadBean> attrs,Timestamp cor_time,ResultSet rs,Map<String, Pair<Timestamp, Object>> obj) throws Exception
    {
        String text = rs.getString("text");
        String comment = "";//rs.getString("comment");

        obj.put("o_cor_time",new Pair<Timestamp, Object>(cor_time,cor_time));
//        if (!attrs.containsKey("o_cor_time"))
//            attrs.put("o_cor_time",new ColumnHeadBean("o_cor_time","o_cor_time",ListGridFieldType.INTEGER.toString()));

        obj.put("o_text",new Pair<Timestamp, Object>(cor_time,text));
        if (!attrs.containsKey("o_text"))
            attrs.put("o_text",new ColumnHeadBean("o_text","o_text",ListGridFieldType.TEXT.toString()));



        obj.put("o_comment",new Pair<Timestamp, Object>(cor_time,comment));

//        if (!attrs.containsKey("o_comment"))
//            attrs.put("o_comment",new ColumnHeadBean("o_comment","o_comment",ListGridFieldType.TEXT.toString()));

    }


}
