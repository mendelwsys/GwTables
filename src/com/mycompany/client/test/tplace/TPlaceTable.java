package com.mycompany.client.test.tplace;

import com.google.gwt.core.client.JavaScriptObject;
import com.mycompany.client.GridCtrl;
import com.mycompany.client.GridUtils;
import com.mycompany.client.IDataFlowCtrl;
import com.mycompany.client.ListGridWithDesc;
import com.mycompany.client.apps.App.AppConst;
import com.mycompany.client.apps.App.OptionsViewers;
import com.mycompany.client.test.TestBuilder;
import com.mycompany.client.updaters.*;
import com.mycompany.client.utils.*;
import com.mycompany.common.DescOperation;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.*;
import com.smartgwt.client.types.DragAppearance;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.util.JSON;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.PortalLayout;
import com.smartgwt.client.widgets.layout.Portlet;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 01.06.15
 * Time: 19:00
 * Тестирование получения данных для провайдеров вместе с указанием места событий
 */
public class TPlaceTable //   extends DSRegisterImpl
        implements TestBuilder,Runnable
{


    public static final String PLACE_CODE_NAME = "CODEPLACE";
    public static final String PLACE_CODE_TITLE = "Коды мест";


    public static final String PLACE_NAMES_NAME = "NAMESPLACE";
    public static final String PLACE_NAMES_TITLE = "Места";

    @Override
    public void run() {

        final HLayout mainLayout = new HLayout();
        mainLayout.setID(AppConst.t_MY_ROOT_PANEL);
        mainLayout.setShowEdges(false);
        mainLayout.setHeight100();
        mainLayout.setWidth100();
        mainLayout.setDragAppearance(DragAppearance.TARGET);

        this.setComponents(mainLayout);
        mainLayout.draw();
    }


    private ListGridWithDesc grid;
    private Map<String,Record> placeCache= new HashMap<String,Record>();


    private class PlacesDSCallback implements DSCallback {
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
                    record.setAttribute(PLACE_CODE_NAME, codes.get(ix));
                }
                names.clear();
                codes.clear();
            }

            if (wasChanged)
                grid.markForRedraw();





        }
    }



    private TextAreaItem textAreaItem;
    private TextAreaItem textAreaItem2;

    @Override
    public void setComponents(Layout mainLayout)
    {
        final PortalLayout portalLayout = new PortalLayout(0);
        portalLayout.setWidth100();
        portalLayout.setHeight100();
        portalLayout.setShowColumnMenus(false);

        final String dataURL = "transport/tdata3";
        final String headerURL = "theadDesc.jsp";


        final String tType = TablesTypes.WARNINGS;
        {
            Criteria criteria1 = new Criteria(TablesTypes.TTYPE, tType);
            grid = GridUtils.createGridTable(
                    new BGridConstructor()
//                    {
//
//                protected RecordList extractData4Update(Record[] data,int[] countWasChanged)
//                {
//                    long ln=System.currentTimeMillis();
//                    RecordList rv = super.extractData4Update(data, countWasChanged);
//                    ln=System.currentTimeMillis()-ln;
//                    textAreaItem2.setValue("Check  status:  grid update time: " + ln+ " update Size:"+data.length);
//                    return rv;
//                }
//                    }
                    , new GridUtils.DefaultGridFactory() {
                        public ListGridWithDesc createGrid() {
                            ListGridWithDesc _grid = super.createGrid();
                            _grid.setDescOperation(new DescOperation());
                            return _grid;
                        }

                    }, new GridMetaProviderBase(), criteria1, headerURL, dataURL, true, false);
        }


        if (grid.isMetaWasSet())
            getDataPlaces(dataURL, headerURL, tType);
        else
        {
            new PostponeOperationProvider(new PostponeOperationProvider.IPostponeOperation()
            {
                @Override
                public boolean operate()
                {
                    if (grid.isMetaWasSet())
                    {
                        getDataPlaces(dataURL, headerURL, tType);
                        return true;
                    }
                    return false;
                }
            });
        }


        {
            final DynamicForm form = OptionsViewers.createEmptyForm();
            form.setLayoutAlign(VerticalAlignment.TOP);
            textAreaItem= new TextAreaItem();
            textAreaItem.setWidth("100%");
            textAreaItem.setTitle("");

            textAreaItem2= new TextAreaItem();
            textAreaItem2.setWidth("100%");
            textAreaItem2.setTitle("");
            form.setFields(textAreaItem,textAreaItem2);

            Portlet portlet0 = new Portlet();
            portlet0.addItem(form);
            portlet0.setHeight("20%");
            portlet0.setTitle("Статус теста");

            portalLayout.addPortlet(portlet0);

        }


        Portlet portlet = new Portlet();
        portlet.addItem(grid);
        portalLayout.addPortlet(portlet);



        mainLayout.addMembers(portalLayout);
    }

    private void getDataPlaces(String dataURL, String headerURL, String tType)
    {

        grid.getCtrl().addAfterUpdater(new PlacesDSCallback());


        Criteria criteria = new Criteria(TablesTypes.TTYPE, TablesTypes.PLACEPOLG);
        final String inTableName = tType + "_" + TablesTypes.PLACES;
        JavaScriptObject js = JSOHelper.convertToJavaScriptArray(new String[]{inTableName});
        criteria.addCriteria(TablesTypes.TTYPE+"_ADD", JSON.encode(js));

        final BMetaConstructor metaConstructor = new BMetaConstructor();
        Pair<DSCallback, MyDSCallback> dataCallBacks = new MyDSUpdaterInit().initUpdater(metaConstructor, headerURL, dataURL, TablesTypes.PLACEPOLG, IDataFlowCtrl.DEF_DELAY_DATA_MILLIS);
        final String addDataSourceId = "$" + criteria.getAttribute(TablesTypes.TTYPE);
        final GridCtrl ctrl=new GridCtrl(addDataSourceId, dataCallBacks, criteria, headerURL, dataURL);
        ctrl.updateMeta(null);


        new PostponeOperationProvider(new PostponeOperationProvider.IPostponeOperation()
        {
            @Override
            public boolean operate()
            {

                if (metaConstructor.isMetaWasSet()) //после прибытия метаданных инициализирует функции и стартуем апдейт данных виджета
                {
                    grid.addGridField(new ListGridField(PLACE_CODE_NAME, PLACE_CODE_TITLE),-1);
                    grid.addGridField(new ListGridField(PLACE_NAMES_NAME, PLACE_NAMES_TITLE),-1);
                    ctrl.startUpdateData(false);
                    return true;
                }
                return false;
            }
        });
    }


//    public Pair<DSCallback, MyDSCallback> initUpdater(IMetaTableConstructor metaConstructor, String headerURL, String dataURL, String tblType, int period)
//    {
//
//        DSCallback headerCallBack = initMetaDataUpdater(metaConstructor,headerURL,dataURL,tblType);
//        MyDSCallback dataCallBack = initDataUpdater(period);
//        return new Pair<DSCallback, MyDSCallback>(headerCallBack,dataCallBack);
//    }
//
//    public DSCallback initMetaDataUpdater(IMetaTableConstructor metaConstructor,String headerURL, final String dataURL, String tblType)
//    {
//        final Criteria criteria = new Criteria(TablesTypes.TTYPE, tblType);
//        metaConstructor.setAddIdDataSource("$" + criteria.getAttribute(TablesTypes.TTYPE));
//        return new MetaTableDSCallback(headerURL,dataURL,new DSRegisterImpl(),metaConstructor);
//
//    }

    private class MyDSUpdaterInit extends DSDefUpdaterInit
    {
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

                    long ln=System.currentTimeMillis();


                    for (int i=0;i< rl.getLength();i++)
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
                        record.setAttribute(PLACE_CODE_NAME, codes.get(ix));
                    }

                    names.clear();
                    codes.clear();

                    ln=System.currentTimeMillis()-ln;

                    if (wasChanged)
                        grid.markForRedraw();

                    textAreaItem.setValue("Check  status:  update time: " + ln+ " update Size:"+data.length+" docLn "+rl.getLength());

                }
            };
        }
    }


}
