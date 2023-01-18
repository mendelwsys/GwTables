package com.mwlib.tablo.db.desc;

import com.mwlib.tablo.db.EventProvider;
import com.mycompany.common.FieldException;
import com.mycompany.common.TablesTypes;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 07.07.14
 * Time: 18:20
 *
 */

public class WindowsCurrentDesc extends WindowsCommonDec
{



    protected void initDescriptor()
    {
        {
            fNames=new String[]{TablesTypes.DOR_NAME,"ID_Z",SERV,"PEREG",TablesTypes.PRED_NAME,TablesTypes.COMMENT,"ND","KD",TablesTypes.CRDURL,
                    TablesTypes.DOR_CODE,TablesTypes.PRED_ID, TablesTypes.VID_ID,TablesTypes.VID_NAME,
                    TablesTypes.STATUS_FACT,STATUS_PL,OVERTIME,TablesTypes.KEY_FNAME,TablesTypes.EVTYPE,
                    TablesTypes.EVTYPE_NAME,TablesTypes.EVTYPE_NAME,TablesTypes.DATATYPE_ID,TablesTypes.ACTUAL, EventProvider.DATE_MIN_ND,EventProvider.COR_MAX_TIME};
        }

        super.initDescriptor();

        tableTypeName=TablesTypes.WINDOWS_CURR;
        tableTypes=new int[]{46,57,60};
    }

    public WindowsCurrentDesc(boolean test)
    {
        super(test);
    }

    protected String setRowStyle(Map tuple, Map<String, Object> _outTuple) throws FieldException
    {
        String rValue="Карточка";
        Integer overTime = (Integer) tuple.get(OVERTIME);
        if (overTime!=null && overTime > 0)
        {
            rValue = rValue + " Перед (" + overTime + " мин)";
            _outTuple.put(TablesTypes.ROW_STYLE, "background-color:#DD3300;");
        }
        return rValue;
    }



}
