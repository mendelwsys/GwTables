package com.mwlib.tablo;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 12.10.14
 * Time: 14:15
 * Контекс табличного приложения
 */
public class AppContext
{
    public static final String APPCONTEXT = "APPCONTEXT";
    private AtomicInteger tblCnt=new AtomicInteger(0);
    public int getNextTableIndex()
    {
        return tblCnt.addAndGet(1);
    }
}
