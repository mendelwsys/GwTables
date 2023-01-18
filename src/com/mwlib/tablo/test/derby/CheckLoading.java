package com.mwlib.tablo.test.derby;

import com.mwlib.tablo.db.*;
import com.mwlib.utils.db.DbUtil;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.tables.ColumnHeadBean;

import com.mwlib.tablo.db.desc.WarningDesc;
import com.mwlib.tablo.db.desc.WindowsDesc;
import com.mwlib.tablo.derby.DerbyUtils;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 17.10.14
 * Time: 13:52
 * Загрузка данными таблицы Дерби, посмотрим сколько потребуется времени для перегрузки данных
 */
public class CheckLoading
{
    public static void main(String[] args) throws Exception
    {

        Map<String, Object> outParams = new HashMap<String, Object>();
        Timestamp maxTimeStamp = EventProvider.getMaxTimeStamp2(outParams);
        Map<String, ParamVal> valMap = new HashMap<String, ParamVal>();
        valMap.put(TablesTypes.MAX_TIMESTAMP, new ParamVal(-1, maxTimeStamp, Types.NULL));
        valMap.put(TablesTypes.CORTIME, new ParamVal(1, maxTimeStamp, Types.TIMESTAMP));


        WarningDesc warningDesc = new WarningDesc(false);
//        WarningDesc warningDescNP = new WarningDesc(false,TablesTypes.WARNINGS_NP,new int[]{4});//TODO Нет ни одного тапла из-за этого и не грузимся что ли, так должно грузится из метаинформации
        WindowsDesc windowsDesc = new WindowsDesc(false);
        //BaseTableDesc[] descs = {windowsDesc, warningDescNP, warningDesc};
        BaseTableDesc[] descs = {windowsDesc, warningDesc};
        EventTypeDistributer metaProvider = new EventTypeDistributer(new Type2NameMapperAuto(descs),false);

        EventProviderTImpl eventProvider = new EventProviderTImpl(metaProvider);
        Pair<IMetaProvider, Map[]> utbl = eventProvider.getUpdateTable(valMap, new HashMap());

        for (BaseTableDesc desc : descs)
        {
            Set<String> nCols=new HashSet<String>();
            loadTableByEvent(desc, utbl, nCols);
        }


//        String checkSql=" select count(*) from "+tblName;
//        ResultSet rs = stmt.executeQuery(checkSql);
//        rs.next();
//        int cnt=rs.getInt(1);
//
//        System.out.println("CNT:"+cnt);
    }

    public static void loadTableByEvent(BaseTableDesc tableDesc, Pair<IMetaProvider, Map[]> utbl, Set<String> nCols) throws Exception
    {
        String tblName=tableDesc.getTableType();
        HashMap<String,ColumnHeadBean> nColNames=new HashMap<String,ColumnHeadBean>();
        ColumnHeadBean[] cols = utbl.first.getColumnsByEventName(tableDesc.getTableType());
        for (ColumnHeadBean col : cols)
            nColNames.put(col.getName(),col);//TODO а возможно все таки по заголовку?

        if (nCols==null)
            nCols=new HashSet<String>();
        if (nCols.size()==0)
            nCols.addAll(nColNames.keySet());

        //Создаем сначала таблицу
        //1.Создать запрос на создание

        StringBuffer tbl=new StringBuffer(tblName+" ( ");
        StringBuffer itbl=new StringBuffer(tblName+" ( ");
        for (String colNames : nCols)
        {
                tbl = tbl.append(colNames).append(" ").append(DerbyUtils.translate2DerbyType(nColNames.get(colNames).getType())).append(" ,");
                itbl=itbl.append(colNames).append(" ,");

        }
        String createTbl="create table "+tbl.substring(0,tbl.length()-1)+" )";

        Connection conn= DbUtil.getConnection2(DbUtil.DS_JAVA_NAME);
        Statement stmt = conn.createStatement();
        stmt.execute(createTbl);



        int[]  tbls=tableDesc.getDataTypes();
        Set<Integer> datatypes=new HashSet<Integer>();
        for (int i : tbls) {
            datatypes.add(i);
        }

        StringBuffer insertSQL=new StringBuffer("insert into "+itbl.substring(0,itbl.length()-1)+" )  values ");

        //Далее загружаем
        for (Map map : utbl.second)
        { //Перегружаем


           if(!datatypes.contains(map.get(TablesTypes.DATATYPE_ID)))
               continue;

            StringBuffer vals=new StringBuffer(" (");
            for (String colNames : nCols)
            {
                Object obj = map.get(colNames);
                if (obj instanceof String)
                    vals.append("'").append(obj).append("',");
                else if (obj instanceof Timestamp)
                    vals.append("'").append(obj).append("',");
                else
                    vals.append(obj).append(",");
            }
            insertSQL= insertSQL.append(vals.substring(0, vals.length() - 1)).append(" )").append(",");
        }
        stmt.execute(insertSQL.substring(0, insertSQL.length() - 1));
    }

}
