package com.mwlib.tablo.test.tpolg;

import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.cache.CacheException;
import com.mycompany.common.tables.ColumnHeadBean;
import com.mwlib.tablo.SQLUtils;
import com.mwlib.tablo.UpdateContainer;
import com.mwlib.tablo.cache.CliProvider;
import com.mwlib.tablo.cache.ICache;
import com.mwlib.tablo.db.BaseTableDesc;
import com.mwlib.tablo.derby.DerbyTableOperations;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 04.06.15
 * Time: 14:23
 *
 */
public class PolgCliProvider  extends CliProvider
{

    private Object currentCounter;
    private String reqSQLPrefix;
    private String maxSQLPrefix;

    public PolgCliProvider(Pair<ICache, BaseTableDesc> dataCache, String cliId, String tblId, String[] tType,String tblName)
    {
        super(dataCache, cliId, tblId, tType);


        this.reqSQLPrefix="select distinct DATA_OBJ_ID || '##' || trim(char(DATATYPE_ID)) || '##' || trim(char(po.POLG_ID)) as "+TablesTypes.KEY_FNAME+","+
                "DATA_OBJ_ID || '##' || trim(char(DATATYPE_ID)) as "+TablesTypes.KEY_FNAME+"2,"+" plg.NAME as PNAME,"
                +TablesTypes.DATATYPE_ID+"," +
                "DATA_OBJ_ID,po.POLG_ID from "+tblName+" tdp,POLG_OBJ po, POLG plg where plg.POLG_ID=po.POLG_ID and po.POLG_TYPE=100185 and po.OBJ_OSN_ID=tdp.OBJ_OSN_ID  and tdp.KEY_DERBY_UPD00>";
        this.maxSQLPrefix="select MAX(tdp.KEY_DERBY_UPD00) from "+tblName+" tdp where tdp.KEY_DERBY_UPD00>";

    }

   DerbyTableOperations  derbyTableOperations=DerbyTableOperations.getDefDerbyTableOperations();

    @Override
    public Object[][] getTuplesByParameters(Map<String, Object> parameters,UpdateContainer updateContainer) throws CacheException
    {
        if (currentCounter==null || (updateContainer!=null && updateContainer.dataRef!=null && updateContainer.dataRef.size()>0))
        {
            if (currentCounter==null)
                currentCounter=-1l;

            try
            {
                final String maxSQLPrefix = this.maxSQLPrefix + String.valueOf(currentCounter);
                Object[][] res=SQLUtils.execQuery(derbyTableOperations.getDerbyConnection(),maxSQLPrefix,null);
                if (res==null || res.length==0)
                    return new Object[0][];
                final String reqSQLPrefix = this.reqSQLPrefix + String.valueOf(currentCounter);

                currentCounter=res[0][0];

                final List<ColumnHeadBean> listOfColumn;
                if (dataCache.first.getMeta()==null || dataCache.first.getMeta().length==0)
                    listOfColumn = new LinkedList<ColumnHeadBean>();
                else
                    listOfColumn=null;

                Object[][] rv = SQLUtils.execQuery(derbyTableOperations.getDerbyConnection(), reqSQLPrefix, listOfColumn);

                if (listOfColumn!=null)
                    dataCache.first.setMeta(listOfColumn.toArray(new ColumnHeadBean[listOfColumn.size()]));

                return rv;

            }
            catch (Exception e)
            {
                throw new CacheException(e);
            }
        }
        return new Object[0][];

    }


}
