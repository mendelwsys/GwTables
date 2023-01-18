package com.mycompany.client.dojoChart;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayMixed;
import com.mycompany.client.utils.IListenerCtrl;
import com.mycompany.client.utils.JScriptUtils;
import com.mycompany.client.utils.ListenerCtrl;
import com.mycompany.client.utils.PostponeOperationProvider;
import com.mycompany.common.DescOperation;
import com.smartgwt.client.types.ContentsType;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.events.ResizedEvent;
import com.smartgwt.client.widgets.events.ResizedHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 21.01.15
 * Time: 18:48
 * Элемент управления графиком dojo (Вытащим из него потом интерфейс для )
 */
abstract public class BaseDOJOChart extends HTMLFlow
{
    private static int id=100;
    public static final String DEF_SERIES_NAME = "DEF_SERIES";
    protected int width;
    protected int height;

    public BaseDOJOChart(int width, int height)
    {
        this.width = width;
        this.height = height;

        setContentsType(ContentsType.PAGE);
        setContentsURL("chr2/tf4.jsp");

        setHandlers();

    }

    private boolean _2Save=true;
    public void set2Save(boolean _2Save)
    {
        this._2Save=_2Save;
    }

    public boolean get2Save()
    {
        return this._2Save;
    }


    public DescOperation getDescOperation()
    {
        return descOperation;
    }

    public void setDescOperation(DescOperation descOperation) {
        this.descOperation = descOperation;
    }

    protected DescOperation descOperation;

    protected void setHandlers()
    {
        addResizedHandler(new ResizedHandler() {
            @Override
            public void onResized(ResizedEvent event)
            {
                final int height = BaseDOJOChart.this.getInnerHeight();
                final int width = BaseDOJOChart.this.getInnerWidth();

                try
                {
//                    if (!BaseDOJOChart.this.wasInit())
//                    {
//                        SC.say("Resize is not effect: " + width+" "+height+" "+bindName);
//                    }


//                    new PostponeOperationProvider(new PostponeOperationProvider.IPostponeOperation()
//                    {
//                        @Override
//                        public boolean operate()
//                        {
                            BaseDOJOChart.this.reSizeChart(Math.max(20,width - 20), Math.max(20,height - 20));
//                            return true;
//                        }
//                    },300);


                }
                catch (Exception e)
                {
                    SC.say("Resize is not effect: " + width+" "+height+" "+bindName+" exception: "+e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    protected static String getNexID()
    {
        id++;
        return id+"_chartElement_"+id;
    }

    protected String bindName;
    protected String getBindName()
    {
        if (bindName==null)
            bindName = getNexID();
        return bindName;
    }

    public Object getGraphId()
    {
        return getBindName();
    }


    public void destroy()
    {
        unRegisterName(getBindName());
        super.destroy();
    }

    private static native void unRegisterName(String name) /*-{
        if ($wnd.binding && $wnd.binding[name])
            $wnd.binding[name]=undefined;
    }-*/;

    private static native void registerName(String name) /*-{
        if (!$wnd.binding)
            $wnd.binding=new Array();
        if (!$wnd.binding[name])
         $wnd.binding[name]=new Object();//Создать объект
}-*/;

   public void setContentsURL(String contentsURL) {
       final String bindName = getBindName();
       registerName(bindName);
       contentsURL+="?bindName="+bindName;
       super.setContentsURL(contentsURL);
   }

    protected JavaScriptObject getDefTitleObject()
    {
        JavaScriptObject title = getChartTitleObject();
        if (title==null)
        {
            title = JScriptUtils.s2j("{title:'',titleGap:20,titleFontColor:'orange'}");
            setChartTitleObject(title);
        }
        return title;
    }





    private IToolTipListener toolTipListener;

    public void setToolTipListener(IToolTipListener toolTipListener)
    {
            this.toolTipListener=toolTipListener;
    }

    public void removeToolTipListener()
    {
        this.toolTipListener=null;
    }


    protected ChartType chartType;
    public ChartType getChartType() {
        return chartType;
    }
    public void setChartType(ChartType chartType) {
        this.chartType = chartType;
    }
    private String _getStringChartType()
    {
        if (chartType!=null)
            return chartType.getValue();
        throw new RuntimeException("Can't create chart with undefined type");
    }


    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
    String theme;


    public IListenerCtrl<Integer> getListenerCtrl() {
        return listenerCtrl;
    }

    ListenerCtrl<Integer> listenerCtrl=new ListenerCtrl<Integer>();


    private void _clickCallBack(int index)
    {

        listenerCtrl.clickIndex(index);
//        for (IClickListener iClickListener : clickListener)
//            iClickListener.clickIndex(index);
    }


    private String _toolTipCallBack(JavaScriptObject val)
    {
        if (toolTipListener!=null)
            return toolTipListener.getToolTip(val);

        return JSOHelper.getAttribute(val,"tooltip");
    }


    private JavaScriptObject chartTitleObj;
    public JavaScriptObject getChartTitleObject()
    {
        return  chartTitleObj;
    }
    public void setChartTitleObject(JavaScriptObject chartTitleObj)
    {
        this.chartTitleObj=chartTitleObj;
    }
//TODO Переопределить
//    public boolean defaultPrepare4Draw(ValDef[] levelDef, Object[] arg)
//    {
//        return levelDef.length > 0 && defaultPrepare4Draw(levelDef[0], (arg != null && arg.length > 0 && arg[0] != null) ? (JavaScriptObject) arg[0] : null);
//    }

//    public void drawDefaultChart(ValDef[] levelDef, Object[] arg)
//    {
//        if (defaultPrepare4Draw(levelDef,arg))
//            fullRenderChart();
//        else
//            renderChart();
//    }


    public boolean defaultPrepare4Draw(ValDef levelDef, JavaScriptObject arg)
    {
        prepareSeries(DEF_SERIES_NAME,levelDef,arg);
        return updateTitleByVal(levelDef);

    }

//    protected String getFullTitle(ValDef levelDef)
//    {
//       JavaScriptObject titleObject=levelDef.getMetaDef().getChartTitle(getGraphId(),levelDef);
//       return JSOHelper.getAttribute(titleObject,"title");
//    }


    protected boolean updateTitleByVal(ValDef levelDef)
    {
        JavaScriptObject title=levelDef.getMetaDef().getChartTitle(getGraphId(),levelDef);
        JavaScriptObject currentTittle=this.getChartTitleObject();

        if  (
               currentTittle!=title
            )
        {
            this.updateTitle(title);
            return true;
        }
        return false;
    }

    public void drawDefaultChart(ValDef levelDef, JavaScriptObject arg)
    {
        if (!wasLoad())
            return;

        boolean wasInit=wasInit();
        final boolean needFullRender = defaultPrepare4Draw(levelDef, arg);
        if (needFullRender)
        {
            if (!wasInit)
                reSizeChart();
            fullRenderChart();
        }
        else
        {
            if (!wasInit)
                reSizeChart();
//            else
            renderChart();
        }
    }


    public void prepareSeries(String serName,ValDef levelDef,JavaScriptObject arg)
    {
        if (!hasSeries(serName))
            addSeries(serName, levelDef,arg);
        else
        {
//            JavaScriptObject[] arrs = JSOHelper.toArray(JSOHelper.getAttributeAsJavaScriptObject(getThemeObject(), "seriesThemes"));
//            int ixTheam=1;
//            int ixColor=4;
//            String color=JSOHelper.getAttribute(JSOHelper.getAttributeAsJavaScriptObjectArray(JSOHelper.getAttributeAsJavaScriptObject(arrs[ixColor], "fill"), "colors")[ixTheam],"color");

            resetTheme(0);
            updateSeries(serName, levelDef, arg);
        }
    }

    Map<Integer,Object> index2key=new HashMap<Integer,Object>();

    public Object getKeyByIndex(int index)
    {
        return index2key.get(index);
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
            ValDef level=levels.get(o);
            IFormatValue formatValue= level.getFormatValue(getBindName());
            if (formatValue==null)
                continue;
                //throw new RuntimeException("Can't define type of value");

            final Value value = level.getValue(getBindName());
            final Object o1 = formatValue.presentationValue(value);
            if (!formatValue.isPresentableValue(value)) continue;
            numbers.push((Double)o1);

//            final Double value = level.getValue(getBindName());
//            if (value==null)
//                continue;
//
//            ColumnMeta formatValue= level.getFormatValue(getBindName());
//            if (formatValue==null)
//                throw new RuntimeException("Can't define type of value");
//
//            if (ListGridFieldType.INTEGER.getValue().equals(formatValue.getFtype()))
//                numbers.push((int) Math.round(value));
//            else
//                numbers.push(value);

            ix++;
        }
        return numbers;
    }


    public void addSeries(String serName,ValDef levelDef,JavaScriptObject kwArgs)
    {
        _addSeries(serName, prepareData(levelDef),kwArgs);
    }

    public void updateSeries(String serName,ValDef levelDef,JavaScriptObject kwArgs)
    {
        _updateSeries(serName, prepareData(levelDef),kwArgs);
    }

    public boolean reSizeChart()
    {
        return reSizeChart(width,height);
    }




    public native boolean reSizeChart(int width,int height) /*-{

        this.@com.mycompany.client.dojoChart.BaseDOJOChart::width=width;
        this.@com.mycompany.client.dojoChart.BaseDOJOChart::height=height;
        var bindObject=this.@com.mycompany.client.dojoChart.BaseDOJOChart::_getBindObject()();
        if (bindObject && bindObject.chart)
        {
            bindObject.chart.resize(width,height);
            return true;
        }
        else
            return false;
    }-*/;

    protected  native JavaScriptObject _getBindObject()
    /*-{

        var name=this.@com.mycompany.client.dojoChart.BaseDOJOChart::getBindName()();
        if ($wnd.binding && $wnd.binding[name])
            return $wnd.binding[name];
        return null;
    }-*/;

    protected native JavaScriptObject _getChartObject()
    /*-{
        var bindObject=this.@com.mycompany.client.dojoChart.BaseDOJOChart::_getBindObject()();
        if (bindObject)
        {
            if (!bindObject.chart)
                bindObject=this.@com.mycompany.client.dojoChart.BaseDOJOChart::_initBindObject(Lcom/google/gwt/core/client/JavaScriptObject;)(bindObject);
            return bindObject.chart;
        }
    }-*/;


    public native void fullRenderChart()/*-{
        var chart=this.@com.mycompany.client.dojoChart.BaseDOJOChart::_getChartObject()();
        if (chart)
            chart.fullRender();
    }-*/;

    public native void renderChart()/*-{
        var chart=this.@com.mycompany.client.dojoChart.BaseDOJOChart::_getChartObject()();
        if (chart)
            chart.render()
    }-*/;


    public native void updateTitle(JavaScriptObject titleObj) /*-{
        this.@com.mycompany.client.dojoChart.BaseDOJOChart::setChartTitleObject(Lcom/google/gwt/core/client/JavaScriptObject;)(titleObj);
        var chart=this.@com.mycompany.client.dojoChart.BaseDOJOChart::_getChartObject()();
        if (chart && titleObj)
        {
            if (titleObj.title)
                chart.title=titleObj.title;
            if (titleObj.titleFontColor)
                 chart.titleFontColor=titleObj.titleFontColor;
            if (titleObj.titleGap)
                 chart.titleGap=titleObj.titleGap;
            if (titleObj.titleFont)
                 chart.titleFont=titleObj.titleFont;
            if (titleObj.titlePos)
                chart.titlePos=titleObj.titlePos;
        }

    }-*/;



    protected native void _updateSeries(String serName,JsArrayMixed chartData,JavaScriptObject kwArgs)/*-{
        if (chartData)
        {
            var chart=this.@com.mycompany.client.dojoChart.BaseDOJOChart::_getChartObject()();
            if (chart)
            {
                chart.updateSeries(serName, {data:chartData});//Add the series of data
                if (chart.sliceRef) //TODO !!!Грязный ХАК!!!!
                    chart.sliceRef.angles=null;
            }
        }
    }-*/;

    protected native void _addSeries(String serName,JsArrayMixed chartData,JavaScriptObject kwArgs)/*-{
        if (chartData)
        {
            var chart=this.@com.mycompany.client.dojoChart.BaseDOJOChart::_getChartObject()();
            if (chart)
                chart.addSeries(serName, {data:chartData},kwArgs);//Add the series of data
        }
    }-*/;


    public native void addGrid()/*-{
            var chart=this.@com.mycompany.client.dojoChart.BaseDOJOChart::_getChartObject()();
            if (chart)
                chart.addPlot("grid", {type: "Grid"});
    }-*/;

    public native void highlight(JavaScriptObject kwArgs)/*-{

            var chart=this.@com.mycompany.client.dojoChart.BaseDOJOChart::_getChartObject()();
            if (chart)
            {
                var bindObject=this.@com.mycompany.client.dojoChart.BaseDOJOChart::_getBindObject()();
                new bindObject.Highlight(chart,"default",kwArgs);
            }
    }-*/;


    public  boolean hasAxis(String name)
    {
        return getAxis(name)!=null;
    }

    public native JavaScriptObject getAxis(String name)/*-{
            var chart=this.@com.mycompany.client.dojoChart.BaseDOJOChart::_getChartObject()();
            if (chart)
                return chart.getAxis(name);
            return null;
    }-*/;


    public native void addAxis(String name,JavaScriptObject kwArgs)/*-{
            var chart=this.@com.mycompany.client.dojoChart.BaseDOJOChart::_getChartObject()();
            if (chart)
                chart.addAxis(name, kwArgs);
    }-*/;


    public  boolean hasSeries(String serName)
    {

//        JavaScriptObject bindObject = _getBindObject();
//        Object chart = JSOHelper.getAttributeAsObject(bindObject, "chart");
//        if (chart==null)
//            _initBindObject(bindObject);
        return getSeries(serName)!=null;
    }

    public native JavaScriptObject getThemeObject()/*-{
        var chart=this.@com.mycompany.client.dojoChart.BaseDOJOChart::_getChartObject()();
        if (chart)
            return chart.theme;
        return null;
    }-*/;

    public native void resetTheme(int startColor)/*-{
        var chart=this.@com.mycompany.client.dojoChart.BaseDOJOChart::_getChartObject()();
        if (chart)
            chart.theme._current=startColor;
    }-*/;


    public native JavaScriptObject getSeries(String serName)/*-{
        var chart=this.@com.mycompany.client.dojoChart.BaseDOJOChart::_getChartObject()();
        if (chart)
            return chart.getSeries(serName);
        return null;
    }-*/;


    public native boolean wasInit()/*-{

        var bindObject=this.@com.mycompany.client.dojoChart.BaseDOJOChart::_getBindObject()();
        if (bindObject  && bindObject.wasInit)
            return true;
        else
            return false;

    }-*/;

    public native boolean wasLoad()/*-{

        var bindObject=this.@com.mycompany.client.dojoChart.BaseDOJOChart::_getBindObject()();
        if (bindObject && bindObject.wasLoad)
            return true;
        else
            return false;
    }-*/;


    protected native JavaScriptObject  _load(String libName)/*-{

        var bindObject=this.@com.mycompany.client.dojoChart.BaseDOJOChart::_getBindObject()();
        if (bindObject)
        {
            bindObject.libRequire(libName);
            return bindObject.lang.getObject(libName);
        }
        return null;
    }-*/;

//    protected JavaScriptObject  _load(String libName,JavaScriptObject bindObject)
//    {
//        return __load(libName,bindObject);
//    }

    protected native JavaScriptObject  _load(String libName,JavaScriptObject bindObject)/*-{

        if (bindObject)
        {
            bindObject.libRequire(libName);
            return bindObject.lang.getObject(libName);
        }
        return null;
    }-*/;

    abstract protected JavaScriptObject getChartPlotKwArs();


    protected native JavaScriptObject  _initBindObject(JavaScriptObject bindObject)/*-{

        if (bindObject && !bindObject.wasInit)
        {
            var javaObj=this;
            var chartType = javaObj.@com.mycompany.client.dojoChart.BaseDOJOChart::_getStringChartType()();
            if (!bindObject.chartTypes[chartType])
                bindObject.chartTypes[chartType]=javaObj.@com.mycompany.client.dojoChart.BaseDOJOChart::_load(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)("dojox.charting.plot2d." + chartType,bindObject);

            var theme = bindObject.defaultTheme;
            var themeName = javaObj.@com.mycompany.client.dojoChart.BaseDOJOChart::getTheme()();
            if (themeName)
            {
                var resTheme = javaObj.@com.mycompany.client.dojoChart.BaseDOJOChart::_load(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)("dojox.charting.themes." + themeName,bindObject);
                if (resTheme)
                    theme = resTheme;
            }

            var chart = bindObject.chart = new bindObject.Chart("chartNode", javaObj.@com.mycompany.client.dojoChart.BaseDOJOChart::getDefTitleObject()());
            chart.setTheme(theme);

            var kwArgs=javaObj.@com.mycompany.client.dojoChart.BaseDOJOChart::getChartPlotKwArs()();
            // Add the only/default plot
            kwArgs.type=bindObject.chartTypes[chartType]; // our type value
            kwArgs.tooltipFunc=function(o) {
                            return javaObj.@com.mycompany.client.dojoChart.BaseDOJOChart::_toolTipCallBack(Lcom/google/gwt/core/client/JavaScriptObject;)(o);
                        };
            chart.addPlot("default",kwArgs);

            // Add the only/default plot
//                chart.addPlot("default",
//                        {
//                            type: bindObject[chartType], // our type value
//                            radius: 200,
//                            fontColor: "black",
//                            labelOffset: -20,
//                            tooltipFunc: function(o) {
//                                return javaObj.@com.mycompany.client.dojoChart.BaseDOJOChart::_toolTipCallBack(Ljava/lang/String;)('' + o.y);
//                            }
//                        }
//                );


            chart.connectToPlot("default", function(evt) {
                // Get access to the shape and type
                var shape = evt.shape, type = evt.type;
                // React to mouseover event
                if (type == "onclick") {
                    if (evt.index >= 0) //$wnd.alert()
                        javaObj.@com.mycompany.client.dojoChart.BaseDOJOChart::_clickCallBack(I)(evt.index);
                }
            });


            var width = javaObj.@com.mycompany.client.dojoChart.BaseDOJOChart::width;
            var height = javaObj.@com.mycompany.client.dojoChart.BaseDOJOChart::height;
            chart.resize(width, height);
            // Create the tooltip
            new bindObject.Tooltip(chart, "default");
            // Animate the donut slices
            chart.sliceRef=new bindObject.MoveSlice(chart, "default");//TODO Сделать опционально при размещении объекта


            bindObject.wasInit = true;
        }
        else //К этому времени должно быть готово, поскольку мы инициализируем все из класса, а вот загрузится оно может и не успеть...
        {
            $wnd.alert("bind object is null");
        }
        return bindObject;

    }-*/;

    private native boolean _renderPieDonutChart(JsArray chartData) /*-{

        var javaObj=this;
        var bindObject=javaObj.@com.mycompany.client.dojoChart.BaseDOJOChart::_getBindObject()();
        if (bindObject)
        {

            var chart=bindObject.chart;

            if (!bindObject.wasInit)
            {
                var chartType=javaObj.@com.mycompany.client.dojoChart.BaseDOJOChart::_getStringChartType()();
                if (!bindObject.chartTypes[chartType])
                {
                  bindObject.libRequire("dojox.charting.plot2d." + chartType);
                  bindObject.chartTypes[chartType]=bindObject.lang.getObject("dojox.charting.plot2d." + chartType);
                }

                var themeName=javaObj.@com.mycompany.client.dojoChart.BaseDOJOChart::getTheme()();
                var theme=bindObject.defaultTheme;

                if (themeName)
                {
                    bindObject.libRequire("dojox.charting.themes."+themeName);
                    var resTheme = bindObject.lang.getObject('dojox.charting.themes.'+themeName);
                    if (!resTheme)
                        theme=resTheme;
                }

                chart=bindObject.chart=new bindObject.Chart("chartNode",javaObj.@com.mycompany.client.dojoChart.BaseDOJOChart::getDefTitleObject()());
                chart.setTheme(theme);

                // Add the only/default plot
                chart.addPlot("default",
                {
                    type: chartType, // our plot2d/Pie module reference as type value
                    radius: 200,
                    fontColor: "black",
                    labelOffset: -20,
                    tooltipFunc: function(o)
                    {
                        return javaObj.@com.mycompany.client.dojoChart.BaseDOJOChart::_toolTipCallBack(Lcom/google/gwt/core/client/JavaScriptObject;)(o);
                    }
                });

                //Add the series of data
                chart.addSeries("J", {data:chartData});

                chart.connectToPlot("default",function(evt)
                {
                    // Get access to the shape and type
                    var shape = evt.shape, type = evt.type;
                    // React to mouseover event
                    if(type == "onclick")
                    {
                        if (evt.index>=0) //$wnd.alert()
                            javaObj.@com.mycompany.client.dojoChart.BaseDOJOChart::_clickCallBack(I)(evt.index);
                    }
                });


                var width = javaObj.@com.mycompany.client.dojoChart.BaseDOJOChart::width;
                var height = javaObj.@com.mycompany.client.dojoChart.BaseDOJOChart::height;
                chart.resize(width,height);

                // Create the tooltip
                new bindObject.Tooltip(chart,"default");
                // Animate the donut slices
                new bindObject.MoveSlice(chart,"default");

                // Render the chart!
                chart.render();
//                var legend3 = new bindObject.Legend({chart: chart, horizontal: false}, "legend3");
                bindObject.wasInit=true;
                return true;
            }
            else
            {
                chart.updateSeries("J", {data:chartData});
                chart.render();
            }
        }
        else
        {
            $wnd.alert("bind object is null");
        }
        return false;
     }-*/;
//    public boolean testRenderChart(ChartLevelDef levelDef)
//    {
//        JsArray numbers = prepareData(levelDef);
//        return _renderPieDonutChart(numbers);
//    }

}
