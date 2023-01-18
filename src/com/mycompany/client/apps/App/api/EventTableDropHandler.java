package com.mycompany.client.apps.App.api;

import com.mycompany.client.ListGridWithDesc;
import com.mycompany.client.apps.OperationNode;
import com.mycompany.client.operations.IOperation;
import com.mycompany.client.operations.IOperationFactory;
import com.mycompany.client.operations.IOperationParam;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.util.EventHandler;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.DropEvent;
import com.smartgwt.client.widgets.events.DropHandler;
import com.smartgwt.client.widgets.tree.TreeGrid;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 13.12.14
 * Time: 17:33
 * Стандартный обработчик действий над таблицей событий
 */
public class EventTableDropHandler implements DropHandler {
    public ListGridWithDesc getNewGrid() {
        return newGrid;
    }

    private  final ListGridWithDesc newGrid;

    public EventTableDropHandler(ListGridWithDesc newGrid) {
        this.newGrid = newGrid;
    }

    public void onDrop(DropEvent event) {
        Canvas dragTarget = EventHandler.getDragTarget();
        if (dragTarget instanceof TreeGrid) {
            Record[] dragData = ((TreeGrid) dragTarget).getDragData();
            for (Record record : dragData) {
                Object operation1 = OperationNode.getOperation(record);
                IOperation operation = null;
                if (operation1 instanceof IOperation)
                    operation = (IOperation) operation1;
                else if (operation1 instanceof IOperationFactory) {
                    IOperationFactory factory = (IOperationFactory) operation1;
                    operation = factory.getOperation();
                }

                if (operation != null) {
                    switch (operation.getTypeOperation()) {
//                                                                 case addFilter:
//                                                                     if (newGrid.addFilter(operation))
//                                                                     {
//
//                                                                         HeaderControl pinUp = operation.createHeaderControl(newGrid, window);
//                                                                         if (pinUp != null)
//                                                                         {
//                                                                             LinkedList<Canvas> ll = new LinkedList<Canvas>(Arrays.asList(window.getHeader().getMembers()));
//                                                                             if (ll.size() >= 1)
//                                                                                 ll.add(1, pinUp);
//                                                                             else
//                                                                                 ll.add(pinUp);
//                                                                             window.getHeader().setMembers(ll.toArray(new Canvas[ll.size()]));
//                                                                         }
//                                                                         newGrid.applyClientFilters();
//                                                                     }
//                                                                 break;
//                                                                 case addServerFilter:

                        case addMenu: {//TODO Перенести все это в операцию!!!!!
                           /* final Window window = newGrid.getTarget();
                            final MyHeaderControl pinUp = new MyHeaderControl
                                    (
                                            //new HeaderControl.HeaderIcon("pred1.png"),
                                            HeaderControl.DOCUMENT,
                                            new com.smartgwt.client.widgets.events.ClickHandler() {
                                                public void onClick(ClickEvent event) {
                                                    String slg = null;


                                                    if (!newGrid.isGrouped()) {
                                                        ListGridRecord[] listGridRecords = newGrid.getRecords();
                                                        JavaScriptObject[] mps = new JavaScriptObject[listGridRecords.length];

                                                        for (int i = 0, listGridRecordsLength = listGridRecords.length; i < listGridRecordsLength; i++) {
                                                            ListGridRecord listGridRecord = listGridRecords[i];


                                                            mps[i] = listGridRecord.getJsObj();
                                                        }
                                                        slg = JSON.encode(JSOHelper.arrayConvert(mps));
                                                    } else
                                                    {
                                                        Tree t = newGrid.getGroupTree();

                                                        TreeNode[] tn = t.getAllNodes();

                                                        String topLevelGroup = newGrid.getGroupByFields()[0];
                                                        List<JavaScriptObject> al = new ArrayList<JavaScriptObject>();


                                                        for (int i = 0; i < tn.length; i++) {
                                                            if (tn[i].getAttributeAsBoolean("isFolder") && tn[i].getAttribute("groupName").equalsIgnoreCase(topLevelGroup))
                                                                al.add(tn[i].getJsObj());


                                                        }
                                                        slg = JSON.encode(JSOHelper.arrayConvert(al.toArray(new JavaScriptObject[al.size()])));


                                                    }


                                                    //Определяем порядок полей и исключаем скрытые


                                                    App01.GUI_STATE_DESC.getBuildTreeService().buildExcelReport_new(slg, ListGridDescriptor.buildDescriptor(newGrid),
                                                            new AsyncCallback<String>() {
                                                                @Override
                                                                public void onFailure(Throwable caught) {
                                                                    //To change body of implemented methods use File | Settings | File Templates.

                                                                    SC.warn("Файл не сформирован");
                                                                }

                                                                @Override
                                                                public void onSuccess(String result) {
                                                                    //TODO Открываем уже сформированный документ
                                                                    //                                       com.google.gwt.user.client.Window.open("XXXXX","_blank",null);
                                                                    String filename = result;
                                                                    if (filename == null) {
                                                                        SC.warn("Файл не сформирован");

                                                                    } else

                                                                    {


                                                                        //Делаем вызов в сервлет, который вернет содержимое
                                                                        String url = GWT.getHostPageBaseURL() + "transport/export?";
                                                                        url += "filename=" + filename + "&format=xls";
                                                                        // GWT.log(GWT.getModuleBaseURL());
                                                                        // GWT.log(GWT.getHostPageBaseURL());
                                                                        // GWT.log(GWT.getModuleBaseForStaticFiles());

                                                                        com.google.gwt.user.client.Window.open(url, null, null);

                                                                       *//* try {
                                                                            final RequestBuilder builder = new RequestBuilder(
                                                                                    RequestBuilder.GET, url);
                                                                            Request response = builder.sendRequest(null, new RequestCallback() {
                                                                                @Override
                                                                                public void onResponseReceived(Request request, Response response) {

                                                                                }

                                                                                @Override
                                                                                public void onError(Request request, Throwable exception) {

                                                                                }
                                                                            });
                                                                        } catch (Exception e) {


                                                                        }*//*

                                                                    }

                                                                }
                                                            }
                                                    );


                                                }
                                            });
                            pinUp.setGrid(newGrid);
                            pinUp.setOperation(null);
                            pinUp.setTarget(window);

                            pinUp.setTooltip("Excel");
                            pinUp.setCanDrag(true);
                            pinUp.setCanDrop(true);

                            if (window.isCreated())
                                NodesHolder.addHeaderCtrl(window, pinUp);*/


                        }
                        break;

                        case addClientFilter:
                        case addGISEventPositioning:
                        case addExcelExport:
                        case addChartByTable:
                        case extendGridByPlace:
                        case addStyleToTable:
                        {
//                                Window target = newGrid.getTarget();
//                                HeaderControl pinUp = operation.createHeaderControl(newGrid, target);
//                                NodesHolder.addHeaderCtrl(target, pinUp);

                            operation.operate(newGrid,null);
                            break;
                        }
                        case addData: {
                            if (operation instanceof IOperationParam) {
                                operation.operate(newGrid, null);

//                                    Window target = newGrid.getTarget();
//                                    List params = ((IOperationParam)operation).getParams();
//                                    Pair<String,String> pair = (Pair<String,String>) params.get(0);
//
//                                    IServerFilter serverFilter = newGrid.getServerDataFilter();
//
//                                    if (serverFilter==null)
//                                    {
//                                        newGrid.setServerDataFilter(serverFilter = new CommonServerFilter(TablesTypes.FILTERDATAEXPR));
//                                        HeaderControl pinUp = operation.createHeaderControl(newGrid, target);
//                                        NodesHolder.addHeaderCtrl(target, pinUp);
//                                    }
//
//                                    AdvancedCriteria cr = serverFilter.getCriteria();
//                                    Criterion[] criteria1 = cr.getCriteria();
//                                    if (criteria1 !=null && criteria1.length>0)
//                                       cr.appendToCriterionList(new Criterion(pair.first, OperatorId.EQUALS,pair.second));
//                                    else
//                                       cr.addCriteria(pair.first, OperatorId.EQUALS, pair.second);
//                                    cr.setOperator(OperatorId.OR);
//
////                                                                     { TODO а так мы будем сохранять фильтр в системе профилей пользователя
////                                                                         String js = cr.toJSON();
////                                                                         AdvancedCriteria ac =new AdvancedCriteria(JSOHelper.eval(js));
////                                                                         ac.toJSON();
////                                                                     }
//
//                                    IDataFlowCtrl ctrl = newGrid.getCtrl();
//                                    serverFilter.set2Criteria(ctrl.getCriteria());
//                                    ctrl.setFullDataUpdate();
//                                    ctrl.startUpdateData(true);
                            } else {
                                String message;
                                if (operation != null)
                                    message = "Can't apply operation " + operation.getViewName() + " with id " + operation.getOperationId() + " to add data";
                                else
                                    message = "Can't apply null operation to add data";

                                SC.say(message);
                            }

                        }

                        break;
                    }
                }

            }
        }
        event.cancel();
    }
}