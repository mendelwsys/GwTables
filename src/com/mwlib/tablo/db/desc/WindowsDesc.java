package com.mwlib.tablo.db.desc;

import com.mycompany.common.FieldException;
import com.mycompany.common.TablesTypes;


import java.sql.Timestamp;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 07.07.14
 * Time: 18:20
 *
 */

public class WindowsDesc extends WindowsCommonDec
{


    protected void initDescriptor()
    {
        super.initDescriptor();
        tableTypeName=TablesTypes.WINDOWS;
        tableTypes=new int[]{54, 56, 59};

    }


    public WindowsDesc(boolean test, String tableTypeName, int[] tableTypes)
    {
        super(test, tableTypeName, tableTypes);
    }

    public WindowsDesc(boolean test)
    {
        super(test);
    }

    @Override
    protected String setRowStyle(Map tuple, Map<String, Object> _outTuple) throws FieldException
    {
        Integer overTime = (Integer) tuple.get(OVERTIME);

        Timestamp nd = (Timestamp) tuple.get("ND");
        Timestamp kd = (Timestamp) tuple.get("KD");

        Timestamp gnd = (Timestamp) tuple.get("DT_ND");
        Timestamp gkd = (Timestamp) tuple.get("DT_KD");

        if (gnd != null && gnd.getTime() > 0)
            nd = gnd;
        if (gkd != null && gkd.getTime() > 0)
            kd = gkd;

        Integer fact = (Integer) tuple.get(TablesTypes.STATUS_FACT);
        if (fact==null)
            fact=-1;


        long current = System.currentTimeMillis();

        String rValue = "";
        switch (fact) {
            case 0: { //не активно, проверяем предоставлено оно или просто не активно

                if (nd.getTime() > current)
                {
                    final long l = nd.getTime() - current;
                    if (l / 60000 < 30)
                    {
                         rValue = "Ожидание открытия:" + getTimeInterval(nd, current, 1);
                            if (l>=0)
                                _outTuple.put(TablesTypes.ROW_STYLE, "background-color:#55FFFF;");
                            else
                                _outTuple.put(TablesTypes.ROW_STYLE, "background-color:#DD8800;");
                    }
                    else
                        rValue = "До начала:" + getTimeInterval(nd, current, 1);
                }
                else
                    rValue = "Не предоставлено";
                break;
            }
            case 1: { //активно проверяем не передержано оно
                if (kd.getTime() < current) {
                    if (kd.getTime() < current + 5*60000)
                        rValue = "Передержка:" + getTimeInterval(kd, current, -1);
                    else
                        rValue = "Ожидание закрытия:" + getTimeInterval(kd, current, -1);
                    _outTuple.put(TablesTypes.ROW_STYLE, "background-color:#FF3300;");
                }
                else
                {
                    final long l = kd.getTime() - current;
                    if (l / 60000 < 30)
                    {
                        rValue = "Ожидание закрытия:" + getTimeInterval(kd, current, 1);
                        _outTuple.put(TablesTypes.ROW_STYLE,"background-color:#DDDD00;");
                    }
                    else
                    {
                        rValue = "До окончания:" + getTimeInterval(kd, current, 1);
                        _outTuple.put(TablesTypes.ROW_STYLE, "background-color:#77DD00;");
                    }
                }
                break;
            }
            case 2: { //закрыто, по овертайму проверим было ли оно передержано
                rValue = "Закрыто";
                if (overTime!=null && overTime > 0) {
                    rValue = rValue + " " + overTime + " мин";
                    _outTuple.put(TablesTypes.ROW_STYLE, "background-color:#DD3300;");
                } else
                    _outTuple.put(TablesTypes.ROW_STYLE, "background-color:#55DD00;");
                break;
            }
            case 3: {
                _outTuple.put(TablesTypes.ROW_STYLE, "background-color:#FF0000;");
                rValue = "Сорвано";
                break;
            }
            default:
                rValue = "Карточка";
        }
        return rValue;
    }





}
