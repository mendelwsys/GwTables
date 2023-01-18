package com.mycompany.client.apps.App.api;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.HandlerRegistration;
import com.mycompany.client.GridCtrl;
import com.mycompany.client.IDataFlowCtrl;
import com.mycompany.client.ListGridWithDesc;
import com.mycompany.client.MyHeaderControl;
import com.mycompany.client.apps.App.AppConst;
import com.mycompany.client.apps.App.ISyncHandler;
import com.mycompany.client.apps.App.NodesHolder;
import com.mycompany.client.apps.App.SyncHandlerImpl;
import com.mycompany.client.apps.SimpleOperation;
import com.mycompany.client.operations.IOperation;
import com.mycompany.client.updaters.BMetaConstructor;
import com.mycompany.client.updaters.DataDSCallback;
import com.mycompany.client.utils.DSDefUpdaterInit;
import com.mycompany.client.utils.MyDSCallback;
import com.mycompany.client.utils.PostponeOperationProvider;
import com.mycompany.common.DescOperation;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.*;
import com.smartgwt.client.types.ListGridFieldType;
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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 27.11.14
 * Time: 13:59
 * Операция создания полей для отображения мест событий
 */
public class ExtendGridByPlaces extends SimpleOperation
{
    public ExtendGridByPlaces(int operationId, int parentOperationId, String viewName, TypeOperation type)
    {
        super(operationId, parentOperationId, viewName, type);
    }

    protected ExtendGridByPlaces()
    {
    }

    protected IOperation getEmptyObject(DescOperation descOperation)
    {
        return new ExtendGridByPlaces();
    }

    private ISyncHandler syncHandler;

    public ISyncHandler getSyncHandler()
    {
        return syncHandler;
    }

    @Override
    public Canvas operate(Canvas dragTarget, IOperationContext ctx)
    {
        if (dragTarget instanceof Window)
        {
            Canvas[] items = ((Window) dragTarget).getItems();
            if (items!=null && items.length>0)
                return operate(items[0], null);
        }
        else if (dragTarget instanceof ListGridWithDesc)
        {
            final ListGridWithDesc newGrid = (ListGridWithDesc) dragTarget;

            final SyncHandlerImpl _syncHandler = new SyncHandlerImpl();
            this.syncHandler=_syncHandler;


            if (newGrid.isMetaWasSet())
                _addPlaceField2Grid(newGrid,_syncHandler);
            else
                new PostponeOperationProvider(new PostponeOperationProvider.IPostponeOperation()
                {

                    @Override
                    public boolean operate()
                    {
                        if (newGrid.isMetaWasSet())
                        {
                            _addPlaceField2Grid(newGrid,_syncHandler);
                            return true;
                        }
                        return false;
                    }
                });
        }

        return super.operate(dragTarget,ctx);
    }


    @Override
    public IOperation createOperation(DescOperation descOperation, IOperation operation)
    {
        ExtendGridByPlaces retOperation= (ExtendGridByPlaces) super.createOperation(descOperation,operation);
//        retOperation.dor_kod=(Integer)descOperation.get(DOR_KOD); //TODO Устанавливать позицию полей при сохранении и загрузки данных, устанавливать видимость полей

        return retOperation;
    }


    @Override
    public DescOperation getDescOperation(DescOperation descOperation)
    {
        descOperation=super.getDescOperation(descOperation);
//        descOperation.put(DOR_KOD, dor_kod);

        return descOperation;
    }


    @Override
    public Canvas onRemove(Canvas warnGrid, Window target) {
        if (warnGrid instanceof ListGridWithDesc)
        {
            ListGridWithDesc newGrid = (ListGridWithDesc) warnGrid;
            List<DescOperation> subOperation = newGrid.getDescOperation().getSubOperation();
            int ix=findIndexInDesc(subOperation,ExtendGridByPlaces.class);
            if (ix>=0)
                  subOperation.remove(ix);
        }
        return super.onRemove(warnGrid, target);
    }

//----------------------------------------------------------------------//


    public static final String PLACE_CODE_NAME = "CODEPLACE";
    public static final String PLACE_CODE_TITLE = "Коды мест";


    public static final String PLACE_NAMES_NAME = "NAMESPLACE";
    public static final String PLACE_NAMES_TITLE = "Места";

    final String dataURL = "transport/tdata2";
    final String headerURL = "theadDesc.jsp";


    protected void _addPlaceField2Grid(final ListGridWithDesc grid,final SyncHandlerImpl _syncHandler)
    { //TODO Но это еще не все, надо создать пиктограмму операции
        String tType=null;
        DescOperation gridDesc = grid.getDescOperation();
        if (gridDesc!=null)
            tType=(String)gridDesc.get(ListGridWithDesc.EVENTS_NAME);

        if (tType==null)
        {
            SC.warn("Неизвестный тип таблицы, операция невозможна");
            return;
        }

        DescOperation gridDescOperation = grid.getDescOperation();
        List<DescOperation> subOperation = gridDescOperation.getSubOperation();
        int ix=findIndexInDesc(subOperation);
        if (ix>=0)
        {
            SC.warn("Поля уже добавлены");
            return;
        }

        final Window target = grid.getTarget();
        final ExtendHeaderControl pinUp = (ExtendHeaderControl) createHeaderControl(grid, target);
        if (target.isDrawn())
            NodesHolder.addHeaderCtrl(target, pinUp);
        else {
            final HandlerRegistration[] handlerRegistrations = new HandlerRegistration[1];
            handlerRegistrations[0] = target.addDrawHandler(new DrawHandler()
            {
                @Override
                public void onDraw(DrawEvent event) {
                    NodesHolder.addHeaderCtrl(target, pinUp);
                    handlerRegistrations[0].removeHandler();//удалить хандлер после обработки
                }
            });
        }


        final Map<String,Record> placeCache= new HashMap<String,Record>();

        Criteria criteria = new Criteria(TablesTypes.TTYPE, TablesTypes.PLACEPOLG);
        final String inTableName = tType + "_" + TablesTypes.PLACES;
        JavaScriptObject js = JSOHelper.convertToJavaScriptArray(new String[]{inTableName});
        criteria.addCriteria(TablesTypes.TTYPE+"_ADD", JSON.encode(js));

        final BMetaConstructor metaConstructor = new BMetaConstructor();
        Pair<DSCallback, MyDSCallback> dataCallBacks = new MyDSUpdaterInit(grid,placeCache).initUpdater(metaConstructor, headerURL, dataURL, TablesTypes.PLACEPOLG, IDataFlowCtrl.DEF_DELAY_DATA_MILLIS);
        final String addDataSourceId = "$" + criteria.getAttribute(TablesTypes.TTYPE);
        final GridCtrl ctrl=new GridCtrl(addDataSourceId, dataCallBacks, criteria, headerURL, dataURL);
        ctrl.updateMeta(null);


        final PlacesDSCallback updater = new PlacesDSCallback(grid, placeCache);
        pinUp.setUpdater4Remove(updater);
        pinUp.setCtrl4Stop(ctrl);

        setDescOperation(grid);



        new PostponeOperationProvider(new PostponeOperationProvider.IPostponeOperation()
        {
            @Override
            public boolean operate()
            {
                if (metaConstructor.isMetaWasSet()) //после прибытия метаданных инициализирует функции и стартуем апдейт данных виджета
                {
                    final ListGridField codePlaceField = new ListGridField(PLACE_CODE_NAME, PLACE_CODE_TITLE);
                    codePlaceField.setType(ListGridFieldType.TEXT);
                    final ListGridField titlePlaceFiled = new ListGridField(PLACE_NAMES_NAME, PLACE_NAMES_TITLE);
                    titlePlaceFiled.setType(ListGridFieldType.TEXT);
                    grid.addGridField(new ListGridField[]{codePlaceField,titlePlaceFiled});

                    final IDataFlowCtrl gridCtrl = grid.getCtrl();
                    gridCtrl.addAfterUpdater(updater);
                    ctrl.startUpdateData(true);
                    _syncHandler.setCompletely(true);
                    return true;
                }
                return false;
            }
        });
    }



    private static class ExtendHeaderControl extends MyHeaderControl
    {
        private final Canvas canvas;
        private PlacesDSCallback updater;
        private GridCtrl ctrl;

        public ExtendHeaderControl(final Canvas canvas)
        {
            super(HeaderControl.ZOOM);
            addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler()
            {
                public void onClick(ClickEvent event) {
                    Canvas form = getOperation().getInputFrom(canvas);
                    if (form!=null)
                    {
                        Canvas rootCanvas = Canvas.getById(AppConst.t_MY_ROOT_PANEL);
                        rootCanvas.addChild(form);
                    }
                }
            });
            this.canvas = canvas;
        }



        public void onRemoveCtrl()
        {
            ctrl.stopUpdateData();

            ListGridWithDesc newGrid=(ListGridWithDesc) canvas;
            final IDataFlowCtrl gridCtrl = newGrid.getCtrl();
            gridCtrl.stopUpdateData();

            gridCtrl.removeAfterUpdater(updater);

            newGrid.delGridField(new String[]{PLACE_CODE_NAME,PLACE_NAMES_NAME});

            gridCtrl.startUpdateData(true);
        }

        public void setUpdater4Remove(PlacesDSCallback updater)
        {
            this.updater = updater;
        }

        public void setCtrl4Stop(GridCtrl ctrl)
        {
            this.ctrl = ctrl;
        }
    }

    private static class PlacesDSCallback implements DSCallback
    {
        private ListGridWithDesc grid;
        private Map<String, Record> placeCache;

        PlacesDSCallback(ListGridWithDesc grid,Map<String,Record> placeCache)
        {
            this.grid = grid;
            this.placeCache = placeCache;
        }

        @Override
        public void execute(DSResponse dsResponse, Object rawData, DSRequest dsRequest)
        {

            final Record[] data ;
            if (placeCache.size()==0 || (data= dsResponse.getData()) ==null || data.length==0)
                return;

            RecordList rl = grid.getCacheData();

            Map<String,Integer> gridIndex= new HashMap<String,Integer>();
            for (Record record : data)
            {
                Integer actual=record.getAttributeAsInt(TablesTypes.ACTUAL);
                if (actual!=null && actual==0)
                    continue;
                final String id = record.getAttribute(TablesTypes.KEY_FNAME);
                int ix=rl.findIndex(TablesTypes.KEY_FNAME, id);
                if (ix>=0)
                    gridIndex.put(id,ix);
            }

            Map<Integer,String> names= new HashMap<Integer,String>();
            Map<Integer,String> codes= new HashMap<Integer,String>();
            boolean wasChanged=false;

            {//TODO Очень плохо полные перебор,необходимо как то ускорить этот процесс
                Collection<Record> vals = placeCache.values();
                for (Record recordVals : vals)
                {
                    String id2=recordVals.getAttribute(TablesTypes.KEY_FNAME + "2");
                    Integer ix=gridIndex.get(id2);
                    if (ix!=null)
                    {
                        {
                            final String attribute = recordVals.getAttribute(TablesTypes.POLG_ID);
                            String name=codes.get(ix);
                            if (name==null || name.length()==0)
                               name=attribute;
                            else
                               name+=","+attribute;
                            codes.put(ix,name);
                        }

                        {
                            final String attribute = recordVals.getAttribute(TablesTypes.POLG_NAME);
                            String name=names.get(ix);
                            if (name==null || name.length()==0)
                               name=attribute;
                            else
                               name+=","+attribute;
                            names.put(ix,name);//
                        }
                        wasChanged=true;
                    }
                }
            }


            {
                for (Integer ix : codes.keySet())
                {
                    final Record record = rl.get(ix);
                    record.setAttribute(PLACE_NAMES_NAME, names.get(ix));
                    final String valCode = codes.get(ix);
                    if (valCode!=null && valCode.length()>0)
                        record.setAttribute(PLACE_CODE_NAME, ","+ valCode +",");
                    else
                        record.setAttribute(PLACE_CODE_NAME,valCode);
                }
                names.clear();
                codes.clear();
            }

            if (wasChanged)
            {
                grid.setCliWasChanged();
                grid.markForRedraw();
            }
        }
    }

    private static class MyDSUpdaterInit extends DSDefUpdaterInit
    {
        private ListGridWithDesc grid;
        private Map<String, Record> placeCache;

        MyDSUpdaterInit(ListGridWithDesc grid,Map<String,Record> placeCache)
        {
            this.grid = grid;
            this.placeCache = placeCache;
        }

        @Override
        public MyDSCallback initDataUpdater(final int period)
        {
            return new DataDSCallback(period)
            {
                @Override
                protected void updateData(Record[] data, boolean resetAll)
                {
                    //TODO Здесь производим фильтрацию данных в открытой таблице добавляя фильтр прямо в открытую таблицу WARNING (т.е. это должно быть оформлено как процедура добавления фильтра по местам)

                    RecordList rl=grid.getCacheData();

                    Map<String,Integer> gridIndex= new HashMap<String,Integer>();
//                    long ln=System.currentTimeMillis();
                    for (int i=0;rl!=null && i< rl.getLength();i++)
                        gridIndex.put(rl.get(i).getAttribute(TablesTypes.KEY_FNAME),i);


                    Map<Integer,String> names= new HashMap<Integer,String>();
                    Map<Integer,String> codes= new HashMap<Integer,String>();


                    boolean wasChanged=false;
                    for (Record record : data)
                    {

                        String id=record.getAttribute(TablesTypes.KEY_FNAME);
                        Integer actual=record.getAttributeAsInt(TablesTypes.ACTUAL);


                        if (actual!=null && actual==0)
                        {
                            Record removedRecord = placeCache.remove(id);
                            if (removedRecord!=null)
                            {
                                String id2 = removedRecord.getAttribute(TablesTypes.KEY_FNAME + "2");
                                Integer ix=gridIndex.get(id2);
                                if (ix!=null)
                                {
                                    final Record lgRecord = rl.get(ix);
                                    lgRecord.setAttribute(TablesTypes.POLG_ID, "");
                                    lgRecord.setAttribute(TablesTypes.POLG_NAME, "");
                                    wasChanged=true;
                                    continue;
                                }
                            }
                        }
                        else
                            placeCache.put(id, record);


                        String id2 = record.getAttribute(TablesTypes.KEY_FNAME + "2");
                        Integer ix=gridIndex.get(id2);
                        if (ix!=null)
                        {
                            {
                                final String attribute = record.getAttribute(TablesTypes.POLG_ID);
                                String name=codes.get(ix);
                                if (name==null || name.length()==0)
                                   name=attribute;
                                else
                                   name+=","+attribute;
                                codes.put(ix,name);
                            }

                            {
                                final String attribute = record.getAttribute(TablesTypes.POLG_NAME);
                                String name=names.get(ix);
                                if (name==null || name.length()==0)
                                   name=attribute;
                                else
                                   name+=","+attribute;
                                names.put(ix,name);//
                            }
                            wasChanged=true;
                        }
                    }

                    for (Integer ix : codes.keySet())
                    {
                        final Record record = rl.get(ix);
                        record.setAttribute(PLACE_NAMES_NAME, names.get(ix));
                        final String valCode = codes.get(ix);
                        if (valCode!=null && valCode.length()>0)
                            record.setAttribute(PLACE_CODE_NAME, ","+valCode+",");
                        else
                            record.setAttribute(PLACE_CODE_NAME, valCode);

                    }

                    names.clear();
                    codes.clear();

    //                ln=System.currentTimeMillis()-ln;

                    if (wasChanged)
                    {
                        grid.setCliWasChanged();
                        grid.markForRedraw();
                    }
    //                textAreaItem.setValue("Check  status:  update time: " + ln+ " update Size:"+data.length+" docLn "+rl.getLength());

                }
            };
        }
    }

    public HeaderControl createHeaderControl(final Canvas canvas, final Window target)
    {
        final MyHeaderControl pinUp = new ExtendHeaderControl(canvas);
        pinUp.setGrid(canvas);
        pinUp.setOperation(this);
        pinUp.setTarget(target);

        pinUp.setTooltip("Место событий");
        pinUp.setCanDrag(true);
        pinUp.setCanDrop(true);
        return pinUp;
    }


}
