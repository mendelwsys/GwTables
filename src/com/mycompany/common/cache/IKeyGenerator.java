package com.mycompany.common.cache;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 22.09.14
 * Time: 18:41
 * To change this template use File | Settings | File Templates.
 */
public interface IKeyGenerator
{
    Object getKeyByTuple(Object[] tuple) throws CacheException;
    public Object getKeyByTuple(Map tuple) throws CacheException;
    String[] getKeyCols();
    void setKeyCols(int[] ixCols) throws CacheException;
    void setKeyCols(String[] cols) throws CacheException;
}

