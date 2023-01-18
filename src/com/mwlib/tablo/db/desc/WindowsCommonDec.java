package com.mwlib.tablo.db.desc;

import com.mwlib.tablo.analit2.BusinessUtils;
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
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 07.08.15
 * Time: 13:39
 * Общий класс для "предоставленных" окон и "всех" окон
 */
public abstract class WindowsCommonDec extends BaseTableDesc
{


    static Map<String, int[]> service2Ix = new HashMap<String, int[]>();
    static Map<Integer,String> ix2Service= new HashMap<Integer, String>();
    public static final String SERV = "o_serv";
    public static final String STATUS_PL="STATUS_PL";
    public static final String OVERTIME = "OVERTIME";
    public static final String STATE = "o_state";



    public WindowsCommonDec(boolean test,String tableTypeName,int[] tableTypes)
    {
        this(test);
        this.tableTypeName=tableTypeName;
        this.tableTypes=tableTypes;
    }

    public WindowsCommonDec(boolean test)
    {
        super(test);
        initDescriptor();
    }


    protected String tableTypeName;
    protected int[] tableTypes;



    protected String[] fNames;

    protected FieldTranslator[] fieldTranslator=new FieldTranslator[]
    {
                    new SimpleField(TablesTypes.DOR_NAME,"Дорога",ListGridFieldType.TEXT.toString(),false)
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException
                        {
                            Integer dor_kod=(Integer)tuple.get(TablesTypes.DOR_CODE);
                            if (Directory.isInit())
                            {
                                Directory.RailRoad byDorCode = Directory.getByDorCode(dor_kod);
                                if (byDorCode!=null)
                                    return byDorCode.getNAME();
                            }
                            return String.valueOf(dor_kod);
                        }
                    },
                    new SimpleField("ID_Z","№")
                    {
                        {
//                            autofit=true;
                        }
                    },
                    new SimpleField(SERV,"Служба")
                    {
                        {
//                            autofit=true;
                        }
                    },
                    new AField("PEREG","Перегон")
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

                    },//
                    new DateField2("ND","Начало") ,
                    new DateField2("KD","Конец") ,

                    new DateField2("DT_ND","Начало(ГИД)")
                    {
                        public Object getS(Map<String,ColumnHeadBean> column, Map tuple, Map<String, Object> outTuple) throws FieldException
                        {
                            Timestamp gnd=(Timestamp)tuple.get("DT_ND");
                            if (gnd==null || gnd.getTime()<0)
                                return null;
                            return super.getS(column,tuple,outTuple);
                        }
                    },
                    new DateField2("DT_KD","Конец(ГИД)")
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> outTuple) throws FieldException {

                            Timestamp gnd=(Timestamp)tuple.get("DT_KD");
                            if (gnd==null || gnd.getTime()<0)
                                return null;
                            return super.getS(column,tuple,outTuple);
                        }
                    },

                    new AField(TablesTypes.CRDURL,"Состояние")
                    {
                        {
                            type=ListGridFieldType.LINK.toString();
                        }

                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> outTuple) throws FieldException
                        {
                            return getS((Map<String,ColumnHeadBean> )null,tuple,outTuple);
                        }

                        public Object getS(Map<String,ColumnHeadBean> column, Map tuple, Map<String, Object> outTuple) throws FieldException
                        {
                            String refHead = setRowStyle(tuple, outTuple);
                            LinkObject linkObject = new LinkObject(refHead, "");
                            String id=(String)tuple.get(TablesTypes.KEY_FNAME);
                            id = removeKeySeparator(id);

                            if (id!=null && id.length()>0)
                            {
                                String[] dor2id=id.split(";");
                                linkObject.setLink("http://host_card:port_card/wnd_pr/operative/card.jsp?wid=" + dor2id[1] + "&dorKod=" + dor2id[0] + "&opener=0");
                            }
                            return linkObject;
                        }
                    },

                    new SimpleField(TablesTypes.DOR_CODE,"№ Дороги",ListGridFieldType.INTEGER.toString(),false),
                    new SimpleField(TablesTypes.PRED_ID,"№ Предприятия",ListGridFieldType.INTEGER.toString(),false),
                    new SimpleField(TablesTypes.VID_ID,"№ Службы",ListGridFieldType.INTEGER.toString(),false),
                    new SimpleField(TablesTypes.VID_NAME,"Служба(Гр)",ListGridFieldType.TEXT.toString(),false),
//                    new SimpleField(STATE,"Код события", ListGridFieldType.TEXT.toString(),false),

                    new SimpleField(TablesTypes.STATUS_FACT,"Исполнение",ListGridFieldType.INTEGER.toString(),false),
                    new SimpleField(STATUS_PL,"Планирование",ListGridFieldType.INTEGER.toString(),false),
                    new SimpleField(OVERTIME,"Передержка",ListGridFieldType.INTEGER.toString(),false),

                    new SimpleField(TablesTypes.KEY_FNAME,TablesTypes.KEY_FNAME, ListGridFieldType.TEXT.toString(),false),
                    new SimpleField(TablesTypes.EVTYPE,"Событие", ListGridFieldType.TEXT.toString(),false)
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple)
                        {
                            return TablesTypes.WINDOWS;
                        }
                    },
                    new SimpleField(TablesTypes.EVTYPE_NAME,"Тип События", ListGridFieldType.TEXT.toString(),false)
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple)
                        {
                            return "ОКНА";
                        }
                    },
                    new SimpleField("F_Y_PLAN","Год. план", ListGridFieldType.INTEGER.toString(),false)
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple)
                        {
                            Object id_tuch=tuple.get("ID_TUCH");
                            if (id_tuch!=null && !"0".equals(String.valueOf(id_tuch)))
                                return 1;
                            return 0;
                        }
                    },

//                    new SimpleField("ID_TUCH","Ид. Титульного участка",ListGridFieldType.INTEGER.toString(),false),
                    new SimpleField(TablesTypes.DATATYPE_ID,"Ид.типа события",ListGridFieldType.INTEGER.toString(),false),
                    new SimpleField(TablesTypes.ACTUAL,"Актуальность",ListGridFieldType.INTEGER.toString(),false),
                    new DateField2(EventProvider.DATE_MIN_ND,"Время получения записи",false),
                    new DateField2(EventProvider.COR_MAX_TIME,"Время изменения записи",false)
    };


    protected void initDescriptor()
    {
        try
        {
            if (fNames!=null)
            {
                Map<String,FieldTranslator> fieldTranslatorMap=new HashMap<String,FieldTranslator>();
                List<FieldTranslator> ll=new LinkedList<FieldTranslator>();
                for (FieldTranslator aFieldTranslator : fieldTranslator)
                    fieldTranslatorMap.put(aFieldTranslator.getColumnHeadBean().getName(), aFieldTranslator);
                for (String fname : fNames)
                    if (fieldTranslatorMap.containsKey(fname))
                        ll.add(fieldTranslatorMap.get(fname));
                    else
                        System.out.println("fieldTranslator not contains " + fname);
                fieldTranslator=ll.toArray(new FieldTranslator[ll.size()]);
            }
        }
        catch (FieldException e)
        {//
        }
    }

    @Override
    public String getTableType()
    {
        return tableTypeName;
    }

    @Override
    public  FieldTranslator[] getFieldTranslator() {
        return fieldTranslator;
    }


    @Override
    public int[] getDataTypes()
    {
        return tableTypes;
    }


    static
    {
        int[] p=new int[]{54,46};
        service2Ix.put("П",p);
        int[] e=new int[]{59,60};
        service2Ix.put("Э",e);
        int[] h=new int[]{56,57};
        service2Ix.put("Ш",h);

        int[] h1=new int[]{61};
        service2Ix.put("O",h1);


        for (String s : service2Ix.keySet())
            for (int anIx : service2Ix.get(s))
                ix2Service.put(anIx, s);
    }



    protected String setRowStyle(Map tuple, Map<String, Object> _outTuple) throws FieldException
    {
        return "Карточка";
    }

    public  void addMeta2Type(IMetaProvider metaProvider)
    {
        int typeid=getDataTypes()[0];
        metaProvider.addColumnByEventType(typeid, new ColumnHeadBean(WindowsCommonDec.SERV, WindowsCommonDec.SERV, ListGridFieldType.TEXT.toString()));
        metaProvider.addColumnByEventType(typeid, new ColumnHeadBean(STATE, STATE, ListGridFieldType.TEXT.toString()));
        BusinessUtils.fillMetaByVid(metaProvider, typeid);
    }

    protected IRowOperation _getRowOperation()
    {
        return new IRowOperation()
        {
            public void setObjectAttr(IMetaProvider metaProvider, ColumnHeadBean attr, ResultSet rs, Map<String, Object> tuple) throws Exception
            {
                int typeid = rs.getInt(TablesTypes.DATATYPE_ID);
                String servName = WindowsCommonDec.ix2Service.get(typeid);
                if (servName != null)
                {
                    tuple.put(WindowsCommonDec.SERV,servName);
                    Object o_state = tuple.get(STATE);
                    if (o_state != null && !o_state.toString().contains(String.valueOf(typeid)))
                        o_state = (String) o_state + ',' + typeid;
                    else if (o_state == null)
                        o_state = String.valueOf(typeid);
                    tuple.put(STATE, o_state);
                }
                BusinessUtils.fillVidTupleByPredId(tuple);
            }
        };
    }
}
