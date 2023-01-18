package com.mycompany.client.dojoChart;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayMixed;
import com.mycompany.client.utils.JScriptUtils;
import com.smartgwt.client.util.JSOHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 29.01.15
 * Time: 20:10
 *
 */
public class ClusteredColumns extends BaseDOJOChart
{

    public ClusteredColumns(int width, int height)
    {
        super(width, height);
        setChartType(ChartType.ClusteredColumns);

    }

    @Override
    protected JavaScriptObject getChartPlotKwArs()
    {
        return JScriptUtils.s2j("{markers:true,gap:10}");
    }



    public JavaScriptObject getDefaultXArgs(int max)
    {
        Map<String,Object> param=new HashMap<String,Object>();
        param.put("microTickStep", 2);
        param.put("minorTickStep",2);
        param.put("max",max);
        return JScriptUtils.m2j(param);
    }

    public JavaScriptObject getDefaultYArgs()
    {
        Map<String,Object> param=new HashMap<String,Object>();
        param.put("vertical", true);
        param.put("fixLower","major");
        param.put("fixUpper","major");
        return JScriptUtils.m2j(param);
    }

    public JavaScriptObject getDefaultHighlightArs()
    {
        return JScriptUtils.s2j("{highlight:'gold'}");
    }

    public JavaScriptObject getDefaultSeriesArs()
    {
//        return JS.s2j("{stroke:{color:'black'},fill:'blue'}");
        return null;
    }


    public JsArrayMixed prepareData(ValDef levelDef)
    {
        Map<Object,ValDef> levels = levelDef.getChildDef();
        JsArrayMixed numbers= JsArray.createArray().cast();

        int ix=0;
        index2key.clear();
        for (Object o : levels.keySet())
        {
            index2key.put(ix,o);
            JavaScriptObject object=JavaScriptObject.createObject().cast();

            ValDef level=levels.get(o);


            IFormatValue formatValue = level.getFormatValue(getBindName());
            if (formatValue==null)
                continue;
                //throw new RuntimeException("Can't define type of value");

            final Value value = level.getValue(getBindName());
            if (!formatValue.isPresentableValue(value)) continue;
            JSOHelper.setAttribute(object, "y", formatValue.presentationValue(value));

//            final Value value = level.getValue(getBindName());
//            if (value==null || value<=0)
//                continue;

//            if (ListGridFieldType.INTEGER.getValue().equals(formatValue.getFtype()))
//                JSOHelper.setAttribute(object, "y", (int) Math.round(value));
//            else
//                JSOHelper.setAttribute(object, "y", value);

            String tooltipVal=level.getViewVal(getBindName());

            String viewText=level.getViewText();
            if (viewText!=null)
                JSOHelper.setAttribute(object, "text", viewText+" ["+tooltipVal+"]");

            numbers.push(object);
            ix++;
        }
        return numbers;
    }


//   public boolean defaultPrepare4Draw(ValDef[] levelDef, Object[] arg)
//   {
//        int maxSize=0;
//        for (int i = 0; i < levelDef.length; i++)
//        {
//            String serName=DEF_SERIES_NAME+"#"+i;
//            if (levelDef[i]!=null)
//            {
//                Map<Object, ValDef> childDef = levelDef[i].getChildDef();
//                if (childDef!=null)
//                    maxSize=Math.max(maxSize,childDef.size());
//                prepareSeries(serName,levelDef[i],(arg==null || arg.length<=i || arg[i]==null)?getDefaultSeriesArs():(JavaScriptObject)arg[i]);
//            }
//
//        }
//
//       boolean rv=false;//TODO Подпорка надо как то решить с заголовком для графиков, может быть вообще переписать этот метод!!!, поскольку у нас на каждом уровне теперь мно-во занчений
//       if (levelDef.length>0)
//           rv=updateTitleByVal(levelDef[0]);
//
//
//
//        if (!hasAxis("x"))
//        {
//            highlight(getDefaultHighlightArs());
//            addGrid();
//            addAxis("y",getDefaultYArgs());
//        }
//        addAxis("x",getDefaultXArgs(maxSize+1));
//        return rv;
//    }


    public boolean defaultPrepare4Draw(ValDef levelDef, JavaScriptObject arg)
    {
        Map<Object, ValDef> childDef = levelDef.getChildDef();
        if (childDef!=null)
        {
            if (!hasAxis("x"))
            {
                highlight(getDefaultHighlightArs());
                addGrid();
                addAxis("y",getDefaultYArgs());
            }
            addAxis("x",getDefaultXArgs(childDef.size()+1));
            if (arg==null)
                arg=getDefaultSeriesArs();
            return super.defaultPrepare4Draw(levelDef, arg);
        }
        return false;
    }
}
