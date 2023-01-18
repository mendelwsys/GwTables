package com.mycompany.client.test.Demo;

import com.mycompany.client.ListGridWithDesc;
import com.mycompany.client.MyHeaderControl;
import com.mycompany.client.apps.App.AppConst;
import com.mycompany.client.apps.App.api.IOperationContext;
import com.mycompany.client.apps.FiltersAndGroups;
import com.mycompany.client.apps.OperationNode;
import com.mycompany.client.apps.SimpleOperation;
import com.mycompany.client.apps.SimpleOperationP;
import com.mycompany.client.operations.IOperation;
import com.mycompany.client.operations.IOperationParam;
import com.mycompany.client.operations.SimpleOperationFactory;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.types.FieldType;
import com.smartgwt.client.types.GroupStartOpen;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HeaderControl;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.grid.*;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;

import java.util.LinkedHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 27.11.14
 * Time: 13:51
 * Далее содержимое операция полностью заменяется
 */
public class TestNodesHolder 
{
    public static TreeGrid buildTree()
    {
        return FiltersAndGroups.buildTree(getTestData(), "Таблицы", FieldType.INTEGER,"mainTree");
    }
    
    
    public static TreeNode[] getTestData()
    {
        return new TreeNode[]
                {
                    new OperationNode
                    (
                            new DefNewPortlet(11,1,"Предупреждения", IOperation.TypeOperation.addEventPortlet)
                            {
                                @Override
                                public String getTableType()
                                {
                                    return TablesTypes.WARNINGS;
                                }
                            }
                    ),
                    new OperationNode(new SimpleOperation(111,11,"Фильтры", IOperation.TypeOperation.NON)),
                    new OperationNode(new SimpleOperationFactory()
                    {
                          public IOperationParam _getOperation()
                                        {return new SimpleOperationP(1111,111,"По ПЧ", IOperation.TypeOperation.addFilter)
                    {
                        {
                            setStringParam("ПЧ-11");
                        }


                        public HeaderControl createHeaderControl(final Canvas canvas, final Window target)
                        {
                            final MyHeaderControl pinUp=new MyHeaderControl
                                    (
                                    //new HeaderControl.HeaderIcon("pred1.png"),
                            HeaderControl.HOME,
                            new com.smartgwt.client.widgets.events.ClickHandler()
                            {
                                public void onClick(ClickEvent event)
                                {
                                        Canvas form= getInputFrom(canvas);
                                        Canvas rootCanvas = Canvas.getById(AppConst.t_MY_ROOT_PANEL);
                                        rootCanvas.addChild(form);
                                }
                            });
                            pinUp.setGrid(canvas);
                            pinUp.setOperation(this);
                            pinUp.setTarget(target);

                            pinUp.setTooltip("Фильтр ПЧ");
                            pinUp.setCanDrag(true);
                            pinUp.setCanDrop(true);
                            return pinUp;
                        }

                        public Canvas operate(Canvas _warnGrid, IOperationContext ctx)
                        {
                            if (_warnGrid instanceof ListGridWithDesc)
                            {
//TODO изменился интерфейс                                String filterParam = getStringParam();

                                ListGridWithDesc warnGrid = (ListGridWithDesc)_warnGrid;
//
//                                List<IOperation> filters = warnGrid.getFiltersOperations();
//                                RecordList rl;
//                                if (filters!=null && filters.size()>0)
//                                {
//                                   if (filters.get(0)==this)
//                                        rl =  warnGrid.getCacheData();
//                                    else
//                                        rl = warnGrid.getDataAsRecordList();
//
//                                    for (int i=0;i<rl.getLength();)
//                                    {
//                                        Record rc=rl.get(i);
//                                        if (filterParam!=null && filterParam.length()>0 && !filterParam.equals(rc.getAttribute("PRED_NAME")))
//                                            rl.removeAt(i);
//                                        else
//                                            i++;
//                                    }
//                                    warnGrid.setData(rl);
//                                }

                                return warnGrid;
                            }
                            return null;
                        }

                        public Canvas getInputFrom(final Canvas _warnGrid)
                        {
                            final DynamicForm form = new DynamicForm();
                            form.setWidth(250);

                            TextItem pchName = new TextItem();
                            pchName.setTitle("ПЧ");
                            pchName.setRequired(true);
                            pchName.setValue(getStringParam());

                            ButtonItem button = new ButtonItem("Apply", "Применить");
                            button.addClickHandler(new ClickHandler()
                            {
                                public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event)
                                {
                                    setStringParam((String) form.getValues().values().iterator().next());
                                    if (_warnGrid instanceof ListGridWithDesc)
                                    {
                                        ListGridWithDesc warnGrid=(ListGridWithDesc)_warnGrid;
//TODO Изменился интерфейс                                        warnGrid.applyClientFilters();
                                    }
                                }
                            });
                            form.setFields(pchName, button);

//                                    layout.addMember(form);
//                                    layout.addMember(swapButton);

                            Window wnd = new Window();
                            wnd.setTitle("Фильтр по ПЧ");
                            wnd.setCanDragReposition(true);
                            wnd.setCanDragResize(true);
                            wnd.setAutoSize(true);
                            wnd.addItem(form);
                            wnd.setIsModal(true);
                            return  wnd;
                        }
                    };}}
                    ),
                    new OperationNode(new SimpleOperationFactory()
                    {

                            public static final String VPAS_VGR_VEL_VGRPOR_VSTR = "VPAS_VGR_VEL_VGRPOR_VSTR";
                              public IOperationParam _getOperation()
                                            {return new SimpleOperationP(1112,111,"По Скоростям", IOperation.TypeOperation.addFilter)
                        {
                            {
                                setStringParam("background-color:red;");
                            }

                           String filterTitleByValue(String groupValue)
                            {
                                if ("background-color:red;".equals(groupValue))
                                    return "<=15";
                                else if ("background-color:#DDDD00;".equals(groupValue))

                                    return "(15-25]";
                                else if ("background-color:#99DD00;".equals(groupValue))
                                    return "(25 - 40]";
                                else
                                    return ">40";

                            }


                            public HeaderControl createHeaderControl(final Canvas canvas, final Window target)
                            {
                                final MyHeaderControl pinUp=new MyHeaderControl
                                        (
                                HeaderControl.DOUBLE_ARROW_UP,
                                new com.smartgwt.client.widgets.events.ClickHandler()
                                {
                                    public void onClick(ClickEvent event)
                                    {
                                            Canvas form= getInputFrom(canvas);
                                            Canvas rootCanvas = Canvas.getById(AppConst.t_MY_ROOT_PANEL);
                                            rootCanvas.addChild(form);
                                    }
                                });
                                pinUp.setGrid(canvas);
                                pinUp.setOperation(this);
                                pinUp.setTarget(target);

                                pinUp.setTooltip("По скоростям");
                                pinUp.setCanDrag(true);
                                pinUp.setCanDrop(true);
                                return pinUp;
                            }

                            public Canvas operate(Canvas _warnGrid, IOperationContext ctx)
                            {
                                if (_warnGrid instanceof ListGridWithDesc)
                                {

                                    String filterParam = getStringParam();

                                    ListGridWithDesc warnGrid = (ListGridWithDesc)_warnGrid;

//TODO изменился интерфейс                                     List<IOperation> filters = warnGrid.getFiltersOperations();
//                                    RecordList rl;
//                                    if (filters!=null && filters.size()>0)
//                                    {
//                                       if (filters.get(0)==this)
//                                            rl =  warnGrid.getCacheData();
//                                        else
//                                            rl = warnGrid.getDataAsRecordList();
//
//                                        for (int i=0;i<rl.getLength();)
//                                        {
//                                            MyRecord rc=(MyRecord)rl.get(i);
//                                            String rowStyle=rc.getRowStyle();
//                                            if (filterParam.equals(rowStyle) || (filterParam.equals("X") && rowStyle==null))
//                                                i++;
//                                            else
//                                                rl.removeAt(i);
//                                        }
//                                        warnGrid.setData(rl);
//                                    }

                                    return warnGrid;
                                }
                                return null;
                            }

                            public Canvas getInputFrom(final Canvas _warnGrid)
                            {
                                final DynamicForm form = new DynamicForm();
                                form.setWidth(250);

                                SelectItem pchName = new SelectItem();
                                pchName.setTitle("Скорости");
                                pchName.setRequired(true);

                                LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();

                                valueMap.put("background-color:red;", filterTitleByValue("background-color:red;"));
                                valueMap.put("background-color:#DDDD00;", filterTitleByValue("background-color:#DDDD00;"));
                                valueMap.put("background-color:#99DD00;", filterTitleByValue("background-color:#99DD00;"));
                                valueMap.put("X", filterTitleByValue("X"));
                                pchName.setValueMap(valueMap);

                                pchName.setValue(getStringParam());

                                ButtonItem button = new ButtonItem("Apply", "Применить");
                                button.addClickHandler(new ClickHandler()
                                {
                                    public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event)
                                    {
                                        setStringParam((String) form.getValues().values().iterator().next());
                                        if (_warnGrid instanceof ListGridWithDesc)
                                        {
                                            ListGridWithDesc warnGrid=(ListGridWithDesc)_warnGrid;
//TODO Изменился интерфейс                                             warnGrid.applyClientFilters();
                                        }
                                    }
                                });
                                form.setFields(pchName, button);
//
                                Window wnd = new Window();
                                wnd.setTitle("Фильтр по Скоростям");
                                wnd.setCanDragReposition(true);
                                wnd.setCanDragResize(true);
                                wnd.setAutoSize(true);
                                wnd.addItem(form);
                                wnd.setIsModal(true);
                                return  wnd;
                            }
                        };}}
                    ),


                    new OperationNode(new SimpleOperation(112,11,"Группы", IOperation.TypeOperation.NON)),
                    new OperationNode(new SimpleOperationFactory()
                    {
                        public IOperationParam _getOperation()
                        {
                            return new SimpleOperationP(1121,112,"По скоростям", IOperation.TypeOperation.addGroping)
                            {

                                public static final String VPAS_VGR_VEL_VGRPOR_VSTR = "VPAS_VGR_VEL_VGRPOR_VSTR";
                                public HeaderControl createHeaderControl(final Canvas canvas, final Window target)
                                {
                                    final MyHeaderControl pinUp=new MyHeaderControl
                                            (
                                            HeaderControl.SETTINGS,
                                            new com.smartgwt.client.widgets.events.ClickHandler()
                                            {
                                                boolean group=true;
                                                public void onClick(ClickEvent event)
                                                {
                                                    if (!group)
                                                        ((ListGrid) canvas).groupBy(VPAS_VGR_VEL_VGRPOR_VSTR);
                                                    else
                                                        ((ListGrid) canvas).ungroup();
                                                    group=!group;
                                                }
                                            });
                                            pinUp.setGrid(canvas);
                                            pinUp.setOperation(this);
                                            pinUp.setTarget(target);

                                            pinUp.setTooltip("Группировка по скоростям");
                                            pinUp.setCanDrag(true);
                                            pinUp.setCanDrop(true);
                                            return pinUp;
                                }

                                public Canvas operate(Canvas _warnGrid, IOperationContext ctx)
                                {
                                    if (_warnGrid instanceof ListGridWithDesc)
                                    {

                                        ListGridWithDesc warnGrid = (ListGridWithDesc) _warnGrid;

//TODO изменился интерфейс                                         List<IOperation> filters = warnGrid.getFiltersOperations();
//                                        if (filters!=null && filters.size()>0 && filters.get(0)==this)
//                                                warnGrid.setData(warnGrid.getCacheData());

                                        ListGridField fld = warnGrid.getField(VPAS_VGR_VEL_VGRPOR_VSTR);
                                        fld.setGroupValueFunction(
                                                new GroupValueFunction()
                                                {
                                                    public Object getGroupValue(Object value, ListGridRecord record, ListGridField field, String fieldName, ListGrid grid)
                                                    {
                                                        MyRecord rc=(MyRecord)record;
                                                        return rc.getRowStyle();
                                                    }
                                                });

                                        fld.setGroupTitleRenderer(new GroupTitleRenderer()
                                        {
                                             public String getGroupTitle(Object groupValue, GroupNode groupNode, ListGridField field, String fieldName, ListGrid grid)
                                             {
                                                 if ("background-color:red;".equals(groupValue))
                                                     return "<=15";
                                                 else if ("background-color:#DDDD00;".equals(groupValue))
                                                     return "(15 - 25]";
                                                 else if ("background-color:#99DD00;".equals(groupValue))
                                                     return "(25 - 40]";
                                                 else
                                                     return ">40";
                                             }
                                        });

                                        warnGrid.setGroupStartOpen(GroupStartOpen.ALL);
                                        warnGrid.groupBy(VPAS_VGR_VEL_VGRPOR_VSTR);
                                    }
                                    return null;
                                }

                                public Canvas onRemove(Canvas warnGrid, Window target)
                                {
                                    ((ListGrid)warnGrid).ungroup();
                                    return null;
                                }
                            };
                        }
                    }),

                    new OperationNode
                    (
                        new DefNewPortlet(12,1,"Окна", IOperation.TypeOperation.addEventPortlet)
                        {
                            @Override
                            public String getTableType()
                            {
                                return TablesTypes.WINDOWS;
                            }
                        }
                    ),

                    new OperationNode(new SimpleOperation(121,12,"Фильтры", IOperation.TypeOperation.NON),true),
                    new OperationNode(new SimpleOperation(122,12,"Группы", IOperation.TypeOperation.NON),true),

//                    new OperationNode(new SimpleOperationFactory()
//                    {
//                        public IOperation _getOperation()
//                        {
//                            return new SimpleOperation(1221,122,"По завершенности", IOperation.TypeOperation.addGroping)
//                            {
//
//                            }
//                        }
//                    }),

                    new OperationNode(new DefNewPortlet(13,1,"Отказы", IOperation.TypeOperation.addEventPortlet)
                    {
                        @Override
                        public String getTableType()
                        {
                            return TablesTypes.REFUSES;
                        }
                    }),
                        new OperationNode(new SimpleOperation(131,13,"Фильтры", IOperation.TypeOperation.NON),true),
                        new OperationNode(new SimpleOperation(132,13,"Группы", IOperation.TypeOperation.NON),true),


                    new OperationNode(new DefNewPortlet(14,1,"Нарушения", IOperation.TypeOperation.addEventPortlet)
                    {
                        @Override
                        public String getTableType()
                        {
                            return TablesTypes.VIOLATIONS;
                        }
                    }),
                    new OperationNode(new SimpleOperation(141,14,"Фильтры", IOperation.TypeOperation.NON),true),
                    new OperationNode(new SimpleOperation(142,14,"Группы", IOperation.TypeOperation.NON),true),


                    new OperationNode(new SimpleOperation(15,1,"Задержки поездов", IOperation.TypeOperation.NON),true),
                    new OperationNode(new DefNewPortlet(151,15,"ГИД", IOperation.TypeOperation.addEventPortlet)
                    {
                        @Override
                        public String getTableType()
                        {
                            return TablesTypes.DELAYS_GID;
                        }
                    }),
                    new OperationNode(new SimpleOperation(1511,151,"Фильтры", IOperation.TypeOperation.NON),true),
                    new OperationNode(new SimpleOperation(1512,151,"Группы", IOperation.TypeOperation.NON),true),

                    new OperationNode(new DefNewPortlet(152,15,"АБВГД", IOperation.TypeOperation.addEventPortlet)
                    {
                        @Override
                        public String getTableType()
                        {
                            return TablesTypes.DELAYS_ABVGD;
                        }
                    }),
                    new OperationNode(new SimpleOperation(1521,152,"Фильтры", IOperation.TypeOperation.NON),true),
                    new OperationNode(new SimpleOperation(1522,152,"Группы", IOperation.TypeOperation.NON),true),

                    new OperationNode(new DefNewPortlet(16,1,"Брошенные поезда", IOperation.TypeOperation.addEventPortlet)
                    {
                        @Override
                        public String getTableType()
                        {
                            return TablesTypes.LOST_TRAIN;
                        }
                    }),
                    new OperationNode(new SimpleOperation(161,16,"Фильтры", IOperation.TypeOperation.NON),true),
                    new OperationNode(new SimpleOperation(162,16,"Группы", IOperation.TypeOperation.NON),true),
/*
                    new OperationNode(new DefNewPortlet(17,1,"Вагоны в ТОР", IOperation.TypeOperation.addEventPortlet)
                    {
                        @Override
                        public String getEventsName()
                        {
                            return TablesTypes.VAGTOR;
                        }
                    }),
                    new OperationNode(new SimpleOperation(171,17,"Фильтры", IOperation.TypeOperation.NON),true),
                    new OperationNode(new SimpleOperation(172,17,"Группы", IOperation.TypeOperation.NON),true),
*/
                    new OperationNode(new DefNewPortlet(18,1,"Отметки ГИД", IOperation.TypeOperation.addEventPortlet)
                    {
                        @Override
                        public String getTableType()
                        {
                            return TablesTypes.MARKS_GID;
                        }
                    }),

                    new OperationNode(new SimpleOperation(181,18,"Фильтры", IOperation.TypeOperation.NON),true),
                    new OperationNode(new SimpleOperation(182,18,"Группы", IOperation.TypeOperation.NON),true),


                };
    }

    
}
