package com.mycompany.client.updaters;

import com.mycompany.common.TablesTypes;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 23.04.15
 * Time: 14:51
 * Устанавливает время для персчета в фильтрах  и засекает интервалы
 */
public class TimeUpdaterCtrl
{
    private Date currentDT= new Date(System.currentTimeMillis()+ TablesTypes.tMSK);
    private Long currentTimeInterval =null;
    private int recalcMin;
    private int recalcSec;

    public TimeUpdaterCtrl(int recalcMin,int recalcSec)
    {
        this.recalcMin = recalcMin;
        this.recalcSec = recalcSec;
    }

//-----------------TODO Все это перенести в отдельный апдейтер для окон или формировать на сервере спец. вычислительную функцию!!!!!
    public boolean isNeedCalcFilter()
    {
    boolean filterWasChanged=(currentTimeInterval==null);
    long l = System.currentTimeMillis();
    if (!filterWasChanged)
    {
        long dc = (l - currentTimeInterval)/(1000);
        filterWasChanged=(dc>=(recalcMin*60+recalcSec));
    }

    if (filterWasChanged)
    {
//            long delta=(new Date().getTimezoneOffset()+180)*60000;//TODO Поскольку время в БД московское //07.04.2015 НЕ ВЕРНО поскольку с сервера приходит московское время
// и  его при преобразовании эксплорер двигает в ту тайм зону в которой он функционирует, т.е. суммарное время = гринвич+tMSK + TCURRENTZONE
        currentDT.setTime(l+ TablesTypes.tMSK);
        currentTimeInterval =l;
    }
    return filterWasChanged;
}

    public Date getCurrentDT() {
        return currentDT;
    }
}
