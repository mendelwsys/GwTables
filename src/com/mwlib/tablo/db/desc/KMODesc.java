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
import com.mycompany.common.TablesTypes;
import com.mycompany.common.tables.ColumnHeadBean;

import com.smartgwt.client.types.ListGridFieldType;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 07.07.14
 * Time: 18:20
 * Таблица комиссионных осмотров
 */
public class KMODesc extends BaseTableDesc
{
    private FieldTranslator[] fieldTranslator=new FieldTranslator[]
            {

                    new SimpleField("DOR_NAME","Дорога",ListGridFieldType.TEXT.toString(),true)
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException
                        {
                            Integer dor_kod=(Integer)tuple.get("DOR_KOD");
                            if (Directory.isInit())
                            {
                                Directory.RailRoad byDorCode = Directory.getByDorCode(dor_kod);
                                if (byDorCode!=null)
                                    return byDorCode.getNAME();
                            }
                            return String.valueOf(dor_kod);
                        }
                    },
                    new SimpleField("DU_46_NUM","Номер ДУ-46",ListGridFieldType.INTEGER.toString(),true),
                    new DateField2("DATE_USTR_PLAN","Плановая дата<br>устранения",ListGridFieldType.DATE.toString()),
                    new AField("STAN_ID","Станция")
                    {
                        public Object getS(Map<String,ColumnHeadBean> column, Map tuple, Map<String, Object> outTuple) throws FieldException
                        {
                            Object stan1code = tuple.get("STAN_ID");

                            if (Directory.isInit())
                            {
                                Directory.StanDesc stan1=null;
                                if (stan1code!=null && (Integer)stan1code>0)
                                    stan1 = Directory.getByStanId((Integer)stan1code);
                                if (stan1!=null)
                                    return stan1.getNAME();
                                return "";
                            }
                            return  stan1code.toString();
                        }
                    },
                    new SimpleField("PL","Место обнаружения",ListGridFieldType.TEXT.toString(),true),
                    new SimpleField("BUG_COMMENT","Неисправность",ListGridFieldType.TEXT.toString(),true),
                    new SimpleField("MERA_BEZ","Запретные меры",ListGridFieldType.TEXT.toString(),true),
                    new SimpleField("PRED_ID","Ответственные<br>предприятия",true)
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> outTuple) throws FieldException
                        {
                            Integer pred_id=(Integer)tuple.get("PRED_ID");
                            if (pred_id!=null && pred_id!=0)
                            {
                                Directory.Pred pred = Directory.getByPredId(pred_id);
                                if (pred!=null)
                                    return pred.getSNAME();
                            }
                            return "";
                        }
                    },
                    new SimpleField("NSROK","Просроченное",ListGridFieldType.INTEGER.toString(),false)
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> outTuple) throws FieldException
                        {
                            Object rv=super.getS(column,tuple,outTuple);
                            Integer nspok = (Integer) tuple.get(this.name);
                            if (nspok!=null && nspok>0)
                                outTuple.put(TablesTypes.ROW_STYLE,"background-color:#FF7777;");
                            return rv;
                        }
                    },

//                    new SimpleField("MERA_BEZ_COMMENT","Мера (MERA_BEZ_COMMENT)",ListGridFieldType.TEXT.toString(),true),
//                    new SimpleField("MEROPR_COMMENT","Мера (MEROPR_COMMENT)",ListGridFieldType.TEXT.toString(),true),
                    new DateField2("DATE_USTR","Дата устранения",ListGridFieldType.DATE.toString(),false),
                    new SimpleField(TablesTypes.DOR_CODE,"№ Дороги",ListGridFieldType.TEXT.toString(),false),

                    new SimpleField(TablesTypes.KEY_FNAME,TablesTypes.KEY_FNAME, ListGridFieldType.TEXT.toString(),false),

                    new SimpleField(TablesTypes.VID_ID, "№ Службы", ListGridFieldType.INTEGER.toString(), false)
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException
                        {
                            Directory.Pred pred = null;
                            {
                                Integer pred_id = (Integer) tuple.get(TablesTypes.PRED_ID);
                                if (pred_id!=null)
                                    pred = Directory.getByPredId(pred_id);
                            }

                            if (pred!=null)
                                BusinessUtils.fillVIDByPred(tuple, pred);
                            else
                            {
                                tuple.put("VID_NAME", TablesTypes.Z_SERVAL);
                                tuple.put("VID_ID", TablesTypes.Z_ID_SERVAL);
                            }
                            return super.getS(column,tuple,_outTuple);
                        }
                    },
                    new SimpleField(TablesTypes.VID_NAME,"Служба(Гр)",ListGridFieldType.TEXT.toString(),false),



                    new SimpleField(TablesTypes.DATATYPE_ID,"Ид.типа события",ListGridFieldType.INTEGER.toString(),false),
                    new SimpleField(TablesTypes.ACTUAL,"Актуальность",ListGridFieldType.INTEGER.toString(),false),
                    new DateField2(EventProvider.DATE_MIN_ND,"Время получения записи",false),
                    new DateField2(EventProvider.COR_MAX_TIME,"Время изменения записи",false)

            };

    public KMODesc(boolean test) {
        super(test);
    }

    @Override
    public String getTableType() {
        return TablesTypes.KMOTABLE;
    }

    @Override
    public FieldTranslator[] getFieldTranslator() {
        return  fieldTranslator;
    }

    public  void addMeta2Type(IMetaProvider metaProvider)
    {
    }

    @Override
    protected IRowOperation _getRowOperation()
    {
        return null;
//                new IRowOperation()
//        {
//            @Override
//            public void setObjectAttr(IMetaProvider metaProvider, ColumnHeadBean attr, ResultSet rs, Map<String, Object> tuple) throws Exception
//            {
//                {
//                    //int typeid = rs.getInt(TablesTypes.DATATYPE_ID);
//                }
//            }
//        };
    }

    @Override
    public int[] getDataTypes()
    {
        return new int[]{ 65,66,67,/*КМО*/};
    }
}
