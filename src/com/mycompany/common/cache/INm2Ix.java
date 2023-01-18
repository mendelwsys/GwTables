package com.mycompany.common.cache;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 18.10.14
 * Time: 18:39
 * To change this template use File | Settings | File Templates.
 */
public interface INm2Ix
{
    Map<String, Integer> getColName2Ix();
    Map<Integer, String> getIx2ColName();

}
