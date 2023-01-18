package com.mwlib.tablo.test.tables;

import com.mycompany.common.TablesTypes;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 07.07.14
 * Time: 18:06
 * TODO Здесь необходимо проверить что обеспечивается многосессионность (многопользовательский режим)
 */
public class TableSwitcher
{
    public static boolean isTest()
    {
        return true;
    }

    public static BaseTable getInstance(Map mapParams,boolean test) throws Exception
    {
        String[] params=(String[])mapParams.get(TablesTypes.TTYPE);
        String addErr="";
        if (params!=null && params.length>0)
        {
            if (TablesTypes.WARNINGS.equals(params[0]))
                return WarningsT.getInstance(test);
            else if (params[0]!=null && params[0].startsWith(TablesTypes.WINDOWS))
                return WindowsT.getInstance(test);
            else if (TablesTypes.REFUSES.equals(params[0]))
                return RefuseT.getInstance(test);
            else if (TablesTypes.VIOLATIONS.equals(params[0]))
                return ViolationT.getInstance(test);
            else if (TablesTypes.DELAYS_GID.equals(params[0]))
                return DelayGIDT.getInstance(test);
            else if (TablesTypes.LOST_TRAIN.equals(params[0]))
                return LostTrT.getInstance(test);
            else if (TablesTypes.MARKS_GID.equals(params[0]))
                return GidMarksT.getInstance(test);
            else if (TablesTypes.LENTA.equals(params[0]))
                return StripT.getInstance(test);
                //return StripT.getInstance(true);//TODO ТЕСТ ДЛЯ ПОЛОСЫ

//            else if (TablesTypes.VAGTOR.equals(params[0]))
//                return VagInTORT.getInstance(test);

            addErr=": params:"+params[0];
        }
        throw new Exception("Can't define table type for instance "+addErr);

    }


}
