package com.mycompany.client.apps.App;

import com.mycompany.client.updaters.BGridConstructor;
import com.mycompany.client.utils.SetGridException;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.grid.ListGridField;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 10.03.15
 * Time: 17:41
 * конструктор грида ленты
 */
public class LentaGridConstructor extends BGridConstructor
{

    private interface IValBuilder
    {
        String[] R();
        boolean isInType(Object type);
        void setColValue(Record inRecord, Record myRecord);
    }

    private static class BaseValBuilder implements IValBuilder
    {

        Set<Object> types;

        public boolean isInType(Object type)
        {
            return types == null || types.contains(type);
        }


        String[] reflection;
        BaseValBuilder(String... reflection)
        {
            this.reflection=reflection;
        }

        BaseValBuilder(Object[] types,String... reflection)
        {
            this(reflection);
            this.types=new HashSet<Object>();
            this.types.addAll(Arrays.asList(types));
        }


        @Override
        public String[] R() {
            return reflection;
        }

        @Override
        public void setColValue(Record inRecord, Record myRecord)
        {
            Object value = inRecord.getAttributeAsObject(reflection[0]);
            if (!reflection[0].equals(reflection[1]))
                myRecord.setAttribute(reflection[1], value);
            if (inRecord!=myRecord)
                myRecord.setAttribute(reflection[0], value);
        }
    }


    private static class LinkValBuilder extends BaseValBuilder
    {

        LinkValBuilder(String... reflection)
        {
            super(reflection);
        }

        LinkValBuilder(Object[] types,String... reflection)
        {
            super(types,reflection);
        }

        @Override
        public void setColValue(Record inRecord, Record myRecord)
        {
            Map value = inRecord.getAttributeAsMap(reflection[0]);
            myRecord.setAttribute(reflection[1], value.get("link"));
//            myRecord.setLinkText((String) value.get("linkText"));
            myRecord.setAttribute(TablesTypes.LINKTEXT, value.get(TablesTypes.LINKTEXT));
        }
    }


    private static class AddStringValBuilder extends BaseValBuilder
    {

        AddStringValBuilder(String... reflection)
        {
            super(reflection);
        }

        AddStringValBuilder(Object[] types,String... reflection)
        {
            super(types,reflection);
        }

        @Override
        public void setColValue(Record inRecord, Record myRecord)
        {
            Object value = inRecord.getAttributeAsObject(reflection[0]);
            if (!reflection[0].equals(reflection[1]))
            {
                Object addValue = null;
                if (reflection.length>4)
                    addValue=reflection[4];
                if (reflection.length>3)
                {
                    Object val3 = inRecord.getAttributeAsObject(reflection[3]);
                    addValue = ((addValue!=null)?(String.valueOf(addValue)+val3):val3);
                }
                myRecord.setAttribute(reflection[1], value+((addValue!=null && String.valueOf(addValue).length()>0)?" ["+addValue+"]":""));
            }
            if (inRecord!=myRecord)
                myRecord.setAttribute(reflection[0], value);
        }
    }

    String[] transl=new String[]{"PRICH_NAME","FNAME","o_serv","o_serv_name","SERV_CHAR",TablesTypes.VID_NAME,
            "PLACE","STAN1_ID","ND","KD","TIM_BEG","TIM_OTM","MRB","MRE",TablesTypes.ROW_STATUS,TablesTypes.STATUS_FACT};

    private static final IValBuilder[] reflection=new IValBuilder[]
    {
        new BaseValBuilder(TablesTypes.KEY_FNAME,TablesTypes.KEY_FNAME),
        new BaseValBuilder(TablesTypes.EVTYPE,TablesTypes.EVTYPE),
        new BaseValBuilder(TablesTypes.EVTYPE_NAME,TablesTypes.EVTYPE_NAME),
        new BaseValBuilder(TablesTypes.DOR_CODE,TablesTypes.DOR_CODE),
        new BaseValBuilder(TablesTypes.DOR_NAME,TablesTypes.DOR_NAME),
        new BaseValBuilder(TablesTypes.PRED_ID,TablesTypes.PRED_ID),
        new BaseValBuilder(TablesTypes.PRED_NAME,TablesTypes.PRED_NAME,"Предприятие"),

        new BaseValBuilder(TablesTypes.COMMENT,TablesTypes.COMMENT,"Описание"),
        new BaseValBuilder("PRICH_NAME",TablesTypes.COMMENT,"Описание"),
        new AddStringValBuilder("FNAME",TablesTypes.COMMENT,"Описание","MRS"),



        new BaseValBuilder("o_serv","SERV","Служба"),
        new BaseValBuilder("o_serv_name","SERV","Служба"),
        new BaseValBuilder("SERV_CHAR","SERV","Служба"),
        new BaseValBuilder(TablesTypes.VID_NAME,"SERV","Служба"),


        new BaseValBuilder("PLACE","PEREG","Место"),
        new BaseValBuilder("PEREG","PEREG","Место"),
        new AddStringValBuilder("STAN1_ID","PEREG","Место","MRSWAY","Путь "),


        new BaseValBuilder(new String[]{TablesTypes.REFUSES,TablesTypes.VIOLATIONS,TablesTypes.WINDOWS},"ND","TIM_BEG","Начало"),
        new BaseValBuilder(new String[]{TablesTypes.REFUSES,TablesTypes.VIOLATIONS,TablesTypes.WINDOWS},"KD","TIM_OTM","Окончание"),
        new BaseValBuilder(new String[]{TablesTypes.WARNINGS},"TIM_BEG","TIM_BEG","Начало"),       //TODO Обязательно задавать отображение для дат самого в себя иначе null
        //TODO сформированный на этапе парсинга дат не содержащихся в кортежах может перезаписаться поверх актуальных дат.
        new BaseValBuilder(new String[]{TablesTypes.WARNINGS},"TIM_OTM","TIM_OTM","Окончание"),
        new BaseValBuilder(new String[]{TablesTypes.VIP_GID},"MRB","TIM_BEG","Начало"),
        new BaseValBuilder(new String[]{TablesTypes.VIP_GID},"MRE","TIM_OTM","Окончание"),

        new BaseValBuilder(new String[]{TablesTypes.WINDOWS}, TablesTypes.STATUS_FACT,TablesTypes.ID_KIND,"Важность"),
        new BaseValBuilder(new String[]{TablesTypes.WARNINGS},TablesTypes.ROW_STATUS,TablesTypes.ID_KIND,"Важность"),
        new BaseValBuilder(new String[]{TablesTypes.VIOLATIONS,TablesTypes.REFUSES},TablesTypes.ID_KIND,TablesTypes.ID_KIND,"Важность"),


        new LinkValBuilder("CRDURL","CRDURL","Подробнее"),
//        new LinkValBuilder("o_state_desc","CRDURL","Подробнее"),
    };


    static Map<String,IValBuilder> col2FieldName=new HashMap<String,IValBuilder>();
    static
    {
        for (IValBuilder aReflection : reflection)
            col2FieldName.put(aReflection.R()[0], aReflection);
    }


    public static final String IMGEV = "IMGEV";

    private String getImgUrl(Object val)
    {
        if ( val.equals(TablesTypes.WINDOWS))
            return "win/time.png";
        else if ( val.equals(TablesTypes.REFUSES))
            return "ref/remove.png";
        else if ( val.equals(TablesTypes.WARNINGS))
            return "warn/exclamation.png";
        else if ( val.equals(TablesTypes.VIOLATIONS))
            return "viol/error.png";
        else if ( val.equals(TablesTypes.VIP_GID))
            return "gid/download.png";
        else
            return val.toString();
    }

    boolean isDateField(ListGridField field)
    {
            return (field.getType().equals(ListGridFieldType.DATETIME)
                ||
                field.getType().equals(ListGridFieldType.DATE)
                ||
                field.getType().equals(ListGridFieldType.TIME)
            );
    }


    private final Map<String, List<ListGridField>> type2Fields;

    public LentaGridConstructor(Map<String, List<ListGridField>> type2Fields) {
        this.type2Fields = type2Fields;
    }

    public void setAddIdDataSource(String addDataUrlId)
    {
        super.setAddIdDataSource("$"+ TablesTypes.LENTA);
    }

    public Record toMyRecord2(Record record)
    {
        Object evType=  record.getAttributeAsObject(TablesTypes.EVTYPE);

        Map link=record.getAttributeAsMap(TablesTypes.CRDURL);
        if (link!=null)
        {
            record.setAttribute(TablesTypes.CRDURL, link.get("link"));
            record.setAttribute(TablesTypes.LINKTEXT, link.get(TablesTypes.LINKTEXT));
        }

        for (String propName : transl)
        {
            IValBuilder builder;
            Object o=record.getAttributeAsObject(propName);
            if (o!=null && (builder=col2FieldName.get(propName))!=null && builder.isInType(evType))
                 builder.setColValue(record,record);
        }

        Object v1 = record.getAttributeAsObject(TablesTypes.EVTYPE);
        record.setAttribute(IMGEV,getImgUrl(v1));

        return record;
    }

//    public MyRecord toMyRecord(Record record)
//    {
//        Map recordProperties = record.toMap();
//
//        Object evType=  recordProperties.get(TablesTypes.EVTYPE);
//
//
//        Object v = recordProperties.remove(TablesTypes.ROW_STYLE);
//        MyRecord myRecord = new MyRecord();
//        for (Object propName : recordProperties.keySet())
//        {
//            Object value = recordProperties.get(propName);
//            if (value instanceof Map)
//            {
//                Map value1 = (Map) value;
//                IValBuilder builder=col2FieldName.get(propName);
//                if (builder!=null)
//                {
//                    if (builder.isInType(evType))
//                        builder.setColValue(recordProperties,myRecord);
//                }
//                else
//                {
//                    myRecord.setAttribute((String) propName, value1.get("link"));
//                    myRecord.setLinkText((String) value1.get("linkText"));
//                }
//            }
//            else
//            {
//                IValBuilder builder=col2FieldName.get(propName);
//                if (builder!=null)
//                {
//                    if (builder.isInType(evType))
//                        builder.setColValue(recordProperties,myRecord);
//                }
//                else
//                    myRecord.setAttribute((String)propName, value);
//            }
//        }
//
//        if (v != null)
//            myRecord.setRowStyle((String) v);
//
//        Object v1 = recordProperties.get(TablesTypes.EVTYPE);
//        myRecord.setAttribute(IMGEV,getImgUrl(v1));
//
//        return myRecord;
//    }


    public void setHeaderGrid(Record[] gridOptions) throws SetGridException
    {

        type2Fields.clear();

        setOptions(gridOptions[0]);

        Map<String,ListGridField>  listGridFieldMap=new LinkedHashMap<String,ListGridField>();


        ListGridField imgField = new ListGridField(IMGEV, "Тип",30);
        imgField.setAlign(Alignment.CENTER);
        imgField.setType(ListGridFieldType.IMAGE);
        imgField.setCanEdit(false);
        listGridFieldMap.put(imgField.getName(),imgField);



        for (Record gridOption : gridOptions)
        {
            String tableType=gridOption.getAttribute("tableType");
            ListGridField[] fields = extractFields(gridOption);
            for (ListGridField field : fields)
            {

                String name=field.getName();

                List<ListGridField> tableTypeFields = type2Fields.get(tableType);
                if (tableTypeFields==null)
                    type2Fields.put(tableType, tableTypeFields=new LinkedList<ListGridField>());
                tableTypeFields.add(field);


                IValBuilder fieldName2Name = col2FieldName.get(name);
                if (fieldName2Name!=null)
                {
                    final String[] ref = fieldName2Name.R();
                    if (!listGridFieldMap.containsKey(ref[1]))
                    {
                        ListGridField field2=new ListGridField(ref[1],field.getTitle());
                        if (ref.length>2)
                            field2.setTitle(ref[2]);
                        field2.setHidden(field.getHidden());
                        field2.setType(field.getType());

                        if (ListGridFieldType.LINK.equals(field2.getType()) && !App01.isDefOpenMode())
                                field2.setTarget("javascript");


                        listGridFieldMap.put(ref[1],field2);
                    }
                }

                if (isDateField(field) && !listGridFieldMap.containsKey(name))
                {
                    ListGridField field2=new ListGridField(name,field.getTitle());
                    field2.setType(field.getType());
                    field2.setHidden(true);
                    field2.setCanHide(false);
                    listGridFieldMap.put(name,field2);
                }

            }
        }
        ListGridField[] fields = listGridFieldMap.values().toArray(new ListGridField[listGridFieldMap.size()]);
        setListGridFields(fields);
    }
}
