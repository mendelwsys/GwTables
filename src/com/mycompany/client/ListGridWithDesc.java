package com.mycompany.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.mycompany.client.apps.App.App01;
import com.mycompany.client.operations.ICliFilter;
import com.mycompany.client.test.aggregates.AggregatesUtils;
import com.mycompany.client.test.evalf.BuilderDlg;
import com.mycompany.client.test.evalf.EvalUtils;
import com.mycompany.client.test.evalf.IFormulaContainer;
import com.mycompany.client.test.evalf.JSFormula;
import com.mycompany.client.updaters.BMetaConstructor;
import com.mycompany.client.updaters.DataDSCallback;
import com.mycompany.client.utils.ListenerCtrl;
import com.mycompany.client.utils.SetGridException;
import com.mycompany.common.DescOperation;
import com.mycompany.common.DiagramDesc;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.*;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SortDirection;
import com.smartgwt.client.util.DateUtil;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.util.JSONEncoder;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellClickEvent;
import com.smartgwt.client.widgets.grid.events.CellClickHandler;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;

import java.util.*;


/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 03.06.14
 * Time: 16:18
 * Грид с доп. информацией по поводу графиков
 */
public class ListGridWithDesc  extends ListGrid
{

    public native void setNullRedraw() /*-{
        var self = this.@com.smartgwt.client.widgets.BaseWidget::getOrCreateJsObj()();
        self.body.redraw = function()
        {
        };
    }-*/;

    public static String EVENTS_NAME ="EVENTS_NAME";


    public static final String EDIT_FIELD = "EditField";
    public static final String ADD_FIELD = "AddField";
    public static final String DEL_FIELD="DelField";

    static public final String FIELD_ORDER="fieldOrder";
    static public final String FIELD_HIDDEN ="fieldHidden";
    static public final String FIELD_AGGREGATES = "aggregates";
    static public final String FIELD_FORMATS = "fieldFormats";

    static public final String M_GROUP ="m_group";
    static public final String M_GROUP_ORDER ="m_group_order";

    static public final String M_SORT ="m_sort";
    static public final String M_SORT_ORDER ="m_sort_order";
    static public final String M_SORT_ORDER_DIRECTION ="m_sort_order_direction";

    static public final String M_COLNUM="COLNUM";
    static public final String M_FUNCTION ="m_function";

//Добавляем обработчки клика на ссылке
    {
        if (!App01.isDefOpenMode())
        {
            this.addCellClickHandler(new CellClickHandler() {
                @Override
                public void onCellClick(CellClickEvent event)
                {
                    int colNum=event.getColNum();
                    ListGridField fld = getField(colNum);
                    if (TablesTypes.CRDURL.equals(fld.getName()))
                    {
                        ListGridRecord record = event.getRecord();
                        if (record!=null)
                        {
                            String evType= record.getAttribute(TablesTypes.EVTYPE);
                            String url= record.getAttribute(TablesTypes.CRDURL);
                            if (url!=null)
                                App01.GUI_STATE_DESC.openUrl(url,evType);
                        }
                    }
                }
            });
        }

    }


    public boolean isMetaWasSet() {
        return metaWasSet;
    }

    public void setMetaWasSet(boolean metaWasSet)
    {
        this.metaWasSet = metaWasSet;
    }

    boolean metaWasSet=false;

    public DataSource getFieldsMetaDS() {
        return fieldMetaDS;
    }

    public DataSource getFilterDS() {
        return filterDS;
    }

    private DataSource filterDS;

    public void setFilterDS(DataSource filterDS)
    {
        this.filterDS = filterDS;
    }

    public void setFieldMetaDS(DataSource fieldMetaDS)
    {
        this.fieldMetaDS = fieldMetaDS;
    }

    private DataSource fieldMetaDS;

    private long colNum=0;
    protected long getNextColNum()
    {
        return ++colNum;
    }

//------------ TODO все криво бля подпорище -----------------------------------------
    private DescOperation findDesc(DescOperation descOperation)
    {
        if (apiName.equals(descOperation.apiName))
            return descOperation;

        List<DescOperation> subOperation = descOperation.getSubOperation();
        if (subOperation!=null && subOperation.size()>0)
        {
            for (DescOperation operation : subOperation)
            {
                DescOperation res = findDesc(operation);
                if (res!=null)
                    return res;
            }
        }
        return null;
    }

    private String apiName;
    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    private DescOperation getDescriptor4Save()
    {
        if (descriptor!=null && apiName!=null)
                return findDesc(descriptor);
        return descriptor;
    }
//------------ TODO все криво бля подпорище -----------------------------------------

    public DescOperation getDescOperation()
    {
        final DescOperation descriptor4Save = getDescriptor4Save();
        if (descriptor4Save !=null)
        {
            ListGridField[] flds = this.getAllFields();
            String[] fname=new String[flds.length];
            Boolean [] fview=new Boolean[flds.length];
            Map<String, String> aggregates = new HashMap<String, String>();
            Map<String, String> summaryField_Formats = new HashMap<String, String>();
            for (int i = 0, fldsLength = flds.length; i < fldsLength; i++)
            {
                fname[i]="'"+flds[i].getName()+"'";
                fview[i]=flds[i].getHidden();
                String function = flds[i].getAttribute(TablesTypes.AGGREGATE_FUNCTIONS_TYPES_KEY);
                if (function == null) function = flds[i].getAttribute(TablesTypes.AGGREGATE_CUSTOM_FUNCTIONS_KEY);
                aggregates.put(flds[i].getName(), function);
                summaryField_Formats.put(flds[i].getName(), flds[i].getAttribute(TablesTypes.AGGREGATE_FIELD_FORMAT_KEY));
            }
            JSONEncoder jsonEncoder = new JSONEncoder();
            descriptor4Save.put(FIELD_AGGREGATES, jsonEncoder.encode(JSOHelper.convertMapToJavascriptObject(aggregates)));
            descriptor4Save.put(FIELD_FORMATS, jsonEncoder.encode(JSOHelper.convertMapToJavascriptObject(summaryField_Formats)));



            descriptor4Save.put(FIELD_ORDER, removeFL(jsonEncoder.encode(Arrays.asList(fname))));
            final String hidden = jsonEncoder.encode(Arrays.asList(fview));
            descriptor4Save.put(FIELD_HIDDEN, removeFL(hidden));

            String[] groupByFields = getGroupByFields();
            if (groupByFields!=null && groupByFields.length>0)
            {
                for (int i = 0; i < groupByFields.length; i++)
                    groupByFields[i]="'"+groupByFields[i]+"'";
                descriptor4Save.put(M_GROUP_ORDER, removeFL(jsonEncoder.encode(Arrays.asList(groupByFields))));
            }
            else
                descriptor4Save.remove(M_GROUP_ORDER);

            descriptor4Save.put(M_GROUP, getCanMultiGroup());

            SortSpecifier[] specifiers = getSort();
            if (specifiers!=null && specifiers.length>0)
            {
                String[] filedSort=new String[specifiers.length];
                String[] filedSortDirection=new String[specifiers.length];
                for (int i = 0; i < specifiers.length; i++) {
                    filedSort[i]="'"+specifiers[i].getField()+"'";
                    filedSortDirection[i]="'"+specifiers[i].getSortDirection().getValue()+"'";
                }
                descriptor4Save.put(M_SORT_ORDER, removeFL(jsonEncoder.encode(Arrays.asList(filedSort))));
                descriptor4Save.put(M_SORT_ORDER_DIRECTION, removeFL(jsonEncoder.encode(Arrays.asList(filedSortDirection))));
            }
            else
            {
                descriptor4Save.remove(M_SORT_ORDER);
                descriptor4Save.remove(M_SORT_ORDER_DIRECTION);
            }
            descriptor4Save.put(M_SORT, getCanMultiSort());


            String[] sformulas = new String[formulas.size()];
            int i=0;
            for (JSFormula jsFormula : formulas.values())
            {
                sformulas[i]=jsonEncoder.encode(jsFormula.getJsObj());
                i++;
            }

            String resString="[";
            for (int i1 = 0, sformulasLength = sformulas.length; i1 < sformulasLength; i1++)
            {
                if (i1!=0)
                    resString+=",";
                resString+=sformulas[i1];
            }
            resString+="]";
            descriptor4Save.put(M_FUNCTION, resString);
            descriptor4Save.put(M_COLNUM, String.valueOf(colNum));

//            var RV=srv.dDay(L,M); return RV>10?'>10':' '+RV;
        }

        return descriptor;
    }



    public void setDescOperation(DescOperation descriptor)
    {
        this.descriptor = descriptor;
    }

    private DescOperation descriptor;


    private String removeFL(String encode) {
        encode=encode.substring(1);
        if (encode.length()>0)
            encode=encode.substring(0, encode.length() - 1);
        return encode;
    }

    private String removeFL2(String encode) {

        encode = encode.replace("\\r", "\r");
        encode = encode.replace("\\t", "\t");
        encode = encode.replace("\\n", "\n");
        return encode;
    }


    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    String viewName;





//    protected void setMetaInfo(ListGridField[] fields) {
//        fieldMetaDS = new DataSource();
//        fieldMetaDS.setClientOnly(true);
//
//        filterDS = new DataSource();
//        filterDS.setClientOnly(true);
//
//
//        DataSourceTextField nameField = new DataSourceTextField("name");
//        DataSourceTextField titleField = new DataSourceTextField("title");
//        DataSourceTextField typeField = new DataSourceTextField("type");
////        DataSourceTextField editorType = new DataSourceTextField("editorType");
////        DataSourceField editorProperties = new DataSourceField("editorProperties",FieldType.BINARY);
//
//
////        fieldMetaDS.setFields(nameField, titleField, typeField,editorType,editorProperties);
//
//        fieldMetaDS.setFields(nameField, titleField, typeField);
//
//        ListGridRecord fieldMetaData[] = new ListGridRecord[fields.length];
//
//        for (int i = 0, fieldsLength = fields.length; i < fieldsLength; i++)
//        {
//            ListGridField field = fields[i];
//
//            final ListGridFieldType type = field.getType();
//            if (type!=null)
//            {
//                ListGridRecord record = new ListGridRecord();
//                record.setAttribute("name", field.getName());
//                record.setAttribute("title", field.getTitle());
//
//                record.setAttribute("type", type.toString());
//
////        TODO проверка фильтра (НЕ РАБОТАЕТ !@#$!!!!)
////                if ("ND".equals(field.getName()))
////                {
////                    RelativeDateItem rangeItem = new RelativeDateItem();
////                    record.setAttribute("editorType", rangeItem.getAttribute("editorType"));
////                    record.setAttribute("editorProperties", rangeItem.getConfig());
////                }
////          TODO проверка фильтра  (НЕ РАБОТАЕТ !@#$!!!!)
//
//
//                fieldMetaData[i] = record;
//
//                DataSourceField dsField = new DataSourceField(field.getName(), FieldType.valueOf(type.name()));
//                dsField.setValidOperators(OperatorId.EQUALS,OperatorId.NOT_EQUAL,
//                        OperatorId.CONTAINS,OperatorId.ICONTAINS,
//                        OperatorId.STARTS_WITH,OperatorId.ISTARTS_WITH,
//                        OperatorId.ENDS_WITH,OperatorId.IENDS_WITH,
//                        OperatorId.BETWEEN,OperatorId.LESS_THAN,OperatorId.LESS_OR_EQUAL,OperatorId.GREATER_THAN,
//                        OperatorId.GREATER_OR_EQUAL,OperatorId.NOT_NULL,OperatorId.NOT,OperatorId.IS_NULL);
//
//        //TODO проверка фильтра (РАБОТАЕТ для любого типа, полезно например для ограничения мно-в ввода)
////                if ("ND".equals(field.getName()))
////                {
////                    DateRangeItem rangeItem = new DateRangeItem();
////                    rangeItem.setWidth("*");
//
////                    DateRangeItem rangeItem = new DateRangeItem();
////                    rangeItem.setWidth("*");
//
////                    DateItem rangeItem = new DateItem();
//
////                    MiniDateRangeItem rangeItem = new MiniDateRangeItem();
////                     RelativeDateItem rangeItem = new RelativeDateItem();
//
////                     ComboBoxItem rangeItem = new ComboBoxItem();
////                     rangeItem.setValueMap("Cat", "Dog", "Giraffe", "Goat", "Marmoset", "Mouse");
////                    rangeItem.setShowTitle(false);
////                    dsField.setEditorProperties(rangeItem);
//                    //dsField.getAttributes();
////                }
//   //TODO проверка фильтра (РАБОТАЕТ)
//
//
//                filterDS.addField(dsField);
//            }
//        }
//        fieldMetaDS.setCacheData(fieldMetaData);
//    }


    final long  endDt=new Date(8099,0,1).getTime();

    public ListGridField[] addFormulaFields(ListGridField... fields)
    {
        if (descriptor!=null)
        {
            {
                String colNum=(String) descriptor.get(M_COLNUM);
                if (colNum!=null)
                {
                    long _colNum=0;

                    try {
                        _colNum=Long.parseLong(colNum);
                    } catch (NumberFormatException e) {
                        //
                    }
                    if (_colNum>this.colNum)
                        this.colNum=_colNum;
                }
            }

            String _sFunctions= (String) descriptor.get(M_FUNCTION);
            if (_sFunctions!=null && _sFunctions.length()>0)
            {
                formulas.clear();
                Object[] jsFunctions = JSOHelper.convertToJavaObjectArray(JSONEncoder.decode(_sFunctions));
                ListGridField[] _fields=new ListGridField[fields.length+jsFunctions.length];
                for (int i = 0, fieldsLength = fields.length; i < fieldsLength; i++)
                    _fields[i]=fields[i];
                for (int i1 = 0, jsFunctionsLength = jsFunctions.length; i1 < jsFunctionsLength; i1++)
                {
                    Object jsFunction = jsFunctions[i1];
                    JSFormula jsf = new JSFormula((JavaScriptObject) jsFunction);
                    formulas.put(jsf.getFName(), jsf);
                    ListGridField nlgf = new ListGridField(jsf.getFName(), jsf.getTitle());
                    nlgf.setType(jsf.getFieldType());
                //Добавить универсальный апдейтер после инициализации всех полей, а пока просто добавь поля.
                    _fields[fields.length+i1]=nlgf;
                }
                fields=_fields;
            }
        }

        return fields;
    }

    public void setFields(ListGridField... fields)
    {
        if (descriptor!=null)
        {
            String jsFOrder= (String) descriptor.get(FIELD_ORDER);
            if (jsFOrder!=null)
            {

                Map<String,ListGridField> nm2lg= new LinkedHashMap<String,ListGridField>();
                for (ListGridField field : fields)
                    nm2lg.put(field.getName(),field);


                LinkedList<ListGridField> listOfFields = new LinkedList<ListGridField>();
                String[] names=JSOHelper.convertToJavaStringArray(JSONEncoder.decode(jsFOrder));
                for (String name : names)
                       listOfFields.add(nm2lg.remove(name));

                listOfFields.addAll(nm2lg.values());

                fields=listOfFields.toArray(new ListGridField[listOfFields.size()]);
            }

            String jsHidden= (String) descriptor.get(FIELD_HIDDEN);
            if (jsHidden!=null)
            {
                Object[] hidden=JSOHelper.convertToArray(JSONEncoder.decode(jsHidden));
                for (int i = 0; i < hidden.length; i++)
                    if (fields[i]!=null)
                        fields[i].setHidden((Boolean)hidden[i]);
            }
        }


        boolean isNullField=false;
        for (ListGridField field : fields)
        {
            if (field==null)
            {
                isNullField=true;
                continue;
            }

            if (ListGridFieldType.DATETIME.equals(field.getType()))
            {
                    field.setCellFormatter(new CellFormatter()
                    {
                        @Override
                        public String format(Object value, ListGridRecord record, int rowNum, int colNum)
                        {
                            if (value instanceof Date)
                            {
                                if (((Date)value).getTime()>=endDt)
                                   return "до отмены"; //TODO Подпорка
                                else
                                    return DateUtil.formatAsShortDatetime((Date)value);
                            }
                            return value==null?null:value.toString();
                        }
                    });
            }
            else if (ListGridFieldType.DATE.equals(field.getType()))
            {
                field.setCellFormatter(new CellFormatter()
                {
                    @Override
                    public String format(Object value, ListGridRecord record, int rowNum, int colNum)
                    {
                        if (value instanceof Date)
                        {
                            if (((Date)value).getTime()>=endDt)
                               return "до отмены"; //TODO Подпорка
                            else
                                return DateUtil.formatAsNormalDate((Date)value);
                        }
                        return value==null?null:value.toString();
                    }
                });
            }
            if (field.getName().equals("KD_ND") || field.getName().equals("DT_KD_ND"))  //TODO подпорка!!!!
            {
                field.setCellFormatter(new CellFormatter()
            {
                @Override
                public String format(Object value, ListGridRecord record, int rowNum, int colNum)
                {
                        Integer tlen=(Integer)value;
                        if (tlen!=null)
                        {
                            int dn=tlen/(24*60);
                            int h=(tlen-dn*24*60)/60;
                            int min=tlen-dn*24*60-h*60;

                            if (min==0 && h ==0 && dn==0)
                                return "0 м";

                            String res="";
                            if (dn>0)
                                res+=dn+" д ";
                            if (h>0)
                                res+=h+" ч ";
                            if (min>0)
                                res+=min+" м";

                            return res;
                        }

                        return null;
                }
            });
            }
            if (field.getName().equals("W_KD_ND"))  //TODO подпорка!!!!
            {
                field.setCellFormatter(new CellFormatter() {
                    @Override
                    public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
                        Integer tlen = (Integer) value;
                        if (tlen != null) {
                            int dn = tlen / (24 * 60);
                            int h = (tlen - dn * 24 * 60) / 60;
                            int min = tlen - dn * 24 * 60 - h * 60;

                            if (min == 0 && h == 0 && dn == 0)
                                return "0 м";

                            String res = "";
                            if (dn > 0)
                                res += dn + " д ";
                            if (h > 0)
                                res += h + " ч ";
                            if (min > 0)
                                res += min + " м";
                            if (res.length() == 0) res = "до отмены";
                            return res;
                        }

                        return null;


                    }
                });
            }

        }


        if (isNullField)
        {
            List<ListGridField> lgf = new LinkedList<ListGridField>();
            for (ListGridField field : fields)
                if (field!=null)
                    lgf.add(field);
            fields = lgf.toArray(new ListGridField[lgf.size()]);
            super.setFields(fields);
        }
        else
            super.setFields(fields);


        if (descriptor!=null)
        {
            {

                for (JSFormula formula : formulas.values())
                {
                    BuilderDlg.updateFormulaByNewParams(formula, BuilderDlg.getParamters(BuilderDlg.getParametersByFields(fields)));
                    final Map<String,String> varName2NameInRecord = formula.getVarName2NameInRecord();
                    final JavaScriptObject foo = EvalUtils.buildFunction(formula.getExpressionValue(), varName2NameInRecord);
                    IDataFlowCtrl ctrl = getCtrl();
                    DataDSCallback updater=new FormulaUpdater(formula,foo,varName2NameInRecord);
                    formula.setUpdater(updater);
                    ctrl.addAfterUpdater(updater);
                }
            }
        //Группировки и сортировки
            String jsSortOrderFields= (String) descriptor.get(M_SORT_ORDER);
            String jsSortOrderDirectionFields= (String) descriptor.get(M_SORT_ORDER_DIRECTION);
            if (jsSortOrderFields!=null && jsSortOrderDirectionFields!=null)
            {
                Object[] sortOrderFields=JSOHelper.convertToArray(JSONEncoder.decode(jsSortOrderFields));
                Object[] sortOrderDirection=JSOHelper.convertToArray(JSONEncoder.decode(jsSortOrderDirectionFields));
                if (sortOrderFields!=null && sortOrderFields.length>0)
                {
                    SortSpecifier[] specifiers=new SortSpecifier[sortOrderFields.length];

                    for (int i = 0, sortOrderFieldsLength = sortOrderFields.length; i < sortOrderFieldsLength; i++) {
                        specifiers[i]=new SortSpecifier(String.valueOf(sortOrderFields[i]), SortDirection.valueOf(String.valueOf(sortOrderDirection[i]).toUpperCase()));
                    }
                    super.setSort(specifiers);
                }
            }

            String jsGroupOrderFields= (String) descriptor.get(M_GROUP_ORDER);
            if (jsGroupOrderFields!=null)
            {
                Object[] groupOrderFields= JSOHelper.convertToArray(JSONEncoder.decode(jsGroupOrderFields));
                if (groupOrderFields!=null && groupOrderFields.length>0)
                {
                    String[] grpFields=new String[groupOrderFields.length];
                    for (int i = 0; i < grpFields.length; i++)
                        grpFields[i]=(String)groupOrderFields[i];


//                    super.setShowGroupSummaryInHeader(true);
//                    super.setShowGroupSummary(true);
//                    for (ListGridField field : fields)
//                        if (field.getName().equals(grpFields[grpFields.length-1]))
//                        {
//                            field.setSummaryFunction(SummaryFunctionType.COUNT);
//                            field.setShowGroupSummary(true);
//                        }
//                        else
//                        {
//                            field.setIncludeInRecordSummary(false);
//                            field.setShowGroupSummary(false);
//                        }

                    super.groupBy(grpFields);
                }
            }

            //Форматирование
            if (descriptor.get(FIELD_FORMATS) != null) {
                Map<String, String> formats = JSOHelper.convertToMap(JSONEncoder.decode((String) descriptor.get(FIELD_FORMATS)));
                ListGridField[] allFields = this.getAllFields();

                for (int i = 0, allFieldsLength = allFields.length; i < allFieldsLength; i++) {
                    allFields[i].setAttribute(TablesTypes.AGGREGATE_FIELD_FORMAT_KEY, formats.get(allFields[i].getName()));


                }
            }


            if (descriptor.get(FIELD_AGGREGATES) != null)
                AggregatesUtils.setAggregates(this, JSOHelper.convertToMap(JSONEncoder.decode((String) descriptor.get(FIELD_AGGREGATES))));

        }



//        setMetaInfo(fields);

    }

    public void addGridField(ListGridField lgf,int ix)
    {
        List<ListGridField> ll = new LinkedList<ListGridField>(Arrays.asList(super.getAllFields()));
        if (ix>=0)
            ll.add(ix,lgf);
        else
            ll.add(lgf);
        updateFields(ll);
    }

    public void addGridField(ListGridField[] lgf)
    {
        List<ListGridField> ll = new LinkedList<ListGridField>(Arrays.asList(super.getAllFields()));
        Collections.addAll(ll, lgf);
        updateFields(ll);
    }

    public void delGridField(String fNames)
    {
        delGridField(new String[]{fNames});
    }

    public void delGridField(String[] fNames)
    {
        List<ListGridField> ll = new LinkedList<ListGridField>(Arrays.asList(super.getAllFields()));
        Set<String> fieldSetNames=new HashSet<String>();
        Collections.addAll(fieldSetNames,fNames);
        int k=0;
        br:
        {
            for (int i = 0; i < ll.size();)
            {
                ListGridField listGridField = ll.get(i);
                if (fieldSetNames.contains(listGridField.getName()))
                {
                    ll.remove(i);
                    k++;
                    if (k==fieldSetNames.size())
                        break br;
                }
                else
                    i++;
            }
            return;
        }
        updateFields(ll);
    }

    public void replaceGridField(String fName, ListGridField lgf)
    {
        List<ListGridField> ll = new LinkedList<ListGridField>(Arrays.asList(super.getAllFields()));

        for (int i = 0, llSize = ll.size(); i < llSize; i++)
        {
            ListGridField listGridField = ll.get(i);
            if (listGridField.getName().equals(fName))
            {
                ll.remove(i);
                break;
            }
        }
        ll.add(lgf);
        updateFields(ll);
    }


    private void updateFields(List<ListGridField> ll) {
        super.setFields(ll.toArray(new ListGridField[ll.size()]));
        final BMetaConstructor bMetaConstructor = new BMetaConstructor();
        bMetaConstructor.setListGridFields(this.getAllFields());
        this.setFieldMetaDS(bMetaConstructor.getFieldsMetaDS());
        this.setFilterDS(bMetaConstructor.getFilterDS());
    }


    public void destroy()
    {
        final IDataFlowCtrl ctrl = getCtrl();
        if (ctrl!=null)
            ctrl.stopUpdateData(); //TODO Послать команду очистки серверных ресурсов

        super.destroy(); //TODO проверить что у нас прекарщается опрос сервера, и в дальнейшем необходимо явно очистить серверные ресурсы
    }

    public IServerFilter getServerDataFilter()
    {
        return serverDataFilter;
    }

    public void setServerDataFilter(IServerFilter serverDataFilter)
    {
        this.serverDataFilter = serverDataFilter;
    }

    private IServerFilter serverDataFilter;

    private List<ICliFilter> cliFilters = new LinkedList<ICliFilter>();

    public boolean resetCliWasChanged() {
        final boolean _cliWasChanged = cliWasChanged;
        cliWasChanged=false;
        return _cliWasChanged;
    }

    private boolean cliWasChanged =true;

    public void setCliWasChanged()
    {
        cliWasChanged=true;
    }

    public List<ICliFilter> getCliFilters()
    {
        return new LinkedList<ICliFilter>(cliFilters);
    }

    public ListenerCtrl<Pair<ICliFilter,ICliFilter>> getFilterChangeListenerCtrl()
    {
        return filterChangeListener;
    }

    ListenerCtrl<Pair<ICliFilter,ICliFilter>> filterChangeListener=new ListenerCtrl<Pair<ICliFilter,ICliFilter>>();

    public boolean removeCliFilter(ICliFilter cliFilter)
    {
        boolean remove=false;
        while (this.cliFilters.remove(cliFilter))
            remove=true;

        if (remove)
        {
            cliWasChanged =true;
            filterChangeListener.clickIndex(new Pair(null,cliFilter));
        }
        return remove;
    }

    public void replaceCliFilter(ICliFilter newFilter,ICliFilter oldFilter)
    {
        boolean remove=false;
        if (oldFilter!=null)
        {
            while (this.cliFilters.remove(oldFilter))
                remove=true;
        }

        if (newFilter!=null && !cliFilters.contains(newFilter))
        {
            cliFilters.add(newFilter);
            this.cliWasChanged=true;
            filterChangeListener.clickIndex(new Pair(newFilter,oldFilter));
        }
        else if (remove)
        {
            this.cliWasChanged=true;
            filterChangeListener.clickIndex(new Pair(newFilter,oldFilter));
        }


    }


    public IDataFlowCtrl getCtrl() {
        return ctrl;
    }

    public void setCtrl(IDataFlowCtrl ctrl) {
        this.ctrl = ctrl;
    }

    protected IDataFlowCtrl ctrl;




    public interface IHoverGetter
    {
        Canvas getCellHoverComponent(ListGrid grid,Record record, Integer rowNum, Integer colNum);
    }


    public IHoverGetter getHover() {
        return hover;
    }

    public void setHoverGetter(IHoverGetter hover) {
        this.hover = hover;
    }

    private IHoverGetter hover;

    @Override
    protected Canvas getCellHoverComponent(Record record, Integer rowNum, Integer colNum)
    {
//        Canvas hover = new DetailViewer();
//        hover.setWidth(200);
//        hover.setDataSource(ItemSupplyXmlDS.getInstance());
//        Criteria criteria = new Criteria();
//        criteria.addCriteria("itemID", record.getAttribute("itemID"));
//        hover.fetchData(criteria);

        if (hover!=null)
            return hover.getCellHoverComponent(this,record,rowNum,colNum);
        else
            return super.getCellHoverComponent(record, rowNum, colNum);
    }

//    public List<IOperation> getFiltersOperations()
//    {
//        if (filtersOperations ==null)
//            filtersOperations = new LinkedList<IOperation>();
//        return filtersOperations;
//    }

//    public boolean addFilter(IOperation operation)
//    {
//        if (filtersOperations ==null)
//            filtersOperations = new LinkedList<IOperation>();
//        for (final IOperation filter : filtersOperations)
//        {
//            if (filter.getOperationId()==operation.getOperationId())
//            {
////                SC.say("Фильтр уже применен");
//                String message = "Фильтр уже применен,удалить его?";
//                if (filter.getTypeOperation().equals(IOperation.TypeOperation.addGroping))
//                    message = "Группировка уже применена,расгруппировать?";
//
//                SC.confirm(message, new BooleanCallback() {
//                    public void execute(Boolean value) {
//                        if (value) {
//                            filtersOperations.remove(filter);
//                            ListGridWithDesc.this.applyClientFilters();
//                        }
//                    }
//                });
//                return false;
//            }
//        }
//        List<IOperation> gr=new LinkedList<IOperation>();
//        List<IOperation> lfilters=new LinkedList<IOperation>();
//        List<IOperation> ldatas=new LinkedList<IOperation>();
//        for (IOperation filter : filtersOperations)
//        {
//            switch(filter.getTypeOperation())
//            {
//                case addGroping:
//                    gr.add(filter);
//                    break;
//                case addFilter:
//                    lfilters.add(filter);
//                break;
//            }
//        }
//
//        if (operation.getTypeOperation().equals(IOperation.TypeOperation.addGroping))
//            gr.add(operation);
//        else
//            lfilters.add(operation);
//
//        filtersOperations.clear();
//        filtersOperations.addAll(lfilters);
//        filtersOperations.addAll(gr);
//
//        return true;
//    }


//    public void applyClientFilters()
//    {
//        if (filtersOperations !=null && filtersOperations.size()>0)
//            for (IOperation filter : filtersOperations)
//                    filter.operate(this);
//        else
//            setData(getCacheData());
//    }


//    public void reBuildServerFilters()
//    {
//        if (serverFilters!=null && serverFilters.size()>0)
//        {
//            final IDataFlowCtrl ctrl=getCtrl();
//            ctrl.addAfterUpdater(new DSCallback()
//            {
//                @Override
//                public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest)
//                {
//                    Map<String,List<String>> param2List=null;
//                    for (IServerFilter filter : serverFilters)
//                        param2List=filter.append2Criteria(ctrl.getCriteria(), param2List);
//
//                    ctrl.setFullDataUpdate();
//                    ctrl.removeAfterUpdater(this);
//                }
//            });
//
//        }
//    }
//    private List<IOperation> filtersOperations;


    public RecordList getCacheData() {
        return data;
    }

    public void setCacheData(RecordList data) {
        this.data = data;
    }

    private RecordList data;


    protected Map<String,JSFormula> formulas=new LinkedHashMap<String,JSFormula>();


    private Map<String,MenuItem[]> fldName2Items=new HashMap<String,MenuItem[]>();
    protected MenuItem[] getHeaderContextMenuItems(final Integer fieldNum)
    {
        MenuItem[] rv = super.getHeaderContextMenuItems(fieldNum);
        ListGridField fld = this.getField(fieldNum);
        final String fldName = fld.getName();
        MenuItem[] addMenuItems = fldName2Items.get(fldName);

        if (addMenuItems==null)
        {
            int newLn = 1;
            final boolean containsField = formulas.containsKey(fldName);


            final String[] userAction= new String[1];
            final JSFormula jsf;
            if (containsField)
            {
                newLn+=2;
                jsf=formulas.get(fldName);
            }
            else
            {
                jsf=new JSFormula(BuilderDlg.getParamters(BuilderDlg.getParametersGrid(this)));
                jsf.setFName(jsf.getFName() + "_" + getNextColNum());
            }


            addMenuItems=new MenuItem[newLn];
//            final IFormulaContainer container= new IFormulaContainer()
//            {
//
//                @Override
//                public JSFormula getFormula()
//                {
//                    return jsf;
//                }
//
//                @Override
//                public void setFormula(final JSFormula formula)
//                {
//
//                    try
//                    {
//                        final Map<String,String> varName2NameInRecord = formula.getVarName2NameInRecord();
//                        final JavaScriptObject foo = EvalUtils.buildFunction(formula.getExpressionValue(), varName2NameInRecord);
//                        IDataFlowCtrl ctrl = getCtrl();
//                        DataDSCallback updater=new FormulaUpdater(formula,foo,varName2NameInRecord);
//
//                        final String title = formula.getTitle();
//                        ListGridField lf = new ListGridField(formula.getFName(),title);
//                        lf.setType(formula.getFieldType());
//
//                        if (userAction[0].equals(EDIT_FIELD))
//                        {
//                            formulas.remove(jsf.getFName());
//                            final DSCallback oldUpdater = jsf.getUpdater();
//                            if (oldUpdater != null)
//                                ctrl.removeAfterUpdater(oldUpdater);
//                            if (    !jsf.getFieldType().equals(formula.getFieldType())
//                                    || !jsf.getTitle().equals(formula.getTitle()))
//                                replaceGridField(jsf.getFName(), lf);
//                        }
//                        else if (userAction[0].equals(ADD_FIELD))
//                        {
//                            if (containsField)
//                            {//корретировка имени поля при добалвнеии
//                                formula.setFName(JSFormula.NEW_CALCULATE_FILED + "_" + getNextColNum());
//                                lf.setName(formula.getFName());
//                            }
//                            addGridField(lf);
//                        }
//                        else
//                        {
//                            throw new UnsupportedOperationException("Can't find operation for action param:" + userAction[0]);
//                        }
//                        formula.setUpdater(updater);
//                        ctrl.addAfterUpdater(updater);
//                        formulas.put(formula.getFName(),formula);
//                    }
//                    finally
//                    {
//                        fldName2Items.clear();
//                    }
//                }
//            };

            final IFormulaContainer container=createIFormulaContainer(jsf, userAction,containsField,-1);
            if (containsField)
            {
                addMenuItems[0]=new MenuItem("Редактировать поле...");
                addMenuItems[0].addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler()
                {
                    @Override
                    public void onClick(MenuItemClickEvent event)
                    {
                        userAction[0]= EDIT_FIELD;
                        BuilderDlg.createViewFilterOptions(BuilderDlg.getParametersGrid(ListGridWithDesc.this), container).show();
                    }
                });

                addMenuItems[1]=new MenuItem("Удалить поле");
                addMenuItems[1].addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler()
                {
                    @Override
                    public void onClick(MenuItemClickEvent event)
                    {
                        try {
                            userAction[0]= DEL_FIELD;
                            delJSFormula(jsf.getFName());
//                            formulas.remove(jsf.getFName());
//                            final DSCallback oldUpdater = jsf.getUpdater();
//                            if (oldUpdater!=null)
//                                ctrl.removeAfterUpdater(oldUpdater);
//                            delGridField(jsf.getFName());
                        }
                        finally
                        {
                            fldName2Items.clear();
                        }
                    }
                });
            }

            addMenuItems[addMenuItems.length-1]=new MenuItem("Вычисляемое поле...");
            addMenuItems[addMenuItems.length-1].addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
                @Override
                public void onClick(MenuItemClickEvent event) {
                    userAction[0] = ADD_FIELD;
                    BuilderDlg.createViewFilterOptions(BuilderDlg.getParametersGrid(ListGridWithDesc.this), container).show();
                }
            });


            fldName2Items.put(fldName,addMenuItems);
        }


        MenuItem[] _rrv=new MenuItem[rv.length+addMenuItems.length];
        for (int i = 0, rvLength = rv.length; i < rvLength; i++)
            _rrv[i]= rv[i];
        for (int i = 0, rvLength = addMenuItems.length; i < rvLength; i++)
            _rrv[i+rv.length]= addMenuItems[i];
        return _rrv;
    }

    public void addJSFormula(JSFormula jsf,boolean ignoreIfExists,int ix)
    {
        final boolean containsField = formulas.containsKey(jsf.getFName());

        if (containsField && !ignoreIfExists)
            return;

        IFormulaContainer fcont = new IFormulaContainerImpl(jsf, new String[]{ADD_FIELD}, containsField,ix);
        fcont.setFormula(jsf);
    }

    public void delJSFormula(final String fName)
    {
        final boolean containsField = formulas.containsKey(fName);
        if (containsField)
        {
            JSFormula jsf=formulas.remove(fName);
            if (jsf!=null)
            {
                final DSCallback oldUpdater = jsf.getUpdater();
                if (oldUpdater!=null)
                    ctrl.removeAfterUpdater(oldUpdater);
            }
            delGridField(fName);
        }
    }



    public JSFormula createEmptyJSFormula(String fName)
    {
        JSFormula jsf = new JSFormula(BuilderDlg.getParamters(BuilderDlg.getParametersGrid(this)));
        if (fName==null)
            do
            {
                fName=JSFormula.NEW_CALCULATE_FILED + "_" + getNextColNum();
            }
            while (formulas.containsKey(fName));
        jsf.setFName(fName);
        return jsf;
    }

    public IFormulaContainer createIFormulaContainer(JSFormula jsf,String[] userAction,boolean containsField,int ix)
    {
        return new IFormulaContainerImpl(jsf,userAction,containsField,ix);
    }


    protected class IFormulaContainerImpl implements IFormulaContainer
    {

        final private JSFormula jsf;
        final String[] userAction;
        final boolean containsField;
        private int ix;

        public IFormulaContainerImpl(JSFormula jsf,String[] userAction,boolean containsField,int ix)
        {
               this.jsf=jsf;
               this.userAction=userAction;
               this.containsField=containsField;
                this.ix = ix;
        }

        @Override
        public JSFormula getFormula()
        {
            return jsf;
        }

        @Override
        public void setFormula(final JSFormula formula)
        {

            try
            {
                final Map<String,String> varName2NameInRecord = formula.getVarName2NameInRecord();
                final JavaScriptObject foo = EvalUtils.buildFunction(formula.getExpressionValue(), varName2NameInRecord);
                IDataFlowCtrl ctrl = getCtrl();
                DataDSCallback updater=new FormulaUpdater(formula,foo,varName2NameInRecord);

                final String title = formula.getTitle();
                ListGridField lf = new ListGridField(formula.getFName(),title);
                lf.setType(formula.getFieldType());

                if (userAction[0].equals(EDIT_FIELD))
                {
                    formulas.remove(jsf.getFName());
                    final DSCallback oldUpdater = jsf.getUpdater();
                    if (oldUpdater != null)
                        ctrl.removeAfterUpdater(oldUpdater);
                    if (    !jsf.getFieldType().equals(formula.getFieldType())
                            || !jsf.getTitle().equals(formula.getTitle()))
                        replaceGridField(jsf.getFName(), lf);
                }
                else if (userAction[0].equals(ADD_FIELD))
                {
                    if (containsField)
                    {//корретировка имени поля при добалвнеии
                        formula.setFName(JSFormula.NEW_CALCULATE_FILED + "_" + getNextColNum());
                        lf.setName(formula.getFName());
                    }
                    addGridField(lf,ix);
                }
                else
                {
                    throw new UnsupportedOperationException("Can't find operation for action param:" + userAction[0]);
                }
                formula.setUpdater(updater);
                ctrl.addAfterUpdater(updater);
                formulas.put(formula.getFName(),formula);
            }
            finally
            {
                fldName2Items.clear();
            }
        }
    }



    public ListGridWithDesc() {
    }

    public ListGridWithDesc(JavaScriptObject jsObj) {
        super(jsObj);
    }

    public DiagramDesc getDesc() {
        return desc;
    }

    public void setDesc(DiagramDesc desc) {
        this.desc = desc;
    }

    private DiagramDesc desc;



    public Window getTarget() {
        return target;
    }

    public void setTarget(Window target) {
        this.target = target;
    }

    private Window target;

    public IRolloverHandler getRolloverHandler() {
        return rolloverHandler;
    }

    public void setRolloverHandler(IRolloverHandler rolloverHandler) {
        this.rolloverHandler = rolloverHandler;
        if (rolloverHandler!=null)
            this.rolloverHandler.setListGrid(this);
    }

    IRolloverHandler rolloverHandler = null;

    @Override
    protected Canvas getRollOverCanvas(Integer rowNum, Integer colNum)
    {
        if (rolloverHandler!=null)
            return rolloverHandler.handleRollover(rowNum, colNum);
        return null;
    }

    public interface IRolloverHandler
    {

        Canvas handleRollover(Integer row, Integer col);
        void setListGrid(ListGrid lg);
        ListGrid getListGrid();
    }


    public boolean isContainsFieldInCliFilter(String fieldName)
    {
        final List<ICliFilter> cliFilters1 = getCliFilters();
        for (ICliFilter filter : cliFilters1)
        {
            Criteria criteria = filter.getCriteria();

            if (criteria instanceof AdvancedCriteria)
            {
                final String sFilter = ((AdvancedCriteria) criteria).toJSON();
                if (sFilter.contains(fieldName))
                    return true;
            }
            else
            {
                Map vals = criteria.getValues();
                for (Object key : vals.keySet())
                {
                  boolean isCalcFilter= fieldName.equals(key);
                  isCalcFilter=isCalcFilter || fieldName.equals(vals.get(key));
                  if (isCalcFilter)
                      return true;
                }
            }
        }
        return false;
    }


    protected class FormulaUpdater extends DataDSCallback
    {
        boolean isInit = false;
        long currentTime=System.currentTimeMillis();
        private JSFormula formula;
        private JavaScriptObject foo;
        private Map<String, String> varName2NameInRecord;

        public FormulaUpdater(JSFormula formula,JavaScriptObject foo,Map<String,String> varName2NameInRecord)
        {
            super(0);
            this.formula=formula;
            this.foo = foo;
            this.varName2NameInRecord = varName2NameInRecord;
        }

        @Override
        protected void updateData(Record[] data, boolean resetAll) throws SetGridException
        {
            if (formula.isReCalcFormula() && (System.currentTimeMillis()-currentTime)/1000>=formula.getPeriod())
            {
                isInit=false;//Пересчитать полностью
                if (isContainsFieldInCliFilter(formula.getFName()))
                    setCliWasChanged();
                currentTime=System.currentTimeMillis();
            }
            RecordList rl = getCacheData();
            if (!isInit)
            {
                if (rl != null) {
                    Record[] ra = rl.toArray();
                    for (Record record : ra)
                        record.setAttribute(formula.getFName(), EvalUtils.evalFunction(foo, record, varName2NameInRecord));
                }
                setHiliteState(getHiliteState());
                if (isGrouped())
                    recalculateSummaries();
                markForRedraw();
                isInit = true;
            }
            else if (data != null && data.length > 0)
            {
                for (Record inRecord : data)
                {
                    String id = inRecord.getAttributeAsString(TablesTypes.KEY_FNAME);
                    Integer actual = inRecord.getAttributeAsInt(TablesTypes.ACTUAL);
                    if (actual > 0) {

                        int inRlIx = rl.findIndex(TablesTypes.KEY_FNAME, id);
                        if (inRlIx >= 0) {
                            Record record = rl.get(inRlIx);
                            if (record != null)
                                record.setAttribute(formula.getFName(), EvalUtils.evalFunction(foo, record, varName2NameInRecord));
                        }
                    }
                }
                setHiliteState(getHiliteState());
                if (isGrouped())
                    recalculateSummaries();
                markForRedraw();
            }
        }
    }

    @Override
    public String getCellCSSText(ListGridRecord record, int rowNum, int colNum) {
        return super.getCellCSSText(record, rowNum, colNum);
    }
/* public String getCellCSSTextForExport(ListGridRecord lgr, int row, int col) {

        return getCellCSSText(lgr, row, col);

    }*/
}
