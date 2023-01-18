package com.mycompany.client.test.DemoChart;

import com.mycompany.client.GridCtrl;
import com.mycompany.client.IDataFlowCtrl;
import com.mycompany.client.ListGridWithDesc;
import com.mycompany.client.apps.App.AppConst;
import com.mycompany.client.dojoChart.*;
import com.mycompany.client.updaters.BGridConstructor;
import com.mycompany.client.utils.GridMetaProviderBase;
import com.mycompany.client.utils.MyDSCallback;
import com.mycompany.client.utils.PostponeOperationProvider;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.analit2.*;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.types.DragAppearance;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.layout.HLayout;


/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 02.03.15
 * Time: 18:09
 * TODO Для удаления от 04.03.2015
 */
public class ChartDemo3_BU implements Runnable
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



    protected CalcManager manager = new CalcManager(
            new IChartLevelDef[]
            {
                    new ChartLevelDef(TablesTypes.KEY_FNAME,TablesTypes.DOR_CODE, TablesTypes.DOR_NAME),
                    new ChartLevelDef(TablesTypes.KEY_FNAME,TablesTypes.PRED_ID, TablesTypes.PRED_NAME),
                    new ChartLevelDef(TablesTypes.KEY_FNAME,TablesTypes.KEY_FNAME, TablesTypes.PRED_NAME),
                    new ChartLevelDef(TablesTypes.KEY_FNAME,null, null) //Группировок нет вернуть значение указанного поля
            })
    {
        protected IGetRecordVal[] createCalcFunctions(BaseChartWrapper wrapper, String baseTitle, Object graphId)
        {

            ColumnMeta columnMeta = ((ChartWrapper) wrapper).getColumnMeta();

            if (baseTitle==null)
                baseTitle="";
            final String title=baseTitle;

            return new IGetRecordVal[]{
                    new SumGetRecordVal(graphId,columnMeta,title,"","red"),
                    new SumGetRecordVal(graphId,columnMeta,title,"","green"),
                    new SumGetRecordVal(graphId,columnMeta,title,"","yellow"),
                    new GetConstRecordVal(graphId, columnMeta, title," [ таблица отсутствует]","orange",1)};
        }
    };


    void initGraph(final Window wnd)
    {

        String tblType=TablesTypes.VIOLATIONS;



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
                    BaseDOJOChart chart = new IChartFactoryImpl().createChart(ChartType.Pie, 200, 200);
                    final ColumnMeta columnMeta = new ColumnMeta(criteria.getAttribute(TablesTypes.TTYPE),"PRED_ID",new ColDef("PRED_ID","PRED_ID",false,null, ListGridFieldType.INTEGER.getValue()));
                    manager.addChart(chart, "TEST", columnMeta, null);

                    ctrl.startUpdateData(true);
                    wnd.addItem(chart);

                    return true;
                }
                return false;
            }
        });


    }

}
