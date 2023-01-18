package com.mycompany.client.dojoChart;

import com.google.gwt.core.client.JavaScriptObject;
import com.mycompany.client.CommonServerFilter;
import com.mycompany.client.IDataFlowCtrl;
import com.mycompany.client.IServerFilter;
import com.mycompany.client.ListGridWithDesc;
import com.mycompany.client.apps.App.api.CreateEventTable;
import com.mycompany.client.apps.App.api.IOperationContext;
import com.mycompany.client.operations.IOperation;
import com.mycompany.client.utils.PostponeOperationProvider;
import com.mycompany.common.FieldException;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.analit2.IAnalisysDesc;
import com.smartgwt.client.data.AdvancedCriteria;
import com.smartgwt.client.types.OperatorId;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.grid.ListGridField;


/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 03.02.15
 * Time: 11:37
 *
 */
public class CalcManager extends BaseCalcManager
{

    public void setIAnalisysDesc(IAnalisysDesc desc) throws FieldException
    {
        descExt=new DescExt(desc);
    }


    private DescExt descExt;

    public DescExt getIAnalisysDescExt() {
        return descExt;
    }


    public CalcManager(IChartLevelDef[] defIs) {
        super(defIs);
    }

    private ColumnMeta columnMeta;

    public class ChartWrapper extends BaseChartWrapper
    {
        public String getColName() {
            return columnMeta.getColId();
        }


        public ColumnMeta getColumnMeta() {
            return columnMeta;
        }

        ChartWrapper(BaseDOJOChart _chart, String title, ValDef currentNode,IOperationContext ctx)
        {
            super(CalcManager.this,_chart,title,currentNode,ctx);
        }

        protected void drawByCurrentNode()
        {
            ValDef newNode=currentNode;
            if (!newNode.getMetaDef().isGrouping())
            {

                String tableType= columnMeta.getTableType();
                if (tableType!=null)
                {
                    final IOperationContext ctx = (IOperationContext) this.ctx.copy();
                    Window wnd= (Window) ctx.getDst();
                    Canvas[] items = wnd.getItems();
                    for (Canvas item : items)
                        if (item instanceof ListGridWithDesc)
                            return;//TODO (Подпорка!!!!) Не допускать повторной инициализации уже проинициализированного грида

                    chart.setVisible(false);//TODO Да работает,
                    chart.set2Save(false);//TODO только надо как то устанавливать будем ли мы сохранять график или нет
//                    wnd.removeItem(chart);//Посмотреть возможно просто скрывать график, тогда он не будет перегружаться???

                    IOperation operation = new CreateEventTable(-1, -1, "", IOperation.TypeOperation.addEventPortlet, tableType);
                    operation.operate(null, ctx);

                    final ListGridWithDesc newGrid = (ListGridWithDesc) ctx.getChildList().get(0).getSrc();
                    wnd.setTitle(title+" "+currentNode.getFullViewText());

                    newGrid.setDescOperation(this.getChart().getDescOperation());

                    IServerFilter serverFilter;
                    newGrid.setServerDataFilter(serverFilter = new CommonServerFilter(TablesTypes.FILTERDATAEXPR));

                    AdvancedCriteria cr;
                    newNode.getParentDef().getMetaDef().appendToCriterionList(cr = new AdvancedCriteria(OperatorId.AND),newNode);


                    serverFilter.setCriteria(cr);//Установить фильтр сервера

                    final IDataFlowCtrl ctrl = newGrid.getCtrl();
                    serverFilter.set2Criteria(ctrl.getCriteria());

                    new PostponeOperationProvider(new PostponeOperationProvider.IPostponeOperation()
                    {
                        @Override
                        public boolean operate()
                        {
                            if (newGrid.isMetaWasSet())
                            {

                                final ListGridField[] allFields = newGrid.getAllFields();
                                if (allFields==null || allFields.length==0)
                                {
                                    Window wnd= (Window) ctx.getDst();
                                    wnd.removeItem(newGrid);
                                    wnd.setTitle(title);
                                    chart.setVisible(true);
                                    chart.set2Save(true);
//                                    wnd.addItem(chart);
                                    chart.drawDefaultChart(ChartWrapper.this.currentNode, null);
                                }
                                else
                                {
                                    ctrl.setFullDataUpdate();
                                    ctrl.startUpdateData(true);
                                }
                                return true;
                            }
                            return false;
                        }
                    });

                }
            }
            else
                super.drawByCurrentNode();
        }

        public boolean backLevel()
        {
            ValDef parentDef = currentNode.getParentDef();
            if (parentDef!=null)
            {
                Window wnd= (Window) ctx.getDst();
                Canvas[] cnvs=wnd.getItems();
                for (Canvas cnv : cnvs)
                    if (!(cnv instanceof BaseDOJOChart))
                    {
                        wnd.removeItem(cnv);//TODO Переделать это на что-то более вменяемое
                        cnv.markForDestroy();

                        wnd.setTitle(title);
                        chart.setVisible(true);

                        chart.set2Save(true);
//                        wnd.addItem(chart);
                        break;
                    }


                currentNode=parentDef;
//                chart.drawDefaultChart(currentNode, null); //TODO Это верно тольк во одном случае если и толко если у нас один не стандартный вьювер на конце
                drawByCurrentNode();
            }
            return currentNode.getParentDef()!=null;
        }



    }

    public ChartWrapper addChart(final BaseDOJOChart chart, String title, ColumnMeta columnMeta,final IOperationContext ctx)
    {
        return addChart(chart,title,columnMeta,null,ctx);
    }


    public ChartWrapper addChart(final BaseDOJOChart chart, String title, ColumnMeta columnMeta,ValDef currentNode,final IOperationContext ctx)
    {
        this.columnMeta = columnMeta;
        return (ChartWrapper)super.addChart(chart,title,currentNode,ctx);
    }

    @Override
    protected BaseChartWrapper createWrapper(BaseDOJOChart chart, String title, ValDef currentNode, IOperationContext ctx)
    {
        return new ChartWrapper(chart, title, currentNode,ctx);
    }


    @Override
    protected IGetRecordVal[] createCalcFunctions(BaseChartWrapper wrapper, String baseTitle, Object graphId)
    {

        ColumnMeta columnMeta = ((ChartWrapper) wrapper).getColumnMeta();


        if (baseTitle==null)
            baseTitle="";
        final String title=baseTitle;


        return new IGetRecordVal[]{new SumGetRecordVal(graphId,columnMeta,title," [$val] дороги ","red"),new SumGetRecordVal(graphId,columnMeta,title," [$val] ($) службы ","blue"),new SumGetRecordVal(graphId,columnMeta,title,"","green")
        {
            public JavaScriptObject getCharTitle(ValDef def)
            {

                JavaScriptObject rv=super.getCharTitle(def);
                JSOHelper.setAttribute(rv, "title", this.baseTitle + " [" + getViewVal(def) + "] (" + def.getParentDef().getViewText() + ") предприятия службы " + def.getViewText());
                return rv;
            }
        },new GetRecordValByColNameEx(graphId, columnMeta, title," [ таблица отсутствует]","orange")};
    }

}


