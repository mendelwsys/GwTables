package com.mwlib.tablo.test.csv;

import com.mwlib.tablo.cache.ICache;
import com.mwlib.tablo.derby.DerbyCache;
import com.mwlib.utils.db.DbUtil;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.cache.CacheException;
import com.mycompany.common.tables.ColumnHeadBean;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 14.05.15
 * Time: 16:41
 * To change this template use File | Settings | File Templates.
 */
public class TestSelectFromCache
{

    static Connection connection;
    static {
        DbUtil.name2Connector.put(DbUtil.DS_JAVA_CACHE_NAME,new DbUtil.IJdbCOnnection()
        {
            @Override
            public Connection getConnection() throws ClassNotFoundException, SQLException
            {
                connection = DriverManager.getConnection("jdbc:derby:C:/PapaWK/Projects/JavaProj/SGWTVisual2/db/nsi;create=true");
                return connection;
            }
        });
    }

    public static void main(String[] args) throws CacheException
    {
        String tableNames="POLG_OBJ";
        final String[] keyCols = {"POLG_ID","OBJ_OSN_ID"};
        Integer[] obj_osn_ids={
                6507,
      6508,
      6510,
      6511 ,
      6513  ,
     41448   ,
   1015120    ,
   1015122     ,
   1015123      ,
  17004787       ,
  17004788
        };

        final ICache cache;
        {
            Map cacheParams = new HashMap();
            cacheParams.put(ICache.CACHENAME,tableNames);
            cacheParams.put(ICache.TEST,true);
            cache = new DerbyCache(cacheParams);
        }

        cache.setMeta(new ColumnHeadBean[0]);
        //cache.setKeyGenerator(new SimpleKeyGenerator(keyCols, cache));
        {
            Map params=new HashMap<String,String>();

            if (obj_osn_ids.length>0)
            {
                String filter = "OBJ_OSN_ID IN (";
                for (int i = 0; i < obj_osn_ids.length; i++)
                {
                    if (i>0)
                        filter+=",";
                    filter+=String.valueOf(obj_osn_ids[i]);
                }
                filter+=") ";
                params.put(TablesTypes.FILTERDATAEXPR,new String[]{filter});
            }
            Object[][] res = cache.getTuplesByParameters(params, null);
            Set<Integer> set = new HashSet<Integer>();
            for (Object[] re : res)
                set.add((Integer)re[1]);
            System.out.println("set = " + set);
        }




    }
}
