package com.mycompany.client.test.DemoChart;

import com.mycompany.client.apps.App.AppConst;
import com.mycompany.client.apps.App.api.CreateChatView;
import com.mycompany.client.dojoChart.BaseDOJOChart;
import com.mycompany.client.dojoChart.CalcManager;
import com.mycompany.client.dojoChart.DonutChart;
import com.mycompany.client.operations.IOperation;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.types.DragAppearance;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.layout.HLayout;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 20.01.15
 * Time: 12:14
 * Проверка графиков
 */
public class ChartDemo2 implements Runnable
{
    /*
.dijitTooltipContainer {
	border: solid black 2px;
	background: #ffb5b5;
	color: green;
	font-size: small;
}

     */
    @Override
    public void run()
    {

        final HLayout mainLayout = new HLayout();
        mainLayout.setID(AppConst.t_MY_ROOT_PANEL);
        mainLayout.setShowEdges(false);
        mainLayout.setHeight100();
        mainLayout.setWidth100();
        mainLayout.setDragAppearance(DragAppearance.TARGET);


        //        final ClusteredColumns dojoChartPane = new ClusteredColumns(200,200);
//        final BaseDOJOChart dojoChartPane1 = new PieChart(200,200);
        final BaseDOJOChart dojoChartPane2 = new DonutChart(200,200);


        final CreateChatView operation = new CreateChatView(0, 0, "", IOperation.TypeOperation.addChart, TablesTypes.STATEDESC);


        final Map<Object,String> map = new HashMap<Object,String>();
        map.put(dojoChartPane2.getGraphId(),"Предупреждения");
//        map.put(dojoChartPane1.getGraphId(),"Предупреждения длительные");

        final CalcManager manger = new TestCalcManager();
//        final GridCtrl ctrl=operation.initManagerAndCtrl(manger);
//        ctrl.updateMeta(new DSCallback() {
//            @Override
//            public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest) {
//
//                CalcManager.DescExt descExt = manger.getIAnalisysDescExt();
//                Map<Integer, String> n2k = descExt.getNumber2Key();
//
//                for (Integer colix : n2k.keySet())
//                {
//                    if (colix==2 || colix==4)
//                    {
//                        ColDef colDef = ChartOptionsView.getColDef(n2k.get(colix), descExt.getDesc().getTupleDef());
//                        if (colix==2)
//                        {
//                            //createChart(dojoChartPane2, manager, mainLayout, map.get(dojoChartPane2.getGraphId()), new ColumnMeta(String.valueOf(colix), colDef), 50, 50);
//                            final Window chartWidgetWrapper = newWindow(map.get(dojoChartPane2.getGraphId()),  50, 50);
//                            chartWidgetWrapper.addItem(dojoChartPane2);
////TODO                            operation.initChartHandler(chartWidgetWrapper, dojoChartPane2, manger, map.get(dojoChartPane2.getGraphId()), new ColumnMeta(String.valueOf(colix), colDef));
//                            mainLayout.addChild(chartWidgetWrapper);
//
//                        }
//                        else
//                        {
//                        }
//                    }
//                }
//                ctrl.startUpdateData(true);
//            }
//        }); //Получение только метаданных
        mainLayout.draw();
    }


    private Window newWindow(String title, int left,int top)
    {
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
}
