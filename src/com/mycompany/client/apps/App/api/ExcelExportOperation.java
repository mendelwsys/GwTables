package com.mycompany.client.apps.App.api;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.mycompany.client.ListGridWithDesc;
import com.mycompany.client.MyHeaderControl;
import com.mycompany.client.ReportListGridWithDesc;
import com.mycompany.client.apps.App.App01;
import com.mycompany.client.apps.App.NodesHolder;
import com.mycompany.client.apps.SimpleOperationP;
import com.mycompany.client.operations.IOperation;
import com.mycompany.client.utils.PostponeOperationProvider;
import com.mycompany.common.DescOperation;
import com.mycompany.common.ListGridDescriptor;
import com.mycompany.common.analit2.ColDef;
import com.mycompany.common.analit2.GrpDef;
import com.mycompany.common.analit2.NNode2;
import com.mycompany.common.tables.CommonGridUtils;
import com.mycompany.common.tables.HeaderSpanMimic;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.util.JSON;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HeaderControl;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.DrawEvent;
import com.smartgwt.client.widgets.events.DrawHandler;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeNode;

import java.util.*;

/**
 * Created by Anton.Pozdnev on 03.04.2015.
 */
public class ExcelExportOperation extends SimpleOperationP {





    public ExcelExportOperation() {
        super(-1, -1, null);

    }


    public ExcelExportOperation(int operationId, int parentOperationId, String viewName) {
        super(operationId, parentOperationId, viewName);

    }

    public ExcelExportOperation(int operationId, int parentOperationId, String viewName, TypeOperation type) {
        super(operationId, parentOperationId, viewName, type);

    }

    @Override
    public DescOperation getDescOperation(DescOperation descOperation) {
        descOperation = super.getDescOperation(descOperation);
        descOperation.apiName = ExcelExportOperation.class.getName();

        return descOperation;
    }

    @Override
    public boolean isViewAsFolder() {
        return super.isViewAsFolder();
    }

    @Override
    public void setViewAsFolder(boolean viewAsFolder) {
        super.setViewAsFolder(viewAsFolder);
    }

    @Override
    public String getFolderName() {
        return super.getFolderName();
    }

    @Override
    public void setFolderName(String folderName) {
        super.setFolderName(folderName);
    }

    @Override
    public int getParentOperationId() {
        return super.getParentOperationId();
    }

    @Override
    public void setParentOperationId(int parentOperationId) {
        super.setParentOperationId(parentOperationId);
    }

    @Override
    public void setOperationId(int operationId) {
        super.setOperationId(operationId);
    }

    @Override
    public int getOperationId() {
        return super.getOperationId();
    }

    @Override
    public String getViewName() {
        return super.getViewName();
    }

    @Override
    public void setViewName(String viewName) {
        super.setViewName(viewName);
    }

    @Override
    public TypeOperation getTypeOperation() {
        return super.getTypeOperation();
    }

    @Override
    public void setTypeOperation(TypeOperation type) {
        super.setTypeOperation(type);
    }


    // ListGridWithDesc newGrid = null;

    @Override
    public Canvas operate(Canvas dragTarget, IOperationContext ctx) {
        if (dragTarget instanceof Window) {
            Canvas[] items = ((Window) dragTarget).getItems();
            if (items != null && items.length > 0)
                return operate(items[0], null);
        } else if (dragTarget instanceof ListGridWithDesc) {


            final ListGridWithDesc newGrid = (ListGridWithDesc) dragTarget;

            if (newGrid.isMetaWasSet()) {
                List<DescOperation> subOperation = newGrid.getDescOperation().getSubOperation();
                int ix = findIndexInDesc(subOperation);
                if (ix >= 0)
                    return null;
            }

            final Window target = newGrid.getTarget();

            final HeaderControl pinUp = createHeaderControl(newGrid, target);

            if (target.isDrawn())
                NodesHolder.addHeaderCtrl(target, pinUp);
            else {
                final HandlerRegistration[] handlerRegistrations = new HandlerRegistration[1];
                handlerRegistrations[0] = target.addDrawHandler(new DrawHandler() {
                    @Override
                    public void onDraw(DrawEvent event) {
                        NodesHolder.addHeaderCtrl(target, pinUp);
                        handlerRegistrations[0].removeHandler();//удалить хандлер после обработки
                    }
                });
            }


            if (newGrid.isMetaWasSet())
                setDescOperation(newGrid);
            else
                new PostponeOperationProvider(new PostponeOperationProvider.IPostponeOperation() {

                    @Override
                    public boolean operate() {
                        if (newGrid.isMetaWasSet()) {
                            setDescOperation(newGrid);
                            return true;
                        }
                        return false;
                    }
                });


        }


        return super.operate(dragTarget, ctx);
    }

//    private void setDescOperation(ListGridWithDesc newGrid) {
//        List<DescOperation> subOperation = newGrid.getDescOperation().getSubOperation();
//        subOperation.add(this.getDescOperation(new DescOperation()));
//    }

    @Override
    public Canvas getInputFrom(Canvas warnGrid) {
        return super.getInputFrom(warnGrid);
    }

    @Override
    public HeaderControl createHeaderControl(final Canvas canvas, Window target)
    {
        final MyHeaderControl[] pinUp= new MyHeaderControl[1];
        pinUp[0] = new MyHeaderControl
                (
                        //new HeaderControl.HeaderIcon("pred1.png"),
                        HeaderControl.DOCUMENT,
                        new com.smartgwt.client.widgets.events.ClickHandler() {
                            public void onClick(ClickEvent event) {
                                String slg = null;
                                String sStyles = null;
                                Map<String, String> m = new HashMap<String, String>();
                                Map<String, String> styles = new HashMap<String, String>();
                                final Canvas canvas1 = pinUp[0].getGrid();
                                int columns = ((ListGridWithDesc) canvas1).getFields().length;
                                if (!((ListGridWithDesc) canvas1).isGrouped()) {
                                    ListGridRecord[] listGridRecords = ((ListGridWithDesc) canvas1).getRecords();
                                    JavaScriptObject[] mps = new JavaScriptObject[listGridRecords.length];

                                    for (int i = 0, listGridRecordsLength = listGridRecords.length; i < listGridRecordsLength; i++) {

                                        ListGridRecord listGridRecord = listGridRecords[i];
                                        for (int j = 0; j < columns; j++) {
                                            String style = ((ListGridWithDesc) canvas1).getCellCSSText(listGridRecord, i, j);
                                            String savedStyleAdress = styles.get(style);
                                            if (savedStyleAdress != null) {
                                                m.put("" + i + "_" + j, "#!#" + savedStyleAdress);


                                            } else {
                                                styles.put(style, "" + i + "_" + j);
                                                m.put("" + i + "_" + j, style);
                                            }
                                        }
                                        mps[i] = listGridRecord.getJsObj();
                                    }
                                    styles.clear();
                                    sStyles = JSON.encode(JSOHelper.convertMapToJavascriptObject(m));
                                    slg = JSON.encode(JSOHelper.arrayConvert(mps));
                                    m.clear();
                                } else {
                                    Tree t = ((ListGridWithDesc) canvas1).getGroupTree();

                                    TreeNode[] tn = t.getAllNodes();

                                    String topLevelGroup = ((ListGridWithDesc) canvas1).getGroupByFields()[0];
                                    List<JavaScriptObject> al = new ArrayList<JavaScriptObject>();


                                    for (int i = 0, tnLength = tn.length; i < tnLength; i++) {
                                        if (tn[i].getAttributeAsBoolean("isFolder") && tn[i].getAttribute("groupName").equalsIgnoreCase(topLevelGroup)) {

                                            al.add(tn[i].getJsObj());

                                        }

                                        ListGridRecord listGridRecord = tn[i];
                                        for (int j = 0; j < columns; j++) {
                                            String style = ((ListGridWithDesc) canvas1).getCellCSSText(listGridRecord, i, j);
                                            String savedStyleAdress = styles.get(style);
                                            if (savedStyleAdress != null) {
                                                m.put("" + i + "_" + j, "#!#" + savedStyleAdress);


                                            } else {
                                                styles.put(style, "" + i + "_" + j);
                                                m.put("" + i + "_" + j, style);
                                            }
                                        }


                                    }

                                    styles.clear();
                                    sStyles = JSON.encode(JSOHelper.convertMapToJavascriptObject(m));
                                    slg = JSON.encode(JSOHelper.arrayConvert(al.toArray(new JavaScriptObject[al.size()])));

                                    m.clear();
                                }
                                HeaderSpanMimic hsmroot = null;
                                if (((ListGridWithDesc) canvas1) instanceof ReportListGridWithDesc) {
                                    //Определяем порядок полей и исключаем скрытые
                                    try {
                                        List<Map> mappings = CommonGridUtils.getKeyNumberMappings(((ReportListGridWithDesc) canvas1).getiAnalisysDesc());
                                        //NNode2 root = new NNode2("ROOT", "", "ROOT", NNode2.NNodeType, null, false, null, ((ReportListGridWithDesc) canvas1).getiAnalisysDesc().getNodes(), null);
//                                        NNode2 root=((ReportListGridWithDesc) canvas1).getiAnalisysDesc().getNodes()[0].getParent(); //TODO!!!! ПОдпорка
                                        NNode2 root = CommonGridUtils.findRootNode(((ReportListGridWithDesc) canvas).getiAnalisysDesc());

                                        hsmroot = CommonGridUtils.buildSpans(root, mappings.get(0));
                                        final GrpDef[] keyCols = ((ReportListGridWithDesc) canvas1).getiAnalisysDesc().getGrpXHierarchy();//Получим ключевые поля по X


                                        List<ListGridField> ll = new LinkedList<ListGridField>();
                                        List<String> grpNames = new LinkedList<String>();

//            HeaderSpan grpSpan = new HeaderSpan();
                                        Map<String, ColDef> tupleDef = ((ReportListGridWithDesc) canvas1).getiAnalisysDesc().getTupleDef();


                                        //Ключевые поля
                                        for (int i = 0, keyColsLength = keyCols.length; i < keyColsLength; i++) {
                                            final GrpDef grpDef = keyCols[i];
                                            final String tid = grpDef.getTid();
                                            ColDef colDef = tupleDef.get(tid);
                                            ListGridField pdID = new ListGridField(tid, colDef.getTitle());

                                            // pdID.setHidden(colDef.isHide());

                                            if (i < keyColsLength - 1) {

                                            } else {
                                                String tColId = grpDef.gettColId();
                                                if (!tid.equals(tColId)) {
                                                    colDef = tupleDef.get(tColId);
                                                    pdID = new ListGridField(tColId, colDef.getTitle());
                                                    //  pdID.setHidden(colDef.isHide());
                                                    //   pdID.setShowGridSummary(false);
                                                    ll.add(pdID);

                                                    if (!colDef.isHide()) {
//                            grpSpan.setTitle(pdID.getTitle());
//                            grpSpan.setFields(pdID.getName());
//                            pdID.setTitle(String.valueOf(keyCols[0].getColN()));
                                                        //  pdID.setTitle(pdID.getTitle());
                                                        HeaderSpanMimic firstcolMimic = new HeaderSpanMimic();
                                                        firstcolMimic.setName(pdID.getTitle());
                                                        firstcolMimic.setFieldNames(new String[]{pdID.getName()});
                                                        List<HeaderSpanMimic> subs = Arrays.asList(hsmroot.getSubs());
                                                        List<HeaderSpanMimic> subs2 = new ArrayList<HeaderSpanMimic>();
                                                        subs2.addAll(subs);
                                                        subs2.add(0, firstcolMimic);
                                                        hsmroot.setSubs(subs2.toArray(new HeaderSpanMimic[subs2.size()]));
                                                        // grid.setGroupTitleField(tColId);//TODO подпорка поле должно описываться явно
                                                    }
//                        grid.setInitialSort(new SortSpecifier(tColId, SortDirection.ASCENDING));
                                                }
                                            }

                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                App01.GUI_STATE_DESC.getBuildTreeService().buildExcelReport_new(slg, sStyles,
                                        ListGridDescriptor.buildDescriptor((ListGridWithDesc) canvas1), hsmroot,
                                        new AsyncCallback<String[]>() {
                                            @Override
                                            public void onFailure(Throwable caught) {
                                                //To change body of implemented methods use File | Settings | File Templates.

                                                SC.warn("Файл не сформирован");
                                            }

                                            @Override
                                            public void onSuccess(String[] result) {
                                                //TODO Открываем уже сформированный документ
                                                //
                                                //
                                                //           com.google.gwt.user.client.Window.open("XXXXX","_blank",null);

                                                if (result == null || result.length != 2 || result[0] == null || result[1] == null) {
                                                    SC.warn("Файл не сформирован");


                                                } else

                                                {

                                                    String sessionId = result[0];
                                                    String filename = result[1];
                                                    //Делаем вызов в сервлет, который вернет содержимое
                                                    String url = GWT.getHostPageBaseURL() + "transport/export?";
//                                                    url += "filename=" + filename.replace(" ", "%20") + "&sid=" + sessionId + "&format=xls";
                                                    url += "filename=" + UriUtils.encode(filename) + "&sid=" + sessionId + "&format=xls";
                                                    // GWT.log(GWT.getModuleBaseURL());
                                                    // GWT.log(GWT.getHostPageBaseURL());
                                                    // GWT.log(GWT.getModuleBaseForStaticFiles());

                                                    com.google.gwt.user.client.Window.open(url, null, null);

                                                                       /* try {
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


                                                                        }*/

                                                }

                                            }
                                        });


                            }
                        });


        pinUp[0].setTooltip("Excel");

        pinUp[0].setGrid(canvas);
        pinUp[0].setOperation(this);
        pinUp[0].setTarget(target);
        pinUp[0].setCanDrag(true);
        pinUp[0].setCanDrop(true);

        return pinUp[0];
    }

    @Override
    public Canvas onRemove(Canvas warnGrid, Window target) {
        if (warnGrid instanceof ListGridWithDesc) {
            ListGridWithDesc newGrid = (ListGridWithDesc) warnGrid;
            // newGrid.setRolloverHandler(null);
            List<DescOperation> subOperation = newGrid.getDescOperation().getSubOperation();
            int ix = findIndexInDesc(subOperation);
            if (ix >= 0)
                subOperation.remove(ix);
        }
        return super.onRemove(warnGrid, target);
    }


//    protected int findIndexInDesc(List<DescOperation> subOperation) {
//        String apiName = ExcelExportOperation.class.getName();
//        for (int i = 0, subOperationSize = subOperation.size(); i < subOperationSize; i++) {
//            DescOperation operation = subOperation.get(i);
//            if (apiName.equals(operation.apiName))
//                return i;
//        }
//        return -1;
//    }


    @Override
    public Object copy() {
        return super.copy();
    }

    @Override
    public IOperation createOperation(DescOperation descOperation) {

        return super.createOperation(descOperation);
    }

    @Override
    protected String getNameOperation(IOperation operation) {
        return super.getNameOperation(operation);
    }

    @Override
    protected IOperation getEmptyObject(DescOperation descOperation) {
        return new ExcelExportOperation();
    }

    @Override
    public IOperation createOperation(DescOperation descOperation, IOperation operation) {
        ExcelExportOperation newOperation = (ExcelExportOperation) super.createOperation(descOperation, operation);

        return newOperation;
    }

    @Override
    public void setStringParam(String param) {
        super.setStringParam(param);
    }

    @Override
    public String getStringParam() {
        return super.getStringParam();
    }

    @Override
    public void setParams(List params) {
        super.setParams(params);
    }

    @Override
    public List getParams() {
        return super.getParams();
    }

}
