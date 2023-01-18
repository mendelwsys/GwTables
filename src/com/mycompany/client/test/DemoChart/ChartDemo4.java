package com.mycompany.client.test.DemoChart;

import com.google.gwt.event.shared.HandlerRegistration;
import com.mycompany.client.GridCtrl;
import com.mycompany.client.IDataFlowCtrl;
import com.mycompany.client.ListGridWithDesc;
import com.mycompany.client.apps.App.AppConst;
import com.mycompany.client.apps.App.NodesHolder;
import com.mycompany.client.apps.App.api.IOperationContext;
import com.mycompany.client.dojoChart.*;
import com.mycompany.client.operations.OperationCtx;
import com.mycompany.client.updaters.BGridConstructor;
import com.mycompany.client.utils.GridMetaProviderBase;
import com.mycompany.client.utils.IClickListener;
import com.mycompany.client.utils.MyDSCallback;
import com.mycompany.client.utils.PostponeOperationProvider;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.types.DragAppearance;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.HeaderControl;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.DrawEvent;
import com.smartgwt.client.widgets.events.DrawHandler;
import com.smartgwt.client.widgets.layout.HLayout;


/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 02.03.15
 * Time: 18:09
 */
public class ChartDemo4 implements Runnable
{


    public String getDataURL() {
        return "transport/tdata2";
    }

    public String getHeaderURL() {
        return "theadDesc.jsp";
    }

    @Override
    public void run() {

        final HLayout mainLayout = new HLayout();
        mainLayout.setID(AppConst.t_MY_ROOT_PANEL);
        mainLayout.setShowEdges(false);
        mainLayout.setHeight100();
        mainLayout.setWidth100();
        mainLayout.setDragAppearance(DragAppearance.TARGET);

        Window wnd = newWindow("ТЕСТ", 50, 50);
        initGraph(wnd);
        mainLayout.addChild(wnd);

        mainLayout.draw();
    }


    private Window newWindow(String title, int left, int top) {
        final Window window = new Window();
        window.setOverflow(Overflow.HIDDEN);
        window.setTitle(title);
        window.setWidth(900);
        window.setHeight(600);
        window.setLeft(left);
        window.setTop(top);
        window.setCanDragReposition(true);
        window.setCanDragResize(true);

        window.setMinWidth(50);
        window.setMinHeight(50);
        return window;
    }



    protected BaseCalcManager manager = new BaseCalcManager
            (
                new IChartLevelDef[]
                {
                        new ChartLevelDef(TablesTypes.KEY_FNAME,TablesTypes.DOR_CODE, TablesTypes.DOR_NAME),
                        new ChartLevelDef(TablesTypes.KEY_FNAME,TablesTypes.VID_ID, TablesTypes.VID_NAME),
                        new MapLevelDef(TablesTypes.KEY_FNAME,TablesTypes.KEY_FNAME, TablesTypes.PRED_NAME,SpeedFilters.getSpeedFilters()),

                        new ChartLevelDef(TablesTypes.KEY_FNAME,TablesTypes.KEY_FNAME, TablesTypes.PRED_NAME),
                        new ChartLevelDef(TablesTypes.KEY_FNAME,null, null) //Группировок нет вернуть значение функции
                }
            )
    {
        protected IGetRecordVal[] createCalcFunctions(BaseChartWrapper wrapper, String baseTitle, Object graphId)
        {

            IFormatValue formatValue = new IFormatValue() {

                @Override
                public boolean isPresentableValue(Value value)
                {
                    Object obj=value.getMean();
                    return (obj!=null && ((Double)obj)>0);
                }

                @Override
                public Object presentationValue(Value value)
                {
                    final Double mean = (Double) value.getMean();
                    if (mean!=null)
                        return mean.intValue();
                    else
                        return 0;
                }

                @Override
                public String formatValue(Value value) {
                    return String.valueOf(presentationValue(value));
                }
            };

            if (baseTitle==null)
                baseTitle="";
            final String title=baseTitle;

            return new IGetRecordVal[]
            {
                    new SumGetRecordVal(graphId,formatValue,title,"","red"),
                    new SumGetRecordVal(graphId,formatValue,title,"","blue"),
                    new SumGetRecordVal(graphId,formatValue,title,"","green"),

                    new SumGetRecordVal(graphId,formatValue,title,"","yellow"),
                    new GetConstRecordVal(graphId, formatValue, title," [ таблица отсутствует]","orange",1)};
            }
    };


    void initGraph(final Window chartWidgetWrapper)
    {

        String tblType=TablesTypes.WARNINGS;



        final Criteria criteria = new Criteria(TablesTypes.TTYPE, tblType);

        GridMetaProviderBase gridMetaProvider= new GridMetaProviderBase();

        final String headerURL = getHeaderURL();
        final String dataURL = getDataURL();

        final ListGridWithDesc gridWithDesc = new ListGridWithDesc();
        final BGridConstructor gridConstructor = new BGridConstructor(gridWithDesc);
        gridConstructor.setAddIdDataSource("$" + criteria.getAttribute(TablesTypes.TTYPE));

        DSCallback metaDataUpdater = gridMetaProvider.initMetaDataUpdater(gridConstructor, headerURL, dataURL);
        final WidgetUpdater widgetUpdater = new WidgetUpdater();
        MyDSCallback dataCallBack = widgetUpdater.initDataUpdater(manager, IDataFlowCtrl.DEF_DELAY_DATA_MILLIS);
        final GridCtrl ctrl = new GridCtrl(gridConstructor.getAddIdDataSource(), new Pair<DSCallback,MyDSCallback>(metaDataUpdater,dataCallBack), criteria, headerURL, dataURL);
        widgetUpdater.registerDataSource(dataURL,false,gridConstructor.getAddIdDataSource());

        ctrl.updateMeta(null);


        new PostponeOperationProvider(
                new PostponeOperationProvider.IPostponeOperation()
        {
            @Override
            public boolean operate()
            {
                if (gridWithDesc.isMetaWasSet())
                {
                    //Здесь создаем диалог и заполняем исходя из данных
                    final BaseDOJOChart dojoChartPane = new IChartFactoryImpl().createChart(ChartType.Pie, 200, 200);

                    final HeaderControl[] backLevel=new HeaderControl[1];
                    IOperationContext ctx = new OperationCtx(null, chartWidgetWrapper);

                    BaseChartWrapper wrapper = manager.addChart(dojoChartPane, "TEST", ctx);
                    wrapper.addIndexListener(new IClickListener<BaseChartWrapper>() {
                        @Override
                        public void clickIndex(BaseChartWrapper wrapper)
                        {
                            backLevel[0].setDisabled(!manager.hasBackLevel(dojoChartPane.getGraphId()));
                        }
                    });
                    initHandlers(backLevel,chartWidgetWrapper, dojoChartPane);
                    chartWidgetWrapper.addItem(dojoChartPane);

                    ctrl.startUpdateData(true);

                    return true;
                }
                return false;
            }
        });


    }



    private HeaderControl[] initHandlers(final HeaderControl[] backLevel,final Window chartWidgetWrapper, final BaseDOJOChart dojoChartPane)
    {


    backLevel[0] = new HeaderControl(
            HeaderControl.DOUBLE_ARROW_UP,
            new ClickHandler() {

                @Override
                public void onClick(ClickEvent event)
                {
                    backLevel[0].setDisabled(!manager.backLevel(dojoChartPane.getGraphId()));
                }
            });
    backLevel[0].setDisabled(true);

    if (chartWidgetWrapper.isCreated())
        NodesHolder.addHeaderCtrl(chartWidgetWrapper, backLevel);
     else
    {
        final HandlerRegistration[] rs = new HandlerRegistration[1];
        rs[0] = chartWidgetWrapper.addDrawHandler(new DrawHandler() {
            @Override
            public void onDraw(DrawEvent event) {
                NodesHolder.addHeaderCtrl(chartWidgetWrapper, backLevel);
                rs[0].removeHandler();
            }
        });
    }
    return backLevel;
}

}
