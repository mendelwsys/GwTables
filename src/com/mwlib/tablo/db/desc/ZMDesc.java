package com.mwlib.tablo.db.desc;

import com.mwlib.tablo.db.BaseTableDesc;
import com.mwlib.tablo.db.EventProvider;
import com.mwlib.tablo.db.IMetaProvider;
import com.mwlib.tablo.db.IRowOperation;
import com.mwlib.tablo.tables.AField;
import com.mwlib.tablo.tables.DateField2;
import com.mwlib.tablo.tables.FieldTranslator;
import com.mwlib.tablo.tables.SimpleField;
import com.mwlib.utils.db.Directory;
import com.mycompany.common.FieldException;
import com.mycompany.common.LinkObject;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.tables.ColumnHeadBean;
import com.smartgwt.client.types.ListGridFieldType;

import java.sql.ResultSet;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 07.07.14
 * Time: 18:20
 * Таблица ЗМ
 */
public class ZMDesc extends BaseTableDesc
{
    private FieldTranslator[] fieldTranslator = new FieldTranslator[]
            {

                    new SimpleField("ID", "№", ListGridFieldType.INTEGER.toString(), false) {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException {
                            String id = (String) tuple.get(TablesTypes.KEY_FNAME);
                            return formatVal(type, id.split("##")[0]);

                        }
                    },
                    new SimpleField(TablesTypes.DOR_NAME, "Дорога", ListGridFieldType.TEXT.toString(), false) {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException {
                            Integer dor_kod = (Integer) tuple.get(TablesTypes.DOR_CODE);
                            if (Directory.isInit()) {
                                Directory.RailRoad byDorCode = Directory.getByDorCode(dor_kod);
                                if (byDorCode != null)
                                    return byDorCode.getNAME();
                            }
                            return String.valueOf(dor_kod);
                        }
                    },

                    new SimpleField("PRIORITY_LEVEL", "Приоритет", false) {
                        {
//                            autofit=true;
                        }
                    },

                    new SimpleField("CRITIC_LEVEL", "Критичность", false) {
                        {
//                            autofit=true;
                        }
                    },
                   /* new AField("PEREG","Перегон")
                    {

                        public Object getS(Map<String,ColumnHeadBean> column, Map tuple, Map<String, Object> outTuple) throws FieldException
                        {
                            Object stan1code = tuple.get("STAN1_ID");
                            Object stan2code = tuple.get("STAN2_ID");

                            if (Directory.isInit())
                            {
                                Directory.StanDesc stan1=null;
                                if (stan1code!=null && (Integer)stan1code>0)
                                    stan1 = Directory.getByStanId((Integer)stan1code);

                                Directory.StanDesc stan2=null;
                                if (stan2code!=null && (Integer)stan2code>0)
                                    stan2 = Directory.getByStanId((Integer)stan2code);
                                if (stan1!=null && stan1code.equals(stan2code))
                                    return stan1.getNAME();
                                else if (stan1!=null)
                                    return stan1.getNAME()+(stan2!=null?"-"+stan2.getNAME():"");
                                else if (stan2!=null)
                                    return stan2.getNAME();
                                return "";
                            }
                            if (stan2code!=null)
                                return  stan1code.toString()+"-"+stan1code.toString();
                            else
                                return  stan1code.toString();
                        }
                    },
                    new SimpleField(TablesTypes.PRED_NAME,"Исполнитель")
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> outTuple) throws FieldException
                        {
                            Integer pred_id=(Integer)tuple.get(TablesTypes.PRED_ID);
                            if (pred_id!=null && pred_id!=0)
                            {
                                Directory.Pred pred = Directory.getByPredId(pred_id);
                                if (pred!=null)
                                    return pred.getSNAME();
                                else
                                    return String.valueOf(pred_id);
                            }
                            return null;
                        }
                    },
                    new SimpleField(TablesTypes.COMMENT,"Работы")
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> outTuple) throws FieldException
                        {
                            String wkName= (String) super.getS(column,tuple,outTuple);
                            String vname=(String)tuple.get("OBJECT_VNAME");
                            return "<b>"+vname+":</b><br>"+wkName;

                        }

                    },*///
                    new SimpleField(TablesTypes.PRED_NAME, "Предприятие") {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> outTuple) throws FieldException {
                            Integer pred_id = (Integer) tuple.get(TablesTypes.PRED_ID);
                            if (pred_id != null && pred_id != 0) {
                                Directory.Pred pred = Directory.getByPredId(pred_id);
                                if (pred != null)
                                    return pred.getSNAME();
                                else
                                    return String.valueOf(pred_id);
                            }
                            return null;
                        }


                    },
                    new DateField2("TARGETFINISH", "Плановая дата устранения"),
                    new DateField2("ERRORDATE", "Дата обнаружения"),
                    new SimpleField("STATUS", "Статус"),
                    new SimpleField("DESCRIPTION", "Описание"),
                    new AField(TablesTypes.CRDURL, "Карточка")
                    {
                        {
                            linkText = "Карточка";
                            type = ListGridFieldType.LINK.toString();
                        }

                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) {

                            LinkObject linkObject = new LinkObject(linkText, "");

                            String id = (String) tuple.get(TablesTypes.KEY_FNAME);
                            id=removeKeySeparator(id);
                            Integer dor_kod = (Integer) tuple.get(TablesTypes.DOR_CODE);
                            if (id != null && id.length() > 0) {

                                linkObject.setLink("http://asui_host:asui_port/TabloReport2/AXXX/ShowCard?TicketId=" + id + "&amp;DorKod=" + dor_kod.intValue());
                            }
                            return linkObject;

                        }
                    },
                    new SimpleField(TablesTypes.EVTYPE,"Событие", ListGridFieldType.TEXT.toString(),false)
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple)
                        {
                            return TablesTypes.ZMTABLE;
                        }
                    },
                    new SimpleField(TablesTypes.EVTYPE_NAME,"Тип События", ListGridFieldType.TEXT.toString(),false)
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple)
                        {
                            return "ЗМ";
                        }
                    },
                    new SimpleField(TablesTypes.DATATYPE_ID,"Ид.типа события",ListGridFieldType.INTEGER.toString(),false),
                    new SimpleField(TablesTypes.ACTUAL,"Актуальность",ListGridFieldType.INTEGER.toString(),false),
                    new DateField2(EventProvider.DATE_MIN_ND,"Время получения записи",false),
                    new DateField2(EventProvider.COR_MAX_TIME,"Время изменения записи",false)



            };

    public ZMDesc(boolean test) {
        super(test);
    }

    @Override
    public String getTableType() {
        return TablesTypes.ZMTABLE;
    }

    @Override
    public FieldTranslator[] getFieldTranslator() {
        return  fieldTranslator;
    }

    public  void addMeta2Type(IMetaProvider metaProvider)
    {
        int typeid=getDataTypes()[0];
        metaProvider.addColumnByEventType(typeid, new ColumnHeadBean(TablesTypes.DOR_CODE,TablesTypes.DOR_CODE, ListGridFieldType.INTEGER.toString()));
    }

    @Override
    protected IRowOperation _getRowOperation()
    {
        return new IRowOperation()
        {
            @Override
            public void setObjectAttr(IMetaProvider metaProvider, ColumnHeadBean attr, ResultSet rs, Map<String, Object> tuple) throws Exception
            {
                int typeid = rs.getInt(TablesTypes.DATATYPE_ID);
                if (typeid==84 || typeid==85)
                {

                    int dor_code = rs.getInt(TablesTypes.DOR_CODE);
                    tuple.put(TablesTypes.DOR_CODE, dor_code);


                }
            }
        };
    }

    @Override
    public int[] getDataTypes()
    {
        return new int[]{ 84,85/*ЗМ*/,};
    }
}
