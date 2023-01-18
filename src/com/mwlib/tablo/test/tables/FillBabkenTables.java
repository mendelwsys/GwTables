package com.mwlib.tablo.test.tables;

import com.mwlib.tablo.cache.ICache;
import com.mwlib.tablo.db.*;
import com.mwlib.tablo.db.desc.BabkenDesc;
import com.mwlib.tablo.derby.DerbyCache;
import com.mwlib.utils.db.DbUtil;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.cache.SimpleKeyGenerator;
import com.mycompany.common.tables.ColumnHeadBean;


import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: NEMO
 * Date: 09.10.19
 * Time: 18:31
 * To change this template use File | Settings | File Templates.
 */
public class FillBabkenTables
{

    static Connection connection;
    static {
        DbUtil.name2Connector.clear();
        DbUtil.name2Connector.put(DbUtil.DS_JAVA_CACHE_NAME,new DbUtil.IJdbCOnnection()
        {
            @Override
            public Connection getConnection() throws ClassNotFoundException, SQLException
            {
                connection = DriverManager.getConnection("jdbc:derby:C:/PapaWK/Projects/JavaProj/SGWTVisual2/db/udb4;create=false");
                return connection;
            }
        });

//        DbUtil.name2Connector.put(DbUtil.DS_JAVA_CACHE_H_NAME,new DbUtil.IJdbCOnnection()
//        {
//            @Override
//            public Connection getConnection() throws ClassNotFoundException, SQLException
//            {
//                connection = DriverManager.getConnection("jdbc:derby:C:/PapaWK/Projects/JavaProj/SGWTVisual2/db/udb4;create=false");
//                return connection;
//            }
//        });

    }

    public static void main(String[] args) throws Exception
    {
         new FillBabkenTables().doThis();
    }

    private void doThis() throws Exception {
        boolean test=false;
        Type2NameMapperAuto mapper = new Type2NameMapperAuto
        (
            new BaseTableDesc[]
            {
                new BabkenDesc(test)
            }
        );

        EventTypeDistributer metaProvider = new EventTypeDistributer(mapper, test);
        String[] names = mapper.getNames();
        for (String nm : names)
        {
            BaseTableDesc desc = mapper.getTableDescByTblName(nm);
            metaProvider.addToMeta(desc.getTableType(), desc.getMeta());
        }


        IEventProvider eventProvider = new BabkenProvider(metaProvider);

        Map<String, Object> outParams_update = new HashMap<String, Object>();
        Map<String, ParamVal> valMap_upd = new HashMap<String, ParamVal>();
        Timestamp maxTimeStamp_update = EventProvider.getMaxTimeStamp2(outParams_update);
        {
            maxTimeStamp_update.setTime(maxTimeStamp_update.getTime() - 10000);
            valMap_upd.put(TablesTypes.MAX_TIMESTAMP, new ParamVal(-1, maxTimeStamp_update, Types.NULL));
            valMap_upd.put(TablesTypes.CORTIME, new ParamVal(1, maxTimeStamp_update, Types.TIMESTAMP));
        }
        Pair<IMetaProvider, Map[]> rv = eventProvider.getUpdateTable(valMap_upd, outParams_update);
        ColumnHeadBean[] colHeaders = rv.first.getColumnsByEventName(TablesTypes.BABKEN_TYPE);


        Map<String, Object> params = new HashMap<String,Object>();
        params.put(ICache.CACHENAME, TablesTypes.BABKEN_TYPE);
        DerbyCache cache = new DerbyCache(params)
        {
            protected String[] getIXColNames()
            {
                return new String[0];
                        //new String[]{TablesTypes.DATA_OBJ_ID,TablesTypes.OBJ_OSN_ID};
            }

        };

        cache.setMeta(colHeaders);
        cache.setKeyGenerator(new SimpleKeyGenerator(new String[]{TablesTypes.KEY_FNAME}, cache));
        cache.update(rv.second,true);
    }
}
