package com.mycompany.client.test.DemoChart;

import com.mycompany.client.dojoChart.CalcManager;
import com.mycompany.client.dojoChart.ChartLevelDef;
import com.mycompany.client.dojoChart.IChartLevelDef;
import com.mycompany.common.TablesTypes;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 03.02.15
 * Time: 14:06
 * To change this template use File | Settings | File Templates.
 */
public class TestCalcManager extends CalcManager {

    public TestCalcManager()
    {
        super(new IChartLevelDef[]
        {
                new ChartLevelDef(TablesTypes.KEY_FNAME,"DOR_KOD", "DOR_NAME"),
                new ChartLevelDef(TablesTypes.KEY_FNAME,"VID_ID", "VID_NAME"),
                new ChartLevelDef(TablesTypes.KEY_FNAME,"PRED_ID", "PRED_NAME"),
                new ChartLevelDef(TablesTypes.KEY_FNAME,null, null) //Группировок нет вернуть занчение указанного поля
        });
    }

//    protected IGetRecordVal[] createCalcFunctions(String _title,String colName, Object graphId)
//    {
//        if (_title==null)
//            _title="";
//        final String title=_title;
//
//
//        return new IGetRecordVal[]{new SumGetRecordVal(graphId,title+" [$val] дороги ","red"),new SumGetRecordVal(graphId,title+" [$val] ($) службы ","blue"),new SumGetRecordVal(graphId,"","green")
//        {
//            public JavaScriptObject getCharTitle(ValDef def)
//            {
//
//                JavaScriptObject rv=super.getCharTitle(def);
//                JSOHelper.setAttribute(rv,"title",title+" ["+getViewVal(def)+"] ("+def.getParentDef().getViewText()+") предприятия службы "+def.getViewText());
//                return rv;
//            }
//        },new GetRecordValByColName(colName,graphId,title+" [здесь должна быть таблица]","orange")};
//    }


}
