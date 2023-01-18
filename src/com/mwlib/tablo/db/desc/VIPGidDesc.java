package com.mwlib.tablo.db.desc;

import com.mwlib.tablo.analit2.BusinessUtils;
import com.mwlib.tablo.db.BaseTableDesc;
import com.mwlib.tablo.db.EventProvider;
import com.mwlib.tablo.db.IMetaProvider;
import com.mwlib.tablo.db.IRowOperation;
import com.mwlib.tablo.tables.DateField2;
import com.mwlib.tablo.tables.FieldTranslator;
import com.mwlib.tablo.tables.SimpleField;
import com.mwlib.utils.db.Directory;
import com.mycompany.common.FieldException;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.tables.ColumnHeadBean;
import com.smartgwt.client.types.ListGridFieldType;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 07.07.14
 * Time: 18:20
 * Важные пометки Гид
 */
public class VIPGidDesc extends BaseTableDesc
{
    private FieldTranslator[] fieldTranslator=new FieldTranslator[]
    {
                    new SimpleField(TablesTypes.DOR_NAME,"Дорога")
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException
                        {
                            Double dor_kod=(Double)tuple.get(TablesTypes.DOR_CODE);

                            if (Directory.isInit())
                            {
                                Directory.RailRoad byDorCode = Directory.getByDorCode(dor_kod.intValue());
                                if (byDorCode!=null)
                                    return byDorCode.getNAME();
                            }
                            return String.valueOf(dor_kod);
                        }
                    },

                    new SimpleField("SERV_CHAR","Служба")
                    {
//                        {
//                            autofit=true;
//                        }
                    },
                    new SimpleField("TNAME","Тип"),
            new SimpleField("MRMCOLOR", "Цвет") {
                public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException {
                    Directory.MarkColor mc = Directory.getByMarkColorId(new BigDecimal((Double) tuple.get("MRMCOLOR")));
                    return mc == null ? tuple.get("MRMCOLOR") : mc.getNAME();
                }

            },
                    new SimpleField("STAN1_ID","Место")
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException
                        {
                            Double dstan1code=(Double)tuple.get("MRCODEA");
                            Double dstan2code=(Double)tuple.get("MRCODEB");
                            int stan1code=0;
                            if (dstan1code!=null)
                                stan1code=dstan1code.intValue()/10;
                            int stan2code=0;
                            if (dstan2code!=null)
                                stan2code=dstan2code.intValue()/10;

                            if (Directory.isInit())
                            {
                                Directory.StanDesc stan1=null;
                                if (stan1code!=0)
                                    stan1 = Directory.getByStanCode(stan1code);

                                Directory.StanDesc stan2=null;
                                if (stan2code!=0)
                                    stan2 = Directory.getByStanCode(stan2code);
                                if (stan1!=null)
                                    return stan1.getNAME()+(stan2!=null?"-"+stan2.getNAME():"");
                                else if (stan2!=null)
                                    return stan2.getNAME();
                                return "";
                            }
                            else
                            {
                                return stan1code+((stan2code!=0)?"-"+stan2code:"");
                            }

                        }
                    },

                    new SimpleField("MRSWAY","Путь")
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException
                        {
                            Double way=(Double)tuple.get("MRSWAY");
                            int w = way.intValue();
                            if (w ==0)
                                return "Все";
                            return ""+w;
                        }
                    },

                    new DateField2("MRB","Время возникновения"),
                    new DateField2("MRE","Время завершения"),

                    new SimpleField("KD_ND","Длительность",ListGridFieldType.INTEGER.toString(),true)
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> outTuple) throws FieldException
                        {
                            Double dtlen=(Double)tuple.get("TIME_LEN");
                            if (dtlen!=null)
                                return dtlen;
                            return null;
                        }
                    },

//                    new DateField2("KD_ND","Длительность",ListGridFieldType.TIME.toString(),true)
//                    {
//                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> outTuple) throws FieldException
//                        {
//                            Double dtlen=(Double)tuple.get("TIME_LEN");
//                            if (dtlen!=null)
//                            {
//                                long tlen=dtlen.longValue()*60*1000;
//                                final Timestamp value = new Timestamp(tlen-TZ_MILS);
//                                tuple.put("KD_ND", value);
//                                return super.getS(column, tuple, outTuple);
//                            }
//                            return null;
//                        }
//                    },

//                    new SimpleField("TIME_LEN","Длительность")
//                    {
//                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException
//                        {
//                            Double dtlen=(Double)tuple.get("TIME_LEN");
//                            int tlen=dtlen.intValue();
//
//                            int dn=tlen/(24*60);
//                            int h=(tlen-dn*24*60)/60;
//                            int min=tlen-dn*24*60-h*60;
//                            String res="";
//                            if (dn>0) res+=dn+" д ";
//                            if (h>0) res+=h+" ч ";
//                            if (min>0) res+=min+" м";
//                            return res;
//                        }
//
//                    },
            /*
            ВРЕМЯ ВВОДА MRDATECORR
             */
                    new DateField2("MRDATECORR","Время ввода"),

                    new SimpleField("FNAME","Причина")
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException
                        {
                            String rv=(String)super.getS(column,tuple,_outTuple);
                            Double dflt=(Double)tuple.get("MRCFAULT");
                            if (dflt!=null)
                                return dflt.intValue()+" - "+rv;
                            return rv;
                        }
                    },

                    new SimpleField("MRS","Описание")
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException
                        {
                            Object rv=tuple.get(name);
                            if (rv==null) return "";
                            return super.getS(column,tuple,_outTuple);
                        }
                    },

                    new SimpleField(TablesTypes.PRED_NAME,"Предприятие",false)
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> outTuple) throws FieldException
                        {
                            Map<String, Object> hs=new HashMap<String, Object>();

                            Integer pred_id=(Integer)tuple.get(TablesTypes.PRED_ID);
                            if (pred_id!=null && pred_id!=0)
                            {
                                Directory.Pred pred = Directory.getByPredId(pred_id);
                                if (pred!=null)
                                    return pred.getSNAME();
                                else
                                    return String.valueOf(pred_id);
                            }
                            else if (pred_id==null)
                                    tuple.put(TablesTypes.PRED_ID,TablesTypes.Z_ID_SERVAL);
                            return null;
                        }
                    },


                    new SimpleField(TablesTypes.DOR_CODE,"№ Дороги",ListGridFieldType.INTEGER.toString(),false),

                    new SimpleField(TablesTypes.PRED_ID,"№ Предприятия",ListGridFieldType.INTEGER.toString(),false),
                    new SimpleField(TablesTypes.VID_ID,"№ Службы",ListGridFieldType.INTEGER.toString(),false),
                    new SimpleField(TablesTypes.VID_NAME,"Служба(Гр)",ListGridFieldType.TEXT.toString(),false),


                    new SimpleField(TablesTypes.KEY_FNAME,TablesTypes.KEY_FNAME,ListGridFieldType.TEXT.toString(),false),


                    new SimpleField(TablesTypes.EVTYPE,"Событие", ListGridFieldType.TEXT.toString(),false)
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple)
                        {
                            return TablesTypes.VIP_GID;
                        }
                    },
                    new SimpleField(TablesTypes.EVTYPE_NAME,"Тип События", ListGridFieldType.TEXT.toString(),false)
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple)
                        {
                            return "ГИД";
                        }
                    },

                    new SimpleField(TablesTypes.DATATYPE_ID,"Ид.типа события",ListGridFieldType.INTEGER.toString(),false),
                    new SimpleField(TablesTypes.ACTUAL,"Актуальность",ListGridFieldType.INTEGER.toString(),false),
                    new DateField2(EventProvider.DATE_MIN_ND,"Время получения записи",false),
                    new DateField2(EventProvider.COR_MAX_TIME,"Время изменения записи",false)


    };



    public VIPGidDesc(boolean test) {
        super(test);
    }

    @Override
    public String getTableType() {
        return TablesTypes.VIP_GID;
    }

    @Override
    public FieldTranslator[] getFieldTranslator() {
        return  fieldTranslator;
    }

    public  void addMeta2Type(IMetaProvider metaProvider)
    {
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
                {
                    int typeid = rs.getInt(TablesTypes.DATATYPE_ID);
                    for (int dataType : getDataTypes())
                    {
                        if (typeid==dataType)
                        {
                            BusinessUtils.fillVidTupleByServChar(tuple);
                            break;
                        }
                    }
                }
            }
        };
    }


    @Override
    public int[] getDataTypes()
    {
        return new int[]{90};
    }
}
