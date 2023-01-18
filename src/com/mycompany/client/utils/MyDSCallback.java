package com.mycompany.client.utils;

import com.google.gwt.user.client.Timer;
import com.mycompany.client.operations.ICliFilter;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.DSCallback;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 26.05.14
 * Time: 14:00
 *
 */
abstract public class MyDSCallback implements DSCallback
{

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public MyDSCallback(int period)
    {
        this.period=period;
    }

    public MyDSCallback()
    {
        period=3000;
    }

    public Collection<ICliFilter> getFilters() {
        return filters;
    }

    public void setFilters(Collection<ICliFilter> filters) {
        this.filters = filters;
    }

    protected Collection<ICliFilter> filters;

    public int getSrvCnt() {
        return ++srvCnt;
    }

    public int resetSrvCnt() {
        return srvCnt = TablesTypes.START_POS-1;
    }


    protected int cliCnt = TablesTypes.START_POS;

    public String getTblId() {
        return tblId;
    }

    protected String tblId;
    protected int srvCnt = TablesTypes.START_POS-1;

    public Timer getTimer()
    {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    Timer timer;

    public long getTimeStamp() {
        return timeStamp;
    }

    protected long timeStamp=0;

    public long getLastTimeStamp() {
        return lastTimeStamp;
    }

    public void setLastTimeStamp(long lastTimeStamp) {
        this.lastTimeStamp = lastTimeStamp;
    }

    protected long lastTimeStamp = 0;

    public int getTimeStampN() {
        return timeStampN;
    }

    protected int timeStampN=0;


    protected int period;


}
