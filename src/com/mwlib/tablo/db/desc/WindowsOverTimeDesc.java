package com.mwlib.tablo.db.desc;

import com.mwlib.tablo.db.EventProvider;
import com.mycompany.common.TablesTypes;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 07.07.14
 * Time: 18:20
 *
 */
public class WindowsOverTimeDesc extends WindowsDesc
{
    public WindowsOverTimeDesc(boolean test)
    {
        super(test);
    }

    @Override
    protected void initDescriptor()
    {
        {
            fNames=new String[]{TablesTypes.DOR_NAME,"ID_Z",TablesTypes.VID_ID,TablesTypes.VID_NAME,"PEREG",TablesTypes.PRED_NAME,TablesTypes.COMMENT,"ND","KD","DT_ND","DT_KD",TablesTypes.CRDURL,
                    TablesTypes.DOR_CODE,TablesTypes.PRED_ID,
                    TablesTypes.STATUS_FACT,STATUS_PL,OVERTIME,TablesTypes.KEY_FNAME,TablesTypes.EVTYPE,
                    TablesTypes.EVTYPE_NAME,TablesTypes.EVTYPE_NAME,TablesTypes.DATATYPE_ID,TablesTypes.ACTUAL, EventProvider.DATE_MIN_ND,EventProvider.COR_MAX_TIME};
        }
        super.initDescriptor();
        tableTypeName=TablesTypes.WINDOWS_OVERTIME;
        tableTypes=new int[]{61};
    }

}
