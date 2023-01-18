package com.mwlib.tablo;

import com.mycompany.common.TablesTypes;
import com.mycompany.common.cache.CacheException;
import com.mwlib.tablo.db.BaseTableDesc;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 25.09.14
 * Time: 17:40
 *
 */
public interface ICliProvider extends IDataProvider
{
    static int START_CLI_POS_CNT = TablesTypes.START_POS-1;

    UpdateContainer getNewDataKeys(Map<String, Object> parameters) throws CacheException;
    BaseTableDesc getTableDesc(Map<String, Object> parameters) throws CacheException;

    String[] getKeyCols();
    int getCliCurrCnt();
    int getCliCnt();
    String getTblId();


}
