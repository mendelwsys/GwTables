package com.mwlib.tablo.derby;

import com.mwlib.tablo.cache.CliProvider;
import com.mwlib.tablo.cache.ICache;
import com.mwlib.tablo.cache.INm2IxEx;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.cache.CacheException;
import com.mwlib.tablo.UpdateContainer;
import com.mwlib.tablo.db.BaseTableDesc;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 22.09.14
 * Time: 20:04
 * Провайдер для работы с кешем дерби
 */
public class DerbyCliProvider extends CliProvider
{
    public DerbyCliProvider(Pair<ICache, BaseTableDesc> dataCache, String cliId, String tblId, String tType)
    {
        super(dataCache, cliId, tblId, tType);
    }

    private long transactionId=DerbyCache.DERBY_START;


    protected boolean isNotFullRequest(Map<String, Object> parameters, boolean fUpdate) throws CacheException
    {
        boolean rv = super.isNotFullRequest(parameters, fUpdate);
        if (!rv)
            transactionId=DerbyCache.DERBY_START;
        return rv;
    }

    @Override
    public Object[][] getTuplesByParameters(Map<String, Object> parameters, UpdateContainer containerParams) throws CacheException
    {
        String[] filters=(String[]) parameters.get(TablesTypes.FILTERDATAEXPR);  //Приходять разными фильтрами поскольку разные алгоритмы клиентской обработки (в частности снятия фильтра)
        if (filters==null)
            filters=new String[0];
        final List<String> lFilters = new LinkedList<String>(Arrays.asList(filters));

//        String[] serverFilters=(String[]) parameters.get(TablesTypes.SERVERFILTER);
//        if (serverFilters==null)
//            serverFilters=new String[0];

//        lFilters.addAll(Arrays.asList(serverFilters));

        String filter=DerbyTableOperations.DERBYUPDATE+" > "+String.valueOf(transactionId);
        lFilters.add(0, filter);


        parameters.put(TablesTypes.FILTERDATAEXPR,lFilters.toArray(new String[lFilters.size()]));

        Object[][] res = super.getTuplesByParameters(parameters, containerParams);

        Integer ixDerbyUpdate;
        if (dataCache.first instanceof INm2IxEx)
            ixDerbyUpdate=((INm2IxEx)dataCache.first).getAllColName2Ix().get(DerbyTableOperations.DERBYUPDATE);
        else
            ixDerbyUpdate=dataCache.first.getColName2Ix().get(DerbyTableOperations.DERBYUPDATE);

        if (ixDerbyUpdate==null)
            throw new UnsupportedOperationException("Can't operate with data cache with not supported "+DerbyTableOperations.DERBYUPDATE+" column name");

        for (Object[] re : res)
        {
            Long transId = (Long) re[ixDerbyUpdate];
            if (transactionId< transId)
                transactionId= transId;
        }
        return res;
    }
}
