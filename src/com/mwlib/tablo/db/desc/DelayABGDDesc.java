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

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/*
TABLO_ID               DATATYPE_ID            NUM                    ATTR_ID                        ATTR_NAME                                                                                            ATTR_TYPE            COR_TIP DATE_ND                   DATE_KD                   COR_TIME
---------------------- ---------------------- ---------------------- ------------------------------ ---------------------------------------------------------------------------------------------------- -------------------- ------- ------------------------- ------------------------- -------------------------
6                      62                     10                     ARRIVALDATE                    ARRIVALDATE                                                                                          TIMESTAMP            I       19.11.13 22:58:59,328000000 31.12.99 23:59:59,999000000 19.11.13 22:59:06,218000000
6                      62                     12                     ARRIVALDATEFACT                ARRIVALDATEFACT                                                                                      TIMESTAMP            I       19.11.13 22:58:59,328000000 31.12.99 23:59:59,999000000 19.11.13 22:59:06,218000000
6                      62                     8                      ARRIVALDELAY                   ARRIVALDELAY                                                                                         INTEGER              I       19.11.13 22:58:59,328000000 31.12.99 23:59:59,999000000 19.11.13 22:59:06,218000000
6                      62                     18                     COMMENT                        COMMENT                                                                                              STRING               I       19.11.13 22:58:59,328000000 31.12.99 23:59:59,999000000 19.11.13 22:59:06,234000000
6                      62                     7                      DATEEND                        DATEEND                                                                                              TIMESTAMP            I       19.11.13 22:58:59,328000000 31.12.99 23:59:59,999000000 19.11.13 22:59:06,203000000
6                      62                     6                      DATESTART                      DATESTART                                                                                            TIMESTAMP            I       19.11.13 22:58:59,328000000 31.12.99 23:59:59,999000000 19.11.13 22:59:06,203000000
6                      62                     11                     DEPARTUREDATE                  DEPARTUREDATE                                                                                        TIMESTAMP            I       19.11.13 22:58:59,328000000 31.12.99 23:59:59,999000000 19.11.13 22:59:06,218000000
6                      62                     13                     DEPARTUREDATEFACT              DEPARTUREDATEFACT                                                                                    TIMESTAMP            I       19.11.13 22:58:59,328000000 31.12.99 23:59:59,999000000 19.11.13 22:59:06,218000000
6                      62                     9                      DEPARTUREDELAY                 DEPARTUREDELAY                                                                                       INTEGER              I       19.11.13 22:58:59,328000000 31.12.99 23:59:59,999000000 19.11.13 22:59:06,218000000
6                      62                     16                     FAULTCODE                      FAULTCODE                                                                                            INTEGER              I       19.11.13 22:58:59,328000000 31.12.99 23:59:59,999000000 19.11.13 22:59:06,234000000
6                      62                     17                     REASONTYPE                     REASONTYPE                                                                                           INTEGER              I       19.11.13 22:58:59,328000000 31.12.99 23:59:59,999000000 19.11.13 22:59:06,234000000
6                      62                     14                     RELID                          RELID                                                                                                INTEGER              I       19.11.13 22:58:59,328000000 31.12.99 23:59:59,999000000 19.11.13 22:59:06,234000000
6                      62                     15                     SERVICECODE                    SERVICECODE                                                                                          INTEGER              I       19.11.13 22:58:59,328000000 31.12.99 23:59:59,999000000 19.11.13 22:59:06,234000000
6                      62                     3                      STFROMNAME                     STFROMNAME                                                                                           STRING               I       19.11.13 22:58:59,328000000 31.12.99 23:59:59,999000000 19.11.13 22:59:06,187000000
6                      62                     4                      STTONAME                       STTONAME                                                                                             STRING               I       19.11.13 22:58:59,328000000 31.12.99 23:59:59,999000000 19.11.13 22:59:06,203000000
6                      62                     5                      STTYPE                         STTYPE                                                                                               INTEGER              I       19.11.13 22:58:59,328000000 31.12.99 23:59:59,999000000 19.11.13 22:59:06,203000000
6                      62                     1                      TRAINID                        TRAINID                                                                                              INTEGER              I       19.11.13 22:58:59,328000000 31.12.99 23:59:59,999000000 19.11.13 22:59:06,187000000
6                      62                     2                      TRAINNAME                      TRAINNAME                                                                                            STRING               I       19.11.13 22:58:59,328000000 31.12.99 23:59:59,999000000 19.11.13 22:59:06,187000000
*/


/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 07.07.14
 * Time: 18:20
 * Задержки поездов по системе ABGD
 */
public class DelayABGDDesc extends BaseTableDesc
{

    private static final SimpleDateFormat dataformat=new SimpleDateFormat("dd.MM.yyyy");
    private static final ThreadLocal<SimpleDateFormat> thsdf =
            new ThreadLocal<SimpleDateFormat>() {
                @Override
                protected SimpleDateFormat initialValue() {
                    return dataformat;
                }
            };



    private FieldTranslator[] addFieldTranslator=new FieldTranslator[]
            {

    new SimpleField("RPNAMEIN","Станция2"),
    new DateField2("DATEFAKTIN","Приб. Факт2",true),
    new DateField2("DATENORMIN","Приб. Граф.2",true),
    new SimpleField("LATEIN","Время задержки мин.2",ListGridFieldType.INTEGER.toString(),true),



    new SimpleField("RPNAMEOUT","Станция отпр.2"),
    new DateField2("DATEFAKTOUT","Отпр. Факт 2",true),
    new DateField2("DATENORMOUT","Отпр. Граф.2",true),
    new SimpleField("LATEOUT","Время задержки мин.2",ListGridFieldType.INTEGER.toString(),true),
            };

    private FieldTranslator[] fieldTranslator=new FieldTranslator[]
            {

                    new SimpleField("DOR_NAME", "Дорога", ListGridFieldType.TEXT.toString(), false) {
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

                    new SimpleField(SERV_NAME,"Поезд")
                    {
        //                        {
        //                            autofit=true;
        //                        }
                    },

                    new SimpleField("ASOUP","Номер<br>Поезда",ListGridFieldType.TEXT.toString(),true)
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException
                        {
                            if (new Integer(64).equals(tuple.get(TablesTypes.DATATYPE_ID)))
                                return super.getS(column,tuple,_outTuple);
                            else
                            {
                                Timestamp datestart = (Timestamp) tuple.get("DATESTART");
                                String otpr = "";
                                if (datestart!=null)
                                    otpr = "отпр. " + thsdf.get().format(datestart);
                                return tuple.get("TRAINNAME") + " "+otpr+" ";
                            }

                        }

                    },




                    new SimpleField("STTONAME","Станция")
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException
                        {
                            if (new Integer(64).equals(tuple.get(TablesTypes.DATATYPE_ID)))
                                return addFieldTranslator[0].getS(column,tuple,_outTuple);
                            else
                                return super.getS(column,tuple,_outTuple);
                        }
                    },
                    new DateField2("ARRIVALDATEFACT","Приб. Факт",true)
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException
                        {
                            if (new Integer(64).equals(tuple.get(TablesTypes.DATATYPE_ID)))
                                return addFieldTranslator[1].getS(column,tuple,_outTuple);
                            else
                                return super.getS(column,tuple,_outTuple);
                        }
                    },

                    new DateField2("ARRIVALDATE","Приб. Граф.",true)
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException
                        {
                            if (new Integer(64).equals(tuple.get(TablesTypes.DATATYPE_ID)))
                                return addFieldTranslator[2].getS(column,tuple,_outTuple);
                            else
                                return super.getS(column,tuple,_outTuple);
                        }
                    },

                    new SimpleField("ARRIVALDELAY","Время задержки мин.",ListGridFieldType.INTEGER.toString(),true)
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException
                        {
                            if (new Integer(64).equals(tuple.get(TablesTypes.DATATYPE_ID)))
                                return addFieldTranslator[3].getS(column,tuple,_outTuple);
                            else
                                return super.getS(column,tuple,_outTuple);
                        }
                    },



                    new SimpleField("STFROMNAME","Станция отпр.")
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException
                        {
                            if (new Integer(64).equals(tuple.get(TablesTypes.DATATYPE_ID)))
                                return addFieldTranslator[4].getS(column,tuple,_outTuple);
                            else
                                return super.getS(column,tuple,_outTuple);
                        }
                    },

                    new DateField2("DEPARTUREDATEFACT","Отпр. Факт",true)
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException
                        {
                            if (new Integer(64).equals(tuple.get(TablesTypes.DATATYPE_ID)))
                                return addFieldTranslator[5].getS(column,tuple,_outTuple);
                            else
                                return super.getS(column,tuple,_outTuple);
                        }
                    },

                    new DateField2("DEPARTUREDATE","Отпр. Граф.",true)
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException
                        {
                            if (new Integer(64).equals(tuple.get(TablesTypes.DATATYPE_ID)))
                                return addFieldTranslator[6].getS(column,tuple,_outTuple);
                            else
                                return super.getS(column,tuple,_outTuple);
                        }
                    },

                    new SimpleField("DEPARTUREDELAY","Время задержки мин.",ListGridFieldType.INTEGER.toString(),true)
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException
                        {
                            if (new Integer(64).equals(tuple.get(TablesTypes.DATATYPE_ID)))
                                return addFieldTranslator[7].getS(column,tuple,_outTuple);
                            else
                                return super.getS(column,tuple,_outTuple);
                        }
                    },


                    new SimpleField(TablesTypes.VID_ID, "№ Службы", ListGridFieldType.INTEGER.toString(), false)
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException
                        {
                            Integer serviceCode = (Integer) tuple.get("SERVICECODE");
                            switch (serviceCode)
                            {
                                case  130:
                                    BusinessUtils.fillVIDByBidName(tuple,"В");
                                break;
                                case  143:
                                case  144:
                                    BusinessUtils.fillVIDByBidName(tuple, "П");
                                break;
                                case  152:
                                    BusinessUtils.fillVIDByBidName(tuple, "Ш");
                                break;
                                case  157:
                                    BusinessUtils.fillVIDByBidName(tuple, "Э");
                                break;
                                case  6666:
                                    BusinessUtils.fillVIDByBidName(tuple, "ДПМ");
                                break;
                                default:
                                    BusinessUtils.fillVIDByBidName(tuple, TablesTypes.Z_SERVAL);
                            }
                            return super.getS(column,tuple,_outTuple);
                        }
                    },
                    new SimpleField(TablesTypes.VID_NAME, "Виновная служба", ListGridFieldType.TEXT.toString(), true),

                    new SimpleField("TRAINID","Ид.Поезда",ListGridFieldType.INTEGER.toString(),false),

                    new SimpleField(TablesTypes.DOR_CODE, "№ Дороги", ListGridFieldType.INTEGER.toString(), false),
                    new SimpleField(TablesTypes.DATATYPE_ID,"Ид.типа события",ListGridFieldType.INTEGER.toString(),false),

                    new SimpleField(TablesTypes.KEY_FNAME,TablesTypes.KEY_FNAME, ListGridFieldType.TEXT.toString(),false),
                    new SimpleField(TablesTypes.ACTUAL,"Актуальность",ListGridFieldType.INTEGER.toString(),false),
                    new DateField2(EventProvider.DATE_MIN_ND,"Время получения записи",false),
                    new DateField2(EventProvider.COR_MAX_TIME,"Время изменения записи",false)

            };

    public DelayABGDDesc(boolean test) {
        super(test);
    }

    @Override
    public String getTableType() {
        return TablesTypes.DELAYS_ABVGD;
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
        metaProvider.addColumnByEventType(typeid,new ColumnHeadBean(TablesTypes.DOR_CODE, TablesTypes.DOR_CODE, ListGridFieldType.INTEGER.toString()));
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
                    int dor_code = rs.getInt(TablesTypes.DOR_CODE);

                    tuple.put(TablesTypes.DOR_CODE, dor_code);
                    tuple.put(SERV_IX, typeid);
                    tuple.put(SERV_NAME, servName);
                }
            }
        };
    }

    @Override
    public int[] getDataTypes()
    {
        return new int[]{ 62,63,64/*ИХ АВГД*/,    };
    }


    static Map<String, int[]> service2Ix = new HashMap<String, int[]>();
    static Map<Integer,String> ix2Service= new HashMap<Integer, String>();
    public static final String SERV_NAME = "o_serv_name";
    public static final String SERV_IX = "o_serv_ix";

//    public static final String PASS = "Пас";
//    public static final String REG = "Пр";
//    public static final String CRG = "Гр";

    static  void initAll()
    {
        int[] p=new int[]{62};
        service2Ix.put(DelayGIDTDesc.PASS,p);
        int[] h=new int[]{63};
        service2Ix.put(DelayGIDTDesc.REG,h);
        int[] e=new int[]{64};
        service2Ix.put(DelayGIDTDesc.CRG,e);

        for (String s : service2Ix.keySet())
            for (int anIx : service2Ix.get(s))
                ix2Service.put(anIx, s);
    }

    public static String getServNameByDataTypeId(int typeid)
    {
        if (service2Ix.size()==0)
            initAll();

        return ix2Service.get(typeid);
    }


}
