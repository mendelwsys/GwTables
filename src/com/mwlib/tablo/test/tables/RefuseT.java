package com.mwlib.tablo.test.tables;

import com.mwlib.utils.db.FillFromDb;
import com.mycompany.common.FieldException;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.tables.ColumnHeadBean;
import com.mwlib.tablo.tables.AField;
import com.mwlib.tablo.tables.FieldTranslator;
import com.mwlib.tablo.tables.SimpleField;
import com.smartgwt.client.types.ListGridFieldType;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 07.07.14
 * Time: 18:20
 * To change this template use File | Settings | File Templates.
 */
public class RefuseT extends BaseTable
{
    private FieldTranslator[] fieldTranslator=new FieldTranslator[]
    {
                    new SimpleField(SERV_NAME,"Служба")
                    {
                        {
                            autofit=true;
                        }
                    },
                    new SimpleField("PLACE","Место отказа"),
                    new DateField("ND","Начало") ,
                    new DateField("KD","Устранение")
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> outTuple) throws FieldException {

                            Timestamp kd=(Timestamp)tuple.get("KD");
                            Timestamp nd=(Timestamp)tuple.get("ND");
                            if (nd.before(kd))
                                return super.getS(column,tuple,outTuple);
                            return "-";
                        }
                    },


                    new SimpleField("COMMENT","Проявление отказа"),

                    //new SimpleField("PRED_ID","Предприятие"), //TODO Из справочника
                    //new SimpleField("HOZ_ID","Хозяйство"),//TODO Из справочника
                    new AField("CRDURL","Карточка")
                    {
                        {
                            linkText="Показать";
                            type=ListGridFieldType.LINK.toString();
                        }
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple)
                        {
                            String id=(String)tuple.get(TablesTypes.KEY_FNAME);
                            if (id!=null && id.length()>0)
                            {
                                String[] dor2id=id.split(";");
                                String link = "http://k_host/TabloReport2/KXXXX/ShowOriginalCard?DorKod=" + dor2id[0] + "&EventId=" + dor2id[1] + "&Type=Kasant";
//                                return new LinkObject("Карточка1",link);
                                return link;
                            }
//                            return new LinkObject("","");
                            return "";
                        }
                    },
                    /*
                        3.Раскраска по категориям.
                    */
                    new SimpleField("DOR_KOD","Дорога",ListGridFieldType.INTEGER.toString(),false),
                    new SimpleField("ID_KIND","Тип отказа",ListGridFieldType.INTEGER.toString(),false)
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException
                        {
                            return getRowStyle(column,tuple, _outTuple);
                            //super.getS(column,tuple,_outTuple);
                        }
                    },
                    new SimpleField(TablesTypes.KEY_FNAME,TablesTypes.KEY_FNAME, ListGridFieldType.TEXT.toString(),false),


    };

    private static FieldTranslator desc=new SimpleField("ID_KIND","Тип отказа",ListGridFieldType.INTEGER.toString(),false);
    public static Object getRowStyle(ColumnHeadBean[] column,Map tuple, Map<String, Object> _outTuple) throws FieldException
    {
        Integer rv=(Integer)tuple.get("ID_KIND");
        Integer serv_ix=(Integer)tuple.get(SERV_IX);

        if (serv_ix!=null && serv_ix==73)
            _outTuple.put(TablesTypes.ROW_STYLE,"background-color:#FF0000;");
        else
        {
            if (rv==1)
                _outTuple.put(TablesTypes.ROW_STYLE,"background-color:#FFBB99;");
            else if (rv==2)
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
        int[] p=new int[]{48};
        service2Ix.put("П",p);
        int[] h=new int[]{49};
        service2Ix.put("Ш",h);
        int[] e=new int[]{50};
        service2Ix.put("Э",e);
        int[] v=new int[]{51};
        service2Ix.put("В",v);
        int[] np=new int[]{73};
        service2Ix.put("НС",np);


        for (String s : service2Ix.keySet())
            for (int anIx : service2Ix.get(s))
                ix2Service.put(anIx, s);
    }


    @Override
    public String getTableType() {
        return TablesTypes.REFUSES;
    }

    protected RefuseT(boolean test) {
        super(test);
    }

    protected static BaseTable inst;
    public static BaseTable getInstance(boolean test) throws Exception
    {

        if (inst!=null)
            return inst;
        return inst=new RefuseT(test);
    }

    @Override
    protected FieldTranslator[] getFieldTranslator() {
        return  fieldTranslator;
    }

    @Override
    protected void addParameters(Map mapParams)
    {
        mapParams.put(FillFromDb.DOR_COD_ATTR,new Integer[] {28});
    }

    @Override
    protected Pair<ColumnHeadBean[], Map[]> getDbTable(Map mapParams, Map<String, Object> outParams) throws Exception
    {
        Map<String,FillFromDb.ParamVal> valMap = new HashMap<String,FillFromDb.ParamVal>();

        Timestamp maxTimeStamp = FillFromDb.getMaxTimeStamp(mapParams);
        valMap.put(TablesTypes.MAX_TIMESTAMP,new FillFromDb.ParamVal(-1, maxTimeStamp, Types.NULL));

        Integer dor_code = ((Integer[])mapParams.get(FillFromDb.DOR_COD_ATTR))[0];

        valMap.put(FillFromDb.DOR_COD_ATTR+"_1",new FillFromDb.ParamVal(1, dor_code, Types.INTEGER));

        valMap.put("ND",new FillFromDb.ParamVal(2, new Timestamp(Calendar.getInstance().getTimeInMillis()-24*60*60*1000), Types.TIMESTAMP));
        valMap.put("CORTIME1",new FillFromDb.ParamVal(3, maxTimeStamp, Types.TIMESTAMP));

        Pair<ColumnHeadBean[], Map[]> resVal = FillFromDb._getDbTable(GET_W_REFUSE, valMap, outParams, new FillFromDb.IRowOperation()
        {
            public void setObjectArc(Map<String, ColumnHeadBean> attrs, Timestamp cor_time, ResultSet rs, Map<String, Pair<Timestamp, Object>> obj) throws Exception
            {
                int typeid = rs.getInt(TablesTypes.DATATYPE_ID);


                 obj.put(SERV_IX, new Pair<Timestamp, Object>(cor_time, typeid));
                if (!attrs.containsKey(SERV_IX))
                    attrs.put(SERV_IX, new ColumnHeadBean(SERV_IX, SERV_IX, ListGridFieldType.INTEGER.toString()));

                String servName = ix2Service.get(typeid);
                if (servName != null)
                    obj.put(SERV_NAME, new Pair<Timestamp, Object>(cor_time, servName));
                else if (obj.get(SERV_NAME) == null)
                    obj.put(SERV_NAME, new Pair<Timestamp, Object>(cor_time, "X"));

                if (!attrs.containsKey(SERV_NAME))
                    attrs.put(SERV_NAME, new ColumnHeadBean(SERV_NAME, SERV_NAME, ListGridFieldType.TEXT.toString()));

            }
        });
        return resVal;
    }


    public static final String GET_W_REFUSE="select \n" +
        "toj1.data_obj_id,\n" +
        "toj2.cor_time," +
        "da.attr_id,al.attr_type,da.value_s,da.value_i,da.value_f,da.value_t,da.cor_time as acor_time,\n" +
        "toj2.text, da.datatype_id \n"+
        "from (  \n" +
        "select distinct(toj.data_obj_id) as data_obj_id from tablo_data_objects toj,tablo_data_attributes da where toj.DATATYPE_ID IN (48,49,50,51,73)\n" +
        "and toj.cor_tip IN ('I','U') and toj.dor_kod=? \n" +
        "and toj.data_obj_id=da.data_obj_id  and " +
        "(" +
         "da.attr_id='ND' and ( da.value_t>=? ) \n "+//"TO_TIMESTAMP ('14-07-14 15:50:00', 'DD-MM-RR HH24:MI:SS')) \n" +
        " or\n" +
        " da.attr_id='KD' and (da.value_t<TO_TIMESTAMP ('01-01-00 00:00:00', 'DD-MM-RR HH24:MI:SS'))" +
        ") "+
        "and toj.cor_time> ? \n"+
        ") toj1, \n" +
        "tablo_data_objects toj2,\n" +
        "tablo_data_attributes da,\n" +
        "tablo_data_attr_list al  where  \n" +
        "toj1.data_obj_id=da.data_obj_id \n" +
        "and toj2.data_obj_id=da.data_obj_id \n" +
        "and toj2.datatype_id=da.datatype_id\n" +
        "and al.attr_id=da.attr_id \n" +
        "and al.DATATYPE_ID=da.datatype_id \n"+
        " order by \n" +
        "toj2.cor_time,\n" +
        "da.data_obj_id,da.datatype_id ";


}
