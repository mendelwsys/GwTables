package com.mycompany.client.test.evalf;

import com.mycompany.client.ListGridWithDesc;
import com.mycompany.client.apps.App.AppConst;
import com.mycompany.client.apps.App.OptionsViewers;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DragAppearance;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.DrawEvent;
import com.smartgwt.client.widgets.events.DrawHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangeEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangeHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellClickEvent;
import com.smartgwt.client.widgets.grid.events.CellClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.PortalLayout;
import com.smartgwt.client.widgets.layout.VLayout;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 16.04.15
 * Time: 13:23
 * Диалог для
 */
public class BuilderDlg implements Runnable
{

    public static final String letters="ABCDEFGIJKLMNOPQRSTUVWXYZ";
    public static final String KEY_NM = "key";
    public static final String KEY_HD = "Ключ";
    public static final String F_NAME_NM = "fname";
    public static final String F_NAME_HD = "Имя Поля";
    public static final String F_TYPE_NM = "ftype";
    public static final String F_TYPE_HD = "Тип";
    public static final String F_TITLE_NM = "ftitle";
    public static final String F_TITLE_HD = "Поле";

    public static Window createViewFilterOptions(ListGrid params,final IFormulaContainer container)
    {
        final JSFormula formula = container.getFormula().copy();


        Map<String, String> newKey2Fname = getParamters(params);
        updateFormulaByNewParams(formula, newKey2Fname);

        final Window winModal = OptionsViewers.createEmptyWindow(AppConst.CUSTOM_FILTER_OPTIONS_HEADER);
        winModal.setCanDragResize(true);
        winModal.setWidth(490);
        winModal.setHeight(400);



        final DynamicForm form = OptionsViewers.createEmptyForm();
        form.setNumCols(4);
        form.setPadding(10);


        final TextItem titleItem = new TextItem();
        titleItem.setTitle("Заголовок");
        titleItem.setValue(formula.getTitle());


        final ComboBoxItem selectType = new ComboBoxItem();
        selectType.setTitle("Тип");
        selectType.setType("comboBox");
        selectType.setAlign(Alignment.CENTER);

        final LinkedHashMap<String, String> dTypes = new LinkedHashMap<String, String>();
        dTypes.put(String.valueOf(ListGridFieldType.TEXT),"Текст");
        dTypes.put(String.valueOf(ListGridFieldType.INTEGER),"Целое");
        dTypes.put(String.valueOf(ListGridFieldType.FLOAT), "С плавающей точкой");
        dTypes.put(String.valueOf(ListGridFieldType.BOOLEAN), "Двоичное");
        dTypes.put(String.valueOf(ListGridFieldType.DATE), "Дата");
        dTypes.put(String.valueOf(ListGridFieldType.TIME), "Время");
        dTypes.put(String.valueOf(ListGridFieldType.DATETIME), "Врменная метка");
        selectType.setValueMap(dTypes);

        selectType.setAddUnknownValues(false);
        selectType.setAllowEmptyValue(false);
        selectType.setDefaultToFirstOption(true);

        if (formula.getFieldType()!=null)
        {
            selectType.setValue(String.valueOf(formula.getFieldType()));
        }




        final TextAreaItem  formulaValueItem = new TextAreaItem();
        formulaValueItem.setColSpan(3);
        formulaValueItem.setWidth(375);
        formulaValueItem.setTitle("Значение");
        final String expressionValue = formula.getExpressionValue();
        if (expressionValue!=null)
             formulaValueItem.setValue(expressionValue);


        params.addCellClickHandler(new CellClickHandler() {
            @Override
            public void onCellClick(CellClickEvent event) {
                ListGridRecord rec = event.getRecord();
                String key = rec.getAttribute(KEY_NM);
                String value = formulaValueItem.getValueAsString();
                if (value == null) value = "";
                formulaValueItem.setValue(value + key);
            }
        });

        final TextItem recalcPeriod = new TextItem();
        recalcPeriod.setTitle("Период в сек.");
        recalcPeriod.setKeyPressFilter("[0-9]");
        recalcPeriod.setDisabled(!formula.isReCalcFormula());
        if (formula.isReCalcFormula())
            recalcPeriod.setValue(formula.getPeriod());
        else
            recalcPeriod.setValue(0);


        final CheckboxItem reCalculate = new CheckboxItem("reCalculate");
        reCalculate.setTitle("Пересчитывать");
        reCalculate.setValue(formula.isReCalcFormula());
        reCalculate.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                recalcPeriod.setDisabled(reCalculate.getValueAsBoolean());
            }
        });

        form.setFields(titleItem, selectType, formulaValueItem, reCalculate, recalcPeriod);


        final IButton saveButton = new IButton(AppConst.SAVE_BUTTON_OPTIONS);
        saveButton.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                formula.setExpressionValue(formulaValueItem.getValueAsString());
                formula.setFieldType(ListGridFieldType.valueOf((String)selectType.getValue()));
                formula.setReCalcFormula(reCalculate.getValueAsBoolean());
                formula.setTitle(titleItem.getValueAsString());
                try {
                    formula.setPeriod(Integer.parseInt(recalcPeriod.getValueAsString()));
                } catch (NumberFormatException e) {
                    //
                }
                container.setFormula(formula);

                winModal.destroy();
            }
        });

        final IButton cancelButton = new IButton(AppConst.CANCEL_BUTTON_OPTIONS);
        cancelButton.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                winModal.destroy();
            }
        });

        HLayout hLayout= new HLayout();
        hLayout.addMembers(saveButton,cancelButton);
        hLayout.setAlign(Alignment.RIGHT);
        hLayout.setAutoHeight();

        final VLayout vl = new VLayout();
        vl.addMember(form);
        vl.setIsGroup(true);
        vl.setGroupTitle("Формула");
        vl.setPadding(5);
        vl.setMargin(5);
        vl.addMember(hLayout);

        vl.addDrawHandler(new DrawHandler() {
            @Override
            public void onDraw(DrawEvent event)
            {

//                int w=formulaValueItem.getWidth();
//                SC.say(""+w);

            }
        });



        winModal.addItem(params);
        winModal.addItem(vl);


        return winModal;

    }

    public static Map<String, String> getParamters(ListGrid params) {
        Map<String, String> newKey2Fname= new LinkedHashMap<String, String>();
        {
            RecordList recList = params.getRecordList();
            if (recList!=null)
            {
                int ln=recList.getLength();
                for (int i=0;i<ln;i++)
                {
                    Record rec=recList.get(i);
                    newKey2Fname.put(rec.getAttribute(KEY_NM), rec.getAttribute(F_NAME_NM));
                }
            }
        }
        return newKey2Fname;
    }

    public static ListGrid getParametersGrid(ListGridWithDesc desc)
    {
        ListGridField[] fields = desc.getAllFields();


        return getParametersByFields(fields);
    }

    public static ListGrid getParametersByFields(ListGridField[] fields)
    {

        Arrays.sort(fields,new Comparator<ListGridField>() {   //сортировка необъодима для устойчивости формул, т.е. что бы не переименовать их все время при передвижении колонок или скрытия их
            @Override
            public int compare(ListGridField o1, ListGridField o2)
            {
                String name1=o1.getName();
                boolean b1 = name1.startsWith(JSFormula.NEW_CALCULATE_FILED);
                String name2=o2.getName();
                boolean b2 = name2.startsWith(JSFormula.NEW_CALCULATE_FILED);
                if (b1 && !b2)
                  return 1;
                else if (!b1 && b2)
                  return -1;
                else  if (!b1 && !b2)
                  return name1.compareTo(name2);
                else
                {
                    try {
                        int ix1=Integer.parseInt(name1.substring(JSFormula.NEW_CALCULATE_FILED.length() + 1));
                        int ix2=Integer.parseInt(name2.substring(JSFormula.NEW_CALCULATE_FILED.length()+1));
                        return (int)Math.signum(ix1-ix2);
                    } catch (NumberFormatException e) {
                        //
                    }
                    return 0;
                }
            }
        });

        final ListGrid lg = new ListGrid();
        final ListGridField f1 = new ListGridField(KEY_NM, KEY_HD);
        f1.setType(ListGridFieldType.TEXT);
        final ListGridField f2 = new ListGridField(F_TITLE_NM, F_TITLE_HD);
        f2.setType(ListGridFieldType.TEXT);
        final ListGridField f3 = new ListGridField(F_TYPE_NM, F_TYPE_HD);
        f3.setType(ListGridFieldType.TEXT);
        f3.setHidden(true);
        final ListGridField f4 = new ListGridField(F_NAME_NM, F_NAME_HD);
        f4.setHidden(true);
        f4.setType(ListGridFieldType.TEXT);
        lg.setFields(f1, f2, f3, f4);

        final int length = letters.length();
        ListGridRecord[] lgrs = new ListGridRecord[fields.length];
        for (int i = 0, fieldsLength = fields.length; i < fieldsLength; i++)
        {
            ListGridField field = fields[i];
            lgrs[i]= new ListGridRecord();

            if (i< length)
                lgrs[i].setAttribute(KEY_NM, "" + letters.charAt(i));
            else
                lgrs[i].setAttribute(KEY_NM, "" + letters.charAt(i % length) + (i / length));
            lgrs[i].setAttribute(F_NAME_NM, field.getName());
            final ListGridFieldType type = field.getType();
            if (type!=null)
                lgrs[i].setAttribute(F_TYPE_NM, type.getValue());
            lgrs[i].setAttribute(F_TITLE_NM, field.getTitle());
        }
        lg.setRecords(lgrs);
        lg.setShowAllRecords(true);
        if (fields.length>0)
            lg.getRecord(0);
        return lg;
    }

    public static String updateFormulaByNewParams(JSFormula formula, Map<String, String> newKey2Fname)
    {
        String expression=formula.getExpressionValue();
        if (expression==null || expression.length()==0)
            return expression;

        Map<String,String> holder2newKey=new HashMap<String,String>();

        Map<String,String> fname2key=new HashMap<String,String>();
        for (String key : newKey2Fname.keySet())
            fname2key.put(newKey2Fname.get(key),key);


        int[] phIx=new int[]{0};

        boolean wasReplaced=false;

        Map<String, String> oldKey2Fname = formula.getVarName2NameInRecord();
        for (String oldKey : oldKey2Fname.keySet())
        {
            final String oldFName = oldKey2Fname.get(oldKey);
            final String newFName = newKey2Fname.get(oldKey);


            if (!oldFName.equals(newFName))
            {
                if (!expression.contains(oldKey))
                    continue;

                if (!wasReplaced)
                {
                    expression = replaceQuotes(expression, holder2newKey,phIx,'"');
                    expression = replaceQuotes(expression, holder2newKey,phIx,'\'');
                    wasReplaced=true;
                }

                String newKey4OldName=fname2key.get(oldFName);
                if (newKey4OldName==null)
                    newKey4OldName="!_Поле: "+oldFName+" Не найдено_!";


                final String holder = "#_" + phIx[0] + "_#";
                expression=expression.replaceAll("\\b" + oldKey + "\\b", holder);
                holder2newKey.put(holder,newKey4OldName);
                phIx[0]++;
            }
        }

        for (String holder : holder2newKey.keySet())
            expression=expression.replace(holder,holder2newKey.get(holder));

        formula.setVarName2NameInRecord(newKey2Fname);
        formula.setExpressionValue(expression);
        return formula.getExpressionValue();
    }

    static private String replaceQuotes(String expression, Map<String, String> newKey2Holder, int[] phIx,char q)
    {
        int ln = expression.length();
        int ix0=-1;

        for (int i = 0; i < ln; i++)
        {
            if (expression.charAt(i)==q)
            {
                if (ix0<0)
                {
                    if (i==0 || expression.charAt(i-1)!='\\')
                        ix0=i;
                }
                else
                {
                    if (expression.charAt(i-1)!='\\')
                    {
                        String holder = "#_" + phIx[0] + "_#";
                        newKey2Holder.put(holder,(((i+1)<ln)?expression.substring(ix0,i+1):expression.substring(ix0)));
                        expression=expression.substring(0,ix0)+holder+(((i+1)<ln)?expression.substring(i+1):"");
                        phIx[0]++;
                        return replaceQuotes(expression,newKey2Holder, phIx,q);
                    }
                }
            }
        }
        return expression;
    }


//    private String replaseString(String expression, Map<String, String> newKey2Holder, int phIx, String pattern)
//    {
//        RegExp str2=RegExp.compile(pattern);
//        MatchResult res2 = str2.exec(expression);
//        int cnt2=res2.getGroupCount();
//        if (cnt2>0)
//        {
//            String holder = "#_" + phIx + "_#";
//            final String group = res2.getGroup(0);
//            newKey2Holder.put(holder, group);
//            expression=expression.replace(group,holder);
//            return replaseString(expression,newKey2Holder,phIx+1, pattern);
//        }
//        return expression;
//    }


    @Override
    public void run()
    {
        HLayout mainLayout = new HLayout();
        mainLayout.setShowEdges(false);
        mainLayout.setHeight100();
        mainLayout.setWidth100();
        mainLayout.setDragAppearance(DragAppearance.TARGET);

        final PortalLayout portalLayout = new PortalLayout(0);
        portalLayout.setWidth100();
        portalLayout.setHeight100();


        ListGridWithDesc lgd = new ListGridWithDesc();
        lgd.setFields(new ListGridField("FA11","Заг1"),new ListGridField("FA1","Заг2"),new ListGridField("FD1","Заг3"),new ListGridField("C1","Заг4"),new ListGridField("FB11","Заг4"));


        String expr="A?*B*E+f(\"?A@\",C+D)*A*'C+B*X\"1@'";

        final JSFormula jsf;
        {
            final Map<String, String> map1 = new HashMap<String, String>();
            map1.put("A","FA11");
            map1.put("B","FA1");
            map1.put("C","FD1");
            map1.put("D","C1");
            map1.put("E","FB11");
            jsf = new JSFormula("Новое поле", expr, map1);
        }

//        final Map<String, String> map2 = new HashMap<String, String>();
//        map2.put("A1","FA11");
//        map2.put("A","FA1");
//        map2.put("D","FD1");
//        map2.put("E","C13");
//        map2.put("B2","FB11");



        IFormulaContainer container = new IFormulaContainer()
        {

            @Override
            public JSFormula getFormula() {
                return jsf;
            }

            @Override
            public void setFormula(JSFormula formula)
            {

            }
        };

        final ListGrid lg = getParametersGrid(lgd);
        createViewFilterOptions(lg,container).show();

        mainLayout.draw();

    }

}
