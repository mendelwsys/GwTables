package com.mwlib.tablo.cache;

import com.mycompany.common.cache.CacheException;
import com.mycompany.common.cache.IKeyGenerator;
import com.mycompany.common.cache.INm2Ix;
import com.mycompany.common.tables.ColumnHeadBean;
import com.mwlib.tablo.IDataProvider;
import com.mwlib.tablo.UpdateContainer;

import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 22.09.14
 * Time: 18:55
 *
 */
public interface ICache extends IDataProvider, INm2Ix {


    public static String CACHENAME="CACHENAME";
    public static String TEST="TEST";

    ColumnHeadBean[] setMeta(ColumnHeadBean[] cols) throws CacheException;

    int size() throws CacheException;

    UpdateContainer update(Map[] inTuples, boolean insertAllNotFound) throws CacheException;
    UpdateContainer update(Map inTuples, boolean insertAllNotFound) throws CacheException;

//   Object[] map2Tuple(Map inTuple) throws CacheException;

//    Map<Object,long[]> update(Object[][] inTuples) throws CacheException;

//    Map<Object,long[]> reset(Object[][] inTuples) throws CacheException;

    IKeyGenerator getKeyGenerator();

    void setKeyGenerator(IKeyGenerator keyGenerator);

    Map<Object,long[]> remove(Map inTuple) throws CacheException;

    Map<Object,long[]> removeTuples(Map<Object,Map> delTuples) throws CacheException;

    Map<Object,long[]> removeAll(Set<Object> keys4Remove) throws CacheException;

//    Set<Object> createContainsKeys(Map[] inTuples) throws CacheException;

    Object createContainsKey(Map inTuple) throws CacheException;


}
