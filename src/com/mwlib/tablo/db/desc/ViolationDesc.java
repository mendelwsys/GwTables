package com.mwlib.tablo.db.desc;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 15.07.14
 * Time: 18:04
 * To change this template use File | Settings | File Templates.
 */

import com.mwlib.tablo.analit2.BusinessUtils;
import com.mwlib.tablo.tables.AField;
import com.mwlib.tablo.tables.DateField2;
import com.mwlib.tablo.tables.FieldTranslator;
import com.mwlib.tablo.tables.SimpleField;
import com.mwlib.utils.db.Directory;
import com.mycompany.common.FieldException;
import com.mycompany.common.LinkObject;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.tables.ColumnHeadBean;
import com.mwlib.tablo.db.BaseTableDesc;
import com.mwlib.tablo.db.EventProvider;
import com.mwlib.tablo.db.IMetaProvider;
import com.mwlib.tablo.db.IRowOperation;

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
 * To change this template use File | Settings | File Templates.
 */
public class ViolationDesc extends BaseTableDesc
{
    public static final String ID_KIND = "ID_KIND";
    private FieldTranslator[] fieldTranslator=new FieldTranslator[]
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
                    new SimpleField(SERV_NAME,"Служба")
                    {
//                        {
//                            autofit=true;
//                        }
                    },
                    new SimpleField("PLACE","Место"),
                    new DateField2("ND","Начало") ,
                    new DateField2("KD","Устранение")
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> outTuple) throws FieldException {

                            Timestamp kd=(Timestamp)tuple.get("KD");
                            Timestamp nd=(Timestamp)tuple.get("ND");
                            if (nd!=null && kd!=null && nd.before(kd))
                                return super.getS(column,tuple,outTuple);
                            return null;
                        }
                    },

                    new SimpleField("KD_ND","Длительность",ListGridFieldType.INTEGER.toString(),true)
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> outTuple) throws FieldException
                        {
                            Timestamp kd=(Timestamp)tuple.get("KD");
                            Timestamp nd=(Timestamp)tuple.get("ND");

                            if (nd!=null && kd!=null && (nd.before(kd) || nd.equals(kd)))
                            {
                                final long l = (kd.getTime() - nd.getTime())/(60000);
                                tuple.put("KD_ND",new Integer((int)l));
                                return super.getS(column, tuple, outTuple);
                            }
                            return null;
                        }
                    },

//                    new DateField2("KD_ND","Длительность",ListGridFieldType.DATETIME.toString(),true)
//                    {
//                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> outTuple) throws FieldException
//                        {
//                            Timestamp kd=(Timestamp)tuple.get("KD");
//                            Timestamp nd=(Timestamp)tuple.get("ND");
//
//                            if (nd!=null && kd!=null && nd.before(kd))
//                            {
//                                tuple.put("KD_ND",new Timestamp(kd.getTime()-nd.getTime()));
//                                return super.getS(column, tuple, outTuple);
//                            }
//                            return null;
//                        }
//                    },

                    new SimpleField(TablesTypes.COMMENT,"Проявление нарушения"),
                    new SimpleField(TablesTypes.PRED_NAME,"Предприятие")
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> outTuple) throws FieldException
                        {
                            Map<String, Object> hs=new HashMap<String, Object>();

                            Integer pred_id=(Integer)tuple.get(TablesTypes.PRED_ID);
                            if (pred_id!=null && pred_id!=0)
                            {
                                Directory.Pred pred = Directory.getByPredId(pred_id);
                                if (pred!=null)
                                {
                                    final Object o_vid_id = tuple.get(TablesTypes.VID_ID);
                                    if (Integer.valueOf(TablesTypes.Z_ID_SERVAL).equals(o_vid_id))
                                    {
                                        BusinessUtils.fillVIDByPred(hs, pred_id);
                                        final Object value = hs.get(TablesTypes.VID_ID);
                                        if (value!=null)
                                            tuple.put(TablesTypes.VID_ID, value);
                                    }

                                    return pred.getSNAME();
                                }
                                else
                                {
                                    BusinessUtils.fillTupleByPreds(tuple, hs);
                                    return String.valueOf(pred_id);
                                }
                            }
                            else
                            {
                                if (pred_id==null)
                                    tuple.put(TablesTypes.PRED_ID,TablesTypes.Z_ID_SERVAL);
                                BusinessUtils.fillTupleByPreds(tuple, hs);
                            }
                            return null;
                        }
                    },

                    //new SimpleField("HOZ_ID","Хозяйство"),//TODO Из справочника
                    new AField(TablesTypes.CRDURL,"Карточка")
                    {
                        {
                            linkText="Карточка";
                            type= ListGridFieldType.LINK.toString();
                        }
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple)
                        {

                            LinkObject linkObject = new LinkObject(linkText, "");
                            String id=(String)tuple.get(TablesTypes.KEY_FNAME);
                            id=removeKeySeparator(id);
                            if (id!=null && id.length()>0)
                            {
                                String[] dor2id=id.split(";");
                                String link="http://k_host/TabloReport2/KXXXX/ShowOriginalCard?DorKod="+dor2id[0]+"&EventId="+dor2id[1]+"&Type=Kasat";
                                linkObject.setLink(link);
                            }
                            return linkObject;
                        }
                    },
                    new SimpleField(TablesTypes.DOR_CODE,"№ Дороги",ListGridFieldType.INTEGER.toString(),false),

                    new SimpleField(TablesTypes.PRED_ID,"№ Предприятия",ListGridFieldType.INTEGER.toString(),false),
                    new SimpleField(TablesTypes.VID_ID,"№ Службы",ListGridFieldType.INTEGER.toString(),false),
                    new SimpleField(TablesTypes.VID_NAME,"Служба(Гр)",ListGridFieldType.TEXT.toString(),false),

//                    new SimpleField(SERV_IX,"SERV_IX",ListGridFieldType.INTEGER.toString(),false),

                    new SimpleField(ID_KIND,"Тип нарушения",ListGridFieldType.INTEGER.toString(),false)
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException
                        {
                            return getRowStyle(column,tuple, _outTuple);
                            //super.getS(column,tuple,_outTuple);
                        }
                    },
                    new SimpleField(TablesTypes.KEY_FNAME,TablesTypes.KEY_FNAME, ListGridFieldType.TEXT.toString(),false),
                    new SimpleField(TablesTypes.EVTYPE,"Событие", ListGridFieldType.TEXT.toString(),false)
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple)
                        {
                            return TablesTypes.VIOLATIONS;
                        }
                    },
                    new SimpleField(TablesTypes.EVTYPE_NAME,"Тип События", ListGridFieldType.TEXT.toString(),false)
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple)
                        {
                            return "НАРУШЕНИЯ";
                        }
                    },

                    new SimpleField(TablesTypes.DATATYPE_ID,"Ид.типа события",ListGridFieldType.INTEGER.toString(),false),
                    new SimpleField(TablesTypes.ACTUAL,"Актуальность",ListGridFieldType.INTEGER.toString(),false),
                    new DateField2(EventProvider.DATE_MIN_ND,"Время получения записи",false),
                    new DateField2(EventProvider.COR_MAX_TIME,"Время изменения записи",false)

    };

    private static FieldTranslator desc=new SimpleField("ID_KIND","Тип нарушения",ListGridFieldType.INTEGER.toString(),false);
    public static Object getRowStyle(ColumnHeadBean[] column,Map tuple, Map<String, Object> _outTuple) throws FieldException
    {
        Integer rv=(Integer)tuple.get("ID_KIND");
        Integer serv_ix=(Integer)tuple.get(SERV_IX);

        if (serv_ix!=null && serv_ix==72)
            _outTuple.put(TablesTypes.ROW_STYLE,"background-color:#FF0000;");
        else
        {
            if (rv==1)
                _outTuple.put(TablesTypes.ROW_STYLE,"background-color:#FFFFCC;");
        }
        return desc.getS(column,tuple,_outTuple);
    }


    static Map<String, int[]> service2Ix = new HashMap<String, int[]>();
    static Map<Integer,String> ix2Service= new HashMap<Integer, String>();
    public static final String SERV_NAME = "o_serv_name";
    public static final String SERV_IX = "o_serv_ix";

    static
    {
        int[] p=new int[]{68};
        service2Ix.put("П",p);
        int[] h=new int[]{69};
        service2Ix.put("Ш",h);
        int[] e=new int[]{70};
        service2Ix.put("Э",e);
        int[] v=new int[]{71};
        service2Ix.put("В",v);
        int[] np=new int[]{72};
        service2Ix.put("НС",np);


        for (String s : service2Ix.keySet())
            for (int anIx : service2Ix.get(s))
                ix2Service.put(anIx, s);
    }


    @Override
    public String getTableType() {
        return TablesTypes.VIOLATIONS;
    }



    public ViolationDesc(boolean test)
    {
        super(test);
    }


    @Override
    public FieldTranslator[] getFieldTranslator() {
        return  fieldTranslator;
    }

    public  void addMeta2Type(IMetaProvider metaProvider)
    {
        int typeid=getDataTypes()[0];
        metaProvider.addColumnByEventType(typeid,new ColumnHeadBean(SERV_NAME, SERV_NAME, ListGridFieldType.TEXT.toString()));
        metaProvider.addColumnByEventType(typeid,new ColumnHeadBean(SERV_IX, SERV_IX, ListGridFieldType.INTEGER.toString()));
        BusinessUtils.fillMetaByVid(metaProvider,typeid);
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
                String servName = ix2Service.get(typeid);
                if (servName != null)
                {
                    tuple.put(SERV_IX, typeid);
                    tuple.put(SERV_NAME,servName);

                    BusinessUtils.fillVidTupleByPredIdHozId(tuple);
                }
            }
        };
    }

    @Override
    public int[] getDataTypes() {
        return new int[]{68,69,70,71,72};
    }

}
