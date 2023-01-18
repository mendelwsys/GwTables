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

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 07.07.14
 * Time: 18:20
 * Задержки ГИД
 */
public class DelayGIDTDesc extends BaseTableDesc
{
    private FieldTranslator[] fieldTranslator=new FieldTranslator[]
    {

                    new SimpleField(SERV_NAME,"Поезд")
                    {
        //                        {
        //                            autofit=true;
        //                        }
                    },
                    new SimpleField("TRAIN_ID","Номер<br>Поезда")
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> outTuple) throws FieldException
                        {
                            String train_id=(String)tuple.get("TRAIN_ID");
                            String[] tr1_2=train_id.split(";");
                            String tail="";
                            if (tr1_2.length==2)
                            {
                                train_id=tr1_2[0];
                                tail=tr1_2[1];
                            }
                            int ix=train_id.indexOf("-");
                            train_id=train_id.substring(ix+1);
                            String[] parts=train_id.split("-");
                            String res=parts[0]+" ";
                            for (int i = 1; i < parts.length; i++)
                            {
                                res+=parts[i];
                                if (tail.length()>0 || i<parts.length-1)
                                    res+=" - ";
                            }
                            res+=tail;
                            return res;
                        }
                    },

                    new SimpleField(TablesTypes.DOR_NAME,"Дорога")
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException
                        {
                            int dor_kod=(Integer) tuple.get(TablesTypes.DOR_CODE);
                            if (Directory.isInit())
                            {
                                Directory.RailRoad byDorCode = Directory.getByDorCode(dor_kod);
                                if (byDorCode!=null)
                                    return byDorCode.getNAME();
                            }
                            return String.valueOf(dor_kod);
                        }
                    },


                    new SimpleField("SYS_TYPE_NAME","Тип")
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException
                        {
                            String sys=(String)tuple.get("SYS_TYPE");
                            if ("Kasant".equalsIgnoreCase(sys))
                                return "Отказ";
                            else if ("Kasat".equalsIgnoreCase(sys))
                                return "Нарушение";
                            else
                                return null;
                        }

                    },

                    new SimpleField("PLACE","Место"),

                    new DateField2("ND","Начало") ,
                    new DateField2("KD","Устранение")
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> outTuple) throws FieldException
                        {
                            Timestamp kd=(Timestamp)tuple.get("KD");
                            Timestamp nd=(Timestamp)tuple.get("ND");
                            if (nd!=null && kd!=null && nd.before(kd))
                                return super.getS(column,tuple,outTuple);
                            return null;
                        }
                    },

                    new SimpleField("VID_NAME","Служба")
                    {
//                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> outTuple) throws FieldException
//                        {
//
//                            Integer hoz_id=(Integer)tuple.get("HOZ_ID");
//                            if (hoz_id==null)
//                                hoz_id=0;
//                            BusinessUtils.fillVIDByHozId(outTuple,hoz_id);
//                            return super.getS(column,tuple,outTuple);
//                        }
                    },

                    new SimpleField(TablesTypes.PRED_NAME,"Предприятие")
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> outTuple) throws FieldException
                        {
                            Integer pred_id=(Integer)tuple.get(TablesTypes.PRED_ID);
                            if (pred_id!=null && pred_id!=0)
                            {
                                Directory.Pred pred = Directory.getByPredId(pred_id);
                                if (pred!=null)
                                    return pred.getNAME();
                            }
                            return null;

                        }
                    },

                    new SimpleField(TablesTypes.COMMENT,"Проявление"),
                    new SimpleField("DELAY","Время<br>Задержки<br>Мин",ListGridFieldType.INTEGER.toString(),true)
                    {
                        {
                            alignment=Alignment.CENTER.name();
                        }
                    },
                    new AField(TablesTypes.CRDURL,"Карточка")
                    {
                        {
                            linkText="Карточка";
                            type=ListGridFieldType.LINK.toString();
                        }
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple)
                        {
                            LinkObject linkObject = new LinkObject(linkText, "");
                            String id=(String)tuple.get(TablesTypes.KEY_FNAME);
                            id=removeKeySeparator(id);
                            String sys=(String)tuple.get("SYS_TYPE");
                            if (id!=null && id.length()>0)
                            {
                                String[] dor2id=id.split(";");
                                int ln=dor2id.length;
                                linkObject.setLinkText(dor2id[ln-1]);
                                linkObject.setLink("http://k_host/TabloReport2/KXXXX/ShowOriginalCard?DorKod="+dor2id[ln-2]+"&EventId="+dor2id[ln-1]+"&Type="+sys);
                            }
                            return linkObject;

                        }
                    },
                    new SimpleField("SYS_TYPE","Система",ListGridFieldType.TEXT.toString(),false),

                    new SimpleField(TablesTypes.DOR_CODE,"№ Дороги",ListGridFieldType.INTEGER.toString(),false),
                    new SimpleField(TablesTypes.PRED_ID,"№ Предприятия",ListGridFieldType.INTEGER.toString(),false),
                    new SimpleField(TablesTypes.VID_ID,"№ Службы",ListGridFieldType.INTEGER.toString(),false),
                    new SimpleField(TablesTypes.HOZ_ID,"№ Хозяйства",ListGridFieldType.INTEGER.toString(),false),
                    //new SimpleField(,"№ Нарушения"),

                    //Раскраска по категориям.
                    new SimpleField("ID_KIND","Тип отказа",ListGridFieldType.INTEGER.toString(),false)
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException
                        {
                            Integer rv=(Integer)tuple.get("ID_KIND");
                            if (rv==1)
                                _outTuple.put(TablesTypes.ROW_STYLE,"background-color:#FFBB99;");
                            else if (rv==2)
                                _outTuple.put(TablesTypes.ROW_STYLE,"background-color:#FFFFCC;");
                            return super.getS(column,tuple,_outTuple);
                        }
                    },

                    new SimpleField(TablesTypes.EVTYPE,"Событие", ListGridFieldType.TEXT.toString(),false)
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple)
                        {
                            return TablesTypes.DELAYS_GID;
                        }
                    },
                    new SimpleField(TablesTypes.EVTYPE_NAME,"Тип События", ListGridFieldType.TEXT.toString(),false)
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple)
                        {
                            return "ЗАДЕРЖКА ГИД";
                        }
                    },

                    new SimpleField(TablesTypes.DATATYPE_ID,"Ид.типа события",ListGridFieldType.INTEGER.toString(),false),
                    new SimpleField(TablesTypes.KEY_FNAME,TablesTypes.KEY_FNAME, ListGridFieldType.TEXT.toString(),false),
                    new SimpleField(TablesTypes.ACTUAL,"Актуальность",ListGridFieldType.INTEGER.toString(),false),
                    new DateField2(EventProvider.DATE_MIN_ND,"Время получения записи",false),
                    new DateField2(EventProvider.COR_MAX_TIME,"Время изменения записи",false)
    };


    static Map<String, int[]> service2Ix = new HashMap<String, int[]>();
    static Map<Integer,String> ix2Service= new HashMap<Integer, String>();
    public static final String SERV_NAME = "o_serv_name";
    public static final String SERV_IX = "o_serv_ix";

    public static final String PASS = "Пас";
    public static final String REG = "Пр";
    public static final String CRG = "Гр";

    static
    {
        int[] p=new int[]{74};
        service2Ix.put(PASS,p);
        int[] h=new int[]{75};
        service2Ix.put(REG,h);
        int[] e=new int[]{76};
        service2Ix.put(CRG,e);

        for (String s : service2Ix.keySet())
            for (int anIx : service2Ix.get(s))
                ix2Service.put(anIx, s);
    }




    @Override
    public String getTableType() {
        return TablesTypes.DELAYS_GID;
    }

    public DelayGIDTDesc(boolean test) {
        super(test);
        headerHeight=43;
    }

    @Override
    public FieldTranslator[] getFieldTranslator() {
        return  fieldTranslator;
    }

    public  void addMeta2Type(IMetaProvider metaProvider)
    {
        int typeid=getDataTypes()[0];
        metaProvider.addColumnByEventType(typeid,new ColumnHeadBean(SERV_IX, SERV_IX, ListGridFieldType.INTEGER.toString()));
        metaProvider.addColumnByEventType(typeid,new ColumnHeadBean(SERV_NAME, SERV_NAME, ListGridFieldType.TEXT.toString()));
        BusinessUtils.fillMetaByVid(metaProvider, getDataTypes()[0]);
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
                String servName = getServNameByDataTypeId(typeid);
                if (servName != null)
                {
                    tuple.put(SERV_IX, typeid);
                    tuple.put(SERV_NAME, servName);

                    if (tuple.containsKey(TablesTypes.HOZ_ID)) {
                        Integer hoz_id = (Integer) tuple.get(TablesTypes.HOZ_ID);
                        if (hoz_id==null) hoz_id=0;
                        BusinessUtils.fillVIDByHozId(tuple, hoz_id);
                    }

                }
            }
        };
    }

    public static String getServNameByDataTypeId(int typeid) {
        return DelayGIDTDesc.ix2Service.get(typeid);
    }

    @Override
    public int[] getDataTypes() {
        return new int[]{74,75,76};
    }



}
